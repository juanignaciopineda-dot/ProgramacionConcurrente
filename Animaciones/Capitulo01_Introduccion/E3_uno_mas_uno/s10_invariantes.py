"""Escena 10 — Invariantes: basta un instante en que se rompan para que el programa esté mal."""
from comun.escena import *

X0, X1 = -6.2, 6.2          # extremos de la línea de tiempo
Y_LINEA = 0.2
X_FALLA = 2.0               # dónde se rompe el invariante


class Invariantes(EscenaCurso):
    ESCENA = "10"

    def construct(self):
        with self.narrar("b1"):
            # «un cuarto problema» — las tres cartas y una cuarta
            self.en("cuarto")
            cartas = VGroup(*[tarjeta(t, c, ancho=2.0, alto=2.6, escala_texto=0.5)
                              for t, c in [("deadlock", ROJO), ("inanición", AMBAR), ("livelock", MAGENTA)]])
            cuarta = tarjeta(None, TXT, ancho=2.0, alto=2.6)
            fila = VGroup(*cartas, cuarta).arrange(RIGHT, buff=0.5).move_to(UP * 0.2)
            self.play(FadeIn(cartas), run_time=0.25)
            self.pop(cuarta)

            # «engloba en parte» — la cuarta crece y cubre en parte a las otras
            self.en("engloba")
            abrazo = DashedVMobject(SurroundingRectangle(fila, color=TXT, buff=0.3, corner_radius=0.3), num_dashes=60)
            self.play(cartas.animate.fade(0.5), cuarta.animate.scale(1.15), Create(abrazo), run_time=0.5)

            # «violación de invariantes»
            self.en("violacion")
            inv = voltear(self, cuarta, tarjeta("invariante", VERDE, ancho=2.0, alto=2.6, escala_texto=0.5))

            # «Un invariante es una condición» — al centro, grande
            self.en("condicion")
            self.play(FadeOut(cartas), FadeOut(abrazo), inv.animate.scale(1.3).move_to(UP * 0.2), run_time=0.4)

            # «debería cumplirse siempre» — línea de tiempo con una banda verde continua
            self.en("siempre")
            self.corte()
            eje = Arrow(LEFT * 6.8 + UP * Y_LINEA, RIGHT * 6.8 + UP * Y_LINEA, color=INK, stroke_width=3, buff=0,
                        max_tip_length_to_length_ratio=0.03)
            banda = Line([X0, Y_LINEA, 0], [X1, Y_LINEA, 0], color=VERDE, stroke_width=18)
            self.play(Create(eje), run_time=0.3)
            self.play(Create(banda), run_time=0.8, rate_func=linear)

            # «en cualquier instante» — un cursor recorre la banda
            self.en("cualquier")
            cursor = VGroup(Line(UP * 0.55, DOWN * 0.55, color=TXT, stroke_width=4),
                            Triangle().set_fill(TXT, 1).set_stroke(width=0).scale(0.12).rotate(PI).shift(UP * 0.7))
            cursor.move_to([X0, Y_LINEA + 0.1, 0])
            self.play(FadeIn(cursor), run_time=0.15)
            self.play(cursor.animate.set_x(X1 - 1.0), run_time=max(0.6, self.margen() - 0.1), rate_func=linear)

            # «nunca hay más de un proceso» — el invariante del ejemplo
            self.en("ejemplo")
            regla = VGroup(Text("en sección crítica", color=TXT, weight=BOLD).scale(0.7),
                           Text("≤ 1", color=VERDE, weight=BOLD).scale(0.9)).arrange(RIGHT, buff=0.3)
            regla.move_to(UP * 2.9)
            self.pop(regla)

            # «a la vez en la sección crítica» — debajo, la sala con un proceso
            self.en("sc")
            sala = SalaPuerta(3.8, 1.8).move_to(DOWN * 2.6)
            p1 = proceso_mini("P1", AZUL).move_to(sala.dentro)
            self.play(FadeIn(sala), FadeIn(p1), run_time=0.35)

            # «la sincronización falla» — un contador que avanza con el tiempo
            self.en("falla")
            cuenta = Text("1", color=VERDE, weight=BOLD).scale(0.8)
            cuenta.add_updater(lambda m: m.next_to(cursor, UP, buff=0.12))
            self.add(cuenta)
            self.play(cursor.animate.set_x(X_FALLA - 1.4), run_time=1.2, rate_func=linear)

            # «un solo interleaving» — un punto de la línea se enciende
            self.en("uno")
            punto = Dot([X_FALLA, Y_LINEA, 0], radius=0.16, color=AMBAR)
            self.play(FadeIn(punto, scale=3), run_time=0.3)
            self.play(cursor.animate.set_x(X_FALLA - 0.3), run_time=0.8, rate_func=linear)

            # «por un instante» — salta a 2 en rojo y vuelve a 1
            self.en("instante")
            self.play(cursor.animate.set_x(X_FALLA), run_time=0.15, rate_func=linear)
            cuenta.clear_updaters()
            dos = Text("2", color=ROJO, weight=BOLD).scale(1.0).move_to(cuenta)
            p2 = proceso_mini("P2", MAGENTA).move_to(sala.dentro + RIGHT * 0.85)
            self.sfx("alarma", gain=-10)
            self.play(Transform(cuenta, dos), p1.animate.shift(LEFT * 0.85), FadeIn(p2), run_time=0.2)
            self.wait(0.25)
            uno = Text("1", color=VERDE, weight=BOLD).scale(0.8).move_to(cuenta)
            self.play(Transform(cuenta, uno), FadeOut(p2), p1.animate.shift(RIGHT * 0.85), run_time=0.2)

            # «el programa esté mal» — la banda queda cortada en rojo en ese punto
            self.en("mal")
            corte = Line([X_FALLA - 0.25, Y_LINEA, 0], [X_FALLA + 0.25, Y_LINEA, 0], color=ROJO, stroke_width=22)
            self.sfx("error", gain=-12)
            self.play(FadeIn(corte, scale=1.5), FadeOut(punto), regla[1].animate.set_color(ROJO), run_time=0.35)
            self.play(Flash(corte, color=ROJO, flash_radius=0.6), run_time=0.4)

        self.wait(1.0)
        self.limpiar()
