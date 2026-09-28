# Episodio 1 — El error que ningún test encontró

Capítulo 1 (Introducción), episodio 1 de 4. Duración objetivo: ~10 min.
Texto tomado de `../guion_base.md` (escenas 01 a 04), más un gancho y un cierre nuevos.

Cambios de texto respecto del guion base (para revisar):
- Escena 01 (gancho) y escena 11 (cierre): texto nuevo.
- Escena 07, b2: "en la próxima escena vamos a ver" → "más adelante vamos a ver"
  (la condición de carrera llega en el episodio 3).

---

# Escena 01 — Gancho

**Capítulo YouTube:** El error invisible
**Objetivo:** enganchar en los primeros segundos con la tragedia y la paradoja
(funcionaba perfecto, ningún test lo encontró).

## Narración

<!-- b1 -->
Seis pacientes. Una máquina que funcionaba perfecto. Y un error que solo
aparecía cuando alguien escribía demasiado rápido. Ningún test lo encontró.
Hoy vamos a entender por qué, y por qué ese error es el corazón de toda una
materia.

- «Seis pacientes» → `seis`: seis siluetas aparecen de golpe, en fila
- «Una máquina» → `maquina`: las siluetas se corren y entra la máquina de radioterapia con el haz encendido
- «que funcionaba perfecto» → `perfecto`: un tilde verde sobre la máquina
- «Y un error» → `error`: el tilde se vuelve cruz, el haz se pone rojo y la pantalla destella
- «escribía demasiado rápido» → `teclas`: corte: un teclado con las teclas encendiéndose en ráfaga
- «Ningún test lo encontró» → `tests`: corte: un contador de tests que sube hasta 1000, todos con tilde verde
- «Hoy vamos a entender por qué» → `porque`: corte a negro con un signo de pregunta gigante
- «el corazón» → `corazon`: el signo de pregunta late dos veces, como un corazón
- «toda una materia» → `titulo`: el título del episodio, solo, con un whoosh

---

# Escena 02 — ParaQueSirve

**Capítulo YouTube:** ¿Para qué sirve esta materia?

## Narración

<!-- b1 -->
En este curso de Programación Concurrente vamos a meternos de lleno en el
mundo de los sistemas críticos: esos programas que no se pueden dar el
lujo de fallar. Y para entender por qué existe toda una materia dedicada
a esto, arranquemos con una historia.

- «Programación Concurrente» → `curso`: el nombre del curso aparece solo, centrado
- «meternos de lleno» → `lleno`: la cámara se zambulle en el nombre, que se desarma hacia los costados
- «sistemas críticos» → `criticos`: entran cuatro íconos: monitor de hospital, avión, cohete y banco
- «no se pueden dar el lujo de fallar» → `fallar`: los cuatro íconos laten en rojo
- «para entender» → `pregunta`: los íconos salen; queda solo «¿Para qué sirve?»
- «arranquemos con una historia» → `historia`: la pregunta se encoge y sale con un whoosh

---

# Escena 03 — Therac

**Capítulo YouTube:** Therac-25

## Narración

<!-- b1 -->
Corría 1985. Un equipo de radioterapia llamado Therac 25 empezaba a
tratar pacientes de cáncer en varios hospitales de Estados Unidos y
Canadá. La máquina funcionaba perfecto, hasta que un día dejó de
funcionar perfecto: seis pacientes recibieron una sobredosis de
radiación varias veces mayor a la indicada, y algunos de ellos murieron.

- «Corría 1985» → `linea`: se dibuja la línea de tiempo abajo y aparece la marca 1985
- «Un equipo de radioterapia» → `maquina`: la máquina entra desde la derecha al centro, con el haz titilando
- «Therac 25» → `nombre`: aparece el nombre «Therac-25» sobre la máquina
- «tratar pacientes» → `pacientes`: un paciente aparece acostado en la camilla
- «Estados Unidos y Canadá» → `hospitales`: tres cruces de hospital aparecen arriba, una tras otra
- «funcionaba perfecto» → `ok`: un tilde verde sobre la máquina
- «dejó de funcionar perfecto» → `falla`: el tilde se vuelve una cruz roja y el haz se pone rojo y se ensancha
- «seis pacientes» → `seis`: la máquina se achica a un costado; seis siluetas en fila se tiñen de rojo una a una
- «algunos de ellos murieron» → `murieron`: algunas siluetas se apagan en gris

