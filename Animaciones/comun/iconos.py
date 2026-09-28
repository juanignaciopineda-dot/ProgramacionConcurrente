"""Pictogramas planos dibujados con primitivas de Manim.

Convenciones:
- Cada función devuelve un VGroup de ~1 unidad de alto (escalar con .scale()).
- `color` es el color principal; el relleno usa el mismo color con opacidad.
- Las partes que las escenas animan por separado quedan como atributos
  (ej. `maquina.haz`, `teclado.teclas`, `cohete.llama`).
"""
from __future__ import annotations

from manim import *
import numpy as np

from comun.estilo import AMBAR, AZUL, CIAN, FONDO, INK, MAGENTA, ROJO, TXT, VERDE


def _relleno(m: VMobject, color, opacidad: float = 1.0, borde: float = 0) -> VMobject:
    return m.set_fill(color, opacity=opacidad).set_stroke(color, width=borde)


# ============================================================ personas

def persona(color=AZUL) -> VGroup:
    """Silueta clásica: cabeza + hombros."""
    cabeza = _relleno(Circle(radius=0.19), color)
    cuerpo = _relleno(Sector(radius=0.36, start_angle=0, angle=PI), color)
    cabeza.next_to(cuerpo, UP, buff=0.06)
    g = VGroup(cuerpo, cabeza)
    g.cabeza, g.cuerpo = cabeza, cuerpo
    return g


def ingeniero(color=AZUL, casco=AMBAR) -> VGroup:
    """Persona con casco."""
    p = persona(color)
    domo = _relleno(Sector(radius=0.215, start_angle=0, angle=PI), casco)
    domo.move_to(p.cabeza.get_center() + UP * 0.07)
    ala = _relleno(RoundedRectangle(width=0.55, height=0.05, corner_radius=0.02), casco)
    ala.next_to(domo, DOWN, buff=0)
    p.add(domo, ala)
    p.casco = VGroup(domo, ala)
    return p


def paciente_acostado(color=CIAN) -> VGroup:
    cabeza = _relleno(Circle(radius=0.15), color)
    cuerpo = _relleno(RoundedRectangle(width=1.0, height=0.26, corner_radius=0.13), color)
    cabeza.next_to(cuerpo, LEFT, buff=0.05)
    g = VGroup(cuerpo, cabeza)
    g.cabeza, g.cuerpo = cabeza, cuerpo
    return g


def burbuja_pensamiento(contenido: Mobject | None = None, color=TXT) -> VGroup:
    globo = Ellipse(width=1.3, height=0.9, color=color, stroke_width=2.5)
    globo.set_fill(FONDO, opacity=1)
    c1 = Circle(radius=0.08, color=color, stroke_width=2.5).set_fill(FONDO, 1)
    c2 = Circle(radius=0.05, color=color, stroke_width=2.5).set_fill(FONDO, 1)
    c1.move_to(globo.get_bottom() + DL * 0.22)
    c2.move_to(c1.get_center() + DL * 0.17)
    g = VGroup(c2, c1, globo)
    if contenido is not None:
        contenido.move_to(globo.get_center())
        g.add(contenido)
    g.globo = globo
    return g


# ============================================================ máquinas y objetos

