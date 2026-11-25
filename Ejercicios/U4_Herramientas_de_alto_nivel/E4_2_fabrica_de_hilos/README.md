# Ejercicio 4.2 — Fábrica de hilos

> Unidad 4 · Fábricas y executors · Nivel ★★ · ~35 min

## Objetivo

Escribir una `ThreadFactory` que centraliza la creación de hilos. Cada hilo sale con nombre
numerado y daemon o no según la fábrica. La fábrica lleva estadísticas de lo que creó y puede
limitar cuántos hilos crea. Después, pasársela a un pool para que los hilos del pool salgan de ahí.
Es el patrón Factory de la teoría aplicado a los hilos.

```
  pedido ──newThread(tarea)──► FabricaDeHilos ──► "trabajador-1" (NEW)
  pedido ──newThread(tarea)──►   (monitor)    ──► "trabajador-2" (NEW)
  pedido ──newThread(tarea)──►   límite: 2    ──► null  (rechazado, queda anotado)
```

## Conceptos

- `ThreadFactory`: un método, `newThread(Runnable)`, que devuelve el hilo **sin arrancar**.
- Por qué la fábrica es un monitor: varios hilos pueden pedirle hilos a la vez.
- Devolver `null` para rechazar un pedido, y qué hace un pool cuando le pasa.
- `Executors.newFixedThreadPool(n, fabrica)`: el pool usa tu fábrica y reutiliza los hilos.
- Devolver una copia de una colección protegida.

## Archivos

| Archivo               | Qué hacer                                                                 |
| --------------------- | ------------------------------------------------------------------------- |
| `FabricaDeHilos.java` | Constructor, `newThread`, `crearPool`, `getCreados` y `getEstadisticas`   |
| `Verificador.java`    | No se toca                                                                |

## Orden sugerido

1. Constructor, `getCreados` y `getEstadisticas`.
2. `newThread`, primero sin límite y después con.
3. `crearPool`.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

## Restricciones

`synchronized`, `ThreadFactory`, `Executors` y colecciones comunes. Nada de clases `Atomic...`,
colecciones concurrentes ni `Collections.synchronized...` (el verificador lo controla).

## Para pensar

1. Sacá el `synchronized` de `newThread` y corré el verificador varias veces. ¿Qué caso falla? ¿Qué
   tiene que ver con el `contador++` de la Unidad 2?
2. Los apuntes dicen que en `MyThreadFactory` la `ArrayList` de estadísticas "funciona porque
   `newThread` sólo se llama desde `main`". En un pool, ¿desde qué hilo se llama a `newThread`?
3. Con un pool de 4 y una fábrica limitada a 2, ¿cuántas líneas de "rechazado" aparecen para 8
   tareas, y por qué no una sola? ¿Qué pasaría con un límite de 0 (si el constructor lo permitiera)?
4. ¿Qué ganás con que **todos** los hilos de un programa salgan de la misma fábrica? ¿Qué se rompe
   si una parte del programa sigue usando `new Thread` por su cuenta?
5. Agregale a la fábrica un `UncaughtExceptionHandler` que se le ponga a cada hilo (Unidad 1). ¿Por
   qué es un buen lugar para hacerlo?

## Pistas

> [!tip]- Pista 1: `newThread`
> ```java
> if (creados >= maxHilos) {
>     estadisticas.add("rechazado: límite de " + maxHilos + " hilos");
>     return null;
> }
> creados++;
> Thread hilo = new Thread(tarea, prefijo + "-" + creados);
> hilo.setDaemon(daemon);
> estadisticas.add("creado " + hilo.getName());
> return hilo;
> ```

> [!tip]- Pista 2: respuesta a la pregunta 3
> Mientras el pool tenga menos hilos que su tamaño, cada `execute()` intenta crear uno nuevo, y cada
> intento es un pedido a la fábrica. Con 2 hilos creados y un tamaño de 4, las tareas 3 a 8 piden
> cada una un hilo y reciben `null`: 6 rechazos. Esas tareas igual se ejecutan, porque esperan en la
> cola a que se libere uno de los 2 hilos que sí existen.

## Conexión con la teoría y los TPs

- Apuntes: "El patrón Factory" (las facturas) y "`ThreadFactory`: crear hilos con una fábrica"
  (`MyThreadFactory`), y la actividad "Pool fijo y tareas rechazadas", punto (d).
- TP1: el enunciado permitía `Executors`. Con una fábrica propia, los hilos del pool tendrían
  nombres que se entienden en el log de eventos.
