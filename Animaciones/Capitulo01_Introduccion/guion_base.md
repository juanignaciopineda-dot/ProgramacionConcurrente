# Capítulo 1 — Introducción a la Programación Concurrente

> **Guion base: narración APROBADA de todo el capítulo. No se renderiza.**
> Es la fuente de texto para los 4 episodios (ver `../README.md` §1 para
> qué escenas van en cada uno). Cada episodio copia de aquí su narración a
> su propio `E<N>_.../guion.md`, que es el que lleva los momentos y se anima.
> Las secciones "Visual" de abajo son ideas iniciales, no especificación.

Guion para las 14 escenas del capítulo. Fuente: `Apuntes/PROGRAMACION CONCURRENTE Y PARALELA.md`
(sección "Introducción a la Programación Concurrente", líneas 211-603) y
`Filmimas/Clase 1`. Solo teoría, sin código Java ni práctica.

Convención: cada escena tiene Objetivo, qué hace falta saber antes (para no
dar nada por sabido), qué se ve, y la narración partida en bloques
`<!-- bN -->` que se sincronizan uno a uno con un tramo de animación.

Tono: narrado como una historia, no como una lista de definiciones. Se
arranca de un caso, una pregunta o una escena concreta, y desde ahí se
llega al concepto — no al revés.

---

# Escena 01 — PorQueImporta

**Objetivo:** enganchar con el "por qué" de la materia contando casos reales
de fallas de software, para llegar a la idea de que el testing no alcanza.

**Qué hace falta saber antes:** nada. Es la escena de apertura.

**Visual:**
- Título del curso.
- Una línea de tiempo horizontal con 4 casos reales de fallas de software
  (Therac-25, Ariane 5, Patriot, Pentium FDIV), apareciendo uno por uno con
  año y una frase de la causa.
- Se resalta Therac-25 con un color de alerta (fue el más grave: personas
  sobreexpuestas a radiación).
- Cierre con la frase de Dijkstra en pantalla grande.

## Narración

<!-- b1 -->
En este curso de Programación Concurrente vamos a meternos de lleno en el
mundo de los sistemas críticos: esos programas que no se pueden dar el
lujo de fallar. Y para entender por qué existe toda una materia dedicada
a esto, arranquemos con una historia.

<!-- b2 -->
Corría 1985. Un equipo de radioterapia llamado Therac 25 empezaba a
tratar pacientes de cáncer en varios hospitales de Estados Unidos y
Canadá. La máquina funcionaba perfecto, hasta que un día dejó de
funcionar perfecto: seis pacientes recibieron una sobredosis de
radiación varias veces mayor a la indicada, y algunos de ellos murieron.

<!-- b3 -->
Cuando los ingenieros fueron a buscar el error, no encontraron nada
obvio. No era un botón mal programado, ni una cuenta mal hecha. El
problema aparecía solo si el operador escribía ciertas teclas
rápidamente, en un orden puntual: ahí, y solo ahí, dos partes del
software —la interfaz y el control del haz de radiación— se pisaban
entre sí. Eso tiene nombre, y lo vamos a estudiar en detalle: es una
condición de carrera.

<!-- b4 -->
Y Therac no estuvo solo. Once años después, en 1996, el cohete Ariane
cinco se autodestruyó cuarenta segundos después del despegue, porque un
número no entraba en la casilla donde el programa intentaba guardarlo.
En 1991, un sistema de misiles Patriot falló por un error de redondeo
que se fue acumulando, milésima a milésima, hasta que el reloj interno
quedó totalmente desincronizado. Y en 1994, Intel tuvo que salir a
cambiar procesadores enteros porque una tabla grabada en el propio
hardware tenía errores de división.

<!-- b5 -->
Lo que tienen en común estos casos es más inquietante que la anécdota en
sí: ninguno de estos errores aparecía con cualquier entrada. Aparecían
solo ante una combinación puntual de datos, o de tiempos exactos. Y ese
es, justamente, el tipo de error que el testing convencional no
encuentra: puedes correr el programa mil veces, que funcione las mil
veces, y aun así el error siga ahí, escondido, esperando la
combinación exacta que lo dispare.

<!-- b6 -->
Edsger Dijkstra, una de las figuras que fundó esta disciplina, lo dijo
mejor que nadie, y lo vamos a repetir varias veces en el curso: el
testing puede mostrar la presencia de errores, pero nunca puede
demostrar su ausencia. Entonces, si probar no alcanza, ¿qué hacemos?
Construimos modelos que se puedan recorrer por completo, para demostrar
que ciertas cosas malas son directamente imposibles. De eso se trata
todo lo que viene.

---

# Escena 02 — ProgramaYCPU

**Objetivo:** contar, como una pequeña historia de "un día en la vida de un
procesador", qué es la memoria, los registros y el contador de programa, y
que una instrucción no es tan atómica como parece.

**Qué hace falta saber antes:** nada; se explica desde el nivel más bajo a
propósito.

**Visual:**
- Una CPU (caja) al centro, con un registro dentro.
- Al lado, una pila de celdas de memoria con direcciones y contenido.
- Una flecha (el contador de programa, "PC") que apunta a una celda y va
  avanzando una por una mientras se "ejecutan" instrucciones simples.
- Se muestra cómo una operación aparentemente simple como sumar 1 a una
  variable, en realidad son 3 micro-pasos: leer, calcular, escribir.

## Narración

<!-- b1 -->
Para que la historia de Therac tenga sentido técnico, tenemos que bajar
un escalón más de lo habitual, hasta el lugar donde realmente ocurren
las cosas: adentro del procesador. Y ahí, en el fondo, pasa algo
sorprendentemente simple. Un ordenador hace una sola cosa, una y otra
vez, millones de veces por segundo: lee una instrucción, la ejecuta, y
pasa a la siguiente.