def maquina_radioterapia(color=INK, color_haz="#ffd166") -> VGroup:
    """Acelerador lineal: columna, brazo con cabezal, camilla y haz."""
    base = _relleno(RoundedRectangle(width=0.9, height=2.0, corner_radius=0.12), color, 0.9)
    brazo = _relleno(RoundedRectangle(width=1.9, height=0.5, corner_radius=0.2), color, 0.9)
    brazo.next_to(base, RIGHT, buff=-0.2).align_to(base, UP).shift(DOWN * 0.1)
    cabezal = _relleno(Polygon([-0.3, 0, 0], [0.3, 0, 0], [0.18, -0.35, 0], [-0.18, -0.35, 0]),
                       color, 1.0)
    cabezal.next_to(brazo, DOWN, buff=0).align_to(brazo, RIGHT).shift(LEFT * 0.35)
    camilla = _relleno(RoundedRectangle(width=2.0, height=0.14, corner_radius=0.05), color, 0.9)
    camilla.next_to(base, RIGHT, buff=0.15).align_to(base, DOWN).shift(UP * 0.4)
    pata = _relleno(Rectangle(width=0.14, height=0.55), color, 0.9)
    pata.next_to(camilla, DOWN, buff=0).shift(RIGHT * 0.3)
    punta = cabezal.get_bottom()
    haz = Polygon(punta + LEFT * 0.08, punta + RIGHT * 0.08,
                  camilla.get_top() + RIGHT * 0.28 + UP * 0.02,
                  camilla.get_top() + LEFT * 0.28 + UP * 0.02)
    haz.set_fill(color_haz, opacity=0.7).set_stroke(width=0)
    g = VGroup(base, brazo, cabezal, pata, camilla, haz)
    g.haz, g.camilla, g.cabezal = haz, camilla, cabezal
    g.scale(1 / g.height)
    return g


def teclado(color=INK, filas: int = 3, columnas: int = 8) -> VGroup:
    marco = RoundedRectangle(width=columnas * 0.32 + 0.2, height=filas * 0.32 + 0.2,
                             corner_radius=0.08, color=color, stroke_width=2.5)
    marco.set_fill(FONDO, 1)
    teclas = VGroup(*[
        RoundedRectangle(width=0.26, height=0.26, corner_radius=0.04, color=color,
                         stroke_width=1.5).set_fill(color, 0.15)
        for _ in range(filas * columnas)
    ]).arrange_in_grid(filas, columnas, buff=0.06)
    teclas.move_to(marco.get_center())
    g = VGroup(marco, teclas)
    g.teclas = teclas
    return g


def monitor_signos(color=VERDE) -> VGroup:
    """Monitor de hospital con línea de pulso."""
    pantalla = RoundedRectangle(width=1.5, height=1.0, corner_radius=0.08, color=INK,
                                stroke_width=2.5).set_fill(FONDO, 1)
    xs = np.linspace(-0.6, 0.6, 13)
    ys = [0, 0, 0, 0.05, -0.05, 0, 0.35, -0.3, 0.05, 0, 0, 0, 0]
    pulso = VMobject(color=color, stroke_width=3).set_points_as_corners(
        [pantalla.get_center() + np.array([x, y, 0]) for x, y in zip(xs, ys)])
    pie = Line(pantalla.get_bottom(), pantalla.get_bottom() + DOWN * 0.2, color=INK, stroke_width=3)
    g = VGroup(pantalla, pulso, pie)
    g.pulso = pulso
    return g


def avion(color=AZUL) -> VGroup:
    fuselaje = _relleno(RoundedRectangle(width=1.8, height=0.28, corner_radius=0.14), color)
    ala = _relleno(Polygon([-0.15, 0, 0], [0.25, 0, 0], [-0.25, -0.75, 0], [-0.5, -0.75, 0]), color)
    ala2 = ala.copy().flip(RIGHT)
    cola = _relleno(Polygon([-0.9, 0, 0], [-0.65, 0, 0], [-0.85, 0.45, 0], [-1.0, 0.45, 0]), color)
    g = VGroup(ala, ala2, fuselaje, cola)
    g.scale(1 / g.height)
    return g


def cohete(color=TXT, color_llama=AMBAR) -> VGroup:
    cuerpo = _relleno(RoundedRectangle(width=0.5, height=1.3, corner_radius=0.1), color)
    nariz = _relleno(Triangle().stretch_to_fit_width(0.5).stretch_to_fit_height(0.45), ROJO)
    nariz.next_to(cuerpo, UP, buff=-0.02)
    ventana = Circle(radius=0.11, color=AZUL, stroke_width=3).set_fill(FONDO, 1)
    ventana.move_to(cuerpo.get_center() + UP * 0.25)
    aleta_i = _relleno(Polygon([0, 0, 0], [0, 0.4, 0], [-0.3, -0.1, 0]), ROJO)
    aleta_i.next_to(cuerpo, LEFT, buff=0).align_to(cuerpo, DOWN)
    aleta_d = aleta_i.copy().flip(UP).next_to(cuerpo, RIGHT, buff=0).align_to(cuerpo, DOWN)
    llama = _relleno(Polygon([-0.18, 0, 0], [0.18, 0, 0], [0, -0.55, 0]), color_llama)
    llama.next_to(cuerpo, DOWN, buff=0)
    g = VGroup(llama, aleta_i, aleta_d, cuerpo, nariz, ventana)
    g.llama, g.cuerpo = llama, cuerpo
    g.scale(1 / g.height)
    return g


