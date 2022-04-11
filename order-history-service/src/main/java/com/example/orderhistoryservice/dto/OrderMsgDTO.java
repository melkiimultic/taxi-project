package com.example.orderhistoryservice.dto;

import com.example.orderhistoryservice.domain.OrderStatus;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class OrderMsgDTO {

    @NotNull
    private Long id; //orderId

    @NotNull
    private OrderStatus status;

    @NotNull
    private Long userId;

    @NotEmpty
    private String driver;

    @NotEmpty
    private String departure;

    @NotEmpty
    private String arrival;

    @NotNull
    private LocalDateTime localDateTime;
}
