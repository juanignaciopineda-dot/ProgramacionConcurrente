"""Paleta y helpers compartidos por todas las escenas del curso.

Misma idea que en las animaciones de Procesamiento de Señales: una sola
fuente de verdad para colores y piezas de texto, para que todos los
capítulos se vean de la misma familia.
"""
from manim import *
import numpy as np

# ------------------------------------------------------------------ paleta
INK = "#9aa3b8"      # ejes, texto secundario, líneas neutras
AZUL = "#4c8dff"
AMBAR = "#f0883e"
VERDE = "#3fb950"
MAGENTA = "#c86bd8"
ROJO = "#ff6b6b"
CIAN = "#39c5cf"
FONDO = "#12141a"

TXT = "#e6e9f0"      # texto principal

# El color de fondo hay que fijarlo al importar el módulo: si se setea
# dentro de construct() la escena ya se instanció y no lo toma.
config.background_color = FONDO

# Tipografía sans moderna (la de Manim por defecto se ve con serifa).
FUENTE = "Segoe UI"
Text.set_default(font=FUENTE)


def configurar():
    """Se conserva por compatibilidad; el fondo ya se fija al importar."""
    config.background_color = FONDO


# ------------------------------------------------------------------ texto
def titulo(txt, color=TXT, scale=0.68):
    """Título de escena, arriba a la izquierda."""
    t = Text(txt, color=color, weight=BOLD).scale(scale)
    t.to_corner(UL, buff=0.45)
    return t


def subtitulo(txt, scale=0.55, color=INK):
    return Text(txt, color=color).scale(scale)


def conclusion(txt, color=TXT, scale=0.6):
    """Cartel de cierre, abajo, resaltado."""
    t = Text(txt, color=color).scale(scale)
    t.to_edge(DOWN, buff=0.6)
    return t


def nota(txt, scale=0.45, color=INK):
    return Text(txt, color=color, slant=ITALIC).scale(scale)


def etiqueta(txt, color=TXT, scale=0.5, weight=NORMAL):
    return Text(txt, color=color, weight=weight).scale(scale)


# ------------------------------------------------------------------ ejes
def ejes(x_rango, y_rango, ancho=6.0, alto=3.2, x_label=None, y_label=None,
         x_nums=None, y_nums=None, tip=True):
    """Ejes con el estilo del curso: finos, en tinta, con punta de flecha."""
    ax = Axes(
        x_range=x_rango,
        y_range=y_rango,
        x_length=ancho,
        y_length=alto,
        axis_config={
            "color": INK,
            "stroke_width": 2,
            "include_ticks": False,
            "tip_width": 0.16,
            "tip_height": 0.16,
            "include_tip": tip,
        },
    )
    etiquetas = VGroup()
    if x_label is not None:
        lx = MathTex(x_label, color=INK).scale(0.6)
        lx.next_to(ax.x_axis.get_end(), DR, buff=0.12)
        etiquetas.add(lx)
    if y_label is not None:
        ly = MathTex(y_label, color=INK).scale(0.6)
        ly.next_to(ax.y_axis.get_end(), UR, buff=0.08)
        etiquetas.add(ly)
    if x_nums:
        ax.x_axis.add_numbers(x_nums, font_size=22, color=INK)
    if y_nums:
        ax.y_axis.add_numbers(y_nums, font_size=22, color=INK)
    return ax, etiquetas


# ------------------------------------------------------------------ cajas y bloques
def caja(txt, color=AZUL, ancho=None, alto=0.9, scale=0.55, relleno=0.12):
    """Caja rectangular con texto centrado: procesos, hilos, recursos, etc."""
    t = Text(txt, color=TXT).scale(scale)
    if ancho is None:
        ancho = t.width + 0.6
    r = RoundedRectangle(corner_radius=0.12, width=ancho, height=alto,
                          color=color, fill_color=color, fill_opacity=relleno,
                          stroke_width=2.5)
    t.move_to(r.get_center())
    return VGroup(r, t)


def flecha(inicio, fin, color=INK, ancho=2.5, **kw):
    return Arrow(inicio, fin, color=color, stroke_width=ancho,
                 buff=0.05, max_tip_length_to_length_ratio=0.15, **kw)