<!-- b2 -->
Imaginemos la memoria como una fila larguísima de casilleros numerados,
cada uno guardando un dato. El procesador no trabaja directamente ahí:
primero tiene que traer el dato a un registro, un casillero mucho más
pequeño, pero muchísimo más rápido, que vive dentro de la propia CPU.

<!-- b3 -->
Y hay un registro que es como el dedo que va siguiendo la lectura: el
contador de programa. Guarda, en todo momento, cuál es la próxima
instrucción a ejecutar. Apenas termina una, el contador avanza a la
siguiente. Ese dedo que recorre las instrucciones, una por una, es
ni más ni menos que el hilo de ejecución de un programa.

<!-- b4 -->
Ahora, aquí viene el giro de esta escena, el que después va a explicar
absolutamente todo lo demás. Miremos algo tan pequeño como "contador más
igual uno". Para nosotros es una línea, una sola acción. Para el
procesador, no. Son tres pasos, uno atrás del otro: primero lee el valor
actual desde la memoria y lo trae a un registro. Después, adentro de ese
registro, le suma uno. Y recién al final, escribe ese resultado nuevo de
vuelta en la memoria.

<!-- b5 -->
Guardemos bien esta idea, porque en la próxima escena vamos a ver
exactamente qué pasa cuando dos procesos intentan hacer esto mismo al
mismo tiempo: lo que en el código parece un solo paso, para la máquina
son tres. Y entre esos tres pasos, puede pasar absolutamente cualquier
cosa.

---

# Escena 03 — Proceso

**Objetivo:** distinguir programa (algo estático) de proceso (algo en
ejecución) contando la transformación como si fuera un "nacimiento", y
presentar el PCB como la ficha de identidad de ese proceso.

**Qué hace falta saber antes:** la escena 02 (memoria, CPU, contador de
programa).

**Visual:**
- Un archivo/ícono estático a la izquierda, etiquetado "programa".
- Se "ejecuta": el archivo se transforma en una caja animada con su propio
  contador de programa, memoria asignada y estado — etiquetada "proceso".
- Aparece la tabla PCB llenándose campo por campo (PID, estado, PC,
  registros, memoria, prioridad, recursos).
- Un proceso "padre" crea un proceso "hijo" (una segunda caja se desprende).

## Narración

<!-- b1 -->
Con esa idea del contador de programa recorriendo instrucciones, ya
podemos contar algo que usamos todo el tiempo sin pensarlo: qué diferencia
hay entre un programa y un proceso. Un programa, mientras nadie lo
ejecuta, es una entidad completamente estática: un archivo guardado en el
disco, código quieto que no hace absolutamente nada por sí solo.

<!-- b2 -->
Pero en el momento en que alguien lo ejecuta, ese programa nace a otra
cosa: un proceso. Ahí sí empieza a tener un contador de programa
avanzando de verdad, una porción de memoria propia, y un estado que
cambia segundo a segundo. Si el programa es la receta, el proceso es la
tarta que se está horneando ahora mismo, en este instante.

<!-- b3 -->
Y como todo lo que nace en un sistema operativo necesita una identidad,
apenas nace un proceso el sistema le abre una ficha: el Bloque de Control
de Proceso. Ahí se anota todo lo que hace falta saber de él: un
identificador único, su estado actual, el valor de su contador de
programa, el contenido de sus registros, qué región de memoria le
pertenece, su prioridad, y qué recursos tiene asignados, como archivos
abiertos.

<!-- b4 -->
Esa ficha tan completa tiene un costo: cada vez que el sistema operativo
decide pasar de un proceso a otro, tiene que guardar ese bloque entero y
cargar el del siguiente. Toda esa información hay que moverla, y eso
lleva tiempo. Es el precio de tener procesos tan ricos en detalle.

<!-- b5 -->
Una última vuelta de tuerca: un proceso puede, a su vez, dar vida a otro.
Al que crea se lo llama proceso padre, al creado, proceso hijo. Y apenas
nace el hijo, algo importante pasa: padre e hijo corren de manera
concurrente, uno no espera al otro. Cada uno sigue su propio camino.

---

# Escena 04 — EstadosProceso

**Objetivo:** contar la vida de un proceso como un recorrido, de principio
a fin, por sus 5 estados, más la distinción entre suspendido bloqueado y
suspendido listo.

**Qué hace falta saber antes:** escena 03 (qué es un proceso, el PCB).

**Visual:**
- Diagrama de estados con 5 nodos: New, Ready, Running, Waiting, Terminated.
- Un "token" (punto de color) que recorre las transiciones mientras se narra
  cada una.
- Al final, dos nodos extra tenues a un costado: "Suspendido bloqueado" y
  "Suspendido listo", conectados con líneas punteadas para no saturar el
  diagrama principal.

## Narración

<!-- b1 -->
Sigamos a ese proceso recién nacido a lo largo de toda su vida, porque no
va a quedarse quieto: va a recorrer distintos estados, uno tras otro,
hasta el final. El modelo más simple para contar esta historia tiene solo
tres momentos: listo para ejecutar, ejecutando, o bloqueado. Pero los
sistemas operativos reales cuentan una versión más detallada, con cinco
momentos, y ese vocabulario nos va a acompañar todo el curso.

<!-- b2 -->
Nuestro proceso nace en el estado Nuevo: existe, pero el sistema
operativo todavía no terminó de darle el visto bueno. En cuanto lo
admite, pasa a Preparado: ya está listo para correr, solo le falta que
el procesador quede libre para él.

