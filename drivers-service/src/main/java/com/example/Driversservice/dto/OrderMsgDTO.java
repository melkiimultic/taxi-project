package com.example.Driversservice.dto;

import com.example.Driversservice.domain.OrderStatus;
import com.sun.istack.NotNull;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
public class OrderMsgDTO {

    @NotNull
    @NotEmpty
    private Long id;  //Order ID

    @NotNull
    @NotEmpty
    private OrderStatus status;

    @NotNull
    @NotEmpty
    private Long userId;

    @NotNull
    @NotEmpty
    private String driver;

    @NotNull
    @NotEmpty
    private LocalDateTime localDateTime;
}
