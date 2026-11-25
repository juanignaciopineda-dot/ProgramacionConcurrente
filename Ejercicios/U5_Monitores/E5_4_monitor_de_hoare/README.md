# Ejercicio 5.4 — Monitor de Hoare: *signal and urgent wait* con cola de cortesía

> Unidad 5 · Monitores · Nivel ★★★★ · ~60 min

## Objetivo

Construir un monitor genérico con la política de Hoare (SU), con semáforos y tres tipos de cola:
- **De entrada.**
- **Una por condición.**
- **De cortesía:** el que señaliza le cede el monitor al despertado y espera ahí, con prioridad sobre
  la entrada.

Después, escribir sobre ese monitor el buffer acotado **con `if`**. Si el monitor está bien hecho, el
buffer funciona. Si no, el verificador lo nota.

```
  W: entrar ─ c.esperar() ·········································· despierta ─ ... ─ salir
  S: entrar ─ ... ─ c.senalar() ─ [cola de cortesía] ··························· sigue ─ salir
  E:            entrar ─ [cola de entrada] ··················································· entra
  orden en el monitor:   W  →  S  →  E
```

## Conceptos

- Las tres colas del monitor y sus prioridades: cortesía > entrada.
- "Ceder el tiempo de procesamiento restante del hilo activo al hilo despertado, por encontrarse
  urgido": la respuesta textual del parcial sobre la cola de cortesía.
- La regla del monitor: quien lo deja (al salir o al dormirse) se lo pasa a la cortesía si hay
  alguien, y si no a la entrada.
- Una clase interna (`Condicion`) que toca el estado de la externa (`MonitorHoare`).
- Con SU el despertado encuentra la condición cierta: alcanza `if`.

## Archivos

| Archivo             | Qué hacer                                                                         |
| ------------------- | --------------------------------------------------------------------------------- |
| `MonitorHoare.java` | `entrar`, `salir`, `cederMonitor`, `Condicion.esperar` y `Condicion.senalar`      |
| `BufferHoare.java`  | `poner` y `sacar`                                                                 |
| `Verificador.java`  | No se toca                                                                        |

## Orden sugerido

1. `entrar`, `salir` y `cederMonitor`: con eso pasa el primer caso.
2. `Condicion.esperar` y `Condicion.senalar`, siguiendo los esquemas del Javadoc.
3. `BufferHoare`.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

Los dos casos de orden anotan quién usó el monitor y en qué orden, y lo comparan con lo que dice la
política. El segundo es el difícil: el despertado se vuelve a dormir en otra condición, y el monitor
tiene que volver al de cortesía, no ir a la entrada.

## Restricciones

Solo `Semaphore`. Nada de `synchronized`, locks, la `Condition` de Java ni `wait`/`notify`. En
`BufferHoare` se espera con `if` (el verificador lo controla).

## Para pensar

1. Cambiá `senalar` para que despierte y siga, sin pasar por la cola de cortesía. ¿Qué política te
   quedó? ¿Qué caso falla y por qué el buffer con `if` se rompe?
2. Cambiá `salir` para que siempre haga `mutex.release()`. ¿Qué le pasa al que estaba en cortesía?
   ¿Por qué el verificador dice `[COLGADO]` y no puede decir quién espera a quién?
3. Pregunta de parcial: ¿qué resuelven las políticas de **reactivación** (entrada, waiting, cortesía)?
   ¿Y las de las **colas de condición**? Usá este ejercicio y el 5.5 para no confundirlas.
4. La variante SW (*signal and wait*) es igual pero sin cola de cortesía: el que señaliza vuelve a la
   cola de entrada. ¿Qué cambiaría en el orden del primer caso? ¿Qué riesgo de postergación tiene
   el que señalizó?
5. ¿Cuántos cambios de contexto cuesta un `senalar` con alguien esperando en SU, comparado con SC?
   ¿Por qué Java eligió SC?

## Pistas

> [!tip]- Pista 1: `cederMonitor`
> ```java
> if (enCortesia > 0) {
>     cortesia.release();
> } else {
>     mutex.release();
> }
> ```

> [!tip]- Pista 2: `senalar`
> ```java
> if (esperando > 0) {
>     esperando--;
>     enCortesia++;
>     cola.release();
>     cortesia.acquireUninterruptibly();
>     enCortesia--;
> }
> ```

> [!tip]- Pista 3: respuesta a la pregunta 3
> Las de **reactivación** resuelven la **secuencia de acceso al monitor**: cuando se libera, a quién se
> le da entre la cola de entrada, las de condición y la de cortesía. Es lo de este ejercicio. Las de
> las **colas de condición** resuelven los **conflictos**: entre varias colas con gente que podrían
> seguir, cuál se despierta. Es lo del 5.5. La corrección de los parciales castiga mezclarlas.

## Conexión con la teoría y los TPs

- Apuntes: "Clasificación y políticas de señalización": la tabla de SC, SW, SU y SX, las colas $e$,
  $s$ y $c.q$, y por qué con SU alcanza `if`.
- Parciales: "¿Cuál es el objetivo de implementar una cola de cortesía en un monitor?" y "¿Qué
  resuelven las políticas de reactivación de un monitor?".