<!-- b2 -->
Cuando los ingenieros fueron a buscar el error, no encontraron nada
obvio. No era un botón mal programado, ni una cuenta mal hecha. El
problema aparecía solo si el operador escribía ciertas teclas
rápidamente, en un orden puntual: ahí, y solo ahí, dos partes del
software —la interfaz y el control del haz de radiación— se pisaban
entre sí. Eso tiene nombre, y lo vamos a estudiar en detalle: es una
condición de carrera.

- «los ingenieros» → `ingenieros`: plano nuevo: entran dos ingenieros con casco
- «no encontraron nada obvio» → `pensando`: sobre cada uno, una burbuja con un signo de pregunta
- «un botón mal programado» → `boton`: aparece un botón y se tacha
- «una cuenta mal hecha» → `cuenta`: aparece «2 + 2 = 4» y se tacha
- «El problema aparecía» → `operador`: plano nuevo: el operador frente a un teclado
- «ciertas teclas» → `teclas`: las teclas se encienden en ráfaga, con sonido de tecleo
- «dos partes del software» → `partes`: dos cajas vacías arriba y un dato compartido en el medio
- «la interfaz» → `interfaz`: la caja izquierda se rotula «interfaz» (azul), conectada al teclado
- «el control del haz» → `control`: la caja derecha se rotula «control del haz» (magenta)
- «se pisaban» → `pisan`: las dos cajas disparan una flecha hacia el mismo dato del medio; choque con destello rojo
- «Eso tiene nombre» → `nombre`: la cámara se acerca al choque
- «condición de carrera» → `carrera`: la cámara vuelve y el término aparece grande, en ámbar

---

# Escena 04 — OtrasFallas

**Capítulo YouTube:** Ariane 5, Patriot y Pentium

## Narración

<!-- b1 -->
Y Therac no estuvo solo. Once años después, en 1996, el cohete Ariane
cinco se autodestruyó cuarenta segundos después del despegue, porque un
número no entraba en la casilla donde el programa intentaba guardarlo.

- «Therac no estuvo solo» → `solo`: la máquina de Therac vuelve grande y se achica hasta su lugar en 1985 sobre la línea de tiempo
- «Once años después» → `m1996`: un año grande corre de 1985 a 1996 mientras un punto se desliza por la línea
- «el cohete Ariane» → `cohete`: un cohete despega desde abajo
- «se autodestruyó» → `explota`: el cohete estalla en pedazos
- «cuarenta segundos» → `segundos`: donde estalló aparece «T + 40 s»
- «un número no entraba» → `casilla`: un número de 64 bits intenta entrar en una casilla de 16 bits y se desborda

<!-- b2 -->
En 1991, un sistema de misiles Patriot falló por un error de redondeo
que se fue acumulando, milésima a milésima, hasta que el reloj interno
quedó totalmente desincronizado.

- «En 1991» → `m1991`: el año corre hacia atrás, de 1996 a 1991
- «misiles Patriot» → `misil`: un misil entra por la izquierda apuntando a un blanco
- «un error de redondeo» → `redondeo`: «0,1» se transforma en «0,0999999»: la máquina no puede guardar 0,1 exacto
- «milésima a milésima» → `reloj`: un reloj con un contador de error que sube de 0,0001 s a 0,34 s
- «totalmente desincronizado» → `falla`: el misil pasa de largo del blanco

<!-- b3 -->
Y en 1994, Intel tuvo que salir a cambiar procesadores enteros porque una
tabla grabada en el propio hardware tenía errores de división.

