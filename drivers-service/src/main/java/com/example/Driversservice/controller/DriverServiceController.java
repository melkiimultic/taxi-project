package com.example.Driversservice.controller;

import com.example.Driversservice.dto.CreateDriverDTO;
import com.example.Driversservice.dto.OrderForDriverDTO;
import com.example.Driversservice.dto.OrderMsgDTO;
import com.example.Driversservice.dto.UnassignedOrderDto;
import com.example.Driversservice.dto.UpdateOrderDTO;
import com.example.Driversservice.feign.OrderServiceClient;
import com.example.Driversservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/driver")
@RequiredArgsConstructor
public class DriverServiceController {

    private final DriverService driverService;
    private final OrderServiceClient orderServiceClient;

    @PostMapping()
    public Long createDriver(@Valid @RequestBody CreateDriverDTO createDriverDTO) {
        return driverService.createDriver(createDriverDTO);
    }

    @PostMapping("/order/update")
    public OrderMsgDTO updateOrder(@Valid @RequestBody UpdateOrderDTO updateOrderDTO) {
        return driverService.updateOrder(updateOrderDTO);
    }

    @GetMapping("/history/{orderId}")
    public List<OrderMsgDTO> getOrderHistory(@PathVariable("orderId") Long orderId) {
        return driverService.getOrderHistory(orderId);
    }

    @GetMapping("/unassigned")
    public List<UnassignedOrderDto> getUnassigned() {
        return orderServiceClient.getUnassigned();
    }

    @GetMapping("/orders")
    public List<OrderForDriverDTO> getOrdersByDriver(@RequestParam(value = "page", defaultValue = "1") int page,
                                                     @RequestParam(value = "size", defaultValue = "5") int size,
                                                     @RequestParam("driver") String driver) {
        driverService.checkDriver(driver);
        return orderServiceClient.getOrdersByDriver(page, size, driver);
    }
}
