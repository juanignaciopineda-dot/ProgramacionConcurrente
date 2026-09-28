"""Escena 03 — Therac-25: la historia y su causa (condición de carrera)."""
from comun.escena import *


class Therac(EscenaCurso):
    ESCENA = "03"

    def construct(self):
        self.historia()
        self.corte()
        self.causa()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def historia(self):
        with self.narrar("b1"):
            self.en("linea")
            lt = LineaTiempo().move_to(DOWN * 3.3)
            m85 = lt.marca(1985, ROJO)
            self.sfx("whoosh")
            self.play(Create(lt), run_time=0.5)
            self.pop(m85)

            self.en("maquina")
            maq = maquina_radioterapia().scale(4.2).move_to(DOWN * 0.1)
            self.sfx("whoosh")
            self.play(FadeIn(maq, shift=LEFT * 3), run_time=0.6)
            self.play(maq.haz.animate(rate_func=there_and_back).set_fill(opacity=1), run_time=0.5)

            self.en("nombre")
            nombre = Text("Therac-25", color=TXT, weight=BOLD).scale(0.9).next_to(maq, UP, buff=0.3)
            self.pop(nombre)

            self.en("pacientes")
            pac = paciente_acostado().scale_to_fit_width(maq.camilla.width * 0.8)
            pac.next_to(maq.camilla, UP, buff=0.02)
            self.play(GrowFromCenter(pac), run_time=0.4)

            self.en("hospitales")
            hosp = VGroup(*[cruz_hospital().scale(0.8) for _ in range(3)]).arrange(RIGHT, buff=0.35)
            hosp.to_corner(UR, buff=0.6)
            for i in range(3):
                self.sfx("pop", offset=i * 0.18)
            self.play(LaggedStart(*[GrowFromCenter(h) for h in hosp], lag_ratio=0.35), run_time=0.7)

            self.en("ok")
            ok = tilde(VERDE, 0.9).next_to(nombre, RIGHT, buff=0.35)
            self.sfx("exito")
            self.play(Create(ok), run_time=0.4)

            self.en("falla")
            mal = cruz(ROJO, 1.0).move_to(ok)
            flash = FullScreenRectangle().set_fill(ROJO, 0.25).set_stroke(width=0)
            self.sfx("error", gain=-10)
            self.play(ReplacementTransform(ok, mal),
                      maq.haz.animate.set_fill(ROJO, 0.9).stretch(2.2, 0),
                      FadeIn(flash, rate_func=there_and_back), run_time=0.5)
            self.remove(flash)

            self.en("seis")
            escena_maq = VGroup(maq, pac, mal, hosp, nombre)
            fila = VGroup(*[persona(TXT) for _ in range(6)]).arrange_in_grid(2, 3, buff=(0.7, 0.5))
            fila.scale(1.5).move_to(RIGHT * 3.3 + UP * 0.2)
            self.play(escena_maq.animate.scale(0.6).move_to(LEFT * 3.6 + UP * 0.3),
                      FadeIn(fila, shift=LEFT), run_time=0.6)
            for i in range(6):
                self.sfx("latido", gain=-16, offset=i * 0.25)
            self.play(LaggedStart(*[p.animate.set_color(ROJO) for p in fila], lag_ratio=0.4),
                      run_time=1.6)

            self.en("murieron")
            self.play(*[fila[i].animate.set_color(INK).set_opacity(0.3) for i in (1, 3, 4)],
                      run_time=1.2)

    # ------------------------------------------------------------------ b2
    def causa(self):
        with self.narrar("b2"):
            self.en("ingenieros")
            ings = VGroup(ingeniero(AZUL), ingeniero(CIAN)).arrange(RIGHT, buff=1.2).scale(2.0)
            ings.move_to(LEFT * 3.4 + DOWN * 1.2)
            self.play(LaggedStart(*[FadeIn(i, shift=RIGHT) for i in ings], lag_ratio=0.3), run_time=0.7)

            self.en("pensando")
            burbujas = VGroup(*[
                burbuja_pensamiento(Text("?", color=AMBAR, weight=BOLD).scale(1.0))
                .scale(1.1).next_to(i, UP, buff=0.1).shift(RIGHT * 0.5)
                for i in ings])
            self.pop(*burbujas)

            self.en("boton")
            bot = boton().scale(1.4).move_to(RIGHT * 3.2 + UP * 1.4)
            self.pop(bot)
            self.wait(0.2)
            self.sfx("error", gain=-20)
            self.play(Create(tachar(bot)), run_time=0.3)

            self.en("cuenta")
            cta = Text("2 + 2 = 4", color=TXT, weight=BOLD).scale(1.2).move_to(RIGHT * 3.2 + DOWN * 1.0)
            self.pop(cta)
            self.wait(0.2)
            self.sfx("error", gain=-20)
            self.play(Create(tachar(cta)), run_time=0.3)

            # plano nuevo: el operador
            self.en("operador")
            self.corte()
            op = persona(VERDE).scale(1.9).move_to(LEFT * 5.0 + DOWN * 2.0)
            tec = teclado().scale(1.6).next_to(op, RIGHT, buff=0.5).align_to(op, DOWN)
            self.play(FadeIn(op, shift=UP * 0.3), FadeIn(tec, shift=UP * 0.3), run_time=0.5)

            self.en("teclas")
            self.rafaga(tec.teclas, n=16, run_time=1.5)

            self.en("partes")
            ui = caja("interfaz", AZUL, ancho=3.4, alto=1.1, scale=0.7).move_to(LEFT * 4.3 + UP * 1.9)
            ctrl = caja("control del haz", MAGENTA, ancho=3.4, alto=1.1, scale=0.7).move_to(RIGHT * 4.3 + UP * 1.9)
            dato = caja("modo", INK, alto=1.0, scale=0.7).move_to(UP * 1.9)
            for c in (ui, ctrl):
                c[1].set_opacity(0)          # cajas vacías: el rótulo llega con su frase
            self.pop(ui[0], ctrl[0])
            self.pop(dato, sonido=None)

            self.en("interfaz")
            cable = Line(tec.get_top(), ui.get_bottom(), color=AZUL, stroke_width=3)
            self.play(ui[1].animate.set_opacity(1), ui[0].animate.set_fill(AZUL, 0.3), Create(cable), run_time=0.4)

            self.en("control")
            self.play(ctrl[1].animate.set_opacity(1), ctrl[0].animate.set_fill(MAGENTA, 0.3), run_time=0.4)

            self.en("pisan")
            a1 = Arrow(ui.get_right(), dato.get_left(), color=AZUL, stroke_width=6, buff=0.1)
            a2 = Arrow(ctrl.get_left(), dato.get_right(), color=MAGENTA, stroke_width=6, buff=0.1)
            self.play(GrowArrow(a1), GrowArrow(a2), run_time=0.35)
            self.sfx("impacto", gain=-8)
            self.play(dato[0].animate.set_color(ROJO).set_fill(ROJO, 0.4),
                      Flash(dato, color=ROJO, flash_radius=1.1, num_lines=14),
                      Wiggle(dato, scale_value=1.2), run_time=0.7)

            self.en("nombre")
            self.zoom(dato, 6.0, run_time=0.8)
            self.play(Wiggle(dato, scale_value=1.15), run_time=0.8)

            self.en("carrera")
            self.zoom_reset(run_time=0.5)
            termino = self.palabra("condición de carrera", AMBAR, 1.2).move_to(RIGHT * 3.1 + DOWN * 0.3)
            self.pop(termino, sonido="impacto")
            self.flotar(termino)
