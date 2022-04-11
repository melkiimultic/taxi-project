package com.example.ordersservice.mapper;

import com.example.ordersservice.domain.Order;
import com.example.ordersservice.dto.UnassignedOrderDto;
import com.example.ordersservice.dto.OrderMsgDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderDtoMapper {

    OrderMsgDTO toOrderMsgDTO(Order order);

    UnassignedOrderDto toOrderIdDTO(Order order);
}
