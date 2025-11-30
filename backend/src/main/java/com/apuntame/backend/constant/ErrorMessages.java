package com.apuntame.backend.constant;

public class ErrorMessages {

    // User errors
    public static final String USER_NOT_FOUND = "Usuario no encontrado: %s";
    public static final String USER_ALREADY_EXISTS = "El usuario '%s' ya existe";
    public static final String USERNAME_EMPTY = "El nombre de usuario no puede estar vacío";
    public static final String PASSWORD_EMPTY = "La contraseña no puede estar vacía";
    public static final String ROLE_EMPTY = "El rol no puede estar vacío";

    // Authentication errors
    public static final String BAD_CREDENTIALS = "Usuario o contraseña incorrectos";
    public static final String UNAUTHORIZED = "No tienes autorización para acceder a este recurso";

    // Item errors
    public static final String ITEM_NOT_FOUND = "Producto no encontrado con id: %s";
    public static final String ITEM_NAME_EMPTY = "El nombre del producto no puede estar vacío";
    public static final String ITEM_PRICE_INVALID = "El precio debe ser mayor que 0";

    // Category errors
    public static final String CATEGORY_NOT_FOUND = "Categoría no encontrada con id: %s";
    public static final String CATEGORY_NAME_EMPTY = "El nombre de la categoría no puede estar vacío";

    // Section errors
    public static final String SECTION_NOT_FOUND = "Sección no encontrada con id: %s";
    public static final String SECTION_NAME_EMPTY = "El nombre de la sección no puede estar vacío";

    // Order errors
    public static final String ORDER_NOT_FOUND = "Pedido no encontrado con id: %s";
    //TODO
    //public static final String ORDER_TABLE_EMPTY = "El número de mesa no puede estar vacío";
    public static final String ORDER_PAYMENT_STATUS_INVALID = "Estado de pago inválido";
    public static final String ORDER_PREPARATION_STATUS_INVALID = "Estado de preparación inválido";
    public static final String ORDER_DELIVERY_STATUS_INVALID = "Estado de entrega inválido";

    // OrderItem errors
    public static final String ORDER_ITEM_NOT_FOUND = "Producto en pedido no encontrado (Order ID: %s, Item ID: %s)";
    public static final String ORDER_ITEM_AMOUNT_INVALID = "La cantidad debe ser mayor que 0";
    public static final String ORDER_ITEM_NULL = "El item no puede ser nulo en un OrderItem";

    // Generic
    public static final String INTERNAL_ERROR = "Ha ocurrido un error interno del servidor";

    private ErrorMessages() {}
}