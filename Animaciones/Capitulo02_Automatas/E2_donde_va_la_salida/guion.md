# Episodio 2 — ¿Dónde va la salida?

Capítulo 2 (Autómatas), episodio 2 de 6. Duración objetivo: ~10 min.
Texto tomado de `../guion_base.md` (escenas 04 a 06), más un gancho y un
cierre nuevos. Las 3 escenas del guion base se parten en 9 escenas atómicas,
más gancho y cierre. Este episodio da por sabido E1 (torniquete, estado,
transición, las dos funciones y la expendedora de latas).

Cambios de texto respecto del guion base (para revisar):
- Escena 01 (gancho) y escena 11 (cierre): texto nuevo.
- Escena 04, b1: «Agreguemos» → «Añadamos».
- Escena 05, b2: «se muda» → «se traslada».
- Escena 08: «Piensa en dos máquinas de Mealy conectadas» → «Ahora imagina dos
  máquinas de Mealy conectadas» (la escena anterior ya empieza con «Piensa»).
- Pausas: se partieron 05 b3 (la idea de Moore / su efecto al encenderse) y
  06 b2 (el reloj / el torniquete en las dos versiones) en dos bloques; el
  texto no cambió.
- Pronunciación a probar de oído antes de sintetizar: Mealy, Moore.

---

# Escena 01 — Gancho

**Capítulo YouTube:** ¿Dónde va la salida?
**Objetivo:** abrir el episodio con la paradoja: dos máquinas que hacen lo
mismo, y una llega siempre un instante tarde, a propósito.

## Narración

<!-- b1 -->
Dos máquinas que hacen exactamente lo mismo. Y una de ellas reacciona
siempre un instante más tarde, a propósito. ¿Por qué querrías una máquina que
llega tarde? Hoy lo vamos a averiguar.

- «Dos máquinas» → `dos`: plano negro; entran dos cajas idénticas, una azul y una magenta, con whoosh
- «hacen exactamente lo mismo» → `igual`: entre ellas aparece un «=» grande en verde
- «una de ellas reacciona» → `reacciona`: sobre las dos cajas cae un pulso de entrada; la azul responde en el acto
- «siempre un instante más tarde» → `tarde`: la magenta responde con retraso; un reloj marca el hueco entre las dos respuestas
- «a propósito» → `proposito`: el retraso se pone en ámbar, con un tick
- «¿Por qué querrías» → `porque`: signo de interrogación grande sobre la magenta
- «llega tarde» → `llega`: la magenta se atenúa un poco, con el reloj al lado
- «lo vamos a averiguar» → `titulo`: corte; el título del episodio, solo, con un whoosh

---

# Escena 02 — Mealy

**Capítulo YouTube:** La máquina de Mealy
**Objetivo:** presentar la máquina de Mealy como el modelo que ya se vio (la
salida depende de entrada y estado) y mostrar que la salida vive en la flecha.

## Narración

<!-- b1 -->
Volvamos al torniquete. En el episodio anterior le dimos dos funciones: una
que decide a qué estado pasa, y otra que decide qué hace hacia fuera. Las
dos miran lo mismo: la entrada que acaba de llegar y el estado en el que
está. Una máquina así, en la que la salida depende de ese par, tiene nombre
propio: es una máquina de Mealy, por George Mealy, que la describió en 1955.

- «Volvamos al torniquete» → `torniquete`: reaparece grande el diagrama del torniquete (dos círculos, cuatro flechas)
- «dos funciones» → `funciones`: junto al diagrama aparecen dos cajas, f y g
- «a qué estado pasa» → `f`: se enciende f; una flecha del diagrama se ilumina
- «qué hace hacia fuera» → `g`: se enciende g; una señal sale del diagrama hacia la derecha
- «Las dos miran lo mismo» → `mismo`: f y g reciben la misma pareja por dos cables
- «la entrada que acaba de llegar» → `entrada`: el cable de la entrada se resalta
- «el estado en el que está» → `estado`: el cable del estado se resalta
- «la salida depende de ese par» → `par`: una llave agrupa la pareja y apunta a la salida g
- «es una máquina de Mealy» → `nombre`: pop «Mealy» en ámbar
- «George Mealy» → `george`: el nombre aparece bajo la palabra, en gris
- «en 1955» → `anio`: pop «1955» junto al nombre

