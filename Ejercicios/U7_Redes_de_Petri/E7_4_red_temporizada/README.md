# Ejercicio 7.4 — Red de Petri con tiempo

> Unidad 7 · Redes de Petri · Nivel ★★★ · ~45 min

## Objetivo

Agregarle tiempo a la red con la semántica de **tiempo de sensibilización**:
- Cada transición tiene una ventana `[α, β]`.
- Solo se puede disparar si lleva sensibilizada entre α y β milisegundos.

Lo difícil no es la cuenta, sino saber **cuándo arranca de nuevo cada cronómetro**. Es exactamente el
`updateTimestamps` del TP final. Para probarlo sin esperar de verdad, la red recibe un `Reloj`, y el
verificador usa uno falso que avanza cuando él quiere.

```
  T1 se sensibiliza en τ ──────[ τ+α ··········· τ+β ]──────►
                       no se puede   se puede         ya no se puede
```

## Conceptos

- RdPT: `<P, T, Pre, Post, M0, CIS>`, con un intervalo estático `[α, β]` por transición.
- El estado de una red con tiempo es `(M, I)`: el marcado y desde cuándo está sensibilizada cada una.
- Cuándo arranca un cronómetro:
  - Una transición recién sensibilizada arranca de cero, y la disparada que sigue sensibilizada
    también.
  - Las que siguen sensibilizadas conservan su instante.
  - Las que dejan de estarlo se apagan.
- `faltaParaDisparar`: lo que se duerme el hilo en el monitor con tiempo.
- Inyectar el reloj para que las pruebas sean exactas.

## Archivos

| Archivo               | Qué hacer                                                                          |
| --------------------- | ---------------------------------------------------------------------------------- |
| `RedTemporizada.java` | Constructor, `tiempoSensibilizada`, `puedeDisparar`, `faltaParaDisparar` y `disparar` |
| `Reloj.java`          | Ya está hecho                                                                      |
| `RedDePetri.java`     | Ya está hecha (es la del 7.1)                                                      |
| `Verificador.java`    | No se toca                                                                         |

## Orden sugerido

1. Constructor y `tiempoSensibilizada`.
2. `puedeDisparar` y `faltaParaDisparar`.
3. `disparar`, con las reglas de los cronómetros del Javadoc de la clase.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

Ningún caso espera de verdad: el reloj falso salta los milisegundos que haga falta, así que el
resultado no depende de lo cargada que esté la máquina.

## Para pensar

1. ¿Por qué la transición que se disparó y sigue sensibilizada arranca su cronómetro de cero, y no
   sigue con el que tenía? Pensá en una transición con dos fichas en su lugar de entrada: ¿qué
   modela cada ficha?
2. Una transición sensibilizada desde hace mucho y otra recién sensibilizada están en conflicto.
   ¿Cuál dispara primero según la política de resolución de los apuntes ("disparando antes la que
   termina antes")?
3. Los apuntes dicen que la suspensión o el aborto de una actividad (*timeout*) solo se pueden
   modelar con tiempo de sensibilización, no con tiempo de disparo. ¿Por qué? Armá la red de una
   actividad con timeout: una transición CODE en conflicto con una TIME.
4. En el monitor con tiempo del TP final, el hilo que llega antes de α suelta el mutex y duerme
   `faltaParaDisparar`. ¿Qué pasaría si durmiera con el mutex tomado? ¿Y si, en vez de dormir,
   preguntara en un bucle?
5. ¿Por qué `Reloj.DEL_SISTEMA` usa `System.nanoTime()` y no `System.currentTimeMillis()`? (El Javadoc
   del `TransitionTimer` del TP final lo explica.)

## Pistas

> [!tip]- Pista 1: `faltaParaDisparar`
> ```java
> long tiempo = tiempoSensibilizada(t);
> if (tiempo < 0 || tiempo > beta[t]) {
>     return -1;
> }
> return Math.max(0, alfa[t] - tiempo);
> ```

> [!tip]- Pista 2: los cronómetros en `disparar`
> ```java
> boolean[] antes = red.sensibilizadas();
> red.disparar(t);
> boolean[] despues = red.sensibilizadas();
> long ahora = reloj.ahoraMs();
> for (int u = 0; u < despues.length; u++) {
>     if (!despues[u]) {
>         desde[u] = NO_SENSIBILIZADA;
>     } else if (!antes[u] || u == t) {
>         desde[u] = ahora;
>     }
> }
> ```

> [!tip]- Pista 3: respuesta a la pregunta 1
> Con dos fichas, la transición representa dos "trabajos" de la misma actividad. Cuando dispara, uno
> terminó, y el siguiente recién empieza a ser atendido: su α se cuenta desde ahora, no desde que
> llegó el primero. Si conservara el cronómetro, el segundo trabajo podría terminar sin haber tardado
> nada.

## Conexión con la teoría y los TPs

- Apuntes: "Redes de Petri con Tiempo (RdPT)" (definición, regla de disparo, casos particulares y
  estado de una RdPT), "Interpretaciones con tiempo" (tiempo de sensibilización contra tiempo de
  disparo), "Planificación, análisis e implementación" y, en "Monitores", el final de "Un monitor
  guiado por una red de Petri" (el monitor con tiempo).
- TP final: `TransitionTimer` y `PetriNet.updateTimestamps`, con la misma regla.
