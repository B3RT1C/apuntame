package com.apuntame.backend.dto;

import com.apuntame.backend.model.Order;

public class OrderResponseDTO {

    private Order order;
    private String serverTimestamp;

    public OrderResponseDTO() {
    }

    public OrderResponseDTO(Order order, String serverTimestamp) {
        this.order = order;
        this.serverTimestamp = serverTimestamp;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(String serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }
}
