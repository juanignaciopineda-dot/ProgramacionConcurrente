# Ejercicio 4.3 — Servidor con un pool fijo de hilos

> Unidad 4 · Executors · Nivel ★★★ · ~50 min

## Objetivo

El servidor de los apuntes: recibe pedidos y se los pasa a un pool fijo de hilos, sin crear un solo
hilo a mano. Vas a ver tres cosas:
- Que el pool reutiliza siempre los mismos hilos.
- Qué pasa con un pedido que llega con el pool cerrado.
- La diferencia entre las dos formas de cerrar un pool y cómo esperar a que termine.

```
  atender(p) ──execute──► [ cola del pool ] ──► pool-1-thread-1 ─┐
                                              ──► pool-1-thread-2 ─┼─► pedidos atendidos
                                              ──► pool-1-thread-3 ─┘
  (pool cerrado) ──execute──► RegistroDeRechazos.rejectedExecution() → queda anotado
```

## Conceptos

- `Executors.newFixedThreadPool(n)` y el cast a `ThreadPoolExecutor` para las estadísticas.
- `execute()`: el pedido corre en un hilo libre o espera en la cola.
- `RejectedExecutionHandler`: qué hacer con lo que el pool no acepta.
- `shutdown()` contra `shutdownNow()`, y por qué ninguno de los dos espera.
- `awaitTermination()` para esperar con plazo.
- Una tarea tiene que responder a la interrupción, o `shutdownNow()` no la frena.

## Archivos

| Archivo                   | Qué hacer                                                                       |
| ------------------------- | ------------------------------------------------------------------------------- |
| `Pedido.java`             | `run()`                                                                         |
| `RegistroDeRechazos.java` | `rejectedExecution`                                                             |
| `Servidor.java`           | Constructor, `atender`, `cerrar`, `cerrarYa`, `esperarCierre` y las 3 estadísticas |
| `Verificador.java`        | No se toca                                                                      |

## Orden sugerido

1. `Pedido.run`: los dos casos de `Pedido` del verificador no necesitan el servidor.
2. `RegistroDeRechazos.rejectedExecution`.
3. `Servidor`: constructor, `atender`, `cerrar`, `esperarCierre` y las estadísticas.
4. `Servidor.cerrarYa`.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

El caso del pool fijo mide tiempos: 12 pedidos de 100 ms con 3 hilos son 4 tandas, unos 400 ms. Con
un pool que crea un hilo por pedido tardaría 100 ms y usaría 12 hilos.

## Restricciones

`Executors` y `ThreadPoolExecutor`. Nada de crear hilos a mano (`new Thread`, `start()`) ni de
`newCachedThreadPool` (el verificador lo controla).

## Para pensar

1. Cambiá `shutdown()` por `shutdownNow()` en `cerrar()`. ¿Qué caso falla? ¿Qué les pasa a los
   pedidos que esperaban en la cola?
2. En `Pedido.run`, sacá el `Thread.currentThread().interrupt()` del `catch`. El pedido termina igual,
   pero un caso falla: ¿por qué le importa al pool esa marca, si la tarea ya terminó?
3. Sin el `RegistroDeRechazos`, ¿qué excepción recibe quien llama a `atender()` con el pool cerrado,
   y en qué hilo? ¿En qué hilo se ejecuta `rejectedExecution`?
4. Respondé la actividad "Pool fijo y tareas rechazadas" de los apuntes para una máquina de 4 núcleos.
   ¿Cuánto vale `getPoolSize()` después de la tarea 10 y de la 100? ¿Puede `getActiveCount()` pasar
   de 4? ¿Y `getTaskCount()`?
5. ¿Qué pasaría si `main` termina sin llamar a `cerrar()`? ¿Por qué el programa no termina, si ya no
   queda ningún pedido?

## Pistas

> [!tip]- Pista 1: el constructor
> ```java
> pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(hilos);
> pool.setRejectedExecutionHandler(rechazos);
> ```

> [!tip]- Pista 2: `Pedido.run`
> ```java
> atendidoPor = Thread.currentThread().getName();
> try {
>     Thread.sleep(duracionMs);
>     terminado = true;
> } catch (InterruptedException e) {
>     interrumpido = true;
>     Thread.currentThread().interrupt();
> }
> ```

> [!tip]- Pista 3: respuesta a la pregunta 5
> Los hilos de un pool no son daemon, y cuando no hay pedidos no terminan: quedan esperando en la cola
> por si llega uno nuevo. Mientras haya un hilo no daemon vivo, la JVM no termina. Por eso cerrar el
> pool es obligatorio.

## Conexión con la teoría y los TPs

- Apuntes: "Executors: un pool de hilos administrado" (el ciclo de uso, la tabla de métodos y el
  servidor de 100 pedidos con `RejectedTaskController`), "Executor vs hilos a mano" y la actividad
  "Pool fijo y tareas rechazadas".
- TP1: el enunciado permitía `Executor`, `ExecutorService`, `Executors` y `ThreadPoolExecutor`, y
  pedía terminar "sin dejar hilos activos". Con un pool, eso es `shutdown()` más `awaitTermination()`.
