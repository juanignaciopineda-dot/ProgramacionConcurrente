# Ejercicio 3.1 — Cuenta bancaria con `ReentrantLock`

> Unidad 3 · Locks · Nivel ★★ · ~40 min

## Objetivo

Proteger una cuenta bancaria con un `ReentrantLock` en vez de `synchronized`, y usar lo que el lock da
de más: rendirse si el candado no llega a tiempo (`tryLock`), volver a tomarlo desde adentro
(reentrancia) y elegir si es justo con los que esperan (*fairness*). Lo más importante del ejercicio
es una regla: **el `unlock()` va en el `finally`**.

```
  candado.lock();
  try {
      procesar();          ← simula la base de datos y controla que tengas el candado
      saldo -= monto;
  } finally {
      candado.unlock();    ← aunque salga una excepción
  }
```

## Conceptos

- El patrón `lock()` / `try` / `finally unlock()`, y qué pasa si una excepción se saltea el `unlock()`.
- `tryLock(tiempo, unidad)`: esperar un tiempo máximo, y dónde va el `try` cuando el candado puede no
  llegar.
- Reentrancia: el hilo que tiene el candado lo vuelve a pedir y se lo dan, y eso permite armar
  operaciones atómicas con otras más chicas.
- Fairness: `new ReentrantLock(true)` contra el modo por defecto.
- *Check-then-act* con un lock.

## Archivos

| Archivo                           | Qué hacer                                                                   |
| --------------------------------- | --------------------------------------------------------------------------- |
| `Cuenta.java`                     | Constructor, `depositar`, `extraer`, `intentarExtraer`, `extraerTodo` y `getSaldo` |
| `SaldoInsuficienteException.java` | Ya está hecha                                                               |
| `Verificador.java`                | No se toca                                                                  |

## Orden sugerido

1. Leé `getRegistro()`, que ya está hecho: es el modelo del patrón.
2. Constructor y `getSaldo`.
3. `depositar` y `extraer`.
4. `intentarExtraer`.
5. `extraerTodo`.
6. Mirá el último caso del verificador: el experimento de fairness.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

El último caso es un experimento y no falla nunca. Cuatro hilos, que arrancan con 10 ms de diferencia,
depositan dos veces cada uno. El verificador muestra en qué orden pasaron por el candado, primero con
un candado justo y después con uno no justo.

## Restricciones

Solo `ReentrantLock`. Nada de `synchronized`, `Semaphore` ni clases `Atomic...` (el verificador lo
controla).

## Para pensar

1. Mirá el resultado del experimento. ¿Por qué con el candado justo sale `0 1 2 3 0 1 2 3` y con el no
   justo, casi siempre, `0 0 1 1 2 2 3 3`? ¿Está garantizado ese segundo orden? Es el experimento de la
   filmina de la Clase 4.
2. Sacá el `unlock()` del `finally` de `extraer` (dejalo al final del `try`) y corré el verificador.
   ¿Qué caso falla y por qué? ¿Por qué el hilo que llamó a `extraer` puede seguir usando la cuenta sin
   problema, y los demás no?
3. ¿Qué pasa si en `intentarExtraer` ponés el `tryLock` adentro del `try`? ¿Qué excepción sale, y en
   qué situación?
4. `extraerTodo` toma el candado y llama a `getSaldo()` y a `extraer()`, que lo vuelven a tomar.
   ¿Cuánto vale `candado.getHoldCount()` adentro de `extraer` en ese caso? ¿Qué pasaría si el lock no
   fuera reentrante?
5. Mirá "Elegir el mecanismo", más abajo. Para los casos (a) y (e), ¿usarías
   `synchronized` o `ReentrantLock`? ¿Qué capacidad del lock decide la respuesta?

### Más para pensar: elegir el mecanismo

Para cada situación, decidí qué mecanismo usarías (`synchronized`, `ReentrantLock`, `ReadWriteLock`
o `Semaphore`) y justificá en una línea:

- (a) Un contador de visitas que se incrementa desde muchos hilos y se lee de vez en cuando.
- (b) Una caché en memoria que se consulta miles de veces por segundo y se recarga una vez por hora.
- (c) Un pool de 5 conexiones a base de datos.
- (d) Un hilo que debe esperar a que otro termine de inicializar el sistema antes de arrancar.
- (e) Una sección crítica de la que hay que salir si no se puede entrar en 50 ms.

> [!tip]- Pista
> (d) y (e) descartan de entrada a `synchronized`. Para (d) pensá en un semáforo inicializado en 0, y
> para (e) en `tryLock(long, TimeUnit)`.

## Pistas

> [!tip]- Pista 1: `extraer`
> ```java
> if (monto <= 0) {
>     throw new IllegalArgumentException("monto inválido: " + monto);
> }
> candado.lock();
> try {
>     procesar();
>     if (saldo < monto) {
>         throw new SaldoInsuficienteException(saldo, monto);
>     }
>     saldo -= monto;
> } finally {
>     candado.unlock();
> }
> ```

> [!tip]- Pista 2: `intentarExtraer`
> ```java
> if (!candado.tryLock(esperaMaxMs, TimeUnit.MILLISECONDS)) {
>     return false;
> }
> try {
>     extraer(monto);      // reentrante: lo vuelve a tomar sin trabarse
>     return true;
> } finally {
>     candado.unlock();
> }
> ```

> [!tip]- Pista 3: respuesta a la pregunta 1
> Con el candado justo, cuando el hilo 0 suelta el candado después de su primer depósito, los hilos
> 1, 2 y 3 ya están en la cola. El 0 lo vuelve a pedir enseguida, pero queda último, detrás de ellos.
> Con el no justo, el 0 lo pide en el mismo instante en que lo soltó, y como ya está corriendo se lo
> lleva antes de que el 1 termine de despertarse. No está garantizado: el modo no justo no promete
> ningún orden, y por eso admite inanición.

## Conexión con la teoría y los TPs

- Apuntes: "Lock: el punto de partida" (las tres reglas), "Fairness: quién entra primero" (el
  experimento del `PrintQueue`) y "`synchronized` vs `Lock`: cuándo usar cada uno".
- TP1: el `EventLogger` del grupo protege la escritura del log con un `ReentrantLock` y libera el
  candado en un `finally`.
