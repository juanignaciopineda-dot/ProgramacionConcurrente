# Episodio 2 — La ilusión de hacerlo todo a la vez

Capítulo 1 (Introducción), episodio 2 de 4. Duración objetivo: ~10 min.
Texto tomado de `../guion_base.md` (escenas 05 a 08), más un gancho y un cierre nuevos.
Las 4 escenas del guion base se parten en 8 escenas atómicas, más gancho y cierre.

Cambios de texto respecto del guion base (aprobados por el usuario):
- Escena 01 (gancho) y escena 10 (cierre): texto nuevo.
- Castellano de España: "adentro" → "dentro"; "se lo llama" / "se la llama" → "se le llama".
- Escena 04, b2: "cada variable de instancia, cada variable estática" → "todas sus
  variables globales" (sin jerga de Java).
- Escena 05, b1: "Y ahí aparece, otra vez, la frase que va a resonar" → "Y ahí
  aparece la idea que va a resonar".
- Escena 06, b1: "Con la escena del truco de la multiprogramación" → "Con el
  truco de la multiprogramación".
- Escena 06, b2: "el programa con un solo núcleo de la escena anterior" → "el
  sistema con un solo núcleo que vimos al principio".

---

# Escena 01 — Gancho

**Capítulo YouTube:** La gran ilusión
**Objetivo:** enganchar con la paradoja (parece simultáneo y no lo es) y con la
amenaza de fondo (miles de ejecuciones posibles, una sola basta para romperlo).

## Narración

<!-- b1 -->
Ahora mismo, tu ordenador te está engañando. Parece que hace muchas cosas
a la vez y en realidad no. Además, cuando de verdad las hace a la vez, aparece algo peor: el
mismo programa puede ejecutarse de miles de formas distintas. Y basta una
sola para romperlo todo.

- «Ahora mismo» → `ahora`: un ordenador aparece de golpe en el centro
- «te está engañando» → `engana`: la pantalla del ordenador guiña: destello y un «¿?» pequeño encima
- «muchas cosas» → `cosas`: en la pantalla, tres ventanas (música, navegador, vídeo), latiendo a la vez
- «en realidad no» → `no`: las ventanas se cortan en tiras que se encienden de a una; sonido de error
- «de verdad las hace a la vez» → `paralelo`: corte: cuatro núcleos en fila, los cuatro encendidos a la vez
- «algo peor» → `peor`: los núcleos destellan en rojo, con alarma
- «el mismo programa» → `programa`: corte: un documento (el programa) a la izquierda
- «miles de formas distintas» → `miles`: del documento brota un árbol de caminos que se ramifica hasta llenar la pantalla
- «basta una» → `una`: todos los caminos se apagan salvo uno, que se pone rojo
- «romperlo todo» → `romper`: impacto y destello rojo; después, el título del episodio solo, con un whoosh

---

# Escena 02 — Multiprogramacion

**Capítulo YouTube:** El truco de la multiprogramación

## Narración

<!-- b1 -->
Ahora que conocemos la vida de un proceso, hagamos una pregunta que
parece obvia pero no lo es: ¿cómo hace tu ordenador, con un solo
procesador, para tener el navegador, la música y este mismo vídeo
corriendo "al mismo tiempo"? La respuesta, spoiler, es que en realidad no
corren al mismo tiempo. Es un truco, y muy bueno: los intercala tan
rápido que nuestro cerebro no lo nota.

- «la vida de un proceso» → `vida`: vuelve el mini diagrama de 5 estados de E1, con un punto recorriéndolo
- «hagamos una pregunta» → `pregunta`: el diagrama sale; un signo de pregunta grande
- «pero no lo es» → `noes`: el signo de pregunta se tambalea
- «tu ordenador» → `ordenador`: el signo se achica arriba; entra el ordenador del gancho
- «con un solo» → `cpu`: el ordenador se abre: abajo, una CPU con un único núcleo
- «el navegador» → `navegador`: arriba a la izquierda, una ventana de navegador
- «la música» → `musica`: arriba al centro, una ventana de música
- «este mismo vídeo» → `video`: arriba a la derecha, una ventana de vídeo
- «al mismo tiempo» → `mismo`: tres cables unen las ventanas con la CPU y laten a la vez
- «spoiler» → `spoiler`: un sello «SPOILER» cae sobre la pantalla y sale
- «no corren al mismo tiempo» → `nocorren`: los cables se apagan; queda encendido uno solo
- «Es un truco» → `truco`: «truco» aparece de golpe, en ámbar, arriba
- «los intercala» → `intercala`: el cable encendido salta de ventana en ventana, cada vez más rápido
- «nuestro cerebro no lo nota» → `nota`: los saltos son tan rápidos que las tres ventanas parecen encendidas a la vez

