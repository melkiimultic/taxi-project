package com.example.orderhistoryservice.service;

import com.example.orderhistoryservice.domain.HistoryEntry;
import com.example.orderhistoryservice.dto.OrderMsgDTO;
import com.example.orderhistoryservice.mapper.EntryDtoMapper;
import com.example.orderhistoryservice.repo.HistoryEntryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoryEntryRepo historyEntryRepo;
    private final EntryDtoMapper entryDtoMapper;

    @Transactional
    public List<OrderMsgDTO> getOrderHistory(Long orderId) {
        List<HistoryEntry> entries = historyEntryRepo.findAllByOrderId(orderId);
        if (entries.isEmpty()) {
            throw new EntityNotFoundException("Order №" + orderId + " doesn't exist");
        }
        return entries.stream().map(entryDtoMapper::toDTO)
                .sorted(Comparator.comparing(OrderMsgDTO::getLocalDateTime)).collect(Collectors.toList());
    }

}
