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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(KafkaTestContainersConfiguration.class)
public class KafkaProducerTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
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
        createDTO.setClientId(1L);
        createDTO.setDeparture("From");
        createDTO.setArrival("To");

        OrderMsgDTO after = orderService.createOrder(createDTO);

        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> !records.isEmpty());
        assertEquals(1, records.size());
        ConsumerRecord<String, OrderMsgDTO> recordsEntry = records.get(0);
        assertEquals(String.valueOf(after.getId()), recordsEntry.key());
        OrderMsgDTO fromKafka = recordsEntry.value();
        assertEquals(after.getId(), fromKafka.getId());
        assertEquals(createDTO.getArrival(),fromKafka.getArrival());
        assertEquals(createDTO.getDeparture(),fromKafka.getDeparture());
        assertEquals(OrderStatus.CREATED,fromKafka.getStatus());
        assertEquals(createDTO.getClientId(),fromKafka.getUserId());

    }

    @Test
    @DisplayName("Updated order was sent to kafka")
    public void updatedOrderWasSentToKafka() {

        com.example.ordersservice.domain.Order order = new com.example.ordersservice.domain.Order();
        order.setUserId(1L);
        order.setStatus(OrderStatus.CREATED);
        order.setArrival("to");
        order.setDeparture("from");
        Order saved = orderRepo.saveAndFlush(order);

        UpdateOrderDTO updateDTO = new UpdateOrderDTO();
        updateDTO.setOrderId(saved.getId());
        updateDTO.setStatus(OrderStatus.ASSIGNED);
        updateDTO.setDriver("test");

        OrderMsgDTO msgDTO = new OrderMsgDTO();
        msgDTO.setId(updateDTO.getOrderId());
        msgDTO.setStatus(updateDTO.getStatus());
        msgDTO.setUserId(order.getUserId());
        msgDTO.setDriver(updateDTO.getDriver());
        msgDTO.setArrival(order.getArrival());
        msgDTO.setDeparture(order.getDeparture());

        orderService.updateOrder(updateDTO);

        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> !records.isEmpty());

        assertEquals(1, records.size());
        ConsumerRecord<String, OrderMsgDTO> recordsEntry = records.get(0);
        assertEquals(String.valueOf(msgDTO.getId()), recordsEntry.key());
        OrderMsgDTO fromKafka = recordsEntry.value();
        assertEquals(msgDTO.getId(), fromKafka.getId());
        assertEquals(msgDTO.getArrival(),fromKafka.getArrival());
        assertEquals(msgDTO.getDeparture(),fromKafka.getDeparture());
        assertEquals(msgDTO.getStatus(),fromKafka.getStatus());
        assertEquals(msgDTO.getUserId(),fromKafka.getUserId());
        assertEquals(msgDTO.getDriver(),fromKafka.getDriver());
    }

}
