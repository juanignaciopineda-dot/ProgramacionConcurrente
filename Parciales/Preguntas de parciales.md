# Preguntas de parciales viejos

Transcripción de las preguntas que salieron en los parciales de Programación Concurrente (FCEFyN, UNC), con la respuesta que aceptó la cátedra y las notas sobre cómo corrigió. Reemplaza a los PDF y fotos de los parciales, que se sacaron del repositorio por peso.

Está generado a partir de los cuestionarios (`Primer Parcial/Cuestionario-Parcial-1.html` y `Segundo parcial/Cuestionario-Parcial-2.html`): sólo incluye las preguntas que salieron en un parcial, no las armadas con el apunte. "Salió en" indica el año y cuántas veces apareció; las preguntas repetidas entre años están unificadas.

## Primer parcial

Fuentes: parciales de 2020 y 2021 (varios intentos), recuperatorio 2021, "Respuestas escritas primer parcial", fotos de preguntas (1.jpg a 10.jpg y la imagen de WhatsApp de 2020) y los PDF sueltos ("Parcial 1 2020", "parcial1", "primer parcial", "Primer parcial(1)"). 63 preguntas.

### Concurrencia e interleaving

#### ¿Cómo se ejecutan los procesos concurrentes? (interleaving)

*Salió en: 2020 (x2)*

Se ejecutan **intercalando las acciones atómicas** que los componen. A eso se lo llama **interleaving**.

El orden en que se ejecutan las acciones atómicas **no puede decidirse** en general. Por el **no determinismo** en la elección de la próxima acción atómica, un mismo par de procesos puede tener ejecuciones distintas.

Puntos clave: Intercalan acciones atómicas (= interleaving) · El orden no se puede decidir de antemano · No determinismo: el mismo programa puede ejecutarse de formas distintas

> **Ojo con la corrección:** En 2020 alguien respondió "se ejecutan de manera simultánea… con synchronized" y la cátedra le corrigió con la definición de arriba. No digas "simultánea": decí "intercalando acciones atómicas".

#### ¿Qué diferencia hay entre la ejecución de procesos paralelos y procesos concurrentes? (interleaving)

*Salió en: 2021, 2023*

En la ejecución **paralela** los procesos se ejecutan **al mismo tiempo**, en procesadores o núcleos distintos.

En la **concurrente** las acciones atómicas se **intercalan**, y el interleaving puede ser **cualquiera, incluso el simultáneo**. El paralelismo es entonces un caso particular de la concurrencia.

Puntos clave: Paralelo: al mismo tiempo, en varios procesadores · Concurrente: interleaving de acciones atómicas · El interleaving puede ser cualquiera, incluso el simultáneo

> **Ojo con la corrección:** Comentario de la cátedra (2021): "en el paralelo los dos se ejecutan al mismo tiempo; en el concurrente el interleaving puede ser cualquiera, incluso el simultáneo".

#### ¿El orden en que se ejecutan las sentencias atómicas de un programa concurrente es siempre igual?

*Salió en: 2020 (x2)*

**No.** El orden **no puede decidirse**: la ejecución intercala las acciones atómicas y la elección de cuál sigue es **no determinista**. Dos corridas del mismo programa pueden tener interleavings distintos.

Puntos clave: Respuesta: No · No determinismo en la elección de la siguiente acción atómica

#### ¿Por qué diferentes interleavings de acciones atómicas pueden llevar a diferentes resultados o comportamientos de los sistemas concurrentes?

*Salió en: 2021, 2023*

Porque no se puede determinar (al menos no del todo) el **orden de ejecución** de los procesos o hilos: qué acción atómica sigue se elige de forma no determinista. Si los procesos **comparten datos**, el resultado depende de ese orden. Por ejemplo, dos hilos que hacen `x++` sobre la misma variable pueden perder una actualización.

Puntos clave: No se puede determinar el orden de ejecución · Hay datos o recursos compartidos · El resultado depende del orden en que se intercalan las acciones

#### ¿Si el sistema es determinístico, los interleavings de acciones atómicas pueden llevar a diferentes resultados o comportamientos?

*Salió en: 2021*

**No.** Si el sistema es determinístico, toda ejecución da el mismo resultado, sea cual sea el interleaving.

Puntos clave: Respuesta: No

> **Ojo con la corrección:** Es una trampa. En 2021 respondieron "Sí, porque no se sabe qué acción va a suceder primero" y sacaron 0; el comentario fue sólo "No". La premisa de la pregunta es que el sistema es determinístico.

#### ¿Cómo son las operaciones atómicas? (variante: ¿para qué son las operaciones atómicas?)

*Salió en: 2020 (x2), 2021 (x2)*

Una operación atómica se ejecuta **completa o no se ejecuta**: empieza y termina, o no empieza. No puede quedar a medio hacer ni ser interrumpida por otro hilo en el medio, así que nadie ve un estado intermedio.

**Para qué sirven:** garantizan que una acción sobre un recurso compartido no quede incompleta. Protegen los recursos compartidos.

Puntos clave: Todo o nada · Indivisible: no se interrumpe a la mitad · Sirven para proteger los recursos compartidos

#### ¿Por qué diferentes corridas en un programa concurrente con acciones no atómicas pueden llevar a diferentes resultados o comportamientos erróneos?

*Salió en: 2021 (x2)*

Por **condiciones de carrera** (race conditions). Si una acción no es atómica, otro hilo puede meterse en el medio (por ejemplo, entre el leer y el escribir de `x++`) y trabajar con un valor intermedio. Como el interleaving cambia de una corrida a otra, el resultado también cambia.

Puntos clave: Nombrar "condición de carrera" · Otro hilo se intercala en el medio de la acción · El interleaving cambia en cada corrida

> **Ojo con la corrección:** El comentario de la cátedra fue sólo "problemas de carrera". Nombralo explícitamente.

#### ¿Cuántos son los estados posibles que tendría un proceso compuesto por **dos hilos** los cuales tienen **2 estados** cada uno?

*Salió en: 2021 (x3), recup. 2021*

**4.** Con h hilos de e estados cada uno, el proceso tiene **e^h** estados: son todas las combinaciones posibles. Acá 2^2 = 4.

Puntos clave: Resultado: 4 · Regla: e^h (combinaciones)

#### ¿Cuántos son los estados posibles que tendría un proceso compuesto por **dos hilos** los cuales tienen **3 estados** cada uno?

*Salió en: 2021, 2023*

**9.** e^h = 3^2 = 9, que es la cantidad de combinaciones.

Puntos clave: Resultado: 9 = 3^2 · Mostrá la cuenta

> **Ojo con la corrección:** En 2023 la cátedra corrigió "3^2 = cantidad de combinaciones = 9". En 2021, a esta misma pregunta le pusieron "4" en el comentario, que parece copiado de la de 2×2. Respondé 9 y mostrá la cuenta.

#### ¿Cuántos son los estados posibles que tendría un proceso compuesto por **cuatro hilos** los cuales tienen **3 estados** cada uno?

*Salió en: 2021*

**81.** e^h = 3^4 = 81 (combinatoria).

Puntos clave: Resultado: 81 = 3^4

> **Ojo con la corrección:** Ojo: en 2021 el comentario de la cátedra fue "12" (4 × 3). Eso no cuadra con la regla que usan en las otras preguntas (2×2 = 4, 2×3 = 9 = 3^2). El resumen de respuestas de la carpeta pone 3^4 = 81. Respondé 81 con la cuenta, y si querés mencioná la regla.

### Testing, verificación y reactivos

#### En un programa concurrente el número de interleavings posibles es en general muy grande. ¿Es factible diseñar un testing para verificar su buen funcionamiento? (dé una razón)

*Salió en: 2020 (x3), 2021, recup. 2021*

**No.** Los casos a testear **crecen exponencialmente** con la cantidad de hilos y de acciones, así que no se pueden probar todos los interleavings. Además, un error puede aparecer sólo en un interleaving muy específico e improbable que el testing nunca ejercita.

En general es muy difícil razonar sobre programas concurrentes, y el testing difícilmente pueda darnos confianza de que funcionan correctamente.

Puntos clave: Respuesta: No · Los interleavings crecen exponencialmente · Un error puede darse sólo en un interleaving raro

> **Ojo con la corrección:** A dos personas que respondieron "sí" les dieron el punto igual, pero el comentario de la cátedra dice que **no**: "las instancias a testear crecen exponencialmente". Respondé "No" y dá la razón.

#### En un programa concurrente, ¿podría garantizar que es correcto realizando testing?

*Salió en: resumen de respuestas*

**No.** El testing puede mostrar la **presencia** de errores, pero nunca su **ausencia**. Cada corrida puede tener otro interleaving: un programa concurrente puede andar bien 10.000 veces y fallar una.

Puntos clave: No · El testing muestra presencia de errores, no ausencia · Cada ejecución puede ser distinta

#### ¿Cuáles son las limitaciones de la verificación automática de software? (variante: enumere dos)

*Salió en: 2021 (x2)*

Tiene **serias limitaciones**. Por ejemplo, decidir si un programa dado **termina o no** no es computable (es el problema de la parada). Sólo si imponemos **restricciones** sobre las propiedades que queremos verificar, algunas tareas pueden verificarse automáticamente.

Otras limitaciones que acepta la cátedra: la verificación se basa en un **modelo**, y el modelo tiene que ser correcto. Hay que terminar la codificación para empezar a testear, y si cambia la estructura del código hay que rehacer los casos. La fase más compleja es determinar las **trazas** particulares según las entradas y los estados del sistema.

Puntos clave: Decidir si un programa termina no es computable · Sólo restringiendo las propiedades se puede automatizar · Depende de que el modelo sea correcto · Hay que rehacer los casos si cambia el código

#### La semántica de un programa concurrente se basa en los sistemas de transición de estados, que podemos representar con grafos dirigidos. ¿Qué representan los nodos? ¿Y las aristas?

*Salió en: 2020*

Los **nodos** representan los **estados** del sistema. Las **aristas** (arcos dirigidos) representan las **transiciones**, es decir, las **acciones atómicas** que llevan de un estado a otro.

Una ejecución es un camino en ese grafo.

Puntos clave: Nodos = estados · Aristas = transiciones (acciones atómicas)

#### Los modelos abstractos de los programas que diseñamos se centran en características reales. ¿Por qué cree que éstas son importantes: datos, recursos, interacción con el medio y concurrencia?

*Salió en: 2020 (x2)*

Porque los sistemas que modelamos suelen ser **reactivos** y críticos: una falla puede tener consecuencias graves.

Por eso se introducen mecanismos para verificar que el modelo satisface **condiciones de seguridad** y **propiedades de progreso**. Para poder verificarlas, el modelo tiene que representar los **datos**, la **asignación de recursos**, la **interacción con el usuario o el medio** y la **concurrencia**.

Puntos clave: Sistemas reactivos y críticos: una falla es grave · Verificar condiciones de seguridad y de progreso · Para eso el modelo tiene que representar datos, recursos e interacción

#### ¿Cómo se realizan los mecanismos introducidos para verificar que el modelo satisface condiciones particulares de seguridad y las propiedades de progreso?

*Salió en: 2021*

Con **modelado formal**: el sistema se modela con **Redes de Petri** y **autómatas**. Sobre el modelo se verifican propiedades que se cumplen a lo largo de toda la ejecución del programa: **exclusión mutua**, **sincronización**, **invariantes de recursos**, **invariantes de acciones**, **concurrencia**, **persistencia** y **conflicto**.

