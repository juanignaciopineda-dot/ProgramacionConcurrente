"""Escena 05 — Sección crítica y exclusión mutua; seis reglas para cualquier solución."""
from comun.escena import *

CODIGO = ["x = leer()", "contador += 1", "print(contador)", "guardar(x)"]


class SeccionCritica(EscenaCurso):
    ESCENA = "05"

    def construct(self):
        self.m = montaje_contador("1")
        self.play(FadeIn(self.m), run_time=0.3)
        self.la_regla()
        self.seis_reglas()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def la_regla(self):
        with self.narrar("b1"):
            # «una sola razón»
            self.en("razon")
            m = self.m
            self.resaltar(m.celda.caja, color=AMBAR, escala=1.1)

            # «sin ningún control» — los dos se lanzan a la vez sobre la celda y chocan
            self.en("control")
            self.play(m.h1.caja.animate.move_to(m.celda.caja.get_bottom() + DOWN * 0.8 + LEFT * 1.45),
                      m.h2.caja.animate.move_to(m.celda.caja.get_bottom() + DOWN * 0.8 + RIGHT * 1.45),
                      run_time=0.4, rate_func=rate_functions.ease_in_quad)
            self.sfx("impacto", gain=-10)
            self.play(Wiggle(m.celda, scale_value=1.1), run_time=0.4)

            # «el mismo recurso»
            self.en("recurso")
            self.play(m.celda.caja.animate.set_stroke(ROJO, 7), run_time=0.3)

            # «la parte del código» — el código de un hilo
            self.en("parte")
            self.corte()
            lineas = VGroup(*[Text(l, color=INK, font="Consolas").scale(0.85) for l in CODIGO])
            lineas.arrange(DOWN, aligned_edge=LEFT, buff=0.35).move_to(LEFT * 3.6 + UP * 0.4)
            self.play(LaggedStart(*[FadeIn(l, shift=RIGHT * 0.2) for l in lineas], lag_ratio=0.15), run_time=0.6)

            # «sección crítica» — las líneas que tocan contador
            self.en("seccion")
            sc = SurroundingRectangle(lineas[1:3], color=AMBAR, buff=0.2, corner_radius=0.12, stroke_width=5)
            et_sc = Text("sección crítica", color=AMBAR, weight=BOLD).scale(0.75).next_to(lineas, DOWN, buff=0.5)
            self.play(Create(sc), lineas[1:3].animate.set_color(TXT), run_time=0.4)
            self.pop(et_sc)

            # «recurso crítico»
            self.en("recursoc")
            celda = celda_memoria("contador", "1", ancho=1.6, alto=0.9).scale(1.8).move_to(RIGHT * 4.0 + UP * 0.4)
            et_rc = Text("recurso crítico", color=ROJO, weight=BOLD).scale(0.75).next_to(celda, DOWN, buff=0.5)
            self.play(FadeIn(celda, shift=LEFT * 0.4), run_time=0.35)
            self.pop(et_rc)

            # «La regla que necesitamos imponer» — un candado entre los dos
            self.en("regla")
            cand = candado(AMBAR).scale(1.1).move_to(RIGHT * 0.75 + UP * 0.4)
            self.pop(cand, sonido="tick")

            # «en una frase» — la sala con su puerta
            self.en("frase")
            self.corte()
            sala = SalaPuerta(4.4, 3.4).move_to(RIGHT * 2.2 + DOWN * 0.3)
            self.play(FadeIn(sala), run_time=0.4)

            # «solo un proceso» — P1 entra; P2 espera fuera
            self.en("uno")
            p1 = proceso_mini("P1", AZUL).move_to(sala.fuera)
            p2 = proceso_mini("P2", MAGENTA).move_to(sala.fuera + LEFT * 1.9)
            self.play(FadeIn(p1, shift=RIGHT * 0.3), FadeIn(p2, shift=RIGHT * 0.3), run_time=0.3)
            self.play(sala.abrir(), run_time=0.25)
            self.play(p1.animate.move_to(sala.dentro), p2.animate.move_to(sala.fuera), run_time=0.45)
            self.play(sala.cerrar(), run_time=0.25)

            # «a la vez» — P2 intenta y rebota
            self.en("vez")
            self.play(p2.animate.shift(RIGHT * 0.45), run_time=0.2, rate_func=rate_functions.ease_in_quad)
            self.sfx("error", gain=-14)
            self.play(p2.animate.shift(LEFT * 0.45), run_time=0.3, rate_func=rate_functions.ease_out_bounce)

            # «la exclusión mutua»
            self.en("exclusion")
            pal = self.palabra("exclusión mutua", AMBAR, 1.1).next_to(sala, UP, buff=0.35).set_x(0)
            self.pop(pal, sonido="impacto")
            self.sala, self.pal = sala, pal

    # ------------------------------------------------------------------ b2
    def seis_reglas(self):
        sala = self.sala
        with self.narrar("b2"):
            # «no es tan simple»
            self.en("simple")
            self.play(Wiggle(sala.puerta, scale_value=1.2, rotation_angle=0.15), run_time=0.6)

            # «no cualquier solución sirve» — candados; dos se rompen y caen
            self.en("sirve")
            cands = VGroup(*[candado(c).scale(0.8) for c in (AMBAR, AZUL, VERDE, MAGENTA)]).arrange(RIGHT, buff=0.7)
            cands.move_to(LEFT * 3.5 + DOWN * 2.9)
            self.play(LaggedStart(*[FadeIn(c, scale=0.5) for c in cands], lag_ratio=0.1), run_time=0.4)
            self.sfx("error", gain=-16)
            self.play(*[c.animate.shift(DOWN * 2).rotate(PI / 3).set_color(ROJO).set_opacity(0) for c in (cands[1], cands[3])],
                      run_time=0.5, rate_func=rate_functions.ease_in_quad)

            # «por software» — cuatro huecos; el primero, una ventana de código
            self.en("software")
            self.corte()
            huecos = VGroup(*[DashedVMobject(RoundedRectangle(width=2.4, height=2.4, corner_radius=0.2),
                                             num_dashes=30).set_stroke(INK, 3) for _ in range(4)])
            huecos.arrange(RIGHT, buff=0.5).move_to(UP * 0.4)
            barras = VGroup(*[Line(LEFT * 0.7, RIGHT * (0.7 - 0.3 * (i % 2)), color=INK, stroke_width=5)
                              for i in range(3)]).arrange(DOWN, buff=0.18, aligned_edge=LEFT)
            icos = [ventana_app("código", AZUL, barras).scale(0.9), chip(CIAN).scale(1.3), semaforo().scale(1.8),
                    Text("?", color=AMBAR, weight=BOLD).scale(2.2)]
            for ico, h in zip(icos, huecos):
                ico.move_to(h)
            self.add(huecos)
            self.pop(icos[0])

            # «por hardware», «con semáforos», «más sofisticados»
            for marca, ico in zip(["hardware", "semaforo", "sofisticados"], icos[1:]):
                self.en(marca)
                self.pop(ico)

            # «más adelante»
            self.en("adelante")
            fl = Arrow(huecos[3].get_bottom() + DOWN * 0.3 + LEFT * 0.8, huecos[3].get_bottom() + DOWN * 0.3 + RIGHT * 0.8,
                       color=AMBAR, stroke_width=6, buff=0)
            self.play(GrowArrow(fl), run_time=0.35)

            # «toda solución» — los cuatro se juntan en un bloque
            self.en("toda")
            bloque = VGroup(*icos)
            self.play(FadeOut(huecos), FadeOut(fl), bloque.animate.arrange(RIGHT, buff=0.3).scale(0.6).move_to(UP * 0.4),
                      run_time=0.5)
            marco = SurroundingRectangle(bloque, color=TXT, buff=0.3, corner_radius=0.2, stroke_width=4)
            self.play(Create(marco), run_time=0.35)

            # «seis reglas del juego»
            self.en("seis")
            reglas = reglas_seis(lado=1.4).move_to(UP * 0.6)
            self.play(FadeOut(VGroup(bloque, marco), scale=0.6), run_time=0.25)
            for i in range(6):
                self.sfx("tick", gain=-14, offset=i * 0.1)
            self.play(LaggedStart(*[FadeIn(r, scale=1.5) for r in reglas], lag_ratio=0.15), run_time=0.7)

            # «considerarse correcta»
            self.en("correcta")
            ok = tilde(VERDE, 1.6).next_to(reglas, DOWN, buff=0.7)
            self.play(Create(ok), run_time=0.35)
            self.sfx("exito", gain=-14)
