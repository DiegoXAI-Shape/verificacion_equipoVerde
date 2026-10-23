# Guía del proyecto para Claude

## Descripción general

Este proyecto trata sobre una materia de la universidad llamada: "Verificación y validación de Software", en el cual tenemos que hacer un proyecto que realice una bateria de pruebas automatizadas usando Selenium.

## Descripción de la página web

La página web es https://www.saucedemo.com/ (antes se evaluó https://demoblaze.com/, ver `docs/propuestas-sitios.md`). Es una página de e-commerce hecha sandbox para automatizaciones, todo lo tenemos que hacer en base a la literatura consultada en clase. Misma que viene en el archivo PDF llamado: "demoura2017.pdf", el cual es un paper sobre dicho método de automatización de baterías de pruebas.

## Stack tecnológico

- Java: Se usa para automatizar las pruebas.
- Selenium: Se usa para automatizar las pruebas.
- Cucumber: Se usa para ejecutar las pruebas.
- Python (opcional): Se puede usar para hacer pruebas rápidas, pero en sí, el profe nos revisará el código Java.

## Metodología del Paper (demoura2017.pdf)

El proyecto implementa la metodología descrita en *de Moura et al. (2017)*:
1. **Modelado de Flujos (BPMN a Escenarios):**
   - Se identifican los flujos de usuario en Demoblaze (ej. catálogo, registro, login, agregar al carrito, checkout).
   - Se representan las bifurcaciones (Gateways exclusivos: compra exitosa vs carrito vacío; login válido vs inválido).
2. **Matriz de Cobertura de Flujos:**
   - Antes de programar, se define una tabla/matriz donde las columnas son las tareas/acciones y las filas son los flujos posibles (marcando 'X' o '-').
   - Cada flujo de la matriz representa un `Scenario` de Cucumber independiente.
3. **Implementación Cucumber + Selenium en Java:**
   - **Archivos `.feature`:** Escribir los escenarios en Gherkin (`Given`, `When`, `Then`) reflejando estrictamente la secuencia de tareas del flujo.
   - **Steps (`NavegacionSteps.java`):** Métodos anotados con `@Given`, `@When`, `@Then` que ejecutan las acciones sobre el navegador usando Selenium WebDriver.
   - **Hooks (`NavegacionHooks.java`):** Configuración del `WebDriver` (`@Before` para inicializar el navegador y `@After` para cerrarlo).
   - **Buenas prácticas:** Uso de esperas explícitas (`WebDriverWait`) para manejar la asincronía de Demoblaze (ventanas modales/alertas de JavaScript).


## Particularidades de Demoblaze.com (Reglas de Automatización)

- **Alertas nativas de JavaScript:** Al hacer clic en "Add to cart", la página lanza un `alert()`. Siempre se debe usar `wait.until(ExpectedConditions.alertIsPresent())` y luego `driver.switchTo().alert().accept()`.
- **Ventanas modales (Bootstrap):** Las pantallas de Login, Registro y Compra ("Place Order") no son páginas nuevas, sino modales flotantes. Se debe esperar su visibilidad antes de interactuar con sus campos (`#logInModal`, `#orderModal`, etc.).
- **Carga asíncrona (AJAX):** Los productos del catálogo se cargan dinámicamente mediante llamadas a la API de Demoblaze. **Nunca uses `Thread.sleep()`**; utiliza siempre esperas explícitas (`WebDriverWait`).

## Estructura del proyecto (Maven)

El código debe ubicarse estrictamente en la convención estándar:
- Generador BPMN -> tabla/features/steps: `src/main/java/generator/*.java` (modelo en `src/main/resources/saucedemo.bpmn`)
- Features: `src/test/resources/features/*.feature` (`generated.feature` es generado, no se edita a mano)
- Steps Definitions y Hooks: `src/test/java/steps/*.java` (`GeneratedSteps.java` es generado)
- Runner de Cucumber: `src/test/java/runner/RunCucumberTest.java`

## Comandos de ejecución

- Generar tabla de flujos y features: `mvn compile exec:java`
- Ejecutar todas las pruebas: `mvn test` (sin ventana: `mvn test -Dheadless=true`)
- Ejecutar un escenario específico por tag: `mvn test -Dcucumber.filter.tags="@zip"`
- Reporte HTML: `target/cucumber-report.html`.


## Generación de respuesta

- Idioma: Español
- Siempre explícame todo como si fuera un niño de 5 años, para entender bien el tema. Una vez que me des una analogía, explícame poco a poco con más detalle en el proceso.
