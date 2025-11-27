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
        dto.setId(order.getId());
        dto.setTable(order.getTable());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setPreparationStatus(order.getPreparationStatus());
        dto.setDeliveryStatus(order.getDeliveryStatus());
        dto.setCreationDate(order.getCreationDate());
        dto.setPaidAt(order.getPaidAt());
        dto.setPreparedAt(order.getPreparedAt());
        dto.setDeliveredAt(order.getDeliveredAt());

        if (order.getTakenBy() != null) {
            dto.setTakenBy(order.getTakenBy().getUsername());
        }

        if (order.getOrderItems() != null) {
            dto.setOrderItems(
                order.getOrderItems().stream()
                        .map(orderItem -> new OrderEventDTO.OrderItemDTO(
                                orderItem.getItem().getId(),
                                orderItem.getItem().getName(),
                                orderItem.getItem().getPrice(),
                                orderItem.getAmount()
                        ))
                        .collect(Collectors.toList())
            );
        }

        return dto;
    }
}
