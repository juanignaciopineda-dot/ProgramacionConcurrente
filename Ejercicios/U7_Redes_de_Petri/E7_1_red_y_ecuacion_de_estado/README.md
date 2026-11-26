# Ejercicio 7.1 — La red y la ecuación de estado

> Unidad 7 · Redes de Petri · Nivel ★★ · ~40 min

## Objetivo

Escribir la clase que representa una red de Petri marcada. Es la misma pieza que la `PetriNet` del
TP final, y se usa en el resto de la unidad. Una red son dos matrices y un vector:

```
  Pre[p][t]   peso del arco p → t          C = Post − Pre        (la matriz de incidencia)
  Post[p][t]  peso del arco t → p          M[p] = fichas en p    (el estado)

  t sensibilizada en M  ⇔  M(p) ≥ Pre(p, t) para todo p
  disparar t:              M' = M + C(·, t)
```

## Conceptos

- La red marcada `<P, T, Pre, Post, M0>` y la matriz de incidencia.
- Sensibilizado con `Pre`, y por qué la incidencia no alcanza cuando hay auto-lazos.
- El disparo como suma de una columna: indivisible y de duración cero.
- La ecuación fundamental `M = M0 + C·s` y el vector característico.
- Distintas secuencias con el mismo vector llegan al mismo marcado.
- Conflicto estructural: dos transiciones que comparten un lugar de entrada.
- Copias defensivas: que nadie te cambie la red desde afuera.

## Archivos

| Archivo            | Qué hacer                                                                                  |
| ------------------ | ------------------------------------------------------------------------------------------ |
| `RedDePetri.java`  | Constructor, `estaSensibilizada`, `sensibilizadas`, `disparar`, `ecuacionDeEstado` y `conflictosEstructurales` |
| `Verificador.java` | No se toca                                                                                 |

## Orden sugerido

1. El constructor: la validación ya está hecha; faltan las copias y la incidencia.
2. `estaSensibilizada`, `sensibilizadas` y `disparar`.
3. `ecuacionDeEstado`.
4. `conflictosEstructurales`.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

El verificador usa las redes de los apuntes:
- **La del disparo:** con `Pre(P1, T1) = 2`.
- **La exclusión mutua de dos procesos:** con un lugar mutex.
- **Una red con un auto-lazo:** un lugar que es entrada y salida de la misma transición.

## Para pensar

1. Pregunta de parcial: "En una red de Petri ordinaria, ¿cuál es la condición para que una
   transición esté sensibilizada?". ¿Qué cambia en una red generalizada (con pesos)?
2. El TP final decide el sensibilizado con "ningún lugar queda negativo", es decir, con `M + C(·, t)`.
   ¿Por qué ahí está bien? ¿Qué dice su Javadoc sobre los auto-lazos? ¿Qué caso de este verificador
   fallaría con esa regla?
3. `ecuacionDeEstado(0, 1, 0, 0)` da un marcado con un −1. ¿Qué significa? ¿Por qué la ecuación
   fundamental es una condición necesaria pero no suficiente para que un marcado sea alcanzable?
4. Pregunta de parcial: "Explique la diferencia entre conflicto estructural y conflicto efectivo". En
   la exclusión mutua, ¿en qué marcado el conflicto entre T0 y T2 es efectivo? ¿Hay alguno en el que
   no lo sea?
5. ¿Qué representa el marcado de una plaza? Respondelo para el lugar mutex y para "A afuera".

## Pistas

> [!tip]- Pista 1: `estaSensibilizada`
> ```java
> validarTransicion(t);
> for (int p = 0; p < marcado.length; p++) {
>     if (marcado[p] < pre[p][t]) {
>         return false;
>     }
> }
> return true;
> ```

> [!tip]- Pista 2: `ecuacionDeEstado`
> ```java
> int[] resultado = marcado.clone();
> for (int p = 0; p < marcado.length; p++) {
>     for (int t = 0; t < s.length; t++) {
>         resultado[p] += incidencia[p][t] * s[t];
>     }
> }
> return resultado;
> ```

> [!tip]- Pista 3: respuesta a la pregunta 2
> La red del TP final no tiene auto-lazos, y su Javadoc lo dice ("The incidence matrix is enough
> because the net has no self-loops"). En una red pura, cada lugar es entrada **o** salida de una
> transición, nunca las dos cosas. Entonces `C(p, t) = −Pre(p, t)` en las entradas, y "no queda
> negativo" es lo mismo que `M(p) ≥ Pre(p, t)`. Con un auto-lazo, `C(p, t) = 0` y la regla deja
> disparar con el lugar vacío. Es el caso "Auto-lazo" del verificador.

## Conexión con la teoría y los TPs

- Apuntes: "Definición y elementos" (la definición formal y `C = Post − Pre`), "Sensibilización y
  disparo", "Ecuación fundamental e invariantes de marcado" y "Conflictos: estructural, efectivo y
  general".
- TP final: `PetriNet.isEnabled`, `fire` y `getConflicts`.
