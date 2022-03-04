package com.example.ordersservice.service;

import com.example.ordersservice.config.KafkaTestContainersConfiguration;
import com.example.ordersservice.domain.Order;
import com.example.ordersservice.domain.OrderStatus;
import com.example.ordersservice.dto.CreateOrderDTO;
import com.example.ordersservice.dto.OrderMsgDTO;
import com.example.ordersservice.dto.UpdateOrderDTO;
import com.example.ordersservice.mapper.OrderDtoMapper;
import com.example.ordersservice.repo.OrderRepo;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(KafkaTestContainersConfiguration.class)
public class KafkaProducerTest {
    @Autowired
    private OrderService orderService;
    @MockBean
    private OrderRepo orderRepo;
    @MockBean
    private OrderDtoMapper orderDtoMapper;

    //List should be static for working in multiple tests
    private static List<ConsumerRecord<String, OrderMsgDTO>> records = new ArrayList<>();

    private final String topic = "orderHistory";

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

    @KafkaListener(topics = topic, groupId = "history")
    public void consume(ConsumerRecord<String, OrderMsgDTO> cr) {
        records.add(cr);
    }

    @AfterEach
    private void cleanRecords() {
        records.clear();
    }

    @Test
    @DisplayName("Created order was sent to kafka")
    public void createdOrderWasSentToKafka() {

        CreateOrderDTO createDTO = new CreateOrderDTO();
        createDTO.setUserId(1L);

        OrderMsgDTO msgDTO = new OrderMsgDTO();
        msgDTO.setId(1L);
        msgDTO.setStatus(OrderStatus.CREATED);
        msgDTO.setUserId(createDTO.getUserId());
        LocalDateTime now = LocalDateTime.now();
        msgDTO.setLocalDateTime(now);
        com.example.ordersservice.domain.Order order = new com.example.ordersservice.domain.Order();
        order.setUserId(1L);
        order.setStatus(OrderStatus.CREATED);
        when(orderRepo.save(order)).thenReturn(order);
        when(orderDtoMapper.toOrderMsgDTO(any(Order.class))).thenReturn(msgDTO);

        orderService.createOrder(createDTO);

        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> !records.isEmpty());
        assertEquals(1, records.size());
        ConsumerRecord<String, OrderMsgDTO> recordsEntry = records.get(0);
        assertEquals(String.valueOf(msgDTO.getId()), recordsEntry.key());
        OrderMsgDTO fromKafka = recordsEntry.value();
        assertEquals(msgDTO, fromKafka);

    }

    @Test
    @DisplayName("Updated order was sent to kafka")
    public void updatedOrderWasSentToKafka() {
        UpdateOrderDTO updateDTO = new UpdateOrderDTO();
        updateDTO.setOrderId(1L);
        updateDTO.setStatus(OrderStatus.ASSIGNED);
        updateDTO.setDriver("test");
        com.example.ordersservice.domain.Order order = new com.example.ordersservice.domain.Order();
        order.setUserId(1L);
        order.setStatus(OrderStatus.CREATED);
        order.setId(1L);

        Order updated = new Order();
        updated.setId(updateDTO.getOrderId());
        updated.setDriver(updateDTO.getDriver());
        updated.setStatus(updateDTO.getStatus());
        updated.setUserId(order.getUserId());

        OrderMsgDTO msgDTO = new OrderMsgDTO();
        msgDTO.setId(updated.getId());
        msgDTO.setStatus(updated.getStatus());
        msgDTO.setUserId(updated.getUserId());
        msgDTO.setDriver(updated.getDriver());
        LocalDateTime now = LocalDateTime.now();
        msgDTO.setLocalDateTime(now);

        when(orderRepo.findById(updateDTO.getOrderId())).thenReturn(Optional.of(order));
        when(orderRepo.save(updated)).thenReturn(updated);
        when(orderDtoMapper.toOrderMsgDTO(any(Order.class))).thenReturn(msgDTO);

        orderService.updateOrder(updateDTO);

        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> !records.isEmpty());

        assertEquals(1, records.size());
        ConsumerRecord<String, OrderMsgDTO> recordsEntry = records.get(0);
        assertEquals(String.valueOf(msgDTO.getId()), recordsEntry.key());
        OrderMsgDTO fromKafka = recordsEntry.value();
        assertEquals(msgDTO, fromKafka);

    }

}
