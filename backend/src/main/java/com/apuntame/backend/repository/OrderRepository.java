package com.apuntame.backend.repository;

import com.apuntame.backend.enums.DeliveryStatus;
import com.apuntame.backend.enums.PaymentStatus;
import com.apuntame.backend.enums.PreparationStatus;
import com.apuntame.backend.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.orderItems oi " +
           "LEFT JOIN FETCH oi.item " +
           "WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Integer id);

    @Query("SELECT DISTINCT o FROM Order o " +
           "LEFT JOIN FETCH o.orderItems oi " +
           "LEFT JOIN FETCH oi.item " +
           "WHERE o.id IN :ids")
    List<Order> findByIdsWithItems(@Param("ids") List<Integer> ids);

    @Query(value = "SELECT DISTINCT o FROM Order o " +
                   "LEFT JOIN FETCH o.orderItems oi " +
                   "LEFT JOIN FETCH oi.item",
           countQuery = "SELECT COUNT(DISTINCT o) FROM Order o")
    Page<Order> findAllWithItems(Pageable pageable);

    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);
    List<Order> findByPreparationStatus(PreparationStatus preparationStatus);
    List<Order> findByDeliveryStatus(DeliveryStatus deliveryStatus);
    List<Order> findByPaymentStatusAndPreparationStatus(PaymentStatus paymentStatus, PreparationStatus preparationStatus);
    List<Order> findByPaymentStatusAndDeliveryStatus(PaymentStatus paymentStatus, DeliveryStatus deliveryStatus);
    List<Order> findByPreparationStatusAndDeliveryStatus(PreparationStatus preparationStatus, DeliveryStatus deliveryStatus);
    List<Order> findByPaymentStatusAndPreparationStatusAndDeliveryStatus(PaymentStatus paymentStatus, PreparationStatus preparationStatus, DeliveryStatus deliveryStatus);
}