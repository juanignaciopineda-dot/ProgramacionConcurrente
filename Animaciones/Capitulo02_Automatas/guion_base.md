# Capítulo 2 — Autómatas

> **Guion base: narración de todo el capítulo. No se renderiza.**
> Es la fuente de texto para los 6 episodios (ver `../README.md` §1 para qué
> escenas van en cada uno). Cada episodio copia de aquí su narración a su
> propio `E<N>_.../guion.md`, que es el que lleva gancho, cierre y momentos, y
> el que se anima. Las secciones "Visual" son ideas iniciales, no
> especificación.
>
> **Estado:** narración completa, **esperando la aprobación del usuario**.
> Mientras no esté aprobada, cualquier ajuste de texto que pida el usuario se
> hace aquí primero, y después se copia al `guion.md` del episodio.

Guion para las 18 escenas del capítulo. Fuente: `Apuntes/PROGRAMACION
CONCURRENTE Y PARALELA.md` (sección "Gramáticas, Lenguajes y Autómatas",
líneas 746-1362), **sin** "Definición formal de gramática", "Jerarquía de
Chomsky", "Expresiones regulares" ni las conversiones gramática ↔ autómata y
ER ↔ autómata: todo eso va al capítulo 3 (Lenguajes y gramáticas). Filminas:
`Filmimas/Clase 2` y `Clase 3` (*Lenguaje regular con notas*). Solo teoría: sin
JFLAP ni ejercicios.

Orden de la materia: Concurrencia → **Autómatas** → Lenguajes y gramáticas →
Redes de Petri. Como autómatas va antes que lenguajes, aquí solo se presenta
lo mínimo (símbolo, alfabeto, cadena) cuando hace falta, sin la definición
formal de lenguaje ni la palabra "gramática".

Convención: cada escena tiene Objetivo, qué hace falta saber antes (para no
dar nada por sabido), qué se ve, y la narración partida en bloques
`<!-- bN -->` que se sincronizan uno a uno con un tramo de animación.

Tono: narrado como una historia, no como una lista de definiciones. Se
arranca de un caso concreto (un torniquete, una máquina expendedora, una pila
de platos) y desde ahí se llega al concepto. Castellano de España.

## Reparto en episodios

| Episodio | Escenas | Idea fuerza |
|---|---|---|
| E1 · La máquina que recuerda sin memoria | 01 SistemasReactivos, 02 IdeaDeEstado, 03 DefinicionAutomata | El estado es un resumen de toda la historia |
| E2 · ¿Dónde va la salida? | 04 Mealy, 05 Moore, 06 MealyVsMoore | Dos máquinas que hacen lo mismo, y una reacciona un ciclo más tarde a propósito |
| E3 · La máquina que dice sí o no | 07 CadenasYAlfabetos, 08 AutomataFinito, 09 AFND | Una máquina que no elige camino: los recorre todos |
| E4 · De muchos caminos a uno | 10 Subconjuntos, 11 PrecioDelDeterminismo, 12 AutomataProducto | Por qué la materia no modela con esto, aunque lo necesite para entender |
| E5 · Darle memoria a la máquina | 13 LimiteDeLoFinito, 14 AutomataDePila, 15 PilaNoDeterminista | Un autómata finito no puede contar; con una pila, sí |
| E6 · La máquina que puede calcularlo todo | 16 MaquinaDeTuring, 17 TesisDeChurch, 18 JerarquiaPorMemoria | La potencia de una máquina es exactamente la memoria que tiene |

El gancho y el cierre de cada episodio **no** están aquí: se escriben al
armar el `guion.md` del episodio (README §2, paso 2), igual que en el
capítulo 1. Cada cierre anuncia el episodio siguiente por su título.

## Hilos que cruzan el capítulo

Para quien arme cada episodio: estos bucles ya están sembrados en la
narración y hay que respetarlos (y retomarlos en ganchos y cierres).

- **Pregunta del capítulo**: se abre en la escena 03 (E1) y se responde en la
  escena 18 (E6): ¿qué puede hacer una máquina que solo recuerda su estado, y
  qué no podrá hacer jamás?
- **"Contar"**: se abre con la expendedora en la escena 02 (E1) y se paga en la
  escena 13 (E5).
- **Lambda**: aparece como "no ha llegado nada" en la escena 05 (E2) y se
  reutiliza como cadena vacía en la escena 07 (E3).
- **No determinismo**: la escena 06 (E2) termina anunciándolo; escena 09 (E3)
  lo conecta con el planificador del capítulo 1 (E2); escena 15 (E5) muestra
  que con pila sí suma potencia.
- **El ejemplo S, A, final**: se presenta en la escena 09 (E3) y se convierte
  a determinista en la escena 10 (E4). Tiene que ser el mismo dibujo.
- **Explosión de estados**: la escena 12 (E4) pone números a la escena 08 de
  Cap. 1 E4 y deja abierta la pregunta que responde el capítulo 4 (Redes de
  Petri), sin nombrarlas.
- **Verificación**: la escena 17 (E6) presenta el problema de la parada como
  el límite de la verificación automática del capítulo 1 (el capítulo 1 **no**
  lo nombró: aquí se presenta por primera vez).
- **Cierre del capítulo** (cierre de E6, a escribir): cada máquina reconoce
  una familia de lenguajes… ¿quién escribe esos lenguajes? → capítulo 3.
  Sin nombrar a Chomsky ni los tipos de gramática.

## Recortes decididos

No entran, o solo se mencionan: las n-uplas formales del autómata de pila y
de la máquina de Turing (se muestran las piezas, no la tupla); el autómata de
pila de "una a, entre una y tres bes" entero (solo la idea de los caminos); la
tabla completa de ventajas de Mealy y Moore para circuitos (solo velocidad y
seguridad); la notación de configuraciones con ⊢; JFLAP.

## Pronunciación

Probar de oído antes de sintetizar (README §9): Mealy, Moore, Shannon,
Turing, Church, Alonzo. También cómo suenan las letras sueltas que se leen en
voz alta ("aes", "bes", "q", "lambda").

---

# Escena 01 — SistemasReactivos

**Objetivo:** retomar la distinción transformacional / reactivo del capítulo 1,
sumar la tercera familia (interactivo) con la pregunta "¿quién marca el
ritmo?", y presentar el evento como lo que llega de fuera. Terminar en que un
sistema reactivo necesita un modelo, y el más sencillo es el autómata.

**Qué hace falta saber antes:** Cap. 1 E4, escena 06 (sistemas reactivos y
transformacionales).

**Visual:**
- Tres columnas que aparecen de a una: transformacional (un compilador:
  entra código, sale programa, fin), interactivo (una terminal que responde
  cuando puede, con un reloj de arena), reactivo (un semáforo o un freno que
  reacciona al instante).
- Sobre cada columna, una batuta: en la primera no la tiene nadie, en la
  segunda la tiene el sistema, en la tercera la tiene el entorno.
- Eventos como pequeños rayos que llegan desde fuera, en desorden, algunos a
  la vez: botón, sensor, mensaje. Emisores a la izquierda, consumidores a la
  derecha.
- El hecho (un dedo sobre un botón) y el aviso (un sobre que viaja): los
  sobres llegan con retraso, amontonados, desordenados.

## Narración

<!-- b1 -->
En el capítulo anterior separamos los programas en dos grandes familias.
Los transformacionales, como un compilador: reciben una entrada, calculan,
entregan un resultado y terminan. Y los reactivos, que no terminan nunca:
están siempre atentos a lo que pasa a su alrededor. Pero entre esos dos
extremos hay una tercera familia, y la diferencia entre las tres está en una
pregunta muy sencilla: ¿quién marca el ritmo?

<!-- b2 -->
En un programa transformacional, nadie: arranca, corre y termina. En uno
interactivo, como una terminal de comandos o una base de datos, la
conversación con quien lo usa es continua, pero es el propio sistema el que
decide cuándo responde: si está ocupado, te toca esperar. En un sistema
reactivo, en cambio, el ritmo lo marca el entorno. El freno de un coche, el
piloto automático de un avión o el control de un semáforo no pueden pedirle
al mundo que espere: tienen que reaccionar a la velocidad a la que el mundo
cambia.

