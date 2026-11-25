# Ejercicio 5.1 — Buffer con un lock y dos variables de condición

> Unidad 5 · Monitores · Nivel ★★ · ~30 min

## Objetivo

El buffer acotado por tercera vez, ahora como monitor con **dos variables de condición**:
- Un `ReentrantLock` da la exclusión mutua.
- Una `Condition` para los productores (`noLleno`) y otra para los consumidores (`noVacio`) son las
  dos colas donde se espera.

Como cada tipo de hilo espera en su propia cola, `signal()` despierta siempre a uno que sirve, y ya
no hace falta `notifyAll()`. Lo que no cambia es el `while`: Java es *signal and continue*.

```
  poner():  lock ─ while (lleno) noLleno.await() ─ agregar ─ noVacio.signal() ─ unlock
  sacar():  lock ─ while (vacío) noVacio.await() ─ sacar   ─ noLleno.signal() ─ unlock
```

## Conceptos

- Variable de condición: un "contenedor" de hilos que esperan una condición sobre el estado del
  monitor.
- `await()` suelta el lock y duerme; `signal()` despierta a uno de **esa** cola.
- Varias condiciones sobre el mismo lock, que Java con `synchronized` no permite.
- Por qué `signal()` alcanza acá y en el 2.3 no alcanzaba `notify()`.
- Por qué hace falta `while` igual: la política *signal and continue*.

## Archivos

| Archivo                     | Qué hacer                                       |
| --------------------------- | ----------------------------------------------- |
| `BufferConCondiciones.java` | Constructor, `poner`, `sacar` y `tamanio`       |
| `Verificador.java`          | No se toca                                      |

## Orden sugerido

1. Constructor y `tamanio`.
2. `poner` y `sacar`, siguiendo el esquema del Javadoc.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

## Restricciones

`ReentrantLock`, `Condition` (`await` y `signal`) y colecciones comunes. Nada de `signalAll`,
`synchronized`, `wait`/`notify` ni semáforos (el verificador lo controla).

## Para pensar

1. **Experimento A:** hacé que productores y consumidores esperen y avisen en la **misma** condición
   (`noVacio`), con `signal()`. ¿Qué caso falla? Es la misma traza que la pista 2 del ejercicio 2.3.
2. **Experimento B:** volvé a las dos condiciones y cambiá los `while` por `if`. ¿Falla siempre? ¿Qué
   tiene que pasar entre el `signal()` y el momento en que el despertado recupera el lock?
3. ¿Qué de esto responde la pregunta del parcial "¿Qué función cumplen las variables de condición en
   un monitor?"? Nombrá las **dos** funciones, como pide la corrección.
4. `Condition` tiene `awaitNanos` y `await(tiempo, unidad)`. ¿Para qué te serviría un `await` con
   plazo en un buffer?
5. Compará las cuatro versiones del buffer: 2.3 (`synchronized`), 3.4 (semáforos), esta, y la del 5.4
   (monitor de Hoare). ¿Cuál deja más clara la diferencia entre exclusión mutua y sincronización por
   condición? ¿Cuál es más fácil de romper?

## Pistas

> [!tip]- Pista 1: `poner`
> ```java
> if (elemento == null) {
>     throw new NullPointerException("elemento null");
> }
> candado.lock();
> try {
>     while (elementos.size() == capacidad) {
>         noLleno.await();
>     }
>     elementos.addLast(elemento);
>     noVacio.signal();
> } finally {
>     candado.unlock();
> }
> ```

> [!tip]- Pista 2: respuesta al experimento B
> Con *signal and continue*, el despertado no entra enseguida: vuelve a competir por el lock. Si en
> ese momento llega otro consumidor que no estaba esperando, puede tomar el lock antes y llevarse el
> elemento. El candado no justo favorece justamente al que llega. Cuando el despertado por fin entra,
> el buffer está vacío otra vez, y con `if` intenta sacar igual.

## Conexión con la teoría y los TPs

- Apuntes: "Variables de condición" (incluida la diferencia con `wait`/`notify` de Java) y
  "Clasificación y políticas de señalización" (por qué con SC hace falta `while`).
- Parciales: "¿Qué función cumplen las variables de condición en un monitor?" (2021 y 2023).