Puntos clave: Modelado con Redes de Petri y autómatas · Propiedades que valen durante toda la ejecución · Ejemplos: exclusión mutua, sincronización, invariantes, persistencia, conflicto

#### Indique qué caracteriza a un programa reactivo (variante: ¿cuál es su característica principal?)

*Salió en: 2020, 2021 (x3), recup. 2021*

Su funcionalidad involucra la **interacción permanente con el ambiente** (y con otros procesos). En muchos casos **no computan un resultado**, y suele **no requerirse que terminen**.

Ejemplos: sistemas operativos, software de control, hardware.

Puntos clave: Interacción permanente con el ambiente · No computan un resultado final · No se espera que terminen · Ejemplo: un sistema operativo

#### ¿Qué entiende por starvation en un sistema de tiempo real? (variante: ¿cuándo se produce starvation?)

*Salió en: 2021 (x4), recup. 2021, 2023*

Cuando **no se atienden las solicitudes de un proceso (hilo) en el tiempo en que se requiere la respuesta**.

En general, starvation (inanición) es que un proceso queda esperando indefinidamente un recurso que nunca obtiene, porque siempre se lo ganan otros.

Puntos clave: Frase de la cátedra: no se atienden sus solicitudes en el tiempo requerido · Espera indefinida por un recurso

> **Ojo con la corrección:** La cátedra puso esa misma frase en el comentario todas las veces; memorizala. En 2021 respondieron "cuando hay exclusión mutua, errores en los algoritmos o fuga de recursos" y sacaron 0.

### Estados de hilos y procesos

#### Estados de un hilo: **Bloqueo** (explíquelo: de dónde viene, a dónde va y qué hace la MV)

*Salió en: 2020 (x2), 2021, recup. 2021*

**Viene de ejecución** (running). El hilo necesita esperar un suceso: un recurso ocupado, un lock, E/S o un `wait()`. La MV lo **saca del procesador** y lo deja **bloqueado**, sin consumir CPU, **esperando la señal de desbloqueo**. Mientras tanto la MV le da el procesador a otro hilo de la cola de listos.

Cuando llega la señal (se libera el recurso, un `notify()`, fin de la E/S), **va a listo/preparado**, no directamente a ejecución.

Puntos clave: Viene de EJECUCIÓN · Espera un suceso o señal sin usar CPU · La MV despacha otro hilo de la cola de listos · Va a LISTO, no directo a ejecución

> **Ojo con la corrección:** En 2020 alguien describió "el hilo entra a la sección crítica… y se bloquea" y sacó 0. Comentario de la cátedra: "Viene del estado ejecución; cuando recibe la señal de desbloqueo va a listo/preparado; lo que hace es esperar la condición de desbloqueo (señal)".

#### Estados de un hilo: **Desbloqueo** (explíquelo: de dónde viene, a dónde va y qué hace la MV)

*Salió en: 2020 (x2), 2021*

**Viene del estado bloqueado.** Cuando ocurre el suceso que esperaba (se libera el recurso, otro hilo hace `notify()`, termina la E/S), el hilo **recibe la señal de desbloqueo** y **pasa a listo/preparado**, a la cola de listos. Ahí espera a que la MV le asigne el procesador para volver a ejecución.

Puntos clave: Viene de BLOQUEADO · Recibe la señal de desbloqueo (el suceso que esperaba) · Va a LISTO/preparado

> **Ojo con la corrección:** Un comentario de 2021 dice "recibe la señal de desbloqueo (viene de ejecución) y va a listo/preparado", mezclando el bloqueo con el desbloqueo. Lo que la cátedra quiere oír: **señal de desbloqueo → listo/preparado**.

#### Estados de un proceso: **Bloqueo** (explíquelo: de dónde viene, a dónde va y qué hace el SO)

*Salió en: 2021, resumen*

**Viene de ejecución.** El proceso no puede avanzar porque necesita un evento: E/S, un recurso no disponible (variable, puerto de E/S, etc.). El SO lo saca de la CPU y lo deja **bloqueado (dormido)**, sin consumir procesador, y despacha otro proceso listo.

Cuando termina la operación u ocurre el evento, recibe la señal de desbloqueo y **pasa a listo/preparado**.

Puntos clave: Viene de EJECUCIÓN · Espera un evento o E/S sin usar CPU · El SO despacha otro proceso · Al desbloquearse va a LISTO

#### Estados de un proceso: **Desbloqueo** (explíquelo: de dónde viene, a dónde va y qué hace)

*Salió en: 2021 (x3)*

**Viene del estado bloqueado.** Cuando recibe la **señal de desbloqueo** (terminó la E/S, se liberó el recurso, ocurrió el evento), **va a listo/preparado**. Lo que hace mientras está bloqueado es **esperar la condición de desbloqueo (la señal)**.

Puntos clave: Viene de BLOQUEADO · Recibe la señal de desbloqueo · Va a LISTO/preparado

> **Ojo con la corrección:** Comentario textual de la cátedra: "Viene del estado bloqueado; cuando recibe la señal de desbloqueo va a listo/preparado; lo que hace es esperar la condición de desbloqueo (señal)".

#### Estados de un hilo: **Creación** (explíquelo: de dónde viene, a dónde va y qué hace la MV)

*Salió en: resumen*

Es el estado inicial, **nuevo**: el objeto `Thread` existe pero todavía no se llamó a `start()`. La MV le prepara sus estructuras propias: contador de programa, registros y pila.

Al llamar a `start()` **pasa a listo** (la cola de listos). Todavía no se ejecuta: espera que el planificador lo elija.

Puntos clave: Estado inicial (nuevo), antes de start() · La MV le asigna contador de programa, registros y pila · Con start() va a LISTO, no directo a ejecución

#### Estados de un proceso: **Creación** (explíquelo: de dónde viene, a dónde va y qué hace el SO)

*Salió en: resumen*

Es el estado **nuevo**: el SO está creando el proceso, le arma su PCB y le asigna memoria y recursos. Cuando el SO lo admite, **pasa a listo/preparado** a esperar que el planificador le asigne la CPU; de ahí pasará a ejecución.

Puntos clave: Estado inicial · El SO crea el PCB y asigna recursos · Va a LISTO/preparado

#### Explique el estado **LISTO PARA EJECUTAR** de un hilo (de dónde viene, a dónde va y qué hace la Máquina Virtual)

*Salió en: 2023*

Puede venir de tres estados:

- **nuevo**: recién creado, después de `start()`
- **bloqueado**: recibió la señal de desbloqueo
- **ejecución**: le quitaron el procesador porque se le acabó el quantum o hizo `yield()`

Mientras está listo espera en la **cola de listos**. Cuando el planificador de la MV lo elige, **va a ejecución**.

Puntos clave: Viene de NUEVO (start) · Viene de BLOQUEADO (desbloqueo) · Viene de EJECUCIÓN (le quitan la CPU) · Va a EJECUCIÓN cuando el planificador lo elige

> **Ojo con la corrección:** En 2023 respondieron "viene de sleeping…" y el comentario fue: "¿Y si fue recientemente creado?". Nombrá todos los orígenes posibles.

### Sección crítica, monitor, semáforo

#### ¿Qué es una sección crítica?

*Salió en: recup. 2021, captura 2021, 2020*

Es la región de código que **accede a un recurso compartido** y en la que **no puede ejecutarse más de un hilo o proceso a la vez**. Si eso pasara, puede haber comportamiento erróneo (el recurso se corrompe). Se protege con **exclusión mutua**.

Puntos clave: Región que accede a un recurso compartido · Un solo hilo a la vez · Si no, hay comportamiento erróneo · Se protege con exclusión mutua

#### Dé una idea (comparativa) del tiempo de una sección crítica.

*Salió en: 2021*

Una sección crítica es del orden de **5 veces más lenta** que el mismo código sin sincronizar.

Por eso conviene tener **pocas** secciones críticas y que sean lo más **cortas** posible.

Puntos clave: ≈ 5 veces más lenta · Consecuencia: pocas secciones críticas, y cortas

> **Ojo con la corrección:** La cátedra espera literalmente "5 veces más lento". A una respuesta que explicaba qué es una sección crítica le pusieron 0.

#### ¿Cómo se protegen los recursos compartidos?

*Salió en: 2020 (x2)*

Definiendo una **sección crítica** alrededor del acceso al recurso y garantizando **exclusión mutua**, es decir, que entre un solo hilo a la vez. Se hace con `synchronized`, locks, semáforos o monitores. Así el recurso no se corrompe.

Puntos clave: Sección crítica alrededor del acceso · Exclusión mutua: un hilo a la vez · Mecanismos: synchronized, Lock, semáforo, monitor

#### Dé un ejemplo de recursos compartidos que es necesario proteger.

*Salió en: resumen*

Variables compartidas (por ejemplo, un contador), estructuras de datos (listas, colas, un buffer productor-consumidor), archivos, puertos de E/S, una impresora, una conexión a base de datos.

Puntos clave: Variables · Estructuras de datos · Archivos o dispositivos

#### Explique el modificador `synchronized(a)` y el rol de su argumento, donde `a` es un objeto.

*Salió en: 2020 (x2), 2021*

`synchronized` define una **sección crítica**: sólo un hilo por vez puede ejecutar un bloque sincronizado sobre el mismo objeto.

El **argumento** es el objeto cuyo **lock** (monitor intrínseco) se usa como llave. Todo objeto hereda de `Object` y tiene uno. El hilo que entra toma el lock y lo devuelve al salir; los demás que quieran entrar a un bloque sincronizado sobre **ese mismo objeto** quedan dormidos hasta que se libere. Dos bloques sincronizados sobre objetos distintos **no** se excluyen.

Se recomienda un `Object` dedicado (`private final Object lock = new Object();`) y no wrappers como `Integer`, que se reemplazan o comparten y rompen la exclusión.

Puntos clave: Define una sección crítica (exclusión mutua) · El argumento es el objeto cuyo lock se usa como llave · Todo Object tiene un lock · Sólo se excluyen los bloques sobre el mismo objeto · Usar un Object dedicado, no Integer ni otro wrapper

#### ¿Qué es un monitor?

*Salió en: 2020 (x2), 2021*

Es una **herramienta (módulo) de alto nivel** para la **gestión de recursos** que se usan concurrentemente. Encapsula los datos compartidos junto con los procedimientos que los acceden. Garantiza **exclusión mutua** (un solo hilo adentro a la vez) y da **sincronización** con variables de condición: colas donde duermen los hilos que esperan.

Puntos clave: Herramienta de alto nivel · Gestiona recursos usados concurrentemente · Exclusión mutua + sincronización (colas de condición)

#### ¿Qué ventaja tiene un monitor?

*Salió en: 2021 (x3)*

Por ser un **patrón de alto nivel** y **centralizar la gestión de los hilos**, mantiene todos los mecanismos (exclusión mutua, sincronización, etc.) **en la misma estructura**. Las secciones críticas no quedan desparramadas por el código, que resulta más claro y fácil de controlar.

Puntos clave: Alto nivel de abstracción · Centraliza la gestión de los hilos · Exclusión mutua y sincronización en una sola estructura