<!-- b2 -->
¿Dónde se dibuja la salida en una máquina de Mealy? En la flecha. Cada
transición lleva escrito lo que la dispara y, después de una barra, lo que
produce. Moneda, barra, nada. Empujón, barra, liberar el paso. La salida no
vive en ningún estado: aparece en el instante en que la máquina cruza de
uno a otro, como el sonido de una puerta al abrirse.

- «¿Dónde se dibuja la salida» → `donde`: las cajas f y g se apartan; un signo de interrogación sobre el diagrama
- «En la flecha.» → `flecha`: una flecha se ilumina con destello
- «lo que la dispara» → `dispara`: sobre la flecha aparece «moneda»
- «después de una barra» → `barra`: aparece «/» en ámbar
- «lo que produce» → `produce`: tras la barra, un hueco para la salida
- «Moneda, barra, nada» → `mon`: la etiqueta se completa: «moneda / nada»
- «Empujón, barra, liberar el paso» → `emp`: la otra flecha: «empujón / liberar el paso»
- «ningún estado» → `nostado`: un tachón sobre los círculos: no llevan salida
- «en el instante en que la máquina cruza» → `cruza`: una ficha recorre la flecha de un estado a otro
- «el sonido de una puerta al abrirse» → `puerta`: al llegar, se abre un candado sobre la flecha, con sonido

---

# Escena 03 — SalidaEnLaFlecha

**Capítulo YouTube:** La salida vive en la flecha
**Objetivo:** ver la expendedora como Mealy y preparar con un ejemplo mínimo
(A, B, x) el problema de la escena siguiente.

## Narración

<!-- b1 -->
La expendedora del episodio anterior también es una máquina de Mealy, y
en ella se ve todavía más claro. Con cero céntimos, llega una moneda: pasa
a cincuenta, y no hace nada. Con cincuenta, llega otra moneda: vuelve a
cero, y suelta la lata. La lata no cae por estar en ningún estado. Cae en
el salto: es la salida de esa flecha, y solo de esa.

- «La expendedora» → `expendedora`: la máquina expendedora grande a la izquierda; a la derecha, su diagrama de dos estados
- «Con cero céntimos» → `cero`: se enciende el estado «0» con una ficha
- «llega una moneda» → `moneda1`: cae una moneda de 0,50
- «pasa a cincuenta» → `cincuenta`: la ficha salta a «0,50»
- «no hace nada» → `nada`: sobre la flecha, «moneda / nada» en gris
- «llega otra moneda» → `moneda2`: cae la segunda moneda
- «vuelve a cero» → `vuelve`: la ficha regresa a «0»
- «suelta la lata» → `lata`: la lata cae de la máquina en mitad de la flecha; «moneda / lata» en verde
- «no cae por estar en ningún estado» → `noestado`: los dos círculos brillan sin lata: nada cae desde ellos
- «Cae en el salto» → `salto`: la ficha vuelve a cruzar y la lata cae justo en ese instante
- «y solo de esa» → `solo`: la flecha queda resaltada; la otra, atenuada

<!-- b2 -->
Eso tiene una consecuencia curiosa. Imagina una máquina con dos estados, A
y B, y una sola entrada, x. Si estando en A llega una x, pasa a B y
responde uno. Si estando en B llega otra x, se queda en B, pero responde
cero. Es como un timbre que solo suena con el primer cliente del día. Fíjate
bien: la máquina acaba en el mismo estado, y sin embargo ha dicho cosas
distintas. En Mealy, la salida no depende solo de dónde estás, sino de por
dónde has llegado.

- «una consecuencia curiosa» → `curiosa`: corte seco; plano limpio con una lupa que baja
- «dos estados» → `estados`: aparecen los círculos A y B
- «una sola entrada» → `entrada`: aparece el rótulo «x»
- «estando en A llega una x» → `enA`: la ficha en A; cae una x
- «pasa a B» → `aB`: la ficha salta de A a B
- «responde uno» → `uno`: la flecha se rotula «x / 1»; sale un 1 en verde
- «estando en B llega otra x» → `enB`: cae otra x
- «se queda en B» → `bucle`: la ficha recorre el bucle de B
- «responde cero» → `cero`: el bucle se rotula «x / 0»; sale un 0 en gris
- «un timbre» → `timbre`: aparece un timbre; suena con la primera x y calla con la segunda
- «primer cliente del día» → `cliente`: una persona entra y suena; las siguientes, en silencio
- «el mismo estado» → `mismo`: las dos flechas aterrizan en B, que se resalta
- «cosas distintas» → `distintas`: el «1» y el «0» lado a lado
- «no depende solo de dónde estás» → `donde`: el círculo B se resalta
- «por dónde has llegado» → `llegado`: se iluminan las dos flechas que llegan a B

