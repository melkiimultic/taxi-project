package com.example.Clientservice.dto;

import com.example.Clientservice.domain.OrderStatus;
import com.sun.istack.NotNull;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
public class OrderMsgDTO {

    @NotNull
    @NotEmpty
    private Long id;

    @NotNull
    @NotEmpty
    private OrderStatus status;

    @NotNull
    @NotEmpty
    private Long userId;

    @NotNull
    @NotEmpty
    private LocalDateTime localDateTime;
}
