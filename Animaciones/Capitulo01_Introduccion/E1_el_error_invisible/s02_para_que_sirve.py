"""Escena 02 — ¿Para qué sirve? Sistemas críticos."""
from comun.escena import *


class ParaQueSirve(EscenaCurso):
    ESCENA = "02"

    def construct(self):
        with self.narrar("b1"):
            # «Programación Concurrente»
            self.en("curso")
            curso = Text("Programación Concurrente", color=TXT, weight=BOLD).scale(1.0)
            self.pop(curso)

            # «meternos de lleno»: la cámara se zambulle en el nombre
            self.en("lleno")
            self.sfx("whoosh")
            self.play(self.camera.frame.animate.set(width=2.0).move_to(curso), run_time=0.7,
                      rate_func=rate_functions.ease_in_cubic)
            self.corte()
            self.camera.frame.move_to(ORIGIN).set(width=config.frame_width)

            # «sistemas críticos»
            self.en("criticos")
            iconos = VGroup(monitor_signos(), avion(), cohete(), banco())
            for ic in iconos:
                ic.scale_to_fit_height(2.0)
            iconos.arrange(RIGHT, buff=1.0)
            self.play(LaggedStart(*[FadeIn(ic, shift=UP * 0.5) for ic in iconos], lag_ratio=0.2),
                      run_time=0.9)

            # «no se pueden dar el lujo de fallar»
            self.en("fallar")
            for _ in range(2):
                self.sfx("latido", gain=-12)
                self.play(*[Indicate(ic, color=ROJO, scale_factor=1.15) for ic in iconos], run_time=0.7)

            # «por qué existe»
            self.en("pregunta")
            q = Text("¿Para qué sirve?", color=AMBAR, weight=BOLD).scale(1.3)
            self.play(LaggedStart(*[FadeOut(ic, shift=DOWN * 0.4) for ic in iconos], lag_ratio=0.1),
                      run_time=0.5)
            self.pop(q)

            # «arranquemos con una historia»
            self.en("historia")
            self.sfx("whoosh")
            self.play(FadeOut(q, scale=0.3, shift=LEFT * 3), run_time=0.5)
