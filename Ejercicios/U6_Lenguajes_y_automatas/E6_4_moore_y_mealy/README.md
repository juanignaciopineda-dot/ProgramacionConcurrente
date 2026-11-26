# Ejercicio 6.4 — Máquinas de Moore y Mealy, y un sistema reactivo

> Unidad 6 · Lenguajes y autómatas · Nivel ★★★ · ~45 min

## Objetivo

Los autómatas con salida, en tres pasos:
1. **Las dos máquinas:** traducir una entrada con Mealy (la salida va en el arco) y con Moore (la
   salida va en el estado).
2. **El ejercicio 9 de los apuntes:** el detector de `101` con solapamiento, con las dos máquinas, y la
   conversión de Mealy a Moore. Muestra por qué Moore tiende a tener más estados.
3. **El sistema reactivo:** la máquina de Mealy corriendo en su propio hilo. El entorno le manda
   eventos por una cola, al ritmo que quiere, y ella reacciona a cada uno apenas llega. Así se usa un
   autómata en un sistema concurrente.

```
  entrada      1 0 1 0 1
  Mealy        0 0 1 0 1        g: E × Q → S    (n símbolos de salida)
  Moore      0 0 0 1 0 1        g: Q → S        (n + 1: la primera es la del estado inicial)
```

## Conceptos

- La 6-upla `(E, S, Q, q0, f, g)`: la única diferencia entre las dos está en `g`.
- Moore: a cada estado una salida y sólo una.
- Mealy reacciona en el mismo paso; Moore, un paso después.
- Mealy tiende a tener menos estados: un estado de Mealy al que se llega con salidas distintas se parte
  en Moore.
- Sistema reactivo y arquitectura dirigida por eventos: el entorno marca el ritmo, y el consumidor
  reacciona tan pronto como el evento está presente.
- `BlockingQueue.take()` para esperar sin consumir procesador, y la interrupción para terminar.

## Archivos

| Archivo                 | Qué hacer                                                         |
| ----------------------- | ----------------------------------------------------------------- |
| `MaquinaMealy.java`     | `traducir`. El resto ya está hecho                                |
| `MaquinaMoore.java`     | `definirSalida` y `traducir`                                      |
| `Detectores.java`       | `mealy101` y `moore101`: los diseños                              |
| `Conversion.java`       | `mealyAMoore`                                                     |
| `DetectorReactivo.java` | `run()`                                                           |
| `Verificador.java`      | No se toca                                                        |

## Orden sugerido

1. `MaquinaMealy.traducir`, y en Moore `definirSalida` y `traducir`.
2. Dibujá los dos detectores en papel, con solapamiento, y después escribilos.
3. `Conversion.mealyAMoore`.
4. `DetectorReactivo.run`. Los casos reactivos usan un detector de referencia, así que no dependen de
   tu diseño.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

Los casos reactivos hacen tres cosas:
- **Bloqueo:** sin eventos, miran que el detector esté bloqueado en `take()` (`WAITING`) y no
  preguntando en un bucle.
- **Reacción:** mandan los eventos de a uno y esperan cada salida antes de mandar el siguiente.
- **Ráfaga e interrupción:** mandan 5000 eventos de golpe, y después lo interrumpen mientras espera.

## Restricciones

`DetectorReactivo` solo usa los métodos de `BlockingQueue` (`take` y `put`). Nada de `sleep`,
`synchronized`, `poll` ni `peek` (el verificador lo controla).

## Para pensar

1. Preguntas de parcial: "¿Qué máquina tiende a tener menos estados para reconocer una misma fórmula,
   MEALY o MOORE? ¿Por qué?" y "¿Qué máquina es más segura de utilizar, MEALY o MOORE? ¿Por qué?".
   Respondelas con tus dos detectores y con lo que dio la conversión.
2. El ejercicio 9.b de los apuntes: ¿en qué instante emite cada máquina la salida respecto del último
   símbolo leído? Ubicalo en la tabla de arriba.
3. Los apuntes explican por qué no se puede extender la `g` de Mealy a la entrada vacía λ. ¿Qué tendría
   que emitir `DetectorReactivo` si no llega ningún evento? ¿Qué emitiría una máquina de Moore?
4. "Cambió el bit" tiene 2 estados en Mealy y 4 en Moore, según el verificador. ¿Hay una máquina de
   Moore con menos estados que haga lo mismo?
5. ¿Por qué el detector no puede preguntar `eventos.isEmpty()` en un bucle? Pensá en el procesador y
   en lo que pasa si el entorno tarda una hora en mandar el próximo evento.
6. El detector reacciona a cada evento, pero ¿qué pasa si el entorno manda eventos más rápido de lo que
   el detector los procesa? ¿Dónde se acumulan? ¿Qué cambiaría con una cola acotada?

## Pistas

> [!tip]- Pista 1: el detector de Mealy
> Tres estados: nada visto, visto `1` y visto `10`. Desde visto `10`, el `1` emite `1` y vuelve a visto
> `1`: ese último `1` puede ser el comienzo de la próxima secuencia.

> [!tip]- Pista 2: el detector de Moore
> Hace falta un cuarto estado, visto `101`, que emite `1`. Desde ahí, con `0` se pasa a visto `10` (el
> solapamiento), y con `1`, a visto `1`.

> [!tip]- Pista 3: el ciclo de `run()`
> ```java
> String estado = maquina.inicial();
> try {
>     while (true) {
>         char e = eventos.take();
>         if (e == FIN) {
>             return;
>         }
>         MaquinaMealy.Transicion t = maquina.transicion(estado, e);
>         if (t != null) {
>             salidas.put(t.salida());
>             estado = t.hacia();
>         }
>     }
> } catch (InterruptedException ex) {
>     Thread.currentThread().interrupt();
> }
> ```

## Conexión con la teoría y los TPs

- Apuntes: en "Gramáticas, Lenguajes y Autómatas", "Autómatas" ("Definición formal" y "Moore y Mealy"),
  "Motivación: por qué este tema está en Programación Concurrente" (sistemas reactivos y "Arquitectura
  dirigida por eventos (EDA)") y "Ejercicios Propuestos" (el 9).
- Parciales: "En una máquina de Moore, ¿con qué está relacionado el símbolo de salida?", "¿Qué máquina
  tiende a tener menos estados para reconocer una misma fórmula, MEALY o MOORE? ¿Por qué?" y "¿Qué
  máquina es más segura de utilizar, MEALY o MOORE? ¿Por qué?".
- Unidades anteriores: la interrupción (1.4) y las colas bloqueantes del productor/consumidor.
