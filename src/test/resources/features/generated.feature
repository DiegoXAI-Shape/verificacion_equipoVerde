# Archivo generado por generator.Main a partir del modelo BPMN. No editar a mano.
@generated
Feature: Testing BPM Processes - Compra SauceDemo

  # F1: StartEvent_1 -> UT_Login -> ST_Auth -> Gw_Login -> UT_AddCart -> UT_OpenCart -> UT_Checkout -> UT_FillForm -> Gw_Form -> UT_Finish -> ST_Order -> MT_Thanks -> End_OK
  Scenario: Compra SauceDemo 0 (Orden completada)
    Given I am on the task Ingresar usuario y contrasena
    Then the task Ingresar usuario y contrasena is completed
    When I am on the task Agregar producto al carrito
    Then the task Agregar producto al carrito is completed
    When I am on the task Abrir el carrito
    Then the task Abrir el carrito is completed
    When I am on the task Iniciar checkout
    Then the task Iniciar checkout is completed
    When I am on the task Capturar datos de envio
    Then the task Capturar datos de envio is completed
    When I am on the task Confirmar la orden
    Then the task Confirmar la orden is completed
    When I am on the task Verificar mensaje de agradecimiento
    Then the task Verificar mensaje de agradecimiento is completed

  # F2: StartEvent_1 -> UT_Login -> ST_Auth -> Gw_Login -> UT_AddCart -> UT_OpenCart -> UT_Checkout -> UT_FillForm -> Gw_Form -> MT_FormError -> Gw_Retry -> UT_FillForm -> Gw_Form -> UT_Finish -> ST_Order -> MT_Thanks -> End_OK
  Scenario: Compra SauceDemo 1 (Orden completada)
    Given I am on the task Ingresar usuario y contrasena
    Then the task Ingresar usuario y contrasena is completed
    When I am on the task Agregar producto al carrito
    Then the task Agregar producto al carrito is completed
    When I am on the task Abrir el carrito
    Then the task Abrir el carrito is completed
    When I am on the task Iniciar checkout
    Then the task Iniciar checkout is completed
    When I am on the task Capturar datos de envio
    Then the task Capturar datos de envio is completed
    When I am on the task Verificar mensaje de error del formulario
    Then the task Verificar mensaje de error del formulario is completed
    When I am on the task Capturar datos de envio
    Then the task Capturar datos de envio is completed
    When I am on the task Confirmar la orden
    Then the task Confirmar la orden is completed
    When I am on the task Verificar mensaje de agradecimiento
    Then the task Verificar mensaje de agradecimiento is completed

  # F3: StartEvent_1 -> UT_Login -> ST_Auth -> Gw_Login -> UT_AddCart -> UT_OpenCart -> UT_Checkout -> UT_FillForm -> Gw_Form -> MT_FormError -> Gw_Retry -> UT_FillForm -> Gw_Form -> MT_FormError -> Gw_Retry -> End_FormError
  Scenario: Compra SauceDemo 2 (Checkout abandonado)
    Given I am on the task Ingresar usuario y contrasena
    Then the task Ingresar usuario y contrasena is completed
    When I am on the task Agregar producto al carrito
    Then the task Agregar producto al carrito is completed
    When I am on the task Abrir el carrito
    Then the task Abrir el carrito is completed
    When I am on the task Iniciar checkout
    Then the task Iniciar checkout is completed
    When I am on the task Capturar datos de envio
    Then the task Capturar datos de envio is completed
    When I am on the task Verificar mensaje de error del formulario
    Then the task Verificar mensaje de error del formulario is completed
    When I am on the task Capturar datos de envio
    Then the task Capturar datos de envio is completed
    When I am on the task Verificar mensaje de error del formulario
    Then the task Verificar mensaje de error del formulario is completed

  # F4: StartEvent_1 -> UT_Login -> ST_Auth -> Gw_Login -> UT_AddCart -> UT_OpenCart -> UT_Checkout -> UT_FillForm -> Gw_Form -> MT_FormError -> Gw_Retry -> End_FormError
  Scenario: Compra SauceDemo 3 (Checkout abandonado)
    Given I am on the task Ingresar usuario y contrasena
    Then the task Ingresar usuario y contrasena is completed
    When I am on the task Agregar producto al carrito
    Then the task Agregar producto al carrito is completed
    When I am on the task Abrir el carrito
    Then the task Abrir el carrito is completed
    When I am on the task Iniciar checkout
    Then the task Iniciar checkout is completed
    When I am on the task Capturar datos de envio
    Then the task Capturar datos de envio is completed
    When I am on the task Verificar mensaje de error del formulario
    Then the task Verificar mensaje de error del formulario is completed

  # F5: StartEvent_1 -> UT_Login -> ST_Auth -> Gw_Login -> MT_Locked -> End_Locked
  Scenario: Compra SauceDemo 4 (Acceso denegado)
    Given I am on the task Ingresar usuario y contrasena
    Then the task Ingresar usuario y contrasena is completed
    When I am on the task Verificar mensaje de usuario bloqueado
    Then the task Verificar mensaje de usuario bloqueado is completed
