# Matriz de Cobertura de Flujos - SauceDemo (Metodología de Moura et al., 2017)

Siguiendo la metodología del paper `demoura2017.pdf`:
- Cada columna representa una tarea/acción del diagrama BPMN.
- Cada fila representa un flujo de ejecución completo (de inicio a fin).
- "X" = La tarea se ejecuta en ese flujo.
- "-" = La tarea no se ejecuta.
- **Cada fila equivale a un `Scenario` en Gherkin (Cucumber).**

---

## 1. Proceso de Login (`bpmn/opcionB_saucedemo_login.bpmn`)
**Bifurcación:** Gateway Exclusivo (3 ramas según credenciales)

| Flujo / Tarea | Abrir login | Ingresar credenciales | Clic botón Login | Verificar Inventory (éxito) | Verificar error "locked out" | Verificar error "do not match" |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: |
| **F1: Login exitoso (standard_user)** | X | X | X | X | - | - |
| **F2: Usuario bloqueado (locked_out_user)** | X | X | X | - | X | - |
| **F3: Credenciales inválidas** | X | X | X | - | - | X |

**Escenarios Cucumber correspondientes:**
1. `Scenario: Iniciar sesión exitosamente con standard_user`
2. `Scenario: Intento de inicio de sesión con usuario bloqueado`
3. `Scenario: Intento de inicio de sesión con contraseña incorrecta`

---

## 2. Proceso de Catálogo y Carrito (`bpmn/opcionB_saucedemo_carrito.bpmn`)
**Bifurcación:** Gateway Exclusivo (Continuar a carrito vs Quitar producto)

| Flujo / Tarea | Ver Inventory | Clic "Add to cart" | Verificar badge (+1) | Clic icono Carrito | Verificar producto en carrito | Clic botón "Remove" | Verificar badge vacío |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **F1: Agregar producto y ver en carrito** | X | X | X | X | X | - | - |
| **F2: Agregar y remover producto** | X | X | X | - | - | X | X |

**Escenarios Cucumber correspondientes:**
1. `Scenario: Agregar un producto y verificarlo en la página del carrito`
2. `Scenario: Remover un producto previamente agregado al carrito`

---

## 3. Proceso de Checkout / Compra (`bpmn/opcionB_saucedemo_checkout.bpmn`)
**Bifurcación:** Gateway Exclusivo (3 ramas: Compra exitosa, Validación de campos, Cancelación)

| Flujo / Tarea | Clic Checkout | Llenar formulario (First, Last, Zip) | Clic Continue | Ver resumen Overview | Clic Finish | Verificar "Thank you..." | Dejar campos vacíos | Clic Continue (error) | Verificar error First Name | Clic Cancel | Retorno a Carrito |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **F1: Compra exitosa** | X | X | X | X | X | X | - | - | - | - | - |
| **F2: Formulario incompleto** | X | - | - | - | - | - | X | X | X | - | - |
| **F3: Cancelar checkout** | X | - | - | - | - | - | - | - | - | X | X |

**Escenarios Cucumber correspondientes:**
1. `Scenario: Completar orden de compra exitosamente con datos válidos`
2. `Scenario: Intento de checkout con campos requeridos vacíos`
3. `Scenario: Cancelar el proceso de checkout y regresar al carrito`

---

## Resumen de Cobertura
- **Total de procesos modelados:** 3 (Login, Carrito, Checkout).
- **Total de caminos / escenarios de prueba:** **8 escenarios**.
- **Cobertura de flujos de negocio:** 100% de los caminos definidos en los modelos BPMN.
