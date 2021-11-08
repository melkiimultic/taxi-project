package com.example.ordersservice.dto;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class OrderIdDto {

    @NotNull
    @NotEmpty
    private Long id;
}
