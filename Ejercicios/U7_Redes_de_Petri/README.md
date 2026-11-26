# Unidad 7 — Redes de Petri en Java

Es lo último de la materia: implementar redes de Petri y ejecutarlas con un monitor, y verificar el
resultado con expresiones regulares. Por eso va después de todas las demás unidades, y usa los
monitores de la Unidad 5 y las expresiones regulares de la Unidad 6. La unidad lleva la teoría a
código, pieza por pieza:
- La red y su ecuación de estado.
- Los invariantes que prueban propiedades sin ejecutar nada.
- El grafo de alcanzabilidad, que encuentra los deadlocks y los muestra.
- El tiempo.
- El monitor de la materia: una red que guía a los hilos.
- Al final, el análisis del log de una ejecución con expresiones regulares, para comprobar que se
  cumplieron los T-invariantes.

Los cuatro primeros son de un solo hilo, como la parte de la red del TP final. La concurrencia la
pone el monitor, que llega en el 7.5, y el 7.6 analiza el log que deja una ejecución como esa.

| Ejercicio                                                          | Nivel | Tiempo  | Practica                                                                     |
| ------------------------------------------------------------------ | ----- | ------- | ---------------------------------------------------------------------------- |
| [7.1 La red y la ecuación de estado](E7_1_red_y_ecuacion_de_estado) | ★★   | ~40 min | `Pre`, `Post` y `C`, sensibilizado, disparo, `M + C·s` y conflictos estructurales |
| [7.2 Invariantes](E7_2_invariantes)                                | ★★★   | ~40 min | P-invariantes y T-invariantes, constantes, control en cada disparo; la barbería |
| [7.3 Grafo de alcanzabilidad](E7_3_grafo_de_alcanzabilidad)        | ★★★   | ~45 min | Recorrido en anchura, puntos muertos, acotación y el contraejemplo; los filósofos |
| [7.4 Red con tiempo](E7_4_red_temporizada)                         | ★★★   | ~45 min | Ventana `[α, β]`, tiempo de sensibilización y cuándo arranca cada cronómetro   |
| [7.5 El monitor de la red](E7_5_monitor_de_la_red)                 | ★★★★  | ~60 min | Productor/consumidor modelado como red y ejecutado por el monitor: `dispararTransicion`, despertables y política |
| [7.6 T-invariantes en el log](E7_6_t_invariantes_en_un_log)       | ★★★   | ~50 min | Una sola expresión con huecos perezosos y el prefijo factorizado, borrar invariantes del log, reproducirlo en la red |

Hacelos en orden. Del 7.2 en adelante, la `RedDePetri` viene resuelta (es la del 7.1). El 7.5 usa
la `ColaDeCondicion` del 5.3 y la idea de política del 5.5, y el 7.6, las expresiones regulares del
6.7.

**Herramientas permitidas:** del 7.1 al 7.4, Java común (arreglos, colecciones, `record`). En el 7.5,
las del monitor de la Unidad 5: `Semaphore` y la `ColaDeCondicion`, sin `synchronized`, locks ni
`wait`/`notify`. En el 7.6, `java.util.regex`.

**Teoría:** en los apuntes, el capítulo "Redes de Petri" (sobre todo "Definición y elementos",
"Sensibilización y disparo", "Ecuación fundamental e invariantes de marcado", "Conflictos:
estructural, efectivo y general", "Grafo de marcas y árbol de cobertura", "La cena de los filósofos" y
"El problema de la barbería"), "Redes de Petri con tiempo y ecuación de estado generalizada" y, en
"Monitores", "Un monitor guiado por una red de Petri". En el apunte práctico, "Expresiones regulares
en la práctica: verificar T-invariantes".
