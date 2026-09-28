"""Escena 01 — Gancho: uno más uno igual a uno."""
from comun.escena import *


class Gancho(EscenaCurso):
    ESCENA = "01"

    def construct(self):
        with self.narrar("b1"):
            # «Uno más uno»
            self.en("suma")
            suma = Text("1 + 1", color=TXT, weight=BOLD).scale(3.2)
            self.pop(suma, sonido="impacto")

            # «igual a uno»
            self.en("igual")
            igual = Text("= 1", color=ROJO, weight=BOLD).scale(3.2)
            ecuacion = VGroup(suma.copy(), igual).arrange(RIGHT, buff=0.8)
            self.sfx("error", gain=-12)
            self.play(suma.animate.move_to(ecuacion[0]), FadeIn(igual, shift=LEFT * 0.5), run_time=0.45)
            self.remove(ecuacion[0])
            ecuacion = VGroup(suma, igual)

            # «No es un chiste»
            self.en("chiste")
            self.play(Wiggle(ecuacion, scale_value=1.08, rotation_angle=0.04), run_time=0.7)

            # «error de cálculo»
            self.en("calculo")
            self.play(ecuacion.animate.scale(0.45).to_edge(UP, buff=0.45), run_time=0.45)

            # «un programa»
            self.en("programa")
            codigo = Text("contador += 1", color=TXT, font="Consolas").scale(0.9)
            prog = ventana_app("programa", AZUL, codigo).scale(2.2).move_to(DOWN * 0.4)
            codigo.scale_to_fit_width(prog.marco.width * 0.8).move_to(prog.marco.get_center() + DOWN * 0.15)
            self.sfx("whoosh")
            self.play(FadeIn(prog, shift=UP * 0.6), run_time=0.5)

            # «ningún error»
            self.en("sinerror")
            ok = tilde(VERDE, 1.4).next_to(prog, RIGHT, buff=0.5)
            self.play(Create(ok), run_time=0.35)
            self.sfx("exito", gain=-16)

            # «Dos hilos»
            self.en("hilos")
            h1 = hilo("H1", AZUL).scale(1.7).move_to(LEFT * 4.6 + DOWN * 1.0)
            h2 = hilo("H2", MAGENTA).scale(1.7).move_to(RIGHT * 4.6 + DOWN * 1.0)
            self.play(prog.animate.scale(0.65).move_to(UP * 0.9), FadeOut(ok),
                      FadeIn(h1, shift=RIGHT), FadeIn(h2, shift=LEFT), run_time=0.6)

            # «una suma»
            self.en("unasuma")
            mas1 = Text("+1", color=AZUL, weight=BOLD).scale(1.5).next_to(h1, UP, buff=0.35)
            mas2 = Text("+1", color=MAGENTA, weight=BOLD).scale(1.5).next_to(h2, UP, buff=0.35)
            self.pop(mas1, mas2)

            # «la mala suerte del orden»
            self.en("orden")
            self.sfx("whoosh", gain=-16)
            self.play(Swap(mas1, mas2, path_arc=PI / 2), run_time=0.5)
            self.play(Swap(mas1, mas2, path_arc=PI / 2), run_time=0.4)
            d = dado().scale(1.1).move_to(UP * 4.5 + LEFT * 1.0)
            self.sfx("tick", gain=-12, offset=0.35)
            self.sfx("tick", gain=-14, offset=0.6)
            self.play(d.animate.move_to(DOWN * 2.6 + RIGHT * 0.2).rotate(-2 * PI), run_time=0.7,
                      rate_func=rate_functions.ease_out_bounce)

            # «paso a paso» — título solo
            self.en("titulo")
            self.corte()
            tit = self.texto_centrado("Uno más uno", "igual a uno", escala=1.35)
            tit[1].set_color(AMBAR)
            self.sfx("whoosh")
            self.play(FadeIn(tit, scale=0.8), run_time=0.5)

        self.wait(1.0)
        self.limpiar()
