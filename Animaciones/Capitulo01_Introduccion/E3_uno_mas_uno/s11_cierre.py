"""Escena 11 — Cierre: resumen y la pregunta que abre el episodio 4."""
from comun.escena import *


class Cierre(EscenaCurso):
    ESCENA = "11"

    def construct(self):
        with self.narrar("b1"):
            # «Resumamos.»
            self.en("resumen")
            linea = Line(LEFT * 6.5, RIGHT * 6.5, color=AMBAR, stroke_width=4)
            self.sfx("whoosh")
            self.play(Create(linea), run_time=0.4)
            self.play(FadeOut(linea, shift=UP * 0.3), run_time=0.3)

            # «Dos hilos que suman uno» — miniatura del montaje
            self.en("hilos")
            m = montaje_contador("0").scale(0.5).move_to(LEFT * 4.1 + UP * 2.0)
            self.play(FadeIn(m, scale=0.8), run_time=0.4)

            # «el contador en uno»
            self.en("uno")
            uno = Text("1", color=ROJO).scale(0.32).move_to(m.celda.caja)
            self.play(FadeOut(m.celda.contenido), FadeIn(uno), m.celda.caja.animate.set_stroke(ROJO, 4), run_time=0.3)

            # «condición de carrera»
            self.en("carrera")
            et1 = Text("condición de carrera", color=AMBAR, weight=BOLD).scale(0.5).next_to(m, DOWN, buff=0.3)
            self.pop(et1)

            # «la sección crítica necesita» — la sala con su puerta
            self.en("sc")
            sala = SalaPuerta(2.6, 1.9).move_to(RIGHT * 3.6 + UP * 2.0)
            p = proceso_mini("P1", AZUL).scale(0.8).move_to(sala.dentro)
            self.play(FadeIn(sala), FadeIn(p), run_time=0.35)

            # «exclusión mutua» — la puerta se cierra con candado
            self.en("exclusion")
            cand = candado(AMBAR).scale(0.5).next_to(sala.puerta, LEFT, buff=0.1)
            self.play(FadeIn(cand, scale=0.5), run_time=0.3)
            et2 = Text("exclusión mutua", color=AMBAR, weight=BOLD).scale(0.5).next_to(sala, DOWN, buff=0.3)
            self.pop(et2)

            # «seis reglas del juego»
            self.en("seis")
            reglas = reglas_seis(lado=0.8).move_to(DOWN * 0.3)
            for i in range(6):
                self.sfx("tick", gain=-16, offset=i * 0.08)
            self.play(LaggedStart(*[FadeIn(r, scale=1.4) for r in reglas], lag_ratio=0.12), run_time=0.6)

            # «Y aun así» — las casillas se apartan
            self.en("aunasi")
            self.play(reglas.animate.scale(0.7).move_to(LEFT * 4.1 + DOWN * 0.3), run_time=0.4)

            # «quedar atascados» — las tres cartas
            self.en("atascados")
            cartas = VGroup(*[tarjeta(t, c, ancho=1.9, alto=1.2, escala_texto=0.45)
                              for t, c in [("deadlock", ROJO), ("inanición", AMBAR), ("livelock", MAGENTA)]])
            cartas.arrange(RIGHT, buff=0.35).move_to(DOWN * 2.6)
            self.play(LaggedStart(*[FadeIn(k, shift=UP * 0.3) for k in cartas], lag_ratio=0.12), run_time=0.4)

            # «por interbloqueo», «por inanición», «o por livelock»
            for marca, k in zip(["t1", "t2", "t3"], cartas):
                self.en(marca)
                self.resaltar(k.marco, color=TXT, escala=1.12, run_time=0.4)

            # «en común» — todo se agrupa y se aleja
            self.en("comun")
            todo = VGroup(m, uno, et1, sala, p, cand, et2, reglas, cartas)
            self.play(todo.animate.scale(0.4).fade(0.7), run_time=0.4)

            # «un solo intercalado malo» — árbol gris con un camino rojo
            self.en("malo")
            self.corte()
            arbol = ArbolIntercalado(3, 3, ancho=9.0, alto=5.6, etiquetas=False, color_a=INK, color_b=INK,
                                     grosor=2).move_to(ORIGIN)
            arbol.set_opacity(0.6)
            self.play(FadeIn(arbol), run_time=0.3)
            camino = arbol.camino(arbol.hojas[7])
            self.sfx("error", gain=-14)
            self.play(*[a.linea.animate.set_stroke(ROJO, 6, opacity=1) for a in camino], run_time=0.4)

            # «entre millones» — la cámara se aleja y el árbol se multiplica
            self.en("millones")
            copias = VGroup()
            for i in range(-2, 3):
                for j in range(-2, 3):
                    if i == 0 and j == 0:
                        continue
                    copias.add(arbol.copy().set_opacity(0.4).shift(RIGHT * 10.5 * i + UP * 6.6 * j))
            self.add(copias)
            self.play(self.camera.frame.animate.set(width=config.frame_width * 4.2), run_time=1.6,
                      rate_func=rate_functions.ease_in_out_sine)

            # «Probarlo mil veces» — llueven tildes verdes
            self.en("probar")
            self.corte()
            self.camera.frame.set(width=config.frame_width).move_to(ORIGIN)
            tildes = VGroup(*[tilde(VERDE, 0.45) for _ in range(40)]).arrange_in_grid(5, 8, buff=(0.75, 0.55))
            tildes.move_to(DOWN * 0.2)
            for i in range(8):
                self.sfx("tick", gain=-20, offset=i * 0.08)
            self.play(LaggedStart(*[FadeIn(t, shift=DOWN * 1.2) for t in tildes], lag_ratio=0.02), run_time=0.8)

            # «no sirve» — se tachan de golpe
            self.en("nosirve")
            tacha = Line(tildes.get_corner(DL) + DL * 0.2, tildes.get_corner(UR) + UR * 0.2, color=ROJO, stroke_width=12)
            self.sfx("impacto", gain=-10)
            self.play(Create(tacha), tildes.animate.set_color(INK), run_time=0.3)

            # «¿cómo se demuestra» — una demostración que se escribe sola
            self.en("como")
            self.corte()
            hoja = documento(TXT).scale(4.2).move_to(LEFT * 3.2)
            simbolos = VGroup(Text("∀ t", color=AMBAR, weight=BOLD).scale(1.0),
                              Text("⇒", color=AMBAR, weight=BOLD).scale(1.0),
                              Text("□", color=AMBAR, weight=BOLD).scale(0.8)).arrange(DOWN, buff=0.35)
            simbolos.move_to(hoja[0].get_center() + RIGHT * 0.1)
            self.play(FadeIn(hoja[0]), run_time=0.25)
            self.play(LaggedStart(*[Write(s) for s in simbolos], lag_ratio=0.4), run_time=1.0)

            # «nunca falla?» — la pregunta
            self.en("pregunta")
            preg = Text("¿nunca falla?", color=TXT, weight=BOLD).scale(1.3).move_to(RIGHT * 2.9)
            self.pop(preg, sonido="impacto")

            # «el próximo episodio»
            self.en("proximo")
            ep = Text("Episodio 4", color=INK, weight=BOLD).scale(0.7).next_to(preg, UP, buff=0.5)
            self.play(FadeIn(ep, shift=DOWN * 0.2), run_time=0.3)

            # «demostrar que nunca falla» — el título de E4, solo
            self.en("titulo")
            self.corte()
            tit = self.texto_centrado("Demostrar que", "nunca falla", escala=1.35)
            tit[1].set_color(AMBAR)
            self.sfx("whoosh")
            self.play(FadeIn(tit, scale=0.8), run_time=0.5)

        self.wait(1.0)
        self.limpiar()
