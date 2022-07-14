package com.example.ordersservice.mapper;

import com.example.ordersservice.domain.Order;
import com.example.ordersservice.domain.OrderStatus;
import com.example.ordersservice.dto.OrderForDriverDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderDtoMapperTest {

   private OrderDtoMapper mapper = new OrderDtoMapperImpl();

   private com.example.ordersservice.domain.Order createUnassignedOrder(OrderStatus status, Long userId, String driver) {
      Order order = new Order();
      order.setStatus(status);
      order.setUserId(userId);
      order.setDeparture("from");
      order.setArrival("to");
      order.setDriver(driver);
      return order;
   }

   @Test
   public void toOrderForDriverDTO(){
      final Order order = createUnassignedOrder(OrderStatus.CREATED, 1L, "test");
      final OrderForDriverDTO orderForDriverDTO = mapper.toOrderForDriverDTO(order);
      assertEquals(orderForDriverDTO.getId(),order.getId());
      assertEquals(orderForDriverDTO.getArrival(),order.getArrival());
      assertEquals(orderForDriverDTO.getDeparture(),orderForDriverDTO.getDeparture());
      assertEquals(orderForDriverDTO.getUserId(),order.getUserId());
      assertEquals(orderForDriverDTO.getStatus(),order.getStatus());
   }

}