import sys, os
sys.path.insert(0, os.path.dirname(__file__))
from pnlib import *

OUT = sys.argv[1]
ONLY = sys.argv[2:]


def out(name):
    return os.path.join(OUT, name + ".svg")


def want(name):
    return not ONLY or name in ONLY


def ciclo(f, cx, cy, R):
    f.place("pri", cx, cy - R, 1, "primavera", "n")
    f.place("ver", cx + R, cy, 0, "verano", "e")
    f.place("oto", cx, cy + R, 0, "otoño", "s")
    f.place("inv", cx - R, cy, 0, "invierno", "w")
    d = R * 0.72
    f.trans("T1", cx + d, cy - d, "h", "T1", (16, -12, "start"), rot=45)
    f.trans("T2", cx + d, cy + d, "h", "T2", (16, 20, "start"), rot=-45)
    f.trans("T3", cx - d, cy + d, "h", "T3", (-16, 20, "end"), rot=45)
    f.trans("T4", cx - d, cy - d, "h", "T4", (-16, -12, "end"), rot=-45)
    for a, b in [("pri", "T1"), ("T1", "ver"), ("ver", "T2"), ("T2", "oto"), ("oto", "T3"), ("T3", "inv"), ("inv", "T4"), ("T4", "pri")]:
        f.arc(a, b)


# ---------------------------------------------------------------- autónomas / no autónomas
if want("pn-autonomas"):
    f = Fig(660, 560)
    f.title(20, 28, "(a) Autónoma")
    f.para(20, 48, "Ciclo estacional: no se sabe cuándo dispara", 280)
    ciclo(f, 165, 190, 82)
    f.line(345, 20, 345, 310, dash="4 4")
    f.title(365, 28, "(b) No autónoma: sincronizada")
    f.para(365, 48, "El disparo espera un evento externo", 280)
    f.place("det", 420, 95, 1, "motor detenido", "e")
    f.trans("on", 420, 155, "h", "orden de inicio", (26, 4, "start"))
    f.place("mar", 420, 215, 0, "motor en marcha", "e")
    f.trans("off", 420, 275, "h", "orden de parada", (26, 4, "start"))
    f.arc("det", "on"); f.arc("on", "mar"); f.arc("mar", "off")
    f.arc("off", "det", via=[(380, 275), (380, 95)])
    f.line(20, 330, 640, 330, dash="4 4")
    f.title(20, 358, "(c) No autónoma: temporizada")
    f.para(20, 378, "Semáforo: cada transición dispara un tiempo fijo después de sensibilizarse", 620)
    y = 445
    f.place("verde", 70, y, 1, "verde", "s")
    f.trans("t1", 160, y, "v", "60 s", "n")
    f.place("ambar", 250, y, 0, "ámbar", "s")
    f.trans("t2", 340, y, "v", "5 s", "n")
    f.place("rojo", 430, y, 0, "rojo", "s")
    f.trans("t3", 520, y, "v", "45 s", "n")
    for a, b in [("verde", "t1"), ("t1", "ambar"), ("ambar", "t2"), ("t2", "rojo"), ("rojo", "t3")]:
        f.arc(a, b)
    f.arc("t3", "verde", via=[(520, 505), (25, 505), (25, y)])
    f.para(560, 425, "La red es la misma; cambia qué gobierna el disparo.", 90, size=11)
    f.save(out("pn-autonomas"), "pn-autonomas")

