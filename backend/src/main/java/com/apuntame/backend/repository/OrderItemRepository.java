package com.apuntame.backend.repository;

import com.apuntame.backend.model.OrderItem;
import com.apuntame.backend.model.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
}