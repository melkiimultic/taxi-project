package com.example.Driversservice.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UnassignedOrderDto {

    @NotNull
    private Long id;

    @NotEmpty
    private String departure;

    @NotEmpty
    private String arrival;
}
