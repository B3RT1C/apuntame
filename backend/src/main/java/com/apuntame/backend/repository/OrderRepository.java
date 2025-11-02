package com.apuntame.backend.repository;

import com.apuntame.backend.enums.DeliveryStatus;
import com.apuntame.backend.enums.PaymentStatus;
import com.apuntame.backend.enums.PreparationStatus;
import com.apuntame.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);
    List<Order> findByPreparationStatus(PreparationStatus preparationStatus);
    List<Order> findByDeliveryStatus(DeliveryStatus deliveryStatus);
    List<Order> findByPaymentStatusAndPreparationStatus(PaymentStatus paymentStatus, PreparationStatus preparationStatus);
    List<Order> findByPaymentStatusAndDeliveryStatus(PaymentStatus paymentStatus, DeliveryStatus deliveryStatus);
    List<Order> findByPreparationStatusAndDeliveryStatus(PreparationStatus preparationStatus, DeliveryStatus deliveryStatus);
    List<Order> findByPaymentStatusAndPreparationStatusAndDeliveryStatus(PaymentStatus paymentStatus, PreparationStatus preparationStatus, DeliveryStatus deliveryStatus);
}