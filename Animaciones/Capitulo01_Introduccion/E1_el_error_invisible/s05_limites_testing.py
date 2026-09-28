"""Escena 05 — Por qué el testing no alcanza, y la frase de Dijkstra."""
from comun.escena import *


def grilla(filas: int, cols: int, sep: float = 0.5, r: float = 0.1, color=VERDE) -> VGroup:
    return VGroup(*[Dot(radius=r, color=color) for _ in range(filas * cols)]).arrange_in_grid(
        filas, cols, buff=sep - 2 * r)


class LimitesTesting(EscenaCurso):
    ESCENA = "05"
    ROJA = 58

    def construct(self):
        self.patron()
        self.dijkstra()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def patron(self):
        with self.narrar("b1"):
            self.en("comun")
            casos = VGroup(maquina_radioterapia(), cohete(), misil(), chip(AZUL))
            for c in casos:
                c.scale_to_fit_height(2.0)
            casos.arrange(RIGHT, buff=1.0)
            self.play(LaggedStart(*[FadeIn(c, shift=UP * 0.4) for c in casos], lag_ratio=0.15), run_time=0.7)

            self.en("inquietante")
            self.play(self.camera.frame.animate.set(width=config.frame_width * 0.8), run_time=1.5,
                      rate_func=rate_functions.linear)

            self.en("ninguno")
            self.play(LaggedStart(*[c.animate.set_color(INK).set_opacity(0.5) for c in casos], lag_ratio=0.25),
                      run_time=0.8)
            fila = casos.copy().set_opacity(1).scale(0.45).arrange(RIGHT, buff=1.0).to_edge(UP, buff=0.4)
            self.play(self.camera.frame.animate.set(width=config.frame_width),
                      Transform(casos, fila), run_time=0.6)

            self.en("entradas")
            g = grilla(7, 14).move_to(DOWN * 0.5)
            self.play(LaggedStart(*[GrowFromCenter(d) for d in g], lag_ratio=0.008), run_time=0.9)

            self.en("puntual")
            roja = g[self.ROJA]
            self.sfx("error", gain=-16)
            self.play(roja.animate.set_color(ROJO).scale(1.6), run_time=0.3)

            self.en("tiempos")
            rel = reloj().scale(0.45).next_to(g, LEFT, buff=0.35).align_to(roja, UP)
            flecha = Arrow(rel.get_right(), roja.get_left(), color=INK, stroke_width=2, buff=0.1)
            self.play(FadeIn(rel, scale=0.5), GrowArrow(flecha), run_time=0.4)
            self.play(Rotate(rel.minutero, -PI, about_point=rel.aro.get_center()), run_time=0.6)

            self.en("tipo")
            aro = Circle(radius=0.3, color=AMBAR, stroke_width=5).move_to(roja)
            self.play(Create(aro), FadeOut(flecha), FadeOut(rel), run_time=0.4)

            self.en("testing")
            self.play(FadeOut(aro), run_time=0.2)
            lp = lupa().scale(1.3)
            offset = lp.get_center() - lp.vidrio.get_center()
            lp.move_to(g[3].get_center() + offset)
            self.pop(lp)

            self.en("mil")
            cnt = Integer(0, color=VERDE, group_with_commas=False).scale(1.4)
            cnt.next_to(g, RIGHT, buff=0.35).shift(UP * 0.3)
            ok = tilde(VERDE, 0.5).next_to(cnt, DOWN, buff=0.2)
            self.play(FadeIn(cnt), FadeIn(ok), run_time=0.2)
            rng = np.random.default_rng(4)
            visitas = [int(i) for i in rng.choice([i for i in range(len(g)) if i != self.ROJA], 12, replace=False)]
            for k in range(12):
                self.sfx("tick", gain=-20, offset=k * 0.16)
            self.play(Succession(*[lp.animate(run_time=0.16).move_to(g[i].get_center() + offset) for i in visitas]),
                      ChangeDecimalToValue(cnt, 1000), run_time=1.9)

            self.en("escondido")
            for _ in range(2):
                self.sfx("latido", gain=-14)
                self.play(Indicate(roja, color=ROJO, scale_factor=2.0), run_time=0.6)

            self.en("combinacion")
            flash = FullScreenRectangle().set_fill(ROJO, 0.3).set_stroke(width=0)
            self.play(lp.animate.move_to(roja.get_center() + offset), run_time=0.35)
            self.sfx("impacto", gain=-8)
            self.play(FadeIn(flash, rate_func=there_and_back), Flash(roja, color=ROJO, flash_radius=0.8),
                      run_time=0.5)
            self.remove(flash)

    # ------------------------------------------------------------------ b2
    def dijkstra(self):
        with self.narrar("b2"):
            self.en("dijkstra")
            self.corte()
            nombre = Text("Edsger Dijkstra", color=TXT, weight=BOLD).scale(1.3).move_to(UP * 2.8)
            self.play(Write(nombre), run_time=0.6)

            self.en("fundo")
            ideas = VGroup(*[caja(t, c, ancho=3.4, alto=1.0, scale=0.6, relleno=0.18)
                             for t, c in (("sección crítica", AZUL), ("semáforo", VERDE), ("deadlock", ROJO))])
            ideas.arrange(RIGHT, buff=0.5).move_to(DOWN * 0.2)
            for i in range(3):
                self.sfx("pop", offset=i * 0.2)
            self.play(LaggedStart(*[GrowFromCenter(x) for x in ideas], lag_ratio=0.3), run_time=0.8)

            self.en("dijo")
            comillas = Text("“ ”", color=AMBAR, weight=BOLD).scale(4).move_to(DOWN * 0.2)
            self.play(FadeOut(ideas, shift=DOWN * 0.4), FadeIn(comillas, scale=0.5), run_time=0.45)

            self.en("veces")
            for _ in range(3):
                eco = comillas.copy()
                self.play(eco.animate.scale(1.6).set_opacity(0), run_time=0.3)
                self.remove(eco)
            self.play(FadeOut(comillas), run_time=0.2)

            self.en("presencia")
            l1 = Text("El testing puede mostrar la presencia de errores,", color=TXT).scale(0.62)
            l1.next_to(nombre, DOWN, buff=0.6)
            g1 = grilla(4, 6, sep=0.45).move_to(LEFT * 3.4 + DOWN * 1.6)
            g1[9].set_color(ROJO)
            lp = lupa().scale(0.9)
            off = lp.get_center() - lp.vidrio.get_center()
            lp.move_to(g1[2].get_center() + off)
            self.play(FadeIn(l1, shift=UP * 0.2), FadeIn(g1), FadeIn(lp), run_time=0.45)
            self.play(lp.animate.move_to(g1[9].get_center() + off), run_time=0.45)
            self.sfx("exito")
            self.play(Indicate(g1[9], color=ROJO, scale_factor=2.2), run_time=0.45)

            self.en("ausencia")
            l2 = Text("pero nunca puede demostrar su ausencia.", color=AMBAR, weight=BOLD).scale(0.62)
            l2.next_to(l1, DOWN, buff=0.25)
            g2 = grilla(4, 6, sep=0.45).move_to(RIGHT * 3.4 + DOWN * 1.6)
            lp2 = lupa().scale(0.9).next_to(g2, UP, buff=0.1).shift(LEFT * 0.6)
            duda = Text("?", color=AMBAR, weight=BOLD).scale(1.6).next_to(lp2, RIGHT, buff=0.25)
            self.play(FadeIn(l2, shift=UP * 0.2), FadeIn(g2), FadeIn(lp2), run_time=0.45)
            self.pop(duda)

            self.en("noalcanza")
            self.sfx("error", gain=-14)
            self.play(Create(tachar(lp2)), run_time=0.35)

            self.en("quehacemos")
            self.corte()
            q = Text("¿Qué hacemos?", color=AMBAR, weight=BOLD).scale(1.6)
            self.pop(q, sonido="impacto")

            self.en("modelos")
            self.play(q.animate.scale(0.5).to_edge(UP, buff=0.4), run_time=0.4)
            centro = DOWN * 0.4
            pos = [centro + 2.3 * np.array([np.cos(a), np.sin(a), 0])
                   for a in np.linspace(PI / 2, PI / 2 - 2 * PI, 6, endpoint=False)]
            nodos = VGroup(*[Circle(radius=0.38, color=AZUL, stroke_width=4).set_fill(FONDO, 1).move_to(p)
                             for p in pos])
            aristas = [(0, 1), (1, 2), (2, 3), (3, 4), (4, 5), (5, 0), (1, 4), (2, 5)]
            flechas = VGroup(*[Arrow(nodos[a].get_center(), nodos[b].get_center(), buff=0.42,
                                     color=INK, stroke_width=3, max_tip_length_to_length_ratio=0.1)
                               for a, b in aristas])
            self.sfx("whoosh")
            self.play(LaggedStart(*[GrowFromCenter(n) for n in nodos], lag_ratio=0.1),
                      LaggedStart(*[GrowArrow(f) for f in flechas], lag_ratio=0.08), run_time=1.0)

            self.en("recorrer")
            punto = Dot(nodos[0].get_center(), radius=0.14, color=AMBAR)
            self.add(punto)
            pasos = [AnimationGroup(punto.animate.move_to(nodos[i]),
                                    nodos[i].animate.set_fill(VERDE, 0.6).set_color(VERDE)) for i in range(6)]
            for i in range(6):
                self.sfx("tick", gain=-18, offset=i * 0.3)
            self.play(Succession(*pasos), run_time=1.8)

            self.en("imposible")
            malo = Circle(radius=0.38, color=ROJO, stroke_width=4).set_fill(ROJO, 0.4).move_to(RIGHT * 5 + DOWN * 0.4)
            self.pop(malo, sonido="error")
            self.wait(0.3)
            self.sfx("exito")
            self.play(Create(tachar(malo)), run_time=0.4)

            self.en("viene")
            self.play(self.camera.frame.animate.set(width=config.frame_width * 1.25),
                      *[n.animate.set_fill(VERDE, 0.9) for n in nodos], run_time=1.0)
            self.play(Indicate(VGroup(nodos, flechas), color=VERDE, scale_factor=1.05), run_time=0.7)
        self.play(self.camera.frame.animate.set(width=config.frame_width), run_time=0.3)
