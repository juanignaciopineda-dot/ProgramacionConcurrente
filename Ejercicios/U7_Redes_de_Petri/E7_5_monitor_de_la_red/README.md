# Ejercicio 7.5 — El monitor de la red: productor/consumidor ejecutado como red de Petri

> Unidad 7 · Redes de Petri · Nivel ★★★★ · ~60 min

## Objetivo

Juntar todo: modelar el productor/consumidor con buffer acotado como red de Petri y ejecutarlo con
un **monitor guiado por la red**, sin tiempo. Es el corazón del TP final. Cada hilo hace su trabajo
como una secuencia de disparos, que es su T-invariante: el productor dispara TOMAR_HUECO y
DEPOSITAR. La sincronización está toda en la red. Si el buffer está lleno, TOMAR_HUECO no está
sensibilizada y el productor espera en la cola de esa transición, sin una línea de control.

Es el paso del buffer de tres semáforos (3.4) al monitor del TP final: `Huecos`, `Productos` y `Mutex`
son esos tres semáforos, ahora lugares de una red.

```
  dispararTransicion(t):
      mutex ─ while (!sensibilizada(t)) colas[t].esperar(mutex)
            ─ disparar ─ despertables = sensibilizadas ∧ con gente
            ─ ¿alguna?  sí → colas[politica.elegir(despertables)].despertarUno()   (el mutex pasa)
                        no → mutex.release()
```

## Antes de programar

Respondé, mirando la tabla de `ProductorConsumidor`:

1. ¿Qué transiciones dispara el productor y cuáles el consumidor? ¿Cuál es el T-invariante de cada
   uno?
2. ¿Cuáles son los P-invariantes de la red? ¿Qué propiedad del buffer prueba cada uno? (Con eso
   escribís `pInvariantes`.)

## Conceptos

- Modelar un problema conocido como red: lugares, transiciones y marcado inicial.
- La tabla de componentes del monitor:
  - **Mutex:** la exclusión mutua de entrada.
  - **Red:** la lógica.
  - **Colas:** una por transición.
  - **Política:** la estrategia.
  - **Monitor:** el gestor.
- Despertables `= Vs ∧ Vc`: sensibilizadas **y** con hilos esperando.
- *Signal and exit*: el mutex pasa de mano y el despertado encuentra su transición sensibilizada.
- La política resuelve los conflictos: solo se la consulta cuando hay a quién despertar.
- Los P-invariantes controlados en cada disparo, como en el TP.
- Los hilos no saben nada de sincronización: solo qué transiciones disparan.

## Archivos

| Archivo                    | Qué hacer                                       |
| -------------------------- | ----------------------------------------------- |
| `ProductorConsumidor.java` | `crear` (la red) y `pInvariantes`               |
| `MonitorDeRed.java`        | Constructor y `dispararTransicion`              |
| `Productor.java`           | `run()`                                         |
| `Consumidor.java`          | `run()`                                         |
| `RedDePetri.java`          | Ya está hecha (la del 7.1)                      |
| `ColaDeCondicion.java`     | Ya está hecha (la del 5.3)                      |
| `Politica.java`            | Ya está hecha (la interfaz)                     |
| `PoliticaPrimera.java`     | Ya está hecha: despierta a la primera que encuentra |
| `Verificador.java`         | No se toca                                      |

## Orden sugerido

1. `ProductorConsumidor.crear` y `pInvariantes`: los dos primeros casos.
2. `MonitorDeRed`, siguiendo el algoritmo del Javadoc, que es el de los apuntes.
3. `Productor` y `Consumidor`.

## Cómo correrlo

Desde esta carpeta:

```powershell
java src/Verificador.java
```

En el último caso trabajan 2 productores y 2 consumidores con un buffer de 3.
- **Invariantes en cada disparo:** la red que usa el verificador revisa los cuatro P-invariantes.
  El de la sección crítica es el que prueba que nunca hay dos hilos adentro.
- **Mutex al final:** tiene que haber quedado con un solo permiso. Si alguien lo soltó después de
  pasárselo a otro, sobran.

## Restricciones

`MonitorDeRed` usa `Semaphore`, la `ColaDeCondicion`, la `RedDePetri` y la `Politica`.
`Productor` y `Consumidor` solo usan el monitor. Nada de `synchronized`, locks ni `wait`/`notify`
(el verificador lo controla).

## Para pensar

