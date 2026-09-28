"""Escena 07 — Seguridad y progreso; verificar en vez de probar; el modelo."""
from comun.escena import *


def grilla(filas: int, cols: int, sep: float = 0.5, r: float = 0.1, color=VERDE) -> VGroup:
    return VGroup(*[Dot(radius=r, color=color) for _ in range(filas * cols)]).arrange_in_grid(
        filas, cols, buff=sep - 2 * r)


class Verificacion(EscenaCurso):
    ESCENA = "07"

    def construct(self):
        self.familias()
        self.modelo()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def familias(self):
        with self.narrar("b1"):
            # «dos familias» — dos huecos
            self.en("familias")
            huecos = VGroup(*[DashedVMobject(RoundedRectangle(width=6.6, height=7.2, corner_radius=0.3),
                                             num_dashes=60).set_stroke(INK, 3).move_to(c)
                              for c in (LEFT * 3.55, RIGHT * 3.55)])
            self.play(Create(huecos), run_time=0.45)

            # «seguridad» — el escudo
            self.en("seguridad")
            esc = escudo(VERDE).scale(1.8).move_to(LEFT * 3.55 + UP * 2.0)
            et1 = Text("seguridad", color=VERDE, weight=BOLD).scale(0.85).next_to(esc, DOWN, buff=0.25)
            self.pop(esc, et1)

            # «nada malo ocurra nunca» — el escudo destella
            self.en("nadamalo")
            self.sfx("exito", gain=-18)
            self.play(esc.animate(rate_func=there_and_back).scale(1.15), run_time=0.5)

            # «nunca hay dos» — la sala: uno dentro, otro llega a la puerta
            self.en("ejemplo1")
            sala = SalaPuerta(3.4, 2.5).move_to(LEFT * 2.3 + DOWN * 2.1)
            p1 = proceso_mini("P1", AZUL).scale(1.05).move_to(sala.dentro)
            p2 = proceso_mini("P2", MAGENTA).scale(1.05).move_to(sala.fuera + LEFT * 0.6)
            self.play(FadeIn(sala), FadeIn(p1), FadeIn(p2), run_time=0.35)
            self.play(p2.animate.move_to(sala.fuera), run_time=0.4)

            # «a la vez» — el de fuera rebota, con una cruz
            self.en("alavez")
            self.play(p2.animate.shift(RIGHT * 0.4), run_time=0.2, rate_func=rate_functions.ease_in_quad)
            self.sfx("error", gain=-14)
            x = cruz(ROJO, 0.45).next_to(p2, UP, buff=0.2)
            self.play(p2.animate.shift(LEFT * 0.4), FadeIn(x, scale=1.6), run_time=0.3,
                      rate_func=rate_functions.ease_out_bounce)

            # «de progreso» — la flecha hacia delante
            self.en("progreso")
            fl = Arrow(RIGHT * 1.8 + UP * 2.0, RIGHT * 5.3 + UP * 2.0, color=VERDE, stroke_width=18, buff=0,
                       max_tip_length_to_length_ratio=0.3)
            et2 = Text("progreso", color=VERDE, weight=BOLD).scale(0.85).move_to(RIGHT * 3.55 + UP * 0.75)
            self.play(GrowArrow(fl), run_time=0.35)
            self.pop(et2)

            # «algo bueno termine pasando» — la flecha avanza
            self.en("bueno")
            self.play(fl.animate(rate_func=there_and_back).shift(RIGHT * 0.5), run_time=0.6)

            # «todo proceso que pide» — un proceso en fila levanta la mano
            self.en("pide")
            sala2 = SalaPuerta(2.5, 2.5).move_to(RIGHT * 5.1 + DOWN * 2.1)
            p3 = proceso_mini("P3", CIAN).scale(1.05).move_to(sala2.fuera + RIGHT * 0.1)
            mano = Text("!", color=AMBAR, weight=BOLD).scale(1.2).next_to(p3, UP, buff=0.2)
            self.play(FadeIn(sala2), FadeIn(p3, shift=RIGHT * 0.3), run_time=0.35)
            self.pop(mano)

            # «tarde o temprano lo consigue» — reloj; llega su turno y entra
            self.en("consigue")
            rj = reloj(TXT).scale(0.7).move_to(mano)
            self.play(FadeOut(mano), FadeIn(rj), run_time=0.15)
            self.play(Rotate(rj.minutero, angle=-2 * PI, about_point=rj.aro.get_center()), run_time=0.8)
            self.play(sala2.abrir(), FadeOut(rj), run_time=0.25)
            self.play(p3.animate.move_to(sala2.dentro), run_time=0.4)
            self.play(sala2.cerrar(), run_time=0.2)
            self.sfx("exito", gain=-16)

    # ------------------------------------------------------------------ b2
    def modelo(self):
        with self.narrar("b2"):
            # «la frase de Dijkstra»
            self.en("dijkstra")
            self.corte()
            nombre = Text("Edsger Dijkstra", color=TXT, weight=BOLD).scale(1.3).move_to(UP * 2.9)
            self.play(Write(nombre), run_time=0.6)

            # «el primer episodio»
            self.en("ep1")
            ep = Text("Episodio 1", color=INK, weight=BOLD).scale(0.5).to_corner(UR, buff=0.4)
            self.play(FadeIn(ep, shift=LEFT * 0.2), run_time=0.3)

            # «la presencia de errores» — la lupa encuentra un punto rojo
            self.en("presencia")
            l1 = Text("El testing puede mostrar la presencia de errores,", color=TXT).scale(0.62)
            l1.next_to(nombre, DOWN, buff=0.55)
            g1 = grilla(4, 6, sep=0.5).move_to(LEFT * 3.4 + DOWN * 1.7)
            g1[9].set_color(ROJO)
            lp = lupa().scale(0.9)
            off = lp.get_center() - lp.vidrio.get_center()
            lp.move_to(g1[2].get_center() + off)
            self.play(FadeIn(l1, shift=UP * 0.2), FadeIn(g1), FadeIn(lp), run_time=0.4)
            self.play(lp.animate.move_to(g1[9].get_center() + off), run_time=0.45)
            self.sfx("exito", gain=-16)
            self.play(g1[9].animate(rate_func=there_and_back).scale(2.2), run_time=0.4)

            # «nunca su ausencia» — segunda mitad; la lupa en una zona vacía
            self.en("ausencia")
            l2 = Text("pero nunca puede demostrar su ausencia.", color=AMBAR, weight=BOLD).scale(0.62)
            l2.next_to(l1, DOWN, buff=0.25)
            g2 = grilla(4, 6, sep=0.5).move_to(RIGHT * 3.4 + DOWN * 1.7)
            lp2 = lupa().scale(0.9).move_to(g2.get_center() + off)
            duda = Text("?", color=AMBAR, weight=BOLD).scale(1.6).next_to(g2, RIGHT, buff=0.4)
            self.play(FadeIn(l2, shift=UP * 0.2), FadeIn(g2), FadeIn(lp2), run_time=0.4)
            self.pop(duda)

            # «la verificación»
            self.en("verificacion")
            self.corte()
            ver = self.palabra("verificación", AMBAR, 1.4).move_to(UP * 3.15)
            self.pop(ver, sonido="impacto")

            # «casos sueltos» — el árbol de ejecuciones; solo unos pocos caminos
            self.en("sueltos")
            arbol = ArbolIntercalado(3, 3, ancho=11.0, alto=5.2, etiquetas=False, color_a=INK, color_b=INK,
                                     grosor=2.5).move_to(DOWN * 0.7)
            self.play(FadeIn(arbol), run_time=0.35)
            sueltos = [arbol.hojas[i] for i in (3, 9, 15)]
            self.play(*[a.linea.animate.set_stroke(CIAN, 6) for h in sueltos for a in arbol.camino(h)], run_time=0.4)

            # «de manera exhaustiva» — todas las ramas, en un barrido
            self.en("exhaustiva")
            aristas = sorted(arbol.aristas.values(), key=lambda a: a.linea.get_center()[0])
            self.sfx("whoosh", gain=-16)
            self.play(LaggedStart(*[a.linea.animate.set_stroke(VERDE, 4) for a in aristas], lag_ratio=0.02),
                      run_time=1.3)

            # «incluidas» — las ramas raras del borde, en ámbar
            self.en("incluidas")
            raras = [arbol.hojas[0], arbol.hojas[-1]]
            self.play(*[a.linea.animate.set_stroke(AMBAR, 7) for h in raras for a in arbol.camino(h)], run_time=0.35)

            # «ningún test» — un rótulo junto a ellas
            self.en("ningun")
            nt = Text("ningún test", color=ROJO, weight=BOLD).scale(0.6).move_to(RIGHT * 5.3 + UP * 2.55)
            self.pop(nt)

            # «solo es posible» — el árbol se encoge; «¿cómo?»
            self.en("posible")
            self.play(VGroup(ver, arbol, nt).animate.scale(0.3).to_corner(UL, buff=0.4), run_time=0.5)
            como = Text("¿cómo?", color=TXT, weight=BOLD).scale(1.6)
            self.pop(como)

            # «un modelo» — programa → esquema
            self.en("modelo")
            self.corte()
            barras = VGroup(*[Line(LEFT * 0.7, RIGHT * (0.7 - 0.3 * (i % 2)), color=INK, stroke_width=5)
                              for i in range(3)]).arrange(DOWN, buff=0.18, aligned_edge=LEFT)
            prog = ventana_app("programa", AZUL, barras).scale(2.0).move_to(LEFT * 4.1 + UP * 1.0)
            datos = caja("datos", CIAN, ancho=1.9, alto=0.9, scale=0.65, relleno=0.2).move_to(LEFT * 5.2 + DOWN * 1.6)
            inter = ventana_app("interfaz", MAGENTA).scale(0.85).move_to(LEFT * 2.9 + DOWN * 1.6)
            fl = Arrow(LEFT * 1.4 + UP * 0.2, RIGHT * 1.3 + UP * 0.2, color=TXT, stroke_width=8, buff=0)
            self.play(FadeIn(prog), FadeIn(datos), FadeIn(inter), GrowArrow(fl), run_time=0.45)

            # «una representación abstracta» — el esquema se dibuja
            self.en("abstracta")
            h1 = Circle(radius=0.55, color=INK, stroke_width=5).move_to(RIGHT * 3.0 + UP * 1.4)
            h2 = Circle(radius=0.55, color=INK, stroke_width=5).move_to(RIGHT * 5.8 + UP * 1.4)
            cd = candado(INK).scale(0.9).move_to(RIGHT * 4.4 + DOWN * 0.9)
            a1 = Arrow(h1.get_center(), cd.get_center(), color=INK, stroke_width=5, buff=0.65)
            a2 = Arrow(h2.get_center(), cd.get_center(), color=INK, stroke_width=5, buff=0.65)
            self.play(Create(h1), Create(h2), FadeIn(cd), GrowArrow(a1), GrowArrow(a2), run_time=0.6)

            # «lo que necesitamos analizar» — hilos y candado se encienden
            self.en("analizar")
            cd2 = candado(AMBAR).scale(0.9).move_to(cd)
            self.play(h1.animate.set_stroke(AZUL).set_fill(AZUL, 0.3), h2.animate.set_stroke(MAGENTA).set_fill(MAGENTA, 0.3),
                      ReplacementTransform(cd, cd2), run_time=0.45)

            # «descarta el resto» — el programa empieza a desarmarse
            self.en("descarta")
            self.play(Wiggle(VGroup(prog, datos, inter), scale_value=1.05), run_time=0.6)

            # «la representación interna» — cae «datos»
            self.en("datos")
            self.play(datos.animate.shift(DOWN * 1.4).rotate(-PI / 6).fade(0.85), run_time=0.5,
                      rate_func=rate_functions.ease_in_quad)

            # «interfaz con el usuario» — cae la ventana; queda el esquema limpio
            self.en("interfaz")
            self.play(inter.animate.shift(DOWN * 1.4).rotate(PI / 6).fade(0.85), run_time=0.5,
                      rate_func=rate_functions.ease_in_quad)
            esquema = VGroup(h1, h2, cd2, a1, a2)
            self.play(esquema.animate(rate_func=there_and_back).scale(1.08), run_time=0.45)
