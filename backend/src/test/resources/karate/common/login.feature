@ignore
Feature: Helper de login reutilizable

  Background:
    * url baseUrl

  Scenario:
    Given path '/api/auth/login'
    And request { username: '#(user)', password: '#(pass)' }
    When method post
    Then status 200
    And def token = response.token
    And def username = response.username
    And def role = response.role
