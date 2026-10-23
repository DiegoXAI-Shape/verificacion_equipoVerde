Feature: Login y checkout en SauceDemo
  Pruebas reales derivadas de los caminos de la tabla de flujos (output/flow_table.csv).

  Background:
    Given que estoy en la pagina de login de SauceDemo

  # Camino F1: login valido -> carrito -> checkout -> Finish
  @login @checkout
  Scenario: Login valido y checkout completo
    When inicio sesion con usuario "standard_user" y contrasena "secret_sauce"
    Then veo la pagina de productos
    When agrego el producto "Sauce Labs Backpack" al carrito
    And abro el carrito
    And inicio el checkout
    And capturo First Name "Juan", Last Name "Perez" y Zip "64000"
    And presiono Continue
    Then veo el resumen de la orden
    When presiono Finish
    Then veo el mensaje de confirmacion "Thank you for your order!"

  # Camino F5: usuario bloqueado
  @login
  Scenario: Login con usuario bloqueado
    When inicio sesion con usuario "locked_out_user" y contrasena "secret_sauce"
    Then veo el mensaje de error "Epic sadface: Sorry, this user has been locked out."

  # Caminos F3/F4: error de validacion del formulario
  @checkout
  Scenario: Checkout con First Name vacio
    When inicio sesion con usuario "standard_user" y contrasena "secret_sauce"
    And agrego el producto "Sauce Labs Backpack" al carrito
    And abro el carrito
    And inicio el checkout
    And capturo First Name "", Last Name "Perez" y Zip "64000"
    And presiono Continue
    Then veo el mensaje de error "Error: First Name is required"

  # Particion de equivalencia / valores limite del Zip (formato mexicano: 5 digitos).
  # Gherkin recorta los espacios de las celdas, por eso "<vacio>" y "<3 espacios>" son marcadores.
  @checkout @zip
  Scenario Outline: Validacion del Zip/Postal Code "<caso>"
    When inicio sesion con usuario "standard_user" y contrasena "secret_sauce"
    And agrego el producto "Sauce Labs Backpack" al carrito
    And abro el carrito
    And inicio el checkout
    And capturo First Name "Juan", Last Name "Perez" y Zip "<zip>"
    And presiono Continue
    Then el Zip es "<resultado>"

    Examples:
      | caso          | zip           | resultado |
      | valido        | 64000         | aceptado  |
      | vacio         | <vacio>       | rechazado |
      | con letras    | A1027         | rechazado |
      | 4 digitos     | 6400          | rechazado |
      | 6 digitos     | 640000        | rechazado |
      | solo espacios | <3 espacios>  | rechazado |
      | simbolos      | @#$%!         | rechazado |