def misil(color=TXT) -> VGroup:
    c = cohete(color)
    c.stretch(0.7, 0)
    c.rotate(-PI / 2)
    return c


def banco(color=TXT) -> VGroup:
    techo = _relleno(Triangle().stretch_to_fit_width(1.4).stretch_to_fit_height(0.4), color)
    columnas = VGroup(*[_relleno(Rectangle(width=0.14, height=0.7), color) for _ in range(4)])
    columnas.arrange(RIGHT, buff=0.18).next_to(techo, DOWN, buff=0.06)
    base = _relleno(Rectangle(width=1.4, height=0.12), color)
    base.next_to(columnas, DOWN, buff=0.04)
    g = VGroup(techo, columnas, base)
    g.scale(1 / g.height)
    return g


def reloj(color=TXT) -> VGroup:
    aro = Circle(radius=0.5, color=color, stroke_width=4).set_fill(FONDO, 1)
    marcas = VGroup(*[
        Line(UP * 0.42, UP * 0.5, color=color, stroke_width=3).rotate(i * PI / 6, about_point=ORIGIN)
        for i in range(12)
    ])
    horaria = Line(ORIGIN, UP * 0.25, color=color, stroke_width=5)
    minutero = Line(ORIGIN, UP * 0.38, color=AMBAR, stroke_width=4).rotate(-PI / 2.5, about_point=ORIGIN)
    g = VGroup(aro, marcas, horaria, minutero)
    g.minutero, g.horaria, g.aro = minutero, horaria, aro
    return g


def chip(color=AZUL, texto: str = "") -> VGroup:
    cuerpo = RoundedRectangle(width=1.0, height=1.0, corner_radius=0.08, color=color,
                              stroke_width=3).set_fill(color, 0.15)
    patas = VGroup()
    for i in range(5):
        d = -0.4 + i * 0.2
        for inicio, fin in [((d, 0.5), (d, 0.68)), ((d, -0.5), (d, -0.68)),
                            ((0.5, d), (0.68, d)), ((-0.5, d), (-0.68, d))]:
            patas.add(Line([*inicio, 0], [*fin, 0], color=color, stroke_width=3))
    g = VGroup(patas, cuerpo)
    if texto:
        t = Text(texto, color=TXT, weight=BOLD).scale(0.35).move_to(cuerpo)
        g.add(t)
    g.cuerpo = cuerpo
    return g


def boton(color=ROJO) -> VGroup:
    aro = Circle(radius=0.5, color=INK, stroke_width=4).set_fill(FONDO, 1)
    centro = _relleno(Circle(radius=0.34), color)
    return VGroup(aro, centro)


def documento(color=INK) -> VGroup:
    """Hoja con esquina doblada y renglones (un archivo, un programa quieto)."""
    hoja = Polygon([-0.35, 0.5, 0], [0.2, 0.5, 0], [0.35, 0.35, 0], [0.35, -0.5, 0], [-0.35, -0.5, 0],
                   color=color, stroke_width=3).set_fill(FONDO, 1)
    renglones = VGroup(*[Line([-0.22, y, 0], [0.22, y, 0], color=color, stroke_width=2.5)
                         for y in (0.2, 0.05, -0.1, -0.25)])
    return VGroup(hoja, renglones)


