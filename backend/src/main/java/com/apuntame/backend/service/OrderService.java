package com.apuntame.backend.service;

import com.apuntame.backend.constant.ErrorMessages;
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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public List<Order> getAllOrders(Integer limit) {
        if (limit != null && limit > 0) {
            return orderRepository.findAll(PageRequest.of(0, limit)).getContent();
        }
        return orderRepository.findAll();
    }

    public Order createOrder(Order order) {
        validateOrder(order);

        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            order.getOrderItems().forEach(orderItem -> {
                if (orderItem.getItem() == null || orderItem.getItem().getId() == null) {
                    throw new InvalidDataException(ErrorMessages.ORDER_ITEM_NULL);
                }

                validateOrderItemAmount(orderItem.getAmount());

                int itemId = orderItem.getItem().getId() != null ? orderItem.getItem().getId() : orderItem.getId().getItemId();
                Item attachedItem = itemRepository.findById(itemId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                String.format(ErrorMessages.ITEM_NOT_FOUND, itemId)));
                orderItem.setItem(attachedItem);

                orderItem.setOrder(order);
            });
        }

        return orderRepository.save(order);
    }

    public Order getOrderById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, id)));
    }

    public Order updateOrder(Integer id, Order orderDetails) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, id)));

        if (orderDetails.getTable() != null && !orderDetails.getTable().trim().isEmpty()) {
            order.setTable(orderDetails.getTable());
        }

        if (orderDetails.getState() != null && !orderDetails.getState().trim().isEmpty()) {
            order.setState(orderDetails.getState());
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

    public void deleteOrder(Integer id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format(ErrorMessages.ORDER_NOT_FOUND, id));
        }
        orderRepository.deleteById(id);
    }

    private void validateOrder(Order order) {
        //TODO
        /*
        if (order.getTable() == null || order.getTable().trim().isEmpty()) {
            throw new InvalidDataException(ErrorMessages.ORDER_TABLE_EMPTY);
        }
        */
        if (order.getState() == null || order.getState().trim().isEmpty()) {
            throw new InvalidDataException(ErrorMessages.ORDER_STATE_EMPTY);
        }
    }

    private void validateOrderItemAmount(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new InvalidDataException(ErrorMessages.ORDER_ITEM_AMOUNT_INVALID);
        }
    }
}