<!-- b3 -->
Y eso que le llega al sistema desde fuera tiene nombre: evento. Un botón
que se pulsa, un sensor que se activa, un mensaje que llega por la red. Hay
arquitecturas enteras construidas alrededor de esta idea: unos componentes
emiten eventos, otros los consumen, y quien consume tiene que reaccionar en
cuanto el evento aparece. Fíjate en que un sistema así es concurrente casi
por definición: los eventos llegan cuando quieren, en el orden que quieren,
y a veces varios a la vez.

<!-- b4 -->
Un detalle de vocabulario que parece pequeño, pero no lo es. Cuando
alguien pulsa un botón, el hecho físico ocurre una sola vez, allí, en el
botón. Lo que viaja por el sistema es otra cosa: un mensaje que avisa de
que eso ha ocurrido. A ese aviso es a lo que llamamos evento. El sistema
nunca ve el mundo: ve avisos del mundo, que pueden llegar tarde,
amontonados o en un orden distinto del real. Para razonar sobre algo así,
con garantías, necesitamos un modelo. Y el modelo más sencillo que existe
es el autómata.

---

# Escena 02 — IdeaDeEstado

**Objetivo:** construir la idea de estado desde un torniquete: el estado es
el resumen de la historia que importa para decidir la próxima respuesta.
Presentar transición, configuración y movimiento. Con la expendedora, abrir
el bucle "contar" que se paga en la escena 13.

**Qué hace falta saber antes:** escena 01.

**Visual:**
- Claude Shannon: nombre y década, con un circuito sencillo que se enciende.
  Corte al torniquete de metro, grande, al centro.
- Moneda que entra → el torniquete se desbloquea (candado que se abre).
  Empujón sin pagar → el torniquete no se mueve (sacudida en rojo).
- Las preguntas "¿cuántas personas?", "¿en qué orden?", "¿a qué hora?"
  aparecen y se tachan una a una; queda solo "bloqueado / desbloqueado".
- Una cinta larguísima de eventos (moneda, empujón, empujón, moneda…) que se
  comprime y entra en un único círculo: el estado.
- Los dos estados como círculos, con el torniquete en miniatura dentro, y las
  flechas de transición que se dibujan a medida que se dicen, incluidos los
  bucles (empujón en bloqueado, moneda en desbloqueado).
- Expendedora: un contador de "lo pagado" que salta 0 → 0,50 → lata. Con la
  lata a dos euros, los estados se multiplican (0; 0,50; 1; 1,50).

## Narración

<!-- b1 -->
La idea viene de la ingeniería eléctrica. A finales de los años treinta,
Claude Shannon mostró que la lógica matemática podía describir el
funcionamiento de un circuito, y de esa línea de trabajo nació la teoría de
autómatas. Pero para entenderla no hace falta ningún circuito: basta con un
torniquete, como los del metro.

<!-- b2 -->
Un torniquete tiene una tarea muy simple. Si metes una moneda, te deja
pasar. Si empujas sin haber pagado, no se mueve. Ahora, piensa en lo que el
torniquete necesita saber para decidir qué hacer cuando lo empujas.
¿Necesita recordar cuántas personas han pasado hoy? ¿En qué orden pagaron?
¿A qué hora? No. Solo necesita saber una cosa: si ahora mismo está
bloqueado o desbloqueado.

<!-- b3 -->
Eso es un estado. El estado de un autómata es toda la información que hace
falta, en un momento dado, para saber cómo va a responder a la próxima
entrada. Es un resumen de la historia: el torniquete no guarda el pasado,
guarda solo lo que del pasado importa. Por eso decimos que recuerda sin
memoria. La historia completa, por larga que sea, cabe en dos estados.

<!-- b4 -->
Veámoslo funcionar. El torniquete está bloqueado. Llega una moneda: pasa a
desbloqueado. Alguien empuja: pasa, y el torniquete vuelve a bloqueado.
Cada uno de esos saltos es una transición: dado el estado actual y lo que
acaba de llegar, el autómata pasa a un estado nuevo, que a veces es el
mismo. Si empujas estando bloqueado, sigue bloqueado. Si metes otra moneda
estando desbloqueado, sigue desbloqueado. A la situación del autómata en un
instante se le llama configuración, y al paso de una configuración a la
siguiente, movimiento. Y todo autómata arranca en algún sitio: su estado
inicial. El torniquete, cuando lo encienden por la mañana, empieza
bloqueado. Conocer el estado actual equivale a conocer ese punto de
partida más toda la historia de entradas, pero sin tener que guardarla.

<!-- b5 -->
Compliquemos un poco el ejemplo. Una máquina expendedora vende latas a un
euro, y solo acepta monedas de cincuenta céntimos. Aquí el estado ya no es
abierto o cerrado: es cuánto llevas pagado. Cero, o cincuenta céntimos. Con
la segunda moneda, cae la lata y la máquina vuelve a cero. Si la lata
costara dos euros, harían falta cuatro estados. El estado puede contar,
pero solo hasta donde le dejan sus estados. Guarda esta idea, porque dentro
de unos episodios vamos a encontrar una máquina que necesitaría contar sin
límite, y ahí este truco se rompe.

---

# Escena 03 — DefinicionAutomata

**Objetivo:** dar la definición formal (entradas, salidas, estados, función de
transición, función de salida) sobre el torniquete, las dos
representaciones (tabla y diagrama), y abrir la pregunta del capítulo.

**Qué hace falta saber antes:** escena 02.

**Visual:**
- Las cinco piezas aparecen de a una como tarjetas, cada una con su ejemplo
  del torniquete: E = {moneda, empujón}, S = {liberar el paso, nada},
  Q = {bloqueado, desbloqueado}, f y g como dos cajas con dos entradas
  (entrada, estado) y una salida.
- f: se meten "moneda" y "bloqueado", sale "desbloqueado". g: se meten
  "empujón" y "desbloqueado", sale "liberar el paso".
- Una nube de "pasado" intenta entrar en las cajas y no cabe: se tacha.
- Tabla de transiciones 2 × 2 que se llena casilla a casilla; al lado, el
  diagrama de estados; una casilla y su flecha se resaltan a la vez (misma
  información).
- La tarde de seis eventos: cada evento ilumina una casilla de la tabla y la
  flecha correspondiente del diagrama.
- Cierre: una cinta infinita de eventos pasa por el torniquete sin que los
  dos estados cambien de tamaño; aparece la pregunta.

## Narración

<!-- b1 -->
Con esto ya podemos escribir la definición formal, y verás que no tiene
misterio. Un autómata son cinco cosas. Un conjunto de entradas: en el
torniquete, moneda y empujón. Un conjunto de salidas, lo que el autómata
hace hacia fuera: liberar el paso, o nada. Un conjunto de estados:
bloqueado y desbloqueado. Y dos funciones. En general, ese conjunto de
estados podría ser infinito. Cuando es finito, como aquí, hablamos de
autómatas finitos, y son los protagonistas de buena parte de este
capítulo.

<!-- b2 -->
La primera es la función de transición: recibe una entrada y el estado
actual, y devuelve el estado siguiente. Moneda estando bloqueado:
desbloqueado. La segunda es la función de salida: recibe los mismos dos
datos, y devuelve qué hace el autómata. Empujón estando desbloqueado:
libera el paso. Fíjate en lo que no aparece en ninguna de las dos: el
pasado. Todo lo que el autómata necesita está en el par formado por la
entrada y el estado.

<!-- b3 -->
Y hay dos maneras de dibujar todo esto. La tabla de transiciones: una fila
por estado, una columna por entrada, y en cada casilla, a dónde se va. Y el
diagrama de estados: un círculo por estado, y una flecha por transición,
con la entrada escrita encima. Son exactamente la misma información. La
tabla es cómoda para un ordenador; el diagrama, para nosotros.

<!-- b4 -->
Pongamos la tabla a trabajar con una tarde cualquiera. Empujón, moneda,
empujón, moneda, moneda, empujón. Empezamos en bloqueado. El empujón nos
deja en bloqueado. La moneda, desbloqueado. El empujón, bloqueado otra vez,
y alguien ha pasado. Moneda, desbloqueado. La segunda moneda no cambia
nada. Y el último empujón vuelve a bloquear. Seis eventos, seis consultas a
la tabla, y ni una sola vez hemos necesitado mirar hacia atrás.

