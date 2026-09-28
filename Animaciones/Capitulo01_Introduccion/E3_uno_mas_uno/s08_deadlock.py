"""Escena 08 — Deadlock: dos procesos que se esperan mutuamente, para siempre."""
from comun.escena import *


class Deadlock(EscenaCurso):
    ESCENA = "08"

    def construct(self):
        self.tres_formas()
        self.interbloqueo()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def tres_formas(self):
        with self.narrar("b1"):
            # «Resolver la exclusión mutua» — la puerta con candado verde
            self.en("resuelta")
            sala = SalaPuerta(3.6, 2.8).move_to(LEFT * 2.6)
            cand = candado(VERDE).scale(0.7).next_to(sala.puerta, LEFT, buff=0.15)
            ok = tilde(VERDE, 1.0).next_to(sala, UP, buff=0.3)
            self.play(FadeIn(sala), FadeIn(cand, scale=0.6), run_time=0.4)
            self.play(Create(ok), run_time=0.3)
            self.sfx("exito", gain=-16)

            # «abre otro» — detrás se abre otra puerta, con luz roja
            self.en("abre")
            otra = SalaPuerta(3.6, 2.8, color=ROJO).move_to(RIGHT * 3.2)
            otra.piso.set_fill(ROJO, 0.05)
            self.play(FadeIn(otra, shift=LEFT * 0.4), run_time=0.35)
            self.sfx("whoosh", gain=-14)
            self.play(otra.abrir(), otra.piso.animate.set_fill(ROJO, 0.35), run_time=0.5)

            # «compiten» — dos procesos tiran de un mismo recurso
            self.en("compiten")
            self.corte()
            rec = candado(AMBAR).scale(0.9).move_to(UP * 1.4)
            a = proceso_mini("P1", AZUL).move_to(LEFT * 3.2 + UP * 1.4)
            b = proceso_mini("P2", MAGENTA).move_to(RIGHT * 3.2 + UP * 1.4)
            cuerdas = VGroup(Line(a.get_right(), rec.get_left(), color=INK, stroke_width=4),
                             Line(rec.get_right(), b.get_left(), color=INK, stroke_width=4))
            self.play(FadeIn(VGroup(a, b, rec, cuerdas)), run_time=0.25)
            self.play(a.animate(rate_func=there_and_back).shift(LEFT * 0.4),
                      b.animate(rate_func=there_and_back).shift(RIGHT * 0.4), run_time=0.4)

            # «cooperan» — dos procesos se pasan una ficha
            self.en("cooperan")
            c = proceso_mini("P3", CIAN).move_to(LEFT * 2.2 + DOWN * 1.6)
            d = proceso_mini("P4", VERDE).move_to(RIGHT * 2.2 + DOWN * 1.6)
            ficha = Dot(c.get_right() + RIGHT * 0.3, radius=0.16, color=AMBAR)
            self.play(FadeIn(c), FadeIn(d), FadeIn(ficha), run_time=0.2)
            self.play(ficha.animate.move_to(d.get_left() + LEFT * 0.3), run_time=0.45, rate_func=rate_functions.ease_in_out_sine)

            # «quedar atascados» — todo se congela en gris
            self.en("atascados")
            todos = VGroup(a, b, c, d)
            self.play(*[p.caja.animate.set_stroke(INK).set_fill(INK, 0.1) for p in todos],
                      rec.animate.set_color(INK), ficha.animate.set_color(INK), run_time=0.4)

            # «de formas distintas» — tres cartas boca abajo
            self.en("formas")
            self.corte()
            cartas = VGroup(*[tarjeta(None) for _ in range(3)]).arrange(RIGHT, buff=0.8).move_to(UP * 0.2)
            self.play(LaggedStart(*[FadeIn(k, shift=UP * 0.4) for k in cartas], lag_ratio=0.15), run_time=0.5)

            # «se confunden fácilmente» — se barajan
            self.en("confunden")
            self.sfx("whoosh", gain=-16)
            self.play(Swap(cartas[0], cartas[2], path_arc=PI / 2), run_time=0.45)
            self.play(Swap(cartas[1], cartas[2], path_arc=PI / 2), run_time=0.4)
            self.cartas = cartas

    # ------------------------------------------------------------------ b2
    def interbloqueo(self):
        with self.narrar("b2"):
            # «el deadlock» — la primera carta se da vuelta
            self.en("deadlock")
            primera = min(self.cartas, key=lambda k: k.get_x())
            dl = voltear(self, primera, tarjeta("deadlock", ROJO))

            # «interbloqueo»
            self.en("interbloqueo")
            it = Text("interbloqueo", color=INK).scale(0.55).next_to(dl, DOWN, buff=0.3)
            self.play(FadeIn(it, shift=UP * 0.2), run_time=0.3)

            # «esperándose mutuamente» — P1 y P2 frente a frente
            self.en("esperan")
            self.corte()
            p1 = caja_proceso("P1", AZUL, 2.2, 1.3).move_to(LEFT * 4.3)
            p2 = caja_proceso("P2", MAGENTA, 2.2, 1.3).move_to(RIGHT * 4.3)
            self.play(FadeIn(p1, shift=RIGHT * 0.5), FadeIn(p2, shift=LEFT * 0.5), run_time=0.4)

            # «sin que ninguno pueda avanzar»
            self.en("avanzar")
            for _ in range(2):
                self.play(p1.animate(rate_func=there_and_back).shift(RIGHT * 0.2),
                          p2.animate(rate_func=there_and_back).shift(LEFT * 0.2), run_time=0.3)

            # «El caso típico» — recursos A y B
            self.en("caso")
            ra = VGroup(candado(AZUL).scale(0.9), Text("A", color=TXT, weight=BOLD).scale(0.7)).arrange(RIGHT, buff=0.25)
            rb = VGroup(candado(MAGENTA).scale(0.9), Text("B", color=TXT, weight=BOLD).scale(0.7)).arrange(RIGHT, buff=0.25)
            ra.move_to(UP * 2.4)
            rb.move_to(DOWN * 2.4)
            self.play(FadeIn(ra, scale=0.6), FadeIn(rb, scale=0.6), run_time=0.35)

            def sujeta(p, r, color):
                return Line(p.get_center(), r[0].get_center(), color=color, stroke_width=6, buff=0.8)

            def pide(p, r):
                ini, fin = p.get_center(), r[0].get_center()
                u = (fin - ini) / np.linalg.norm(fin - ini)
                linea = DashedLine(ini + u * 0.8, fin - u * 0.8, color=AMBAR, stroke_width=5, dash_length=0.2)
                punta = Triangle().set_fill(AMBAR, 1).set_stroke(width=0).scale(0.16)
                punta.rotate(np.arctan2(u[1], u[0]) - PI / 2).move_to(fin - u * 0.8)
                return VGroup(linea, punta)

            # «ya tomó el recurso A»
            self.en("p1a")
            s1 = sujeta(p1, ra, AZUL)
            self.play(Create(s1), run_time=0.4)

            # «ahora pide el B»
            self.en("p1b")
            q1 = pide(p1, rb)
            self.play(Create(q1), run_time=0.4)

            # «ya tomó el B»
            self.en("p2b")
            s2 = sujeta(p2, rb, MAGENTA)
            self.play(Create(s2), run_time=0.4)

            # «ahora pide el A» — el ciclo se cierra en rojo
            self.en("p2a")
            q2 = pide(p2, ra)
            self.play(Create(q2), run_time=0.35)
            self.sfx("alarma", gain=-14)
            self.play(q1.animate.set_color(ROJO), q2.animate.set_color(ROJO), run_time=0.3)

            # «Ninguno va a soltar»
            self.en("soltar")
            self.play(s1.animate.set_stroke(width=12), s2.animate.set_stroke(width=12), run_time=0.4)

            # «conseguir lo que le falta» — las flechas punteadas parpadean
            self.en("falta")
            for _ in range(2):
                self.play(q1.animate(rate_func=there_and_back).set_opacity(0.2),
                          q2.animate(rate_func=there_and_back).set_opacity(0.2), run_time=0.4)

            # «lo tiene, justamente, el otro» — el ciclo gira en rojo
            self.en("otro")
            ciclo = Arc(radius=1.3, start_angle=PI / 2, angle=-1.7 * PI, color=ROJO, stroke_width=8).add_tip(tip_length=0.3)
            self.play(Create(ciclo), run_time=0.5)
            self.play(Rotate(ciclo, angle=-2 * PI, about_point=ORIGIN), run_time=1.2)

            # «quedan congelados»
            self.en("congelados")
            hielo = FullScreenRectangle().set_fill(CIAN, 0.1).set_stroke(width=0)
            self.sfx("impacto", gain=-14)
            self.play(FadeIn(hielo), *[p.caja.animate.set_stroke(INK).set_fill(INK, 0.15) for p in (p1, p2)],
                      s1.animate.set_color(INK), s2.animate.set_color(INK), ciclo.animate.set_color(INK), run_time=0.5)

            # «para siempre» — un reloj corre y nada más se mueve
            self.en("siempre")
            rel = reloj().scale(1.0).move_to(RIGHT * 5.7 + UP * 2.8)
            self.play(FadeIn(rel, scale=0.5), run_time=0.2)
            self.sfx("tick", gain=-16)
            self.play(Rotate(rel.minutero, angle=-4 * PI, about_point=rel.aro.get_center()),
                      run_time=max(0.5, self.margen() - 0.1), rate_func=linear)
