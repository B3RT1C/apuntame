@serial
Feature: Vista de pedidos

  Background:
    Given he iniciado sesion como "camarero1"
    And existe un pedido pendiente en mesa "88" con "Coca-Cola"

  Scenario: Marcar pedido como entregado desde la vista
    When abro la vista de pedidos
    Then veo el pedido de mesa "88"
    When marco como entregado el pedido de mesa "88"
    Then el pedido de mesa "88" muestra estado entregado