<!-- b3 -->
Y llega su momento: el planificador lo elige, y pasa a Ejecución. Ahí
tiene, por fin, el control real del procesador. En una máquina de un solo
núcleo, en este estado solo puede estar un proceso a la vez: el
protagonista del momento.

<!-- b4 -->
Desde Ejecución, la historia puede ir por dos caminos. Si se le termina
el tiempo asignado, o algo más urgente lo interrumpe, vuelve a
Preparado, a hacer fila para su próximo turno. Pero si necesita algo que
todavía no tiene, como el resultado de una lectura de disco, pasa a
Bloqueado, y ahí espera. Cuando por fin ocurre lo que estaba esperando,
no vuelve directo a Ejecución: vuelve a la fila de Preparado, como
cualquier otro que recién llega.

<!-- b5 -->
Y en algún momento, la historia termina: Terminado. Puede ser porque el
proceso hizo su trabajo y cerró en paz, o porque el sistema detectó un
error fatal y lo tuvo que bajar.

<!-- b6 -->
Hay un capítulo extra, para cuando un proceso además es sacado de la
memoria principal por falta de espacio. Un proceso suspendido puede
seguir bloqueado, si la causa de su espera sigue vigente, o puede quedar
suspendido pero ya listo, si esa causa ya desapareció y solo falta que
alguien lo devuelva a memoria para que retome su camino.

---

# Escena 05 — Multiprogramacion

**Objetivo:** contar el "truco" de cómo un único procesador da la ilusión
de hacer varias cosas a la vez, mediante el planificador y el cambio de
contexto, y su costo.

**Qué hace falta saber antes:** escenas 03 y 04 (proceso, PCB, estados).

**Visual:**
- Un solo núcleo de CPU al centro.
- Tres procesos en una cola de "Preparados" a la izquierda.
- El planificador va tomando uno, lo pone en el núcleo un rato (barra de
  tiempo = quantum), lo devuelve a la cola, guarda su PCB y carga el
  siguiente.
- Se ve una línea de tiempo abajo mostrando la intercalación real (P1, P2,
  P3, P1, P2...) para mostrar que nunca hay dos al mismo tiempo, pero da la
  sensación de simultaneidad.

## Narración

<!-- b1 -->
Ahora que conocemos la vida de un proceso, hagamos una pregunta que
parece obvia pero no lo es: ¿cómo hace tu ordenador, con un solo
procesador, para tener el navegador, la música y este mismo vídeo
corriendo "al mismo tiempo"? La respuesta, spoiler, es que en realidad no
corren al mismo tiempo. Es un truco, y muy bueno: los intercala tan
rápido que nuestro cerebro no lo nota.

<!-- b2 -->
A este truco se lo llama multiprogramación: gestionar varios procesos
dentro de un sistema con un solo procesador. Adentro del sistema
operativo hay un componente que oficia de director de orquesta, el
planificador, decidiendo en cada instante cuál de los procesos
preparados pasa a ejecutar, y por cuánto tiempo exactamente: ese tiempo
asignado se llama quantum.

<!-- b3 -->
Cuando el quantum de un proceso se acaba, o el proceso necesita esperar
algo, pasa el momento más delicado de esta historia: el cambio de
contexto. El sistema operativo guarda el bloque de control completo del
proceso saliente, y carga el del que entra. Ese ida y vuelta no es
gratis: cada cambio de contexto consume tiempo de procesador que no se
usa para hacer trabajo útil. Es el peaje que se paga por la ilusión de
simultaneidad.

<!-- b4 -->
El resultado, si lo miramos en una línea de tiempo, es un vaivén
constante: un pedacito de un proceso, un pedacito de otro, un pedacito
del tercero. No hay paralelismo real ahí adentro, y encima está ese
costo extra de los cambios de contexto. Y aun así, este intercalado
mejora muchísimo el aprovechamiento del procesador, comparado con hacer
esperar a todos los demás mientras uno solo ocupa la máquina de punta a
punta.

---

# Escena 06 — Hilos

**Objetivo:** contar qué son los hilos como "compañeros de casa" dentro de
un proceso: qué comparten, qué es propio de cada uno, y por qué salen tan
baratos comparados con un proceso.

**Qué hace falta saber antes:** escenas 03 y 05 (proceso, cambio de
contexto).

**Visual:**
- Una caja grande "proceso", con memoria compartida adentro (representada
  como un área común con variables).
- Dentro, 3 hilos: cada uno con su propio contador de programa (flecha) y su
  propia pila (torres de marcos apilados), pero todos apuntando/leyendo la
  misma memoria compartida.
- Tabla comparativa proceso vs hilo apareciendo fila por fila (costo de
  creación, cambio de contexto, comunicación).

## Narración

<!-- b1 -->
Hasta aquí hablamos de procesos como si cada uno tuviera una sola línea de
ejecución. Pero adentro de un mismo proceso puede convivir más de una: a
cada una de esas líneas se la llama hilo, y conviene pensarlos como
compañeros de casa que comparten casi todo.

<!-- b2 -->
¿Qué comparten exactamente? Todo el espacio de memoria: cada variable de
instancia, cada variable estática. También comparten los archivos
abiertos, y la autenticación y los permisos del proceso que los
contiene. Es una convivencia muy íntima.

<!-- b3 -->
Lo único que cada hilo guarda para sí, como su propio dormitorio dentro
de esa casa compartida, es su contador de programa, su propia pila de
ejecución, y el estado de sus registros. Cada uno sabe en qué instrucción
va, cada uno tiene su espacio para variables locales, pero todos leen y
escriben la misma memoria común.

