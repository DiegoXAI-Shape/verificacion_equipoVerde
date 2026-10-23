# Propuestas de sitio web para el proyecto (a presentar al profesor)

Siguiendo la metodología de *de Moura et al. (2017)* (`demoura2017.pdf`): modelar en BPMN
los flujos de usuario de una aplicación web, sacar la matriz de cobertura de flujos, y
de ahí generar los escenarios de Cucumber/Selenium. Se presentan 3 opciones de sitio
por si el profesor no aprueba la primera.

---

## Opción A - Demoblaze (https://www.demoblaze.com/) — MODELADO COMPLETO

Procesos modelados (4 diagramas BPMN 2.0):
- `bpmn/opcionA_demoblaze_login.bpmn` — Gateway exclusivo, 3 ramas
- `bpmn/opcionA_demoblaze_registro.bpmn` — Gateway exclusivo, 2 ramas
- `bpmn/opcionA_demoblaze_catalogo_carrito.bpmn` — Flujo lineal, sin gateway
- `bpmn/opcionA_demoblaze_checkout.bpmn` — Gateway exclusivo, 2 ramas

**Matriz de cobertura:** `docs/matriz_cobertura_opcionA_demoblaze.md` y `docs/matriz-cobertura.xlsx` (Total: 8 escenarios de Cucumber).

* **Ventaja:** Ya hay avance real y un hallazgo interesante (Demoblaze no valida el carrito vacío antes de confirmar la compra).
* **Riesgo:** Usa `alert()` nativos de JavaScript, que en Selenium requieren manejo explícito con `WebDriverWait` + `switchTo().alert()`.

---

## Opción B - SauceDemo (https://www.saucedemo.com/) — MODELADO COMPLETO (RECOMENDADA)

Procesos modelados (3 diagramas BPMN 2.0):
- `bpmn/opcionB_saucedemo_login.bpmn` — Gateway exclusivo, 3 ramas (standard_user, locked_out_user, credenciales inválidas).
- `bpmn/opcionB_saucedemo_carrito.bpmn` — Gateway exclusivo, 2 ramas (ver producto en carrito vs quitar producto con botón Remove).
- `bpmn/opcionB_saucedemo_checkout.bpmn` — Gateway exclusivo, 3 ramas (compra exitosa, formulario vacío "First Name is required", cancelación).

**Matriz de cobertura:** `docs/matriz_cobertura_opcionB_saucedemo.md` (Total: 8 escenarios de Cucumber).

* **Ventaja:** Sitio construido específicamente para practicar automatización QA, sin alertas nativas complejas (todo son elementos HTML estándar), altamente estable para Selenium. Cuenta con usuarios de prueba con comportamientos distintos prefabricados.
* **Riesgo:** Ninguno; es el estándar de la industria para pruebas.

---

## Opción C - OpenCart demo (https://demo.opencart.com/)

Proceso modelado (1 diagrama BPMN 2.0):
- `bpmn/opcionC_opencart_registro.bpmn` — Gateway exclusivo, 3 ramas (registro exitoso, política de privacidad no aceptada, correo ya registrado).

**Matriz de cobertura:** `docs/matriz_cobertura_opcionC_opencart.md` (Total: 3 escenarios de Cucumber).

* **Ventaja:** Tienda real con validaciones de formulario más ricas.
* **Riesgo:** Es una demo pública compartida por miles de usuarios simultáneos, los datos se resetean y puede aplicar bloqueos de Cloudflare.

---

## Recomendación

Si se quiere avanzar con el menor riesgo de pruebas inestables (flaky) y máxima estabilidad técnica en Selenium, **Opción B (SauceDemo)** es la mejor alternativa. Si se desea continuar con la tienda inicial acordada en clase, **Opción A (Demoblaze)** también tiene todo el modelado listo.
