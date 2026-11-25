# Unidad 3 — Locks y semáforos

Las herramientas de `java.util.concurrent` que reemplazan a `synchronized` cuando hace falta algo que
el monitor no da: rendirse si el candado no llega a tiempo, elegir quién entra primero, dejar leer a
varios a la vez, o dejar pasar a N en vez de a uno. Los últimos dos ejercicios vuelven a problemas
conocidos (el buffer del 2.3 y la cena de los filósofos de Redes de Petri) para resolverlos con estas
herramientas y comparar.

| Ejercicio                                                           | Nivel | Tiempo  | Practica                                                                 |
| ------------------------------------------------------------------- | ----- | ------- | ------------------------------------------------------------------------ |
| [3.1 Cuenta con `ReentrantLock`](E3_1_cuenta_con_lock)              | ★★    | ~40 min | `lock`/`unlock` en `finally`, `tryLock` con tiempo, reentrancia, fairness |
| [3.2 Catálogo de precios](E3_2_catalogo_lectores_escritores)        | ★★    | ~45 min | `ReadWriteLock`: lectores a la vez, escritor solo, no "ascender" de lock  |
| [3.3 Estacionamiento](E3_3_estacionamiento)                         | ★★    | ~40 min | Semáforo de N permisos y semáforo binario; el semáforo no tiene dueño     |
| [3.4 Buffer con semáforos y largada](E3_4_buffer_con_semaforos)     | ★★★   | ~50 min | Productor/consumidor con 3 semáforos, orden de adquisición, semáforo como señal |
| [3.5 La cena de los filósofos](E3_5_filosofos)                      | ★★★★  | ~60 min | Deadlock: orden global, mayordomo y `tryLock` con retroceso                |

**Herramientas permitidas en toda la unidad:** `Thread`, `ReentrantLock`, `ReentrantReadWriteLock`,
`Semaphore` y colecciones comunes no thread-safe. Son las de `java.util.concurrent` que permitía el
TP1 (`Semaphore`, `Lock`, `ReentrantLock`) más el `ReadWriteLock`; los `Executors`, que el TP1 también
permitía, quedan para la Unidad 4. **No** se permite `synchronized` (justamente, se practica sin él), atómicas, colecciones
concurrentes ni `BlockingQueue`. Cada README dice exactamente qué se puede usar en ese ejercicio, y el
verificador lo revisa.

**Lo nuevo del verificador:** si un caso queda `[COLGADO]` porque hay hilos en espera circular sobre
locks o monitores, te dice quién espera a quién. Con semáforos no puede: un semáforo no tiene dueño,
así que no hay "a quién" esperar.

**Teoría:** en los apuntes, "Sincronización básica en Java — Locks, ReadWriteLocks y Semáforos"
(Clase 4), "`synchronized` vs `Lock`: cuándo usar cada uno", y en Redes de Petri, "Modelos básicos"
y "La cena de los filósofos".
