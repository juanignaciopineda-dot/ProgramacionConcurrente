"""Escena 02 — La escalera: cuánto se conocen los procesos (competencia,
compartimiento, comunicación)."""
from comun.escena import *


def mirada(p: Mobject, lado) -> VGroup:
    """Flechita que indica hacia dónde mira un proceso."""
    ini = p.get_edge_center(lado) + lado * 0.15
    return Arrow(ini, ini + lado * 1.0, color=INK, stroke_width=7, buff=0, max_tip_length_to_length_ratio=0.3)


class Escalera(EscenaCurso):
    ESCENA = "02"

    def construct(self):
        self.intro()
        self.primero()
        self.segundo()
        self.tercero()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ piezas
    def mini_escalera(self, i: int) -> VGroup:
        """Escalera pequeña arriba a la izquierda, con el escalón `i` encendido."""
        m = escalera(3, ancho=0.55, alto=0.28).to_corner(UL, buff=0.4)
        m.escalones[i].set_fill(AMBAR, 0.6).set_stroke(AMBAR)
        return m

    def encuadre(self, i: int) -> tuple[np.ndarray, float]:
        """Centro y ancho de cámara para mirar el escalón `i` de la escalera grande."""
        return self.esc.escalones[i].get_center() + UP * 0.8, 10.5

    def subir(self, i: int) -> None:
        """Vuelve a la escalera grande (con la cámara en el escalón anterior) y sube al `i`."""
        self.corte(self.esc, self.nums, self.cerc)
        c, w = self.encuadre(i - 1)
        self.camera.frame.move_to(c).set(width=w)
        c, w = self.encuadre(i)
        self.sfx("whoosh", gain=-18)
        self.play(self.camera.frame.animate.move_to(c).set(width=w),
                  self.esc.escalones[i].animate.set_fill(AMBAR, 0.15).set_stroke(AMBAR, 5), run_time=0.7)

    def plano_nuevo(self, i: int) -> None:
        self.corte(self.mini_escalera(i))
        self.camera.frame.move_to(ORIGIN).set(width=config.frame_width)

    # ------------------------------------------------------------------ b1
    def intro(self):
        with self.narrar("b1"):
            # «Hasta ahora» — dos procesos y un recurso
            self.en("hasta")
            p1 = proceso_mini("P1", AZUL).scale(2.0).move_to(LEFT * 4.3 + DOWN * 0.3)
            p2 = proceso_mini("P2", MAGENTA).scale(2.0).move_to(RIGHT * 4.3 + DOWN * 0.3)
            rec = candado(AMBAR).scale(1.7).move_to(DOWN * 0.3)
            self.play(FadeIn(p1, shift=RIGHT * 0.4), FadeIn(p2, shift=LEFT * 0.4), FadeIn(rec, scale=0.6), run_time=0.4)

            # «peleándose por un recurso»
            self.en("pelea")
            self.play(p1.animate.shift(RIGHT * 1.0), p2.animate.shift(LEFT * 1.0), run_time=0.3,
                      rate_func=rate_functions.ease_in_quad)
            self.sfx("impacto", gain=-12)
            self.play(Wiggle(rec, scale_value=1.15), p1.animate.shift(LEFT * 0.3), p2.animate.shift(RIGHT * 0.3),
                      run_time=0.5)

            # «conocerse» — cada uno mira hacia fuera, con un «?»
            self.en("desconocidos")
            m1, m2 = mirada(p1, LEFT), mirada(p2, RIGHT)
            q1 = Text("?", color=INK, weight=BOLD).scale(1.6).next_to(p1, UP, buff=0.3)
            q2 = Text("?", color=INK, weight=BOLD).scale(1.6).next_to(p2, UP, buff=0.3)
            self.play(GrowArrow(m1), GrowArrow(m2), run_time=0.3)
            self.pop(q1, q2)

            # «la relación entre procesos» — una línea punteada los une
            self.en("relacion")
            lazo = DashedLine(p1.get_bottom() + DOWN * 0.3, p2.get_bottom() + DOWN * 0.3, color=INK, stroke_width=4)
            self.play(Create(lazo), run_time=0.5)

            # «mucho más cercana» — se acercan
            self.en("cercana")
            corto = Line(LEFT * 2.0 + DOWN * 1.8, RIGHT * 2.0 + DOWN * 1.8, color=AMBAR, stroke_width=8)
            self.play(FadeOut(rec, scale=0.5), FadeOut(m1), FadeOut(m2), FadeOut(q1), FadeOut(q2),
                      p1.animate.move_to(LEFT * 2.0 + DOWN * 0.3), p2.animate.move_to(RIGHT * 2.0 + DOWN * 0.3),
                      ReplacementTransform(lazo, corto), run_time=0.6)

            # «una escalera» — tres escalones que suben
            self.en("escalera")
            self.corte()
            self.esc = escalera(3, ancho=3.2, alto=1.3).move_to(DOWN * 1.1 + LEFT * 0.5)
            self.play(LaggedStart(*[FadeIn(e, shift=UP * 0.4) for e in self.esc.escalones], lag_ratio=0.25),
                      run_time=0.6)

            # «tres escalones» — numerados
            self.en("tres")
            self.nums = VGroup(*[Text(str(i + 1), color=TXT, weight=BOLD).scale(1.1)
                                 .move_to(self.esc.escalones[i].get_bottom() + UP * 0.65) for i in range(3)])
            for i in range(3):
                self.sfx("tick", gain=-12, offset=i * 0.15)
            self.play(LaggedStart(*[FadeIn(n, scale=1.8) for n in self.nums], lag_ratio=0.3), run_time=0.6)

            # «conocen entre sí» — flecha de cercanía
            self.en("cuanto")
            x = self.esc.get_right()[0] + 0.8
            fl = Arrow([x, self.esc.get_bottom()[1], 0], [x, self.esc.get_top()[1] + 0.6, 0], color=AMBAR,
                       stroke_width=8, buff=0, max_tip_length_to_length_ratio=0.1)
            et = Text("cercanía", color=AMBAR, weight=BOLD).scale(0.6).next_to(fl, UP, buff=0.15)
            self.cerc = VGroup(fl, et)
            self.play(GrowArrow(fl), FadeIn(et, shift=UP * 0.2), run_time=0.6)

    # ------------------------------------------------------------------ b2
    def primero(self):
        with self.narrar("b2"):
            # «El primer escalón» — se enciende y la cámara se acerca
            self.en("e1")
            c, w = self.encuadre(0)
            self.play(self.esc.escalones[0].animate.set_fill(AMBAR, 0.15).set_stroke(AMBAR, 5),
                      self.camera.frame.animate.move_to(c).set(width=w), run_time=0.7)

            # «no conocerse en absoluto» — dos procesos de espaldas
            self.en("absoluto")
            self.plano_nuevo(0)
            p1 = proceso_mini("P1", AZUL).scale(2.0).move_to(LEFT * 3.9)
            p2 = proceso_mini("P2", MAGENTA).scale(2.0).move_to(RIGHT * 3.9)
            m1, m2 = mirada(p1, LEFT), mirada(p2, RIGHT)
            self.play(FadeIn(p1), FadeIn(p2), GrowArrow(m1), GrowArrow(m2), run_time=0.45)

            # «ni siquiera saben»
            self.en("saben")
            q1 = Text("?", color=INK, weight=BOLD).scale(1.6).next_to(p1, UP, buff=0.3)
            q2 = Text("?", color=INK, weight=BOLD).scale(1.6).next_to(p2, UP, buff=0.3)
            self.pop(q1, q2)

            # «entran en conflicto» — los dos agarran el mismo recurso: rayo
            self.en("conflicto")
            rec = candado(AMBAR).scale(1.6)
            self.play(FadeIn(rec, scale=0.5), p1.animate.shift(RIGHT * 1.6), p2.animate.shift(LEFT * 1.6),
                      m1.animate.shift(RIGHT * 1.6), m2.animate.shift(LEFT * 1.6), run_time=0.35)
            rayo = VMobject(color=ROJO, stroke_width=7).set_points_as_corners(
                [[-0.4, 1.9, 0], [0.15, 1.3, 0], [-0.15, 1.2, 0], [0.4, 0.6, 0]])
            self.sfx("impacto", gain=-10)
            self.play(Create(rayo), run_time=0.2)

            # «porque quieren el mismo» — el recurso se pone rojo y tiembla
            self.en("mismo")
            self.play(rec.animate.set_color(ROJO), Wiggle(rec, scale_value=1.15), run_time=0.5)

            # «competencia»
            self.en("competencia")
            pal = self.palabra("competencia", AMBAR, 1.5).move_to(UP * 2.9)
            self.pop(pal, sonido="impacto")

            # «exclusión mutua» — miniatura de la sala con puerta
            self.en("exclusion")
            sala = SalaPuerta(2.6, 1.5).move_to(DOWN * 2.75 + LEFT * 1.4)
            pm = proceso_mini("P1", AZUL).scale(0.55).move_to(sala.dentro)
            et = Text("exclusión mutua", color=TXT, weight=BOLD).scale(0.75).next_to(sala, RIGHT, buff=0.4)
            self.play(FadeIn(sala), FadeIn(pm), FadeIn(et, shift=LEFT * 0.2), run_time=0.4)

    # ------------------------------------------------------------------ b3
    def segundo(self):
        with self.narrar("b3"):
            # «El segundo escalón»
            self.en("e2")
            self.subir(1)

            # «conocimiento indirecto»
            self.en("indirecto")
            self.plano_nuevo(1)
            rot = Text("conocimiento indirecto", color=TXT, weight=BOLD).scale(1.1).move_to(UP * 3.1)
            self.pop(rot)

            # «comparten un objeto» — una caja entre los dos, que no se miran
            self.en("objeto")
            p1 = proceso_mini("P1", AZUL).scale(1.9).move_to(LEFT * 4.8 + DOWN * 0.4)
            p2 = proceso_mini("P2", MAGENTA).scale(1.9).move_to(RIGHT * 4.8 + DOWN * 0.4)
            obj = RoundedRectangle(width=2.0, height=2.0, corner_radius=0.15, color=INK, stroke_width=4)
            obj.set_fill(INK, 0.1).move_to(DOWN * 0.4)
            f1 = Arrow(p1.get_right(), obj.get_left(), color=AZUL, stroke_width=7, buff=0.2)
            f2 = Arrow(p2.get_left(), obj.get_right(), color=MAGENTA, stroke_width=7, buff=0.2)
            self.play(FadeIn(p1), FadeIn(p2), FadeIn(obj, scale=0.6), run_time=0.35)
            self.play(GrowArrow(f1), GrowArrow(f2), run_time=0.35)

            # «una variable» — la caja es una celda «x»
            self.en("variable")
            var = caja("x = 5", CIAN, ancho=2.6, alto=1.5, scale=1.0, relleno=0.2).move_to(obj)
            self.play(FadeOut(obj), FadeIn(var, scale=0.7), run_time=0.35)

            # «un archivo» — se vuelve un documento
            self.en("archivo")
            doc = documento(TXT).scale(2.2).move_to(var)
            self.play(FadeOut(var, scale=0.7), FadeIn(doc, scale=0.7), run_time=0.4)

            # «cooperación por compartimiento»
            self.en("compartimiento")
            pal = Text("cooperación por compartimiento", color=AMBAR, weight=BOLD).scale(0.95).move_to(UP * 2.1)
            self.pop(pal)

            # «dolores de cabeza» — vuelve la sala con puerta, y tiembla
            self.en("dolores")
            sala = SalaPuerta(2.6, 1.4).move_to(LEFT * 2.4 + DOWN * 2.95)
            pm = proceso_mini("P1", AZUL).scale(0.5).move_to(sala.dentro)
            self.play(FadeIn(sala), FadeIn(pm), run_time=0.3)
            self.play(Wiggle(VGroup(sala, pm), scale_value=1.1), run_time=0.5)

            # «uno nuevo» — un «+» y un hueco
            self.en("nuevo")
            mas = Text("+", color=TXT, weight=BOLD).scale(1.4).move_to(LEFT * 0.2 + DOWN * 2.95)
            hueco = DashedVMobject(RoundedRectangle(width=3.2, height=1.4, corner_radius=0.15), num_dashes=30)
            hueco.set_stroke(INK, 3).move_to(RIGHT * 2.2 + DOWN * 2.95)
            self.play(FadeIn(mas, scale=1.6), Create(hueco), run_time=0.45)

            # «datos compartidos» — dos valores distintos que se pisan
            self.en("datos")
            dist = Text("5 ≠ 7", color=ROJO, weight=BOLD).scale(1.0).move_to(hueco)
            self.sfx("error", gain=-16)
            self.play(FadeIn(dist, scale=1.4), run_time=0.3)
            self.play(Wiggle(dist), run_time=0.45)

            # «coherentes» — se igualan
            self.en("coherentes")
            ig = Text("7 = 7", color=VERDE, weight=BOLD).scale(1.0).move_to(hueco).shift(LEFT * 0.35)
            ok = tilde(VERDE, 0.45).next_to(ig, RIGHT, buff=0.2)
            self.play(FadeOut(dist), FadeIn(ig), Create(ok), run_time=0.35)
            self.sfx("exito", gain=-16)

    # ------------------------------------------------------------------ b4
    def tercero(self):
        with self.narrar("b4"):
            # «el tercer escalón»
            self.en("e3")
            self.subir(2)

            # «conocimiento directo»
            self.en("directo")
            self.plano_nuevo(2)
            rot = Text("conocimiento directo", color=TXT, weight=BOLD).scale(1.1).move_to(UP * 3.1)
            self.pop(rot)

            # «se hablan explícitamente» — se miran; burbuja de diálogo
            self.en("hablan")
            p1 = proceso_mini("P1", AZUL).scale(1.9).move_to(LEFT * 4.3 + DOWN * 0.7)
            p2 = proceso_mini("P2", MAGENTA).scale(1.9).move_to(RIGHT * 4.3 + DOWN * 0.7)
            m1, m2 = mirada(p1, RIGHT), mirada(p2, LEFT)
            burb = burbuja_dialogo(3.0, 1.3).move_to(LEFT * 2.0 + UP * 1.4)
            self.play(FadeIn(p1), FadeIn(p2), GrowArrow(m1), GrowArrow(m2), run_time=0.4)
            self.play(FadeIn(burb, scale=0.6), run_time=0.3)

            # «identificador» — la burbuja se dirige a P2
            self.en("id")
            dest = Text("→ P2", color=MAGENTA, weight=BOLD).scale(1.0).move_to(burb.globo)
            self.pop(dest)
            self.resaltar(p2.caja, color=MAGENTA, escala=1.12, run_time=0.4)

            # «cooperación por comunicación»
            self.en("comunicacion")
            pal = Text("cooperación por comunicación", color=TXT, weight=BOLD).scale(0.95).move_to(DOWN * 2.9)
            self.pop(pal)

            # «el mecanismo» — un engranaje entre los dos
            self.en("mecanismo")
            eng = engranaje(TXT).scale(1.3).move_to(DOWN * 0.7)
            self.play(FadeIn(eng, scale=0.5), run_time=0.25)
            self.play(Rotate(eng, angle=-PI / 2), run_time=0.7)

            # «pasaje de mensajes» — un sobre viaja de P1 a P2
            self.en("mensajes")
            sb = sobre(TXT).scale(0.8).move_to(p1.get_top() + UP * 0.5)
            self.play(FadeOut(eng, scale=0.5), FadeIn(sb, scale=0.5), run_time=0.2)
            self.sfx("whoosh")
            self.play(sb.animate.move_to(p2.get_top() + UP * 0.5), run_time=0.6,
                      rate_func=rate_functions.ease_in_out_sine)
            nueva = Text("pasaje de mensajes", color=AMBAR, weight=BOLD).scale(1.2).move_to(pal)
            self.play(FadeOut(pal, shift=UP * 0.3), run_time=0.15)
            self.play(FadeIn(nueva, shift=UP * 0.3), run_time=0.3)
