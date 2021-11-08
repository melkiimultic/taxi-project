package com.example.Clientservice.dto;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CreateClientDTO {
    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @NotEmpty
    private String firstName;

    @NotNull
    @NotEmpty
    private String lastName;

    @NotNull
    @NotEmpty
    private String phoneNumber;


}
