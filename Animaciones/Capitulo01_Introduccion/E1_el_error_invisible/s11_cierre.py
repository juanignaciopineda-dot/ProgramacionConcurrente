"""Escena 11 — Resumen y la trampa que abre el episodio 2."""
from comun.escena import *


def mini_estados() -> VGroup:
    pos = [(-1.3, 0.5), (-0.45, 0.5), (0.45, 0.5), (1.3, 0.5), (0, -0.5)]
    return VGroup(*[RoundedRectangle(width=0.6, height=0.35, corner_radius=0.08, color=INK, stroke_width=2)
                    .set_fill(FONDO, 1).move_to([x, y, 0]) for x, y in pos])


class Cierre(EscenaCurso):
    ESCENA = "11"

    def construct(self):
        with self.narrar("b1"):
            self.en("resumen")
            flash = FullScreenRectangle().set_fill(TXT, 0.3).set_stroke(width=0)
            self.sfx("whoosh")
            self.play(FadeIn(flash, rate_func=there_and_back), run_time=0.3)
            self.remove(flash)

            self.en("sabemos")
            slots = VGroup(*[RoundedRectangle(width=4.0, height=3.0, corner_radius=0.2, color=INK, stroke_width=2)
                             for _ in range(3)]).arrange(RIGHT, buff=0.4)
            self.play(LaggedStart(*[Create(s) for s in slots], lag_ratio=0.2), run_time=0.6)

            self.en("r1")
            c = cpu().scale(0.9)
            punta = Triangle(color=AMBAR).set_fill(AMBAR, 1).rotate(-PI / 2).scale(0.16)
            punta.next_to(c, LEFT, buff=0.2)
            uno = VGroup(c, punta).move_to(slots[0])
            self.pop(uno)

            self.en("r2")
            dos = self.texto_centrado("1 línea", "=", "3 pasos", color=AMBAR, escala=0.75).move_to(slots[1])
            self.pop(dos)

            self.en("r3")
            tres = mini_estados().scale(1.5).move_to(slots[2])
            self.pop(tres)

            self.en("salta")
            saltarin = Dot(radius=0.1, color=CIAN).move_to(tres[0])
            self.add(saltarin)
            self.play(Succession(*[saltarin.animate(run_time=0.25).move_to(tres[k]) for k in (1, 2, 4, 1, 2)]),
                      run_time=1.3)
            self.remove(saltarin)

            self.en("uno")
            ejec = tres[2]
            punto = Dot(radius=0.1, color=AMBAR).move_to(ejec)
            self.play(ejec.animate.set_fill(AMBAR, 0.4).set_color(AMBAR), FadeIn(punto, scale=2), run_time=0.4)
            self.play(Indicate(ejec, color=AMBAR, scale_factor=1.4), run_time=0.5)

            self.en("trampa")
            flash = FullScreenRectangle().set_fill(ROJO, 0.25).set_stroke(width=0)
            self.sfx("alarma", gain=-16)
            resumen = VGroup(slots, uno, dos, tres, punto)
            self.play(resumen.animate.scale(0.5).to_edge(UP, buff=0.3),
                      FadeIn(flash, rate_func=there_and_back), run_time=0.6)
            self.remove(flash)

            self.en("solouno")
            self.play(Indicate(ejec, color=AMBAR, scale_factor=1.6), Flash(punto, color=AMBAR), run_time=0.6)

            self.en("posible")
            q = Text("?", color=AMBAR, weight=BOLD).scale(4).move_to(DOWN * 0.6)
            self.pop(q, sonido="impacto")

            self.en("musica")
            mus = ventana_app("música", MAGENTA, nota_musical()).scale(1.4).move_to(LEFT * 4.4 + DOWN * 0.8)
            self.play(FadeOut(q, scale=0.4), run_time=0.2)
            self.pop(mus)

            self.en("navegador")
            barras = VGroup(*[Line(LEFT * 0.8, RIGHT * (0.8 - 0.3 * (i % 2)), color=INK, stroke_width=5)
                              for i in range(4)]).arrange(DOWN, buff=0.18, aligned_edge=LEFT)
            nav = ventana_app("navegador", AZUL, barras).scale(1.4).move_to(DOWN * 0.8)
            self.pop(nav)

            self.en("video")
            vid = ventana_app("vídeo", ROJO, triangulo_play()).scale(1.4).move_to(RIGHT * 4.4 + DOWN * 0.8)
            self.pop(vid)

            self.en("vez")
            for _ in range(2):
                self.sfx("latido", gain=-16)
                self.play(*[v.animate(rate_func=there_and_back).scale(1.08) for v in (mus, nav, vid)], run_time=0.5)

            self.en("desarmar")
            # cada ventana se corta en tiras verticales que se intercalan en una sola fila:
            # adelanto visual del interleaving del episodio 2
            tiras = []
            for v, col in ((mus, MAGENTA), (nav, AZUL), (vid, ROJO)):
                ancho = v.marco.width / 4
                for k in range(4):
                    r = Rectangle(width=ancho, height=v.marco.height, stroke_width=0).set_fill(col, 0.7)
                    r.move_to(v.marco.get_left() + RIGHT * (ancho * (k + 0.5)))
                    tiras.append((k, r))
            self.play(FadeOut(VGroup(mus, nav, vid)), *[FadeIn(r) for _, r in tiras], run_time=0.3)
            orden = sorted(tiras, key=lambda kr: kr[0])     # 1ª tira de cada una, luego la 2ª...
            destino = VGroup(*[r.copy() for _, r in orden]).arrange(RIGHT, buff=0.04).scale(0.8).move_to(DOWN * 0.8)
            self.sfx("whoosh")
            self.play(*[r.animate.move_to(d).scale(0.8) for (_, r), d in zip(orden, destino)], run_time=0.9,
                      rate_func=rate_functions.ease_in_out_cubic)

            self.en("proximo")
            self.corte()
            ep = Text("Episodio 2", color=AMBAR, weight=BOLD).scale(0.8).move_to(UP * 0.9)
            tit = self.texto_centrado("La ilusión de", "hacerlo todo a la vez", escala=1.1).next_to(ep, DOWN, buff=0.4)
            self.sfx("whoosh")
            self.play(FadeIn(ep, shift=DOWN * 0.3), FadeIn(tit, scale=0.8), run_time=0.6)
            self.flotar(tit)
        self.wait(1.2)