<!-- b2 -->
A este truco se le llama multiprogramación: gestionar varios procesos
dentro de un sistema con un solo procesador. Dentro del sistema
operativo hay un componente que oficia de director de orquesta, el
planificador, decidiendo en cada instante cuál de los procesos
preparados pasa a ejecutar, y por cuánto tiempo exactamente: ese tiempo
asignado se llama quantum.

- «multiprogramación» → `multi`: corte: «multiprogramación» grande, en ámbar
- «varios procesos» → `procesos`: la palabra sube; tres procesos P1, P2, P3 en fila a la izquierda
- «un solo procesador» → `uno`: a la derecha, la CPU de un núcleo
- «Dentro del sistema» → `so`: un marco grande «sistema operativo» envuelve todo
- «un componente» → `componente`: dentro del marco, entre la fila y la CPU, una caja vacía
- «director de orquesta» → `director`: la caja se llena con una batuta
- «el planificador» → `planificador`: la batuta se rotula «planificador»
- «en cada instante» → `instante`: la batuta señala P1, P2 y P3, rápido
- «pasa a ejecutar» → `ejecutar`: P1 viaja a la CPU
- «por cuánto tiempo» → `tiempo`: sobre la CPU, una barra de tiempo llena que empieza a vaciarse
- «ese tiempo asignado» → `asignado`: la barra se enmarca en ámbar
- «se llama quantum» → `quantum`: la barra se rotula «quantum»

---

# Escena 03 — CambioContexto

**Capítulo YouTube:** El cambio de contexto

## Narración

<!-- b1 -->
Cuando el quantum de un proceso se acaba, o el proceso necesita esperar
algo, pasa el momento más delicado de esta historia: el cambio de
contexto. El sistema operativo guarda el bloque de control completo del
proceso saliente, y carga el del que entra. Ese ida y vuelta no es
gratis: cada cambio de contexto consume tiempo de procesador que no se
usa para hacer trabajo útil. Es el peaje que se paga por la ilusión de
simultaneidad.

- «el quantum de un proceso» → `quantum`: P1 en la CPU, con su barra de quantum vaciándose; P2 y P3 esperan en fila
- «se acaba» → `acaba`: la barra llega a cero y parpadea en rojo, con un tick
- «necesita esperar» → `esperar`: sobre P1 aparece un reloj de espera
- «el momento más delicado» → `delicado`: la cámara se acerca a la CPU; latido
- «el cambio de» → `cambio`: «cambio de contexto» aparece grande, en ámbar
- «El sistema operativo» → `so`: la cámara vuelve; el marco «sistema operativo» destella
- «guarda el bloque de control» → `guarda`: la ficha PCB de P1 se copia y sale de la CPU
- «proceso saliente» → `saliente`: P1 vuelve a la fila, con su ficha
- «carga el del que entra» → `carga`: la ficha PCB de P2 entra a la CPU y P2 queda ejecutando
- «Ese ida y vuelta» → `idavuelta`: las fichas van y vienen entre la fila y la CPU, dos veces
- «no es» → `gratis`: abajo aparece una barra de tiempo del procesador
- «consume tiempo de procesador» → `consume`: la barra se llena de tramos azules y, entre ellos, tramos rojos
- «trabajo útil» → `util`: los tramos azules se rotulan «útil»; los rojos, «cambio»
- «el peaje» → `peaje`: una barrera de peaje baja delante de la CPU
- «la ilusión de» → `ilusion`: la barrera sube y las tres ventanas del principio laten a la vez

