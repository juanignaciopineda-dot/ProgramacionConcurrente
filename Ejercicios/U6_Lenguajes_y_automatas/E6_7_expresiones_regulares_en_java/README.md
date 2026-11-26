# Ejercicio 6.7 — Expresiones regulares en Java

> Unidad 6 · Lenguajes y autómatas · Nivel ★★★ · ~45 min

## Objetivo

Las expresiones regulares de la teoría (y de JFLAP), escritas y usadas en Java con `Pattern` y
`Matcher`. Es el paso que falta para el 7.6, donde se verifica el log de una ejecución con una
expresión regular. El ejercicio tiene tres partes:
- **Escribir expresiones:** las de la actividad "Escribir y comparar expresiones regulares" y la de L3,
  en notación de Java. El verificador las compara con la definición de cada lenguaje.
- **Traducir y comparar:** pasar una expresión de JFLAP a Java, y encontrar la cadena que separa dos
  expresiones distintas.
- **Buscar en un texto** con `find()`: la diferencia entre codicioso y perezoso, y los disparos de un
  log.

```
  JFLAP      Java          matches(): ¿la cadena ENTERA está en el lenguaje?   ← la teoría
  a + b      a|b           find():    ¿hay un PEDAZO que esté?                  ← buscar en un log
  !          (?:)
  aa*        a+            <.*>  sobre <a>b<c>  →  <a>b<c>      (codicioso)
                           <.*?> sobre <a>b<c>  →  <a>, <c>     (perezoso)
```

## Conceptos

- Una expresión regular describe un lenguaje regular: es un metalenguaje.
- Las dos notaciones: en JFLAP `+` es la unión, en Java es "una o más veces".
- Los metacaracteres de Java (`[ ]`, `.`, `?`, `{ }`, `\`...) y cómo escaparlos.
- La igualdad de expresiones: designan el mismo conjunto. Para mostrar que dos **no** son iguales
  alcanza con una cadena.
- `matches()` contra `find()`: pertenencia contra búsqueda.
- Cuantificadores codiciosos y perezosos.

## Archivos

| Archivo                     | Qué hacer                                                                          |
| --------------------------- | ---------------------------------------------------------------------------------- |
| `ExpresionesRegulares.java` | Las seis constantes, `deJflap`, `primeraDiferencia` y `buscarTodas`                |
| `Verificador.java`          | No se toca                                                                         |

## Orden sugerido

1. Las cuatro primeras constantes. Si una falla, el verificador te da la cadena que la rompe.
2. `primeraDiferencia`: con ella podés resolver la parte d) de la actividad.
3. `deJflap`.
4. `buscarTodas` y las dos últimas constantes, `ETIQUETA` y `TRANSICION`.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

Ojo con las barras: en un literal de Java, `"T\\d+"` es la expresión `T\d+`.

## Para pensar

1. La actividad d): ¿es cierto que `(a+b)* = a*b*`? `primeraDiferencia` da `ba`. ¿Y
   `(a+b)* = (a*b*)*`? ¿Por qué el código no alcanza para demostrar que dos expresiones son iguales?
2. Para EMPIEZA_Y_TERMINA_IGUAL se puede escribir `([ab])([ab]*\1)?`, con una referencia hacia atrás.
   El verificador la acepta, pero ¿es una expresión regular de la teoría? Buscá un lenguaje que se
   pueda describir con `\1` y que no sea regular.
3. `<[^>]*>` también encuentra cada etiqueta por separado. ¿En qué se diferencia de `<.*?>`? ¿Cuál
   prefieren los apuntes para los huecos entre transiciones de un log, y por qué?
4. Con `T\d` (sin el `+`), ¿qué encuentra `buscarTodas` en `T1T10T1`? ¿Por qué en el 7.6 hay que tener
   cuidado de que `T1` no coincida con el comienzo de `T10`?
5. JFLAP y Java tienen la misma precedencia, así que `deJflap` no agrega paréntesis. ¿Qué habría que
   cambiar si se tradujera a una notación en la que la unión tuviera más precedencia que la
   concatenación?
6. ¿Qué autómata construye `Pattern.compile`? Buscá qué dicen los apuntes sobre el método del GTG, y
   pensá por qué Java prefiere probar caminos con vuelta atrás antes que armar el AFD de subconjuntos
   del 6.6.

## Pistas

> [!tip]- Pista 1: PAR_DE_B y L3
> Para PAR_DE_B, pensá en "cualquier cantidad de `a`, y después grupos de dos `b` con `a` en el medio y
> después". L3 es una `x` con par de `b` seguida de grupos de tres `c`: `(a|ba*b)*` es otra forma de
> escribir la primera parte.

> [!tip]- Pista 2: `deJflap`
> ```java
> switch (c) {
>     case '+' -> java.append('|');
>     case '!' -> java.append("(?:)");
>     case '*', '(', ')' -> java.append(c);
>     default -> {
>         if (!Character.isLetterOrDigit(c)) {
>             java.append('\\');
>         }
>         java.append(c);
>     }
> }
> ```

> [!tip]- Pista 3: respuesta a la pregunta 4
> `T\d` toma un solo dígito: en `T1T10T1` encuentra `T1`, `T1` y `T1`, y el `0` queda afuera. Con
> `T\d+`, el `+` es codicioso y se lleva todos los dígitos, así que encuentra `T10` entero. En el 7.6 el
> problema es el inverso: ahí se busca un `T1` concreto, y hay que evitar que coincida con el comienzo
> de `T10`.

## Conexión con la teoría y los TPs

- Apuntes: en el teórico, "Expresiones regulares" ("Operaciones sobre expresiones regulares",
  "Igualdad y propiedades" y la actividad "Escribir y comparar expresiones regulares"), "Expresiones
  regulares ↔ autómatas finitos" ("JFLAP en la práctica") y "Ejercicios Propuestos" (el 1.b y el 4); en
  el práctico, "Expresiones regulares en la práctica: verificar T-invariantes" ("Expresiones regulares
  como herramienta", con los cuantificadores y "Codicioso vs perezoso").
- TP final: `InvariantAnalyzer`, que encuentra los disparos del log con el patrón `T\d+` y los
  T-invariantes con una sola expresión de huecos perezosos.
- Unidad 7: el 7.6 usa todo esto para verificar los T-invariantes de un log.
