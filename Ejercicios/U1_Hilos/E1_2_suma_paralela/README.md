# Ejercicio 1.2 — Suma paralela con `join()`

> Unidad 1 · Hilos en Java · Nivel ★ · ~40 min

## Objetivo

Sumar un arreglo grande repartiéndolo entre `k` hilos: cada hilo suma un tramo, y el hilo principal
espera a todos y junta los resultados parciales. Es el esquema más simple de paralelismo de datos,
y funciona **sin** `synchronized` porque ningún dato es escrito por dos hilos.

```
           ┌─ sumador-1: [0, 4)  ─┐
  datos ───┼─ sumador-2: [4, 7)  ─┼──► join() de todos ──► total
           └─ sumador-3: [7, 10) ─┘
```

## Conceptos

- Partir el trabajo en tramos parejos.
- Lanzar `k` hilos y esperarlos con `join()`.
- Por qué cada hilo escribe en **su** campo y no en uno compartido.
- `join()` como garantía de visibilidad: lo que escribió el hilo se ve después del `join()`.
- Concurrencia vs paralelismo: el experimento final mide si de verdad gana algo.

## Archivos

| Archivo               | Qué hacer                                                                      |
| --------------------- | ------------------------------------------------------------------------------ |
| `SumadorDeTramo.java` | `run()`: sumar el tramo                                                        |
| `SumaParalela.java`   | `partir`, `crearSumadores`, `lanzar`, `esperarYCombinar` y `sumar`, en ese orden |
| `Verificador.java`    | No se toca                                                                     |

## Orden sugerido

1. `SumaParalela.partir`: es pura aritmética, sin hilos. Hacé los ejemplos del Javadoc a mano primero.
2. `SumadorDeTramo.run`.
3. `crearSumadores`, `lanzar`, `esperarYCombinar` y `sumar`, corriendo el verificador después de cada uno.
4. Mirá la tabla del experimento.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

## Para pensar

1. ¿Por qué no hace falta sincronizar nada, si todos los hilos leen el mismo arreglo?
2. Supongamos que en lugar de un `resultado` por sumador hubiera un único `static long total` y
   cada hilo hiciera `total += datos[i]`. ¿Qué podría salir mal? (Es exactamente el ejercicio 2.1.)
3. Mirá la tabla del experimento. ¿Mejora el tiempo de 1 a 2 a 4 hilos? ¿Y de 8 a 64? ¿Por qué con
   64 hilos puede ser más lento que con 8, si la máquina tiene muchos núcleos?
4. Con un arreglo de 20 millones de enteros, ¿cuánto tarda la suma en un hilo? ¿Cuánto cuesta crear
   un hilo? ¿Para qué tamaño de problema deja de convenir paralelizar?
5. ¿Qué pasa si leés `getResultado()` sin hacer `join()` antes? Pensá en dos casos: el hilo todavía
   no terminó, y el hilo terminó pero sin ninguna instrucción que sincronice.

## Pistas

> [!tip]- Pista 1: `partir`
> ```java
> int desde = 0;
> for (int i = 0; i < k; i++) {
>     int tamanio = n / k + (i < n % k ? 1 : 0);
>     // el tramo i es {desde, desde + tamanio}
>     desde += tamanio;
> }
> ```

> [!tip]- Pista 2: respuesta a la pregunta 3
> Con pocos hilos se gana porque la suma se reparte entre núcleos (paralelismo real). Con muchos más
> hilos que núcleos ya no hay más paralelismo para ganar: los hilos se turnan en los mismos núcleos
> (concurrencia), y se suma el costo de crear 64 hilos y de cambiar de contexto entre ellos. Además,
> sumar enteros es tan barato que el cuello de botella termina siendo la memoria, no la CPU.

## Conexión con la teoría y los TPs

- Apuntes: "Concurrencia vs paralelismo" y "Hilos — Monohilo y multihilo".
- TP1 y TP final: el análisis de tiempos teórico contra observado y "el efecto de cambiar la cantidad
  de hilos" que piden los informes es la misma pregunta que la tabla de este experimento.
