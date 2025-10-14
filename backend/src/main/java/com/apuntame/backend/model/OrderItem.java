package com.apuntame.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "Order_Items")
public class OrderItem {

    @EmbeddedId
    @JsonIgnore
    private OrderItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orderId")
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "order_fk"))
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("itemId")
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "item_fk"))
    private Item item;

    @Column(nullable = false)
    private Integer amount;

    public OrderItem() {
    }

    public OrderItem(Order order, Item item, Integer amount) {
        this.order = order;
        this.item = item;
        this.amount = amount;
        this.id = new OrderItemId(order.getId(), item.getId());
    }

    public OrderItemId getId() {
        return id;
    }

    public void setId(OrderItemId id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
        if (this.id == null) {
            this.id = new OrderItemId();
        }
        this.id.setOrderId(order.getId());
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        if (this.id == null) {
            this.id = new OrderItemId();
        }
        this.id.setItemId(item.getId());
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
