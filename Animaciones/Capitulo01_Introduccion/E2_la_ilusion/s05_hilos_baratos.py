"""Escena 05 — Exclusión mutua, y por qué un hilo sale tan barato."""
from comun.escena import *

X_ROT, X_P, X_H = -4.7, -0.4, 4.0          # columnas: rótulo de fila, proceso, hilo
Y_FILAS = (1.6, -0.3, -2.2)
IZQ_P, IZQ_H = -2.1, 2.1                   # donde arrancan las barras de cada columna


class HilosBaratos(EscenaCurso):
    ESCENA = "05"

    def construct(self):
        self.exclusion()
        self.tabla()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def exclusion(self):
        with self.narrar("b1"):
            # «la idea que va a resonar»
            self.en("resonar")
            mem = RoundedRectangle(width=3.6, height=1.8, corner_radius=0.15, color=AMBAR, stroke_width=3)
            mem.set_fill(AMBAR, 0.1).move_to(UP * 0.4)
            x5 = Text("x = 5", color=TXT, weight=BOLD).scale(0.8).move_to(mem)
            h1 = persona(AZUL).scale(1.3).move_to([-4.6, -1.4, 0])
            h2 = persona(CIAN).scale(1.3).move_to([0, -2.7, 0])
            h3 = persona(MAGENTA).scale(1.3).move_to([4.6, -1.4, 0])
            self.play(FadeIn(mem), FadeIn(x5), *[FadeIn(h, shift=UP * 0.3) for h in (h1, h2, h3)], run_time=0.4)
            ondas = [Circle(radius=1.3, color=AMBAR, stroke_width=4).move_to(mem) for _ in range(3)]
            self.sfx("whoosh", gain=-20)
            self.play(LaggedStart(*[AnimationGroup(o.animate.scale(4).set_stroke(opacity=0)) for o in ondas],
                                  lag_ratio=0.3), run_time=1.1)
            self.remove(*ondas)

            # «cualquier hilo puede modificar»
            self.en("modifica")
            f1 = Arrow(h1.get_right(), mem.get_left(), color=AZUL, stroke_width=5, buff=0.15)
            x9 = Text("x = 9", color=AZUL, weight=BOLD).scale(0.8).move_to(mem)
            self.play(GrowArrow(f1), run_time=0.35)
            self.sfx("tick", gain=-14)
            self.play(Transform(x5, x9), run_time=0.35)

            # «los demás lo ven»
            self.en("ven")
            f2 = Arrow(mem.get_bottom(), h2.get_top(), color=AMBAR, stroke_width=4, buff=0.1)
            f3 = Arrow(mem.get_right(), h3.get_top() + UP * 0.1, color=AMBAR, stroke_width=4, buff=0.15)
            self.play(GrowArrow(f2), GrowArrow(f3), run_time=0.35)
            self.play(*[h.animate(rate_func=there_and_back).scale(1.15) for h in (h2, h3)], run_time=0.4)

            # «al instante»
            self.en("instante")
            flash = FullScreenRectangle().set_fill(TXT, 0.3).set_stroke(width=0)
            self.sfx("pop", gain=-16)
            self.play(FadeIn(flash, rate_func=there_and_back), run_time=0.25)
            self.remove(flash)

            # «algún mecanismo»: puerta con candado delante de la memoria
            self.en("mecanismo")
            puerta = SurroundingRectangle(mem, color=AMBAR, buff=0.35, corner_radius=0.1, stroke_width=6)
            cand = candado(AMBAR).scale(0.65).next_to(puerta, UP, buff=0.1)
            self.play(FadeOut(VGroup(f1, f2, f3)), Create(puerta), run_time=0.45)
            self.pop(cand, sonido="tick")

            # «ese acceso»: H1 entra; H2 rebota
            self.en("acceso")
            self.play(h1.animate.move_to(puerta.get_left() + LEFT * 0.55), run_time=0.35)
            self.sfx("error", gain=-18)
            self.play(Succession(h2.animate(run_time=0.3).move_to([0, -1.55, 0]),
                                 h2.animate(run_time=0.3, rate_func=rate_functions.ease_out_bounce)
                                 .move_to([0, -2.7, 0])), run_time=0.6)

            # «exclusión mutua»
            self.en("exclusion")
            ex = self.palabra("exclusión mutua", escala=1.1).move_to(UP * 3.1)
            self.pop(ex, sonido="impacto")

            # «capítulo tras capítulo»
            self.en("capitulos")
            resto = VGroup(mem, x5, h1, h2, h3, puerta, cand)
            caps = VGroup(*[caja(f"Cap. {i}", AMBAR, ancho=2.4, alto=1.1, scale=0.6) for i in (1, 2, 3)])
            caps.arrange(RIGHT, buff=0.5).move_to(DOWN * 1.0)
            self.play(FadeOut(resto), ex.animate.scale(1.15).move_to(UP * 1.2), run_time=0.45)
            self.play(LaggedStart(*[FadeIn(c, shift=UP * 0.5) for c in caps], lag_ratio=0.35), run_time=0.8)

    # ------------------------------------------------------------------ b2
    def tabla(self):
        with self.narrar("b2"):
            # «una buena noticia»
            self.en("noticia")
            self.corte()
            flash = FullScreenRectangle().set_fill(VERDE, 0.3).set_stroke(width=0)
            self.sfx("exito", gain=-14)
            self.play(FadeIn(flash, rate_func=there_and_back), run_time=0.4)
            self.remove(flash)
            barata = self.palabra("¡barata!", color=VERDE, escala=2.0)
            self.pop(barata)
            self.flotar(barata)

            # «sale barata»: la tabla
            self.en("tabla")
            cab_p = Text("proceso", color=AZUL, weight=BOLD).scale(0.7).move_to([X_P, 3.0, 0])
            cab_h = Text("hilo", color=CIAN, weight=BOLD).scale(0.7).move_to([X_H, 3.0, 0])
            lineas = VGroup(*[Line([-6.8, y, 0], [6.4, y, 0], color=INK, stroke_width=1.5) for y in (2.45, 0.65, -1.25)],
                            *[Line([x, 3.4, 0], [x, -3.2, 0], color=INK, stroke_width=1.5) for x in (-2.5, 1.8)])
            barata.clear_updaters()
            self.play(FadeOut(barata, scale=0.5), Create(lineas), run_time=0.4)
            self.pop(cab_p, cab_h)

            # «Crear un hilo»
            self.en("crear")
            self.rotulo("crear", 0)

            # «diez veces»
            self.en("diez")
            y = Y_FILAS[0]
            self.bp1 = self.barra(IZQ_P, y, 3.4, ROJO)
            self.bh1 = self.barra(IZQ_H, y, 0.34, CIAN)
            x10 = Text("×10", color=ROJO, weight=BOLD).scale(0.6).next_to(self.bp1, UP, buff=0.12)
            x10.align_to(self.bp1, RIGHT)
            self.play(GrowFromEdge(self.bh1, LEFT), run_time=0.2)
            self.play(GrowFromEdge(self.bp1, LEFT), run_time=0.7)
            self.pop(x10)

            # «un proceso entero»
            self.en("entero")
            self.play(self.bp1.animate(rate_func=there_and_back).stretch(1.6, 1), run_time=0.45)

            # «mucha menos información»: ficha PCB grande frente a una chica
            self.en("info")
            grande = VGroup(RoundedRectangle(width=2.4, height=2.7, corner_radius=0.12, color=AMBAR, stroke_width=3)
                            .set_fill(FONDO, 1))
            grande.add(Text("PCB", color=AMBAR, weight=BOLD).scale(0.5).move_to(grande[0].get_top() + DOWN * 0.35))
            grande.add(VGroup(*[Line(LEFT * 0.8, RIGHT * 0.8, color=INK, stroke_width=2.5) for _ in range(6)])
                       .arrange(DOWN, buff=0.22).move_to(grande[0].get_center() + DOWN * 0.3))
            grande.move_to([X_P, -1.1, 0])
            chica = RoundedRectangle(width=1.5, height=1.5, corner_radius=0.12, color=CIAN, stroke_width=3)
            chica.set_fill(FONDO, 1).move_to([X_H, -0.8, 0])
            self.play(FadeIn(grande, shift=UP * 0.3), FadeIn(chica, shift=UP * 0.3), run_time=0.45)

            # «ni bloque de control»
            self.en("nibloque")
            tacha = tachar(grande)
            self.sfx("error", gain=-18)
            self.play(Create(tacha), run_time=0.3)

            # «solo un contexto pequeño y una pila»
            self.en("pila")
            pc = Text("PC", color=AMBAR, weight=BOLD).scale(0.45).move_to(chica.get_center() + LEFT * 0.35)
            mini = VGroup(*[Rectangle(width=0.45, height=0.22, color=CIAN, stroke_width=2).set_fill(CIAN, 0.15)
                            for _ in range(3)]).arrange(DOWN, buff=0.04).move_to(chica.get_center() + RIGHT * 0.35)
            self.pop(pc, mini)

            # «Por la misma razón»: las fichas salen; la celda del hilo en verde
            self.en("razon")
            self.celdas = []
            self.play(FadeOut(VGroup(grande, tacha, chica, pc, mini)), run_time=0.3)
            self.celda(0)

            # «cambiar de contexto entre hilos»
            self.en("cambio")
            self.rotulo_doble("cambio de", "contexto", 1)

            # «casi gratis»
            self.en("gratis")
            y = Y_FILAS[1]
            bp2 = self.barra(IZQ_P, y, 3.4, ROJO)
            bh2 = self.barra(IZQ_H, y, 0.12, CIAN)
            self.play(GrowFromEdge(bp2, LEFT), GrowFromEdge(bh2, LEFT), run_time=0.6)

            # «el espacio de memoria»
            self.en("memoria")
            misma = Text("misma memoria", color=TXT).scale(0.5).move_to([X_H + 0.4, y, 0])
            self.play(FadeIn(misma, shift=LEFT * 0.2), run_time=0.3)
            self.celda(1)

            # «comunicarse entre ellos»
            self.en("comunicar")
            self.rotulo("comunicarse", 2)

            # «el sistema operativo»: en proceso, un rodeo por el SO
            self.en("so")
            y = Y_FILAS[2]
            a = Dot([X_P - 1.5, y - 0.35, 0], radius=0.12, color=AZUL)
            b = Dot([X_P + 1.5, y - 0.35, 0], radius=0.12, color=MAGENTA)
            so = caja("SO", INK, ancho=1.1, alto=0.55, scale=0.45).move_to([X_P, y + 0.45, 0])
            r1 = Arrow(a.get_center(), so.get_left(), color=ROJO, stroke_width=4, buff=0.12)
            r2 = Arrow(so.get_right(), b.get_center(), color=ROJO, stroke_width=4, buff=0.12)
            self.play(FadeIn(a), FadeIn(b), FadeIn(so), run_time=0.25)
            self.play(GrowArrow(r1), run_time=0.35)
            self.play(GrowArrow(r2), run_time=0.35)

            # «alcanza con leer»: en hilo, una flecha directa a la variable
            self.en("leer")
            h = Dot([IZQ_H + 0.3, y, 0], radius=0.12, color=CIAN)
            var = caja("x", AMBAR, ancho=0.8, alto=0.55, scale=0.5).move_to([X_H + 1.3, y, 0])
            directa = Arrow(h.get_center(), var.get_left(), color=VERDE, stroke_width=5, buff=0.12)
            self.play(FadeIn(h), FadeIn(var), run_time=0.2)
            self.play(GrowArrow(directa), run_time=0.35)

            # «una variable que ya comparten»
            self.en("variable")
            self.resaltar(var, color=VERDE, escala=1.2)
            self.celda(2)

    # ------------------------------------------------------------------ helpers
    def rotulo(self, txt: str, fila: int):
        t = Text(txt, color=TXT, weight=BOLD).scale(0.65).move_to([X_ROT, Y_FILAS[fila], 0])
        self.play(FadeIn(t, shift=RIGHT * 0.3), run_time=0.3)

    def rotulo_doble(self, a: str, b: str, fila: int):
        t = self.texto_centrado(a, b, escala=0.62).move_to([X_ROT, Y_FILAS[fila], 0])
        self.play(FadeIn(t, shift=RIGHT * 0.3), run_time=0.3)

    def barra(self, x0: float, y: float, ancho: float, color) -> Rectangle:
        return Rectangle(width=ancho, height=0.45, stroke_width=0).set_fill(color, 0.85).move_to(
            [x0 + ancho / 2, y, 0])

    def celda(self, fila: int):
        fondo = Rectangle(width=4.5, height=1.8, stroke_width=0).set_fill(VERDE, 0.13)
        fondo.move_to([X_H, Y_FILAS[fila] + (0.05 if fila == 0 else 0), 0]).set_z_index(-1)
        ok = tilde(VERDE, 0.45).move_to([6.0, Y_FILAS[fila] + 0.5, 0])
        self.sfx("exito", gain=-18)
        self.play(FadeIn(fondo), Create(ok), run_time=0.35)