<!-- b4 -->
Y ahí aparece, otra vez, la frase que va a resonar en toda la materia:
como cualquier hilo puede modificar algo compartido, y los demás lo ven
modificado al instante, hace falta algún mecanismo que controle ese
acceso. Eso se llama exclusión mutua, y es el problema que perseguimos
capítulo tras capítulo.

<!-- b5 -->
Pero hay una buena noticia sobre esta convivencia tan cercana: sale
barata. Crear un hilo es, más o menos, diez veces más económico que crear
un proceso entero, porque hay mucha menos información que armar: ni
bloque de control completo, solo un contexto pequeño y una pila. Por la
misma razón, cambiar de contexto entre hilos es casi gratis, porque ni
siquiera cambia el espacio de memoria. Y comunicarse entre ellos no
necesita pasar por el sistema operativo: alcanza con leer y escribir una
variable que ya comparten.

---

# Escena 07 — ConcurrenciaVsParalelismo

**Objetivo:** contar la diferencia entre concurrencia y paralelismo como
dos preguntas distintas ("cómo está armado" vs "qué pasa realmente"), y
ubicar multiprogramación, multiprocesamiento y distribución.

**Qué hace falta saber antes:** escena 05 (multiprogramación con un solo
núcleo).

**Visual:**
- Panel izquierdo: 1 núcleo con 3 procesos intercalándose en el tiempo
  (repite el visual de la escena 05, más pequeño).
- Panel derecho: 4 núcleos, cada uno ejecutando un proceso realmente al
  mismo tiempo (barras simultáneas, no intercaladas).
- Rótulos: "concurrencia: propiedad de la estructura del programa" /
  "paralelismo: propiedad de la ejecución".
- Tabla de 3 filas: multiprogramación (1 procesador), multiprocesamiento
  (varios procesadores, misma máquina), distribuido (varias máquinas).

## Narración

<!-- b1 -->
Con la escena del truco de la multiprogramación en la cabeza, podemos
desarmar una confusión muy común: la gente usa "concurrente" y "paralelo"
como si fueran sinónimos, y no lo son. Son dos preguntas distintas.

<!-- b2 -->
Concurrencia responde a cómo está armado el programa: es un conjunto de
tareas que, en principio, podrían ejecutarse en cualquier orden, o
intercaladas entre sí. Fíjate que el programa con un solo núcleo de la
escena anterior sigue siendo perfectamente concurrente, aunque nunca haya
dos cosas pasando literalmente al mismo tiempo. Y sigue teniendo todos
los mismos dolores de cabeza de coordinación que ya vimos.

<!-- b3 -->
Paralelismo responde a otra pregunta: qué pasa realmente en el hardware.
Es que haya instrucciones ejecutándose de manera simultánea, de verdad, en
piezas de hardware distintas: núcleos distintos de un mismo procesador,
procesadores distintos, u ordenadores distintos conectados en red.

<!-- b4 -->
Por eso la concurrencia se nos aparece en tres escenarios bien distintos,
según cuánto hardware hay detrás. Multiprogramación, cuando gestionamos
varios procesos dentro de un único procesador. Multiprocesamiento, cuando
hay varios procesadores en la misma máquina. Y procesamiento distribuido,
cuando hay varios ordenadores separados, hablándose por red.

<!-- b5 -->
La concurrencia está ahí en los tres casos, sin excepción. Lo único que
cambia de uno a otro es si, además de intercalarse en la estructura, las
tareas también corren físicamente al mismo tiempo. Esa es la línea
divisoria entre estructura y ejecución, y la vamos a usar todo el curso.

---

# Escena 08 — Interleaving

**Objetivo:** contar el interleaving como el momento en que "el orden deja
de estar en nuestras manos", con el árbol de intercalaciones creciendo
hasta la explosión de estados.

**Qué hace falta saber antes:** escena 02 (acción atómica: leer, calcular,
escribir), escena 06 (memoria compartida entre hilos).

**Visual:**
- Dos procesos, cada uno con 2 acciones atómicas para ejecutar (A1, A2 y
  B1, B2).
- Se despliega un árbol de todas las intercalaciones posibles (4 caminos:
  A1A2B1B2, A1B1A2B2, A1B1B2A2, B1A1A2B2... hasta 6 combinaciones válidas
  de intercalar 2+2).
- El árbol crece visualmente para transmitir la "explosión de estados" al
  pasar de 2+2 pasos a, por ejemplo, 3+3.
- Se resalta que cada camino del árbol es una ejecución "legítima".

## Narración

<!-- b1 -->
Llegamos al concepto que sostiene todo este capítulo, y todo lo que sigue
en la materia. Se llama interleaving, intercalado, y una vez que lo
vemos, no se puede dejar de ver: los procesos concurrentes ejecutan
intercalando las acciones atómicas que los componen.

<!-- b2 -->
Imaginemos dos procesos pequeños. El proceso uno tiene dos acciones para
hacer, A uno y A dos, en ese orden. El proceso dos también tiene dos, B
uno y B dos. Cada uno, por separado, respeta perfectamente su propio
orden interno. Pero entre los dos procesos, nadie fijó un orden: podrían
ir alternándose de varias maneras distintas.

<!-- b3 -->
Dibujemos todas esas maneras posibles. A uno, A dos, B uno, B dos. O A
uno, B uno, A dos, B dos. O B uno, A uno, A dos, B dos. Y así. Aquí está
lo que hay que asumir de una buena vez: cada uno de estos caminos es una
ejecución tan válida como cualquier otra. Nada, en la definición de
proceso concurrente, dice que uno de estos caminos es "el correcto" y
los demás no.

