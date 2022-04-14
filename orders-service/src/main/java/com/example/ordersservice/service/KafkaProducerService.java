package com.example.ordersservice.service;

import com.example.ordersservice.dto.OrderMsgDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, OrderMsgDTO> kafkaTemplate;

    public void sendMessage(OrderMsgDTO order){
        ListenableFuture<SendResult<String, OrderMsgDTO>> orderHistory =
                kafkaTemplate.send("orderHistory", String.valueOf(order.getId()), order);
        kafkaTemplate.flush();

        orderHistory.addCallback(new ListenableFutureCallback<>() {

            @Override
            public void onSuccess(SendResult<String, OrderMsgDTO> result) {
                System.out.println("Sent message=[" + order +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }
            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Unable to send message=["
                        + order + "] due to : " + ex.getMessage());
            }
        });

    }

}
