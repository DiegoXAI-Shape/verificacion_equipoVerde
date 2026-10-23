# Matrices de cobertura de flujos - Demoblaze (Metodología de Moura et al., 2017)

Siguiendo la Sección III.A del paper `demoura2017.pdf`: cada columna es una tarea del
diagrama BPMN, cada fila es un flujo/camino posible. "X" = la tarea se ejecuta en ese
flujo, "-" = no se ejecuta. Cada fila de cada tabla equivale a un `Scenario` en el
archivo `.feature` de Cucumber.

---

## 1. Login (`bpmn/opcionA_demoblaze_login.bpmn`) - Gateway Exclusivo (3 ramas)

| Abrir modal Login | Ingresar user/pass | Clic Log in | Verificar sesion iniciada | Aceptar alerta "User does not exist." | Aceptar alerta "Wrong password." |
|---|---|---|---|---|---|
| X | X | X | X | - | - |
| X | X | X | - | X | - |
| X | X | X | - | - | X |

Escenarios: `Login - credenciales validas`, `Login - usuario no existe`, `Login - password incorrecta`.

---

## 2. Registro / Sign up (`bpmn/opcionA_demoblaze_registro.bpmn`) - Gateway Exclusivo (2 ramas)

| Abrir modal Sign up | Ingresar user/pass | Clic Sign up | Aceptar alerta "Sign up successful." | Aceptar alerta "This user already exist." |
|---|---|---|---|---|
| X | X | X | X | - |
| X | X | X | - | X |

Escenarios: `Registro - usuario nuevo`, `Registro - usuario ya existe`.

---

## 3. Catálogo a Carrito (`bpmn/opcionA_demoblaze_catalogo_carrito.bpmn`) - Sin gateway (flujo lineal)

| Seleccionar categoria | Seleccionar producto | Clic Add to cart | Aceptar alerta "Product added." |
|---|---|---|---|
| X | X | X | X |

Escenario: `Catalogo - agregar producto al carrito`.

---

## 4. Checkout / Place Order (`bpmn/opcionA_demoblaze_checkout.bpmn`) - Gateway Exclusivo (2 ramas)

| Ir a Carrito | Clic Place Order | Llenar formulario | Clic Purchase | Verificar "Thank you..." | Verificar confirmacion indebida |
|---|---|---|---|---|---|
| X | X | X | X | X | - |
| X | X | - | X | - | X |

Escenarios: `Checkout - compra exitosa`, `Checkout - carrito vacio (hallazgo de falta de validacion)`.

---

## Resumen
- Total de escenarios de Cucumber a implementar: **8** (3 + 2 + 1 + 2).
- El escenario "Checkout - carrito vacio" documenta un hallazgo real de Demoblaze (no valida el carrito ni el formulario antes de confirmar la compra).
