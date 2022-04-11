package com.example.ordersservice.dto;

import com.example.ordersservice.domain.OrderStatus;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UpdateOrderDTO {

    @NotEmpty
    private String driver;

    @NotNull
    private Long orderId;

    @NotNull
    private OrderStatus status;
}
