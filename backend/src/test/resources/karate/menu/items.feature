Feature: Productos del menu

  Background:
    * def login = callonce read('classpath:karate/common/login.feature') { user: '#(waiterUser)', pass: '#(waiterPass)' }
    * configure headers = { Authorization: '#("Bearer " + login.token)' }

  Scenario: Listar productos con datos demo
    Given path '/api/items'
    When method get
    Then status 200
    And match response == '#? _.length >= 20'
    And match response[0].name == '#string'

  Scenario: Filtrar productos por categoria
    Given path '/api/items'
    And param categories = 1
    And param filterMode = 'OR'
    When method get
    Then status 200
    And match each response contains { id: '#number', name: '#string', price: '#number' }