<!-- b4 -->
De aquí salen dos consecuencias que no tienen vuelta atrás. Primero: el
orden en que se ejecutan las acciones no lo decide el programador, ni es
reproducible de una corrida a la siguiente. Segundo: el mismo par de
procesos, corrido dos veces, puede darnos dos resultados distintos,
simplemente porque esa vez el intercalado fue otro.

<!-- b5 -->
Y hay un problema todavía más de fondo, que aparece apenas agrandamos un
poco el ejemplo. Con dos acciones por proceso, ya teníamos varios
caminos posibles. Con tres acciones, o con diez, o con cien, la cantidad
de intercalaciones crece exponencialmente. A esto se lo llama explosión
de estados, y es la razón profunda de por qué el testing aquí casi no
sirve: es prácticamente imposible construir a mano el test que dispare,
justo, esa combinación puntual que rompe todo.

<!-- b6 -->
Una última precisión que vamos a necesitar pronto: el estado completo de
un sistema concurrente no es solo el valor de sus variables. También
incluye, para cada proceso, en qué instrucción está parado: su contador
de programa. Dos procesos con las mismas variables, pero con el contador
de programa en un lugar distinto, están, en rigor, en estados distintos.

---

# Escena 09 — CondicionDeCarrera

**Objetivo:** narrar paso a paso, casi como una escena de suspenso, cómo
`contador++` con dos hilos puede terminar mal, hasta la moraleja: el
resultado no puede depender de quién corre más rápido.

**Qué hace falta saber antes:** escena 02 (leer/calcular/escribir), escena
08 (interleaving).

**Visual:**
- Una celda de memoria "contador" en 0.
- Dos hilos (H1, H2), cada uno con su propio registro.
- Se anima paso a paso el peor interleaving: H1 lee 0, H2 lee 0 (antes de
  que H1 escriba), H1 incrementa y escribe 1, H2 incrementa (su registro
  ya tenía 0, ahora tiene 1) y escribe 1.
- Resultado final: contador = 1, con un cartel "¡debería ser 2!" en rojo.
- Se contrasta brevemente con un interleaving "bueno" (secuencial) que sí
  da 2, para remarcar que ambos son ejecuciones válidas.

## Narración

<!-- b1 -->
Bajemos todo lo que vimos hasta ahora a un ejemplo muy concreto, uno de
los más citados de toda la materia, porque cuando se entiende este, se
entiende el resto del curso. Tenemos una variable compartida, contador,
que arranca en cero. Dos hilos quieren, cada uno, sumarle uno. Si todo
sale bien, contador debería terminar en dos. Veamos si sale bien.

<!-- b2 -->
Recordemos de la escena de la CPU que esa suma, en realidad, son tres
pasos separados: leer, incrementar, escribir. Vamos a intercalar esos
pasos de la peor manera posible, y vamos a ver que nada raro tiene que
pasar para que se rompa: alcanza con la mala suerte del orden.

<!-- b3 -->
El hilo uno lee contador: es cero. Pero justo antes de que llegue a
escribir su resultado, el sistema operativo le da el turno al hilo dos.
El hilo dos también lee contador, y como el hilo uno todavía no escribió
nada, lee exactamente lo mismo: cero.

<!-- b4 -->
Ahora los dos hilos tienen, cada uno guardado en su propio registro, el
mismo valor: cero. El hilo uno lo incrementa a uno, y lo escribe:
contador pasa a valer uno. El hilo dos, que sigue teniendo cero en su
registro porque lo leyó antes de que nadie escribiera nada, también lo
incrementa a uno, y también lo escribe. Contador se queda en uno.

<!-- b5 -->
El resultado final es contador igual a uno, cuando pedimos sumar dos
veces desde cero. Y lo que hace que esto valga la pena de estudiar es
que no fue un bug en el sentido de "algo se rompió". Fue una ejecución
perfectamente válida, según la propia definición de interleaving que
acabamos de ver. No hubo ningún error de programación en cada hilo por
separado. Simplemente, esta vez, el orden de las acciones fue ese.

<!-- b6 -->
A esto se lo llama condición de carrera, y de aquí sale el principio que
va a ordenar el diseño de todo programa concurrente que escribamos en
adelante: el resultado no puede depender de qué tan rápido corre cada
proceso respecto de los demás. Si el resultado cambia según quién le
gane la carrera a quién, el programa está mal, aunque compile, y aunque a
veces, incluso la mayoría de las veces, funcione bien.

---

# Escena 10 — SeccionCritica

**Objetivo:** presentar sección crítica y exclusión mutua como la respuesta
directa al problema de la escena anterior, y contar los 6 requisitos como
las reglas del juego que cualquier solución tiene que cumplir.

**Qué hace falta saber antes:** escena 09 (la condición de carrera como
motivación).

**Visual:**
- Un recurso compartido (ícono simple, ej. una variable o archivo) con una
  "puerta" que solo deja pasar a uno.
- Dos procesos intentan entrar; solo uno puede estar "adentro" (en su
  sección crítica) a la vez.
- Lista de los 6 requisitos apareciendo uno por uno, con un ícono de
  check al confirmarse cada uno sobre el ejemplo de la puerta.
- Se agrupan visualmente: 1 y 4 como "seguridad", 3 y 6 como "progreso",
  2 y 5 como "condiciones que descartan soluciones tramposas".

## Narración

<!-- b1 -->
La condición de carrera de la escena anterior pasó por una sola razón:
dos hilos entraron sin ningún control a tocar el mismo recurso. A la
parte del código que toca ese recurso compartido la llamamos sección
crítica, y al recurso en sí, recurso crítico. La regla que necesitamos
imponer, en una frase, es esta: solo un proceso puede estar en su
sección crítica a la vez. Eso es exclusión mutua.

