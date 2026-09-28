"""Escena 04 — Condición de carrera: no fue un bug, fue el orden."""
from comun.escena import *

MALA = [("H1 lee 0", AZUL), ("H2 lee 0", MAGENTA), ("H1 escribe 1", AZUL), ("H2 escribe 1", MAGENTA)]
BUENA = [("H1 lee 0", AZUL), ("H1 escribe 1", AZUL), ("H2 lee 1", MAGENTA), ("H2 escribe 2", MAGENTA)]
PASOS = [("LEER", AZUL), ("SUMAR", AMBAR), ("ESCRIBIR", VERDE)]


def rebobinar_icono(color=TXT) -> VGroup:
    t = Triangle(color=color).set_fill(color, 1).set_stroke(width=0).rotate(PI / 2).scale(0.45)
    return VGroup(t, t.copy()).arrange(RIGHT, buff=-0.15)


class CondicionCarrera(EscenaCurso):
    ESCENA = "04"

    def construct(self):
        self.no_fue_un_bug()
        self.rebobinar()
        self.principio()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def no_fue_un_bug(self):
        with self.narrar("b1"):
            # «El resultado final»
            self.en("final")
            res = Text("contador = 1", color=TXT, weight=BOLD, t2c={"1": ROJO}).scale(1.5).move_to(UP * 1.4)
            self.pop(res, sonido="impacto")

            # «sumar dos veces»
            self.en("dosveces")
            cuenta = Text("0 + 1 + 1", color=TXT, weight=BOLD).scale(1.2).move_to(DOWN * 0.4 + LEFT * 0.9)
            self.play(FadeIn(cuenta, shift=UP * 0.3), run_time=0.35)

            # «desde cero»
            self.en("desde")
            dos = Text("= 2", color=VERDE, weight=BOLD).scale(1.2).next_to(cuenta, RIGHT, buff=0.35)
            self.play(FadeIn(dos, shift=LEFT * 0.3), run_time=0.35)

            # «valga la pena de estudiar»
            self.en("estudiar")
            eq = VGroup(res, cuenta, dos)
            self.play(eq.animate.scale(0.55).move_to(UP * 2.6), run_time=0.45)
            lp = lupa().scale(1.3).move_to(eq.get_right() + RIGHT * 0.9 + DOWN * 0.1)
            self.play(FadeIn(lp, shift=LEFT * 0.4), run_time=0.35)

            # «no fue un bug»
            self.en("bug")
            b = bicho(AMBAR).scale(2.2).move_to(DOWN * 1.1)
            self.pop(b)

            # «algo se rompió»
            self.en("roto")
            x = cruz(ROJO, 2.6).move_to(b)
            self.sfx("error", gain=-14)
            self.play(Create(x), run_time=0.35)

            # «perfectamente válida» — la línea de pasos de la escena anterior, con tilde
            self.en("valida")
            self.corte()
            fila = fila_pasos(MALA, y=0.3, ancho=3.0)
            ok = tilde(VERDE, 1.0).next_to(fila, UP, buff=0.4).align_to(fila, RIGHT)
            self.play(LaggedStart(*[FadeIn(p, shift=UP * 0.2) for p in fila], lag_ratio=0.15), run_time=0.5)
            self.play(Create(ok), run_time=0.3)
            self.sfx("exito", gain=-16)

            # «definición de interleaving»
            self.en("interleaving")
            pal = self.palabra("interleaving", AMBAR, 1.1).move_to(UP * 2.6)
            self.pop(pal)

            # «el episodio anterior» — el árbol de E2, con un camino encendido
            self.en("anterior")
            arbol = ArbolIntercalado(2, 2, ancho=5.5, alto=2.4, etiquetas=False).move_to(DOWN * 2.3)
            self.play(FadeIn(arbol), run_time=0.4)
            hoja = arbol.hojas[1]
            self.play(*[a.linea.animate.set_stroke(AMBAR, 7) for a in arbol.camino(hoja)], run_time=0.6)

            # «ningún error de programación»
            self.en("codigo")
            self.corte()
            c1 = caja("contador += 1", AZUL, ancho=4.2, alto=1.1, scale=0.7).move_to(LEFT * 3.5 + UP * 1.6)
            c2 = caja("contador += 1", MAGENTA, ancho=4.2, alto=1.1, scale=0.7).move_to(RIGHT * 3.5 + UP * 1.6)
            e1 = Text("H1", color=AZUL, weight=BOLD).scale(0.6).next_to(c1, UP, buff=0.2)
            e2 = Text("H2", color=MAGENTA, weight=BOLD).scale(0.6).next_to(c2, UP, buff=0.2)
            self.play(FadeIn(VGroup(c1, e1), shift=RIGHT), FadeIn(VGroup(c2, e2), shift=LEFT), run_time=0.45)

            # «por separado»
            self.en("separado")
            t1 = tilde(VERDE, 0.8).next_to(c1, DOWN, buff=0.3)
            t2 = tilde(VERDE, 0.8).next_to(c2, DOWN, buff=0.3)
            self.play(Create(t1), Create(t2), run_time=0.35)

            # «el orden de las acciones fue ese»
            self.en("ese")
            fila = fila_pasos(MALA, y=-2.2, ancho=3.0)
            self.play(LaggedStart(*[FadeIn(p, shift=UP * 0.2) for p in fila], lag_ratio=0.15), run_time=0.5)
            marco = SurroundingRectangle(fila, color=AMBAR, buff=0.18, corner_radius=0.12, stroke_width=5)
            self.play(Create(marco), run_time=0.4)

    # ------------------------------------------------------------------ b2
    def rebobinar(self):
        with self.narrar("b2"):
            # «rebobinemos»
            self.en("rebobinar")
            todo = Group(*[m for m in self.mobjects if m is not self.camera.frame])
            icono = rebobinar_icono().scale(1.8)
            self.sfx("whoosh", gain=-10)
            self.play(FadeOut(todo, shift=RIGHT * 3), FadeIn(icono, scale=0.6), run_time=0.5)
            self.corte(icono)
            m = montaje_contador("0")
            self.play(FadeOut(icono, scale=1.4), FadeIn(m, shift=RIGHT * 0.5), run_time=0.4)

            # «Mismos dos hilos»
            self.en("mismos")
            self.resaltar(m.h1.caja, m.h2.caja, color=AMBAR, escala=1.15)

            # «mismo código»
            self.en("mismocodigo")
            mas1 = Text("+1", color=AZUL, weight=BOLD).scale(1.3).next_to(m.h1, UP, buff=0.3)
            mas2 = Text("+1", color=MAGENTA, weight=BOLD).scale(1.3).next_to(m.h2, UP, buff=0.3)
            self.pop(mas1, mas2)

            # «termina sus tres pasos» — las casillas de H1 en ráfaga
            self.en("h1pasos")
            col = VGroup(*[caja(t, c, ancho=2.2, alto=0.72, scale=0.5, relleno=0.15) for t, c in PASOS]
                         ).arrange(DOWN, buff=0.2).move_to(LEFT * 1.9 + DOWN * 0.9)
            self.play(ReplacementTransform(mas1, col), run_time=0.4)
            for i in range(3):
                self.sfx("tick", gain=-14, offset=i * 0.22)
            self.play(LaggedStart(*[p[0].animate(rate_func=there_and_back).set_fill(PASOS[i][1], 0.8)
                                    for i, p in enumerate(col)], lag_ratio=0.5), run_time=0.8)

            # «lee cero»
            self.en("lee0")
            v1 = m.celda.contenido.copy()
            self.sfx("whoosh", gain=-16)
            self.play(v1.animate.move_to(m.h1.registro.caja).set_color(AZUL), run_time=0.45)

            # «escribe uno»
            self.en("esc1")
            uno = Text("1", color=AZUL, weight=BOLD).scale(0.75).move_to(m.h1.registro.caja)
            self.play(ReplacementTransform(v1, uno), run_time=0.2)
            celda1 = Text("1", color=TXT).scale(0.64).move_to(m.celda.caja)
            viajero = uno.copy()
            self.play(viajero.animate.move_to(m.celda.caja), FadeOut(m.celda.contenido), run_time=0.4)
            self.remove(viajero)
            self.add(celda1)

            # «el hilo dos lee uno»
            self.en("lee1")
            v2 = celda1.copy()
            self.sfx("whoosh", gain=-16)
            self.play(v2.animate.move_to(m.h2.registro.caja).set_color(MAGENTA), FadeOut(mas2), run_time=0.45)

            # «y escribe dos»
            self.en("esc2")
            dos = Text("2", color=MAGENTA, weight=BOLD).scale(0.75).move_to(m.h2.registro.caja)
            self.play(ReplacementTransform(v2, dos), run_time=0.2)
            celda2 = Text("2", color=TXT).scale(0.64).move_to(m.celda.caja)
            viajero = dos.copy()
            self.play(viajero.animate.move_to(m.celda.caja), FadeOut(celda1), run_time=0.4)
            self.remove(viajero)
            self.add(celda2)

            # «Contador vale dos.»
            self.en("vale2")
            self.sfx("exito", gain=-12)
            self.play(m.celda.caja.animate.set_stroke(VERDE, 6).set_fill(VERDE, 0.2), celda2.animate.set_color(VERDE),
                      Flash(m.celda.caja, color=VERDE), run_time=0.45)

            # «igual de válidas» — las dos ejecuciones, una encima de otra
            self.en("ambas")
            self.corte()
            mala = fila_pasos(MALA, y=1.2, ancho=2.3).shift(LEFT * 1.0)
            buena = fila_pasos(BUENA, y=-0.8, ancho=2.3).shift(LEFT * 1.0)
            r1 = Text("= 1", color=ROJO, weight=BOLD).scale(0.8).next_to(mala, RIGHT, buff=0.35)
            r2 = Text("= 2", color=VERDE, weight=BOLD).scale(0.8).next_to(buena, RIGHT, buff=0.35)
            k1 = tilde(VERDE, 0.6).next_to(r1, RIGHT, buff=0.3)
            k2 = tilde(VERDE, 0.6).next_to(r2, RIGHT, buff=0.3)
            self.play(FadeIn(VGroup(mala, r1), shift=DOWN * 0.3), FadeIn(VGroup(buena, r2), shift=UP * 0.3),
                      run_time=0.4)
            self.play(Create(k1), Create(k2), run_time=0.3)

            # «lo único que cambió» — las casillas que están en otro lugar
            self.en("cambio")
            self.resaltar(mala[1], mala[2], buena[1], buena[2], color=AMBAR, escala=1.12)

            # «fue el orden»
            self.en("orden")
            pal = self.palabra("orden", AMBAR, 1.5).move_to(DOWN * 2.7)
            self.pop(pal, sonido="impacto")

    # ------------------------------------------------------------------ b3
    def principio(self):
        with self.narrar("b3"):
            # «condición de carrera»
            self.en("nombre")
            self.corte()
            nombre = self.palabra("condición de carrera", AMBAR, 1.5)
            self.pop(nombre, sonido="impacto")

            # «el principio» — el nombre sube; debajo, una pista
            self.en("principio")
            p = pista(ancho=12.0, carriles=4, alto_carril=0.95).move_to(DOWN * 0.9)
            self.play(nombre.animate.scale(0.6).to_edge(UP, buff=0.4), FadeIn(p), run_time=0.5)

            # «todo programa concurrente» — ventanas sobre la pista
            self.en("todo")
            colores = [AZUL, MAGENTA, CIAN, VERDE]
            ventanas = VGroup(*[ventana_app("programa", c).scale(0.55) for c in colores]).arrange(RIGHT, buff=0.35)
            ventanas.move_to(UP * 1.9 + LEFT * 3.2)
            self.play(LaggedStart(*[FadeIn(v, shift=DOWN * 0.3) for v in ventanas], lag_ratio=0.15), run_time=0.5)

            # «en adelante»
            self.en("adelante")
            self.play(ventanas.animate.shift(RIGHT * 6.2), run_time=0.8, rate_func=rate_functions.ease_in_out_sine)

            # «no puede depender» — H1 y H2 en la salida
            self.en("depender")
            self.play(FadeOut(ventanas), run_time=0.2)
            c1 = persona(AZUL).scale(0.85).move_to(p.carril(0) + RIGHT * 0.3)
            c2 = persona(MAGENTA).scale(0.85).move_to(p.carril(1) + RIGHT * 0.3)
            self.play(FadeIn(c1, shift=RIGHT * 0.3), FadeIn(c2, shift=RIGHT * 0.3), run_time=0.35)

            # «lo rápido que corre» — uno acelera, el otro se queda atrás
            self.en("rapido")
            self.sfx("whoosh", gain=-14)
            self.play(c1.animate.shift(RIGHT * 6.5), c2.animate.shift(RIGHT * 2.8),
                      run_time=1.4, rate_func=rate_functions.ease_in_quad)

            # «respecto de los demás» — dos corredores más
            self.en("demas")
            c3 = persona(INK).scale(0.85).move_to(p.carril(2) + RIGHT * 0.3)
            c4 = persona(INK).scale(0.85).move_to(p.carril(3) + RIGHT * 0.3)
            self.play(FadeIn(c3, shift=RIGHT * 0.3), FadeIn(c4, shift=RIGHT * 0.3), run_time=0.35)
            self.play(c3.animate.shift(RIGHT * 4.0), c4.animate.shift(RIGHT * 1.8), run_time=1.0)

            # «Si el resultado cambia» — sobre la meta, un marcador que salta entre 1 y 2
            self.en("cambia")
            marcador = Text("1", color=ROJO, weight=BOLD).scale(1.0).next_to(p.meta, UP, buff=0.25)
            self.pop(marcador)
            for txt, col in [("2", VERDE), ("1", ROJO), ("2", VERDE)]:
                nuevo = Text(txt, color=col, weight=BOLD).scale(1.0).move_to(marcador)
                self.sfx("tick", gain=-16)
                self.play(Transform(marcador, nuevo), run_time=0.25)

            # «la carrera a quién» — uno cruza la meta primero
            self.en("gana")
            self.play(c1.animate.move_to([p.meta.get_x() + 0.6, c1.get_y(), 0]), run_time=0.4)
            b = bandera().scale(0.9).next_to(marcador, RIGHT, buff=0.4)
            self.pop(b)

            # «el programa está mal»
            self.en("mal")
            x = cruz(ROJO, 3.0).move_to(DOWN * 0.9)
            self.sfx("error", gain=-12)
            self.play(Create(x), run_time=0.35)

            # «aunque compile»
            self.en("compila")
            comp = VGroup(tilde(VERDE, 0.5), Text("compila", color=VERDE).scale(0.55)).arrange(RIGHT, buff=0.2)
            comp.move_to(DOWN * 3.5)
            self.play(FadeIn(comp, shift=UP * 0.2), run_time=0.3)

            # «la mayoría de las veces» — veinte ejecuciones, una roja
            self.en("mayoria")
            self.corte()
            g = grilla_ejecuciones(20, malas=(13,)).scale(1.9).move_to(DOWN * 0.2)
            self.play(LaggedStart(*[FadeIn(c, scale=0.5) for c in g.celdas], lag_ratio=0.04), run_time=0.7)

            # «funcione bien» — las verdes brillan, la roja parpadea
            self.en("bien")
            verdes = VGroup(*[c for c in g.celdas if c not in g.malas])
            self.play(verdes.animate.set_fill(VERDE, 0.7), run_time=0.3)
            for _ in range(2):
                self.sfx("alarma", gain=-20)
                self.play(g.malas.animate(rate_func=there_and_back).scale(1.4).set_fill(ROJO, 1), run_time=0.5)
