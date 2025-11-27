package com.apuntame.backend.service;

import com.apuntame.backend.dto.OrderEventDTO;
import com.apuntame.backend.model.Order;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class OrderWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public OrderWebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyOrderCreated(Order order) {
        OrderEventDTO event = convertToDTO(order, OrderEventDTO.EventType.CREATED);
        System.out.println("Enviando notificación WebSocket CREATED para pedido ID: " + order.getId());
        messagingTemplate.convertAndSend("/topic/orders", event);
    }

    public void notifyOrderUpdated(Order order) {
        OrderEventDTO event = convertToDTO(order, OrderEventDTO.EventType.UPDATED);
        System.out.println("Enviando notificación WebSocket UPDATED para pedido ID: " + order.getId() +
                          " - Payment: " + order.getPaymentStatus() +
                          ", Preparation: " + order.getPreparationStatus() +
                          ", Delivery: " + order.getDeliveryStatus());
        messagingTemplate.convertAndSend("/topic/orders", event);
        System.out.println("Notificación WebSocket UPDATED enviada");
    }

    private OrderEventDTO convertToDTO(Order order, OrderEventDTO.EventType eventType) {
        OrderEventDTO dto = new OrderEventDTO();
        dto.setEventType(eventType);

        mapBasicOrderFields(order, dto);
        mapOrderDates(order, dto);
        mapTakenByUser(order, dto);
        mapOrderItems(order, dto);

        return dto;
    }

    private void mapBasicOrderFields(Order order, OrderEventDTO dto) {
        dto.setId(order.getId());
        dto.setTable(order.getTable());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setPreparationStatus(order.getPreparationStatus());
        dto.setDeliveryStatus(order.getDeliveryStatus());
    }

    private void mapOrderDates(Order order, OrderEventDTO dto) {
        dto.setCreationDate(order.getCreationDate());
        dto.setPaidAt(order.getPaidAt());
        dto.setPreparedAt(order.getPreparedAt());
        dto.setDeliveredAt(order.getDeliveredAt());
    }

    private void mapTakenByUser(Order order, OrderEventDTO dto) {
        if (order.getTakenBy() != null) {
            dto.setTakenBy(order.getTakenBy().getUsername());
        }
    }

    private void mapOrderItems(Order order, OrderEventDTO dto) {
        if (order.getOrderItems() != null) {
            dto.setOrderItems(
                order.getOrderItems().stream()
                        .map(this::convertToOrderItemDTO)
                        .collect(Collectors.toList())
            );
        }
    }

    private OrderEventDTO.OrderItemDTO convertToOrderItemDTO(com.apuntame.backend.model.OrderItem orderItem) {
        return new OrderEventDTO.OrderItemDTO(
                orderItem.getItem().getId(),
                orderItem.getItem().getName(),
                orderItem.getItem().getPrice(),
                orderItem.getAmount()
        );
    }
}