---

# Escena 04 — EntradaVacia

**Capítulo YouTube:** La entrada vacía
**Objetivo:** llegar a Moore desde un problema: con la entrada vacía (lambda)
la transición no tiene dudas, pero la salida de Mealy queda indeterminada.

## Narración

<!-- b1 -->
Ahora hagámosle una pregunta incómoda a esa máquina. ¿Qué responde cuando
no llega nada? Porque en un sistema real hay instantes en los que no pasa
nada, y conviene poder decirlo. Añadamos, entonces, una entrada especial,
la entrada vacía. Se escribe con la letra griega lambda, y significa
exactamente eso: no ha llegado nada.

- «una pregunta incómoda» → `pregunta`: vuelve el ejemplo A, B; un signo de interrogación sobre la máquina
- «no llega nada?» → `nada`: un reloj gira y no cae ninguna x
- «hay instantes» → `instantes`: una línea de tiempo con dos x y un hueco largo entre ellas
- «conviene poder decirlo» → `decirlo`: el hueco se marca con una señal propia
- «una entrada especial» → `especial`: una ficha translúcida se une a la ficha x
- «la entrada vacía» → `vacia`: una caja sin nada dentro
- «letra griega lambda» → `lambda`: pop de una λ enorme
- «no ha llegado nada» → `nollego`: bajo la λ, el rótulo «no ha llegado nada»

<!-- b2 -->
Para la función de transición no hay ningún problema: si no llega nada, la
máquina se queda donde está. Pero para la función de salida, sí. La máquina
está en B, y no llega nada. ¿Qué responde? Si a B llegó desde A, lo último
que dijo fue uno. Si llegó desde el propio B, fue cero. Con la entrada
vacía, la respuesta depende de un pasado que el estado no guarda. La salida
queda indeterminada.

- «función de transición» → `ft`: la caja f aparece con una tilde verde
- «se queda donde está» → `quieta`: la ficha en B; pasa una λ y la ficha no se mueve
- «función de salida» → `fs`: la caja g aparece con un signo de interrogación
- «La máquina está en B» → `enB`: la ficha destacada en B
- «¿Qué responde?» → `q`: sobre B, un «?» que oscila entre 1 y 0
- «llegó desde A» → `desdeA`: se ilumina la flecha «x / 1»
- «fue uno» → `uno`: sale un 1
- «desde el propio B» → `desdeB`: se ilumina el bucle «x / 0»
- «fue cero» → `cero`: sale un 0
- «un pasado que el estado no guarda» → `pasado`: una nube «pasado» sobre B que se desvanece; B queda vacío
- «indeterminada» → `indet`: «?» rojo enorme que tiembla

---

# Escena 05 — Moore

**Capítulo YouTube:** La máquina de Moore
**Objetivo:** resolver el problema con una única salida por estado y mostrar
que Moore habla al encenderse y Mealy, no. El estado B se parte en dos.

## Narración

<!-- b1 -->
Solo hay una forma de arreglarlo: que cada estado tenga una salida, y solo
una. Así, sepas o no qué acaba de llegar, basta con mirar el estado para
saber qué dice la máquina. Esa es la máquina de Moore, que Edward Moore
publicó un año después, en 1956.

- «una forma de arreglarlo» → `arreglo`: se limpia la «?»; queda el ejemplo A, B
- «cada estado tenga una salida» → `cadauna`: dentro de cada círculo aparece un hueco para la salida
- «y solo una» → `sola`: cada hueco se cierra con un candado
- «sepas o no qué acaba de llegar» → `sepas`: la x y la λ parpadean sobre la ficha sin efecto
- «basta con mirar el estado» → `mirar`: una lupa sobre el círculo B
- «qué dice la máquina» → `dice`: un bocadillo con la salida del estado
- «la máquina de Moore» → `moore`: pop «Moore» en ámbar
- «Edward Moore» → `edward`: el nombre completo aparece en gris
- «en 1956» → `anio`: pop «1956» junto a «1955» en una línea de tiempo

