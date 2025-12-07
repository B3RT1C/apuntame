package com.apuntame.backend.config;

import com.apuntame.backend.constant.DateTimeConstants;
import com.apuntame.backend.enums.DeliveryStatus;
import com.apuntame.backend.enums.PaymentStatus;
import com.apuntame.backend.enums.PreparationStatus;
import com.apuntame.backend.model.Category;
import com.apuntame.backend.model.Item;
import com.apuntame.backend.model.Order;
import com.apuntame.backend.model.OrderItem;
import com.apuntame.backend.model.Section;
import com.apuntame.backend.model.User;
import com.apuntame.backend.repository.CategoryRepository;
import com.apuntame.backend.repository.ItemRepository;
import com.apuntame.backend.repository.OrderRepository;
import com.apuntame.backend.repository.SectionRepository;
import com.apuntame.backend.repository.UserRepository;
import com.apuntame.backend.service.CategoryService;
import com.apuntame.backend.service.ItemService;
import com.apuntame.backend.service.OrderService;
import com.apuntame.backend.service.SectionService;
import com.apuntame.backend.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Value("${apuntame.initialize-demo-data:false}")
    private boolean initializeDemoData;

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final CategoryRepository categoryRepository;
    private final SectionRepository sectionRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final OrderService orderService;
    private final CategoryService categoryService;
    private final SectionService sectionService;

    public DataInitializer(UserRepository userRepository,
                          ItemRepository itemRepository,
                          OrderRepository orderRepository,
                          CategoryRepository categoryRepository,
                          SectionRepository sectionRepository,
                          UserService userService,
                          ItemService itemService,
                          OrderService orderService,
                          CategoryService categoryService,
                          SectionService sectionService) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
        this.categoryRepository = categoryRepository;
        this.sectionRepository = sectionRepository;
        this.userService = userService;
        this.itemService = itemService;
        this.orderService = orderService;
        this.categoryService = categoryService;
        this.sectionService = sectionService;
    }

    @Override
    public void run(String... args) {
        boolean isDatabaseEmpty =
            userRepository.count() == 0 &&
            itemRepository.count() == 0 &&
            categoryRepository.count() == 0 &&
            sectionRepository.count() == 0;

        if (!isDatabaseEmpty) {
            System.out.println("Database already initialized, skipping initialization");
            return;
        }

        System.out.println("First run detected - Initializing database...");
        initializeAdminUser();

        if (initializeDemoData) {
            System.out.println("Demo data initialization enabled");
            initializeCategories();
            initializeSections();
            initializeMenuItems();
            initializeDemoUsers();
            initializeSampleOrders();
            System.out.println("Demo data initialization complete!");
        } else {
            System.out.println("Demo data initialization disabled (set INITIALIZE_DEMO_DATA=true to enable)");
        }

        System.out.println("Database initialization complete!");
    }

    private void initializeAdminUser() {
        userService.createUser(new User("admin", "admin", "ADMIN"));
        System.out.println("Created admin user (username: admin, password: admin)");
        System.out.println("SECURITY WARNING: Change admin password immediately after first login!");
    }

    private void initializeDemoUsers() {
        userService.createUser(new User("camarero1", "camarero1", "WAITER"));
        userService.createUser(new User("camarero2", "camarero2", "WAITER"));
        System.out.println("Created demo waiters (camarero1, camarero2)");
    }

    private void initializeCategories() {
        categoryService.createCategory(new Category("Entrantes"));
        categoryService.createCategory(new Category("Ensaladas"));
        categoryService.createCategory(new Category("Carnes"));
        categoryService.createCategory(new Category("Pescados"));
        categoryService.createCategory(new Category("Postres"));
        categoryService.createCategory(new Category("Bebidas"));
        categoryService.createCategory(new Category("Cafés"));
        categoryService.createCategory(new Category("Infusiones"));
        System.out.println("Created 8 categories");
    }

    private void initializeSections() {
        sectionService.createSection(new Section("Barra"));
        sectionService.createSection(new Section("Plancha"));
        sectionService.createSection(new Section("Fríos"));
        sectionService.createSection(new Section("Horno"));
        sectionService.createSection(new Section("Postres"));
        sectionService.createSection(new Section("Freidora"));
        System.out.println("Created 6 sections");
    }

    private void initializeMenuItems() {
        List<Category> categories = categoryRepository.findAll();
        Category entrantes = categories.get(0);
        Category ensaladas = categories.get(1);
        Category carnes = categories.get(2);
        Category pescados = categories.get(3);
        Category postres = categories.get(4);
        Category bebidas = categories.get(5);
        Category cafes = categories.get(6);
        Category infusiones = categories.get(7);

        List<Section> sections = sectionRepository.findAll();
        Section barra = sections.get(0);
        Section plancha = sections.get(1);
        Section frios = sections.get(2);
        Section horno = sections.get(3);
        Section postresZone = sections.get(4);
        Section freidora = sections.get(5);

        // Entrantes
        createItem("Croquetas de jamón", "8.50", List.of(entrantes), List.of(freidora));
        createItem("Jamón ibérico", "16.00", List.of(entrantes), List.of(frios));
        createItem("Tortilla española", "6.50", List.of(entrantes), List.of(plancha));
        createItem("Calamares a la romana", "10.00", List.of(entrantes), List.of(freidora));

        // Ensaladas
        createItem("Ensalada mixta", "7.00", List.of(ensaladas), List.of(frios));
        createItem("Ensalada César", "8.50", List.of(ensaladas), List.of(frios));

        // Carnes
        createItem("Entrecot de ternera", "18.50", List.of(carnes), List.of(plancha));
        createItem("Pollo asado", "12.00", List.of(carnes), List.of(horno));
        createItem("Secreto ibérico", "15.00", List.of(carnes), List.of(plancha));

        // Pescados
        createItem("Merluza a la plancha", "14.00", List.of(pescados), List.of(plancha));
        createItem("Salmón al horno", "16.50", List.of(pescados), List.of(horno));

        // Postres
        createItem("Tarta de queso", "5.50", List.of(postres), List.of(postresZone));
        createItem("Flan casero", "4.00", List.of(postres), List.of(postresZone));
        createItem("Helado", "4.50", List.of(postres), List.of(postresZone));

        // Bebidas
        createItem("Agua mineral", "1.50", List.of(bebidas), List.of(barra));
        createItem("Coca-Cola", "2.50", List.of(bebidas), List.of(barra));
        createItem("Cerveza Estrella Galicia", "2.50", List.of(bebidas), List.of(barra));
        createItem("Vino tinto (copa)", "3.00", List.of(bebidas), List.of(barra));
        createItem("Vino blanco (copa)", "3.00", List.of(bebidas), List.of(barra));

        // Cafés e infusiones
        createItem("Café solo", "1.20", List.of(cafes), List.of(barra));
        createItem("Café con leche", "1.50", List.of(cafes), List.of(barra));
        createItem("Té verde", "1.50", List.of(infusiones), List.of(barra));
        createItem("Manzanilla", "1.30", List.of(infusiones), List.of(barra));

        System.out.println("Created 23 realistic Spanish restaurant menu items");
    }

    private void createItem(String name, String price, List<Category> categories, List<Section> sections) {
        Item item = new Item(name, new BigDecimal(price));
        item.setCategories(categories);
        item.setSections(sections);
        itemService.createItem(item);
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

        System.out.println("Successfully created 6 sample orders");
    }

    private void createCompletedOrder(List<Item> items, User waiter, LocalDateTime now) {
        Order order = createOrder("Mesa 1", PaymentStatus.PAID, PreparationStatus.READY,
            DeliveryStatus.DELIVERED, now.minusHours(2), waiter);
        order.setPaidAt(now.minusHours(2).plusMinutes(5).format(DateTimeConstants.TIMESTAMP_FORMATTER));
        order.setPreparedAt(now.minusHours(2).plusMinutes(15).format(DateTimeConstants.TIMESTAMP_FORMATTER));
        order.setDeliveredAt(now.minusHours(2).plusMinutes(20).format(DateTimeConstants.TIMESTAMP_FORMATTER));
        order.setChargedBy(waiter);
        order.setPreparedBy(waiter);
        order.setDeliveredBy(waiter);
        addOrderItem(order, items.get(0), 2); // Croquetas
        addOrderItem(order, items.get(19), 2); // Café solo
        orderService.createOrder(order);
    }

    private void createReadyForDeliveryOrder(List<Item> items, User waiter, LocalDateTime now) {
        Order order = createOrder("Mesa 2", PaymentStatus.PAID, PreparationStatus.READY,
            DeliveryStatus.PENDING, now.minusMinutes(30), waiter);
        order.setPaidAt(now.minusMinutes(30).plusMinutes(2).format(DateTimeConstants.TIMESTAMP_FORMATTER));
        order.setPreparedAt(now.minusMinutes(10).format(DateTimeConstants.TIMESTAMP_FORMATTER));
        order.setChargedBy(waiter);
        order.setPreparedBy(waiter);
        addOrderItem(order, items.get(6), 1); // Entrecot
        addOrderItem(order, items.get(16), 1); // Cerveza
        orderService.createOrder(order);
    }

    private void createPendingPreparationOrder(List<Item> items, User waiter, LocalDateTime now) {
        Order order = createOrder("Mesa 3", PaymentStatus.PAID, PreparationStatus.PENDING,
            DeliveryStatus.PENDING, now.minusMinutes(20), waiter);
        order.setPaidAt(now.minusMinutes(20).plusMinutes(1).format(DateTimeConstants.TIMESTAMP_FORMATTER));
        order.setChargedBy(waiter);
        addOrderItem(order, items.get(7), 2); // Pollo asado
        addOrderItem(order, items.get(15), 2); // Coca-Cola
        orderService.createOrder(order);
    }

    private void createPendingPaymentOrder(List<Item> items, User waiter, LocalDateTime now) {
        Order order = createOrder("Mesa 4", PaymentStatus.PENDING, PreparationStatus.PENDING,
            DeliveryStatus.PENDING, now.minusMinutes(5), waiter);
        addOrderItem(order, items.get(9), 1); // Merluza
        addOrderItem(order, items.get(11), 1); // Tarta de queso
        addOrderItem(order, items.get(14), 2); // Agua
        orderService.createOrder(order);
    }

    private void createAnotherPendingOrder(List<Item> items, User waiter, LocalDateTime now) {
        Order order = createOrder("Mesa 5", PaymentStatus.PENDING, PreparationStatus.PENDING,
            DeliveryStatus.PENDING, now.minusMinutes(2), waiter);
        addOrderItem(order, items.get(4), 1); // Ensalada mixta
        addOrderItem(order, items.get(18), 1); // Vino blanco
        orderService.createOrder(order);
    }

    private void createCancelledOrder(List<Item> items, User waiter, LocalDateTime now) {
        Order order = createOrder("Mesa 6", PaymentStatus.CANCELLED, PreparationStatus.CANCELLED,
            DeliveryStatus.CANCELLED, now.minusHours(1), waiter);
        addOrderItem(order, items.get(1), 1); // Jamón ibérico
        orderService.createOrder(order);
    }

    private Order createOrder(String table, PaymentStatus paymentStatus,
                             PreparationStatus preparationStatus, DeliveryStatus deliveryStatus,
                             LocalDateTime creationDate, User takenBy) {
        Order order = new Order();
        order.setTable(table);
        order.setPaymentStatus(paymentStatus);
        order.setPreparationStatus(preparationStatus);
        order.setDeliveryStatus(deliveryStatus);
        order.setCreationDate(creationDate.format(DateTimeConstants.TIMESTAMP_FORMATTER));
        order.setTakenBy(takenBy);
        return order;
    }

    private void addOrderItem(Order order, Item item, int amount) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setAmount(amount);
        order.getOrderItems().add(orderItem);
    }
}
