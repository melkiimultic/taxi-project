package com.example.ordersservice.controller;

import com.example.ordersservice.config.KafkaTestContainersConfiguration;
import com.example.ordersservice.domain.Order;
import com.example.ordersservice.domain.OrderStatus;
import com.example.ordersservice.dto.CreateOrderDTO;
import com.example.ordersservice.dto.OrderForDriverDTO;
import com.example.ordersservice.dto.UnassignedOrderDto;
import com.example.ordersservice.dto.OrderMsgDTO;
import com.example.ordersservice.dto.UpdateOrderDTO;
import com.example.ordersservice.repo.OrderRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(KafkaTestContainersConfiguration.class)
class
OrderServiceControllerTest {

    @Autowired
    MockMvc mockMvc;
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

    private com.example.ordersservice.domain.Order createAssignedOrder(OrderStatus status, Long userId, String driver) {
        Order order = new Order();
        order.setStatus(status);
        order.setUserId(userId);
        order.setDeparture("from");
        order.setArrival("to");
        order.setDriver(driver);
        return order;
    }

    private void populateBDWithOrders(OrderStatus status, String driver, int number) {
        for (int i = 0; i < number; i++) {
            final Order assignedOrder = createAssignedOrder(status, (long) i + 1, driver);
            orderRepo.saveAndFlush(assignedOrder);
        }
    }

    @Test
    @DisplayName("Create order happy path")
    @SneakyThrows
    public void createOrder() {
        CreateOrderDTO createDTO = new CreateOrderDTO();
        createDTO.setClientId(1L);
        createDTO.setDeparture("from");
        createDTO.setArrival("to");
        String body = mapper.writeValueAsString(createDTO);
        MvcResult mvcResult = mockMvc.perform(post("/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        OrderMsgDTO orderMsgDTO = mapper.readValue(mvcResult.getResponse().getContentAsString(), OrderMsgDTO.class);
        assertNotNull(orderMsgDTO.getId());
        assertEquals(LocalDate.now(), orderMsgDTO.getLocalDateTime().toLocalDate());
        assertEquals(createDTO.getClientId(), orderMsgDTO.getUserId());
        assertEquals(OrderStatus.CREATED, orderMsgDTO.getStatus());
        assertEquals(createDTO.getDeparture(), orderMsgDTO.getDeparture());
        assertEquals(createDTO.getArrival(), orderMsgDTO.getArrival());
        assertNull(orderMsgDTO.getDriver());
    }

    @Test
    @DisplayName("Update order happy path")
    @SneakyThrows
    public void updateOrder() {
        Order order = createUnassignedOrder(OrderStatus.CREATED, 1L);
        Order saved = orderRepo.saveAndFlush(order);
        UpdateOrderDTO updateDTO = new UpdateOrderDTO();
        updateDTO.setOrderId(saved.getId());
        updateDTO.setStatus(OrderStatus.ASSIGNED);
        updateDTO.setDriver("test");
        String body = mapper.writeValueAsString(updateDTO);
        MvcResult mvcResult = mockMvc.perform(post("/order/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        OrderMsgDTO orderMsgDTO = mapper.readValue(mvcResult.getResponse().getContentAsString(), OrderMsgDTO.class);
        assertEquals(LocalDate.now(), orderMsgDTO.getLocalDateTime().toLocalDate());
        assertEquals(updateDTO.getOrderId(), orderMsgDTO.getId());
        assertEquals(1L, orderMsgDTO.getUserId());
        assertEquals(saved.getDeparture(), orderMsgDTO.getDeparture());
        assertEquals(saved.getArrival(), orderMsgDTO.getArrival());
        assertEquals(OrderStatus.ASSIGNED, orderMsgDTO.getStatus());
        assertEquals(updateDTO.getDriver(), orderMsgDTO.getDriver());
    }

    @Test
    @DisplayName("Get unassigned orders happy path")
    @SneakyThrows
    public void getUnassigned() {
        Order order = createUnassignedOrder(OrderStatus.CREATED, 1L);
        Order saved = orderRepo.saveAndFlush(order);
        MvcResult mvcResult = mockMvc.perform(get("/order/unassigned"))
                .andExpect(status().isOk())
                .andReturn();
        List<UnassignedOrderDto> unassignedOrderDtos = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(1, unassignedOrderDtos.size());
        assertEquals(saved.getId(), unassignedOrderDtos.get(0).getId());
        assertEquals(saved.getDeparture(), unassignedOrderDtos.get(0).getDeparture());
        assertEquals(saved.getArrival(), unassignedOrderDtos.get(0).getArrival());
    }

    @Test
    @DisplayName("Get empty list when unassigned orders don't exist")
    @SneakyThrows
    public void getEmptyList() {
        Order order = createUnassignedOrder(OrderStatus.ASSIGNED, 1L);
        order.setDriver("test");
        orderRepo.saveAndFlush(order);
        MvcResult mvcResult = mockMvc.perform(get("/order/unassigned"))
                .andExpect(status().isOk())
                .andReturn();
        List<UnassignedOrderDto> orderIdDtos = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertTrue(orderIdDtos.isEmpty());
    }

    @Test
    @DisplayName("get 1st page if request with default params")
    @SneakyThrows
    public void getCorrectFirstPage() {
        String username = "username";
        populateBDWithOrders(OrderStatus.CREATED, username, 6);
        assertEquals(6, orderRepo.findAll().size());
        final MvcResult result = mockMvc.perform(get("/order/all").param("driver", username))
                .andExpect(status().isOk())
                .andReturn();
        List<OrderForDriverDTO> dtos = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(5, dtos.size());
        assertEquals(6L, dtos.get(0).getUserId());
        assertEquals(5L, dtos.get(1).getUserId());
        assertEquals(4L, dtos.get(2).getUserId());
        assertEquals(3L, dtos.get(3).getUserId());
        assertEquals(2L, dtos.get(4).getUserId());
    }

    @Test
    @DisplayName("get correct 1st page if request without page but with size")
    @SneakyThrows
    public void getCorrectFirstPage2() {
        String username = "username";
        populateBDWithOrders(OrderStatus.CREATED, username, 6);
        assertEquals(6, orderRepo.findAll().size());
        final MvcResult result = mockMvc.perform(get("/order/all")
                        .param("size", "6")
                        .param("driver", username))
                .andExpect(status().isOk())
                .andReturn();
        List<OrderForDriverDTO> dtos = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(6, dtos.size());
        assertEquals(6L, dtos.get(0).getUserId());
        assertEquals(5L, dtos.get(1).getUserId());
        assertEquals(4L, dtos.get(2).getUserId());
        assertEquals(3L, dtos.get(3).getUserId());
        assertEquals(2L, dtos.get(4).getUserId());
        assertEquals(1L, dtos.get(5).getUserId());
    }

    @Test
    @DisplayName("get only orders of this driver")
    @SneakyThrows
    public void getCorrectFirstPage3() {
        String username = "username";
        String username1 = "username1";
        populateBDWithOrders(OrderStatus.CREATED, username, 6);
        populateBDWithOrders(OrderStatus.CREATED, username1, 1);
        assertEquals(7, orderRepo.findAll().size());
        final MvcResult result = mockMvc.perform(get("/order/all")
                        .param("size", "10")
                        .param("driver", username))
                .andExpect(status().isOk())
                .andReturn();
        List<OrderForDriverDTO> dtos = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(6, dtos.size());
        assertEquals(6L, dtos.get(0).getUserId());
        assertEquals(5L, dtos.get(1).getUserId());
        assertEquals(4L, dtos.get(2).getUserId());
        assertEquals(3L, dtos.get(3).getUserId());
        assertEquals(2L, dtos.get(4).getUserId());
        assertEquals(1L, dtos.get(5).getUserId());

    }

    @Test
    @DisplayName("get all orders with all params in request")
    @SneakyThrows
    public void getAllOrdersWithAllParams() {
        String username = "username";
        populateBDWithOrders(OrderStatus.CREATED, username, 6);
        assertEquals(6, orderRepo.findAll().size());
        final MvcResult result = mockMvc.perform(get("/order/all")
                        .param("page", "0")
                        .param("size", "6")
                        .param("driver", username))
                .andExpect(status().isOk())
                .andReturn();
        List<OrderForDriverDTO> dtos = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(6, dtos.size());
        assertEquals(6L, dtos.get(0).getUserId());
        assertEquals(5L, dtos.get(1).getUserId());
        assertEquals(4L, dtos.get(2).getUserId());
        assertEquals(3L, dtos.get(3).getUserId());
        assertEquals(2L, dtos.get(4).getUserId());
        assertEquals(1L, dtos.get(5).getUserId());
    }

    @Test
    @DisplayName("get correct 2nd page")
    @SneakyThrows
    public void getCorrectSecondPage() {
        String username = "username";
        populateBDWithOrders(OrderStatus.CREATED, username, 6);
        assertEquals(6, orderRepo.findAll().size());
        final MvcResult result = mockMvc.perform(get("/order/all")
                        .param("page", "1")
                        .param("size", "4")
                        .param("driver", username))
                .andExpect(status().isOk())
                .andReturn();
        List<OrderForDriverDTO> dtos = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(2, dtos.size());
        assertEquals(2L, dtos.get(0).getUserId());
        assertEquals(1L, dtos.get(1).getUserId());
    }

    @Test
    @DisplayName("get correct 2nd page with default size")
    @SneakyThrows
    public void getCorrectSecondPage2() {
        String username = "username";
        populateBDWithOrders(OrderStatus.CREATED, username, 6);
        assertEquals(6, orderRepo.findAll().size());
        final MvcResult result = mockMvc.perform(get("/order/all")
                        .param("page", "1")
                        .param("driver", username))
                .andExpect(status().isOk())
                .andReturn();
        List<OrderForDriverDTO> dtos = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(1, dtos.size());
        assertEquals(1L, dtos.get(0).getUserId());
    }

    @Test
    @DisplayName("get empty 2nd page with such params")
    @SneakyThrows
    public void getEmptySecondPage() {
        String username = "username";
        populateBDWithOrders(OrderStatus.CREATED, username, 6);
        assertEquals(6, orderRepo.findAll().size());
        final MvcResult result = mockMvc.perform(get("/order/all")
                        .param("page", "1")
                        .param("size", "10")
                        .param("driver", username))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("[]", result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("get empty page with non-existent driver")
    @SneakyThrows
    public void getEmptyPage() {
        String username = "username";
        populateBDWithOrders(OrderStatus.CREATED, username, 6);
        assertEquals(6, orderRepo.findAll().size());
        final MvcResult result = mockMvc.perform(get("/order/all")
                        .param("driver", "test"))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("[]", result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("no params in request")
    @SneakyThrows
    public void emptyParamsRequest() {
        mockMvc.perform(get("/order/all"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("wrong type size param in request")
    @SneakyThrows
    public void wrongTypeParam() {
        mockMvc.perform(get("/order/all")
                        .param("size", "abc")
                        .param("driver","driver"))
                .andExpect(status().isBadRequest());
    }

}