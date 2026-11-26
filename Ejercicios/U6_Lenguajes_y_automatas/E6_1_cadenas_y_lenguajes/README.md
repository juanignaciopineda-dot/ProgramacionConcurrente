# Ejercicio 6.1 — Cadenas y lenguajes

> Unidad 6 · Lenguajes y autómatas · Nivel ★ · ~25 min

## Objetivo

Programar las operaciones sobre lenguajes de la teoría y ver en código las dos trampas que más se
toman en el parcial:
- **∅ no es {λ}:** el lenguaje vacío no tiene cadenas, y `{λ}` tiene una, que resulta ser la vacía.
- **L⁰ = {λ} para cualquier L:** por eso `∅* = {λ}`.

Un lenguaje es un `Set<String>` y λ es `""`. Como `V*` y `L*` son infinitos, las operaciones que los
calculan cortan en un largo máximo.

```
  V* = { λ, a, b, aa, ab, ... }       L1 L2 = { xy | x ∈ L1, y ∈ L2 }
  L⁰ = { λ }    Lⁱ = L Lⁱ⁻¹            L* = L⁰ ∪ L¹ ∪ L² ∪ ...     L⁺ = L L*
```

## Conceptos

- Símbolo, alfabeto (finito y **no vacío**), cadena, longitud y cadena vacía.
- El universo del discurso `V*`, que siempre es infinito y contiene a λ.
- Concatenación, potencia, cierre y cierre positivo.
- `{λ}` es el neutro de la concatenación y `∅` es el absorbente.
- Un lenguaje se define por las propiedades que cumplen sus cadenas (los palíndromos).

## Archivos

| Archivo            | Qué hacer                                                                                   |
| ------------------ | ------------------------------------------------------------------------------------------- |
| `Lenguajes.java`   | `universo`, `concatenacion`, `potencia`, `cierre`, `cierrePositivo`, `porPropiedad` y `esPalindromo` |
| `Verificador.java` | No se toca                                                                                  |

## Orden sugerido

1. `universo` y `concatenacion`.
2. `potencia`, apoyada en `concatenacion`.
3. `cierre` y `cierrePositivo`. Leé el Javadoc antes: hay dos casos que cuelgan o dan mal a la primera.
4. `esPalindromo` y `porPropiedad`.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

Si la consola no puede mostrar λ, ∅ o ⁰, el verificador escribe `lambda`, `{}` y `^0`.

## Para pensar

1. La actividad "Vacío, λ y cardinalidades" de los apuntes: respondé las cinco y comprobá con el código
   las que se puedan. ¿Cuánto da `Lenguajes.potencia(Set.of(), 0).size()`? ¿Y
   `Lenguajes.cierre(Set.of(), 5).size()`?
2. `{a, ab}{b, λ}` tiene tres cadenas, no cuatro. ¿Cuándo vale `|L1 L2| = |L1| · |L2|`?
3. Los apuntes escriben `L⁺ = L* − {λ}`. ¿Vale siempre? Probalo con `L = {λ, a}`. ¿Y con `V⁺`, la
   clausura positiva de un alfabeto?
4. ¿Por qué `cierre` necesita un largo máximo y `potencia` no? ¿Qué pasaría con `cierre` si `L` tuviera
   λ y cortaras "cuando las potencias se pasan del largo"?
5. ¿Cuántas cadenas tiene `universo({a, b}, n)`? Escribí la fórmula y comparala con lo que da el código.

## Pistas

> [!tip]- Pista 1: `potencia`
> Arrancá con `{λ}`, que es `L⁰`, y concatenalo con `L` tantas veces como diga el exponente:
> ```java
> Set<String> resultado = Set.of(LAMBDA);
> for (int k = 0; k < i; k++) {
>     resultado = concatenacion(l, resultado);
> }
> ```

> [!tip]- Pista 2: `cierre` como punto fijo
> ```java
> Set<String> resultado = new HashSet<>();
> resultado.add(LAMBDA);
> boolean cambio = true;
> while (cambio) {
>     cambio = false;
>     for (String x : l) {
>         for (String y : new ArrayList<>(resultado)) {
>             String xy = x + y;
>             if (xy.length() <= largoMax && resultado.add(xy)) {
>                 cambio = true;
>             }
>         }
>     }
> }
> ```
> La copia `new ArrayList<>(resultado)` es para no modificar el conjunto mientras se lo recorre.

> [!tip]- Pista 3: respuesta a la pregunta 3
> Si λ está en `L`, también está en `L¹`, así que está en `L⁺`. Entonces `L⁺ = L* − {λ}` solo vale
> cuando λ no está en `L`. Para un alfabeto siempre vale, porque un símbolo nunca es la cadena vacía.

## Conexión con la teoría y los TPs

- Apuntes: en "Gramáticas, Lenguajes y Autómatas", "Definiciones previas" (símbolo, alfabeto, cadena,
  "Universo del discurso y clausura" y "Lenguaje") y, en "Expresiones regulares", "Operaciones sobre
  lenguajes regulares".
- Parciales: "Defina qué es un lenguaje y relaciónelo con las gramáticas.", la pregunta que más se
  repite.
