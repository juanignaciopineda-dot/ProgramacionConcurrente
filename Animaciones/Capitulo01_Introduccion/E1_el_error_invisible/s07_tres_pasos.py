"""Escena 07 — `contador += 1` no es un paso: son tres (leer, sumar, escribir)."""
from comun.escena import *

PASOS = [("LEER", AZUL), ("SUMAR", AMBAR), ("ESCRIBIR", VERDE)]


def paso(txt: str, color, ancho: float = 2.6) -> VGroup:
    return caja(txt, color, ancho=ancho, alto=0.8, scale=0.6, relleno=0.18)


def columna(color) -> VGroup:
    return VGroup(*[paso(t, color, 2.4) for t, _ in PASOS]).arrange(DOWN, buff=0.25)


def bloque_codigo(color) -> VGroup:
    return caja("contador += 1", color, ancho=2.8, alto=0.9, scale=0.5, relleno=0.18)


class TresPasos(EscenaCurso):
    ESCENA = "07"

    def construct(self):
        self.desarme()
        self.consecuencia()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def desarme(self):
        with self.narrar("b1"):
            self.en("giro")
            flash = FullScreenRectangle().set_fill(TXT, 0.35).set_stroke(width=0)
            self.sfx("whoosh")
            self.play(FadeIn(flash, rate_func=there_and_back), run_time=0.35)
            self.remove(flash)

            self.en("todo")
            anillos = VGroup(*[Circle(radius=0.2, color=AMBAR, stroke_width=4) for _ in range(3)])
            self.play(LaggedStart(*[a.animate.scale(40).set_stroke(opacity=0) for a in anillos],
                                  lag_ratio=0.25), run_time=1.2)
            self.remove(anillos)

            self.en("pequeno")
            lp = lupa().scale(2.2).move_to(UP * 0.4)
            self.pop(lp)

            self.en("codigo")
            codigo = Text("contador += 1", color=TXT, font="Consolas", weight=BOLD).scale(1.5)
            self.play(lp.animate.scale(0.5).move_to(UP * 2.6 + LEFT * 3.8).set_opacity(0.35), run_time=0.35)
            self.pop(codigo)

            self.en("linea")
            sub = Underline(codigo, color=AMBAR, stroke_width=5)
            self.play(Create(sub), run_time=0.35)

            self.en("una")
            marco = SurroundingRectangle(codigo, color=AMBAR, buff=0.35, corner_radius=0.15, stroke_width=5)
            uno = Text("1 acción", color=AMBAR, weight=BOLD).scale(0.6).next_to(marco, DOWN, buff=0.25)
            self.play(ReplacementTransform(sub, marco), FadeIn(uno, shift=UP * 0.2), run_time=0.45)

            self.en("no")
            tres = VGroup(*[paso(t, c) for t, c in PASOS]).arrange(RIGHT, buff=0.35).move_to(UP * 2.4)
            self.sfx("impacto", gain=-10)
            self.play(ReplacementTransform(VGroup(codigo, marco), tres), FadeOut(uno), FadeOut(lp), run_time=0.6)
            mem = celda_memoria("contador", "0", ancho=1.6, alto=0.9).scale(1.3).move_to(LEFT * 3.6 + DOWN * 1.3)
            etq_mem = Text("memoria", color=INK).scale(0.45).next_to(mem, DOWN, buff=0.25)
            c = cpu().scale(1.2).move_to(RIGHT * 3.4 + DOWN * 1.2)
            reg = registro("registro", "").move_to(c.caja.get_center() + DOWN * 0.25)
            self.play(FadeIn(mem), FadeIn(etq_mem), FadeIn(c), FadeIn(reg), run_time=0.4)

            self.en("tres")
            nums = VGroup(*[Text(str(i + 1), color=TXT, weight=BOLD).scale(0.5).next_to(p, UP, buff=0.15)
                            for i, p in enumerate(tres)])
            for i in range(3):
                self.sfx("tick", gain=-14, offset=i * 0.15)
            self.play(LaggedStart(*[FadeIn(n, scale=2) for n in nums], lag_ratio=0.3), run_time=0.6)

            self.en("leer")
            self.play(tres[0].animate.scale(1.15), mem.caja.animate(rate_func=there_and_back).set_stroke(AZUL, 6), run_time=0.4)

            self.en("trae")
            viajero = mem.contenido.copy()
            self.sfx("whoosh", gain=-16)
            self.play(viajero.animate.move_to(reg.caja).set_color(AZUL), run_time=0.6)
            self.play(tres[0].animate.scale(1 / 1.15).set_opacity(0.45), run_time=0.2)

            self.en("adentro")
            self.zoom(c, 6.0, run_time=0.6)

            self.en("sumar")
            self.play(tres[1].animate.scale(1.15), run_time=0.2)
            nuevo = Text("1", color=AMBAR, weight=BOLD).scale(0.6).move_to(reg.caja)
            mas = Text("+1", color=AMBAR, weight=BOLD).scale(0.55).next_to(reg.caja, RIGHT, buff=0.2)
            self.sfx("pop")
            self.play(FadeIn(mas, shift=LEFT * 0.2), run_time=0.25)
            self.play(ReplacementTransform(viajero, nuevo), FadeOut(mas), run_time=0.35)
            self.play(tres[1].animate.scale(1 / 1.15).set_opacity(0.45), run_time=0.2)

            self.en("escribir")
            self.zoom_reset(run_time=0.5)
            self.play(tres[2].animate.scale(1.15), run_time=0.2)
            vuelta = nuevo.copy()
            self.sfx("whoosh", gain=-16)
            self.play(vuelta.animate.move_to(mem.caja).set_color(VERDE), FadeOut(mem.contenido), run_time=0.6)

            self.en("vuelta")
            self.sfx("exito", gain=-16)
            self.play(mem.caja.animate(rate_func=there_and_back).set_stroke(VERDE, 6), Flash(mem.caja, color=VERDE),
                      tres[2].animate.scale(1 / 1.15), run_time=0.5)

    # ------------------------------------------------------------------ b2
    def consecuencia(self):
        with self.narrar("b2"):
            self.en("guardar")
            self.corte()
            regla = Text("1 línea = 3 pasos", color=AMBAR, weight=BOLD).scale(1.5)
            self.pop(regla, sonido="impacto")

            self.en("adelante")
            self.play(regla.animate.scale(0.5).to_edge(UP, buff=0.4).shift(LEFT * 1.2), run_time=0.4)
            futuro = Text("→ episodio 3", color=INK, weight=BOLD).scale(0.55).next_to(regla, RIGHT, buff=0.4)
            self.play(FadeIn(futuro, shift=LEFT * 0.3), run_time=0.35)

            self.en("dos")
            col_a = columna(AZUL).move_to(LEFT * 4.2 + DOWN * 0.3)
            col_b = columna(MAGENTA).move_to(RIGHT * 4.2 + DOWN * 0.3)
            eti_a = Text("proceso A", color=AZUL, weight=BOLD).scale(0.55).next_to(col_a, UP, buff=0.3)
            eti_b = Text("proceso B", color=MAGENTA, weight=BOLD).scale(0.55).next_to(col_b, UP, buff=0.3)
            self.play(LaggedStart(FadeIn(VGroup(eti_a, col_a), shift=RIGHT),
                                  FadeIn(VGroup(eti_b, col_b), shift=LEFT), lag_ratio=0.3), run_time=0.6)

            self.en("mismo")
            self.play(VGroup(eti_a, col_a).animate.shift(RIGHT * 1.6),
                      VGroup(eti_b, col_b).animate.shift(LEFT * 1.6), run_time=0.5)

            self.en("unpaso")
            blo_a = bloque_codigo(AZUL).move_to(col_a)
            blo_b = bloque_codigo(MAGENTA).move_to(col_b)
            self.play(ReplacementTransform(col_a, blo_a), ReplacementTransform(col_b, blo_b), run_time=0.45)

            self.en("sontres")
            col_a = columna(AZUL).move_to(blo_a)
            col_b = columna(MAGENTA).move_to(blo_b)
            self.sfx("pop")
            self.play(ReplacementTransform(blo_a, col_a), ReplacementTransform(blo_b, col_b), run_time=0.45)

            self.en("entre")
            orden = [col_a[0], col_b[0], col_a[1], col_b[1], col_b[2], col_a[2]]
            destino = VGroup(*[m.copy() for m in orden]).arrange(DOWN, buff=0.12).scale(0.85).move_to(DOWN * 0.3)
            self.sfx("whoosh")
            self.play(*[m.animate.move_to(d).scale(0.85) for m, d in zip(orden, destino)],
                      FadeOut(eti_a), FadeOut(eti_b), run_time=0.9, rate_func=rate_functions.ease_in_out_cubic)

            self.en("cualquier")
            preguntas = VGroup(*[Text("?", color=AMBAR, weight=BOLD).scale(1.4) for _ in range(3)])
            for q, p in zip(preguntas, [LEFT * 3.2 + UP * 0.6, RIGHT * 3.2 + DOWN * 0.2, LEFT * 2.6 + DOWN * 2.4]):
                q.move_to(p)
            self.pop(*preguntas)
            for q in preguntas:
                self.flotar(q, amp=0.1, periodo=1.8)
