Feature: Autenticacion API

  Scenario: Login exitoso como admin
    Given url baseUrl + '/api/auth/login'
    And request { username: '#(adminUser)', password: '#(adminPass)' }
    When method post
    Then status 200
    And match response contains { token: '#string', username: '#(adminUser)', role: 'ADMIN' }

  Scenario: Login exitoso como camarero
    Given url baseUrl + '/api/auth/login'
    And request { username: '#(waiterUser)', password: '#(waiterPass)' }
    When method post
    Then status 200
    And match response contains { token: '#string', username: '#(waiterUser)', role: 'WAITER' }

  Scenario: Login con credenciales invalidas
    Given url baseUrl + '/api/auth/login'
    And request { username: 'admin', password: 'wrong-password' }
    When method post
    Then status 401
    And match response.errorCode == 'UNAUTHORIZED'
