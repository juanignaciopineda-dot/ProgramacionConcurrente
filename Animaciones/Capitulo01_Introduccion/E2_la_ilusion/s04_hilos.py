"""Escena 04 — Hilos: compañeros de casa. Qué comparten y qué es propio de cada uno."""
from comun.escena import *

COLORES = (AZUL, CIAN, MAGENTA)


def flecha_pc(color=AMBAR) -> VMobject:
    return Triangle(color=color).set_fill(color, 1).rotate(-PI / 2).scale(0.14)


class Hilos(EscenaCurso):
    ESCENA = "04"

    def construct(self):
        self.lineas()
        self.comparten()
        self.propio()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def lineas(self):
        with self.narrar("b1"):
            # «Hasta aquí»: un proceso
            self.en("proceso")
            caja = RoundedRectangle(width=8.0, height=5.6, corner_radius=0.2, color=AZUL, stroke_width=3)
            caja.set_fill(AZUL, 0.06).move_to(DOWN * 0.2)
            rot = Text("proceso", color=TXT, weight=BOLD).scale(0.55).move_to(caja.get_top() + DOWN * 0.4)
            self.play(FadeIn(caja, scale=0.9), FadeIn(rot), run_time=0.45)

            # «una sola línea de ejecución»
            self.en("linea")
            xs = (-2.2, 0.0, 2.2)
            arriba, abajo = 1.7, -2.0
            self.lins, self.pcs = [], []
            l0 = Line([xs[0], arriba, 0], [xs[0], abajo, 0], color=COLORES[0], stroke_width=6)
            p0 = flecha_pc(COLORES[0]).next_to(l0.get_start(), LEFT, buff=0.1)
            self.add(p0)
            self.play(Create(l0), p0.animate.shift(DOWN * (arriba - abajo)), run_time=1.1, rate_func=linear)
            self.lins.append(l0)
            self.pcs.append(p0)

            # «Pero dentro»
            self.en("dentro")
            self.play(self.camera.frame.animate.set(width=11.5).move_to(DOWN * 0.2), run_time=0.6)

            # «puede convivir más de una»
            self.en("mas")
            nuevos = []
            for x, c in zip(xs[1:], COLORES[1:]):
                l = Line([x, arriba, 0], [x, abajo, 0], color=c, stroke_width=6)
                p = flecha_pc(c).next_to(l.get_start(), LEFT, buff=0.1)
                self.add(p)
                self.lins.append(l)
                self.pcs.append(p)
                nuevos += [Create(l), p.animate.shift(DOWN * (arriba - abajo))]
            self.play(*nuevos, run_time=1.0, rate_func=linear)

            # «cada una de esas líneas»
            self.en("lineas")
            for l in self.lins:
                self.sfx("tick", gain=-20)
                self.play(l.animate(rate_func=there_and_back).set_stroke(width=14), run_time=0.3)

            # «se le llama hilo»
            self.en("hilo")
            hilo = self.palabra("hilo", escala=1.6).move_to(UP * 0.1)
            fondo = BackgroundRectangle(hilo, color=FONDO, fill_opacity=1, buff=0.15)
            self.pop(VGroup(fondo, hilo), sonido="impacto")
            etis = VGroup(*[Text(f"H{i + 1}", color=c, weight=BOLD).scale(0.55).move_to([x, abajo - 0.4, 0])
                            for i, (x, c) in enumerate(zip(xs, COLORES))])
            self.wait(0.3)
            self.play(FadeOut(VGroup(fondo, hilo), scale=0.5), FadeIn(etis, shift=UP * 0.2), run_time=0.4)

            # «compañeros de casa»: la caja se vuelve casa y los hilos, personas
            self.en("casa")
            self.casa = casa(9.0, 4.4)
            self.casa.muros.move_to(DOWN * 0.9)
            self.casa.tejado.next_to(self.casa.muros, UP, buff=0)
            self.gente = VGroup(*[persona(c).scale(1.15).move_to([x * 1.45, -2.35, 0])
                                  for x, c in zip(xs, COLORES)])
            self.play(self.camera.frame.animate.set(width=config.frame_width).move_to(ORIGIN),
                      ReplacementTransform(caja, self.casa.muros), FadeOut(rot), FadeOut(etis),
                      FadeIn(self.casa.tejado, shift=DOWN * 0.3),
                      *[ReplacementTransform(VGroup(l, p), g) for l, p, g in zip(self.lins, self.pcs, self.gente)],
                      run_time=0.8)

            # «casi todo»: se acercan al centro
            self.en("todo")
            self.play(*[g.animate(rate_func=there_and_back).shift(RIGHT * 0.45 * np.sign(-g.get_x()) +
                                                                  UP * 0.15)
                        for g in self.gente], run_time=0.6)

    # ------------------------------------------------------------------ b2
    def comparten(self):
        with self.narrar("b2"):
            # «¿Qué comparten exactamente?»
            self.en("que")
            q = Text("?", color=AMBAR, weight=BOLD).scale(1.6).move_to(self.casa.tejado.get_center() + DOWN * 0.2)
            self.pop(q)

            # «Todo el espacio de memoria»
            self.en("memoria")
            self.mem = RoundedRectangle(width=3.6, height=2.0, corner_radius=0.15, color=AMBAR, stroke_width=3)
            self.mem.set_fill(AMBAR, 0.1).move_to(DOWN * 0.35)
            mem_t = Text("memoria", color=AMBAR).scale(0.38).move_to(self.mem.get_top() + DOWN * 0.25)
            self.play(FadeOut(q), GrowFromCenter(self.mem), FadeIn(mem_t), run_time=0.45)
            self.mem_g = VGroup(self.mem, mem_t)

            # «variables globales»
            self.en("globales")
            vars_ = VGroup(Text("x = 5", color=TXT).scale(0.5), Text("total = 0", color=TXT).scale(0.5))
            vars_.arrange(DOWN, buff=0.18).move_to(self.mem.get_center() + DOWN * 0.2)
            self.play(LaggedStart(*[FadeIn(v, shift=UP * 0.2) for v in vars_], lag_ratio=0.3), run_time=0.45)
            self.mem_g.add(vars_)
            self.vars = vars_

            # «los archivos abiertos»
            self.en("archivos")
            docs = VGroup(documento(TXT), documento(TXT)).arrange(RIGHT, buff=0.25).scale(0.95)
            docs.move_to([3.0, -0.3, 0])
            self.play(LaggedStart(*[FadeIn(d, shift=DOWN * 0.3) for d in docs], lag_ratio=0.3), run_time=0.45)

            # «la autenticación»
            self.en("auth")
            ll = llave(AMBAR).scale(0.95).move_to([-3.1, 0.2, 0])
            self.pop(ll)

            # «los permisos»
            self.en("permisos")
            esc = escudo(VERDE).scale(0.85).move_to([-3.1, -0.95, 0])
            self.pop(esc)
            self.extras = VGroup(docs, ll, esc)

            # «que los contiene»
            self.en("contiene")
            self.sfx("whoosh", gain=-20)
            self.resaltar(self.casa, color=AMBAR, escala=1.03, run_time=0.6)

            # «convivencia muy íntima»: los tres apuntan a la misma memoria
            self.en("intima")
            self.flechas = VGroup(*[Arrow(g.get_top(), self.mem.get_bottom() + RIGHT * (g.get_x() * 0.25),
                                          color=g[0].get_color(), stroke_width=4, buff=0.1)
                                    for g in self.gente])
            self.play(*[GrowArrow(f) for f in self.flechas], run_time=0.5)

    # ------------------------------------------------------------------ b3
    def propio(self):
        with self.narrar("b3"):
            # «Lo único»: lo compartido se atenúa y sube; los hilos se separan
            self.en("unico")
            xs = (-4.4, 0.0, 4.4)
            # atenuar con fade() sobre una copia: set_opacity(1) después volvería maciza la memoria
            self.mem_final = self.mem_g.copy().scale(0.75).move_to([0, 2.55, 0])
            tenue = self.mem_final.copy().fade(0.7)
            self.play(FadeOut(self.casa), FadeOut(self.extras), FadeOut(self.flechas),
                      Transform(self.mem_g, tenue),
                      *[g.animate.scale(0.75).move_to([x, 0.55, 0]) for g, x in zip(self.gente, xs)],
                      run_time=0.6)

            # «su propio dormitorio»
            self.en("dormitorio")
            self.cuartos = VGroup(*[RoundedRectangle(width=3.8, height=3.9, corner_radius=0.15, color=c,
                                                     stroke_width=2.5).set_fill(c, 0.05).move_to([x, -0.95, 0])
                                    for x, c in zip(xs, COLORES)])
            self.play(LaggedStart(*[Create(c) for c in self.cuartos], lag_ratio=0.2), run_time=0.6)

            # «esa casa compartida»
            self.en("compartida")
            casa2 = casa(13.0, 4.4).move_to(ORIGIN)
            casa2.muros.move_to(DOWN * 0.95)
            casa2.tejado.next_to(casa2.muros, UP, buff=0)
            self.play(Create(casa2), run_time=0.6)
            self.mem_g.set_z_index(2)

            # «su contador de programa»: lista de instrucciones y flecha PC en cada cuarto
            self.en("pc")
            self.listas, self.pcs = [], []
            for cu in self.cuartos:
                lista = VGroup(*[Line(LEFT * 0.55, RIGHT * 0.55, color=INK, stroke_width=5) for _ in range(4)])
                lista.arrange(DOWN, buff=0.28).move_to(cu.get_center() + LEFT * 0.75 + DOWN * 0.2)
                pc = flecha_pc().next_to(lista[0], LEFT, buff=0.12)
                self.listas.append(lista)
                self.pcs.append(pc)
            self.play(*[FadeIn(l) for l in self.listas], run_time=0.25)
            self.pop(*self.pcs, sonido="tick")

            # «su propia pila»
            self.en("pila")
            self.pilas = []
            for cu, c in zip(self.cuartos, COLORES):
                p = pila_de_hilo(alto=1.3, ancho=0.95, n_marcos=3, color=c)
                p.move_to(cu.get_center() + RIGHT * 0.95 + DOWN * 0.5)
                self.pilas.append(p)
            self.play(*[GrowFromEdge(p, DOWN) for p in self.pilas], run_time=0.5)

            # «sus registros»
            self.en("registros")
            regs = []
            for cu in self.cuartos:
                r = VGroup(*[VGroup(Square(0.42, color=INK, stroke_width=2).set_fill(FONDO, 1),
                                    Text(n, color=TXT).scale(0.28)) for n in ("R1", "R2")])
                for sq in r:
                    sq[1].move_to(sq[0])
                r.arrange(RIGHT, buff=0.15).move_to(cu.get_bottom() + UP * 0.42 + LEFT * 0.75)
                regs.append(r)
            self.pop(*regs, sonido="pop")

            # «en qué instrucción va»: cada PC salta a una altura distinta
            self.en("instruccion")
            self.sfx("tick", gain=-18)
            self.play(*[pc.animate.next_to(lista[k], LEFT, buff=0.12)
                        for pc, lista, k in zip(self.pcs, self.listas, (2, 1, 3))], run_time=0.45)

            # «variables locales»: cada pila crece un marco
            self.en("locales")
            nuevos = []
            for p, c in zip(self.pilas, COLORES):
                m = p[0][0].copy().set_fill(c, 0.35)
                m.next_to(p[0], UP, buff=0.03)
                nuevos.append(m)
            self.play(*[FadeIn(m, shift=DOWN * 0.3) for m in nuevos], run_time=0.4)

            # «la misma memoria común»
            self.en("comun")
            self.play(Transform(self.mem_g, self.mem_final), run_time=0.3)
            fls = [Arrow(g.get_top(), self.mem_g[0].get_bottom() + RIGHT * (g.get_x() * 0.2), color=c,
                         stroke_width=4, buff=0.08) for g, c in zip(self.gente, COLORES)]
            self.play(*[GrowArrow(f) for f in fls], run_time=0.45)
            self.resaltar(self.mem_g[0], color=AMBAR, escala=1.08)
