# Ejercicio 1.4 — Interrupción, `sleep` y `join`

> Unidad 1 · Hilos en Java · Nivel ★★ · ~45 min

## Objetivo

Detener hilos **de la forma correcta**: pidiéndoselo con `interrupt()` y haciendo que cooperen. Vas a
ver los dos casos posibles: un hilo que duerme (la interrupción le llega como excepción) y un hilo que
calcula sin parar (tiene que preguntar). Y la trampa de "tragarse" la interrupción.

## Conceptos

- `interrupt()` no mata a nadie: marca un pedido.
- `InterruptedException`: cómo llega el pedido a un hilo que está en `sleep`, `join` o `wait`.
- `isInterrupted()` vs `Thread.interrupted()`.
- `sleep` borra la marca al lanzar la excepción, y por qué hay que restaurarla.
- `join(ms)`: esperar con plazo para no quedar colgado.

## Archivos

| Archivo                 | Qué hacer                                     |
| ----------------------- | --------------------------------------------- |
| `Interrupciones.java`   | `detener` y `dormir`                          |
| `BuscadorDePrimos.java` | `esPrimo` y `run()` (hilo que nunca duerme)   |
| `Latido.java`           | `run()` (hilo que casi siempre duerme)        |
| `Verificador.java`      | No se toca                                    |

## Orden sugerido

1. `BuscadorDePrimos.esPrimo` (calentamiento, sin hilos).
2. `Interrupciones.dormir` e `Interrupciones.detener`.
3. `Latido.run`.
4. `BuscadorDePrimos.run`.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

## Para pensar

1. `Latido` y `BuscadorDePrimos` terminan los dos al ser interrumpidos, pero por mecanismos distintos.
   Explicá cada uno.
2. Cambiá `Latido.run()` para que el `try/catch` quede **adentro** del bucle y en el `catch` solo
   haya `e.printStackTrace()`. Corré el verificador. ¿Qué caso falla y por qué? (Es la actividad de
   los apuntes: el flag que `sleep` limpió.)
3. ¿Por qué `detener` usa `join(timeoutMs)` y no `join()`? ¿Qué le pasaría al programa si el hilo no
   cooperara?
4. El último caso del verificador ("Cadena") falla si `dormir` no restaura la marca. Dibujá la
   secuencia de eventos que lleva al bucle infinito.
5. ¿Por qué `Thread.stop()` es peligroso? Pensá en un hilo cortado a la mitad de una sección crítica.

## Pistas

> [!tip]- Pista 1: dónde va el `try/catch` en `Latido`
> ```java
> try {
>     while (true) {
>         latidos++;
>         Thread.sleep(periodoMs);
>     }
> } catch (InterruptedException e) {
>     // pedido de terminar
> }
> ```
> Acá no hace falta restaurar la marca: el `run()` termina enseguida y nadie más la va a mirar.

> [!tip]- Pista 2: diferencia entre `isInterrupted()` y `Thread.interrupted()`
> `t.isInterrupted()` consulta la marca de `t` sin tocarla. `Thread.interrupted()` es `static`:
> consulta la del hilo actual **y la borra**. Si el `BuscadorDePrimos` usara la segunda, el hilo
> terminaría igual, pero quien lo llamó ya no podría saber que lo interrumpieron.

## Conexión con la teoría y los TPs

- Apuntes: "Interrupción, sleep y join de hilos" (`PrimeGenerator`, `FileClock`, el buscador de
  archivos) y "Cómo se relacionan los tres mecanismos".
- TP1: el enunciado prohíbe `Thread.stop` y `System.exit` para terminar. Las demoras de cada etapa son
  `sleep`, y cada worker tiene que decidir qué hacer si lo interrumpen mientras duerme.
- TP final: el monitor duerme hasta que se abre la ventana temporal de una transición
  (`sleepUninterruptibly`) y, si lo interrumpieron mientras dormía, restaura la marca antes de volver:
  el mismo cuidado que tiene `dormir`.
