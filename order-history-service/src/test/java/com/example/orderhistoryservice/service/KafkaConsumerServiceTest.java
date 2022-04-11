package com.example.orderhistoryservice.service;

import com.example.orderhistoryservice.config.KafkaTestContainersConfiguration;
import com.example.orderhistoryservice.domain.HistoryEntry;
import com.example.orderhistoryservice.domain.OrderStatus;
import com.example.orderhistoryservice.dto.OrderMsgDTO;
import com.example.orderhistoryservice.mapper.EntryDtoMapper;
import com.example.orderhistoryservice.repo.HistoryEntryRepo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(KafkaTestContainersConfiguration.class)
public class KafkaConsumerServiceTest {

    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));

    @BeforeAll
    static void startKafka() {
        kafka.start();
    }

    @AfterAll
    static void stopKafka() {
        kafka.stop();
    }

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", () -> kafka.getHost() + ":" + kafka.getFirstMappedPort());
    }

    @Autowired
    private KafkaConsumerService consumer;
    @Autowired
    private KafkaTemplate<String, OrderMsgDTO> kafkaTemplate;
    @Autowired
    private EntryDtoMapper dtoMapper;
    @Autowired
    private HistoryEntryRepo entryRepo;
    private String topic = "orderHistory";

    @BeforeEach
    @AfterEach
    public void cleanBD() {
        entryRepo.deleteAll();
    }

    private OrderMsgDTO createOrderMsgDTO(Long orderId, OrderStatus status, Long userId, String driver, LocalDateTime dateTime) {
        OrderMsgDTO dto = new OrderMsgDTO();
        dto.setId(orderId);
        dto.setStatus(status);
        dto.setUserId(userId);
        dto.setDriver(driver);
        dto.setDeparture("from");
        dto.setArrival("to");
        dto.setLocalDateTime(dateTime);
        return dto;
    }

    @Test
    @DisplayName("Read by Kafka message was saved to DB")
    public void sentMsgWasSaved() {

        LocalDate date = LocalDate.of(2021, 12, 12);
        LocalTime time = LocalTime.of(12, 12);
        LocalDateTime localDateTime = LocalDateTime.of(date, time);
        OrderMsgDTO dto = createOrderMsgDTO(1L, OrderStatus.CREATED, 1L, "test", localDateTime);

        kafkaTemplate.send(topic, String.valueOf(dto.getId()), dto);
        kafkaTemplate.flush();
        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> !entryRepo.findAllByOrderId(dto.getId()).isEmpty());
        List<HistoryEntry> entries = entryRepo.findAllByOrderId(dto.getId());
        assertEquals(1, entries.size());
        OrderMsgDTO dtoFromDB = dtoMapper.toDTO(entries.get(0));
        assertEquals(dto, dtoFromDB);
    }

}