<!-- b2 -->
Ahora, imponer esa regla no es tan simple como suena, y no cualquier
solución sirve. Sea por software, por hardware, con semáforos o con
mecanismos más sofisticados que vamos a ver más adelante, toda solución
al problema de la sección crítica tiene que cumplir seis reglas del
juego para considerarse correcta.

<!-- b3 -->
La primera es la exclusión mutua en sí misma: de todos los procesos con
sección crítica sobre el mismo recurso, solo uno puede tener permiso
para entrar en un momento dado. La cuarta la complementa: si en este
momento nadie está adentro, cualquiera que lo pida tiene que poder
entrar sin demora. Estas dos son las reglas de seguridad: garantizan que
nada malo pase.

<!-- b4 -->
La tercera y la sexta son las reglas de progreso: garantizan que algo
bueno termine pasando. La tercera dice que ningún proceso puede quedar
esperando para siempre para entrar: nada de interbloqueo, nada de
inanición. La sexta pone el límite del otro lado: un proceso solo puede
quedarse en su sección crítica por un tiempo finito, y de eso es
responsable el programador, no el mecanismo.

<!-- b5 -->
Y la segunda y la quinta son las que descartan las soluciones tramposas,
las que "andan casi siempre". La segunda exige que un proceso que se cae
fuera de su sección crítica no arrastre a los demás con él. La quinta
prohíbe asumir nada sobre la velocidad de los procesos ni sobre cuántos
procesadores hay: la solución tiene que funcionar igual si un proceso es
mil veces más lento que el otro, y con uno o con mil núcleos.

---

# Escena 11 — ProblemasClasicos

**Objetivo:** contar deadlock, starvation y livelock como tres formas
distintas de "quedar trabado", cada una con su propio mecanismo, cerrando
con la violación de invariantes.

**Qué hace falta saber antes:** escena 10 (sección crítica, recursos).

**Visual:**
- Deadlock: dos procesos, cada uno con un recurso tomado (candado), cada
  uno esperando el recurso del otro. Ambos quedan "congelados" (color
  atenuado, sin movimiento).
- Starvation: P1 y P2 alternándose un recurso sin parar; P3 al costado,
  pidiendo una y otra vez, nunca atendido (parpadea en rojo sin avanzar).
- Livelock: dos personas en un pasillo (representadas como flechas) que
  se corren siempre hacia el mismo lado al cruzarse, en loop infinito —
  a diferencia del deadlock, estas siguen "vivas" y moviéndose.
- Violación de invariante: un contador que debería quedar siempre ≤ 1 y
  en un instante muestra 2, con alarma visual.

## Narración

<!-- b1 -->
Resolver la exclusión mutua no cierra el capítulo de problemas: abre
otro. Cuando varios procesos compiten o cooperan, pueden quedar trabados
de formas distintas, y vale la pena separarlas bien porque se confunden
fácil entre sí.

<!-- b2 -->
La primera es el deadlock, o interbloqueo: dos o más procesos se quedan
esperándose mutuamente, sin que ninguno pueda avanzar. El caso típico: el
proceso uno ya tomó el recurso A y ahora pide el B; el proceso dos ya
tomó el B y ahora pide el A. Ninguno va a soltar lo que tiene hasta
conseguir lo que le falta, y lo que le falta lo tiene, justamente, el
otro. Los dos quedan congelados, sin consumir procesador, para siempre.

<!-- b3 -->
La segunda es la inanición, o starvation, y es más sutil: aquí el sistema,
en conjunto, sí sigue avanzando, pero hay un proceso puntual que queda
afuera para siempre. Si dos procesos se turnan un recurso entre ellos sin
parar, un tercero que también lo pide puede no conseguirlo jamás, aunque
el recurso nunca deje de usarse.

<!-- b4 -->
La tercera es la más engañosa de las tres, y se confunde fácil con el
deadlock: el livelock. Aquí los procesos no se bloquean: siguen activos,
consumiendo procesador, reaccionando todo el tiempo a lo que hace el
otro, pero sin avanzar en absoluto. Es exactamente lo que nos pasa cuando
nos cruzamos con alguien en un pasillo angosto y los dos nos corremos
para el mismo lado, una y otra vez: estamos en movimiento constante, y
sin embargo ninguno de los dos logra pasar.

<!-- b5 -->
Y hay un cuarto problema, más general que engloba en parte a los otros:
la violación de invariantes. Un invariante es una condición que debería
cumplirse siempre, en cualquier instante de la ejecución, como "nunca hay
más de un proceso a la vez en la sección crítica". Cuando la
sincronización falla, alcanza con que exista un solo interleaving que
rompa esa condición, aunque sea por un instante, para que el programa
esté mal.

---

# Escena 12 — InteraccionYMensajes

**Objetivo:** contar los 3 niveles de conocimiento entre procesos como una
escalera de cercanía creciente, y llegar al pasaje de mensajes con sus
combinaciones bloqueante/no bloqueante, incluyendo el rendezvous.

**Qué hace falta saber antes:** escena 06 (memoria compartida entre
hilos), escena 10 (sección crítica).

**Visual:**
- Tabla de 3 filas apareciendo progresivamente: no se conocen (compiten) /
  conocimiento indirecto (comparten un objeto) / conocimiento directo (se
  comunican por identificador).
- Transición al mecanismo de mensajes: dos procesos con sobres viajando
  entre ellos.
