"""Escena 06 — Las reglas del juego: seguridad (1 y 4) y progreso (3 y 6)."""
from comun.escena import *


class SeguridadProgreso(EscenaCurso):
    ESCENA = "06"

    def construct(self):
        self.seguridad()
        self.progreso()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ utilidades
    def destacar(self, *indices: int) -> None:
        """Agranda y pinta de ámbar las casillas de esas reglas (1 a 6); las demás se atenúan."""
        anims = []
        for i, r in enumerate(self.reglas):
            if i + 1 in indices:
                anims.append(r.animate.scale(1.25 * self.lado / r.caja.width))
                anims.append(r.caja.animate.set_stroke(AMBAR, 5, opacity=1))
                anims.append(r.num.animate.set_opacity(1))
            else:
                anims.append(r.animate.scale(self.lado / r.caja.width))
                anims.append(r.caja.animate.set_stroke(INK, 3, opacity=0.35))
                anims.append(r.num.animate.set_opacity(0.35))
        self.sfx("tick", gain=-12)
        self.play(*anims, run_time=0.35)

    def casilla(self, n: int) -> VGroup:
        """Copia nueva (sin atenuar) de la casilla de la regla n, en su lugar."""
        return reglas_seis(lado=self.lado)[n - 1].move_to(self.reglas[n - 1]).set(width=self.reglas[n - 1].width)

    def entrar(self, p: Mobject, destino, run_time: float = 0.45) -> None:
        self.play(self.sala.abrir(), run_time=0.2)
        self.play(p.animate.move_to(destino), run_time=run_time)
        self.play(self.sala.cerrar(), run_time=0.2)

    # ------------------------------------------------------------------ b1
    def seguridad(self):
        with self.narrar("b1"):
            # «La primera»
            self.en("r1")
            self.lado = 1.0
            self.reglas = reglas_seis(lado=self.lado).to_edge(UP, buff=0.35)
            self.play(FadeIn(self.reglas, shift=DOWN * 0.3), run_time=0.3)
            self.destacar(1)

            # «de todos los procesos» — cuatro procesos en fila ante la puerta
            self.en("procesos")
            sala = self.sala = SalaPuerta(4.2, 3.2).move_to(RIGHT * 2.6 + DOWN * 0.4)
            colores = [AZUL, MAGENTA, CIAN, VERDE]
            cola = VGroup(*[proceso_mini(f"P{i + 1}", c) for i, c in enumerate(colores)])
            for i, p in enumerate(cola):
                p.move_to(sala.fuera + LEFT * 1.7 * i)
            self.play(FadeIn(sala), LaggedStart(*[FadeIn(p, shift=RIGHT * 0.3) for p in cola], lag_ratio=0.12),
                      run_time=0.6)

            # «el mismo recurso»
            self.en("mismo")
            rec = celda_memoria("", "1", ancho=0.9, alto=0.6).scale(1.2).move_to(sala.dentro + UP * 0.8 + RIGHT * 1.1)
            self.play(sala.piso.animate.set_fill(AMBAR, 0.15), FadeIn(rec, scale=0.6), run_time=0.4)

            # «solo uno puede tener permiso» — P1 recibe una tilde y entra
            self.en("permiso")
            ok = tilde(VERDE, 0.6).next_to(cola[0], UP, buff=0.2)
            self.play(Create(ok), run_time=0.25)
            self.play(FadeOut(ok), run_time=0.15)
            self.entrar(cola[0], sala.dentro + DOWN * 0.5 + LEFT * 0.3)

            # «en un momento dado» — candado en la puerta
            self.en("momento")
            self.cand = candado(AMBAR).scale(0.6).next_to(sala.puerta, LEFT, buff=0.12)
            self.pop(self.cand, sonido="tick")

            # «La cuarta»
            self.en("r4")
            self.destacar(4)

            # «nadie está dentro» — P1 sale y la sala queda vacía
            self.en("vacia")
            self.play(FadeOut(self.cand), sala.abrir(), run_time=0.25)
            self.play(cola[0].animate.move_to(sala.fuera + UP * 1.3), run_time=0.35)
            self.play(FadeOut(cola[0], shift=UP * 0.6), sala.cerrar(), run_time=0.3)

            # «sin demora» — el primero de la fila entra de golpe
            self.en("demora")
            self.sfx("whoosh")
            self.entrar(cola[1], sala.dentro + DOWN * 0.5 + LEFT * 0.3, run_time=0.3)
            self.play(cola[2].animate.move_to(sala.fuera), cola[3].animate.move_to(sala.fuera + LEFT * 1.7),
                      run_time=0.3)

            # «reglas de seguridad» — 1 y 4 bajo un escudo
            self.en("seguridad")
            esc = escudo(VERDE).scale(1.1)
            n1 = self.casilla(1)
            n4 = self.casilla(4)
            grupo = VGroup(esc, VGroup(n1.copy(), n4.copy()).arrange(RIGHT, buff=0.2)).arrange(RIGHT, buff=0.3)
            et = Text("seguridad", color=VERDE, weight=BOLD).scale(0.6)
            bloque = VGroup(grupo, et).arrange(DOWN, buff=0.25).scale(1.25).move_to(LEFT * 4.4 + DOWN * 2.7)
            self.play(FadeIn(esc, scale=0.5), n1.animate.move_to(grupo[1][0]), n4.animate.move_to(grupo[1][1]),
                      run_time=0.5)
            self.pop(et)
            self.seg = VGroup(esc, n1, n4, et)

            # «nada malo pase»
            self.en("nadamalo")
            self.play(Flash(esc, color=VERDE, flash_radius=0.9), esc.animate(rate_func=there_and_back).scale(1.2),
                      run_time=0.5)
            self.cola = cola

    # ------------------------------------------------------------------ b2
    def progreso(self):
        sala, cola = self.sala, self.cola
        with self.narrar("b2"):
            # «La tercera y la sexta»
            self.en("r36")
            self.destacar(3, 6)

            # «reglas de progreso» — 3 y 6 bajo una flecha hacia delante
            self.en("progreso")
            fl = Arrow(LEFT * 0.7, RIGHT * 0.7, color=AZUL, stroke_width=10, buff=0)
            n3 = self.casilla(3)
            n6 = self.casilla(6)
            grupo = VGroup(fl, VGroup(n3.copy(), n6.copy()).arrange(RIGHT, buff=0.2)).arrange(RIGHT, buff=0.3)
            et = Text("progreso", color=AZUL, weight=BOLD).scale(0.6)
            bloque = VGroup(grupo, et).arrange(DOWN, buff=0.25).scale(1.25).move_to(LEFT * 0.6 + DOWN * 2.9)
            self.play(GrowArrow(fl), n3.animate.move_to(grupo[1][0]), n6.animate.move_to(grupo[1][1]), run_time=0.5)
            self.pop(et)

            # «algo bueno»
            self.en("bueno")
            self.play(fl.animate(rate_func=there_and_back).shift(RIGHT * 0.5), run_time=0.5)

            # «La tercera dice» — de vuelta a la sala: un proceso esperando fuera
            self.en("r3")
            self.play(FadeOut(VGroup(self.seg, fl, n3, n6, et)), FadeOut(cola[3]), run_time=0.3)
            self.destacar(3)

            # «para siempre para entrar» — un reloj que gira sobre el que espera
            self.en("siempre")
            rel = reloj().scale(0.8).next_to(cola[2], UP, buff=0.3)
            self.play(FadeIn(rel, scale=0.5), run_time=0.2)
            self.sfx("tick", gain=-16)
            self.play(Rotate(rel.minutero, angle=-4 * PI, about_point=rel.aro.get_center()), run_time=1.0)

            # «nada de interbloqueo», «nada de inanición»
            palabras = {}
            for marca, txt, y in [("interbloqueo", "interbloqueo", -2.3), ("inanicion", "inanición", -3.2)]:
                self.en(marca)
                t = Text(txt, color=TXT, weight=BOLD).scale(0.7).move_to(LEFT * 4.3 + UP * y)
                self.play(FadeIn(t, shift=UP * 0.2), run_time=0.25)
                self.play(Create(tachar(t)), run_time=0.25)
                palabras[marca] = t

            # «La sexta» — foco al de dentro
            self.en("r6")
            self.play(FadeOut(rel), run_time=0.15)
            self.destacar(6)
            foco = SurroundingRectangle(cola[1], color=AMBAR, buff=0.12, corner_radius=0.12, stroke_width=5)
            self.play(Create(foco), run_time=0.3)

            # «el límite del otro lado» — una línea de salida dentro de la sala
            self.en("limite")
            salida = DashedLine(sala.piso.get_left() + RIGHT * 0.6 + UP * 1.4, sala.piso.get_left() + RIGHT * 0.6 + DOWN * 1.4,
                                color=VERDE, stroke_width=5)
            self.play(Create(salida), run_time=0.4)

            # «quedarse en su sección crítica»
            self.en("quedarse")
            self.play(FadeOut(foco), run_time=0.2)
            self.flotar(cola[1], amp=0.1, periodo=1.6)

            # «tiempo finito» — un reloj da una vuelta y el proceso sale
            self.en("finito")
            rel2 = reloj().scale(0.7).next_to(cola[1], UP, buff=0.25)
            self.play(FadeIn(rel2, scale=0.5), run_time=0.2)
            self.sfx("tick", gain=-16)
            self.play(Rotate(rel2.minutero, angle=-2 * PI, about_point=rel2.aro.get_center()), run_time=0.8)
            cola[1].clear_updaters()
            self.play(FadeOut(rel2), sala.abrir(), run_time=0.2)
            self.play(cola[1].animate.move_to(sala.fuera + UP * 1.3), run_time=0.35)
            self.play(FadeOut(cola[1], shift=UP * 0.5), sala.cerrar(), run_time=0.25)

            # «el programador»
            self.en("programador")
            ing = ingeniero(AZUL).scale(1.4).move_to(RIGHT * 5.9 + DOWN * 3.0)
            self.pop(ing)

            # «no el mecanismo» — el candado de la puerta se atenúa
            self.en("mecanismo")
            cand = candado(AMBAR).scale(0.6).move_to(self.cand)
            self.play(FadeIn(cand), run_time=0.15)
            self.play(cand.animate.fade(0.75), self.reglas[5].caja.animate.set_stroke(AMBAR, 7), run_time=0.4)
