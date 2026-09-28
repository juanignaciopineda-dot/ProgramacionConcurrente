"""Escena 10 — La vida de un proceso: los 5 estados y los suspendidos."""
from comun.escena import *

POS = {"N": np.array([-5.2, 1.6, 0]), "P": np.array([-1.9, 1.6, 0]), "E": np.array([1.9, 1.6, 0]),
       "T": np.array([5.2, 1.6, 0]), "B": np.array([0.0, -1.0, 0])}
ANCHO_NODO = 2.3


def nodo(nombre: str, ancho: float = ANCHO_NODO, color=INK) -> VGroup:
    r = RoundedRectangle(width=ancho, height=0.95, corner_radius=0.15, color=color, stroke_width=3)
    r.set_fill(FONDO, 1)
    t = Text(nombre, color=TXT, weight=BOLD).scale(0.5).move_to(r)
    g = VGroup(r, t)
    g.r, g.t = r, t
    return g


def sobre(n: Mobject, dx: float = 0.0) -> np.ndarray:
    """Lugar donde se para un proceso (punto) que está en el estado `n`."""
    return n.get_top() + UP * 0.32 + RIGHT * dx


class EstadosProceso(EscenaCurso):
    ESCENA = "10"

    def construct(self):
        self.presentacion()
        self.nacimiento()
        self.ejecucion()
        self.caminos()
        self.final()
        self.suspendidos()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ helpers
    def encender(self, n: VGroup, si: bool = True):
        return n.r.animate.set_fill(AMBAR if si else FONDO, 0.3 if si else 1).set_color(AMBAR if si else INK)

    def rotulo_arriba(self, txt: str, a: str, b: str) -> Text:
        """Rótulo de una flecha horizontal entre los nodos a y b: por ENCIMA de
        la fila de nodos (entre dos nodos no entra sin tocarlos)."""
        x = (self.n[a].get_right()[0] + self.n[b].get_left()[0]) / 2
        return Text(txt, color=AMBAR).scale(0.34).move_to([x, self.n[a].get_top()[1] + 0.22, 0])

    def rotulo_abajo(self, txt: str, a: str, b: str) -> Text:
        x = (self.n[a].get_right()[0] + self.n[b].get_left()[0]) / 2
        return Text(txt, color=AMBAR).scale(0.34).move_to([x, self.n[a].get_bottom()[1] - 0.22, 0])

    def viajar(self, destino, *extra, t: float = 0.5):
        self.play(self.dot.animate.move_to(destino), *extra, run_time=t)

    # ------------------------------------------------------------------ b1
    def presentacion(self):
        with self.narrar("b1"):
            self.en("sigamos")
            self.dot = Dot(radius=0.2, color=AMBAR)
            self.pop(self.dot)

            self.en("quieto")
            for d in (LEFT * 2.5, RIGHT * 2.5, ORIGIN):
                self.sfx("tick", gain=-18)
                self.play(self.dot.animate.move_to(d), run_time=0.3, rate_func=rate_functions.ease_out_back)

            self.en("recorrer")
            zig = [ORIGIN, UL * 1.5, RIGHT * 1.2 + UP * 0.3, DR * 1.5 + RIGHT, LEFT * 2 + DOWN * 1.8, ORIGIN]
            camino = VMobject(color=AMBAR, stroke_width=4).set_points_as_corners(zig)
            self.play(Create(camino), MoveAlongPath(self.dot, camino), run_time=1.3)

            self.en("final")
            self.play(FadeOut(camino), self.dot.animate.scale(0.8), run_time=0.4)

            self.en("modelo")
            self.play(self.dot.animate.move_to([-6.2, 3.2, 0]), run_time=0.5)

            self.en("tres")
            pos3 = [np.array([-3.2, -0.6, 0]), np.array([3.2, -0.6, 0]), np.array([0, 2.2, 0])]
            vacios = [RoundedRectangle(width=ANCHO_NODO, height=0.95, corner_radius=0.15, color=INK,
                                       stroke_width=3).set_fill(FONDO, 1).move_to(p) for p in pos3]
            self.pop(*vacios)
            nombres = ["Listo", "Ejecución", "Bloqueado"]
            tri = []
            for marca, v, nom in zip(("listo", "ejecutando", "bloqueado"), vacios, nombres):
                self.en(marca)
                n = nodo(nom).move_to(v)
                self.sfx("pop", gain=-16)
                self.play(ReplacementTransform(v, n), run_time=0.3)
                tri.append(n)

            self.en("reales")
            self.resaltar(*tri, color=AZUL, run_time=0.6)

            self.en("detallada")
            lp = lupa().scale(1.2).move_to(LEFT * 3.2 + DOWN * 0.6)
            self.play(FadeIn(lp), run_time=0.2)
            self.play(lp.animate.move_to(UP * 2.2), run_time=0.45)
            self.play(lp.animate.move_to(RIGHT * 3.2 + DOWN * 0.6), run_time=0.45)
            self.play(FadeOut(lp), run_time=0.2)

            self.en("cinco")
            self.n = {"P": nodo("Preparado"), "E": nodo("Ejecución"), "B": nodo("Bloqueado"),
                      "N": nodo("Nuevo"), "T": nodo("Terminado")}
            for k, v in self.n.items():
                v.move_to(POS[k])
            n = self.n
            self.sfx("whoosh")
            self.play(ReplacementTransform(tri[0], n["P"]), ReplacementTransform(tri[1], n["E"]),
                      ReplacementTransform(tri[2], n["B"]), self.dot.animate.move_to([-6.8, 3.2, 0]),
                      run_time=0.7)
            self.pop(n["N"], n["T"])

            self.en("acompanar")
            A = lambda a, b: Arrow(a, b, color=INK, stroke_width=3, buff=0.08, max_tip_length_to_length_ratio=0.12)
            self.e = {
                "NP": A(n["N"].get_right(), n["P"].get_left()),
                "PE": A(n["P"].get_right() + UP * 0.2, n["E"].get_left() + UP * 0.2),
                "EP": A(n["E"].get_left() + DOWN * 0.2, n["P"].get_right() + DOWN * 0.2),
                "EB": A(n["E"].get_bottom(), n["B"].get_right() + UP * 0.15),
                "BP": A(n["B"].get_left() + UP * 0.15, n["P"].get_bottom()),
                "ET": A(n["E"].get_right(), n["T"].get_left()),
            }
            self.play(LaggedStart(*[GrowArrow(a) for a in self.e.values()], lag_ratio=0.12), run_time=0.9)

    # ------------------------------------------------------------------ b2
    def nacimiento(self):
        n = self.n
        with self.narrar("b2"):
            self.en("nuevo")
            self.viajar(sobre(n["N"]), self.encender(n["N"]))

            self.en("existe")
            self.sfx("latido", gain=-16)
            self.play(self.dot.animate(rate_func=there_and_back).scale(1.6), run_time=0.5)

            self.en("visto")
            espera = reloj().scale(0.35).next_to(self.dot, UP, buff=0.12)
            self.play(FadeIn(espera, scale=0.5), run_time=0.25)
            self.play(Rotate(espera.minutero, -2 * PI, about_point=espera.aro.get_center()), run_time=1.0)

            self.en("admite")
            ok = tilde(VERDE, 0.35).move_to(espera)
            self.sfx("exito", gain=-16)
            self.play(ReplacementTransform(espera, ok), run_time=0.3)
            self.pop(self.rotulo_arriba("admitido", "N", "P"), sonido=None)
            self.viajar(sobre(n["P"]), FadeOut(ok), self.encender(n["N"], False), self.encender(n["P"]), t=0.6)

            self.en("correr")
            self.resaltar(n["P"], color=AMBAR, escala=1.08)

            self.en("libre")
            self.cpu = cpu().scale(0.45).move_to(n["E"].get_center() + UP * 1.8)
            self.otro = Dot(radius=0.13, color=MAGENTA).move_to(self.cpu.caja.get_center() + DOWN * 0.1)
            self.play(FadeIn(self.cpu, shift=DOWN * 0.3), FadeIn(self.otro), run_time=0.4)
            self.play(Indicate(self.otro, color=MAGENTA, scale_factor=1.6), run_time=0.5)

    # ------------------------------------------------------------------ b3
    def ejecucion(self):
        n = self.n
        with self.narrar("b3"):
            self.en("elige")
            self.pop(self.rotulo_arriba("planificador", "P", "E"), sonido=None)
            self.play(FadeOut(self.otro, shift=RIGHT), run_time=0.3)

            self.en("ejecucion")
            self.viajar(sobre(n["E"]), self.encender(n["P"], False), self.encender(n["E"]), t=0.5)

            self.en("control")
            self.sfx("pop")
            self.play(self.cpu.caja.animate.set_fill(AMBAR, 0.5), Flash(self.cpu.caja, color=AMBAR), run_time=0.5)

            self.en("nucleo")
            nucleo = Square(0.28, color=AMBAR, stroke_width=3).set_fill(AMBAR, 0.8)
            nucleo.move_to(self.cpu.caja.get_center() + DOWN * 0.08)
            self.pop(nucleo, sonido="tick")
            self.nucleo = nucleo

            self.en("uno")
            intruso = Dot(radius=0.15, color=VERDE).move_to(sobre(n["P"]))
            self.play(FadeIn(intruso, scale=2), run_time=0.25)
            self.sfx("error", gain=-14, offset=0.4)
            self.play(intruso.animate(rate_func=there_and_back).move_to(sobre(n["E"], -0.6)), run_time=0.9)
            self.play(FadeOut(intruso), run_time=0.2)

            self.en("protagonista")
            foco = Circle(radius=0.55, color=AMBAR, stroke_width=0).set_fill(AMBAR, 0.25).move_to(self.dot)
            foco.permitir_encima = True      # brillo translúcido, a propósito
            self.play(FadeIn(foco, scale=0.4), run_time=0.4)
            self.play(foco.animate.scale(1.3).set_fill(opacity=0), run_time=0.8)
            self.remove(foco)

    # ------------------------------------------------------------------ b4
    def caminos(self):
        n, e = self.n, self.e
        with self.narrar("b4"):
            self.en("caminos")
            for _ in range(2):
                self.play(Indicate(e["EP"], color=AMBAR, scale_factor=1.1),
                          Indicate(e["EB"], color=AMBAR, scale_factor=1.1), run_time=0.45)

            self.en("quantum")
            rel = reloj().scale(0.5).next_to(self.cpu, RIGHT, buff=0.4)
            self.play(FadeIn(rel), run_time=0.2)
            self.sfx("tick")
            self.play(Rotate(rel.minutero, -2 * PI, about_point=rel.aro.get_center()), run_time=0.8)

            self.en("urgente")
            urg = VGroup(Dot(radius=0.2, color=ROJO), Text("!", color=TXT, weight=BOLD).scale(0.4))
            urg.next_to(self.cpu, LEFT, buff=0.4)
            self.pop(urg, sonido="alarma")

            self.en("vuelve")
            self.pop(self.rotulo_abajo("fin de quantum", "P", "E"), sonido=None)
            self.viajar(sobre(n["P"]), self.encender(n["E"], False), self.encender(n["P"]),
                        self.cpu.caja.animate.set_fill(FONDO, 1), FadeOut(rel), FadeOut(urg), t=0.5)

            self.en("turno")
            delante = Dot(radius=0.15, color=CIAN).move_to(sobre(n["P"], 0.35))
            self.play(FadeIn(delante, shift=DOWN * 0.3), self.dot.animate.move_to(sobre(n["P"], -0.35)), run_time=0.4)

            self.en("necesita")
            self.viajar(sobre(n["E"]), FadeOut(delante), self.encender(n["P"], False), self.encender(n["E"]), t=0.4)
            duda = Text("?", color=AMBAR, weight=BOLD).scale(0.6).next_to(self.dot, RIGHT, buff=0.12)
            self.pop(duda)

            self.en("disco")
            dsk = disco().scale(0.6).next_to(n["B"], RIGHT, buff=1.6)
            self.pop(dsk)
            self.pop(Text("espera E/S", color=AMBAR).scale(0.34).next_to(e["EB"].get_center(), RIGHT, buff=0.15),
                     sonido=None)
            self.viajar(n["B"].get_bottom() + DOWN * 0.32, FadeOut(duda), self.encender(n["E"], False),
                        self.encender(n["B"]), t=0.6)

            self.en("espera")
            self.play(self.dot.animate(rate_func=there_and_back).scale(1.6), run_time=1.0)

            self.en("ocurre")
            self.sfx("pop")
            self.play(Indicate(dsk, color=VERDE), Flash(dsk, color=VERDE), run_time=0.5)

            self.en("nodirecto")
            atajo = DashedVMobject(Arrow(n["B"].get_top(), n["E"].get_bottom() + LEFT * 0.4, color=ROJO,
                                         stroke_width=4, buff=0.1), num_dashes=12)
            self.play(Create(atajo), run_time=0.4)
            no = cruz(ROJO, 0.5).move_to(atajo.get_center())
            self.sfx("error", gain=-14)
            self.play(Create(no), run_time=0.25)
            self.play(FadeOut(atajo), FadeOut(no), run_time=0.3)

            self.en("fila")
            fila = VGroup(Dot(radius=0.14, color=CIAN).move_to(sobre(n["P"], -0.8)),
                          Dot(radius=0.14, color=VERDE).move_to(sobre(n["P"], -0.4)))
            self.play(FadeIn(fila, shift=DOWN * 0.2), FadeOut(dsk), run_time=0.3)
            self.pop(Text("ocurre el evento", color=AMBAR).scale(0.34).next_to(e["BP"].get_center(), LEFT, buff=0.15),
                     sonido=None)
            self.viajar(sobre(n["P"], 0.0), self.encender(n["B"], False), self.encender(n["P"]), t=0.6)
            self.fila = fila

    # ------------------------------------------------------------------ b5
    def final(self):
        n = self.n
        with self.narrar("b5"):
            self.en("termina")
            self.play(FadeOut(self.fila), run_time=0.2)
            self.viajar(sobre(n["E"]), self.encender(n["P"], False), self.encender(n["E"]), t=0.4)

            self.en("terminado")
            self.pop(self.rotulo_arriba("fin", "E", "T"), sonido=None)
            self.viajar(sobre(n["T"]), self.encender(n["E"], False), self.encender(n["T"]), t=0.45)

            self.en("trabajo")
            barra = Rectangle(width=2.0, height=0.18, color=INK, stroke_width=2).next_to(n["T"], DOWN, buff=0.3)
            prog = Rectangle(width=2.0, height=0.18, stroke_width=0).set_fill(VERDE, 0.9)
            prog.align_to(barra, LEFT).stretch_to_fit_width(0.01, about_edge=LEFT)
            self.add(barra, prog)
            self.play(prog.animate.stretch_to_fit_width(2.0, about_edge=LEFT), run_time=0.8)

            self.en("paz")
            ok = tilde(VERDE, 0.5).next_to(barra, DOWN, buff=0.3).shift(LEFT * 0.5)
            self.sfx("exito")
            self.play(Create(ok), run_time=0.35)

            self.en("fatal")
            mal = cruz(ROJO, 0.5).next_to(barra, DOWN, buff=0.3).shift(RIGHT * 0.5)
            self.sfx("error", gain=-12)
            self.play(Create(mal), run_time=0.3)
            self.marcas_fin = VGroup(ok, mal, barra, prog)

    # ------------------------------------------------------------------ b6
    def suspendidos(self):
        n = self.n
        with self.narrar("b6"):
            self.en("sacado")
            todo = Group(*[m for m in self.mobjects if m is not self.camera.frame])
            self.play(todo.animate.scale(0.85).shift(UP * 0.7), FadeOut(self.marcas_fin), run_time=0.6)
            exp = Dot(radius=0.15, color=CIAN).move_to(n["B"].get_bottom() + DOWN * 0.3)
            self.play(FadeIn(exp, scale=2), self.encender(n["B"]), run_time=0.3)
            self.sfx("whoosh")
            self.play(exp.animate.move_to(DOWN * 2.4 + RIGHT * 0.6), run_time=0.5)

            self.en("espacio")
            mem = Rectangle(width=2.6, height=0.4, color=INK, stroke_width=2).move_to(DOWN * 1.2 + RIGHT * 4.6)
            llena = Rectangle(width=2.6, height=0.4, stroke_width=0).set_fill(ROJO, 0.8).move_to(mem)
            etq = Text("memoria llena", color=ROJO).scale(0.34).next_to(mem, DOWN, buff=0.12)
            self.play(FadeIn(mem), GrowFromEdge(llena, LEFT), FadeIn(etq), run_time=0.6)

            self.en("suspendido")
            self.play(exp.animate.set_color(INK).scale(0.9), run_time=0.4)

            self.en("susp_bloq")
            sb = nodo("Suspendido bloqueado", ancho=3.9).move_to(DOWN * 3.2 + RIGHT * 2.6)
            l1 = DashedLine(n["B"].get_bottom(), sb.get_top(), color=INK, stroke_width=3)
            self.play(FadeIn(sb, scale=0.8), Create(l1), exp.animate.move_to(sb.get_top() + UP * 0.25 + RIGHT * 1.4),
                      run_time=0.6)

            self.en("susp_listo")
            sl = nodo("Suspendido listo", ancho=3.4).move_to(DOWN * 3.2 + LEFT * 2.8)
            l2 = DashedLine(sb.get_left(), sl.get_right(), color=INK, stroke_width=3)
            l3 = DashedLine(sl.get_top(), n["P"].get_bottom(), color=INK, stroke_width=3)
            self.play(FadeIn(sl, scale=0.8), Create(l2), Create(l3), run_time=0.6)

            self.en("desaparecio")
            self.play(exp.animate.move_to(sl.get_top() + UP * 0.25 + LEFT * 1.2).set_color(CIAN), run_time=0.6)

            self.en("devuelve")
            self.sfx("whoosh", gain=-16)
            self.play(exp.animate.move_to(sobre(n["P"], -0.6)), self.encender(n["P"]), run_time=0.7)
