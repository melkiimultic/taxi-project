package com.example.ordersservice.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @NotNull
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "driver")
    private String driver;

    @NotEmpty
    @Column(name = "departure")
    private String departure;

    @NotEmpty
    @Column(name = "arrival")
    private String arrival;

}
