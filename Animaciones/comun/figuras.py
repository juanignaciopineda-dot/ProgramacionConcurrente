"""Piezas visuales reutilizables entre escenas del curso.

Cada función/clase devuelve un VGroup ya armado (no animado): la escena
decide cómo animarlo (FadeIn, Create, etc.) y cómo posicionarlo.
"""
from __future__ import annotations

from manim import *
import numpy as np

from comun.estilo import AMBAR, AZUL, CIAN, FONDO, INK, MAGENTA, ROJO, TXT, VERDE

# ======================================================================
#  CPU / memoria / registros — para la escena "ProgramaYCPU"
# ======================================================================

def cpu(ancho: float = 2.6, alto: float = 1.8, color=AZUL) -> VGroup:
    """Una caja de CPU con su etiqueta y un hueco marcado para el PC."""
    caja = RoundedRectangle(corner_radius=0.12, width=ancho, height=alto,
                             color=color, fill_color=color, fill_opacity=0.10,
                             stroke_width=2.5)
    titulo = Text("CPU", color=TXT, weight=BOLD).scale(0.5)
    titulo.next_to(caja.get_top(), DOWN, buff=0.18)
    grupo = VGroup(caja, titulo)
    grupo.caja = caja
    grupo.titulo = titulo
    return grupo


def registro(nombre: str, valor: str = "", ancho: float = 1.1, alto: float = 0.55,
             color=INK) -> VGroup:
    """Un registro de CPU: casillero con nombre arriba y valor adentro."""
    r = Rectangle(width=ancho, height=alto, color=color, stroke_width=2,
                  fill_color=FONDO, fill_opacity=1.0)
    nom = Text(nombre, color=INK).scale(0.35)
    nom.next_to(r, UP, buff=0.08)
    val = Text(valor, color=TXT).scale(0.42)
    val.move_to(r.get_center())
    grupo = VGroup(r, nom, val)
    grupo.caja = r
    grupo.valor = val
    return grupo


def celda_memoria(direccion: str, contenido: str = "", ancho: float = 1.3,
                   alto: float = 0.55, color=INK) -> VGroup:
    """Una celda de memoria: dirección a la izquierda, contenido adentro."""
    r = Rectangle(width=ancho, height=alto, color=color, stroke_width=2,
                  fill_color=FONDO, fill_opacity=1.0)
    dire = Text(direccion, color=INK).scale(0.32)
    dire.next_to(r, LEFT, buff=0.12)
    cont = Text(contenido, color=TXT).scale(0.4)
    cont.move_to(r.get_center())
    grupo = VGroup(dire, r, cont)
    grupo.caja = r
    grupo.contenido = cont
    return grupo


def memoria(filas: list[tuple[str, str]], ancho: float = 1.3, alto: float = 0.5,
            color=INK) -> VGroup:
    """Una pila vertical de celdas de memoria."""
    celdas = VGroup(*[celda_memoria(d, c, ancho, alto, color) for d, c in filas])
    celdas.arrange(DOWN, buff=0.06)
    return celdas


def puntero_pc(objetivo: Mobject, color=AMBAR, etiqueta: str = "PC") -> VGroup:
    """Flecha + etiqueta que señala la instrucción/celda actual (contador
    de programa). Se reubica con `.animate.next_to(nuevo_objetivo, ...)`."""
    flecha = Triangle(color=color, fill_color=color, fill_opacity=1.0).scale(0.12)
    flecha.rotate(-PI / 2)
    lab = Text(etiqueta, color=color).scale(0.32)
    lab.next_to(flecha, UP, buff=0.05)
    grupo = VGroup(flecha, lab)
    grupo.next_to(objetivo, LEFT, buff=0.15)
    return grupo


# ======================================================================
#  Línea de tiempo con años
# ======================================================================

class LineaTiempo(VGroup):
    """Línea horizontal con escala de años. `marca(anio)` devuelve el tick +
    año (no lo agrega a la escena)."""

    def __init__(self, desde: int = 1983, hasta: int = 1998, ancho: float = 12.4,
                 color=INK, **kwargs):
        super().__init__(**kwargs)
        self.desde, self.hasta = desde, hasta
        self.linea = Line(LEFT * ancho / 2, RIGHT * ancho / 2, color=color, stroke_width=3)
        self.add(self.linea)

    def punto(self, anio: float) -> np.ndarray:
        frac = (anio - self.desde) / (self.hasta - self.desde)
        return self.linea.point_from_proportion(float(np.clip(frac, 0, 1)))

    def marca(self, anio: int, color=AMBAR) -> VGroup:
        p = self.punto(anio)
        punto = Dot(p, radius=0.09, color=color)
        texto = Text(str(anio), color=color, weight=BOLD).scale(0.5).next_to(p, DOWN, buff=0.18)
        g = VGroup(punto, texto)
        g.punto = punto
        return g


