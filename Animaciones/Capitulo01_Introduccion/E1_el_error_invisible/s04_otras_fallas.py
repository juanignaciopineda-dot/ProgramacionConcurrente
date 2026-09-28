"""Escena 04 — Ariane 5, Patriot y Pentium sobre la línea de tiempo."""
from comun.escena import *


def num_es(v: float, dec: int) -> str:
    """Número con coma decimal (formato español)."""
    return f"{v:.{dec}f}".replace(".", ",")


class OtrasFallas(EscenaCurso):
    ESCENA = "04"

    def construct(self):
        self.lt = LineaTiempo().move_to(DOWN * 3.3)
        self.ariane()
        self.patriot()
        self.pentium()
        self.wait(1.0)
        self.limpiar()

    def viñeta_fuera(self, *mobs):
        self.play(*[FadeOut(m, shift=UP * 0.5) for m in mobs], run_time=0.35)

    def viaje(self, desde: int, hasta: int, color, fuera=()):
        """Año grande que corre de `desde` a `hasta` mientras un punto se desliza
        por la línea; al llegar queda la marca. `fuera`: viñeta anterior a sacar."""
        anio = ValueTracker(desde)
        num = always_redraw(lambda: Text(str(int(round(anio.get_value()))), color=color,
                                         weight=BOLD).scale(2.2).move_to(UP * 0.6))
        dot = always_redraw(lambda: Dot(self.lt.punto(anio.get_value()), radius=0.13, color=color))
        self.sfx("whoosh")
        self.play(*[FadeOut(m, shift=UP * 0.5) for m in fuera], FadeIn(num), FadeIn(dot), run_time=0.3)
        self.play(anio.animate.set_value(hasta), run_time=0.9, rate_func=rate_functions.ease_in_out_cubic)
        marca = self.lt.marca(hasta, color)
        num.clear_updaters()
        dot.clear_updaters()
        self.sfx("pop")
        self.play(FadeIn(marca, scale=1.5), FadeOut(num, scale=0.4, target_position=marca), run_time=0.45)
        self.remove(dot)
        return marca

    # ------------------------------------------------------------------ b1
    def ariane(self):
        with self.narrar("b1"):
            self.en("solo")
            m85 = self.lt.marca(1985, ROJO)
            mini = maquina_radioterapia().scale(3.2).move_to(UP * 0.5)
            self.play(FadeIn(mini, scale=0.8), Create(self.lt), FadeIn(m85), run_time=0.5)
            self.play(mini.animate.scale(0.28).next_to(m85, UP, buff=0.25), run_time=0.7)

            self.en("m1996")
            self.viaje(1985, 1996, VERDE)

            self.en("cohete")
            coh = cohete().scale(2.6).move_to(DOWN * 6 + RIGHT * 1)
            self.add(coh)
            self.sfx("whoosh")
            self.play(coh.animate.move_to(UP * 0.8 + RIGHT * 1), run_time=0.9,
                      rate_func=rate_functions.ease_out_cubic)
            self.play(coh.llama.animate(rate_func=there_and_back).stretch(1.4, 1), run_time=0.4)

            self.en("explota")
            centro = coh.get_center()
            rng = np.random.default_rng(1)
            self.sfx("impacto", gain=-6)
            self.play(
                *[p.animate.shift(np.array([*rng.uniform(-3, 3, 2), 0]))
                   .rotate(rng.uniform(-3, 3)).set_opacity(0) for p in coh],
                Flash(centro, color=AMBAR, flash_radius=1.6, num_lines=18, line_length=0.6),
                run_time=0.8)
            self.remove(*coh)

            self.en("segundos")
            t40 = Text("T + 40 s", color=AMBAR, weight=BOLD).scale(1.6).move_to(centro)
            self.pop(t40, sonido="tick")
            self.flotar(t40)

            self.en("casilla")
            t40.clear_updaters()
            self.play(FadeOut(t40, shift=UP * 0.5), run_time=0.3)
            grande = VGroup(*[Square(0.3, color=AZUL, stroke_width=2).set_fill(AZUL, 0.25)
                              for _ in range(32)]).arrange(RIGHT, buff=0.02)
            chica = VGroup(*[Square(0.3, color=TXT, stroke_width=2.5) for _ in range(8)]).arrange(RIGHT, buff=0.02)
            grande.move_to(UP * 1.6)
            chica.move_to(DOWN * 0.4 + LEFT * 4.8)
            et_g = Text("64 bits", color=AZUL, weight=BOLD).scale(0.55).next_to(grande, UP, buff=0.2)
            et_c = Text("16 bits", color=TXT, weight=BOLD).scale(0.55).next_to(chica, DOWN, buff=0.2)
            self.play(FadeIn(grande), FadeIn(et_g), FadeIn(chica), FadeIn(et_c), run_time=0.4)
            self.play(grande.animate.align_to(chica, LEFT).align_to(chica, UP), FadeOut(et_g), run_time=0.6)
            sobra = grande[8:]
            self.sfx("error", gain=-10)
            self.play(sobra.animate.set_color(ROJO).set_fill(ROJO, 0.4), run_time=0.25)
            self.play(LaggedStart(*[s.animate.shift(DOWN * 4).rotate(1.2).set_opacity(0) for s in sobra],
                                  lag_ratio=0.03), run_time=1.0)
            self.vineta_ariane = (grande[:8], chica, et_c)

    # ------------------------------------------------------------------ b2
    def patriot(self):
        with self.narrar("b2"):
            self.en("m1991")
            self.viaje(1996, 1991, AMBAR, fuera=self.vineta_ariane)

            self.en("misil")
            blanco = VGroup(*[Circle(radius=r, color=ROJO, stroke_width=4) for r in (0.7, 0.45, 0.2)])
            blanco.move_to(RIGHT * 4.5 + UP * 1.3)
            mis = misil().scale(1.3).move_to(LEFT * 7.5 + UP * 1.3)
            self.add(mis)
            self.sfx("whoosh")
            self.play(mis.animate.move_to(LEFT * 3.2 + UP * 1.3), GrowFromCenter(blanco), run_time=0.7)

            self.en("redondeo")
            exacto = Text("0,1", color=TXT, weight=BOLD).scale(1.2).move_to(DOWN * 1.1)
            aprox = Text("0,0999999", color=AMBAR, weight=BOLD).scale(1.2).move_to(exacto)
            self.pop(exacto)
            self.wait(0.3)
            self.sfx("error", gain=-18)
            self.play(Transform(exacto, aprox), run_time=0.5)

            self.en("reloj")
            self.play(FadeOut(exacto, shift=UP * 0.3), run_time=0.25)
            rel = reloj().scale(1.3).move_to(LEFT * 1.8 + DOWN * 1.1)
            error = ValueTracker(0.0001)
            lectura = always_redraw(lambda: Text(f"+{num_es(error.get_value(), 4)} s", color=AMBAR,
                                                 weight=BOLD).scale(0.9).next_to(rel, RIGHT, buff=0.5))
            self.play(FadeIn(rel), FadeIn(lectura), run_time=0.3)
            for i in range(8):
                self.sfx("tick", gain=-20, offset=i * 0.3)
            self.play(Rotate(rel.minutero, -6 * PI, about_point=rel.aro.get_center()),
                      error.animate.set_value(0.34), run_time=2.4, rate_func=rate_functions.ease_in_quad)

            self.en("falla")
            self.sfx("whoosh")
            self.play(mis.animate.move_to(RIGHT * 8 + UP * 2.6), run_time=0.7, rate_func=rate_functions.linear)
            fallo = cruz(ROJO, 1.0).move_to(blanco)
            self.sfx("error", gain=-12)
            self.play(Create(fallo), run_time=0.3)
            self.remove(mis)
            lectura.clear_updaters()
            self.vineta_patriot = (blanco, fallo, rel, lectura)

    # ------------------------------------------------------------------ b3
    def pentium(self):
        with self.narrar("b3"):
            self.en("m1994")
            self.viaje(1991, 1994, AZUL, fuera=self.vineta_patriot)

            self.en("chip")
            ch = chip(AZUL, "Pentium").scale(2.2).move_to(UP * 0.7)
            self.pop(ch)

            self.en("cambio")
            for x in ch.get_family():
                x.permitir_fuera = True          # sale de cuadro a propósito
            for _ in range(2):
                nuevo = chip(AZUL, "Pentium").scale(2.2).move_to(UP * 0.7 + RIGHT * 9)
                for x in nuevo.get_family():
                    x.permitir_fuera = True
                self.sfx("whoosh", gain=-16)
                self.play(ch.animate.shift(LEFT * 9), nuevo.animate.move_to(UP * 0.7), run_time=0.45,
                          rate_func=rate_functions.ease_in_out_cubic)
                self.remove(ch)
                ch = nuevo

            self.en("tabla")
            celdas = VGroup(*[Rectangle(width=0.36, height=0.2, color=AZUL, stroke_width=1.2)
                              .set_fill(AZUL, 0.2) for _ in range(30)]).arrange_in_grid(6, 5, buff=0.04)
            celdas.move_to(ch.cuerpo)
            self.play(FadeOut(ch[2]), LaggedStart(*[FadeIn(c) for c in celdas], lag_ratio=0.03),
                      run_time=0.6)
            # encuadre corrido a la derecha: ahí van a aparecer las cuentas
            self.zoom(ch.get_center() + RIGHT * 1.5, 7.5, run_time=0.7)

            self.en("division")
            mala = celdas[17]
            self.sfx("error", gain=-10)
            self.play(mala.animate.set_fill(ROJO, 0.9).set_color(ROJO), run_time=0.3)
            cuenta = Text("4195835 ÷ 3145727", color=TXT, weight=BOLD).scale(0.32)
            bien = Text("correcto: 1,333820", color=VERDE).scale(0.26)
            chip_mal = Text("Pentium:  1,333739", color=ROJO).scale(0.26)
            textos = VGroup(cuenta, bien, chip_mal).arrange(DOWN, buff=0.1, aligned_edge=LEFT)
            textos.next_to(ch, RIGHT, buff=0.25)
            self.play(LaggedStart(*[FadeIn(t, shift=LEFT * 0.2) for t in textos], lag_ratio=0.4),
                      run_time=0.9)
        self.zoom_reset(run_time=0.6)