<!-- b2 -->
El resultado, si lo miramos en una línea de tiempo, es un vaivén
constante: un pedacito de un proceso, un pedacito de otro, un pedacito
del tercero. No hay paralelismo real ahí dentro, y encima está ese
costo extra de los cambios de contexto. Y aun así, este intercalado
mejora muchísimo el aprovechamiento del procesador, comparado con hacer
esperar a todos los demás mientras uno solo ocupa la máquina de punta a
punta.

- «una línea de tiempo» → `linea`: corte: un eje de tiempo con tres carriles, P1, P2 y P3
- «un vaivén constante» → `vaiven`: un cabezal empieza a recorrer el eje
- «un pedacito de un proceso» → `p1`: se pinta un tramo en el carril de P1
- «un pedacito de otro» → `p2`: un tramo en el carril de P2
- «del tercero» → `p3`: un tramo en P3, y el patrón se repite hasta el final del eje
- «No hay paralelismo real» → `noparalelo`: una línea vertical recorre el eje: en cada instante toca un solo tramo
- «ese costo extra» → `costo`: entre tramo y tramo aparecen franjas rojas finas
- «de los cambios de contexto» → `cambios`: las franjas rojas destellan juntas
- «Y aun así» → `aunasi`: el gráfico sube y se achica; se rotula «intercalado»
- «mejora muchísimo» → `mejora`: un medidor de uso del procesador junto al gráfico sube casi al máximo
- «comparado con» → `comparado`: debajo aparece un segundo eje, «uno detrás de otro»
- «a todos los demás» → `esperan`: P1 ocupa el eje entero, con huecos grises de espera; P2 y P3, al final, con relojes
- «de punta a punta» → `punta`: el medidor de abajo queda a la mitad; tilde verde sobre el intercalado

---

# Escena 04 — Hilos

**Capítulo YouTube:** Hilos: compañeros de casa

## Narración

<!-- b1 -->
Hasta aquí hablamos de procesos como si cada uno tuviera una sola línea de
ejecución. Pero dentro de un mismo proceso puede convivir más de una: a
cada una de esas líneas se le llama hilo, y conviene pensarlos como
compañeros de casa que comparten casi todo.

- «Hasta aquí» → `proceso`: una caja de proceso grande
- «una sola línea de» → `linea`: dentro, una flecha PC baja dibujando una línea
- «Pero dentro» → `dentro`: la cámara se acerca a la caja
- «puede convivir más de una» → `mas`: aparecen dos líneas más, cada una con su flecha PC, en otros colores
- «cada una de esas líneas» → `lineas`: las tres líneas destellan, una por una
- «se le llama hilo» → `hilo`: «hilo» aparece de golpe; las tres líneas se rotulan H1, H2 y H3
- «compañeros de casa» → `casa`: la caja se vuelve una casa con tejado; los hilos, tres personas dentro
- «casi todo» → `todo`: las tres personas se giran hacia el centro de la casa

<!-- b2 -->
¿Qué comparten exactamente? Todo el espacio de memoria: todas sus
variables globales. También comparten los archivos abiertos, y la
autenticación y los permisos del proceso que los contiene. Es una
convivencia muy íntima.

- «¿Qué comparten exactamente?» → `que`: un signo de pregunta sobre la casa
- «Todo el espacio de memoria» → `memoria`: en el centro de la casa aparece un bloque de memoria compartida
- «variables globales» → `globales`: dentro de la memoria: «x = 5», «total = 0»
- «los archivos abiertos» → `archivos`: junto a la memoria, dos documentos
- «la autenticación» → `auth`: aparece una llave
- «los permisos» → `permisos`: aparece un escudo con un tilde
- «que los contiene» → `contiene`: el contorno de la casa destella, envolviendo todo
- «convivencia muy íntima» → `intima`: los tres hilos lanzan flechas a la memoria, a la vez

<!-- b3 -->
Lo único que cada hilo guarda para sí, como su propio dormitorio dentro
de esa casa compartida, es su contador de programa, su propia pila de
ejecución, y el estado de sus registros. Cada uno sabe en qué instrucción
va, cada uno tiene su espacio para variables locales, pero todos leen y
escriben la misma memoria común.

