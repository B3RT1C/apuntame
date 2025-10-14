package com.apuntame.backend.service;

import com.apuntame.backend.constant.ErrorMessages;
import com.apuntame.backend.exception.InvalidDataException;
import com.apuntame.backend.exception.ResourceNotFoundException;
import com.apuntame.backend.model.OrderItem;
import com.apuntame.backend.model.OrderItemId;
import com.apuntame.backend.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public OrderItem addItemToOrder(OrderItem orderItem) {
        validateOrderItem(orderItem);
        return orderItemRepository.save(orderItem);
    }

    public void removeItemFromOrder(Integer orderId, Integer itemId) {
        OrderItemId id = new OrderItemId(orderId, itemId);
        if (!orderItemRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ORDER_ITEM_NOT_FOUND, orderId + "," + itemId));
        }
        orderItemRepository.deleteById(id);
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ORDER_ITEM_NOT_FOUND, orderId, itemId)));
    }

    public OrderItem getOrderItemById(Integer orderId, Integer itemId) {
        OrderItemId id = new OrderItemId(orderId, itemId);
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ORDER_ITEM_NOT_FOUND, orderId, itemId)));
    }

    public OrderItem updateOrderItem(Integer orderId, Integer itemId, OrderItem orderItemDetails) {
        OrderItemId id = new OrderItemId(orderId, itemId);
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ORDER_ITEM_NOT_FOUND, orderId, itemId)));

        if (orderItemDetails.getAmount() != null && orderItemDetails.getAmount() > 0) {
            orderItem.setAmount(orderItemDetails.getAmount());
        }

        return orderItemRepository.save(orderItem);
    }

    private void validateOrderItem(OrderItem orderItem) {
        if (orderItem.getAmount() == null || orderItem.getAmount() <= 0) {
            throw new InvalidDataException(ErrorMessages.ORDER_ITEM_AMOUNT_INVALID);
        }
    }
}