- Se anima cada combinación: send bloqueante+receive bloqueante (ambos
  "congelados" hasta que el sobre llega = rendezvous), send no
  bloqueante+receive bloqueante (emisor sigue de largo, receptor espera).
- Tabla final comparando memoria compartida vs pasaje de mensajes.

## Narración

<!-- b1 -->
Hasta ahora vimos procesos peleándose por un recurso sin siquiera
conocerse. Pero la relación entre procesos puede ser mucho más cercana
que eso, y hay una escalera de tres escalones que describe cuánto se
conocen entre sí.

<!-- b2 -->
El primer escalón es no conocerse en absoluto: ni siquiera saben que el
otro existe, y aun así entran en conflicto porque quieren el mismo
recurso. A esto lo llamamos competencia, y es exactamente el escenario de
exclusión mutua que ya venimos viendo.

<!-- b3 -->
El segundo escalón es el conocimiento indirecto: no se conocen por
nombre, pero comparten un objeto, como una variable o un archivo. Es
cooperación por compartimiento, y trae los mismos dolores de cabeza de
exclusión mutua, más uno nuevo: mantener esos datos compartidos
coherentes.

<!-- b4 -->
Y el tercer escalón, el más cercano, es el conocimiento directo: los
procesos se hablan explícitamente, dirigiéndose el uno al otro por su
identificador. Es cooperación por comunicación, y el mecanismo que la
hace posible es el pasaje de mensajes.

<!-- b5 -->
El pasaje de mensajes trae su propia sincronización de fábrica: nadie
puede recibir un mensaje antes de que alguien lo haya enviado. Lo que
falta definir es qué le pasa a cada uno después de su operación. Enviar
puede bloquear al emisor hasta que el mensaje efectivamente llegue, o
puede dejarlo seguir de inmediato. Recibir puede bloquear al receptor
hasta que llegue algo, o puede dejarlo continuar con las manos vacías.

<!-- b6 -->
Cuando las dos puntas son bloqueantes, pasa algo especial: emisor y
receptor quedan sincronizados en el mismo instante exacto, esperándose
mutuamente hasta que el mensaje pasa de uno a otro. Eso tiene nombre
propio, rendezvous, y es la forma más fuerte de sincronización entre dos
partes. En la práctica, la combinación que más se usa es otra: emisor no
bloqueante, que sigue trabajando apenas envía, y receptor bloqueante, que
espera lo que necesita antes de seguir.

<!-- b7 -->
Cerremos comparando los dos grandes caminos que tenemos para que los
procesos interactúen. Con memoria compartida, todos leen y escriben las
mismas variables, y lo que hay que proteger, con sincronización
explícita, es la sección crítica sobre ese dato. Con pasaje de mensajes
no hay ningún dato compartido que cuidar: la sincronización ya viene
incluida en el propio acto de enviar y recibir.

---

# Escena 13 — CorreccionYModelado

**Objetivo:** contar por qué toda la materia elige "modelar" en vez de
"programar y probar": sistemas reactivos, propiedades de seguridad y
progreso, y el límite de las máquinas de estados que empuja hacia las
Redes de Petri.

**Qué hace falta saber antes:** escenas 01 (límites del testing), 08
(explosión de estados).

**Visual:**
- Dos columnas: sistema transformacional (recibe entrada, calcula, termina
  — ej. un compilador) vs sistema reactivo (interactúa permanentemente,
  no termina — ej. un cajero automático, un servidor).
- Reaparece brevemente la frase de Dijkstra (escena 01) como puente.
- Se muestra un grafo de estados de una FSM que "explota" en tamaño al
  agregar un segundo proceso (2 FSMs de 10 estados -> 100 estados producto),
  y al lado la misma situación modelada como una red de Petri compacta (20
  lugares) — sin entrar en detalle de Petri todavía, solo la comparación de
  tamaño.

## Narración

<!-- b1 -->
Cerremos el capítulo dando un paso atrás, para entender por qué toda la
materia que sigue está armada alrededor de "modelar", en vez de
"programar y cruzar los dedos con el testing".

<!-- b2 -->
Primero, una distinción de qué tipo de sistema estamos construyendo. Un
sistema transformacional recibe una entrada, calcula una salida, y
termina ahí: un compilador, un cálculo científico. La corrección, en ese
mundo, es una relación clara entre entrada y salida. Un sistema reactivo
es otra historia completamente distinta: interactúa todo el tiempo con
su entorno y con otros procesos, casi nunca calcula un resultado final, y
ni siquiera se espera que termine, como un sistema operativo o un cajero
automático. La programación concurrente vive, casi siempre, del lado
reactivo, y eso cambia la pregunta de fondo: ya no alcanza con mirar un
resultado final, hay que poder razonar sobre secuencias infinitas de
interacciones.

<!-- b3 -->
Sobre esas interacciones se verifican dos familias de propiedades. Las de
seguridad, que garantizan que nada malo ocurra nunca, como "nunca hay dos
procesos en la sección crítica a la vez". Y las de progreso, que
garantizan que algo bueno termine pasando, como "todo proceso que pide
un recurso, tarde o temprano lo consigue".

<!-- b4 -->
Y volvamos a la frase con la que arrancamos el capítulo: el testing solo
puede mostrar la presencia de errores, nunca su ausencia. La alternativa
es la verificación: en vez de probar casos sueltos, recorrer de manera
exhaustiva todas las secuencias posibles de eventos, incluidas las que
ningún test se le hubiera ocurrido generar. Y eso solo es posible si
antes construimos un modelo: una representación abstracta del programa,
que se queda con lo que necesitamos analizar de la concurrencia y
descarta el resto, como la representación interna de los datos o la
interfaz con el usuario.

