"""Escena 02 — El truco de la multiprogramación: planificador y quantum."""
from comun.escena import *


def cpu_un_nucleo(ancho: float = 3.2, alto: float = 2.4) -> VGroup:
    c = cpu(ancho, alto)
    n = nucleo(AZUL, 0.9).move_to(c.caja.get_center() + DOWN * 0.25)
    g = VGroup(c, n)
    g.caja, g.nucleo = c.caja, n
    return g


class Multiprogramacion(EscenaCurso):
    ESCENA = "02"

    def construct(self):
        self.pregunta()
        self.planificador()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def pregunta(self):
        with self.narrar("b1"):
            # «la vida de un proceso»
            self.en("vida")
            est = mini_estados().scale(3.2)
            punto = Dot(radius=0.14, color=AMBAR).move_to(est[0])
            self.play(FadeIn(est, scale=0.8), FadeIn(punto), run_time=0.4)
            self.play(Succession(*[punto.animate(run_time=0.22).move_to(est[k]) for k in (1, 2, 4, 1, 2, 3)]),
                      run_time=1.3)

            # «hagamos una pregunta»
            self.en("pregunta")
            q = Text("?", color=AMBAR, weight=BOLD).scale(5)
            self.play(FadeOut(VGroup(est, punto), scale=0.7), run_time=0.25)
            self.pop(q, sonido="impacto")

            # «pero no lo es»
            self.en("noes")
            self.play(Succession(q.animate(run_time=0.15).rotate(0.25), q.animate(run_time=0.2).rotate(-0.5),
                                 q.animate(run_time=0.15).rotate(0.25)), run_time=0.5)

            # «tu ordenador»
            self.en("ordenador")
            pc = ordenador().scale(3.6).move_to(DOWN * 0.5)
            self.play(q.animate.scale(0.3).move_to(UP * 3.2), FadeIn(pc, shift=UP * 0.4), run_time=0.45)

            # «con un solo procesador»: el ordenador se abre y queda la CPU
            self.en("cpu")
            self.cpu = cpu_un_nucleo(3.6, 2.6).move_to(DOWN * 2.2)
            self.play(FadeOut(pc, scale=1.5), FadeOut(q), FadeIn(self.cpu, scale=0.5), run_time=0.5)

            # «el navegador», «la música», «este mismo vídeo»
            mus, nav, vid = ventanas_multitarea()
            self.wins = VGroup(nav, mus, vid)
            for w, x in zip(self.wins, (-4.4, 0, 4.4)):
                w.scale(1.35).move_to([x, 1.7, 0])
            for marca, w in zip(("navegador", "musica", "video"), self.wins):
                self.en(marca)
                self.pop(w)

            # «al mismo tiempo»: cables de cada ventana a la CPU
            self.en("mismo")
            cols = (AZUL, MAGENTA, ROJO)
            self.cables = VGroup(*[Line(w.get_bottom(), self.cpu.caja.get_top(), color=c, stroke_width=5)
                                   for w, c in zip(self.wins, cols)])
            self.play(LaggedStart(*[Create(c) for c in self.cables], lag_ratio=0.15), run_time=0.45)
            self.sfx("latido", gain=-16)
            self.play(*[c.animate(rate_func=there_and_back).set_stroke(width=11) for c in self.cables],
                      *[w.animate(rate_func=there_and_back).scale(1.06) for w in self.wins], run_time=0.45)

            # «spoiler»
            self.en("spoiler")
            sello = VGroup(RoundedRectangle(width=3.8, height=1.0, corner_radius=0.1, color=ROJO, stroke_width=5)
                           .set_fill(FONDO, 1),
                           Text("SPOILER", color=ROJO, weight=BOLD).scale(0.8))
            sello.move_to(DOWN * 0.1).rotate(0.12)
            self.pop(sello, sonido="impacto")
            self.wait(0.5)
            self.play(FadeOut(sello, scale=1.3), run_time=0.25)

            # «no corren al mismo tiempo»
            self.en("nocorren")
            self.encender(0, run_time=0.4)

            # «Es un truco»
            self.en("truco")
            truco = self.palabra("truco", escala=1.1).move_to(UP * 3.35)
            self.pop(truco)

            # «los intercala»: el cable encendido salta, cada vez más rápido
            self.en("intercala")
            for i, d in enumerate([0.3, 0.25, 0.2, 0.16, 0.13, 0.1, 0.08, 0.07]):
                self.sfx("tick", gain=-22)
                self.encender((i + 1) % 3, run_time=d)

            # «nuestro cerebro no lo nota»
            self.en("nota")
            for i in range(12):
                self.encender(i % 3, run_time=0.05)
            self.play(*[c.animate.set_stroke(opacity=1) for c in self.cables],
                      *[w.animate.set_opacity(1) for w in self.wins], run_time=0.3)

    def encender(self, k: int, run_time: float):
        self.play(*[c.animate.set_stroke(opacity=1 if i == k else 0.12) for i, c in enumerate(self.cables)],
                  *[w.animate.set_opacity(1 if i == k else 0.3) for i, w in enumerate(self.wins)],
                  run_time=run_time)

    # ------------------------------------------------------------------ b2
    def planificador(self):
        with self.narrar("b2"):
            # «multiprogramación»
            self.en("multi")
            self.corte()
            multi = self.palabra("multiprogramación", escala=1.4)
            self.pop(multi, sonido="impacto")

            # «varios procesos»
            self.en("procesos")
            procs = VGroup(*[caja_proceso(f"P{i + 1}", c, ancho=2.3, alto=1.1)
                             for i, c in enumerate((AZUL, CIAN, MAGENTA))]).arrange(DOWN, buff=0.35)
            procs.move_to(LEFT * 4.6 + DOWN * 0.3)
            self.play(multi.animate.scale(0.55).move_to(UP * 3.45),
                      LaggedStart(*[FadeIn(p, shift=RIGHT * 0.4) for p in procs], lag_ratio=0.2), run_time=0.6)

            # «un solo procesador»
            self.en("uno")
            cpu_ = cpu_un_nucleo(3.6, 2.8).move_to(RIGHT * 3.9 + DOWN * 0.3)
            cpu_.nucleo.set_opacity(0)
            self.play(FadeIn(cpu_, shift=LEFT * 0.4), run_time=0.4)

            # «Dentro del sistema operativo»
            self.en("so")
            marco = RoundedRectangle(width=13.4, height=5.5, corner_radius=0.2, color=INK, stroke_width=2.5)
            marco.move_to(DOWN * 0.15)
            rot = Text("sistema operativo", color=INK).scale(0.42).move_to(marco.get_corner(UL) + [1.55, -0.32, 0])
            self.play(Create(marco), FadeIn(rot), run_time=0.6)

            # «un componente»
            self.en("componente")
            hueco = DashedVMobject(RoundedRectangle(width=1.7, height=1.7, corner_radius=0.15, color=AMBAR,
                                                    stroke_width=3), num_dashes=24).move_to(DOWN * 0.1)
            self.play(Create(hueco), run_time=0.4)

            # «director de orquesta»
            self.en("director")
            bat = batuta().scale(1.2).move_to(hueco)
            self.pop(bat)

            # «el planificador»
            self.en("planificador")
            lab = Text("planificador", color=AMBAR, weight=BOLD).scale(0.5).next_to(hueco, DOWN, buff=0.2)
            self.play(FadeIn(lab, shift=UP * 0.2), run_time=0.3)

            # «en cada instante»: la batuta señala a cada proceso
            self.en("instante")
            for p in procs:
                ang = np.arctan2(*(p.get_center() - bat.get_center())[1::-1]) - PI / 4
                self.sfx("tick", gain=-20)
                self.play(Rotate(bat, ang - getattr(bat, "_ang", 0)), self.resaltar_anim(p), run_time=0.3)
                bat._ang = ang

            # «pasa a ejecutar»
            self.en("ejecutar")
            p1 = procs[0]
            self.sfx("whoosh", gain=-18)
            self.play(p1.animate.move_to(cpu_.caja.get_center() + DOWN * 0.25), Rotate(bat, -bat._ang),
                      run_time=0.5)

            # «por cuánto tiempo»: barra de quantum que se vacía
            self.en("tiempo")
            borde = Rectangle(width=2.6, height=0.32, color=TXT, stroke_width=2).next_to(cpu_, UP, buff=0.25)
            barra = Rectangle(width=2.6, height=0.32, stroke_width=0).set_fill(AMBAR, 0.85).move_to(borde)
            self.play(FadeIn(borde), FadeIn(barra), run_time=0.25)
            self.play(barra.animate.stretch_to_fit_width(1.9, about_edge=LEFT), run_time=max(0.3, self.margen() - 0.35),
                      rate_func=linear)

            # «ese tiempo asignado»
            self.en("asignado")
            self.play(borde.animate.set_stroke(AMBAR, width=5), run_time=0.3)
            self.play(barra.animate.stretch_to_fit_width(1.3, about_edge=LEFT), run_time=max(0.3, self.margen() - 0.35),
                      rate_func=linear)

            # «se llama quantum»
            self.en("quantum")
            q = Text("quantum", color=AMBAR, weight=BOLD).scale(0.6).next_to(borde, UP, buff=0.12)
            self.pop(q)
            self.play(barra.animate.stretch_to_fit_width(0.7, about_edge=LEFT), run_time=1.0, rate_func=linear)

    def resaltar_anim(self, m: Mobject):
        return m.animate(rate_func=there_and_back).scale(1.1)
