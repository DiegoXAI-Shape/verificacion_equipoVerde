Feature: Login y checkout en SauceDemo
  Pruebas reales derivadas de los caminos de la tabla de flujos (output/flow_table.csv).
  Cada escenario lleva el tag de su fila en la tabla (@F1 ... @F5).

  Background:
    Given que estoy en la pagina de login de SauceDemo

  # Camino F1: login valido -> carrito -> checkout -> Finish
  @F1 @login @checkout
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

  # Camino F2: el formulario falla, el usuario corrige y termina la compra (ciclo "Corregir datos? -> Si")
  @F2 @checkout
  Scenario: Corregir el formulario y completar la compra
    When inicio sesion con usuario "standard_user" y contrasena "secret_sauce"
    And agrego el producto "Sauce Labs Backpack" al carrito
    And abro el carrito
    And inicio el checkout
    And capturo First Name "Juan", Last Name "Perez" y Zip ""
    And presiono Continue
    Then veo el mensaje de error "Error: Postal Code is required"
    When corrijo el campo "Zip" a "64000"
    And presiono Continue
    Then veo el resumen de la orden
    When presiono Finish
    Then veo el mensaje de confirmacion "Thank you for your order!"

  # Camino F3: error de validacion del formulario y el usuario se rinde
  @F3 @checkout
  Scenario: Checkout con First Name vacio
    When inicio sesion con usuario "standard_user" y contrasena "secret_sauce"
    And agrego el producto "Sauce Labs Backpack" al carrito
    And abro el carrito
    And inicio el checkout
    And capturo First Name "", Last Name "Perez" y Zip "64000"
    And presiono Continue
    Then veo el mensaje de error "Error: First Name is required"

  # Camino F4: falla, corrige, vuelve a fallar y abandona el checkout (ciclo recorrido dos veces)
  @F4 @checkout
  Scenario: Corregir el formulario, volver a fallar y abandonar
    When inicio sesion con usuario "standard_user" y contrasena "secret_sauce"
    And agrego el producto "Sauce Labs Backpack" al carrito
    And abro el carrito
    And inicio el checkout
    And capturo First Name "Juan", Last Name "Perez" y Zip ""
    And presiono Continue
    Then veo el mensaje de error "Error: Postal Code is required"
    When corrijo el campo "Zip" a "64000"
    And corrijo el campo "First Name" a ""
    And presiono Continue
    Then veo el mensaje de error "Error: First Name is required"
    When cancelo el checkout
    Then regreso al carrito

  # Camino F5: usuario bloqueado
  @F5 @login
  Scenario: Login con usuario bloqueado
    When inicio sesion con usuario "locked_out_user" y contrasena "secret_sauce"
    Then veo el mensaje de error "Epic sadface: Sorry, this user has been locked out."

  # Particion de equivalencia / valores limite del Zip (formato mexicano: 5 digitos).
  # El valido recorre el camino F1; los invalidos deberian recorrer F3.
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

    @F1
    Examples: Zip valido
      | caso          | zip           | resultado |
      | valido        | 64000         | aceptado  |

    @F3
    Examples: Zip invalido
      | caso          | zip           | resultado |
      | vacio         | <vacio>       | rechazado |
      | con letras    | A1027         | rechazado |
      | 4 digitos     | 6400          | rechazado |
      | 6 digitos     | 640000        | rechazado |
      | solo espacios | <3 espacios>  | rechazado |
      | simbolos      | @#$%!         | rechazado |
