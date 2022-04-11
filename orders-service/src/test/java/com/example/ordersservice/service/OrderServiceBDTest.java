package com.example.ordersservice.service;

import com.example.ordersservice.config.KafkaTestContainersConfiguration;
import com.example.ordersservice.domain.Order;
import com.example.ordersservice.domain.OrderStatus;
import com.example.ordersservice.dto.CreateOrderDTO;
import com.example.ordersservice.dto.OrderMsgDTO;
import com.example.ordersservice.dto.UpdateOrderDTO;
import com.example.ordersservice.mapper.OrderDtoMapper;
import com.example.ordersservice.repo.OrderRepo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
@Import(KafkaTestContainersConfiguration.class)
class OrderServiceBDTest {
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepo orderRepo;
    @Autowired
    private OrderDtoMapper orderDtoMapper;
    @MockBean
    private KafkaProducerService producerService;

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

    @BeforeEach
    @AfterEach
    private void cleanBD() {
        orderRepo.deleteAll();
    }

    private com.example.ordersservice.domain.Order createUnassignedOrder(OrderStatus status, Long userId) {
        Order order = new Order();
        order.setStatus(status);
        order.setUserId(userId);
        order.setDeparture("from");
        order.setArrival("to");
        return order;
    }

    @Test
    @DisplayName("Created order was saved to db")
    public void createdOrderWasSaved() {
        CreateOrderDTO createDTO = new CreateOrderDTO();
        createDTO.setClientId(1L);
        createDTO.setDeparture("from");
        createDTO.setArrival("to");
        doNothing().when(producerService).sendMessage(any(OrderMsgDTO.class));

        OrderMsgDTO orderMsgDTO = orderService.createOrder(createDTO);

        Optional<Order> byId = orderRepo.findById(orderMsgDTO.getId());
        assertTrue(byId.isPresent());
        Order saved = byId.get();
        assertEquals(orderMsgDTO.getId(), saved.getId());
        assertEquals(OrderStatus.CREATED, saved.getStatus());
        assertNull(saved.getDriver());
        assertEquals(createDTO.getClientId(), saved.getUserId());
        assertEquals(createDTO.getArrival(), saved.getArrival());
        assertEquals(createDTO.getDeparture(), saved.getDeparture());
    }

    @Test
    @DisplayName("Order was updated in DB")
    public void orderWasUpdated() {
        Order order = createUnassignedOrder(OrderStatus.CREATED, 1L);
        Order saved = orderRepo.saveAndFlush(order);
        UpdateOrderDTO updateDTO = new UpdateOrderDTO();
        updateDTO.setOrderId(saved.getId());
        updateDTO.setStatus(OrderStatus.ASSIGNED);
        updateDTO.setDriver("test");
        doNothing().when(producerService).sendMessage(any(OrderMsgDTO.class));

         orderService.updateOrder(updateDTO);

        Optional<Order> byId = orderRepo.findById(saved.getId());
        assertTrue(byId.isPresent());
        Order fromBD = byId.get();
        assertEquals(saved.getId(), fromBD.getId());
        assertEquals(OrderStatus.ASSIGNED, fromBD.getStatus());
        assertEquals("test", fromBD.getDriver());
        assertEquals(saved.getUserId(), fromBD.getUserId());
        assertEquals(saved.getDeparture(), fromBD.getDeparture());
        assertEquals(saved.getArrival(), fromBD.getArrival());
    }

    @Test
    @DisplayName("Update non-existent order")
    public void updateNonExistentOrder() {
        UpdateOrderDTO updateDTO = new UpdateOrderDTO();
        updateDTO.setOrderId(1L);
        updateDTO.setStatus(OrderStatus.ASSIGNED);
        updateDTO.setDriver("test");

        assertThrows(EntityNotFoundException.class, () -> orderService.updateOrder(updateDTO));

    }


}