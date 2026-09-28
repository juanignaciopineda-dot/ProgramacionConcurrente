"""Escena 05 — Memoria compartida o pasaje de mensajes: dónde vive la sincronización."""
from comun.escena import *

IZQ, DER = LEFT * 3.55, RIGHT * 3.55
POS = [UP * 1.0 + LEFT * 2.1, UP * 1.0 + RIGHT * 2.1, DOWN * 2.45]


def trio(centro: np.ndarray) -> VGroup:
    return VGroup(*[proceso_mini(n, c).scale(1.4).move_to(centro + p)
                    for n, c, p in zip(["P1", "P2", "P3"], [AZUL, MAGENTA, CIAN], POS)])


class MemoriaOMensajes(EscenaCurso):
    ESCENA = "05"

    def construct(self):
        with self.narrar("b1"):
            # «comparemos» — la pantalla se parte en dos
            self.en("compara")
            div = Line(UP * 3.8, DOWN * 3.8, color=INK, stroke_width=3)
            self.play(Create(div), run_time=0.35)

            # «dos grandes caminos»
            self.en("caminos")
            t1 = Text("memoria compartida", color=TXT, weight=BOLD).scale(0.85).move_to(IZQ + UP * 3.25)
            t2 = Text("mensajes", color=TXT, weight=BOLD).scale(0.85).move_to(DER + UP * 3.25)
            self.play(FadeIn(t1, shift=DOWN * 0.2), FadeIn(t2, shift=DOWN * 0.2), run_time=0.4)

            # «los procesos interactúen» — tres procesos en cada lado
            self.en("interactuan")
            a, b = trio(IZQ), trio(DER)
            self.play(LaggedStart(*[FadeIn(p, scale=0.6) for p in (*a, *b)], lag_ratio=0.1), run_time=0.6)

            # «Con memoria compartida» — foco a la izquierda: una celda en medio
            self.en("memoria")
            der = VGroup(t2, b)
            der.save_state()
            celda = caja("x", CIAN, ancho=1.7, alto=1.3, scale=1.2, relleno=0.2).move_to(IZQ + DOWN * 0.55)
            self.play(der.animate.fade(0.65), FadeIn(celda, scale=0.6), run_time=0.4)

            # «las mismas variables» — flechas de los tres a la celda
            self.en("variables")
            fls = VGroup(*[Arrow(p.get_center(), celda.get_center(), color=p.caja.get_color(), stroke_width=6,
                                 buff=0.75) for p in a])
            for i in range(3):
                self.sfx("tecla", gain=-14, offset=i * 0.15)
            self.play(LaggedStart(*[GrowArrow(f) for f in fls], lag_ratio=0.3), run_time=0.6)
            self.resaltar(celda[0], color=AMBAR, escala=1.1, run_time=0.4)

            # «lo que hay que proteger» — candado sobre la celda
            self.en("proteger")
            cand = candado(AMBAR).scale(0.7).next_to(celda, RIGHT, buff=0.15).shift(UP * 0.4)
            self.pop(cand, sonido="tick")

            # «sincronización explícita»
            self.en("explicita")
            ex = Text("sincronización explícita", color=AMBAR, weight=BOLD).scale(0.6).move_to(IZQ + UP * 2.45)
            self.play(FadeIn(ex, shift=DOWN * 0.2), run_time=0.35)

            # «la sección crítica» — marco ámbar alrededor de la celda
            self.en("seccion")
            marco = SurroundingRectangle(VGroup(celda, cand), color=AMBAR, buff=0.2, corner_radius=0.15,
                                         stroke_width=5)
            self.play(Create(marco), run_time=0.4)

            # «Con pasaje» — foco a la derecha
            self.en("pasaje")
            izq = VGroup(t1, a, celda, fls, cand, ex, marco)
            self.play(izq.animate.fade(0.65), Restore(der), run_time=0.4)

            # «ningún dato compartido» — una celda fantasma que se tacha
            self.en("ningun")
            fantasma = DashedVMobject(RoundedRectangle(width=1.7, height=1.3, corner_radius=0.12),
                                      num_dashes=20).set_stroke(INK, 3).move_to(DER + DOWN * 0.55)
            self.play(Create(fantasma), run_time=0.3)
            self.sfx("error", gain=-16)
            self.play(Create(tachar(fantasma)), run_time=0.3)

            # «viene incluida» — el sobre empieza a viajar
            self.en("incluida")
            sb = sobre(TXT).scale(0.6).next_to(b[0], DOWN, buff=0.2)
            self.play(FadeIn(sb, scale=0.5), run_time=0.2)
            self.sfx("whoosh", gain=-16)
            self.play(sb.animate.next_to(b[2], LEFT, buff=0.2), run_time=1.3, rate_func=rate_functions.linear)

            # «enviar y recibir» — llegó: sincronización incluida
            self.en("enviar")
            inc = Text("sincronización incluida", color=VERDE, weight=BOLD).scale(0.6).move_to(DER + UP * 2.45)
            self.pop(inc)
            self.sfx("exito", gain=-14)

        self.wait(1.0)
        self.limpiar()
