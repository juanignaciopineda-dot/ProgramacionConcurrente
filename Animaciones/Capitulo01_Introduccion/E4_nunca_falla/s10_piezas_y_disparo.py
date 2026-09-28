"""Escena 10 — Las piezas de una red de Petri y la regla de disparo."""
from comun.escena import *


def lugar(etiqueta: str, fichas: int = 0, radio: float = 0.95) -> Lugar:
    l = Lugar(etiqueta, fichas, radio=radio, color=INK, color_fichas=TXT)
    l.etiqueta.scale(1.6).next_to(l.circulo, DOWN, buff=0.2)
    return l


def transicion(etiqueta: str, alto: float = 1.6) -> Transicion:
    t = Transicion(etiqueta, ancho=0.38, alto=alto)
    t.etiqueta.scale(1.6).next_to(t.barra, DOWN, buff=0.2)
    return t


def centrar(m: Mobject, punto) -> Mobject:
    """Mueve un lugar o transición para que su círculo/barra quede en `punto`."""
    ancla = m.circulo if isinstance(m, Lugar) else m.barra
    return m.shift(np.array(punto) - ancla.get_center())


def arco(a: Mobject, b: Mobject, color=INK) -> Arrow:
    return Arrow(a.get_center(), b.get_center(), color=color, stroke_width=5, buff=0.95,
                 max_tip_length_to_length_ratio=0.1)


