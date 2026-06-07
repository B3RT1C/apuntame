Feature: Gestion de usuarios

  Background:
    * def login = callonce read('classpath:karate/common/login.feature') { user: '#(adminUser)', pass: '#(adminPass)' }
    * configure headers = { Authorization: '#("Bearer " + login.token)' }

  Scenario: Listar usuarios demo
    Given path '/api/users'
    When method get
    Then status 200
    And match response == '#[3]'

  Scenario: Crear usuario y detectar duplicado
    * def newUser = 'karate-user-' + java.util.UUID.randomUUID()

    Given path '/api/users'
    And request { username: '#(newUser)', password: 'testpass', role: 'WAITER' }
    When method post
    Then status 201
    And match response.username == newUser

    Given path '/api/users'
    And request { username: '#(newUser)', password: 'testpass2', role: 'WAITER' }
    When method post
    Then status 409
    And match response.errorCode == 'CONFLICT'

    Given path '/api/users/', newUser
    When method delete
    Then status 204

  Scenario: Usuario inexistente devuelve 404
    Given path '/api/users/usuario-que-no-existe'
    When method get
    Then status 404
    And match response.errorCode == 'NOT_FOUND'
