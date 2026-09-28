"""Escena 02 — Dos hilos, un contador: el planteo y los tres pasos de la suma."""
from comun.escena import *

PASOS = [("LEER", AZUL), ("SUMAR", AMBAR), ("ESCRIBIR", VERDE)]


def columna_pasos(color) -> VGroup:
    return VGroup(*[caja(t, color, ancho=2.2, alto=0.72, scale=0.5, relleno=0.15) for t, _ in PASOS]
                  ).arrange(DOWN, buff=0.2)


class Contador(EscenaCurso):
    ESCENA = "02"

    def construct(self):
        self.planteo()
        self.tres_pasos()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def planteo(self):
        with self.narrar("b1"):
            # «Bajemos todo» — lo visto cae y se funde en un punto
            self.en("bajar")
            minis = VGroup(cpu().scale(0.9), caja_proceso("P1", AZUL, 1.6, 0.9),
                           ArbolIntercalado(2, 2, ancho=2.6, alto=1.8, etiquetas=False))
            minis.arrange(RIGHT, buff=0.9).move_to(UP * 2.4)
            self.sfx("whoosh")
            self.play(LaggedStart(*[FadeIn(m, shift=DOWN * 0.8) for m in minis], lag_ratio=0.2), run_time=0.6)
            punto = Dot(ORIGIN, radius=0.12, color=AMBAR)
            self.play(*[m.animate.scale(0.05).move_to(ORIGIN).set_opacity(0) for m in minis],
                      FadeIn(punto), run_time=0.6)
            self.remove(*minis)

            # «un ejemplo muy concreto»
            self.en("concreto")
            vacia = Square(2.0, color=TXT, stroke_width=5).set_fill(FONDO, 1)
            self.play(ReplacementTransform(punto, vacia), run_time=0.4)

            # «más citados» — documentos alrededor que la señalan
            self.en("citados")
            angulos = [PI * k / 4 + PI / 8 for k in (0, 1, 3, 4, 5, 7)]
            docs = VGroup(*[documento().scale(1.3).move_to(3.1 * np.array([np.cos(a) * 1.5, np.sin(a), 0]))
                            for a in angulos])
            flechas = VGroup(*[flecha(d.get_center() * 0.8, d.get_center() * 0.35, INK) for d in docs])
            self.play(LaggedStart(*[FadeIn(d, scale=0.6) for d in docs], lag_ratio=0.12),
                      LaggedStart(*[GrowArrow(f) for f in flechas], lag_ratio=0.12), run_time=0.8)

            # «cuando se entiende este»
            self.en("este")
            self.play(vacia.animate.set_stroke(AMBAR, 6), docs.animate.set_opacity(0.3),
                      flechas.animate.set_opacity(0.3), run_time=0.4)

            # «el resto del curso» — los documentos en fila, encendiéndose en cadena
            self.en("curso")
            fila = docs.copy().set_opacity(1).arrange(RIGHT, buff=0.7).move_to(DOWN * 2.5)
            self.play(FadeOut(flechas), vacia.animate.shift(UP * 1.0),
                      *[d.animate.move_to(f).set_opacity(1) for d, f in zip(docs, fila)], run_time=0.5)
            self.play(LaggedStart(*[d[0].animate(rate_func=there_and_back).set_stroke(AMBAR, 6) for d in docs],
                                  lag_ratio=0.25), run_time=0.9)

            # «una variable compartida» — corte al montaje
            self.en("variable")
            self.corte()
            m = montaje_contador("0")
            self.play(FadeIn(VGroup(m.celda[0], m.celda.caja), scale=0.8), run_time=0.35)

            # «que arranca en cero»
            self.en("cero")
            self.pop(m.celda.contenido)

            # «Dos hilos»
            self.en("doshilos")
            self.play(FadeIn(m.h1, shift=RIGHT), FadeIn(m.h2, shift=LEFT), run_time=0.5)

            # «sumarle uno»
            self.en("sumarle")
            mas1 = Text("+1", color=AZUL, weight=BOLD).scale(1.3).next_to(m.h1, UP, buff=0.3)
            mas2 = Text("+1", color=MAGENTA, weight=BOLD).scale(1.3).next_to(m.h2, UP, buff=0.3)
            self.pop(mas1, mas2)

            # «Si todo sale bien»
            self.en("bien")
            self.play(mas1.animate.shift(RIGHT * 0.9), mas2.animate.shift(LEFT * 0.9), run_time=0.5)

            # «terminar en dos»
            self.en("dos")
            dos = Text("2", color=VERDE, weight=BOLD).scale(1.6).set_opacity(0.45)
            dos.next_to(m.celda.caja, UP, buff=0.25)
            self.play(FadeIn(dos, scale=1.4), run_time=0.4)

            # «Veamos si sale bien.»
            self.en("veamos")
            self.play(self.camera.frame.animate.set(width=config.frame_width * 0.9),
                      run_time=self.margen(), rate_func=linear)

            self.m, self.mas, self.dos = m, VGroup(mas1, mas2), dos

    # ------------------------------------------------------------------ b2
    def tres_pasos(self):
        m = self.m
        with self.narrar("b2"):
            # «del primer episodio»
            self.en("ep1")
            regla = caja("1 línea = 3 pasos", AMBAR, alto=0.7, scale=0.5, relleno=0.15).to_edge(UP, buff=0.35)
            regla.shift(LEFT * 4.2)
            self.play(FadeOut(self.dos), FadeIn(regla, shift=DOWN * 0.3),
                      self.camera.frame.animate.set(width=config.frame_width), run_time=0.4)

            # «tres pasos separados» — cada «+1» se despliega en tres casillas
            self.en("tres")
            col1 = columna_pasos(AZUL).move_to(LEFT * 1.9 + DOWN * 0.9)
            col2 = columna_pasos(MAGENTA).move_to(RIGHT * 1.9 + DOWN * 0.9)
            self.sfx("pop")
            self.play(ReplacementTransform(self.mas[0], col1), ReplacementTransform(self.mas[1], col2), run_time=0.5)

            for i, marca in enumerate(["leer", "incrementar", "escribir"]):
                self.en(marca)
                color = PASOS[i][1]
                self.sfx("tick", gain=-12)
                self.play(*[c[i][0].animate(rate_func=there_and_back).set_fill(color, 0.8) for c in (col1, col2)],
                          *[c[i].animate(rate_func=there_and_back).scale(1.12) for c in (col1, col2)],
                          run_time=0.45)

            # «intercalar esos pasos» — las seis casillas bajan a una fila, mezcladas
            self.en("intercalar")
            orden = [col1[0], col2[0], col1[1], col1[2], col2[1], col2[2]]
            destino = VGroup(*[o.copy() for o in orden]).arrange(RIGHT, buff=0.15)
            k = min(1.0, 13.2 / destino.width)
            destino.scale(k).move_to(DOWN * 3.3)
            self.sfx("whoosh")
            self.play(*[o.animate.scale(k).move_to(d) for o, d in zip(orden, destino)], run_time=0.8,
                      rate_func=rate_functions.ease_in_out_cubic)
            fila = VGroup(*orden)

            # «la peor manera posible»
            self.en("peor")
            marco = SurroundingRectangle(fila, color=ROJO, buff=0.15, corner_radius=0.12, stroke_width=5)
            self.sfx("alarma", gain=-16)
            self.play(Create(marco), run_time=0.4)

            # «nada raro» — el código de cada hilo está bien
            self.en("nada")
            t1 = tilde(VERDE, 0.9).next_to(m.h1, UP, buff=0.35)
            t2 = tilde(VERDE, 0.9).next_to(m.h2, UP, buff=0.35)
            self.play(Create(t1), Create(t2), run_time=0.35)

            # «la mala suerte del orden» — el dado cae sobre la fila y rueda
            self.en("suerte")
            d = dado().scale(0.9).move_to(UP * 5 + LEFT * 3)
            self.sfx("tick", gain=-12, offset=0.4)
            self.sfx("tick", gain=-14, offset=0.7)
            self.play(d.animate.move_to(DOWN * 1.9 + LEFT * 0.2).rotate(-2 * PI), run_time=0.6,
                      rate_func=rate_functions.ease_out_bounce)
            self.play(d.animate.shift(RIGHT * 1.6).rotate(-PI), run_time=0.5)
