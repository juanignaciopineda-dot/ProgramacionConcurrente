"""Escena 04 — Rendezvous: las dos puntas bloqueantes, y la combinación que más se usa."""
from comun.escena import *


class Rendezvous(EscenaCurso):
    ESCENA = "04"

    def pareja(self, y: float = -0.3) -> tuple[VGroup, VGroup]:
        em = proceso_mini("P1", AZUL).scale(2.0).move_to(LEFT * 4.4 + UP * y)
        re = proceso_mini("P2", MAGENTA).scale(2.0).move_to(RIGHT * 4.4 + UP * y)
        return em, re

    def tabla(self) -> VGroup:
        return tabla_bloqueo().scale(1.4).move_to(RIGHT * 1.3 + DOWN * 0.3)

    def construct(self):
        with self.narrar("b1"):
            # «las dos puntas» — la tabla 2×2; se enciende bloquea-bloquea
            self.en("puntas")
            tb = self.tabla()
            self.play(FadeIn(tb), run_time=0.4)
            self.play(tb.celda(0, 0).animate.set_fill(AMBAR, 0.35).set_stroke(AMBAR), run_time=0.3)

            # «pasa algo especial» — frente a frente, los dos con candado
            self.en("especial")
            self.corte()
            em, re = self.pareja()
            c1 = candado(INK).scale(0.9).next_to(em, UP, buff=0.35)
            c2 = candado(INK).scale(0.9).next_to(re, UP, buff=0.35)
            self.play(FadeIn(em, shift=RIGHT * 0.4), FadeIn(re, shift=LEFT * 0.4), run_time=0.4)
            self.play(FadeIn(c1, scale=0.5), FadeIn(c2, scale=0.5), run_time=0.3)

            # «quedan sincronizados» — se iluminan a la vez
            self.en("sincronizados")
            self.resaltar(em.caja, re.caja, color=AMBAR, escala=1.12, run_time=0.5)

            # «el mismo instante exacto» — un reloj; las agujas se alinean
            self.en("instante")
            rj = reloj(TXT).scale(1.5).move_to(UP * 2.2)
            self.play(FadeIn(rj, scale=0.6), run_time=0.3)
            self.sfx("tick", gain=-10)
            self.play(Rotate(rj.minutero, angle=-(2 * PI - PI / 2.5), about_point=rj.aro.get_center()), run_time=0.6)

            # «mutuamente» — flechas de espera, de uno al otro
            self.en("esperan")
            ida = DashedVMobject(Arrow(em.get_right() + UP * 0.45, re.get_left() + UP * 0.45, color=INK,
                                       stroke_width=5, buff=0.3), num_dashes=22)
            vuelta = DashedVMobject(Arrow(re.get_left() + DOWN * 0.45, em.get_right() + DOWN * 0.45, color=INK,
                                          stroke_width=5, buff=0.3), num_dashes=22)
            self.play(Create(ida), Create(vuelta), run_time=0.5)

            # «pasa de uno a otro» — el sobre cruza; se abren los dos candados
            self.en("pasa")
            sb = sobre(TXT).scale(0.8).next_to(em, RIGHT, buff=0.35)
            self.play(FadeIn(sb, scale=0.5), run_time=0.15)
            self.sfx("whoosh", gain=-16)
            self.play(sb.animate.next_to(re, LEFT, buff=0.35), run_time=0.6)
            a1 = candado(VERDE, abierto=True).scale(0.9).move_to(c1)
            a2 = candado(VERDE, abierto=True).scale(0.9).move_to(c2)
            self.sfx("impacto", gain=-10)
            self.play(ReplacementTransform(c1, a1), ReplacementTransform(c2, a2), run_time=0.3)

            # «rendezvous»
            self.en("rendezvous")
            pal = self.palabra("rendezvous", AMBAR, 1.6).move_to(DOWN * 2.7)
            self.pop(pal, sonido="impacto")

            # «la forma más fuerte» — una línea gruesa los une
            self.en("fuerte")
            lazo = Line(em.get_right(), re.get_left(), color=AMBAR, stroke_width=16)
            self.play(FadeOut(ida), FadeOut(vuelta), FadeOut(sb), Create(lazo), run_time=0.45)

            # «de sincronización entre dos» — un medidor sube al máximo
            self.en("maxima")
            barra = RoundedRectangle(width=4.4, height=0.5, corner_radius=0.1, color=INK, stroke_width=3)
            barra.move_to(UP * 2.3)
            llena = RoundedRectangle(width=4.4, height=0.5, corner_radius=0.1, stroke_width=0)
            llena.set_fill(AMBAR, 0.8).move_to(barra)
            et = Text("sincronización", color=TXT, weight=BOLD).scale(0.6).next_to(barra, UP, buff=0.2)
            self.play(FadeOut(rj), FadeIn(barra), FadeIn(et), run_time=0.25)
            self.play(GrowFromEdge(llena, LEFT), run_time=0.55)

            # «En la práctica» — vuelve la tabla
            self.en("practica")
            self.corte()
            tb = self.tabla()
            tb.celda(0, 0).set_fill(AMBAR, 0.2).set_stroke(AMBAR)
            self.play(FadeIn(tb), run_time=0.4)

            # «que más se usa» — se enciende envía-sigue / recibe-espera, con estrella
            self.en("usa")
            cel = tb.celda(1, 0)
            est = Star(5, outer_radius=0.4, color=AMBAR, fill_opacity=1).move_to(cel.get_corner(UR) + DL * 0.45)
            self.play(cel.animate.set_fill(VERDE, 0.35).set_stroke(VERDE), run_time=0.3)
            self.pop(est)

            # «sigue trabajando» — el emisor suelta el sobre y sigue, con un engranaje
            self.en("trabajando")
            self.corte()
            em, re = self.pareja(y=-0.6)
            sb = sobre(TXT).scale(0.8).next_to(em, RIGHT, buff=0.35)
            eng = engranaje(TXT).scale(1.1).next_to(em, UP, buff=0.4)
            self.play(FadeIn(em), FadeIn(re), FadeIn(sb, scale=0.5), FadeIn(eng, scale=0.5), run_time=0.3)
            self.play(sb.animate.move_to(DOWN * 0.6), Rotate(eng, angle=-PI), run_time=0.9,
                      rate_func=rate_functions.linear)

            # «receptor bloqueante» — el receptor espera con un reloj
            self.en("receptor")
            rj = reloj(TXT).scale(1.1).next_to(re, UP, buff=0.4)
            self.play(FadeIn(rj, scale=0.5), Rotate(eng, angle=-PI / 2), run_time=0.3)
            self.play(Rotate(rj.minutero, angle=-2 * PI, about_point=rj.aro.get_center()), Rotate(eng, angle=-PI),
                      run_time=1.0)

            # «antes de seguir» — llega el sobre; el receptor se desbloquea y sigue
            self.en("seguir")
            self.play(sb.animate.next_to(re, LEFT, buff=0.35), Rotate(eng, angle=-PI / 2), run_time=0.45)
            ok = tilde(VERDE, 0.9).move_to(rj)
            self.play(FadeOut(rj), Create(ok), run_time=0.3)
            self.sfx("exito", gain=-14)

        self.wait(1.0)
        self.limpiar()
