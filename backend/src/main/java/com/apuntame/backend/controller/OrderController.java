package com.apuntame.backend.controller;

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
    public ResponseEntity<List<Order>> getAllOrders(@RequestParam(required = false) Integer limit) {
        List<Order> orders = orderService.getAllOrders(limit);
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
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

    // Endpoints para gestionar OrderItems dentro de un pedido
    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderItem> addItemToOrder(@PathVariable Integer orderId, @RequestBody OrderItem orderItem) {
        OrderItem createdOrderItem = orderItemService.addItemToOrder(orderItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrderItem);
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
    public ResponseEntity<Void> removeItemFromOrder(@PathVariable Integer orderId, @PathVariable Integer itemId) {
        orderItemService.removeItemFromOrder(orderId, itemId);
        return ResponseEntity.noContent().build();
    }
}