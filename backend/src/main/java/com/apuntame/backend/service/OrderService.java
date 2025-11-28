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
import com.apuntame.backend.model.OrderItem;
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
        order.setCreationDate(getCurrentTimestamp());
        processOrderItems(order);

        Order savedOrder = orderRepository.save(order);
        orderWebSocketService.notifyOrderCreated(savedOrder);
        return savedOrder;
    }

    private void processOrderItems(Order order) {
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            return;
        }

        order.getOrderItems().forEach(orderItem -> {
            validateOrderItem(orderItem);
            attachItemToOrderItem(orderItem);
            orderItem.setOrder(order);
        });
    }

    private void validateOrderItem(OrderItem orderItem) {
        if (orderItem.getItem() == null || orderItem.getItem().getId() == null) {
            throw new InvalidDataException(ErrorMessages.ORDER_ITEM_NULL);
        }
        validateOrderItemAmount(orderItem.getAmount());
    }

    private void attachItemToOrderItem(OrderItem orderItem) {
        int itemId = orderItem.getItem().getId();
        Item attachedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessages.ITEM_NOT_FOUND, itemId)));
        orderItem.setItem(attachedItem);
    }

    public Order getOrderById(Integer id) {
        return orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, id)));
    }

    public Order updateOrder(Integer id, Order orderDetails) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, id)));

        updateTableIfPresent(order, orderDetails);
        updateStatusFieldsIfPresent(order, orderDetails);
        updateTakenByIfPresent(order, orderDetails);
        validateOrderItemsIfPresent(orderDetails);

        return orderRepository.save(order);
    }

    private void updateTableIfPresent(Order order, Order orderDetails) {
        if (orderDetails.getTable() != null && !orderDetails.getTable().trim().isEmpty()) {
            order.setTable(orderDetails.getTable());
        }
    }

    private void updateStatusFieldsIfPresent(Order order, Order orderDetails) {
        if (orderDetails.getPaymentStatus() != null) {
            order.setPaymentStatus(orderDetails.getPaymentStatus());
        }
        if (orderDetails.getPreparationStatus() != null) {
            order.setPreparationStatus(orderDetails.getPreparationStatus());
        }
        if (orderDetails.getDeliveryStatus() != null) {
            order.setDeliveryStatus(orderDetails.getDeliveryStatus());
        }
    }

    private void updateTakenByIfPresent(Order order, Order orderDetails) {
        if (orderDetails.getTakenBy() != null && orderDetails.getTakenBy().getUsername() != null) {
            User attachedUser = userRepository.findById(orderDetails.getTakenBy().getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            String.format(ErrorMessages.USER_NOT_FOUND, orderDetails.getTakenBy().getUsername())));
            order.setTakenBy(attachedUser);
        }
    }

    private void validateOrderItemsIfPresent(Order orderDetails) {
        if (orderDetails.getOrderItems() != null && !orderDetails.getOrderItems().isEmpty()) {
            orderDetails.getOrderItems().forEach(orderItem ->
                validateOrderItemAmount(orderItem.getAmount())
            );
        }
    }

    public Order updatePaymentStatus(Integer orderId, PaymentStatus newStatus, User currentUser) {
        validateStatusNotNull(newStatus, ErrorMessages.ORDER_PAYMENT_STATUS_INVALID);
        Order order = findOrderById(orderId);

        order.setPaymentStatus(newStatus);
        setPaymentTimestampIfNeeded(order, newStatus);
        setChargedByIfNeeded(order, newStatus, currentUser);

        return saveAndNotifyUpdate(order);
    }

    public Order updatePreparationStatus(Integer orderId, PreparationStatus newStatus, User currentUser) {
        validateStatusNotNull(newStatus, ErrorMessages.ORDER_PREPARATION_STATUS_INVALID);
        Order order = findOrderById(orderId);

        order.setPreparationStatus(newStatus);
        setPreparationTimestampIfNeeded(order, newStatus);
        setPreparedByIfNeeded(order, newStatus, currentUser);

        return saveAndNotifyUpdate(order);
    }

    public Order updateDeliveryStatus(Integer orderId, DeliveryStatus newStatus, User currentUser) {
        validateStatusNotNull(newStatus, ErrorMessages.ORDER_DELIVERY_STATUS_INVALID);
        Order order = findOrderById(orderId);

        order.setDeliveryStatus(newStatus);
        setDeliveryTimestampIfNeeded(order, newStatus);
        setDeliveredByIfNeeded(order, newStatus, currentUser);

        return saveAndNotifyUpdate(order);
    }

    private void validateStatusNotNull(Object status, String errorMessage) {
        if (status == null) {
            throw new InvalidDataException(errorMessage);
        }
    }

    private Order findOrderById(Integer orderId) {
        return orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, orderId)));
    }

    private void setPaymentTimestampIfNeeded(Order order, PaymentStatus newStatus) {
        if ((newStatus == PaymentStatus.PAID || newStatus == PaymentStatus.CANCELLED ||
             newStatus == PaymentStatus.REFUNDED) && order.getPaidAt() == null) {
            order.setPaidAt(getCurrentTimestamp());
        }
    }

    private void setChargedByIfNeeded(Order order, PaymentStatus newStatus, User currentUser) {
        if ((newStatus == PaymentStatus.PAID || newStatus == PaymentStatus.CANCELLED ||
             newStatus == PaymentStatus.REFUNDED) && order.getChargedBy() == null && currentUser != null) {
            order.setChargedBy(currentUser);
        }
    }

    private void setPreparationTimestampIfNeeded(Order order, PreparationStatus newStatus) {
        if (newStatus == PreparationStatus.READY && order.getPreparedAt() == null) {
            order.setPreparedAt(getCurrentTimestamp());
        }
    }

    private void setPreparedByIfNeeded(Order order, PreparationStatus newStatus, User currentUser) {
        if (newStatus == PreparationStatus.READY && order.getPreparedBy() == null && currentUser != null) {
            order.setPreparedBy(currentUser);
        }
    }

    private void setDeliveryTimestampIfNeeded(Order order, DeliveryStatus newStatus) {
        if (newStatus == DeliveryStatus.DELIVERED && order.getDeliveredAt() == null) {
            order.setDeliveredAt(getCurrentTimestamp());
        }
    }

    private void setDeliveredByIfNeeded(Order order, DeliveryStatus newStatus, User currentUser) {
        if (newStatus == DeliveryStatus.DELIVERED && order.getDeliveredBy() == null && currentUser != null) {
            order.setDeliveredBy(currentUser);
        }
    }

    private Order saveAndNotifyUpdate(Order order) {
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

    public OrderResponseDTO updatePaymentStatusWithTimestamp(Integer orderId, PaymentStatus newStatus, User currentUser) {
        Order updatedOrder = updatePaymentStatus(orderId, newStatus, currentUser);
        return new OrderResponseDTO(updatedOrder, getCurrentTimestamp());
    }

    public OrderResponseDTO updatePreparationStatusWithTimestamp(Integer orderId, PreparationStatus newStatus, User currentUser) {
        Order updatedOrder = updatePreparationStatus(orderId, newStatus, currentUser);
        return new OrderResponseDTO(updatedOrder, getCurrentTimestamp());
    }

    public OrderResponseDTO updateDeliveryStatusWithTimestamp(Integer orderId, DeliveryStatus newStatus, User currentUser) {
        Order updatedOrder = updateDeliveryStatus(orderId, newStatus, currentUser);
        return new OrderResponseDTO(updatedOrder, getCurrentTimestamp());
    }

    public OrderResponseDTO updateMultipleStatuses(Integer orderId, PaymentStatus paymentStatus,
                                                    PreparationStatus preparationStatus,
                                                    DeliveryStatus deliveryStatus,
                                                    User currentUser) {
        Order order = findOrderById(orderId);

        if (paymentStatus != null) {
            order.setPaymentStatus(paymentStatus);
            setPaymentTimestampIfNeeded(order, paymentStatus);
            setChargedByIfNeeded(order, paymentStatus, currentUser);
        }

        if (preparationStatus != null) {
            order.setPreparationStatus(preparationStatus);
            setPreparationTimestampIfNeeded(order, preparationStatus);
            setPreparedByIfNeeded(order, preparationStatus, currentUser);
        }

        if (deliveryStatus != null) {
            order.setDeliveryStatus(deliveryStatus);
            setDeliveryTimestampIfNeeded(order, deliveryStatus);
            setDeliveredByIfNeeded(order, deliveryStatus, currentUser);
        }

        Order updatedOrder = saveAndNotifyUpdate(order);
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