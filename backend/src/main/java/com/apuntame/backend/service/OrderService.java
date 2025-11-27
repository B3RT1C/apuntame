package com.apuntame.backend.service;

import com.apuntame.backend.constant.ErrorMessages;
import com.apuntame.backend.dto.OrderListResponseDTO;
import com.apuntame.backend.dto.OrderResponseDTO;
import com.apuntame.backend.enums.DeliveryStatus;
import com.apuntame.backend.enums.PaymentStatus;
import com.apuntame.backend.enums.PreparationStatus;
import com.apuntame.backend.exception.InvalidDataException;
import com.apuntame.backend.exception.ResourceNotFoundException;
import com.apuntame.backend.model.Item;
import com.apuntame.backend.model.Order;
import com.apuntame.backend.model.User;
import com.apuntame.backend.repository.ItemRepository;
import com.apuntame.backend.repository.OrderRepository;
import com.apuntame.backend.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final OrderWebSocketService orderWebSocketService;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ItemRepository itemRepository, OrderWebSocketService orderWebSocketService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.orderWebSocketService = orderWebSocketService;
    }

    public List<Order> getAllOrders(Integer limit) {
        if (limit != null && limit > 0) {
            return orderRepository.findAllWithItems(PageRequest.of(0, limit)).getContent();
        }
        return orderRepository.findAllWithItems(Pageable.unpaged()).getContent();
    }

    public Order createOrder(Order order) {
        validateOrder(order);

        // Always set creation date from server
        order.setCreationDate(getCurrentTimestamp());

        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            order.getOrderItems().forEach(orderItem -> {
                if (orderItem.getItem() == null || orderItem.getItem().getId() == null) {
                    throw new InvalidDataException(ErrorMessages.ORDER_ITEM_NULL);
                }

                validateOrderItemAmount(orderItem.getAmount());

                int itemId = orderItem.getItem().getId();
                Item attachedItem = itemRepository.findById(itemId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                String.format(ErrorMessages.ITEM_NOT_FOUND, itemId)));
                orderItem.setItem(attachedItem);

                orderItem.setOrder(order);
            });
        }

        Order savedOrder = orderRepository.save(order);
        orderWebSocketService.notifyOrderCreated(savedOrder);
        return savedOrder;
    }

    public Order getOrderById(Integer id) {
        return orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, id)));
    }

    public Order updateOrder(Integer id, Order orderDetails) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, id)));

        if (orderDetails.getTable() != null && !orderDetails.getTable().trim().isEmpty()) {
            order.setTable(orderDetails.getTable());
        }

        if (orderDetails.getPaymentStatus() != null) {
            order.setPaymentStatus(orderDetails.getPaymentStatus());
        }

        if (orderDetails.getPreparationStatus() != null) {
            order.setPreparationStatus(orderDetails.getPreparationStatus());
        }

        if (orderDetails.getDeliveryStatus() != null) {
            order.setDeliveryStatus(orderDetails.getDeliveryStatus());
        }

        if (orderDetails.getTakenBy() != null && orderDetails.getTakenBy().getUsername() != null) {
            User attachedUser = userRepository.findById(orderDetails.getTakenBy().getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            String.format(ErrorMessages.USER_NOT_FOUND, orderDetails.getTakenBy().getUsername())));
            order.setTakenBy(attachedUser);
        }

        if (orderDetails.getOrderItems() != null && !orderDetails.getOrderItems().isEmpty()) {
            orderDetails.getOrderItems().forEach(orderItem -> {
                validateOrderItemAmount(orderItem.getAmount());
            });
        }

        return orderRepository.save(order);
    }

    public Order updatePaymentStatus(Integer orderId, PaymentStatus newStatus) {
        if (newStatus == null) {
            throw new InvalidDataException(ErrorMessages.ORDER_PAYMENT_STATUS_INVALID);
        }

        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, orderId)));

        order.setPaymentStatus(newStatus);

        if (newStatus == PaymentStatus.PAID && order.getPaidAt() == null) {
            order.setPaidAt(getCurrentTimestamp());
        } else if ((newStatus == PaymentStatus.CANCELLED || newStatus == PaymentStatus.REFUNDED) && order.getPaidAt() == null) {
            order.setPaidAt(getCurrentTimestamp());
        }

        Order updatedOrder = orderRepository.save(order);
        orderWebSocketService.notifyOrderUpdated(updatedOrder);
        return updatedOrder;
    }

    public Order updatePreparationStatus(Integer orderId, PreparationStatus newStatus) {
        if (newStatus == null) {
            throw new InvalidDataException(ErrorMessages.ORDER_PREPARATION_STATUS_INVALID);
        }

        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, orderId)));

        order.setPreparationStatus(newStatus);

        if (newStatus == PreparationStatus.READY && order.getPreparedAt() == null) {
            order.setPreparedAt(getCurrentTimestamp());
        }

        Order updatedOrder = orderRepository.save(order);
        orderWebSocketService.notifyOrderUpdated(updatedOrder);
        return updatedOrder;
    }

    public Order updateDeliveryStatus(Integer orderId, DeliveryStatus newStatus) {
        if (newStatus == null) {
            throw new InvalidDataException(ErrorMessages.ORDER_DELIVERY_STATUS_INVALID);
        }

        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, orderId)));

        order.setDeliveryStatus(newStatus);

        if (newStatus == DeliveryStatus.DELIVERED && order.getDeliveredAt() == null) {
            order.setDeliveredAt(getCurrentTimestamp());
        }

        Order updatedOrder = orderRepository.save(order);
        orderWebSocketService.notifyOrderUpdated(updatedOrder);
        return updatedOrder;
    }

    public void deleteOrder(Integer id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, id));
        }
        orderRepository.deleteById(id);
    }

    private void validateOrder(Order order) {
        if (order.getPaymentStatus() == null) {
            throw new InvalidDataException(ErrorMessages.ORDER_PAYMENT_STATUS_INVALID);
        }

        if (order.getPreparationStatus() == null) {
            throw new InvalidDataException(ErrorMessages.ORDER_PREPARATION_STATUS_INVALID);
        }

        if (order.getDeliveryStatus() == null) {
            throw new InvalidDataException(ErrorMessages.ORDER_DELIVERY_STATUS_INVALID);
        }
    }

    public OrderListResponseDTO getAllOrdersWithTimestamp(Integer limit) {
        List<Order> orders = getAllOrders(limit);
        return new OrderListResponseDTO(orders, getCurrentTimestamp());
    }

    public OrderResponseDTO createOrderWithTimestamp(Order order) {
        Order createdOrder = createOrder(order);
        return new OrderResponseDTO(createdOrder, getCurrentTimestamp());
    }

    public OrderResponseDTO getOrderByIdWithTimestamp(Integer id) {
        Order order = getOrderById(id);
        return new OrderResponseDTO(order, getCurrentTimestamp());
    }

    public OrderResponseDTO updatePaymentStatusWithTimestamp(Integer orderId, PaymentStatus newStatus) {
        Order updatedOrder = updatePaymentStatus(orderId, newStatus);
        return new OrderResponseDTO(updatedOrder, getCurrentTimestamp());
    }

    public OrderResponseDTO updatePreparationStatusWithTimestamp(Integer orderId, PreparationStatus newStatus) {
        Order updatedOrder = updatePreparationStatus(orderId, newStatus);
        return new OrderResponseDTO(updatedOrder, getCurrentTimestamp());
    }

    public OrderResponseDTO updateDeliveryStatusWithTimestamp(Integer orderId, DeliveryStatus newStatus) {
        Order updatedOrder = updateDeliveryStatus(orderId, newStatus);
        return new OrderResponseDTO(updatedOrder, getCurrentTimestamp());
    }

    private void validateOrderItemAmount(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new InvalidDataException(ErrorMessages.ORDER_ITEM_AMOUNT_INVALID);
        }
    }

    private String getCurrentTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }
}