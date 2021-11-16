package com.example.orderhistoryservice.domain;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "history")
public class HistoryEntry {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @NotEmpty
    @Column(name = "order_id")
    private Long orderId;

    @NotNull
    @NotEmpty
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @NotNull
    @NotEmpty
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "driver")
    private String driver;

    @NotNull
    @NotEmpty
    @Column(name = "date_time")
    private LocalDateTime localDateTime;

}
