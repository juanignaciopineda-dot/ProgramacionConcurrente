"""Escena 09 — Consecuencias del interleaving y la explosión de estados."""
from comun.escena import *


def fila_fichas(orden: list[str], lado: float = 0.7) -> VGroup:
    return VGroup(*[ficha_accion(n, AZUL if n.startswith("A") else MAGENTA, lado) for n in orden]
                  ).arrange(RIGHT, buff=0.1)


def rotulo_caja(txt: str, color=AMBAR, escala: float = 0.7) -> VGroup:
    t = Text(txt, color=color, weight=BOLD).scale(escala)
    r = RoundedRectangle(width=t.width + 0.5, height=t.height + 0.35, corner_radius=0.12, color=color,
                         stroke_width=3).set_fill(FONDO, 1)
    t.move_to(r)
    g = VGroup(r, t)
    g.texto = t
    return g


class ExplosionEstados(EscenaCurso):
    ESCENA = "09"

    def construct(self):
        self.consecuencias()
        self.explosion()
        self.estado_completo()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def consecuencias(self):
        with self.narrar("b1"):
            # «dos consecuencias»
            self.en("dos")
            self.t1 = RoundedRectangle(width=6.3, height=6.6, corner_radius=0.2, color=INK, stroke_width=2.5)
            self.t1.move_to([-3.4, -0.2, 0])
            self.t2 = self.t1.copy().move_to([3.4, -0.2, 0])
            n1 = Text("1", color=AMBAR, weight=BOLD).scale(1.1).move_to(self.t1.get_corner(UL) + [0.5, -0.5, 0])
            n2 = Text("2", color=AMBAR, weight=BOLD).scale(1.1).move_to(self.t2.get_corner(UL) + [0.5, -0.5, 0])
            self.play(Create(self.t1), Create(self.t2), run_time=0.5)
            self.pop(n1, n2)

            # «Primero»
            self.en("primero")
            self.resaltar(self.t1, color=AMBAR, escala=1.03)

            # «no lo decide el programador»: las fichas se barajan solas
            self.en("programador")
            prog = persona(TXT).scale(1.2).move_to([-5.75, 1.9, 0])
            fichas = fila_fichas(["A1", "A2", "B1", "B2"], 0.9).move_to([-2.95, 1.9, 0])
            self.play(FadeIn(prog, shift=RIGHT * 0.3), FadeIn(fichas), run_time=0.3)
            pos = [f.get_center() for f in fichas]
            for orden in ((2, 0, 3, 1), (1, 3, 0, 2)):
                self.sfx("tick", gain=-20)
                self.play(*[f.animate.move_to(pos[o]) for f, o in zip(fichas, orden)], run_time=0.3, path_arc=0.9)

            # «ni es reproducible»: tres corridas, tres órdenes
            self.en("reproducible")
            self.corridas = VGroup(*[fila_fichas(o, 0.9) for o in
                                     (["A1", "B1", "A2", "B2"], ["B1", "A1", "B2", "A2"], ["A1", "A2", "B1", "B2"])])
            self.corridas.arrange(DOWN, buff=0.3).move_to([-3.4, -1.25, 0])
            self.play(LaggedStart(*[FadeIn(c, shift=DOWN * 0.3) for c in self.corridas], lag_ratio=0.3), run_time=0.7)

            # «a la siguiente»
            self.en("siguiente")
            for c in self.corridas:
                self.play(c.animate(rate_func=there_and_back).scale(1.1), run_time=0.25)

            # «Segundo»
            self.en("segundo")
            self.resaltar(self.t2, color=AMBAR, escala=1.03)

            # «corrido dos veces»
            self.en("dosveces")
            self.dos = VGroup(fila_fichas(["A1", "A2", "B1", "B2"], 1.05), fila_fichas(["A1", "B1", "A2", "B2"], 1.05))
            self.dos.arrange(DOWN, buff=1.2).move_to([2.45, -0.2, 0])
            self.play(LaggedStart(*[FadeIn(c, shift=RIGHT * 0.3) for c in self.dos], lag_ratio=0.4), run_time=0.6)

            # «dos resultados distintos»
            self.en("resultados")
            r1 = Text("= 2", color=VERDE, weight=BOLD).scale(0.9).next_to(self.dos[0], RIGHT, buff=0.35)
            r2 = Text("= 1", color=ROJO, weight=BOLD).scale(0.9).next_to(self.dos[1], RIGHT, buff=0.35)
            self.pop(r1, r2)

            # «el intercalado fue otro»
            self.en("otro")
            self.resaltar(*[self.dos[i][j] for i in (0, 1) for j in (1, 2)], color=AMBAR, escala=1.18, run_time=0.6)

    # ------------------------------------------------------------------ b2
    def explosion(self):
        with self.narrar("b2"):
            # «todavía más de fondo»: vuelve el árbol pequeño
            self.en("fondo")
            self.corte()
            a2 = ArbolIntercalado(2, 2, ancho=3.8, alto=2.8, etiquetas=False, radio=0.05, grosor=3)
            a2.move_to([-4.4, 1.5, 0])
            self.play(Create(a2), run_time=0.6)

            # «agrandamos un poco»
            self.en("agrandar")
            self.play(a2.animate(rate_func=there_and_back).scale(1.15), run_time=0.5)

            # «Con dos acciones»
            self.en("dos")
            tag2 = rotulo_caja("2 + 2 → 6", escala=0.6).next_to(a2, DOWN, buff=0.35)
            self.pop(tag2)

            # «varios caminos posibles»
            self.en("varios")
            self.play(*[a.animate(rate_func=there_and_back).set_color(AMBAR) for a in a2.aristas.values()],
                      run_time=0.5)

            # «Con tres acciones»
            self.en("tres")
            a3 = ArbolIntercalado(3, 3, ancho=5.8, alto=4.6, etiquetas=False, radio=0.035, grosor=2)
            a3.move_to([2.6, 1.0, 0])
            tag3 = rotulo_caja("3 + 3 → 20", escala=0.6).next_to(a3, DOWN, buff=0.3)
            self.play(Create(a3), run_time=0.6)
            self.pop(tag3)

            # «o con diez»: el árbol llena la pantalla
            self.en("diez")
            self.a4 = ArbolIntercalado(4, 4, ancho=13.6, alto=7.6, etiquetas=False, radio=0.001, grosor=1.3)
            self.a4.set_opacity(0.55)
            self.tag = rotulo_caja("10 + 10 → 184.756", escala=0.9).move_to(DOWN * 0.2)
            self.play(FadeOut(VGroup(a2, tag2, a3, tag3)), Create(self.a4), run_time=0.45)
            self.pop(self.tag, sonido="impacto")

            # «o con cien»
            self.en("cien")
            nuevo = rotulo_caja("100 + 100 → 9 · 10⁵⁸", color=ROJO, escala=1.0).move_to(self.tag)
            self.sfx("impacto", gain=-12)
            self.play(Transform(self.tag, nuevo), self.a4.animate.set_color(ROJO), run_time=0.4)
            self.play(Wiggle(self.tag, scale_value=1.12), run_time=0.6)

            # «crece exponencialmente»
            self.en("exponencial")
            self.ax, _ = ejes([0, 5, 1], [0, 10, 2], ancho=7.0, alto=4.6)
            self.ax.move_to(DOWN * 0.6)
            self.curva = self.ax.plot(lambda x: 0.05 * np.exp(1.25 * x), x_range=[0, 4.95], color=AMBAR,
                                      stroke_width=6)
            self.play(FadeOut(self.tag), self.a4.animate.set_opacity(0.15), Create(self.ax), run_time=0.4)
            self.play(Create(self.curva), run_time=1.0, rate_func=rate_functions.ease_in_quad)

            # «A esto se le llama»
            self.en("llama")
            self.play(Wiggle(self.curva, scale_value=1.05), run_time=0.6)

            # «explosión»: el árbol estalla
            self.en("explosion")
            rng = np.random.default_rng(7)
            aristas = list(self.a4.aristas.values())
            self.sfx("impacto", gain=-8)
            self.play(*[a.animate.shift((a.get_center() / max(np.linalg.norm(a.get_center()), 0.3)) * rng.uniform(2, 5))
                        .set_opacity(0) for a in aristas],
                      FadeOut(self.ax), FadeOut(self.curva), run_time=0.6)
            self.corte()
            exp = self.texto_centrado("explosión", "de estados", color=AMBAR, escala=1.6)
            self.pop(exp, sonido=None)

            # «el testing»: vuelve la lupa, sobre todas las ejecuciones posibles
            self.en("testing")
            self.grilla = VGroup(*[Dot([x, y, 0], radius=0.05, color=INK)
                                   for x in np.linspace(-6.6, 6.6, 29) for y in np.linspace(-3.5, 3.5, 15)])
            self.lup = lupa().scale(1.4).move_to([-5.7, 2.8, 0])
            self.play(FadeOut(exp, scale=0.6), FadeIn(self.grilla), FadeIn(self.lup, scale=0.5), run_time=0.45)

            # «casi no sirve»: solo alcanza a recorrer un rincón
            self.en("nosirve")
            ruta = [[-5.0, 2.8, 0], [-5.0, 2.1, 0], [-5.9, 2.1, 0], [-5.7, 2.8, 0]]
            for p in ruta:
                self.play(self.lup.animate.move_to(p), run_time=0.22)

            # «prácticamente imposible»
            self.en("imposible")
            self.play(Wiggle(self.lup, scale_value=1.1, rotation_angle=0.08 * TAU), run_time=0.6)

            # «el test que dispare»
            self.en("test")
            for p in ([-4.6, 2.3, 0], [-5.3, 1.6, 0], [-4.4, 2.9, 0]):
                self.sfx("tick", gain=-20)
                self.play(self.lup.animate.move_to(p), run_time=0.25)

            # «esa combinación puntual»: un único punto rojo, lejísimos
            self.en("puntual")
            malo = self.grilla[-1]
            self.play(malo.animate.set_color(ROJO).scale(3), run_time=0.3)
            self.sfx("latido", gain=-14)
            self.play(malo.animate(rate_func=there_and_back).scale(1.6), run_time=0.45)

            # «rompe todo»
            self.en("rompe")
            flash = FullScreenRectangle().set_fill(ROJO, 0.35).set_stroke(width=0)
            self.sfx("impacto", gain=-10)
            self.play(FadeIn(flash, rate_func=there_and_back), Flash(malo, color=ROJO, flash_radius=0.5), run_time=0.45)
            self.remove(flash)

    # ------------------------------------------------------------------ b3
    def estado_completo(self):
        with self.narrar("b3"):
            # «Una última precisión»: la ficha «estado»
            self.en("precision")
            self.corte()
            self.ficha = RoundedRectangle(width=4.6, height=4.2, corner_radius=0.15, color=AMBAR, stroke_width=3)
            self.ficha.set_fill(AMBAR, 0.06).move_to([-3.8, 0.2, 0])
            self.titulo = Text("estado", color=AMBAR, weight=BOLD).scale(0.7).move_to(self.ficha.get_top() + DOWN * 0.45)
            self.play(FadeIn(self.ficha, shift=RIGHT * 0.3), FadeIn(self.titulo), run_time=0.4)

            # «el estado completo»
            self.en("estado")
            self.resaltar(self.ficha, color=TXT, escala=1.05)

            # «el valor de sus variables»
            self.en("variables")
            self.vars = VGroup(Text("x = 1", color=TXT).scale(0.85), Text("y = 0", color=TXT).scale(0.85))
            self.vars.arrange(DOWN, buff=0.3).move_to(self.ficha.get_center() + DOWN * 0.3)
            self.play(LaggedStart(*[FadeIn(v, shift=UP * 0.2) for v in self.vars], lag_ratio=0.3), run_time=0.45)

            # «También incluye»: la ficha se estira
            self.en("incluye")
            larga = RoundedRectangle(width=7.6, height=4.2, corner_radius=0.15, color=AMBAR, stroke_width=3)
            larga.set_fill(AMBAR, 0.06).move_to(self.ficha).align_to(self.ficha, LEFT)
            self.play(Transform(self.ficha, larga),
                      self.titulo.animate.move_to(larga.get_top() + DOWN * 0.45), run_time=0.5)

            # «en qué instrucción está parado»: dos listas de instrucciones
            self.en("instruccion")
            self.listas = VGroup()
            for x, nombre, col in ((3.9, "P1", AZUL), (5.9, "P2", MAGENTA)):
                cab = Text(nombre, color=col, weight=BOLD).scale(0.55).move_to([x, 2.1, 0])
                ls = VGroup(*[Line(LEFT * 0.6, RIGHT * 0.6, color=INK, stroke_width=5) for _ in range(4)])
                ls.arrange(DOWN, buff=0.45).move_to([x, 0.2, 0])
                self.listas.add(VGroup(cab, ls))
            self.play(LaggedStart(*[FadeIn(l, shift=LEFT * 0.3) for l in self.listas], lag_ratio=0.3), run_time=0.5)

            # «su contador de programa»
            self.en("pc")
            pcs = [flecha_pc_en(self.listas[0][1][1]), flecha_pc_en(self.listas[1][1][0])]
            self.pop(*pcs, sonido="tick")
            vals = VGroup(Text("PC1 = 2", color=AMBAR).scale(0.75), Text("PC2 = 1", color=AMBAR).scale(0.75))
            vals.arrange(DOWN, buff=0.3).move_to(self.ficha.get_center() + RIGHT * 1.9 + DOWN * 0.3)
            self.play(FadeIn(vals, shift=LEFT * 0.3), run_time=0.35)

            # «Dos procesos con las mismas variables»: la ficha se duplica
            self.en("mismas")
            f1 = VGroup(self.ficha, self.titulo, self.vars, vals)
            self.play(FadeOut(self.listas), *[FadeOut(p) for p in pcs], run_time=0.25)
            self.play(f1.animate.scale(0.8).move_to([-3.6, 0.2, 0]), run_time=0.4)
            f2 = f1.copy()
            self.play(f2.animate.move_to([3.6, 0.2, 0]), run_time=0.45)
            self.f2_vals = f2[3]

            # «pero con el contador»: los PC de la segunda ficha se mueven
            self.en("contador")
            nuevo = Text("PC1 = 3", color=AMBAR).scale(0.75 * 0.8).move_to(self.f2_vals[0])
            self.sfx("tick", gain=-16)
            self.play(Transform(self.f2_vals[0], nuevo), run_time=0.35)

            # «en un lugar distinto»
            self.en("distinto")
            marco = SurroundingRectangle(self.f2_vals[0], color=ROJO, buff=0.12, stroke_width=4)
            self.play(Create(marco), run_time=0.35)

            # «estados distintos»
            self.en("distintos")
            neq = Text("≠", color=ROJO, weight=BOLD).scale(2.6).move_to([0, 0.2, 0])
            self.pop(neq, sonido="impacto")


def flecha_pc_en(linea: Mobject) -> VGroup:
    return puntero_pc(linea, AMBAR)
