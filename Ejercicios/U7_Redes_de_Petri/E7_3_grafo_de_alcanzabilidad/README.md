# Ejercicio 7.3 — Grafo de alcanzabilidad

> Unidad 7 · Redes de Petri · Nivel ★★★ · ~45 min

## Objetivo

Recorrer todos los marcados alcanzables de una red y sacar de ahí sus propiedades de comportamiento:
- **Deadlock:** si hay puntos muertos.
- **Acotación:** cuántas fichas llega a tener un lugar, y si la red es segura.
- **Un contraejemplo:** la secuencia más corta que lleva al primer deadlock.

Es lo que hace PIPE con "State Space Analysis". Aplicado a la cena de los filósofos, el recorrido
encuentra solo el deadlock que en la Unidad 3 viste en vivo. El contraejemplo son los cinco tomando
el tenedor izquierdo.

```
  M0 ──T0──► M1 ──T0──► M2 (punto muerto)
              └───T1──► M3 (punto muerto)
```

## Conceptos

- El grafo de marcas: un vértice por marcado alcanzable y un arco por disparo.
- Recorrido en anchura y por qué da el contraejemplo más corto.
- Marcados como claves: por qué un `int[]` no sirve en un `Set`.
- Puntos muertos, acotación, red segura.
- Redes no acotadas: el grafo es infinito y hay que cortar.

## Archivos

| Archivo            | Qué hacer                                                         |
| ------------------ | ----------------------------------------------------------------- |
| `Explorador.java`  | `explorar`                                                        |
| `Grafo.java`       | Ya está hecho (el `record` con el resultado)                       |
| `RedDePetri.java`  | Ya está hecha (la del 7.1, con dos métodos para mirar marcados ajenos) |
| `Verificador.java` | No se toca                                                        |

## Orden sugerido

1. Leé `Grafo` y los dos métodos nuevos de `RedDePetri` (`sensibilizadaEn` y `disparoDesde`).
2. `explorar`, siguiendo el pseudocódigo del Javadoc.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

En los casos de los filósofos, el verificador muestra cuántos marcados y arcos tiene cada red y cuál
es el contraejemplo.

## Para pensar

1. Preguntas de parcial: "Con un grafo de marcado, ¿podemos determinar todas las propiedades de una
   RdP?". Explicá cómo encontrás con él que la red es acotada, que es segura y que tiene un
   interbloqueo. ¿Qué pasa si la red no es acotada?
2. Cambiá la cola por una pila (en profundidad). ¿Qué caso falla? ¿Sigue encontrando el deadlock de
   los filósofos? ¿Por qué el contraejemplo ya no es el más corto?
3. Los filósofos ingenuos tienen 82 marcados y los atómicos 11. Contá a mano los 11 de la versión
   atómica: ¿cuántos con nadie comiendo, cuántos con uno y cuántos con dos?
4. Pregunta de parcial: "Si una RdP está libre de interbloqueo para m₀, ¿es libre de interbloqueo
   para un marcado m > m₀?". Armá la red del ejemplo de la respuesta y explorala con 1 y con 2 fichas
   en P. La red tiene una plaza P, un ciclo P → t₁ → Q → t₂ → P, y una t₃ que necesita 2 fichas de P y
   las manda a una plaza sin salida. ¿Qué cambia en el grafo?
5. Con 5 filósofos el grafo es chico, pero ¿cómo crece con 10, con 20? ¿Por qué la cátedra insiste en
   los invariantes, que no necesitan recorrer nada?

## Pistas

> [!tip]- Pista 1: la estructura
> ```java
> Map<List<Integer>, List<Integer>> camino = new HashMap<>();
> Deque<int[]> pendientes = new ArrayDeque<>();
> int[] inicial = red.getMarcado();
> camino.put(clave(inicial), List.of());
> pendientes.add(inicial);
> while (!pendientes.isEmpty()) {
>     int[] m = pendientes.poll();
>     List<Integer> hastaM = camino.get(clave(m));
>     // ... para cada t sensibilizada en m: arcos++, siguiente, si es nuevo: camino y cola
>     // ... si no había ninguna sensibilizada: punto muerto
> }
> ```

> [!tip]- Pista 2: respuesta a la pregunta 1
> Sí, si la red es acotada: el grafo es finito y tiene todo. Es acotada si el máximo de fichas en
> los marcados es finito, y segura si ese máximo es 1. Hay interbloqueo si algún vértice no tiene
> arcos de salida. Un T-invariante es un ciclo del grafo que vuelve al mismo marcado. Si la red no
> es acotada, el grafo es infinito, y para eso está el árbol de cobertura, con el símbolo ω.

## Conexión con la teoría y los TPs

- Apuntes: "Grafo de accesibilidad" ("el grafo de accesibilidad de una red de Petri es un autómata"),
  "Grafo de marcas y árbol de cobertura" (el ejemplo de `m0 = (2, 0)`), "Acotación y seguridad",
  "Vivacidad y punto muerto" y "La cena de los filósofos".
- Unidad 3: el ejercicio 3.5 mostraba el deadlock de los filósofos en ejecución. Acá se lo encuentra
  sin ejecutar nada.