- «Lo único» → `unico`: lo compartido se atenúa y sube; los hilos se separan en tres columnas
- «su propio dormitorio» → `dormitorio`: cada hilo recibe un recuadro propio, su cuarto
- «esa casa compartida» → `compartida`: la casa se dibuja alrededor de los tres cuartos
- «su contador de programa» → `pc`: en cada cuarto, una flecha PC
- «su propia pila» → `pila`: en cada cuarto, una pila de marcos
- «sus registros» → `registros`: en cada cuarto, dos registros pequeños
- «en qué instrucción» → `instruccion`: cada PC salta a una altura distinta
- «variables locales» → `locales`: cada pila crece un marco
- «la misma memoria común» → `comun`: la memoria compartida vuelve a brillar; flechas de los tres cuartos hacia ella

---

# Escena 05 — HilosBaratos

**Capítulo YouTube:** Exclusión mutua y el precio de un hilo

## Narración

<!-- b1 -->
Y ahí aparece la idea que va a resonar en toda la materia:
como cualquier hilo puede modificar algo compartido, y los demás lo ven
modificado al instante, hace falta algún mecanismo que controle ese
acceso. Eso se llama exclusión mutua, y es el problema que perseguimos
capítulo tras capítulo.

- «la idea que va a resonar» → `resonar`: la memoria compartida al centro; ondas que se expanden desde ella
- «cualquier hilo puede modificar» → `modifica`: H1 escribe en la memoria: «x = 5» → «x = 9»
- «los demás lo ven» → `ven`: H2 y H3 destellan con el 9
- «al instante» → `instante`: flash
- «algún mecanismo» → `mecanismo`: una puerta con candado aparece delante de la memoria
- «ese acceso» → `acceso`: H1 pasa; H2 intenta pasar y rebota
- «exclusión mutua» → `exclusion`: «exclusión mutua» grande, en ámbar
- «capítulo tras capítulo» → `capitulos`: el término se achica y sube; debajo, fichas «Cap. 1», «Cap. 2», «Cap. 3» se apilan

<!-- b2 -->
Pero hay una buena noticia sobre esta convivencia tan cercana: sale
barata. Crear un hilo es, más o menos, diez veces más económico que crear
un proceso entero, porque hay mucha menos información que armar: ni
bloque de control completo, solo un contexto pequeño y una pila. Por la
misma razón, cambiar de contexto entre hilos es casi gratis, porque ni
siquiera cambia el espacio de memoria. Y comunicarse entre ellos no
necesita pasar por el sistema operativo: alcanza con leer y escribir una
variable que ya comparten.

- «una buena noticia» → `noticia`: corte; destello verde
- «sale» → `tabla`: una tabla de dos columnas: «proceso» y «hilo»
- «Crear un hilo» → `crear`: primera fila: «crear»
- «diez veces» → `diez`: en la fila, una barra larga para proceso y una diez veces más corta para hilo; «×10»
- «un proceso entero» → `entero`: la barra de proceso destella en rojo
- «mucha menos información» → `info`: al lado, la ficha PCB grande frente a una ficha pequeña
- «ni bloque de control» → `nibloque`: la ficha PCB grande se tacha
- «solo un contexto pequeño» → `pila`: la ficha pequeña muestra solo PC y una pila
- «Por la misma razón» → `razon`: las fichas salen; la celda de hilo de la primera fila, en verde
- «cambiar de contexto entre hilos» → `cambio`: segunda fila: «cambio de contexto»
- «casi gratis» → `gratis`: barra larga roja para proceso, barra mínima para hilo
- «el espacio de memoria» → `memoria`: en la celda de hilo, «misma memoria»
- «comunicarse entre ellos» → `comunicar`: tercera fila: «comunicarse»
- «el sistema operativo» → `so`: en la columna proceso, una flecha que da un rodeo por «SO»
- «alcanza con leer» → `leer`: en la columna hilo, una flecha directa a una variable
- «una variable que ya comparten» → `variable`: la variable destella; las tres celdas de hilo, en verde

---

# Escena 06 — ConcurrenciaParalelismo

**Capítulo YouTube:** Concurrencia no es paralelismo

## Narración

<!-- b1 -->
Con el truco de la multiprogramación en la cabeza, podemos
desarmar una confusión muy común: la gente usa "concurrente" y "paralelo"
como si fueran sinónimos, y no lo son. Son dos preguntas distintas.