<!-- b2 -->
Su función de salida ya no mira la entrada: solo mira el estado. Y eso tiene
un efecto curioso: una máquina de Moore ya dice algo nada más encenderse,
porque su estado inicial tiene su propia salida. Una de Mealy, en cambio,
necesita que llegue una primera entrada para decir cualquier cosa.

- «Su función de salida» → `fsalida`: la caja g aparece
- «ya no mira la entrada» → `noentrada`: el cable de la entrada a g se corta
- «solo mira el estado» → `soloestado`: queda solo el cable del estado
- «una máquina de Moore ya dice algo» → `habla`: la máquina de Moore, con su salida visible
- «nada más encenderse» → `enciende`: un interruptor se activa; la salida ya está ahí
- «su estado inicial» → `inicial`: la flecha de inicio; la salida junto al estado
- «Una de Mealy, en cambio,» → `mealy`: al lado, la Mealy, callada, sin ninguna salida
- «una primera entrada» → `primera`: cae una x sobre la Mealy
- «decir cualquier cosa» → `dice2`: solo entonces la Mealy suelta su salida

<!-- b3 -->
En el dibujo, la salida se traslada: sale de las flechas y se escribe dentro de
los círculos. Y en nuestro ejemplo, el estado B tiene que partirse en dos:
un B que dice uno, al que se llega desde A, y un B que dice cero, al que se
llega desde sí mismo. La máquina gana un estado, pero ya nunca duda de qué
responder.

- «la salida se traslada» → `traslada`: las salidas de las etiquetas suben hacia los círculos
- «de las flechas» → `flechas`: los rótulos de las flechas pierden su «/ salida»
- «dentro de los círculos» → `dentro`: las salidas quedan escritas dentro de A y de B
- «tiene que partirse en dos» → `parte`: el círculo B se parte en dos
- «un B que dice uno» → `b1`: el círculo «B / 1»
- «al que se llega desde A» → `desdea`: la flecha de A a «B / 1»
- «un B que dice cero» → `b0`: el círculo «B / 0»
- «desde sí mismo» → `sim`: la flecha de «B / 1» a «B / 0» y el bucle en «B / 0»
- «gana un estado» → `gana`: un contador de estados pasa de 2 a 3
- «nunca duda de qué responder» → `nunca`: cada círculo con una sola salida; tilde verde

---

# Escena 06 — Semaforo

**Capítulo YouTube:** Un semáforo es una máquina de Moore
**Objetivo:** dar un caso natural donde Moore encaja: la salida describe cómo
está el sistema, no qué acaba de pasar.

## Narración

<!-- b1 -->
Hay máquinas que son de Moore por naturaleza. Piensa en un semáforo de
tráfico. Tiene tres estados: verde, ámbar y rojo. Y su salida, el color de
la luz, depende solo del estado en el que está. Da igual qué señal le hizo
cambiar, o cuánto tiempo lleva así: si está en ámbar, luce ámbar. Cuando la
salida describe cómo está el sistema, y no qué acaba de pasar, Moore encaja
como un guante.

- «de Moore por naturaleza» → `naturaleza`: corte; el rótulo «Moore» con un tilde
- «un semáforo de tráfico» → `semaforo`: el semáforo grande a la derecha
- «tres estados» → `tres`: tres círculos en fila a la izquierda
- «verde» → `verde`: el primer círculo se pinta de verde
- «ámbar» → `ambar`: el segundo, de ámbar
- «rojo» → `rojo`: el tercero, de rojo; flechas de uno a otro en ciclo
- «el color de la luz» → `color`: cada círculo enciende su luz en el semáforo
- «depende solo del estado» → `depende`: una flecha del círculo a la luz; sin más cables
- «Da igual qué señal» → `igual`: llegan señales y se tachan una a una
- «cuánto tiempo lleva así» → `tiempo`: un reloj de arena que se tacha
- «si está en ámbar» → `enambar`: la ficha en el círculo ámbar
- «luce ámbar» → `luce`: el semáforo se enciende en ámbar
- «cómo está el sistema» → `como`: el rótulo «cómo está» en verde
- «qué acaba de pasar» → `pasar`: el rótulo «qué acaba de pasar», tachado
- «como un guante» → `guante`: una tilde verde y el sello «Moore» sobre el semáforo

