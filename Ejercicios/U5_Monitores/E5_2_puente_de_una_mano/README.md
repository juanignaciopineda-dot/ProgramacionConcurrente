# Ejercicio 5.2 — Puente de una mano

> Unidad 5 · Monitores · Nivel ★★★ · ~50 min

## Objetivo

Un puente angosto por el que pueden cruzar varios autos a la vez, pero todos en el mismo sentido. Es
un problema de monitor clásico. Hay que decidir el estado (cuántos cruzan, en qué sentido, cuántos
esperan de cada lado) y sacar de ahí la condición para entrar. Además, el monitor tiene que evitar
la **postergación indefinida**: si del norte no dejan de llegar autos, el del sur igual tiene que
poder cruzar.

```
  NORTE ◄── ▓▓▓ ▓▓▓ ▓▓▓ ──        SUR: espera en su cola
  (después de maxSeguidos, si alguien espera del otro lado, el norte cede el turno)
```

## Conceptos

- El estado de un monitor y las condiciones que se derivan de él.
- Una variable de condición por tipo de espera (una por sentido).
- `signalAll` cuando un cambio de estado habilita a varios a la vez.
- Postergación indefinida (inanición) contra deadlock: el sistema avanza, pero alguien no.
- La regla de la cátedra: dar prioridad a los que ya esperan sobre los que llegan.

## Archivos

| Archivo            | Qué hacer                                                   |
| ------------------ | ----------------------------------------------------------- |
| `Puente.java`      | Constructor, `puedeEntrar`, `entrar` y `salir`              |
| `Sentido.java`     | Ya está hecho                                               |
| `Verificador.java` | No se toca                                                  |

## Orden sugerido

1. Constructor.
2. `puedeEntrar`, siguiendo las dos reglas del Javadoc.
3. `entrar` y `salir`.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

El último caso deja a cuatro autos cruzando al norte sin parar y mide cuánto espera uno del sur. Con
el turno funcionando son unas decenas de milisegundos; sin el turno, no cruza nunca.

## Restricciones

`ReentrantLock` y `Condition`. Nada de `synchronized`, `wait`/`notify` ni semáforos (el verificador
lo controla).

## Para pensar

1. Sacá la parte de `maxSeguidos` de `puedeEntrar` (que entren siempre los del sentido que cruza).
   ¿Qué caso falla? ¿Por qué eso no es un deadlock?
2. ¿Por qué `salir` despierta a **todos** los que esperan, de los dos sentidos, y no solo a uno del
   otro lado? ¿Qué caso falla con `signal()`?
3. ¿Por qué el auto se suma a `esperando` antes de mirar `puedeEntrar`, y no recién cuando tiene que
   dormirse?
4. Supongamos que el único auto que esperaba del sur se cansa y se va (lo interrumpen) mientras los
   del norte ya no entraban porque había alguien esperando del otro lado. ¿Puede quedar algún auto
   esperando para siempre? ¿Qué agregarías en el `finally` de `entrar` para evitarlo?
5. Modelá el puente como red de Petri con dos autos por lado. ¿Qué lugar hace de "sentido actual"?
   ¿Cómo modelarías el límite de `maxSeguidos`?

## Pistas

> [!tip]- Pista 1: `puedeEntrar`
> ```java
> boolean esperanDelOtroLado = esperando.get(s.otro()) > 0;
> if (enElPuente == 0) {
>     return !esperanDelOtroLado || sentido != s;
> }
> return sentido == s && (seguidos < maxSeguidos || !esperanDelOtroLado);
> ```

> [!tip]- Pista 2: `entrar`
> ```java
> candado.lock();
> try {
>     esperando.merge(s, 1, Integer::sum);
>     try {
>         while (!puedeEntrar(s)) {
>             colas.get(s).await();
>         }
>     } finally {
>         esperando.merge(s, -1, Integer::sum);
>     }
>     if (enElPuente == 0) {
>         sentido = s;
>         seguidos = 0;
>     }
>     enElPuente++;
>     seguidos++;
> } finally {
>     candado.unlock();
> }
> ```

> [!tip]- Pista 3: respuesta a la pregunta 1
> Sin el límite, mientras quede al menos un auto del norte sobre el puente, siempre puede entrar otro
> del norte, así que el puente nunca se vacía y el del sur nunca cumple su condición. No es deadlock
> porque los del norte siguen cruzando: el sistema avanza, pero un proceso queda postergado
> indefinidamente. Es lo que los apuntes llaman inanición, y la razón de la regla "darle prioridad al
> que espera sobre los que llegan".

## Conexión con la teoría y los TPs

- Apuntes: "Definición" del monitor (la regla de diseño contra la postergación indefinida),
  "Variables de condición", y en la Introducción, "Problemas comunes de los programas concurrentes"
  (inanición contra deadlock).
- Es el mismo problema que el de lectores y escritores (3.2) con "lectores" de dos tipos que se
  excluyen entre sí.
