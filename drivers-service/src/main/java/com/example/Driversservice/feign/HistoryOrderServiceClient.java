package com.example.Driversservice.feign;

import com.example.Driversservice.dto.OrderMsgDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient
public interface HistoryOrderServiceClient {

    @GetMapping(value = "/history/{orderId}")
    List<OrderMsgDTO> getOrderHistory(@PathVariable("orderId") Long orderId);
}