- «Y en 1994» → `m1994`: el año corre de 1991 a 1994
- «Intel» → `chip`: aparece un chip
- «cambiar procesadores enteros» → `cambio`: el chip sale de cuadro y entra uno nuevo, dos veces
- «una tabla grabada» → `tabla`: zoom al chip: una tabla de celdas
- «errores de división» → `division`: una celda se pone roja con «4195835 / 3145727 ≠ …»

---

# Escena 05 — LimitesTesting

**Capítulo YouTube:** Por qué el testing no alcanza

## Narración

<!-- b1 -->
Lo que tienen en común estos casos es más inquietante que la anécdota en
sí: ninguno de estos errores aparecía con cualquier entrada. Aparecían
solo ante una combinación puntual de datos, o de tiempos exactos. Y ese
es, justamente, el tipo de error que el testing convencional no
encuentra: puedes correr el programa mil veces, que funcione las mil
veces, y aun así el error siga ahí, escondido, esperando la
combinación exacta que lo dispare.

- «Lo que tienen en común» → `comun`: los cuatro casos, grandes, en fila
- «más inquietante» → `inquietante`: la cámara se acerca despacio a los cuatro casos
- «ninguno de estos errores» → `ninguno`: los cuatro se apagan en gris, uno por uno, y suben a una fila chica arriba
- «con cualquier entrada» → `entradas`: una grilla de puntos verdes (todas las entradas posibles)
- «combinación puntual» → `puntual`: un único punto se pone rojo
- «o de tiempos exactos» → `tiempos`: junto al punto rojo aparece un reloj
- «el tipo de error» → `tipo`: un círculo ámbar rodea el punto rojo
- «el testing convencional» → `testing`: aparece una lupa sobre la grilla
- «mil veces» → `mil`: la lupa salta de punto en punto y un contador sube hasta 1000; nunca cae en el rojo
- «escondido» → `escondido`: el punto rojo late
- «la combinación exacta» → `combinacion`: la lupa cae justo en el punto rojo y la pantalla destella en rojo

<!-- b2 -->
Edsger Dijkstra, una de las figuras que fundó esta disciplina, lo dijo
mejor que nadie, y lo vamos a repetir varias veces en el curso: el
testing puede mostrar la presencia de errores, pero nunca puede
demostrar su ausencia. Entonces, si probar no alcanza, ¿qué hacemos?
Construimos modelos que se puedan recorrer por completo, para demostrar
que ciertas cosas malas son directamente imposibles. De eso se trata
todo lo que viene.

- «Edsger Dijkstra» → `dijkstra`: plano nuevo: el nombre de Dijkstra
- «fundó esta disciplina» → `fundo`: debajo, tres ideas suyas que vamos a ver: «sección crítica», «semáforo», «deadlock»
- «lo dijo mejor que nadie» → `dijo`: las ideas salen y entran comillas grandes
- «varias veces» → `veces`: las comillas hacen eco, tres veces
- «la presencia de errores» → `presencia`: primera mitad de la frase; al lado, la lupa encuentra un punto rojo
- «nunca puede» → `ausencia`: segunda mitad de la frase; al lado, una grilla toda verde con un signo de pregunta
- «si probar no alcanza» → `noalcanza`: la lupa se tacha
- «¿qué hacemos?» → `quehacemos`: todo sale; la pregunta al centro
- «Construimos modelos» → `modelos`: se dibuja un grafo de estados
- «recorrer por completo» → `recorrer`: un punto recorre todos los nodos y cada uno se pone verde
- «directamente imposibles» → `imposible`: un estado rojo queda aislado, sin ninguna flecha que llegue, y se tacha
- «todo lo que viene» → `viene`: la cámara se aleja y el grafo entero brilla en verde

---

# Escena 06 — DentroCPU

**Capítulo YouTube:** Dentro del procesador

## Narración

