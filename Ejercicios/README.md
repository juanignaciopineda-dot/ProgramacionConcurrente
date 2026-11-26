# Ejercicios de Programación Concurrente en Java

Ejercicios cortos para programar, ordenados por unidad y de menor a mayor dificultad. Salen de los
[apunte práctico](../Apuntes/PROGRAMACION%20CONCURRENTE%20-%20PRACTICA.md) (con la teoría en el [apunte teórico](../Apuntes/PROGRAMACION%20CONCURRENTE%20-%20TEORIA.md)), de las filminas y de los dos TPs
(TP1: granja de impresión 3D; TP final: monitor de una red de Petri de pagos). La idea es que al terminar
la última unidad hayas programado, en chiquito, cada pieza de los TPs.

## Cómo está armado cada ejercicio

```
E1_1_crear_hilos/
├── README.md             ← enunciado, orden sugerido, preguntas para pensar y pistas
└── src/
    ├── HiloContador.java ← lo que completás vos
    ├── Lanzador.java     ←   "
    ├── Tarea.java        ←   "
    └── Verificador.java  ← pruebas automáticas: no hace falta tocarlo
```

- **El Javadoc es la guía.** Cada clase explica para qué sirve, y cada método explica qué tiene que
  hacer, en qué orden y por qué. Leé primero el Javadoc de la clase y después el de cada método.
  En IntelliJ también lo ves con `Ctrl+Q` sobre el nombre del método.
- Los métodos que te tocan tienen un `// TODO` y lanzan `UnsupportedOperationException`. Borrá esas
  dos líneas y escribí la implementación.
- No cambies las firmas (nombres, parámetros, tipos): el verificador las usa.

## Cómo correr un ejercicio

Hace falta un JDK 21 o posterior, el mismo de los TPs. Desde la carpeta del ejercicio:

```powershell
javac -d out src/*.java
```

```powershell
java -cp out Verificador
```

Con Java 22 o posterior alcanza con un solo comando, que compila en memoria y corre:

```powershell
java src/Verificador.java
```

Desde IntelliJ: abrí la carpeta del ejercicio, marcá `src` como *Sources Root* (clic derecho →
*Mark Directory as*) y corré `main` de `Verificador`.

El verificador muestra una línea por caso:

| Marca       | Significa                                                                    |
| ----------- | ---------------------------------------------------------------------------- |
| `[OK]`      | El caso pasó                                                                 |
| `[TODO]`    | Todavía hay un método sin implementar                                        |
| `[FALLA]`   | Está implementado pero da mal; abajo dice qué se esperaba                    |
| `[COLGADO]` | No terminó a tiempo: un `wait()` que nadie despierta, un deadlock, un `join()` eterno. Si hay hilos en espera circular sobre locks o monitores, dice quién espera a quién |
| `[ERROR]`   | Se lanzó una excepción que no se esperaba                                    |

Las líneas que empiezan con `>` son datos informativos (tiempos, contadores) que conviene mirar.
Si las tildes se ven raras en la consola, corré `chcp 65001` antes.

## Reglas generales

- **Herramientas permitidas.** Como en los TPs, cada ejercicio dice qué se puede usar; el
  verificador revisa algunas prohibiciones (por ejemplo, `java.util.concurrent` en la Unidad 2).
- **Pasar el verificador es necesario pero no suficiente**, igual que la suite pública del TP1: un
  error de concurrencia puede no aparecer en una corrida. Por eso cada README tiene preguntas
  "Para pensar", que son parte del ejercicio y el tipo de cosa que preguntan en los parciales.
- **Pistas plegadas.** Las pistas están en bloques que se abren con un clic en Obsidian. Probá un rato
  antes de abrirlas.
- **Soluciones de referencia.** Están en [_soluciones/](_soluciones/README.md), con la misma
  estructura que los ejercicios. Usalas para comparar cuando ya tengas tu versión en verde, o para
  destrabarte de un `[COLGADO]` después de haber probado con las pistas.
- Para practicar lo que evalúan los TPs, cuando termines un ejercicio dibujá su diagrama de
  secuencia (hilos como líneas de vida, `start`/`join`/`wait` como mensajes).

## Plan de unidades

Nivel: ★ (calentamiento) a ★★★★★ (tipo TP).

| Unidad                                                       | Temas                                                                                                          | Ejercicios        | Estado         |
| ------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------- | ----------------- | -------------- |
| [1. Hilos en Java](U1_Hilos/README.md)                       | `Thread` y `Runnable`, `start` vs `run`, `join`, estados y atributos, `interrupt`/`sleep`, daemon, excepciones no controladas, `ThreadLocal` | 5 (★ a ★★)        | ✅ Disponible  |
| [2. Exclusión mutua y sincronización por condición](U2_Exclusion_mutua/README.md) | Condición de carrera, `synchronized` (método, bloque, `static`), una llave por recurso, `wait`/`notifyAll`, productor/consumidor, terminación limpia | 5 (★★ a ★★★)      | ✅ Disponible  |
| [3. Locks y semáforos](U3_Locks_y_semaforos/README.md)       | `ReentrantLock`, `tryLock`, `ReadWriteLock`, fairness, semáforo binario y de N permisos, deadlock y orden global de adquisición (filósofos) | 5 (★★ a ★★★★)     | ✅ Disponible  |
| [4. Herramientas de alto nivel](U4_Herramientas_de_alto_nivel/README.md) | `CountDownLatch`, `ThreadFactory`, `Executors` y `ThreadPoolExecutor`, `Callable`/`Future`, `shutdown` vs `shutdownNow`, `Phaser` | 5 (★★ a ★★★)      | ✅ Disponible  |
| [5. Monitores](U5_Monitores/README.md)                       | Estructura de un monitor, colas de condición con semáforos, señalización *signal and continue*, *signal and exit* y *signal and urgent wait* (cola de cortesía), políticas, postergación indefinida | 5 (★★ a ★★★★)     | ✅ Disponible  |
| [6. Lenguajes y autómatas](U6_Lenguajes_y_automatas/README.md) | Operaciones sobre lenguajes, gramáticas y jerarquía de Chomsky, autómata finito, Moore y Mealy (con un sistema reactivo), autómata de pila, AFND y construcción de subconjuntos, expresiones regulares en Java (`Pattern`/`Matcher`) | 7 (★ a ★★★★)      | ✅ Disponible  |
| [7. Redes de Petri en Java](U7_Redes_de_Petri/README.md)     | Matriz de incidencia, sensibilizado y disparo con la ecuación de estado, P-invariantes en cada disparo, grafo de alcanzabilidad, transiciones temporales, el monitor de la red y, al final, el análisis de T-invariantes del log con regex | 6 (★★ a ★★★★)     | ✅ Disponible  |
| 8. Integradores                                              | Mini TP1 (etapas con recursos compartidos) y mini TP final (monitor de RdP con política y tiempo)              | —                 | 🔜 Próximamente |

Las unidades siguen el orden de la materia. La implementación de redes de Petri con monitores y el
análisis de T-invariantes con expresiones regulares son lo último, así que la Unidad 7 va después de
lenguajes y autómatas, y la 8 junta todo.
