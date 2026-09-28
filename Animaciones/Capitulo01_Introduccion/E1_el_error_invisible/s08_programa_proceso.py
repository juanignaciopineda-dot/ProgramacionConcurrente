"""Escena 08 — Programa (estático) vs proceso (en ejecución). Receta y tarta."""
from comun.escena import *


class ProgramaProceso(EscenaCurso):
    ESCENA = "08"

    def construct(self):
        self.programa()
        self.proceso()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def programa(self):
        with self.narrar("b1"):
            self.en("recap")
            filas = VGroup(*[RoundedRectangle(width=3.2, height=0.6, corner_radius=0.08, color=INK, stroke_width=2)
                             for _ in range(5)]).arrange(DOWN, buff=0.15)
            punta = Triangle(color=AMBAR).set_fill(AMBAR, 1).rotate(-PI / 2).scale(0.22)
            punta.next_to(filas[0], LEFT, buff=0.25)
            self.play(FadeIn(filas), FadeIn(punta), run_time=0.35)
            self.play(Succession(*[punta.animate(run_time=0.3).next_to(filas[k], LEFT, buff=0.25)
                                   for k in range(1, 5)]), run_time=1.2)

            self.en("apps")
            self.corte()
            apps = VGroup(ventana_app("música", MAGENTA, nota_musical()),
                          ventana_app("navegador", AZUL), ventana_app("vídeo", ROJO, triangulo_play()),
                          ventana_app("notas", VERDE), ventana_app("chat", CIAN))
            for i, a in enumerate(apps):
                a.scale(1.1).move_to([(-4.5 + i * 2.25), 0.9 * ((-1) ** i), 0])
                self.sfx("pop", offset=i * 0.12)
            self.play(LaggedStart(*[FadeIn(a, scale=0.6) for a in apps], lag_ratio=0.2), run_time=0.8)

            self.en("pensarlo")
            self.play(LaggedStart(*[FadeOut(a, scale=0.8) for a in apps], lag_ratio=0.1), run_time=0.5)

            self.en("ambos")
            prog = Text("programa", color=TXT, weight=BOLD).scale(1.3).move_to(LEFT * 3.3)
            proc = Text("proceso", color=AZUL, weight=BOLD).scale(1.3).move_to(RIGHT * 3.3)
            dist = Text("≠", color=AMBAR, weight=BOLD).scale(1.8)
            self.pop(prog, proc)
            self.pop(dist, sonido=None)

            self.en("nadie")
            self.play(FadeOut(proc, shift=RIGHT), FadeOut(dist), prog.animate.move_to(ORIGIN).set_color(INK),
                      run_time=0.5)

            self.en("estatica")
            self.doc = documento(INK).scale(2.6).move_to(UP * 0.6)
            self.play(ReplacementTransform(prog, self.doc), run_time=0.5)

            self.en("disco")
            self.dsk = disco().scale(1.6).move_to(DOWN * 2.2)
            self.play(FadeIn(self.dsk, shift=UP * 0.3), run_time=0.3)
            self.sfx("whoosh", gain=-16)
            self.play(self.doc.animate.scale(0.35).move_to(self.dsk.get_top() + UP * 0.1), run_time=0.6)

            self.en("nada")
            plano = Line(LEFT * 2.5, RIGHT * 2.5, color=INK, stroke_width=4).move_to(UP * 1.6)
            cero = Text("0 %", color=INK, weight=BOLD).scale(0.7).next_to(plano, RIGHT, buff=0.3)
            self.play(Create(plano), FadeIn(cero), run_time=1.0)
            self.plano = VGroup(plano, cero)

    # ------------------------------------------------------------------ b2
    def proceso(self):
        with self.narrar("b2"):
            self.en("ejecuta")
            self.sfx("whoosh", gain=-16)
            self.play(FadeOut(self.plano), self.doc.animate.scale(2.2).move_to(UP * 0.8), run_time=0.5)

            self.en("proceso")
            caja_p = RoundedRectangle(width=4.2, height=2.8, corner_radius=0.2, color=AZUL, stroke_width=4)
            caja_p.set_fill(AZUL, 0.12).move_to(UP * 0.6 + LEFT * 1.2)
            nombre = Text("proceso", color=AZUL, weight=BOLD).scale(0.6).next_to(caja_p, UP, buff=0.15)
            self.sfx("pop")
            self.play(ReplacementTransform(self.doc, caja_p), FadeOut(self.dsk, shift=DOWN),
                      FadeIn(nombre), run_time=0.6)
            self.resaltar(caja_p, color=AMBAR, escala=1.05)

            self.en("pc")
            lineas = VGroup(*[Line(LEFT * 1.3, RIGHT * 1.3, color=INK, stroke_width=4) for _ in range(4)])
            lineas.arrange(DOWN, buff=0.4).move_to(caja_p.get_center() + RIGHT * 0.3)
            punta = Triangle(color=AMBAR).set_fill(AMBAR, 1).rotate(-PI / 2).scale(0.18)
            punta.next_to(lineas[0], LEFT, buff=0.25)
            self.play(FadeIn(lineas), FadeIn(punta), run_time=0.3)
            for k in range(1, 4):
                self.sfx("tick", gain=-18, offset=(k - 1) * 0.3)
            self.play(Succession(*[punta.animate(run_time=0.3).next_to(lineas[k], LEFT, buff=0.25)
                                   for k in range(1, 4)]), run_time=0.9)

            self.en("memoria")
            mem = memoria([("0", "7"), ("1", "0"), ("2", "3")], ancho=1.0, alto=0.55).next_to(caja_p, RIGHT, buff=0.6)
            enlace = Line(caja_p.get_right(), mem.get_left(), color=AZUL, stroke_width=3)
            self.play(Create(enlace), FadeIn(mem, shift=LEFT * 0.4), run_time=0.5)

            self.en("estado")
            estados = ["listo", "ejecutando", "bloqueado", "ejecutando"]
            tag = Text(estados[0], color=VERDE, weight=BOLD).scale(0.6).next_to(caja_p, DOWN, buff=0.35)
            self.play(FadeIn(tag, shift=UP * 0.2), run_time=0.3)
            for e in estados[1:]:
                nuevo = Text(e, color=VERDE, weight=BOLD).scale(0.6).move_to(tag)
                self.sfx("tick", gain=-18)
                self.play(Transform(tag, nuevo), run_time=0.35)
                self.wait(0.15)

            self.en("receta")
            self.corte()
            rec = receta().scale(2.6).move_to(LEFT * 3.5 + UP * 0.3)
            et_r = Text("programa", color=INK, weight=BOLD).scale(0.7).next_to(rec, DOWN, buff=0.4)
            self.play(FadeIn(rec, shift=RIGHT), FadeIn(et_r), run_time=0.5)

            self.en("tarta")
            hor = horno(tarta()).scale(1.7).move_to(RIGHT * 3.5 + UP * 0.3)
            et_t = Text("proceso", color=AZUL, weight=BOLD).scale(0.7).next_to(hor, DOWN, buff=0.4)
            self.play(FadeIn(hor, shift=LEFT), FadeIn(et_t), run_time=0.5)

            self.en("ahora")
            for _ in range(2):
                self.sfx("latido", gain=-18)
                self.play(hor.ventana.animate(rate_func=there_and_back).set_fill(AMBAR, 0.7), run_time=0.7)