<!-- b1 -->
Para que la historia de Therac tenga sentido técnico, tenemos que bajar
un escalón más de lo habitual, hasta el lugar donde realmente ocurren
las cosas: adentro del procesador. Y ahí, en el fondo, pasa algo
sorprendentemente simple. Un ordenador hace una sola cosa, una y otra
vez, millones de veces por segundo: lee una instrucción, la ejecuta, y
pasa a la siguiente.

- «la historia de Therac» → `therac`: vuelve la máquina de Therac, grande
- «tenga sentido técnico» → `tecnico`: la máquina se abre y adentro aparece un chip
- «bajar un escalón» → `bajar`: la cámara se zambulle en el chip
- «adentro del procesador» → `dentro`: dentro del chip: la CPU y, al lado, una lista de instrucciones
- «sorprendentemente simple» → `simple`: la CPU destella una vez, limpia
- «una sola cosa» → `bucle`: una flecha circular aparece alrededor de la CPU y empieza a girar
- «millones de veces por segundo» → `millones`: un contador de ciclos que gira a toda velocidad
- «lee una instrucción» → `lee`: la primera instrucción viaja a la CPU
- «la ejecuta» → `ejecuta`: la CPU destella
- «pasa a la siguiente» → `siguiente`: se lee la segunda instrucción

<!-- b2 -->
Imaginemos la memoria como una fila larguísima de casilleros numerados,
cada uno guardando un dato. El procesador no trabaja directamente ahí:
primero tiene que traer el dato a un registro, un casillero mucho más
pequeño, pero muchísimo más rápido, que vive dentro de la propia CPU.

- «una fila larguísima de casilleros» → `casilleros`: una fila de casilleros numerados que se estira fuera de cuadro
- «guardando un dato» → `datos`: los valores aparecen dentro
- «no trabaja directamente ahí» → `directo`: una flecha de la CPU a la memoria aparece y se tacha
- «primero tiene que traer el dato» → `registro`: un dato viaja del casillero a un registro dentro de la CPU
- «mucho más» → `pequeno`: el registro se encoge al lado de la fila de casilleros
- «muchísimo más rápido» → `rapido`: el registro destella
- «dentro de la propia CPU» → `propia`: la cámara se acerca a la CPU con el registro adentro

<!-- b3 -->
Y hay un registro que es como el dedo que va siguiendo la lectura: el
contador de programa. Guarda, en todo momento, cuál es la próxima
instrucción a ejecutar. Apenas termina una, el contador avanza a la
siguiente. Ese dedo que recorre las instrucciones, una por una, es
ni más ni menos que el hilo de ejecución de un programa.

- «el dedo» → `dedo`: aparece una flecha ámbar junto a la lista de instrucciones
- «el contador de programa» → `pc`: la flecha se rotula «PC»
- «la próxima instrucción» → `proxima`: la instrucción señalada se ilumina
- «Apenas termina una» → `avanza`: la flecha salta de instrucción en instrucción, con un tick
- «Ese dedo que recorre» → `recorre`: la flecha baja rápido hasta la última instrucción
- «el hilo de ejecución» → `hilo`: una línea se dibuja uniendo las instrucciones recorridas

---

# Escena 07 — TresPasos

**Capítulo YouTube:** Una línea, tres pasos

## Narración

<!-- b1 -->
Ahora, aquí viene el giro de esta escena, el que después va a explicar
absolutamente todo lo demás. Miremos algo tan pequeño como "contador más
igual uno". Para nosotros es una línea, una sola acción. Para el
procesador, no. Son tres pasos, uno atrás del otro: primero lee el valor
actual desde la memoria y lo trae a un registro. Después, adentro de ese
registro, le suma uno. Y recién al final, escribe ese resultado nuevo de
vuelta en la memoria.