<!-- b5 -->
Y aquí aparece la gran pregunta que recorre todo este capítulo. Con dos
estados y dos entradas, el torniquete responde bien ante cualquier
secuencia de eventos, por larga que sea: mil monedas y mil empujones, en
cualquier orden. Un puñado finito de estados describe un comportamiento
que no termina nunca. ¿Hasta dónde llega ese truco? ¿Qué puede hacer una
máquina que solo recuerda su estado, y qué cosas no podrá hacer jamás?

---

# Escena 04 — Mealy

**Objetivo:** presentar la máquina de Mealy como el modelo que ya se vio (la
salida depende de entrada y estado), mostrar que la salida vive en la flecha,
y preparar con un ejemplo mínimo el problema de la escena 05.

**Qué hace falta saber antes:** escena 03 (función de salida).

**Visual:**
- El diagrama del torniquete reaparece; las dos funciones de la escena 03 se
  resaltan juntas con el rótulo "Mealy, 1955".
- Las salidas se escriben sobre las flechas con el formato
  "entrada / salida"; una ficha recorre el diagrama y deja caer la salida en
  el instante en que cruza cada flecha (sonido de puerta).
- La expendedora de E1 como Mealy: "moneda / nada" de 0 a 0,50 y
  "moneda / lata" de 0,50 a 0; la lata cae en mitad de la flecha.
- Corte a un diagrama nuevo y abstracto: estados A y B. Flecha A → B con
  "x / 1" y bucle en B con "x / 0". La ficha recorre A → B (sale un 1) y
  luego el bucle (sale un 0): el mismo estado, dos respuestas distintas.

## Narración

<!-- b1 -->
Volvamos al torniquete. En el episodio anterior le dimos dos funciones: una
que decide a qué estado pasa, y otra que decide qué hace hacia fuera. Las
dos miran lo mismo: la entrada que acaba de llegar y el estado en el que
está. Una máquina así, en la que la salida depende de ese par, tiene nombre
propio: es una máquina de Mealy, por George Mealy, que la describió en
1955.

<!-- b2 -->
¿Dónde se dibuja la salida en una máquina de Mealy? En la flecha. Cada
transición lleva escrito lo que la dispara y, después de una barra, lo que
produce. Moneda, barra, nada. Empujón, barra, liberar el paso. La salida no
vive en ningún estado: aparece en el instante en que la máquina cruza de
uno a otro, como el sonido de una puerta al abrirse.

<!-- b3 -->
La expendedora del episodio anterior también es una máquina de Mealy, y
en ella se ve todavía más claro. Con cero céntimos, llega una moneda: pasa
a cincuenta, y no hace nada. Con cincuenta, llega otra moneda: vuelve a
cero, y suelta la lata. La lata no cae por estar en ningún estado. Cae en
el salto: es la salida de esa flecha, y solo de esa.

<!-- b4 -->
Eso tiene una consecuencia curiosa. Imagina una máquina con dos estados, A
y B, y una sola entrada, x. Si estando en A llega una x, pasa a B y
responde uno. Si estando en B llega otra x, se queda en B, pero responde
cero. Es como un timbre que solo suena con el primer cliente del día. Fíjate
bien: la máquina acaba en el mismo estado, y sin embargo ha dicho cosas
distintas. En Mealy, la salida no depende solo de dónde estás, sino de por
dónde has llegado.

---

# Escena 05 — Moore

**Objetivo:** llegar a Moore desde el problema de "no hay entrada": con la
entrada vacía (lambda) la transición no tiene problema, pero la salida de
Mealy queda indeterminada. Moore lo resuelve con una única salida por estado.

**Qué hace falta saber antes:** escena 04 (el ejemplo A, B).

**Visual:**
- El ejemplo A, B de la escena 04. Aparece la letra λ grande con el rótulo
  "no ha llegado nada".
- La ficha está en B y llega λ: se queda en B (bien); sobre B aparece un
  signo de interrogación que oscila entre 1 y 0, con dos flechas fantasma que
  recuerdan desde dónde se llegó.
- Rótulo "Moore, 1956". Las salidas se mudan de las flechas al interior de
  los círculos. B se parte en dos círculos: "B / 1" (se llega desde A) y
  "B / 0" (se llega desde sí mismo). Queda: A → B/1 → B/0, con bucle en B/0.
- Moore "habla" al encenderse (el estado inicial ya muestra su salida);
  Mealy calla hasta la primera entrada.
- Semáforo de tráfico como Moore: tres círculos verde, ámbar y rojo, cada
  uno pintado de su color.

## Narración

<!-- b1 -->
Ahora hagámosle una pregunta incómoda a esa máquina. ¿Qué responde cuando
no llega nada? Porque en un sistema real hay instantes en los que no pasa
nada, y conviene poder decirlo. Agreguemos, entonces, una entrada especial,
la entrada vacía. Se escribe con la letra griega lambda, y significa
exactamente eso: no ha llegado nada.

<!-- b2 -->
Para la función de transición no hay ningún problema: si no llega nada, la
máquina se queda donde está. Pero para la función de salida, sí. La máquina
está en B, y no llega nada. ¿Qué responde? Si a B llegó desde A, lo último
que dijo fue uno. Si llegó desde el propio B, fue cero. Con la entrada
vacía, la respuesta depende de un pasado que el estado no guarda. La salida
queda indeterminada.

<!-- b3 -->
Solo hay una forma de arreglarlo: que cada estado tenga una salida, y solo
una. Así, sepas o no qué acaba de llegar, basta con mirar el estado para
saber qué dice la máquina. Esa es la máquina de Moore, que Edward Moore
publicó un año después, en 1956. Su función de salida ya no mira la
entrada: solo mira el estado. Y eso tiene un efecto curioso: una máquina de
Moore ya dice algo nada más encenderse, porque su estado inicial tiene su
propia salida. Una de Mealy, en cambio, necesita que llegue una primera
entrada para decir cualquier cosa.

<!-- b4 -->
En el dibujo, la salida se muda: sale de las flechas y se escribe dentro de
los círculos. Y en nuestro ejemplo, el estado B tiene que partirse en dos:
un B que dice uno, al que se llega desde A, y un B que dice cero, al que se
llega desde sí mismo. La máquina gana un estado, pero ya nunca duda de qué
responder.

<!-- b5 -->
Hay máquinas que son de Moore por naturaleza. Piensa en un semáforo de
tráfico. Tiene tres estados: verde, ámbar y rojo. Y su salida, el color de
la luz, depende solo del estado en el que está. Da igual qué señal le hizo
cambiar, o cuánto tiempo lleva así: si está en ámbar, luce ámbar. Cuando la
salida describe cómo está el sistema, y no qué acaba de pasar, Moore encaja
como un guante.

---

# Escena 06 — MealyVsMoore

**Objetivo:** comparar las dos (cantidad de estados, velocidad de reacción,
seguridad al interconectar), dejar claro que tienen la misma potencia y
presentar estado accesible, autómata conexo y determinismo, que prepara E3.

**Qué hace falta saber antes:** escenas 04 y 05.

**Visual:**
- Las dos versiones del ejemplo lado a lado: Mealy con 2 estados, Moore con
  3; contador de estados debajo de cada una.
- Un reloj que hace tic: en Mealy la salida cambia en cuanto cambia la
  entrada; en Moore cambia en el tic siguiente (se ve el retraso de un ciclo).
- Dos máquinas de Mealy conectadas en bucle: una señal rebota de una a otra
  cada vez más rápido y se pone roja. Con dos de Moore, el reloj la frena.
- El torniquete en versión Moore (luz verde fija mientras está desbloqueado)
  y en versión Mealy (destello en el instante del empujón).
- Signo "=" gigante entre las dos máquinas: misma potencia. Las dos recetas
  de conversión: salidas que bajan del estado a las flechas que llegan, y un
  estado que se parte en copias.
- La expendedora con un estado "0,75" al que no llega ninguna flecha.
- Un autómata con un estado suelto, sin flechas que lleguen a él: se ilumina
  el camino desde el inicial a los demás, y el suelto se desvanece.
