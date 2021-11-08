package com.example.ordersservice.controller;

import com.example.ordersservice.dto.CreateOrderDTO;
import com.example.ordersservice.dto.UpdateOrderDTO;
import com.example.ordersservice.dto.OrderIdDto;
import com.example.ordersservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderServiceController {

    private final OrderService orderService;

    @PostMapping(value = "/create")
    public Long createOrder(@RequestBody CreateOrderDTO createOrderDTO) {
        return orderService.createOrder(createOrderDTO);
    }
    @PostMapping(value = "/update")
    public Long updateOrder(@RequestBody UpdateOrderDTO updateOrderDTO) {
        return orderService.updateOrder(updateOrderDTO);
    }

    @GetMapping("/unassigned")
    public List<OrderIdDto> getOrders() {return orderService.getUnassigned();}

}
