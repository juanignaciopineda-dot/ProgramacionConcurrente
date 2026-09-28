"""Escena 07 — Soluciones tramposas: las reglas 2 y 5."""
from comun.escena import *


def grieta(m: Mobject, color=ROJO) -> VMobject:
    """Zigzag que atraviesa `m` de arriba abajo (una grieta)."""
    arriba, abajo = m.get_top() + LEFT * 0.1, m.get_bottom() + RIGHT * 0.1
    pts = [arriba + (abajo - arriba) * k / 5 + RIGHT * (0.12 if k % 2 else -0.12) for k in range(6)]
    return VMobject(color=color, stroke_width=6).set_points_as_corners(pts)


class SolucionesTramposas(EscenaCurso):
    ESCENA = "07"

    def construct(self):
        with self.narrar("b1"):
            # «la segunda y la quinta»
            self.en("r25")
            lado = 0.8
            reglas = reglas_seis(lado=lado).to_edge(UP, buff=0.35)
            self.play(FadeIn(reglas, shift=DOWN * 0.3), run_time=0.3)
            anims = []
            for i, r in enumerate(reglas):
                if i in (1, 4):
                    anims += [r.animate.scale(1.25), r.caja.animate.set_stroke(AMBAR, 5)]
                else:
                    anims += [r.caja.animate.set_stroke(INK, 3, opacity=0.35), r.num.animate.set_opacity(0.35)]
            self.sfx("tick", gain=-12)
            self.play(*anims, run_time=0.35)

            # «soluciones tramposas» — un candado con una grieta
            self.en("tramposas")
            cand = candado(AMBAR).scale(2.4).move_to(LEFT * 3.4 + DOWN * 0.4)
            self.play(FadeIn(cand, scale=0.6), run_time=0.3)
            g = grieta(cand)
            self.sfx("impacto", gain=-14)
            self.play(Create(g), run_time=0.3)

            # «casi siempre» — veinte ejecuciones, una roja
            self.en("casi")
            grilla = grilla_ejecuciones(20, malas=(16,), columnas=5).scale(1.2).move_to(RIGHT * 3.4 + DOWN * 0.4)
            self.play(LaggedStart(*[FadeIn(c, scale=0.5) for c in grilla.celdas], lag_ratio=0.03), run_time=0.5)
            self.play(grilla.malas.animate(rate_func=there_and_back).scale(1.5), run_time=0.4)

            # «La segunda exige» — tres procesos alrededor de la sala
            self.en("r2")
            self.play(FadeOut(VGroup(cand, g, grilla)), run_time=0.2)
            sala = SalaPuerta(4.0, 3.0).move_to(RIGHT * 2.4 + DOWN * 0.5)
            p1 = proceso_mini("P1", AZUL).move_to(sala.dentro)
            p2 = proceso_mini("P2", MAGENTA).move_to(sala.fuera)
            p3 = proceso_mini("P3", CIAN).move_to(sala.fuera + LEFT * 2.6 + DOWN * 1.2)
            self.play(FadeIn(sala), FadeIn(p1), FadeIn(p2), FadeIn(p3), run_time=0.4)

            # «se cae» — P3, fuera de la sala, gira y cae
            self.en("cae")
            self.sfx("error", gain=-12)
            self.play(p3.animate.rotate(-PI / 2).shift(DOWN * 0.9).set_color(ROJO), run_time=0.5,
                      rate_func=rate_functions.ease_in_quad)
            self.play(p3.animate.fade(0.6), run_time=0.2)

            # «no arrastre a los demás» — los otros dos siguen con normalidad
            self.en("arrastre")
            self.play(sala.abrir(), run_time=0.2)
            self.play(p1.animate.move_to(sala.fuera + UP * 1.3), p2.animate.move_to(sala.dentro), run_time=0.45)
            self.play(sala.cerrar(), run_time=0.2)
            ok = tilde(VERDE, 0.9).next_to(sala, RIGHT, buff=0.3)
            self.play(Create(ok), run_time=0.3)

            # «La quinta prohíbe» — corte a la pista
            self.en("r5")
            self.play(FadeOut(VGroup(sala, p1, p2, p3, ok)), run_time=0.2)
            anims = []
            for i, r in enumerate(reglas):
                if i == 4:
                    anims.append(r.animate.scale(1.25))
                elif i == 1:
                    anims += [r.animate.scale(1 / 1.25), r.caja.animate.set_stroke(INK, 3, opacity=0.35),
                              r.num.animate.set_opacity(0.35)]
            self.play(*anims, run_time=0.3)
            p = pista(ancho=10.0, carriles=2, alto_carril=1.2).move_to(LEFT * 1.3 + DOWN * 0.8)
            r1 = proceso_mini("P1", AZUL).move_to(p.carril(0) + RIGHT * 0.8)
            r2 = proceso_mini("P2", MAGENTA).move_to(p.carril(1) + RIGHT * 0.8)
            self.play(FadeIn(p), FadeIn(r1), FadeIn(r2), run_time=0.35)

            # «la velocidad» — uno rápido, otro despacio
            self.en("velocidad")
            self.sfx("whoosh", gain=-14)
            self.play(r1.animate.shift(RIGHT * 6.5), r2.animate.shift(RIGHT * 0.8), run_time=1.2)

            # «cuántos procesadores»
            self.en("procesadores")
            ch = chip(CIAN).scale(1.1).move_to(RIGHT * 5.6 + UP * 1.3)
            self.pop(ch)

            # «funcionar igual»
            self.en("igual")
            ok = tilde(VERDE, 1.0).next_to(p, RIGHT, buff=0.3).shift(DOWN * 0.2)
            self.play(Create(ok), run_time=0.3)

            # «mil veces más lento»
            self.en("lento")
            mil = Text("×1000", color=MAGENTA, weight=BOLD).scale(0.8).next_to(r2, UP, buff=0.3)
            self.pop(mil)
            self.play(r2.animate.shift(RIGHT * 0.15), run_time=1.0, rate_func=linear)

            # «con mil núcleos» — el chip se multiplica
            self.en("nucleos")
            muchos = VGroup(*[chip(CIAN).scale(0.28) for _ in range(24)]).arrange_in_grid(4, 6, buff=0.18)
            muchos.move_to(ch).shift(LEFT * 0.3 + UP * 0.35)
            self.sfx("pop")
            self.play(ReplacementTransform(VGroup(*[ch.copy() for _ in range(24)]), muchos), FadeOut(ch), run_time=0.6)

        self.wait(1.0)
        self.limpiar()