# ---------------------------------------------------------------- grafo de estado vs grafo de eventos
if want("pn-estado-vs-eventos"):
    f = Fig(680, 470)
    f.title(20, 28, "Grafo de estado")
    f.para(20, 48, "Cada TRANSICIÓN tiene exactamente una plaza de entrada y una de salida.", 290)
    f.place("A1", 165, 120, 1, "P1", "n")
    f.place("A2", 85, 270, 0, "P2", "w")
    f.place("A3", 245, 270, 0, "P3", "e")
    f.trans("a1", 215, 185, "h", "", "e", rot=55)
    f.trans("a2", 165, 300, "h", "", "s")
    f.trans("a3", 115, 185, "h", "", "w", rot=-55)
    f.arc("A1", "a1"); f.arc("a1", "A3"); f.arc("A3", "a2"); f.arc("a2", "A2"); f.arc("A2", "a3"); f.arc("a3", "A1")
    f.para(20, 355, "Con EXACTAMENTE UNA ficha equivale a un autómata de 3 estados: la ficha es el estado actual. Con dos fichas ya no, porque la red admite 6 marcados distintos.", 290)
    f.line(340, 20, 340, 455, dash="4 4")
    f.title(360, 28, "Grafo de eventos (grafo marcado)")
    f.para(360, 48, "Cada PLAZA tiene exactamente una transición de entrada y una de salida.", 300)
    X = 500
    f.place("B0", X, 100, 1, "P0", "w")
    f.trans("b1", X, 150, "h", "T1", "w")
    f.place("B1", X - 55, 205, 0, "P1", "w")
    f.place("B2", X + 55, 205, 0, "P2", "e")
    f.trans("b2", X - 55, 260, "h", "T2", "w")
    f.trans("b3", X + 55, 260, "h", "T3", "e")
    f.place("B3", X - 55, 315, 0, "P3", "w")
    f.place("B4", X + 55, 315, 0, "P4", "e")
    f.trans("b4", X, 370, "h", "T4", "w")
    for a, b in [("B0", "b1"), ("b1", "B1"), ("b1", "B2"), ("B1", "b2"), ("B2", "b3"), ("b2", "B3"), ("b3", "B4"), ("B3", "b4"), ("B4", "b4")]:
        f.arc(a, b)
    f.arc("b4", "B0", via=[(X + 130, 370), (X + 130, 100)])
    f.para(360, 412, "Nunca hay conflicto: cada plaza alimenta a una sola transición. Hay paralelismo y sincronización, pero no elección.", 300)
    f.save(out("pn-estado-vs-eventos"), "pn-estado-vs-eventos")

# ---------------------------------------------------------------- modelos básicos
if want("pn-modelos-basicos"):
    f = Fig(680, 430)
    f.title(20, 28, "Co-begin / co-end")
    f.para(20, 48, "Arranque en paralelo y terminación sincronizada", 300)
    X = 165
    f.place("c0", X, 95, 1, "", "n")
    f.trans("cb", X, 145, "h", "co-begin", "e")
    f.place("cA", X - 55, 200, 0, "A", "w")
    f.place("cB", X + 55, 200, 0, "B", "e")
    f.trans("ce", X, 255, "h", "co-end", "e")
    f.place("c1", X, 310, 0, "", "n")
    for a, b in [("c0", "cb"), ("cb", "cA"), ("cb", "cB"), ("cA", "ce"), ("cB", "ce"), ("ce", "c1")]:
        f.arc(a, b)
    f.para(20, 365, "Co-end sólo dispara cuando A y B tienen ficha: es una barrera.", 300)
    f.line(340, 20, 340, 415, dash="4 4")
    f.title(360, 28, "Sincronización por señal")
    f.para(360, 48, "P2 no puede avanzar hasta que P1 le avise", 300)
    f.place("s1", 410, 95, 1, "P1", "w")
    f.trans("st1", 410, 150, "h", "", "e")
    f.place("s1b", 410, 210, 0, "", "n")
    f.place("sig", 510, 200, 0, "señal", "n")
    f.place("s2", 610, 95, 1, "P2", "e")
    f.trans("st2", 610, 255, "h", "", "e")
    f.place("s2b", 610, 315, 0, "", "n")
    f.arc("s1", "st1"); f.arc("st1", "s1b"); f.arc("st1", "sig"); f.arc("sig", "st2"); f.arc("s2", "st2"); f.arc("st2", "s2b")
    f.para(360, 365, "La transición de P2 tiene DOS entradas: su propia plaza de control y la señal. Es un semáforo inicializado en 0.", 300)
    f.save(out("pn-modelos-basicos"), "pn-modelos-basicos")

