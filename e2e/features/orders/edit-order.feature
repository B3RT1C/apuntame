Feature: Editar pedido

  Background:
    Given he iniciado sesion como "camarero1"
    And existe un pedido pendiente en mesa "77" con "Coca-Cola"

  Scenario: Eliminar productos de un pedido existente
    When abro editar pedido
    And selecciono el pedido de mesa "77"
    And elimino todos los productos del dialogo de edicion
    And confirmo los cambios del pedido
    Then los cambios del pedido se guardaron correctamente
