package com.example.Driversservice.dto;

import com.example.Driversservice.domain.OrderStatus;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UpdateOrderDTO {

    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @NotEmpty
    private Long orderId;

    @NotNull
    @NotEmpty
    private OrderStatus status;



}
