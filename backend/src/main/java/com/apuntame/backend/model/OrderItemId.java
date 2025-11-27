package com.apuntame.backend.model;

import java.io.Serializable;
import java.util.Objects;

public class OrderItemId implements Serializable {

    private Integer order;
    private Integer item;

    public OrderItemId() {
    }

    public OrderItemId(Integer order, Integer item) {
        this.order = order;
        this.item = item;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemId that = (OrderItemId) o;
        return Objects.equals(order, that.order) && Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, item);
    }
}