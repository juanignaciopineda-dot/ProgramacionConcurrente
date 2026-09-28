"""Escena 08 — Interleaving: los procesos intercalan sus acciones atómicas; todos los caminos valen."""
from comun.escena import *


class Interleaving(EscenaCurso):
    ESCENA = "08"

    def construct(self):
        self.concepto()
        self.dos_procesos()
        self.arbol()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def concepto(self):
        with self.narrar("b1"):
            # «Llegamos al concepto»
            self.en("concepto")
            flash = FullScreenRectangle().set_fill(TXT, 0.3).set_stroke(width=0)
            self.sfx("whoosh")
            self.play(FadeIn(flash, rate_func=there_and_back), run_time=0.3)
            self.remove(flash)

            # «sostiene todo este capítulo»: un pilar sostiene la losa del capítulo 1
            self.en("pilar")
            pilar = Rectangle(width=1.4, height=3.4, color=INK, stroke_width=3).set_fill(INK, 0.35)
            pilar.move_to([0, -2.3, 0])
            self.losas = [caja("capítulo 1", AMBAR, ancho=5.2, alto=0.7, scale=0.5)
                          .next_to(pilar, UP, buff=0)]
            self.play(GrowFromEdge(pilar, DOWN), run_time=0.5)
            self.play(FadeIn(self.losas[0], shift=DOWN * 0.4), run_time=0.3)

            # «todo lo que sigue»: más losas encima
            self.en("sigue")
            for nombre in ("Petri", "autómatas", "sincronización"):
                l = caja(nombre, INK, ancho=5.2, alto=0.7, scale=0.5).next_to(self.losas[-1], UP, buff=0.08)
                self.losas.append(l)
            self.play(LaggedStart(*[FadeIn(l, shift=DOWN * 0.5) for l in self.losas[1:]], lag_ratio=0.4),
                      run_time=0.9)

            # «Se llama interleaving»
            self.en("interleaving")
            self.corte()
            pal = self.palabra("interleaving", escala=1.8).move_to(UP * 1.4)
            self.pop(pal, sonido="impacto")

            # «intercalado»
            self.en("intercalado")
            sub = Text("intercalado", color=INK).scale(0.9).next_to(pal, DOWN, buff=0.3)
            self.play(FadeIn(sub, shift=UP * 0.2), run_time=0.3)

            # «no se puede dejar de ver»: un ojo se abre
            self.en("ver")
            o = ojo().scale(1.1).next_to(pal, UP, buff=0.35)
            cerrado = o.copy().stretch(0.05, 1)
            self.add(cerrado)
            self.play(Transform(cerrado, o), run_time=0.4)

            # «los procesos concurrentes»: dos filas de fichas
            self.en("procesos")
            fa = VGroup(*[ficha_accion(f"A{i}", AZUL, 1.0) for i in range(1, 5)]).arrange(RIGHT, buff=0.15)
            fb = VGroup(*[ficha_accion(f"B{i}", MAGENTA, 1.0) for i in range(1, 5)]).arrange(RIGHT, buff=0.15)
            fa.move_to([-3.2, -1.6, 0])
            fb.move_to([3.2, -1.6, 0])
            self.play(FadeOut(cerrado), VGroup(pal, sub).animate.shift(UP * 1.1),
                      FadeIn(fa, shift=RIGHT * 0.4), FadeIn(fb, shift=LEFT * 0.4), run_time=0.5)

            # «intercalando las acciones atómicas»: se barajan en una sola fila
            self.en("intercalan")
            orden = [fa[0], fb[0], fb[1], fa[1], fa[2], fb[2], fa[3], fb[3]]
            destino = VGroup(*[f.copy() for f in orden]).arrange(RIGHT, buff=0.15).move_to(DOWN * 1.9)
            self.sfx("whoosh", gain=-16)
            self.play(*[f.animate.move_to(d) for f, d in zip(orden, destino)], run_time=0.8, path_arc=0.6)
            self.fila = orden

            # «que los componen»: cada ficha conserva su color
            self.en("componen")
            self.resaltar(*fa, color=AZUL, escala=1.15, run_time=0.4)
            self.resaltar(*fb, color=MAGENTA, escala=1.15, run_time=0.4)

    # ------------------------------------------------------------------ b2
    def dos_procesos(self):
        with self.narrar("b2"):
            # «dos procesos pequeños»
            self.en("dos")
            self.corte()
            p1 = RoundedRectangle(width=5.0, height=3.4, corner_radius=0.15, color=AZUL, stroke_width=3)
            p1.set_fill(AZUL, 0.06).move_to([-3.6, 0.2, 0])
            p2 = p1.copy().set_color(MAGENTA).set_fill(MAGENTA, 0.06).move_to([3.6, 0.2, 0])
            t1 = Text("P1", color=AZUL, weight=BOLD).scale(0.6).move_to(p1.get_top() + DOWN * 0.35)
            t2 = Text("P2", color=MAGENTA, weight=BOLD).scale(0.6).move_to(p2.get_top() + DOWN * 0.35)
            self.play(FadeIn(p1, shift=RIGHT * 0.4), FadeIn(p2, shift=LEFT * 0.4), FadeIn(t1), FadeIn(t2),
                      run_time=0.45)
            self.g1, self.g2 = VGroup(p1, t1), VGroup(p2, t2)

            # «El proceso uno»
            self.en("p1")
            self.resaltar(p1, color=AZUL, escala=1.06, run_time=0.4)

            # «A uno y A dos»
            self.en("a")
            a1 = ficha_accion("A1", AZUL, 1.2).move_to(p1.get_center() + LEFT * 1.0 + DOWN * 0.25)
            a2 = ficha_accion("A2", AZUL, 1.2).move_to(p1.get_center() + RIGHT * 1.0 + DOWN * 0.25)
            self.pop(a1, a2)

            # «en ese orden»
            self.en("orden")
            fa = Arrow(a1.get_right(), a2.get_left(), color=AZUL, stroke_width=5, buff=0.1)
            self.play(GrowArrow(fa), run_time=0.3)

            # «El proceso dos»
            self.en("p2")
            self.resaltar(p2, color=MAGENTA, escala=1.06, run_time=0.4)

            # «B uno y B dos»
            self.en("b")
            b1 = ficha_accion("B1", MAGENTA, 1.2).move_to(p2.get_center() + LEFT * 1.0 + DOWN * 0.25)
            b2 = ficha_accion("B2", MAGENTA, 1.2).move_to(p2.get_center() + RIGHT * 1.0 + DOWN * 0.25)
            fb = Arrow(b1.get_right(), b2.get_left(), color=MAGENTA, stroke_width=5, buff=0.1)
            self.pop(b1, b2)
            self.play(GrowArrow(fb), run_time=0.3)
            self.g1.add(a1, a2, fa)
            self.g2.add(b1, b2, fb)

            # «Cada uno, por separado»
            self.en("separado")
            self.play(self.g1.animate.shift(LEFT * 0.35), self.g2.animate.shift(RIGHT * 0.35), run_time=0.45)

            # «su propio orden interno»
            self.en("interno")
            oks = [tilde(VERDE, 0.4).next_to(f, UP, buff=0.15) for f in (fa, fb)]
            self.sfx("exito", gain=-16)
            self.play(*[Create(o) for o in oks], run_time=0.35)

            # «entre los dos procesos»: el espacio del medio se ilumina
            self.en("entre")
            medio = Rectangle(width=1.8, height=3.4, stroke_width=0).move_to(UP * 0.2).set_fill(AMBAR, 0.12)
            self.play(FadeIn(medio), run_time=0.35)

            # «nadie fijó un orden»
            self.en("nadie")
            q = Text("?", color=AMBAR, weight=BOLD).scale(2.2).move_to(UP * 0.2)
            self.pop(q)

            # «varias maneras distintas»: las fichas saltan al centro y se mezclan
            self.en("maneras")
            copias = [f.copy() for f in (a1, b1, a2, b2)]
            self.play(FadeOut(q), *[FadeIn(c) for c in copias], run_time=0.15)
            fila = VGroup(*[c.copy() for c in copias]).scale(0.6).arrange(RIGHT, buff=0.08).move_to(DOWN * 2.5)
            self.play(*[c.animate.scale(0.6).move_to(d) for c, d in zip(copias, fila)], run_time=0.45, path_arc=0.8)
            orden_b = [copias[i].get_center() for i in (1, 0, 2, 3)]
            orden_c = [copias[i].get_center() for i in (0, 1, 3, 2)]
            for destino in (orden_b, orden_c):
                self.sfx("tick", gain=-20)
                self.play(*[c.animate.move_to(d) for c, d in zip(copias, destino)], run_time=0.3, path_arc=0.8)

    # ------------------------------------------------------------------ b3
    def arbol(self):
        with self.narrar("b3"):
            # «Dibujemos»: la raíz
            self.en("raiz")
            self.corte()
            self.ar = ArbolIntercalado(2, 2, ancho=8.4, alto=6.4, etiquetas=True, radio=0.08, grosor=4)
            self.ar.move_to(LEFT * 1.4)
            for a in self.ar.aristas.values():
                if len(a) > 1:
                    a[1].scale(1.35)
            self.dibujadas = set()
            fantasma = self.ar.copy().set_opacity(0.08)   # < 0,1: el control de encimados lo ignora
            self.play(FadeIn(fantasma), run_time=0.5)
            self.pop(self.ar.raiz)

            # «A uno, A dos, B uno, B dos», etc.: cada camino, ficha por ficha
            caminos = [("A1", "A2", "B1", "B2"), ("A1", "B1", "A2", "B2"), ("B1", "A1", "A2", "B2")]
            for marca, hoja in zip(("c1", "c2", "c3"), caminos):
                self.en(marca)
                self.dibujar(hoja, run_time=1.3)

            # «Y así»: el resto de golpe
            self.en("todos")
            resto = [h for h in self.ar.hojas if h not in caminos]
            anims = []
            for h in resto:
                for k in range(1, 5):
                    if h[:k] not in self.dibujadas:
                        self.dibujadas.add(h[:k])
                        anims += [Create(self.ar.aristas[h[:k]]), FadeIn(self.ar.nodos[h[:k]])]
            self.sfx("whoosh", gain=-16)
            self.play(*anims, run_time=0.6)
            seis = Text("6 caminos", color=AMBAR, weight=BOLD).scale(0.8).move_to([5.3, -2.4, 0])
            self.pop(seis)

            # «de una buena vez»: la cámara se aleja
            self.en("asumir")
            self.play(self.camera.frame.animate.set(width=config.frame_width * 1.08), run_time=0.6)

            # «cada uno de estos caminos»: un punto recorre uno cualquiera
            self.en("cada")
            hoja = self.ar.hojas[4]
            punto = Dot(radius=0.13, color=AMBAR).move_to(self.ar.raiz)
            ruta = VMobject().set_points_as_corners([self.ar.nodos[hoja[:k]].get_center() for k in range(5)])
            self.play(MoveAlongPath(punto, ruta), run_time=1.0, rate_func=linear)
            self.play(FadeOut(punto), run_time=0.15)

            # «tan válida como cualquier otra»: los seis caminos en verde, uno tras otro
            self.en("validas")
            self.sfx("exito", gain=-16)
            self.play(LaggedStart(*[AnimationGroup(*[a.linea.animate.set_color(VERDE) for a in self.ar.camino(h)])
                                    for h in self.ar.hojas], lag_ratio=0.5), run_time=1.3)

            # «Nada, en la definición»
            self.en("definicion")
            doc = documento(TXT).scale(1.1).move_to([5.3, 2.4, 0])
            dt = Text("definición", color=TXT).scale(0.4).next_to(doc, DOWN, buff=0.12)
            self.play(FadeIn(doc, shift=DOWN * 0.3), FadeIn(dt), run_time=0.4)

            # «uno de estos caminos»: el documento busca uno entre las hojas
            self.en("uno")
            buscador = Triangle(color=AMBAR).set_fill(AMBAR, 1).rotate(PI / 2).scale(0.16)
            hojas_pos = [self.ar.nodos[h].get_center() + RIGHT * 0.35 for h in self.ar.hojas]
            buscador.move_to(hojas_pos[0])
            self.play(FadeIn(buscador), run_time=0.15)
            for p in hojas_pos[1:4]:
                self.play(buscador.animate.move_to(p), run_time=0.22)

            # «el correcto»: el rótulo busca dónde posarse
            self.en("correcto")
            rot = VGroup(RoundedRectangle(width=2.7, height=0.7, corner_radius=0.1, color=AMBAR, stroke_width=3)
                         .set_fill(FONDO, 1), Text("el correcto", color=AMBAR, weight=BOLD).scale(0.5))
            rot[1].move_to(rot[0])
            rot.next_to(buscador, RIGHT, buff=0.15)
            self.pop(rot)

            # «los demás no»: el rótulo se tacha y cae
            self.en("demasno")
            t = tachar(rot)
            self.sfx("error", gain=-16)
            self.play(Create(t), run_time=0.2)
            self.play(VGroup(rot, t).animate.shift(DOWN * 4).rotate(-0.6).set_opacity(0), FadeOut(buscador),
                      run_time=0.6, rate_func=rate_functions.ease_in_quad)
            self.play(self.camera.frame.animate.set(width=config.frame_width), run_time=0.4)

    def dibujar(self, hoja: tuple, run_time: float):
        anims = []
        for k in range(1, len(hoja) + 1):
            pref = hoja[:k]
            if pref in self.dibujadas:
                continue
            self.dibujadas.add(pref)
            anims.append(AnimationGroup(Create(self.ar.aristas[pref]), FadeIn(self.ar.nodos[pref], scale=2)))
        self.sfx("tick", gain=-20)
        self.play(LaggedStart(*anims, lag_ratio=0.8), run_time=run_time)
