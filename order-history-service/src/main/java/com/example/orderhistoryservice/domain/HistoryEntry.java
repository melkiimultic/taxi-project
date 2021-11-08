package com.example.orderhistoryservice.domain;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "History")
public class HistoryEntry {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @NotEmpty
    @Column(name = "orderId")
    private Long orderId;

    @NotNull
    @NotEmpty
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    @NotNull
    @NotEmpty
    private Long userId;

    @NotNull
    @NotEmpty
    private String driver;

    @NotNull
    @NotEmpty
    private LocalDateTime localDateTime;

}
