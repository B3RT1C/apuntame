Feature: Seguridad API

  Scenario: Acceso sin token JWT
    Given url baseUrl + '/api/orders'
    When method get
    Then status 403

  Scenario: Acceso con token invalido
    Given url baseUrl + '/api/orders'
    And header Authorization = 'Bearer token-invalido'
    When method get
    Then status 403
