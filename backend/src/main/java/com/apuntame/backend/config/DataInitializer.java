package com.apuntame.backend.config;

import com.apuntame.backend.enums.DeliveryStatus;
import com.apuntame.backend.enums.PaymentStatus;
import com.apuntame.backend.enums.PreparationStatus;
import com.apuntame.backend.model.Item;
import com.apuntame.backend.model.Order;
import com.apuntame.backend.model.OrderItem;
import com.apuntame.backend.model.User;
import com.apuntame.backend.repository.ItemRepository;
import com.apuntame.backend.repository.OrderRepository;
import com.apuntame.backend.repository.UserRepository;
import com.apuntame.backend.service.ItemService;
import com.apuntame.backend.service.OrderService;
import com.apuntame.backend.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final OrderService orderService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DataInitializer(UserRepository userRepository,
                          ItemRepository itemRepository,
                          OrderRepository orderRepository,
                          UserService userService,
                          ItemService itemService,
                          OrderService orderService) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.itemService = itemService;
        this.orderService = orderService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            initializeUsers();
        }

        if (itemRepository.count() == 0) {
            initializeMenuItems();
        }

        if (orderRepository.count() == 0) {
            initializeSampleOrders();
        }

        System.out.println("Database initialization complete!");
    }

    private void initializeUsers() {
        System.out.println("Initializing database with default data...");

        userService.createUser(new User("admin", "admin", "ADMIN"));
        System.out.println("Created admin user (username: admin, password: admin)");

        userService.createUser(new User("user", "user", "WAITER"));
        System.out.println("Created test user (username: user, password: user)");

        userService.createUser(new User("camarero1", "camarero1", "WAITER"));
        userService.createUser(new User("camarero2", "camarero2", "WAITER"));
        System.out.println("Created sample waiters (camarero1, camarero2)");
    }

    private void initializeMenuItems() {
        itemService.createItem(new Item("Café", new BigDecimal("1.50")));
        itemService.createItem(new Item("Té", new BigDecimal("1.30")));
        itemService.createItem(new Item("Agua", new BigDecimal("1.00")));
        itemService.createItem(new Item("Refresco", new BigDecimal("2.00")));
        itemService.createItem(new Item("Cerveza", new BigDecimal("2.50")));
        itemService.createItem(new Item("Vino tinto", new BigDecimal("3.00")));
        itemService.createItem(new Item("Vino blanco", new BigDecimal("3.00")));
        itemService.createItem(new Item("Bocadillo jamón", new BigDecimal("4.50")));
        itemService.createItem(new Item("Bocadillo queso", new BigDecimal("4.00")));
        itemService.createItem(new Item("Tostadas", new BigDecimal("2.50")));
        itemService.createItem(new Item("Croissant", new BigDecimal("1.80")));
        itemService.createItem(new Item("Ensalada", new BigDecimal("5.50")));
        itemService.createItem(new Item("Hamburguesa", new BigDecimal("8.00")));
        itemService.createItem(new Item("Pizza", new BigDecimal("9.00")));
        itemService.createItem(new Item("Pasta", new BigDecimal("7.50")));
        System.out.println("Created 15 sample menu items");
    }

    private void initializeSampleOrders() {
        System.out.println("Creating sample orders...");

        User waiter1 = userRepository.findById("camarero1").orElse(null);
        User waiter2 = userRepository.findById("camarero2").orElse(null);
        List<Item> items = itemRepository.findAll();

        if (waiter1 == null || waiter2 == null || items.isEmpty()) {
            System.out.println("Cannot create orders: missing users or items");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        createCompletedOrder(items, waiter1, now);
        createReadyForDeliveryOrder(items, waiter1, now);
        createPendingPreparationOrder(items, waiter2, now);
        createPendingPaymentOrder(items, waiter2, now);
        createAnotherPendingOrder(items, waiter1, now);
        createCancelledOrder(items, waiter2, now);

        System.out.println("Successfully created 6 sample orders with items");
    }

    private void createCompletedOrder(List<Item> items, User waiter, LocalDateTime now) {
        Order order = createOrder("Mesa 1", PaymentStatus.PAID, PreparationStatus.READY,
            DeliveryStatus.DELIVERED, now.minusHours(2), waiter);
        order.setPaidAt(formatTime(now.minusHours(2).plusMinutes(5)));
        order.setPreparedAt(formatTime(now.minusHours(2).plusMinutes(15)));
        order.setDeliveredAt(formatTime(now.minusHours(2).plusMinutes(20)));
        order.setChargedBy(waiter);
        order.setPreparedBy(waiter);
        order.setDeliveredBy(waiter);
        addOrderItem(order, items.get(0), 2);
        addOrderItem(order, items.get(10), 2);
        orderService.createOrder(order);
        System.out.println("Created Order 1: Mesa 1 (Completed)");
    }

    private void createReadyForDeliveryOrder(List<Item> items, User waiter, LocalDateTime now) {
        Order order = createOrder("Mesa 2", PaymentStatus.PAID, PreparationStatus.READY,
            DeliveryStatus.PENDING, now.minusMinutes(30), waiter);
        order.setPaidAt(formatTime(now.minusMinutes(30).plusMinutes(2)));
        order.setPreparedAt(formatTime(now.minusMinutes(10)));
        order.setChargedBy(waiter);
        order.setPreparedBy(waiter);
        addOrderItem(order, items.get(7), 1);
        addOrderItem(order, items.get(4), 1);
        orderService.createOrder(order);
        System.out.println("Created Order 2: Mesa 2 (Ready for delivery)");
    }

    private void createPendingPreparationOrder(List<Item> items, User waiter, LocalDateTime now) {
        Order order = createOrder("Mesa 3", PaymentStatus.PAID, PreparationStatus.PENDING,
            DeliveryStatus.PENDING, now.minusMinutes(20), waiter);
        order.setPaidAt(formatTime(now.minusMinutes(20).plusMinutes(1)));
        order.setChargedBy(waiter);
        addOrderItem(order, items.get(13), 2);
        addOrderItem(order, items.get(3), 2);
        orderService.createOrder(order);
        System.out.println("Created Order 3: Mesa 3 (Pending preparation)");
    }

    private void createPendingPaymentOrder(List<Item> items, User waiter, LocalDateTime now) {
        Order order = createOrder("Mesa 4", PaymentStatus.PENDING, PreparationStatus.PENDING,
            DeliveryStatus.PENDING, now.minusMinutes(5), waiter);
        addOrderItem(order, items.get(12), 1);
        addOrderItem(order, items.get(14), 1);
        addOrderItem(order, items.get(2), 2);
        orderService.createOrder(order);
        System.out.println("Created Order 4: Mesa 4 (Pending payment)");
    }

    private void createAnotherPendingOrder(List<Item> items, User waiter, LocalDateTime now) {
        Order order = createOrder("Mesa 5", PaymentStatus.PENDING, PreparationStatus.PENDING,
            DeliveryStatus.PENDING, now.minusMinutes(2), waiter);
        addOrderItem(order, items.get(11), 1);
        addOrderItem(order, items.get(6), 1);
        orderService.createOrder(order);
        System.out.println("Created Order 5: Mesa 5 (Pending)");
    }

    private void createCancelledOrder(List<Item> items, User waiter, LocalDateTime now) {
        Order order = createOrder("Mesa 6", PaymentStatus.CANCELLED, PreparationStatus.CANCELLED,
            DeliveryStatus.CANCELLED, now.minusHours(1), waiter);
        addOrderItem(order, items.get(1), 1);
        orderService.createOrder(order);
        System.out.println("Created Order 6: Mesa 6 (Cancelled)");
    }

    private Order createOrder(String table, PaymentStatus paymentStatus,
                             PreparationStatus preparationStatus, DeliveryStatus deliveryStatus,
                             LocalDateTime creationDate, User takenBy) {
        Order order = new Order();
        order.setTable(table);
        order.setPaymentStatus(paymentStatus);
        order.setPreparationStatus(preparationStatus);
        order.setDeliveryStatus(deliveryStatus);
        order.setCreationDate(formatTime(creationDate));
        order.setTakenBy(takenBy);
        return order;
    }

    private void addOrderItem(Order order, Item item, int amount) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setAmount(amount);
        order.getOrderItems().add(orderItem);
    }

    private String formatTime(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }
}
