"""Escena 09 — El PCB: la ficha de cada proceso, su costo, y padre/hijo."""
from comun.escena import *

CAMPOS = [("pid", "PID", "4127"), ("estado", "estado", "ejecutando"), ("pc", "PC", "0x2A"),
          ("regs", "registros", "R1=7  R2=0"), ("mem", "memoria", "0x4000–0x7FFF"),
          ("prio", "prioridad", "alta"), ("archivos", "archivos", "notas.txt")]


def tuerca(color=INK) -> VGroup:
    hexa = RegularPolygon(6, color=color, stroke_width=4).set_fill(color, 0.5).scale(0.9)
    agujero = Circle(radius=0.35, color=color, stroke_width=4).set_fill(FONDO, 1)
    return VGroup(hexa, agujero)


def caja_simple(ancho, alto, color) -> RoundedRectangle:
    return RoundedRectangle(width=ancho, height=alto, corner_radius=0.15, color=color,
                            stroke_width=3).set_fill(color, 0.12)


class PCB(EscenaCurso):
    ESCENA = "09"

    def construct(self):
        self.ficha()
        self.costo()
        self.padre_hijo()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def ficha(self):
        with self.narrar("b1"):
            self.en("identidad")
            self.p1 = caja_proceso("P1", AZUL, ancho=2.4, alto=1.6).move_to(LEFT * 4.6)
            self.p1[1].set_opacity(0)
            duda = Text("?", color=AMBAR, weight=BOLD).scale(1.4).next_to(self.p1, UP, buff=0.2)
            self.play(FadeIn(self.p1, shift=RIGHT * 0.4), run_time=0.35)
            self.pop(duda)

            self.en("nace")
            self.sfx("latido", gain=-14)
            self.play(self.p1[0].animate(rate_func=there_and_back).scale(1.15), run_time=0.5)

            self.en("ficha")
            self.marco = RoundedRectangle(width=5.2, height=5.6, corner_radius=0.15, color=AMBAR,
                                          stroke_width=3).set_fill(FONDO, 1).move_to(RIGHT * 1.9)
            self.sfx("whoosh", gain=-16)
            self.play(FadeIn(self.marco, shift=LEFT * 2), FadeOut(duda), self.p1[1].animate.set_opacity(1),
                      run_time=0.5)

            self.en("pcb")
            titulo = Text("PCB", color=AMBAR, weight=BOLD).scale(0.9).move_to(self.marco.get_top() + DOWN * 0.5)
            self.pop(titulo)
            self.filas = VGroup(titulo)

            self.en("anota")
            ys = [titulo.get_bottom()[1] - 0.45 - 0.62 * i for i in range(len(CAMPOS))]
            renglones = VGroup(*[DashedLine([self.marco.get_left()[0] + 0.3, y - 0.24, 0],
                                            [self.marco.get_right()[0] - 0.3, y - 0.24, 0],
                                            color=INK, stroke_width=1.5, dash_length=0.08) for y in ys])
            self.play(LaggedStart(*[Create(r) for r in renglones], lag_ratio=0.1), run_time=0.6)

            for (marca, campo, valor), y in zip(CAMPOS, ys):
                self.en(marca)
                c = Text(campo, color=INK).scale(0.48)
                v = Text(valor, color=TXT, weight=BOLD).scale(0.48)
                c.move_to([self.marco.get_left()[0] + 0.35, y, 0], aligned_edge=LEFT)
                v.move_to([self.marco.get_right()[0] - 0.35, y, 0], aligned_edge=RIGHT)
                fila = VGroup(c, v)
                self.sfx("tick", gain=-14)
                self.play(FadeIn(fila, shift=LEFT * 0.3), run_time=0.25)
                self.filas.add(fila)
            self.ficha1 = VGroup(self.marco, renglones, self.filas)

    # ------------------------------------------------------------------ b2
    def costo(self):
        with self.narrar("b2"):
            self.en("costo")
            f1 = self.ficha1
            self.play(self.p1.animate.scale(0.6).move_to(LEFT * 5.3 + UP * 2.2),
                      f1.animate.scale(0.32).move_to(LEFT * 3.6 + UP * 2.2), run_time=0.6)
            p2 = caja_proceso("P2", MAGENTA, ancho=2.4, alto=1.6).scale(0.6).move_to(RIGHT * 5.3 + UP * 2.2)
            f2 = f1.copy()
            f2[0].set_stroke(MAGENTA)          # solo el borde: set_color pintaría el relleno
            f2[2][0].set_color(MAGENTA)        # el título «PCB»
            f2.move_to(RIGHT * 3.6 + UP * 2.2)
            self.play(FadeIn(p2, shift=LEFT * 0.4), FadeIn(f2, shift=LEFT * 0.4), run_time=0.4)

            self.en("pasar")
            c = cpu().scale(1.5).move_to(DOWN * 1.0)
            dentro = c.caja.get_center() + DOWN * 0.35
            en_cpu = f1.copy().scale(0.85).move_to(dentro)
            self.play(FadeIn(c), TransformFromCopy(f1, en_cpu), run_time=0.5)

            self.en("guardar")
            self.sfx("whoosh", gain=-14)
            self.play(en_cpu.animate.move_to(f1).set_opacity(0), f1[0].animate(rate_func=there_and_back).set_stroke(AZUL, 6), run_time=0.6)
            self.remove(en_cpu)

            self.en("cargar")
            entra = f2.copy()
            self.sfx("whoosh", gain=-14)
            self.play(entra.animate.scale(0.85).move_to(dentro), run_time=0.6)

            self.en("info")
            # "bits" que viajan entre la CPU y las dos fichas, en ambos sentidos
            caminos = [Line(c.caja.get_left(), f1.get_bottom(), color=AZUL, stroke_width=2),
                       Line(c.caja.get_right(), f2.get_bottom(), color=MAGENTA, stroke_width=2)]
            self.play(*[Create(l) for l in caminos], run_time=0.3)
            bits = []
            for k in range(10):
                l = caminos[k % 2]
                d = Dot(radius=0.07, color=l.get_color())
                bits.append(MoveAlongPath(d, l if k % 4 < 2 else l.copy().reverse_points()))
            self.sfx("whoosh", gain=-20)
            self.play(LaggedStart(*bits, lag_ratio=0.15), run_time=1.6)
            self.play(*[FadeOut(b.mobject) for b in bits], *[FadeOut(l) for l in caminos], run_time=0.25)

            self.en("tiempo")
            barra = Rectangle(width=5.0, height=0.35, color=INK, stroke_width=2).next_to(c, DOWN, buff=0.35)
            relleno = Rectangle(width=5.0, height=0.35, stroke_width=0).set_fill(ROJO, 0.8)
            relleno.align_to(barra, LEFT).align_to(barra, DOWN).stretch_to_fit_width(0.01, about_edge=LEFT)
            rel = reloj().scale(0.45).next_to(barra, LEFT, buff=0.25)
            self.add(barra, rel, relleno)
            self.play(relleno.animate.stretch_to_fit_width(5.0, about_edge=LEFT),
                      Rotate(rel.minutero, -2 * PI, about_point=rel.aro.get_center()), run_time=1.1)

            self.en("precio")
            for _ in range(2):
                self.sfx("latido", gain=-16)
                self.play(relleno.animate(rate_func=there_and_back).set_fill(ROJO, 1).stretch(1.4, 1), run_time=0.45)

    # ------------------------------------------------------------------ b3
    def padre_hijo(self):
        with self.narrar("b3"):
            self.en("tuerca")
            self.corte()
            t = tuerca().scale(1.4)
            self.pop(t, sonido="tick")
            self.play(Rotate(t, -2 * PI), run_time=0.9)

            self.en("vida")
            padre = caja_simple(3.0, 2.0, AZUL)
            hijo = caja_simple(2.2, 1.5, CIAN)
            self.play(FadeOut(t, scale=0.5), FadeIn(padre), run_time=0.3)
            self.add(hijo)
            self.sfx("pop")
            self.play(padre.animate.move_to(LEFT * 2.6), hijo.animate.move_to(RIGHT * 2.6),
                      run_time=0.7, rate_func=rate_functions.ease_out_back)

            self.en("padre")
            t_p = Text("padre", color=AZUL, weight=BOLD).scale(0.8).next_to(padre, UP, buff=0.3)
            self.pop(t_p)

            self.en("hijo")
            t_h = Text("hijo", color=CIAN, weight=BOLD).scale(0.8).next_to(hijo, UP, buff=0.3)
            self.pop(t_h)

            self.en("importante")
            self.resaltar(padre, hijo, color=AMBAR, run_time=0.6)

            self.en("corren")

            def con_pc(caja_x, n):
                lineas = VGroup(*[Line(LEFT * 0.7, RIGHT * 0.7, color=INK, stroke_width=3) for _ in range(n)])
                lineas.arrange(DOWN, buff=0.22).move_to(caja_x.get_center() + RIGHT * 0.2)
                punta = Triangle(color=AMBAR).set_fill(AMBAR, 1).rotate(-PI / 2).scale(0.13)
                punta.next_to(lineas[0], LEFT, buff=0.15)
                return lineas, punta

            l_p, pp = con_pc(padre, 5)
            l_h, ph = con_pc(hijo, 4)
            self.play(FadeIn(l_p), FadeIn(pp), FadeIn(l_h), FadeIn(ph), run_time=0.3)
            self.play(Succession(*[pp.animate(run_time=0.35).next_to(l_p[k], LEFT, buff=0.15) for k in range(1, 5)]),
                      Succession(*[ph.animate(run_time=0.22).next_to(l_h[k % 4], LEFT, buff=0.15)
                                   for k in range(1, 7)]), run_time=1.4)

            self.en("noespera")
            rel = reloj().scale(0.6).move_to(ORIGIN)
            self.play(FadeIn(rel, scale=0.5), run_time=0.3)
            self.sfx("error", gain=-18)
            tacha = tachar(rel)
            self.play(Create(tacha), run_time=0.3)

            self.en("camino")
            self.sfx("whoosh")
            self.play(FadeOut(rel), FadeOut(tacha), VGroup(padre, t_p, l_p, pp).animate.shift(LEFT * 2.2),
                      VGroup(hijo, t_h, l_h, ph).animate.shift(RIGHT * 2.2), run_time=0.8)
