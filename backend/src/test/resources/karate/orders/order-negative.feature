Feature: Casos negativos de pedidos

  Background:
    * def login = callonce read('classpath:karate/common/login.feature') { user: '#(waiterUser)', pass: '#(waiterPass)' }
    * configure headers = { Authorization: '#("Bearer " + login.token)' }

  Scenario: Pedido inexistente devuelve 404
    Given path '/api/orders/999999'
    When method get
    Then status 404
    And match response.errorCode == 'NOT_FOUND'

  Scenario: Estado de pago invalido devuelve 400
    Given path '/api/orders'
    And request { table: 'Mesa Neg', takenBy: { username: '#(waiterUser)' } }
    When method post
    Then status 201
    * def orderId = response.order.id

    Given path '/api/orders/', orderId, '/payment-status'
    And request 'INVALID_STATUS'
    And header Content-Type = 'application/json'
    When method patch
    Then status 400
    And match response.errorCode == 'BAD_REQUEST'
