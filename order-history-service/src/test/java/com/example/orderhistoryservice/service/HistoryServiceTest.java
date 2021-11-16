package com.example.orderhistoryservice.service;

import com.example.orderhistoryservice.domain.HistoryEntry;
import com.example.orderhistoryservice.domain.OrderStatus;
import com.example.orderhistoryservice.dto.OrderMsgDTO;
import com.example.orderhistoryservice.mapper.EntryDtoMapper;
import com.example.orderhistoryservice.repo.HistoryEntryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {

    @Mock
    private HistoryEntryRepo historyEntryRepo;
    private EntryDtoMapper entryMapper = Mappers.getMapper(EntryDtoMapper.class);
    private HistoryService historyService;

    @BeforeEach
    private void setUp() {
        historyService = new HistoryService(historyEntryRepo, entryMapper);
    }

    private HistoryEntry createEntry(Long id, Long orderId, OrderStatus status, Long userId, String driver, LocalDateTime dateTime) {
        HistoryEntry entry = new HistoryEntry();
        entry.setId(id);
        entry.setOrderId(orderId);
        entry.setStatus(status);
        entry.setUserId(userId);
        entry.setDriver(driver);
        entry.setLocalDateTime(dateTime);
        return entry;
    }

    @Test
    @DisplayName("Get list of OrderMsgDTO sorted by datetime")
    public void getSortedList() {
        LocalDate firstDate = LocalDate.of(2021, 12, 12);
        LocalTime firstTime = LocalTime.of(12, 12);
        LocalDateTime firstLocalDateTime = LocalDateTime.of(firstDate, firstTime);
        HistoryEntry first = createEntry(1L, 1L, OrderStatus.CREATED, 1L, "test", firstLocalDateTime);
        OrderMsgDTO firstDto = entryMapper.toDTO(first);

        LocalDate secondDate = LocalDate.of(2021, 12, 12);
        LocalTime secondTime = LocalTime.of(12, 13);
        LocalDateTime secondLocalDateTime = LocalDateTime.of(secondDate, secondTime);
        HistoryEntry second = createEntry(1L, 1L, OrderStatus.ASSIGNED, 1L, "test", secondLocalDateTime);
        OrderMsgDTO secondDto = entryMapper.toDTO(second);

        LocalDate thirdDate = LocalDate.of(2021, 12, 12);
        LocalTime thirdTime = LocalTime.of(12, 14);
        LocalDateTime thirdLocalDateTime = LocalDateTime.of(thirdDate, thirdTime);
        HistoryEntry third = createEntry(1L, 1L, OrderStatus.INPROGRESS, 1L, "test", thirdLocalDateTime);
        OrderMsgDTO thirdDto = entryMapper.toDTO(third);

        List<HistoryEntry> reversedList = List.of(third, second, first);
        when(historyEntryRepo.findAllByOrderId(1L)).thenReturn(reversedList);

        List<OrderMsgDTO> sortedList = historyService.getOrderHistory(1L);
        assertEquals(firstDto, sortedList.get(0));
        assertEquals(secondDto, sortedList.get(1));
        assertEquals(thirdDto, sortedList.get(2));

    }

}