package com.example.orderhistoryservice.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "history")
public class HistoryEntry {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Column(name = "order_id")
    private Long orderId;

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

    @NotNull
    @Column(name = "date_time")
    private LocalDateTime localDateTime;

}
