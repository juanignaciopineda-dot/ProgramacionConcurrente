"""Escena 01 — Gancho: el ordenador te engaña; miles de ejecuciones, una sola rompe todo."""
from comun.escena import *


class Gancho(EscenaCurso):
    ESCENA = "01"

    def construct(self):
        with self.narrar("b1"):
            # «Ahora mismo»
            self.en("ahora")
            pc = ordenador().scale(5.8).move_to(DOWN * 0.45)
            self.pop(pc, sonido="impacto")

            # «te está engañando»
            self.en("engana")
            duda = Text("¿?", color=AMBAR, weight=BOLD).scale(1.3).next_to(pc, UP, buff=0.1)
            self.play(pc.pantalla.animate(rate_func=there_and_back).set_fill(TXT, 0.45), run_time=0.35)
            self.pop(duda)

            # «muchas cosas»
            self.en("cosas")
            wins = ventanas_multitarea().arrange(RIGHT, buff=0.15)
            wins.scale_to_fit_width(pc.pantalla.width * 0.92).move_to(pc.pantalla)
            self.play(FadeOut(duda), LaggedStart(*[FadeIn(w, scale=0.6) for w in wins], lag_ratio=0.15),
                      run_time=0.5)
            self.sfx("latido", gain=-16)
            self.play(*[w.animate(rate_func=there_and_back).scale(1.08) for w in wins], run_time=0.4)

            # «y no»: en realidad se turnan, de a una
            self.en("no")
            self.sfx("error", gain=-14)
            for k in range(6):
                self.play(*[w.animate.set_opacity(1 if i == k % 3 else 0.12) for i, w in enumerate(wins)],
                          run_time=0.12)

            # «de verdad las hace a la vez»: corte a cuatro núcleos
            self.en("paralelo")
            self.corte()
            nucs = VGroup(*[nucleo(AZUL, 2.0) for _ in range(4)]).arrange(RIGHT, buff=0.5)
            self.add(nucs)
            self.sfx("pop")
            self.play(*[n.caja.animate.set_fill(AZUL, 0.75) for n in nucs], run_time=0.35)

            # «algo peor»
            self.en("peor")
            flash = FullScreenRectangle().set_fill(ROJO, 0.3).set_stroke(width=0)
            self.sfx("alarma", gain=-16)
            self.play(*[n.caja.animate.set_fill(ROJO, 0.6).set_stroke(ROJO) for n in nucs],
                      FadeIn(flash, rate_func=there_and_back), run_time=0.5)
            self.remove(flash)

            # «el mismo programa»
            self.en("programa")
            self.corte()
            doc = documento(TXT).scale(2.2).move_to(LEFT * 5.6)
            self.play(FadeIn(doc, shift=RIGHT * 0.5), run_time=0.35)

            # «miles de formas distintas»: el árbol de ejecuciones brota del programa
            self.en("miles")
            arbol = ArbolIntercalado(3, 3, ancho=10.0, alto=7.2, etiquetas=False, radio=0.035, grosor=2.2)
            arbol.next_to(doc, RIGHT, buff=0.15)
            niveles = {}
            for pref, ar in arbol.aristas.items():
                niveles.setdefault(len(pref), []).extend([ar, arbol.nodos[pref]])
            self.sfx("whoosh")
            self.add(arbol.raiz)
            self.play(LaggedStart(*[AnimationGroup(*[Create(a) for a in niveles[k]])
                                    for k in sorted(niveles)], lag_ratio=0.55), run_time=1.6)

            # «basta una»
            self.en("una")
            hoja = arbol.hojas[7]
            camino = arbol.camino(hoja)
            resto = [a for p, a in arbol.aristas.items() if a not in camino]
            self.play(*[a.animate.set_opacity(0.12) for a in resto],
                      *[a.animate.set_color(ROJO).set_stroke(width=6) for a in camino], run_time=0.5)

            # «romperlo todo»
            self.en("romper")
            flash = FullScreenRectangle().set_fill(ROJO, 0.35).set_stroke(width=0)
            self.sfx("impacto", gain=-10)
            self.play(FadeIn(flash, rate_func=there_and_back),
                      VGroup(*camino).animate(rate_func=there_and_back).shift(UP * 0.12), run_time=0.4)
            self.remove(flash)

        # título del episodio, solo
        tit = self.texto_centrado("La ilusión de", "hacerlo todo a la vez", escala=1.6)
        todo = Group(*[m for m in self.mobjects if m is not self.camera.frame])
        self.sfx("whoosh")
        self.play(FadeOut(todo, scale=1.3), FadeIn(tit, scale=0.8), run_time=0.6)
        self.wait(1.4)
        self.limpiar()
