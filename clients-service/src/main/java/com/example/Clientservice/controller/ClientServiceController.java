package com.example.Clientservice.controller;

import com.example.Clientservice.dto.CreateClientDTO;
import com.example.Clientservice.dto.CreateOrderDTO;
import com.example.Clientservice.dto.OrderMsgDTO;
import com.example.Clientservice.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientServiceController {

    private final ClientService clientService;

    @PostMapping("/create")
    public Long createClient(@Valid @RequestBody CreateClientDTO createClientDTO){ return clientService.createClient(createClientDTO);}

    @PostMapping("/createOrder")
    public OrderMsgDTO createOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO){ return clientService.createOrder(createOrderDTO);}

    @GetMapping("/hello")
    public String greeting(){
        return "hello";
    }
}
