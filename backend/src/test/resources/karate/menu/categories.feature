Feature: CRUD de categorias

  Background:
    * url baseUrl
    * def login = callonce read('classpath:karate/common/login.feature') { user: '#(adminUser)', pass: '#(adminPass)' }
    * configure headers = { Authorization: '#("Bearer " + login.token)' }
    * def categoryName = 'KarateCat-' + java.util.UUID.randomUUID()

  Scenario: Crear y eliminar categoria
    Given path '/api/categories'
    And request { name: '#(categoryName)' }
    When method post
    Then status 201
    And match response.name == categoryName
    * def categoryId = response.id

    Given path '/api/categories/', categoryId
    When method get
    Then status 200
    And match response.name == categoryName

    Given path '/api/categories/', categoryId
    When method delete
    Then status 204

    Given path '/api/categories/', categoryId
    When method get
    Then status 404
    And match response.errorCode == 'NOT_FOUND'
