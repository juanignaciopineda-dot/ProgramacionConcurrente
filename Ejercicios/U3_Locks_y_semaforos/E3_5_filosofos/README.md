# Ejercicio 3.5 — La cena de los filósofos

> Unidad 3 · Deadlock · Nivel ★★★★ · ~60 min

## Objetivo

Cinco filósofos, cinco tenedores, y cada uno necesita los dos que tiene al lado para comer. La versión
ingenua (cada uno toma el izquierdo y después el derecho) ya está hecha, y el primer caso del
verificador te la muestra trabada. Vas a escribir **tres** maneras de evitar el deadlock, cada una
rompiendo una condición distinta.

```
            F0
        T0      T1          F0 usa T0 y T1,  F1 usa T1 y T2,  ...,  F4 usa T4 y T0
     F4            F1
        T4      T2          ingenua: los 5 toman el izquierdo a la vez → nadie consigue el derecho
         F3  T3  F2
```

## Conceptos

Para que haya deadlock tienen que darse **las cuatro** condiciones a la vez:

| Condición             | En la mesa                                                    | ¿Quién la rompe?          |
| --------------------- | ------------------------------------------------------------- | ------------------------- |
| Exclusión mutua       | Un tenedor lo usa un filósofo por vez                         | Nadie: es el problema     |
| Retención y espera    | Un filósofo se queda con un tenedor mientras espera el otro   | `intentarComer`           |
| No apropiación        | Nadie le puede sacar un tenedor a otro                        | Ninguna de las tres       |
| Espera circular       | F0 espera a F1, que espera a F2, ..., que espera a F0         | Orden global y mayordomo  |

Además:

- Dos locks anidados con un `try`/`finally` por cada uno.
- `tryLock` con tiempo y **retroceso** (soltar lo que tenés si no conseguís lo que falta), y el
  riesgo de *livelock*.
- Deadlock e inanición son problemas distintos.

## Archivos

| Archivo            | Qué hacer                                                                          |
| ------------------ | ---------------------------------------------------------------------------------- |
| `Mesa.java`        | `comerConOrdenGlobal`, el semáforo del mayordomo, `comerConMayordomo` e `intentarComer` |
| `Verificador.java` | No se toca                                                                         |

`comerIngenuo` ya está hecho, y es el modelo de cómo se toman y se sueltan dos locks anidados.
`pausa()` va entre el primer tenedor y el segundo, para que una estrategia que se puede trabar se
trabe. `comer(i)` controla que tengas los dos tenedores.

## Orden sugerido

1. Corré el verificador sin tocar nada y mirá el primer caso: la JVM detecta la espera circular y te
   dice quién espera a quién.
2. `comerConOrdenGlobal`.
3. El semáforo del mayordomo (campo y constructor) y `comerConMayordomo`.
4. `intentarComer`.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

Si una estrategia se traba, el caso dice `[FALLA]` con la lista de hilos que no terminaron y, si hay
espera circular entre locks, quién espera a quién. El verificador los destraba interrumpiéndolos, así
que conviene usar `lockInterruptibly()`, como `comerIngenuo`.

## Restricciones

Solo `ReentrantLock` (los tenedores) y `Semaphore` (el mayordomo). Nada de `synchronized` ni
`wait`/`notify` (el verificador lo controla).

## Para pensar

1. En el orden global, ¿qué filósofo es el único que toma los tenedores en un orden distinto al
   ingenuo? Contá por qué, con ese único cambio, la ronda de esperas ya no se puede cerrar.
2. Con el mayordomo, ¿por qué alcanza con `n - 1`? Probá con `new Semaphore(n)` y mirá qué caso
   falla. ¿Qué perderías si dejara sentarse solo a 2, que es lo máximo que pueden comer a la vez con 5?
3. En `intentarComer`, ¿qué pasaría si los cinco filósofos usaran `tryLock()` **sin** tiempo, todos
   arrancaran a la vez y, al fallar, reintentaran enseguida, sin pausa? Explicá por qué eso es un
   *livelock* y no un deadlock. ¿Para qué sirve la espera al azar que hace el verificador entre
   intentos?
4. Ninguna de las tres estrategias garantiza que nadie se quede sin comer para siempre. Describí una
   secuencia en la que el filósofo 2 nunca come con el orden global. ¿Qué cambiarías para evitarlo?
   (Pensá en el modo *fair* del 3.1.)
5. La red de Petri de los apuntes resuelve el problema con una transición $Toma_i$ que saca los dos
   tenedores **a la vez**. ¿Por qué en Java no se puede escribir eso con dos `ReentrantLock`? ¿Cuál de
   las tres estrategias se le parece más?

## Pistas

> [!tip]- Pista 1: `comerConOrdenGlobal`
> ```java
> int a = Math.min(izquierdo(i), derecho(i));
> int b = Math.max(izquierdo(i), derecho(i));
> ```
> Y después, la misma estructura que `comerIngenuo` con `tenedores[a]` primero y `tenedores[b]`
> después.

> [!tip]- Pista 2: el mayordomo
> Campo `private final Semaphore mayordomo;`, en el constructor `this.mayordomo = new Semaphore(n - 1);`,
> y en `comerConMayordomo`:
> ```java
> mayordomo.acquire();
> try {
>     comerIngenuo(i);
> } finally {
>     mayordomo.release();
> }
> ```

> [!tip]- Pista 3: respuesta a la pregunta 1
> El último, el filósofo `n - 1`: su izquierdo es `n - 1` y su derecho es `0`, así que con el orden
> global pide primero el 0. Para que haya ronda, cada filósofo tiene que tener un tenedor y esperar el
> de su vecino. Pero el `n - 1` compite por el 0 con el filósofo 0 **antes** de tener nada en la mano:
> si gana el 0, el `n - 1` espera con las manos vacías y no retiene a nadie; si gana el `n - 1`, el
> que espera con las manos vacías es el 0. En los dos casos hay un filósofo que no tiene nada en la
> mano, y la ronda de esperas se corta ahí.

## Conexión con la teoría y los TPs

- Apuntes: en Redes de Petri, "La cena de los filósofos" (la versión ingenua con deadlock
  alcanzable, la versión con disparo atómico y sus P-invariantes, y por qué la red no evita la
  inanición). En la Introducción, "Problemas comunes de los programas concurrentes" (deadlock,
  inanición y livelock) y "Requisitos para la exclusión mutua" (sin demora indefinida).
- "Lock: el punto de partida": la advertencia de deadlock del libro y la regla de fijar un orden
  global de adquisición.
- TP final: el monitor resuelve este tipo de conflicto con una **política**. Cuando varias
  transiciones sensibilizadas tienen hilos esperando, la política elige a cuál de esos hilos
  despertar.
