# Episodio 4 — Demostrar que nunca falla

Capítulo 1 (Introducción), episodio 4 de 4 (cierra el capítulo). Duración
objetivo: ~10 min. Texto tomado de `../guion_base.md` (escenas 12 a 14), más un
gancho y un cierre nuevos. Las 3 escenas del guion base se parten en 11 escenas
atómicas, más gancho y cierre. El gancho paga la pregunta con la que cierra E3
("¿cómo se demuestra que un programa concurrente nunca falla?").

Cambios de texto respecto del guion base (aprobados por el usuario):
- Escena 01 (gancho) y escena 13 (cierre): texto nuevo.
- Escena 12, b2: son las dos primeras frases de la escena 14 b7 del guion base;
  el resto de ese bloque ("Con esta idea cerramos la introducción…") pasa, reescrito,
  al cierre.
- Referencias a otros episodios y al formato por episodios:
  - escena 05: "Cerremos comparando" → "Para terminar esta parte, comparemos";
  - escena 06, b1: "Cerremos el capítulo dando un paso atrás" → "Ahora demos un
    paso atrás";
  - escena 07, b2: "la frase con la que arrancamos el capítulo" → "la frase de
    Dijkstra que vimos en el primer episodio";
  - escena 08, b1: "que ya vimos con el interleaving" → "que vimos en el segundo
    episodio con el interleaving";
  - escena 09: "Para cerrar el capítulo, conozcamos" → "Conozcamos";
  - escena 11, b1: "el mismo problema que abrió el capítulo" → "el mismo problema
    del episodio anterior".
- Narración sin siglas: "P1 fuera" → "proceso uno fuera", "P1 en sección
  crítica" → "proceso uno en sección crítica" (igual con el dos).
- Castellano de España: "chico alemán" → "niño alemán"; "ya andaba inventando" →
  "ya se dedicaba a inventar"; "se lo considera" → "se le considera"; "se lo
  llama" → "se le llama"; "ya mismo" → "en ese mismo instante"; "Recién cuando" →
  "Solo cuando"; "adentro" → "dentro"; "apenas envía" → "nada más enviar";
  "Hasta ahora vimos" → "Hasta ahora hemos visto".
- Escena 13 (cierre), aprobado por el usuario: ya no anuncia las Redes de Petri
  como capítulo siguiente (el orden de la materia es Concurrencia → Autómatas →
  Lenguajes y gramáticas → Redes de Petri). Deja abierta la pregunta de los
  "cincuenta lugares" (se paga en el capítulo 4) y anuncia el capítulo 2 con
  el título de su E1, "La máquina que recuerda sin memoria".

Continuidad visual: la red de exclusión mutua del gancho es la misma que se
construye en la escena 11 y se demuestra en la 12. Se reutilizan de E3
`SalaPuerta`, `tarjeta`, `grilla_ejecuciones`, `escudo` y la lluvia de tildes, y
de E1 la frase de Dijkstra con la lupa.

---

# Escena 01 — Gancho

**Capítulo YouTube:** Probar no basta
**Objetivo:** pagar la pregunta del cierre de E3 con la promesa: hoy no se
prueba, se demuestra.

## Narración

<!-- b1 -->
Un millón de pruebas superadas no demuestran nada: basta un solo intercalado
malo para que el programa falle. Pero hay otro camino. Con cinco círculos,
cuatro barras y una sola ficha, hoy vamos a demostrar que dos procesos nunca
entrarán a la vez en su sección crítica.

- «Un millón de pruebas» → `millon`: plano negro; una grilla enorme de tildes verdes se llena en ráfaga, con tecleo
- «no demuestran nada» → `nada`: las tildes se atenúan de golpe
- «un solo intercalado» → `intercalado`: una casilla de la grilla se vuelve roja, con impacto
- «el programa falle» → `falla`: la casilla roja crece al centro y la grilla se va
- «otro camino» → `camino`: corte; bifurcación: «probar» (gris) y «demostrar» (ámbar); se enciende «demostrar»
- «cinco círculos» → `circulos`: corte; cinco lugares aparecen en cadena, con pop
- «cuatro barras» → `barras`: cuatro transiciones entre ellos; los arcos se dibujan solos
- «una sola ficha» → `ficha`: una ficha cae en el lugar central, con tick
- «vamos a demostrar» → `demostrar`: la red dispara una transición; la ficha viaja
- «entrarán a la vez» → `nunca`: los dos lugares de sección crítica se resaltan; entre ellos, «nunca juntos»
- «su sección crítica» → `titulo`: corte; el título del episodio, solo, con un whoosh

---

# Escena 02 — Escalera

**Capítulo YouTube:** Cuánto se conocen los procesos

## Narración

<!-- b1 -->
Hasta ahora hemos visto procesos peleándose por un recurso sin siquiera
conocerse. Pero la relación entre procesos puede ser mucho más cercana
que eso, y hay una escalera de tres escalones que describe cuánto se
conocen entre sí.