- «el truco de la multiprogramación» → `truco`: vuelve la CPU de un núcleo con las ventanas turnándose
- «podemos desarmar» → `desarmar`: la CPU se desarma en piezas que salen volando
- «una confusión muy común» → `confusion`: corte: dos burbujas de diálogo vacías
- «la gente usa» → `gente`: las burbujas se llenan: «concurrente» y «paralelo»
- «como si fueran sinónimos» → `sinonimos`: entre las dos aparece un «=»
- «y no lo son» → `nolo`: el «=» se vuelve «≠», con sonido de error
- «dos preguntas distintas» → `preguntas`: las palabras se separan a los costados; debajo de cada una, un signo de pregunta

<!-- b2 -->
Concurrencia responde a cómo está armado el programa: es un conjunto de
tareas que, en principio, podrían ejecutarse en cualquier orden, o
intercaladas entre sí. Fíjate que el sistema con un solo núcleo que
vimos al principio sigue siendo perfectamente concurrente, aunque nunca haya
dos cosas pasando literalmente al mismo tiempo. Y sigue teniendo todos
los mismos dolores de cabeza de coordinación que ya vimos.

- «Concurrencia responde» → `concurrencia`: plano izquierdo: «concurrencia» arriba
- «cómo está armado» → `armado`: debajo, «¿cómo está armado?»
- «un conjunto de» → `tareas`: tres tareas A, B y C en fila
- «podrían ejecutarse» → `ejecutarse`: las tareas destellan
- «en cualquier orden» → `orden`: las tareas se reordenan: C A B, después B C A
- «intercaladas entre sí» → `intercaladas`: las tareas se cortan en pedacitos que se intercalan
- «Fíjate» → `fijate`: la cámara se acerca un poco
- «un solo núcleo» → `nucleo`: la cámara vuelve; abajo, un núcleo que ejecuta los pedacitos de a uno
- «perfectamente concurrente» → `concurrente`: tilde verde sobre el plano
- «dos cosas pasando literalmente» → `literal`: una línea vertical recorre los pedacitos: nunca toca dos a la vez
- «Y sigue teniendo» → `sigue`: el tilde verde se vuelve un «!» ámbar
- «dolores de cabeza» → `dolores`: rayos rojos sobre el plano
- «de coordinación» → `coordinacion`: dos tareas apuntan al mismo dato y chocan, con destello rojo

<!-- b3 -->
Paralelismo responde a otra pregunta: qué pasa realmente en el hardware.
Es que haya instrucciones ejecutándose de manera simultánea, de verdad, en
piezas de hardware distintas: núcleos distintos de un mismo procesador,
procesadores distintos, u ordenadores distintos conectados en red.

- «Paralelismo responde» → `paralelismo`: plano derecho: «paralelismo» arriba
- «realmente en el hardware» → `hardware`: debajo, «¿qué pasa en el hardware?» y cuatro núcleos
- «Es que haya instrucciones» → `instrucciones`: sobre cada núcleo, una tira de instrucciones
- «de manera simultánea» → `simultanea`: cuatro barras crecen juntas, una por núcleo
- «de verdad» → `verdad`: la línea vertical recorre las barras: toca las cuatro a la vez
- «núcleos distintos» → `nucleos`: los cuatro núcleos se enmarcan en un solo chip
- «procesadores distintos» → `procesadores`: el chip se duplica: dos chips
- «ordenadores distintos» → `ordenadores`: los chips se vuelven dos ordenadores
- «conectados en red» → `red`: una línea de red une los ordenadores, con paquetes viajando

---

# Escena 07 — TresEscenarios

**Capítulo YouTube:** Uno, varios o muchos ordenadores

## Narración

<!-- b1 -->
Por eso la concurrencia se nos aparece en tres escenarios bien distintos,
según cuánto hardware hay detrás. Multiprogramación, cuando gestionamos
varios procesos dentro de un único procesador. Multiprocesamiento, cuando
hay varios procesadores en la misma máquina. Y procesamiento distribuido,
cuando hay varios ordenadores separados, hablándose por red.

