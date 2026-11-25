# Ejercicio 1.3 — Estados y atributos de un hilo

> Unidad 1 · Hilos en Java · Nivel ★★ · ~45 min

## Objetivo

Armar un hilo que recorra, en orden, **los seis estados** de `Thread.State`, quedándose unos 200 ms
en cada uno para que el verificador lo pueda ver. Para lograrlo tenés que saber qué operación lleva a
cada estado. De paso, leer y fijar los atributos de un hilo.

```
NEW ──start()──► RUNNABLE ──sleep()──► TIMED_WAITING ──join()──► WAITING
    ──synchronized ocupado──► BLOCKED ──fin de run()──► TERMINATED
```

## Conceptos

- Los seis estados de un hilo en Java y qué los provoca.
- `WAITING` vs `TIMED_WAITING` (`join()` vs `join(ms)`, `sleep(ms)`).
- `BLOCKED`: esperar para entrar a un `synchronized` que tiene otro hilo.
- Dormir **adentro** de un `synchronized` no libera el candado.
- Atributos: nombre, id, prioridad, daemon. La prioridad es solo una sugerencia.

## Archivos

| Archivo            | Qué hacer                                                         |
| ------------------ | ----------------------------------------------------------------- |
| `FichaDeHilo.java` | `describir` y `crear` (atributos)                                 |
| `Recorrido.java`   | `calcularDurante`, `arrancarBloqueador` y `crearHiloObservado`     |
| `Verificador.java` | No se toca                                                        |

## Orden sugerido

1. `FichaDeHilo` completa.
2. `Recorrido.calcularDurante`.
3. `Recorrido.arrancarBloqueador`.
4. `Recorrido.crearHiloObservado`, siguiendo la línea de tiempo del Javadoc de la clase.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

El último caso muestra la secuencia de estados que observó. Puede aparecer algún `RUNNABLE` de más
entre fases (mientras el hilo crea al dormilón, por ejemplo): es correcto, el verificador solo exige
que los seis estados aparezcan en orden.

## Para pensar

1. ¿Por qué el bloqueador se arranca al **principio** del `run()` del observado y no justo antes de
   la fase `BLOCKED`? ¿Qué podría pasar si lo arrancaras justo antes?
2. El bloqueador duerme adentro del `synchronized`. Si en vez de `Thread.sleep` hiciera
   `candado.wait(6 * FASE_MS)`, ¿el observado quedaría `BLOCKED`? ¿Por qué?
3. ¿Qué estado tendría el observado si en la fase 4 usara `dormilon.join(1000)`?
4. `getState()` es una foto: cuando la mirás, el hilo ya puede estar en otro estado. ¿Por qué entonces
   nunca hay que usar `getState()` para sincronizar hilos?
5. Creá dos hilos con prioridades 1 y 10 que cuenten lo más rápido posible durante un segundo.
   ¿El de prioridad 10 cuenta siempre más? Corrélo varias veces.

## Pistas

> [!tip]- Pista 1: el estado `RUNNABLE`
> `RUNNABLE` incluye tanto "ejecutando" como "listo esperando CPU": Java no distingue. Para que se vea,
> alcanza con que el hilo esté calculando.

> [!tip]- Pista 2: esqueleto de `crearHiloObservado`
> ```java
> return new Thread(() -> {
>     try {
>         arrancarBloqueador();
>         calcularDurante(FASE_MS);   // RUNNABLE
>         Thread.sleep(FASE_MS);      // TIMED_WAITING
>         // crear, arrancar y hacer join() al dormilón  → WAITING
>         // synchronized (candado) { }                  → BLOCKED
>     } catch (InterruptedException e) {
>         Thread.currentThread().interrupt();
>     }
> }, "observado");
> ```

> [!tip]- Pista 3: respuesta a la pregunta 2
> `wait()` **libera** el monitor mientras espera (y lo vuelve a tomar al despertar). Entonces el
> candado quedaría libre, el observado entraría directamente y nunca se lo vería `BLOCKED`.

## Conexión con la teoría y los TPs

- Apuntes: "Threads en Java — Estados de un hilo en Java" y "Ejemplo: seguir los estados de 10 hilos".
- TP1: el informe pide las "condiciones de espera y despertar de los hilos": son estas transiciones
  (`WAITING` en una cola vacía, `BLOCKED` en la entrada a una sección crítica).
