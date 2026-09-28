"""Escena 13 — Cierre: resumen, fin del capítulo 1 y la máquina que recuerda sin memoria."""
from comun.escena import *


class Cierre(EscenaCurso):
    ESCENA = "13"

    def construct(self):
        with self.narrar("b1"):
            # «Resumamos.»
            self.en("resumen")
            linea = Line(LEFT * 6.5, RIGHT * 6.5, color=AMBAR, stroke_width=4)
            self.sfx("whoosh")
            self.play(Create(linea), run_time=0.4)
            self.play(FadeOut(linea, shift=UP * 0.3), run_time=0.3)

            # «competir sin conocerse» — la escalera; escalón 1
            self.en("competir")
            esc = escalera(3, ancho=1.9, alto=0.75).move_to(LEFT * 3.6 + UP * 1.4)
            self.play(FadeIn(esc, shift=UP * 0.3), run_time=0.3)
            self.play(esc.escalones[0].animate.set_fill(AMBAR, 0.3).set_stroke(AMBAR), run_time=0.3)

            # «compartir datos» — escalón 2
            self.en("compartir")
            self.play(esc.escalones[1].animate.set_fill(AMBAR, 0.3).set_stroke(AMBAR), run_time=0.3)

            # «hablarse con mensajes» — escalón 3, con el sobre
            self.en("hablar")
            sb = sobre(TXT).scale(0.55).next_to(esc.escalones[2], UP, buff=0.15)
            self.play(esc.escalones[2].animate.set_fill(AMBAR, 0.3).set_stroke(AMBAR), run_time=0.3)
            self.pop(sb)

            # «sea cual sea la forma» — los tres escalones, en un grupo
            self.en("forma")
            marco = SurroundingRectangle(VGroup(esc, sb), color=TXT, buff=0.25, corner_radius=0.15, stroke_width=4)
            self.play(Create(marco), run_time=0.4)

            # «probar no basta» — la escalera se va; lluvia de tildes, tachada
            self.en("probar")
            tildes = VGroup(*[tilde(VERDE, 0.4) for _ in range(20)]).arrange_in_grid(4, 5, buff=(0.55, 0.4))
            tildes.move_to(RIGHT * 3.7 + UP * 1.6)
            self.play(FadeOut(VGroup(esc, sb, marco), scale=0.7),
                      LaggedStart(*[FadeIn(t, shift=DOWN * 0.8) for t in tildes], lag_ratio=0.03), run_time=0.5)
            tacha = Line(tildes.get_corner(DL) + DL * 0.15, tildes.get_corner(UR) + UR * 0.15, color=ROJO, stroke_width=10)
            self.sfx("impacto", gain=-12)
            self.play(Create(tacha), tildes.animate.set_color(INK), run_time=0.25)

            # «modelar y verificar»
            self.en("modelar")
            w1 = self.palabra("modelar", AMBAR, 1.0).move_to(LEFT * 3.8 + UP * 2.1)
            w2 = self.palabra("verificar", AMBAR, 1.0).move_to(LEFT * 3.8 + UP * 1.1)
            self.pop(w1, w2)

            # «cinco lugares y cuatro transiciones» — la red, con su ecuación
            self.en("red")
            red = RedMutex().scale(0.45).move_to(DOWN * 2.15)
            eq = Text("P1 en SC + P2 en SC + mutex = 1", color=TXT, weight=BOLD).scale(0.5).move_to(DOWN * 0.35)
            self.play(FadeIn(red, shift=UP * 0.3), FadeIn(eq), run_time=0.5)

            # «ningún test» — el sello
            self.en("test")
            s = sello("demostrado").scale(0.9).move_to(RIGHT * 4.6 + DOWN * 2.1)
            self.pop(s, sonido="impacto")

            # «Con esto cerramos» — todo se aleja
            self.en("cerramos")
            todo = VGroup(tildes, tacha, w1, w2, red, eq, s)
            self.play(todo.animate.scale(0.45).fade(0.6), run_time=0.45)

            # «introducción» — capítulo 1, con tilde
            self.en("intro")
            cap = Text("Capítulo 1: Introducción", color=TXT, weight=BOLD).scale(1.3)
            ok = tilde(VERDE, 0.7).next_to(cap, RIGHT, buff=0.35)
            self.play(FadeOut(todo), run_time=0.2)
            self.pop(VGroup(cap, ok), sonido="exito")

            # «no tiene cinco lugares» — la red de cinco lugares
            self.en("cinco")
            self.corte()
            red = RedMutex().scale(0.7)
            self.play(FadeIn(red, scale=0.8), run_time=0.35)

            # «cincuenta» — la red se multiplica; la cámara se aleja
            self.en("cincuenta")
            copias = VGroup(*[red.copy().move_to([10.5 * i, 7.4 * j - 3.7, 0])
                              for i in range(-2, 3) for j in range(2) if not (i == 0 and j == 0)])
            cincuenta = Text("50 lugares", color=AMBAR, weight=BOLD).scale(3.2).move_to(UP * 8.6)
            self.add(copias)
            self.sfx("whoosh", gain=-14)
            self.play(red.animate.move_to(DOWN * 3.7), self.camera.frame.animate.set(width=60).move_to(UP * 1.2),
                      FadeIn(cincuenta), run_time=1.3, rate_func=rate_functions.ease_in_out_sine)

            # «Llegaremos ahí.» — todo se encoge a un punto: capítulo 4
            self.en("llegaremos")
            todo = VGroup(red, copias, cincuenta)
            self.play(todo.animate.scale(0.01).move_to(ORIGIN),
                      self.camera.frame.animate.set(width=config.frame_width).move_to(ORIGIN), run_time=1.0)
            self.remove(red, copias, cincuenta)
            punto = Dot(ORIGIN, radius=0.18, color=AMBAR)
            c4 = Text("capítulo 4", color=AMBAR, weight=BOLD).scale(0.8).next_to(punto, DOWN, buff=0.3)
            self.add(punto)
            self.pop(c4)

            # «el próximo capítulo» — Capítulo 2
            self.en("proximo")
            self.corte()
            c2 = Text("Capítulo 2", color=AMBAR, weight=BOLD).scale(1.4)
            self.pop(c2, sonido="impacto")

            # «la pieza más sencilla» — un solo estado
            self.en("pieza")
            m = maquina_estados_mini(AZUL, lado=3.6, radio=0.6).move_to(DOWN * 0.5)
            solo = m.estados[0].copy()
            self.play(c2.animate.scale(0.65).move_to(UP * 3.2), run_time=0.35)
            self.pop(solo)

            # «la máquina de estados» — se completa con tres estados
            self.en("fsm")
            self.play(LaggedStart(*[GrowFromCenter(e) for e in m.estados[1:]], lag_ratio=0.3),
                      Create(m.flechas), run_time=0.6)
            self.remove(solo)
            self.add(m)

            # «por qué explota» — destello de la grilla de 100 puntos
            self.en("explota")
            g = VGroup(*[Dot(radius=0.07, color=ROJO) for _ in range(100)]).arrange_in_grid(10, 10, buff=0.2)
            g.move_to(m)
            self.sfx("impacto", gain=-14)
            self.play(FadeIn(g, scale=1.6), run_time=0.3)
            self.play(FadeOut(g, scale=1.3), run_time=0.45)

            # «cómo piensa» — un punto recorre la máquina
            self.en("piensa")
            cs = [e.get_center() for e in m.estados]
            pt = Dot(cs[0], radius=0.17, color=AMBAR)
            self.add(pt)
            self.play(MoveAlongPath(pt, VMobject().set_points_as_corners([*cs, cs[0]])), run_time=1.3,
                      rate_func=rate_functions.linear)

            # «la máquina que recuerda sin memoria» — el título del capítulo 2, episodio 1
            self.en("titulo")
            self.corte()
            tit = self.texto_centrado("La máquina que", "recuerda sin memoria", escala=1.3)
            tit[1].set_color(AMBAR)
            self.sfx("whoosh")
            self.play(FadeIn(tit, scale=0.8), run_time=0.5)

        self.wait(1.0)
        self.limpiar()