- Un estado con dos flechas con la misma entrada hacia sitios distintos, con
  un signo de interrogación (anticipo de E3).

## Narración

<!-- b1 -->
Entonces, ¿cuál de las dos es mejor? Depende de qué se quiera cuidar.
Mealy suele necesitar menos estados, porque puede apoyar salidas distintas
en las flechas que salen de un mismo estado. Moore necesita un estado por
cada salida distinta, como acabamos de ver. Pero hay una diferencia más
importante, y tiene que ver con el tiempo.

<!-- b2 -->
En los circuitos, estas máquinas avanzan al compás de un reloj. Una máquina
de Mealy reacciona en el mismo ciclo: cambia la entrada, y la salida cambia
al instante. Una de Moore espera: su salida depende del estado, y el estado
solo cambia con el siguiente tic del reloj. Siempre va un ciclo por detrás.
Parece un defecto, y sin embargo es una protección. Piensa en el
torniquete. Si fuera de Moore, la salida viviría en el estado:
desbloqueado significaría "paso libre" durante todo el tiempo que dure. Si
es de Mealy, libera el paso justo en el instante del empujón. Los dos
funcionan. La diferencia es cuándo se entera el mundo.

<!-- b3 -->
Piensa en dos máquinas de Mealy conectadas, donde la salida de cada una es
la entrada de la otra. Un cambio en la primera llega al instante a la
segunda, que responde al instante a la primera, y así, en un bucle que no
espera a ningún reloj. Eso se llama realimentación asíncrona, y es una
fuente de errores muy difícil de rastrear. Con Moore, ese ciclo de retraso
corta el bucle.

<!-- b4 -->
Aun así, que nadie se confunda: las dos son igual de potentes. Todo lo que
hace una, lo puede hacer la otra, y hay recetas para pasar de una a otra.
De Moore a Mealy basta con copiar la salida de cada estado en las flechas
que llegan a él. De Mealy a Moore hay que hacer lo que hicimos con B: partir
cada estado en tantas copias como salidas distintas traigan las flechas que
le llegan. Cambia el dibujo, pero no lo que la máquina es capaz de hacer.

<!-- b5 -->
Antes de seguir, tres palabras que vamos a usar mucho. Un estado es
accesible si alguna secuencia de entradas lleva hasta él desde el estado
inicial. Imagina que alguien le añade a la expendedora un estado de
setenta y cinco céntimos, pero la máquina solo acepta monedas de
cincuenta: nunca se llegará a él. Un autómata es conexo si todos sus
estados son accesibles. Y si no lo es, se pueden borrar los estados a los
que nunca se llega, y el autómata sigue haciendo exactamente lo mismo,
porque nadie los iba a visitar.

<!-- b6 -->
La tercera palabra es determinista: para cada estado y cada entrada, hay un
único destino posible. Todas las máquinas que hemos visto hasta ahora lo
son. Pero pronto vamos a conocer una que no lo es, y que, en vez de elegir
un camino, se atreve con todos.

---

# Escena 07 — CadenasYAlfabetos

**Objetivo:** dar solo lo mínimo para poder hablar de reconocer: símbolo,
alfabeto, cadena, longitud y cadena vacía. Sin definición formal de lenguaje
(va al capítulo 3).

**Qué hace falta saber antes:** E1 (entradas de un autómata), escena 05
(lambda).

**Visual:**
- Fichas con una letra, un dígito, un signo: cada una "encaja" como ladrillo.
- Las fichas caen en una caja con tapa: el alfabeto. Dentro de otra caja,
  las fichas moneda y empujón del torniquete.
- Las fichas se ensartan en un hilo: la cadena; un contador de longitud sube
  con cada ficha. Una tarde del torniquete como hilo largo de monedas y
  empujones.
- Un hilo sin ninguna ficha, con λ encima y longitud 0.
- Dos hilos que se unen (concatenación) y sus longitudes que se suman; un
  abanico de hilos cada vez más largos que no termina.

## Narración

<!-- b1 -->
Hasta ahora nuestras máquinas respondían a eventos: una moneda, un empujón.
Para hablar de ellas con precisión necesitamos un poco de vocabulario, y es
muy poco. La pieza más pequeña es el símbolo. No se define: es el ladrillo
con el que se construye todo lo demás. Puede ser una letra, un dígito, un
signo, o incluso una palabra entera, si la tratamos como una sola pieza.

<!-- b2 -->
Un alfabeto es un conjunto finito, y no vacío, de símbolos. Las letras del
castellano forman un alfabeto. La tabla con la que un ordenador codifica los
caracteres, también. Y las entradas del torniquete, moneda y empujón, eran
un alfabeto de dos símbolos.

<!-- b3 -->
Con los símbolos de un alfabeto se forman cadenas: secuencias finitas de
símbolos, uno detrás de otro. Una tarde entera de uso del torniquete,
moneda, empujón, moneda, empujón, es una cadena. La cantidad de símbolos de
una cadena es su longitud. Y hay una cadena muy especial, la que no tiene
ningún símbolo: la cadena vacía. Se escribe con lambda, la misma letra que
usamos para decir que no llegaba nada, y su longitud es cero.

<!-- b4 -->
Dos cadenas se pueden pegar una detrás de otra: eso es concatenarlas, y la
longitud del resultado es la suma de las dos. Y un detalle que conviene no
perder de vista: cada cadena es finita, pero con un alfabeto de solo dos
símbolos se pueden formar infinitas cadenas distintas. De longitud uno, de
dos, de tres, sin fin. Con estas palabras ya podemos hacerle a un autómata
la pregunta más importante de todas.

---

# Escena 08 — AutomataFinito

**Objetivo:** presentar el autómata finito como reconocedor: no tiene
salidas, solo acepta o rechaza. Estado inicial, estados finales, y el
recorrido completo de una cadena aceptada y de una rechazada. Conectarlo con
la especificación de sistemas reactivos.

**Qué hace falta saber antes:** escena 07.

**Visual:**
- Cinta horizontal con la cadena, cabezal que avanza de izquierda a derecha
  (nunca vuelve); arriba, el control de estados.
- Flecha de inicio que entra "desde ninguna parte" en un estado; doble
  círculo para los finales.
- Ejemplo de la paridad de bes: estados "par" (inicial y final) e "impar";
  bucles con "a" en los dos; flechas con "b" de ida y vuelta.
- Recorrido de a b b a: el estado activo se ilumina con cada símbolo; al
  final, tilde verde. Recorrido de a b a: termina en impar, cruz roja.
- Un sistema reactivo con eventos como símbolos; secuencias aceptadas en
  verde (trazas válidas) y rechazadas en rojo.
- Protocolo del recurso: estados "libre" (inicial y final) y "tomado";
  flechas pedir, usar (bucle) y liberar. La traza "usar" sin pedir no
  encuentra flecha y se pone roja.

## Narración

<!-- b1 -->
Un autómata finito es una máquina que recibe una cadena y responde una sola
cosa: sí o no. Imagínalo así: una cinta con la cadena escrita, un cabezal
que la lee de izquierda a derecha, un símbolo cada vez, sin volver nunca
atrás, y un control que va cambiando de estado con cada símbolo. A
diferencia de Mealy y de Moore, no tiene salidas. Su única respuesta es
aceptar la cadena o rechazarla.

<!-- b2 -->
¿Cómo decide? Uno de sus estados es el inicial, y se marca con una flecha
que entra desde ninguna parte. Algunos de sus estados son finales, y se
dibujan con un círculo doble. La regla es sencilla: cuando se termina la
cadena, si el autómata está en un estado final, la acepta. Si no, la
rechaza. ¿Y por qué se llama finito? Porque tiene una cantidad fija de
estados, decidida de antemano, que no crece por muy larga que sea la
cadena. Esa es su fuerza, y, como veremos, también su límite.

<!-- b3 -->
Probemos con un ejemplo. Queremos aceptar las cadenas de aes y bes que
tienen una cantidad par de bes. Bastan dos estados: par, que es el inicial
y también el final, e impar. Una a no cambia nada, así que cada estado se
la queda con un bucle. Una b cambia la paridad: de par a impar, y de impar
a par.

