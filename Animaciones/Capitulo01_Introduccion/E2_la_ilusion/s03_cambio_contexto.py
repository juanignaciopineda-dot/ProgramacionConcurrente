"""Escena 03 — El cambio de contexto: el peaje de la ilusión, y la línea de tiempo."""
from comun.escena import *

SLOTS = [np.array([-4.6, y, 0]) for y in (1.3, 0.0, -1.3)]
CPU_POS = np.array([3.5, 0.2, 0])


def ficha_pcb(color=AMBAR) -> VGroup:
    r = RoundedRectangle(width=0.8, height=1.0, corner_radius=0.08, color=color, stroke_width=2.5)
    r.set_fill(FONDO, 1)
    t = Text("PCB", color=color, weight=BOLD).scale(0.28).move_to(r.get_top() + DOWN * 0.2)
    renglones = VGroup(*[Line(LEFT * 0.25, RIGHT * 0.25, color=INK, stroke_width=2) for _ in range(3)])
    renglones.arrange(DOWN, buff=0.13).move_to(r.get_center() + DOWN * 0.15)
    return VGroup(r, t, renglones)


class CambioContexto(EscenaCurso):
    ESCENA = "03"

    def construct(self):
        self.cambio()
        self.linea_tiempo()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def cambio(self):
        with self.narrar("b1"):
            # «el quantum de un proceso»
            self.en("quantum")
            self.cpu = cpu(3.6, 2.6).move_to(CPU_POS)
            self.procs = [caja_proceso(f"P{i + 1}", c, ancho=2.3, alto=1.1)
                          for i, c in enumerate((AZUL, CIAN, MAGENTA))]
            p1, p2, p3 = self.procs
            p1.move_to(CPU_POS + DOWN * 0.3)
            p2.move_to(SLOTS[1])
            p3.move_to(SLOTS[2])
            borde = Rectangle(width=2.6, height=0.32, color=AMBAR, stroke_width=3).next_to(self.cpu, UP, buff=0.2)
            barra = Rectangle(width=1.0, height=0.32, stroke_width=0).set_fill(AMBAR, 0.85)
            barra.move_to(borde).align_to(borde, LEFT)
            self.play(FadeIn(VGroup(self.cpu, p1, p2, p3, borde, barra)), run_time=0.35)
            self.play(barra.animate.stretch_to_fit_width(0.25, about_edge=LEFT),
                      run_time=max(0.3, self.margen() - 0.3), rate_func=linear)

            # «se acaba»
            self.en("acaba")
            self.sfx("tick", gain=-12)
            self.play(FadeOut(barra), run_time=0.15)
            for _ in range(2):
                self.play(borde.animate(rate_func=there_and_back).set_stroke(ROJO, width=7), run_time=0.3)

            # «necesita esperar»
            self.en("esperar")
            rel = reloj().scale(0.6).next_to(self.cpu, RIGHT, buff=0.2).shift(UP * 0.8)
            self.pop(rel)

            # «el momento más delicado»
            self.en("delicado")
            self.sfx("latido", gain=-12)
            self.play(FadeOut(rel), FadeOut(borde), run_time=0.2)
            self.zoom(self.cpu, 8.0, run_time=0.7)

            # «el cambio de contexto»
            self.en("cambio")
            titulo = self.palabra("cambio de contexto", escala=0.75).next_to(self.cpu, DOWN, buff=0.3)
            self.pop(titulo, sonido="impacto")

            # «El sistema operativo»
            self.en("so")
            marco = RoundedRectangle(width=13.4, height=5.6, corner_radius=0.2, color=INK, stroke_width=2.5)
            marco.move_to(UP * 0.2)
            rot = Text("sistema operativo", color=INK).scale(0.42).move_to(marco.get_corner(UL) + [1.55, -0.32, 0])
            self.play(self.camera.frame.animate.move_to(ORIGIN).set(width=config.frame_width),
                      titulo.animate.scale(0.75).move_to(UP * 3.5), run_time=0.6)
            self.play(Create(marco), FadeIn(rot), run_time=0.4)
            self.play(marco.animate(rate_func=there_and_back).set_stroke(AMBAR, width=5), run_time=0.4)

            # «guarda el bloque de control»
            self.en("guarda")
            f1 = ficha_pcb(AZUL).move_to(CPU_POS + RIGHT * 1.3 + DOWN * 0.3)
            self.play(FadeIn(f1, scale=0.5), run_time=0.25)
            self.sfx("whoosh", gain=-18)
            self.play(f1.animate.move_to(SLOTS[0] + RIGHT * 1.7), run_time=0.5)

            # «proceso saliente»
            self.en("saliente")
            self.play(p1.animate.move_to(SLOTS[0]), run_time=0.5)

            # «carga el del que entra»
            self.en("carga")
            f2 = ficha_pcb(CIAN).move_to(SLOTS[1] + RIGHT * 1.7)
            self.play(FadeIn(f2, scale=0.5), run_time=0.2)
            self.sfx("whoosh", gain=-18)
            self.play(f2.animate.move_to(CPU_POS + RIGHT * 1.3 + DOWN * 0.3), run_time=0.45)
            self.play(p2.animate.move_to(CPU_POS + DOWN * 0.3), run_time=0.4)

            # «Ese ida y vuelta»
            self.en("idavuelta")
            a, b = f1.get_center(), f2.get_center()
            for k in range(4):
                self.sfx("tick", gain=-22)
                self.play(f1.animate.move_to(b if k % 2 == 0 else a), f2.animate.move_to(a if k % 2 == 0 else b),
                          run_time=0.22)

            # «no es gratis»: barra de tiempo del procesador
            self.en("gratis")
            x0, x1, yb = -6.0, 6.0, -3.25
            caja_t = Rectangle(width=x1 - x0, height=0.45, color=INK, stroke_width=2).move_to([0, yb, 0])
            self.play(Create(caja_t), run_time=0.4)

            # «consume tiempo de procesador»
            self.en("consume")
            segs, x, i = [], x0, 0
            while x < x1 - 0.01:
                w = min(1.5 if i % 2 == 0 else 0.35, x1 - x)
                col = AZUL if i % 2 == 0 else ROJO
                segs.append(Rectangle(width=w, height=0.45, stroke_width=0).set_fill(col, 0.85)
                            .move_to([x + w / 2, yb, 0]))
                x += w
                i += 1
            self.play(LaggedStart(*[GrowFromEdge(s, LEFT) for s in segs], lag_ratio=0.6), run_time=1.1)

            # «trabajo útil»
            self.en("util")
            util = Text("útil", color=AZUL, weight=BOLD).scale(0.38).next_to(segs[0], UP, buff=0.1)
            camb = Text("cambio", color=ROJO, weight=BOLD).scale(0.38).next_to(segs[1], UP, buff=0.1)
            self.play(FadeIn(util, shift=DOWN * 0.1), FadeIn(camb, shift=DOWN * 0.1), run_time=0.3)
            self.play(*[s.animate(rate_func=there_and_back).stretch(1.5, 1) for s in segs[1::2]], run_time=0.5)

            # «el peaje»: la barrera baja delante de la CPU
            self.en("peaje")
            bar = barrera(ROJO, largo=1.5).scale(1.2).move_to([0.6, 0.2, 0])
            pivote = bar.brazo.get_left()
            bar.brazo.rotate(PI / 2, about_point=pivote)
            self.play(FadeIn(bar), run_time=0.2)
            self.sfx("impacto", gain=-16)
            self.play(Rotate(bar.brazo, -PI / 2, about_point=pivote), run_time=0.4,
                      rate_func=rate_functions.ease_out_bounce)

            # «la ilusión de simultaneidad»
            self.en("ilusion")
            self.play(Rotate(bar.brazo, PI / 2, about_point=pivote), run_time=0.3)
            self.corte()
            wins = ventanas_multitarea().arrange(RIGHT, buff=0.5).scale(1.6)
            self.play(LaggedStart(*[FadeIn(w, scale=0.6) for w in wins], lag_ratio=0.1), run_time=0.4)
            self.sfx("latido", gain=-16)
            self.play(*[w.animate(rate_func=there_and_back).scale(1.08) for w in wins], run_time=0.45)

    # ------------------------------------------------------------------ b2
    def linea_tiempo(self):
        with self.narrar("b2"):
            ys = [1.8, 0.6, -0.6]
            cols = [AZUL, CIAN, MAGENTA]
            x0, x1 = -5.2, 6.2

            # «una línea de tiempo»
            self.en("linea")
            self.corte()
            eje = Arrow([x0 - 0.2, -1.3, 0], [x1 + 0.4, -1.3, 0], color=INK, stroke_width=3, buff=0,
                        max_tip_length_to_length_ratio=0.03)
            t_lab = Text("tiempo", color=INK).scale(0.36).next_to(eje, DOWN, buff=0.12).align_to(eje, RIGHT)
            etiquetas = VGroup(*[Text(f"P{i + 1}", color=c, weight=BOLD).scale(0.5).move_to([-6.0, y, 0])
                                 for i, (y, c) in enumerate(zip(ys, cols))])
            guias = VGroup(*[DashedLine([x0, y, 0], [x1, y, 0], color=INK, stroke_width=1.2, dash_length=0.1)
                             .set_opacity(0.4) for y in ys])
            self.play(Create(eje), FadeIn(t_lab), FadeIn(etiquetas, shift=RIGHT * 0.2), Create(guias), run_time=0.6)

            # «un vaivén constante»: el cabezal
            self.en("vaiven")
            cab = Line([x0, 2.35, 0], [x0, -1.3, 0], color=AMBAR, stroke_width=4)
            self.play(Create(cab), run_time=0.3)

            w, g = 0.85, 0.18
            self.slices, self.gaps = [], []
            x, i = x0, 0
            while x + w <= x1 + 0.01:
                s = Rectangle(width=w, height=0.7, stroke_width=0).set_fill(cols[i % 3], 0.85)
                s.move_to([x + w / 2, ys[i % 3], 0])
                self.slices.append(s)
                if x + w + g + w <= x1 + 0.01:
                    gap = Rectangle(width=g, height=3.1, stroke_width=0).set_fill(ROJO, 0.28)
                    gap.move_to([x + w + g / 2, 0.6, 0])
                    self.gaps.append(gap)
                x += w + g
                i += 1

            # «un pedacito de un proceso» / «de otro» / «del tercero»
            for k, marca in enumerate(("p1", "p2", "p3")):
                self.en(marca)
                s = self.slices[k]
                self.sfx("tick", gain=-20)
                self.play(GrowFromEdge(s, LEFT), cab.animate.set_x(s.get_right()[0]), run_time=0.5)
            resto = self.slices[3:]
            self.play(LaggedStart(*[GrowFromEdge(s, LEFT) for s in resto], lag_ratio=0.9),
                      cab.animate.set_x(resto[-1].get_right()[0]), run_time=1.2, rate_func=linear)

            # «No hay paralelismo real»: la línea vertical toca un solo tramo a la vez
            self.en("noparalelo")
            self.play(cab.animate.set_x(x0).set_color(TXT), run_time=0.25)
            self.play(cab.animate.set_x(x1), run_time=1.4, rate_func=linear)
            self.play(FadeOut(cab), run_time=0.2)

            # «ese costo extra»
            self.en("costo")
            self.play(LaggedStart(*[FadeIn(gp) for gp in self.gaps], lag_ratio=0.3), run_time=0.6)

            # «de los cambios de contexto»
            self.en("cambios")
            self.sfx("error", gain=-20)
            self.play(*[gp.animate(rate_func=there_and_back).set_fill(ROJO, 0.9) for gp in self.gaps], run_time=0.5)

            # «Y aun así»: el gráfico sube y se achica
            self.en("aunasi")
            grafico = VGroup(eje, t_lab, etiquetas, guias, *self.slices, *self.gaps)
            self.play(grafico.animate.scale(0.72).move_to([-0.8, 1.85, 0]), run_time=0.6)
            inter = Text("intercalado", color=AMBAR, weight=BOLD).scale(0.5).move_to([-0.8, 3.55, 0])
            self.play(FadeIn(inter, shift=DOWN * 0.2), run_time=0.3)

            # «mejora muchísimo»: medidor de uso del procesador
            self.en("mejora")
            m1_borde = Rectangle(width=0.6, height=2.2, color=TXT, stroke_width=2).move_to([5.9, 1.85, 0])
            m1 = Rectangle(width=0.6, height=2.2 * 0.9, stroke_width=0).set_fill(VERDE, 0.8)
            m1.move_to(m1_borde).align_to(m1_borde, DOWN)
            uso = Text("uso de CPU", color=INK).scale(0.36).move_to([5.9, 0.15, 0])
            self.play(Create(m1_borde), FadeIn(uso), run_time=0.3)
            self.play(GrowFromEdge(m1, DOWN), run_time=0.6)

            # «comparado con»: segundo eje, uno detrás de otro
            self.en("comparado")
            abajo = VGroup(eje.copy(), t_lab.copy(), etiquetas.copy(), guias.copy()).shift(DOWN * 3.4)
            seq = Text("uno detrás de otro", color=INK, weight=BOLD).scale(0.45).move_to([-0.8, 0.14, 0])
            self.play(FadeIn(seq, shift=DOWN * 0.2), FadeIn(abajo, shift=DOWN * 0.3), run_time=0.5)

            # «a todos los demás»: P1 ocupa todo, con huecos de espera; P2 y P3 esperan
            self.en("esperan")
            gx0, gx1 = self.slices[0].get_left()[0], self.slices[-1].get_right()[0]
            y_lanes = [e.get_y() for e in abajo[2]]
            alto = self.slices[0].height
            bloques, x, i = [], gx0, 0
            while x < gx1 - 0.01:
                w = min(0.9 if i % 2 == 0 else 0.6, gx1 - x)
                col = (AZUL, 0.85) if i % 2 == 0 else (INK, 0.3)
                bloques.append(Rectangle(width=w, height=alto, stroke_width=0).set_fill(*col)
                               .move_to([x + w / 2, y_lanes[0], 0]))
                x += w
                i += 1
            relojes = VGroup(*[reloj().scale(0.32).move_to([gx0 + 0.3, y, 0]) for y in y_lanes[1:]])
            self.play(LaggedStart(*[GrowFromEdge(b, LEFT) for b in bloques], lag_ratio=0.8), run_time=1.1,
                      rate_func=linear)
            self.pop(*relojes, sonido="tick")

            # «de punta a punta»
            self.en("punta")
            m2_borde = m1_borde.copy().move_to([5.9, -1.55, 0])
            m2 = Rectangle(width=0.6, height=2.2 * 0.5, stroke_width=0).set_fill(AMBAR, 0.8)
            m2.move_to(m2_borde).align_to(m2_borde, DOWN)
            self.play(Create(m2_borde), run_time=0.25)
            self.play(GrowFromEdge(m2, DOWN), run_time=0.5)
            ok = tilde(VERDE, 0.6).next_to(inter, RIGHT, buff=0.3)
            self.sfx("exito", gain=-14)
            self.play(Create(ok), run_time=0.3)
