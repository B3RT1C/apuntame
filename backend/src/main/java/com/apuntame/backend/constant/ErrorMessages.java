package com.apuntame.backend.constant;

public class ErrorMessages {

    // User errors
    public static final String USER_NOT_FOUND = "Usuario no encontrado: %s";
    public static final String USER_ALREADY_EXISTS = "El usuario '%s' ya existe";
    public static final String USERNAME_EMPTY = "El nombre de usuario no puede estar vacío";
    public static final String PASSWORD_EMPTY = "La contraseña no puede estar vacío";
    public static final String ROLE_EMPTY = "El rol no puede estar vacío";

    // Item errors
    public static final String ITEM_NOT_FOUND = "Producto no encontrado con id: %s";
    public static final String ITEM_NAME_EMPTY = "El nombre del producto no puede estar vacía";
    public static final String ITEM_PRICE_INVALID = "El precio debe ser mayor que 0";

    // Order errors
    public static final String ORDER_NOT_FOUND = "Pedido no encontrado con id: %s";
    //TODO
    //public static final String ORDER_TABLE_EMPTY = "El número de mesa no puede estar vacío";
    public static final String ORDER_STATE_EMPTY = "El estado del pedido no puede estar vacío";

    // OrderItem errors
    public static final String ORDER_ITEM_NOT_FOUND = "OrderItem no encontrado con id: %s";
    public static final String ORDER_ITEM_AMOUNT_INVALID = "La cantidad debe ser mayor que 0";

    // Generic
    public static final String INTERNAL_ERROR = "Ha ocurrido un error interno del servidor";

    private ErrorMessages() {
        // Clase de utilidad, no instanciable
    }
}