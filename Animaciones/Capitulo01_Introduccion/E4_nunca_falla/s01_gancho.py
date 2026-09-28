"""Escena 01 — Gancho: probar no basta; hoy se demuestra con una red de Petri."""
from comun.escena import *


class Gancho(EscenaCurso):
    ESCENA = "01"

    def construct(self):
        with self.narrar("b1"):
            # «Un millón de pruebas» — una grilla enorme de tildes verdes
            self.en("millon")
            tildes = VGroup(*[tilde(VERDE, 0.38) for _ in range(72)]).arrange_in_grid(6, 12, buff=(0.62, 0.5))
            for i in range(10):
                self.sfx("tecla", gain=-18, offset=i * 0.11)
            self.play(LaggedStart(*[FadeIn(t, scale=0.4) for t in tildes], lag_ratio=0.015), run_time=1.2)

            # «no demuestran nada» — se atenúan de golpe
            self.en("nada")
            self.play(tildes.animate.fade(0.7), run_time=0.3)

            # «un solo intercalado» — una casilla se vuelve roja
            self.en("intercalado")
            mala = cruz(ROJO, 0.4).move_to(tildes[40])
            self.sfx("impacto", gain=-10)
            self.play(FadeOut(tildes[40]), FadeIn(mala, scale=2.2), run_time=0.3)
            self.play(Wiggle(mala, scale_value=1.4), run_time=0.45)

            # «el programa falle» — la cruz crece al centro y la grilla se va
            self.en("falla")
            self.sfx("alarma", gain=-16)
            self.play(FadeOut(tildes, scale=0.8), mala.animate.scale(5).move_to(ORIGIN), run_time=0.6)

            # «otro camino» — bifurcación: probar / demostrar
            self.en("camino")
            self.corte()
            tronco = Line([-6, -0.3, 0], [-2.2, -0.3, 0], color=INK, stroke_width=8)
            arriba = Line(tronco.get_end(), [1.8, 1.6, 0], color=INK, stroke_width=8)
            abajo = Line(tronco.get_end(), [1.8, -2.2, 0], color=INK, stroke_width=8)
            probar = Text("probar", color=INK, weight=BOLD).scale(0.9).next_to(abajo.get_end(), RIGHT, buff=0.3)
            demostrar = Text("demostrar", color=INK, weight=BOLD).scale(0.9).next_to(arriba.get_end(), RIGHT, buff=0.3)
            self.play(Create(tronco), Create(arriba), Create(abajo), FadeIn(probar), FadeIn(demostrar), run_time=0.5)
            self.sfx("whoosh")
            self.play(arriba.animate.set_stroke(AMBAR, 12), demostrar.animate.set_color(AMBAR).scale(1.25),
                      abajo.animate.set_stroke(opacity=0.35), probar.animate.fade(0.6), run_time=0.45)

            # «cinco círculos» — los cinco lugares, en cadena
            self.en("circulos")
            self.corte()
            red = RedMutex(fichas=False).scale(1.05).move_to(DOWN * 0.1)
            for i in range(5):
                self.sfx("pop", offset=i * 0.14)
            self.play(LaggedStart(*[GrowFromCenter(l) for l in red.lugares], lag_ratio=0.2), run_time=0.8)

            # «cuatro barras» — las transiciones y los arcos
            self.en("barras")
            red.f1.poner_fichas(1)
            red.f2.poner_fichas(1)
            self.play(LaggedStart(*[FadeIn(t, scale=0.5) for t in red.transiciones], lag_ratio=0.15),
                      Create(red.flechas), run_time=0.65)

            # «una sola ficha» — cae en mutex
            self.en("ficha")
            self.sfx("tick", gain=-10)
            soltar_ficha(self, red.mutex, run_time=0.45)
            self.resaltar(red.mutex.circulo, color=AMBAR, escala=1.25, run_time=0.4)

            # «vamos a demostrar» — la red dispara: P1 entra
            self.en("demostrar")
            self.sfx("whoosh", gain=-18)
            red.entrar(self, 1, run_time=0.8)

            # «entrarán a la vez» — los dos lugares de sección crítica: nunca juntos
            self.en("nunca")
            nunca = Text("nunca juntos", color=AMBAR, weight=BOLD).scale(0.95).move_to(UP * 3.45)
            self.play(red.sc1.circulo.animate.set_stroke(AMBAR, 7), red.sc2.circulo.animate.set_stroke(AMBAR, 7),
                      run_time=0.3)
            self.pop(nunca, sonido="impacto")

            # «su sección crítica» — el título del episodio, solo
            self.en("titulo")
            self.corte()
            tit = self.texto_centrado("Demostrar que", "nunca falla", escala=1.35)
            tit[1].set_color(AMBAR)
            self.sfx("whoosh")
            self.play(FadeIn(tit, scale=0.8), run_time=0.5)

        self.wait(1.0)
        self.limpiar()
