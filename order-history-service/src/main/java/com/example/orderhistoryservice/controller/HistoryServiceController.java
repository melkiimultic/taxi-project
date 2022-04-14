package com.example.orderhistoryservice.controller;

import com.example.orderhistoryservice.dto.OrderMsgDTO;
import com.example.orderhistoryservice.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/history" )
public class HistoryServiceController {

    private final HistoryService historyService;

    @GetMapping("/{orderId}")
    public List<OrderMsgDTO> getOrderHistory(@PathVariable ("orderId") Long orderId){
        return historyService.getOrderHistory(orderId);
    }

}