- «aquí viene el giro» → `giro`: plano limpio, un destello
- «absolutamente todo lo demás» → `todo`: anillos que se expanden desde el centro
- «Miremos algo tan pequeño» → `pequeno`: aparece una lupa en el centro
- «contador más» → `codigo`: bajo la lupa aparece `contador += 1` grande
- «es una línea» → `linea`: la línea se subraya
- «una sola acción» → `una`: la línea se enmarca como un solo bloque, con «1 acción»
- «Para el procesador, no.» → `no`: el bloque se parte en tres piezas: LEER, SUMAR, ESCRIBIR; abajo, la memoria y la CPU
- «Son tres pasos» → `tres`: las piezas se numeran 1, 2 y 3
- «lee el valor actual» → `leer`: LEER se agranda y la celda de memoria destella
- «lo trae a un registro» → `trae`: el 0 viaja de la memoria al registro
- «adentro de ese registro» → `adentro`: la cámara se acerca a la CPU
- «le suma uno» → `sumar`: en el registro, 0 → 1
- «escribe ese resultado» → `escribir`: la cámara vuelve y el 1 viaja hacia la memoria
- «de vuelta en la memoria» → `vuelta`: la memoria queda en 1 y destella en verde

<!-- b2 -->
Guardemos bien esta idea, porque más adelante vamos a ver
exactamente qué pasa cuando dos procesos intentan hacer esto mismo al
mismo tiempo: lo que en el código parece un solo paso, para la máquina
son tres. Y entre esos tres pasos, puede pasar absolutamente cualquier
cosa.

- «Guardemos bien esta idea» → `guardar`: los tres pasos se compactan en una tarjeta «1 línea = 3 pasos»
- «más adelante» → `adelante`: la tarjeta sube y a su lado aparece «→ episodio 3»
- «dos procesos» → `dos`: aparecen dos columnas, cada una con sus tres pasos
- «al mismo tiempo» → `mismo`: las dos columnas se acercan al centro
- «parece un solo paso» → `unpaso`: cada columna se colapsa en un solo bloque «contador += 1»
- «son tres» → `sontres`: los bloques se vuelven a abrir en tres pasos
- «entre esos tres pasos» → `entre`: los pasos de las dos columnas se mezclan entre sí
- «cualquier» → `cualquier`: signos de pregunta sobre la mezcla

---

# Escena 08 — ProgramaProceso

**Capítulo YouTube:** Programa vs proceso

## Narración

<!-- b1 -->
Con esa idea del contador de programa recorriendo instrucciones, ya
podemos contar algo que usamos todo el tiempo sin pensarlo: qué diferencia
hay entre un programa y un proceso. Un programa, mientras nadie lo
ejecuta, es una entidad completamente estática: un archivo guardado en el
disco, código quieto que no hace absolutamente nada por sí solo.

- «recorriendo instrucciones» → `recap`: vuelve la lista de instrucciones con el PC bajando
- «usamos todo el tiempo» → `apps`: aparecen varias ventanas de aplicaciones, una tras otra
- «sin pensarlo» → `pensarlo`: las ventanas se desvanecen
- «un programa y un proceso» → `ambos`: las dos palabras enfrentadas, «programa» y «proceso»
- «mientras nadie lo ejecuta» → `nadie`: «proceso» y el signo salen; «programa» se apaga a gris
- «completamente estática» → `estatica`: «programa» se vuelve una hoja gris quieta
- «guardado en el disco» → `disco`: la hoja se guarda dentro de un disco
- «no hace absolutamente nada» → `nada`: un indicador de actividad plano

<!-- b2 -->
Pero en el momento en que alguien lo ejecuta, ese programa nace a otra
cosa: un proceso. Ahí sí empieza a tener un contador de programa
avanzando de verdad, una porción de memoria propia, y un estado que
cambia segundo a segundo. Si el programa es la receta, el proceso es la
tarta que se está horneando ahora mismo, en este instante.

- «alguien lo ejecuta» → `ejecuta`: la hoja sale del disco
- «un proceso» → `proceso`: la hoja se transforma en una caja de proceso que late
- «un contador de programa» → `pc`: dentro de la caja, una flecha PC que avanza
- «memoria propia» → `memoria`: un bloque de memoria se engancha a la caja
- «un estado que» → `estado`: una etiqueta de estado que va cambiando
- «la receta» → `receta`: plano partido: a la izquierda una receta
- «la tarta» → `tarta`: a la derecha una tarta dentro de un horno encendido
- «ahora mismo» → `ahora`: el horno se enciende más fuerte, dos veces

