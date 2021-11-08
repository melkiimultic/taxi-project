package com.example.ordersservice.service;

import com.example.ordersservice.dto.OrderMsgDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, OrderMsgDTO> kafkaTemplate;

    public void sendMessage(OrderMsgDTO order){
        kafkaTemplate.send("orderHistory", String.valueOf(order.getId()), order);
    }

}