- «Hasta ahora» → `hasta`: dos procesos a los lados de un recurso (candado), como en E3
- «peleándose por un recurso» → `pelea`: los dos tiran del recurso; el recurso tiembla
- «conocerse» → `desconocidos`: cada proceso mira hacia fuera, con un «?» encima
- «la relación entre procesos» → `relacion`: una línea punteada une a los dos procesos
- «mucho más cercana» → `cercana`: los dos procesos se acercan entre sí
- «una escalera» → `escalera`: corte; escalera de tres escalones que sube hacia la derecha
- «tres escalones» → `tres`: los escalones se numeran 1, 2 y 3 en cadena
- «conocen entre sí» → `cuanto`: a la derecha, una flecha vertical «cercanía» sube hasta el escalón 3

<!-- b2 -->
El primer escalón es no conocerse en absoluto: ni siquiera saben que el
otro existe, y aun así entran en conflicto porque quieren el mismo
recurso. A esto lo llamamos competencia, y es exactamente el escenario de
exclusión mutua que ya venimos viendo.

- «El primer escalón» → `e1`: el escalón 1 se enciende; la cámara se acerca a él
- «no conocerse en absoluto» → `absoluto`: dos procesos sobre el escalón, de espaldas
- «ni siquiera saben» → `saben`: un «?» sobre cada uno
- «entran en conflicto» → `conflicto`: entre ellos, un recurso; los dos lo agarran y salta un rayo rojo
- «porque quieren el mismo» → `mismo`: el recurso se resalta en rojo y tiembla
- «competencia» → `competencia`: «competencia» entra con pop en ámbar sobre el escalón
- «exclusión mutua» → `exclusion`: al lado, miniatura de la sala con puerta de E3

<!-- b3 -->
El segundo escalón es el conocimiento indirecto: no se conocen por
nombre, pero comparten un objeto, como una variable o un archivo. Es
cooperación por compartimiento, y trae los mismos dolores de cabeza de
exclusión mutua, más uno nuevo: mantener esos datos compartidos
coherentes.

- «El segundo escalón» → `e2`: la cámara sube al escalón 2, que se enciende
- «conocimiento indirecto» → `indirecto`: rótulo «conocimiento indirecto»
- «comparten un objeto» → `objeto`: entre dos procesos que no se miran, una caja; flechas de los dos hacia ella
- «una variable» → `variable`: la caja se vuelve celda «x»
- «un archivo» → `archivo`: la celda se transforma en documento
- «cooperación por compartimiento» → `compartimiento`: «cooperación por compartimiento» entra con pop
- «dolores de cabeza» → `dolores`: vuelve la miniatura de la sala con puerta, que tiembla
- «uno nuevo» → `nuevo`: a su lado, un «+» y un hueco nuevo
- «datos compartidos» → `datos`: en el hueco, el documento con dos valores distintos que se pisan, en rojo
- «coherentes» → `coherentes`: los valores se igualan; tilde verde

<!-- b4 -->
Y el tercer escalón, el más cercano, es el conocimiento directo: los
procesos se hablan explícitamente, dirigiéndose el uno al otro por su
identificador. Es cooperación por comunicación, y el mecanismo que la
hace posible es el pasaje de mensajes.

- «el tercer escalón» → `e3`: la cámara sube al escalón 3, que se enciende
- «conocimiento directo» → `directo`: rótulo «conocimiento directo»
- «se hablan explícitamente» → `hablan`: dos procesos se giran y se miran; burbuja de diálogo entre ellos
- «identificador» → `id`: los procesos se rotulan «P1» y «P2»; la burbuja dice «→ P2»
- «cooperación por comunicación» → `comunicacion`: «cooperación por comunicación» entra con pop
- «el mecanismo» → `mecanismo`: un engranaje aparece entre los dos procesos
- «pasaje de mensajes» → `mensajes`: un sobre viaja de P1 a P2, con whoosh; «pasaje de mensajes» en ámbar

---

# Escena 03 — PasajeMensajes

**Capítulo YouTube:** Pasaje de mensajes

## Narración

<!-- b1 -->
El pasaje de mensajes trae su propia sincronización de fábrica: nadie
puede recibir un mensaje antes de que alguien lo haya enviado. Lo que
falta definir es qué le pasa a cada uno después de su operación. Enviar
puede bloquear al emisor hasta que el mensaje efectivamente llegue, o
puede dejarlo seguir de inmediato. Recibir puede bloquear al receptor
hasta que llegue algo, o puede dejarlo continuar con las manos vacías.

