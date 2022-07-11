package com.example.ordersservice.service;

import com.example.ordersservice.domain.Order;
import com.example.ordersservice.domain.OrderStatus;
import com.example.ordersservice.dto.CreateOrderDTO;
import com.example.ordersservice.dto.OrderForDriverDTO;
import com.example.ordersservice.dto.OrderMsgDTO;
import com.example.ordersservice.dto.UnassignedOrderDto;
import com.example.ordersservice.dto.UpdateOrderDTO;
import com.example.ordersservice.mapper.OrderDtoMapper;
import com.example.ordersservice.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        order.setDeparture(createOrderDTO.getDeparture());
        order.setArrival(createOrderDTO.getArrival());
        Order saved = orderRepo.save(order);

        //when order has been created send msg to kafka
        OrderMsgDTO orderMsgDTO = orderDtoMapper.toOrderMsgDTO(saved);
        orderMsgDTO.setLocalDateTime(LocalDateTime.now());
        producerService.sendMessage(orderMsgDTO);
        return orderMsgDTO;
    }

    @Transactional
    public List<UnassignedOrderDto> getUnassigned() {
        List<Order> unassigned = orderRepo.findByStatusAndDriverIsNull(OrderStatus.CREATED);
        return unassigned.stream().map(orderDtoMapper::toOrderIdDTO).collect(Collectors.toList());
    }

    @Transactional
    public OrderMsgDTO updateOrder(UpdateOrderDTO updateOrderDTO) {
        Optional<Order> orderOpt = orderRepo.findById(updateOrderDTO.getOrderId());
        if (orderOpt.isEmpty()) {
            throw new EntityNotFoundException("Wrong order identifier!");
        }
        Order order = orderOpt.get();
        if (OrderStatus.CLOSED.equals(order.getStatus()) ||
                !OrderStatus.next(order.getStatus()).equals(updateOrderDTO.getStatus())) {
            throw new IllegalArgumentException();
        }
        //update fields in DB
        order.setStatus(updateOrderDTO.getStatus());
        order.setDriver(updateOrderDTO.getDriver());
        orderRepo.save(order);

        //when order has been updated send msg to kafka
        OrderMsgDTO orderMsgDTO = orderDtoMapper.toOrderMsgDTO(order);
        orderMsgDTO.setLocalDateTime(LocalDateTime.now());
        producerService.sendMessage(orderMsgDTO);

        return orderMsgDTO;
    }

    public List<OrderForDriverDTO> getAllOrdersByDriver(String driver, Pageable pageable) {
        final List<Order> orders = orderRepo.findByDriver(driver, pageable);
        List<OrderForDriverDTO> dtos = new ArrayList<>();
        orders.forEach((order -> {
            final OrderForDriverDTO orderForDriverDTO = orderDtoMapper.toOrderForDriverDTO(order);
            dtos.add(orderForDriverDTO);
        }));
        return dtos;
    }
}

