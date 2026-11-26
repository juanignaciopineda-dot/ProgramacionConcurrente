# Ejercicio 3.2 — Catálogo de precios con `ReadWriteLock`

> Unidad 3 · Locks · Nivel ★★ · ~45 min

## Objetivo

Un catálogo de precios que se consulta todo el tiempo y se modifica poco: el problema de **lectores y
escritores**. Con un `ReentrantReadWriteLock`, varios lectores entran a la vez y el escritor entra
solo. El último método es una caché: "si el precio no está, calculalo y guardalo". Ahí aparece la
trampa típica del `ReadWriteLock`, que es querer pasar del candado de lectura al de escritura.

```
  lectores:   ──[leer]──            escritor:  ──────────[escribir]──────────
              ──[leer]──                        (espera a que no quede ningún lector,
              ──[leer]──                         y mientras escribe no entra nadie)
```

## Conceptos

- `readLock()` y `writeLock()`: dos candados que trabajan juntos.
- Por qué los lectores no se esperan entre sí, y por qué el escritor espera a todos.
- Una operación compuesta (aumentar todos los precios) va en **una** sola sección crítica.
- Devolver una copia, no el mapa interno.
- No se puede "ascender" del candado de lectura al de escritura: el hilo se traba esperándose a sí
  mismo.
- Volver a chequear después de conseguir el candado de escritura.

## Archivos

| Archivo            | Qué hacer                                                                |
| ------------------ | ------------------------------------------------------------------------ |
| `Catalogo.java`    | `precio`, `actualizar`, `aplicarAumento`, `copia` y `obtenerOCalcular`   |
| `Verificador.java` | No se toca                                                               |

`simularLectura()` y `simularEscritura()` ya están hechos: tardan lo que diga la demora y controlan
que tengas el candado correcto. Si escribís con el de lectura, te lo dicen.

## Orden sugerido

1. `precio` y `actualizar`.
2. `copia` y `aplicarAumento`.
3. `obtenerOCalcular`, siguiendo las dos fases del Javadoc.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

El verificador mide tiempos con accesos de 150 ms:
- 4 lecturas a la vez tienen que tardar unos 150 ms en total, porque entran juntas.
- 2 escrituras tienen que tardar unos 300 ms, porque van de a una.
- Con una escritura en curso, las lecturas esperan a que termine.

## Restricciones

Solo `ReentrantReadWriteLock` y colecciones comunes. Nada de `synchronized`, `Semaphore`,
`ConcurrentHashMap`, `Collections.synchronized...` ni `StampedLock` (el verificador lo controla).

## Para pensar

1. Cambiá `precio` para que use el candado de escritura. ¿Qué caso falla y con qué tiempo? ¿Qué
   resultado sigue siendo correcto?
2. Agregá un método que sume 1 a un precio usando el candado de **lectura** (ver "Romper el
   `ReadWriteLock`", más abajo). Con 5 hilos que lo llamen 1000 veces, ¿cuánto vale el precio al
   final? ¿Qué garantía se rompió?
3. En `obtenerOCalcular`, mové el `writeLock().lock()` adentro del bloque del `readLock()` (antes de
   soltarlo). ¿Qué pasa? ¿Por qué el verificador dice `[COLGADO]` pero no puede decirte quién espera a
   quién?
4. Sacá el segundo chequeo de la fase 2 de `obtenerOCalcular`. ¿Cuántas veces se calcula el precio con
   8 hilos? ¿En qué caso real eso sería un problema y en cuál daría igual?
5. Escenarios de la filmina: (A) una base que se consulta todo el tiempo y casi nunca cambia, y (B) una
   en la que los precios cambian todo el tiempo y casi nadie consulta. ¿En cuál conviene el
   `ReadWriteLock`? ¿Por qué en el otro puede salir más caro que un `synchronized`?

### Más para pensar: romper el `ReadWriteLock`

Sobre el `PricesInfo` del apunte práctico (o sobre tu catálogo), agregá este método:

```java
public void bumpPrice1() {
    lock.readLock().lock();
    price1 = price1 + 1;      // ¡escribe con el candado de LECTURA!
    lock.readLock().unlock();
}
```

1. ¿Compila? ¿Da error en tiempo de ejecución?
2. Con 5 hilos llamando a este método 1000 veces cada uno, ¿cuánto vale `price1` al final? ¿Por qué
   no vale 5000?
3. ¿Qué garantía del `ReadWriteLock` se violó exactamente?

> [!tip]- Pista
> El `readLock` deja entrar a varios hilos a la vez. Volvé a la descomposición de `contador++` en
> tres acciones atómicas (leer, sumar, escribir).

## Pistas

> [!tip]- Pista 1: `precio`
> ```java
> candado.readLock().lock();
> try {
>     simularLectura();
>     return Optional.ofNullable(precios.get(producto));
> } finally {
>     candado.readLock().unlock();
> }
> ```

> [!tip]- Pista 2: la estructura de `obtenerOCalcular`
> ```java
> candado.readLock().lock();
> try {
>     simularLectura();
>     Double precio = precios.get(producto);
>     if (precio != null) {
>         return precio;
>     }
> } finally {
>     candado.readLock().unlock();      // se suelta ANTES de pedir el de escritura
> }
> candado.writeLock().lock();
> try {
>     simularEscritura();
>     // volver a buscar: ¿lo calculó otro mientras esperábamos?
>     ...
> } finally {
>     candado.writeLock().unlock();
> }
> ```

> [!tip]- Pista 3: respuesta a la pregunta 3
> El hilo tiene el candado de lectura y pide el de escritura. El de escritura espera a que no quede
> ningún lector, y el propio hilo es uno. Nadie lo va a soltar: se traba solo. Con 8 hilos pasa lo
> mismo, pero los 8 a la vez. La JVM no lo ve como deadlock porque el candado de lectura no tiene un
> dueño único. Lo puede tener mucha gente a la vez, así que no hay una "espera circular" entre dueños
> que detectar.

## Conexión con la teoría y los TPs

- Apuntes: "ReadWriteLock" (el ejemplo `PricesInfo`, "La responsabilidad es del programador" y
  "¿Cuándo conviene un `ReadWriteLock`?"), el caso (b) de "Elegir el mecanismo" del ejercicio 3.1 y
  "Romper el `ReadWriteLock`", más arriba.
- Redes de Petri: lectores/escritores es uno de los modelos básicos del libro de la cátedra (§1.5.4).
  En el modelo clásico hay un lugar con N tokens: cada lector toma uno, y el escritor los toma
  **todos** de un saque (un arco de peso N). Es lo mismo que hace, con semáforos, un escritor que
  pide todos los permisos de un semáforo de N.
