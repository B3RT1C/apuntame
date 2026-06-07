Feature: Inicio de sesion

  Scenario: Login con credenciales validas
    Given estoy en la pagina de login
    When introduzco usuario "camarero1" y contraseña "camarero1"
    And pulso "Iniciar Sesión"
    Then veo la pagina de inicio

  Scenario: Login con credenciales invalidas
    Given estoy en la pagina de login
    When introduzco usuario "admin" y contraseña "incorrecta"
    And pulso "Iniciar Sesión"
    Then veo el mensaje de error de login