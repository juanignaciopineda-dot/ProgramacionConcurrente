# Ejercicio 2.3 — Buffer acotado con `wait()` y `notifyAll()`

> Unidad 2 · Sincronización por condición · Nivel ★★★ · ~45 min

## Objetivo

Implementar el buffer del productor/consumidor: el productor espera si está lleno, el consumidor
espera si está vacío, y ninguno gasta CPU mientras espera. Es una `BlockingQueue` hecha a mano, y el
primer **monitor** completo de la materia: exclusión mutua (`synchronized`) más sincronización por
condición (`wait`/`notifyAll`).

```
  productores ──poner()──► [ · · · ] capacidad 3 ──sacar()──► consumidores
                  lleno → wait()          vacío → wait()
```

## Conceptos

- `wait()` libera el monitor y duerme; `notifyAll()` despierta a los que esperan.
- `wait`/`notify` solo se pueden llamar con el monitor tomado (si no, `IllegalMonitorStateException`).
- `while` y no `if` alrededor del `wait()`.
- `notifyAll()` y no `notify()` cuando hay hilos esperando condiciones distintas.
- Dejar salir la `InterruptedException`.

## Archivos

| Archivo              | Qué hacer                                       |
| -------------------- | ----------------------------------------------- |
| `BufferAcotado.java` | Constructor, `poner`, `sacar` y `tamanio`       |
| `Verificador.java`   | No se toca                                      |

## Orden sugerido

1. Constructor y `tamanio`.
2. `poner` y `sacar` sin esperar (como si nunca estuviera lleno ni vacío): pasan los casos de un hilo.
3. Agregá los `wait()` y `notifyAll()`.
4. Cuando todo esté en verde, hacé los dos experimentos de "Para pensar".

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

## Restricciones

Solo `synchronized`, `wait`, `notify`/`notifyAll` y colecciones comunes. Nada de
`java.util.concurrent`, `Collections.synchronized...` ni `Thread.sleep` adentro del buffer (el
verificador lo controla).

## Para pensar

1. **Experimento A:** cambiá los dos `notifyAll()` por `notify()` y corré el verificador varias veces.
   ¿Qué caso falla? Contá una secuencia concreta en la que todos los hilos quedan dormidos.
2. **Experimento B:** volvé a `notifyAll()` y cambiá los `while` por `if`. ¿Qué falla ahora? ¿Qué
   excepción o qué valor raro aparece, y por qué?
3. ¿Por qué el consumidor queda en `WAITING` y no en `BLOCKED`? ¿Cuándo un hilo que llamó a
   `sacar()` sí podría verse `BLOCKED`?
4. Cuando un hilo vuelve de `wait()`, ¿tiene el monitor? ¿Cuánto tiempo pasó entre el `notifyAll()`
   y el momento en que lo recuperó, y qué pudo pasar en el medio?
5. Modelá este buffer como red de Petri (lugares "libres" y "ocupados", transiciones *producir* y
   *consumir*). ¿Qué P-invariante tiene? ¿A qué línea del código corresponde cada arco?

## Pistas

> [!tip]- Pista 1: estructura de `poner`
> ```java
> while (elementos.size() == capacidad) {
>     wait();
> }
> elementos.addLast(elemento);
> notifyAll();
> ```

> [!tip]- Pista 2: respuesta al experimento A
> Capacidad 1, productores P1 y P2, consumidores C1 y C2. Todos esperan en el mismo monitor:
>
> 1. C1 y C2 encuentran el buffer vacío y se duermen. Esperando: {C1, C2}.
> 2. P1 pone (queda lleno) y su `notify()` despierta a C1, que todavía no corre.
> 3. P2 y después P1 quieren poner, lo encuentran lleno y se duermen. Esperando: {C2, P2, P1}.
> 4. C1 corre, saca (queda vacío) y su `notify()` despierta a... C2, el tipo equivocado.
> 5. C1 vuelve a `sacar()`, lo encuentra vacío y se duerme. C2 corre, lo encuentra vacío y se duerme.
>
> Esperando: {P2, P1, C1, C2}. Nadie quedó despierto para avisar: deadlock. Con `notifyAll()` en el
> paso 4 se despiertan también P1 y P2, y uno de ellos pone.

## Conexión con la teoría y los TPs

- Apuntes: "Comunicación entre hilos: `wait()`, `notify()`, `notifyAll()`" (`EventStorage`) y, en
  Redes de Petri, "Modelos básicos → Buffer acotado".
- TP1: cada etapa le pasa las órdenes a la siguiente por una cola, y el enunciado prohíbe
  `BlockingQueue`: había que escribirla, como acá. La del grupo (`CloseableBlockingQueue`) usa un
  `Semaphore` para la espera; en la Unidad 3 vas a poder compararla con esta versión.