- «El pasaje de mensajes» → `canal`: corte; emisor a la izquierda, receptor a la derecha, un canal entre ellos
- «sincronización de fábrica» → `fabrica`: el canal se resalta en ámbar
- «recibir un mensaje» → `recibir`: el receptor extiende la mano; no llega nada
- «lo haya enviado» → `enviado`: el emisor manda el sobre y solo entonces llega al receptor
- «qué le pasa a cada uno» → `cada`: corte; los dos procesos de nuevo, con un «?» debajo de cada uno
- «Enviar» → `enviar`: foco en el emisor: rótulo «enviar»
- «bloquear al emisor» → `bloquea`: el emisor suelta el sobre y se congela en gris, con un candado
- «efectivamente llegue» → `llega`: el sobre llega al receptor; el candado del emisor se abre
- «seguir de inmediato» → `sigue`: otra vez: el emisor suelta el sobre y sigue avanzando
- «Recibir puede bloquear» → `recibe`: foco en el receptor: rótulo «recibir»
- «hasta que llegue algo» → `espera`: el receptor congelado con un reloj que gira; llega el sobre y se descongela
- «las manos vacías» → `vacias`: otra vez: el receptor sigue sin sobre, con «∅» en la mano

---

# Escena 04 — Rendezvous

**Capítulo YouTube:** Rendezvous

## Narración

<!-- b1 -->
Cuando las dos puntas son bloqueantes, pasa algo especial: emisor y
receptor quedan sincronizados en el mismo instante exacto, esperándose
mutuamente hasta que el mensaje pasa de uno a otro. Eso tiene nombre
propio, rendezvous, y es la forma más fuerte de sincronización entre dos
partes. En la práctica, la combinación que más se usa es otra: emisor no
bloqueante, que sigue trabajando nada más enviar, y receptor bloqueante, que
espera lo que necesita antes de seguir.

- «las dos puntas» → `puntas`: tabla 2×2 (enviar bloquea / no bloquea × recibir bloquea / no bloquea); se enciende la casilla bloquea-bloquea
- «pasa algo especial» → `especial`: corte; emisor y receptor frente a frente, los dos con candado
- «quedan sincronizados» → `sincronizados`: los dos se iluminan a la vez
- «el mismo instante exacto» → `instante`: un reloj entre los dos; las agujas se alinean, con tick
- «mutuamente» → `esperan`: flechas punteadas de espera de uno al otro, formando un ciclo
- «pasa de uno a otro» → `pasa`: el sobre cruza; los dos candados se abren a la vez, con impacto
- «rendezvous» → `rendezvous`: «rendezvous» entra con pop en ámbar
- «la forma más fuerte» → `fuerte`: una línea gruesa ámbar une a los dos procesos
- «de sincronización entre dos» → `maxima`: al lado, un medidor «sincronización» sube al máximo
- «En la práctica» → `practica`: corte; vuelve la tabla 2×2
- «que más se usa» → `usa`: se enciende la casilla no bloquea-bloquea, con una estrella
- «sigue trabajando» → `trabajando`: el emisor suelta el sobre y sigue, con un engranaje girando
- «receptor bloqueante» → `receptor`: el receptor espera con un reloj que gira
- «antes de seguir» → `seguir`: el sobre llega; el receptor se desbloquea y avanza

---

# Escena 05 — MemoriaOMensajes

**Capítulo YouTube:** Memoria compartida o mensajes

## Narración

<!-- b1 -->
Para terminar esta parte, comparemos los dos grandes caminos que tenemos
para que los procesos interactúen. Con memoria compartida, todos leen y
escriben las mismas variables, y lo que hay que proteger, con
sincronización explícita, es la sección crítica sobre ese dato. Con pasaje
de mensajes no hay ningún dato compartido que cuidar: la sincronización ya
viene incluida en el propio acto de enviar y recibir.

- «comparemos» → `compara`: corte; la pantalla se parte en dos columnas
- «dos grandes caminos» → `caminos`: títulos «memoria compartida» (izquierda) y «mensajes» (derecha)
- «los procesos interactúen» → `interactuan`: bajo cada título entran tres procesos
- «Con memoria compartida» → `memoria`: foco a la izquierda: aparece una celda entre sus procesos
- «las mismas variables» → `variables`: flechas de los tres a la celda, encendiéndose en ráfaga
- «lo que hay que proteger» → `proteger`: la celda recibe un candado
- «sincronización explícita» → `explicita`: debajo, «sincronización explícita»
- «la sección crítica» → `seccion`: marco ámbar alrededor de la celda y su candado
- «Con pasaje» → `pasaje`: foco a la derecha; la izquierda se atenúa
- «ningún dato compartido» → `ningun`: entre los procesos de la derecha, una celda fantasma aparece y se tacha
- «viene incluida» → `incluida`: el sobre empieza a viajar
- «enviar y recibir» → `enviar`: el sobre llega; «sincronización incluida» con tilde verde

---

# Escena 06 — SistemasReactivos

**Capítulo YouTube:** Sistemas que nunca terminan

## Narración

