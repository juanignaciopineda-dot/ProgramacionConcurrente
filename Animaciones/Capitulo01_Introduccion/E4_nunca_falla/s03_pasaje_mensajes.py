"""Escena 03 — Pasaje de mensajes: enviar y recibir, bloqueantes o no."""
from comun.escena import *


class PasajeMensajes(EscenaCurso):
    ESCENA = "03"

    def montaje(self) -> None:
        """Emisor a la izquierda, receptor a la derecha y el canal entre ellos."""
        self.em = proceso_mini("P1", AZUL).scale(1.9).move_to(LEFT * 5.0 + UP * 0.4)
        self.re = proceso_mini("P2", MAGENTA).scale(1.9).move_to(RIGHT * 5.0 + UP * 0.4)
        self.et_em = Text("emisor", color=INK, weight=BOLD).scale(0.8).next_to(self.em, DOWN, buff=0.3)
        self.et_re = Text("receptor", color=INK, weight=BOLD).scale(0.8).next_to(self.re, DOWN, buff=0.3)
        self.canal = VGroup(*[Line(LEFT * 3.4, RIGHT * 3.4, color=INK, stroke_width=7).shift(UP * (0.4 + dy))
                              for dy in (0.6, -0.6)])

    def nuevo_sobre(self) -> VGroup:
        return sobre(TXT).scale(0.8).move_to(self.canal.get_left() + RIGHT * 0.5)

    def construct(self):
        with self.narrar("b1"):
            # «El pasaje de mensajes» — emisor, receptor y el canal
            self.en("canal")
            self.montaje()
            self.play(FadeIn(self.em, shift=RIGHT * 0.3), FadeIn(self.re, shift=LEFT * 0.3),
                      FadeIn(self.et_em), FadeIn(self.et_re), Create(self.canal), run_time=0.6)

            # «sincronización de fábrica» — el canal se resalta
            self.en("fabrica")
            self.play(self.canal.animate.set_color(AMBAR), run_time=0.4)

            # «recibir un mensaje» — el receptor espera: no llega nada
            self.en("recibir")
            q = Text("?", color=MAGENTA, weight=BOLD).scale(1.7).next_to(self.re, UP, buff=0.35)
            self.pop(q)
            self.play(Wiggle(self.re, scale_value=1.08), run_time=0.5)

            # «lo haya enviado» — el emisor manda el sobre; recién entonces llega
            self.en("enviado")
            sb = self.nuevo_sobre()
            self.play(FadeIn(sb, scale=0.5), run_time=0.2)
            self.sfx("whoosh")
            self.play(sb.animate.move_to(self.canal.get_right() + LEFT * 0.5), run_time=0.8,
                      rate_func=rate_functions.ease_in_out_sine)
            ok = tilde(VERDE, 0.9).move_to(q)
            self.play(FadeOut(q), Create(ok), run_time=0.3)
            self.sfx("exito", gain=-16)

            # «qué le pasa a cada uno» — plano nuevo, un «?» debajo de cada uno
            self.en("cada")
            self.corte()
            self.montaje()
            self.add(self.em, self.re, self.et_em, self.et_re, self.canal)
            q1 = Text("?", color=TXT, weight=BOLD).scale(1.6).next_to(self.et_em, DOWN, buff=0.35)
            q2 = Text("?", color=TXT, weight=BOLD).scale(1.6).next_to(self.et_re, DOWN, buff=0.35)
            self.pop(q1, q2)

            # «Enviar» — foco en el emisor
            self.en("enviar")
            r_env = Text("enviar", color=AMBAR, weight=BOLD).scale(1.1).next_to(self.em, UP, buff=0.35)
            self.play(FadeOut(q1), FadeOut(q2), run_time=0.15)
            self.pop(r_env)

            # «bloquear al emisor» — suelta el sobre y se congela, con candado
            self.en("bloquea")
            sb = self.nuevo_sobre()
            cand = candado(INK).scale(1.0).next_to(self.et_em, DOWN, buff=0.35)
            self.em.save_state()
            self.add(sb)
            self.sfx("tick", gain=-12)
            self.play(sb.animate.move_to(self.canal.get_center()), self.em.animate.fade(0.65),
                      FadeIn(cand, scale=0.5), run_time=1.2, rate_func=rate_functions.linear)

            # «efectivamente llegue» — el sobre llega; el candado se abre
            self.en("llega")
            self.play(sb.animate.move_to(self.canal.get_right() + LEFT * 0.5), run_time=0.5)
            self.play(Restore(self.em), FadeOut(cand, scale=0.5), run_time=0.3)

            # «seguir de inmediato» — otra vez: suelta el sobre y sigue avanzando
            self.en("sigue")
            sb2 = self.nuevo_sobre()
            fl = Arrow(self.et_em.get_bottom() + DOWN * 0.15, self.et_em.get_bottom() + DOWN * 2.4, color=VERDE,
                       stroke_width=12, buff=0, max_tip_length_to_length_ratio=0.2)
            self.play(FadeOut(sb), FadeIn(sb2, scale=0.5), run_time=0.2)
            self.sfx("whoosh", gain=-16)
            self.play(sb2.animate.move_to(self.canal.get_center()), GrowArrow(fl),
                      VGroup(self.em, self.et_em, r_env).animate.shift(DOWN * 0.35), run_time=0.8)

            # «Recibir puede bloquear» — foco en el receptor
            self.en("recibe")
            r_rec = Text("recibir", color=AMBAR, weight=BOLD).scale(1.1).next_to(self.re, UP, buff=0.35)
            self.play(FadeOut(sb2), r_env.animate.fade(0.7), fl.animate.fade(0.7), run_time=0.2)
            self.pop(r_rec)

            # «hasta que llegue algo» — congelado con un reloj; llega el sobre
            self.en("espera")
            rj = reloj(TXT).scale(1.5).next_to(self.et_re, DOWN, buff=0.4)
            self.re.save_state()
            self.play(self.re.animate.fade(0.65), FadeIn(rj, scale=0.5), run_time=0.25)
            self.play(Rotate(rj.minutero, angle=-2 * PI, about_point=rj.aro.get_center()), run_time=0.9)
            sb3 = self.nuevo_sobre()
            self.play(FadeIn(sb3, scale=0.5), run_time=0.15)
            self.play(sb3.animate.move_to(self.canal.get_right() + LEFT * 0.5), run_time=0.5)
            self.play(Restore(self.re), FadeOut(rj), run_time=0.3)

            # «las manos vacías» — otra vez: el receptor sigue sin sobre
            self.en("vacias")
            fl2 = Arrow(self.et_re.get_bottom() + DOWN * 0.15, self.et_re.get_bottom() + DOWN * 2.4, color=VERDE,
                        stroke_width=12, buff=0, max_tip_length_to_length_ratio=0.2).shift(LEFT * 0.9)
            vacio = Text("∅", color=AMBAR, weight=BOLD).scale(2.4).next_to(fl2, RIGHT, buff=0.4)
            self.play(FadeOut(sb3), GrowArrow(fl2), run_time=0.4)
            self.pop(vacio)

        self.wait(1.0)
        self.limpiar()
