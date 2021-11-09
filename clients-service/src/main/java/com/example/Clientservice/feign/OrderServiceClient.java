package com.example.Clientservice.feign;

import com.example.Clientservice.dto.CreateOrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "orders-service")
public interface OrderServiceClient {

    @PostMapping(value = "/order/create")
    Long createOrder(@RequestBody CreateOrderDTO createOrderDTO);
}