---

# Escena 07 — TiempoDeReaccion

**Capítulo YouTube:** Mealy o Moore: el tiempo
**Objetivo:** comparar las dos por número de estados y por velocidad de
reacción: Moore siempre va un ciclo por detrás, y eso protege.

## Narración

<!-- b1 -->
Entonces, ¿cuál de las dos es mejor? Depende de qué se quiera cuidar.
Mealy suele necesitar menos estados, porque puede apoyar salidas distintas
en las flechas que salen de un mismo estado. Moore necesita un estado por
cada salida distinta, como acabamos de ver. Pero hay una diferencia más
importante, y tiene que ver con el tiempo.

- «cuál de las dos es mejor?» → `mejor`: corte; el ejemplo A, B en versión Mealy y en versión Moore, lado a lado
- «Depende de qué se quiera cuidar» → `depende`: una balanza vacía entre las dos
- «Mealy suele necesitar menos estados» → `menos`: un contador «2» bajo la Mealy
- «apoyar salidas distintas» → `apoyar`: las salidas 1 y 0 se resaltan sobre sus flechas
- «un mismo estado» → `mismo`: las flechas que llegan a B se resaltan
- «Moore necesita un estado por» → `moore`: un contador «3» bajo la Moore
- «cada salida distinta» → `cada`: se resaltan «B / 1» y «B / 0»
- «como acabamos de ver» → `visto`: un tick sobre la Moore
- «una diferencia más importante» → `diferencia`: los contadores se atenúan
- «con el tiempo» → `tiempo`: un reloj grande entra en el centro

<!-- b2 -->
En los circuitos, estas máquinas avanzan al compás de un reloj. Una máquina
de Mealy reacciona en el mismo ciclo: cambia la entrada, y la salida cambia
al instante. Una de Moore espera: su salida depende del estado, y el estado
solo cambia con el siguiente tic del reloj. Siempre va un ciclo por detrás.
Parece un defecto, y sin embargo es una protección.

- «En los circuitos» → `circuitos`: un chip con pistas en el fondo
- «al compás de un reloj» → `reloj`: una señal de reloj cuadrada abajo, con sus tics
- «en el mismo ciclo» → `ciclo`: dos cronogramas: entrada y salida de la Mealy
- «cambia la entrada» → `entrada`: la señal de entrada sube
- «al instante» → `instante`: la salida de la Mealy sube en el mismo punto
- «Una de Moore espera» → `espera`: el cronograma de la Moore, con la salida quieta
- «depende del estado» → `estado`: sube la señal del estado, en el siguiente tic
- «siguiente tic del reloj» → `tic`: el tic; la salida de la Moore sube un ciclo después
- «un ciclo por detrás» → `detras`: una llave en ámbar marca el retraso de un ciclo
- «Parece un defecto» → `defecto`: una cruz roja sobre la llave
- «es una protección» → `proteccion`: la cruz da paso a un escudo

<!-- b3 -->
Piensa en el torniquete. Si fuera de Moore, la salida viviría en el estado:
desbloqueado significaría «paso libre» durante todo el tiempo que dure. Si
es de Mealy, libera el paso justo en el instante del empujón. Los dos
funcionan. La diferencia es cuándo se entera el mundo.

- «Piensa en el torniquete» → `torniquete`: corte; el torniquete grande al centro
- «Si fuera de Moore» → `moore`: versión Moore del diagrama, a la izquierda
- «la salida viviría en el estado» → `vivir`: una luz verde dentro del círculo «desbloqueado»
- «paso libre» → `libre`: el rótulo «paso libre»; la luz verde encendida
- «durante todo el tiempo que dure» → `dure`: una barra verde que se alarga sobre una línea de tiempo
- «Si es de Mealy» → `mealy`: versión Mealy del diagrama, a la derecha
- «libera el paso» → `libera`: la flecha «empujón / liberar el paso»
- «justo en el instante del empujón» → `empujon`: un destello puntual sobre la línea de tiempo
- «Los dos funcionan» → `funcionan`: una tilde sobre cada versión
- «cuándo se entera el mundo» → `mundo`: las dos marcas en la misma línea de tiempo, una barra larga y un destello

