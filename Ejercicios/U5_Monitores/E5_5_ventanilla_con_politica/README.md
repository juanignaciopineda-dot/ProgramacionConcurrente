# Ejercicio 5.5 — Ventanilla con política

> Unidad 5 · Monitores · Nivel ★★★★ · ~50 min

## Objetivo

Una ventanilla que atiende de a un cliente, con dos tipos de cliente (prioritarios y comunes) que
esperan en colas distintas. Cuando la ventanilla se libera y hay gente en las dos colas, **una
política** decide de cuál sale el siguiente. Es la estructura del monitor del TP final sin la red de
Petri:

| Pieza | Acá | En el TP final |
| --- | --- | --- |
| mutex | `Semaphore(1, true)` | `Semaphore(1)` |
| colas de condición | una por tipo de cliente | una por transición |
| estado | `ocupada` | el marcado de la red |
| política | `Politica.elegir(conGente)` | elige a qué hilo despertar |

Vas a escribir dos políticas y el monitor. Con prioridad estricta vas a ver la postergación
indefinida de los comunes, y con turnos, que desaparece.

## Conceptos

- Una variable de condición por tipo de espera.
- La política como objeto aparte: cambia la estrategia sin tocar el monitor.
- Las políticas de las colas de condición resuelven los conflictos (pregunta de parcial).
- *Signal and exit*: la ventanilla pasa de mano, ocupada, al despertado.
- Prioridad estricta contra turnos: rendimiento predecible contra equidad.

## Archivos

| Archivo                  | Qué hacer                                           |
| ------------------------ | --------------------------------------------------- |
| `PoliticaPrioridad.java` | `elegir`                                            |
| `PoliticaAlternada.java` | `elegir`                                            |
| `Ventanilla.java`        | Constructor, `solicitar`, `liberar` y `atender`     |
| `Politica.java`          | Ya está hecha (la interfaz)                         |
| `PoliticaAleatoria.java` | Ya está hecha: el modelo para las otras dos         |
| `ColaDeCondicion.java`   | Ya está hecha: es la del 5.3                        |
| `Verificador.java`       | No se toca                                          |

## Orden sugerido

1. Las dos políticas: los dos primeros casos no necesitan la ventanilla.
2. `Ventanilla`: constructor, `solicitar` y `liberar`.
3. `atender`.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

El último caso pone a tres prioritarios a usar la ventanilla sin parar y mide cuánto espera un común:
- Con turnos, unas decenas de milisegundos.
- Con prioridad estricta, normalmente no pasa en el segundo y medio que espera el verificador. Eso se
  muestra como dato y no cuenta como falla: es lo que la política promete.

## Restricciones

`Semaphore` y la `ColaDeCondicion`. Nada de `synchronized`, locks ni `wait`/`notify` (el verificador
lo controla).

## Para pensar

1. Pregunta de parcial: "¿Qué resuelven las políticas de las colas de condición en un monitor?".
   Respondela con este ejercicio. ¿En qué se diferencia de lo que resuelven las políticas de
   reactivación del 5.4?
2. Con `PoliticaPrioridad(0)` y prioritarios sin parar, ¿el común se queda esperando para siempre?
   ¿Hay deadlock? ¿Qué propiedad de los requisitos de exclusión mutua se viola?
3. En `liberar`, ¿qué pasaría si, después de despertar a alguien, pusieras `ocupada = false` y
   soltaras el mutex? ¿Qué caso falla?
4. Inventá una tercera política que mezcle las dos: prioridad para los prioritarios, pero que un
   común no espere más de K atenciones. ¿Qué estado tiene que guardar?
5. Pregunta de parcial: "¿Cómo se relacionan las transiciones con las variables de condición en un
   monitor?". La palabra que pide la corrección es "biunívoca". ¿Qué sería acá la "transición" de
   cada cola?

## Pistas

> [!tip]- Pista 1: `PoliticaAlternada`
> ```java
> int n = conGente.length;
> for (int paso = 1; paso <= n; paso++) {
>     int i = Math.floorMod(ultima + paso, n);
>     if (conGente[i]) {
>         ultima = i;
>         return i;
>     }
> }
> throw new IllegalArgumentException("ninguna cola tiene gente");
> ```

> [!tip]- Pista 2: `liberar`
> ```java
> mutex.acquireUninterruptibly();
> if (!ocupada) {
>     mutex.release();
>     throw new IllegalStateException("la ventanilla no estaba ocupada");
> }
> boolean[] conGente = new boolean[colas.length];
> boolean alguien = false;
> for (int t = 0; t < colas.length; t++) {
>     conGente[t] = colas[t].esperando() > 0;
>     alguien |= conGente[t];
> }
> if (!alguien) {
>     ocupada = false;
>     mutex.release();
>     return;
> }
> colas[politica.elegir(conGente)].despertarUno();   // ocupada y el mutex pasan al despertado
> ```

> [!tip]- Pista 3: respuesta a la pregunta 1
> Resuelven **los conflictos**: cuando hay varias colas con hilos que podrían seguir (en el TP, varias
> transiciones sensibilizadas con hilos esperando) y solo uno puede, la política elige cuál. Las de
> **reactivación** resuelven otra cosa, la secuencia de acceso al monitor entre las colas de entrada,
> condición y cortesía.

## Conexión con la teoría y los TPs

- Apuntes: "Un monitor guiado por una red de Petri" (apunte teórico): la tabla de componentes (mutex, red, colas, política, gestor) y
  el paso de `seleccionarHilo(m)` del algoritmo.
- Parciales: "¿Qué resuelven las políticas de las colas de condición?" y "¿Cómo se relacionan las
  transiciones con las variables de condición?".
- TP final: las políticas `Random` y `Priority` del grupo. `PoliticaAleatoria` es la primera, y
  `PoliticaPrioridad` una versión de la segunda.
