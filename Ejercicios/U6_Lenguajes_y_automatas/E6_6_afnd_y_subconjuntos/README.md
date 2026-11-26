# Ejercicio 6.6 — AFND, construcción de subconjuntos y gramáticas regulares

> Unidad 6 · Lenguajes y autómatas · Nivel ★★★★ · ~60 min

## Objetivo

Cerrar el triángulo de los lenguajes regulares: gramática de tipo 3, AFND y AFD.

```
  gramática tipo 3 ──deGramatica──► AFND ──aAFD (subconjuntos)──► AFD
         ▲                                                        │
         └──────────── a mano, con el algoritmo AF → G3 ──────────┘
```

Un AFND está en varios estados a la vez, y simularlo es seguir todos los caminos. La construcción de
subconjuntos convierte esa simulación en un autómata determinista: cada estado del AFD es un conjunto
de estados del AFND. El camino inverso, del autómata a la gramática, lo hacés a mano, y el verificador
comprueba tu resultado.

## Conceptos

- `f: E × Q → P(Q)`: un conjunto de destinos, varios estados iniciales y transiciones sobre λ.
- La clausura-λ y `mover`, las dos piezas de la simulación.
- La construcción de subconjuntos, que solo construye los conjuntos alcanzables.
- La explosión: un AFND de `n + 1` estados puede necesitar un AFD de `2ⁿ`.
- El algoritmo gramática de tipo 3 → autómata finito, y su inverso, con los casos borde.
- `{L regulares} = {L(AF)} = {L(G3)}`.

## Archivos

| Archivo            | Qué hacer                                                                       |
| ------------------ | ------------------------------------------------------------------------------- |
| `AFND.java`        | `clausuraLambda`, `mover`, `acepta`, `aAFD` y `deGramatica`                     |
| `ALaMano.java`     | Las producciones de las gramáticas de L3 y L4, obtenidas a mano                 |
| `AFD.java`         | Ya está hecho (es el del 6.3)                                                   |
| `Gramatica.java`   | Ya está hecha (es la del 6.2)                                                   |
| `Verificador.java` | No se toca                                                                      |

## Orden sugerido

1. `clausuraLambda` y `mover`.
2. `acepta`: la simulación en varios estados a la vez.
3. `aAFD`. Hacé antes a mano la tabla del AFND de la figura de los apuntes (el de las cadenas
   terminadas en `ab`), y comparala con lo que da tu código.
4. `deGramatica`.
5. `ALaMano`: aplicá en papel el algoritmo AF → gramática regular a los autómatas de L3 y L4 del 6.3, y
   copiá las producciones.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

Para `ALaMano`, el verificador revisa tres cosas:
- **El tipo:** la gramática tiene que ser de tipo 3.
- **La forma estricta:** λ solo en `S → λ`, y `S` nunca a la derecha.
- **El lenguaje:** compara lo que genera, con el `generar` del 6.2, contra la definición del lenguaje.

## Para pensar

1. Pregunta de parcial: "¿Cuál es la principal característica de un autómata finito NO
   determinístico?". ¿Dónde aparece eso en tu código?
2. El ejercicio 5.b de los apuntes: ¿cuántos subconjuntos de `Q` hay en total y cuántos aparecieron en
   la construcción? Explicá la diferencia.
3. Con `n = 5`, el AFND de "la quinta desde el final es `a`" tiene 6 estados y el AFD, 32. ¿Por qué el
   AFD no puede tener menos? Pensá en qué tiene que recordar de las últimas cinco letras.
4. La gramática del ejercicio 3 tiene `B → λ`, y `deGramatica` la resuelve haciendo final a `B`. ¿Qué
   otra forma había, sin tocar los finales? (El AFND admite transiciones sobre λ.) En el camino inverso,
   el ejemplo resuelto de los apuntes muestra que JFLAP usa `X → λ` para los estados finales: ¿es la
   misma idea?
5. En L3, al estado inicial le llegan arcos y además es final. ¿Qué pasa si te olvidás de darle su
   propio no terminal y usás `S` a la derecha? ¿El lenguaje cambia, o solo la forma?
6. El no determinismo del AFND es el mismo que el del planificador de hilos ("Enganches con el resto de
   la materia"). ¿Qué tiene en común `acepta`, que sigue todos los caminos, con verificar un programa
   concurrente?

## Pistas

> [!tip]- Pista 1: `acepta`
> ```java
> Set<String> actuales = clausuraLambda(iniciales);
> for (char c : cadena.toCharArray()) {
>     actuales = clausuraLambda(mover(actuales, c));
> }
> return !Collections.disjoint(actuales, finales);
> ```

> [!tip]- Pista 2: las dos pasadas de `aAFD`
> En la primera, el recorrido: una cola de conjuntos, una lista de los vistos y una lista de arcos
> `{nombre(X), c, nombre(Y)}`. En la segunda, con todos los conjuntos ya conocidos, se calculan los
> finales, se crea el `AFD` y se le agregan los arcos.

> [!tip]- Pista 3: los no terminales de L3
> `e0` es el inicial, le llegan arcos (desde `e0` mismo y desde `e1`) y es final, así que le
> corresponden `S` y además un no terminal propio, por ejemplo `A`. `e4` es final pero salen arcos, así
> que también lleva no terminal. Los arcos que salen de `e0` dan producciones para `S` y para `A`, y
> `S → λ` porque el inicial es final.

## Conexión con la teoría y los TPs

- Apuntes: en "Gramáticas, Lenguajes y Autómatas", dentro de "Autómatas finitos", "AFND: autómatas
  finitos no deterministas", "Teorema de transformación AFND → AFD", "Algoritmo: gramática de tipo 3 →
  autómata finito" y "Algoritmo: autómata finito → gramática regular"; "Ejercicios Propuestos" (el 1.c,
  el 2.b, el 3 y el 5); y "Enganches con el resto de la materia".
- Parciales: "¿Cuál es la principal característica de un autómata finito NO determinístico?" y
  "Defina qué es un autómata y relaciónelo con las gramáticas."