# ---------------------------------------------------------------- exclusión mutua con N recursos + buffer acotado
if want("pn-mutex-buffer"):
    f = Fig(700, 400)
    f.title(20, 28, "Exclusión mutua con N recursos")
    f.para(20, 48, "Es exactamente el semáforo de N permisos", 320)
    f.place("f1", 80, 100, 1, "fuera", "n")
    f.trans("e1", 80, 160, "h", "", "e")
    f.place("sc1", 80, 220, 0, "SC", (0, 5))
    f.trans("x1", 80, 280, "h", "", "e")
    f.place("rec", 180, 190, 3, "recursos", "n", r=22)
    f.place("f2", 280, 100, 1, "fuera", "n")
    f.trans("e2", 280, 160, "h", "", "e")
    f.place("sc2", 280, 220, 0, "SC", (0, 5))
    f.trans("x2", 280, 280, "h", "", "e")
    for a, b in [("f1", "e1"), ("e1", "sc1"), ("sc1", "x1"), ("f2", "e2"), ("e2", "sc2"), ("sc2", "x2"),
                 ("rec", "e1"), ("rec", "e2"), ("x1", "rec"), ("x2", "rec")]:
        f.arc(a, b)
    f.arc("x1", "f1", via=[(40, 280), (40, 100)])
    f.arc("x2", "f2", via=[(320, 280), (320, 100)])
    f.para(20, 320, "Con 1 ficha: exclusión mutua estricta. Con 3 fichas, hasta tres procesos a la vez. Invariante: M(SC izq) + M(SC der) + M(recursos) = 3.", 320)
    f.line(355, 20, 355, 385, dash="4 4")
    f.title(375, 28, "Buffer acotado")
    f.para(375, 48, "Productor/consumidor con capacidad 3", 300)
    f.place("lib", 420, 170, 3, "libres", "n", r=22)
    f.trans("prod", 525, 170, "v", "producir", "n")
    f.place("ocu", 630, 170, 0, "ocupados", "n", r=22)
    f.trans("cons", 525, 280, "v", "consumir", "s")
    f.arc("lib", "prod"); f.arc("prod", "ocu"); f.arc("ocu", "cons", bend=-0.25); f.arc("cons", "lib", bend=-0.25)
    f.para(375, 350, "«producir» sólo dispara si queda un hueco libre en el buffer; «consumir», sólo si hay algo ocupado.", 300)
    f.save(out("pn-mutex-buffer"), "pn-mutex-buffer")

# ---------------------------------------------------------------- coloreada
if want("pn-coloreada"):
    f = Fig(660, 375)
    f.title(20, 28, "Red ordinaria (desplegada)")
    f.para(20, 48, "Una plaza y una transición por cada color", 300)
    for x, c, col in [(90, "b", BLUE), (230, "g", GREEN)]:
        f.place(f"P1{c}", x, 105, 1, f"P1{c}", "n", tcolors=[col])
        f.trans(f"T1{c}", x, 165, "h", f"T1{c}", "w" if c == "b" else "e")
        f.place(f"P2{c}", x, 225, 0, f"P2{c}", "s")
        f.arc(f"P1{c}", f"T1{c}"); f.arc(f"T1{c}", f"P2{c}")
    f.line(330, 20, 330, 290, dash="4 4")
    f._marker("ah", PURPLE)
    f.body.append(f'<line x1="355" y1="145" x2="440" y2="145" stroke="{PURPLE}" stroke-width="2" marker-end="url(#ah-c86dd7)"/>')
    f.body.append(f'<line x1="440" y1="190" x2="355" y2="190" stroke="{PURPLE}" stroke-width="2" marker-end="url(#ah-c86dd7)"/>')
    f.text(397, 135, "plegado", 11.5, color=PURPLE)
    f.text(397, 212, "despliegue", 11.5, color=PURPLE)
    f.title(470, 28, "Red coloreada (plegada)")
    f.para(470, 48, "Los colores viajan en las fichas", 180)
    f.place("P1", 545, 105, 2, "P1 = {P1b, P1g}", "n", tcolors=[BLUE, GREEN])
    f.trans("T1", 545, 165, "h", "T1", "w")
    f.place("P2", 545, 225, 0, "P2 = {P2b, P2g}", "s")
    f.arc("P1", "T1", label="id", loff=-12); f.arc("T1", "P2", label="id", loff=-12)
    f.para(20, 315, "Los arcos llevan FUNCIONES. Con la función identidad, disparar T1 en el color <b> pide al menos una ficha <b> en P1, la saca y pone una <b> en P2: exactamente el disparo de T1b en la red desplegada. El despliegue completo de una red coloreada da siempre UNA red ordinaria única.", 620)
    f.save(out("pn-coloreada"), "pn-coloreada")

