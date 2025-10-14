package com.apuntame.backend.service;

import com.apuntame.backend.constant.ErrorMessages;
import com.apuntame.backend.exception.InvalidDataException;
import com.apuntame.backend.exception.ResourceNotFoundException;
import com.apuntame.backend.model.Order;
import com.apuntame.backend.repository.OrderRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders(Integer limit) {
        if (limit != null && limit > 0) {
            return orderRepository.findAll(PageRequest.of(0, limit)).getContent();
        }
        return orderRepository.findAll();
    }

    public Order createOrder(Order order) {
        validateOrder(order);
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
}