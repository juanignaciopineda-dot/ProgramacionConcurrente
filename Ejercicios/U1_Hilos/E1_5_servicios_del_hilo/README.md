# Ejercicio 1.5 — Daemon, excepciones no controladas y `ThreadLocal`

> Unidad 1 · Hilos en Java · Nivel ★★ · ~40 min

## Objetivo

Tres herramientas que da la clase `Thread` para casos especiales: hilos de fondo que no retienen al
programa (daemon), un manejador para enterarse cuando un hilo muere por una excepción, y variables con
una copia distinta por hilo.

## Conceptos

- Hilos daemon: cuándo termina un programa Java y por qué `setDaemon` va antes de `start`.
- `Thread.UncaughtExceptionHandler`: qué pasa con una excepción que se escapa de `run()`.
- Manejador por hilo vs manejador por defecto.
- `ThreadLocal`: estado propio de cada hilo, sin compartir y sin sincronizar.

## Archivos

| Archivo                 | Qué hacer                                               |
| ----------------------- | ------------------------------------------------------- |
| `Servicios.java`        | `crearVigilante` y `crearSupervisado`                   |
| `RegistroDeFallas.java` | `uncaughtException`                                     |
| `NumeradorPorHilo.java` | Declarar el `ThreadLocal`, `siguiente` y `reiniciar`    |
| `Verificador.java`      | No se toca                                              |

## Orden sugerido

1. `Servicios.crearVigilante`.
2. `RegistroDeFallas.uncaughtException` y `Servicios.crearSupervisado`.
3. `NumeradorPorHilo` completo.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

## Restricciones

En `NumeradorPorHilo` no se permite `synchronized` ni `java.util.concurrent`: la gracia es que
`ThreadLocal` lo hace innecesario (el verificador lo controla).

## Para pensar

1. Escribí un `main` que arranque un vigilante daemon que imprima "vigilando..." cada 200 ms, y que
   el `main` termine después de un segundo. ¿Cuántas veces imprime? ¿Y si sacás el `setDaemon(true)`?
2. ¿Qué pasa si llamás a `setDaemon(true)` después de `start()`?
3. ¿Desde qué hilo se ejecuta `uncaughtException`? ¿Por qué entonces `registrar` necesita
   `synchronized`?
4. Un hilo muere por una excepción no capturada. ¿Se liberan los candados (`synchronized`) que tenía
   tomados? ¿Y un `Lock` de la Unidad 3 tomado sin `try/finally`?
5. `NumeradorPorHilo` no tiene ningún `synchronized` y es thread-safe. ¿Contradice la regla de "todo
   estado compartido se protege"? ¿Qué es compartido y qué no?

## Pistas

> [!tip]- Pista 1: declarar el `ThreadLocal`
> ```java
> private final ThreadLocal<Integer> ultimo = ThreadLocal.withInitial(() -> 0);
> ```
> El objeto `ultimo` es uno solo y lo comparten todos, pero `get()` y `set()` trabajan sobre la copia
> del hilo que llama.

> [!tip]- Pista 2: respuesta a la pregunta 4
> Sí: al salir de un `synchronized` por una excepción, la JVM libera el monitor automáticamente. Un
> `Lock` no: si el `unlock()` no está en un `finally`, queda tomado para siempre y todos los que lo
> pidan se cuelgan.

## Conexión con la teoría y los TPs

- Apuntes: "Excepciones no controladas, hilos daemon, variables locales de hilo y CountDownLatch".
- TP1: el enunciado exige que "todos los hilos creados por la simulación finalizaron" antes de
  devolver. Un hilo daemon no cumpliría eso: la JVM lo corta, no termina. Y un worker que muere por
  una excepción no controlada deja órdenes a medio camino: un manejador sirve para detectarlo.
