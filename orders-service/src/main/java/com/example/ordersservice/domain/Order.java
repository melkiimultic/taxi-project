package com.example.ordersservice.domain;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Data
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @NotEmpty
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @NotNull
    @NotEmpty
    @Column(name = "userId")
    private Long userId;

    @Column(name = "driver")
    private String driver;




}
