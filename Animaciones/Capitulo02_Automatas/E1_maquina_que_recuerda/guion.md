# Episodio 1 — La máquina que recuerda sin memoria

Capítulo 2 (Autómatas), episodio 1 de 6. Duración objetivo: ~10 min.
Texto tomado de `../guion_base.md` (escenas 01 a 03), más un gancho y un cierre nuevos.
Las 3 escenas del guion base se parten en 9 escenas atómicas, más gancho y cierre.

**Estado: borrador de narración. Todavía sin momentos** (paso 3 del flujo: espera
que el usuario apruebe el texto nuevo).

Texto nuevo, para revisar (marcado con ⭐):
- ⭐ Escena 01 (gancho): texto nuevo.
- ⭐ Escena 11 (cierre): texto nuevo. Anuncia E2, «¿Dónde va la salida?».

Retoques de ajuste al formato por episodios (mínimos):
- Escena 02, b1: «En el capítulo anterior» se mantiene (aquí «capítulo anterior» es
  el de Introducción, y el episodio es el primero del capítulo 2).
- Escena 06, b4 (b4 del guion base, escena 02): se parte en dos bloques para
  sincronizar mejor la animación; el texto no cambia.
- Escena 07: «dentro de unos episodios» se mantiene tal cual; se paga en el E5.

Bucles que este episodio abre (hay que pagarlos más adelante, ver `../guion_base.md`):
- **Pregunta del capítulo** (escena 10): se paga en el E6.
- **«Contar»** con la expendedora (escena 07): se paga en el E5.
- **Salida** (cierre): se paga en el E2.

Pronunciación a probar de oído antes de sintetizar: **Shannon** (escena 05).

---

# Escena 01 — Gancho

**Capítulo YouTube:** ¿Cómo decide sin recordar?
**Objetivo:** enganchar con la paradoja (una máquina que decide bien sin acordarse de
nada) y anunciar el concepto de estado sin nombrarlo todavía.

## Narración

<!-- b1 -->
Un torniquete de metro no sabe cuántas personas han pasado hoy. No sabe quién
pagó primero, ni a qué hora. Y aun así, jamás se equivoca. ¿Cómo decide, entonces,
si no se acuerda de nada?

---

# Escena 02 — Quién marca el ritmo

**Capítulo YouTube:** ¿Quién marca el ritmo?
**Objetivo:** retomar transformacional / reactivo del capítulo 1 y sumar la
familia interactiva con la pregunta «¿quién marca el ritmo?».

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

---

# Escena 03 — Eventos

**Capítulo YouTube:** Qué es un evento
**Objetivo:** definir evento como el aviso que viaja (no el hecho), y terminar en
que un sistema reactivo necesita un modelo: el autómata.

## Narración

<!-- b1 -->
Y eso que le llega al sistema desde fuera tiene nombre: evento. Un botón
que se pulsa, un sensor que se activa, un mensaje que llega por la red. Hay
arquitecturas enteras construidas alrededor de esta idea: unos componentes
emiten eventos, otros los consumen, y quien consume tiene que reaccionar en
cuanto el evento aparece. Fíjate en que un sistema así es concurrente casi
por definición: los eventos llegan cuando quieren, en el orden que quieren,
y a veces varios a la vez.

<!-- b2 -->
Un detalle de vocabulario que parece pequeño, pero no lo es. Cuando
alguien pulsa un botón, el hecho físico ocurre una sola vez, allí, en el
botón. Lo que viaja por el sistema es otra cosa: un mensaje que avisa de
que eso ha ocurrido. A ese aviso es a lo que llamamos evento. El sistema
nunca ve el mundo: ve avisos del mundo, que pueden llegar tarde,
amontonados o en un orden distinto del real. Para razonar sobre algo así,
con garantías, necesitamos un modelo. Y el modelo más sencillo que existe
es el autómata.

---

# Escena 04 — El torniquete

**Capítulo YouTube:** El torniquete
**Objetivo:** presentar el torniquete y ver qué necesita (y qué no) recordar.

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

---

# Escena 05 — El estado

**Capítulo YouTube:** Qué es un estado
**Objetivo:** definir estado como el resumen de la historia que importa para
decidir la próxima respuesta.

## Narración

<!-- b1 -->
Eso es un estado. El estado de un autómata es toda la información que hace
falta, en un momento dado, para saber cómo va a responder a la próxima
entrada. Es un resumen de la historia: el torniquete no guarda el pasado,
guarda solo lo que del pasado importa. Por eso decimos que recuerda sin
memoria. La historia completa, por larga que sea, cabe en dos estados.

---

# Escena 06 — Transiciones

