# Ejercicio 1.1 — Dos formas de crear un hilo (y una trampa)

> Unidad 1 · Hilos en Java · Nivel ★ · ~30 min

## Objetivo

Crear hilos de las tres maneras que ofrece Java, lanzarlos de verdad con `start()` y esperarlos con
`join()`. Y comprobar con tus propios ojos por qué llamar a `run()` no crea ningún hilo.

## Conceptos

- Heredar de `Thread` vs implementar `Runnable` (y por qué se prefiere `Runnable`).
- Una lambda como `Runnable`.
- `start()` vs `run()`.
- `join()`: esperar a que un hilo termine.
- `Thread.currentThread()` y el nombre de un hilo.

## Archivos

| Archivo             | Qué hacer                                                                            |
| ------------------- | ------------------------------------------------------------------------------------ |
| `HiloContador.java` | Constructor y `run()`. Forma 1: `extends Thread`                                     |
| `Tarea.java`        | `run()`. Forma 2: `implements Runnable`                                              |
| `Lanzador.java`     | `crearConLambda` (forma 3), `ejecutarConStart` y la trampa `ejecutarConRun`           |
| `Verificador.java`  | No se toca                                                                           |

## Orden sugerido

1. `HiloContador`: constructor y `run()`. Corré el verificador: los dos primeros casos pasan a `[OK]`.
2. `Lanzador.crearConLambda`.
3. `Tarea.run`.
4. `Lanzador.ejecutarConStart`.
5. `Lanzador.ejecutarConRun`, y compará los dos tiempos que muestra el verificador.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

(o `javac -d out src/*.java` y `java -cp out Verificador`, ver el [README general](../../README.md)).

## Para pensar

1. ¿Qué hilo ejecutó las tareas en `ejecutarConRun`? ¿Por qué? ¿Qué hace en realidad `Thread.run()`
   cuando el hilo se creó con un `Runnable`?
2. Mirá los dos tiempos que imprime el verificador (unos 300 ms contra unos 500 ms). Explicá la
   diferencia con un diagrama de secuencia de `main` y los cinco hilos.
3. ¿Qué pasa si llamás a `start()` dos veces sobre el mismo hilo? Probalo en un `main` aparte.
4. `HiloContador` hereda de `Thread`. Si mañana tuviera que heredar también de una clase `Medidor`,
   ¿podría? ¿Y si fuera un `Runnable`?
5. `ejecutadaPor` en `Tarea` no está sincronizado ni es `volatile`. ¿Por qué el hilo principal igual
   ve el valor correcto? ¿Qué podría ver si lo leyera **antes** del `join()`?

### Más para pensar: debug y diagramas

Con el ejercicio terminado:

1. Poné un breakpoint adentro de `run()` y corré en modo debug con `start()`. En el panel de hilos
   tendrían que aparecer dos hilos vivos, `main` y el nuevo. ¿Qué pasa con `main` mientras el otro
   está detenido en el breakpoint?
2. Repetí el debug llamando a `run()`. ¿Cuántos hilos hay ahora? Mirá la pila de llamadas: ¿qué
   método aparece debajo de `run()` en cada caso?
3. Dibujá el diagrama de clases y el diagrama de secuencia del ejercicio. ¿Qué tipo de mensaje
   (sincrónico o asincrónico) es `start()`? ¿Y `run()`?

## Pistas

> [!tip]- Pista 1: ¿cómo le pongo nombre al hilo?
> Tanto `Thread` como sus subclases tienen constructores que reciben el nombre:
> `new Thread(tarea, "hilo-1")` y, en una subclase, `super("contador-1")`.

> [!tip]- Pista 2: `ejecutarConStart` tarda 1500 ms
> Si hacés `start()` y `join()` en el mismo bucle, el hilo 2 arranca recién cuando el 1 terminó.
> Usá dos bucles: uno que arranque a todos y otro que los espere a todos.

> [!tip]- Pista 3: respuesta a la pregunta 1
> `Thread.run()` no tiene nada de especial: es un método común que llama a `target.run()`. Quien
> crea el hilo nuevo (pide la pila, lo registra en el planificador) es `start()`. Llamar a `run()` es
> una llamada a método más, y la ejecuta el hilo que la hace.

## Conexión con la teoría y los TPs

- Apunte práctico: "Primer hilo: `start()` vs `run()`" (la pregunta de `h1.start()` vs `h1.run()`) y "Threads en Java —
  Creación de hilos".
- TP1: `ConcurrentSimulation` arranca **todos** los workers al principio y después los espera con
  `join()`, etapa por etapa. Es `ejecutarConStart`: si se hubiera hecho `start` y `join` en el mismo
  bucle, las etapas habrían quedado secuenciales, que el enunciado prohíbe explícitamente.