<!-- b4 -->
Leamos a, b, b, a. Empezamos en par. La a: seguimos en par. La b: impar. La
otra b: par. La última a: par. Se acabó la cadena y estamos en un estado
final: aceptada. Ahora leamos a, b, a. Terminamos en impar: rechazada. Dos
estados bastan para juzgar cadenas de cualquier longitud.

<!-- b5 -->
Y esto tiene un uso directo en concurrencia. Un sistema reactivo se puede
especificar con un autómata cuyas entradas son los eventos. Las secuencias
de eventos que el autómata acepta son las trazas válidas del sistema, y
todo lo demás es un comportamiento prohibido.

<!-- b6 -->
Un ejemplo. Un proceso que usa un recurso compartido tiene que seguir un
protocolo: primero pedirlo, después usarlo tantas veces como quiera, y al
final liberarlo. Dos estados lo capturan. Libre, que es el inicial y el
final: con pedir, pasa a tomado. Tomado: con usar, se queda en tomado; con
liberar, vuelve a libre. La traza pedir, usar, usar, liberar, se acepta. La
traza usar, sin haber pedido, no encuentra ninguna flecha por donde ir: se
rechaza. Con dos círculos acabamos de escribir una regla que un programa
concurrente tiene que cumplir siempre.

---

# Escena 09 — AFND

**Objetivo:** presentar el autómata finito no determinista como el que puede
estar en varios estados a la vez, recorrer un ejemplo completo y conectarlo
con el no determinismo del planificador del capítulo 1.

**Qué hace falta saber antes:** escena 08; Cap. 1 E2 (interleaving y
planificador).

**Visual:**
- Un estado con dos flechas con la misma letra hacia sitios distintos, y
  otro sin ninguna flecha para una letra. Una flecha rotulada λ que salta sin
  leer.
- Ejemplo: estados S (inicial), A y final (doble círculo). S con bucle "a" y
  flecha "a" hacia A; A con bucle "b" y flecha "b" hacia el final.
  **Este mismo dibujo se reutiliza en la escena 10.**
- Recorrido de a a b con fichas de luz: con cada símbolo, cada luz se divide
  en tantas como flechas haya, y se apaga donde no hay flecha. Al final, una
  luz sobre el doble círculo: aceptada.
- Recorrido de b a: la única luz se apaga en el primer símbolo; cruz roja.
- Callback al capítulo 1: el árbol de intercalados del planificador, con
  todas sus ramas iluminadas a la vez.

## Narración

<!-- b1 -->
Ahora rompamos una regla. Hasta aquí, para cada estado y cada símbolo había
un único destino. ¿Y si hubiera dos? ¿O ninguno? Un autómata así se llama
no determinista. Y además puede tener transiciones con lambda: saltos de un
estado a otro sin leer ningún símbolo, como una puerta que se abre sola.

<!-- b2 -->
Veamos uno. Queremos aceptar las cadenas con al menos una a, seguida de al
menos una b. Tres estados: S, que es el inicial, A, y un estado final. Desde
S, con una a, se puede volver a S, o saltar a A. Desde A, con una b, se
puede seguir en A, o ir al final. ¿Y qué hace la máquina cuando tiene dos
caminos? No elige. Los sigue todos a la vez.

<!-- b3 -->
Leamos a, a, b. Empezamos en S. Con la primera a, la máquina está a la vez
en S y en A. Con la segunda a, desde S vuelve a abrirse en S y en A; desde A
no hay ninguna flecha con a, así que ese camino se apaga. Con la b, desde S
no hay salida, y desde A la máquina se abre en A y en el final. Se acabó la
cadena, y uno de los estados activos es final: aceptada. Esa es la regla:
si al terminar alguno de los caminos está en un estado final, la cadena se
acepta.

<!-- b4 -->
Leamos ahora b, a. Desde S no hay ninguna flecha con b: el único camino se
apaga en el primer símbolo. Cuando todos los caminos se apagan, ya no hay
nada que seguir, y la cadena se rechaza. Y si la cadena termina sin que
ningún camino haya llegado a un estado final, también.

<!-- b5 -->
¿Por qué querríamos algo así? Porque los autómatas no deterministas suelen
ser mucho más pequeños y más fáciles de diseñar: describen lo que se busca,
sin preocuparse de cómo encontrarlo. Y porque este no determinismo ya lo
conocemos. Es el mismo del planificador que vimos en el capítulo anterior:
no sabemos qué camino va a tomar la ejecución, así que hay que comprobar
que todos funcionen. Una máquina que no elige un camino, sino que los
recorre todos, piensa exactamente como tiene que pensar quien verifica.

---

# Escena 10 — Subconjuntos

**Objetivo:** mostrar paso a paso la construcción de subconjuntos (de no
determinista a determinista) sobre el ejemplo de la escena 09.

**Qué hace falta saber antes:** escena 09 (el ejemplo S, A, final).

**Visual:**
- El autómata S, A, final de la escena 09, pequeño, a la izquierda.
- A la derecha se construye el determinista con estados-caja que contienen
  los nombres de los originales: {S}, {S, A}, {A, final}.
- Una tabla de subconjuntos (filas: conjuntos; columnas: a, b) que se llena
  casilla a casilla, al ritmo de la voz; cada conjunto nuevo parpadea al
  aparecer. El conjunto vacío se marca como "a ninguna parte".
- La libreta: una hoja con la lista de "sitios posibles" que se tacha y
  reescribe con cada pista.
- Al terminar, {A, final} recibe el doble círculo.
- Recorrido de a a b sobre el determinista: una sola luz, un paso por
  símbolo.

## Narración

<!-- b1 -->
Un autómata que está en varios estados a la vez es cómodo de diseñar, pero
un ordenador real sigue un solo camino. Por suerte, hay un teorema que lo
resuelve: para todo autómata finito no determinista existe uno determinista
que acepta exactamente las mismas cadenas. Y lo mejor es que la
demostración es una receta que se puede seguir a mano.

<!-- b2 -->
La idea es darle la vuelta al problema. Si la máquina no determinista está
en varios estados a la vez, ¿por qué no llamar estado a ese conjunto? Cada
estado del autómata nuevo va a ser un conjunto de estados del viejo: todos
los sitios donde la máquina podría estar. Es como seguir a alguien que ha
podido tomar varias calles: en vez de elegir una al azar, apuntas en una
libreta todas las calles donde podría estar, y actualizas la lista con
cada pista nueva. La libreta, en cada momento, es un único estado. Por eso
la receta se llama construcción de subconjuntos.

<!-- b3 -->
Hagámosla con el ejemplo anterior. El estado inicial es el conjunto que
contiene solo a S. Desde ahí, con una a, el autómata viejo puede ir a S o a
A: el destino es el conjunto S, A. Con una b, no va a ninguna parte. Ahora,
desde S, A. Con una a, S lleva a S y a A, y A no lleva a nada: la unión es,
otra vez, S, A. Con una b, S no lleva a nada, y A lleva a A y al final:
aparece un conjunto nuevo, A y final.

<!-- b4 -->
Desde A y final, con una b se vuelve a A y final, y con una a, a ninguna
parte. Ya no aparecen conjuntos nuevos, así que la construcción termina.
Solo falta decidir cuáles son finales: todos los conjuntos que contengan al
menos un estado final del autómata original. El resultado es un autómata
determinista, con un único camino para cada cadena, que acepta exactamente
lo mismo que el original.

<!-- b5 -->
Comprobémoslo con la misma cadena de antes: a, a, b. Empezamos en S. La a
nos lleva a S, A. La otra a, otra vez a S, A. La b, a A y final, que es un
estado final: aceptada. El mismo veredicto que antes, pero ahora sin
dividirse nunca: una sola luz, un solo camino, un paso por símbolo. Y si el
autómata original tuviera saltos con lambda, la receta solo añade un paso:
a cada conjunto se le suman todos los estados a los que se llega gratis,
sin leer nada.

---

# Escena 11 — PrecioDelDeterminismo

**Objetivo:** mostrar el precio: un no determinista de n estados puede dar un
determinista de hasta 2ⁿ estados. Solo se construyen los alcanzables, y a
veces no explota.

**Qué hace falta saber antes:** escena 10.

