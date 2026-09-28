"""Escena 06 — Sistemas transformacionales y reactivos: por qué hay que modelar."""
from comun.escena import *

IZQ, DER = LEFT * 3.55, RIGHT * 3.55


class SistemasReactivos(EscenaCurso):
    ESCENA = "06"

    def construct(self):
        self.paso_atras()
        self.transformacional()
        self.reactivo()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def paso_atras(self):
        cartas = VGroup(*[tarjeta(t, INK, ancho=2.8, alto=3.2, escala_texto=0.75)
                          for t in ("concurrencia", "autómatas", "lenguajes", "Petri")])
        cartas.arrange(RIGHT, buff=0.35).move_to(DOWN * 0.2)
        self.add(cartas)
        self.camera.frame.move_to(cartas[0]).set(width=4.0)
        with self.narrar("b1"):
            # «un paso atrás» — la cámara se aleja
            self.en("atras")
            self.sfx("whoosh", gain=-16)
            self.play(self.camera.frame.animate.move_to(ORIGIN).set(width=config.frame_width), run_time=0.9)

            # «toda la materia» — las cartas se encienden en cadena
            self.en("materia")
            for i in range(4):
                self.sfx("tick", gain=-16, offset=i * 0.18)
            self.play(LaggedStart(*[c.marco.animate.set_stroke(AMBAR) for c in cartas], lag_ratio=0.3), run_time=0.8)

            # «modelar»
            self.en("modelar")
            pal = self.palabra("modelar", AMBAR, 1.6).move_to(UP * 2.95)
            self.pop(pal, sonido="impacto")

            # «programar y cruzar los» — programar y probar, con tildes que tiemblan
            self.en("cruzar")
            pp = Text("programar y probar", color=INK, weight=BOLD).scale(0.9)
            ts = VGroup(*[tilde(VERDE, 0.4) for _ in range(3)]).arrange(RIGHT, buff=0.2)
            fila = VGroup(pp, ts).arrange(RIGHT, buff=0.5).move_to(DOWN * 2.95)
            self.play(FadeIn(fila, shift=UP * 0.2), run_time=0.3)
            self.play(Wiggle(ts, scale_value=1.2), run_time=0.6)

            # «el testing» — se tacha
            self.en("testing")
            self.sfx("error", gain=-14)
            self.play(Create(tachar(fila)), fila.animate.fade(0.4), run_time=0.35)

    # ------------------------------------------------------------------ b2
    def columnas(self) -> None:
        self.cols = VGroup(*[DashedVMobject(RoundedRectangle(width=6.6, height=6.9, corner_radius=0.3),
                                            num_dashes=60).set_stroke(INK, 3).move_to(c + DOWN * 0.2)
                             for c in (IZQ, DER)])
        self.qs = VGroup(*[Text("?", color=INK, weight=BOLD).scale(2.2).move_to(c) for c in (IZQ, DER)])

    def transformacional(self):
        with self.narrar("b2"):
            # «qué tipo de sistema» — dos columnas vacías
            self.en("tipo")
            self.corte()
            self.columnas()
            self.play(Create(self.cols), run_time=0.45)
            self.pop(*self.qs)

            # «sistema transformacional»
            self.en("transf")
            t = Text("transformacional", color=TXT, weight=BOLD).scale(0.85).move_to(IZQ + UP * 2.8)
            self.play(FadeOut(self.qs[0], scale=0.5), FadeIn(t, shift=DOWN * 0.2), run_time=0.35)

            # «recibe una entrada» — una flecha entra en una caja
            self.en("entrada")
            caja_t = RoundedRectangle(width=1.8, height=1.8, corner_radius=0.2, color=AZUL, stroke_width=4)
            caja_t.set_fill(AZUL, 0.12).move_to(IZQ + UP * 0.5)
            ent = Arrow(IZQ + LEFT * 3.0 + UP * 0.5, caja_t.get_left(), color=TXT, stroke_width=7, buff=0.1)
            self.play(FadeIn(caja_t, scale=0.7), GrowArrow(ent), run_time=0.45)

            # «calcula una salida» — el engranaje gira y sale una flecha
            self.en("salida")
            eng = engranaje(TXT).scale(1.1).move_to(caja_t)
            sal = Arrow(caja_t.get_right(), IZQ + RIGHT * 2.95 + UP * 0.5, color=TXT, stroke_width=7, buff=0.1)
            self.play(FadeIn(eng), run_time=0.15)
            self.play(Rotate(eng, angle=-PI), run_time=0.7)
            self.play(GrowArrow(sal), run_time=0.3)

            # «termina ahí» — la caja se apaga; bandera de fin
            self.en("termina")
            band = bandera(TXT).scale(0.8).move_to(IZQ + RIGHT * 2.5 + UP * 1.4)
            self.play(VGroup(caja_t, eng).animate.fade(0.6), run_time=0.25)
            self.pop(band, sonido="tick")

            # «un compilador»
            self.en("compilador")
            comp = ventana_app("compilador", AZUL).scale(1.25).move_to(IZQ + LEFT * 1.35 + DOWN * 1.85)
            self.play(FadeIn(comp, shift=UP * 0.3), run_time=0.3)

            # «un cálculo científico»
            self.en("cientifico")
            sig = Text("∑", color=AMBAR, weight=BOLD).scale(2.2).move_to(IZQ + RIGHT * 1.6 + DOWN * 1.85)
            self.pop(sig)

            # «La corrección»
            self.en("correccion")
            corr = Text("corrección", color=TXT, weight=BOLD).scale(0.8).move_to(IZQ + DOWN * 3.2)
            self.play(FadeIn(corr, shift=UP * 0.2), run_time=0.3)

            # «una relación clara» — una flecha une entrada y salida
            self.en("clara")
            rel = Arrow(ent.get_start() + UP * 1.5, sal.get_end() + UP * 1.5, color=INK, stroke_width=6, buff=0)
            self.play(GrowArrow(rel), run_time=0.5)

            # «entrada y salida» — la flecha se pone verde
            self.en("relacion")
            ok = tilde(VERDE, 0.5).next_to(corr, RIGHT, buff=0.3)
            self.play(rel.animate.set_color(VERDE), Create(ok), run_time=0.35)
            self.sfx("exito", gain=-16)
            self.izq = VGroup(t, caja_t, eng, ent, sal, band, comp, sig, corr, rel, ok)

    # ------------------------------------------------------------------ b3
    def reactivo(self):
        with self.narrar("b3"):
            # «Un sistema reactivo»
            self.en("reactivo")
            t = Text("reactivo", color=TXT, weight=BOLD).scale(0.85).move_to(DER + UP * 2.8)
            self.play(FadeOut(self.qs[1], scale=0.5), FadeIn(t, shift=DOWN * 0.2), run_time=0.35)

            # «interactúa» — una caja con flechas que entran y salen sin parar
            self.en("interactua")
            c = DER + UP * 0.8
            caja_r = RoundedRectangle(width=1.6, height=1.6, corner_radius=0.2, color=MAGENTA, stroke_width=4)
            caja_r.set_fill(MAGENTA, 0.12).move_to(c)
            eng = engranaje(TXT).scale(1.0).move_to(c)
            fls = VGroup(
                Arrow(c + LEFT * 2.5, c + LEFT * 0.85, color=TXT, stroke_width=6, buff=0),
                Arrow(c + RIGHT * 0.85, c + RIGHT * 2.5, color=TXT, stroke_width=6, buff=0),
                Arrow(c + LEFT * 2.0 + DOWN * 1.7, c + LEFT * 0.6 + DOWN * 0.85, color=TXT, stroke_width=6, buff=0),
                Arrow(c + RIGHT * 0.6 + DOWN * 0.85, c + RIGHT * 2.0 + DOWN * 1.7, color=TXT, stroke_width=6, buff=0))
            self.play(FadeIn(caja_r, scale=0.7), FadeIn(eng), run_time=0.3)
            self.play(LaggedStart(*[GrowArrow(f) for f in fls], lag_ratio=0.25), run_time=0.7)
            eng.add_updater(lambda m, dt: m.rotate(-dt * 1.4))

            # «su entorno» — alrededor, personas y procesos
            self.en("entorno")
            ent = VGroup(persona(AZUL).scale(0.8).move_to(c + LEFT * 2.6 + UP * 0.9),
                         proceso_mini("P1", CIAN).scale(0.75).move_to(c + RIGHT * 2.5 + UP * 0.9),
                         persona(MAGENTA).scale(0.8).move_to(c + LEFT * 2.35 + DOWN * 2.3),
                         proceso_mini("P2", AZUL).scale(0.75).move_to(c + RIGHT * 2.3 + DOWN * 2.3))
            self.play(LaggedStart(*[FadeIn(e, scale=0.5) for e in ent], lag_ratio=0.2), run_time=0.6)

            # «resultado final» — una bandera de fin, tachada
            self.en("final")
            band = bandera(TXT).scale(0.6).move_to(c + UP * 1.3 + RIGHT * 1.6)
            self.pop(band, sonido=None)
            self.sfx("error", gain=-16)
            self.play(Create(tachar(band)), run_time=0.3)

            # «se espera que termine» — un ∞ que gira
            self.en("termine")
            inf = Text("∞", color=AMBAR, weight=BOLD).scale(1.8).move_to(c + DOWN * 1.75)
            self.pop(inf)
            self.play(Rotate(inf, angle=2 * PI), run_time=0.8)

            # «un cajero automático» — un chip «SO» y un cajero
            self.en("cajero")
            so = chip(AZUL, "SO").scale(1.0).move_to(DER + LEFT * 1.3 + DOWN * 3.0)
            caj = cajero().scale(1.1).move_to(DER + RIGHT * 1.3 + DOWN * 3.0)
            self.pop(so, caj)

            # «La programación concurrente»
            self.en("concurrente")
            pc = Text("programación concurrente", color=AMBAR, weight=BOLD).scale(0.6).move_to(UP * 3.62)
            self.play(FadeIn(pc, shift=DOWN * 0.2), run_time=0.3)

            # «del lado reactivo» — la columna reactiva se resalta; la otra se atenúa
            self.en("lado")
            self.play(self.cols[1].animate.set_stroke(AMBAR, 5), self.izq.animate.fade(0.65),
                      self.cols[0].animate.set_stroke(opacity=0.3), run_time=0.45)

            # «la pregunta de fondo» — «?» grande
            self.en("pregunta")
            eng.clear_updaters()
            self.corte()
            q = Text("?", color=AMBAR, weight=BOLD).scale(4.0)
            self.pop(q, sonido="impacto")

            # «mirar un resultado final» — una lupa sobre «resultado», que se tacha
            self.en("mirar")
            res = caja("resultado", INK, ancho=5.2, alto=2.0, scale=1.3, relleno=0.1).move_to(UP * 0.5)
            lp = lupa(TXT).scale(2.2)
            lp.permitir_encima = True
            lp.move_to(res.get_corner(UL) + UL * 0.3)
            self.play(q.animate.scale(0.35).move_to(UP * 2.9), FadeIn(res), FadeIn(lp), run_time=0.4)
            self.play(lp.animate.move_to(res.get_center() + DR * 0.25), run_time=0.45)
            self.sfx("error", gain=-14)
            self.play(Create(tachar(res)), run_time=0.3)

            # «infinitas de interacciones» — una cinta de eventos que avanza sin fin
            self.en("infinitas")
            cols = [AZUL, MAGENTA, CIAN, AMBAR]
            cinta = VGroup(*[RoundedRectangle(width=0.45, height=0.45, corner_radius=0.08, stroke_width=0)
                             .set_fill(cols[i % 4], 0.7) for i in range(46)]).arrange(RIGHT, buff=0.15)
            cinta.move_to(DOWN * 2.3).align_to(LEFT * 7.0, LEFT)
            inf = Text("∞", color=AMBAR, weight=BOLD).scale(1.3).next_to(DOWN * 2.3 + RIGHT * 6.2, UP, buff=0.3)
            self.add(cinta)
            self.play(cinta.animate.shift(LEFT * 7.0), FadeIn(inf), run_time=self.margen(),
                      rate_func=rate_functions.linear)
