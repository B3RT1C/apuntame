package com.apuntame.backend.controller;

import com.apuntame.backend.dto.OrderListResponseDTO;
import com.apuntame.backend.dto.OrderResponseDTO;
import com.apuntame.backend.enums.DeliveryStatus;
import com.apuntame.backend.enums.PaymentStatus;
import com.apuntame.backend.enums.PreparationStatus;
import com.apuntame.backend.model.Order;
import com.apuntame.backend.model.OrderItem;
import com.apuntame.backend.service.OrderItemService;
import com.apuntame.backend.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    public OrderController(OrderService orderService, OrderItemService orderItemService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
    }

    @GetMapping
    public ResponseEntity<OrderListResponseDTO> getAllOrders(@RequestParam(required = false) Integer limit) {
        OrderListResponseDTO response = orderService.getAllOrdersWithTimestamp(limit);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody Order order) {
        OrderResponseDTO response = orderService.createOrderWithTimestamp(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Integer id) {
        OrderResponseDTO response = orderService.getOrderByIdWithTimestamp(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Integer id, @RequestBody Order orderDetails) {
        Order updatedOrder = orderService.updateOrder(id, orderDetails);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderItem> addItemToOrder(@PathVariable Integer orderId, @RequestBody OrderItem orderItem) {
        OrderItem createdOrderItem = orderItemService.addItemToOrder(orderId, orderItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrderItem);
    }

    @PostMapping("/{orderId}/items/batch")
    public ResponseEntity<List<OrderItem>> addMultipleItemsToOrder(
            @PathVariable Integer orderId,
            @RequestBody List<OrderItem> orderItems) {
        List<OrderItem> createdOrderItems = orderItemService.addMultipleItemsToOrder(orderId, orderItems);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrderItems);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderItem> getOrderItem(@PathVariable Integer orderId, @PathVariable Integer itemId) {
        OrderItem orderItem = orderItemService.getOrderItemById(orderId, itemId);
        return ResponseEntity.ok(orderItem);
    }

    @PutMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable Integer orderId, @PathVariable Integer itemId, @RequestBody OrderItem orderItemDetails) {
        OrderItem updatedOrderItem = orderItemService.updateOrderItem(orderId, itemId, orderItemDetails);
        return ResponseEntity.ok(updatedOrderItem);
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<Void> removeItemFromOrder(@PathVariable Integer orderId, @PathVariable Integer itemId, @RequestParam(required = false) Integer amount) {
        orderItemService.removeItemFromOrder(orderId, itemId, amount);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/payment-status")
    public ResponseEntity<OrderResponseDTO> updatePaymentStatus(@PathVariable Integer id, @RequestBody PaymentStatus paymentStatus) {
        OrderResponseDTO response = orderService.updatePaymentStatusWithTimestamp(id, paymentStatus);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/preparation-status")
    public ResponseEntity<OrderResponseDTO> updatePreparationStatus(@PathVariable Integer id, @RequestBody PreparationStatus preparationStatus) {
        OrderResponseDTO response = orderService.updatePreparationStatusWithTimestamp(id, preparationStatus);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/delivery-status")
    public ResponseEntity<OrderResponseDTO> updateDeliveryStatus(@PathVariable Integer id, @RequestBody DeliveryStatus deliveryStatus) {
        OrderResponseDTO response = orderService.updateDeliveryStatusWithTimestamp(id, deliveryStatus);
        return ResponseEntity.ok(response);
    }
}