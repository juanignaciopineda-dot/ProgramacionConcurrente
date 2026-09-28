"""Escena 03 — El peor orden posible: H1 y H2 leen 0 antes de que nadie escriba."""
from comun.escena import *

FILA = [("H1 lee 0", AZUL), ("H2 lee 0", MAGENTA), ("H1 escribe 1", AZUL), ("H2 escribe 1", MAGENTA)]


class PeorIntercalado(EscenaCurso):
    ESCENA = "03"

    def construct(self):
        m = self.m = montaje_contador("0")
        self.fila = fila_pasos(FILA, ancho=2.6)
        self.play(FadeIn(m), run_time=0.3)
        self.leen()
        self.escriben()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ utilidades
    def valor(self, h, txt: str, color) -> Text:
        return Text(txt, color=color, weight=BOLD).scale(0.75).move_to(h.registro.caja)

    def viaja(self, origen: Mobject, destino: Mobject, color, *extra, run_time: float = 0.55) -> Mobject:
        """Una copia de `origen` viaja hasta `destino` y queda ahí (`extra`: animaciones a la vez)."""
        c = origen.copy()
        self.sfx("whoosh", gain=-16)
        self.play(c.animate.move_to(destino).set_color(color), *extra, run_time=run_time)
        return c

    def anotar(self, i: int) -> None:
        self.sfx("tick", gain=-14)
        self.play(FadeIn(self.fila[i], shift=UP * 0.3), run_time=0.3)

    # ------------------------------------------------------------------ b1
    def leen(self):
        m = self.m
        with self.narrar("b1"):
            # «El hilo uno lee»
            self.en("h1lee")
            self.foco = SurroundingRectangle(m.h1.caja, color=AMBAR, buff=0.15, corner_radius=0.15, stroke_width=6)
            f1 = flecha(m.celda.caja.get_left() + DOWN * 0.2, m.h1.registro.caja.get_top() + UP * 0.85 + RIGHT * 0.5,
                        AZUL, ancho=4)
            self.play(Create(self.foco), GrowArrow(f1), run_time=0.45)

            # «es cero»
            self.en("h1cero")
            self.v1 = self.viaja(m.celda.contenido, m.h1.registro.caja, AZUL)
            self.anotar(0)

            # «justo antes» — todo se congela un instante
            self.en("antes")
            hielo = FullScreenRectangle().set_fill(CIAN, 0.12).set_stroke(width=0)
            self.sfx("tick", gain=-8)
            self.play(FadeIn(hielo), run_time=0.2)
            self.wait(0.5)
            self.play(FadeOut(hielo), run_time=0.3)

            # «el sistema operativo»
            self.en("so")
            so = chip(INK, "SO").scale(1.3).move_to(RIGHT * 5.6 + UP * 2.9)
            self.pop(so)

            # «le da el turno al hilo dos»
            self.en("turno")
            m.h1.save_state()
            self.play(self.foco.animate.move_to(m.h2.caja), m.h1.animate.fade(0.65),
                      self.v1.animate.set_opacity(0.35), f1.animate.set_opacity(0.2), run_time=0.5)

            # «también lee contador»
            self.en("h2lee")
            f2 = flecha(m.celda.caja.get_right() + DOWN * 0.2, m.h2.registro.caja.get_top() + UP * 0.85 + LEFT * 0.5,
                        MAGENTA, ancho=4)
            self.play(GrowArrow(f2), run_time=0.4)

            # «todavía no escribió nada» — la celda sigue en 0
            self.en("nada")
            self.resaltar(m.celda.caja, color=AMBAR, escala=1.08)

            # «lo mismo: cero»
            self.en("h2cero")
            self.v2 = self.viaja(m.celda.contenido, m.h2.registro.caja, MAGENTA)
            self.anotar(1)
            self.play(FadeOut(f1), FadeOut(f2), run_time=0.3)

    # ------------------------------------------------------------------ b2
    def escriben(self):
        m = self.m
        with self.narrar("b2"):
            # «los dos hilos tienen»
            self.en("ambos")
            self.play(Restore(m.h1), self.v1.animate.set_opacity(1), FadeOut(self.foco), run_time=0.3)
            self.resaltar(m.h1.registro.caja, m.h2.registro.caja, color=AMBAR, escala=1.2)

            # «el mismo valor: cero»
            self.en("mismo")
            une = DashedLine(self.v1.get_right() + RIGHT * 0.5, self.v2.get_left() + LEFT * 0.5,
                             color=AMBAR, stroke_width=4, dash_length=0.18)
            self.play(Create(une), run_time=0.5)

            # «lo incrementa a uno»
            self.en("h1suma")
            self.foco.move_to(m.h1.caja)
            uno1 = self.valor(m.h1, "1", AMBAR)
            self.sfx("pop")
            self.play(FadeOut(une), Create(self.foco), ReplacementTransform(self.v1, uno1), run_time=0.4)

            # «y lo escribe»
            self.en("h1escribe")
            viajero = self.viaja(uno1, m.celda.caja, AZUL, FadeOut(m.celda.contenido))

            # «pasa a valer uno»
            self.en("vale1")
            nuevo = Text("1", color=TXT).scale(0.64).move_to(m.celda.caja)
            self.remove(viajero)
            self.play(FadeIn(nuevo), Flash(m.celda.caja, color=AZUL), run_time=0.4)
            self.anotar(2)

            # «sigue teniendo cero»
            self.en("sigue0")
            self.play(self.foco.animate.move_to(m.h2.caja), run_time=0.35)
            self.play(self.v2.animate(rate_func=there_and_back).scale(1.5).set_color(AMBAR), run_time=0.5)
            self.play(self.v2.animate(rate_func=there_and_back).scale(1.5).set_color(AMBAR), run_time=0.5)

            # «lo leyó antes» — «H2 lee 0» está antes de «H1 escribe 1»
            self.en("antes2")
            self.resaltar(self.fila[1], color=AMBAR, escala=1.15)

            # «también lo incrementa»
            self.en("h2suma")
            uno2 = self.valor(m.h2, "1", AMBAR)
            self.sfx("pop")
            self.play(ReplacementTransform(self.v2, uno2), run_time=0.35)

            # «también lo escribe» — pisa el 1
            self.en("h2escribe")
            viajero = self.viaja(uno2, m.celda.caja, MAGENTA, FadeOut(nuevo, scale=1.6), run_time=0.45)
            self.sfx("impacto", gain=-10)
            otro = Text("1", color=TXT).scale(0.64).move_to(m.celda.caja)
            self.remove(viajero)
            self.play(FadeIn(otro, scale=1.6), Flash(m.celda.caja, color=MAGENTA), run_time=0.3)
            self.anotar(3)

            # «se queda en uno»
            self.en("queda1")
            self.sfx("alarma", gain=-12)
            self.play(m.celda.caja.animate.set_stroke(ROJO, 6).set_fill(ROJO, 0.25), otro.animate.set_color(ROJO),
                      FadeOut(self.foco), run_time=0.4)
            self.play(Wiggle(VGroup(m.celda.caja, otro), scale_value=1.15), run_time=0.7)
