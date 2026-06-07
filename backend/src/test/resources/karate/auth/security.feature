Feature: Seguridad API

  Scenario: Acceso sin token JWT
    Given path '/api/orders'
    When method get
    Then status 403

  Scenario: Acceso con token invalido
    Given path '/api/orders'
    And header Authorization = 'Bearer token-invalido'
    When method get
    Then status 403