def cruz_hospital(color=ROJO) -> VGroup:
    fondo = RoundedRectangle(width=0.8, height=0.8, corner_radius=0.12, color=TXT,
                             stroke_width=2.5).set_fill(TXT, 0.9)
    brazo_v = _relleno(Rectangle(width=0.2, height=0.55), color)
    brazo_h = _relleno(Rectangle(width=0.55, height=0.2), color)
    return VGroup(fondo, brazo_v, brazo_h)


def lupa(color=TXT) -> VGroup:
    vidrio = Circle(radius=0.32, color=color, stroke_width=6).set_fill(AZUL, 0.12)
    mango = Line(ORIGIN, DR * 0.42, color=color, stroke_width=10)
    mango.shift(vidrio.point_at_angle(-PI / 4) - mango.get_start())
    g = VGroup(mango, vidrio)
    g.vidrio = vidrio
    g.permitir_encima = True    # una lupa pasa sobre textos a propósito
    return g


def disco(color=INK) -> VGroup:
    """Cilindro de almacenamiento."""
    cuerpo = Rectangle(width=1.1, height=0.8, stroke_width=0).set_fill(color, 0.35)
    tapa = Ellipse(width=1.1, height=0.3, color=color, stroke_width=3).set_fill(color, 0.6)
    fondo = Ellipse(width=1.1, height=0.3, color=color, stroke_width=3).set_fill(color, 0.35)
    tapa.move_to(cuerpo.get_top())
    fondo.move_to(cuerpo.get_bottom())
    lados = VGroup(Line(cuerpo.get_corner(UL), cuerpo.get_corner(DL), color=color, stroke_width=3),
                   Line(cuerpo.get_corner(UR), cuerpo.get_corner(DR), color=color, stroke_width=3))
    return VGroup(fondo, cuerpo, lados, tapa)


def receta(color=TXT) -> VGroup:
    doc = documento(color)
    titulo = Text("receta", color=color).scale(0.28).next_to(doc[0].get_top(), DOWN, buff=0.12)
    doc[1][0].set_opacity(0)
    return VGroup(doc, titulo)


def tarta(color=MAGENTA) -> VGroup:
    piso1 = _relleno(RoundedRectangle(width=1.0, height=0.35, corner_radius=0.05), color, 0.9)
    piso2 = _relleno(RoundedRectangle(width=0.7, height=0.3, corner_radius=0.05), color, 0.6)
    piso2.next_to(piso1, UP, buff=0)
    vela = _relleno(Rectangle(width=0.06, height=0.22), TXT).next_to(piso2, UP, buff=0)
    llama = _relleno(Ellipse(width=0.08, height=0.14), AMBAR).next_to(vela, UP, buff=0.01)
    return VGroup(piso1, piso2, vela, llama)


def horno(contenido: Mobject | None = None, color=INK) -> VGroup:
    caja = RoundedRectangle(width=1.8, height=1.5, corner_radius=0.1, color=color,
                            stroke_width=3).set_fill(FONDO, 1)
    ventana = RoundedRectangle(width=1.3, height=0.9, corner_radius=0.08, color=color,
                               stroke_width=2).set_fill(AMBAR, 0.18)
    ventana.move_to(caja.get_center() + DOWN * 0.12)
    perillas = VGroup(*[Circle(radius=0.05, color=color, stroke_width=2) for _ in range(3)])
    perillas.arrange(RIGHT, buff=0.2).next_to(ventana, UP, buff=0.1)
    resistencia = Line(ventana.get_corner(DL) + UP * 0.1 + RIGHT * 0.1,
                       ventana.get_corner(DR) + UP * 0.1 + LEFT * 0.1, color=ROJO, stroke_width=4)
    g = VGroup(caja, ventana, perillas, resistencia)
    if contenido is not None:
        contenido.scale_to_fit_height(0.55).move_to(ventana.get_center() + DOWN * 0.05)
        g.add(contenido)
    g.ventana = ventana
    return g


