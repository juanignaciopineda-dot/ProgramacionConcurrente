# Episodio 3 — Uno más uno igual a uno

Capítulo 1 (Introducción), episodio 3 de 4. Duración objetivo: ~10 min.
Texto tomado de `../guion_base.md` (escenas 09 a 11), más un gancho, un cierre
y un bloque nuevo. Las 3 escenas del guion base se parten en 9 escenas
atómicas, más gancho y cierre. El gancho paga la pregunta con la que cierra E2
("dos hilos, una variable que vale cero, cada uno le suma uno").

Cambios de texto respecto del guion base (para revisar):
- Escena 01 (gancho) y escena 11 (cierre): texto nuevo.
- Escena 04, b2: bloque nuevo, el intercalado "bueno" que sí da dos (el guion
  base lo pedía en el visual pero no tenía narración).
- Referencias a otros episodios: "Recordemos de la escena de la CPU" →
  "Recordemos, del primer episodio,"; "la propia definición de interleaving que
  acabamos de ver" → "que vimos en el episodio anterior"; "Eso es exclusión
  mutua" → "Eso es, justamente, la exclusión mutua" (se presentó en E2);
  "La condición de carrera de la escena anterior" → "La condición de carrera
  que acabamos de ver".
- Castellano de España: "alcanza con" → "basta con", "se lo llama" → "se le
  llama", "qué tan rápido corre" → "lo rápido que corre", "adentro" →
  "dentro", "afuera" → "fuera", "andan casi siempre" → "funcionan casi
  siempre", "trabados" → "atascados", "se confunden fácil" → "se confunden
  fácilmente", "pasillo angosto" → "pasillo estrecho", "nos corremos para el
  mismo lado" → "nos apartamos hacia el mismo lado".

---

# Escena 01 — Gancho

**Capítulo YouTube:** Uno más uno igual a uno
**Objetivo:** pagar la pregunta del cierre de E2 con la paradoja: un programa
sin ningún error que, a veces, suma mal.

## Narración

<!-- b1 -->
Uno más uno, igual a uno. No es un chiste ni un error de cálculo: es lo que
devuelve, a veces, un programa en el que nadie cometió ningún error. Dos
hilos, una suma, y la mala suerte del orden. Hoy lo vamos a ver paso a paso.

- «Uno más uno» → `suma`: plano negro; aparece gigante «1 + 1» al centro
- «igual a uno» → `igual`: se completa «= 1» en rojo, con impacto
- «No es un chiste» → `chiste`: la ecuación tiembla
- «error de cálculo» → `calculo`: la ecuación se encoge y sube arriba
- «un programa» → `programa`: entra una ventana de código con `contador += 1`
- «ningún error» → `sinerror`: tilde verde grande junto a la ventana
- «Dos hilos» → `hilos`: de la ventana salen dos hilos, H1 (azul) y H2 (magenta)
- «una suma» → `unasuma`: sobre cada hilo aparece «+1»
- «la mala suerte del orden» → `orden`: los dos «+1» se barajan entre sí; cae un dado que rueda
- «paso a paso» → `titulo`: corte; el título del episodio, solo, con un whoosh

---

# Escena 02 — Contador

**Capítulo YouTube:** Dos hilos, un contador

## Narración

<!-- b1 -->
Bajemos todo lo que vimos hasta ahora a un ejemplo muy concreto, uno de
los más citados de toda la materia, porque cuando se entiende este, se
entiende el resto del curso. Tenemos una variable compartida, contador,
que arranca en cero. Dos hilos quieren, cada uno, sumarle uno. Si todo
sale bien, contador debería terminar en dos. Veamos si sale bien.

- «Bajemos todo» → `bajar`: miniaturas de lo visto (CPU, proceso, árbol de intercalados) caen y se funden en un punto
- «un ejemplo muy concreto» → `concreto`: el punto se abre en una celda de memoria vacía
- «más citados» → `citados`: alrededor aparecen varios documentos con flechas hacia la celda
- «cuando se entiende este» → `este`: la celda se ilumina y los documentos se atenúan
- «el resto del curso» → `curso`: los documentos se ordenan en fila y se encienden en cadena
- «una variable compartida» → `variable`: corte; celda grande «contador» al centro
- «que arranca en cero» → `cero`: el 0 entra con pop
- «Dos hilos» → `doshilos`: H1 a la izquierda y H2 a la derecha, cada uno con su registro vacío
- «sumarle uno» → `sumarle`: sobre cada hilo aparece «+1»
- «Si todo sale bien» → `bien`: los dos «+1» avanzan un poco hacia la celda
- «terminar en dos» → `dos`: junto a la celda aparece un «2» translúcido en verde
- «Veamos si sale bien.» → `veamos`: la cámara se acerca un poco, lento

