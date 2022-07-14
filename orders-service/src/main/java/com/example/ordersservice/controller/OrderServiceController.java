package com.example.ordersservice.controller;

import com.example.ordersservice.dto.CreateOrderDTO;
import com.example.ordersservice.dto.OrderForDriverDTO;
import com.example.ordersservice.dto.OrderMsgDTO;
import com.example.ordersservice.dto.UnassignedOrderDto;
import com.example.ordersservice.dto.UpdateOrderDTO;
import com.example.ordersservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderServiceController {

    private final OrderService orderService;

    @PostMapping()
    public OrderMsgDTO createOrder(@Valid  @RequestBody CreateOrderDTO createOrderDTO) {
        return orderService.createOrder(createOrderDTO);
    }

    @PostMapping(value = "/update")
    public OrderMsgDTO updateOrder(@Valid @RequestBody UpdateOrderDTO updateOrderDTO) {
        return orderService.updateOrder(updateOrderDTO);
    }

    @GetMapping("/unassigned")
    public List<UnassignedOrderDto> getOrders() {return orderService.getUnassigned();}

    @GetMapping("/all")
    public List<OrderForDriverDTO> getOrdersByDriver(@RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value = "size", defaultValue = "5") int size,
                                                     @RequestParam("driver") String driver){

        Pageable reqSortedById = PageRequest.of(page, size, Sort.by("id").descending());
        return orderService.getAllOrdersByDriver(driver, reqSortedById);
    }

}
