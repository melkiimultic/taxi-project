package com.example.ordersservice.dto;

import com.example.ordersservice.domain.OrderStatus;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class OrderMsgDTO {

    @NotNull
    private Long id;

    @NotNull
    private OrderStatus status;

    @NotNull
    private Long userId;

    @NotEmpty
    private String departure;

    @NotEmpty
    private String arrival;

    private String driver;

    @NotNull
    private LocalDateTime localDateTime;
}