**Capítulo YouTube:** Transiciones y estado inicial
**Objetivo:** ver funcionar el torniquete; transición, configuración, movimiento
y estado inicial.

## Narración

<!-- b1 -->
Veámoslo funcionar. El torniquete está bloqueado. Llega una moneda: pasa a
desbloqueado. Alguien empuja: pasa, y el torniquete vuelve a bloqueado.
Cada uno de esos saltos es una transición: dado el estado actual y lo que
acaba de llegar, el autómata pasa a un estado nuevo, que a veces es el
mismo. Si empujas estando bloqueado, sigue bloqueado. Si metes otra moneda
estando desbloqueado, sigue desbloqueado.

<!-- b2 -->
A la situación del autómata en un instante se le llama configuración, y al
paso de una configuración a la siguiente, movimiento. Y todo autómata arranca
en algún sitio: su estado inicial. El torniquete, cuando lo encienden por la
mañana, empieza bloqueado. Conocer el estado actual equivale a conocer ese
punto de partida más toda la historia de entradas, pero sin tener que
guardarla.

---

# Escena 07 — La expendedora

**Capítulo YouTube:** La máquina expendedora
**Objetivo:** un estado que cuenta, y su límite. Abre el bucle «contar» (se paga
en el E5).

## Narración

<!-- b1 -->
Compliquemos un poco el ejemplo. Una máquina expendedora vende latas a un
euro, y solo acepta monedas de cincuenta céntimos. Aquí el estado ya no es
abierto o cerrado: es cuánto llevas pagado. Cero, o cincuenta céntimos. Con
la segunda moneda, cae la lata y la máquina vuelve a cero. Si la lata
costara dos euros, harían falta cuatro estados. El estado puede contar,
pero solo hasta donde le dejan sus estados. Guarda esta idea, porque dentro
de unos episodios vamos a encontrar una máquina que necesitaría contar sin
límite, y ahí este truco se rompe.

---

# Escena 08 — Las cinco piezas

**Capítulo YouTube:** Definición de autómata
**Objetivo:** dar la definición formal (entradas, salidas, estados, función de
transición, función de salida) sobre el torniquete.

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

---

# Escena 09 — Tabla y diagrama

**Capítulo YouTube:** Tabla y diagrama de estados
**Objetivo:** las dos representaciones (misma información) y una tarde de seis
eventos sin mirar atrás.

## Narración

<!-- b1 -->
Y hay dos maneras de dibujar todo esto. La tabla de transiciones: una fila
por estado, una columna por entrada, y en cada casilla, a dónde se va. Y el
diagrama de estados: un círculo por estado, y una flecha por transición,
con la entrada escrita encima. Son exactamente la misma información. La
tabla es cómoda para un ordenador; el diagrama, para nosotros.

<!-- b2 -->
Pongamos la tabla a trabajar con una tarde cualquiera. Empujón, moneda,
empujón, moneda, moneda, empujón. Empezamos en bloqueado. El empujón nos
deja en bloqueado. La moneda, desbloqueado. El empujón, bloqueado otra vez,
y alguien ha pasado. Moneda, desbloqueado. La segunda moneda no cambia
nada. Y el último empujón vuelve a bloquear. Seis eventos, seis consultas a
la tabla, y ni una sola vez hemos necesitado mirar hacia atrás.

---

# Escena 10 — La pregunta del capítulo

**Capítulo YouTube:** La gran pregunta
**Objetivo:** abrir la pregunta que recorre el capítulo (se responde en el E6).

## Narración

<!-- b1 -->
Y aquí aparece la gran pregunta que recorre todo este capítulo. Con dos
estados y dos entradas, el torniquete responde bien ante cualquier
secuencia de eventos, por larga que sea: mil monedas y mil empujones, en
cualquier orden. Un puñado finito de estados describe un comportamiento
que no termina nunca. ¿Hasta dónde llega ese truco? ¿Qué puede hacer una
máquina que solo recuerda su estado, y qué cosas no podrá hacer jamás?

---

# Escena 11 — Cierre

**Capítulo YouTube:** Lo que viene

## Narración

<!-- b1 -->
Resumamos. Un estado es un resumen de toda la historia: guarda solo lo que del
pasado importa. Con un puñado de estados, dos funciones y una tabla, una máquina
responde a secuencias que no terminan nunca. Pero nos ha quedado una pieza por
explorar: la salida. La del torniquete depende de la entrada y del estado. ¿Y si
dependiera solo del estado? Dos ingenieros, con un año de diferencia, respondieron
de dos maneras distintas. Y las dos tienen razón. En el próximo episodio:
¿dónde va la salida?