---

# Escena 08 — Realimentacion

**Capítulo YouTube:** Cuando dos máquinas se hablan
**Objetivo:** mostrar por qué el ciclo de retraso de Moore es una protección:
corta la realimentación asíncrona.

## Narración

<!-- b1 -->
Ahora imagina dos máquinas de Mealy conectadas, donde la salida de cada
una es la entrada de la otra. Un cambio en la primera llega al instante a la
segunda, que responde al instante a la primera, y así, en un bucle que no
espera a ningún reloj. Eso se llama realimentación asíncrona, y es una
fuente de errores muy difícil de rastrear. Con Moore, ese ciclo de retraso
corta el bucle.

- «dos máquinas de Mealy conectadas» → `dos`: dos cajas «M1» y «M2» con dos cables que se cruzan
- «la salida de cada una» → `salida`: el cable de M1 a M2 se resalta
- «la entrada de la otra» → `entrada`: el cable de M2 a M1 se resalta
- «Un cambio en la primera» → `cambio`: un pulso sale de M1
- «llega al instante a la segunda» → `llega`: el pulso llega a M2
- «responde al instante» → `responde`: M2 devuelve un pulso a M1
- «un bucle» → `bucle`: el pulso gira entre las dos, cada vez más rápido
- «que no espera a ningún reloj» → `sinreloj`: un reloj tachado
- «realimentación asíncrona» → `realim`: pop del término; las cajas se ponen rojas
- «errores muy difícil de rastrear» → `errores`: una lupa recorre el bucle y no encuentra el fallo
- «Con Moore» → `conmoore`: las cajas pasan a ser de Moore, con un reloj entre ellas
- «ese ciclo de retraso» → `retraso`: el reloj marca un tic entre cada pulso
- «corta el bucle» → `corta`: una barra corta el cable; el pulso se detiene y aparece una tilde verde

---

# Escena 09 — MismaPotencia

**Capítulo YouTube:** Misma potencia, dos recetas
**Objetivo:** dejar claro que Mealy y Moore son igual de potentes y mostrar
las dos recetas de conversión, con el ejemplo A, B.

## Narración

<!-- b1 -->
Aun así, que nadie se confunda: las dos son igual de potentes. Todo lo que
hace una, lo puede hacer la otra, y hay recetas para pasar de una a otra.
De Moore a Mealy basta con copiar la salida de cada estado en las flechas
que llegan a él. De Mealy a Moore hay que hacer lo que hicimos con B: partir
cada estado en tantas copias como salidas distintas traigan las flechas que
le llegan. Cambia el dibujo, pero no lo que la máquina es capaz de hacer.

- «igual de potentes» → `igual`: un «=» gigante entre Mealy y Moore
- «Todo lo que hace una» → `una`: una flecha de la Mealy a la Moore
- «lo puede hacer la otra» → `otra`: la flecha se hace doble, de ida y vuelta
- «hay recetas» → `recetas`: aparecen dos tarjetas de receta
- «De Moore a Mealy» → `moorea`: la primera tarjeta se abre con el ejemplo en versión Moore
- «copiar la salida de cada estado» → `copiar`: las salidas de los círculos se copian
- «en las flechas que llegan a él» → `flechas`: las copias bajan a las flechas entrantes; los círculos quedan sin salida
- «De Mealy a Moore» → `mealya`: la segunda tarjeta se abre con el ejemplo en versión Mealy
- «lo que hicimos con B» → `conb`: el círculo B se parte en dos
- «tantas copias como salidas distintas» → `copias`: se cuentan las salidas distintas de las flechas que llegan: dos, dos copias
- «Cambia el dibujo» → `dibujo`: los dos dibujos se intercambian de sitio
- «lo que la máquina es capaz de hacer» → `capaz`: el «=» vuelve a llenar la pantalla

---

# Escena 10 — Accesibles

**Capítulo YouTube:** Accesibles, conexos y deterministas
**Objetivo:** presentar estado accesible, autómata conexo y determinismo,
que prepara E3.

## Narración

