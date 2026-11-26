# Ejercicio 5.3 — Monitor con semáforos y *signal and exit*

> Unidad 5 · Monitores · Nivel ★★★ · ~45 min

## Objetivo

Construir un monitor a mano, con las mismas piezas que el del TP final:
- **Un mutex:** un semáforo binario, que es la cola de entrada.
- **Una variable de condición:** un semáforo en 0 con un contador.
- **La política *signal and exit*:** el que despierta a otro le pasa el mutex en mano y sale sin
  soltarlo.

Con eso se administra una sala de N impresoras. Dos ideas de los parciales quedan a la vista:
- El despertado encuentra la impresora que le dejaron, así que alcanza con un `if`.
- La impresión va **afuera** del monitor, para no serializar todo.

```
  imprimir(doc):   tomar() ──► [ imprime, SIN el mutex ] ──► devolver()

  devolver():  mutex ─ libres++ ─ ¿alguien espera?  sí → despertarUno()  (el mutex pasa al despertado)
                                                    no → mutex.release()
```

## Conceptos

- Variable de condición hecha con un semáforo en 0 y un contador.
- Anotarse antes de soltar el mutex, para no perder avisos.
- *Signal and exit*: el mutex pasa de mano y nadie de la cola de entrada se cuela.
- Por qué con SX alcanza un `if` y con SC (Java) hace falta `while`.
- Semáforos justos: los que esperan se atienden en orden de llegada.
- Las acciones van fuera del monitor: el monitor solo administra el acceso.

## Archivos

| Archivo                | Qué hacer                                                 |
| ---------------------- | --------------------------------------------------------- |
| `ColaDeCondicion.java` | `esperar` y `despertarUno`                                |
| `SalaDeImpresion.java` | Constructor, `tomar`, `devolver` e `imprimir`             |
| `Verificador.java`     | No se toca                                                |

## Orden sugerido

1. `ColaDeCondicion`: los tres primeros casos son solo de la cola.
2. `SalaDeImpresion`: constructor, `tomar` y `devolver`.
3. `imprimir`.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

## Restricciones

`Semaphore` y la `ColaDeCondicion`. Nada de `synchronized`, locks, `Condition` ni `wait`/`notify` (el
verificador lo controla).

## Para pensar

1. Cambiá `devolver` para que, después de `despertarUno()`, suelte el mutex igual
   (*signal and continue*). ¿Qué caso falla? ¿Por qué el `if` de `tomar` deja de ser correcto?
2. En `esperar`, poné `mutex.release()` **antes** de `esperando++`. Describí la secuencia de dos hilos
   que pierde un aviso. ¿Por qué el caso de estrés casi nunca la encuentra? Mirá en el verificador
   cómo la encuentra el caso que usa `MutexQueMira`.
3. Pregunta de parcial: "Las acciones que implementan operaciones sobre los recursos, ¿se realizan
   adentro o afuera del monitor? ¿Por qué?". Respondela con el caso de las 3 impresoras y los tiempos
   que mide el verificador.
4. Compará con el `Estacionamiento` del 3.3, que también administra N recursos. ¿Qué hace acá el
   monitor que allá hacía el semáforo de N permisos? ¿Qué ganás con el monitor?
5. Mirá `ConditionQueues` y `Monitor` del TP final. ¿Qué parte de este ejercicio es cada método?
   ¿Dónde está el "le paso el mutex y no lo suelto"?

### Más para pensar: varios hilos que se pueden despertar

En el monitor guiado por una red de Petri hay 2 productores y 2 consumidores. En un momento hay un
productor y un consumidor esperando, y después de un disparo las transiciones de los dos quedan
sensibilizadas. ¿Quién decide a cuál se despierta? Proponé dos políticas distintas y explicá qué
propiedad favorece cada una (el ejercicio 5.5 implementa una).

## Pistas

> [!tip]- Pista 1: `ColaDeCondicion`
> ```java
> public void esperar(Semaphore mutex) {
>     esperando++;
>     mutex.release();
>     dormidos.acquireUninterruptibly();
> }
>
> public boolean despertarUno() {
>     if (esperando == 0) {
>         return false;
>     }
>     esperando--;
>     dormidos.release();
>     return true;
> }
> ```

> [!tip]- Pista 2: `devolver`
> ```java
> mutex.acquireUninterruptibly();
> if (libres == impresoras) {
>     mutex.release();
>     throw new IllegalStateException("estaban todas las impresoras libres");
> }
> libres++;
> if (!esperanImpresora.despertarUno()) {
>     mutex.release();
> }
> ```

> [!tip]- Pista 3: respuesta a la pregunta 2
> El hilo A suelta el mutex sin haberse anotado. Antes de que llegue a `esperando++`, el hilo B entra,
> devuelve una impresora y mira `esperando`, que es 0: no despierta a nadie y suelta el mutex. Recién
> ahí A se anota y se duerme, esperando un aviso que ya pasó. La ventana entre las dos líneas es de
> nanosegundos, por eso una prueba de estrés casi nunca la ve. El verificador no espera a que pase:
> le da a `esperar()` un mutex espía que, en el instante en que lo sueltan, anota cuántos hilos hay en
> la cola. Si es 0, la ventana existe, aunque en esa corrida nadie la haya aprovechado.

## Conexión con la teoría y los TPs

- Apuntes: "Un monitor guiado por una red de Petri" (apunte teórico) (el algoritmo de
  `dispararTransicion` y el mutex que pasa "de mano en mano") y la tabla de políticas.
- Parciales: "Las acciones ... ¿se realizan adentro o afuera del monitor?" (afuera, para no
  serializar) y "¿Cómo deben ejecutarse los métodos de un monitor?" (en exclusión mutua).
- TP final: `ConditionQueues.waitOn` y `signal` son `esperar` y `despertarUno`, y el `Monitor` solo
  suelta el mutex cuando no despertó a nadie.