# ======================================================================
#  Procesos, hilos, PCB — para "Proceso", "EstadosProceso", "Hilos"
# ======================================================================

def caja_proceso(nombre: str, color=AZUL, ancho: float = 2.2, alto: float = 1.2) -> VGroup:
    r = RoundedRectangle(corner_radius=0.12, width=ancho, height=alto,
                          color=color, fill_color=color, fill_opacity=0.12,
                          stroke_width=2.5)
    t = Text(nombre, color=TXT).scale(0.5)
    t.move_to(r.get_center())
    grupo = VGroup(r, t)
    grupo.caja = r
    grupo.texto = t
    return grupo


def tabla_pcb(campos: dict[str, str], ancho: float = 4.6, color=INK) -> VGroup:
    """Una tabla simple de dos columnas (campo / valor) que representa el PCB."""
    filas = VGroup()
    for campo, valor in campos.items():
        fc = Text(campo, color=INK).scale(0.38)
        fv = Text(valor, color=TXT).scale(0.38)
        fila = VGroup(fc, fv).arrange(RIGHT, buff=0.4, aligned_edge=UP)
        filas.add(fila)
    filas.arrange(DOWN, buff=0.14, aligned_edge=LEFT)

    max_ancho = max(f.width for f in filas)
    for f in filas:
        f[1].align_to(filas, LEFT).shift(RIGHT * (max_ancho * 0.62))

    marco = SurroundingRectangle(filas, color=color, buff=0.28, corner_radius=0.1,
                                  stroke_width=2)
    titulo = Text("PCB", color=TXT, weight=BOLD).scale(0.45)
    titulo.next_to(marco, UP, buff=0.12)
    return VGroup(marco, filas, titulo)


def pila_de_hilo(alto: float = 1.8, ancho: float = 0.7, n_marcos: int = 3,
                  color=CIAN) -> VGroup:
    """Representa la pila de ejecución propia de un hilo, como marcos apilados."""
    marcos = VGroup(*[
        Rectangle(width=ancho, height=alto / n_marcos, color=color,
                  stroke_width=1.5, fill_color=color, fill_opacity=0.08)
        for _ in range(n_marcos)
    ])
    marcos.arrange(DOWN, buff=0.03)
    etiqueta = Text("pila", color=INK).scale(0.3)
    etiqueta.next_to(marcos, DOWN, buff=0.08)
    return VGroup(marcos, etiqueta)


# ======================================================================
#  Redes de Petri — para "PuenteAPetri" (y capítulos futuros)
# ======================================================================

class Lugar(VGroup):
    """Un lugar de una red de Petri: círculo + fichas (puntos) + etiqueta."""

    def __init__(self, etiqueta: str = "", fichas: int = 0, radio: float = 0.45,
                 color=INK, color_fichas=None, **kwargs):
        super().__init__(**kwargs)
        self.color_base = color
        self.color_fichas = color_fichas or color
        self.n_fichas = fichas
        self.circulo = Circle(radius=radio, color=color, stroke_width=2.5,
                               fill_color=FONDO, fill_opacity=1.0)
        self.puntos = VGroup()
        self._redibujar_fichas()
        self.etiqueta = Text(etiqueta, color=TXT).scale(0.4)
        self.etiqueta.next_to(self.circulo, DOWN, buff=0.15)
        self.add(self.circulo, self.puntos, self.etiqueta)

    def _redibujar_fichas(self):
        self.puntos.become(VGroup())
        if self.n_fichas <= 0:
            return
        # el radio real (no `.radius`, que no cambia al escalar el lugar)
        r = self.circulo.width / 2
        if self.n_fichas == 1:
            posiciones = [self.circulo.get_center()]
        else:
            radio_chico = r * 0.4
            posiciones = [
                self.circulo.get_center() + radio_chico * np.array(
                    [np.cos(2 * PI * i / self.n_fichas), np.sin(2 * PI * i / self.n_fichas), 0])
                for i in range(self.n_fichas)
            ]
        for p in posiciones:
            punto = Dot(point=p, radius=r * (0.3 if self.n_fichas == 1 else 0.16), color=self.color_fichas)
            self.puntos.add(punto)

    def poner_fichas(self, n: int) -> "Lugar":
        """Cambia las fichas al instante (sin animar)."""
        self.n_fichas = n
        self._redibujar_fichas()
        return self

    def ficha_suelta(self) -> Dot:
        """Una ficha nueva, suelta, del tamaño de las de este lugar (para hacerla viajar)."""
        return Dot(self.circulo.get_center(), radius=self.circulo.width / 2 * 0.3, color=self.color_fichas)

    def con_fichas(self, n: int) -> "Lugar":
        """Devuelve una copia con `n` fichas, para animar con Transform."""
        nueva = self.copy()
        nueva.poner_fichas(n)
        return nueva