> **Ojo con la corrección:** Es el comentario textual de la cátedra. Usá las palabras "centraliza" y "misma estructura".

#### ¿Qué es un semáforo?

*Salió en: 2020, 2023*

Es una **herramienta de sincronización** que permite proteger **uno o varios** recursos. Tiene un **contador** (la cantidad de permisos) y una **cola** donde se ponen a dormir los hilos que esperan acceder.

`acquire()` decrementa el contador, o bloquea al hilo si está en 0. `release()` lo incrementa y despierta a un hilo de la cola.

En Java el constructor recibe **2 parámetros**: el número de permisos y un booleano de **fairness**.

Puntos clave: Herramienta de sincronización para 1 o N recursos · Contador + cola de hilos dormidos · acquire / release · Constructor: (permisos, fairness)

> **Ojo con la corrección:** El comentario de 2023 pedía mencionar la cola y los 2 parámetros (recursos y fairness).

#### ¿Qué tipos de semáforo conoce?

*Salió en: resumen*

**Binario**: toma sólo los valores 0 y 1; funciona como un mutex.

**General (contador)**: toma cualquier valor entero no negativo; sirve para administrar N recursos iguales.

Puntos clave: Binario (0/1) · General o contador (≥ 0)

#### Explique el concepto de OWNERSHIP (dueño) en las primitivas LOCK y SEMAPHORE en Java.

*Salió en: resumen*

Ownership es la idea de **qué hilo puede liberar** la primitiva.

Un **Lock tiene dueño**: sólo el hilo que lo tomó puede hacer `unlock()`; si lo intenta otro, recibe `IllegalMonitorStateException`.

Un **Semaphore no tiene dueño**: cualquier hilo con acceso al semáforo puede hacer `release()`, aunque no haya hecho el `acquire()`.

Puntos clave: Ownership: quién puede liberar · Lock: sólo el hilo que lo tomó · Semaphore: cualquier hilo (sin dueño)

### Java

#### Explique cuáles son las acciones que realiza `wait()`.

*Salió en: 2020, 2021 (x3), 2023*

Le dice al hilo que lo llama que **libere el lock** del objeto (del monitor) y **se vaya a dormir** hasta que otro hilo entre al mismo monitor y llame a `notify()` o `notifyAll()`.

Al despertar, **vuelve a adquirir el lock** antes de retornar. Por eso sólo se puede llamar teniendo el lock, dentro de un `synchronized` sobre ese objeto.

Puntos clave: Libera el lock · Duerme hasta un notify()/notifyAll() sobre el mismo objeto · Vuelve a adquirir el lock antes de retornar · Se llama dentro de synchronized

#### Explique qué precaución tiene que tomar para incluir `wait()` en un código.

*Salió en: 2021, recup. 2021, captura 2021*

Ponerlo dentro de un **`try { wait(); } catch (InterruptedException e) {…}`**, porque `wait()` lanza `InterruptedException`, que es checked.

Además:
- llamarlo **dentro de `synchronized`** sobre el mismo objeto (si no, `IllegalMonitorStateException`)
- llamarlo dentro de un **`while`** que vuelva a chequear la condición
- asegurarse de que **algún hilo haga `notify()`**; si no, el hilo duerme para siempre

Puntos clave: try/catch de InterruptedException (lo que pide la cátedra) · Dentro de synchronized · En un while que re-chequee la condición · Que alguien haga notify()

> **Ojo con la corrección:** En todos los intentos, el comentario de la cátedra fue "ponerlo entre un try { wait(); } catch". Empezá por eso.

#### Explique cuáles son las acciones que realiza `notify()`.

*Salió en: 2021*

**Despierta a un solo hilo** (cualquiera) de los que llamaron a `wait()` sobre el **mismo objeto**.

Llamar a `notify()` **no libera el lock**. Le avisa a un hilo en espera que puede despertarse, pero el lock no se suelta hasta que el notificador sale del bloque sincronizado.

Puntos clave: Despierta a UN hilo que hizo wait() sobre el mismo objeto · No libera el lock · El despertado sigue cuando el notificador sale del synchronized

#### Explique cuáles son las acciones que realiza `notifyAll()`.

*Salió en: resumen*

Despierta a **todos** los hilos que llamaron a `wait()` sobre ese objeto. Compiten por el lock y van entrando de a uno, así que cada uno tiene que volver a chequear su condición (por eso el `while`).

Puntos clave: Despierta a TODOS los que hicieron wait() sobre ese objeto · Compiten por el lock, entran de a uno · Cada uno re-chequea su condición

#### Explique las diferencias entre las excepciones CHECKED y UNCHECKED.

*Salió en: 2023*

La diferencia está en el **momento en que se controlan**.

Las **checked** se verifican **en compilación**: el compilador obliga a capturarlas con `try/catch` o a declararlas con `throws`. Ejemplos: `IOException`, `InterruptedException`.

Las **unchecked** (subclases de `RuntimeException`) aparecen **en ejecución** y no hay obligación de tratarlas. Ejemplos: `NullPointerException`, `ArithmeticException`.

En hilos: `run()` no puede declarar checked. Las unchecked que nadie captura se manejan con un `UncaughtExceptionHandler`.

Puntos clave: Checked: se controlan en compilación (try/catch o throws) · Unchecked: aparecen en ejecución (RuntimeException) · Un ejemplo de cada una

> **Ojo con la corrección:** En 2023 respondieron "si la condición de la excepción fue chequeada o no" y sacaron 0. Comentario: "las checked son verificadas en compilación, las otras en ejecución".

#### En un programa con un hilo main, múltiples hilos productores y dos hilos daemon, indique cuándo finaliza la ejecución del programa y por qué.

*Salió en: resumen*

Finaliza cuando **terminan todos los hilos no-daemon**: el main y todos los productores.

Los daemon no mantienen viva a la JVM. Cuando los únicos hilos vivos son daemons, la JVM **los termina** y cierra el programa; no espera a que terminen solos.

Puntos clave: Cuando terminan el main y todos los productores (no-daemon) · La JVM mata a los daemon que queden

> **Ojo con la corrección:** La respuesta de la carpeta agrega "porque los daemons tienen mínima prioridad y no finalizan nunca". La prioridad no tiene nada que ver: la razón es que la JVM no espera a los daemon.

#### Explique un caso en el cual sea necesario utilizar una LOCAL THREAD VARIABLE (variable local de hilo), y por qué.

*Salió en: resumen*

Cuando varios hilos ejecutan **el mismo objeto `Runnable`** (la misma tarea) pero cada uno necesita **su propia copia** de un atributo. El ejemplo del práctico es la fecha de inicio de cada hilo (`startDate`).

Si fuera un atributo común, los hilos se lo pisarían. Con `ThreadLocal<T>`, cada hilo ve y modifica **sólo su valor**, ningún hilo ve el del otro, y no hace falta sincronizar.

Puntos clave: Varios hilos con el mismo Runnable · Cada hilo necesita su propia copia de un atributo · Ejemplo: startDate · Sin ThreadLocal se pisan; con ThreadLocal cada uno ve el suyo

### Lenguajes y gramáticas

#### Defina qué es un lenguaje y relaciónelo con las gramáticas.

*Salió en: 2020 (x2), 2021 (x3), recup. 2021*

Un **lenguaje** es un conjunto de cadenas de símbolos de un alfabeto (palabras, oraciones, textos o frases).

Los lenguajes están compuestos por **sintaxis**, que es la **gramática**: define qué secuencias de símbolos forman cadenas válidas del lenguaje. Y por **semántica**: el significado de las cadenas que lo componen.

La gramática **genera** el lenguaje.

Puntos clave: Conjunto de cadenas de símbolos de un alfabeto · Sintaxis = gramática (qué cadenas son válidas) · Semántica = significado

> **Ojo con la corrección:** Es la pregunta que más se repite. El comentario de la cátedra es siempre el texto de arriba (sintaxis + semántica). En 2021 alguien dijo "conjunto de alfabetos y cadenas… gramáticas tipo 0, 1 o 2" y sacó 0.

#### Defina qué es una gramática y relacione la regla de producción con un autómata.

*Salió en: 2021, captura 2020*

Es un ente formal para especificar, de manera **finita**, el conjunto de cadenas de símbolos que constituyen un lenguaje. Es una **cuádrupla G = (V_T, V_N, S, P)**:

- V_T: conjunto finito de símbolos terminales
- V_N: conjunto finito de símbolos no terminales
- S ∈ V_N: símbolo inicial
- P: reglas de producción (de derivación)

Entre las reglas de producción de una gramática y un autómata **del mismo tipo** hay una **relación biunívoca**: tipo 0 con la máquina de Turing, 1 con el ALA, 2 con el autómata a pila y 3 con el autómata finito.

Puntos clave: Especifica un lenguaje de forma finita · Cuádrupla (V_T, V_N, S, P) con cada elemento definido · Relación biunívoca gramática ↔ autómata del mismo tipo

#### Defina qué es un autómata y relaciónelo con las gramáticas.

*Salió en: 2020 (x2), resumen*

Un **autómata** es un sistema (una máquina abstracta) que recibe información en forma de símbolos, la transforma según su estado y produce otra información que transmite al entorno.

La **gramática genera** un lenguaje y el **autómata lo reconoce**. Dada una gramática (sus reglas de producción), siempre es posible encontrar un autómata que reconozca las mismas palabras que ella genera. Si el lenguaje es de tipo 0, lo reconoce un autómata de tipo 0 (máquina de Turing), y así con cada tipo.

Puntos clave: Recibe símbolos, los transforma y produce una salida · La gramática genera y el autómata reconoce · Para cada gramática hay un autómata del mismo tipo

> **Ojo con la corrección:** En 2020 contestaron "un autómata es una máquina que posee 6 estados" y les pusieron 1 igual, pero el comentario de la cátedra dio la definición de arriba. Mealy y Moore son 6-uplas, que no es lo mismo que "6 estados".

#### Defina qué es un lenguaje del tipo 1 y relaciónelo con la gramática correspondiente.

*Salió en: 2021 (x2)*

Un lenguaje de tipo 1 es el generado por una gramática **sensible (dependiente) al contexto**. Sus reglas tienen la forma **α₁ A α₂ → α₁ β α₂**, con A ∈ V_N y β ≠ λ.

Es decir, A sólo puede reemplazarse por β **cuando aparece en el contexto** indicado: α₁ a su izquierda y α₂ a su derecha. Lo reconoce un **autómata lineal acotado**.

Puntos clave: Gramática sensible o dependiente del contexto · Regla α₁Aα₂ → α₁βα₂, con β ≠ λ · A se sustituye sólo en ese contexto · Lo reconoce un autómata lineal acotado

#### Dé un fundamento de por qué una gramática de tipo 2 tiene menos capacidad de expresión que una de tipo 1. (Recuerde la relación.)

*Salió en: resumen*

La gramática de tipo 2 (independiente del contexto) sustituye un no terminal **sin mirar el contexto** (A → β). La de tipo 1 puede condicionar la sustitución a lo que hay alrededor (α₁Aα₂ → α₁βα₂). Toda gramática tipo 2 es tipo 1, pero no al revés: es la jerarquía de Chomsky.

**La relación con los autómatas:** el tipo 2 se corresponde con el **autómata a pila**, que lee la entrada en un solo sentido y sólo accede al tope de la pila (su transición es q × e × z → q × z*). El tipo 1 se corresponde con el **autómata lineal acotado**, que lee y escribe la cinta y se mueve a izquierda o derecha (q × Γ → q × Γ × {I, D, S}).

