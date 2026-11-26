import sys, os
sys.path.insert(0, os.path.dirname(__file__))
from pnlib import *

OUT = sys.argv[1]
ONLY = sys.argv[2:]


def out(name):
    return os.path.join(OUT, name + ".svg")


def want(name):
    return not ONLY or name in ONLY


def tr(f, a, b, label, bend=0.0, lcolor=ORANGE, loff=12, wpos=0.5, parts=None):
    s = f.arc(a, b, bend=bend, label=None if parts else label, lcolor=lcolor, loff=loff, wpos=wpos, lsize=12.5)
    if parts:
        f.edge_label_parts(s, parts, t=wpos, off=loff, owner=f"{a}->{b}")


# ---------------------------------------------------------------- AFND -> AFD
if want("lr-afnd-a-afd"):
    f = Fig(640, 600)
    f.title(20, 26, "AFND: reconoce las cadenas que terminan en «ab»", color=RED)
    f.para(20, 46, "Desde q0 con una a se puede ir a q0 o a q1: la transición no es determinista.", 600)
    f.state("q0", 110, 140); f.state("q1", 280, 140); f.state("q2", 450, 140, final=True)
    f.start("q0")
    f.loop("q0", "n", "a, b")
    tr(f, "q0", "q1", "a", loff=-12); tr(f, "q1", "q2", "b", loff=-12)
    f.line(20, 195, 620, 195, dash="4 4")
    f.title(20, 225, "Construcción de subconjuntos", color=BLUE)
    f.line(40, 245, 360, 245, width=1.4)
    for x, s in [(50, "subconjunto"), (180, "a"), (270, "b")]:
        f.text(x, 262, s, 12.5, "start")
    f.line(40, 272, 360, 272, width=1.4)
    rows = [("A = {q0}", "{q0, q1} = B", "{q0} = A", GREY), ("B = {q0, q1}", "{q0, q1} = B", "{q0, q2} = C", GREY),
            ("C = {q0, q2}", "{q0, q1} = B", "{q0} = A", GREEN)]
    for i, (a, b, c, col) in enumerate(rows):
        y = 294 + 22 * i
        f.text(50, y, a, 12.5, "start", col); f.text(180, y, b, 12.5, "start"); f.text(270, y, c, 12.5, "start")
    f.line(40, 350, 360, 350, width=1.4)
    f.para(390, 294, "C es final porque contiene a q2, que era final en el AFND.", 220, color=GREEN)
    f.line(20, 372, 620, 372, dash="4 4")
    f.title(20, 400, "AFD equivalente: reconoce exactamente el mismo lenguaje", color=GREEN)
    f.state("A", 110, 480); f.state("B", 280, 480); f.state("C", 450, 480, final=True)
    f.start("A")
    f.loop("A", "n", "b"); f.loop("B", "n", "a")
    tr(f, "A", "B", "a", loff=-12); tr(f, "B", "C", "b", loff=-12)
    tr(f, "C", "B", "a", bend=-0.3, loff=12)
    tr(f, "C", "A", "b", bend=-0.3, loff=-14)
    f.para(20, 585, "Todo AFND tiene un AFD equivalente: L(AFD) = L(AFND). El precio es que el AFD puede tener hasta 2ⁿ estados.", 600)
    f.h = 600
    f.save(out("lr-afnd-a-afd"), "lr-afnd-a-afd")