<!-- b1 -->
Ahora demos un paso atrás, para entender por qué toda la materia que sigue
está armada alrededor de "modelar", en vez de "programar y cruzar los
dedos con el testing".

- «un paso atrás» → `atras`: la cámara se aleja de la comparación, que se encoge y se va
- «toda la materia» → `materia`: una fila de tarjetas de capítulos
- «modelar» → `modelar`: «modelar» entra con pop en ámbar sobre las tarjetas
- «programar y cruzar los» → `cruzar`: al lado, «programar y probar», con tildes que tiemblan
- «el testing» → `testing`: «programar y probar» se tacha

<!-- b2 -->
Primero, una distinción de qué tipo de sistema estamos construyendo. Un
sistema transformacional recibe una entrada, calcula una salida, y
termina ahí: un compilador, un cálculo científico. La corrección, en ese
mundo, es una relación clara entre entrada y salida.

- «qué tipo de sistema» → `tipo`: corte; dos columnas vacías, cada una con «?»
- «sistema transformacional» → `transf`: la columna izquierda recibe el título «transformacional»
- «recibe una entrada» → `entrada`: una flecha entra en una caja
- «calcula una salida» → `salida`: un engranaje gira en la caja y sale una flecha
- «termina ahí» → `termina`: la caja se apaga, con una bandera de fin
- «un compilador» → `compilador`: debajo, una ventana de código
- «un cálculo científico» → `cientifico`: al lado, una fórmula «∑»
- «La corrección» → `correccion`: debajo, «corrección»
- «una relación clara» → `clara`: una flecha une la entrada con la salida
- «entrada y salida» → `relacion`: la flecha se pone verde, con tilde

<!-- b3 -->
Un sistema reactivo es otra historia completamente distinta: interactúa
todo el tiempo con su entorno y con otros procesos, casi nunca calcula un
resultado final, y ni siquiera se espera que termine, como un sistema
operativo o un cajero automático. La programación concurrente vive, casi
siempre, del lado reactivo, y eso cambia la pregunta de fondo: ya no basta
con mirar un resultado final, hay que poder razonar sobre secuencias
infinitas de interacciones.

- «Un sistema reactivo» → `reactivo`: la columna derecha recibe el título «reactivo»
- «interactúa» → `interactua`: una caja con flechas que entran y salen sin parar
- «su entorno» → `entorno`: alrededor aparecen personas y otros procesos
- «resultado final» → `final`: una bandera de fin aparece y se tacha
- «se espera que termine» → `termine`: sobre la caja, un «∞» que gira
- «un cajero automático» → `cajero`: debajo, un chip «SO» y un cajero automático
- «La programación concurrente» → `concurrente`: «programación concurrente» entra entre las dos columnas
- «del lado reactivo» → `lado`: la columna reactiva se resalta; la otra se atenúa
- «la pregunta de fondo» → `pregunta`: corte; «?» grande
- «mirar un resultado final» → `mirar`: una lupa sobre una caja «resultado», que se tacha
- «infinitas de interacciones» → `infinitas`: una cinta de eventos que avanza sin fin

---

# Escena 07 — Verificacion

**Capítulo YouTube:** Verificar en vez de probar

## Narración

<!-- b1 -->
Sobre esas interacciones se verifican dos familias de propiedades. Las de
seguridad, que garantizan que nada malo ocurra nunca, como "nunca hay dos
procesos en la sección crítica a la vez". Y las de progreso, que
garantizan que algo bueno termine pasando, como "todo proceso que pide
un recurso, tarde o temprano lo consigue".

- «dos familias» → `familias`: corte; dos huecos lado a lado
- «seguridad» → `seguridad`: a la izquierda, el escudo verde de E3: «seguridad»
- «nada malo ocurra nunca» → `nadamalo`: el escudo destella
- «nunca hay dos» → `ejemplo1`: bajo el escudo, la sala con puerta: un proceso dentro, otro llega a la puerta
- «a la vez» → `alavez`: el de fuera rebota, con una cruz roja
- «de progreso» → `progreso`: a la derecha, la flecha de E3: «progreso»
- «algo bueno termine pasando» → `bueno`: la flecha avanza
- «todo proceso que pide» → `pide`: bajo la flecha, un proceso en fila levanta la mano
- «tarde o temprano lo consigue» → `consigue`: un reloj gira sobre él; llega su turno y entra

<!-- b2 -->
Y volvamos a la frase de Dijkstra que vimos en el primer episodio: el
testing solo puede mostrar la presencia de errores, nunca su ausencia. La
alternativa es la verificación: en vez de probar casos sueltos, recorrer
de manera exhaustiva todas las secuencias posibles de eventos, incluidas
las que ningún test se le hubiera ocurrido generar. Y eso solo es posible
si antes construimos un modelo: una representación abstracta del
programa, que se queda con lo que necesitamos analizar de la concurrencia
y descarta el resto, como la representación interna de los datos o la
interfaz con el usuario.