<!-- b2 -->
Recordemos, del primer episodio, que esa suma, en realidad, son tres
pasos separados: leer, incrementar, escribir. Vamos a intercalar esos
pasos de la peor manera posible, y vamos a ver que nada raro tiene que
pasar para que se rompa: basta con la mala suerte del orden.

- «del primer episodio» → `ep1`: vuelve la tarjeta «1 línea = 3 pasos» de E1, arriba
- «tres pasos separados» → `tres`: bajo cada hilo, el «+1» se despliega en tres casillas
- «leer» → `leer`: LEER se enciende en los dos hilos
- «incrementar» → `incrementar`: SUMAR se enciende
- «escribir» → `escribir`: ESCRIBIR se enciende
- «intercalar esos pasos» → `intercalar`: las seis casillas bajan y se mezclan en una fila abajo
- «la peor manera posible» → `peor`: el borde de la fila se pone rojo, con alarma corta
- «nada raro» → `nada`: cada hilo recibe una tilde verde (su código está bien)
- «la mala suerte del orden» → `suerte`: el dado del gancho cae y rueda sobre la fila

---

# Escena 03 — PeorIntercalado

**Capítulo YouTube:** El peor orden posible

## Narración

<!-- b1 -->
El hilo uno lee contador: es cero. Pero justo antes de que llegue a
escribir su resultado, el sistema operativo le da el turno al hilo dos.
El hilo dos también lee contador, y como el hilo uno todavía no escribió
nada, lee exactamente lo mismo: cero.

- «El hilo uno lee» → `h1lee`: H1 se enciende; flecha de la celda a su registro
- «es cero» → `h1cero`: el 0 viaja al registro de H1; abajo, en la línea de pasos, «H1 lee 0»
- «justo antes» → `antes`: tick de reloj; todo se congela un instante
- «el sistema operativo» → `so`: arriba aparece el chip «SO»
- «le da el turno al hilo dos» → `turno`: un foco pasa de H1 a H2; H1 se atenúa
- «también lee contador» → `h2lee`: flecha de la celda al registro de H2
- «todavía no escribió nada» → `nada`: la celda, todavía en 0, destella
- «lo mismo: cero» → `h2cero`: el 0 viaja a H2; en la línea de pasos, «H2 lee 0»

<!-- b2 -->
Ahora los dos hilos tienen, cada uno guardado en su propio registro, el
mismo valor: cero. El hilo uno lo incrementa a uno, y lo escribe:
contador pasa a valer uno. El hilo dos, que sigue teniendo cero en su
registro porque lo leyó antes de que nadie escribiera nada, también lo
incrementa a uno, y también lo escribe. Contador se queda en uno.

- «los dos hilos tienen» → `ambos`: los dos registros se resaltan a la vez
- «el mismo valor: cero» → `mismo`: una línea punteada une los dos 0
- «lo incrementa a uno» → `h1suma`: registro de H1: 0 → 1
- «y lo escribe» → `h1escribe`: el 1 viaja de H1 a la celda
- «pasa a valer uno» → `vale1`: la celda muestra 1; en la línea, «H1 escribe 1»
- «sigue teniendo cero» → `sigue0`: el foco pasa a H2; su 0 parpadea en ámbar
- «lo leyó antes» → `antes2`: en la línea de pasos se resalta «H2 lee 0», antes de «H1 escribe 1»
- «también lo incrementa» → `h2suma`: registro de H2: 0 → 1
- «también lo escribe» → `h2escribe`: el 1 viaja de H2 a la celda y pisa el 1, con impacto
- «se queda en uno» → `queda1`: la celda queda en 1 y se pone roja; alarma

---

# Escena 04 — CondicionCarrera

**Capítulo YouTube:** Condición de carrera

## Narración

