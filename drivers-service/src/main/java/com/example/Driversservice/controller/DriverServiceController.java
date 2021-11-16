package com.example.Driversservice.controller;

import com.example.Driversservice.dto.CreateDriverDTO;
import com.example.Driversservice.dto.OrderMsgDTO;
import com.example.Driversservice.dto.UpdateOrderDTO;
import com.example.Driversservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/driver")
@RequiredArgsConstructor
public class DriverServiceController {

    private final DriverService driverService;

    @PostMapping("/create")
    public Long createDriver(@RequestBody CreateDriverDTO createDriverDTO){ return driverService.createDriver(createDriverDTO);}

    @PostMapping("/update")
    public OrderMsgDTO updateOrder(@RequestBody UpdateOrderDTO updateOrderDTO){ return driverService.updateOrder(updateOrderDTO);}

    @GetMapping("/history/{orderId}")
    public List<OrderMsgDTO> getOrderHistory(@PathVariable("orderId") Long orderId){ return driverService.getOrderHistory(orderId);}

}
