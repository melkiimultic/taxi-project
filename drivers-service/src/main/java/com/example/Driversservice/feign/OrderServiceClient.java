package com.example.Driversservice.feign;

import com.example.Driversservice.dto.OrderMsgDTO;
import com.example.Driversservice.dto.UpdateOrderDTO;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "orders-service")
public interface OrderServiceClient {

    @PostMapping( value = "/order/update")
    OrderMsgDTO updateOrder (@RequestBody UpdateOrderDTO updateOrderDTO);

}
