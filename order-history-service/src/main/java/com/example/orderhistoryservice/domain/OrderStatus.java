package com.example.orderhistoryservice.domain;

public enum OrderStatus {
    CREATED("Created"), ASSIGNED ("Assigned"), INPROGRESS("InProgress"), CLOSED("Closed");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }
}
