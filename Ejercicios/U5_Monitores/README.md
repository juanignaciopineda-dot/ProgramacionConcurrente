# Unidad 5 — Monitores

Un monitor junta en un solo objeto el estado de un recurso y las operaciones sobre él. Todas las
operaciones corren en exclusión mutua, y cada una tiene su sincronización por condición: bloquear al
que no puede seguir y avisarle cuando ya puede. La unidad va de lo que da Java hecho (un `Lock` con
varias `Condition`) a construir un monitor a mano con semáforos, con las distintas políticas de
señalización. Termina con la estructura del monitor del TP final: mutex, colas de condición y una
política aparte que resuelve los conflictos.

| Ejercicio                                                     | Nivel | Tiempo  | Practica                                                                       |
| ------------------------------------------------------------- | ----- | ------- | ------------------------------------------------------------------------------ |
| [5.1 Buffer con dos condiciones](E5_1_buffer_con_condiciones) | ★★    | ~30 min | `Lock` + `Condition`: una cola por condición, `signal` alcanza, `while` sigue haciendo falta (SC) |
| [5.2 Puente de una mano](E5_2_puente_de_una_mano)             | ★★★   | ~50 min | Diseñar el estado y las condiciones de un monitor; postergación indefinida     |
| [5.3 Monitor con signal and exit](E5_3_monitor_signal_and_exit) | ★★★ | ~45 min | Colas de condición con semáforos, el mutex que pasa de mano, acciones afuera del monitor |
| [5.4 Monitor de Hoare](E5_4_monitor_de_hoare)                 | ★★★★  | ~60 min | *Signal and urgent wait* con cola de cortesía; con eso alcanza un `if`         |
| [5.5 Ventanilla con política](E5_5_ventanilla_con_politica)   | ★★★★  | ~50 min | Una cola por tipo y una política que elige a quién despertar (prioridad, turnos) |

**Herramientas permitidas en toda la unidad:** `ReentrantLock` y `Condition` (5.1 y 5.2), y
`Semaphore` para construir los monitores a mano (5.3 a 5.5). No se usa `synchronized` ni
`wait`/`notify`. Ya se vieron en la Unidad 2: acá se trata de ver qué hay adentro. Cada README dice
qué se puede usar en ese ejercicio, y el verificador lo revisa.

**Las políticas, en una tabla** (de los apuntes):

| Política | El que señaliza | El despertado | ¿`if` o `while`? |
| --- | --- | --- | --- |
| Signal and Continue (SC, la de Java) | Sigue con el monitor | Compite por el mutex | `while` |
| Signal and Exit (SX, la del TP final) | Sale y le pasa el mutex | Sigue enseguida | `if` |
| Signal and Urgent Wait (SU, Hoare) | Espera en la cola de cortesía | Sigue enseguida | `if` |

**Teoría:** en los apuntes, el capítulo "Monitores" completo: por qué no alcanza con semáforos, la
definición y la cola de entrada, las variables de condición, la clasificación y las políticas de
señalización, y "Un monitor guiado por una red de Petri". En "Preguntas de parciales", la sección "Monitores" tiene
las preguntas que los ejercicios ayudan a responder.