---

# Escena 09 — PCB

**Capítulo YouTube:** La ficha del proceso (PCB)

## Narración

<!-- b1 -->
Y como todo lo que nace en un sistema operativo necesita una identidad,
apenas nace un proceso el sistema le abre una ficha: el Bloque de Control
de Proceso. Ahí se anota todo lo que hace falta saber de él: un
identificador único, su estado actual, el valor de su contador de
programa, el contenido de sus registros, qué región de memoria le
pertenece, su prioridad, y qué recursos tiene asignados, como archivos
abiertos.

- «necesita una identidad» → `identidad`: aparece un proceso con un signo de pregunta encima: todavía no tiene identidad
- «apenas nace un proceso» → `nace`: el proceso late, recién nacido
- «le abre una ficha» → `ficha`: una ficha vacía se desliza junto al proceso
- «el Bloque de Control» → `pcb`: la ficha recibe su título: PCB
- «Ahí se anota todo» → `anota`: se dibujan los renglones vacíos de la ficha
- «un identificador único» → `pid`: fila: PID 4127
- «su estado actual» → `estado`: fila: estado = ejecutando
- «su contador de» → `pc`: fila: PC = 0x2A
- «sus registros» → `regs`: fila: registros
- «región de memoria» → `mem`: fila: memoria
- «su prioridad» → `prio`: fila: prioridad
- «archivos» → `archivos`: fila: archivos abiertos

<!-- b2 -->
Esa ficha tan completa tiene un costo: cada vez que el sistema operativo
decide pasar de un proceso a otro, tiene que guardar ese bloque entero y
cargar el del siguiente. Toda esa información hay que moverla, y eso
lleva tiempo. Es el precio de tener procesos tan ricos en detalle.

- «tiene un costo» → `costo`: la ficha se achica junto a su proceso; aparece un segundo proceso con su ficha
- «pasar de un proceso a otro» → `pasar`: la CPU en el centro, con el primer proceso adentro
- «guardar ese bloque» → `guardar`: la ficha del primero vuela afuera de la CPU
- «cargar el del siguiente» → `cargar`: la ficha del segundo entra a la CPU
- «Toda esa información» → `info`: las fichas van y vienen entre la CPU y sus procesos, varias veces
- «lleva tiempo» → `tiempo`: una barra roja de tiempo perdido se llena
- «Es el precio» → `precio`: la barra roja late dos veces

<!-- b3 -->
Una última vuelta de tuerca: un proceso puede, a su vez, dar vida a otro.
Al que crea se lo llama proceso padre, al creado, proceso hijo. Y apenas
nace el hijo, algo importante pasa: padre e hijo corren de manera
concurrente, uno no espera al otro. Cada uno sigue su propio camino.

- «vuelta de tuerca» → `tuerca`: plano nuevo: una tuerca gira una vuelta
- «dar vida a otro» → `vida`: de un proceso se desprende otro más pequeño
- «proceso padre» → `padre`: el primero se rotula «padre»
- «proceso hijo» → `hijo`: el segundo se rotula «hijo»
- «algo importante pasa» → `importante`: los dos destellan en ámbar
- «corren de manera» → `corren`: cada uno con su flecha PC avanzando a su propio ritmo
- «uno no espera al otro» → `noespera`: un reloj de espera aparece entre los dos y se tacha
- «su propio camino» → `camino`: se separan hacia los costados

---

# Escena 10 — EstadosProceso

**Capítulo YouTube:** La vida de un proceso: sus 5 estados

## Narración

<!-- b1 -->
Sigamos a ese proceso recién nacido a lo largo de toda su vida, porque no
va a quedarse quieto: va a recorrer distintos estados, uno tras otro,
hasta el final. El modelo más simple para contar esta historia tiene solo
tres momentos: listo para ejecutar, ejecutando, o bloqueado. Pero los
sistemas operativos reales cuentan una versión más detallada, con cinco
momentos, y ese vocabulario nos va a acompañar todo el curso.

