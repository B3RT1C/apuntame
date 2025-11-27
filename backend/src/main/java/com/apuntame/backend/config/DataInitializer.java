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
        // Only initialize if database is empty
        if (userRepository.count() == 0) {
            System.out.println("Initializing database with default data...");

            // Create admin user
            User admin = new User("admin", "admin", "ADMIN");
            userService.createUser(admin);
            System.out.println("Created admin user (username: admin, password: admin)");

            // Create test user
            User user = new User("user", "user", "WAITER");
            userService.createUser(user);
            System.out.println("Created test user (username: user, password: user)");

            // Create sample waiters
            User waiter1 = new User("camarero1", "camarero1", "WAITER");
            User waiter2 = new User("camarero2", "camarero2", "WAITER");
            userService.createUser(waiter1);
            userService.createUser(waiter2);
            System.out.println("Created sample waiters (camarero1, camarero2)");
        }

        if (itemRepository.count() == 0) {
            // Create sample menu items
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

        if (orderRepository.count() == 0) {
            System.out.println("Creating sample orders...");

            // Get users and items for orders
            User waiter1 = userRepository.findById("camarero1").orElse(null);
            User waiter2 = userRepository.findById("camarero2").orElse(null);
            List<Item> items = itemRepository.findAll();

            if (waiter1 != null && waiter2 != null && !items.isEmpty()) {
                LocalDateTime now = LocalDateTime.now();

                // Order 1: Fully completed order (paid, prepared, delivered)
                Order order1 = createOrder("Mesa 1", PaymentStatus.PAID, PreparationStatus.READY,
                    DeliveryStatus.DELIVERED, now.minusHours(2), waiter1);
                order1.setPaidAt(formatTime(now.minusHours(2).plusMinutes(5)));
                order1.setPreparedAt(formatTime(now.minusHours(2).plusMinutes(15)));
                order1.setDeliveredAt(formatTime(now.minusHours(2).plusMinutes(20)));
                addOrderItem(order1, items.get(0), 2);  // 2 Cafés
                addOrderItem(order1, items.get(10), 2); // 2 Croissants
                orderService.createOrder(order1);
                System.out.println("Created Order 1: Mesa 1 (Completed)");

                // Order 2: Paid and ready, waiting for delivery
                Order order2 = createOrder("Mesa 2", PaymentStatus.PAID, PreparationStatus.READY,
                    DeliveryStatus.PENDING, now.minusMinutes(30), waiter1);
                order2.setPaidAt(formatTime(now.minusMinutes(30).plusMinutes(2)));
                order2.setPreparedAt(formatTime(now.minusMinutes(10)));
                addOrderItem(order2, items.get(7), 1);  // 1 Bocadillo jamón
                addOrderItem(order2, items.get(4), 1);  // 1 Cerveza
                orderService.createOrder(order2);
                System.out.println("Created Order 2: Mesa 2 (Ready for delivery)");

                // Order 3: Paid, pending preparation
                Order order3 = createOrder("Mesa 3", PaymentStatus.PAID, PreparationStatus.PENDING,
                    DeliveryStatus.PENDING, now.minusMinutes(20), waiter2);
                order3.setPaidAt(formatTime(now.minusMinutes(20).plusMinutes(1)));
                addOrderItem(order3, items.get(13), 2); // 2 Pizzas
                addOrderItem(order3, items.get(3), 2);  // 2 Refrescos
                orderService.createOrder(order3);
                System.out.println("Created Order 3: Mesa 3 (Pending preparation)");

                // Order 4: Just ordered, pending payment
                Order order4 = createOrder("Mesa 4", PaymentStatus.PENDING, PreparationStatus.PENDING,
                    DeliveryStatus.PENDING, now.minusMinutes(5), waiter2);
                addOrderItem(order4, items.get(12), 1); // 1 Hamburguesa
                addOrderItem(order4, items.get(14), 1); // 1 Pasta
                addOrderItem(order4, items.get(2), 2);  // 2 Aguas
                orderService.createOrder(order4);
                System.out.println("Created Order 4: Mesa 4 (Pending payment)");

                // Order 5: Another pending order
                Order order5 = createOrder("Mesa 5", PaymentStatus.PENDING, PreparationStatus.PENDING,
                    DeliveryStatus.PENDING, now.minusMinutes(2), waiter1);
                addOrderItem(order5, items.get(11), 1); // 1 Ensalada
                addOrderItem(order5, items.get(6), 1);  // 1 Vino blanco
                orderService.createOrder(order5);
                System.out.println("Created Order 5: Mesa 5 (Pending)");

                // Order 6: Cancelled order
                Order order6 = createOrder("Mesa 6", PaymentStatus.CANCELLED, PreparationStatus.CANCELLED,
                    DeliveryStatus.CANCELLED, now.minusHours(1), waiter2);
                addOrderItem(order6, items.get(1), 1);  // 1 Té
                orderService.createOrder(order6);
                System.out.println("Created Order 6: Mesa 6 (Cancelled)");

                System.out.println("Successfully created 6 sample orders with items");
            }
        }

        System.out.println("Database initialization complete!");
    }

    /**
     * Creates a new Order with basic information
     */
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

    /**
     * Adds an OrderItem to an Order
     * Note: Does NOT call setOrder() to avoid setting OrderItemId prematurely.
     * The OrderService.createOrder() will handle the relationship properly.
     */
    private void addOrderItem(Order order, Item item, int amount) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setAmount(amount);
        // Don't call setOrder() here - let the service handle it
        order.getOrderItems().add(orderItem);
    }

    private String formatTime(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }
}
