# Ejercicio 6.3 — Autómata finito determinista

> Unidad 6 · Lenguajes y autómatas · Nivel ★★ · ~35 min

## Objetivo

Un autómata **reconoce** un lenguaje. El ejercicio tiene dos partes:
- **El motor del AFD:** las transiciones, el recorrido de una cadena, aceptar o rechazar, y los estados
  accesibles.
- **El diseño:** tres autómatas de los ejercicios de los apuntes. El verificador los prueba con todas
  las cadenas hasta cierto largo y te muestra el primer contraejemplo.

La idea que hace falta para diseñar es la definición de estado de los apuntes: el estado es toda la
información necesaria para decidir lo que viene. No se guarda la historia: se guarda un resumen.

```
  AF = (E, Q, f, q1, F)        f(e0, b) = e1                    INICIO ──► (e0) ──b──► (e1)
  acepta w  ⇔  leyó todo w y terminó en un estado de F
```

## Conceptos

- La quíntupla del autómata finito: sin vocabulario de salida, la respuesta es sí o no.
- `f` como función: para cada (estado, símbolo), un solo destino. Si no hay ninguno, el autómata se
  traba y rechaza (el estado trampa que no hace falta dibujar).
- El recorrido de una cadena y la tabla de transiciones.
- Estado accesible, autómata conexo y equivalencia al borrar los inaccesibles.
- Diseñar un autómata pensando qué resume cada estado.

## Archivos

| Archivo            | Qué hacer                                                                          |
| ------------------ | ---------------------------------------------------------------------------------- |
| `AFD.java`         | `agregarTransicion`, `siguiente`, `recorrido`, `acepta`, `accesibles` y `esConexo` |
| `Automatas.java`   | `parDeB`, `contiene00o11` y `multiploDe3`. El de L3 de los apuntes ya está hecho: usalo de modelo |
| `Verificador.java` | No se toca                                                                         |

## Orden sugerido

1. `agregarTransicion` y `siguiente`.
2. `recorrido` y `acepta`. Con eso pasan los casos de L3, que usan el autómata ya hecho.
3. `accesibles` y `esConexo`.
4. Los tres diseños. **Dibujalos antes de escribirlos.**

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

Si un diseño falla, el verificador muestra la cadena que lo rompe, el recorrido y la tabla de tu
autómata.

## Para pensar

1. Pregunta de parcial: "¿Con un autómata finito puede derivar la siguiente fórmula? ¿Por qué? { a^n
   b^n : n ≥ 1 }". ¿Por qué el truco del resto que usaste en `multiploDe3` no sirve para contar las
   `a`?
2. `multiploDe3` necesita cuatro estados aunque los restos sean tres. ¿Qué cambia si se acepta λ como
   el número 0? ¿Cuántos estados haría falta para los múltiplos de 5?
3. En `contiene00o11`, ¿qué estados se pueden juntar sin cambiar el lenguaje? ¿Hay un autómata con
   menos estados que el tuyo?
4. El autómata producto de los apuntes ("Enganches con el resto de la materia"): dos procesos de 10
   estados dan 100 estados, y cinco dan 100.000. ¿Cuántos estados tendría el producto de tres copias de
   tu `parDeB`? ¿Y cuántas plazas una red de Petri que modele lo mismo?
5. Borrar los estados inaccesibles da un autómata equivalente. ¿Pasa lo mismo si borrás un estado del
   que no se llega a ningún final?

## Pistas

> [!tip]- Pista 1: `recorrido`
> ```java
> List<String> resultado = new ArrayList<>();
> String actual = inicial;
> resultado.add(actual);
> for (char simbolo : cadena.toCharArray()) {
>     actual = siguiente(actual, simbolo);
>     if (actual == null) {
>         break;
>     }
>     resultado.add(actual);
> }
> return resultado;
> ```
> Con eso, `acepta` es: el recorrido tiene `cadena.length() + 1` estados y el último es final.

> [!tip]- Pista 2: qué recuerda cada diseño
> - **`parDeB`:** si la cantidad de `b` que van es par o impar.
> - **`contiene00o11`:** el último símbolo, mientras no aparezca ningún par; después, que ya apareció.
> - **`multiploDe3`:** un estado de arranque (todavía no se leyó nada) y uno por cada resto.

> [!tip]- Pista 3: respuesta a la pregunta 1
> No. Para aceptar `aⁿbⁿ` hay que recordar cuántas `a` entraron, y ese número no tiene cota. Un
> autómata finito tiene tantos estados como tiene, así que con `n` más grande que la cantidad de estados
> hay dos cantidades distintas de `a` que terminan en el mismo estado, y el autómata ya no las puede
> distinguir. El resto módulo 3 sí tiene cota: siempre es 0, 1 o 2.

## Conexión con la teoría y los TPs

- Apuntes: en "Gramáticas, Lenguajes y Autómatas", "Autómatas" ("La idea de estado" y "Accesibilidad,
  conexión y determinismo"), "Autómatas finitos" y "Ejercicios Propuestos" (el 1.a y el 2.a); para la
  pregunta 4, "Enganches con el resto de la materia".
- Parciales: "¿Con un autómata finito puede derivar la siguiente fórmula? ¿Por qué? { a^n b^n : n ≥
  1 }" y "¿Con qué tipo de autómata puede derivar la siguiente fórmula? { a^n b^m : n ≥ 1, m ≥ 0 }".
- Unidad 7: el grafo de alcanzabilidad del 7.3 es el mismo recorrido en anchura que `accesibles`, con
  marcados en lugar de estados.