**Visual:**
- Tres interruptores (S, A, final), cada uno "dentro" o "fuera": se
  enumeran las 8 combinaciones como casillas; se encienden solo las 3 de la
  escena 10.
- Un contador que duplica: 2, 4, 8… 1.024 (con 10 estados)… 1.048.576 (con 20).
- Dos dibujos del mismo comportamiento: uno pequeño con caminos que se
  bifurcan, otro enorme y rectilíneo; entre ellos, un signo "=".
- Una persona dibuja el autómata pequeño; una herramienta (engranaje) lo
  convierte en el grande.

## Narración

<!-- b1 -->
Pero esta receta tiene un precio escondido. Si los estados del autómata
nuevo son conjuntos de estados del viejo, ¿cuántos conjuntos distintos
puede haber? Cada estado puede estar dentro o fuera del conjunto: dos
posibilidades por estado. Con tres estados, dos por dos por dos: ocho. Con
n estados, dos elevado a n.

<!-- b2 -->
En nuestro ejemplo tuvimos suerte: de los ocho conjuntos posibles solo
aparecieron tres, porque la receta solo construye los conjuntos a los que
de verdad se puede llegar. Pero no siempre hay tanta suerte. Un autómata no
determinista de diez estados puede convertirse en uno determinista de mil
veinticuatro. Con veinte, de más de un millón. Y no es culpa de la
receta: se conocen autómatas no deterministas de unos pocos estados cuyo
equivalente determinista más pequeño necesita, sí o sí, esa cantidad
astronómica. A veces la explosión es inevitable.

<!-- b3 -->
Es una negociación que aparece una y otra vez en informática: una
descripción pequeña, que deja abiertos varios caminos, o una descripción
directa, que no duda nunca, pero que puede ser enorme. Las dos dicen lo
mismo. La diferencia es cuánto espacio hace falta para decirlo.

<!-- b4 -->
Por eso, en la práctica, muchas herramientas se quedan con lo mejor de
cada una: las personas describen lo que buscan con un autómata no
determinista, pequeño y fácil de leer, y es la herramienta la que aplica la
receta de los subconjuntos y construye el determinista, que es el que se
ejecuta. Pero esa misma negociación entre tamaño y claridad, en una
versión mucho más peligrosa, aparece cuando se juntan varios procesos.

---

# Escena 12 — AutomataProducto

**Objetivo:** poner números a la explosión de estados del capítulo 1: el
autómata producto de k procesos de n estados tiene n elevado a k estados, y
sus transiciones son los intercalados. Dejar abierta la pregunta que responde
el capítulo 4.

**Qué hace falta saber antes:** escena 11; Cap. 1 E4 escena 08 (explosión del
modelo) y Cap. 1 E2 (intercalados).

**Visual:**
- Dos autómatas de 3 estados cada uno (fuera, esperando, dentro), en azul y
  en magenta.
- Una rejilla 3 × 3 que se llena con las parejas; flechas horizontales
  (avanza el primero) y verticales (avanza el segundo). Se resalta un camino
  en escalera: es un intercalado.
- La casilla (dentro, dentro) en rojo; una lupa busca un camino desde la
  casilla inicial hasta ella.
- La rejilla se extruye en un cubo con un tercer proceso.
- Contador nᵏ que se dispara: 100, 1.000, 100.000.
- Cierre: una fila de k autómatas pequeños con "k · n" encima frente a la
  rejilla gigante con "nᵏ"; un signo de interrogación.

## Narración

<!-- b1 -->
En el capítulo anterior lo anunciamos sin detenernos: si modelamos cada
proceso con una máquina de estados, el sistema completo explota. Ahora
podemos ver exactamente por qué. Tomemos dos procesos muy sencillos, cada
uno con tres estados: fuera de la sección crítica, esperando para entrar, y
dentro.

<!-- b2 -->
El estado del sistema completo no es el de un proceso ni el del otro: es la
pareja. El primero fuera y el segundo dentro. Los dos esperando. Tres
posibilidades para uno, por tres para el otro: nueve estados. A esta
máquina se le llama autómata producto. ¿Y sus transiciones? Desde cada
pareja, puede avanzar el primer proceso, o puede avanzar el segundo. Esas
flechas son, exactamente, los intercalados que vimos en el capítulo
anterior.

<!-- b3 -->
Y esta rejilla sirve para algo más que asustar. Mira la casilla en la que
los dos procesos están dentro de la sección crítica a la vez. Existe en el
dibujo, porque es una combinación posible. La pregunta de la exclusión
mutua se convierte en una pregunta sobre el autómata: ¿hay algún camino,
desde el estado inicial, que llegue hasta esa casilla? Si no lo hay, el
sistema es seguro. Si lo hay, ese camino es, literalmente, la secuencia de
pasos que provoca el error. Eso es verificar recorriendo el modelo, y
funciona, siempre que el modelo quepa en algún sitio.

<!-- b4 -->
Ahora, hagamos crecer el ejemplo. Con un tercer proceso, la rejilla deja
de ser un cuadrado y se convierte en un cubo, y cada proceso nuevo le añade
una dimensión más. Con k procesos de n estados cada uno, el
autómata producto tiene n elevado a k estados. Dos procesos de diez
estados: cien. Tres: mil. Cinco: cien mil. Y un sistema real no tiene cinco
procesos de diez estados. Esa es, con números, la explosión de estados del
capítulo anterior.

<!-- b5 -->
Por eso la materia usa los autómatas para entender, pero no para modelar
sistemas grandes. Y aquí queda una pregunta en el aire: ¿habrá una forma de
describir los mismos procesos sin dibujar cada combinación? Algo que, en
vez de n elevado a k piezas, necesite solo k por n. La hay, y llegaremos a
ella más adelante en el curso.

---

# Escena 13 — LimiteDeLoFinito

**Objetivo:** pagar el bucle "contar" de la escena 02: el lenguaje de "n aes
seguidas de n bes" no lo reconoce ningún autómata finito, porque habría que
contar sin límite.

**Qué hace falta saber antes:** escena 02 (la expendedora), E3.

**Visual:**
- Callback: la expendedora con su contador de estados.
- Cadenas ab, aabb, aaabbb… cada vez más largas, en fila.
- Un autómata con estados numerados 1, 2, 3… hasta 1.000 que va contando
  aes; llega la a número 1.001 y no queda estado: dos cadenas distintas
  caen en el mismo círculo y se superponen.
- Un rótulo "ningún autómata finito". Paréntesis anidados ((( ))) que se
  emparejan con arcos de colores, cada vez más profundos.

## Narración

<!-- b1 -->
En el primer episodio de este capítulo dejamos una idea guardada: el estado
puede contar, pero solo hasta donde le dejan sus estados. Ha llegado el
momento de usarla. Pensemos en un lenguaje muy sencillo: cadenas con un
bloque de aes seguido de un bloque de bes, con la misma cantidad de cada
una. a, b. a, a, b, b. a, a, a, b, b, b. Y así, sin límite.

<!-- b2 -->
¿Cómo lo reconocería un autómata finito? Mientras lee las aes, tiene que
recordar cuántas lleva, para después exigir las mismas bes. Si lleva tres,
necesita un estado que signifique tres. Si lleva cien, uno que signifique
cien. Pero el autómata tiene una cantidad fija de estados. Supongamos que
tiene mil. Basta con darle una cadena de mil una aes para que se quede sin
números: dos cantidades distintas acaban en el mismo estado, y a partir de
ahí el autómata ya no puede distinguirlas.

<!-- b3 -->
Es la expendedora llevada al extremo: si la lata costara mil euros, harían
falta miles de estados; si el precio no tuviera tope, no bastaría ninguna
cantidad. No es un problema de diseño: ningún autómata finito, por grande
que sea, puede reconocer este lenguaje. Y no es un ejemplo de laboratorio. Es el
mismo problema que comprobar que los paréntesis de una expresión están bien
cerrados: cada paréntesis abierto tiene que encontrar su pareja, y el
anidamiento puede ser tan profundo como se quiera. Una máquina que solo
recuerda su estado no puede hacerlo. Necesita otra cosa: una memoria
auxiliar.

---

# Escena 14 — AutomataDePila

