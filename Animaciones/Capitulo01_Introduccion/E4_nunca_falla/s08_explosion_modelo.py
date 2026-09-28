"""Escena 08 — Máquinas de estados y la explosión del modelo; el salto a Petri."""
from comun.escena import *


def grilla_puntos(n: int = 10, sep: float = 0.34, r: float = 0.07, color=INK) -> VGroup:
    return VGroup(*[Dot(radius=r, color=color) for _ in range(n * n)]).arrange_in_grid(n, n, buff=sep - 2 * r)


class ExplosionModelo(EscenaCurso):
    ESCENA = "08"

    def construct(self):
        self.explota()
        self.otro()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def explota(self):
        with self.narrar("b1"):
            # «máquinas de estados» — tres estados con flechas
            self.en("fsm")
            m = maquina_estados_mini(AZUL, lado=4.0, radio=0.6).move_to(LEFT * 2.0 + DOWN * 0.2)
            self.play(LaggedStart(*[GrowFromCenter(e) for e in m.estados], lag_ratio=0.25),
                      Create(m.flechas), run_time=0.8)

            # «son intuitivas» — un punto recorre los estados
            self.en("intuitivas")
            cs = [e.get_center() for e in m.estados]
            pt = Dot(cs[0], radius=0.16, color=AMBAR)
            self.add(pt)
            self.play(MoveAlongPath(pt, VMobject().set_points_as_corners([*cs, cs[0]])), run_time=1.0,
                      rate_func=rate_functions.linear)

            # «se dibujan fácil»
            self.en("facil")
            ok = tilde(VERDE, 1.0).move_to(RIGHT * 1.6 + UP * 1.8)
            self.play(Create(ok), FadeOut(pt), run_time=0.3)

            # «cualquiera que» — una persona junto a la máquina
            self.en("cualquiera")
            per = persona(CIAN).scale(1.6).move_to(LEFT * 5.8 + DOWN * 1.4)
            self.pop(per)

            # «orientado a objetos» — una ventana de diseño con el mismo diagrama
            self.en("objetos")
            vent = ventana_app("diseño", CIAN, maquina_estados_mini(CIAN)).scale(1.8).move_to(RIGHT * 4.3 + DOWN * 0.7)
            self.play(FadeIn(vent, shift=LEFT * 0.4), run_time=0.45)

            # «un límite serio» — todo se vuelve ámbar
            self.en("limite")
            self.sfx("tick", gain=-10)
            self.play(m.estados.animate.set_stroke(AMBAR).set_fill(AMBAR, 0.2), m.flechas.animate.set_color(AMBAR),
                      ok.animate.fade(0.7), run_time=0.4)

            # «dos procesos» — dos anillos de estados
            self.en("dos")
            self.corte()
            a1 = anillo_estados(10, 1.6, AZUL).move_to(LEFT * 4.9 + UP * 0.9)
            a2 = anillo_estados(10, 1.6, MAGENTA).move_to(RIGHT * 4.9 + UP * 0.9)
            self.play(Create(a1), Create(a2), run_time=0.8)

            # «diez estados posibles»
            self.en("diez")
            d1 = Text("10 estados", color=AZUL, weight=BOLD).scale(0.65).next_to(a1, DOWN, buff=0.3)
            d2 = Text("10 estados", color=MAGENTA, weight=BOLD).scale(0.65).next_to(a2, DOWN, buff=0.3)
            self.pop(d1, d2)

            # «cien estados» — la grilla de 10×10 se llena
            self.en("cien")
            g = grilla_puntos().move_to(UP * 0.9)
            for i in range(8):
                self.sfx("tick", gain=-20, offset=i * 0.14)
            self.play(LaggedStart(*[FadeIn(d, scale=0.3) for d in g], lag_ratio=0.01), run_time=1.1)
            cien = Text("100", color=AMBAR, weight=BOLD).scale(1.2).next_to(g, DOWN, buff=0.4)
            self.pop(cien, sonido="impacto")

            # «todas las combinaciones» — una fila, una columna y su cruce
            self.en("combinaciones")
            fila, col = 3, 6
            self.play(*[g[fila * 10 + j].animate.set_color(AZUL) for j in range(10)],
                      *[g[i * 10 + col].animate.set_color(MAGENTA) for i in range(10)],
                      a1.estados[fila].animate.set_fill(AZUL, 1), a2.estados[col].animate.set_fill(MAGENTA, 1),
                      run_time=0.35)
            self.play(g[fila * 10 + col].animate.set_color(AMBAR).scale(2.4), run_time=0.3)

            # «tres procesos» — un tercer anillo
            self.en("tres")
            a3 = anillo_estados(10, 1.15, CIAN).move_to(DOWN * 2.55)
            self.play(FadeOut(cien), Create(a3), run_time=0.7)

            # «mil» — la grilla se vuelve un cubo de puntos
            self.en("mil")
            capas = VGroup(*[g.copy().fade(0.35 + 0.06 * k).shift(RIGHT * 0.13 * k + UP * 0.1 * k)
                             for k in range(1, 9)])
            mil = Text("1.000", color=AMBAR, weight=BOLD).scale(1.2).move_to(LEFT * 3.4 + DOWN * 2.6)
            self.sfx("impacto", gain=-10)
            self.play(LaggedStart(*[FadeIn(c) for c in capas], lag_ratio=0.1), run_time=0.7)
            self.pop(mil, sonido=None)

            # «inmanejable» — el grafo crece hasta salirse de cuadro
            self.en("inmanejable")
            caos = VGroup(a1, a2, a3, g, capas, d1, d2, mil)
            for t in (d1, d2, mil):
                for x in t.get_family():
                    x.permitir_fuera = True
            self.play(caos.animate.scale(2.6, about_point=UP * 0.3), run_time=1.2,
                      rate_func=rate_functions.ease_in_quad)

            # «explosión de estados» — el caos se apaga y aparece el nombre
            self.en("explosion")
            pal = self.palabra("explosión de estados", AMBAR, 1.3)
            self.play(caos.animate.fade(0.93), run_time=0.3)
            self.pop(pal, sonido="impacto")

            # «el segundo episodio» — miniatura del árbol de E2
            self.en("ep2")
            arb = ArbolIntercalado(2, 2, ancho=2.6, alto=1.6, etiquetas=False).to_corner(UR, buff=0.5)
            et = Text("Episodio 2", color=INK, weight=BOLD).scale(0.45).next_to(arb, DOWN, buff=0.2)
            self.play(FadeIn(arb), FadeIn(et), run_time=0.4)

            # «en el propio modelo» — el sello «modelo»
            self.en("modelo")
            s = sello("modelo", ROJO).scale(1.6).move_to(DOWN * 1.8)
            self.pop(s, sonido="impacto")

    # ------------------------------------------------------------------ b2
    def otro(self):
        with self.narrar("b2"):
            # «otro formalismo» — el caos, encogido a la izquierda
            self.en("otro")
            self.corte()
            g = grilla_puntos(sep=0.3, r=0.06, color=ROJO).move_to(LEFT * 3.9 + UP * 0.4)
            self.play(FadeIn(g, scale=0.4), run_time=0.4)

            # «mucho más compacto» — el mismo sistema como red de Petri: dos ciclos
            self.en("compacto")
            r1 = anillo_estados(10, 1.05, AZUL, radio_estado=0.15, barras=True).move_to(RIGHT * 2.3 + UP * 0.4)
            r2 = anillo_estados(10, 1.05, MAGENTA, radio_estado=0.15, barras=True).move_to(RIGHT * 5.0 + UP * 0.4)
            self.play(FadeIn(r1, scale=0.6), FadeIn(r2, scale=0.6), run_time=0.6)

            # «sin dibujar cada combinación» — 100 contra 20
            self.en("sin")
            n1 = Text("100 estados", color=ROJO, weight=BOLD).scale(0.8).next_to(g, DOWN, buff=0.5)
            n2 = Text("20 lugares", color=VERDE, weight=BOLD).scale(0.8).next_to(VGroup(r1, r2), DOWN, buff=0.5)
            n2.align_to(n1, UP)
            self.pop(n1, n2)

            # «las Redes de Petri»
            self.en("petri")
            pal = self.palabra("Redes de Petri", AMBAR, 1.4).move_to(UP * 3.0)
            self.pop(pal, sonido="impacto")

            # «donde vamos» — la cámara se acerca a la red
            self.en("vamos")
            self.zoom(VGroup(r1, r2, n2), ancho=8.0, run_time=0.8)
