package com.example.orderhistoryservice.service;

import com.example.orderhistoryservice.domain.HistoryEntry;
import com.example.orderhistoryservice.domain.OrderStatus;
import com.example.orderhistoryservice.dto.OrderMsgDTO;
import com.example.orderhistoryservice.mapper.EntryDtoMapper;
import com.example.orderhistoryservice.repo.HistoryEntryRepo;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final HistoryEntryRepo historyEntryRepo;
    private final EntryDtoMapper entryDtoMapper;

    @KafkaListener(topics = "orderHistory", groupId = "history")
    @Transactional
    public void consume(ConsumerRecord<String, OrderMsgDTO> cr) {
        HistoryEntry historyEntry = entryDtoMapper.fromDTO(cr.value());
        historyEntryRepo.save(historyEntry);
    }

}
