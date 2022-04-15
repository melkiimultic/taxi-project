package com.example.Driversservice.controller;

import com.example.Driversservice.ResourceConverter;
import com.example.Driversservice.domain.Driver;
import com.example.Driversservice.domain.OrderStatus;
import com.example.Driversservice.dto.OrderMsgDTO;
import com.example.Driversservice.dto.UnassignedOrderDto;
import com.example.Driversservice.dto.UpdateOrderDTO;
import com.example.Driversservice.feign.OrderHistoryServiceClient;
import com.example.Driversservice.feign.OrderServiceClient;
import com.example.Driversservice.repo.DriversRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DriverServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DriversRepo driversRepo;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private TransactionTemplate template;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private OrderServiceClient orderServiceClient;
    @MockBean
    private OrderHistoryServiceClient historyServiceClient;

    @BeforeEach
    @AfterEach
    void clearDB() {
        driversRepo.deleteAll();
    }

    @DisplayName("Create driver happy path")
    @SneakyThrows
    @Test
    public void createDriver() {
        String body = ResourceConverter.getString(new ClassPathResource("requests/createDriver.json"));

        mockMvc.perform(post("/driver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        template.executeWithoutResult(tr -> {
            Optional<Driver> user = driversRepo.findOneByUsername("test");
            assertTrue(user.isPresent());
            Driver driver = user.get();
            assertEquals("First", driver.getFirstName());
            assertEquals("Last", driver.getLastName());
            assertEquals("79031112233", driver.getPhoneNumber());
            assertTrue(encoder.matches("test1", driver.getPassword()));
        });
    }

    @Test
    @DisplayName("Try to create existing driver")
    @SneakyThrows
    public void createExistingDriver() {
        Driver user = new Driver();
        user.setUsername("test");
        user.setPassword(encoder.encode("test1"));
        user.setFirstName("First");
        user.setLastName("Last");
        user.setPhoneNumber("79031112233");
        driversRepo.saveAndFlush(user);
        String body = ResourceConverter.getString(new ClassPathResource("requests/createDriver.json"));

        MvcResult mvcResult = mockMvc.perform(post("/driver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertEquals("User with username " + user.getUsername() +
                " already exists", contentAsString);
    }

    @Test
    @DisplayName("Update an order if driver isn't in DB")
    @SneakyThrows
    public void updateOrderByUnsignedDriver() {
        UpdateOrderDTO updateOrderDTO = new UpdateOrderDTO();
        updateOrderDTO.setDriver("test");
        updateOrderDTO.setOrderId(1L);
        updateOrderDTO.setStatus(OrderStatus.ASSIGNED);
        String body = mapper.writeValueAsString(updateOrderDTO);

        MvcResult mvcResult = mockMvc.perform(post("/driver/order/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();

        assertEquals("Driver with username " + updateOrderDTO.getDriver() +
                " doesn't exist", contentAsString);
    }

    @Test
    @DisplayName("Update the order status by the driver")
    @SneakyThrows
    public void updateOrderByDriver() {
        Driver driver = new Driver();
        driver.setUsername("test");
        driver.setPassword(encoder.encode("test1"));
        driver.setFirstName("First");
        driver.setLastName("Last");
        driver.setPhoneNumber("79031112233");
        driversRepo.saveAndFlush(driver);

        UpdateOrderDTO updateOrderDTO = new UpdateOrderDTO();
        updateOrderDTO.setDriver("test");
        updateOrderDTO.setOrderId(1L);
        updateOrderDTO.setStatus(OrderStatus.ASSIGNED);
        String body = mapper.writeValueAsString(updateOrderDTO);

        OrderMsgDTO orderMsg = new OrderMsgDTO();
        orderMsg.setId(updateOrderDTO.getOrderId());
        orderMsg.setStatus(OrderStatus.ASSIGNED);
        orderMsg.setDriver(driver.getUsername());
        orderMsg.setUserId(2L);
        LocalDate date = LocalDate.of(2021, 12, 12);
        LocalTime time = LocalTime.of(12, 12);
        LocalDateTime localDateTime = LocalDateTime.of(date, time);
        orderMsg.setLocalDateTime(localDateTime);

        when(orderServiceClient.updateOrder(updateOrderDTO)).thenReturn(orderMsg);

        MvcResult mvcResult = mockMvc.perform(post("/driver/order/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();
        OrderMsgDTO response = mapper.readValue(mvcResult.getResponse().getContentAsString(), OrderMsgDTO.class);
        assertEquals(orderMsg, response);
    }

    @Test
    @DisplayName("get order's history")
    @SneakyThrows
    public void getHistory() {
        OrderMsgDTO orderMsg = new OrderMsgDTO();
        orderMsg.setId(1L);
        orderMsg.setStatus(OrderStatus.CREATED);
        orderMsg.setUserId(2L);
        LocalDate date = LocalDate.of(2021, 12, 12);
        LocalTime time = LocalTime.of(12, 12);
        LocalDateTime localDateTime = LocalDateTime.of(date, time);
        orderMsg.setLocalDateTime(localDateTime);

        OrderMsgDTO orderMsg2 = new OrderMsgDTO();
        orderMsg2.setId(1L);
        orderMsg2.setStatus(OrderStatus.ASSIGNED);
        orderMsg2.setDriver("test");
        orderMsg2.setUserId(2L);
        LocalDate date1 = LocalDate.of(2021, 12, 12);
        LocalTime time1 = LocalTime.of(12, 15);
        LocalDateTime localDateTime1 = LocalDateTime.of(date1, time1);
        orderMsg2.setLocalDateTime(localDateTime1);

        List<OrderMsgDTO> msgs = List.of(orderMsg, orderMsg2);

        when(historyServiceClient.getOrderHistory(1L)).thenReturn(msgs);

        MvcResult mvcResult = mockMvc.perform(get("/driver/history/1"))
                .andExpect(status().isOk())
                .andReturn();
        List<OrderMsgDTO> orderMsgDTOS = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(orderMsg, orderMsgDTOS.get(0));
        assertEquals(orderMsg2, orderMsgDTOS.get(1));

    }

    @Test
    @DisplayName("get unassigned orders happy path")
    @SneakyThrows
    public void getUnassigned() {
        UnassignedOrderDto unassignedOrderDto = new UnassignedOrderDto();
        unassignedOrderDto.setId(1L);
        unassignedOrderDto.setDeparture("from");
        unassignedOrderDto.setArrival("to");
        List<UnassignedOrderDto> orderDtoList = Collections.singletonList(unassignedOrderDto);

        when(orderServiceClient.getUnassigned()).thenReturn(orderDtoList);

        MvcResult mvcResult = mockMvc.perform(get("/driver/unassigned"))
                .andExpect(status().isOk())
                .andReturn();
        List<UnassignedOrderDto> unassignedFromRes = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(1, unassignedFromRes.size());
        assertEquals(unassignedOrderDto.getId(), unassignedFromRes.get(0).getId());
        assertEquals(unassignedOrderDto.getArrival(), unassignedFromRes.get(0).getArrival());
        assertEquals(unassignedOrderDto.getDeparture(), unassignedFromRes.get(0).getDeparture());
    }

    @Test
    @DisplayName("unassigned orders don't exist")
    @SneakyThrows
    public void getEmptyListOfUnassigned() {
        List<UnassignedOrderDto> orderDtoList = new ArrayList<>();
        when(orderServiceClient.getUnassigned()).thenReturn(orderDtoList);
        MvcResult mvcResult = mockMvc.perform(get("/driver/unassigned"))
                .andExpect(status().isOk())
                .andReturn();
        List<UnassignedOrderDto> unassignedFromRes = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertEquals(0, unassignedFromRes.size());
    }
}