- «tres escenarios» → `tres`: tres columnas vacías
- «cuánto hardware hay detrás» → `hardware`: bajo las columnas, una flecha «más hardware» de izquierda a derecha
- «Multiprogramación» → `multiprog`: primera columna: «multiprogramación»
- «varios procesos» → `procesos`: tres procesos pequeños bajo el título
- «un único procesador» → `unico`: una CPU, y los tres procesos se turnan en ella
- «Multiprocesamiento» → `multiproc`: segunda columna: «multiprocesamiento»
- «varios procesadores» → `procs`: cuatro CPU, todas encendidas
- «la misma máquina» → `maquina`: las cuatro CPU se enmarcan en un solo ordenador
- «procesamiento distribuido» → `distribuido`: tercera columna: «distribuido»
- «varios ordenadores separados» → `ordenadores`: tres ordenadores separados
- «hablándose por red» → `red`: líneas de red entre ellos, con paquetes

<!-- b2 -->
La concurrencia está ahí en los tres casos, sin excepción. Lo único que
cambia de uno a otro es si, además de intercalarse en la estructura, las
tareas también corren físicamente al mismo tiempo. Esa es la línea
divisoria entre estructura y ejecución, y la vamos a usar todo el curso.

- «La concurrencia está ahí» → `ahi`: sobre las tres columnas se tiende una banda ámbar, «concurrencia»
- «sin excepción» → `excepcion`: un tilde verde en cada columna
- «Lo único que cambia» → `cambia`: debajo, una fila «¿en paralelo?»
- «además de intercalarse» → `intercalarse`: en la banda ámbar, tiras intercaladas en cada columna
- «las tareas también» → `tareas`: la fila de abajo destella
- «físicamente al mismo tiempo» → `fisico`: columna 1: cruz; columnas 2 y 3: tilde
- «la línea» → `linea`: una línea horizontal separa la banda de arriba de la fila de abajo
- «entre estructura y ejecución» → `rotulos`: la banda se rotula «estructura»; la fila, «ejecución»
- «todo el curso» → `curso`: todo se achica en una tarjeta y sale

---

# Escena 08 — Interleaving

**Capítulo YouTube:** Interleaving: el orden ya no es tuyo

## Narración

<!-- b1 -->
Llegamos al concepto que sostiene todo este capítulo, y todo lo que sigue
en la materia. Se llama interleaving, intercalado, y una vez que lo
vemos, no se puede dejar de ver: los procesos concurrentes ejecutan
intercalando las acciones atómicas que los componen.

- «Llegamos al concepto» → `concepto`: plano limpio; un destello
- «sostiene todo este capítulo» → `pilar`: una columna sube desde abajo y sostiene una losa «capítulo 1»
- «todo lo que sigue» → `sigue`: sobre la losa se apilan más losas, una tras otra
- «Se llama interleaving» → `interleaving`: corte: «interleaving» grande, en ámbar
- «intercalado» → `intercalado`: debajo, «intercalado»
- «no se puede dejar de ver» → `ver`: un ojo se abre sobre la palabra
- «los procesos concurrentes» → `procesos`: dos filas de fichas: azul (P1) y magenta (P2)
- «intercalando las acciones atómicas» → `intercalan`: las fichas se barajan en una sola fila, alternadas
- «que los componen» → `componen`: cada ficha conserva su color: se ve de qué proceso viene

<!-- b2 -->
Imaginemos dos procesos pequeños. El proceso uno tiene dos acciones para
hacer, A uno y A dos, en ese orden. El proceso dos también tiene dos, B
uno y B dos. Cada uno, por separado, respeta perfectamente su propio
orden interno. Pero entre los dos procesos, nadie fijó un orden: podrían
ir alternándose de varias maneras distintas.

- «dos procesos pequeños» → `dos`: dos cajas de proceso, P1 azul y P2 magenta
- «El proceso uno» → `p1`: P1 destella
- «A uno y A dos» → `a`: dentro de P1, las fichas A1 y A2
- «en ese orden» → `orden`: una flecha A1 → A2
- «El proceso dos» → `p2`: P2 destella
- «B uno y B dos» → `b`: dentro de P2, las fichas B1 y B2, con su flecha
- «Cada uno, por separado» → `separado`: P1 y P2 se alejan un poco
- «su propio orden interno» → `interno`: tildes verdes sobre las dos flechas
- «entre los dos procesos» → `entre`: el espacio vacío entre P1 y P2 se ilumina
- «nadie fijó un orden» → `nadie`: entre P1 y P2, un signo de pregunta
- «varias maneras distintas» → `maneras`: las cuatro fichas saltan al centro y se mezclan

