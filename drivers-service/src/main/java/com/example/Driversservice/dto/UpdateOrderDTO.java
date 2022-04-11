package com.example.Driversservice.dto;

import com.example.Driversservice.domain.OrderStatus;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UpdateOrderDTO {

    @NotEmpty
    private String username;

    @NotNull
    private Long orderId;

    @NotNull
    private OrderStatus status;

}
