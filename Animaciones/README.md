# Animaciones — videoclases de Programación Concurrente

Videoclases narradas y animadas para YouTube, hechas con **Manim** +
**manim-voiceover** + una voz neuronal gratuita (**edge-tts**,
`es-ES-XimenaNeural`). Cada video es un **episodio de ~10 minutos** con
dinámica de retención alta, al estilo de TikTok y YouTube.

> **Si eres un agente nuevo:** este archivo es la fuente de verdad. Con él y
> el `guion.md` del episodio en curso alcanza para seguir trabajando. Lee
> §1 (estado), §2 (flujo) y §3–§4 (pautas) antes de tocar nada.

---

## Índice

0. [Reglas de oro](#0-reglas-de-oro)
1. [Estado del proyecto](#1-estado-del-proyecto)
2. [Flujo de trabajo de un episodio](#2-flujo-de-trabajo-de-un-episodio)
3. [Pautas de contenido](#3-pautas-de-contenido)
4. [Pautas visuales y de ritmo](#4-pautas-visuales-y-de-ritmo)
5. [Formato del guion.md](#5-formato-del-guionmd)
6. [Arquitectura del código](#6-arquitectura-del-código)
7. [Cómo se escribe una escena (API)](#7-cómo-se-escribe-una-escena-api)
8. [Herramientas: render, ritmo y controles](#8-herramientas-render-ritmo-y-controles)
9. [Voz y pronunciación](#9-voz-y-pronunciación)
10. [Entorno y trampas conocidas](#10-entorno-y-trampas-conocidas)
11. [Checklist antes de entregar](#11-checklist-antes-de-entregar)
12. [Cómo trabajar con el usuario](#12-cómo-trabajar-con-el-usuario)

---

## 0. Reglas de oro

1. **Cada cosa que dice la voz se ve.** Una animación anclada a la frase cada ~2–3 s.
2. **La imagen llega con la voz, nunca detrás.** Las animaciones son cortas (0,3–1,5 s)
   y arrancan 0,3 s antes de la palabra (lo hace `en()` solo).
3. **Nada de texto de relleno.** En pantalla solo hay términos clave, números o preguntas.
4. **Castellano de España** en todo lo que se narra (la voz es española).
5. **El texto nuevo de narración lo aprueba el usuario** antes de animar.
6. **La pronunciación se elige de oído**: nunca inventar grafías fonéticas sin que el usuario las escuche.
7. **Antes de entregar**: `ritmo.py` sin huecos grandes, render sin avisos y
   hojas de contacto revisadas (§11).

---

## 1. Estado del proyecto

**Orden de los capítulos** (es el orden de la materia; no cambiarlo):

| Cap. | Tema | Carpeta | Estado |
|---|---|---|---|
| 1 | Introducción a la concurrencia | `Capitulo01_Introduccion/` | En proceso (tabla de abajo) |
| 2 | Autómatas | `Capitulo02_Automatas/` | `guion_base.md` con la narración de todo el capítulo, esperando la aprobación del usuario |
| 3 | Lenguajes y gramáticas | *(crear)* | Pendiente. Incluye la jerarquía de Chomsky y las conversiones gramática ↔ autómata y expresión regular ↔ autómata |
| 4 | Redes de Petri | *(crear)* | Pendiente. Paga la pregunta de los "cincuenta lugares" del cierre de Cap. 1 E4 |

Cada capítulo se arma igual: primero un `guion_base.md` con la narración de
todo el capítulo (que el usuario aprueba **episodio por episodio**), y después
cada episodio sigue el flujo de §2.

### Capítulo 1 (Introducción)

En 4 episodios. Toda la narración del capítulo
está aprobada en `Capitulo01_Introduccion/guion_base.md`. Ese archivo es la
fuente de texto, no se renderiza: tiene formato viejo, sin momentos.

| Episodio | Carpeta | Escenas de `guion_base.md` | Estado |
|---|---|---|---|
| E1 · El error que ningún test encontró | `E1_el_error_invisible/` | 01 a 04 (+ gancho y cierre nuevos) | **Terminado y subido a YouTube** (1080p60, 9:56, 0 avisos). Renders borrados del disco: se regenera con `render.py --calidad qh` |
| E2 · La ilusión de hacerlo todo a la vez | `E2_la_ilusion/` | 05 Multiprogramación, 06 Hilos, 07 Concurrencia vs paralelismo, 08 Interleaving (+ gancho y cierre nuevos) | **Terminado y subido a YouTube** (1080p60, 9:09). Renders borrados: se regenera con `render.py --calidad qh` |
| E3 · Uno más uno igual a uno | `E3_uno_mas_uno/` | 09 Condición de carrera, 10 Sección crítica, 11 Problemas clásicos (+ gancho, cierre y bloque «rebobinemos» nuevos) | **Terminado y subido a YouTube** (1080p60, 8:20, 0 avisos). Renders borrados: se regenera con `render.py --calidad qh`. Incluye 3 pausas pedidas por el usuario (bloques partidos en 08, 09 y 10) |
| E4 · Demostrar que nunca falla | `E4_nunca_falla/` | 12 Interacción y mensajes, 13 Corrección y modelado, 14 Puente a Petri (+ gancho y cierre nuevos) | Borrador 480p armado. Falta el visto bueno del usuario (paso 9). El cierre anuncia el capítulo 2 |

- El cierre de E1 anuncia E2 con el título "La ilusión de hacerlo todo a la vez".
  Cada episodio tiene que anunciar el siguiente de la misma forma, **también
  entre capítulos**: el cierre de Cap. 1 E4 anuncia "La máquina que recuerda sin memoria".
- La escena 07 de E1 anuncia que la condición de carrera llega en el episodio 3.
- Fuentes de contenido: `../Apuntes/PROGRAMACION CONCURRENTE Y PARALELA.md` (el
  resumen de la materia) y `../Filmimas/` (diapositivas de clase).

### Capítulo 2 (Autómatas)

En 6 episodios. `Capitulo02_Automatas/guion_base.md` tiene la narración de
las 18 escenas (con objetivo, qué hace falta saber antes y visual), el
reparto en episodios, los bucles que cruzan el capítulo y los recortes: es
todo lo que necesita quien arme un episodio, junto con este README. Fuentes: apuntes, sección
"Gramáticas, Lenguajes y Autómatas" **sin** la parte de gramáticas ni
expresiones regulares (van al capítulo 3), y `../Filmimas/Clase 2` y `Clase 3`
(*Lenguaje regular con notas*).

| Episodio | Escenas de `guion_base.md` | Estado |
|---|---|---|
| E1 · La máquina que recuerda sin memoria | 01 Sistemas reactivos, 02 La idea de estado, 03 Definición de autómata | `E1_maquina_que_recuerda/guion.md` con narración (9 escenas + gancho y cierre), sin momentos. Falta el visto bueno del usuario al gancho y al cierre (paso 3) |
| E2 · ¿Dónde va la salida? | 04 Mealy, 05 Moore, 06 Mealy contra Moore | Narración en `guion_base.md`. Falta crear la carpeta del episodio |
| E3 · La máquina que dice sí o no | 07 Cadenas y alfabetos, 08 Autómata finito, 09 AFND | Narración en `guion_base.md`. Falta crear la carpeta del episodio |
| E4 · De muchos caminos a uno | 10 Subconjuntos, 11 El precio del determinismo, 12 Autómata producto | Narración en `guion_base.md`. Falta crear la carpeta del episodio |
| E5 · Darle memoria a la máquina | 13 El límite de lo finito, 14 Autómata de pila, 15 Pila no determinista | Narración en `guion_base.md`. Falta crear la carpeta del episodio |
| E6 · La máquina que puede calcularlo todo | 16 Máquina de Turing, 17 Tesis de Church, 18 La jerarquía por memoria | Narración en `guion_base.md`. Falta crear la carpeta del episodio |

- La narración base de cada episodio ronda las 1.050–1.150 palabras: con
  gancho y cierre (~150–200 más) queda en ~1.300, como E2 y E3 del capítulo 1.
- Como autómatas va **antes** que lenguajes, este capítulo presenta solo lo
  mínimo (símbolo, alfabeto, cadena) cuando hace falta. Lenguaje formal,
  gramáticas, Chomsky y expresiones regulares quedan para el capítulo 3.
- El cierre de E6 anuncia el capítulo 3: cada máquina reconoce una familia de
  lenguajes, ¿quién escribe esos lenguajes? → gramáticas.
- Piezas nuevas que hacen falta antes de animar (van a `figuras.py` e
  `iconos.py`): diagrama de estados (estado, estado final con doble círculo,
  flecha de inicio, arco con etiqueta, bucle), tabla de transiciones con
  resaltado, cinta con cabezal, pila animable, torniquete, máquina expendedora.
- Pronunciaciones a probar de oído antes de sintetizar: Mealy, Moore, Shannon,
  Turing, Church.

---

## 2. Flujo de trabajo de un episodio

Hay dos puntos donde se frena a esperar al usuario (🟡).

1. **Crear la carpeta** `Capitulo01_Introduccion/E<N>_<nombre_corto>/`.
2. **Armar el `guion.md`** (formato en §5):
   - copiar la narración de las escenas de `guion_base.md` que le tocan (§1);
   - partir en **escenas atómicas** de 30–90 s (en E1, 4 escenas del guion base
     se volvieron 9, más gancho y cierre);
   - escribir un **gancho** de 5–15 s (escena 01) y un **cierre** que anuncie el
     episodio siguiente (última escena);
   - ajustar las frases que ya no aplican al formato por episodios (por ejemplo,
     "en la próxima escena" → "más adelante");
   - poner `**Capítulo YouTube:**` en cada escena.
3. 🟡 **El usuario revisa el texto nuevo** (gancho, cierre y retoques). Mostrar solo eso, marcado.
4. **Escribir los momentos** de cada bloque: uno cada ~2–3 s de voz, describiendo qué se ve.
5. **Validar el guion y medir el ritmo:**
   `python ritmo.py Capitulo01_Introduccion/E<N>_...` (§8). Agregar momentos
   hasta que ningún tramo pase de ~4 s.
6. **Escribir las escenas** `sNN_<nombre>.py` (§7). Reutilizar `iconos.py` y
   `figuras.py`, y sumar ahí los íconos o piezas nuevos (no dentro de la escena).
7. **Renderizar el borrador** (480p15): `python render.py <carpeta>`. Arreglar
   todos los **avisos de los controles** (§8).
8. **Revisar a ojo** con hojas de contacto (§8): huecos, tamaños, cosas que no
   se entienden, rellenos macizos. Arreglar y volver a renderizar solo esas escenas
   (`--escenas 03,07`) y después `--solo-unir`.
9. 🟡 **Mandar el mp4 al usuario** y esperar su visto bueno o sus comentarios.
10. **Render final:** `python render.py <carpeta> --calidad qh`. Entregar el mp4,
    el `.srt` y `capitulos.txt` de `salida/`.

---

## 3. Pautas de contenido

### Tono
- **Narrado como historia**, no como lista de definiciones: se arranca de un
  caso, una pregunta o una imagen concreta y desde ahí se llega al concepto.
  Metáforas cotidianas: programa = receta, proceso = tarta horneándose, hilos =
  compañeros de casa, planificador = director de orquesta.
- **No dar nada por sabido**: cada término se explica la primera vez que aparece.
- **Solo teoría**: sin código Java ni ejercicios prácticos.
- Se habla en primera persona del plural ("veamos", "imaginemos") y de tú
  cuando se interpela ("puedes correr el programa mil veces").

### Castellano de España (obligatorio en lo narrado)
| No | Sí |
|---|---|
| voseo: podés, fijate, tenés | tuteo: puedes, fíjate, tienes |
| acá | aquí |
| computadora (¡ojo al género!: *un* ordenador, ordenador*es distintos*) | ordenador |
| chico/chiquito (= pequeño) | pequeño |
| torta | tarta |
| video | vídeo |
| "o ordenadores" | "u ordenadores" (o → u antes de o-) |

Al reemplazar palabras, **revisar la concordancia** de artículos y adjetivos.

### Estructura de un episodio (~10 min, ~1.400–1.500 palabras)
1. **Gancho** (5–15 s): lo más fuerte del episodio. Después, el título solo (≤2 s).
2. Escenas atómicas, cada una con su capítulo de YouTube.
3. **Bucles abiertos**: anunciar lo que viene y pagarlo después.
4. **Micro-recompensa** al final de cada escena: una idea en una línea.
5. **Cierre**: resumen corto + la "trampa" o pregunta que abre el episodio siguiente.

---

## 4. Pautas visuales y de ritmo

### Retención
1. **Cambio visual cada 2–4 s**: algo aparece, se mueve, hace zoom o cambia de color.
2. **Cada frase se ve**: si la voz dice "seis pacientes", aparecen seis pacientes.
3. **A veces manda la animación**: el dibujo va primero y la voz lo explica.
4. **Tipografía cinética**: la palabra clave aparece de golpe (`pop`) cuando se dice.
5. **Un plano, una idea**: nada de título + subtítulo apilados ni rótulos de relleno.
6. **Cortes secos** (`corte()`) para cambiar de idea; fundidos (`limpiar()`) al cerrar una escena.
7. **Sonido**: efectos cortos en los momentos clave, siempre por debajo de la voz.
8. **Tensión y continuidad**: retomar elementos de escenas anteriores (en E1,
   la máquina de Therac vuelve y se abre para mostrar el chip).

### Ritmo técnico
- Animaciones de **0,3–1,5 s**. **Nunca** estirar una animación a `t.duration`
  para "llenar": se ancla y después queda quieta o con `flotar()`. Excepción:
  movimientos continuos (zoom lento, contadores), que pueden durar `self.margen()`.
- **Densidad**: un momento cada ~2–3 s de voz. Se acepta hasta ~4 s si adentro
  corre una animación de ≥1 s.
- La voz va a ~150 palabras/min (con `VELOCIDAD = "-5%"`). Pasarse un poco de 10 min está bien.
- ~1 s de respiro al final de cada escena (`self.wait(1.0)` antes de `limpiar()`).

### Estética
- Fondo `#12141a`. Paleta en `comun/estilo.py`:
  - `ROJO` = error o peligro; `VERDE` = correcto;
  - `AMBAR` = el concepto que se está presentando, palabras clave y el PC;
  - `AZUL`/`CIAN`/`MAGENTA` = entidades (procesos, hilos, CPU);
  - `INK` = secundario; `TXT` = texto principal.
- Tipografía **Segoe UI** (sans), y **Consolas** para código.
- **Tamaños**: el elemento principal ocupa el **40–70 % del alto** (la pantalla
  mide 14,2 × 8 unidades). En el borrador de 480p lo chico no se ve.
- Íconos planos y geométricos, propios (`comun/iconos.py`). **No se descargan imágenes.**
- Números en formato español: `1000` o `1.000`, `0,34` (nunca `1,000` ni `0.34`).

---

## 5. Formato del `guion.md`

```markdown
# Episodio 1 — El error que ningún test encontró
(notas libres: qué escenas del guion base cubre, qué texto es nuevo)

---

# Escena 03 — Therac

**Capítulo YouTube:** Therac-25
**Objetivo:** (opcional) qué tiene que entender quien mira

## Narración

<!-- b1 -->
Corría 1985. Un equipo de radioterapia llamado Therac 25 empezaba a
tratar pacientes de cáncer...

- «Corría 1985» → `linea`: se dibuja la línea de tiempo abajo y aparece 1985
- «Un equipo de radioterapia» → `maquina`: la máquina entra desde la derecha
- «Therac 25» → `nombre`: aparece el nombre «Therac-25» sobre la máquina

<!-- b2 -->
...
```

Reglas (`comun/guion.py` las valida y falla con un mensaje claro si no se cumplen):
- `# Escena NN — NombreClase`: `NN` es el orden en el video y el archivo
  `sNN_*.py`; `NombreClase` es la clase de Manim dentro de ese archivo.
- `**Capítulo YouTube:**` es el título de esa marca en `capitulos.txt`.
- Bloques `<!-- b1 -->`, `<!-- b2 -->`…: cada uno es una sola síntesis de voz.
  Conviene partir en bloques de 10–30 s.
- **Momentos**: `- «frase» → \`marca\`: descripción`
  - la frase aparece **literal** en la narración del bloque (con tildes y signos,
    ej. «¿qué hacemos?»);
  - los momentos van **en el orden** en que se dicen; se busca desde el momento
    anterior (cuidado con palabras repetidas);
  - la marca es `[a-z0-9_]` y no se repite dentro del bloque;
  - la descripción es para humanos: dice qué se ve (sirve de especificación de la escena).
- Las líneas de momentos no se leen en voz alta. `---` separa escenas.

---

## 6. Arquitectura del código

```
Animaciones/
├── README.md, CLAUDE.md    documentación (CLAUDE.md solo apunta aquí)
├── render.py               renderiza un episodio: escenas → mp4 + srt + capitulos.txt
├── ritmo.py                linter de ritmo: cuándo cae cada momento y qué huecos quedan
├── comun/                  código compartido por todas las escenas
│   ├── escena.py           EscenaCurso: clase base (voz, marcas, sonido, cámara, controles)
│   ├── guion.py            parser del guion.md (narración + momentos → bookmarks)
│   ├── voz.py              servicio edge-tts para manim-voiceover + VOZ y VELOCIDAD del curso
│   ├── pronunciacion.py    reemplazos que solo afectan al audio
│   ├── sfx.py              efectos de sonido sintetizados con numpy
│   ├── estilo.py           paleta, fuente, caja(), flecha(), ejes()
│   ├── iconos.py           pictogramas (personas, máquinas, objetos, marcas)
│   └── figuras.py          piezas técnicas (CPU, memoria, PCB, línea de tiempo, redes de Petri)
├── sfx/                    wav generados por sfx.py (se regeneran solos; ignorado en git)
├── musica/                 opcional: una pista de fondo (YouTube Audio Library); render.py la mezcla
├── voces_prueba/           scripts para elegir voz y pronunciaciones de oído
├── Capitulo01_Introduccion/
│   ├── guion_base.md       narración aprobada de todo el capítulo (fuente de texto)
│   └── E1_el_error_invisible/
│       ├── guion.md        guion del episodio (orden, narración y momentos)
│       ├── s01_gancho.py … s11_cierre.py   una escena por archivo
│       ├── media/          salida de Manim, caché de voz, informes (ignorado en git)
│       └── salida/         episodio final: mp4, srt, capitulos.txt (ignorado en git)
└── Capitulo02_Automatas/
    └── guion_base.md       plan y narración del capítulo 2 (los episodios E<N>_*/ se crean al arrancar cada uno)
```

**Cómo fluye todo:** `render.py` lee el orden de `guion.md` → corre Manim
escena por escena. Cada escena es una `EscenaCurso`, que carga su parte del
guion (`guion.py`), inserta las marcas (`<bookmark/>`) y aplica la
pronunciación. Después sintetiza cada bloque con edge-tts (`voz.py`, con caché
y tiempos por palabra), anima esperando cada marca con `en()` y, al terminar,
escribe un informe de controles. Al final, `render.py` concatena, une los
subtítulos, genera los capítulos y resume los informes.

---

## 7. Cómo se escribe una escena (API)

```python
"""Escena 03 — Therac-25: la historia y su causa."""
from comun.escena import *          # manim + EscenaCurso + paleta + iconos + figuras


class Therac(EscenaCurso):
    ESCENA = "03"                    # número en el guion.md de esta carpeta

    def construct(self):
        self.historia()              # un método por bloque, si la escena es larga
        self.wait(1.0)
        self.limpiar()

    def historia(self):
        with self.narrar("b1"):                      # voz del bloque b1
            self.en("linea")                         # espera a «Corría 1985» (−0,3 s)
            lt = LineaTiempo().move_to(DOWN * 3.3)
            self.play(Create(lt), run_time=0.5)
            self.pop(lt.marca(1985, ROJO))           # entrada con rebote + sonido

            self.en("maquina")
            maq = maquina_radioterapia().scale(4.2)
            self.sfx("whoosh")
            self.play(FadeIn(maq, shift=LEFT * 3), run_time=0.6)
        # al salir del with, espera a que termine el audio del bloque
```

### `EscenaCurso` (`comun/escena.py`)
| Método | Para qué |
|---|---|
| `narrar(bloque)` | context manager: reproduce la voz del bloque con sus marcas |
| `en(marca)` | espera hasta la frase anclada (0,3 s antes: `ANTICIPO`) y anota si llegó tarde |
| `margen()` | segundos disponibles hasta la próxima marca (para dimensionar animaciones continuas) |
| `sfx(nombre, gain=-14, offset=0)` | efecto de sonido ahora (o `offset` s después) |
| `pop(*mobs, sonido="pop")` | entrada con rebote (tipografía cinética) |
| `palabra(txt, color, escala)` | palabra clave en negrita, lista para `pop` |
| `texto_centrado(*lineas)` | varias líneas centradas entre sí |
| `resaltar(*mobs, color)` | resalta agrandando y coloreando **solo el borde** (usar en vez de `Indicate`) |
| `flotar(m)` | movimiento sutil mientras sigue la voz (llamar `m.clear_updaters()` antes de moverlo) |
| `rafaga(teclas)` | teclas que se encienden en ráfaga, con tecleo |
| `corte(*mantener)` | corte seco a plano nuevo (vacía la escena) |
| `limpiar()` | fundido de todo |
| `fuera(*mobs)` | salida rápida |
| `zoom(obj, ancho)` / `zoom_reset()` | cámara (`self.camera.frame` también se puede animar directo) |

### Íconos (`comun/iconos.py`)
Devuelven un `VGroup` de ~1 unidad de alto (se agrandan con `.scale()`). Las
partes animables quedan como atributos.
- **Personas**: `persona`, `ingeniero` (con casco), `paciente_acostado`, `burbuja_pensamiento(contenido)`.
- **Máquinas y objetos**: `maquina_radioterapia` (`.haz`, `.camilla`), `teclado` (`.teclas`),
  `monitor_signos`, `avion`, `cohete` (`.llama`), `misil`, `banco`, `reloj` (`.minutero`, `.aro`),
  `chip(color, texto)` (`.cuerpo`), `boton`, `documento`, `receta`, `tarta`, `horno(contenido)`
  (`.ventana`), `disco`, `cruz_hospital`, `lupa` (`.vidrio`), `ventana_app(titulo, color, contenido)`
  (`.marco`), `nota_musical`, `triangulo_play`.
- **Episodio 2**: `ordenador` (`.pantalla`), `ventanas_multitarea()` (música, navegador, vídeo),
  `batuta`, `casa(ancho, alto)` (`.muros`, `.tejado`), `llave`, `escudo`, `candado(abierto=)`,
  `barrera` (`.brazo`, para girarlo), `burbuja_dialogo(ancho, alto)` (`.globo`), `ojo`.
- **Episodio 3**: `dado` (`.cara`, `.puntos`), `bicho` (un «bug»), `semaforo` (`.luces`), `bandera` (de meta).
- **Episodio 4**: `sobre` (un mensaje: `.cuerpo`, `.solapa`), `engranaje` (girarlo con `Rotate`), `matraz`
  (`.liquido`), `cajero` (automático: `.pantalla`), `sello(texto, color)` (sello inclinado: «demostrado», «modelo»).
- **Marcas**: `tilde`, `cruz`, `tachar(m)` (línea que tacha).

### Piezas técnicas (`comun/figuras.py`)
- `cpu()` (`.caja`, `.titulo`), `registro(nombre, valor)` (`.caja`, `.valor`),
  `celda_memoria(dir, contenido)` (`.caja`, `.contenido`), `memoria(filas)`, `puntero_pc(objetivo)`.
- `LineaTiempo(desde, hasta)` con `.punto(anio)` y `.marca(anio, color)`.
- `caja_proceso(nombre, color)`, `tabla_pcb(campos)`, `pila_de_hilo()`.
- **Episodio 2**: `nucleo(color, lado, texto)` (`.caja`), `ficha_accion("A1", color)` (acción atómica),
  `mini_estados()`, `intercalaciones(a, b)` y `ArbolIntercalado(n, m)`: árbol de todas las
  intercalaciones de dos procesos (`.aristas[prefijo]`, `.nodos[prefijo]`, `.hojas`, `.raiz`, `.camino(hoja)`).
- **Episodio 3**: `hilo(nombre, color)` (`.caja`, `.registro`); `montaje_contador(valor)` (celda
  `contador` + H1 + H2: `.celda`, `.h1`, `.h2`); `paso_linea` y `fila_pasos([(txt, color)...], y)`
  (línea de pasos «H1 lee 0»…); `SalaPuerta(ancho, alto)` (sección crítica vista desde arriba:
  `.puerta`, `.piso`, `.dentro`, `.fuera`, `abrir()`, `cerrar()`); `tarjeta(frente)` (carta, boca
  abajo con `None`) y `voltear(escena, carta, nueva)`; `grilla_ejecuciones(n, malas)` (`.celdas`,
  `.malas`); `pista(ancho, carriles)` (`.carril(i)`, `.meta`); `reglas_seis(lado)` (las 6 reglas
  de la sección crítica: `[i].caja`, `[i].num`); `proceso_mini(nombre, color)`.
- **Redes de Petri** (E4 y el capítulo 4): `Lugar(etiqueta, fichas, radio, color, color_fichas)`
  (`.circulo`, `.puntos`, `.etiqueta`; `poner_fichas(n)` al instante, `ficha_suelta()` para hacerla viajar;
  las fichas se dibujan a escala del círculo, así que el lugar se puede agrandar con `.scale()`),
  `Transicion(etiqueta)` (`.barra`), `arco_pn(origen, destino, peso)`,
  `disparar(escena, transicion, entradas, salidas)` (fichas que aparecen y desaparecen).
  Episodio 4: `disparar_viaje(escena, t, entradas, salidas, run_time, rutas)` (las fichas **viajan** por
  los arcos; `rutas` para caminos con esquinas), `soltar_ficha(escena, lugar)` (cae con rebote) y
  `RedMutex(fichas)`: la red de exclusión mutua armada (`.f1`, `.sc1`, `.f2`, `.sc2`, `.mutex`, `.e1`,
  `.s1`, `.e2`, `.s2`, `.arcos[("f1", "e1")]`, `.lugares`, `.transiciones`, `.flechas`;
  `entrar(escena, i)` / `salir(escena, i)`). Ojo: `Lugar("")` usa `Text("")`; dar siempre una etiqueta.
- **Episodio 4, otras**: `escalera(n, ancho, alto)` (`.escalones`, `.pisada(i)`), `anillo_estados(n, radio,
  color, barras=)` (máquina en anillo; con `barras=True`, el mismo ciclo como red de Petri),
  `maquina_estados_mini(color, lado, radio)` (tres estados en triángulo), `tabla_bloqueo()` (2×2 de
  enviar/recibir: `.celda(i, j)`).
- En `estilo.py`: `caja(txt, color, ancho, alto)` (rectángulo con texto), `flecha()`, `ejes()`.

### Efectos de sonido (`comun/sfx.py`)
`pop`, `whoosh`, `tick`, `tecla`, `impacto`, `alarma`, `exito`, `error`, `latido`.
Se sintetizan con numpy; para agregar uno se escribe una función y se registra en `EFECTOS`.

### Convenciones
- Un método por bloque en escenas largas. Comentar con la frase anclada (`# «Corría 1985»`).
- Íconos o piezas que podrían reutilizarse → a `iconos.py` o `figuras.py`, no dentro de la escena.
- Lo que sale de cuadro a propósito: `m.permitir_fuera = True` (en cada texto de la familia).
- Lo que pasa por encima de texto a propósito (lupa, foco de luz): `m.permitir_encima = True`.

---

## 8. Herramientas: render, ritmo y controles

Todos los comandos se corren desde `Animaciones/` con el Python correcto (§10).

### `ritmo.py`: densidad de momentos, antes de animar
```powershell
python ritmo.py Capitulo01_Introduccion/E1_el_error_invisible [--max 3.0] [--escenas 03,04]
```
Sintetiza la voz (o la toma del caché) y lista en qué segundo cae cada momento,
y los tramos de más de `--max` s sin ninguno. También sirve para validar el guion.

### `render.py`: el episodio
```powershell
python render.py <carpeta_episodio>                    # borrador 480p15, todo el episodio
python render.py <carpeta_episodio> --escenas 03,07    # solo esas escenas
python render.py <carpeta_episodio> --solo-unir        # concatena lo ya renderizado
python render.py <carpeta_episodio> --calidad qh       # final 1080p60
```
Deja en `<carpeta>/salida/`: `<carpeta>-<res>.mp4`, el `.srt` unido y
`capitulos.txt` para la descripción de YouTube. Si hay música en `musica/`,
genera además `-musica.mp4` con la pista mezclada a ~−23 dB (`--sin-musica` para evitarlo).
El detalle de Manim queda en `<carpeta>/render.log`.

### Controles automáticos (en cada render)
`EscenaCurso` revisa todo mientras renderiza y `render.py` imprime un resumen:
- **atraso**: la animación llegó más de 0,2 s tarde a su frase. Se acortan los
  `run_time` o se usa `margen()`.
- **silencio de voz**: las animaciones de un bloque duraron más que su audio.
- **encimado**: un texto tocado *parcialmente* por otra forma u otro texto.
  Contener por completo (texto dentro de su caja) no cuenta.
- **fuera de cuadro**: texto que se sale de la pantalla (solo sin zoom).

Informes por escena en `media/informes/<Clase>.json`. **Meta: 0 avisos.**

### Hojas de contacto: revisar a ojo sin mirar el video entero
```bash
ffmpeg -v error -y -i Escena.mp4 -vf "fps=1,scale=256:-1,pad=260:148:2:2:white,tile=8x7" -frames:v 1 hoja.png
ffmpeg -v error -y -ss 12.5 -i Escena.mp4 -frames:v 1 cuadro.png     # un instante puntual
```
El borde blanco (`pad`) separa las celdas, porque sin él es fácil mezclar filas.
Buscar lo que los controles **no** ven: celdas casi vacías, elementos chicos,
bloques de color macizos (ver `Indicate` en §10), íconos que no se entienden.

### `voces_prueba/`
- `probar_voces.py`: el mismo párrafo con varias voces (así se eligió Ximena).
- `probar_nombres.py`: nombres propios y siglas.
- `probar_anglicismos.py`: variantes numeradas de cada anglicismo ("Opción uno…
  Opción dos…") para que el usuario elija. **Así se decide toda pronunciación nueva.**

---

## 9. Voz y pronunciación

- `comun/voz.py`: `VOZ_POR_DEFECTO = "es-ES-XimenaNeural"`, `VELOCIDAD = "-5%"`.
  Cambiarlas ahí cambia todo el curso (y vuelve a sintetizar todo el audio).
- Caché en `media/voiceovers/` de cada episodio: un bloque solo se vuelve a
  sintetizar si cambia su texto hablado. Cambiar momentos no regenera audio.
- `comun/pronunciacion.py`: reemplazos **solo para el audio**; los subtítulos
  muestran el texto del guion. Estado actual, elegido de oído por el usuario:
  - **tal cual**: Dijkstra, Therac, Ariane, Petri, deadlock, livelock, starvation,
    rendezvous, PCB, software, hardware, CPU, bug, quantum;
  - **reemplazados**: testing → «téstin», interleaving → «interlíving»,
    mutex → «mútex», spoiler → «espóiler».
- Palabra nueva dudosa → agregarla a `probar_anglicismos.py`, generar, mandar
  los mp3 y que el usuario elija un número. No adivinar ("softgüer" sonaba peor
  que "software").

---

## 10. Entorno y trampas conocidas

### Entorno (Windows)
- **Python correcto**: `C:\Users\Nacho\AppData\Local\Programs\Python\Python312\python.exe`.
  El `python` del PATH puede resolver a MSYS2 (3.14, sin Manim ni pip).
- Instalado: Manim 0.21, manim-voiceover 0.4.0, edge-tts 7.2, ffmpeg (winget), MiKTeX.
- `PYTHONPATH` = `Animaciones/` (render.py y ritmo.py lo ponen solos; para correr
  `manim` a mano: `$env:PYTHONPATH = "C:\Users\Nacho\Documents\ProgramacionConcurrente\Animaciones"`).
- edge-tts necesita internet. No hay GPU NVIDIA: los TTS locales serían lentos.
- El aviso "SoX could not be found" es inofensivo.
- En la consola de Windows los acentos se ven rotos (`�`): es la consola, los
  archivos están bien en UTF-8. Para imprimir bien: `PYTHONIOENCODING=utf-8`.
- En bash, los heredocs con código Python van con `<<'EOF'` (entre comillas): sin
  comillas, `\b` y `\\` se rompen.

### Manim
- **Animar una parte de un grupo** (ej. `tecla.animate`) saca esa parte a la
  lista de la escena: `self.remove(grupo)` la deja en pantalla. Para cambiar de
  plano usar `self.corte()` o `self.limpiar()`.
- **`Indicate(...)` y `.set_color(...)` sobre un grupo pintan también el
  relleno**: una caja con fondo oscuro queda como un bloque macizo y el texto
  desaparece. Usar `self.resaltar(m)` o `caja.set_stroke(color)`.
- **`.set_opacity(x)` sobre un grupo también pisa el relleno**: `set_opacity(1)`
  deja las cajas macizas (y un `Arc` relleno). Para atenuar y volver: `m.save_state()`,
  `m.animate.fade(0.65)` y después `Restore(m)`; o una copia nueva en vez de "desatenuar".
- Para sacar de cuadro algo con textos, `FadeOut(grupo, shift=...)`: un `shift` que los
  deja fuera de pantalla dispara el aviso de fuera de cuadro aunque después haya un corte.
- `always_redraw` o updaters: `clear_updaters()` antes de `FadeOut` o de moverlo.
- No anclar un texto a algo que rota (su caja envolvente cambia y el texto salta):
  usar una posición fija.
- `VGroup(...)` no acepta objetos no vectoriales: para "todo lo que hay en
  pantalla" usar `Group(*[m for m in self.mobjects if m is not self.camera.frame])`.
- `Text("")` vacío puede romper. `Text` con `\n` alinea a la izquierda: usar `texto_centrado`.
- Números: `Integer(..., group_with_commas=False)`; decimales con coma a mano.
- Después de `zoom()`, lo nuevo tiene que caber en el encuadre reducido; cerrar con `zoom_reset()`.
- Un zoom muy profundo dentro de una forma rellena deja la pantalla de un solo
  color: hacer zoom hasta que el objeto se vea entero, no más.
- Una copia que vuelve a su original tiene que volver a la **misma escala**, si
  no queda encimada a medias.

### manim-voiceover + edge-tts
- manim-voiceover 0.4.0 **no trae** servicio de edge-tts: el nuestro está en `voz.py`.
- edge-tts ≥ 7 manda por defecto límites de **oración**; `voz.py` pide
  `boundary="WordBoundary"` (sin eso no hay marcas) y ubica cada palabra en el texto.
- El caché se indexa por el texto **sin** marcas; `voz.py` devuelve el texto
  actual para que las marcas nuevas existan.
- **Bloque que empieza con un signo** («¿Qué…»): la primera palabra queda en el
  offset 1 y una marca en el offset 0 cae fuera del interpolador de
  manim-voiceover, que la manda al **final** del audio (atraso enorme en todo el
  bloque). `voz.py` lo corrige solo (`_desde_el_principio`). `ritmo.py` no lo
  mostraba porque usa `np.interp`, que recorta al rango.
- Algo tenue que no debe contar para el control de **encimado** (un árbol
  "fantasma" de guía): opacidad **< 0,1**. Con 0,1 exacto, cada letra cuenta
  como forma y pisa a medias los textos reales. Ojo: `stroke_width=0` no alcanza,
  el control mira la **opacidad** del borde (por defecto 1): pasar también `stroke_opacity=0`.
- `FadeOut` sobre una parte de un grupo que sigue en escena (un arco de `RedMutex`) la deja
  invisible para siempre y un `FadeIn` posterior no la trae de vuelta: usar `save_state()`,
  `.animate.fade(1)` y `Restore`.
- `self.remove(VGroup(a, b))` con un grupo armado al vuelo no saca `a` ni `b` de la escena:
  hay que pasar los objetos que se agregaron (`self.remove(a, b)`).

---

## 11. Checklist antes de entregar

- [ ] `ritmo.py` sin tramos de más de ~4 s (o cubiertos por una animación de ≥1 s).
- [ ] Render sin errores y **0 avisos** en los controles automáticos.
- [ ] Hojas de contacto revisadas: sin huecos, sin elementos chicos, sin bloques macizos.
- [ ] Duración ~10 min (`render.py` la informa).
- [ ] Castellano de España en todo el texto nuevo; números en formato español.
- [ ] Cada escena tiene `**Capítulo YouTube:**` y la primera empieza en 0:00.
- [ ] El cierre anuncia el episodio siguiente.
- [ ] README actualizado si apareció una trampa nueva o una herramienta nueva.

---

## 12. Cómo trabajar con el usuario

- El usuario escribe en español rioplatense; los videos van en castellano de España.
- Mostrar avances con archivos (mp3, mp4, `guion.md`), no descripciones.
  Borradores en 480p; el 1080p va solo al final.
- **Aprueba**: el texto narrado nuevo, la voz y las pronunciaciones (de oído) y
  cada episodio en borrador antes del render final.
- **No hace falta preguntarle**: detalles de animación, íconos, colores, efectos
  y correcciones que surgen de los controles. Se deciden siguiendo este README.
- Lo que más valora, según su propio feedback: animaciones rápidas y sincronizadas,
  que todo lo dicho se vea, nada de relleno en pantalla y ~10 min por episodio.
