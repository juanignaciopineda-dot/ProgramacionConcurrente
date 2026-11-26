# Ejercicio 6.2 — Gramáticas y la jerarquía de Chomsky

> Unidad 6 · Lenguajes y autómatas · Nivel ★★ · ~40 min

## Objetivo

Una gramática **genera** un lenguaje. Este ejercicio la lleva a código:
- **Clasificarla** en la jerarquía de Chomsky mirando la forma de sus producciones.
- **Derivar:** aplicar una producción en una forma sentencial.
- **Generar** todas las cadenas del lenguaje hasta un largo, con un recorrido en anchura de las
  formas sentenciales.

```
  G = (V_T, V_N, S, P)          S → aS | aB     B → bB | b

  S ⇒ aS ⇒ aaB ⇒ aabB ⇒ aabb    L(G) = { aⁿbᵐ | n, m ≥ 1 }
```

## Conceptos

- La cuádrupla `(V_T, V_N, S, P)`: terminales en minúscula, no terminales en mayúscula.
- Derivación directa (`⇒`) y su cierre (`⇒*`), y el lenguaje generado `L(G)`.
- Los cuatro tipos de Chomsky, que se distinguen por la forma de las reglas.
- El tipo de la **gramática** no es el del **lenguaje**: `S → aS | Sb | λ` es de tipo 2, pero genera
  `a*b*`, que es regular.
- Los testigos clásicos: `aⁿbⁿ` (tipo 2, no regular) y `aⁿbⁿcⁿ` (tipo 1, no libre de contexto).

## Archivos

| Archivo            | Qué hacer                                                         |
| ------------------ | ----------------------------------------------------------------- |
| `Gramatica.java`   | `tipo`, `derivacionesDirectas`, `esDerivacion` y `generar`. La lectura de las producciones ya está hecha |
| `Verificador.java` | No se toca                                                        |

Las producciones se escriben como texto: `new Gramatica('S', "S -> aS | aB", "B -> bB | b")`, con
`λ` para la derecha vacía.

## Orden sugerido

1. `tipo`: los cuatro primeros casos del verificador son la actividad "Clasificar gramáticas".
2. `derivacionesDirectas`: ojo con las partes izquierdas que aparecen más de una vez.
3. `esDerivacion`, apoyada en `derivacionesDirectas`.
4. `generar`, siguiendo el pseudocódigo del Javadoc.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

El último caso genera `aⁿbⁿcⁿ` hasta largo 9 con la gramática sensible al contexto clásica. Si tarda
más de unos segundos, revisá que no estés metiendo en la cola formas repetidas.

## Para pensar

1. Preguntas de parcial: "¿Con qué tipo de gramática puede derivar la siguiente fórmula? { a^n b^n : n
   ≥ 1 }" y "Dé un fundamento de por qué una gramática de tipo 2 tiene menos capacidad de expresión que
   una de tipo 1". Respondelas con las gramáticas del verificador.
2. `S → Ba` y `S → ab` dan tipo 2, aunque generen lenguajes regulares. Escribí, para cada una, una
   gramática de tipo 3 que genere el mismo lenguaje.
3. En la gramática de `aⁿbⁿcⁿ`, la regla `CB → BC` reordena los no terminales. ¿Qué pasa si una
   derivación aplica `bC → bc` antes de terminar de ordenar? ¿Se llega a una cadena de terminales?
4. `generar` poda las formas con más de `largoMax` terminales. ¿Por qué esa poda es exacta en las
   gramáticas de tipo 1, 2 y 3? Inventá una gramática de tipo 0 en la que no lo sea.
5. Con `S → AAAAAAAAa` y `A → λ`, ¿qué devuelve `generar(1)`? ¿Por qué la cota de largo
   `2·largoMax + 2` no es una garantía para las gramáticas con λ? ¿Qué tiene que ver con que la
   máquina de Turing pueda no pararse?

## Pistas

> [!tip]- Pista 1: `tipo`
> Probá del 3 para abajo y devolvé el primero que cumplan **todas** las producciones:
> ```java
> if (producciones.stream().allMatch(Gramatica::esRegular)) {   // A → aB, A → a o A → λ
>     return 3;
> }
> if (producciones.stream().allMatch(p -> p.izquierda().length() == 1)) {
>     return 2;
> }
> ```
> Para el tipo 1, la condición es `|izq| <= |der|`, salvo la producción `S → λ` del inicial.

> [!tip]- Pista 2: todas las apariciones en `derivacionesDirectas`
> ```java
> for (int i = forma.indexOf(izq); i >= 0; i = forma.indexOf(izq, i + 1)) {
>     resultado.add(forma.substring(0, i) + p.derecha() + forma.substring(i + izq.length()));
> }
> ```

> [!tip]- Pista 3: respuesta a la pregunta 4
> En las gramáticas de tipo 1, 2 y 3, una producción nunca borra terminales: la parte izquierda tiene
> un no terminal, y lo que se reemplaza se reemplaza entero. Entonces los terminales de una forma
> sentencial quedan hasta el final, y una forma con demasiados no puede dar una cadena corta. En una de
> tipo 0, como `aSb → c`, sí se pueden borrar.

## Conexión con la teoría y los TPs

- Apuntes: en "Gramáticas, Lenguajes y Autómatas", "Definición formal de gramática" (con "Derivación y
  lenguaje generado") y "Jerarquía de Chomsky" (con "Correspondencia entre gramáticas y lenguajes" y
  la actividad "Clasificar gramáticas").
- Parciales: "Defina qué es una gramática y relacione la regla de producción con un autómata." y
  "Defina qué es un lenguaje del tipo 1 y relaciónelo con la gramática correspondiente."