**Objetivo:** presentar el autómata de pila (cinta de solo lectura, en un solo
sentido, más una pila) y animar la traza de a a b b, con sus dos rechazos
típicos.

**Qué hace falta saber antes:** escena 13.

**Visual:**
- Una pila de platos: se pone uno encima, se quita el de arriba; los de abajo
  quedan tapados.
- Cinta arriba, control de estados al centro, pila a la derecha con una
  marca de fondo (Z).
- Traza de a a b b: con cada símbolo, la pila sube o baja; debajo, un
  recuadro con tres casillas (estado, lo que queda por leer, pila) que se
  actualiza en cada paso.
- a a b: al terminar queda una a en la pila, en rojo. a b b: llega la
  segunda b y la pila solo tiene el fondo, cruz roja.
- Dos caminos a la aceptación (estado final / pila vacía) con un "=" entre
  ellos.
- Llamadas a funciones que se apilan como platos (f llama a g, g llama a h)
  y se desapilan al volver; paréntesis y etiquetas que se abren y cierran.

## Narración

<!-- b1 -->
La memoria más sencilla que se le puede dar es una pila. Piensa en una pila
de platos: solo puedes poner un plato encima, o quitar el de arriba. No
puedes mirar los de abajo sin quitar antes los que tienen encima. Poner se
llama apilar; quitar, desapilar. Un autómata de pila es un autómata finito
con una cinta que solo se lee, siempre hacia delante, y además una de estas
pilas. Es una memoria muy limitada: de ella solo se ve el plato de arriba,
y para llegar a uno de abajo hay que perder todos los que tiene encima. Es
poco, sí. Pero es infinitamente más que nada, porque la pila puede crecer
todo lo que haga falta.

<!-- b2 -->
Con eso, el lenguaje de las aes y las bes se resuelve solo. En el fondo de
la pila hay una marca especial, que indica que está vacía. Por cada a que
lee, el autómata apila una a. Cuando empiezan las bes, por cada b desapila
una a. Si al terminar la cadena en la pila solo queda la marca del fondo,
había tantas aes como bes, y la cadena se acepta. La pila hace de contador,
y de un contador sin límite.

<!-- b3 -->
Sigamos a, a, b, b. En cada instante, la situación del autómata se describe
con tres cosas: su estado, lo que le queda por leer y lo que hay en la
pila. Al principio, las cuatro letras por leer y la pila solo con el fondo.
Primera a: apila. Segunda a: apila, ya hay dos. Primera b: desapila, queda
una. Segunda b: desapila, y reaparece el fondo. No queda nada por leer:
aceptada.

<!-- b4 -->
Ahora, dos fallos distintos. Con a, a, b, la cadena se acaba y en la pila
todavía queda una a: sobraban aes. Con a, b, b, la segunda b llega y ya no
hay ninguna a que desapilar: sobraban bes. Por cierto, un autómata de pila
puede aceptar de dos maneras: llegando a un estado final, o dejando la pila
vacía. Se ha demostrado que las dos maneras aceptan exactamente los mismos
lenguajes.

<!-- b5 -->
Y esta memoria no es un invento de pizarra. Cada vez que un programa llama
a una función, apila el punto al que tiene que volver; cuando la función
termina, lo desapila. Por eso una función puede llamar a otra, que llama a
otra, a cualquier profundidad, y todas saben volver. Los paréntesis bien
cerrados, las llamadas anidadas, las etiquetas de una página web: todo lo
que se abre y se cierra en orden inverso es trabajo para una pila.

---

# Escena 15 — PilaNoDeterminista

**Objetivo:** mostrar que con pila el no determinismo sí suma potencia: hay
lenguajes que solo se reconocen adivinando.

**Qué hace falta saber antes:** escenas 09 y 14.

**Visual:**
- Cadenas de ejemplo con tilde o cruz: a b (✓), a b b b (✓), a a b b b b b
  (✓), a b b b b (✗, "demasiadas bes").
- El cabezal sobre una b con un bocadillo "¿cierro esta a o espero otra b?";
  el resto de la cinta tapado con niebla.
- La animación de la pila se triplica en tres carriles (una, dos o tres bes
  por a); dos carriles se apagan y uno llega a la aceptación.
- Rótulo "finito: el no determinismo ahorra estados" frente a "pila: el no
  determinismo suma potencia".
- Palíndromo a b b a: un espejo en el centro de la cadena; el autómata
  prueba el espejo en cada posición y solo una apuesta encaja.

## Narración

<!-- b1 -->
Compliquemos el lenguaje. Ahora, por cada a puede haber una, dos o tres
bes. a, b vale. a, b, b, b, también. Dos aes y cinco bes, también. Pero a
seguida de cuatro bes, no: son demasiadas bes para una sola a. ¿Puede una
pila resolverlo?

<!-- b2 -->
El problema es de tiempo. Cada vez que lee una b, el autómata tiene que
decidir si con ella ya termina de pagar una a, y la desapila, o si esa a
todavía espera otra b. Para saberlo tendría que conocer el resto de la
cadena, y el cabezal solo avanza. Un autómata determinista tiene que
decidir con lo que ha visto hasta ahora, y aquí eso no basta.

<!-- b3 -->
La solución es la que ya conocemos: no elegir. El autómata abre varios
caminos a la vez: para cada a, uno que la empareja con una b, otro con dos,
otro con tres. Los caminos que no encajan se apagan. Si alguno termina con
la cadena leída y la pila vacía, la cadena se acepta.

<!-- b4 -->
Y aquí hay una diferencia importante con los autómatas finitos. Allí, el no
determinismo solo servía para ahorrar estados: siempre se podía convertir
en uno determinista. Con una pila, no: hay lenguajes que solo se pueden
reconocer adivinando.

<!-- b5 -->
El ejemplo más claro son las cadenas que se leen igual al derecho y al
revés, como a, b, b, a. Con una pila, la idea es sencilla: apilar la
primera mitad y compararla, al desapilar, con la segunda. Pero ¿dónde está
la mitad? Leyendo de izquierda a derecha, sin saber cuánto falta, no hay
forma de saberlo. Un autómata no determinista apuesta, en cada posición, a
que la mitad está justo ahí, y basta con que una de sus apuestas acierte.
En general, un autómata de pila es no determinista cuando, con el mismo
símbolo y el mismo plato arriba de la pila, tiene dos movimientos
posibles, o cuando un salto con lambda compite con uno que lee. La memoria
ha sumado potencia. Y ahora, la pregunta obvia: ¿y si la memoria no
tuviera ningún límite?

---

# Escena 16 — MaquinaDeTuring

**Objetivo:** presentar la máquina de Turing como cinta infinita de lectura y
escritura con un cabezal que se mueve en los dos sentidos, animar el ejemplo
de las aes en cantidad potencia de dos, y remarcar que puede no pararse.

**Qué hace falta saber antes:** E5.

**Visual:**
- Rótulo "Alan Turing, 1936" y la pregunta "¿qué significa calcular?".
- Cinta horizontal que sale de cuadro por los dos lados; cabezal triangular
  que lee, escribe y se mueve a izquierda y derecha.
- Comparación con el autómata de pila: la pila de platos se "despliega" en
  una cinta; el candado de "solo lectura" se abre.
- Ejemplo: ocho aes en la cinta; en cada pasada, una sí y otra no se
  convierte en #; contador 8 → 4 → 2 → 1, tilde verde. Seis aes: 6 → 3,
  cruz roja.
- Un cabezal que va y viene sin fin, con un reloj que no se detiene.
- Máquina universal: la descripción de otra máquina escrita en la cinta; la
  universal la lee y la imita. Un mismo aparato con programas distintos.

## Narración

<!-- b1 -->
En 1936, antes de que existieran los ordenadores, el matemático inglés Alan
Turing se hizo una pregunta enorme: ¿qué significa calcular? Para
responderla imaginó una máquina tan sencilla que cabe en una frase: una
cinta infinita, dividida en celdas, y un cabezal que puede leer lo que hay
en una celda, escribir encima, y moverse una celda a la izquierda o a la
derecha.