# ---------------------------------------------------------------- vivacidad y deadlock (Venn corregido)
if want("pn-vida-deadlock"):
    f = Fig(660, 460)
    f.title(20, 28, "Vivacidad y deadlock")
    f.para(20, 48, "Quasi-vivas y libres de deadlock se cruzan, y las vivas están en la intersección.", 620)
    f.rect(20, 66, 620, 278, rx=10, color=RED, width=2)
    f.text(36, 88, "Todas las RdP", 12, "start", RED, bold=True)
    f.ellipse(250, 212, 165, 108, color=BLUE)
    f.ellipse(410, 212, 165, 108, color=ORANGE)
    f.text(150, 200, "Quasi-vivas", 12.5, "middle", BLUE, bold=True)
    f.para(150, 219, "cada transición\ndispara alguna vez", 150, size=11, anchor="middle")
    f.text(510, 200, "Libres de deadlock", 12.5, "middle", ORANGE, bold=True)
    f.para(510, 219, "siempre queda alguna\ntransición sensibilizada", 160, size=11, anchor="middle")
    f.body.append(f'<circle cx="330" cy="215" r="50" fill="none" stroke="{GREEN}" stroke-width="2"/>')
    f.circles.append((330, 215, 50, "vivas"))
    f.text(330, 200, "Vivas", 12.5, "middle", GREEN, bold=True)
    f.para(330, 217, "cada transición\nsiempre puede\nvolver a disparar", 90, size=10, anchor="middle", lh=13)
    y = f.para(20, 370, "Quasi-viva ⇏ libre de deadlock: puede disparar cada transición una vez y después trabarse.", 620)
    y = f.para(20, y, "Libre de deadlock ⇏ quasi-viva: una transición puede no disparar nunca y el resto seguir funcionando.", 620)
    y = f.para(20, y, "Viva ⇒ quasi-viva y libre de deadlock. Todo depende de M₀, y agregar fichas no siempre ayuda.", 620)
    f.save(out("pn-vida-deadlock"), "pn-vida-deadlock")

# ---------------------------------------------------------------- sifón y trampa
if want("pn-sifon-trampa"):
    f = Fig(660, 460)
    f.title(20, 28, "Sifón {P1, P2}: sólo puede PERDER fichas")
    f.para(20, 48, "•{P1, P2} = {T1} ⊆ {P1, P2}• = {T1, T2}: toda transición que le da fichas también le saca.", 620)
    f.place("P1", 80, 120, 1, "P1", "s")
    f.trans("T1", 150, 120, "v", "T1", "n")
    f.place("P2", 220, 120, 0, "P2", "s")
    f.trans("T2", 330, 120, "v", "T2", "n")
    f.place("P3", 420, 120, 0, "P3", "s")
    f.arc("P1", "T1"); f.arc("T1", "P2"); f.arc("P2", "T2"); f.arc("T2", "P3")
    f.rect(45, 84, 210, 88, color=RED, rx=30, dash="5 4")
    f.para(20, 205, "Si dispara T2, la ficha sale del sifón para siempre: vacío, ninguna de sus transiciones de salida vuelve a habilitarse.", 620)
    f.line(20, 236, 640, 236, dash="4 4")
    f.title(20, 264, "Trampa {P4, P5}: sólo puede GANAR fichas")
    f.para(20, 284, "{P4, P5}• = {T3, T4} ⊆ •{P4, P5} = {T3, T4, T5}: toda transición que le saca fichas también le da.", 620)
    f.place("P6", 80, 350, 1, "P6", "s")
    f.trans("T5", 150, 350, "v", "T5", "n")
    f.place("P4", 225, 350, 0, "P4", "s")
    f.trans("T3", 300, 350, "v", "T3", "n")
    f.place("P5", 375, 350, 0, "P5", "s")
    f.trans("T4", 300, 430, "h", "T4", "e")
    f.arc("P6", "T5"); f.arc("T5", "P4"); f.arc("P4", "T3"); f.arc("T3", "P5")
    f.arc("P5", "T4", bend=-0.2); f.arc("T4", "P4", bend=-0.2)
    f.rect(190, 314, 222, 88, color=GREEN, rx=30, dash="5 4")
    f.para(450, 340, "Una vez que la trampa tiene una ficha, nunca vuelve a vaciarse.", 190)
    f.save(out("pn-sifon-trampa"), "pn-sifon-trampa")

