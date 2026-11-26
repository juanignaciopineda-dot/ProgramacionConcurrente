# Ejercicio 2.2 — Teatro: una llave por recurso, y el bug del `Integer`

> Unidad 2 · Exclusión mutua · Nivel ★★ · ~45 min

## Objetivo

Vender entradas de dos sectores independientes (platea y palco) sin sobreventa y **sin** que un sector
frene al otro. Para eso cada sector tiene su propia llave. Una operación toca los dos sectores y
necesita las dos llaves, siempre en el mismo orden. Además, hay que encontrar y corregir un bug de
sincronización escrito por "un compañero".

```
  venderPlatea ──► synchronized (llavePlatea) ──► libresPlatea
  venderPalco  ──► synchronized (llavePalco)  ──► libresPalco
  pasarAPalco  ──► synchronized (llavePlatea) { synchronized (llavePalco) { ... } }
```

## Conceptos

- `synchronized` en bloque con un objeto llave `private final Object`.
- Granularidad: un candado por recurso independiente.
- *Check-then-act*: chequear y modificar en la misma sección crítica.
- Tomar dos llaves anidadas en un orden fijo (prevención de deadlock).
- Sincronizar con el objeto equivocado: wrappers inmutables como `Integer`.

## Archivos

| Archivo                    | Qué hacer                                                         |
| -------------------------- | ----------------------------------------------------------------- |
| `Teatro.java`              | Declarar las dos llaves y completar los cinco métodos con `TODO`   |
| `ContadorDeFunciones.java` | Ya está escrito, **con un bug**: encontralo y corregilo            |
| `Verificador.java`         | No se toca                                                        |

## Orden sugerido

1. Las dos llaves, `venderPlatea` y `venderPalco`.
2. Los dos getters.
3. `pasarAPalco`.
4. `ContadorDeFunciones`: primero corré el verificador sin tocarlo y mirá cuántas funciones cuenta.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

El verificador mide tiempos: con cobros de 100 ms, 4 ventas de un mismo sector tienen que tardar unos
400 ms (van de a una) y 4 + 4 ventas de sectores distintos, también unos 400 ms (los sectores no se
esperan). Con una sola llave tardaría 800 ms.

## Para pensar

1. ¿Por qué el cobro va **adentro** de la sección crítica? ¿Qué pasaría si chequearas el lugar,
   soltaras la llave, cobraras y después descontaras?
2. ¿Qué ganás con dos llaves en lugar de `synchronized` en todos los métodos? ¿Cuándo no valdría la
   pena?
3. Explicá el bug de `ContadorDeFunciones`: ¿qué objeto es el candado en cada llamada? ¿Por qué con
   valores chicos (hasta 127) el bug casi no se nota? (Buscá "Integer cache".)
4. Supongamos un método `pasarAPlatea` que toma primero `llavePalco` y después `llavePlatea`. Dibujá la
   secuencia de dos hilos que lleva a un deadlock con `pasarAPalco`.
5. ¿Es un problema que `getLibresPlatea()` y `getLibresPalco()` se lean en dos momentos distintos?
   ¿Cuándo sí lo sería?

### Más para pensar: encontrar el bug

Un compañero escribió esta clase para contar accesos concurrentes:

```java
public class Contador {
    private Integer valor = 0;

    public void incrementar() {
        synchronized (valor) {
            valor = valor + 1;
        }
    }
}
```

1. ¿Por qué es el mismo bug que el de `ContadorDeFunciones`?
2. Proponé una corrección usando una llave que no cambie de identidad.
3. ¿Por qué con pocas ejecuciones el bug no se nota?

> [!tip]- Pista
> `Integer` es inmutable. `valor = valor + 1` no modifica el objeto, sino que crea uno nuevo y
> reasigna la referencia, así que el monitor que encuentra un segundo hilo en `synchronized(valor)`
> ya no es el mismo.

## Pistas

> [!tip]- Pista 1: estructura de `venderPlatea`
> ```java
> synchronized (llavePlatea) {
>     if (libresPlatea < cantidad) return false;
>     cobrar();
>     libresPlatea -= cantidad;
>     return true;
> }
> ```
> La validación de `cantidad < 1` puede ir antes del `synchronized`: no toca nada compartido.

> [!tip]- Pista 2: el bug
> `funciones = funciones + 1` crea un `Integer` nuevo. El próximo hilo que llegue va a sincronizar
> sobre **ese** objeto nuevo, mientras otro hilo todavía tiene el monitor del viejo: los dos entran a
> la vez. La llave tiene que ser un objeto que nunca cambie: `private final Object llave = new Object();`
> (y el contador, un `int` común). Si al compilar con `javac` viste el *warning* "attempt to
> synchronize on an instance of a value-based class", el compilador te estaba avisando.

## Conexión con la teoría y los TPs

- Apuntes: "Sincronizar un bloque en vez de un método completo" (la playa de estacionamiento),
  "Ejemplo: `Cinema` — dos recursos independientes con dos llaves" y "El error clásico: sincronizar
  con el objeto equivocado".
- TP1: reservar una impresora es *check-then-act* (buscar una `AVAILABLE` y marcarla `RESERVED` en la
  misma sección crítica), y el invariante 4 ("una impresora no puede estar asignada a dos órdenes")
  es la versión impresora de "no hay sobreventa".