<!-- b2 -->
Comparada con el autómata de pila, la diferencia es enorme. La cinta ya no
es solo de lectura: se puede escribir. El cabezal ya no avanza en un solo
sentido: puede volver atrás. Y la memoria ya no es una pila donde solo se ve
el plato de arriba: es una cinta entera, sin fin, donde se puede ir a
buscar cualquier cosa. En cada paso, según su estado y el símbolo que lee,
la máquina puede cambiar de estado, reescribir el símbolo y mover el
cabezal.

<!-- b3 -->
Veamos lo que puede hacer. Queremos aceptar las cadenas de aes cuya
cantidad es una potencia de dos: una, dos, cuatro, ocho, dieciséis aes. La
máquina recorre la cinta, y en cada pasada tacha una a sí y otra no: la
mitad de las que quedan. Con ocho aes, quedan cuatro. Otra pasada, dos.
Otra, una. Si en algún momento queda exactamente una, la cadena se acepta.
Si en alguna pasada queda una cantidad impar mayor que uno, se rechaza: con
seis aes, la primera pasada deja tres, y ahí se acaba.

<!-- b4 -->
Una máquina de Turing acepta una cadena si, al procesarla, se para en un
estado final. Y ese "se para" no es un detalle. Todas las máquinas
anteriores se detienen cuando termina la entrada. Una máquina de Turing, en
cambio, puede seguir moviéndose por la cinta para siempre.

<!-- b5 -->
Y Turing dio un paso más, que cambió la historia. Una máquina de Turing se
puede describir con símbolos, y esa descripción se puede escribir en una
cinta. Entonces es posible construir una máquina que lea en su cinta la
descripción de cualquier otra, y la imite paso a paso: la máquina
universal. Una sola máquina capaz de hacer lo que haga cualquier otra, con
solo cambiarle lo que tiene escrito. Es la idea que hay detrás de
cualquier ordenador: el mismo aparato, y un programa distinto para cada
tarea.

---

# Escena 17 — TesisDeChurch

**Objetivo:** la tesis de Church (todo algoritmo es equivalente a una máquina
de Turing) y por qué no es un teorema; el problema de la parada como límite
de la verificación automática; el autómata lineal acotado como escalón
intermedio.

**Qué hace falta saber antes:** escena 16; Cap. 1 (testing contra
verificación).

**Visual:**
- Tres caminos distintos (un cálculo de funciones, funciones recursivas, la
  cinta de Turing) que convergen en un mismo punto con un "=".
- Rótulo "tesis" con "teorema" tachado; la palabra "algoritmo" como una nube
  sin bordes, frente a una definición con bordes nítidos.
- Una máquina con un signo de interrogación: "¿se parará?"; una lupa que
  intenta responder y se queda girando.
- Un modelo finito (la rejilla de la escena 12) recorrido entero, con tilde
  verde, frente al programa infinito con signo de interrogación.
- La cinta de Turing se acorta hasta quedar entre dos marcas, una a cada lado
  de la entrada: el autómata lineal acotado.

## Narración

<!-- b1 -->
Al mismo tiempo que Turing, otros matemáticos buscaban la misma respuesta
por caminos distintos. Alonzo Church lo intentó con un cálculo de
funciones; otros, con funciones definidas por recursión. Y ocurrió algo
sorprendente: todas esas definiciones de calcular, tan diferentes en
apariencia, resultaron ser equivalentes. Todo lo que se podía calcular con
una, se podía calcular con la máquina de Turing, y al revés.

<!-- b2 -->
De ahí nace la tesis de Church: todo algoritmo es equivalente a una máquina
de Turing. Fíjate en la palabra: tesis, no teorema. No se puede demostrar,
porque "algoritmo" es una idea intuitiva, no una definición matemática: no
hay nada formal con qué compararla. Y aun así, después de casi un siglo,
nadie ha encontrado un algoritmo que una máquina de Turing no pueda
ejecutar. Por eso se acepta de forma casi universal.

<!-- b3 -->
Pero esta máquina tan poderosa tiene un límite, y nos toca de cerca. En el
capítulo anterior vimos que verificar es demostrar una propiedad para todas
las ejecuciones posibles. ¿Podría una máquina hacerlo por nosotros, con
cualquier programa? Aquí está la respuesta: una máquina de Turing puede no
pararse nunca, y no existe ningún procedimiento general capaz de decidir,
para cualquier máquina y cualquier entrada, si se va a parar o no. Es el
problema de la parada. Si ni siquiera eso se puede decidir en general,
ninguna herramienta podrá verificar automáticamente cualquier propiedad de
cualquier programa.

<!-- b4 -->
Y aquí está, justamente, el valor de los modelos finitos. Un autómata
finito sí se puede recorrer entero, y sus preguntas sí tienen respuesta:
¿se llega a esta casilla?, ¿se acepta esta traza? Por eso, para verificar
un programa concurrente, no se razona sobre el programa entero, sino sobre
un modelo finito que se queda con lo importante. Es exactamente lo que
anunciamos en el capítulo anterior: construir modelos que se puedan
recorrer por completo.

<!-- b5 -->
Entre el autómata de pila y la máquina de Turing hay un escalón intermedio:
el autómata lineal acotado. Es una máquina de Turing con una restricción:
su cabezal no puede salir del trozo de cinta donde está escrita la entrada.
Dos marcas, una a cada lado, hacen de paredes. Puede leer, escribir y
volver atrás, pero su memoria mide exactamente lo mismo que la entrada.

---

# Escena 18 — JerarquiaPorMemoria

**Objetivo:** responder la pregunta del capítulo: la potencia de cada máquina
es exactamente la memoria auxiliar que tiene. (El cierre de E6, que se
escribe al armar el episodio, abre el capítulo 3.)

**Qué hace falta saber antes:** todo el capítulo.

**Visual:**
- Escalera de cuatro peldaños que se construye de abajo arriba, cada uno con
  el ícono de su memoria: nada (solo un círculo de estado), pila de platos,
  cinta entre dos topes, cinta infinita.
- Los ejemplos del capítulo suben a su peldaño: el torniquete y la paridad
  de bes abajo; aⁿbⁿ en el segundo; las potencias de dos arriba.
- La pregunta de la escena 03 reaparece; se contesta en dos líneas:
  "mucho" (torniquete, expendedora, trazas) y "contar sin límite" (en rojo).
- Un ordenador de sobremesa con un número gigantesco de estados encima;
  sube, igualmente, al último peldaño.

## Narración

<!-- b1 -->
Pongamos ahora todas las máquinas en fila, porque juntas cuentan una
historia. En el primer peldaño, el autómata finito: ninguna memoria más
allá de su estado. En el segundo, el autómata de pila: una pila donde solo
se ve lo de arriba. En el tercero, el autómata lineal acotado: una cinta
tan larga como la entrada. Y en lo más alto, la máquina de Turing: una
cinta sin fin.

<!-- b2 -->
Cada peldaño reconoce todo lo que reconoce el anterior, y algo más. Las aes
seguidas de las mismas bes separan los dos primeros: sin memoria no se
puede, con una pila, sí. Y en cada salto, lo único que ha cambiado es la
memoria auxiliar. La potencia de una máquina es, exactamente, la memoria
que tiene. Y fíjate en la dirección: cada máquina puede imitar a las de
abajo, simplemente ignorando la memoria que le sobra. Ninguna puede imitar
a las de arriba.

<!-- b3 -->
Así que ya podemos responder la pregunta con la que empezamos el capítulo.
¿Qué puede hacer una máquina que solo recuerda su estado? Mucho: controlar
un torniquete, vender una lata, especificar las trazas válidas de un
sistema reactivo, con un puñado de estados y para siempre. ¿Qué no podrá
hacer jamás? Contar sin límite. Para eso necesita memoria, y según cuánta
le demos, subirá uno o varios peldaños.

<!-- b4 -->
Una curiosidad para terminar. El ordenador en el que ves este vídeo, en
sentido estricto, es un autómata finito: su memoria es enorme, pero
finita, así que su número de estados posibles también lo es. Pero es un
número tan descomunal que tratarlo así no sirve de nada, y por eso
razonamos sobre él como si fuera una máquina de Turing. Elegir el modelo
adecuado, ni más potente ni más pobre de lo que hace falta, es la mitad
del trabajo.
