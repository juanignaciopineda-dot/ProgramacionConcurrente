# Ejercicio 7.2 — Invariantes de lugar y de transición

> Unidad 7 · Redes de Petri · Nivel ★★★ · ~40 min

## Objetivo

Los invariantes son la herramienta que permite **probar** propiedades de una red sin recorrer sus
estados. Vas a escribir:
- Las cuentas para comprobar P-invariantes (`yᵀ·C = 0`) y T-invariantes (`C·x = 0`).
- El vector característico de una secuencia.
- Un control de si una secuencia es repetitiva.
- Una red que revisa sus P-invariantes en cada disparo, como la del TP final.

La prueba de fuego es la barbería de los apuntes, con sus seis invariantes.

```
  P-invariante:  yᵀ · C = 0   ⇒   yᵀ · M = yᵀ · M0  en todo marcado alcanzable
  T-invariante:  C · x = 0    ⇒   una secuencia con ese vector vuelve al mismo marcado
```

## Conceptos

- P-invariante y su constante; el soporte del invariante (componente conservador).
- T-invariante y secuencia repetitiva, y por qué la recíproca no vale.
- Vector característico de una secuencia.
- Control de invariantes en tiempo de ejecución: para qué sirve si el álgebra ya lo garantiza.

## Archivos

| Archivo            | Qué hacer                                                                         |
| ------------------ | --------------------------------------------------------------------------------- |
| `Invariantes.java` | `esPInvariante`, `esTInvariante`, `valor`, `vectorCaracteristico` y `esRepetitiva` |
| `RedVigilada.java` | Constructor y `disparar`                                                          |
| `RedDePetri.java`  | Ya está hecha (es la del 7.1)                                                     |
| `Verificador.java` | No se toca                                                                        |

## Orden sugerido

1. `esPInvariante`, `esTInvariante` y `valor`.
2. `vectorCaracteristico` y `esRepetitiva`.
3. `RedVigilada`.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

## Para pensar

1. Pregunta de parcial: "La constante de marcado de un invariante de plaza, ¿depende de la marca, de
   la estructura de la red o de ambas?". ¿Y el invariante en sí? Mirá qué usa cada método: ¿cuál
   necesita el marcado?
2. Con los seis invariantes de la barbería, demostrá sin recorrer estados que nunca hay dos clientes
   en la puerta de entrada y que nunca hay dos cobros a la vez.
3. `(1, 1, 1, 1)` es un T-invariante de la exclusión mutua. ¿Toda secuencia que use cada transición una
   vez es repetitiva? Probá `T1 T0 T3 T2` con `esRepetitiva`. ¿Qué pasa y por qué?
4. Si la red está bien programada, `RedVigilada` nunca encuentra un invariante violado. ¿Para qué la
   usaba entonces el TP final? Pensá en qué errores de un programa concurrente se detectarían en el
   primer disparo que los produce.
5. El ciclo de las cuatro estaciones tiene el invariante `M(p1) + M(p2) + M(p3) + M(p4) = 1`. Es una
   pregunta de parcial: ¿cómo lo interpretás?

## Pistas

> [!tip]- Pista 1: `esPInvariante`
> ```java
> for (int t = 0; t < incidencia[0].length; t++) {
>     int suma = 0;
>     for (int p = 0; p < incidencia.length; p++) {
>         suma += y[p] * incidencia[p][t];
>     }
>     if (suma != 0) {
>         return false;
>     }
> }
> return true;
> ```
> `esTInvariante` es lo mismo con los índices al revés: para cada lugar, la suma sobre las
> transiciones.

> [!tip]- Pista 2: respuesta a la pregunta 1
> El invariante es **estructural**: `yᵀ·C = 0` solo mira la matriz de incidencia, no el marcado. La
> **constante** depende de las dos cosas: es `yᵀ·M0`, así que con otro marcado inicial el mismo
> invariante tiene otra constante. En la barbería, con 20 clientes en vez de 17, el de los clientes
> seguiría siendo invariante, pero valdría 22.

## Conexión con la teoría y los TPs

- Apuntes: "Invariantes: verificar sin recorrer" y "Ecuación fundamental e invariantes de marcado"
  (P-invariantes, T-invariantes y componentes repetitivos). En "El problema de la barbería", la
  sección "Invariantes".
- TP final: `PetriNet.checkPlaceInvariants`, que controla cada marcado nuevo antes de asignarlo.
