# Verificación y Validación de Software: pruebas generadas desde BPMN (SauceDemo)

Proyecto (PIA) que replica el artículo **de Moura, Charão, Lima y Stein (2017), *"Test Case
Generation from BPMN Models for Automated Testing of Web-Based BPM Applications"*** (IEEE),
aplicado a la tienda de práctica <https://www.saucedemo.com>.

**En una frase:** dibujamos el proceso de compra en BPMN, un programa en Java encuentra **todos**
los caminos posibles del diagrama, convierte cada camino en un escenario de prueba, y Selenium +
Cucumber ejecutan esas pruebas en Chrome.

---

## Cómo funciona

```
 saucedemo.bpmn ──► [1] Generador (Java) ──► flow_table.csv        (tabla de caminos)
  (el proceso)                            ├─► generated.feature     (escenarios vacíos)
                                          └─► GeneratedSteps.java   (métodos vacíos)
                                                     │
                                       se completan a mano con Selenium
                                                     ▼
                     checkout.feature + CheckoutSteps.java ──► [2] mvn test ──► cucumber-report.html
```

### 1. El modelo BPMN: `src/main/resources/saucedemo.bpmn`

Es el "mapa" del proceso de compra: login → agregar al carrito → checkout → confirmar. Se puede
abrir en <https://demo.bpmn.io> (basta con arrastrar el archivo).

- **userTask / manualTask**: lo que hace o revisa el usuario. Son las que nos interesan para
  probar.
- **serviceTask** (p. ej. "Validar credenciales"): lo que hace el sistema. Se ignoran, igual que
  en el paper.
- **Gateways** (rombos): decisiones. "¿Credenciales válidas?", "¿Datos válidos?" y
  "¿Corregir datos?".
- **Un ciclo**: si el formulario tiene error, el usuario puede corregirlo y regresar a llenarlo.

### 2. El generador: `src/main/java/generator/`

| Clase | Qué hace |
|---|---|
| `BpmnParser` | Lee el XML con `javax.xml` (DOM). Quita el prefijo de cada etiqueta (`bpmn:userTask` → `userTask`, `semantic:task` → `task`), porque cada herramienta exporta con uno distinto. |
| `BpmnProcess` | Guarda en memoria los nodos (tareas, eventos, gateways) y las flechas (`sequenceFlow` con `sourceRef` → `targetRef`). |
| `PathFinder` | Recorre las flechas desde el `startEvent` con una **búsqueda en profundidad (DFS)** y guarda cada camino que llega a un `endEvent`. Para no quedar atrapado en el ciclo, aplica el **delimitador de recursión**: una misma flecha se recorre como máximo 2 veces por camino. |
| `FlowTableWriter` | Arma la **tabla de flujos**: columnas = tareas de usuario, filas = caminos, `X` si el camino pasa por la tarea y `-` si no. |
| `TestCodeGenerator` | Escribe un `Scenario` de Gherkin por camino (`generated.feature`) y una clase con métodos vacíos `@Given/@When/@Then` (`GeneratedSteps.java`), igual que las figuras 4 y 5 del paper. |
| `Main` | Une todo: lee el BPMN, busca los caminos, imprime la tabla y escribe los 3 archivos. |

### 3. Las pruebas reales: `src/test/`

El código generado es solo el esqueleto. Para probar SauceDemo de verdad, los escenarios se
escribieron en `checkout.feature` y sus pasos se implementaron con Selenium:

| Archivo | Qué hace |
|---|---|
| `features/checkout.feature` | Escenarios en Gherkin: login válido + compra, usuario bloqueado, First Name vacío y un *Scenario Outline* con 7 códigos postales. |
| `steps/CheckoutSteps.java` | Cada frase del `.feature` se vuelve una acción en Chrome. Localiza los elementos por `id` (`user-name`, `password`, `login-button`, `postal-code`, `continue`, `finish`…) y usa esperas explícitas (`WebDriverWait`). |
| `steps/Hooks.java` | Abre Chrome antes de cada escenario y lo cierra al final. Si el escenario falla, guarda una captura en el reporte. |
| `runner/RunCucumberTest.java` | Runner de JUnit Platform: ejecuta Cucumber y genera `target/cucumber-report.html`. |

---

## Cómo ejecutarlo

### Requisitos