<!-- b1 -->
El resultado final es contador igual a uno, cuando pedimos sumar dos
veces desde cero. Y lo que hace que esto valga la pena de estudiar es
que no fue un bug en el sentido de "algo se rompió". Fue una ejecución
perfectamente válida, según la propia definición de interleaving que
vimos en el episodio anterior. No hubo ningún error de programación en
cada hilo por separado. Simplemente, esta vez, el orden de las acciones
fue ese.

- «El resultado final» → `final`: corte; «contador = 1» grande en rojo
- «sumar dos veces» → `dosveces`: debajo aparece «0 + 1 + 1»
- «desde cero» → `desde`: se completa «= 2» en verde, al lado del 1 rojo
- «valga la pena de estudiar» → `estudiar`: la ecuación se encoge y sube; una lupa se posa encima
- «no fue un bug» → `bug`: aparece un bicho
- «algo se rompió» → `roto`: el bicho se tacha con una cruz roja
- «perfectamente válida» → `valida`: corte; la línea de pasos de la escena anterior, con tilde verde
- «definición de interleaving» → `interleaving`: «interleaving» entra con pop en ámbar
- «el episodio anterior» → `anterior`: miniatura del árbol de intercalados de E2; un camino se enciende
- «ningún error de programación» → `codigo`: corte; dos bloques «contador += 1», uno por hilo
- «por separado» → `separado`: cada bloque recibe una tilde verde
- «el orden de las acciones fue ese» → `ese`: entre los dos bloques vuelve la línea de pasos y se resalta

<!-- b2 -->
Para verlo claro, rebobinemos. Mismos dos hilos, mismo código. Pero esta
vez el hilo uno termina sus tres pasos antes de que empiece el dos: lee
cero, escribe uno. Después, el hilo dos lee uno, y escribe dos. Contador
vale dos. Las dos ejecuciones son igual de válidas: lo único que cambió
fue el orden.

- «rebobinemos» → `rebobinar`: todo se desliza hacia atrás con «◀◀»; vuelve la celda en 0
- «Mismos dos hilos» → `mismos`: H1 y H2 destellan
- «mismo código» → `mismocodigo`: «+1» sobre cada hilo
- «termina sus tres pasos» → `h1pasos`: las tres casillas de H1 se encienden en ráfaga
- «lee cero» → `lee0`: el 0 viaja a H1
- «escribe uno» → `esc1`: el 1 viaja a la celda
- «el hilo dos lee uno» → `lee1`: el 1 viaja a H2
- «y escribe dos» → `esc2`: el 2 viaja a la celda
- «Contador vale dos.» → `vale2`: la celda se pone verde, con sonido de éxito
- «igual de válidas» → `ambas`: corte; las dos líneas de pasos una encima de otra: «= 1» y «= 2», las dos con tilde
- «lo único que cambió» → `cambio`: se resaltan las casillas que están en distinto lugar
- «fue el orden» → `orden`: «orden» entra con pop en ámbar

<!-- b3 -->
A esto se le llama condición de carrera, y de aquí sale el principio que
va a ordenar el diseño de todo programa concurrente que escribamos en
adelante: el resultado no puede depender de lo rápido que corre cada
proceso respecto de los demás. Si el resultado cambia según quién le
gane la carrera a quién, el programa está mal, aunque compile, y aunque a
veces, incluso la mayoría de las veces, funcione bien.

- «condición de carrera» → `nombre`: corte; «condición de carrera» gigante en ámbar
- «el principio» → `principio`: el nombre sube; debajo aparece una pista de carreras
- «todo programa concurrente» → `todo`: varias ventanas de programa pequeñas se alinean sobre la pista
- «en adelante» → `adelante`: las ventanas avanzan por la pista
- «no puede depender» → `depender`: corte a la pista; H1 y H2 en la salida
- «lo rápido que corre» → `rapido`: los dos arrancan; uno acelera, el otro se queda atrás
- «respecto de los demás» → `demas`: entran dos corredores más, grises
- «Si el resultado cambia» → `cambia`: sobre la meta, un marcador salta entre 1 y 2
- «la carrera a quién» → `gana`: uno cruza la meta primero, con bandera
- «el programa está mal» → `mal`: cruz roja grande
- «aunque compile» → `compila`: al lado, una tilde verde pequeña con «compila»
- «la mayoría de las veces» → `mayoria`: corte; grilla de 20 ejecuciones: 19 verdes, 1 roja
- «funcione bien» → `bien`: las verdes brillan y la roja parpadea