class Transicion(VGroup):
    """Una transición de una red de Petri: barra rectangular + etiqueta,
    que cambia de color cuando está habilitada."""

    def __init__(self, etiqueta: str = "", ancho: float = 0.28, alto: float = 0.9,
                 color=INK, **kwargs):
        super().__init__(**kwargs)
        self.barra = Rectangle(width=ancho, height=alto, color=color,
                                fill_color=color, fill_opacity=1.0, stroke_width=0)
        self.etiqueta = Text(etiqueta, color=TXT).scale(0.36)
        self.etiqueta.next_to(self.barra, DOWN, buff=0.15)
        self.add(self.barra, self.etiqueta)

    def habilitar(self, color=VERDE):
        return self.barra.animate.set_color(color)

    def deshabilitar(self, color=INK):
        return self.barra.animate.set_color(color)


def arco_pn(origen: Mobject, destino: Mobject, peso: int = 1, color=INK) -> VGroup:
    """Arco entre lugar y transición (o viceversa), con su peso si es > 1."""
    a = Arrow(origen.get_center(), destino.get_center(), color=color,
              stroke_width=2.2, buff=0.42, max_tip_length_to_length_ratio=0.12)
    grupo = VGroup(a)
    if peso > 1:
        etiqueta = Text(str(peso), color=color).scale(0.32)
        etiqueta.move_to(a.get_center() + a.copy().rotate(PI / 2).get_vector() * 0.18)
        grupo.add(etiqueta)
    return grupo


def disparar(escena: Scene, transicion: Transicion,
             lugares_entrada: list[tuple[Lugar, int]],
             lugares_salida: list[tuple[Lugar, int]],
             tiempo_flash: float = 0.35) -> None:
    """Anima el disparo de una transición: la barra destella y las fichas
    desaparecen de los lugares de entrada y aparecen en los de salida.

    `lugares_entrada`/`lugares_salida` son listas de `(lugar, peso)`.
    Muta el `n_fichas` de cada `Lugar` en el lugar (in-place) y anima el
    cambio con Transform, para poder encadenar varios disparos seguidos.
    """
    escena.play(transicion.habilitar(), run_time=tiempo_flash)
    escena.play(transicion.deshabilitar(), run_time=tiempo_flash)

    nuevos = []
    for lugar, peso in lugares_entrada:
        nuevo = lugar.con_fichas(lugar.n_fichas - peso)
        nuevos.append((lugar, nuevo))
    for lugar, peso in lugares_salida:
        nuevo = lugar.con_fichas(lugar.n_fichas + peso)
        nuevos.append((lugar, nuevo))

    escena.play(*[Transform(l, n) for l, n in nuevos])
    for lugar, nuevo in nuevos:
        lugar.poner_fichas(nuevo.n_fichas)


# ======================================================================
#  Episodio 2: núcleos, acciones atómicas e intercalaciones
# ======================================================================

def nucleo(color=AZUL, lado: float = 1.0, texto: str = "") -> VGroup:
    """Un núcleo de procesador: cuadrado redondeado. `.caja` para encenderlo."""
    c = RoundedRectangle(width=lado, height=lado, corner_radius=0.1, color=color, stroke_width=3)
    c.set_fill(color, 0.12)
    g = VGroup(c)
    if texto:
        g.add(Text(texto, color=TXT, weight=BOLD).scale(0.4 * lado).move_to(c))
    g.caja = c
    return g


def ficha_accion(nombre: str, color=AZUL, lado: float = 0.8) -> VGroup:
    """Una acción atómica (A1, B2...): ficha cuadrada con su nombre."""
    r = RoundedRectangle(width=lado, height=lado, corner_radius=0.12, color=color, stroke_width=3)
    r.set_fill(color, 0.25)
    t = Text(nombre, color=TXT, weight=BOLD).scale(0.42 * lado / 0.8).move_to(r)
    g = VGroup(r, t)
    g.caja, g.texto = r, t
    return g


def mini_estados(color=INK) -> VGroup:
    """Diagrama de 5 estados en miniatura (Nuevo, Preparado, Ejecución,
    Terminado arriba; Bloqueado abajo), como en el cierre de E1."""
    pos = [(-1.3, 0.5), (-0.45, 0.5), (0.45, 0.5), (1.3, 0.5), (0, -0.5)]
    return VGroup(*[RoundedRectangle(width=0.6, height=0.35, corner_radius=0.08, color=color, stroke_width=2)
                    .set_fill(FONDO, 1).move_to([x, y, 0]) for x, y in pos])


def intercalaciones(a: list[str], b: list[str]) -> list[tuple[str, ...]]:
    """Todas las formas de intercalar las secuencias a y b respetando el orden de cada una."""
    if not a:
        return [tuple(b)]
    if not b:
        return [tuple(a)]
    return ([(a[0],) + r for r in intercalaciones(a[1:], b)]
            + [(b[0],) + r for r in intercalaciones(a, b[1:])])


