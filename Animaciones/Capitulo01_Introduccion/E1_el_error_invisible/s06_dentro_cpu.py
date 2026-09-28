"""Escena 06 — Dentro del procesador: ciclo, memoria, registros y contador de programa."""
from comun.escena import *

INSTRUCCIONES = ["cargar x", "sumar 1", "guardar x", "comparar", "saltar"]


def lista_instrucciones(escala: float = 1.0) -> VGroup:
    filas = VGroup()
    for txt in INSTRUCCIONES:
        r = RoundedRectangle(width=3.0, height=0.62, corner_radius=0.08, color=INK, stroke_width=2)
        r.set_fill(FONDO, 1)
        t = Text(txt, color=TXT, font="Consolas").scale(0.5).move_to(r)
        filas.add(VGroup(r, t))
    filas.arrange(DOWN, buff=0.14).scale(escala)
    return filas


def miles(v: float) -> str:
    return f"{int(v):,}".replace(",", ".")


class DentroCPU(EscenaCurso):
    ESCENA = "06"

    def construct(self):
        self.ciclo()
        self.memoria_registros()
        self.contador_programa()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def ciclo(self):
        with self.narrar("b1"):
            self.en("therac")
            maq = maquina_radioterapia().scale(4.6).move_to(DOWN * 0.2)
            self.play(FadeIn(maq, scale=0.8), run_time=0.5)

            self.en("tecnico")
            # la columna de la máquina se "abre" y adentro hay un chip
            tapa = maq[0]
            ch = chip(AZUL).scale(0.9).move_to(tapa)
            self.sfx("whoosh", gain=-16)
            self.play(tapa.animate.set_fill(FONDO, 1).set_stroke(INK, 3), FadeIn(ch, scale=0.5), run_time=0.6)

            self.en("bajar")
            self.sfx("whoosh")
            # el zoom dura hasta la próxima frase: nunca queda la pantalla llena de chip
            self.play(FadeOut(VGroup(*maq[1:])), self.camera.frame.animate.move_to(ch).set(width=3.0),
                      run_time=min(3.5, self.margen() * 0.95), rate_func=rate_functions.ease_in_out_sine)

            self.en("dentro")
            self.corte()
            self.camera.frame.move_to(ORIGIN).set(width=config.frame_width)
            self.c = cpu().scale(1.7).move_to(LEFT * 3.3)
            self.ins = lista_instrucciones().move_to(RIGHT * 3.4)
            self.play(FadeIn(self.c, scale=1.3), LaggedStart(*[FadeIn(i, shift=LEFT * 0.3) for i in self.ins],
                                                              lag_ratio=0.1), run_time=0.7)

            self.en("simple")
            self.sfx("pop")
            self.play(Flash(self.c.caja, color=TXT, flash_radius=2.4, num_lines=16), run_time=0.5)

            self.en("bucle")
            bucle = Arc(radius=1.9, start_angle=PI / 2, angle=-1.7 * PI, color=AMBAR, stroke_width=5)
            bucle.add_tip(tip_length=0.25).move_to(self.c.caja)
            self.play(Create(bucle), run_time=0.6)
            bucle.add_updater(lambda m, dt: m.rotate(-2.5 * dt, about_point=self.c.caja.get_center()))

            self.en("millones")
            ciclos = ValueTracker(0)
            lectura = always_redraw(lambda: Text(miles(ciclos.get_value()), color=AMBAR, weight=BOLD)
                                    .scale(0.8).move_to(self.c.caja.get_center() + UP * 2.4))
            rotulo = Text("ciclos por segundo", color=INK).scale(0.42)
            rotulo.add_updater(lambda m: m.next_to(lectura, UP, buff=0.12))
            self.add(lectura, rotulo)
            bucle.clear_updaters()
            bucle.add_updater(lambda m, dt: m.rotate(-9 * dt, about_point=self.c.caja.get_center()))
            self.play(ciclos.animate.set_value(3_000_000_000), run_time=1.2,
                      rate_func=rate_functions.ease_in_expo)
            lectura.clear_updaters()
            rotulo.clear_updaters()
            bucle.clear_updaters()
            self.play(FadeOut(bucle), run_time=0.2)

            self.en("lee")
            copia = self.ins[0].copy()
            self.sfx("whoosh", gain=-18)
            self.play(copia.animate.scale(0.7).move_to(self.c.caja.get_center() + DOWN * 0.2), run_time=0.45)

            self.en("ejecuta")
            self.sfx("pop")
            self.play(self.c.caja.animate(rate_func=there_and_back).set_fill(AMBAR, 0.6),
                      Flash(self.c.caja, color=AMBAR, flash_radius=1.9), run_time=0.45)

            self.en("siguiente")
            copia2 = self.ins[1].copy()
            self.play(FadeOut(copia, scale=0.5),
                      copia2.animate.scale(0.7).move_to(self.c.caja.get_center() + DOWN * 0.2), run_time=0.45)
            self.play(self.c.caja.animate(rate_func=there_and_back).set_fill(AMBAR, 0.6), run_time=0.4)

    # ------------------------------------------------------------------ b2
    def memoria_registros(self):
        with self.narrar("b2"):
            self.en("casilleros")
            self.corte()
            c = cpu().scale(1.3).move_to(UP * 1.7)
            reg = registro("registro", "").scale(1.2).move_to(c.caja.get_center() + DOWN * 0.25)
            celdas = VGroup()
            for i in range(18):
                r = Square(0.9, color=INK, stroke_width=2).set_fill(FONDO, 1)
                n = Text(str(i), color=INK).scale(0.35).next_to(r, DOWN, buff=0.1)
                n.permitir_fuera = True     # la fila se sale de cuadro a propósito
                celdas.add(VGroup(r, n))
            celdas.arrange(RIGHT, buff=0).move_to(DOWN * 1.9).align_to(LEFT * 6.6, LEFT)
            self.add(c)
            self.play(LaggedStart(*[FadeIn(x, shift=LEFT * 0.4) for x in celdas], lag_ratio=0.05), run_time=1.0)

            self.en("datos")
            rng = np.random.default_rng(2)
            valores = VGroup(*[Text(str(int(v)), color=TXT, weight=BOLD).scale(0.45).move_to(x[0])
                               for v, x in zip(rng.integers(0, 99, len(celdas)), celdas)])
            for v in valores:
                v.permitir_fuera = True
            self.play(LaggedStart(*[FadeIn(v, scale=1.5) for v in valores], lag_ratio=0.04), run_time=0.8)

            self.en("directo")
            directo = Arrow(c.get_bottom(), celdas[4].get_top(), color=ROJO, stroke_width=5, buff=0.15)
            self.play(GrowArrow(directo), run_time=0.35)
            no = cruz(ROJO, 0.6).move_to(directo.get_center())
            self.sfx("error", gain=-16)
            self.play(Create(no), run_time=0.25)

            self.en("registro")
            self.play(FadeOut(directo), FadeOut(no), FadeIn(reg), run_time=0.3)
            viajero = valores[4].copy()
            self.sfx("whoosh", gain=-16)
            self.play(viajero.animate.move_to(reg.caja).set_color(AMBAR),
                      celdas[4][0].animate(rate_func=there_and_back).set_color(AMBAR), run_time=0.7)

            self.en("pequeno")
            self.play(reg.animate(rate_func=there_and_back).scale(0.6), run_time=0.6)

            self.en("rapido")
            self.sfx("pop")
            self.play(Flash(reg.caja, color=AMBAR, flash_radius=1.0, num_lines=12),
                      reg.caja.animate(rate_func=there_and_back).scale(1.15).set_stroke(color=AMBAR), run_time=0.6)

            self.en("propia")
            self.zoom(c, 6.0, run_time=0.8)
            self.resaltar(reg.caja, color=AMBAR, run_time=0.6)
        self.zoom_reset(run_time=0.4)

    # ------------------------------------------------------------------ b3
    def contador_programa(self):
        with self.narrar("b3"):
            self.en("dedo")
            self.corte()
            ins = lista_instrucciones(1.25).move_to(RIGHT * 0.3)
            self.play(LaggedStart(*[FadeIn(i, shift=UP * 0.2) for i in ins], lag_ratio=0.08), run_time=0.5)
            punta = Triangle(color=AMBAR).set_fill(AMBAR, 1).rotate(-PI / 2).scale(0.28)
            punta.next_to(ins[0], LEFT, buff=0.3)
            self.pop(punta)

            self.en("pc")
            rotulo = Text("PC", color=AMBAR, weight=BOLD).scale(0.7).next_to(punta, LEFT, buff=0.2)
            self.play(FadeIn(rotulo, shift=RIGHT * 0.2), run_time=0.3)
            pc = VGroup(punta, rotulo)

            self.en("proxima")
            self.play(ins[0][0].animate.set_color(AMBAR).set_fill(AMBAR, 0.2), run_time=0.3)

            def paso(k):
                self.sfx("tick", gain=-14)
                self.play(pc.animate.shift(ins[k].get_center() - ins[k - 1].get_center()),
                          ins[k - 1][0].animate.set_color(INK).set_fill(FONDO, 1),
                          ins[k][0].animate.set_color(AMBAR).set_fill(AMBAR, 0.2), run_time=0.3)

            self.en("avanza")
            for k in (1, 2):
                paso(k)
                self.wait(0.25)

            self.en("recorre")
            for k in (3, 4):
                paso(k)

            self.en("hilo")
            # el hilo va por la DERECHA de las instrucciones: a la izquierda está el PC
            puntos = [ins[k].get_right() + RIGHT * (0.5 + 0.35 * np.sin(k * 2)) for k in range(5)]
            hilo = VMobject(color=AMBAR, stroke_width=6).set_points_smoothly(puntos)
            self.play(Create(hilo), run_time=0.7)
            termino = self.palabra("hilo de ejecución", AMBAR, 0.9).next_to(ins, DOWN, buff=0.45)
            self.pop(termino)