- «Sigamos a ese proceso» → `sigamos`: aparece un punto (el proceso) en el centro
- «no va a quedarse quieto» → `quieto`: el punto rebota de un lado a otro
- «recorrer distintos estados» → `recorrer`: el punto dibuja un camino en zigzag
- «hasta el final» → `final`: el punto se frena y el camino se borra
- «El modelo más simple» → `modelo`: el punto se aparta a una esquina, dejando lugar
- «solo tres momentos» → `tres`: aparecen tres casilleros vacíos en triángulo
- «listo para ejecutar» → `listo`: el primero se rotula «Listo»
- «ejecutando» → `ejecutando`: el segundo, «Ejecución»
- «o bloqueado» → `bloqueado`: el tercero, «Bloqueado»
- «sistemas operativos reales» → `reales`: los tres destellan
- «más detallada» → `detallada`: una lupa pasa sobre el triángulo
- «con cinco» → `cinco`: el triángulo se reacomoda y suman Nuevo y Terminado
- «nos va a acompañar» → `acompanar`: se dibujan todas las flechas entre estados

<!-- b2 -->
Nuestro proceso nace en el estado Nuevo: existe, pero el sistema
operativo todavía no terminó de darle el visto bueno. En cuanto lo
admite, pasa a Preparado: ya está listo para correr, solo le falta que
el procesador quede libre para él.

- «nace en el estado Nuevo» → `nuevo`: el punto entra al estado Nuevo, que se ilumina
- «existe» → `existe`: el punto late
- «darle el visto bueno» → `visto`: sobre el punto aparece un reloj de espera
- «En cuanto lo» → `admite`: el reloj se vuelve tilde; flecha «admitido»; el punto viaja a Preparado
- «ya está listo para correr» → `correr`: Preparado destella
- «el procesador quede libre» → `libre`: arriba, la CPU está ocupada por otro proceso

<!-- b3 -->
Y llega su momento: el planificador lo elige, y pasa a Ejecución. Ahí
tiene, por fin, el control real del procesador. En una máquina de un solo
núcleo, en este estado solo puede estar un proceso a la vez: el
protagonista del momento.

- «el planificador lo elige» → `elige`: flecha «planificador»; el otro proceso deja la CPU
- «pasa a Ejecución» → `ejecucion`: el punto viaja a Ejecución
- «el control real» → `control`: la CPU se ilumina del color del proceso
- «un solo núcleo» → `nucleo`: dentro de la CPU se marca un único núcleo
- «solo puede estar un proceso» → `uno`: otro punto intenta entrar a Ejecución y rebota
- «el protagonista» → `protagonista`: un foco ilumina al punto en Ejecución

<!-- b4 -->
Desde Ejecución, la historia puede ir por dos caminos. Si se le termina
el tiempo asignado, o algo más urgente lo interrumpe, vuelve a
Preparado, a hacer fila para su próximo turno. Pero si necesita algo que
todavía no tiene, como el resultado de una lectura de disco, pasa a
Bloqueado, y ahí espera. Cuando por fin ocurre lo que estaba esperando,
no vuelve directo a Ejecución: vuelve a la fila de Preparado, como
cualquier otro que recién llega.

- «dos caminos» → `caminos`: dos flechas salen de Ejecución y parpadean
- «se le termina» → `quantum`: junto a la CPU, un reloj da una vuelta completa
- «algo más urgente» → `urgente`: un punto rojo con «!» aparece junto a la CPU
- «vuelve a» → `vuelve`: rótulo «fin de quantum»; el punto vuelve a Preparado
- «su próximo turno» → `turno`: el punto se pone detrás de otro que ya esperaba
- «si necesita algo» → `necesita`: el punto vuelve a Ejecución y le aparece un signo de pregunta
- «una lectura de disco» → `disco`: aparece un disco; rótulo «espera E/S»; el punto viaja a Bloqueado
- «ahí espera» → `espera`: el punto late despacio en Bloqueado
- «por fin ocurre» → `ocurre`: el disco responde con un destello
- «no vuelve directo» → `nodirecto`: una flecha Bloqueado→Ejecución aparece tachada
- «la fila de Preparado» → `fila`: el punto viaja a Preparado y se pone al final de la fila

