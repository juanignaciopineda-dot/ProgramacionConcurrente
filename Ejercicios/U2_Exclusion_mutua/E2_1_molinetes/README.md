# Ejercicio 2.1 — Molinetes: `synchronized` de instancia y de clase

> Unidad 2 · Exclusión mutua · Nivel ★★ · ~30 min

## Objetivo

Ver una condición de carrera en vivo y corregirla. La vuelta de tuerca: cada molinete tiene su propio
contador, pero todos suman a un total del estadio que es `static`. Proteger los dos con el monitor
correcto es justamente la trampa de "`static` y `synchronized`" de los apuntes.

```
  molinete 1 ── pasadas ──┐
  molinete 2 ── pasadas ──┼──► totalEstadio (static, compartido por todos)
  molinete 3 ── pasadas ──┘
```

## Conceptos

- Condición de carrera: `x++` son tres operaciones (leer, sumar, escribir).
- Sección crítica y exclusión mutua.
- El monitor de `this` vs el monitor de la clase (`Molinete.class`, `static synchronized`).
- Leer bajo el mismo monitor que se escribe (visibilidad, no solo atomicidad).

## Archivos

| Archivo                 | Qué hacer                                                    |
| ----------------------- | ------------------------------------------------------------ |
| `MolineteInseguro.java` | Nada: está mal a propósito y sirve para la demostración      |
| `Molinete.java`         | `registrarPasada`, `getPasadas`, `getTotalEstadio`, `reiniciarTotal` |
| `Verificador.java`      | No se toca                                                   |

## Orden sugerido

1. Corré el verificador sin tocar nada y mirá la demostración: cuántas pasadas pierde
   `MolineteInseguro`.
2. **A propósito mal:** implementá `registrarPasada` como `public synchronized void` con los dos `++`
   adentro, y los getters como `synchronized`. Corré el verificador: ¿qué caso falla?
3. Corregilo como dice el Javadoc.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

## Para pensar

1. Explicá con un diagrama de dos hilos intercalados cómo se pierde una pasada en `MolineteInseguro`.
2. En el paso 2, ¿por qué cada molinete daba bien pero el total no?
3. ¿Alcanzaría con declarar `totalEstadio` como `volatile`? ¿Qué garantiza `volatile` y qué no?
4. ¿Qué pasaría si usaras un solo `synchronized (Molinete.class)` para los dos contadores? ¿Sería
   correcto? ¿Qué se pierde?
5. La demostración pierde una cantidad distinta de pasadas en cada corrida, y a veces ninguna. ¿Qué
   dice eso sobre probar programas concurrentes con tests? (Pensá en el Therac-25 de la Introducción.)

## Pistas

> [!tip]- Pista 1: dos bloques
> ```java
> synchronized (this) { pasadas++; }
> synchronized (Molinete.class) { totalEstadio++; }
> ```

> [!tip]- Pista 2: respuesta a la pregunta 3
> `volatile` garantiza **visibilidad** (cada lectura ve la última escritura), pero no **atomicidad**:
> `totalEstadio++` sigue siendo leer-sumar-escribir, y dos hilos pueden intercalarse en el medio.

## Conexión con la teoría y los TPs

- Apuntes: "Sincronización con `synchronized`" → "`synchronized` en un método" (el detalle sobre
  `static`) y "Thread safe" (la pregunta de si los atributos `static` son thread safe).
- TP1: el contador de usos de cada impresora y los contadores globales del resumen tienen exactamente
  esta estructura: uno por objeto y uno para todo el sistema, y el invariante 8 pide que coincidan.
