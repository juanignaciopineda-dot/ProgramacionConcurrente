"""Escena 10 — Resumen y la trampa que abre el episodio 3: uno más uno igual a uno."""
from comun.escena import *

COLS = (AZUL, CIAN, MAGENTA)


class Cierre(EscenaCurso):
    ESCENA = "10"

    def construct(self):
        with self.narrar("b1"):
            # «Resumamos»: cuatro casilleros
            self.en("resumen")
            slots = VGroup(*[RoundedRectangle(width=3.1, height=3.4, corner_radius=0.2, color=INK, stroke_width=2)
                             for _ in range(4)]).arrange(RIGHT, buff=0.25)
            self.play(LaggedStart(*[Create(s) for s in slots], lag_ratio=0.2), run_time=0.6)

            # «Con un solo procesador»: CPU y tiras
            self.en("r1")
            nuc = nucleo(INK, 1.2).move_to(slots[0].get_center() + UP * 0.7)
            tiras = VGroup(*[Rectangle(width=0.36, height=0.55, stroke_width=0).set_fill(COLS[k % 3], 0.35)
                             for k in range(6)]).arrange(RIGHT, buff=0.06)
            tiras.move_to(slots[0].get_center() + DOWN * 0.9)
            self.pop(nuc, tiras)

            # «el sistema intercala»
            self.en("intercala")
            for k in range(6):
                self.play(nuc.caja.animate.set_fill(COLS[k % 3], 0.6).set_stroke(COLS[k % 3]),
                          *[t.animate.set_fill(opacity=0.95 if i == k else 0.3) for i, t in enumerate(tiras)],
                          run_time=0.17)

            # «parecen simultáneos»
            self.en("simult")
            for k in range(12):
                self.play(nuc.caja.animate.set_fill(COLS[k % 3], 0.6).set_stroke(COLS[k % 3]),
                          *[t.animate.set_fill(opacity=0.95 if i == k % 6 else 0.3) for i, t in enumerate(tiras)],
                          run_time=0.05)
            self.play(*[t.animate.set_fill(opacity=0.95) for t in tiras], run_time=0.2)

            # «Dentro de un proceso»
            self.en("dentro")
            self.resaltar(slots[1], color=AMBAR, escala=1.05)

            # «los hilos comparten»: la casa
            self.en("r2")
            c = casa(2.5, 1.5).move_to(slots[1].get_center() + DOWN * 0.1)
            c.muros.move_to(slots[1].get_center() + DOWN * 0.45)
            c.tejado.next_to(c.muros, UP, buff=0)
            mem = RoundedRectangle(width=1.0, height=0.45, corner_radius=0.08, color=AMBAR, stroke_width=2.5)
            mem.set_fill(AMBAR, 0.15).move_to(c.muros.get_center() + UP * 0.3)
            gente = VGroup(*[persona(col).scale(0.45) for col in COLS]).arrange(RIGHT, buff=0.3)
            gente.move_to(c.muros.get_center() + DOWN * 0.35)
            self.pop(VGroup(c, mem, gente))

            # «Concurrencia es»
            self.en("r3")
            t3 = self.texto_centrado("concurrencia", "≠", "paralelismo", color=AMBAR, escala=0.52)
            t3.move_to(slots[2].get_center() + UP * 0.6)
            self.pop(t3)

            # «paralelismo, lo que pasa»: cuatro núcleos encendidos
            self.en("paral")
            nucs = VGroup(*[nucleo(col, 0.45) for col in (AZUL, CIAN, MAGENTA, VERDE)]).arrange(RIGHT, buff=0.12)
            nucs.move_to(slots[2].get_center() + DOWN * 0.95)
            self.play(LaggedStart(*[FadeIn(n, scale=0.5) for n in nucs], lag_ratio=0.15), run_time=0.35)
            self.play(*[n.caja.animate.set_fill(n.caja.get_color(), 0.7) for n in nucs], run_time=0.25)

            # «el orden en que se intercalan»: el árbol
            self.en("r4")
            arbol = ArbolIntercalado(2, 2, ancho=2.4, alto=2.6, etiquetas=False, radio=0.045, grosor=2.5)
            arbol.move_to(slots[3])
            self.play(Create(arbol), run_time=0.6)

            # «no lo decide nadie»
            self.en("nadie")
            self.play(Wiggle(arbol, scale_value=1.08, rotation_angle=0.03 * TAU), run_time=0.6)

            # «juntemos todo»
            self.en("juntar")
            todo = Group(*[m for m in self.mobjects if m is not self.camera.frame])
            self.sfx("whoosh")
            self.play(todo.animate.scale(0.05).move_to(ORIGIN).set_opacity(0), run_time=0.45,
                      rate_func=rate_functions.ease_in_cubic)
            self.corte()

            # «dos hilos»
            self.en("hilos")
            h1 = persona(AZUL).scale(1.9).move_to([-4.3, -1.2, 0])
            h2 = persona(MAGENTA).scale(1.9).move_to([4.3, -1.2, 0])
            self.play(FadeIn(h1, shift=RIGHT * 0.5), FadeIn(h2, shift=LEFT * 0.5), run_time=0.3)

            # «una variable compartida»
            self.en("variable")
            celda = RoundedRectangle(width=4.6, height=1.3, corner_radius=0.15, color=AMBAR, stroke_width=4)
            celda.set_fill(AMBAR, 0.08).move_to(UP * 0.2)
            valor = Text("contador = 0", color=TXT, weight=BOLD).scale(0.85).move_to(celda)
            self.pop(celda, valor)

            # «le suma uno»
            self.en("suma")
            mas = [Text("+1", color=col, weight=BOLD).scale(1.0).next_to(h, UP, buff=0.3) for h, col in
                   ((h1, AZUL), (h2, MAGENTA))]
            fl = [Arrow(m.get_top() + UP * 0.05, celda.get_left() if i == 0 else celda.get_right(), color=col,
                        stroke_width=4, buff=0.15) for i, (m, col) in enumerate(zip(mas, (AZUL, MAGENTA)))]
            self.pop(*mas)
            self.play(*[GrowArrow(f) for f in fl], run_time=0.3)

            # «¿Cuánto vale al final?»
            self.en("cuanto")
            q = Text("?", color=AMBAR, weight=BOLD).scale(1.8).next_to(celda, UP, buff=0.3)
            self.pop(q)

            # «Dos,»
            self.en("dos")
            v2 = Text("contador = 2", color=VERDE, weight=BOLD).scale(0.85).move_to(celda)
            self.sfx("exito", gain=-14)
            self.play(FadeOut(q), Transform(valor, v2), celda.animate.set_stroke(VERDE), run_time=0.35)

            # «¿O no?»
            self.en("ono")
            self.play(Wiggle(VGroup(celda, valor), scale_value=1.08, rotation_angle=0.02 * TAU), run_time=0.5)
            v1 = Text("contador = 1", color=ROJO, weight=BOLD).scale(0.85).move_to(celda)
            flash = FullScreenRectangle().set_fill(ROJO, 0.3).set_stroke(width=0)
            self.sfx("alarma", gain=-14)
            self.play(Transform(valor, v1), celda.animate.set_stroke(ROJO).set_fill(ROJO, 0.1),
                      FadeIn(flash, rate_func=there_and_back), run_time=0.4)
            self.remove(flash)

            # «próximo episodio»
            self.en("proximo")
            self.corte()
            ep = Text("Episodio 3", color=AMBAR, weight=BOLD).scale(0.8).move_to(UP * 2.2)
            self.sfx("whoosh")
            self.play(FadeIn(ep, shift=DOWN * 0.3), run_time=0.45)

            # «uno más uno es igual a uno»
            self.en("titulo")
            ec = Text("1 + 1 = 1", color=AMBAR, weight=BOLD).scale(2.6).move_to(UP * 0.2)
            self.pop(ec, sonido="impacto")
            tit = Text("Uno más uno igual a uno", color=TXT, weight=BOLD).scale(0.8).next_to(ec, DOWN, buff=0.45)
            self.play(FadeIn(tit, shift=UP * 0.2), run_time=0.4)
            self.flotar(ec)
        self.wait(1.2)