- «la frase de Dijkstra» → `dijkstra`: corte; vuelve el nombre de Dijkstra, como en E1
- «el primer episodio» → `ep1`: en la esquina, «Episodio 1»
- «la presencia de errores» → `presencia`: primera mitad de la frase; la lupa encuentra un punto rojo
- «nunca su ausencia» → `ausencia`: segunda mitad; la lupa pasa por una zona vacía con «?»
- «la verificación» → `verificacion`: corte; «verificación» entra con pop en ámbar
- «casos sueltos» → `sueltos`: un árbol de ejecuciones grande; se encienden solo unos pocos caminos
- «de manera exhaustiva» → `exhaustiva`: todas las ramas se encienden en un barrido
- «incluidas» → `incluidas`: las ramas raras del borde destellan en ámbar
- «ningún test» → `ningun`: sobre ellas, «ningún test» y una cruz pequeña
- «solo es posible» → `posible`: el árbol entero se encoge a un rincón; en el centro, «¿cómo?»
- «un modelo» → `modelo`: corte; programa (ventana de código) a la izquierda, flecha hacia un esquema a la derecha
- «una representación abstracta» → `abstracta`: el esquema se dibuja: pocos nodos y flechas
- «lo que necesitamos analizar» → `analizar`: en el esquema se resaltan los hilos y el candado
- «descarta el resto» → `descarta`: el programa empieza a desarmarse
- «la representación interna» → `datos`: cae una pieza «datos»
- «interfaz con el usuario» → `interfaz`: cae la última pieza (una ventana); queda el esquema limpio

---

# Escena 08 — ExplosionModelo

**Capítulo YouTube:** Cuando el modelo explota

## Narración

<!-- b1 -->
El primer formalismo natural para esto son las máquinas de estados
finitos: son intuitivas, se dibujan fácil, y ya las conoce cualquiera que
haya diseñado software orientado a objetos. Pero tienen un límite serio.
Si tenemos dos procesos, cada uno con diez estados posibles, el sistema
completo puede llegar a tener cien estados, porque hay que considerar
todas las combinaciones entre ambos. Con tres procesos de diez estados,
mil. El dibujo se vuelve inmanejable en un abrir y cerrar de ojos: es la
misma explosión de estados que vimos en el segundo episodio con el
interleaving, pero ahora en el propio modelo.

- «máquinas de estados» → `fsm`: corte; una máquina de estados de tres círculos con flechas
- «son intuitivas» → `intuitivas`: un punto recorre los estados
- «se dibujan fácil» → `facil`: tilde verde al lado
- «cualquiera que» → `cualquiera`: junto a la máquina aparece una persona
- «orientado a objetos» → `objetos`: al lado, una ventana de diseño con el mismo diagrama
- «un límite serio» → `limite`: todo se vuelve ámbar, con tick
- «dos procesos» → `dos`: corte; dos anillos de estados, azul y magenta
- «diez estados posibles» → `diez`: bajo cada anillo, «10 estados»
- «cien estados» → `cien`: entre ellos, una grilla de 10×10 puntos se llena en ráfaga: «100»
- «todas las combinaciones» → `combinaciones`: se resaltan una fila, una columna y su cruce
- «tres procesos» → `tres`: aparece un tercer anillo, cian
- «mil» → `mil`: la grilla se vuelve un cubo de puntos: «1.000», con impacto
- «inmanejable» → `inmanejable`: el grafo crece hasta salirse de cuadro
- «explosión de estados» → `explosion`: «explosión de estados» en ámbar, como en E2
- «el segundo episodio» → `ep2`: miniatura del árbol de intercalados de E2
- «en el propio modelo» → `modelo`: el rótulo «modelo» se estampa sobre el caos

<!-- b2 -->
Por eso la materia recurre a otro formalismo, mucho más compacto, capaz
de representar la concurrencia sin dibujar cada combinación de estados
por separado: las Redes de Petri. Y ahí es exactamente donde vamos
ahora.

- «otro formalismo» → `otro`: corte; el caos se encoge a la izquierda
- «mucho más compacto» → `compacto`: a la derecha, el mismo sistema como red de Petri: dos ciclos de 10 lugares
- «sin dibujar cada combinación» → `sin`: debajo, «100 estados» (izquierda) contra «20 lugares» (derecha)
- «las Redes de Petri» → `petri`: «Redes de Petri» entra con pop en ámbar
- «donde vamos» → `vamos`: la cámara se acerca a la red

---

# Escena 09 — CarlAdamPetri

**Capítulo YouTube:** Carl Adam Petri

## Narración

<!-- b1 -->
Conozcamos al autor de la herramienta que nos va a acompañar buena parte
del curso. Carl Adam Petri era un niño alemán que, a los trece años, ya se
dedicaba a inventar diagramas y reglas para describir procesos químicos.
Décadas después, en 1962, esas ideas de la infancia se convirtieron en su
tesis doctoral, y ahí nacieron formalmente las Redes de Petri. Hoy se le
considera, junto con Dijkstra, uno de los fundadores de la teoría de la
concurrencia.