<!-- b5 -->
Y en algún momento, la historia termina: Terminado. Puede ser porque el
proceso hizo su trabajo y cerró en paz, o porque el sistema detectó un
error fatal y lo tuvo que bajar.

- «la historia termina» → `termina`: el punto viaja a Ejecución
- «Terminado» → `terminado`: rótulo «fin»; el punto llega a Terminado, que se ilumina
- «hizo su trabajo» → `trabajo`: una barra de progreso se completa sobre Terminado
- «cerró en paz» → `paz`: tilde verde
- «error fatal» → `fatal`: variante: cruz roja

<!-- b6 -->
Hay un capítulo extra, para cuando un proceso además es sacado de la
memoria principal por falta de espacio. Un proceso suspendido puede
seguir bloqueado, si la causa de su espera sigue vigente, o puede quedar
suspendido pero ya listo, si esa causa ya desapareció y solo falta que
alguien lo devuelva a memoria para que retome su camino.

- «sacado de la memoria principal» → `sacado`: el diagrama sube; un punto en Bloqueado es expulsado hacia abajo
- «por falta de espacio» → `espacio`: arriba a la derecha, una barra de memoria llena, en rojo
- «Un proceso suspendido» → `suspendido`: el punto expulsado se pone gris, dormido
- «seguir bloqueado» → `susp_bloq`: aparece abajo «Suspendido bloqueado», con línea punteada desde Bloqueado
- «suspendido pero ya listo» → `susp_listo`: aparece «Suspendido listo», con línea punteada hacia Preparado
- «esa causa ya desapareció» → `desaparecio`: el punto viaja a Suspendido listo y recupera el color
- «devuelva a memoria» → `devuelve`: el punto sube de Suspendido listo a Preparado

---

# Escena 11 — Cierre

**Capítulo YouTube:** Lo que viene

## Narración

<!-- b1 -->
Resumamos lo que ya sabemos. Un procesador ejecuta instrucciones de una
en una. Una sola línea de código pueden ser varios pasos. Y un proceso se
pasa la vida saltando entre estados, pero en Ejecución solo puede haber
uno a la vez. Y aquí está la trampa: si solo uno puede ejecutar, ¿cómo es
posible que ahora mismo tengas música, el navegador y este vídeo
funcionando a la vez? Eso es exactamente lo que vamos a desarmar en el
próximo episodio.

- «Resumamos» → `resumen`: plano limpio
- «lo que ya sabemos» → `sabemos`: tres casilleros vacíos en fila
- «Un procesador» → `r1`: el primero se llena con la CPU y su flecha PC
- «Una sola línea» → `r2`: el segundo, con «1 línea = 3 pasos»
- «Y un proceso» → `r3`: el tercero, con el mini diagrama de estados
- «saltando entre estados» → `salta`: un punto salta de estado en estado
- «solo puede haber» → `uno`: el estado Ejecución se ilumina con un único punto
- «aquí está la trampa» → `trampa`: todo se corre; alarma
- «si solo uno puede ejecutar» → `solouno`: el estado Ejecución vuelve a destellar
- «cómo es posible» → `posible`: un signo de pregunta gigante
- «música» → `musica`: la pregunta sale; aparece una ventana de música
- «el navegador» → `navegador`: aparece una ventana de navegador
- «este vídeo» → `video`: aparece una ventana de vídeo
- «a la vez» → `vez`: las tres laten a la vez
- «vamos a desarmar» → `desarmar`: las tres ventanas se cortan en tiras que se intercalan
- «próximo episodio» → `proximo`: todo sale; «Episodio 2» con un whoosh
