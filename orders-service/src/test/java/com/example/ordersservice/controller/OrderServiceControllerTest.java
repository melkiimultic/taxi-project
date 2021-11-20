package com.example.ordersservice.controller;

import com.example.ordersservice.config.KafkaTestContainersConfiguration;
import com.example.ordersservice.domain.Order;
import com.example.ordersservice.domain.OrderStatus;
import com.example.ordersservice.dto.OrderIdDto;
import com.example.ordersservice.repo.OrderRepo;
import com.example.ordersservice.service.OrderService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(KafkaTestContainersConfiguration.class)
class OrderServiceControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    private OrderService orderService;
    @Autowired
    OrderRepo orderRepo;
    @Autowired
    ObjectMapper mapper;

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
        registry.add("spring.kafka.bootstrap-servers", () -> {
            return kafka.getHost() + ":" + kafka.getFirstMappedPort();
        });
    }

    @BeforeEach
    @AfterEach
    private void cleanBD(){
        orderRepo.deleteAll();
    }

    private com.example.ordersservice.domain.Order createOrder(OrderStatus status, Long userId, String driver){
        Order order = new Order();
        order.setStatus(status);
        order.setDriver(driver);
        order.setUserId(userId);
        return order;
    }

    @Test
    @DisplayName("Get unassigned orders happy path")
    @SneakyThrows
    public void getUnassigned(){
        Order order = createOrder(OrderStatus.CREATED,1L,"test");
        orderRepo.saveAndFlush(order);
        MvcResult mvcResult = mockMvc.perform(get("/order/unassigned"))
                .andExpect(status().isOk())
                .andReturn();
//        List<OrderIdDto> orderIdDtos = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<OrderIdDto>>() {
//        });

    }



}