- «Conozcamos al autor» → `autor`: corte; una silueta de persona con «?»
- «la herramienta» → `herramienta`: al lado, la red pequeña de la escena anterior
- «acompañar» → `acompanar`: debajo de la red, una fila de tarjetas de capítulos se enciende
- «Carl Adam Petri» → `nombre`: el nombre, en ámbar, sobre la silueta
- «un niño alemán» → `nino`: la silueta se achica: es un niño
- «a los trece años» → `trece`: «13 años» entra con pop
- «diagramas y reglas» → `diagramas`: burbuja de pensamiento con un diagrama pequeño
- «procesos químicos» → `quimicos`: en la burbuja aparece un matraz
- «Décadas después» → `decadas`: abajo se dibuja la línea de tiempo
- «en 1962» → `anio`: marca 1962; el niño vuelve a ser adulto
- «ideas de la infancia» → `infancia`: la burbuja del niño viaja por la línea de tiempo hasta 1962
- «tesis doctoral» → `tesis`: la burbuja se convierte en un documento «tesis»
- «nacieron formalmente» → `nacen`: del documento sale la red de Petri
- «Hoy se le» → `hoy`: la línea de tiempo avanza hasta «hoy»
- «junto con Dijkstra» → `dijkstra`: aparece el nombre de Dijkstra al lado del de Petri
- «uno de los fundadores» → `fundadores`: los dos nombres quedan sobre una base: «concurrencia»

---

# Escena 10 — PiezasYDisparo

**Capítulo YouTube:** Lugares, transiciones y fichas

## Narración

<!-- b1 -->
Una Red de Petri se arma con muy pocas piezas. Los lugares, dibujados
como círculos, representan una condición, como "el recurso está libre".
Las transiciones, dibujadas como barras, representan un evento, como "un
proceso toma el recurso". Los arcos conectan lugares con transiciones, o
transiciones con lugares, marcando qué necesita cada evento y qué
produce. Y las fichas, esos puntitos que viven dentro de los lugares,
indican que esa condición se cumple ahora mismo. Al conjunto completo de
fichas en la red se le llama marcado: es, literalmente, la foto del
estado del sistema en este instante.

- «muy pocas piezas» → `piezas`: corte; cuatro huecos vacíos en fila
- «Los lugares» → `lugar`: un lugar grande entra con pop en el primer hueco
- «una condición» → `condicion`: debajo, «condición»
- «el recurso está libre» → `libre`: el lugar se rotula «recurso libre»
- «Las transiciones» → `transicion`: una transición entra con pop en el segundo hueco
- «un evento» → `evento`: debajo, «evento»
- «toma el recurso» → `toma`: la transición se rotula «tomar recurso»
- «Los arcos» → `arcos`: un arco se dibuja del lugar a la transición
- «transiciones con lugares» → `vuelta`: otro arco sale de la transición hacia un lugar nuevo
- «qué necesita» → `necesita`: el arco de entrada se resalta
- «produce» → `produce`: el arco de salida se resalta
- «las fichas» → `fichas`: una ficha cae en el primer lugar
- «viven dentro de los lugares» → `viven`: la ficha rebota dentro del círculo y se asienta
- «se cumple» → `cumple`: el rótulo «recurso libre» se pone verde
- «ahora mismo» → `ahora`: la ficha brilla
- «Al conjunto completo» → `conjunto`: corte; una red más grande, con fichas en varios lugares
- «marcado» → `marcado`: «marcado» entra con pop
- «la foto» → `foto`: un marco de foto con flash encuadra la red

<!-- b2 -->
Toda la dinámica de la red se resume en una sola regla, la regla de
disparo. Una transición está habilitada cuando cada uno de sus lugares
de entrada tiene, al menos, una ficha. Al dispararse, consume esas
fichas de entrada, y produce fichas nuevas en los lugares de salida. El
disparo es instantáneo y atómico. Y ojo con este detalle: que una
transición esté habilitada no obliga a que se dispare en ese mismo
instante. Justo ahí vive el no determinismo que necesitamos para modelar
concurrencia de verdad.

- «una sola regla» → `regla`: corte; lugar → transición → lugar, con una ficha en la entrada
- «disparo» → `rotulo`: arriba, «regla de disparo» entra con pop
- «está habilitada» → `habilitada`: la transición se enciende en verde
- «al menos, una ficha» → `almenos`: la ficha de entrada destella
- «consume esas» → `consume`: disparo: la ficha entra en la transición
- «produce fichas nuevas» → `nuevas`: una ficha nueva sale de la transición por el arco
- «los lugares de salida» → `salidas`: la ficha aterriza en el lugar de salida, con pop
- «instantáneo y atómico» → `atomico`: flash; «atómico» entra con pop
- «ojo con este detalle» → `ojo`: corte; la red de nuevo, con la transición habilitada
- «no obliga» → `noobliga`: la transición sigue verde pero no dispara; un reloj hace tick
- «Justo ahí» → `justo`: la transición late, en espera
- «el no determinismo» → `nodet`: dos transiciones habilitadas comparten una ficha; un «?» salta entre ellas
- «concurrencia de verdad» → `verdad`: cae el dado de E3 y una de las dos dispara