---

# Escena 05 — SeccionCritica

**Capítulo YouTube:** Sección crítica y exclusión mutua

## Narración

<!-- b1 -->
La condición de carrera que acabamos de ver pasó por una sola razón:
dos hilos entraron sin ningún control a tocar el mismo recurso. A la
parte del código que toca ese recurso compartido la llamamos sección
crítica, y al recurso en sí, recurso crítico. La regla que necesitamos
imponer, en una frase, es esta: solo un proceso puede estar en su
sección crítica a la vez. Eso es, justamente, la exclusión mutua.

- «una sola razón» → `razon`: la celda contador al centro, con H1 y H2 a los lados
- «sin ningún control» → `control`: H1 y H2 se lanzan a la vez hacia la celda y chocan
- «el mismo recurso» → `recurso`: la celda se resalta en rojo
- «la parte del código» → `parte`: corte; el código de un hilo como varias líneas grises
- «sección crítica» → `seccion`: las líneas que tocan contador se enmarcan en ámbar: «sección crítica»
- «recurso crítico» → `recursoc`: la celda, al lado, recibe el rótulo «recurso crítico»
- «La regla que necesitamos imponer» → `regla`: entre la sección crítica y el recurso aparece un candado
- «en una frase» → `frase`: corte; una sala con una puerta
- «solo un proceso» → `uno`: un proceso entra en la sala; otro espera fuera
- «a la vez» → `vez`: el de fuera intenta entrar y rebota
- «la exclusión mutua» → `exclusion`: «exclusión mutua» entra con pop sobre la puerta

<!-- b2 -->
Ahora, imponer esa regla no es tan simple como suena, y no cualquier
solución sirve. Sea por software, por hardware, con semáforos o con
mecanismos más sofisticados que vamos a ver más adelante, toda solución
al problema de la sección crítica tiene que cumplir seis reglas del
juego para considerarse correcta.

- «no es tan simple» → `simple`: la puerta tiembla
- «no cualquier solución sirve» → `sirve`: aparecen varios candados; dos se rompen y caen
- «por software» → `software`: corte; ventana de código, primero de cuatro huecos
- «por hardware» → `hardware`: chip
- «con semáforos» → `semaforo`: semáforo
- «más sofisticados» → `sofisticados`: cuarto hueco con «?»
- «más adelante» → `adelante`: una flecha hacia delante junto al cuarto hueco
- «toda solución» → `toda`: los cuatro íconos se juntan en un bloque delante de la puerta
- «seis reglas del juego» → `seis`: los íconos se van; entran 6 casillas numeradas del 1 al 6
- «considerarse correcta» → `correcta`: tilde verde grande debajo de las 6

---

# Escena 06 — SeguridadProgreso

**Capítulo YouTube:** Las reglas del juego: seguridad y progreso

## Narración

<!-- b1 -->
La primera es la exclusión mutua en sí misma: de todos los procesos con
sección crítica sobre el mismo recurso, solo uno puede tener permiso
para entrar en un momento dado. La cuarta la complementa: si en este
momento nadie está dentro, cualquiera que lo pida tiene que poder
entrar sin demora. Estas dos son las reglas de seguridad: garantizan que
nada malo pase.

- «La primera» → `r1`: las 6 casillas arriba; la 1 se agranda
- «de todos los procesos» → `procesos`: cuatro procesos en fila ante la puerta de la sala
- «el mismo recurso» → `mismo`: la sala se resalta, con el recurso dentro
- «solo uno puede tener permiso» → `permiso`: uno recibe una tilde y entra; los demás esperan
- «en un momento dado» → `momento`: la puerta se cierra detrás de él, con candado
- «La cuarta» → `r4`: la casilla 4 se agranda
- «nadie está dentro» → `vacia`: el de dentro sale y la sala queda vacía
- «sin demora» → `demora`: el primero de la fila entra de golpe, con whoosh
- «reglas de seguridad» → `seguridad`: las casillas 1 y 4 se juntan bajo un escudo verde: «seguridad»
- «nada malo pase» → `nadamalo`: el escudo destella

