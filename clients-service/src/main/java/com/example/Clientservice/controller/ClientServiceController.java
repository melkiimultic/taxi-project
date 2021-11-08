package com.example.Clientservice.controller;

import com.example.Clientservice.dto.CreateClientDTO;
import com.example.Clientservice.dto.CreateOrderDTO;
import com.example.Clientservice.dto.OrderMsgDTO;
import com.example.Clientservice.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientServiceController {

    private final ClientService clientService;

    @PostMapping("/create")
    public Long createClient(@RequestBody CreateClientDTO createClientDTO){ return clientService.createClient(createClientDTO);}

    @PostMapping("/createOrder")
    public Long createOrder(@RequestBody CreateOrderDTO createOrderDTO){ return clientService.createOrder(createOrderDTO);}

    @PostMapping("/orderInfo")
    public String provideOrderInfo(@RequestBody OrderMsgDTO orderMsgDTO){ return clientService.provideOrderInfo(orderMsgDTO);}

}