def ventana_app(titulo: str, color=AZUL, contenido: Mobject | None = None) -> VGroup:
    """Ventana de aplicación de escritorio (para música, navegador, vídeo...)."""
    marco = RoundedRectangle(width=2.2, height=1.5, corner_radius=0.08, color=color,
                             stroke_width=2.5).set_fill(FONDO, 1)
    barra = Rectangle(width=2.2, height=0.28, stroke_width=0).set_fill(color, 0.6)
    barra.align_to(marco, UP)
    puntos = VGroup(*[Dot(radius=0.035, color=TXT) for _ in range(3)]).arrange(RIGHT, buff=0.06)
    puntos.move_to(barra.get_left() + RIGHT * 0.22)
    nombre = Text(titulo, color=TXT).scale(0.26).move_to(barra)
    g = VGroup(marco, barra, puntos, nombre)
    if contenido is not None:
        contenido.scale_to_fit_height(0.75).move_to(marco.get_center() + DOWN * 0.12)
        g.add(contenido)
    g.marco = marco
    return g


def nota_musical(color=MAGENTA) -> VGroup:
    cabeza1 = _relleno(Ellipse(width=0.3, height=0.22), color).rotate(0.4)
    cabeza2 = cabeza1.copy().shift(RIGHT * 0.5 + UP * 0.12)
    palo1 = Line(cabeza1.get_right() + LEFT * 0.02, cabeza1.get_right() + UP * 0.7, color=color, stroke_width=5)
    palo2 = Line(cabeza2.get_right() + LEFT * 0.02, cabeza2.get_right() + UP * 0.7, color=color, stroke_width=5)
    viga = Line(palo1.get_end(), palo2.get_end(), color=color, stroke_width=10)
    return VGroup(cabeza1, cabeza2, palo1, palo2, viga)


def triangulo_play(color=ROJO) -> VMobject:
    return _relleno(Triangle().rotate(-PI / 2).scale(0.3), color)


# ============================================================ marcas

def tilde(color=VERDE, tam: float = 0.5) -> VMobject:
    return VMobject(color=color, stroke_width=8).set_points_as_corners(
        [np.array([-0.5, 0, 0]), np.array([-0.15, -0.35, 0]), np.array([0.5, 0.4, 0])]).scale(tam)


def cruz(color=ROJO, tam: float = 0.5) -> VGroup:
    return VGroup(Line(UL, DR, color=color, stroke_width=8),
                  Line(UR, DL, color=color, stroke_width=8)).scale(tam * 0.5)


def tachar(m: Mobject, color=ROJO) -> Line:
    """Línea diagonal que tacha un mobject."""
    return Line(m.get_corner(DL) + DL * 0.08, m.get_corner(UR) + UR * 0.08,
                color=color, stroke_width=7)


# ============================================================ episodio 2: multitarea, hilos, casa

def ordenador(color=INK) -> VGroup:
    """Monitor de escritorio con pie. `.pantalla` es el área donde van las ventanas."""
    marco = RoundedRectangle(width=1.6, height=1.05, corner_radius=0.08, color=color,
                             stroke_width=3).set_fill(FONDO, 1)
    pantalla = RoundedRectangle(width=1.42, height=0.87, corner_radius=0.05, stroke_width=0)
    pantalla.set_fill(AZUL, 0.08).move_to(marco)
    cuello = _relleno(Rectangle(width=0.16, height=0.18), color)
    cuello.next_to(marco, DOWN, buff=0)
    pie = _relleno(RoundedRectangle(width=0.7, height=0.08, corner_radius=0.03), color)
    pie.next_to(cuello, DOWN, buff=0)
    g = VGroup(marco, pantalla, cuello, pie)
    g.marco, g.pantalla = marco, pantalla
    g.scale(1 / g.height)
    return g


def ventanas_multitarea() -> VGroup:
    """Las tres ventanas de siempre: música, navegador y vídeo (en ese orden)."""
    barras = VGroup(*[Line(LEFT * 0.8, RIGHT * (0.8 - 0.3 * (i % 2)), color=INK, stroke_width=5)
                      for i in range(4)]).arrange(DOWN, buff=0.18, aligned_edge=LEFT)
    return VGroup(ventana_app("música", MAGENTA, nota_musical()),
                  ventana_app("navegador", AZUL, barras),
                  ventana_app("vídeo", ROJO, triangulo_play()))