<!-- b1 -->
Antes de seguir, tres palabras que vamos a usar mucho. Un estado es
accesible si alguna secuencia de entradas lleva hasta él desde el estado
inicial. Imagina que alguien le añade a la expendedora un estado de
setenta y cinco céntimos, pero la máquina solo acepta monedas de
cincuenta: nunca se llegará a él. Un autómata es conexo si todos sus
estados son accesibles. Y si no lo es, se pueden borrar los estados a los
que nunca se llega, y el autómata sigue haciendo exactamente lo mismo,
porque nadie los iba a visitar.

- «tres palabras» → `tres`: corte; tres casillas vacías en fila
- «accesible» → `accesible`: pop «accesible» en la primera casilla
- «alguna secuencia de entradas» → `secuencia`: una cinta con entradas sobre el diagrama
- «desde el estado inicial» → `inicial`: se ilumina el camino desde el estado inicial hasta otro estado
- «Imagina que alguien» → `alguien`: el diagrama de la expendedora, con sus dos estados
- «un estado de setenta y cinco céntimos» → `setenta`: un círculo «0,75» suelto, sin flechas
- «monedas de cincuenta» → `monedas`: cae una moneda de 0,50
- «nunca se llegará a él» → `nunca`: una lupa busca un camino hasta «0,75» y no lo encuentra
- «es conexo» → `conexo`: pop «conexo» en la segunda casilla
- «todos sus estados son accesibles» → `todos`: se iluminan todos los estados menos el suelto
- «se pueden borrar» → `borrar`: una goma pasa sobre «0,75», que se desvanece
- «exactamente lo mismo» → `igual`: el resto queda intacto, con una tilde verde
- «nadie los iba a visitar» → `visitar`: una ficha recorre el diagrama sin acercarse al hueco que dejó «0,75»

<!-- b2 -->
La tercera palabra es determinista: para cada estado y cada entrada, hay un
único destino posible. Todas las máquinas que hemos visto hasta ahora lo
son. Pero pronto vamos a conocer una que no lo es, y que, en vez de elegir
un camino, se atreve con todos.

- «La tercera palabra» → `tercera`: se enciende la tercera casilla
- «es determinista» → `determ`: pop «determinista»
- «cada estado y cada entrada» → `cada`: la tabla 2 × 2 del torniquete: filas de estados, columnas de entradas
- «un único destino posible» → `unico`: cada casilla con una sola flecha; tilde verde
- «Todas las máquinas que hemos visto» → `todas`: el torniquete, la expendedora, una Mealy y una Moore en fila, con una tilde cada una
- «una que no lo es» → `noes`: una silueta velada con un «?»
- «elegir un camino» → `elegir`: un estado con dos flechas de la misma entrada hacia sitios distintos
- «se atreve con todos» → `todos2`: las dos flechas se iluminan a la vez, con dos luces avanzando

---

# Escena 11 — Cierre

**Capítulo YouTube:** Lo que viene: la máquina que dice sí o no
**Objetivo:** recoger lo visto y abrir el episodio siguiente por su título.

## Narración

<!-- b1 -->
Hoy hemos visto dónde vive la salida: en la flecha, en Mealy; dentro del
estado, en Moore. Dos formas de contar lo mismo. Pero hasta ahora nuestras
máquinas siempre tenían algo que decir. En el próximo episodio, una tendrá una
sola respuesta posible: sí o no. Se titula: La máquina que dice sí o no.

- «dónde vive la salida» → `donde`: la pregunta del título, en ámbar
- «en la flecha» → `flecha`: la Mealy con su «x / 1» resaltado
- «en Mealy» → `mealy`: pop «Mealy»
- «dentro del estado» → `dentro`: la Moore con «B / 1» resaltado
- «en Moore» → `moore`: pop «Moore»
- «Dos formas de contar lo mismo» → `igual`: un «=» entre las dos
- «siempre tenían algo que decir» → `decir`: salen de las máquinas un 1, un 0, una lata y una luz
- «una tendrá una sola respuesta» → `una`: una máquina sola, con un único cartel en blanco
- «sí o no» → `sino`: el cartel se parte en «SÍ» verde y «NO» rojo, con pop
- «La máquina que dice sí o no» → `titulo`: corte; el título del próximo episodio, solo, con un whoosh
