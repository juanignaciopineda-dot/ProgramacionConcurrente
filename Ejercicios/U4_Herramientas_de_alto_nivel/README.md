# Unidad 4 — Herramientas de alto nivel

Hasta acá cada ejercicio creaba sus hilos a mano y los coordinaba con `join`, monitores, locks o
semáforos. Esta unidad presenta las herramientas de `java.util.concurrent` que sacan ese trabajo del
código de la aplicación:
- **Sincronizadores** que resuelven un patrón de espera entero: `CountDownLatch` y `Phaser`.
- **Fábricas de hilos.**
- **Pools** que crean, reutilizan y cierran los hilos por vos: `Executors` y `ThreadPoolExecutor`.
- **Tareas que devuelven un resultado:** `Callable` y `Future`.

| Ejercicio                                                    | Nivel | Tiempo  | Practica                                                                    |
| ------------------------------------------------------------ | ----- | ------- | --------------------------------------------------------------------------- |
| [4.1 Arranque de un servidor](E4_1_arranque_con_latch)       | ★★    | ~35 min | `CountDownLatch`: `countDown()` en el `finally`, `await` con plazo, hilo daemon |
| [4.2 Fábrica de hilos](E4_2_fabrica_de_hilos)                | ★★    | ~35 min | `ThreadFactory`: nombres, daemon, estadísticas, límite, y su uso en un pool  |
| [4.3 Servidor con pool fijo](E4_3_servidor_con_pool)         | ★★★   | ~50 min | `ThreadPoolExecutor`, tareas rechazadas, `shutdown` vs `shutdownNow`, `awaitTermination` |
| [4.4 Callable y Future](E4_4_callable_y_future)              | ★★★   | ~45 min | `submit`/`get`, `invokeAll`, `ExecutionException`, `get` con plazo y `cancel` |
| [4.5 Carrera por etapas](E4_5_carrera_con_phaser)            | ★★★   | ~45 min | `Phaser`: barrera que se repite, `onAdvance`, `arriveAndDeregister`          |

**Herramientas permitidas en toda la unidad:** lo de las unidades anteriores más `CountDownLatch`,
`ThreadFactory`, `Executors`, `ExecutorService`, `ThreadPoolExecutor`, `Callable`, `Future` y `Phaser`.
**No** se permiten `CompletableFuture`, `parallelStream`, `ForkJoinPool` ni colecciones concurrentes.
Cada README dice qué se puede usar en ese ejercicio, y el verificador lo revisa.

**Teoría:** en los apuntes, "Esperar múltiples eventos concurrentes: `CountDownLatch`" y "Thread
Factory y Executors" (el patrón Factory, `ThreadFactory` y el servidor con `ThreadPoolExecutor`).
`Callable`/`Future` y `Phaser` no están en las filminas. El primero es la otra mitad de los
`Executors`; el segundo salió en el segundo parcial de 2023 ("¿Con qué valor debe ser inicializada
la primitiva PHASER?").
