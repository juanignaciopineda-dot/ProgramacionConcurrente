"""Escena 01 — Gancho: seis pacientes, una máquina, un error que ningún test encontró."""
from comun.escena import *


class Gancho(EscenaCurso):
    ESCENA = "01"

    def construct(self):
        with self.narrar("b1"):
            # «Seis pacientes»
            self.en("seis")
            fila = VGroup(*[persona(TXT) for _ in range(6)]).arrange(RIGHT, buff=0.5).scale(1.8)
            self.sfx("latido")
            self.play(LaggedStart(*[GrowFromCenter(p) for p in fila], lag_ratio=0.12), run_time=0.7)

            # «Una máquina»
            self.en("maquina")
            maq = maquina_radioterapia().scale(4.8).move_to(RIGHT * 3.3 + DOWN * 0.2)
            self.sfx("whoosh")
            self.play(fila.animate.scale(0.4).to_edge(LEFT, buff=0.4),
                      FadeIn(maq, shift=LEFT * 2), run_time=0.6)
            # «que funcionaba perfecto»
            self.en("perfecto")
            ok = tilde(VERDE, 1.1).next_to(maq, UP, buff=0.2)
            self.sfx("exito")
            self.play(Create(ok), run_time=0.35)

            # «Y un error»
            self.en("error")
            mal = cruz(ROJO, 1.1).move_to(ok)
            flash = FullScreenRectangle().set_fill(ROJO, 0.3).set_stroke(width=0)
            self.sfx("error", gain=-10)
            self.play(ReplacementTransform(ok, mal), maq.haz.animate.set_fill(ROJO, 0.95).stretch(2.0, 0),
                      FadeIn(flash, rate_func=there_and_back), run_time=0.45)
            self.remove(flash)

            # «escribía demasiado rápido» — corte
            self.en("teclas")
            self.corte()
            tec = teclado().scale(3.0)
            self.add(tec)
            self.rafaga(tec.teclas, n=18, run_time=1.4)

            # «Ningún test lo encontró» — corte
            self.en("tests")
            self.corte()
            contador = Integer(0, color=VERDE, group_with_commas=False).scale(3.2).move_to(UP * 1.2)
            rotulo = Text("tests superados", color=INK).scale(0.7).next_to(contador, DOWN, buff=0.3)
            ticks = VGroup(*[tilde(VERDE, 0.4) for _ in range(24)]).arrange_in_grid(2, 12, buff=0.35)
            ticks.next_to(rotulo, DOWN, buff=0.6)
            self.add(contador, rotulo)
            for i in range(10):
                self.sfx("tick", gain=-20, offset=i * 0.13)
            self.play(ChangeDecimalToValue(contador, 1000, rate_func=rate_functions.ease_in_quad),
                      LaggedStart(*[FadeIn(t, scale=1.6) for t in ticks], lag_ratio=0.08),
                      run_time=1.4)

            # «Hoy vamos a entender por qué» — corte a negro
            self.en("porque")
            self.corte()
            q = Text("?", color=AMBAR, weight=BOLD).scale(7)
            self.pop(q, sonido="impacto")

            # «el corazón»
            self.en("corazon")
            for _ in range(2):
                self.sfx("latido", gain=-10)
                self.play(q.animate(rate_func=there_and_back).scale(1.25), run_time=0.45)

            # «toda una materia» — título
            self.en("titulo")
            tit = self.texto_centrado("El error que", "ningún test encontró", escala=1.3)
            self.sfx("whoosh")
            self.play(FadeOut(q, scale=3), FadeIn(tit, scale=0.8), run_time=0.6)

        self.wait(1.0)
        self.limpiar()