class ArbolIntercalado(VGroup):
    """Árbol de todas las intercalaciones de dos procesos, de izquierda a derecha.

    Cada nodo es un prefijo de ejecución; cada hoja, una ejecución completa.
    - `nodos[prefijo]` → Dot;  `aristas[prefijo]` → VGroup(línea[, etiqueta]) que llega a ese prefijo
    - `hojas` → ejecuciones completas (tuplas), de arriba abajo
    - `camino(hoja)` → aristas desde la raíz hasta esa hoja, en orden
    """

    def __init__(self, n: int = 2, m: int = 2, ancho: float = 9.0, alto: float = 5.0,
                 etiquetas: bool = True, color_a=AZUL, color_b=MAGENTA, radio: float = 0.06,
                 grosor: float = 3.0, **kwargs):
        super().__init__(**kwargs)
        a = [f"A{i + 1}" for i in range(n)]
        b = [f"B{i + 1}" for i in range(m)]
        self.hojas = intercalaciones(a, b)
        prof = n + m
        ys = np.linspace(alto / 2, -alto / 2, len(self.hojas))
        # la altura de cada prefijo es el promedio de las de sus hojas
        acum: dict[tuple, list[float]] = {}
        for h, y in zip(self.hojas, ys):
            for k in range(prof + 1):
                acum.setdefault(h[:k], []).append(float(y))
        self.nodos: dict[tuple, Dot] = {}
        self.aristas: dict[tuple, VGroup] = {}
        for pref, lista in sorted(acum.items(), key=lambda kv: len(kv[0])):
            x = -ancho / 2 + ancho * len(pref) / prof
            self.nodos[pref] = Dot([x, float(np.mean(lista)), 0], radius=radio, color=INK)
        for pref, dot in self.nodos.items():
            if not pref:
                continue
            col = color_a if pref[-1].startswith("A") else color_b
            linea = Line(self.nodos[pref[:-1]].get_center(), dot.get_center(), color=col, stroke_width=grosor)
            ar = VGroup(linea)
            if etiquetas:
                t = Text(pref[-1], color=col, weight=BOLD).scale(0.3)
                t.move_to(linea.get_center() + UP * 0.2)
                ar.add(t)
            ar.linea = linea
            self.aristas[pref] = ar
        self.raiz = self.nodos[()]
        self.add(*self.aristas.values(), *self.nodos.values())

    def camino(self, hoja: tuple) -> list[VGroup]:
        return [self.aristas[hoja[:k]] for k in range(1, len(hoja) + 1)]


# ======================================================================
#  Episodio 3: sección crítica, reglas del juego y problemas clásicos
# ======================================================================

def hilo(nombre: str, color=AZUL, valor: str = "") -> VGroup:
    """Un hilo con su propio registro debajo. `.caja`, `.registro` (ver `registro`)."""
    cp = caja_proceso(nombre, color, ancho=1.6, alto=0.8)
    reg = registro("registro", valor, ancho=1.1, alto=0.6, color=color)
    reg.next_to(cp, DOWN, buff=0.45)
    g = VGroup(cp, reg)
    g.caja, g.registro = cp, reg
    return g


class SalaPuerta(VGroup):
    """Sala (la sección crítica) vista desde arriba, con una puerta en la pared izquierda.

    - `.muros`: los tres muros y los dos tramos de la pared de la puerta
    - `.puerta`: la hoja de la puerta (gira sobre `.bisagra` para abrirse)
    - `.dentro`: punto central de la sala; `.fuera`: punto delante de la puerta
    - `cerrar()` / `abrir()`: animaciones de la hoja
    """

    def __init__(self, ancho: float = 4.0, alto: float = 3.0, hueco: float = 1.1,
                 color=INK, **kwargs):
        super().__init__(**kwargs)
        x0, x1, y0, y1 = -ancho / 2, ancho / 2, -alto / 2, alto / 2
        piso = Rectangle(width=ancho, height=alto, stroke_width=0).set_fill(color, 0.07)
        pared = dict(color=color, stroke_width=6)
        self.muros = VGroup(
            Line([x0, y1, 0], [x1, y1, 0], **pared), Line([x1, y1, 0], [x1, y0, 0], **pared),
            Line([x1, y0, 0], [x0, y0, 0], **pared),
            Line([x0, y0, 0], [x0, -hueco / 2, 0], **pared), Line([x0, hueco / 2, 0], [x0, y1, 0], **pared))
        self.bisagra = np.array([x0, hueco / 2, 0])
        self.puerta = Line(self.bisagra, [x0, -hueco / 2, 0], color=AMBAR, stroke_width=7)
        self.add(piso, self.muros, self.puerta)
        self.piso = piso
        self._ancho = ancho

    @property
    def dentro(self) -> np.ndarray:
        return self.piso.get_center()

    @property
    def fuera(self) -> np.ndarray:
        return self.piso.get_left() + LEFT * 1.1

    def abrir(self):
        return Rotate(self.puerta, angle=PI / 2, about_point=self.puerta.get_start())

    def cerrar(self):
        return Rotate(self.puerta, angle=-PI / 2, about_point=self.puerta.get_start())