Ejemplo: a^nb^nc^n es tipo 1 y no es tipo 2.

Puntos clave: Tipo 2 sustituye sin contexto; tipo 1 con contexto · Tipo 2 está contenido en tipo 1 (Chomsky) · Tipo 2 ↔ autómata a pila (sólo el tope, un sentido) · Tipo 1 ↔ ALA (lee y escribe, ambos sentidos) · Ejemplo: a^nb^nc^n

#### ¿Con qué tipo de gramática puede derivar la siguiente fórmula? { a^n b^n : n ≥ 1 }

*Salió en: 2020, resumen*

Con una gramática de **tipo 2** (independiente del contexto). Por ejemplo: S → aSb | ab.

Puntos clave: Tipo 2 · Ejemplo de producción: S → aSb | ab

> **Ojo con la corrección:** En 2020 alguien respondió "tipo 1" y le dieron el punto, pero el comentario de la cátedra dice "Tipo 2". Respondé con el tipo más restrictivo que alcanza.

#### ¿Con qué tipo de gramática puede derivar la siguiente fórmula? { a^n b^m : n ≥ 1, n ≥ m ≥ n }

*Salió en: recup. 2021, 2021*

La cátedra espera **Tipo 1**.

Puntos clave: Respuesta esperada: Tipo 1

> **Ojo con la corrección:** Dos personas respondieron "tipo 2" y sacaron 0; el comentario fue "Tipo 1". En rigor, n ≥ m ≥ n obliga a que m = n, así que el lenguaje es a^nb^n, que es tipo 2. Probablemente la cátedra la pensó como un caso "dependiente del contexto". Si te toca, respondé **Tipo 1**; si querés, agregá la aclaración.

### Autómatas y máquinas

#### ¿Con un autómata finito puede derivar la siguiente fórmula? ¿Por qué? { a^n b^n : n ≥ 1 }

*Salió en: 2021, captura 2021, resumen*

**No.** Para aceptar habría que recordar cuántas a entraron y verificar que después vengan la misma cantidad de b. Un autómata finito tiene memoria **finita** (sólo sus estados) y no tiene dónde guardar ese conteo, que no está acotado. Hace falta un **autómata a pila**.

Puntos clave: No · Hay que contar una cantidad no acotada de a · El AF tiene memoria finita (sólo estados) · Sí se puede con un autómata a pila

#### ¿Con un autómata a pila puede derivar la siguiente fórmula? ¿Por qué? { a^n b^n : n ≥ 1 }

*Salió en: 2020 (x2), 2021*

**Sí.** Por cada **a** que entra se **apila** un símbolo, y por cada **b** se **desapila** uno. Si al terminar la entrada la pila quedó vacía (en el fondo), la cadena se acepta. La pila funciona como un contador no acotado.

Puntos clave: Sí (decilo primero) · Apilar por cada a · Desapilar por cada b · Aceptar si la pila queda vacía al final

> **Ojo con la corrección:** Comentario de la cátedra en 2020: "¿Y la respuesta a la pregunta?… la cual es sí". Arrancá con Sí o No y después explicá.

#### ¿Con qué tipo de autómata puede derivar la siguiente fórmula? { a^n b^n : n ≥ 1 }

*Salió en: 2021, 2020*

Con un **autómata a pila**, que reconoce los lenguajes de tipo 2.

Puntos clave: Autómata a pila

#### ¿Con qué tipo de autómata puede derivar la siguiente fórmula? { a^n b^m : n ≥ 1, m ≥ 0 }

*Salió en: 2021, resumen*

Con un **autómata finito** (la cátedra puso **AFND**). Como n y m son independientes, no hace falta contar: el lenguaje es a^+b^*, que es regular (tipo 3).

Puntos clave: Autómata finito (AFND) · n y m son independientes: no hay que contar · Es regular: a^+b^*

> **Ojo con la corrección:** Alguien respondió "a^n y b^m" y sacó 0; el comentario fue "AFND".

#### ¿Por qué un autómata a pila es más potente que un autómata finito?

*Salió en: 2020 (x2)*

Porque tiene una **memoria auxiliar no acotada**: la pila. Puede "contar" y recordar lo que leyó, algo que un AF, con memoria finita (sólo sus estados), no puede hacer.

Por eso reconoce los lenguajes independientes del contexto (tipo 2), como a^nb^n, además de todos los regulares (tipo 3).

Puntos clave: Tiene una pila: memoria no acotada · El AF sólo tiene sus estados (memoria finita) · Pila: tipo 2; AF: tipo 3 · Ejemplo: a^nb^n

#### ¿Por qué una máquina de Turing es más potente que un autómata a pila?

*Salió en: 2021, resumen*

La máquina de Turing tiene una **cinta infinita** en la que **lee y escribe**, y el cabezal se **mueve en ambos sentidos**. El autómata a pila lee la entrada **en un solo sentido**, sin escribir en ella, y a su memoria sólo accede por el **tope** (LIFO).

La MT reconoce los lenguajes de tipo 0; la pila, los de tipo 2.

Puntos clave: MT: cinta infinita, lee y escribe, ambos sentidos · Pila: lee en un sentido y sólo accede al tope · MT: tipo 0; pila: tipo 2

#### ¿Por qué una máquina de Turing es más potente que un autómata lineal acotado?

*Salió en: 2020 (x2), 2021 (x2)*

