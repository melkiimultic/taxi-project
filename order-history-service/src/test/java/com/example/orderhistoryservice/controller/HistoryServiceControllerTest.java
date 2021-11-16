package com.example.orderhistoryservice.controller;

import com.example.orderhistoryservice.domain.HistoryEntry;
import com.example.orderhistoryservice.domain.OrderStatus;
import com.example.orderhistoryservice.dto.OrderMsgDTO;
import com.example.orderhistoryservice.mapper.EntryDtoMapper;
import com.example.orderhistoryservice.repo.HistoryEntryRepo;
import com.example.orderhistoryservice.service.HistoryService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HistoryServiceControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private HistoryEntryRepo historyRepo;
    @Autowired
    TransactionTemplate template;
    @Autowired
    HistoryService historyService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    EntryDtoMapper dtoMapper;

    @BeforeEach
    @AfterEach
    private void cleanBD() {
        historyRepo.deleteAll();
    }

    private HistoryEntry createEntry(Long orderId, OrderStatus status, Long userId, String driver, LocalDateTime dateTime) {
        HistoryEntry entry = new HistoryEntry();
        entry.setOrderId(orderId);
        entry.setStatus(status);
        entry.setUserId(userId);
        entry.setDriver(driver);
        entry.setLocalDateTime(dateTime);
        return entry;
    }

    @Test
    @DisplayName("Get order's history happy path")
    @SneakyThrows
    public void getHistory() {
        LocalDate firstDate = LocalDate.of(2021, 12, 12);
        LocalTime firstTime = LocalTime.of(12, 12);
        LocalDateTime firstLocalDateTime = LocalDateTime.of(firstDate, firstTime);
        HistoryEntry first = createEntry(1L, OrderStatus.CREATED, 1L, "test", firstLocalDateTime);

        LocalDate secondDate = LocalDate.of(2021, 12, 12);
        LocalTime secondTime = LocalTime.of(12, 13);
        LocalDateTime secondLocalDateTime = LocalDateTime.of(secondDate, secondTime);
        HistoryEntry second = createEntry(1L, OrderStatus.ASSIGNED, 1L, "test", secondLocalDateTime);

        historyRepo.saveAllAndFlush(List.of(first, second));

        MvcResult mvcResult = mockMvc.perform(get("/history/1"))
                .andExpect(status().isOk())
                .andReturn();
        List<OrderMsgDTO> orderMsgDTOS = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<OrderMsgDTO>>() {
        });
        assertEquals(dtoMapper.toDTO(first), orderMsgDTOS.get(0));
        assertEquals(dtoMapper.toDTO(second), orderMsgDTOS.get(1));

    }

    @Test
    @DisplayName("Try to get order that doesn't exist")
    @SneakyThrows
    public void getHistoryByWrongId() {
        Long orderId = 1L;
        MvcResult mvcResult = mockMvc.perform(get("/history/" + orderId))
                .andExpect(status().isNotFound())
                .andReturn();
        assertEquals("Order №" + orderId + " doesn't exist", mvcResult.getResponse().getContentAsString());
    }
}