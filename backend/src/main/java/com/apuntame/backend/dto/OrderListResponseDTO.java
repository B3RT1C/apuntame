package com.apuntame.backend.dto;

import com.apuntame.backend.model.Order;
import java.util.List;

public class OrderListResponseDTO {

    private List<Order> orders;
    private String serverTimestamp;

    public OrderListResponseDTO() {
    }

    public OrderListResponseDTO(List<Order> orders, String serverTimestamp) {
        this.orders = orders;
        this.serverTimestamp = serverTimestamp;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public String getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(String serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }
}