| Herramienta | Versión probada | Instalación en Windows |
|---|---|---|
| Java JDK | 17 (Eclipse Temurin 17.0.20) | `winget install EclipseAdoptium.Temurin.17.JDK` |
| Maven | 3.9.16 | Descargar el zip de <https://maven.apache.org/download.cgi>, descomprimir y agregar su carpeta `bin` al `PATH` |
| Google Chrome | cualquiera reciente | El chromedriver lo descarga solo Selenium Manager |
| Internet | — | Para SauceDemo y para que Maven descargue las librerías |

Para comprobar la instalación, abre una terminal nueva y ejecuta:

```bash
java -version
mvn -version
```

### Pasos

```bash
git clone https://github.com/DiegoXAI-Shape/verificacion_equipoVerde.git
cd verificacion_equipoVerde

# 1) Generar la tabla de flujos, el .feature y los steps a partir del BPMN
mvn compile exec:java

# 2) Correr las pruebas con Chrome visible
mvn test

# 3) Abrir el reporte (Windows)
start target/cucumber-report.html
```

La primera ejecución tarda unos minutos porque Maven descarga las dependencias.

### Variantes útiles

```bash
mvn test -Dheadless=true                          # sin abrir ventana de Chrome
mvn test "-Dcucumber.filter.tags=@login"          # solo escenarios de login
mvn test "-Dcucumber.filter.tags=@zip"            # solo los del código postal
mvn test "-Dcucumber.filter.tags=@generated"      # los escenarios generados (steps vacíos)
mvn test "-Dmaven.test.failure.ignore=true"       # no marcar BUILD FAILURE por las pruebas fallidas
mvn compile exec:java "-Dexec.args=otra/ruta/modelo.bpmn"   # analizar otro BPMN
```

En PowerShell deja las comillas alrededor de cada `-D...`. Si la consola muestra caracteres raros
(`â??`), ejecuta antes `[Console]::OutputEncoding = [System.Text.Encoding]::UTF8`.

### Archivos que se generan

| Archivo | Generado por |
|---|---|
| `output/flow_table.csv` | `mvn compile exec:java` |
| `src/test/resources/features/generated.feature` | `mvn compile exec:java` |
| `src/test/java/steps/GeneratedSteps.java` | `mvn compile exec:java` |
| `target/cucumber-report.html` | `mvn test` |

---

## Resultados

### Tabla de flujos (`output/flow_table.csv`)

| Flujo | Ingresar usuario y contrasena | Verificar mensaje de usuario bloqueado | Agregar producto al carrito | Abrir el carrito | Iniciar checkout | Capturar datos de envio | Confirmar la orden | Verificar mensaje de agradecimiento | Verificar mensaje de error del formulario | Evento final |
|---|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|---|
| F1 | X | - | X | X | X | X | X | X | - | Orden completada |
| F2 | X | - | X | X | X | X | X | X | X | Orden completada |
| F3 | X | - | X | X | X | X | - | - | X | Checkout abandonado |
| F4 | X | - | X | X | X | X | - | - | X | Checkout abandonado |
| F5 | X | X | - | - | - | - | - | - | - | Acceso denegado |

- **F2**: el formulario falla una vez, el usuario corrige y termina la compra (pasa por el ciclo).
- **F3 / F4** tienen las mismas `X`, porque la tabla marca *si* aparece la tarea y no *cuántas
  veces*. F3 se rinde al primer error. F4 corrige, vuelve a fallar y se rinde.
- "Ingresar usuario y contrasena" aparece en el 100% de los flujos: es la tarea más crítica que
  hay que probar.

### Pruebas (`mvn test`)

**10 escenarios: 5 pasan y 5 fallan.**

| Escenario | Esperado | Resultado |
|---|---|---|
| Login `standard_user` + checkout completo | "Thank you for your order!" | ✅ Pasa |
| Login `locked_out_user` | "Epic sadface: Sorry, this user has been locked out." | ✅ Pasa |
| Checkout con First Name vacío | "Error: First Name is required" | ✅ Pasa |
| Zip `64000` (válido) | Avanza al resumen | ✅ Pasa |
| Zip vacío | Error | ✅ Pasa |
| Zip `A1027` (letras) | Error | ❌ SauceDemo lo acepta |
| Zip `6400` (4 dígitos) | Error | ❌ SauceDemo lo acepta |
| Zip `640000` (6 dígitos) | Error | ❌ SauceDemo lo acepta |
| Zip `"   "` (solo espacios) | Error | ❌ SauceDemo lo acepta |
| Zip `@#$%!` (símbolos) | Error | ❌ SauceDemo lo acepta |

