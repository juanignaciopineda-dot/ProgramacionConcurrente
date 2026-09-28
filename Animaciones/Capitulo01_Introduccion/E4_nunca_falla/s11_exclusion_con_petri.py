"""Escena 11 — La exclusión mutua modelada con una red de Petri: cinco lugares y un mutex."""
from comun.escena import *


class ExclusionConPetri(EscenaCurso):
    ESCENA = "11"

    def construct(self):
        self.armar()
        self.disparos()
        self.wait(1.0)
        self.limpiar()

    # ------------------------------------------------------------------ b1
    def armar(self):
        with self.narrar("b1"):
            # «Usemos esto…» — el nombre de la herramienta, mientras arranca la frase
            rp = self.palabra("Red de Petri", AMBAR, 1.3)
            self.play(FadeIn(rp, scale=0.8), run_time=0.4)

            # «el mismo problema» — la sala con puerta del episodio anterior
            self.en("problema")
            sala = SalaPuerta(4.0, 2.6).move_to(DOWN * 0.3)
            p1 = proceso_mini("P1", AZUL).move_to(sala.dentro)
            p2 = proceso_mini("P2", MAGENTA).move_to(sala.fuera)
            self.play(rp.animate.scale(0.7).move_to(UP * 3.1), FadeIn(sala), FadeIn(p1), FadeIn(p2), run_time=0.45)

            # «la exclusión mutua»
            self.en("exclusion")
            ex = Text("exclusión mutua", color=TXT, weight=BOLD).scale(0.9).next_to(sala, DOWN, buff=0.4)
            self.pop(ex)

            # «Tenemos dos procesos» — dos columnas: P1 y P2
            self.en("dos")
            self.corte()
            self.red = red = RedMutex(fichas=False).scale(1.05).move_to(DOWN * 0.25)
            h1 = Text("P1", color=AZUL, weight=BOLD).scale(1.0).move_to([red.f1.get_x(), 3.55, 0])
            h2 = Text("P2", color=MAGENTA, weight=BOLD).scale(1.0).move_to([red.f2.get_x(), 3.55, 0])
            self.pop(h1, h2)

            # «uno puede estar» — la columna de P1 se resalta
            self.en("p1")
            zona = SurroundingRectangle(VGroup(red.f1, red.sc1, red.e1, red.s1), color=AZUL, buff=0.3,
                                        corner_radius=0.25, stroke_width=3)
            self.play(Create(zona), run_time=0.4)

            # «fuera de la sección crítica» — el lugar «P1 fuera», con una ficha
            self.en("fuera")
            red.f1.poner_fichas(1)
            self.pop(red.f1)

            # «o dentro» — «P1 en SC», con entrar y salir
            self.en("dentro")
            arcos1 = VGroup(*[red.arcos[k] for k in [("f1", "e1"), ("e1", "sc1"), ("sc1", "s1"), ("s1", "f1")]])
            self.play(FadeIn(red.sc1, scale=0.6), FadeIn(red.e1), FadeIn(red.s1), run_time=0.35)
            self.play(Create(arcos1), run_time=0.45)

            # «Lo mismo el» — la columna de P2, en espejo
            self.en("mismo")
            red.f2.poner_fichas(1)
            arcos2 = VGroup(*[red.arcos[k] for k in [("f2", "e2"), ("e2", "sc2"), ("sc2", "s2"), ("s2", "f2")]])
            col2 = VGroup(red.f2, red.sc2, red.e2, red.s2)
            self.play(FadeOut(zona), FadeIn(col2, shift=LEFT * 0.4), Create(arcos2), run_time=0.6)

            # «un quinto lugar» — mutex al centro, con sus arcos
            self.en("quinto")
            arcosm = VGroup(*[red.arcos[k] for k in [("mutex", "e1"), ("mutex", "e2"), ("s1", "mutex"), ("s2", "mutex")]])
            self.play(GrowFromCenter(red.mutex), run_time=0.35)
            self.play(Create(arcosm), run_time=0.5)
            # a partir de aquí la red entera está en escena como un solo objeto
            self.remove(red.f1, red.sc1, red.e1, red.s1, red.f2, red.sc2, red.e2, red.s2, red.mutex,
                        arcos1, arcos2, arcosm, col2)
            self.add(red)

            # «sola ficha» — cae una ficha en mutex
            self.en("ficha")
            self.sfx("tick", gain=-10)
            soltar_ficha(self, red.mutex, run_time=0.45)

            # «solo hay uno» — brilla; «1»
            self.en("uno")
            uno = Text("1", color=AMBAR, weight=BOLD).scale(1.1).next_to(red.mutex.circulo, UR, buff=0.2)
            self.resaltar(red.mutex.circulo, color=AMBAR, escala=1.25, run_time=0.4)
            self.pop(uno)
            self.uno = uno

    # ------------------------------------------------------------------ b2
    def disparos(self):
        red = self.red
        with self.narrar("b2"):
            # «el proceso uno quiere entrar»
            self.en("quiere")
            self.play(FadeOut(self.uno), run_time=0.15)
            self.resaltar(red.e1.barra, color=AMBAR, escala=1.3, run_time=0.5)

            # «necesita dos fichas» — sus dos arcos de entrada
            self.en("dosfichas")
            entrada = VGroup(red.arcos[("f1", "e1")], red.arcos[("mutex", "e1")])
            self.play(entrada.animate.set_color(AMBAR).set_stroke(width=5), run_time=0.35)

            # «proceso uno fuera»
            self.en("f1")
            self.play(red.f1.puntos.animate(rate_func=there_and_back).scale(1.8), run_time=0.45)

            # «la del mutex»
            self.en("fm")
            self.play(red.mutex.puntos.animate(rate_func=there_and_back).scale(1.8), run_time=0.45)

            # «las consume las dos» — disparo
            self.en("consume")
            self.play(entrada.animate.set_color(INK).set_stroke(width=2.2), run_time=0.15)
            self.sfx("whoosh", gain=-16)
            red.entrar(self, 1, run_time=0.9)

            # «una ficha nueva» — la ficha en «P1 en SC»
            self.en("nueva")
            self.sfx("pop")
            self.play(red.sc1.puntos.animate(rate_func=there_and_back).scale(1.8), run_time=0.4)

            # «proceso uno en sección» — P1 está dentro
            self.en("p1sc")
            self.resaltar(red.sc1.circulo, color=AZUL, escala=1.2, run_time=0.5)

            # «Mientras esa ficha del mutex» — mutex, vacío
            self.en("mientras")
            self.resaltar(red.mutex.circulo, color=AMBAR, escala=1.2, run_time=0.5)

            # «no vuelva» — parpadea; un reloj pequeño al lado
            self.en("novuelve")
            rj = reloj(TXT).scale(0.55).next_to(red.mutex.circulo, UR, buff=0.15)
            self.play(FadeIn(rj, scale=0.5), run_time=0.25)
            self.play(Rotate(rj.minutero, angle=-PI, about_point=rj.aro.get_center()), run_time=0.4)

            # «la transición de entrada» — la de P2
            self.en("t2")
            self.resaltar(red.e2.barra, color=AMBAR, escala=1.3, run_time=0.5)

            # «segunda ficha» — su arco desde mutex, rojo y punteado
            self.en("segunda")
            arco = red.arcos[("mutex", "e2")]
            roto = DashedVMobject(arco[0].copy().set_color(ROJO).set_stroke(width=5), num_dashes=10)
            arco.save_state()
            self.play(arco.animate.fade(1), FadeIn(roto), run_time=0.3)

            # «está en conflicto» — la transición se pone roja, con una cruz
            self.en("conflicto")
            x = cruz(ROJO, 0.45).next_to(red.e2.barra, UP, buff=0.25)
            self.sfx("error", gain=-14)
            self.play(red.e2.barra.animate.set_color(ROJO), FadeIn(x, scale=1.6), run_time=0.35)

            # «puede dispararse» — intenta y rebota
            self.en("nopuede")
            self.play(Wiggle(red.e2.barra, scale_value=1.2, rotation_angle=0.08), run_time=0.6)

            # «sale y devuelve» — P1 sale: una ficha a «P1 fuera» y otra a mutex
            self.en("sale")
            self.play(FadeOut(x), FadeOut(rj), red.e2.barra.animate.set_color(INK), run_time=0.2)
            red.salir(self, 1, run_time=0.9)
            self.play(FadeOut(roto), Restore(arco), run_time=0.2)

            # «el proceso dos tiene» — foco en P2
            self.en("tiene")
            self.resaltar(red.f2.circulo, red.e2.barra, color=MAGENTA, escala=1.2, run_time=0.5)

            # «su oportunidad de entrar» — su transición se pone verde
            self.en("oportunidad")
            self.play(red.e2.barra.animate.set_color(VERDE), run_time=0.3)
            self.sfx("exito", gain=-14)
