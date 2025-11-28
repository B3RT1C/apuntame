package com.apuntame.backend.dto;

import com.apuntame.backend.enums.DeliveryStatus;
import com.apuntame.backend.enums.PaymentStatus;
import com.apuntame.backend.enums.PreparationStatus;

import java.math.BigDecimal;
import java.util.List;

public class OrderEventDTO {

    public enum EventType {
        CREATED, UPDATED
    }

    private EventType eventType;
    private Integer id;
    private String table;
    private PaymentStatus paymentStatus;
    private PreparationStatus preparationStatus;
    private DeliveryStatus deliveryStatus;
    private String creationDate;
    private String paidAt;
    private String preparedAt;
    private String deliveredAt;
    private String takenBy;
    private String chargedBy;
    private String preparedBy;
    private String deliveredBy;
    private List<OrderItemDTO> orderItems;

    public static class OrderItemDTO {
        private Integer itemId;
        private String itemName;
        private BigDecimal itemPrice;
        private Integer amount;

        public OrderItemDTO() {}

        public OrderItemDTO(Integer itemId, String itemName, BigDecimal itemPrice, Integer amount) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.itemPrice = itemPrice;
            this.amount = amount;
        }

        public Integer getItemId() { return itemId; }
        public void setItemId(Integer itemId) { this.itemId = itemId; }
        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        public BigDecimal getItemPrice() { return itemPrice; }
        public void setItemPrice(BigDecimal itemPrice) { this.itemPrice = itemPrice; }
        public Integer getAmount() { return amount; }
        public void setAmount(Integer amount) { this.amount = amount; }
    }

    public OrderEventDTO() {}

    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTable() { return table; }
    public void setTable(String table) { this.table = table; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public PreparationStatus getPreparationStatus() { return preparationStatus; }
    public void setPreparationStatus(PreparationStatus preparationStatus) { this.preparationStatus = preparationStatus; }

    public DeliveryStatus getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(DeliveryStatus deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public String getCreationDate() { return creationDate; }
    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }

    public String getPaidAt() { return paidAt; }
    public void setPaidAt(String paidAt) { this.paidAt = paidAt; }

    public String getPreparedAt() { return preparedAt; }
    public void setPreparedAt(String preparedAt) { this.preparedAt = preparedAt; }

    public String getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(String deliveredAt) { this.deliveredAt = deliveredAt; }

    public String getTakenBy() { return takenBy; }
    public void setTakenBy(String takenBy) { this.takenBy = takenBy; }

    public String getChargedBy() { return chargedBy; }
    public void setChargedBy(String chargedBy) { this.chargedBy = chargedBy; }

    public String getPreparedBy() { return preparedBy; }
    public void setPreparedBy(String preparedBy) { this.preparedBy = preparedBy; }

    public String getDeliveredBy() { return deliveredBy; }
    public void setDeliveredBy(String deliveredBy) { this.deliveredBy = deliveredBy; }

    public List<OrderItemDTO> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemDTO> orderItems) { this.orderItems = orderItems; }
}