def batuta(color=TXT) -> VGroup:
    """Batuta de director de orquesta, inclinada."""
    vara = Line(ORIGIN, UR * 0.75, color=color, stroke_width=5)
    mango = _relleno(Ellipse(width=0.22, height=0.14), AMBAR).rotate(PI / 4).move_to(vara.get_start())
    g = VGroup(vara, mango)
    g.scale(1 / g.height)
    return g


def casa(ancho: float = 6.0, alto: float = 3.6, color=INK) -> VGroup:
    """Casa con tejado (contorno). `.muros` y `.tejado` por separado."""
    muros = Rectangle(width=ancho, height=alto, color=color, stroke_width=3.5).set_fill(color, 0.05)
    tejado = Polygon(muros.get_corner(UL) + LEFT * 0.35, muros.get_corner(UR) + RIGHT * 0.35,
                     muros.get_top() + UP * ancho * 0.18, color=color, stroke_width=3.5)
    tejado.set_fill(color, 0.12)
    g = VGroup(muros, tejado)
    g.muros, g.tejado = muros, tejado
    return g


def llave(color=AMBAR) -> VGroup:
    anillo = Circle(radius=0.22, color=color, stroke_width=6)
    cana = Line(anillo.get_right(), anillo.get_right() + RIGHT * 0.7, color=color, stroke_width=6)
    d1 = Line(cana.get_end(), cana.get_end() + DOWN * 0.18, color=color, stroke_width=6)
    d2 = Line(cana.get_end() + LEFT * 0.18, cana.get_end() + LEFT * 0.18 + DOWN * 0.14, color=color, stroke_width=6)
    g = VGroup(anillo, cana, d1, d2)
    g.scale(1 / g.width)
    return g


def escudo(color=VERDE) -> VGroup:
    forma = Polygon([-0.4, 0.45, 0], [0, 0.55, 0], [0.4, 0.45, 0], [0.35, -0.1, 0], [0, -0.5, 0],
                    [-0.35, -0.1, 0], color=color, stroke_width=4).set_fill(color, 0.15)
    marca = tilde(color, 0.45).move_to(forma.get_center() + UP * 0.02)
    g = VGroup(forma, marca)
    g.scale(1 / g.height)
    return g


def candado(color=AMBAR, abierto: bool = False) -> VGroup:
    cuerpo = _relleno(RoundedRectangle(width=0.7, height=0.55, corner_radius=0.08), color, 0.9)
    arco = Arc(radius=0.22, start_angle=0, angle=PI, color=color, stroke_width=7)
    arco.next_to(cuerpo, UP, buff=0)
    if abierto:
        arco.shift(UP * 0.12 + RIGHT * 0.1)
    agujero = _relleno(Circle(radius=0.06), FONDO).move_to(cuerpo)
    g = VGroup(arco, cuerpo, agujero)
    g.scale(1 / g.height)
    return g


def barrera(color=ROJO, largo: float = 2.4) -> VGroup:
    """Barrera de peaje: poste y brazo a franjas. `.brazo` gira sobre `.pivote()`."""
    poste = _relleno(RoundedRectangle(width=0.3, height=1.0, corner_radius=0.05), INK)
    franjas = VGroup(*[
        Rectangle(width=largo / 6, height=0.16, stroke_width=0).set_fill(color if i % 2 == 0 else TXT, 1)
        for i in range(6)]).arrange(RIGHT, buff=0)
    franjas.next_to(poste, RIGHT, buff=0).align_to(poste, UP).shift(DOWN * 0.12)
    g = VGroup(poste, franjas)
    g.brazo, g.poste = franjas, poste
    return g


def burbuja_dialogo(ancho: float = 2.6, alto: float = 1.2, color=TXT) -> VGroup:
    """Globo de diálogo con piquito abajo a la izquierda. `.globo` es el área del texto."""
    globo = RoundedRectangle(width=ancho, height=alto, corner_radius=0.3, color=color, stroke_width=3)
    globo.set_fill(FONDO, 1)
    base = globo.get_bottom()
    pico = Polygon(base + LEFT * ancho * 0.25 + UP * 0.02, base + LEFT * ancho * 0.08 + UP * 0.02,
                   base + LEFT * ancho * 0.32 + DOWN * 0.35, color=color, stroke_width=3)
    pico.set_fill(FONDO, 1)
    g = VGroup(pico, globo)
    g.globo = globo
    return g