---

# Escena 11 — ExclusionConPetri

**Capítulo YouTube:** Exclusión mutua con una Red de Petri

## Narración

<!-- b1 -->
Usemos esto para resolver, con una Red de Petri, el mismo problema del
episodio anterior: la exclusión mutua. Tenemos dos procesos. El proceso
uno puede estar fuera de la sección crítica, o dentro. Lo mismo el
proceso dos. Y agregamos un quinto lugar, mutex, que arranca con una
sola ficha: es el permiso para entrar, y solo hay uno.

- «el mismo problema» → `problema`: miniatura de la sala con puerta de E3
- «la exclusión mutua» → `exclusion`: «exclusión mutua» debajo
- «Tenemos dos procesos» → `dos`: corte; dos columnas: P1 (azul) y P2 (magenta)
- «uno puede estar» → `p1`: la columna de P1 se resalta
- «fuera de la sección crítica» → `fuera`: en la columna de P1, el lugar «P1 fuera», con una ficha
- «o dentro» → `dentro`: debajo, el lugar «P1 en SC», vacío, unido por las transiciones «entrar» y «salir»
- «Lo mismo el» → `mismo`: la columna de P2 aparece igual, en espejo
- «un quinto lugar» → `quinto`: el lugar «mutex» al centro, con arcos a las dos columnas
- «sola ficha» → `ficha`: una ficha cae en mutex
- «solo hay uno» → `uno`: la ficha de mutex brilla; «1» encima

<!-- b2 -->
Cuando el proceso uno quiere entrar, su transición necesita dos fichas
para dispararse: la de "proceso uno fuera", y la del mutex. Al dispararse,
las consume las dos, y deja una ficha nueva en "proceso uno en sección
crítica". Mientras esa ficha del mutex no vuelva, la transición de entrada
del proceso dos se queda sin su segunda ficha: está en conflicto, y no
puede dispararse. Solo cuando el proceso uno sale y devuelve la ficha al
mutex, el proceso dos tiene, si quiere, su oportunidad de entrar.

- «el proceso uno quiere entrar» → `quiere`: la transición «entrar» de P1 se resalta
- «necesita dos fichas» → `dosfichas`: sus dos arcos de entrada (desde «P1 fuera» y desde mutex) se resaltan
- «proceso uno fuera» → `f1`: la ficha de «P1 fuera» destella
- «la del mutex» → `fm`: la ficha de mutex destella
- «las consume las dos» → `consume`: disparo: las dos fichas entran en la transición
- «una ficha nueva» → `nueva`: aparece una ficha en «P1 en SC», con pop
- «proceso uno en sección» → `p1sc`: el lugar «P1 en SC» se resalta; P1 está dentro
- «Mientras esa ficha del mutex» → `mientras`: foco en mutex, ahora vacío
- «no vuelva» → `novuelve`: mutex parpadea; a su lado, un reloj pequeño
- «la transición de entrada» → `t2`: la transición «entrar» de P2 se resalta
- «segunda ficha» → `segunda`: su arco desde mutex se pone rojo y punteado
- «está en conflicto» → `conflicto`: la transición se pone roja, con una cruz
- «puede dispararse» → `nopuede`: la transición intenta disparar y rebota
- «sale y devuelve» → `sale`: disparo de «salir» de P1: una ficha vuelve a «P1 fuera» y otra a mutex
- «el proceso dos tiene» → `tiene`: el foco pasa a la columna de P2; la roja de su arco se apaga
- «su oportunidad de entrar» → `oportunidad`: la transición «entrar» de P2 se pone verde

---

# Escena 12 — Invariante

**Capítulo YouTube:** La demostración: un invariante

## Narración

<!-- b1 -->
Y aquí llegamos al momento que justifica todo el capítulo. Sobre esta red
se puede demostrar, con matemática, un invariante: una cantidad que
nunca cambia, disparo tras disparo. En este caso, la suma de fichas en
"proceso uno en sección crítica", más "proceso dos en sección crítica", más
el lugar mutex, es siempre igual a uno. Y de ahí se deduce, directo, que la
suma de los dos primeros nunca puede superar uno: nunca, bajo ningún
interleaving posible, los dos procesos van a estar en su sección
crítica al mismo tiempo.