<!-- b3 -->
Dibujemos todas esas maneras posibles. A uno, A dos, B uno, B dos. O A
uno, B uno, A dos, B dos. O B uno, A uno, A dos, B dos. Y así. Aquí está
lo que hay que asumir de una buena vez: cada uno de estos caminos es una
ejecución tan válida como cualquier otra. Nada, en la definición de
proceso concurrente, dice que uno de estos caminos es "el correcto" y
los demás no.

- «Dibujemos» → `raiz`: a la izquierda, un punto raíz: empieza el árbol
- «A uno, A dos, B uno, B dos» → `c1`: se dibuja el primer camino, ficha por ficha
- «O A uno, B uno, A dos, B dos» → `c2`: se ramifica el segundo camino
- «O B uno, A uno, A dos, B dos» → `c3`: se ramifica el tercero
- «Y así» → `todos`: el árbol completa sus 6 caminos de golpe; al lado, «6 caminos»
- «de una buena vez» → `asumir`: la cámara se aleja y muestra el árbol entero
- «cada uno de estos caminos» → `cada`: un punto recorre un camino cualquiera, de punta a punta
- «tan válida como cualquier otra» → `validas`: los seis caminos se ponen verdes, uno tras otro
- «Nada, en la definición» → `definicion`: arriba, un documento «definición»
- «uno de estos caminos» → `uno`: el documento lanza una flecha que recorre los caminos buscando uno
- «el correcto» → `correcto`: un rótulo «el correcto» busca un camino donde posarse
- «los demás no» → `demasno`: el rótulo se tacha y cae

---

# Escena 09 — ExplosionEstados

**Capítulo YouTube:** La explosión de estados

## Narración

<!-- b1 -->
De aquí salen dos consecuencias que no tienen vuelta atrás. Primero: el
orden en que se ejecutan las acciones no lo decide el programador, ni es
reproducible de una corrida a la siguiente. Segundo: el mismo par de
procesos, corrido dos veces, puede darnos dos resultados distintos,
simplemente porque esa vez el intercalado fue otro.

- «dos consecuencias» → `dos`: dos tarjetas numeradas, 1 y 2, vacías
- «Primero» → `primero`: la tarjeta 1 se agranda
- «no lo decide el programador» → `programador`: una persona intenta ordenar las fichas; las fichas se barajan solas
- «ni es reproducible» → `reproducible`: tres corridas en fila, cada una con otro orden de fichas
- «a la siguiente» → `siguiente`: las tres corridas destellan, cada una con su orden
- «Segundo» → `segundo`: la tarjeta 2 se agranda
- «corrido dos veces» → `dosveces`: dos corridas del mismo par, una arriba de la otra
- «dos resultados distintos» → `resultados`: a la derecha de cada una, un resultado: «2» y «1»
- «el intercalado fue otro» → `otro`: se resaltan los órdenes distintos de las dos corridas

<!-- b2 -->
Y hay un problema todavía más de fondo, que aparece apenas agrandamos un
poco el ejemplo. Con dos acciones por proceso, ya teníamos varios
caminos posibles. Con tres acciones, o con diez, o con cien, la cantidad
de intercalaciones crece exponencialmente. A esto se le llama explosión
de estados, y es la razón profunda de por qué el testing aquí casi no
sirve: es prácticamente imposible construir a mano el test que dispare,
justo, esa combinación puntual que rompe todo.

- «todavía más de fondo» → `fondo`: corte: vuelve el árbol de 6 caminos, pequeño
- «agrandamos un poco» → `agrandar`: el árbol se estira un poco, como si fuera a crecer
- «Con dos acciones» → `dos`: debajo, «2 + 2 → 6»
- «varios caminos posibles» → `varios`: los 6 caminos destellan
- «Con tres acciones» → `tres`: el árbol crece: «3 + 3 → 20»
- «o con diez» → `diez`: el árbol llena la pantalla: «10 + 10 → 184.756»
- «o con cien» → `cien`: un contador se dispara hasta «100 + 100 → 9 · 10⁵⁸»
- «crece exponencialmente» → `exponencial`: una curva exponencial se dibuja y se sale de cuadro
- «A esto se le llama» → `llama`: la curva tiembla
- «explosión» → `explosion`: el árbol estalla; «explosión de estados» grande, en ámbar
- «el testing» → `testing`: vuelve la lupa de E1
- «casi no» → `nosirve`: la lupa sobre una grilla enorme de puntos: solo alcanza a recorrer un rincón
- «prácticamente imposible» → `imposible`: la lupa tiembla, agotada
- «el test que dispare» → `test`: la lupa busca, saltando
- «esa combinación puntual» → `puntual`: un único punto rojo late, lejísimos de la lupa
- «rompe todo» → `rompe`: destello rojo, con impacto

