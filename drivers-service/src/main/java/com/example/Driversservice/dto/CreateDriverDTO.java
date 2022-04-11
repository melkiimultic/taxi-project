package com.example.Driversservice.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CreateDriverDTO {
    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    @NotEmpty
    private String firstname;

    @NotEmpty
    private String lastname;

    @NotEmpty
    private String phoneNumber;
}