1. Partí `TOMAR_HUECO` en dos transiciones: una que toma el mutex y otra que después toma el hueco.
   ¿Aparece el mismo deadlock que en el experimento A del ejercicio 3.4? Encontralo con el
   `Explorador` del 7.3: ¿cuál es el contraejemplo?
2. ¿Por qué el hilo que despierta a otro **no** hace `release()` del mutex? ¿Qué podría pasar si lo
   hiciera? Pensá en un tercer hilo en la cola de entrada, y en si la transición del despertado sigue
   sensibilizada cuando por fin le toca.
3. ¿Por qué se despierta solo a hilos cuya transición está sensibilizada, y no a cualquiera con gente
   en la cola? Si se despertara a uno que no puede disparar, ¿quién se queda sin despertar?
4. Pregunta de parcial: "¿Cómo solucionamos el problema del conflicto efectivo en un conjunto de
   transiciones?". Respondela con el caso de la política del verificador. En esta red, cuando un
   productor y un consumidor esperan el mutex y se libera, ¿quién decide a cuál se despierta?
   Proponé dos políticas y qué propiedad favorece cada una.
5. Preguntas de parcial: ¿qué relación tiene que la red sea **acotada** con los buffers del programa?
   ¿Y que sea **segura** con los hilos, si hay un hilo por plaza?
6. Hacé el diagrama de secuencia de un productor y un consumidor con el buffer vacío: el consumidor
   espera, el productor deposita y le pasa el mutex. Es el diagrama "monitor sin tiempo" que pedía el
   TP.

## Pistas

> [!tip]- Pista 1: respuestas de "Antes de programar"
> El productor dispara T0 (TOMAR_HUECO) y T1 (DEPOSITAR): su T-invariante es `(1, 1, 0, 0)`. El
> consumidor, T2 y T3: `(0, 0, 1, 1)`. Los P-invariantes son cuatro:
> - **Productores:** `M(P0) + M(P1)`.
> - **Consumidores:** `M(P5) + M(P6)`.
> - **Sección crítica:** `M(Mutex) + M(P1) + M(P6) = 1`, así que nunca hay dos adentro.
> - **Buffer:** `M(Huecos) + M(Productos) + M(P1) + M(P6) = N`, así que nunca hay más de N elementos.

> [!tip]- Pista 2: la mitad de `dispararTransicion`
> ```java
> mutex.acquireUninterruptibly();
> while (!red.estaSensibilizada(t)) {
>     colas[t].esperar(mutex);
> }
> red.disparar(t);
> registrarDisparo(t);
> ```

> [!tip]- Pista 3: la otra mitad
> ```java
> boolean[] despertables = new boolean[colas.length];
> boolean alguna = false;
> for (int u = 0; u < colas.length; u++) {
>     despertables[u] = red.estaSensibilizada(u) && colas[u].esperando() > 0;
>     alguna |= despertables[u];
> }
> if (alguna) {
>     colas[politica.elegir(despertables)].despertarUno();
> } else {
>     mutex.release();
> }
> ```

> [!tip]- Pista 4: respuesta a la pregunta 2
> Si el que despierta suelta el mutex, un tercer hilo de la cola de entrada puede entrar antes que el
> despertado y disparar una transición que le quita el sensibilizado (otro consumidor que se lleva el
> elemento). Peor todavía: en este monitor el despertado no pide el mutex, cree que se lo pasaron, así
> que quedan dos hilos adentro a la vez, y la red, que no es thread-safe, se corrompe. Es lo que el
> verificador ve como permisos de más en el mutex.

## Conexión con la teoría y los TPs

- Apuntes: en el teórico, "Un monitor guiado por una red de Petri" (la tabla
  de componentes y el algoritmo de `dispararTransicion`) y, en "Modelos básicos", el buffer acotado.
- Unidades anteriores: el buffer de tres semáforos (3.4), la `ColaDeCondicion` y *signal and exit*
  (5.3), las políticas (5.5) y los invariantes (7.2).
- Parciales: "¿Cómo se relacionan las transiciones con las variables de condición en un monitor?"
  (biunívoca) y "¿Cómo solucionamos el problema del conflicto efectivo?" (con una política).
- TP final: `Monitor.fireTransition`, con el mismo esqueleto más el tiempo, el log y el fin de la
  ejecución. Esa versión completa queda para el integrador de la Unidad 8.
