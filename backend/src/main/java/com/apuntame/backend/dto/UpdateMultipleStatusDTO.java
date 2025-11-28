package com.apuntame.backend.dto;

import com.apuntame.backend.enums.DeliveryStatus;
import com.apuntame.backend.enums.PaymentStatus;
import com.apuntame.backend.enums.PreparationStatus;

public class UpdateMultipleStatusDTO {
    private PaymentStatus paymentStatus;
    private PreparationStatus preparationStatus;
    private DeliveryStatus deliveryStatus;

    public UpdateMultipleStatusDTO() {
    }

    public UpdateMultipleStatusDTO(PaymentStatus paymentStatus, PreparationStatus preparationStatus, DeliveryStatus deliveryStatus) {
        this.paymentStatus = paymentStatus;
        this.preparationStatus = preparationStatus;
        this.deliveryStatus = deliveryStatus;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PreparationStatus getPreparationStatus() {
        return preparationStatus;
    }

    public void setPreparationStatus(PreparationStatus preparationStatus) {
        this.preparationStatus = preparationStatus;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