- «justifica todo el capítulo» → `justifica`: la red completa al centro; un foco se enciende sobre ella
- «se puede demostrar» → `demostrar`: «demostrar» entra con pop en ámbar sobre la red
- «con matemática» → `matematica`: la red se corre abajo; arriba queda un espacio para una ecuación
- «un invariante» → `invariante`: «invariante» entra con pop en ámbar
- «nunca cambia» → `nunca`: «invariante» se fija con un candado pequeño
- «disparo tras disparo» → `disparos`: la red dispara dos transiciones seguidas
- «la suma de fichas» → `suma`: «invariante» sube; empieza la ecuación
- «proceso uno en sección crítica» → `p1sc`: «P1 en SC» en la ecuación; ese lugar se resalta
- «proceso dos en sección crítica» → `p2sc`: « + P2 en SC»; ese lugar se resalta
- «el lugar mutex» → `mutex`: « + mutex»; mutex se resalta
- «siempre igual a uno» → `igual`: « = 1» en verde, con impacto; la ecuación queda fija arriba
- «se deduce» → `deduce`: debajo, «P1 en SC + P2 en SC ≤ 1»
- «nunca puede superar uno» → `superar`: el «≤ 1» se resalta
- «interleaving posible» → `interleaving`: la red dispara en ráfaga; con cada disparo, la ecuación destella y sigue en 1
- «los dos procesos van» → `ambos`: los dos lugares de SC se resaltan: nunca tienen ficha a la vez
- «al mismo tiempo» → `mismo`: tilde verde grande entre los dos

<!-- b2 -->
Esto ya no es testing. Es verificación: una propiedad que vale para
absolutamente todas las ejecuciones posibles de la red, demostrada una
sola vez con matemática, en vez de mil veces con casos de prueba
sueltos.

- «ya no es testing» → `notesting`: corte; la lluvia de tildes de E3, que se tacha de golpe
- «Es verificación» → `verificacion`: «verificación» gigante en ámbar
- «todas las ejecuciones posibles» → `todas`: el árbol de ejecuciones entero se enciende en verde
- «demostrada una» → `demostrada`: sobre el árbol, «1 vez»
- «con matemática» → `mate`: un solo sello «demostrado» cae sobre el árbol, con impacto
- «mil veces» → `mil`: al lado, una pila de casos de prueba
- «sueltos» → `sueltos`: los casos se desparraman y se atenúan

---

# Escena 13 — Cierre

**Capítulo YouTube:** Lo que viene

## Narración

<!-- b1 -->
Resumamos. Los procesos pueden competir sin conocerse, compartir datos o
hablarse con mensajes. Pero, sea cual sea la forma, probar no basta: hay
que modelar y verificar. Y con cinco lugares y cuatro transiciones hemos
demostrado algo que ningún test podría garantizar. Con esto cerramos la
introducción. Pero, ¿qué pasa cuando la red no tiene cinco lugares, sino
cincuenta? Llegaremos ahí. Pero antes, en el próximo capítulo, volvemos a
la pieza más sencilla de todas: la máquina de estados. Porque para
entender por qué explota, primero hay que entender cómo piensa. Empezamos
con la máquina que recuerda sin memoria.

- «Resumamos.» → `resumen`: corte limpio
- «competir sin conocerse» → `competir`: miniatura de la escalera; se enciende el escalón 1
- «compartir datos» → `compartir`: se enciende el escalón 2
- «hablarse con mensajes» → `hablar`: se enciende el escalón 3, con el sobre
- «sea cual sea la forma» → `forma`: los tres escalones se juntan en un grupo
- «probar no basta» → `probar`: la escalera se va; una lluvia de tildes se tacha
- «modelar y verificar» → `modelar`: «modelar» y «verificar» entran con pop en ámbar
- «cinco lugares y cuatro transiciones» → `red`: la red de exclusión mutua, con su ecuación arriba
- «ningún test» → `test`: el sello «demostrado» cae sobre la red
- «Con esto cerramos» → `cerramos`: todo se aleja y se encoge
- «introducción» → `intro`: «Capítulo 1: Introducción» con tilde verde
- «no tiene cinco lugares» → `cinco`: corte; la red de cinco lugares al centro
- «cincuenta» → `cincuenta`: la red se multiplica en diez; la cámara se aleja: «50 lugares»
- «Llegaremos ahí.» → `llegaremos`: la red gigante se encoge a un punto rotulado «capítulo 4»
- «el próximo capítulo» → `proximo`: corte; «Capítulo 2» entra con pop
- «la pieza más sencilla» → `pieza`: el rótulo sube; aparece un solo estado (un círculo)
- «la máquina de estados» → `fsm`: se completa una máquina de tres estados
- «por qué explota» → `explota`: destello de la grilla de 100 puntos de la escena 08, y vuelve
- «cómo piensa» → `piensa`: un punto recorre la máquina
- «la máquina que recuerda sin memoria» → `titulo`: el título del capítulo 2, episodio 1, solo, con un whoosh