<!-- b5 -->
El primer formalismo natural para esto son las máquinas de estados
finitos: son intuitivas, se dibujan fácil, y ya las conoce cualquiera que
haya diseñado software orientado a objetos. Pero tienen un límite serio.
Si tenemos dos procesos, cada uno con diez estados posibles, el sistema
completo puede llegar a tener cien estados, porque hay que considerar
todas las combinaciones entre ambos. Con tres procesos de diez estados,
mil. El dibujo se vuelve inmanejable en un abrir y cerrar de ojos: es la
misma explosión de estados que ya vimos con el interleaving, pero ahora
en el propio modelo.

<!-- b6 -->
Por eso la materia recurre a otro formalismo, mucho más compacto, capaz
de representar la concurrencia sin dibujar cada combinación de estados
por separado: las Redes de Petri. Y ahí es exactamente donde vamos
ahora.

---

# Escena 14 — PuenteAPetri

**Objetivo:** cerrar el capítulo contando quién fue Petri como una pequeña
biografía, para llegar a la regla de disparo y al ejemplo de exclusión
mutua con su invariante como la primera demostración real de "esto se
puede verificar".

**Qué hace falta saber antes:** escena 13 (por qué hace falta un
formalismo compacto).

**Visual:**
- Retrato/nombre de Carl Adam Petri con su fecha y el dato de la tesis de
  1962.
- Se construyen, elemento por elemento, los 4 componentes: lugar (círculo),
  transición (barra), arco (flecha), ficha (punto). Se anima la regla de
  disparo con un ejemplo abstracto de 1 lugar -> 1 transición -> 1 lugar.
- Se arma la red completa de exclusión mutua con 5 lugares (p1, p2, p3, p4,
  mutex) y 4 transiciones, y se disparan un par de transiciones en vivo
  usando `disparar()` de `comun/figuras.py`.
- Al final, el invariante `p2 + p4 + mutex = 1` queda fijo en pantalla
  mientras se dispara la red, mostrando que la suma nunca cambia.

## Narración

<!-- b1 -->
Para cerrar el capítulo, conozcamos al autor de la herramienta que nos va
a acompañar buena parte del curso. Carl Adam Petri era un chico alemán
que, a los trece años, ya andaba inventando diagramas y reglas para
describir procesos químicos. Décadas después, en 1962, esas ideas de
la infancia se convirtieron en su tesis doctoral, y ahí nacieron
formalmente las Redes de Petri. Hoy se lo considera, junto con Dijkstra,
uno de los fundadores de la teoría de la concurrencia.

<!-- b2 -->
Una Red de Petri se arma con muy pocas piezas. Los lugares, dibujados
como círculos, representan una condición, como "el recurso está libre".
Las transiciones, dibujadas como barras, representan un evento, como "un
proceso toma el recurso". Los arcos conectan lugares con transiciones, o
transiciones con lugares, marcando qué necesita cada evento y qué
produce. Y las fichas, esos puntitos que viven dentro de los lugares,
indican que esa condición se cumple ahora mismo. Al conjunto completo de
fichas en la red se lo llama marcado: es, literalmente, la foto del
estado del sistema en este instante.

<!-- b3 -->
Toda la dinámica de la red se resume en una sola regla, la regla de
disparo. Una transición está habilitada cuando cada uno de sus lugares
de entrada tiene, al menos, una ficha. Al dispararse, consume esas
fichas de entrada, y produce fichas nuevas en los lugares de salida. El
disparo es instantáneo y atómico. Y ojo con este detalle: que una
transición esté habilitada no obliga a que se dispare ya mismo. Justo
ahí vive el no determinismo que necesitamos para modelar concurrencia de
verdad.

<!-- b4 -->
Usemos esto para resolver, con una Red de Petri, el mismo problema que
abrió el capítulo: la exclusión mutua. Tenemos dos procesos. El proceso
uno puede estar fuera de la sección crítica, o adentro. Lo mismo el
proceso dos. Y agregamos un quinto lugar, mutex, que arranca con una
sola ficha: es el permiso para entrar, y solo hay uno.

<!-- b5 -->
Cuando el proceso uno quiere entrar, su transición necesita dos fichas
para dispararse: la de "P1 fuera", y la del mutex. Al dispararse, las
consume las dos, y deja una ficha nueva en "P1 en sección crítica".
Mientras esa ficha del mutex no vuelva, la transición de entrada del
proceso dos se queda sin su segunda ficha: está en conflicto, y no puede
dispararse. Recién cuando el proceso uno sale y devuelve la ficha al
mutex, el proceso dos tiene, si quiere, su oportunidad de entrar.

<!-- b6 -->
Y aquí llegamos al momento que justifica todo el capítulo. Sobre esta red
se puede demostrar, con matemática, un invariante: una cantidad que
nunca cambia, disparo tras disparo. En este caso, la suma de fichas en
"P1 en sección crítica", más "P2 en sección crítica", más el lugar
mutex, es siempre igual a uno. Y de ahí se deduce, directo, que la suma
de los dos primeros nunca puede superar uno: nunca, bajo ningún
interleaving posible, los dos procesos van a estar en su sección
crítica al mismo tiempo.

<!-- b7 -->
Esto ya no es testing. Es verificación: una propiedad que vale para
absolutamente todas las ejecuciones posibles de la red, demostrada una
sola vez con matemática, en vez de mil veces con casos de prueba
sueltos. Con esta idea cerramos la introducción. En el próximo capítulo
volvemos a la pieza más sencilla de todas: la máquina de estados. Las
Redes de Petri, con mucho más detalle, llegan más adelante.
