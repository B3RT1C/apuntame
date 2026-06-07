Feature: Tomar pedido

  Background:
    Given he iniciado sesion como "camarero1"

  Scenario: Crear pedido para una mesa
    When voy a "Tomar pedidos"
    And escribo mesa "42"
    And añado el articulo "Coca-Cola" al pedido
    And envio el pedido
    Then el pedido actual queda vacio
