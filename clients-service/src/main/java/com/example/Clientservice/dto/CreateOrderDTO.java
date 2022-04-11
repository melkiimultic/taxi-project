package com.example.Clientservice.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateOrderDTO {

    @NotNull
    private Long clientId;

    @NotEmpty
    private String departure;

    @NotEmpty
    private String arrival;


}
