package com.example.Driversservice.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Data
@Entity
@Table(name = "drivers")
public class Driver {
    @Id
    @GeneratedValue()
    private Long id;

    @NotEmpty
    @Column(name = "username", unique = true)
    private String username;

    @NotEmpty
    @Column(name = "password")
    private String password;

    @NotEmpty
    @Column(name = "firstname")
    private String firstName;

    @NotEmpty
    @Column(name = "lastname")
    private String lastName;

    @NotEmpty
    @Column(name = "phone_number")
    private String phoneNumber;
}
