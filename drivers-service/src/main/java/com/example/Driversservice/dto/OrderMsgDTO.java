package com.example.Driversservice.dto;

import com.example.Driversservice.domain.OrderStatus;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class OrderMsgDTO {

    @NotNull
    private Long id;  //Order ID

    @NotNull
    private OrderStatus status;

    @NotNull
    private Long userId;

    @NotEmpty
    private String driver;

    @NotNull
    private LocalDateTime localDateTime;
}
