package com.example.Clientservice.feign;

import com.example.Clientservice.dto.CreateOrderDTO;
import com.example.Clientservice.dto.OrderMsgDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(name = "orders-service")
public interface OrderServiceClient {

    @PostMapping(value = "/order")
    OrderMsgDTO createOrder( @RequestBody CreateOrderDTO createOrderDTO);

}
