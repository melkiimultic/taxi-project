package com.example.ordersservice.dto;

import com.example.ordersservice.domain.OrderStatus;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UpdateOrderDTO {

    @NotNull
    @NotEmpty
    private String driver;

    @NotNull
    @NotEmpty
    private Long orderId;

    @NotNull
    @NotEmpty
    private OrderStatus status;
}