<!-- b2 -->
La tercera y la sexta son las reglas de progreso: garantizan que algo
bueno termine pasando. La tercera dice que ningún proceso puede quedar
esperando para siempre para entrar: nada de interbloqueo, nada de
inanición. La sexta pone el límite del otro lado: un proceso solo puede
quedarse en su sección crítica por un tiempo finito, y de eso es
responsable el programador, no el mecanismo.

- «La tercera y la sexta» → `r36`: las casillas 3 y 6 se agrandan
- «reglas de progreso» → `progreso`: se juntan bajo una flecha hacia delante: «progreso»
- «algo bueno» → `bueno`: la flecha avanza
- «La tercera dice» → `r3`: corte a la sala; un proceso esperando fuera
- «para siempre para entrar» → `siempre`: un reloj gira rápido sobre el que espera
- «nada de interbloqueo» → `interbloqueo`: «interbloqueo» aparece y se tacha
- «nada de inanición» → `inanicion`: «inanición» aparece y se tacha
- «La sexta» → `r6`: foco al proceso de dentro
- «el límite del otro lado» → `limite`: dentro de la sala aparece una línea de salida
- «quedarse en su sección crítica» → `quedarse`: el proceso de dentro queda quieto, flotando
- «tiempo finito» → `finito`: un reloj sobre él da una vuelta y el proceso sale
- «el programador» → `programador`: aparece un ingeniero, resaltado
- «no el mecanismo» → `mecanismo`: el candado de la puerta se atenúa

---

# Escena 07 — SolucionesTramposas

**Capítulo YouTube:** Soluciones tramposas

## Narración

<!-- b1 -->
Y la segunda y la quinta son las que descartan las soluciones tramposas,
las que "funcionan casi siempre". La segunda exige que un proceso que se
cae fuera de su sección crítica no arrastre a los demás con él. La quinta
prohíbe asumir nada sobre la velocidad de los procesos ni sobre cuántos
procesadores hay: la solución tiene que funcionar igual si un proceso es
mil veces más lento que el otro, y con uno o con mil núcleos.

- «la segunda y la quinta» → `r25`: las 6 casillas arriba; la 2 y la 5 se agrandan
- «soluciones tramposas» → `tramposas`: un candado con una grieta
- «casi siempre» → `casi`: al lado, la grilla de 20 ejecuciones con una roja
- «La segunda exige» → `r2`: corte; tres procesos alrededor de la sala
- «se cae» → `cae`: uno, fuera de la sala, gira y cae en rojo
- «no arrastre a los demás» → `arrastre`: los otros dos siguen entrando y saliendo sin problema
- «La quinta prohíbe» → `r5`: corte; dos procesos en la pista
- «la velocidad» → `velocidad`: uno corre rápido, el otro despacio
- «cuántos procesadores» → `procesadores`: arriba aparece un chip
- «funcionar igual» → `igual`: tilde verde junto a la pista
- «mil veces más lento» → `lento`: el lento casi no se mueve: «×1000»
- «con mil núcleos» → `nucleos`: el chip se multiplica en una grilla de muchos chips

---

# Escena 08 — Deadlock

**Capítulo YouTube:** Deadlock: el abrazo mortal

## Narración

<!-- b1 -->
Resolver la exclusión mutua no cierra el capítulo de problemas: abre
otro. Cuando varios procesos compiten o cooperan, pueden quedar atascados
de formas distintas, y vale la pena separarlas bien porque se confunden
fácilmente entre sí.

- «Resolver la exclusión mutua» → `resuelta`: la puerta con candado verde y tilde
- «abre otro» → `abre`: detrás se abre otra puerta, con luz roja
- «compiten» → `compiten`: dos procesos tiran de un mismo recurso
- «cooperan» → `cooperan`: dos procesos se pasan una ficha
- «quedar atascados» → `atascados`: todos se congelan, en gris
- «de formas distintas» → `formas`: tres tarjetas boca abajo con «?»
- «se confunden fácilmente» → `confunden`: las tarjetas se barajan

<!-- b2 -->
La primera es el deadlock, o interbloqueo: dos o más procesos se quedan
esperándose mutuamente, sin que ninguno pueda avanzar. El caso típico: el
proceso uno ya tomó el recurso A y ahora pide el B; el proceso dos ya
tomó el B y ahora pide el A. Ninguno va a soltar lo que tiene hasta
conseguir lo que le falta, y lo que le falta lo tiene, justamente, el
otro. Los dos quedan congelados, sin consumir procesador, para siempre.