# ---------------------------------------------------------------- barbería
if want("pn-barberia"):
    f = Fig(1100, 500)
    f.title(20, 22, "Barbería: 2 peluqueros, 5 bancos y una caja (los arcos con círculo son inhibidores)")
    Y = 250
    f.place("P0", 60, Y, 17, "P0 afuera", "s")
    f.trans("T0", 150, Y, "v", "T0", (12, -22, "start"))
    f.place("P12", 150, 150, 1, "P12 puerta", "n")
    f.place("P3", 330, 405, 5, "P3 bancos", "s")
    f.place("P1", 240, Y, 0, "P1", "s")
    f.trans("T1", 320, Y, "v", "T1", (12, -22, "start"))
    f.place("P2", 410, Y, 0, "P2 sala", "n")
    f.trans("T2", 500, 160, "v", "T2", (-10, -20, "end"))
    f.place("P4", 500, 70, 0, "P4 libre 1", "w")
    f.trans("T3", 500, 340, "v", "T3", (-10, 32, "end"))
    f.place("P6", 500, 430, 0, "P6 libre 2", "w")
    f.place("P7", 590, 160, 1, "P7 corta 1", "n")
    f.place("P5", 590, 340, 1, "P5 corta 2", "s")
    f.trans("T4", 680, 160, "v", "T4", (10, -12, "start"))
    f.trans("T5", 680, 340, "v", "T5", "s")
    f.place("P9", 630, Y, 1, "P9 caja", "w")
    f.place("P8", 770, Y, 0, "P8 cobrando", (16, -30))
    f.trans("T6", 860, Y, "v", "T6", (12, -22, "start"))
    f.place("P11", 860, 150, 1, "P11 salida", "n")
    f.place("P10", 950, Y, 0, "P10", "s")
    f.trans("T7", 1040, Y, "v", "T7", (12, -22, "start"))
    f.place("P13", 860, 380, 0, "P13 cobro terminado", "e")
    f.trans("T8", 815, 70, "v", "T8", "n")
    f.trans("T9", 770, 440, "v", "T9", "s")
    for a, b in [("P0", "T0"), ("P12", "T0"), ("P3", "T0"), ("T0", "P1"), ("P1", "T1"), ("T1", "P12"), ("T1", "P2"),
                 ("P2", "T2"), ("P2", "T3"), ("P4", "T2"), ("P6", "T3"), ("T2", "P7"), ("T3", "P5"), ("T2", "P3"), ("T3", "P3"),
                 ("P7", "T4"), ("P5", "T5"), ("P9", "T4"), ("P9", "T5"), ("T4", "P8"), ("T5", "P8"), ("P8", "T6"),
                 ("P11", "T6"), ("T6", "P10"), ("T6", "P13"), ("P10", "T7"), ("T7", "P11"), ("P13", "T8"), ("P13", "T9")]:
        f.arc(a, b)
    f.arc("T6", "P9", bend=-0.42)
    f.arc("T7", "P0", via=[(1040, 36), (60, 36)])
    f.arc("T8", "P4", bend=0.1); f.arc("P4", "T8", bend=0.1, kind="inhib", color=RED)
    f.arc("T9", "P6", bend=-0.1); f.arc("P6", "T9", bend=-0.1, kind="inhib", color=RED)
    f.arc("P13", "T5", kind="inhib", color=RED)
    f.arc("P13", "T4", kind="inhib", color=RED, via=[(735, 330), (735, 115), (680, 115)])
    f.save(out("pn-barberia"), "pn-barberia")