def tarjeta(frente: str | None = None, color=AMBAR, ancho: float = 2.4, alto: float = 3.0,
            escala_texto: float = 0.6) -> VGroup:
    """Carta: boca abajo (con «?») si `frente` es None, o con una palabra al frente.
    `.marco` y `.contenido`."""
    marco = RoundedRectangle(width=ancho, height=alto, corner_radius=0.2, color=color,
                             stroke_width=4).set_fill(color, 0.12 if frente else 0.28)
    if frente is None:
        contenido = Text("?", color=color, weight=BOLD).scale(1.6)
    else:
        contenido = Text(frente, color=TXT, weight=BOLD).scale(escala_texto)
        if contenido.width > ancho * 0.85:
            contenido.scale_to_fit_width(ancho * 0.85)
    contenido.move_to(marco)
    g = VGroup(marco, contenido)
    g.marco, g.contenido = marco, contenido
    return g


def grilla_ejecuciones(n: int = 20, malas: tuple[int, ...] = (13,), columnas: int = 10,
                       lado: float = 0.42) -> VGroup:
    """Muchas ejecuciones del mismo programa: cuadraditos verdes y alguno rojo.
    `.celdas` (todas) y `.malas` (las rojas)."""
    celdas = VGroup(*[
        RoundedRectangle(width=lado, height=lado, corner_radius=0.06, stroke_width=2.5,
                         color=ROJO if i in malas else VERDE).set_fill(ROJO if i in malas else VERDE, 0.35)
        for i in range(n)]).arrange_in_grid(cols=columnas, buff=0.14)
    g = VGroup(celdas)
    g.celdas = celdas
    g.malas = VGroup(*[celdas[i] for i in malas])
    return g


def pista(ancho: float = 11.0, carriles: int = 2, alto_carril: float = 0.9, color=INK) -> VGroup:
    """Pista de carreras horizontal: líneas de carril, salida y meta.
    `.carril(i)` da el centro izquierdo del carril i; `.meta` es la línea de llegada."""
    ys = [alto_carril * (carriles / 2 - k) for k in range(carriles + 1)]
    lineas = VGroup(*[Line([-ancho / 2, y, 0], [ancho / 2, y, 0], color=color, stroke_width=2.5) for y in ys])
    salida = Line([-ancho / 2 + 0.6, ys[0], 0], [-ancho / 2 + 0.6, ys[-1], 0], color=color, stroke_width=4)
    meta = DashedLine([ancho / 2 - 0.8, ys[0], 0], [ancho / 2 - 0.8, ys[-1], 0], color=TXT, stroke_width=5)
    g = VGroup(lineas, salida, meta)
    g.meta, g.salida = meta, salida
    centro0 = lineas.get_center().copy()

    def carril(i: int) -> np.ndarray:
        """Punto de largada del carril i (0 = arriba), siga donde siga la pista."""
        return np.array([-ancho / 2 + 0.2, (ys[i] + ys[i + 1]) / 2, 0]) + lineas.get_center() - centro0

    g.carril = carril
    return g


def montaje_contador(valor: str = "0") -> VGroup:
    """El escenario de la condición de carrera: la celda `contador` arriba y
    los hilos H1 (izquierda) y H2 (derecha) con sus registros.
    `.celda`, `.h1`, `.h2`."""
    celda = celda_memoria("contador", valor, ancho=1.6, alto=0.9).scale(1.6).move_to(UP * 1.9)
    h1 = hilo("H1", AZUL).scale(1.7).move_to(LEFT * 4.6 + DOWN * 0.7)
    h2 = hilo("H2", MAGENTA).scale(1.7).move_to(RIGHT * 4.6 + DOWN * 0.7)
    g = VGroup(celda, h1, h2)
    g.celda, g.h1, g.h2 = celda, h1, h2
    return g


def paso_linea(txt: str, color=AZUL, ancho: float = 2.6) -> VGroup:
    """Una casilla de la línea de pasos de abajo («H1 lee 0», «LEER»...)."""
    from comun.estilo import caja
    return caja(txt, color, ancho=ancho, alto=0.75, scale=0.52, relleno=0.18)


def fila_pasos(pasos: list[tuple[str, str]], y: float = -3.3, ancho: float = 2.6) -> VGroup:
    """Línea de pasos completa: [(texto, color), ...] en fila, centrada en `y`."""
    fila = VGroup(*[paso_linea(t, c, ancho) for t, c in pasos]).arrange(RIGHT, buff=0.12)
    if fila.width > 13.4:
        fila.scale_to_fit_width(13.4)
    return fila.move_to(UP * y)


