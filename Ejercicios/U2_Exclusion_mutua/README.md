# Unidad 2 — Exclusión mutua y sincronización por condición

Aparece el estado compartido. Primero ves la condición de carrera en vivo y la resolvés con
`synchronized`, eligiendo bien **qué** monitor protege cada dato. Después pasás a la sincronización
por condición con `wait()`/`notifyAll()`, que es lo que permite que un hilo espere sin gastar CPU.
La unidad cierra con el esqueleto del TP1: un pipeline de etapas concurrentes que termina limpio.

| Ejercicio                                         | Nivel | Tiempo  | Practica                                                         |
| ------------------------------------------------- | ----- | ------- | ---------------------------------------------------------------- |
| [2.1 Molinetes](E2_1_molinetes)                   | ★★    | ~30 min | Condición de carrera, `synchronized` de instancia vs de clase     |
| [2.2 Teatro](E2_2_teatro)                         | ★★    | ~45 min | Una llave por recurso, *check-then-act*, el bug del `Integer`     |
| [2.3 Buffer acotado](E2_3_buffer_acotado)         | ★★★   | ~45 min | Productor/consumidor con `wait`/`notifyAll`, `while` y no `if`    |
| [2.4 Turnos en ronda](E2_4_turnos)                | ★★★   | ~40 min | Una condición distinta por hilo: `notify` vs `notifyAll`          |
| [2.5 Cola cerrable y pipeline](E2_5_cola_cerrable) | ★★★   | ~60 min | Terminación limpia sin `stop`, `exit` ni espera activa            |

**Herramientas permitidas en toda la unidad** (las mismas de base que en el TP1): `Thread`,
`Runnable`, `synchronized` (método y bloque), `wait`, `notify`, `notifyAll` y colecciones comunes no
thread-safe. **No** se permite nada de `java.util.concurrent` (ni atómicas, ni colecciones
concurrentes, ni `BlockingQueue`), `Collections.synchronized...` ni espera activa. El verificador
revisa varias de estas prohibiciones.

**Teoría:** en los apuntes, "Sincronización con `synchronized` — fundamentos" y, en la
Introducción, "Interacción entre procesos" y "Requisitos para la exclusión mutua" (filmina 12).
