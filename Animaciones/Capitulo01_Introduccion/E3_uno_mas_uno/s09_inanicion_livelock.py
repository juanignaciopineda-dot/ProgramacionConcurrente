"""Escena 09 — Inanición (uno se queda fuera para siempre) y livelock (todos se mueven, nadie avanza)."""
from comun.escena import *


def cartas_problemas(caras: list[str | None]) -> VGroup:
    """Las tres cartas de los problemas; `None` = boca abajo."""
    colores = [ROJO, AMBAR, MAGENTA]
    return VGroup(*[tarjeta(t, c if t else AMBAR) for t, c in zip(caras, colores)]).arrange(RIGHT, buff=0.8)


class InanicionLivelock(EscenaCurso):
    ESCENA = "09"

    def construct(self):
        self.inanicion()
        self.livelock()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def turno(self, p: Mobject, run_time: float = 0.7) -> None:
        """`p` va hasta el recurso, lo usa y vuelve a su lugar."""
        ida = self.rec.get_center() + (p.get_center() - self.rec.get_center()) * 0.45
        self.play(p.animate(rate_func=there_and_back).move_to(ida),
                  self.rec.animate(rate_func=there_and_back).set_stroke(p.caja.get_stroke_color(), 8),
                  run_time=run_time)

    def inanicion(self):
        with self.narrar("b1"):
            # «la inanición» — vuelven las cartas; la segunda se da vuelta
            self.en("inanicion")
            cartas = cartas_problemas(["deadlock", None, None]).move_to(UP * 0.3)
            self.play(FadeIn(cartas, shift=UP * 0.3), run_time=0.3)
            ina = voltear(self, cartas[1], tarjeta("inanición", AMBAR))

            # «starvation»
            self.en("starvation")
            st = Text("starvation", color=INK).scale(0.55).next_to(ina, DOWN, buff=0.3)
            self.play(FadeIn(st, shift=UP * 0.2), run_time=0.3)

            # «es más sutil» — la carta se acerca y una lupa pasa por encima
            self.en("sutil")
            self.play(ina.animate.scale(1.25), FadeOut(st), cartas[0].animate.fade(0.7),
                      cartas[2].animate.fade(0.7), run_time=0.35)
            lp = lupa().scale(1.4).move_to(ina.get_left() + DOWN * 0.3)
            self.play(FadeIn(lp), run_time=0.15)
            self.play(lp.animate.move_to(ina.get_right() + DOWN * 0.3), run_time=0.7)

        # pausa pedida por el usuario: el bloque se parte aquí
        with self.narrar("b2"):
            # «sigue avanzando» — un recurso; P1 y P2 se lo turnan
            self.en("avanza")
            self.corte()
            self.rec = Circle(radius=0.9, color=INK, stroke_width=5).set_fill(AMBAR, 0.12).move_to(RIGHT * 0.6)
            et = Text("recurso", color=INK).scale(0.5).next_to(self.rec, DOWN, buff=0.25)
            p1 = proceso_mini("P1", AZUL).move_to(LEFT * 3.6 + UP * 1.6)
            p2 = proceso_mini("P2", MAGENTA).move_to(LEFT * 3.6 + DOWN * 1.6)
            self.play(FadeIn(self.rec), FadeIn(et), FadeIn(p1), FadeIn(p2), run_time=0.3)
            self.turno(p1, 0.6)
            self.turno(p2, 0.6)

            # «un proceso puntual»
            self.en("p3")
            p3 = proceso_mini("P3", CIAN).move_to(RIGHT * 4.6)
            self.play(FadeIn(p3, shift=LEFT * 0.4), run_time=0.3)

            # «queda fuera para siempre» — P3 intenta y rebota
            self.en("fuera")
            self.play(p3.animate.shift(LEFT * 1.4), run_time=0.25, rate_func=rate_functions.ease_in_quad)
            self.sfx("error", gain=-16)
            self.play(p3.animate.shift(RIGHT * 1.4), run_time=0.35, rate_func=rate_functions.ease_out_bounce)
            self.turno(p1, 0.6)

            # «se turnan un recurso» — el turno se acelera
            self.en("turnan")
            for p in (p2, p1, p2, p1):
                self.turno(p, 0.4)

            # «un tercero que también lo pide»
            self.en("pide")
            self.resaltar(p3, color=ROJO, escala=1.2)

            # «no conseguirlo jamás» — el contador de intentos sube
            self.en("jamas")
            et_int = Text("intentos", color=INK).scale(0.5).next_to(p3, UP, buff=0.9)
            n = Text("0", color=ROJO, weight=BOLD).scale(0.9).next_to(et_int, DOWN, buff=0.15)
            self.play(FadeIn(et_int), FadeIn(n), run_time=0.2)
            for k in range(1, 6):
                nuevo = Text(str(k), color=ROJO, weight=BOLD).scale(0.9).move_to(n)
                self.sfx("tick", gain=-16)
                self.play(Transform(n, nuevo), p3.animate(rate_func=there_and_back).shift(LEFT * 0.3), run_time=0.3)

            # «nunca deje de usarse»
            self.en("usarse")
            self.turno(p2, 0.5)
            self.turno(p1, 0.5)

    # ------------------------------------------------------------------ b2
    def livelock(self):
        with self.narrar("b3"):
            # «la más engañosa» — la tercera carta tiembla
            self.en("enganosa")
            self.corte()
            cartas = cartas_problemas(["deadlock", "inanición", None]).move_to(UP * 0.3)
            self.play(FadeIn(cartas), run_time=0.25)
            self.play(Wiggle(cartas[2], scale_value=1.1), run_time=0.6)

            # «con el deadlock» — la carta «deadlock» se acerca a la tercera
            self.en("condeadlock")
            self.play(cartas[0].animate.next_to(cartas[2], LEFT, buff=0.25), FadeOut(cartas[1]), run_time=0.4)

            # «el livelock»
            self.en("livelock")
            voltear(self, cartas[2], tarjeta("livelock", MAGENTA))

            # «no se bloquean» — dos procesos en movimiento
            self.en("noblq")
            self.corte()
            p1 = caja_proceso("P1", AZUL, 2.0, 1.2).move_to(LEFT * 3.0 + DOWN * 0.3)
            p2 = caja_proceso("P2", MAGENTA, 2.0, 1.2).move_to(RIGHT * 0.6 + DOWN * 0.3)
            self.play(FadeIn(p1), FadeIn(p2), run_time=0.3)
            self.play(p1.animate(rate_func=there_and_back).shift(UP * 0.5),
                      p2.animate(rate_func=there_and_back).shift(DOWN * 0.5), run_time=0.6)

            # «consumiendo procesador» — la barra de CPU sube al 100 %
            self.en("cpu")
            marco = Rectangle(width=0.8, height=3.4, color=INK, stroke_width=3).move_to(LEFT * 6.0 + DOWN * 0.3)
            barra = Rectangle(width=0.8, height=3.4, stroke_width=0).set_fill(ROJO, 0.7).move_to(marco)
            barra.stretch(0.02, 1, about_edge=DOWN)
            et_cpu = Text("CPU", color=TXT, weight=BOLD).scale(0.5).next_to(marco, UP, buff=0.2)
            pct = Text("100 %", color=ROJO, weight=BOLD).scale(0.45).next_to(marco, DOWN, buff=0.2)
            self.play(FadeIn(marco), FadeIn(et_cpu), run_time=0.2)
            self.play(barra.animate.stretch_to_fit_height(3.4, about_edge=DOWN), run_time=0.6)
            self.play(FadeIn(pct), run_time=0.2)

            # «reaccionando todo el tiempo» — uno se mueve y el otro responde
            self.en("reaccion")
            for d in (UP, DOWN):
                self.play(p1.animate.shift(d * 0.8), run_time=0.3)
                self.play(p2.animate.shift(d * 0.8), run_time=0.3)

            # «sin avanzar en absoluto» — una meta que nunca se alcanza
            self.en("sinavanzar")
            meta = bandera().scale(1.4).move_to(RIGHT * 5.4 + DOWN * 0.3)
            linea = DashedLine(RIGHT * 4.4 + UP * 1.6, RIGHT * 4.4 + DOWN * 2.2, color=TXT, stroke_width=4)
            self.play(FadeIn(meta), Create(linea), run_time=0.35)
            for d in (UP, DOWN):
                self.play(p1.animate.shift(d * 0.6), p2.animate.shift(d * 0.6), run_time=0.35)

            # «lo que nos pasa» — dos personas caminan una hacia la otra
            self.en("pasa")
            self.corte()
            a = persona(AZUL).scale(1.9).move_to(LEFT * 6.0)
            b = persona(MAGENTA).scale(1.9).move_to(RIGHT * 6.0)
            self.add(a, b)
            self.play(a.animate.move_to(LEFT * 2.0), b.animate.move_to(RIGHT * 2.0), run_time=1.0, rate_func=linear)

            # «nos cruzamos con alguien» — frente a frente
            self.en("cruzamos")
            self.play(a.animate.move_to(LEFT * 1.1), b.animate.move_to(RIGHT * 1.1), run_time=0.4)

            # «un pasillo estrecho» — aparecen las paredes
            self.en("pasillo")
            paredes = VGroup(Line(LEFT * 7.2 + UP * 2.5, RIGHT * 7.2 + UP * 2.5, color=INK, stroke_width=10),
                             Line(LEFT * 7.2 + DOWN * 2.5, RIGHT * 7.2 + DOWN * 2.5, color=INK, stroke_width=10))
            self.play(Create(paredes), run_time=0.4)

            # «hacia el mismo lado» — los dos se apartan a la vez
            fantasmas = VGroup()

            def apartarse(d):
                fantasmas.add(a.copy().set_opacity(0.2), b.copy().set_opacity(0.2))
                self.add(fantasmas[-2:])
                self.sfx("whoosh", gain=-18)
                self.play(a.animate.move_to([a.get_x(), d, 0]), b.animate.move_to([b.get_x(), d, 0]), run_time=0.3)

            self.en("lado")
            apartarse(1.2)

            # «una y otra vez»
            self.en("otravez")
            for d in (-1.2, 1.2, -1.2):
                apartarse(d)

            # «movimiento constante» — estelas
            self.en("movimiento")
            apartarse(1.2)
            apartarse(0.0)

            # «logra pasar»
            self.en("pasar")
            x = cruz(ROJO, 2.0).move_to(ORIGIN + UP * 0.1)
            self.sfx("error", gain=-12)
            self.play(Create(x), run_time=0.35)