- «el deadlock» → `deadlock`: la primera tarjeta se da vuelta: «deadlock»
- «interbloqueo» → `interbloqueo`: debajo, «interbloqueo»
- «esperándose mutuamente» → `esperan`: corte; P1 y P2 frente a frente
- «sin que ninguno pueda avanzar» → `avanzar`: los dos intentan moverse y solo tiemblan
- «El caso típico» → `caso`: aparecen los recursos A y B, como candados
- «ya tomó el recurso A» → `p1a`: P1 agarra A (línea sólida)
- «ahora pide el B» → `p1b`: flecha punteada de P1 a B
- «ya tomó el B» → `p2b`: P2 agarra B
- «ahora pide el A» → `p2a`: flecha punteada de P2 a A; el ciclo se cierra en rojo
- «Ninguno va a soltar» → `soltar`: las líneas sólidas se engrosan
- «conseguir lo que le falta» → `falta`: las flechas punteadas parpadean
- «lo tiene, justamente, el otro» → `otro`: el ciclo destella en rojo, girando
- «quedan congelados» → `congelados`: todo se vuelve gris azulado
- «para siempre» → `siempre`: un reloj corre y nada más se mueve

---

# Escena 09 — InanicionLivelock

**Capítulo YouTube:** Inanición y livelock

## Narración

<!-- b1 -->
La segunda es la inanición, o starvation, y es más sutil: aquí el sistema,
en conjunto, sí sigue avanzando, pero hay un proceso puntual que queda
fuera para siempre. Si dos procesos se turnan un recurso entre ellos sin
parar, un tercero que también lo pide puede no conseguirlo jamás, aunque
el recurso nunca deje de usarse.

- «la inanición» → `inanicion`: vuelven las tarjetas; la segunda se da vuelta: «inanición»
- «starvation» → `starvation`: debajo, «starvation»
- «es más sutil» → `sutil`: la tarjeta se acerca y una lupa pasa por encima
- «sigue avanzando» → `avanza`: corte; un recurso al centro; P1 y P2 entran y salen, alternándose
- «un proceso puntual» → `p3`: P3 aparece a un costado
- «queda fuera para siempre» → `fuera`: P3 intenta entrar y rebota
- «se turnan un recurso» → `turnan`: el turno entre P1 y P2 se acelera
- «un tercero que también lo pide» → `pide`: P3 parpadea
- «no conseguirlo jamás» → `jamas`: el contador de intentos de P3 sube, en rojo
- «nunca deje de usarse» → `usarse`: el recurso brilla, siempre ocupado

<!-- b2 -->
La tercera es la más engañosa de las tres, y se confunde fácilmente con el
deadlock: el livelock. Aquí los procesos no se bloquean: siguen activos,
consumiendo procesador, reaccionando todo el tiempo a lo que hace el
otro, pero sin avanzar en absoluto. Es exactamente lo que nos pasa cuando
nos cruzamos con alguien en un pasillo estrecho y los dos nos apartamos
hacia el mismo lado, una y otra vez: estamos en movimiento constante, y
sin embargo ninguno de los dos logra pasar.

- «la más engañosa» → `enganosa`: vuelven las tarjetas; la tercera tiembla
- «con el deadlock» → `condeadlock`: la tarjeta «deadlock» se acerca a la tercera
- «el livelock» → `livelock`: la tercera se da vuelta: «livelock»
- «no se bloquean» → `noblq`: corte; dos procesos en movimiento
- «consumiendo procesador» → `cpu`: una barra de uso de CPU sube al 100 %
- «reaccionando todo el tiempo» → `reaccion`: uno se mueve y el otro responde
- «sin avanzar en absoluto» → `sinavanzar`: a la derecha, una meta que nunca se alcanza
- «lo que nos pasa» → `pasa`: corte; dos personas caminan una hacia la otra
- «nos cruzamos con alguien» → `cruzamos`: quedan frente a frente
- «un pasillo estrecho» → `pasillo`: corte; aparecen las paredes de un pasillo estrecho alrededor de las dos
- «hacia el mismo lado» → `lado`: las dos se apartan a la vez hacia el mismo lado
- «una y otra vez» → `otravez`: se repite: izquierda, derecha, izquierda
- «movimiento constante» → `movimiento`: estelas de movimiento detrás de las dos
- «logra pasar» → `pasar`: cruz roja entre las dos

