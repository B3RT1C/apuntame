Feature: Ciclo de vida de pedido

  Background:
    * url baseUrl
    * def login = callonce read('classpath:karate/common/login.feature') { user: '#(waiterUser)', pass: '#(waiterPass)' }
    * configure headers = { Authorization: '#("Bearer " + login.token)' }

  Scenario: Crear pedido y marcarlo como pagado
    Given path '/api/orders'
    And request
      """
      {
        "table": "Mesa 99",
        "takenBy": { "username": "#(waiterUser)" },
        "orderItems": [{ "item": { "id": 1 }, "amount": 2 }]
      }
      """
    When method post
    Then status 201
    And match response.order.table == 'Mesa 99'
    And match response.order.paymentStatus == 'PENDING'
    * def orderId = response.order.id

    Given path '/api/orders/', orderId, '/payment-status'
    And request 'PAID'
    And header Content-Type = 'application/json'
    When method patch
    Then status 200
    And match response.order.paymentStatus == 'PAID'
    And match response.order.chargedBy.username == '#(waiterUser)'
