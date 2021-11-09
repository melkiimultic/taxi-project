package com.example.Driversservice.feign;

import com.example.Driversservice.dto.OrderMsgDTO;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-history-service")
public interface OrderHistoryServiceClient {

    @GetMapping(value = "/history/{orderId}")
    List<OrderMsgDTO> getOrderHistory(@PathVariable("orderId") Long orderId);
}
