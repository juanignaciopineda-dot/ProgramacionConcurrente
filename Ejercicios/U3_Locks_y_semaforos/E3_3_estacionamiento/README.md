# Ejercicio 3.3 — Estacionamiento con semáforos

> Unidad 3 · Semáforos · Nivel ★★ · ~40 min

## Objetivo

Un estacionamiento con N lugares numerados. Los autos que llegan con todo lleno esperan a que salga
alguno. Es el ejemplo de las tres impresoras de la teoría, con dos semáforos que hacen dos trabajos
distintos: uno de **N permisos** que cuenta cuántos lugares quedan, y uno **binario** que protege
cuál está libre. Además vas a ver en vivo lo que significa que un semáforo **no tenga dueño**.

```
  auto ──acquire()──► lugaresLibres (N) ──► mutex (1): elegir un lugar libre ──► estacionado
  auto ◄─release()─── lugaresLibres     ◄── mutex: marcar el lugar libre    ◄── sale
```

## Conceptos

- Semáforo de N permisos: deja pasar a N a la vez, y el resto duerme en su cola.
- Semáforo binario como mutex.
- El semáforo garantiza que **haya** un lugar; el mutex garantiza que dos autos no se lleven **el
  mismo**.
- `tryAcquire(tiempo, unidad)` para rendirse.
- `acquireUninterruptibly()`: cuándo conviene que una espera no se pueda interrumpir.
- Sin dueño: cualquiera puede hacer `release()`, y un `release()` de más agranda el estacionamiento.

## Archivos

| Archivo                | Qué hacer                                                                       |
| ---------------------- | ------------------------------------------------------------------------------- |
| `Estacionamiento.java` | Constructor, `entrar`, `intentarEntrar`, `ocuparLugar`, `salir` y `libres`      |
| `Verificador.java`     | No se toca                                                                      |

## Orden sugerido

1. Constructor y `libres`.
2. `ocuparLugar` y `entrar`.
3. `salir`, con sus dos validaciones.
4. `intentarEntrar`.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

## Restricciones

Solo `Semaphore`. Nada de `synchronized`, `wait`/`notify`, `Lock` ni clases `Atomic...` (el
verificador lo controla).

## Para pensar

1. Sacá la validación de `salir` (que libere aunque el lugar estuviera vacío) y corré el verificador.
   ¿Qué caso falla? ¿Por qué con un `ReentrantLock` este error ni siquiera se podría cometer?
2. Sacá el `mutex` de `ocuparLugar`. ¿Falla siempre el estrés? ¿Por qué un error de exclusión mutua
   puede pasar desapercibido en una corrida y aparecer en otra?
3. En `ocuparLugar`, ¿qué pasaría con el permiso de `lugaresLibres` si usaras `mutex.acquire()` y al
   hilo lo interrumpieran justo ahí? ¿Y si lo interrumpen mientras espera en `lugaresLibres.acquire()`?
4. Modelá el estacionamiento de 3 lugares como red de Petri, con un lugar "libres" de 3 tokens. ¿Qué
   P-invariante tiene? ¿A qué llamadas del código corresponden las dos transiciones?
5. Dibujá el diagrama de secuencia de 4 autos y 3 lugares, como el de las tres impresoras de la
   filmina: un fragmento `par` acotado por el semáforo y un `critical` adentro de `ocuparLugar`.

## Pistas

> [!tip]- Pista 1: `entrar` e `intentarEntrar`
> ```java
> lugaresLibres.acquire();
> return ocuparLugar();
> ```
> ```java
> if (!lugaresLibres.tryAcquire(esperaMaxMs, TimeUnit.MILLISECONDS)) {
>     return -1;
> }
> return ocuparLugar();
> ```

> [!tip]- Pista 2: `salir`
> ```java
> if (lugar < 0 || lugar >= ocupado.length) {
>     throw new IllegalArgumentException("no existe el lugar " + lugar);
> }
> mutex.acquireUninterruptibly();
> try {
>     if (!ocupado[lugar]) {
>         throw new IllegalStateException("el lugar " + lugar + " ya estaba libre");
>     }
>     ocupado[lugar] = false;
> } finally {
>     mutex.release();
> }
> lugaresLibres.release();      // solo se llega acá si el lugar estaba ocupado
> ```

> [!tip]- Pista 3: respuesta a la pregunta 1
> Un `ReentrantLock` sabe qué hilo lo tiene. Si otro hilo hace `unlock()`, lanza
> `IllegalMonitorStateException`. Un semáforo es solo un contador: `release()` suma uno, lo haga quien
> lo haga, y no se puede saber si "le correspondía". Esa libertad es lo que lo hace útil para avisar
> entre hilos (el que libera no es el que tomó). Y es también lo que obliga a que el código valide.

## Conexión con la teoría y los TPs

- Apuntes: "Semáforos" (cómo funciona, "Semáforo binario" y "Los semáforos no tienen dueño") y
  "Semáforo de N permisos" (las tres impresoras y su diagrama de secuencia).
- Redes de Petri: "Exclusión mutua con N recursos". `new Semaphore(3)` es el lugar con tres tokens, y
  el invariante es $M(\text{en uso}) + M(\text{libres}) = 3$.
- TP1: reservar una de varias impresoras es este mismo problema: contar cuántas hay disponibles y
  elegir **cuál** sin que dos órdenes se lleven la misma.