Porque la MT **no tiene cotas**: su cinta es infinita. El ALA es una máquina de Turing cuya cinta está **acotada** a izquierda y derecha por marcas (# y $), al largo de la entrada, y el cabezal no puede salir de esos límites. Por eso el ALA reconoce el tipo 1 y no puede representar todos los lenguajes de tipo 0.

Para programas reales, con memoria acotada, en la práctica son **equivalentes**.

Puntos clave: MT: cinta infinita · ALA: cinta acotada por # y $ al largo de la entrada · ALA: tipo 1; MT: tipo 0 · Con memoria acotada son en la práctica equivalentes

> **Ojo con la corrección:** La corrección no fue uniforme. En 2020 el comentario usó "MT: lectura/escritura en ambos sentidos; ALA: sólo lectura, un sentido, cinta finita", pero eso describe más bien un autómata finito, no un ALA. En 2021, a una respuesta de ese estilo le pusieron 0 con este comentario: "una MT es un programa con instrucciones… un ALA es una MT con una cinta finita, por lo que son equivalentes". Lo más seguro es decir que la diferencia es la cinta acotada y agregar que, con memoria finita, son en la práctica equivalentes.

#### ¿Un programa puede ser expresado por una máquina de Turing?

*Salió en: 2021 (x2), captura 2021*

**Sí.** Todo programa (todo algoritmo) tiene una máquina de Turing asociada que lo implementa: es la tesis de Church-Turing. La MT es el modelo de cómputo más general.

Puntos clave: Sí · Todo programa tiene una MT asociada (Church-Turing)

#### ¿Un programa cualquiera puede ser expresado por un autómata a pila?

*Salió en: recup. 2021, resumen*

**No.** El autómata a pila sólo accede al tope de su memoria y lee la entrada en un sentido. Reconoce los lenguajes de tipo 2, pero no los de tipo 1 ni 0 (por ejemplo, a^nb^nc^n). Un programa general necesita la potencia de una máquina de Turing.

Puntos clave: No · Sólo accede al tope y lee en un sentido · Sólo llega a tipo 2

> **Ojo con la corrección:** En el recuperatorio 2021 respondieron "Sí, cualquier programa puede ser expresado por un autómata pila" y sacaron 0.

#### ¿Cualquier programa puede ser expresado por un autómata lineal acotado?

*Salió en: 2021*

**Sí.** Un ALA es una máquina de Turing con la cinta acotada. Como los programas reales están acotados en código y en memoria, pueden expresarse con un ALA.

Puntos clave: Sí · ALA = MT con la cinta acotada · Los programas reales están acotados en código y memoria

#### ¿Cuál es la principal característica de un autómata finito NO determinístico?

*Salió en: 2023*

Tiene al menos un estado en el que, **para un mismo símbolo del alfabeto, hay más de una transición posible** (también puede tener transiciones λ).

Aun así reconoce los mismos lenguajes que un AFD, los regulares: todo AFND puede convertirse en un AFD equivalente.

Puntos clave: Para un mismo símbolo hay más de una transición posible · Transiciones λ · Es equivalente a un AFD

#### En una máquina de Moore, ¿con qué está relacionado el símbolo de salida?

*Salió en: 2023, resumen*

Con el **estado** en el que se encuentra la máquina: g: Q → S. En cambio, en Mealy la salida depende del estado **y** de la entrada: g: E × Q → S.

Puntos clave: Con el estado actual · g: Q → S · Contraste: en Mealy, estado + entrada

> **Ojo con la corrección:** En 2023 respondieron "con la gramática que esté utilizando" y sacaron 0.

#### ¿Qué máquina tiende a tener menos estados para reconocer una misma fórmula, MEALY o MOORE? ¿Por qué?

*Salió en: resumen*

**Mealy.** Genera su salida en función del estado actual **y** de la entrada, así que una misma transición puede dar salidas distintas sin necesidad de estados extra. Moore asocia la salida al estado, y necesita un estado por cada salida distinta.

Puntos clave: Mealy · Su salida depende de estado + entrada · Moore necesita un estado por cada salida distinta

#### ¿Qué máquina es más segura de utilizar, MEALY o MOORE? ¿Por qué?

*Salió en: resumen*

**Moore.** Su salida depende sólo del estado, así que cambia **sincronizada** con el cambio de estado, en el flanco del reloj (un ciclo más tarde). No reacciona a cambios transitorios ni a glitches de la entrada. En Mealy, la salida puede cambiar en cuanto cambia la entrada.

Puntos clave: Moore · La salida cambia con el estado, en el flanco del reloj · No le afectan los glitches de la entrada

## Segundo parcial

Fuentes: parciales de 2020, 2021 (varios intentos) y 2023, recuperatorio 2021, "Resumen parcial 2 Programación concurrente", fotos de preguntas (1.jpg a 10.jpg, Pregunta7.jpg y la imagen de WhatsApp de 2020) y los PDF sueltos ("Parcial 2 2020", "Segundo parcial"). 57 preguntas.

### RdP: elementos, disparo y tipos de red

#### ¿Qué tipo de fenómenos de control e informáticos modelan (o permiten visualizar) las Redes de Petri?

*Salió en: 2020 (x2), 2021, recup. 2021, 2023*

**Sincronización**, **compartir datos (recursos)**, **paralelismo** y **concurrencia**.

Con esas piezas se modelan también la exclusión mutua (un lugar con una ficha compartido por dos regiones críticas), los conflictos (un lugar con varias transiciones de salida), la capacidad limitada (buffers) y la comunicación entre procesos.

Puntos clave: Sincronización · Compartir datos / recursos · Paralelismo · Concurrencia

> **Ojo con la corrección:** En 2021 respondieron "p invariantes" y sacaron 0: los invariantes son una herramienta de análisis, no un fenómeno que se modela. La respuesta que aceptó la cátedra en 2021, 2023 y el recuperatorio es literalmente "sincronización y compartir datos, paralelismo y concurrencia".

#### ¿Son adecuadas las Redes de Petri para modelar sistemas informáticos que tengan concurrencia y sincronización? En caso afirmativo, ¿qué otras propiedades puede visualizar?

*Salió en: 2020 (x2), capturas*

**Sí.** Además de la concurrencia y la sincronización permiten visualizar el **paralelismo** y el **intercambio (compartición) de recursos**.

Y una vez modelado, sobre la red se pueden verificar propiedades como acotación, vivacidad, ausencia de interbloqueo o exclusión mutua.

Puntos clave: Sí · Paralelismo · Intercambio / compartición de recursos

> **Ojo con la corrección:** En una captura respondieron que son adecuadas "porque permiten visualizar las relaciones entre distintos estados..." y sacaron 0. El comentario fue "Paralelismo e intercambio de recursos": eso es lo que hay que nombrar.

#### ¿Qué representa el marcado de una plaza en una Red de Petri?

*Salió en: 2021*

El **estado local** de la parte del sistema que esa plaza representa: por ejemplo, cuántos recursos libres hay, cuántos elementos tiene un buffer, o si un proceso está en su sección crítica.

El marcado de **toda** la red (el vector con las fichas de cada plaza) es el **estado global** del sistema.

Puntos clave: Estado local de la parte del sistema que la plaza representa · El marcado de toda la red es el estado global

> **Ojo con la corrección:** En 2021 respondieron "el estado del sistema en un determinado momento; cuántos tokens tiene cada plaza" y les dieron 1, pero el comentario corrige: "el marcado de una plaza representa el estado local de la parte del sistema que representa". Distinguí plaza (local) de red (global).

#### ¿Cómo se representa la dinámica de un sistema descripto por una Red de Petri?

*Salió en: recup. 2021*

Con la **evolución del marcado** a medida que se **disparan** las transiciones sensibilizadas. Cada disparo saca fichas de los lugares de entrada y pone en los de salida, y el nuevo marcado es el nuevo estado.

Algebraicamente es la **ecuación de estado**: M_k+1 = Mₖ + C · σ, con C la matriz de incidencia y σ el vector de disparo.

Puntos clave: Evolución del marcado · Por el disparo de transiciones sensibilizadas · Ecuación de estado M' = M + C·σ

#### En una Red de Petri **ordinaria**, ¿cuál es la condición para que una transición esté sensibilizada?

*Salió en: 2020 (x2)*

En una red ordinaria **todos los arcos tienen peso 1**. Entonces una transición está sensibilizada cuando **cada una de sus plazas de entrada tiene al menos una marca**.

En general (red no ordinaria): t está sensibilizada en M si para toda plaza p, M(p) ≥ Pre(p, t).

Puntos clave: Ordinaria: arcos de peso 1 · Cada plaza de entrada tiene al menos un token

> **Ojo con la corrección:** En 2020 respondieron "que las plazas tengan la cantidad de tokens que solicita el peso del arco" y el comentario fue "¿Ordinaria? ¿peso?". En una ordinaria no hace falta hablar de pesos: valen 1. Decí "al menos una marca en cada plaza de entrada".

#### Cuando una transición se dispara, ¿qué pasa con los tokens de las plazas que tienen arcos que **salen** de la transición?

*Salió en: 2020*

A cada plaza de salida se le **agregan** tantas marcas como el **peso** del arco que va de la transición a esa plaza (Post(p, t)). En una red ordinaria, una marca por plaza.

El disparo es **atómico**: en un mismo paso indivisible se quitan las marcas de las entradas y se ponen en las salidas.

Puntos clave: Se agregan marcas a cada plaza de salida · Tantas como el peso del arco · Disparo atómico

#### Cuando una transición se dispara, ¿qué pasa con los tokens de las plazas que tienen arcos que **llegan** a la transición?

*Salió en: recup. 2021*

Depende del tipo de arco:
- **Arco común**: se **consumen** tantas marcas como el **peso** del arco.
- **Arco lector**: la plaza tiene que tener marca para que la transición esté sensibilizada, pero el disparo **no la consume**.
- **Arco inhibidor**: la plaza tiene que estar **vacía** para que la transición esté sensibilizada, y el disparo **no consume** nada.
- **Arco reset**: el disparo **vacía** la plaza (le saca todas las marcas).

Puntos clave: Arco común: se consumen según el peso · Lector: habilita pero no consume · Inhibidor: exige plaza vacía, no consume · (Reset: vacía la plaza)

#### Cuando una transición se dispara, ¿qué pasa con los tokens de las plazas que tienen **arcos lectores** que llegan a la transición?

*Salió en: 2021*

**Nada.** Un arco lector exige que haya marca en la plaza para que la transición esté sensibilizada, pero **no la consume**: el disparo no cambia el marcado de esa plaza. Es un **test de presencia**.

Puntos clave: Nada · El lector condiciona la sensibilización · No consume tokens

> **Ojo con la corrección:** En 2021 respondieron "los tokens pasan a la siguiente plaza" y sacaron 0. Comentario: "Nada, un arco lector no consume tokens".

#### Defina una Red de Petri **autónoma**. (Variante: ¿qué tipo de red es una que evoluciona por los disparos de sus transiciones que cumplen con la condición de sensibilidad?)

*Salió en: 2020, 2021*

Es una red que evoluciona **por sí sola**: sus transiciones se disparan sólo porque están **sensibilizadas**, sin depender de eventos externos ni del tiempo. Los instantes de disparo son desconocidos o no están indicados: la red dice **qué** puede pasar, no **cuándo**. Describe el comportamiento de forma **cualitativa**.

Ejemplo de la filmina: el ciclo de las estaciones. Con la ficha en primavera, la única transición sensibilizada lleva a verano, pero no se sabe cuándo.

Puntos clave: Evoluciona sólo por la sensibilización de sus transiciones · No depende de eventos externos ni del tiempo · Dice qué puede pasar, no cuándo

> **Ojo con la corrección:** En 2021 respondieron "una red de Petri sensibilizada" y sacaron 0. La respuesta es "autónoma".

#### Defina una Red de Petri **no autónoma**.

*Salió en: 2020 (x2), 2023*

Es una red cuyo disparo depende, además de la sensibilización, de **eventos externos** o del **tiempo**:
- **Sincronizada**: la transición sensibilizada dispara cuando ocurre un **evento externo**. Ejemplo: un motor detenido que arranca cuando llega la orden de inicio.
- **Temporizada**: la transición dispara cuando pasa un **tiempo**. Ejemplo: el semáforo vial que pasa de verde a amarillo 60 s después de haberse sensibilizado.

Las guardas y las colas de eventos asociadas a las transiciones son las que conectan la red con el medio y la hacen no autónoma.

Puntos clave: Depende de eventos externos (sincronizada) · o del tiempo (temporizada) · Además, la transición tiene que estar sensibilizada

#### ¿Qué tipo de red es una que evoluciona por los disparos de sus transiciones sensibilizadas y **asociadas a un evento externo**?

*Salió en: 2020*

Una red **no autónoma**, en particular **sincronizada**: la transición tiene que estar sensibilizada **y** tiene que ocurrir el evento externo asociado para que dispare.

Puntos clave: No autónoma · Sincronizada (con eventos)

#### ¿Qué es una Red de Petri pura?

*Salió en: 2021*

Una red **sin auto-loops** (bucles propios): ninguna plaza es a la vez **entrada y salida de una misma transición**.

Importa porque, en una red pura, la **matriz de incidencia** C = Post − Pre alcanza para reconstruir la red. Con un auto-loop, Pre(p, t) = Post(p, t) y la columna de C da 0, así que el arco desaparece de la matriz.

Puntos clave: No tiene auto-loops · Ninguna plaza es entrada y salida de la misma transición · La matriz de incidencia describe toda la red

> **Ojo con la corrección:** En 2021 respondieron "una red libre de conflictos y sin bucles" y les dieron 1. Lo que define a la pura es "sin bucles" (auto-loops); "libre de conflictos" no tiene nada que ver. Mejor no agregarlo.

#### ¿Es posible convertir una Red de Petri impura en pura?

*Salió en: 2021, recup. 2021*

**Sí** (Propiedad 1.1: toda RdP impura se puede convertir en una pura), **agregando una plaza y una transición**.

La transición del auto-loop Tⱼ se parte en dos: Tⱼ' consume de la plaza Pᵢ y deposita en una plaza nueva, y Tⱼ'' consume de la plaza nueva y devuelve la marca a Pᵢ. Así ninguna plaza es entrada y salida de la misma transición.

Puntos clave: Sí · Agregando una plaza y una transición · Se parte la transición del auto-loop en dos

### Clases de redes

#### ¿Qué caracteriza a una Red de Petri para que se comporte como una máquina de estado?

*Salió en: 2020 (x2), capturas*

Estructura: **cada transición tiene exactamente una plaza de entrada y una de salida** (es un grafo de estado).

Marcado: **una sola marca** en toda la red. Con un único token, la red está en un solo estado a la vez (la plaza que tiene el token) y se comporta como un autómata clásico.

Con más de una marca ya no es equivalente: 3 plazas con 2 tokens generan 6 marcados distintos, que un autómata de 3 estados no representa.

Puntos clave: Cada transición: una entrada y una salida · Marcado con un único token · El token es el estado actual

> **Ojo con la corrección:** En una captura respondieron "que cada transición tenga solamente una entrada y un lugar de salida" y sacaron 0, con el comentario "¿Y el marcado?". Hay que decir las dos cosas: la estructura **y** un solo token.

#### Una Red de Petri que es un **grafo de marcado** (grafo marcado), ¿puede tener conflicto?

*Salió en: 2020 (x2), 2021 (x2)*

**No.** En un grafo marcado (o grafo de eventos) **cada plaza tiene exactamente una transición de entrada y una de salida**. Para que haya conflicto hace falta una plaza con dos o más transiciones de salida, y eso no existe.

Puede haber **sincronizaciones** (transiciones con varias entradas), pero no conflictos: no hay elecciones, sólo distintas planificaciones.

Puntos clave: No · Cada plaza: una transición de entrada y una de salida · El conflicto necesita una plaza con varias salidas

> **Ojo con la corrección:** No confundas "**grafo marcado**" (una clase de RdP: el grafo de eventos) con el "**grafo de marcado**" o de marcas (el grafo de accesibilidad). En 2021 contestaron describiendo el grafo de accesibilidad y sacaron 0. Ojo: la cátedra usa "grafo de marcado" para las dos cosas; acá "una red **que es** un grafo de marcado" es la clase de red.

#### ¿Qué caracteriza a una Red de Petri de libre elección extendida? (relación plaza-transición, conflicto)

*Salió en: resumen*

**Para cada conflicto, las transiciones en conflicto tienen el mismo conjunto de plazas de entrada.** Formalmente: si p₁• ∩ p₂• ≠ ∅, entonces p₁• = p₂•.

Consecuencia: si se habilita una de las transiciones del conflicto, **se habilitan todas**. La elección es "libre" porque no depende de otra parte del marcado. Es la clase donde se aplica el teorema de Commoner.

Puntos clave: Las transiciones en conflicto tienen el mismo conjunto de entradas · Si se habilita una, se habilitan todas · Se aplica Commoner

### Propiedades: acotada, viva, home state

#### ¿Cómo define una plaza limitada (acotada) de una Red de Petri?

*Salió en: recup. 2021*

Una plaza p es **k-limitada** para el marcado inicial M₀ si, para **cualquier** marcado M alcanzable desde M₀, el número de marcas en p es **menor o igual que k**: M(p) ≤ k.

La cota depende del marcado inicial.

Puntos clave: Existe un k · Para todo marcado alcanzable desde M0 · M(p) ≤ k

> **Ojo con la corrección:** En el recuperatorio 2021 respondieron "mediante un arco inhibidor" y sacaron 0. La pregunta pide la **definición** de plaza limitada, no cómo limitarla.

#### ¿Cómo define una Red de Petri limitada (acotada)? ¿Y una red segura?

*Salió en: recup. 2021*

Una RdP está **limitada** (acotada) para M₀ si **todas sus plazas están limitadas** para esa marca inicial. Es **k-limitada** si todas las plazas son k-limitadas.

Una red es **segura** si es 1-limitada: ninguna plaza tiene nunca más de una marca. Toda red segura es acotada, pero no al revés.

La acotación depende de M₀. Una red es **estructuralmente acotada** si está acotada para cualquier marcado inicial.

Puntos clave: Todas las plazas limitadas para M0 · k-limitada · Segura = 1-limitada · Depende de M0

#### ¿Las propiedades de vivacidad e interbloqueo se mantienen al aumentar el marcado de una red?

*Salió en: 2021, recup. 2021, capturas*

**No.** La vivacidad y la ausencia de interbloqueo dependen del marcado inicial y **no son monótonas**: agregar fichas puede habilitar una secuencia que termine en un deadlock, o desbalancear un conflicto de modo que una transición nunca vuelva a habilitarse.

En cambio, sí se mantiene la quasi-vivacidad (más fichas no le quitan a una transición la posibilidad de disparar alguna vez), y la falta de acotación (agregar fichas no la arregla).

Puntos clave: No · Dependen del marcado inicial · Más fichas pueden llevar a un deadlock

#### Si una RdP está libre de interbloqueo para m₀, ¿es libre de interbloqueo para un marcado m > m₀? ¿Por qué?

*Salió en: recup. 2021*

**No necesariamente.** Con cierta marca puede no habilitarse nunca una transición que lleva a un deadlock, y al agregar fichas sí se puede disparar, y desde ese estado la red no evoluciona más.

Ejemplo: una plaza P con 1 ficha, un ciclo P → t₁ → Q → t₂ → P, y una transición t₃ que necesita **2** fichas de P y las manda a una plaza sin salida. Con 1 ficha, t₃ nunca se habilita y la red cicla para siempre. Con 2 fichas, t₃ puede dispararse, se lleva las dos fichas y la red queda en deadlock.

Puntos clave: No necesariamente · Más fichas habilitan transiciones que antes no · Esas transiciones pueden llevar a una marca muerta

#### ¿Cuántas veces se puede disparar una transición quasi viva?

*Salió en: 2020 (x2), 2021, capturas*

**Al menos una vez.** Una transición es quasi viva si existe **alguna** secuencia de disparos desde M₀ en la que dispara al menos una vez. No garantiza que pueda volver a dispararse después.

Que todas las transiciones sean quasi vivas dice que el diseño no tiene partes "de adorno" que nunca se usan.

Puntos clave: Al menos una vez · Existe alguna secuencia desde M0 que la dispara · No garantiza que vuelva a disparar

> **Ojo con la corrección:** En una captura respondieron "no se sabe cuántas veces se puede disparar" y sacaron 0. Comentario: "Al menos una vez".

#### Una Red de Petri seudo viva (pseudo viva), ¿puede bloquearse totalmente?

*Salió en: 2020, resumen*

**No.** Una red seudo viva tiene **algunas transiciones vivas** (no necesariamente todas). Una transición viva siempre puede volver a dispararse desde cualquier marca alcanzable, así que la red **nunca se bloquea totalmente**: alguna actividad sigue siendo posible. Lo que sí puede pasar es que otras transiciones dejen de estar disponibles para siempre.

Puntos clave: No · Tiene algunas transiciones vivas · Nunca se traba del todo, aunque pierda algunas transiciones

#### Si una Red de Petri es viva, ¿se puede garantizar que de una determinada marca se pueda llegar a cualquier marca del sistema?

*Salió en: 2020, 2023*

Respuesta que acepta la cátedra: **Sí**, porque si la red es viva, desde **cualquier** marca Mᵢ alcanzable siempre existe una secuencia de disparos que contiene a cualquier transición Tⱼ: ninguna capacidad del sistema se pierde nunca.

Puntos clave: Sí (lo que acepta la cátedra) · Desde cualquier marca hay una secuencia que dispara cualquier Tj

> **Ojo con la corrección:** La cátedra le dio 1 al "Sí" en 2020 y en 2023. Pero ojo, formalmente vivacidad **no** es lo mismo que poder alcanzar cualquier marca: eso es la **reversibilidad** (o tener home state). Una red viva puede tener marcas transitorias a las que no se vuelve. Si te la toman, contestá "Sí" con la justificación de arriba (siempre se puede volver a disparar cualquier transición); no la mezcles con la definición de reversible. Comparala con la variante "seudo viva y quasi viva", donde la respuesta es "No".

#### Si una red de Petri es seudo viva y sus transiciones quasi vivas, ¿se puede garantizar que de una determinada marca se pueda llegar a cualquier marca del sistema?

*Salió en: 2021 (x2)*

**No.** Quasi viva sólo garantiza que cada transición dispara **al menos una vez** en alguna secuencia, y seudo viva sólo que **algunas** transiciones son vivas. Puede haber transiciones que, después de dispararse, no se habiliten nunca más, y marcas a las que no se puede volver. Para esa garantía la red tiene que ser **viva** (según la cátedra).

Puntos clave: No · Quasi viva: al menos una vez · Seudo viva: sólo algunas vivas · Hace falta que sea viva

> **Ojo con la corrección:** En 2021 respondieron "Sí se puede garantizar" y sacaron 0; el mismo año, "No se puede garantizar" sacó 1.

#### Si una Red de Petri es reversible para una marca m₀, ¿de cualquier marca es posible alcanzar el estado inicial m₀?

*Salió en: 2020, 2021*

**Sí.** Esa es la definición: una red es **reversible** (o reinicializable) si desde **cualquier marca alcanzable** desde m₀ existe una secuencia de disparos que vuelve a m₀. Es decir, m₀ es un **home state**: siempre se puede volver al estado de arranque.

Puntos clave: Sí · Desde cualquier marca alcanzable se vuelve a m0 · m0 es home state

#### Si una Red de Petri tiene un home state m_h, ¿de cualquier marca es posible alcanzar el estado m_h?

*Salió en: 2020 (x2)*

**Sí.** Una marca m_h es un **home state** si desde **cualquier** marca alcanzable existe una secuencia de disparos que llega a m_h. Si el home state es la marca inicial m₀, la red es **reversible**.

Puntos clave: Sí · Desde cualquier marca alcanzable se llega a mh · Si mh = m0, la red es reversible

#### Si una Red de Petri tiene una marca m_h que se puede alcanzar desde cualquier estado, ¿qué tipo de red es?

*Salió en: 2021*

Es una **RdP con home state** (m_h es un home state).

Sólo sería **reversible** si m_h fuera la marca inicial m₀.

Puntos clave: Red con home state · Reversible sólo si mh = m0

> **Ojo con la corrección:** En 2021 respondieron "es una red reversible" y sacaron 0. Comentario: "RdP con home state". Reversible es el caso particular en que el home state es m0.

#### ¿Es posible relacionar totalmente la estructura de una Red de Petri con su comportamiento?

*Salió en: recup. 2021*

**No.** Además de la estructura hace falta el **marcado** (inicial). La misma red puede ser viva y acotada con un M₀ y no serlo con otro.

Con la estructura sola se ven las propiedades **estructurales**: conflictos estructurales, componentes conservadores (P-invariantes), sifones y trampas. Las de **comportamiento** (vivacidad, acotación, interbloqueo, home state) dependen también del marcado.

Puntos clave: No · Hace falta también el marcado · Estructurales vs de comportamiento

> **Ojo con la corrección:** En el recuperatorio 2021 respondieron "sí, porque por la estructura podemos ver si tiene home state, si tiene conflictos..." y sacaron 0. Comentario: "No, es necesario además de la estructura el marcado".

### Conflicto, concurrencia y prioridades

#### Explique la diferencia entre conflicto estructural y conflicto efectivo.

*Salió en: recup. 2021 (x2)*

- **Conflicto estructural**: dos o más transiciones **comparten al menos una plaza de entrada** (la plaza tiene más de una transición de salida). Depende **sólo de la estructura**, no del marcado.
- **Conflicto efectivo**: hay conflicto estructural **y además**, para un marcado dado, las transiciones están sensibilizadas pero las marcas de la plaza compartida **no alcanzan para dispararlas a todas**. Disparar una deshabilita a la otra.

Si las marcas alcanzan para todas, el conflicto no es efectivo: las transiciones son concurrentes.

Puntos clave: Estructural: comparten una plaza de entrada; no depende del marcado · Efectivo: requiere el estructural · Efectivo: sensibilizadas pero sin marcas para todas

#### ¿Qué entiende por concurrencia (conflicto) en una Red de Petri?

*Salió en: 2021*

Cuando **dos transiciones en conflicto pueden ser disparadas en cualquier secuencia o simultáneamente**: comparten plaza de entrada, pero hay marcas suficientes para las dos (conflicto **no efectivo**). Son causalmente independientes: una puede dispararse antes, después o al mismo tiempo que la otra, y el resultado es el mismo (disparo múltiple [T₁T₂]).

Puntos clave: Dos transiciones en conflicto · Se pueden disparar en cualquier secuencia o simultáneamente · Conflicto no efectivo: marcas suficientes para las dos

> **Ojo con la corrección:** En 2021 respondieron "cuando no pueden realizarse transiciones de tokens hacia otra plaza" y sacaron 0. El comentario es la respuesta textual: "Cuando dos transiciones en conflicto pueden ser disparadas en cualquier secuencia o simultáneamente".

#### ¿Qué entiende por persistencia (conflicto) en una Red de Petri?

*Salió en: 2023*

Una red es **persistente** si, para cada marca alcanzable, dos transiciones habilitadas cualesquiera pueden dispararse **en cualquier secuencia**: el disparo de una **no deshabilita** a la otra. Una transición habilitada sólo deja de estarlo **por su propio disparo**.

La respuesta que aceptó la cátedra en 2023 fue: "Cuando dos transiciones pueden ser disparadas en cualquier secuencia o simultáneamente".

Una red sin conflicto estructural es persistente para cualquier M₀.

Puntos clave: Dos habilitadas se pueden disparar en cualquier orden · El disparo de una no deshabilita a la otra · Sólo deja de estar habilitada por su propio disparo

#### ¿Cómo solucionamos el problema del conflicto efectivo en un conjunto de transiciones?

*Salió en: 2020, 2023, capturas*

Con una **política** que decida cuál de las transiciones en conflicto se dispara: por ejemplo, una **política de prioridades** que resuelva el orden de ejecución de ese conjunto. También puede ser por turnos o probabilidades.

En la implementación, es lo que hacen las políticas de las colas de condición del monitor: entre las transiciones sensibilizadas con hilos esperando, eligen a cuál despertar.

Puntos clave: Una política (de prioridades) que elige cuál dispara · Elimina el no determinismo del conflicto · En el monitor: la política de las colas de condición

> **Ojo con la corrección:** En 2023 respondieron "mediante un arco inhibidor" y sacaron 0. Comentario: "Se puede establecer una política de prioridades que resuelva el orden de ejecución de este conjunto de transiciones". Aunque un inhibidor puede modelar una prioridad, lo que espera la cátedra es "política de prioridades".

#### ¿Cuándo usamos prioridades para el disparo de un conjunto de transiciones?

*Salió en: 2020, resumen*

Cuando hay **varias transiciones sensibilizadas a la vez y en conflicto** (efectivo), y hay que **elegir cuál** de ellas se dispara. La prioridad reduce el no determinismo: de entre las que cumplen la condición de sensibilización, dispara **la de mayor prioridad**.

Puntos clave: Varias transiciones sensibilizadas en conflicto · Hay que elegir cuál dispara · Dispara la de mayor prioridad

#### Con un arco inhibidor, ¿puede controlar la prioridad en una red que comparte un recurso? Explique cómo controla la prioridad para que un **productor** tenga **más** prioridad que un **consumidor**.

*Salió en: 2021 (x2)*

**Sí.** Se coloca un **arco inhibidor desde la plaza del productor** (la que indica que el productor está esperando para tomar el recurso) **hacia la transición de entrada del consumidor**.

Mientras haya un productor esperando (marca en su plaza), la transición del consumidor **no está sensibilizada**: el recurso se lo lleva el productor. El consumidor sólo puede entrar cuando no hay productores esperando.

Puntos clave: Sí · Arco inhibidor de la plaza del productor (esperando) · hacia la transición de entrada del consumidor · Mientras el productor espera, el consumidor no puede entrar

> **Ojo con la corrección:** En 2021 respondieron "de la plaza del consumidor a la transición de entrada del productor" y el comentario fue "**Justamente al revés**": así se le da más prioridad al consumidor. Otra respuesta que sólo explicaba la idea sacó 1 con el comentario "¿Y cómo lo implemento?": nombrá de qué plaza a qué transición va el arco.

#### Con un arco inhibidor, ¿puede controlar la prioridad en una red que comparte un recurso? Explique cómo controla la prioridad para que un **productor** tenga **menos** prioridad que un **consumidor**.

*Salió en: 2020 (x2)*

**Sí.** En un productor/consumidor se coloca un **arco inhibidor desde la plaza del consumidor** (consumidor esperando) **hacia la transición de entrada del productor**.

Mientras haya un consumidor esperando, el productor no puede tomar el recurso: el consumidor tiene prioridad.

Puntos clave: Sí · Arco inhibidor de la plaza del consumidor · hacia la transición de entrada del productor

> **Ojo con la corrección:** En 2020 la respuesta sacó 1, pero el comentario aclaró: "en un productor consumidor se coloca un arco inhibidor entre la plaza del consumidor a la transición de entrada del productor". Es la versión simétrica de la pregunta de 2021.

#### Con un arco lector, ¿puede controlar la prioridad en una red que comparte un recurso? ¿Cómo o por qué?

*Salió en: 2021*

**Sí**, colocando una **plaza de control con un arco lector asociado a la transición que se quiere priorizar**. El arco lector condiciona el disparo a la presencia de una marca en la plaza de control **sin consumirla**, así que esa plaza funciona como un permiso o turno que el resto de la red administra.

Como el inhibidor, el arco lector es una de las extensiones que permiten modelar prioridades.

Puntos clave: Sí · Plaza de control · Arco lector a la transición que se quiere priorizar · No consume la marca de control

> **Ojo con la corrección:** En 2021 respondieron "No, porque sensibilizaría a ambas transiciones... se necesita un arco inhibidor" y sacaron 0. Comentario: "Sí, colocando una plaza de control con un arco lector asociado a la transición que deseamos priorizar".

### Análisis: grafo, invariantes, sifones

#### Con un grafo de marcado, ¿podemos determinar todas las propiedades de una RdP, sí o no? Si contestó no, diga por qué; si contestó sí, explique cómo encuentra que la red es **acotada**.

*Salió en: 2020, 2021, 2023*

**Sí.** El grafo de marcado (de accesibilidad) contiene todas las marcas alcanzables y todos los disparos posibles, así que sobre él se leen las propiedades de comportamiento.

**Acotada**: se recorren **todos los nodos** (marcas) y, para cada plaza, se busca el **máximo** número de marcas. Si el grafo es finito y ese máximo existe para todas las plazas, la red es **k-acotada**, con k el mayor de esos máximos. (Si al construirlo aparece ω en el árbol de cobertura, la red no está acotada.)

Puntos clave: Sí · El grafo tiene todas las marcas alcanzables · Para cada plaza, el máximo de marcas en todos los nodos · Ese máximo es la cota k (con ω: no acotada)

> **Ojo con la corrección:** En 2020 respondieron "No, porque sólo sabemos que la cantidad de arcos de entrada y salida de una plaza es uno" y sacaron 0, con el comentario "Sí, se realiza la suma de token para todos los estados y se verifica que no supere un valor". En 2023, una respuesta con la definición de acotada sacó 1, pero con el comentario "¿Y cómo veo si es acotada en el grafo?": decí el procedimiento sobre los nodos del grafo.

#### Con un grafo de marcado, ¿podemos determinar todas las propiedades de una RdP, sí o no? Si contestó no, diga por qué; si contestó sí, explique cómo encuentra que la red es **segura**.

*Salió en: 2020 (x2), 2021*

**Sí.** El grafo de marcado contiene todas las marcas alcanzables.

**Segura**: se recorren todos los nodos y se verifica que **en ninguna marca alguna plaza tenga más de una marca** (todas las componentes de todas las marcas son 0 o 1).

Puntos clave: Sí · Recorrer todas las marcas del grafo · Ninguna plaza con más de 1 marca en ningún nodo

#### Con un grafo de marcado, ¿podemos determinar todas las propiedades de una RdP, sí o no? Si contestó no, diga por qué; si contestó sí, explique cómo encuentra un **interbloqueo**.

*Salió en: 2021*

**Sí.** Un interbloqueo es una marca desde la que no se puede disparar ninguna transición: en el grafo es un **nodo sin arcos de salida** (un punto muerto, o marca sumidero). Si existe un nodo alcanzable así, la red tiene deadlock; si ningún nodo lo es, la red es libre de interbloqueo.

Puntos clave: Sí · Interbloqueo = nodo sin arcos de salida (punto muerto)

#### Con un grafo de marcado, ¿podemos determinar todas las propiedades de una RdP, sí o no? Si contestó no, diga por qué; si contestó sí, explique cómo encuentra un **invariante de transición**.

*Salió en: 2020, capturas*

**Sí.** Un invariante de transición es un conjunto de disparos que **devuelve la red a la misma marca**. En el grafo se ve como un **ciclo**: un camino que sale de un nodo y vuelve a él. Contando cuántas veces se dispara cada transición a lo largo del ciclo se obtiene el vector del T-invariante (y cumple C · y = 0).

Puntos clave: Sí · T-invariante = secuencia que vuelve a la misma marca · En el grafo: un ciclo · Las transiciones del ciclo (con su cantidad) forman el vector

> **Ojo con la corrección:** En una captura respondieron "No, porque sólo sabemos que el total de arcos de entrada como de salida es uno" y sacaron 0. El comentario fue sólo "Si".

#### En el grafo (árbol) de cobertura, ¿qué representa ω?

*Salió en: recup. 2021 (x2)*

El **marcado de una plaza que crece indefinidamente** (no acotada). Cuando al construir el árbol aparece una marca que **cubre estrictamente** a una anterior del mismo camino (M' > M), las componentes que crecieron se reemplazan por ω, porque repitiendo ese ciclo pueden crecer sin límite.

Gracias a ω el árbol de cobertura **siempre es finito**, incluso si la red no es acotada. Si aparece ω, la red no está acotada.

Puntos clave: Una plaza cuyo marcado crece indefinidamente · Aparece cuando una marca cubre a una anterior · Mantiene finito el árbol · Si aparece, la red no es acotada

> **Ojo con la corrección:** En el recuperatorio 2021 respondieron "representa el vértice del grafo" y sacaron 0.

#### La **constante de marcado** de un invariante de plaza (componente conservador), ¿depende de la marca, de la estructura de la red o de ambas?

*Salió en: 2020 (x2), 2021*

**De ambas.** El invariante es xᵀ · M = xᵀ · M₀: la constante es xᵀ · M₀. Los pesos x salen de la **estructura** (xᵀ · C = 0), y M₀ es la **marca inicial**. Con la misma red y otro marcado inicial, el invariante sigue existiendo pero la constante cambia.

Puntos clave: De ambas · Constante = x^T · M0 · x sale de la estructura; M0 es la marca

> **Ojo con la corrección:** No la confundas con la otra pregunta, casi igual: "**un invariante de plaza** (componente conservador), ¿es dependiente de la marca inicial, de la estructura o de ambas?". Ahí la respuesta es "de la estructura". Acá preguntan por la **constante**, que depende de las dos.

#### Un **invariante de plaza** (componente conservador), ¿es dependiente de la marca inicial, de la estructura de la red o de ambas?

*Salió en: 2020, 2021, recup. 2021*

Depende **exclusivamente de la estructura**: ser componente conservador es una **propiedad estructural**, porque sólo exige que exista un vector x > 0 con xᵀ · C = 0, y C es la matriz de incidencia. Es independiente de la marca inicial.

El **valor numérico** de la constante sí varía con la marca inicial.

Puntos clave: De la estructura · Propiedad estructural: x^T · C = 0 · Independiente de la marca inicial · El valor de la constante sí depende de M0

> **Ojo con la corrección:** Es la contracara de la pregunta sobre la **constante** de marcado (que depende de ambas). La mejor respuesta, que sacó 1 en el recuperatorio 2021: "Depende exclusivamente de la estructura de la red. El valor numérico sí variará según la marca inicial, pero la propiedad es de la estructura".

#### En la Red de Petri del ejemplo de las cuatro estaciones, ¿cómo interpreta el invariante donde la suma de las marcas de todas las plazas es siempre uno?

*Salió en: 2020, recup. 2021, capturas*

Que **sólo se puede estar en una estación a la vez**.

La red es una máquina de estado con un único token: M(primavera) + M(verano) + M(otoño) + M(invierno) = 1 es un componente conservador, y la plaza que tiene el token es la estación actual.

Puntos clave: Sólo se puede estar en una estación a la vez · Es un componente conservador con constante 1 · El token es la estación actual

> **Ojo con la corrección:** En una captura respondieron "componente conservador, el sistema tiene un único estado" y les dieron 1, pero el comentario fue: "Que se puede estar en solo una estación a la vez". Decilo con esas palabras.

### Del modelo al programa

#### ¿Qué relación tiene que una RdP que modela un sistema sea acotada (o tenga una plaza limitada que modela el buffer) con los buffers del programa que implementa el modelo?

*Salió en: 2020 (x2), 2023*

Si la red es acotada, todas las plazas tienen una cota k. La plaza que modela un buffer nunca supera k marcas, así que **el buffer del programa no es infinito: tiene una capacidad máxima**, igual a la cota de la plaza. Se puede implementar con memoria finita (por ejemplo, un arreglo de tamaño k) sin desbordarse, y el productor se bloquea cuando se llena.

Si la red no fuera acotada, el buffer crecería sin límite: el modelo no sería implementable con recursos finitos.

Puntos clave: Acotada: todas las plazas tienen una cota k · El buffer tiene capacidad máxima = cota de la plaza · Implementable con memoria finita · No acotada = buffer infinito

#### ¿Qué relación tiene que una RdP que modela un sistema sea **segura** con los hilos del programa que implementa el modelo, donde se ha instanciado un hilo por plaza?

*Salió en: 2020*

Si la red es segura, cada plaza tiene **a lo sumo una marca**. Si cada plaza la atiende un hilo, nunca hay más de una "actividad" pendiente en esa plaza, así que **un hilo por plaza alcanza**: nunca se necesitan dos ejecuciones simultáneas de la misma acción (no hace falta código reentrante) y ningún trabajo queda esperando a un hilo libre.

Si la red fuera k-acotada con k > 1, esa plaza podría tener k marcas a la vez: para aprovechar ese paralelismo harían falta hasta k hilos, o el hilo tendría que procesar las marcas de a una.

Puntos clave: Segura: a lo sumo una marca por plaza · Un hilo por plaza alcanza · No hay dos ejecuciones simultáneas de la misma acción · Con k marcas harían falta hasta k hilos

> **Ojo con la corrección:** Esta pregunta quedó sin contestar en el parcial de 2020 que hay en la carpeta, así que no hay comentario de la cátedra: la respuesta modelo está razonada desde la definición de red segura.

#### ¿Qué relación tiene que una RdP que modela un sistema tenga una **secuencia de disparos infinita** con el programa que implementa el modelo?

*Salió en: 2021, recup. 2021, capturas*

Que el programa **no termina**: es un sistema reactivo, como un sistema operativo o un servidor, cuyos hilos corren en un **bucle infinito**. Para que eso sea posible, **siempre tiene que poder dispararse al menos una transición** (la red no tiene deadlock: hay al menos una transición viva) y la red tiene que tener **ciclos** (T-invariantes) que la devuelvan a marcas ya visitadas.

Si además es acotada, el programa puede correr para siempre con memoria finita.

Puntos clave: El programa no termina (reactivo, como un SO) · Los hilos corren en un bucle infinito · Siempre hay al menos una transición que se puede disparar · Ciclos / T-invariantes

> **Ojo con la corrección:** En el recuperatorio 2021 respondieron sólo "si o si debe dispararse aunque sea una transición" y sacaron 0. La misma idea **con el ejemplo del sistema operativo** ("tiene secuencia de disparos infinita, lo cual implica que sí o sí tiene al menos una transición...") sacó 1. Desarrollala: programa que no termina, ejemplo, al menos una transición disparable.

### Monitores

#### ¿Qué es un monitor?

*Salió en: resumen*

Un **mecanismo de software de alto nivel** para el control de la concurrencia: un módulo de abstracción que **encapsula** los datos (las variables que representan el estado del recurso) y los procedimientos necesarios para asignar un recurso o grupo de recursos compartidos, reutilizables en serie.

Se cumple que **todos sus métodos se ejecutan en exclusión mutua**, y define la **sincronización por condición** de cada método (bloquear al proceso hasta que se cumpla la condición y avisarle cuando puede seguir). Lo presentó Dijkstra (1971) y lo refinaron Brinch Hansen (1973) y Hoare (1974).

Puntos clave: Mecanismo de alto nivel para gestionar recursos compartidos · Encapsula datos y procedimientos · Métodos en exclusión mutua · Sincronización por condición

#### ¿Cuáles son los elementos que componen un monitor?

*Salió en: resumen*

- Las **variables locales permanentes**, que almacenan el estado interno del recurso.
- El **código de inicialización** (constructor).
- Los **procedimientos internos**, que manejan las variables permanentes.
- La declaración de los **procedimientos exportados**, que son los que pueden llamar los procesos activos.

Para funcionar, además, usa una **cola de entrada** (mutex) y **variables de condición** con sus colas.

Puntos clave: Variables locales permanentes · Código de inicialización (constructor) · Procedimientos internos · Procedimientos exportados

#### ¿Cómo deben ejecutarse los métodos de un monitor?

*Salió en: recup. 2021*

**En exclusión mutua**: un solo proceso a la vez puede estar ejecutando un método del monitor (cualquiera de ellos). Si otro proceso intenta entrar, el código de acceso lo bloquea y lo manda a la **cola de entrada**; cuando el proceso activo sale, el monitor desbloquea al primero de la cola.

Además, cada método tiene su **sincronización por condición**: si no se cumple, el proceso se bloquea en una cola de condición y libera el monitor.

Puntos clave: En exclusión mutua · Un solo proceso a la vez en el monitor · Los demás esperan en la cola de entrada · Sincronización por condición

> **Ojo con la corrección:** En el recuperatorio 2021 respondieron "deben ejecutarse dentro del mismo y no fuera" y sacaron 0. La palabra clave es **exclusión mutua**.

#### Las acciones que implementan operaciones sobre los recursos, ¿se realizan adentro o afuera del monitor? ¿Por qué?

*Salió en: 2021 (x2)*

**Afuera** del monitor, con el fin de **liberar el monitor** permitiendo el acceso a otros hilos y **no serializar todas las acciones** posibles.

El monitor sólo decide si el hilo puede avanzar (dispara la transición, actualiza el estado) y lo deja salir. La acción en sí (imprimir, producir, usar el recurso) se hace fuera; si se hiciera adentro, mientras un hilo trabaja nadie más podría ni siquiera consultar el estado, y se perdería todo el paralelismo.

Puntos clave: Afuera · Para liberar el monitor · No serializar todas las acciones · El monitor sólo gestiona el acceso

> **Ojo con la corrección:** En 2021 respondieron "adentro, porque el monitor es el que administra los recursos" y sacaron 0. El comentario es la respuesta textual: "Se realizan afuera del monitor, con el fin de liberar el monitor permitiendo el acceso y no serializar todas las acciones posibles".

#### ¿Qué función cumplen las variables de condición en un monitor?

*Salió en: 2021, 2023, capturas*

Dos funciones:
- **Hacer esperar** a un proceso hasta que se cumpla una condición sobre el estado del monitor. Con `wait`, el proceso queda dormido en la cola asociada a la variable y **libera el monitor**.
- **Despertar** a un proceso que estaba esperando por esa condición. Con `signal`, si la cola no está vacía se despierta al primero (qué pasa después depende de la política); si está vacía, no pasa nada.

Son "contenedores" de hilos que esperan una determinada condición.

Puntos clave: Hacer esperar a un proceso hasta que se cumpla una condición · Despertar a los procesos que esperaban por ella · wait: duerme en la cola y libera el monitor · signal: despierta al primero (o nada si está vacía)

> **Ojo con la corrección:** En 2021, "nos indica si el proceso tiene que irse a dormir a una cola asociada o se debe ejecutar" sacó 0. En 2023, "hacer esperar a un proceso hasta que una condición del estado del monitor se cumpla; también despierta a procesos que estaban esperando por su causa" sacó 1. Nombrá las **dos** funciones.

#### ¿Cómo se relacionan las transiciones (de la RdP) con las variables de condición en un monitor?

*Salió en: recup. 2021*

Es posible establecer una **relación biunívoca**: una representa a la otra. Cada transición tiene su variable de condición (su cola). Un hilo que quiere disparar una transición que no está sensibilizada se duerme en la cola de **esa** transición; cuando la transición se sensibiliza, el monitor despierta a un hilo de esa cola.

Puntos clave: Relación biunívoca · Una cola (variable de condición) por transición · Se duerme en la cola de la transición no sensibilizada

> **Ojo con la corrección:** En el recuperatorio 2021 se explicó la analogía entre recursos y sensibilización y sacó 0. Lo que pedía la cátedra es la palabra **biunívoca**: una representa a la otra.

#### ¿Qué resuelven las políticas de reactivación de un monitor (entrada, waiting, cortesía)?

*Salió en: 2021 (x2), recup. 2021, 2023*

Resuelven **la secuencia de acceso al monitor**: cuando el monitor se libera, a quién se le da entre los hilos de la **cola de entrada**, los de las **colas de condición** (waiting) que fueron señalizados y el de la **cola de cortesía** (urgente). Se definen por las prioridades relativas de esas colas.

Puntos clave: La secuencia (el orden) de acceso al monitor · Entre cola de entrada, de condición y de cortesía

> **Ojo con la corrección:** Atención: "resuelven los conflictos modelados por la RdP" sacó 0 (recuperatorio 2021) y "resuelven la concurrencia y los conflictos" también (2021). Eso es lo que resuelven las políticas de las **colas de condición**. Las de **reactivación** resuelven **la secuencia de acceso al monitor**.

#### ¿Qué resuelven las políticas de las colas de condición (transiciones) en un monitor?

*Salió en: 2021*

Resuelven **los conflictos modelados por la RdP**. Cuando hay varias transiciones sensibilizadas con hilos esperando en sus colas, la política **elige cuál disparar**, es decir, a qué hilo despertar.

Puntos clave: Los conflictos modelados por la RdP · Eligen cuál de las transiciones sensibilizadas con hilos esperando se dispara

> **Ojo con la corrección:** Es la contracara de las políticas de **reactivación** (entrada-waiting-cortesía), que resuelven la secuencia de acceso al monitor. No las confundas.

#### ¿Cuál es el objetivo de implementar una cola de cortesía en un monitor?

*Salió en: recup. 2021, capturas*

**Ceder el tiempo de procesamiento restante del hilo activo al hilo despertado, por encontrarse urgido.**

Cuando el hilo que está en el monitor despierta a otro (signal), le cede el monitor y se queda esperando en la cola de cortesía, que tiene **prioridad sobre la cola de entrada**. Así el despertado encuentra la condición tal como se la dejaron, y no se la gana un hilo nuevo (lo que podría postergarlo indefinidamente).

Puntos clave: Ceder el procesamiento restante al hilo despertado · El despertado está urgido · La cola de cortesía tiene prioridad sobre la de entrada

> **Ojo con la corrección:** En una captura describieron la cola de entrada (bloquear al que llega mientras hay un proceso activo) y sacaron 0. El comentario es la frase textual: "Ceder el tiempo de procesamiento restante del hilo activo al hilo despertado por encontrarse urgido".

### Sincronizadores y Executors

#### ¿Con qué valor debe ser inicializada la primitiva PHASER?

*Salió en: 2023*

Con la **cantidad de participantes** (parties) que se sincronizan en cada fase: `new Phaser(n)`. La fase avanza cuando **todos** los participantes registrados llegaron.

También se puede crear con 0 y registrar participantes dinámicamente con `register()` (o `bulkRegister(n)`). Cada participante llama a `arriveAndAwaitAdvance()` al terminar su fase, o a `arriveAndDeregister()` para dejar de participar.

Puntos clave: Con la cantidad de participantes (parties) · La fase avanza cuando llegaron todos los registrados · register() para sumar participantes dinámicamente

> **Ojo con la corrección:** En 2023 la dejaron sin responder (un punto) y sacaron 0. Es la única pregunta de Java de los segundos parciales que hay en la carpeta.
