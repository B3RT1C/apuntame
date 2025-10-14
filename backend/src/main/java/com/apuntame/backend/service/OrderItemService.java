package com.apuntame.backend.service;

import com.apuntame.backend.constant.ErrorMessages;
import com.apuntame.backend.exception.InvalidDataException;
import com.apuntame.backend.exception.ResourceNotFoundException;
import com.apuntame.backend.model.Item;
import com.apuntame.backend.model.Order;
import com.apuntame.backend.model.OrderItem;
import com.apuntame.backend.model.OrderItemId;
import com.apuntame.backend.repository.ItemRepository;
import com.apuntame.backend.repository.OrderItemRepository;
import com.apuntame.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository, OrderRepository orderRepository, ItemRepository itemRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
    }

    public OrderItem addItemToOrder(Integer orderId, OrderItem orderItemRequest) {
        validateAmount(orderItemRequest.getAmount());

        if (orderItemRequest.getItem() == null || orderItemRequest.getItem().getId() == null) {
            throw new InvalidDataException(ErrorMessages.ORDER_ITEM_NULL);
        }

        Integer itemId = orderItemRequest.getItem().getId();

        OrderItemId orderItemId = new OrderItemId(orderId, itemId);
        Optional<OrderItem> existingOrderItem = orderItemRepository.findById(orderItemId);

        if (existingOrderItem.isPresent()) {
            OrderItem orderItem = existingOrderItem.get();
            orderItem.setAmount(orderItem.getAmount() + orderItemRequest.getAmount());
            return orderItemRepository.save(orderItem);
        } else {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, orderId)));

            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ITEM_NOT_FOUND, itemId)));

            OrderItem newOrderItem = new OrderItem(order, item, orderItemRequest.getAmount());
            return orderItemRepository.save(newOrderItem);
        }
    }

    public List<OrderItem> addMultipleItemsToOrder(Integer orderId, List<OrderItem> orderItemsRequest) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, orderId));
        }

        return orderItemsRequest.stream()
                .map(orderItemRequest -> addItemToOrder(orderId, orderItemRequest))
                .toList();
    }

    public void removeItemFromOrder(Integer orderId, Integer itemId, Integer amount) {
        OrderItemId id = new OrderItemId(orderId, itemId);
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ORDER_ITEM_NOT_FOUND, orderId, itemId)));

        if (amount == null || amount >= orderItem.getAmount()) {
            orderItemRepository.deleteById(id);
        }else if (amount > 0) {
            orderItem.setAmount(orderItem.getAmount() - amount);
            orderItemRepository.save(orderItem);
        }
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

        if (orderItemDetails.getAmount() != null) {
            validateAmount(orderItemDetails.getAmount());
            orderItem.setAmount(orderItemDetails.getAmount());
        }

        return orderItemRepository.save(orderItem);
    }

    private void validateAmount(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new InvalidDataException(ErrorMessages.ORDER_ITEM_AMOUNT_INVALID);
        }
    }
}