# ---------------------------------------------------------------- Moore vs Mealy
if want("lr-moore-vs-mealy"):
    f = Fig(640, 585)
    f.title(20, 26, "Mealy: la salida está en el ARCO")
    f.para(20, 46, "g : E × Q → S · la salida depende del estado y de la entrada", 600)
    f.state("m0", 150, 150); f.state("m1", 360, 150)
    f.start("m0", 46)
    f.text(84, 140, "inicio", 11.5, "middle")
    f.loop("m0", "n", parts=[("b", BLUE), (" / ", GREY), ("0", ORANGE)])
    f.loop("m1", "n", parts=[("a", BLUE), (" / ", GREY), ("0", ORANGE)])
    tr(f, "m0", "m1", None, bend=-0.2, parts=[("a", BLUE), (" / ", GREY), ("0", ORANGE)], loff=-12)
    tr(f, "m1", "m0", None, bend=-0.2, parts=[("b", BLUE), (" / ", GREY), ("1", ORANGE)], loff=-14)
    y = f.para(440, 140, "Emite 1 en el instante en que llega la b que completa «ab».", 180)
    f.para(440, y + 4, "Usa 2 estados.", 180)
    f.line(20, 255, 620, 255, dash="4 4")
    f.title(20, 285, "Moore: la salida está en el ESTADO")
    f.para(20, 305, "g : Q → S · la salida depende sólo del estado en que se está", 600)
    f.state("o0", 130, 420, "q0", sub="0"); f.state("o1", 290, 420, "q1", sub="0"); f.state("o2", 450, 420, "q2", final=True, sub="1")
    f.start("o0", 40)
    f.text(70, 410, "inicio", 11.5, "middle")
    f.loop("o0", "n", "b", lcolor=BLUE)
    f.loop("o1", "s", "a", lcolor=BLUE)
    tr(f, "o0", "o1", "a", lcolor=BLUE, loff=-12)
    tr(f, "o1", "o2", "b", lcolor=BLUE, loff=-12)
    tr(f, "o2", "o1", "a", bend=0.35, lcolor=BLUE, loff=12)
    tr(f, "o2", "o0", "b", bend=-0.5, lcolor=BLUE, loff=-14)
    y = f.para(500, 400, "Hace falta un estado extra (q2) para colgarle la salida 1.", 125)
    f.para(500, y + 4, "Usa 3 estados.", 125)
    f.para(20, 570, "Las dos máquinas son igualmente expresivas: ambas reconocen exactamente los lenguajes regulares.", 600)
    f.save(out("lr-moore-vs-mealy"), "lr-moore-vs-mealy")

# ---------------------------------------------------------------- ER básicas -> AF
if want("lr-re-af"):
    f = Fig(640, 400)
    f.title(20, 26, "Equivalencia entre expresiones regulares básicas y autómatas finitos")

    def cell(x0, y0, re_txt, kind):
        f.text(x0 + 14, y0, re_txt, 13, "start", BLUE, bold=True)
        cy = y0 + 70
        if kind in ("lambda", "a", "a+"):
            f.state(f"{kind}0", x0 + 50, cy, ""); f.state(f"{kind}1", x0 + 160, cy, "", final=True)
            f.start(f"{kind}0", 30)
            tr(f, f"{kind}0", f"{kind}1", "λ" if kind == "lambda" else "a", loff=-12)
            if kind == "a+":
                f.loop(f"{kind}1", "n", "a")
        elif kind == "a*":
            f.state("as", x0 + 60, cy, "", final=True); f.start("as", 30)
            f.loop("as", "n", "a")
            f.text(x0 + 104, cy + 4, "acepta λ", 11.5, "start")
        elif kind == "a+b":
            f.state("ab0", x0 + 50, cy, ""); f.state("ab1", x0 + 160, cy, "", final=True); f.start("ab0", 30)
            tr(f, "ab0", "ab1", "a", bend=-0.3, loff=-12); tr(f, "ab0", "ab1", "b", bend=0.3, loff=12)
        elif kind == "(a+b)*":
            f.state("st", x0 + 60, cy, "", final=True); f.start("st", 30)
            f.loop("st", "n", "a"); f.loop("st", "s", "b")

    cell(20, 70, "λ", "lambda"); cell(225, 70, "a", "a"); cell(430, 70, "a*", "a*")
    f.line(20, 180, 620, 180, dash="4 4")
    cell(20, 215, "a+", "a+"); cell(225, 215, "a + b", "a+b"); cell(430, 215, "(a + b)*", "(a+b)*")
    f.para(20, 385, "El doble círculo marca los estados finales; la flecha suelta de la izquierda marca el estado inicial.", 600)
    f.save(out("lr-re-af"), "lr-re-af")