def reglas_seis(lado: float = 0.9, color=INK) -> VGroup:
    """Las seis reglas del juego de la sección crítica: casillas numeradas del 1 al 6.
    `reglas_seis()[i]` es la regla i+1; cada casilla tiene `.caja` y `.num`."""
    fila = VGroup()
    for i in range(6):
        c = RoundedRectangle(width=lado, height=lado, corner_radius=0.12, color=color, stroke_width=3)
        c.set_fill(color, 0.12)
        n = Text(str(i + 1), color=TXT, weight=BOLD).scale(0.5 * lado / 0.9).move_to(c)
        g = VGroup(c, n)
        g.caja, g.num = c, n
        fila.add(g)
    return fila.arrange(RIGHT, buff=0.25)


def proceso_mini(nombre: str, color=AZUL) -> VGroup:
    """Proceso chico (para colas, salas y pistas). `.caja` y `.texto`."""
    return caja_proceso(nombre, color, ancho=1.1, alto=0.7).scale(1.35)


def voltear(escena: Scene, carta: VGroup, nueva: VGroup, run_time: float = 0.45) -> VGroup:
    """Da vuelta una carta: se aplasta en horizontal y aparece `nueva` en su lugar.
    Devuelve `nueva` (ya en escena)."""
    nueva.move_to(carta).set(height=carta.height)
    ancho = nueva.width
    nueva.stretch(0.02, 0)
    escena.play(carta.animate.stretch(0.02, 0), run_time=run_time / 2, rate_func=rate_functions.ease_in_sine)
    escena.remove(carta)
    escena.play(nueva.animate.stretch_to_fit_width(ancho), run_time=run_time / 2,
                rate_func=rate_functions.ease_out_sine)
    return nueva


# ======================================================================
#  Episodio 4: interacción entre procesos, modelos y la red de exclusión mutua
# ======================================================================

def disparar_viaje(escena: Scene, transicion: Transicion, entradas: list[Lugar], salidas: list[Lugar],
                   run_time: float = 0.9, rutas: list[tuple[Lugar, list]] | None = None) -> None:
    """Disparo de una transición con las fichas VIAJANDO: salen de los lugares de
    entrada, entran en la barra (que se enciende) y salen hacia los de salida.

    `rutas`: [(lugar_salida, [puntos intermedios])] para que una ficha siga un
    camino con esquinas (arcos que rodean la red) en vez de ir en línea recta.
    Actualiza `n_fichas` de cada lugar. Dura `run_time` (+0,15 s del destello).
    """
    centro = transicion.barra.get_center()
    escena.play(transicion.barra.animate.set_color(VERDE), run_time=0.15)
    viajeras = [l.ficha_suelta() for l in entradas]
    for l in entradas:
        l.poner_fichas(l.n_fichas - 1)
    escena.add(*viajeras)
    escena.play(*[f.animate.move_to(centro) for f in viajeras], run_time=run_time * 0.45,
                rate_func=rate_functions.ease_in_quad)
    escena.remove(*viajeras)
    desvios = {id(l): pts for l, pts in (rutas or [])}
    nuevas, anims = [], []
    for l in salidas:
        f = l.ficha_suelta().move_to(centro)
        nuevas.append(f)
        if id(l) in desvios:
            camino = VMobject().set_points_as_corners([centro, *desvios[id(l)], l.circulo.get_center()])
            anims.append(MoveAlongPath(f, camino))
        else:
            anims.append(f.animate.move_to(l.circulo.get_center()))
    escena.play(*anims, transicion.barra.animate.set_color(INK), run_time=run_time * 0.55,
                rate_func=rate_functions.ease_out_quad)
    escena.remove(*nuevas)
    for l in salidas:
        l.poner_fichas(l.n_fichas + 1)


def soltar_ficha(escena: Scene, lugar: Lugar, altura: float = 2.5, run_time: float = 0.45) -> None:
    """Una ficha cae desde arriba dentro de `lugar` (con rebote) y queda sumada."""
    f = lugar.ficha_suelta().shift(UP * altura)
    escena.add(f)
    escena.play(f.animate.move_to(lugar.circulo.get_center()), run_time=run_time,
                rate_func=rate_functions.ease_out_bounce)
    escena.remove(f)
    lugar.poner_fichas(lugar.n_fichas + 1)


