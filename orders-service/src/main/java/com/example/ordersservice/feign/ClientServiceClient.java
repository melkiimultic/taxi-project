package com.example.ordersservice.feign;

import com.example.ordersservice.dto.OrderMsgDTO;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "clients-service")
@LoadBalancerClient(name = "clients-service")
public interface ClientServiceClient {

    @PostMapping( value = "/client/orderInfo")
    Long provideOrderInfo(@RequestBody OrderMsgDTO orderMsgDTO);
}
