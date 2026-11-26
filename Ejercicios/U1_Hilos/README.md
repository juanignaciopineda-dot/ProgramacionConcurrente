# Unidad 1 — Hilos en Java

Todo lo que se puede hacer con un hilo **antes** de que aparezca el estado compartido: crearlo,
lanzarlo, esperarlo, observarlo, pedirle que termine y darle servicios propios. En esta unidad
ningún dato es escrito por dos hilos a la vez, así que todavía no hace falta `synchronized`
(salvo como obstáculo en el 1.3). Al final del 1.2 vas a ver por qué la Unidad 2 es necesaria.

| Ejercicio                                             | Nivel | Tiempo  | Practica                                                    |
| ----------------------------------------------------- | ----- | ------- | ----------------------------------------------------------- |
| [1.1 Dos formas de crear un hilo](E1_1_crear_hilos)    | ★     | ~30 min | `extends Thread`, `Runnable`, lambda, `start` vs `run`, `join` |
| [1.2 Suma paralela](E1_2_suma_paralela)                | ★     | ~40 min | Repartir trabajo entre `k` hilos y combinar con `join`      |
| [1.3 Estados y atributos](E1_3_estados)                | ★★    | ~45 min | `Thread.State` completo, nombre, id, prioridad, daemon      |
| [1.4 Interrupción](E1_4_interrupcion)                  | ★★    | ~45 min | `interrupt`, `InterruptedException`, `isInterrupted`, `join(ms)` |
| [1.5 Servicios del hilo](E1_5_servicios_del_hilo)      | ★★    | ~40 min | Daemon, `UncaughtExceptionHandler`, `ThreadLocal`           |

**Herramientas permitidas en toda la unidad:** `Thread`, `Runnable`, lambdas y colecciones comunes.
Nada de `java.util.concurrent`.

**Teoría:** en el apunte práctico, los capítulos "Primer hilo: `start()` vs `run()`", "Threads en Java — atributos,
estados y prioridades", "Interrupción, sleep y join de hilos" y "Excepciones no controladas, hilos
daemon, variables locales de hilo y CountDownLatch" (filminas 3, 10 y 15).