def ojo(color=TXT) -> VGroup:
    """Ojo abierto (para 'abrirlo', animar desde una copia aplastada en vertical)."""
    arriba = ArcBetweenPoints(LEFT * 0.7, RIGHT * 0.7, angle=-PI / 2.2, color=color, stroke_width=5)
    abajo = ArcBetweenPoints(LEFT * 0.7, RIGHT * 0.7, angle=PI / 2.2, color=color, stroke_width=5)
    iris = Circle(radius=0.2, color=AMBAR, stroke_width=4).set_fill(AMBAR, 0.35)
    pupila = _relleno(Circle(radius=0.08), TXT)
    g = VGroup(iris, pupila, arriba, abajo)
    g.scale(1 / g.height)
    return g


# ============================================================ episodio 3: carrera, reglas y bloqueos

def dado(color=TXT) -> VGroup:
    """Dado visto de frente, con cinco puntos. `.cara` y `.puntos`."""
    cara = RoundedRectangle(width=1.0, height=1.0, corner_radius=0.18, color=color,
                            stroke_width=4).set_fill(FONDO, 1)
    puntos = VGroup(*[_relleno(Circle(radius=0.09), color).move_to([x, y, 0])
                      for x, y in [(-0.27, 0.27), (0.27, 0.27), (0, 0), (-0.27, -0.27), (0.27, -0.27)]])
    g = VGroup(cara, puntos)
    g.cara, g.puntos = cara, puntos
    return g


def bicho(color=ROJO) -> VGroup:
    """Insecto visto desde arriba (un «bug»): cuerpo, cabeza, seis patas y antenas."""
    cuerpo = _relleno(Ellipse(width=0.55, height=0.75), color, 0.9)
    cabeza = _relleno(Circle(radius=0.15), color).next_to(cuerpo, UP, buff=-0.04)
    raya = Line(cuerpo.get_top(), cuerpo.get_bottom(), color=FONDO, stroke_width=3)
    patas = VGroup()
    for y in (0.18, 0.0, -0.18):
        for lado in (LEFT, RIGHT):
            ini = cuerpo.get_center() + UP * y + lado * 0.25
            patas.add(Line(ini, ini + lado * 0.28 + UP * y * 0.8, color=color, stroke_width=4))
    antenas = VGroup(*[Line(cabeza.get_top(), cabeza.get_top() + UP * 0.2 + lado * 0.15, color=color,
                            stroke_width=3) for lado in (LEFT, RIGHT)])
    g = VGroup(patas, antenas, cuerpo, cabeza, raya)
    g.scale(1 / g.height)
    return g


def semaforo(color=INK) -> VGroup:
    """Semáforo vertical. `.luces` = [roja, ámbar, verde]."""
    caja = RoundedRectangle(width=0.5, height=1.25, corner_radius=0.12, color=color,
                            stroke_width=3).set_fill(FONDO, 1)
    luces = VGroup(*[_relleno(Circle(radius=0.14), c, 0.9) for c in (ROJO, AMBAR, VERDE)])
    luces.arrange(DOWN, buff=0.1).move_to(caja)
    g = VGroup(caja, luces)
    g.luces = luces
    g.scale(1 / g.height)
    return g


def bandera(color=TXT) -> VGroup:
    """Bandera de meta a cuadros sobre un mástil."""
    mastil = Line(DOWN * 0.5, UP * 0.5, color=color, stroke_width=5)
    cuadros = VGroup(*[
        Square(0.16, stroke_width=0).set_fill(color if (i + j) % 2 == 0 else FONDO, 1)
        .move_to([0.08 + 0.16 * i, 0.42 - 0.16 * j, 0])
        for i in range(4) for j in range(3)])
    borde = Rectangle(width=0.64, height=0.48, color=color, stroke_width=2).move_to(cuadros)
    g = VGroup(mastil, cuadros, borde)
    g.scale(1 / g.height)
    return g


