"""Escena 09 — Carl Adam Petri: del niño que dibujaba procesos químicos a la tesis de 1962."""
from comun.escena import *


class CarlAdamPetri(EscenaCurso):
    ESCENA = "09"

    def construct(self):
        with self.narrar("b1"):
            # «Conozcamos al autor» — una silueta con «?»
            self.en("autor")
            per = persona(INK).scale(2.4).move_to(LEFT * 4.2 + UP * 0.2)
            q = Text("?", color=AMBAR, weight=BOLD).scale(1.4).next_to(per, UP, buff=0.3)
            self.play(FadeIn(per, shift=UP * 0.3), run_time=0.35)
            self.pop(q)

            # «la herramienta» — la red de exclusión mutua, pequeña
            self.en("herramienta")
            red = RedMutex().scale(0.5).move_to(RIGHT * 3.4 + UP * 0.6)
            self.play(FadeIn(red, scale=0.8), run_time=0.4)

            # «acompañar» — una fila de capítulos se enciende debajo
            self.en("acompanar")
            caps = VGroup(*[tarjeta(str(i + 1), INK, ancho=0.9, alto=1.2, escala_texto=0.5) for i in range(4)])
            caps.arrange(RIGHT, buff=0.25).next_to(red, DOWN, buff=0.35)
            self.play(FadeIn(caps, shift=UP * 0.2), run_time=0.3)
            self.play(LaggedStart(*[c.marco.animate.set_stroke(AMBAR) for c in caps], lag_ratio=0.3), run_time=0.7)

            # «Carl Adam Petri»
            self.en("nombre")
            nombre = Text("Carl Adam Petri", color=AMBAR, weight=BOLD).scale(0.95).move_to(LEFT * 3.9 + UP * 3.1)
            self.play(FadeOut(q, scale=0.5), Write(nombre), run_time=0.5)

            # «un niño alemán» — la silueta se achica
            self.en("nino")
            self.play(per.animate.scale(0.6, about_edge=DOWN), run_time=0.4)

            # «a los trece años»
            self.en("trece")
            trece = Text("13 años", color=TXT, weight=BOLD).scale(0.8).next_to(per, DOWN, buff=0.3)
            self.pop(trece)

            # «diagramas y reglas» — burbuja de pensamiento con un diagrama
            self.en("diagramas")
            diag = maquina_estados_mini(TXT, lado=2.0, radio=0.3).scale_to_fit_height(0.55)
            burb = burbuja_pensamiento(diag).scale(2.0)
            burb.next_to(per, UR, buff=0.05).shift(DOWN * 0.35)
            self.play(FadeIn(burb, scale=0.4), run_time=0.45)

            # «procesos químicos» — un matraz
            self.en("quimicos")
            mz = matraz(CIAN).scale(0.9).next_to(burb, DOWN, buff=0.15).shift(RIGHT * 0.3)
            self.pop(mz)

            # «Décadas después» — la línea de tiempo
            self.en("decadas")
            lt = LineaTiempo(1920, 2030, ancho=12.4).move_to(DOWN * 3.2)
            self.play(Create(lt), run_time=0.5)

            # «en 1962» — la marca; el niño vuelve a ser adulto
            self.en("anio")
            m62 = lt.marca(1962, AMBAR)
            self.play(per.animate.scale(1 / 0.6, about_edge=DOWN), FadeOut(trece), run_time=0.4)
            self.pop(m62)

            # «ideas de la infancia» — la burbuja viaja hasta 1962
            self.en("infancia")
            idea = VGroup(burb, mz)
            destino = lt.punto(1962) + UP * 1.0
            self.play(idea.animate.scale(0.45).move_to(destino), run_time=0.8)

            # «tesis doctoral» — la burbuja se vuelve un documento
            self.en("tesis")
            doc = documento(TXT).scale(1.1).move_to(destino)
            et = Text("tesis", color=TXT, weight=BOLD).scale(0.55).next_to(doc, RIGHT, buff=0.2)
            self.play(ReplacementTransform(idea, doc), FadeIn(et), run_time=0.45)

            # «nacieron formalmente» — del documento sale la red
            self.en("nacen")
            fl = Arrow(doc.get_top(), red.get_corner(DL) + LEFT * 0.1, color=AMBAR, stroke_width=6, buff=0.15)
            self.play(GrowArrow(fl), run_time=0.45)
            self.resaltar(red.lugares, color=AMBAR, escala=1.08, run_time=0.5)

            # «Hoy se le» — la línea avanza hasta hoy
            self.en("hoy")
            p_hoy = Dot(lt.punto(2025), radius=0.1, color=VERDE)
            t_hoy = Text("hoy", color=VERDE, weight=BOLD).scale(0.5).next_to(p_hoy, DOWN, buff=0.18)
            self.pop(p_hoy, t_hoy, sonido="tick")

            # «junto con Dijkstra» — su nombre al lado del de Petri
            self.en("dijkstra")
            dij = Text("Edsger Dijkstra", color=TXT, weight=BOLD).scale(0.95).move_to(RIGHT * 3.4 + UP * 3.35)
            self.play(nombre.animate.move_to(LEFT * 3.4 + UP * 3.35), FadeIn(dij, shift=LEFT * 0.3), run_time=0.45)

            # «uno de los fundadores» — los dos sobre una base: «concurrencia»
            self.en("fundadores")
            base = Line(LEFT * 6.3 + UP * 2.85, RIGHT * 6.3 + UP * 2.85, color=AMBAR, stroke_width=6)
            conc = Text("concurrencia", color=AMBAR, weight=BOLD).scale(0.6).next_to(base, DOWN, buff=0.15)
            self.play(Create(base), run_time=0.35)
            self.pop(conc)

        self.wait(1.0)
        self.limpiar()
