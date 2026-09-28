"""Escena 12 — La demostración: el invariante P1 en SC + P2 en SC + mutex = 1."""
from comun.escena import *


def fila_ecuacion(partes: list[tuple[str, str]], escala: float = 0.95) -> VGroup:
    return VGroup(*[Text(t, color=c, weight=BOLD).scale(escala) for t, c in partes]).arrange(RIGHT, buff=0.25)


class Invariante(EscenaCurso):
    ESCENA = "12"

    def construct(self):
        self.red = RedMutex().move_to(DOWN * 0.3)
        self.add(self.red)
        self.demostracion()
        self.verificacion()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def demostracion(self):
        red = self.red
        with self.narrar("b1"):
            # «justifica todo el capítulo» — un foco sobre la red
            self.en("justifica")
            foco = Circle(radius=5.2, stroke_width=0, stroke_opacity=0).set_fill(TXT, 0.06).move_to(red)
            self.sfx("whoosh", gain=-18)
            self.play(FadeIn(foco, scale=0.6), run_time=0.5)

            # «se puede demostrar»
            self.en("demostrar")
            pal = self.palabra("demostrar", AMBAR, 1.2).move_to(UP * 3.35)
            self.pop(pal)

            # «con matemática» — la red se corre abajo; arriba queda espacio
            self.en("matematica")
            self.play(red.animate.scale(0.62).move_to(DOWN * 1.35), FadeOut(foco), run_time=0.55)

            # «un invariante»
            self.en("invariante")
            inv = self.palabra("invariante", AMBAR, 1.2).move_to(pal)
            self.play(FadeOut(pal, shift=UP * 0.2), FadeIn(inv, shift=UP * 0.2), run_time=0.35)

            # «nunca cambia» — un candado junto al nombre
            self.en("nunca")
            cd = candado(AMBAR).scale(0.6).next_to(inv, RIGHT, buff=0.3)
            self.pop(cd, sonido="tick")

            # «disparo tras disparo» — la red dispara dos veces
            self.en("disparos")
            red.entrar(self, 2, run_time=0.55)
            red.salir(self, 2, run_time=0.55)

            # «la suma de fichas» — el nombre sube a la esquina; empieza la ecuación
            self.en("suma")
            self.play(VGroup(inv, cd).animate.scale(0.6).to_corner(UL, buff=0.4), run_time=0.4)
            eq = fila_ecuacion([("P1 en SC", AZUL), ("+", TXT), ("P2 en SC", MAGENTA), ("+", TXT),
                                ("mutex", AMBAR), ("= 1", VERDE)]).move_to(UP * 2.35)
            self.eq = eq

            # «proceso uno en sección crítica»
            self.en("p1sc")
            self.play(FadeIn(eq[0], shift=DOWN * 0.2), run_time=0.3)
            self.resaltar(red.sc1.circulo, color=AZUL, escala=1.3, run_time=0.5)

            # «proceso dos en sección crítica»
            self.en("p2sc")
            self.play(FadeIn(eq[1:3], shift=DOWN * 0.2), run_time=0.3)
            self.resaltar(red.sc2.circulo, color=MAGENTA, escala=1.3, run_time=0.5)

            # «el lugar mutex»
            self.en("mutex")
            self.play(FadeIn(eq[3:5], shift=DOWN * 0.2), run_time=0.3)
            self.resaltar(red.mutex.circulo, color=AMBAR, escala=1.3, run_time=0.5)

            # «siempre igual a uno»
            self.en("igual")
            self.pop(eq[5], sonido="impacto")

            # «se deduce» — P1 en SC + P2 en SC ≤ 1
            self.en("deduce")
            eq2 = fila_ecuacion([("P1 en SC", AZUL), ("+", TXT), ("P2 en SC", MAGENTA), ("≤ 1", AMBAR)]).move_to(UP * 1.3)
            fl = Text("⇒", color=TXT, weight=BOLD).scale(0.85).next_to(eq2, LEFT, buff=0.35)
            self.play(FadeIn(VGroup(fl, eq2), shift=DOWN * 0.3), run_time=0.45)

            # «nunca puede superar uno»
            self.en("superar")
            self.play(eq2[3].animate(rate_func=there_and_back).scale(1.4), run_time=0.5)

            # «interleaving posible» — la red dispara en ráfaga; la ecuación sigue en 1
            self.en("interleaving")
            red.entrar(self, 1, run_time=0.45)
            red.salir(self, 1, run_time=0.45)

            # «los dos procesos van» — los dos lugares de SC: nunca juntos
            self.en("ambos")
            self.resaltar(red.sc1.circulo, red.sc2.circulo, color=AMBAR, escala=1.3, run_time=0.45)
            red.entrar(self, 2, run_time=0.45)
            red.salir(self, 2, run_time=0.45)

            # «al mismo tiempo» — tilde verde grande
            self.en("mismo")
            ok = tilde(VERDE, 1.5).move_to(RIGHT * 5.4 + DOWN * 1.3)
            self.play(Create(ok), run_time=0.4)
            self.sfx("exito", gain=-12)

    # ------------------------------------------------------------------ b2
    def verificacion(self):
        with self.narrar("b2"):
            # «ya no es testing» — la lluvia de tildes, tachada de golpe
            self.en("notesting")
            self.corte()
            tildes = VGroup(*[tilde(VERDE, 0.45) for _ in range(40)]).arrange_in_grid(5, 8, buff=(0.75, 0.55))
            tildes.move_to(DOWN * 0.2)
            for i in range(8):
                self.sfx("tick", gain=-20, offset=i * 0.07)
            self.play(LaggedStart(*[FadeIn(t, shift=DOWN * 1.2) for t in tildes], lag_ratio=0.02), run_time=0.6)
            tacha = Line(tildes.get_corner(DL) + DL * 0.2, tildes.get_corner(UR) + UR * 0.2, color=ROJO, stroke_width=12)
            self.sfx("impacto", gain=-10)
            self.play(Create(tacha), tildes.animate.set_color(INK), run_time=0.3)

            # «Es verificación»
            self.en("verificacion")
            ver = self.palabra("verificación", AMBAR, 1.5).move_to(UP * 3.15)
            self.play(FadeOut(VGroup(tildes, tacha)), run_time=0.2)
            self.pop(ver, sonido="impacto")

            # «todas las ejecuciones posibles» — el árbol entero, en verde
            self.en("todas")
            arbol = ArbolIntercalado(3, 3, ancho=8.0, alto=5.0, etiquetas=False, color_a=INK, color_b=INK,
                                     grosor=2.5).move_to(LEFT * 1.8 + DOWN * 0.7)
            self.play(FadeIn(arbol), run_time=0.3)
            aristas = sorted(arbol.aristas.values(), key=lambda a: a.linea.get_center()[0])
            self.play(LaggedStart(*[a.linea.animate.set_stroke(VERDE, 4) for a in aristas], lag_ratio=0.02),
                      run_time=1.0)

            # «demostrada una» — «1 vez»
            self.en("demostrada")
            vez = Text("1 vez", color=AMBAR, weight=BOLD).scale(1.1).move_to(RIGHT * 5.2 + UP * 0.2)
            self.pop(vez)

            # «con matemática» — el sello «demostrado»
            self.en("mate")
            s = sello("demostrado").scale(1.1).move_to(RIGHT * 5.2 + DOWN * 1.6)
            self.pop(s, sonido="impacto")

            # «mil veces» — una pila de casos de prueba
            self.en("mil")
            pila = VGroup(*[documento(INK).scale(0.8).shift(RIGHT * 0.12 * k + DOWN * 0.08 * k) for k in range(7)])
            pila.move_to(RIGHT * 5.2 + UP * 1.9)
            self.play(LaggedStart(*[FadeIn(d, shift=DOWN * 0.3) for d in pila], lag_ratio=0.1), run_time=0.5)

            # «sueltos» — se desparraman y se atenúan
            self.en("sueltos")
            rng = np.random.default_rng(4)
            self.play(*[d.animate.shift(RIGHT * rng.uniform(-1.0, 1.0) + UP * rng.uniform(-0.4, 0.5))
                        .rotate(rng.uniform(-0.8, 0.8)).fade(0.7) for d in pila], run_time=0.6)