class PiezasYDisparo(EscenaCurso):
    ESCENA = "10"

    def construct(self):
        self.piezas()
        self.regla()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def piezas(self):
        with self.narrar("b1"):
            # «muy pocas piezas» — cuatro huecos
            self.en("piezas")
            huecos = VGroup(*[DashedVMobject(RoundedRectangle(width=2.9, height=2.9, corner_radius=0.25),
                                             num_dashes=34).set_stroke(INK, 3) for _ in range(4)])
            huecos.arrange(RIGHT, buff=0.35).move_to(UP * 0.5)
            self.play(Create(huecos), run_time=0.45)

            # «Los lugares» — un círculo en el primer hueco
            self.en("lugar")
            L = centrar(lugar("recurso libre", radio=0.9), huecos[0].get_center() + UP * 0.3)
            self.pop(L.circulo)

            # «una condición»
            self.en("condicion")
            c1 = Text("condición", color=INK, weight=BOLD).scale(0.75).next_to(huecos[0], DOWN, buff=0.35)
            self.play(FadeIn(c1, shift=UP * 0.2), run_time=0.3)

            # «el recurso está libre»
            self.en("libre")
            self.play(FadeIn(L.etiqueta), run_time=0.3)

            # «Las transiciones» — una barra en el segundo hueco
            self.en("transicion")
            T = centrar(transicion("tomar recurso", alto=1.5), huecos[1].get_center() + UP * 0.3)
            self.pop(T.barra)

            # «un evento»
            self.en("evento")
            c2 = Text("evento", color=INK, weight=BOLD).scale(0.75).next_to(huecos[1], DOWN, buff=0.35)
            self.play(FadeIn(c2, shift=UP * 0.2), run_time=0.3)

            # «toma el recurso»
            self.en("toma")
            self.play(FadeIn(T.etiqueta), run_time=0.3)

            # «Los arcos» — lugar y transición se ordenan en fila; un arco los une
            self.en("arcos")
            self.play(FadeOut(huecos), FadeOut(c1), FadeOut(c2),
                      L.animate.shift(LEFT * 4.4 + UP * 0.6 - L.circulo.get_center()),
                      T.animate.shift(UP * 0.6 - T.barra.get_center()),
                      run_time=0.5)
            a1 = arco(L.circulo, T.barra)
            self.play(GrowArrow(a1), run_time=0.4)

            # «transiciones con lugares» — otro arco hacia un lugar nuevo
            self.en("vuelta")
            L2 = centrar(lugar("recurso tomado", radio=0.9), RIGHT * 4.4 + UP * 0.6)
            a2 = arco(T.barra, L2.circulo)
            self.play(FadeIn(L2, scale=0.6), GrowArrow(a2), run_time=0.5)

            # «qué necesita» — el arco de entrada
            self.en("necesita")
            self.play(a1.animate.set_color(AMBAR).set_stroke(width=8), run_time=0.35)

            # «produce» — el arco de salida
            self.en("produce")
            self.play(a1.animate.set_color(INK).set_stroke(width=5), a2.animate.set_color(AMBAR).set_stroke(width=8),
                      run_time=0.35)

            # «las fichas» — cae una ficha en el primer lugar
            self.en("fichas")
            self.play(a2.animate.set_color(INK).set_stroke(width=5), run_time=0.2)
            self.sfx("tick", gain=-10)
            soltar_ficha(self, L, run_time=0.45)

            # «viven dentro de los lugares» — rebota dentro del círculo
            self.en("viven")
            self.play(L.puntos.animate(rate_func=there_and_back).shift(UP * 0.35), run_time=0.45)

            # «se cumple» — «recurso libre» se pone verde
            self.en("cumple")
            self.play(L.etiqueta.animate.set_color(VERDE), run_time=0.3)

            # «ahora mismo» — la ficha brilla
            self.en("ahora")
            self.sfx("exito", gain=-18)
            self.play(L.puntos.animate(rate_func=there_and_back).scale(1.8).set_color(AMBAR), run_time=0.5)

            # «Al conjunto completo» — una red más grande, con fichas
            self.en("conjunto")
            self.corte()
            red = RedMutex().scale(0.95).move_to(DOWN * 0.35)
            self.play(FadeIn(red), run_time=0.45)

            # «marcado»
            self.en("marcado")
            pal = self.palabra("marcado", AMBAR, 1.2).move_to(UP * 3.45)
            self.pop(pal, sonido="impacto")

            # «la foto» — un marco con flash
            self.en("foto")
            marco = SurroundingRectangle(red, color=TXT, buff=0.25, corner_radius=0.2, stroke_width=6)
            flash = FullScreenRectangle(stroke_width=0).set_fill(WHITE, 0)
            self.add(flash)
            self.sfx("tick", gain=-8)
            self.play(Create(marco), flash.animate(rate_func=there_and_back).set_fill(WHITE, 0.5), run_time=0.35)
            self.remove(flash)

    # ------------------------------------------------------------------ b2
    def trio(self, fichas: int = 1) -> tuple:
        L1 = centrar(lugar("entrada", fichas), LEFT * 4.4)
        T = centrar(transicion("t"), ORIGIN)
        L2 = centrar(lugar("salida"), RIGHT * 4.4)
        a1, a2 = arco(L1.circulo, T.barra), arco(T.barra, L2.circulo)
        return L1, T, L2, a1, a2

    def regla(self):
        with self.narrar("b2"):
            # «una sola regla» — lugar → transición → lugar
            self.en("regla")
            self.corte()
            L1, T, L2, a1, a2 = self.trio()
            self.play(FadeIn(VGroup(L1, T, L2)), GrowArrow(a1), GrowArrow(a2), run_time=0.5)

            # «disparo» — el rótulo
            self.en("rotulo")
            rot = self.palabra("regla de disparo", AMBAR, 1.2).move_to(UP * 2.9)
            self.pop(rot, sonido="impacto")

            # «está habilitada» — la transición se enciende
            self.en("habilitada")
            hab = Text("habilitada", color=VERDE, weight=BOLD).scale(0.75).next_to(T.barra, UP, buff=0.3)
            self.play(T.barra.animate.set_color(VERDE), run_time=0.3)
            self.pop(hab)

            # «al menos, una ficha» — la ficha de entrada destella
            self.en("almenos")
            self.play(L1.puntos.animate(rate_func=there_and_back).scale(1.8), run_time=0.5)

            # «consume esas» — la ficha entra en la transición
            self.en("consume")
            f = L1.ficha_suelta()
            L1.poner_fichas(0)
            self.add(f)
            self.play(f.animate.move_to(T.barra), run_time=0.5, rate_func=rate_functions.ease_in_quad)

            # «produce fichas nuevas» — sale por el arco
            self.en("nuevas")
            self.sfx("whoosh", gain=-18)
            self.play(f.animate.move_to(a2.get_center()), run_time=0.5)

            # «los lugares de salida» — aterriza en el lugar de salida
            self.en("salidas")
            self.play(f.animate.move_to(L2.circulo), T.barra.animate.set_color(INK), run_time=0.35)
            self.remove(f)
            L2.poner_fichas(1)
            self.sfx("pop")
            self.play(L2.puntos.animate(rate_func=there_and_back).scale(1.6), run_time=0.35)

            # «instantáneo y atómico» — flash; «atómico»
            self.en("atomico")
            flash = FullScreenRectangle(stroke_width=0).set_fill(WHITE, 0)
            self.add(flash)
            at = Text("atómico", color=AMBAR, weight=BOLD).scale(0.85).move_to(hab)
            self.play(flash.animate(rate_func=there_and_back).set_fill(WHITE, 0.45), FadeOut(hab), run_time=0.3)
            self.remove(flash)
            self.pop(at)

            # «ojo con este detalle» — la red otra vez, habilitada
            self.en("ojo")
            self.corte()
            L1, T, L2, a1, a2 = self.trio()
            T.barra.set_color(VERDE)
            self.play(FadeIn(VGroup(L1, T, L2, a1, a2)), run_time=0.4)

            # «no obliga» — sigue verde pero no dispara; tick de reloj
            self.en("noobliga")
            rj = reloj(TXT).scale(1.0).next_to(T.barra, UP, buff=0.4)
            self.play(FadeIn(rj, scale=0.5), run_time=0.2)
            self.sfx("tick", gain=-10)
            self.play(Rotate(rj.minutero, angle=-2 * PI, about_point=rj.aro.get_center()), run_time=1.0)

            # «Justo ahí» — la transición late, en espera
            self.en("justo")
            self.play(T.barra.animate(rate_func=there_and_back).scale(1.3), run_time=0.5)

            # «el no determinismo» — dos transiciones habilitadas comparten una ficha
            self.en("nodet")
            self.corte()
            L = centrar(lugar("P", 1), LEFT * 4.0 + DOWN * 0.3)
            Ta = centrar(transicion("a", alto=1.2), RIGHT * 0.5 + UP * 1.5)
            Tb = centrar(transicion("b", alto=1.2), RIGHT * 0.5 + DOWN * 2.1)
            La = centrar(lugar("A"), RIGHT * 4.6 + UP * 1.5)
            Lb = centrar(lugar("B"), RIGHT * 4.6 + DOWN * 2.1)
            for t in (Ta, Tb):
                t.barra.set_color(VERDE)
            arcs = VGroup(arco(L.circulo, Ta.barra), arco(L.circulo, Tb.barra),
                          arco(Ta.barra, La.circulo), arco(Tb.barra, Lb.circulo))
            q = Text("?", color=AMBAR, weight=BOLD).scale(1.3).move_to(RIGHT * 0.5 + DOWN * 0.3)
            self.play(FadeIn(VGroup(L, Ta, Tb, La, Lb, arcs)), run_time=0.45)
            self.play(FadeIn(q), run_time=0.15)
            self.play(q.animate(rate_func=there_and_back).shift(UP * 0.8), run_time=0.45)
            self.play(q.animate(rate_func=there_and_back).shift(DOWN * 0.8), run_time=0.45)

            # «concurrencia de verdad» — cae el dado; dispara una
            self.en("verdad")
            dd = dado(TXT).scale(0.9).move_to(LEFT * 5.6 + UP * 2.6)
            self.play(FadeIn(dd, shift=DOWN * 0.5), Rotate(dd, angle=PI / 2), FadeOut(q), run_time=0.4)
            self.sfx("tick", gain=-10)
            disparar_viaje(self, Ta, [L], [La], run_time=0.8)
