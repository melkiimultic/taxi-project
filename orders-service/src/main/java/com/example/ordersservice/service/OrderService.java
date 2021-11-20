package com.example.ordersservice.service;

import com.example.ordersservice.domain.Order;
import com.example.ordersservice.domain.OrderStatus;
import com.example.ordersservice.dto.CreateOrderDTO;
import com.example.ordersservice.dto.OrderIdDto;
import com.example.ordersservice.dto.OrderMsgDTO;
import com.example.ordersservice.dto.UpdateOrderDTO;
import com.example.ordersservice.feign.ClientServiceClient;
import com.example.ordersservice.mapper.OrderDtoMapper;
import com.example.ordersservice.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final OrderDtoMapper orderDtoMapper;
    private final KafkaProducerService producerService;

    @Transactional
    public OrderMsgDTO createOrder(CreateOrderDTO createOrderDTO) {
        Order order = new Order();
        order.setStatus(OrderStatus.CREATED);
        order.setUserId(createOrderDTO.getClientId());
        orderRepo.save(order);

        //when order has been created send msg to kafka    TODO if smth goes wrong at 1st or 2nd step?
        OrderMsgDTO orderMsgDTO = orderDtoMapper.toOrderMsgDTO(order);
        orderMsgDTO.setLocalDateTime(LocalDateTime.now());
        producerService.sendMessage(orderMsgDTO);
        return orderMsgDTO;
    }

    @Transactional
    public List<OrderIdDto> getUnassigned() {
        List<Order> unassigned = orderRepo.findByStatus(OrderStatus.CREATED.name());
        return unassigned.stream().map(orderDtoMapper::toOrderIdDTO).collect(Collectors.toList());
    }

    @Transactional
    public OrderMsgDTO updateOrder(UpdateOrderDTO updateOrderDTO) {
        Optional<Order> orderOpt = orderRepo.findById(updateOrderDTO.getOrderId());
        if (orderOpt.isEmpty()) {
            throw new EntityNotFoundException();
        }
        //update fields in DB
        Order order = orderOpt.get();
        order.setDriver(updateOrderDTO.getUsername());
        order.setStatus(OrderStatus.valueOf(updateOrderDTO.getStatus())); //TODO sequence of statuses check?
        Order saved = orderRepo.save(order);
        final Long savedId = saved.getId();

        //when order has been updated send msg to kafka
        OrderMsgDTO orderMsgDTO = orderDtoMapper.toOrderMsgDTO(order);
        orderMsgDTO.setLocalDateTime(LocalDateTime.now());
        producerService.sendMessage(orderMsgDTO);

        return orderMsgDTO;
    }
}

