# Ejercicio 3.4 — Buffer con tres semáforos, y una largada

> Unidad 3 · Semáforos · Nivel ★★★ · ~50 min

## Objetivo

Dos usos de los semáforos que un candado no puede cubrir, porque **el que toma no es el que
devuelve**:

1. **El buffer acotado del ejercicio 2.3, otra vez**, con la misma interfaz, pero hecho con tres
   semáforos en vez de `synchronized` y `wait`/`notifyAll`. Al terminar, comparalo con tu versión
   del 2.3.
2. **Una largada**: los corredores esperan una señal y el juez los larga a todos de una vez. Es un
   semáforo que arranca en 0 y funciona como señal, no como candado.

```
  poner():  huecos.acquire() → mutex → agregar → mutex → items.release()
  sacar():  items.acquire()  → mutex → sacar   → mutex → huecos.release()

  corredores: esperarLargada() = señal.acquire()    juez: largar() = señal.release(N)
```

## Conceptos

- Productor/consumidor con `huecos` (arranca en la capacidad), `items` (arranca en 0) y un `mutex`.
- El **orden** en que se toman los semáforos, y el deadlock que aparece si se invierte.
- No perder permisos: validar antes de tomar, y no dejar que una interrupción se lleve un permiso ya
  tomado.
- Semáforo inicializado en 0 como señal entre hilos.
- El semáforo **recuerda** las señales; un `notifyAll()` que no encuentra a nadie se pierde.

## Archivos

| Archivo                   | Qué hacer                                                         |
| ------------------------- | ----------------------------------------------------------------- |
| `BufferConSemaforos.java` | Constructor, `poner`, `sacar` y `tamanio`                         |
| `Largada.java`            | Declarar el semáforo, `esperarLargada`, `largar` y `esperando`    |
| `Verificador.java`        | No se toca                                                        |

## Orden sugerido

1. `BufferConSemaforos`: constructor y `tamanio`.
2. `poner` y `sacar`, siguiendo el diagrama del Javadoc. El orden de las líneas es la mitad del
   ejercicio.
3. `Largada` completa.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

## Restricciones

Solo `Semaphore` y colecciones comunes. Nada de `synchronized`, `wait`/`notify`, `Lock`,
`BlockingQueue`, `CountDownLatch`, clases `Atomic...` ni `Collections.synchronized...` (el
verificador lo controla).

## Para pensar

1. **Experimento A:** en `poner`, tomá el `mutex` **antes** que `huecos`. Corré el verificador. ¿Qué
   caso se cuelga? Contá la secuencia de un productor y un consumidor que termina en deadlock con
   capacidad 1. ¿Por qué el verificador no te puede decir quién espera a quién?
2. **Experimento B:** en `poner`, validá el `null` **después** de `huecos.acquire()`. ¿Qué caso falla?
   ¿Cuánto se "achica" el buffer por cada `poner(null)`?
3. En el 2.3 hacía falta `while` alrededor del `wait()` y `notifyAll()`. ¿Por qué acá no hace falta
   ninguna de las dos cosas? ¿Qué ventaja tiene que productores y consumidores esperen en colas
   distintas?
4. La cola cerrable del TP1 (`CloseableBlockingQueue`) usa un único semáforo y no tiene límite de
   capacidad. Cuando se cierra, hace un `release()` y cada consumidor que se despierta con la cola
   vacía hace otro `release()` antes de irse: un despertar "en cascada". ¿Por qué hace falta eso? ¿Qué
   pasaría si `close()` hiciera un solo `release()` y nadie más liberara?
5. En la largada, ¿qué pasa si el juez llama a `largar()` dos veces? ¿Y si llegan más corredores que
   los que se declararon en el constructor? ¿Cómo lo modelarías en una red de Petri?

### Más para pensar: el deadlock en la red de Petri

El buffer con tres semáforos corresponde a la red de Petri del productor/consumidor del apunte
teórico (`huecos` y `productos` son plazas, y el `mutex` es una plaza con una ficha). Después del
experimento A, construí un escenario con un buffer de 3 lugares que lleve a deadlock, y señalá en la
red qué cambió al invertir el orden de `huecos.acquire()` y `mutex.acquire()`.

## Pistas

> [!tip]- Pista 1: `sacar`
> ```java
> items.acquire();
> T primero;
> mutex.acquireUninterruptibly();
> try {
>     primero = elementos.removeFirst();
> } finally {
>     mutex.release();
> }
> huecos.release();
> return primero;
> ```

> [!tip]- Pista 2: la `Largada`
> El semáforo arranca en 0: `private final Semaphore senal = new Semaphore(0);`. Cada corredor hace
> `senal.acquire()`, y el juez `senal.release(corredores)`.

> [!tip]- Pista 3: respuesta al experimento A
> Capacidad 1 y el buffer lleno. El productor toma el `mutex` y se duerme en `huecos.acquire()` (no
> hay huecos). El consumidor pasa `items.acquire()` (hay un elemento) y se duerme en el `mutex`, que
> tiene el productor. El productor espera un hueco que solo puede liberar el consumidor, y el
> consumidor espera un mutex que solo puede liberar el productor. La JVM no lo detecta como deadlock
> porque los semáforos no tienen dueño: para ella nadie "tiene" el mutex, así que no hay una espera
> circular entre dueños que encontrar.

## Conexión con la teoría y los TPs

- Apuntes: "Los semáforos no tienen dueño" (productor/consumidor, señalización entre hilos y
  barreras de arranque) y "Otros métodos de `Semaphore`" (`release(int)`, `getQueueLength()`).
- Redes de Petri: en "Modelos básicos", el buffer acotado (los lugares "libres" y "ocupados" son
  `huecos` e `items`) y la sincronización por señal ("la contracara exacta del semáforo inicializado
  en 0").
- TP1: `CloseableBlockingQueue` del grupo combina un `synchronized` para la cola con un `Semaphore(0)`
  para la espera, y toma el semáforo **afuera** de la sección crítica por el mismo motivo que el
  experimento A.
- TP final: las colas de condición del monitor (`ConditionQueues`) son semáforos que arrancan en 0, y
  el mutex del monitor es un semáforo binario tomado con `acquireUninterruptibly()`.
