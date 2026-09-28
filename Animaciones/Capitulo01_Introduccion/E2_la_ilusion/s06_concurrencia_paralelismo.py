"""Escena 06 — Concurrencia no es paralelismo: estructura vs ejecución."""
from comun.escena import *

XL, XR = -3.6, 3.6          # centro de cada panel
COLS = (AZUL, CIAN, MAGENTA, VERDE)


def rayo(color=ROJO) -> VMobject:
    return VMobject(color=color, stroke_width=5).set_points_as_corners(
        [np.array(p) for p in [(0, 0.35, 0), (0.15, 0.05, 0), (-0.1, 0.0, 0), (0.08, -0.35, 0)]])


class ConcurrenciaParalelismo(EscenaCurso):
    ESCENA = "06"

    def construct(self):
        self.confusion()
        self.concurrencia()
        self.paralelismo()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def confusion(self):
        with self.narrar("b1"):
            # «el truco de la multiprogramación»
            self.en("truco")
            c = cpu(3.2, 2.4).move_to(DOWN * 1.4)
            n = nucleo(AZUL, 0.9).move_to(c.caja.get_center() + DOWN * 0.25)
            wins = ventanas_multitarea().arrange(RIGHT, buff=0.4).scale(1.1).move_to(UP * 1.9)
            self.play(FadeIn(VGroup(c, n, wins)), run_time=0.3)
            for k in range(9):
                self.play(*[w.animate.set_opacity(1 if i == k % 3 else 0.25) for i, w in enumerate(wins)],
                          n.caja.animate.set_fill((MAGENTA, AZUL, ROJO)[k % 3], 0.6),
                          run_time=0.12)

            # «podemos desarmar»: todo sale volando en pedazos
            self.en("desarmar")
            piezas = [*c, n, *wins]
            rng = np.random.default_rng(4)
            self.sfx("whoosh")
            self.play(*[p.animate.shift(np.array([*rng.uniform(-1, 1, 2), 0]) * 7).rotate(rng.uniform(-2, 2))
                        .set_opacity(0) for p in piezas], run_time=0.6, rate_func=rate_functions.ease_in_cubic)
            self.corte()

            # «una confusión muy común»: dos burbujas vacías
            self.en("confusion")
            self.b1 = burbuja_dialogo(5.6, 2.0).move_to([XL, 0.6, 0])
            self.b2 = burbuja_dialogo(5.6, 2.0).flip(UP).move_to([XR, 0.6, 0])
            self.pop(self.b1, self.b2)

            # «la gente usa»
            self.en("gente")
            self.w1 = Text("concurrente", color=AMBAR, weight=BOLD).scale(1.0).move_to(self.b1.globo)
            self.w2 = Text("paralelo", color=CIAN, weight=BOLD).scale(1.0).move_to(self.b2.globo)
            self.play(FadeIn(self.w1, scale=0.7), FadeIn(self.w2, scale=0.7), run_time=0.35)

            # «como si fueran sinónimos»
            self.en("sinonimos")
            igual = Text("=", color=TXT, weight=BOLD).scale(2.2).move_to([0, 0.6, 0])
            self.pop(igual)

            # «y no lo son»
            self.en("nolo")
            distinto = Text("≠", color=ROJO, weight=BOLD).scale(2.2).move_to(igual)
            self.sfx("error", gain=-14)
            self.play(Transform(igual, distinto), run_time=0.3)

            # «dos preguntas distintas»
            self.en("preguntas")
            self.q1 = Text("?", color=INK, weight=BOLD).scale(1.3).move_to([XL, 2.45, 0])
            self.q2 = Text("?", color=INK, weight=BOLD).scale(1.3).move_to([XR, 2.45, 0])
            self.div = DashedLine([0, 3.6, 0], [0, -3.8, 0], color=INK, stroke_width=2)
            self.play(FadeOut(VGroup(self.b1, self.b2, igual)),
                      self.w1.animate.scale(0.88).move_to([XL, 3.3, 0]),
                      self.w2.animate.scale(0.88).move_to([XR, 3.3, 0]), run_time=0.5)
            self.play(Create(self.div), FadeIn(self.q1, shift=DOWN * 0.2), FadeIn(self.q2, shift=DOWN * 0.2),
                      run_time=0.4)

    # ------------------------------------------------------------------ b2
    def concurrencia(self):
        with self.narrar("b2"):
            # «Concurrencia responde»
            self.en("concurrencia")
            w = Text("concurrencia", color=AMBAR, weight=BOLD).scale(0.88).move_to(self.w1)
            self.play(Transform(self.w1, w), run_time=0.35)

            # «cómo está armado»
            self.en("armado")
            preg = Text("¿cómo está armado?", color=INK).scale(0.5).move_to([XL, 2.45, 0])
            self.play(Transform(self.q1, preg), run_time=0.35)

            # «un conjunto de tareas»
            self.en("tareas")
            tareas = VGroup(*[caja(t, c, ancho=1.5, alto=1.2, scale=0.9)
                              for t, c in zip("ABC", COLS)]).arrange(RIGHT, buff=0.55).move_to([XL, 1.2, 0])
            self.pop(*tareas)

            # «podrían ejecutarse»
            self.en("ejecutarse")
            self.resaltar(*tareas, color=TXT, escala=1.12, run_time=0.45)

            # «en cualquier orden»
            self.en("orden")
            pos = [t.get_center() for t in tareas]
            for orden in ((1, 2, 0), (2, 0, 1)):
                self.sfx("tick", gain=-20)
                self.play(*[t.animate.move_to(pos[o]) for t, o in zip(tareas, orden)], run_time=0.4,
                          path_arc=0.8)

            # «intercaladas entre sí»: cada tarea en tres pedacitos, intercalados en una fila
            self.en("intercaladas")
            self.pedazos = VGroup()
            origen = []
            for k in range(3):
                for t, c in zip(tareas, COLS):
                    r = Rectangle(width=0.55, height=0.85, stroke_width=0).set_fill(c, 0.85).move_to(t)
                    self.pedazos.add(r)
            destino = self.pedazos.copy().arrange(RIGHT, buff=0.06).move_to([XL, 0.0, 0])
            self.play(FadeOut(tareas), FadeIn(self.pedazos), run_time=0.2)
            self.sfx("whoosh", gain=-18)
            self.play(*[p.animate.move_to(d) for p, d in zip(self.pedazos, destino)], run_time=0.7)

            # «Fíjate»
            self.en("fijate")
            self.play(self.camera.frame.animate.set(width=10.5).move_to([XL + 0.4, 0.2, 0]), run_time=0.5)

            # «un solo núcleo»: ejecuta los pedacitos de a uno
            self.en("nucleo")
            self.nuc = nucleo(INK, 1.4).move_to([XL, -1.85, 0])
            self.play(self.camera.frame.animate.set(width=config.frame_width).move_to(ORIGIN),
                      FadeIn(self.nuc, shift=UP * 0.3), run_time=0.45)
            for p in self.pedazos:
                self.play(self.nuc.caja.animate.set_fill(p.get_fill_color(), 0.7).set_stroke(p.get_fill_color()),
                          p.animate(rate_func=there_and_back).shift(DOWN * 0.15), run_time=0.11)

            # «perfectamente concurrente»
            self.en("concurrente")
            self.ok = tilde(VERDE, 0.7).next_to(self.w1, RIGHT, buff=0.25)
            self.sfx("exito", gain=-14)
            self.play(Create(self.ok), run_time=0.35)

            # «dos cosas pasando literalmente»: la línea vertical nunca toca dos
            self.en("literal")
            x0, x1 = self.pedazos.get_left()[0] - 0.1, self.pedazos.get_right()[0] + 0.1
            barrido = Line([x0, 0.7, 0], [x0, -0.7, 0], color=TXT, stroke_width=4)
            self.play(Create(barrido), run_time=0.2)
            self.play(barrido.animate.set_x(x1), run_time=1.3, rate_func=linear)
            self.play(FadeOut(barrido), run_time=0.2)

            # «Y sigue teniendo»
            self.en("sigue")
            alerta = Text("!", color=AMBAR, weight=BOLD).scale(1.3).move_to(self.ok)
            self.play(Transform(self.ok, alerta), run_time=0.3)

            # «dolores de cabeza»
            self.en("dolores")
            rayos = VGroup(rayo().scale(1.6).move_to([XL - 2.9, 1.2, 0]), rayo().scale(1.6).move_to([XL + 2.9, 1.2, 0]),
                           rayo().scale(1.6).move_to([XL + 1.9, -1.8, 0]))
            self.sfx("error", gain=-18)
            self.play(LaggedStart(*[Create(r) for r in rayos], lag_ratio=0.3), run_time=0.45)

            # «de coordinación»: dos tareas van por el mismo dato
            self.en("coordinacion")
            dato = caja("x", AMBAR, ancho=1.0, alto=0.75, scale=0.7).move_to([XL - 2.4, -1.85, 0])
            fa = Arrow(self.pedazos[0].get_bottom(), dato.get_top() + LEFT * 0.15, color=COLS[0], stroke_width=4, buff=0.08)
            fb = Arrow(self.pedazos[1].get_bottom(), dato.get_top() + RIGHT * 0.15, color=COLS[1], stroke_width=4, buff=0.08)
            self.play(FadeIn(dato), GrowArrow(fa), GrowArrow(fb), run_time=0.4)
            self.sfx("impacto", gain=-16)
            self.play(Flash(dato, color=ROJO, line_length=0.35, flash_radius=0.6), run_time=0.4)

    # ------------------------------------------------------------------ b3
    def paralelismo(self):
        with self.narrar("b3"):
            # «Paralelismo responde»
            self.en("paralelismo")
            w = Text("paralelismo", color=CIAN, weight=BOLD).scale(0.88).move_to(self.w2)
            self.play(Transform(self.w2, w), run_time=0.35)

            # «realmente en el hardware»: cuatro núcleos
            self.en("hardware")
            preg = Text("¿qué pasa en el hardware?", color=INK).scale(0.5).move_to([XR, 2.45, 0])
            self.nucs = VGroup(*[nucleo(c, 1.0) for c in COLS]).arrange(DOWN, buff=0.2).move_to([1.5, -0.4, 0])
            self.play(Transform(self.q2, preg), run_time=0.3)
            self.play(LaggedStart(*[FadeIn(n, shift=RIGHT * 0.3) for n in self.nucs], lag_ratio=0.15), run_time=0.45)

            # «Es que haya instrucciones»
            self.en("instrucciones")
            lineas = VGroup()
            for n in self.nucs:
                lineas.add(VGroup(*[Line(LEFT * 0.28, RIGHT * 0.28, color=TXT, stroke_width=3) for _ in range(3)])
                           .arrange(DOWN, buff=0.13).move_to(n))
            self.play(LaggedStart(*[Create(l) for l in lineas], lag_ratio=0.2), run_time=0.5)
            self.nucs_g = VGroup(self.nucs, lineas)

            # «de manera simultánea»: cuatro barras que crecen juntas
            self.en("simultanea")
            x0, x1 = 2.1, 6.4
            self.barras = VGroup(*[Rectangle(width=x1 - x0, height=0.62, stroke_width=0).set_fill(c, 0.8)
                                   .move_to([(x0 + x1) / 2, n.get_y(), 0]) for n, c in zip(self.nucs, COLS)])
            self.play(*[GrowFromEdge(b, LEFT) for b in self.barras], run_time=1.0, rate_func=linear)

            # «de verdad»: la línea vertical toca las cuatro a la vez
            self.en("verdad")
            barrido = Line([x0, 1.95, 0], [x0, -2.75, 0], color=TXT, stroke_width=4)
            self.play(Create(barrido), run_time=0.2)
            self.play(barrido.animate.set_x(x1), run_time=1.2, rate_func=linear)

            # «núcleos distintos»: se enmarcan en un solo chip
            self.en("nucleos")
            marco = SurroundingRectangle(self.nucs_g, color=AMBAR, buff=0.25, corner_radius=0.12, stroke_width=4)
            self.play(FadeOut(self.barras), FadeOut(barrido), run_time=0.25)
            chip_ = VGroup(marco, self.nucs_g)
            self.play(Create(marco), run_time=0.35)
            self.play(chip_.animate.move_to([XR, -0.3, 0]), run_time=0.4)

            # «procesadores distintos»: dos chips
            self.en("procesadores")
            self.play(chip_.animate.scale(0.75).move_to([2.2, -0.3, 0]), run_time=0.35)
            chip2 = chip_.copy()
            self.play(chip2.animate.move_to([5.0, -0.3, 0]), run_time=0.4)

            # «ordenadores distintos»
            self.en("ordenadores")
            pcs = VGroup(ordenador().scale(1.9).move_to([2.0, -0.3, 0]), ordenador().scale(1.9).move_to([5.2, -0.3, 0]))
            self.play(ReplacementTransform(chip_, pcs[0]), ReplacementTransform(chip2, pcs[1]), run_time=0.45)

            # «conectados en red»
            self.en("red")
            cable = Line(pcs[0].get_right(), pcs[1].get_left(), color=AMBAR, stroke_width=4)
            self.play(Create(cable), run_time=0.3)
            paq = [Dot(radius=0.09, color=AMBAR).move_to(cable.get_start()),
                   Dot(radius=0.09, color=CIAN).move_to(cable.get_end())]
            self.add(*paq)
            self.play(paq[0].animate.move_to(cable.get_end()), paq[1].animate.move_to(cable.get_start()),
                      run_time=0.7)
            self.remove(*paq)
