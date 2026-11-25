# Ejercicio 2.4 — Turnos en ronda con `wait()` y `notifyAll()`

> Unidad 2 · Sincronización por condición · Nivel ★★★ · ~40 min

## Objetivo

Hacer que `n` hilos actúen en ronda estricta (0, 1, 2, 0, 1, 2, ...), sin importar en qué orden
arrancan ni cómo los planifica el sistema operativo. Con `n = 2` es el ping-pong clásico. Cada hilo
espera una condición **distinta** ("es mi turno"), y eso hace que `notify()` deje de alcanzar.

```
  turno:   0 → 1 → 2 → 0 → 1 → 2 → ...
  hilo 0:  ██ ·  ·  ██ ·  ·
  hilo 1:  ·  ██ ·  ·  ██ ·
  hilo 2:  ·  ·  ██ ·  ·  ██
```

## Conceptos

- Una variable de estado (`turno`) que codifica la condición de cada hilo.
- Muchos hilos con condiciones distintas esperando en el mismo monitor.
- Por qué `notify()` puede dejar a todos dormidos.
- Validar el protocolo: `pasarTurno` de quien no tiene el turno es un error.
- Orden impuesto por sincronización, no por `sleep` ni por prioridades.

## Archivos

| Archivo             | Qué hacer                                                 |
| ------------------- | --------------------------------------------------------- |
| `Turnero.java`      | Constructor, `esperarTurno`, `pasarTurno` y `getTurno`    |
| `Participante.java` | `run()`                                                   |
| `Verificador.java`  | No se toca                                                |

## Orden sugerido

1. `Turnero`: constructor y `getTurno`.
2. `esperarTurno` y `pasarTurno`, con sus validaciones.
3. `Participante.run`.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

## Restricciones

Solo `synchronized`, `wait` y `notifyAll`. Nada de `java.util.concurrent` ni `Thread.sleep` (el
verificador lo controla).

## Para pensar

1. Cambiá `notifyAll()` por `notify()` en `pasarTurno` y corré el verificador. ¿Con 2 participantes
   funciona? ¿Y con 3? Explicá por qué la cantidad importa.
2. `notifyAll()` despierta a los `n - 1` que esperan, pero solo uno puede seguir. ¿Cuánto trabajo se
   desperdicia por turno? ¿Cómo lo evitarías si tuvieras una cola de espera por participante?
   (Es la idea de las colas de condición del monitor de la Unidad 5 y del TP final.)
3. El `registro` del verificador es un `ArrayList` común que escriben todos los participantes, sin
   sincronizar. ¿Por qué no hay condición de carrera? ¿Qué garantiza que cada hilo vea lo que agregó
   el anterior?
4. ¿Qué le pasa a la ronda si un participante termina sus rondas antes que los demás? ¿Y si lo
   interrumpen mientras espera? ¿Qué cambiarías para que los otros no queden colgados?
5. Modelá el turnero de 3 participantes como red de Petri. ¿Es un grafo de estado, un grafo de
   eventos o las dos cosas?

## Pistas

> [!tip]- Pista 1: `esperarTurno`
> ```java
> while (turno != id) {
>     wait();
> }
> ```

> [!tip]- Pista 2: respuesta a la pregunta 3
> El registro se escribe solo entre `esperarTurno` y `pasarTurno`, y en ese tramo ningún otro
> participante puede estar escribiendo (no es su turno): la exclusión la da el protocolo. La
> visibilidad la da el monitor: el que agrega y después llama a `pasarTurno` (que es `synchronized`)
> "publica" lo que escribió, y el siguiente lo ve al volver de su `esperarTurno` (también
> `synchronized`).

## Conexión con la teoría y los TPs

- Apuntes: "Comunicación entre hilos" (`notify` vs `notifyAll`) y "Sincronización por señal" en los
  modelos básicos de Redes de Petri.
- TP final: el monitor tiene una cola de condición **por transición** justamente para despertar solo
  al hilo que puede avanzar, en vez de despertar a todos como acá.
