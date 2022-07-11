package com.example.Driversservice.feign;

import com.example.Driversservice.dto.OrderForDriverDTO;
import com.example.Driversservice.dto.OrderMsgDTO;
import com.example.Driversservice.dto.UnassignedOrderDto;
import com.example.Driversservice.dto.UpdateOrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "orders-service")
public interface OrderServiceClient {

    @PostMapping(value = "/order/update")
    OrderMsgDTO updateOrder (@RequestBody UpdateOrderDTO updateOrderDTO);

    @GetMapping(value = "/order/unassigned")
    List<UnassignedOrderDto> getUnassigned();

    @GetMapping(value = "/order/all")
    List<OrderForDriverDTO> getOrdersByDriver(@RequestParam(value = "page", defaultValue = "1") int page,
                                              @RequestParam(value = "size", defaultValue = "5") int size,
                                              @RequestParam("driver") String driver);

}
