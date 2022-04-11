package com.example.ordersservice.controller;

import com.example.ordersservice.dto.CreateOrderDTO;
import com.example.ordersservice.dto.OrderMsgDTO;
import com.example.ordersservice.dto.UpdateOrderDTO;
import com.example.ordersservice.dto.UnassignedOrderDto;
import com.example.ordersservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderServiceController {

    private final OrderService orderService;

    @PostMapping(value = "/create")
    public OrderMsgDTO createOrder(@Valid  @RequestBody CreateOrderDTO createOrderDTO) {
        return orderService.createOrder(createOrderDTO);
    }
    @PostMapping(value = "/update")
    public OrderMsgDTO updateOrder(@Valid @RequestBody UpdateOrderDTO updateOrderDTO) {
        return orderService.updateOrder(updateOrderDTO);
    }

    @GetMapping("/unassigned")
    public List<UnassignedOrderDto> getOrders() {return orderService.getUnassigned();}


}
