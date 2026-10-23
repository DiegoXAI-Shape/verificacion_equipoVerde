# Guión para presentar el avance al profesor

Duración estimada: 5-7 minutos. Todo lo que se menciona ya existe como archivo en el
proyecto, solo hay que abrirlo en el orden indicado.

---

## 1. Contexto (30 seg)

"Profe, para el proyecto de Verificación y Validación estamos siguiendo la metodología
del paper que nos dio: `demoura2017.pdf` (de Moura et al., 2017). La idea del paper es:
primero modelar los flujos de la aplicación en BPMN, sacar de ahí una tabla con todos
los caminos posibles, y de esa tabla generar las pruebas automatizadas con Selenium y
Cucumber."

---

## 2. Explicar la lógica con UN diagrama (2 min)

Abrir `bpmn/opcionA_demoblaze_login.bpmn` (o `bpmn/opcionB_saucedemo_login.bpmn`) en https://demo.bpmn.io/ (arrastrar el archivo a la página).

"Aquí está el flujo de Login. Estas cajas son las tareas que hace el
usuario, y este rombo es un Gateway exclusivo: representa una decisión, en este caso
'¿credenciales válidas?'. De ahí salen 3 caminos distintos: login exitoso, usuario no
existe (o bloqueado), o password incorrecta. Cada uno de esos 3 caminos se convierte después en
una prueba automatizada distinta."

Abrir `docs/matriz_cobertura_opcionA_demoblaze.md` (o `docs/matriz-cobertura.xlsx`, pestaña "Login").

"Y esta es la tabla que sale de ese diagrama: cada fila es uno de esos 3 caminos, y
cada columna es una tarea. La 'X' marca si esa tarea se ejecuta en ese camino. Esto es
exactamente lo que hace el paper en su Tabla II."

---

## 3. Mostrar que ya hay avance completo en Demoblaze y SauceDemo (2 min)

"Con esa misma lógica ya modelamos los procesos principales organizados con nomenclatura clara:
- **Opción A (Demoblaze):** 4 diagramas (`opcionA_demoblaze_login.bpmn`, `opcionA_demoblaze_registro.bpmn`, `opcionA_demoblaze_catalogo_carrito.bpmn`, `opcionA_demoblaze_checkout.bpmn`).
- **Opción B (SauceDemo):** 3 diagramas (`opcionB_saucedemo_login.bpmn`, `opcionB_saucedemo_carrito.bpmn`, `opcionB_saucedemo_checkout.bpmn`).
- **Opción C (OpenCart):** 1 diagrama de registro (`opcionC_opencart_registro.bpmn`)."

---

## 4. Cerrar pidiendo decisión (30 seg)

"Nuestra recomendación para evitar inestabilidad en Selenium es SauceDemo (Opción B), pero si prefiere que sigamos con Demoblaze (Opción A), también está 100% modelada con sus matrices. Con su luz verde, procedemos con los archivos `.feature` de Cucumber y los `steps` en Java con Selenium."

---

## Preguntas frecuentes del profesor (y cómo responderlas)

- **"¿Por qué varios diagramas y no uno solo?"** → El paper mismo trata cada proceso como un diagrama separado (ver Tabla I del paper, 8 procesos distintos); meter todo junto en un solo diagrama sería ilegible y difícil de mantener.
- **"¿Por qué la tabla con X y -?"** → Es la técnica exacta de Cobertura de Caminos (Path Coverage) del paper *de Moura et al. (2017)*, donde cada fila de la matriz se mapea 1 a 1 con un `Scenario` de Cucumber.
- **"¿Qué pasa con Thread.sleep()?"** → No se usa; se implementan esperas explícitas (`WebDriverWait`) según las mejores prácticas de Selenium.
