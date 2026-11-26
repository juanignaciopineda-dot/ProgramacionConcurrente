# Ejercicio 6.5 — Autómata de pila

> Unidad 6 · Lenguajes y autómatas · Nivel ★★★ · ~40 min

## Objetivo

Un autómata finito no puede reconocer `aⁿbⁿ` porque no tiene dónde contar. El autómata de pila sí,
porque es un autómata finito con una pila como memoria. Este ejercicio programa:
- **El motor:** las transiciones, con el control de determinismo de los apuntes.
- **El movimiento y la traza de configuraciones `(q, W, α)`:** es la actividad "Traza de
  configuraciones".
- **La aceptación por estado final.**

Después diseñás dos autómatas:
- **`aⁿb²ⁿ`:** el ejercicio 6.b de los apuntes.
- **Las trazas de `lock()` y `unlock()` de un hilo:** un lenguaje de la materia que tampoco es regular.

```
  (q0, aabb, Z) ⊢ (q1, abb, aZ) ⊢ (q1, bb, aaZ) ⊢ (q2, b, aZ) ⊢ (q2, λ, Z) ⊢ (q3, λ, Z)   acepta
     estado, lo que falta leer, la pila (tope a la izquierda)
```

## Conceptos

- La 7-upla `(Q, Σ, Γ, δ, q0, Z, F)` y la notación de arco `entrada, tope ; lo que apilo`.
- Transiciones que dependen de la entrada y transiciones sobre λ, que no mueven la cinta.
- Las dos situaciones que hacen no determinista a un autómata de pila.
- Configuración, movimiento `⊢` y traza.
- Si la pila se vacía, el autómata se para.
- Aceptación por estado final: entrada toda leída **y** estado final.
- La pila como contador no acotado: lo que le falta al autómata finito.

## Archivos

| Archivo                | Qué hacer                                                         |
| ---------------------- | ----------------------------------------------------------------- |
| `AutomataDePila.java`  | `agregarTransicion`, `paso`, `traza` y `acepta`                   |
| `AutomatasDePila.java` | `anb2n` y `lockBienUsado`. El de `aⁿbⁿ` de los apuntes ya está hecho |
| `Verificador.java`     | No se toca                                                        |

## Orden sugerido

1. `agregarTransicion`, con los dos controles de determinismo.
2. `paso`, siguiendo el pseudocódigo del Javadoc.
3. `traza` (con el límite de movimientos) y `acepta`.
4. Los dos diseños.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

Antes de correrlo, hacé a mano la actividad "Traza de configuraciones" de los apuntes: el cuarto caso
del verificador es su respuesta.

## Para pensar

1. Preguntas de parcial: "¿Con un autómata a pila puede derivar la siguiente fórmula? ¿Por qué? { a^n
   b^n : n ≥ 1 }" y "¿Por qué un autómata a pila es más potente que un autómata finito?". Arrancá la
   respuesta con sí o no.
2. La segunda parte de la actividad: `aab` y `abb` se rechazan por motivos distintos. ¿Cuáles? Mirá la
   última configuración de cada traza.
3. ¿Por qué `aⁿb²ⁿ` se puede resolver tanto apilando dos por cada `a` como desapilando una cada dos
   `b`? ¿Cuál de las dos necesita más estados?
4. Las trazas de `lock()`/`unlock()` forman un lenguaje que no es regular. ¿Qué pasaría si el lock no
   fuera reentrante (como un `Semaphore` de un permiso usado como mutex)? ¿Seguiría haciendo falta
   una pila?
5. El NPDA de `aⁿbᵐ` con `n ≤ m ≤ 3n` (ejercicio 7 de los apuntes) no se puede hacer con este motor.
   ¿Qué controla `agregarTransicion` que lo impide? ¿Cómo habría que cambiar `paso` para seguir varios
   caminos a la vez, como hace el AFND del 6.6?
6. Pregunta de parcial: "¿Un programa cualquiera puede ser expresado por un autómata a pila?". ¿Qué
   lenguaje de este ejercicio, o del 6.2, sirve de contraejemplo?

## Pistas

> [!tip]- Pista 1: los dos controles de `agregarTransicion`
> Las transiciones que salen del mismo estado con el mismo tope están juntas en
> `desde(estado, tope)`, así que alcanza con mirar ese mapa:
> ```java
> Map<Character, Movimiento> salidas = desde(desde, tope);
> Movimiento anterior = salidas.get(entrada);
> // 1) si anterior no es null y es distinto del nuevo: no determinista
> // 2) si alguna otra entrada del mapa es λ y la nueva no (o al revés): no determinista
> salidas.put(entrada, nuevo);
> ```

> [!tip]- Pista 2: `paso`
> ```java
> char tope = c.pila().charAt(0);
> String debajo = c.pila().substring(1);
> Movimiento m = delta(c.estado(), c.restante().charAt(0), tope);   // si queda entrada
> return new Configuracion(m.hacia(), c.restante().substring(1), m.apilar() + debajo);
> ```
> Y si no hay transición con el símbolo, probá con `LAMBDA`, sin consumir la entrada.

> [!tip]- Pista 3: el lock
> Dos estados: `libre`, que es inicial y final, y `tomado`. Cada `l` apila una `X` y cada `u` desapila
> una. Cuando en `tomado` vuelve a aparecer la `Z` en el tope, una transición sobre λ vuelve a `libre`.

## Conexión con la teoría y los TPs

- Apuntes: en "Gramáticas, Lenguajes y Autómatas", "Jerarquía de los autómatas" y "Autómatas de pila"
  ("Configuración y movimiento", "Lenguaje reconocido: dos definiciones", el PDA de `aⁿbⁿ` y la
  actividad "Traza de configuraciones"); "Ejercicios Propuestos" (el 6).
- Parciales: "¿Con un autómata a pila puede derivar la siguiente fórmula? ¿Por qué? { a^n b^n : n ≥
  1 }", "¿Por qué un autómata a pila es más potente que un autómata finito?" y "¿Un programa cualquiera
  puede ser expresado por un autómata a pila?".
- Unidad 3: el `ReentrantLock` y su `getHoldCount()`, que es lo que cuenta la pila de `lockBienUsado`.
