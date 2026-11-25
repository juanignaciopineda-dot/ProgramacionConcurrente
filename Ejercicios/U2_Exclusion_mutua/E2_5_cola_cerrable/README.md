# Ejercicio 2.5 — Cola cerrable y pipeline con terminación limpia

> Unidad 2 · Sincronización por condición · Nivel ★★★ · ~60 min

## Objetivo

Armar un pipeline de dos etapas concurrentes que procesa todos los pedidos y **termina solo**, sin
dejar hilos vivos y sin `stop`, `System.exit`, banderas sondeadas ni `sleep`. La pieza clave es una
cola que se puede cerrar: cuando el productor termina la cierra, y los consumidores se enteran de que
no va a venir nada más. Es el esqueleto del TP1 en chiquito.

```
  [hilo que llama] ──pedido-i──► colaPedidos ──► empaquetador-1..E ──paquete-i──► colaPaquetes
                                                                          │
                                         despachante-1..D ◄───────────────┘
```

## Conceptos

- El problema de la terminación: un consumidor en `while (true)` nunca termina.
- Cerrar una cola: "no entra nada más", no "tirá lo que queda".
- Despertar a **todos** los que esperan al cerrar.
- Quién cierra cada cola y cuándo: `join()` de la etapa anterior y recién ahí cerrar.
- Evitar estado compartido: cada despachante con su lista, unidas después del `join()`.

## Archivos

| Archivo             | Qué hacer                                    |
| ------------------- | -------------------------------------------- |
| `ColaCerrable.java` | `poner`, `sacar`, `cerrar` y `estaCerrada`   |
| `Empaquetador.java` | `run()`                                      |
| `Despachante.java`  | `run()`                                      |
| `Pipeline.java`     | `ejecutar`                                   |
| `Verificador.java`  | No se toca                                   |

## Orden sugerido

1. `ColaCerrable` completa: los primeros cinco casos del verificador son solo de la cola.
2. `Empaquetador.run` y `Despachante.run`.
3. `Pipeline.ejecutar`, siguiendo los cinco pasos del Javadoc.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

## Restricciones

Solo `synchronized`, `wait`, `notifyAll` y colecciones comunes. Nada de `java.util.concurrent`,
`Thread.sleep`, `System.exit`, `stop()` ni `Collections.synchronized...` (el verificador lo controla).

## Para pensar

1. En `Pipeline.ejecutar`, ¿qué pasa si cerrás `colaPaquetes` justo después de cerrar `colaPedidos`,
   sin esperar a los empaquetadores? Probalo y explicá el resultado.
2. ¿Por qué no puede ser cada empaquetador el que cierra `colaPaquetes` cuando termina? Proponé una
   forma en que **el último** empaquetador en terminar la cierre, sin que el hilo principal tenga que
   hacer `join()`.
3. `cerrar()` con `notify()` en vez de `notifyAll()`: ¿qué caso falla y por qué el pipeline se
   cuelga?
4. Esta cola no tiene límite. ¿Qué pasa con la memoria si los empaquetadores son mucho más rápidos que
   los despachantes? ¿Cómo combinarías esta cola con el `BufferAcotado` del 2.3?
5. Hacé el diagrama de secuencia de una corrida con 1 empaquetador, 1 despachante y 2 pedidos,
   incluyendo los `cerrar()` y los `join()`. Es el tipo de diagrama que pedía el TP1.

## Pistas

> [!tip]- Pista 1: `sacar`
> ```java
> while (elementos.isEmpty() && !cerrada) {
>     wait();
> }
> if (elementos.isEmpty()) {
>     return Optional.empty();
> }
> return Optional.of(elementos.removeFirst());
> ```

> [!tip]- Pista 2: el orden en `ejecutar`
> Crear las colas → crear y arrancar **todos** los hilos → cargar los pedidos → cerrar `colaPedidos` →
> `join()` a los empaquetadores → cerrar `colaPaquetes` → `join()` a los despachantes y juntar listas.

> [!tip]- Pista 3: respuesta a la pregunta 2
> Un contador compartido de empaquetadores activos, protegido por un monitor: cada uno, al terminar,
> lo decrementa adentro de un `synchronized`, y el que lo deja en cero cierra la cola de salida.

## Conexión con la teoría y los TPs

- Apuntes: "Comunicación entre hilos: `wait()`, `notify()`, `notifyAll()`" y "Problemas comunes de
  los programas concurrentes".
- TP1: es la estructura de `ConcurrentSimulation` y `OrderPipeline`: todas las etapas arrancan juntas,
  se hace `join()` de una etapa y recién ahí se cierra la cola de la siguiente. El enunciado pedía
  terminar "sin dejar hilos activos" y "sin `Thread.stop`, `System.exit` ni mecanismos
  equivalentes": este ejercicio es esa parte aislada.