# ============================================================ episodio 4: mensajes, modelos y Petri

def sobre(color=TXT) -> VGroup:
    """Sobre de carta (un mensaje). `.cuerpo` y `.solapa`."""
    cuerpo = RoundedRectangle(width=1.4, height=0.9, corner_radius=0.06, color=color,
                              stroke_width=4).set_fill(FONDO, 1)
    solapa = VMobject(color=color, stroke_width=4).set_points_as_corners(
        [cuerpo.get_corner(UL), cuerpo.get_center() + DOWN * 0.08, cuerpo.get_corner(UR)])
    g = VGroup(cuerpo, solapa)
    g.cuerpo, g.solapa = cuerpo, solapa
    g.scale(1 / g.height)
    return g


def engranaje(color=INK, dientes: int = 8) -> VGroup:
    """Rueda dentada (algo que trabaja). Girar con `Rotate`."""
    pts = []
    for i in range(dientes * 2):
        r = 0.5 if i % 2 == 0 else 0.38
        a0, a1 = 2 * PI * i / (dientes * 2), 2 * PI * (i + 1) / (dientes * 2)
        pts += [[r * np.cos(a0), r * np.sin(a0), 0], [r * np.cos(a1), r * np.sin(a1), 0]]
    rueda = Polygon(*pts, color=color, stroke_width=3).set_fill(color, 0.35)
    eje = _relleno(Circle(radius=0.13), FONDO)
    g = VGroup(rueda, eje)
    g.scale(1 / g.height)
    return g


def matraz(color=CIAN) -> VGroup:
    """Matraz de laboratorio con líquido. `.liquido`."""
    vidrio = Polygon([-0.12, 0.5, 0], [0.12, 0.5, 0], [0.12, 0.15, 0], [0.42, -0.45, 0],
                     [-0.42, -0.45, 0], [-0.12, 0.15, 0], color=TXT, stroke_width=4)
    liquido = Polygon([0.27, -0.15, 0], [0.42, -0.45, 0], [-0.42, -0.45, 0], [-0.27, -0.15, 0],
                      stroke_width=0).set_fill(color, 0.7)
    g = VGroup(liquido, vidrio)
    g.liquido = liquido
    g.scale(1 / g.height)
    return g


def cajero(color=INK) -> VGroup:
    """Cajero automático: pantalla, teclado y ranura. `.pantalla`."""
    cuerpo = RoundedRectangle(width=0.8, height=1.0, corner_radius=0.08, color=color,
                              stroke_width=3.5).set_fill(color, 0.12)
    pantalla = RoundedRectangle(width=0.56, height=0.32, corner_radius=0.04, color=CIAN,
                                stroke_width=2.5).set_fill(CIAN, 0.3).move_to(cuerpo.get_top() + DOWN * 0.27)
    teclas = VGroup(*[Square(0.08, stroke_width=0).set_fill(color, 0.9) for _ in range(9)])
    teclas.arrange_in_grid(3, 3, buff=0.04).move_to(cuerpo.get_center() + DOWN * 0.12)
    ranura = Line(LEFT * 0.22, RIGHT * 0.22, color=TXT, stroke_width=4).move_to(cuerpo.get_bottom() + UP * 0.12)
    g = VGroup(cuerpo, pantalla, teclas, ranura)
    g.pantalla = pantalla
    g.scale(1 / g.height)
    return g


def sello(texto: str = "demostrado", color=VERDE) -> VGroup:
    """Sello de goma inclinado con un texto (para «estamparlo» con un pop)."""
    t = Text(texto, color=color, weight=BOLD).scale(0.5)
    marco = RoundedRectangle(width=t.width + 0.5, height=t.height + 0.4, corner_radius=0.1,
                             color=color, stroke_width=6).move_to(t)
    g = VGroup(marco, t)
    g.marco, g.texto = marco, t
    g.rotate(12 * DEGREES)
    return g
