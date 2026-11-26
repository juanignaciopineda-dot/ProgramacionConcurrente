# Ejercicio 4.1 — Arranque de un servidor con `CountDownLatch`

> Unidad 4 · Sincronizadores · Nivel ★★ · ~35 min

## Objetivo

Combina el `CountDownLatch`, un hilo daemon, la interrupción y el manejo de excepciones:
- Cinco módulos se inicializan en paralelo, cada uno en su hilo y con tiempos distintos.
- El tablero no puede empezar hasta que **todos** terminaron, y después informa periódicamente
  hasta que lo interrumpan.
- Si un módulo falla al inicializarse, el tablero no puede quedarse esperando para siempre.

```
  modulo-db       ███████████ countDown()
  modulo-cache    ████ countDown()
  modulo-red      ██████████████████ countDown()  ← el último: el latch llega a 0
  tablero (daemon) await() ··························► informa, informa, informa...
```

## Conceptos

- `CountDownLatch`: arranca en N, `countDown()` resta uno, `await()` duerme hasta que llegue a 0.
- `countDown()` en un `finally`, para que un módulo que falla igual avise.
- `try`/`finally` sin `catch`: avisa y deja pasar la excepción.
- `await(tiempo, unidad)` para no esperar para siempre.
- Un latch en 0 queda en 0, y no se puede reutilizar.
- Un hilo daemon para el que no termina nunca.
- Esperar dormido (`WAITING`) y no preguntando `getCount()` en un bucle.

## Archivos

| Archivo            | Qué hacer                                                   |
| ------------------ | ----------------------------------------------------------- |
| `Modulo.java`      | `run()`                                                     |
| `Tablero.java`     | `run()`                                                     |
| `Arranque.java`    | `lanzarModulos`, `lanzarTablero` y `esperarModulos`         |
| `Verificador.java` | No se toca                                                  |

## Orden sugerido

1. `Arranque.lanzarTablero` y `Tablero.run`.
2. `Modulo.run` y `Arranque.lanzarModulos`.
3. `Arranque.esperarModulos`.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

En el caso del módulo que falla vas a ver una línea "(el hilo 'modulo-red' terminó con
IllegalStateException...)". Está bien: es la excepción que el módulo deja salir a propósito.

## Restricciones

`CountDownLatch` y `Thread`. Nada de `join()`, `synchronized`, `wait`/`notify`, semáforos ni locks, y
tampoco `getCount()` (el verificador lo controla).

## Para pensar

1. Sacá el `finally` de `Modulo.run` (dejá el `countDown()` después de `inicializar()`). ¿Qué caso
   falla? Si un módulo lanza una excepción, ¿qué pasa con su `countDown()`?
2. ¿Por qué el tablero tiene que ser daemon? ¿Qué pasaría al terminar `main` si no lo fuera? Pensá
   en la pregunta del parcial: "un programa con un hilo main, productores y dos hilos daemon,
   ¿cuándo termina?".
3. Compará con `join()`: ¿qué tendría que saber el tablero para esperar a los módulos con `join()`?
   ¿Qué pasa si el aviso no coincide con el final del hilo (por ejemplo, un módulo que avisa "listo"
   y después sigue trabajando)?
4. Si el servidor se reinicia, ¿se puede reutilizar el mismo latch para el segundo arranque? ¿Qué
   herramienta de esta unidad sí se puede repetir?
5. Modelá el arranque como red de Petri con 5 módulos. ¿Qué estructura de los modelos básicos es el
   latch?

## Pistas

> [!tip]- Pista 1: `Modulo.run`
> ```java
> try {
>     inicializar();
>     inicializado = true;
> } finally {
>     listos.countDown();
> }
> ```

> [!tip]- Pista 2: `lanzarTablero`
> ```java
> Thread hilo = new Thread(tablero, "tablero");
> hilo.setDaemon(true);      // antes de start()
> hilo.start();
> return hilo;
> ```

> [!tip]- Pista 3: respuesta a la pregunta 5
> Es el **co-end** (terminación sincronizada): una transición con un lugar de entrada por módulo, que
> solo dispara cuando los cinco depositaron su token. El latch cuenta los tokens que faltan.

## Conexión con la teoría y los TPs

- Apuntes: "Esperar múltiples eventos concurrentes: `CountDownLatch`" (la videoconferencia, los
  detalles que hay que saber y la tabla contra `join()`), y "Hilos daemon".
- Redes de Petri: co-begin y co-end en "Modelos básicos".
