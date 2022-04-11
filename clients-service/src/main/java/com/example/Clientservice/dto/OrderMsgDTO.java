package com.example.Clientservice.dto;

import com.example.Clientservice.domain.OrderStatus;
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

    @NotNull
    private LocalDateTime localDateTime;
}