**Defecto encontrado:** SauceDemo solo revisa que el código postal no esté vacío. No valida el
formato, y acepta incluso un campo con puros espacios. Las 5 pruebas que fallan están bien:
detectaron un defecto real del sitio. Ejemplo de mensaje:

```
DEFECTO: SauceDemo acepto el Zip invalido 'A1027' y avanzo a Checkout: Overview
```

Las ejecuciones también muestran `Skipped: 5`: son los escenarios de `generated.feature`, que se
excluyen por defecto porque sus métodos están vacíos (están en `junit-platform.properties`).

---

## Paper vs. este proyecto

| Aspecto | de Moura et al. (2017) | Este proyecto |
|---|---|---|
| Aplicación bajo prueba | Aplicaciones web hechas con BPMS (Bonita, Activiti) | SauceDemo (e-commerce web) |
| Modelos | Procesos de ejemplo de la OMG (Hardware Retailer, Travel Booking…) | `saucedemo.bpmn` (login + carrito + checkout) |
| Lectura del BPMN | API XML de Java | `javax.xml` DOM (`BpmnParser`) |
| Prefijos de etiquetas (`semantic:task`) | Se eliminan antes de analizar | `BpmnParser.localName()` quita cualquier prefijo |
| Recorrido | `sourceRef` / `targetRef` de los sequenceFlow | DFS sobre los sequenceFlow (`PathFinder`) |
| Ciclos | "Recursion delimiter" | Máx. 2 recorridos por sequenceFlow en cada camino |
| Tareas consideradas | userTask, manualTask… | userTask y manualTask (serviceTask se ignora) |
| Tabla de flujos | Excel, X / - (Fig. 3, Tabla II) | `output/flow_table.csv`, X / - |
| Selección de proceso | El usuario elige el ID del proceso | Se toma el primer `<process>` del archivo |
| Escenarios | Un Scenario por flujo (Fig. 4) | `generated.feature`, un Scenario por camino |
| Steps | `StepsDefinition` con métodos vacíos (Fig. 5) | `GeneratedSteps.java` con métodos vacíos `method0..N` |
| Completar los steps | Con código grabado en Selenium IDE | A mano, en `CheckoutSteps.java` (Selenium WebDriver 4) |
| Herramientas | Selenium + Cucumber-JVM (API `cucumber.api`, 2017) | Selenium 4.51 + Cucumber-JVM 8.0.4 + JUnit 6.1 |
| Datos de prueba | No se abordan | Scenario Outline con partición de equivalencia y valores límite del Zip |
| Integración continua | No se aborda | `Jenkinsfile` |
| Resultado | Tablas verificadas a mano contra los diagramas | 5 caminos, 10 escenarios ejecutados y 1 defecto real encontrado |

---

## Estructura del repositorio

```
src/main/resources/saucedemo.bpmn          Modelo BPMN 2.0 del proceso
src/main/java/generator/                   Generador (BPMN -> tabla, .feature y steps)
src/test/resources/features/               checkout.feature (real) y generated.feature (generado)
src/test/java/steps/                       CheckoutSteps, Hooks y GeneratedSteps (generado)
src/test/java/runner/RunCucumberTest.java  Runner de Cucumber + JUnit
output/flow_table.csv                      Tabla de flujos (generada)
bpmn/                                      Modelos BPMN de las propuestas iniciales (Demoblaze, SauceDemo, OpenCart)
docs/                                      Matrices de cobertura y propuestas de las primeras entregas
Jenkinsfile                                Pipeline de integración continua
```

## Jenkins

El `Jenkinsfile` declarativo hace: checkout → `mvn compile exec:java` → `mvn test -Dheadless=true`
→ publica `target/cucumber-report.html` y `output/flow_table.csv` como artefactos, junto con los
resultados de JUnit.

Usa `-Dmaven.test.failure.ignore=true`. Así las pruebas del defecto del Zip dejan el build en
**UNSTABLE** (amarillo) en lugar de detenerlo antes de publicar los reportes. El agente necesita
`java` y `mvn` en el `PATH`, o descomentar el bloque `tools` con los nombres configurados en
Jenkins. Funciona en agentes Linux y Windows.