<!-- b3 -->
Una última precisión que vamos a necesitar pronto: el estado completo de
un sistema concurrente no es solo el valor de sus variables. También
incluye, para cada proceso, en qué instrucción está parado: su contador
de programa. Dos procesos con las mismas variables, pero con el contador
de programa en un lugar distinto, están, en rigor, en estados distintos.

- «Una última precisión» → `precision`: plano limpio; una ficha grande «estado»
- «el estado completo» → `estado`: la ficha destella
- «el valor de sus variables» → `variables`: dentro, «x = 1» e «y = 0»
- «También» → `incluye`: la ficha se estira hacia la derecha
- «en qué instrucción está parado» → `instruccion`: al lado, dos listas de instrucciones (P1 y P2)
- «su contador» → `pc`: una flecha PC en cada lista; en la ficha se suman «PC1 = 2» y «PC2 = 1»
- «Dos procesos con las mismas variables» → `mismas`: la ficha se duplica: dos fichas lado a lado, con las mismas variables
- «pero con el contador» → `contador`: las flechas PC de la segunda ficha se mueven
- «en un lugar distinto» → `distinto`: en la segunda ficha, los PC cambian y se resaltan
- «estados distintos» → `distintos`: un «≠» grande entre las dos fichas

---

# Escena 10 — Cierre

**Capítulo YouTube:** Lo que viene

## Narración

<!-- b1 -->
Resumamos. Con un solo procesador, el sistema intercala procesos tan
rápido que parecen simultáneos. Dentro de un proceso, los hilos comparten
la memoria. Concurrencia es cómo está armado el programa; paralelismo, lo
que pasa en el hardware. Y el orden en que se intercalan las acciones no
lo decide nadie. Ahora juntemos todo: dos hilos, una variable compartida
que vale cero, y cada uno le suma uno. ¿Cuánto vale al final? Dos,
obviamente. ¿O no? En el próximo episodio vamos a ver por qué, a veces,
uno más uno es igual a uno.

- «Resumamos» → `resumen`: cuatro casilleros vacíos en fila
- «Con un solo procesador» → `r1`: el primero: una CPU con tiras intercaladas
- «el sistema intercala» → `intercala`: las tiras empiezan a turnarse
- «parecen simultáneos» → `simult`: las tiras se intercalan tan rápido que parecen una sola
- «Dentro de un proceso» → `dentro`: el segundo casillero destella
- «los hilos comparten» → `r2`: el segundo: la casa con tres hilos y la memoria
- «Concurrencia es» → `r3`: el tercero: «concurrencia ≠ paralelismo»
- «paralelismo, lo que pasa» → `paral`: debajo, cuatro núcleos encendidos
- «el orden en que se intercalan» → `r4`: el cuarto: el árbol de caminos
- «no lo decide nadie» → `nadie`: el árbol se sacude
- «juntemos todo» → `juntar`: los cuatro casilleros se juntan al centro y se funden
- «dos hilos» → `hilos`: dos hilos, H1 azul y H2 magenta
- «una variable compartida» → `variable`: entre ellos, una celda «contador = 0»
- «le suma uno» → `suma`: sobre cada hilo, «+1»
- «¿Cuánto vale al final?» → `cuanto`: un signo de pregunta sobre la celda
- «Dos,» → `dos`: la celda muestra «2», en verde
- «¿O no?» → `ono`: el 2 tiembla y se vuelve un «1» rojo, con alarma
- «próximo episodio» → `proximo`: todo sale; «Episodio 3», con un whoosh
- «uno más uno es igual a uno» → `titulo`: «1 + 1 = 1» grande, en ámbar
