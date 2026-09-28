"""Escena 07 — Tres escenarios: multiprogramación, multiprocesamiento y distribuido."""
from comun.escena import *

XS = (-3.4, 0.6, 4.6)          # centro de cada columna
ANCHO, Y_COL, ALTO = 3.6, 0.6, 4.2
Y_FILA = -2.2
PROC_COLS = (AZUL, CIAN, MAGENTA)


class TresEscenarios(EscenaCurso):
    ESCENA = "07"

    def construct(self):
        self.escenarios()
        self.linea_divisoria()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def escenarios(self):
        with self.narrar("b1"):
            # al arrancar: la palabra, sola
            palabra = self.palabra("concurrencia", escala=1.5)
            self.pop(palabra)

            # «tres escenarios»
            self.en("tres")
            self.play(FadeOut(palabra, scale=0.5), run_time=0.25)
            self.cols = VGroup(*[RoundedRectangle(width=ANCHO, height=ALTO, corner_radius=0.15, color=INK,
                                                  stroke_width=2.5).move_to([x, Y_COL, 0]) for x in XS])
            self.play(LaggedStart(*[Create(c) for c in self.cols], lag_ratio=0.2), run_time=0.6)

            # «cuánto hardware hay detrás»
            self.en("hardware")
            fl = Arrow([-5.2, -3.3, 0], [6.4, -3.3, 0], color=AMBAR, stroke_width=5, buff=0,
                       max_tip_length_to_length_ratio=0.03)
            mas = Text("más hardware", color=AMBAR).scale(0.42).next_to(fl, DOWN, buff=0.1)
            self.play(GrowArrow(fl), FadeIn(mas), run_time=0.6)
            self.hw = VGroup(fl, mas)

            # «Multiprogramación» + «varios procesos» + «un único procesador»
            self.en("multiprog")
            self.titulo(0, "multiprogramación")
            self.en("procesos")
            procs = VGroup(*[caja(f"P{i + 1}", c, ancho=0.95, alto=0.65, scale=0.42)
                             for i, c in enumerate(PROC_COLS)]).arrange(RIGHT, buff=0.18).move_to([XS[0], 1.3, 0])
            self.pop(*procs)
            self.en("unico")
            cpu1 = nucleo(INK, 1.5, "CPU").move_to([XS[0], -0.3, 0])
            self.play(FadeIn(cpu1, shift=UP * 0.3), run_time=0.3)
            for k in range(6):
                self.play(cpu1.caja.animate.set_fill(PROC_COLS[k % 3], 0.6).set_stroke(PROC_COLS[k % 3]),
                          procs[k % 3].animate(rate_func=there_and_back).shift(DOWN * 0.15), run_time=0.14)
            self.cpu1 = cpu1

            # «Multiprocesamiento» + «varios procesadores» + «la misma máquina»
            self.en("multiproc")
            self.titulo(1, "multiprocesamiento")
            self.en("procs")
            cpus = VGroup(*[nucleo(c, 1.05, "CPU") for c in (AZUL, CIAN, MAGENTA, VERDE)])
            cpus.arrange_in_grid(2, 2, buff=0.25).move_to([XS[1], 0.4, 0])
            self.pop(*cpus)
            self.play(*[c.caja.animate.set_fill(c.caja.get_color(), 0.6) for c in cpus], run_time=0.3)
            self.en("maquina")
            caja_pc = SurroundingRectangle(cpus, color=TXT, buff=0.25, corner_radius=0.15, stroke_width=4)
            self.play(Create(caja_pc), run_time=0.45)

            # «procesamiento distribuido» + «varios ordenadores separados» + «hablándose por red»
            self.en("distribuido")
            self.titulo(2, "distribuido")
            self.en("ordenadores")
            pcs = VGroup(*[ordenador().scale(1.0).move_to([XS[2] + dx, y, 0])
                           for dx, y in ((-0.9, 1.05), (0.9, 1.05), (0, -0.75))])
            self.play(LaggedStart(*[FadeIn(p, shift=UP * 0.3) for p in pcs], lag_ratio=0.2), run_time=0.5)
            self.en("red")
            cables = VGroup(*[Line(pcs[a].get_center(), pcs[b].get_center(), color=AMBAR, stroke_width=3)
                              .set_z_index(-1) for a, b in ((0, 1), (1, 2), (2, 0))])
            self.play(Create(cables), run_time=0.35)
            paqs = [Dot(radius=0.08, color=AMBAR).move_to(c.get_start()) for c in cables]
            self.add(*paqs)
            self.play(*[p.animate.move_to(c.get_end()) for p, c in zip(paqs, cables)], run_time=0.6)
            self.remove(*paqs)

    def titulo(self, i: int, txt: str):
        t = Text(txt, color=TXT, weight=BOLD).scale(0.45).move_to([XS[i], Y_COL + ALTO / 2 - 0.35, 0])
        self.play(self.cols[i].animate.set_stroke(AMBAR), FadeIn(t, shift=DOWN * 0.2), run_time=0.3)
        self.play(self.cols[i].animate.set_stroke(INK), run_time=0.2)

    # ------------------------------------------------------------------ b2
    def linea_divisoria(self):
        with self.narrar("b2"):
            # «La concurrencia está ahí»: banda sobre las tres columnas
            self.en("ahi")
            x0, x1 = XS[0] - ANCHO / 2, XS[2] + ANCHO / 2
            banda = Rectangle(width=x1 - x0, height=0.6, stroke_width=0).set_fill(AMBAR, 0.25)
            banda.move_to([(x0 + x1) / 2, 3.3, 0])
            bt = Text("concurrencia", color=AMBAR, weight=BOLD).scale(0.55).move_to(banda)
            self.play(GrowFromEdge(banda, LEFT), FadeIn(bt), run_time=0.5)

            # «sin excepción»
            self.en("excepcion")
            oks = [tilde(VERDE, 0.5).move_to([x + ANCHO / 2 - 0.4, Y_COL - ALTO / 2 + 0.35, 0]) for x in XS]
            self.sfx("exito", gain=-16)
            self.play(LaggedStart(*[Create(o) for o in oks], lag_ratio=0.3), run_time=0.6)

            # «Lo único que cambia»: fila «¿en paralelo?»
            self.en("cambia")
            self.celdas = VGroup(*[RoundedRectangle(width=ANCHO, height=0.75, corner_radius=0.12, color=INK,
                                                    stroke_width=2).move_to([x, Y_FILA, 0]) for x in XS])
            preg = VGroup(*[Text("¿en paralelo?", color=INK).scale(0.42).move_to(c) for c in self.celdas])
            self.play(FadeOut(self.hw), FadeIn(self.celdas), FadeIn(preg), run_time=0.45)

            # «además de intercalarse»: tiras intercaladas bajo cada título
            self.en("intercalarse")
            tiras = VGroup()
            for x in XS:
                t = VGroup(*[Rectangle(width=0.4, height=0.2, stroke_width=0).set_fill(PROC_COLS[k % 3], 0.9)
                             for k in range(7)]).arrange(RIGHT, buff=0.03).move_to([x, Y_COL + ALTO / 2 - 0.72, 0])
                tiras.add(t)
            self.play(LaggedStart(*[LaggedStart(*[GrowFromEdge(r, LEFT) for r in t], lag_ratio=0.5) for t in tiras],
                                  lag_ratio=0.2), run_time=0.9)

            # «las tareas también corren»
            self.en("tareas")
            self.resaltar(*self.celdas, color=TXT, escala=1.04, run_time=0.45)

            # «físicamente al mismo tiempo»
            self.en("fisico")
            marcas = VGroup(
                VGroup(cruz(ROJO, 0.35), Text("no", color=ROJO, weight=BOLD).scale(0.5)).arrange(RIGHT, buff=0.2),
                *[VGroup(tilde(VERDE, 0.4), Text("sí", color=VERDE, weight=BOLD).scale(0.5)).arrange(RIGHT, buff=0.2)
                  for _ in range(2)])
            for m, c in zip(marcas, self.celdas):
                m.move_to(c)
            self.sfx("pop")
            self.play(*[Transform(p, m) for p, m in zip(preg, marcas)], run_time=0.4)

            # «la línea divisoria»
            self.en("linea")
            div = DashedLine([x0, -1.72, 0], [x1, -1.72, 0], color=AMBAR, stroke_width=4, dash_length=0.15)
            self.play(Create(div), run_time=0.5)

            # «entre estructura y ejecución»
            self.en("rotulos")
            est = Text("estructura", color=AMBAR, weight=BOLD).scale(0.45).rotate(PI / 2).move_to([-6.2, 1.6, 0])
            eje = Text("ejecución", color=TXT, weight=BOLD).scale(0.38).move_to([-6.2, Y_FILA, 0])
            self.play(FadeIn(est, shift=RIGHT * 0.2), run_time=0.3)
            self.play(FadeIn(eje, shift=RIGHT * 0.2), run_time=0.3)

            # «todo el curso»: todo se achica en una tarjeta y sale
            self.en("curso")
            todo = Group(*[m for m in self.mobjects if m is not self.camera.frame])
            marco = SurroundingRectangle(todo, color=AMBAR, buff=0.3, corner_radius=0.2, stroke_width=4)
            self.play(Create(marco), run_time=0.3)
            tarjeta = Group(todo, marco)
            self.play(tarjeta.animate.scale(0.45), run_time=0.5)
            self.flotar(tarjeta)
