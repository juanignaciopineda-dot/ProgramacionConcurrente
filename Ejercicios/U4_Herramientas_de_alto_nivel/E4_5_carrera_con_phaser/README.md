# Ejercicio 4.5 — Carrera por etapas con `Phaser`

> Unidad 4 · Sincronizadores · Nivel ★★★ · ~45 min

## Objetivo

Una carrera de varias etapas en la que nadie empieza la siguiente hasta que todos terminaron la
actual, y en la que un corredor puede abandonar sin dejar colgados a los demás. Hace falta una
barrera que se repita, una vez por etapa, y que admita participantes que se van: un `Phaser`.

```
  fase 0:  c0 ████      c1 ██████    c2 ███     c3 █████   │ onAdvance(0, 4)
  fase 1:  c0 ██        c1 ████      c2 █████   c3 ██      │ onAdvance(1, 4)
  fase 2:  c0 ███       c1 ██        (c2 abandona)   c3 ████   │ onAdvance(2, 3)
  ...
```

## Conceptos

- `new Phaser(n)`: con cuántos participantes se inicializa (la pregunta del parcial).
- `arriveAndAwaitAdvance()`: la barrera de cada fase.
- `arriveAndDeregister()`: llegar e irse; desde la fase siguiente se espera a uno menos.
- `onAdvance(fase, registrados)`: lo ejecuta el último en llegar, y si devuelve `true` el phaser
  termina.
- Por qué `CountDownLatch` (de un solo uso) y `CyclicBarrier` (no deja irse a nadie) no alcanzan.
- Una clase anónima que redefine un método.

## Archivos

| Archivo            | Qué hacer                                                                               |
| ------------------ | --------------------------------------------------------------------------------------- |
| `Carrera.java`     | Constructor (con `onAdvance`), `llegarAlFinDeEtapa`, `abandonar` y las 3 consultas      |
| `Corredor.java`    | `run()`                                                                                 |
| `Verificador.java` | No se toca                                                                              |

## Orden sugerido

1. El constructor de `Carrera`, siguiendo el Javadoc, y las tres consultas.
2. `abandonar` y `llegarAlFinDeEtapa`.
3. `Corredor.run`.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

## Restricciones

`Phaser`. Nada de `CyclicBarrier`, `CountDownLatch`, semáforos, `join()` ni `wait`/`notify` (el
verificador lo controla).

## Para pensar

1. **La pregunta del parcial 2023:** ¿con qué valor debe ser inicializada la primitiva `Phaser`? ¿Qué
   otra manera hay de registrar participantes? (Mirá `register()` y `bulkRegister()`.)
2. Cambiá `abandonar` para que solo anote, sin `arriveAndDeregister()`. ¿En qué etapa se quedan los
   demás y por qué? ¿Por qué el verificador no puede decirte "quién espera a quién"?
3. Creá el phaser con `corredores - 1`. ¿Qué pasa? ¿Algún corredor queda afuera, o la barrera se
   abre antes de tiempo?
4. Si no redefinís `onAdvance`, ¿cuándo termina el phaser? ¿Qué pasa con los corredores después de
   la última etapa?
5. Hacé la misma carrera con un `CyclicBarrier`, sin abandonos. ¿Qué hace el `Runnable` que se le
   pasa al constructor de la barrera, y a qué parte de este ejercicio se parece? ¿Por qué no podés
   implementar el abandono?

## Pistas

> [!tip]- Pista 1: el constructor
> ```java
> this.etapas = etapas;
> this.fases = new Phaser(corredores) {
>     @Override
>     protected boolean onAdvance(int fase, int registrados) {
>         anotar("fin de la etapa " + fase + ": siguen " + registrados);
>         return fase == etapas - 1 || registrados == 0;
>     }
> };
> ```

> [!tip]- Pista 2: `Corredor.run`
> ```java
> for (int etapa = 0; etapa < carrera.getEtapas(); etapa++) {
>     if (etapa == abandonaEn) {
>         carrera.abandonar(nombre, etapa);
>         return;
>     }
>     carrera.correrEtapa(nombre, etapa);
>     carrera.llegarAlFinDeEtapa();
> }
> ```

> [!tip]- Pista 3: respuesta a la pregunta 1
> Con la **cantidad de participantes** que se sincronizan en cada fase: `new Phaser(n)`. La fase
> avanza cuando llegaron todos los registrados. También se puede crear con 0 y registrar después con
> `register()` (de a uno) o `bulkRegister(n)`.

## Conexión con la teoría y los TPs

- No está en los apuntes ni en las filminas, pero salió en el segundo parcial de 2023 (en
  "Preguntas de parciales", sección "Sincronizadores y Executors").
- Redes de Petri: cada fin de etapa es un **co-end** seguido de un **co-begin**. La transición
  "avanzar" necesita un token de cada corredor y devuelve uno a cada uno.
