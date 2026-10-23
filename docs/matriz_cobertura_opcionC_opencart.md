# Matriz de Cobertura de Flujos - OpenCart Demo (Metodología de Moura et al., 2017)

Siguiendo la metodología del paper `demoura2017.pdf`:
- Cada columna representa una tarea/acción del diagrama BPMN.
- Cada fila representa un flujo de ejecución completo (de inicio a fin).
- "X" = La tarea se ejecuta en ese flujo.
- "-" = La tarea no se ejecuta.
- **Cada fila equivale a un `Scenario` en Gherkin (Cucumber).**

---

## 1. Proceso de Registro de Cuenta (`bpmn/opcionC_opencart_registro.bpmn`)
**Bifurcación:** Gateway Exclusivo (3 ramas según validación de datos y políticas)

| Flujo / Tarea | Abrir formulario Register | Llenar campos de registro | Aceptar checkbox Privacy Policy | Clic en botón Continue | Verificar "Your Account Has Been Created!" | Verificar error "Privacy Policy" | Verificar error "E-Mail Address is already registered" |
| :--- | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **F1: Registro exitoso** | X | X | X | X | X | - | - |
| **F2: Política de privacidad no aceptada** | X | X | - | X | - | X | - |
| **F3: Correo electrónico duplicado** | X | X | X | X | - | - | X |

**Escenarios Cucumber correspondientes:**
1. `Scenario: Registro exitoso con correo nuevo y aceptando políticas`
2. `Scenario: Registro fallido por no aceptar la política de privacidad`
3. `Scenario: Registro fallido por intentar registrar un correo ya existente`

---

## Recomendación técnica sobre OpenCart vs SauceDemo
- **OpenCart (`demo.opencart.com`):** Es una demo pública real y compartida. Presenta riesgos de estabilidad para Selenium porque otros usuarios pueden modificar datos al mismo tiempo y a veces activa protecciones anti-bot (Cloudflare).
- **SauceDemo (`saucedemo.com`):** Es el entorno ideal para automatización de pruebas, ya que es 100% determinista, no usa alertas nativas complejas y sus usuarios (`standard_user`, `locked_out_user`) están diseñados específicamente para evaluar gateways y flujos alternativos.