class RedMutex(VGroup):
    """La red de Petri de la exclusión mutua: dos procesos y el lugar `mutex`.

    P1 (azul) a la izquierda y P2 (magenta) a la derecha, cada uno con su ciclo
    «fuera» (arriba) → entrar → «en SC» (abajo) → salir → «fuera». `mutex` en el
    centro, con una ficha. Los arcos salir → mutex rodean la red por abajo.

    - lugares: `.f1`, `.sc1`, `.f2`, `.sc2`, `.mutex`; transiciones: `.e1`, `.s1`, `.e2`, `.s2`
    - `.arcos[("f1", "e1")]`, etc. (claves con los nombres de arriba)
    - `entrar(escena, i)` / `salir(escena, i)`: disparos con las fichas viajando
    """

    def __init__(self, fichas: bool = True, **kwargs):
        super().__init__(**kwargs)
        n = 1 if fichas else 0
        self.f1 = Lugar("P1 fuera", n, color=AZUL, color_fichas=TXT).move_to([-3.4, 1.9, 0])
        self.sc1 = Lugar("P1 en SC", 0, color=AZUL, color_fichas=TXT).move_to([-3.4, -1.9, 0])
        self.f2 = Lugar("P2 fuera", n, color=MAGENTA, color_fichas=TXT).move_to([3.4, 1.9, 0])
        self.sc2 = Lugar("P2 en SC", 0, color=MAGENTA, color_fichas=TXT).move_to([3.4, -1.9, 0])
        self.mutex = Lugar("mutex", n, color=AMBAR, color_fichas=TXT).move_to(ORIGIN)
        self.e1 = Transicion("entrar").move_to([-2.1, 0, 0])
        self.s1 = Transicion("salir").move_to([-4.7, 0, 0])
        self.e2 = Transicion("entrar").move_to([2.1, 0, 0])
        self.s2 = Transicion("salir").move_to([4.7, 0, 0])
        lugares = [self.f1, self.sc1, self.f2, self.sc2, self.mutex]
        # etiquetas más grandes (en 480p las de 0,4 no se leen), fuera del paso de los arcos
        for l in lugares:
            l.etiqueta.scale(1.5)
        for l in (self.f1, self.f2, self.mutex):
            l.etiqueta.next_to(l.circulo, UP, buff=0.15)
        for l in (self.sc1, self.sc2):
            l.etiqueta.next_to(l.circulo, DOWN, buff=0.15)
        for t in (self.e1, self.s1, self.e2, self.s2):
            t.etiqueta.scale(1.5)
        self.e1.etiqueta.next_to(self.e1.barra, DOWN, buff=0.15)
        self.e2.etiqueta.next_to(self.e2.barra, DOWN, buff=0.15)
        self.s1.etiqueta.next_to(self.s1.barra, LEFT, buff=0.15)
        self.s2.etiqueta.next_to(self.s2.barra, RIGHT, buff=0.15)

        nombres = {"f1": self.f1, "sc1": self.sc1, "f2": self.f2, "sc2": self.sc2, "mutex": self.mutex,
                   "e1": self.e1, "s1": self.s1, "e2": self.e2, "s2": self.s2}
        self.arcos: dict[tuple[str, str], VGroup] = {}
        for a, b in [("f1", "e1"), ("e1", "sc1"), ("sc1", "s1"), ("s1", "f1"), ("mutex", "e1"),
                     ("f2", "e2"), ("e2", "sc2"), ("sc2", "s2"), ("s2", "f2"), ("mutex", "e2")]:
            self.arcos[(a, b)] = arco_pn(nombres[a].circulo if isinstance(nombres[a], Lugar) else nombres[a].barra,
                                         nombres[b].circulo if isinstance(nombres[b], Lugar) else nombres[b].barra)
        # salir → mutex: por abajo, con esquinas (hechos de líneas, que no cuentan como encimado)
        for s, clave in ((self.s1, ("s1", "mutex")), (self.s2, ("s2", "mutex"))):
            self.arcos[clave] = self._arco_rodeo(s)
        self.lugares = VGroup(*lugares)
        self.transiciones = VGroup(self.e1, self.s1, self.e2, self.s2)
        self.flechas = VGroup(*self.arcos.values())
        self.add(self.flechas, self.lugares, self.transiciones)

    def _y_rodeo(self) -> float:
        return self.sc1.etiqueta.get_bottom()[1] - 0.35

    def _arco_rodeo(self, s: Transicion) -> VGroup:
        y = self._y_rodeo()
        p0 = s.barra.get_bottom()
        p1 = np.array([p0[0], y, 0])
        p2 = np.array([self.mutex.circulo.get_x(), y, 0])
        p3 = self.mutex.circulo.get_bottom() + DOWN * 0.05
        estilo = dict(color=INK, stroke_width=2.2)
        return VGroup(Line(p0, p1, **estilo), Line(p1, p2, **estilo),
                      Arrow(p2, p3, buff=0, max_tip_length_to_length_ratio=0.12, **estilo))

    def ruta_a_mutex(self, i: int) -> list:
        """Esquinas del camino salir_i → mutex (para `disparar_viaje`)."""
        arco = self.arcos[(f"s{i}", "mutex")]
        return [arco[0].get_end(), arco[1].get_end()]

    def entrar(self, escena: Scene, i: int, run_time: float = 0.9) -> None:
        f, sc, e = (self.f1, self.sc1, self.e1) if i == 1 else (self.f2, self.sc2, self.e2)
        disparar_viaje(escena, e, [f, self.mutex], [sc], run_time=run_time)

    def salir(self, escena: Scene, i: int, run_time: float = 0.9) -> None:
        f, sc, s = (self.f1, self.sc1, self.s1) if i == 1 else (self.f2, self.sc2, self.s2)
        disparar_viaje(escena, s, [sc], [f, self.mutex], run_time=run_time,
                       rutas=[(self.mutex, self.ruta_a_mutex(i))])


