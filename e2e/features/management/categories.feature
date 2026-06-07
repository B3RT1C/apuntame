Feature: Gestion de categorias

  Background:
    Given he iniciado sesion como "admin"

  Scenario: Crear una categoria nueva
    When voy a "Gestión"
    And abro la pestaña "Categorías"
    And creo una categoria con nombre unico
    Then veo el mensaje "Categoría creada exitosamente"
    And veo la categoria creada en la tabla
