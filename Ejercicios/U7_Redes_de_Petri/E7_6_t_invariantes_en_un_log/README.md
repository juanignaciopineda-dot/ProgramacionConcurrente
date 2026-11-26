# Ejercicio 7.6 — T-invariantes en el log

> Unidad 7 · Redes de Petri · Nivel ★★★ · ~50 min

## Objetivo

El último paso de la materia: comprobar, con el log de una ejecución, que la implementación de una red
de Petri hizo lo que tenía que hacer. Es el script de Python del apunte práctico, en Java, y es lo que
hace el `InvariantAnalyzer` del TP final. Al log se le hacen dos preguntas:
- **¿Era posible?** Se reproduce el log sobre la red, disparo por disparo.
- **¿Se cumplieron los T-invariantes?** Se borran del log los invariantes completos con una expresión
  regular, conservando lo que estaba intercalado, hasta que no quede ninguno. Si no sobra nada, la red
  volvió al marcado inicial.

```
  log:        T0 T3 T1 T2 ...           expresión:  T0(.*?)T1(.*?)T2      (huecos perezosos)
  borrar:     T0 [T3] T1 [] T2   →   T3 (lo de los huecos se conserva)
  repetir hasta que no haya más coincidencias:  invariantes completos + lo que sobró
```

## Conceptos

- El log como prueba de la ejecución: por la ecuación de estado, un log que se descompone en
  T-invariantes completos vuelve al marcado inicial.
- Huecos perezosos `(.*?)` que capturan, y un reemplazo que los conserva.
- **Una sola** expresión para todos los invariantes y todos sus órdenes, con el prefijo común
  factorizado.
- Por qué un hueco codicioso se saltea invariantes enteros.
- `T1` no es el comienzo de `T10`: `(?!\d)`.
- Lo que los T-invariantes no ven: un log imposible para la red puede descomponerse en invariantes
  completos.

## Archivos

| Archivo                | Qué hacer                                                                         |
| ---------------------- | --------------------------------------------------------------------------------- |
| `AnalizadorDeLog.java` | `disparos`, `primerDisparoImposible`, `expresion` y `quitarInvariantes`           |
| `RedDePetri.java`      | Ya está hecha (la del 7.1)                                                         |
| `Verificador.java`     | No se toca                                                                         |

## Orden sugerido

1. `disparos`: leer el log con `T(\d+)`, como en el 6.7.
2. `primerDisparoImposible`, apoyado en `disparos` y en la `RedDePetri`.
3. `quitarInvariantes`. Los casos de la tabla de los apuntes usan la expresión del apunte, así que lo
   podés probar antes de escribir `expresion`.
4. `expresion`, con el árbol de prefijos del Javadoc.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

Los casos usan las redes y los logs del apunte práctico:
- **El productor/consumidor:** su T-invariante en los cuatro órdenes, y la tabla con los cuatro logs.
- **Los dos invariantes que comparten `T0`:** con el log del falso error.
- **La exclusión mutua de dos procesos:** con dos logs imposibles.

Varios casos muestran además lo que pasa con una variante equivocada, como la expresión codiciosa o la
que no factoriza el prefijo.

## Para pensar

1. Con dos expresiones separadas, `T0T2T0T1T2T3` da un falso error. Con una sola expresión, pero con
   cada orden entero como alternativa, `(?:T0(.*?)T1(.*?)T2|T0(.*?)T2(.*?)T3)`, también. ¿Por qué el
   falso error desaparece recién cuando `T0(.*?)` queda afuera de la alternativa? Seguí al motor paso a
   paso: ¿qué prueba primero, otra alternativa o un hueco más largo?
2. En el productor/consumidor de los apuntes, ¿por qué no hace falta una alternativa con `T3` **antes**
   de `T1`? Mirá qué plaza es la entrada de `T3` y qué transición le pone fichas.
3. `T0T2T1T3` y `T0T0T1T1` se descomponen en invariantes completos de la exclusión mutua, y los dos son
   imposibles para la red. ¿Qué propiedad violan? ¿Qué la controla: un T-invariante, un P-invariante
   (como la `RedVigilada` del 7.2) o reproducir el log?
4. Si el resto no está vacío, ¿cómo distinguís un invariante que quedó a medias al cortar la ejecución
   (el `T0T1` del último log de la tabla) de un disparo que no debería haber ocurrido? ¿Qué te dice
   `primerDisparoImposible` en cada caso?
5. ¿Qué tipo de lenguaje, en la jerarquía de Chomsky, es el conjunto de logs posibles de una red
   **acotada**? Pensá en el grafo de alcanzabilidad del 7.3 como un autómata. ¿Y el de una red no
   acotada? Compará con el lenguaje de los `lock()`/`unlock()` del 6.5.
6. El `InvariantAnalyzer` del TP final borra de a una coincidencia y vuelve a empezar desde el
   principio del log, en vez de hacer pasadas como `re.subn`. ¿Da siempre lo mismo? Además, revisa que
   lo que reconoció la expresión sea uno de los invariantes configurados: ¿qué error de la expresión
   detecta eso?

## Pistas

> [!tip]- Pista 1: el árbol de `expresion`
> ```java
> private static final class Nodo {
>     private final Map<Integer, Nodo> hijos = new LinkedHashMap<>();
>     private boolean fin;
> }
> ```
> Para cada orden, bajás desde la raíz con `hijos.computeIfAbsent(t, k -> new Nodo())` y marcás `fin`
> en el último nodo. Si en el camino pasás por un nodo con `fin`, o el último ya tiene hijos, un orden es
> el comienzo de otro.

> [!tip]- Pista 2: una pasada de `quitarInvariantes`
> ```java
> Matcher m = patron.matcher(log);
> StringBuilder resto = new StringBuilder();
> int enEstaPasada = 0;
> while (m.find()) {
>     StringBuilder huecos = new StringBuilder();
>     for (int g = 1; g <= m.groupCount(); g++) {
>         if (m.group(g) != null) {
>             huecos.append(m.group(g));
>         }
>     }
>     m.appendReplacement(resto, Matcher.quoteReplacement(huecos.toString()));
>     enEstaPasada++;
> }
> m.appendTail(resto);
> ```

> [!tip]- Pista 3: respuesta a la pregunta 2
> `T3` toma de la plaza de los productos, y la única transición que pone fichas ahí es `T1`, la que
> deposita. Dentro de un mismo ciclo de productor y consumidor, el `T3` siempre viene después del `T1`.
> Lo que sí puede cambiar es dónde cae el `T2` del productor: antes, entre o después de `T3`, `T4` y
> `T5`. Por eso son cuatro órdenes.

## Conexión con la teoría y los TPs

- Apuntes: en el práctico, "Expresiones regulares en la práctica: verificar T-invariantes" ("Expresiones
  regulares y redes de Petri", con "El problema", "El método", "Por qué una sola expresión" y "Las
  combinaciones del mismo invariante"); en el teórico, "Ecuación fundamental e invariantes de marcado" y
  "Cálculo de P-invariantes y T-invariantes".
- Unidades anteriores: las expresiones en Java, `find()` y codicioso contra perezoso (6.7), la red
  (7.1), los invariantes y la `RedVigilada` (7.2) y el monitor que produce el log (7.5).
- TP final: `InvariantAnalyzer`, que lee la expresión y los invariantes de la configuración, borra de a
  un invariante conservando los huecos y falla si sobra algo del log.