def escalera(n: int = 3, ancho: float = 2.8, alto: float = 1.1, color=INK) -> VGroup:
    """Escalera de `n` escalones que sube hacia la derecha (vista de costado).
    `.escalones[i]` (rectángulos) y `.pisada(i)`: punto sobre el escalón i."""
    escalones = VGroup(*[
        Rectangle(width=ancho, height=alto * (i + 1), color=color, stroke_width=3).set_fill(color, 0.1)
        for i in range(n)]).arrange(RIGHT, buff=0, aligned_edge=DOWN)
    g = VGroup(escalones)
    g.escalones = escalones
    g.pisada = lambda i: escalones[i].get_top()
    return g


def anillo_estados(n: int = 10, radio: float = 1.3, color=AZUL, radio_estado: float = 0.17,
                   barras: bool = False) -> VGroup:
    """Máquina de estados en anillo: `n` estados en círculo, cada uno con una flecha
    al siguiente. `.estados` y `.flechas`. Con `barras=True` pone una transición
    (barrita) sobre cada flecha: el mismo ciclo visto como red de Petri (`.barras`)."""
    estados = VGroup(*[
        Circle(radius=radio_estado, color=color, stroke_width=3).set_fill(color, 0.2)
        .move_to(radio * np.array([np.cos(PI / 2 - 2 * PI * i / n), np.sin(PI / 2 - 2 * PI * i / n), 0]))
        for i in range(n)])
    flechas = VGroup(*[
        Arrow(estados[i].get_center(), estados[(i + 1) % n].get_center(), color=color, stroke_width=2.5,
              buff=radio_estado + 0.03, max_tip_length_to_length_ratio=0.3)
        for i in range(n)])
    g = VGroup(flechas, estados)
    g.estados, g.flechas = estados, flechas
    if barras:
        g.barras = VGroup(*[
            Rectangle(width=0.07, height=radio_estado * 1.6, stroke_width=0).set_fill(TXT, 1)
            .rotate(np.arctan2(*(f.get_end() - f.get_start())[1::-1])).move_to(f.get_center())
            for f in flechas])
        g.add(g.barras)
    return g


def maquina_estados_mini(color=AZUL, lado: float = 2.6, radio: float = 0.42) -> VGroup:
    """Máquina de estados de tres estados en triángulo (A → B → C → A).
    `.estados` y `.flechas`."""
    pos = [np.array([0, lado * 0.45, 0]), np.array([lado / 2, -lado * 0.4, 0]),
           np.array([-lado / 2, -lado * 0.4, 0])]
    estados = VGroup(*[Circle(radius=radio, color=color, stroke_width=4).set_fill(color, 0.15).move_to(p)
                       for p in pos])
    flechas = VGroup(*[Arrow(pos[i], pos[(i + 1) % 3], color=INK, stroke_width=3.5, buff=radio + 0.05,
                             max_tip_length_to_length_ratio=0.12) for i in range(3)])
    g = VGroup(flechas, estados)
    g.estados, g.flechas = estados, flechas
    return g


def tabla_bloqueo(ancho: float = 3.0, alto: float = 1.4) -> VGroup:
    """Tabla 2×2 de las combinaciones del pasaje de mensajes.
    Filas: enviar bloquea / enviar sigue. Columnas: recibir espera / recibir sigue.
    `.celda(i, j)` (fila i, columna j) y `.celdas`."""
    celdas = VGroup(*[
        Rectangle(width=ancho, height=alto, color=INK, stroke_width=3).set_fill(INK, 0.05)
        for _ in range(4)]).arrange_in_grid(2, 2, buff=0)
    t = dict(color=TXT, weight=BOLD)
    cols = VGroup(Text("recibir espera", **t), Text("recibir sigue", **t))
    filas = VGroup(Text("enviar espera", **t), Text("enviar sigue", **t))
    for x in (*cols, *filas):
        x.scale(0.48)
    for j, c in enumerate(cols):
        c.next_to(celdas[j], UP, buff=0.2)
    for i, f in enumerate(filas):
        f.next_to(celdas[2 * i], LEFT, buff=0.25)
    g = VGroup(celdas, cols, filas)
    g.celdas = celdas
    g.celda = lambda i, j: celdas[2 * i + j]
    return g
