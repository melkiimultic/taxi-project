package com.example.ordersservice.domain;

public enum OrderStatus {
    CREATED("Created"), ASSIGNED ("Assigned"), INPROGRESS("InProgress"), CLOSED("Closed");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public static OrderStatus next(OrderStatus status) {
        if (OrderStatus.CREATED.equals(status)) {
            return OrderStatus.ASSIGNED;
        } else if (OrderStatus.ASSIGNED.equals(status)) {
            return OrderStatus.INPROGRESS;
        } else
            return OrderStatus.CLOSED;
    }
}