---

# Escena 10 — Invariantes

**Capítulo YouTube:** Invariantes: lo que nunca debe pasar

## Narración

<!-- b1 -->
Y hay un cuarto problema, más general que engloba en parte a los otros:
la violación de invariantes. Un invariante es una condición que debería
cumplirse siempre, en cualquier instante de la ejecución, como "nunca hay
más de un proceso a la vez en la sección crítica". Cuando la
sincronización falla, basta con que exista un solo interleaving que
rompa esa condición, aunque sea por un instante, para que el programa
esté mal.

- «un cuarto problema» → `cuarto`: las tres tarjetas; aparece una cuarta
- «engloba en parte» → `engloba`: la cuarta crece y cubre en parte a las otras
- «violación de invariantes» → `violacion`: la cuarta se da vuelta: «invariante»
- «Un invariante es una condición» → `condicion`: la tarjeta se agranda al centro
- «debería cumplirse siempre» → `siempre`: corte; línea de tiempo con una banda verde continua
- «en cualquier instante» → `cualquier`: un cursor recorre la banda verde
- «nunca hay más de un proceso» → `ejemplo`: arriba, «en sección crítica ≤ 1»
- «a la vez en la sección crítica» → `sc`: debajo, la sala con un solo proceso dentro
- «la sincronización falla» → `falla`: sobre la línea, un contador «1» que avanza con el tiempo
- «un solo interleaving» → `uno`: un punto de la línea se enciende
- «por un instante» → `instante`: el contador salta a 2 en rojo, con alarma, y vuelve a 1
- «el programa esté mal» → `mal`: la banda verde queda cortada en rojo en ese punto

---

# Escena 11 — Cierre

**Capítulo YouTube:** Lo que viene

## Narración

<!-- b1 -->
Resumamos. Dos hilos que suman uno pueden dejar el contador en uno: eso es
una condición de carrera. Para evitarla, la sección crítica necesita
exclusión mutua, y cualquier solución tiene que cumplir seis reglas del
juego. Y aun así, los procesos pueden quedar atascados: por interbloqueo,
por inanición o por livelock. Pero fíjate en lo que tienen todos en común:
basta un solo intercalado malo, entre millones, para que el programa esté
mal. Probarlo mil veces no sirve. Entonces, ¿cómo se demuestra que un
programa concurrente nunca falla? Eso es lo que vamos a ver en el próximo
episodio: demostrar que nunca falla.

- «Resumamos.» → `resumen`: corte limpio
- «Dos hilos que suman uno» → `hilos`: miniatura: H1, H2 y la celda contador
- «el contador en uno» → `uno`: la celda muestra 1 en rojo
- «condición de carrera» → `carrera`: rótulo «condición de carrera»
- «la sección crítica necesita» → `sc`: aparece la sala con su puerta
- «exclusión mutua» → `exclusion`: la puerta se cierra con candado
- «seis reglas del juego» → `seis`: las 6 casillas
- «Y aun así» → `aunasi`: las casillas se apartan hacia un costado
- «quedar atascados» → `atascados`: las tres tarjetas
- «por interbloqueo» → `t1`: «deadlock» destella
- «por inanición» → `t2`: «inanición» destella
- «o por livelock» → `t3`: «livelock» destella
- «en común» → `comun`: todo se agrupa y se aleja
- «un solo intercalado malo» → `malo`: corte; árbol de caminos grises, uno en rojo
- «entre millones» → `millones`: la cámara se aleja y el árbol se multiplica
- «Probarlo mil veces» → `probar`: llueven tildes verdes
- «no sirve» → `nosirve`: las tildes se tachan de golpe
- «¿cómo se demuestra» → `como`: corte; un documento con una demostración (∀, ⇒) se escribe solo
- «nunca falla?» → `pregunta`: «¿nunca falla?» gigante
- «el próximo episodio» → `proximo`: aparece «Episodio 4»
- «demostrar que nunca falla» → `titulo`: el título de E4, solo, con un whoosh
