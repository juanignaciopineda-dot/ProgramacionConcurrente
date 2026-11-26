# Unidad 6 — Lenguajes y autómatas

Una gramática **genera** un lenguaje y un autómata lo **reconoce**, y cada tipo de gramática tiene su
tipo de autómata. La unidad recorre el capítulo en código, de las definiciones a la herramienta:
- **Lenguajes y gramáticas:** las cadenas y las operaciones sobre lenguajes, y las gramáticas con la
  jerarquía de Chomsky.
- **Los autómatas, de menos a más memoria:**
  - El finito determinista, que solo tiene su estado.
  - Moore y Mealy, que además emiten una salida.
  - El de pila, que tiene una pila para contar.
  - El no determinista, que está en varios estados a la vez.
- **Las expresiones regulares en Java,** que se usan en el 7.6 para verificar el log de una red de
  Petri.

En el medio aparece la concurrencia:
- **Un sistema reactivo:** una máquina de Mealy corriendo en su hilo y reaccionando a los eventos de
  una cola.
- **El lenguaje de los `lock()`/`unlock()` de un hilo,** que no es regular.
- **El no determinismo del AFND,** que es el mismo del planificador.

| Ejercicio                                                                     | Nivel | Tiempo  | Practica                                                                       |
| ----------------------------------------------------------------------------- | ----- | ------- | ------------------------------------------------------------------------------ |
| [6.1 Cadenas y lenguajes](E6_1_cadenas_y_lenguajes)                           | ★     | ~25 min | `V*`, concatenación, potencia, cierre; `∅` contra `{λ}`                         |
| [6.2 Gramáticas y la jerarquía de Chomsky](E6_2_gramaticas_y_chomsky)         | ★★    | ~40 min | Clasificar por la forma de las reglas, derivar y generar el lenguaje; `aⁿbⁿcⁿ` |
| [6.3 Autómata finito determinista](E6_3_automata_finito)                      | ★★    | ~35 min | Recorrido, aceptación, accesibles; diseñar autómatas pensando qué resume el estado |
| [6.4 Moore, Mealy y un sistema reactivo](E6_4_moore_y_mealy)                  | ★★★   | ~45 min | El detector de `101` con las dos máquinas, de Mealy a Moore, y un hilo que reacciona a eventos |
| [6.5 Autómata de pila](E6_5_automata_de_pila)                                 | ★★★   | ~40 min | Determinismo, traza de configuraciones, `aⁿbⁿ` y las trazas de un `ReentrantLock` |
| [6.6 AFND, subconjuntos y gramáticas regulares](E6_6_afnd_y_subconjuntos)     | ★★★★  | ~60 min | Clausura-λ, construcción de subconjuntos, la explosión a `2ⁿ`, gramática ↔ autómata |
| [6.7 Expresiones regulares en Java](E6_7_expresiones_regulares_en_java)       | ★★★   | ~45 min | De JFLAP a `Pattern`, `matches()` contra `find()`, codicioso contra perezoso    |

Hacelos en orden. El 6.6 usa ya resueltos el `AFD` del 6.3 y la `Gramatica` del 6.2.

**Cómo verifica esta unidad:** casi todo es de un solo hilo, así que en vez de buscar carreras, el
verificador prueba los autómatas y las expresiones con **todas** las cadenas hasta cierto largo y las
compara con la definición del lenguaje. Si algo falla, muestra la primera cadena que lo rompe. Pasar
todas las cadenas hasta largo 10 no demuestra que dos lenguajes sean iguales, y es una de las cosas
para pensar.

**Herramientas permitidas:** Java común (colecciones, `record`, `java.util.regex`). En el
`DetectorReactivo` del 6.4, los métodos de `BlockingQueue`, sin `sleep` ni `synchronized`.

**Notación:** λ es `""` en las cadenas y `λ` en las producciones y las transiciones. Si la consola no
puede mostrar λ, ∅, → o los superíndices, el verificador los escribe en ASCII (`lambda`, `{}`, `->`,
`^n`).

**Teoría:** en los apuntes, el capítulo "Gramáticas, Lenguajes y Autómatas" completo, con sus
actividades y sus "Ejercicios Propuestos" (los ejercicios de esta unidad resuelven en código el 1, 2, 3,
4, 5, 6 y 9); en el práctico, "Expresiones regulares en la práctica: verificar T-invariantes". En
"Preguntas de parciales", las secciones "Lenguajes y gramáticas" y "Autómatas y máquinas".
