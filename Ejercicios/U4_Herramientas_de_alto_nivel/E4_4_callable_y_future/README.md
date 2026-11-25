# Ejercicio 4.4 — `Callable` y `Future`

> Unidad 4 · Executors · Nivel ★★★ · ~45 min

## Objetivo

Tareas que **devuelven** un resultado. En el ejercicio 1.2 la suma parcial de cada hilo quedaba en un
campo, que se leía después del `join()`. Acá cada tarea es un `Callable` que devuelve su resultado, y
quien la envía recibe un `Future`: un "vale" que se cambia por el resultado con `get()`. Vas a
escribir tres maneras de usarlos:
- Enviar todo y después recoger.
- `invokeAll`.
- Esperar con plazo y cancelar si no llega.

```
  submit(t1) ─► Future f1 ┐
  submit(t2) ─► Future f2 ├─ (corren en paralelo en el pool)
  submit(t3) ─► Future f3 ┘
  f1.get()  f2.get()  f3.get()   ← recién ahora se espera
```

## Conceptos

- `Callable<V>` contra `Runnable`: devuelve un valor y puede lanzar excepciones checked.
- `submit()` vuelve enseguida; `get()` espera.
- Enviar todas las tareas **antes** de esperar la primera, la misma trampa que `start`/`join` del 1.1.
- `ExecutionException`: cómo llega al que espera la excepción de una tarea.
- `invokeAll`: enviar todo y esperar todo, en una llamada.
- `get(tiempo, unidad)`, `TimeoutException` y `cancel(true)`, que interrumpe a la tarea.
- Una tarea que mira la interrupción para que se la pueda cancelar.

## Archivos

| Archivo              | Qué hacer                                                      |
| -------------------- | -------------------------------------------------------------- |
| `TramoDePrimos.java` | `call()`                                                       |
| `Paralelo.java`      | `ejecutarTodas`, `contarPrimos` y `conTiempoMaximo`            |
| `Verificador.java`   | No se toca                                                     |

## Orden sugerido

1. `TramoDePrimos.call`.
2. `ejecutarTodas`.
3. `contarPrimos` (con `invokeAll`).
4. `conTiempoMaximo`.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

## Restricciones

`ExecutorService`, `Callable` y `Future`, con el pool que te pasan. Nada de `CompletableFuture`,
`parallelStream`, `ForkJoin`, `new Thread` ni `Executors` (el verificador lo controla).

## Para pensar

1. En `ejecutarTodas`, hacé `pool.submit(tarea).get()` adentro del primer bucle. ¿Cuánto tardan 4
   tareas de 200 ms? ¿Qué ejercicio de la Unidad 1 tenía el mismo error?
2. ¿Por qué `Callable.call()` puede declarar `throws Exception` y `Runnable.run()` no puede declarar
   ninguna excepción checked? ¿Dónde termina una excepción que lanza `run()` en un pool?
3. Sacá el `cancel(true)` de `conTiempoMaximo`. ¿Qué le pasa a la tarea lenta? ¿Qué pasaría en un
   pool de 4 hilos si llegaran 4 tareas así?
4. Sacá el chequeo de interrupción de `TramoDePrimos.call`. ¿`cancel(true)` lo puede frenar? ¿Qué
   diferencia hay con una tarea que está en `sleep()`?
5. `invokeAll` devuelve los `Future` cuando ya terminaron todas. ¿Cuándo preferirías los dos bucles
   de `ejecutarTodas`? (Pensá en mostrar resultados a medida que llegan.)

## Pistas

> [!tip]- Pista 1: `ejecutarTodas`
> ```java
> List<Future<T>> futuros = new ArrayList<>();
> for (Callable<T> tarea : tareas) {
>     futuros.add(pool.submit(tarea));
> }
> List<T> resultados = new ArrayList<>();
> for (Future<T> futuro : futuros) {
>     resultados.add(futuro.get());
> }
> return resultados;
> ```

> [!tip]- Pista 2: `contarPrimos`
> ```java
> List<TramoDePrimos> tareas = new ArrayList<>();
> for (long[] tramo : partir(2, hasta, tramos)) {
>     tareas.add(new TramoDePrimos(tramo[0], tramo[1]));
> }
> long total = 0;
> for (Future<Long> futuro : pool.invokeAll(tareas)) {
>     total += futuro.get();
> }
> return total;
> ```

> [!tip]- Pista 3: respuesta a la pregunta 4
> `cancel(true)` no frena a nadie: solo interrumpe al hilo que corre la tarea. Una tarea en `sleep()`
> se entera sola, porque `sleep` lanza `InterruptedException`. Una que calcula sin parar no se entera
> nunca, salvo que mire `isInterrupted()`. Es lo mismo que en el ejercicio 1.4, con el hilo que
> duerme y el buscador de primos.

## Conexión con la teoría y los TPs

- Apuntes: en Conceptos de Java, "`Runnable` y `Callable<V>` son interfaces funcionales", y
  "Executors: un pool de hilos administrado". `Future` no está en las filminas: es lo que devuelve
  `submit()`, la otra forma de enviar tareas a un executor.
- Unidad 1: la suma paralela del 1.2 y la interrupción del 1.4, ahora con un pool.
