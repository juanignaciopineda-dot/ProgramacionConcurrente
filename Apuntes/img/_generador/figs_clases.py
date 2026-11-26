"""Clases de redes (un ejemplo que es y otro que no es) y la cena de los filósofos."""
import sys, os, math
sys.path.insert(0, os.path.dirname(__file__))
from pnlib import *

OUT = sys.argv[1]
ONLY = sys.argv[2:]


def out(name):
    return os.path.join(OUT, name + ".svg")


def want(name):
    return not ONLY or name in ONLY


# ---------------------------------------------------------------- clases de redes
W, H = 680, 400
COND_Y = 312


def panel(f, ox, ok, title, caption):
    f.title(ox + 20, 28, ("✓ " if ok else "✗ ") + title, GREEN if ok else RED)
    f.para(ox + 20, COND_Y, caption, 300, size=11.5)


def two(name, left, right):
    if not want(name):
        return
    f = Fig(W, H)
    left(f, 0)
    f.line(340, 15, 340, H - 15, dash="4 4")
    right(f, 340)
    f.save(out(name), name)


def P(f, ox, n, x, y, k=0, lpos="n", color=GREY, label=None):
    f.place(n + str(ox), ox + x, y, k, label or n, lpos, color=color)


def T(f, ox, n, x, y, lpos="w", color=ORANGE, orient="h", label=None):
    f.trans(n + str(ox), ox + x, y, orient, label or n, lpos, color=color)


def A(f, ox, a, b, color=GREY, **kw):
    f.arc(a + str(ox), b + str(ox), color=color, **kw)


# --- redes que se reutilizan entre figuras
def conflicto(f, ox, red):
    """P1 con dos transiciones de salida."""
    c = RED if red else GREY
    P(f, ox, "P1", 170, 95, 1, color=c)
    T(f, ox, "T1", 100, 175, "w"); T(f, ox, "T2", 240, 175, "e")
    P(f, ox, "P2", 100, 250, 0, "s"); P(f, ox, "P3", 240, 250, 0, "s")
    A(f, ox, "P1", "T1", c); A(f, ox, "P1", "T2", c)
    A(f, ox, "T1", "P2"); A(f, ox, "T2", "P3")


def mismas_entradas(f, ox, red):
    """P1 y P2 con el mismo conjunto de salida {T1, T2}."""
    c = RED if red else GREY
    P(f, ox, "P1", 100, 95, 1); P(f, ox, "P2", 240, 95, 1, color=c)
    T(f, ox, "T1", 100, 175, "w"); T(f, ox, "T2", 240, 175, "e")
    P(f, ox, "P3", 100, 250, 0, "s"); P(f, ox, "P4", 240, 250, 0, "s")
    A(f, ox, "P1", "T1"); A(f, ox, "P1", "T2"); A(f, ox, "P2", "T1", c); A(f, ox, "P2", "T2", c)
    A(f, ox, "T1", "P3"); A(f, ox, "T2", "P4")


def asimetrica(f, ox, red):
    """P1• = {T1, T2}, P2• = {T2}."""
    c = RED if red else GREY
    P(f, ox, "P1", 115, 95, 1); P(f, ox, "P2", 255, 95, 1, color=c)
    T(f, ox, "T1", 60, 175, "w"); T(f, ox, "T2", 185, 175, "e")
    P(f, ox, "P3", 60, 250, 0, "s"); P(f, ox, "P4", 185, 250, 0, "s")
    A(f, ox, "P1", "T1"); A(f, ox, "P1", "T2"); A(f, ox, "P2", "T2", c)
    A(f, ox, "T1", "P3"); A(f, ox, "T2", "P4")


def cruzada(f, ox, red):
    """P1• = {T1, T2}, P2• = {T2, T3}: T2 en dos conflictos."""
    c = RED if red else GREY
    P(f, ox, "P1", 110, 95, 1); P(f, ox, "P2", 230, 95, 1)
    T(f, ox, "T1", 50, 175, "w"); T(f, ox, "T2", 170, 175, (-12, 20, "end"), color=c); T(f, ox, "T3", 290, 175, "e")
    P(f, ox, "P3", 50, 250, 0, "s"); P(f, ox, "P4", 170, 250, 0, "e"); P(f, ox, "P5", 290, 250, 0, "s")
    A(f, ox, "P1", "T1"); A(f, ox, "P1", "T2", c); A(f, ox, "P2", "T2", c); A(f, ox, "P2", "T3")
    A(f, ox, "T1", "P3"); A(f, ox, "T2", "P4"); A(f, ox, "T3", "P5")


def anidada(f, ox):
    """P1• = {T1, T2} ⊆ P2• = {T1, T2, T3}."""
    P(f, ox, "P1", 110, 95, 1); P(f, ox, "P2", 230, 95, 1)
    T(f, ox, "T1", 50, 175, "w"); T(f, ox, "T2", 170, 175, (-12, 20, "end")); T(f, ox, "T3", 290, 175, "e")
    P(f, ox, "P3", 50, 250, 0, "s"); P(f, ox, "P4", 170, 250, 0, "e"); P(f, ox, "P5", 290, 250, 0, "s")
    A(f, ox, "P1", "T1"); A(f, ox, "P1", "T2"); A(f, ox, "P2", "T1"); A(f, ox, "P2", "T2"); A(f, ox, "P2", "T3")
    A(f, ox, "T1", "P3"); A(f, ox, "T2", "P4"); A(f, ox, "T3", "P5")


# --- pura
def impura(f, ox):
    panel(f, ox, False, "Impura", "P1 es entrada y salida de T1 (auto-loop). En C la entrada (P1, T1) da 1 − 1 = 0, así que la matriz de incidencia no registra el arco.")
    P(f, ox, "P0", 60, 160, 1, "n"); T(f, ox, "T1", 170, 160, "n", orient="v"); P(f, ox, "P2", 280, 160, 0, "n")
    P(f, ox, "P1", 170, 255, 1, "e", color=RED)
    A(f, ox, "P0", "T1"); A(f, ox, "T1", "P2")
    A(f, ox, "P1", "T1", RED, bend=-0.3); A(f, ox, "T1", "P1", RED, bend=-0.3)


def pura(f, ox):
    panel(f, ox, True, "Pura equivalente", "T1 se parte en T1' y T1'' con una plaza nueva P1' en el medio. Ninguna plaza es entrada y salida de la misma transición, y C alcanza para reconstruir la red.")
    P(f, ox, "P0", 35, 160, 1, "n"); T(f, ox, "T1'", 100, 160, "n", orient="v")
    P(f, ox, "P1'", 165, 160, 0, "n", color=GREEN); T(f, ox, "T1''", 230, 160, "n", orient="v")
    P(f, ox, "P2", 295, 160, 0, "n"); P(f, ox, "P1", 165, 255, 1, "s")
    A(f, ox, "P0", "T1'"); A(f, ox, "T1'", "P1'"); A(f, ox, "P1'", "T1''"); A(f, ox, "T1''", "P2")
    A(f, ox, "P1", "T1'"); A(f, ox, "T1''", "P1")


two("pn-clase-pura", impura, pura)


# --- libre de conflicto
def lc_si(f, ox):
    panel(f, ox, True, "Libre de conflicto", "Cada plaza tiene a lo sumo una transición de salida. P3 tiene dos de entrada (eso se permite) y T3 sincroniza P3 con P4. No es grafo de eventos, por P3.")
    P(f, ox, "P1", 80, 80, 1, "w"); P(f, ox, "P2", 220, 80, 1, "e")
    T(f, ox, "T1", 80, 145, "w"); T(f, ox, "T2", 220, 145, "e")
    P(f, ox, "P3", 150, 205, 0, "w"); P(f, ox, "P4", 270, 205, 1, "e")
    T(f, ox, "T3", 210, 270, "e")
    for a, b in [("P1", "T1"), ("P2", "T2"), ("T1", "P3"), ("T2", "P3"), ("P3", "T3"), ("P4", "T3")]:
        A(f, ox, a, b)


def lc_no(f, ox):
    panel(f, ox, False, "No es libre de conflicto", "P1 tiene dos transiciones de salida, así que T1 y T2 compiten por su ficha. Es un conflicto estructural, pero la red sí es de libre elección.")
    conflicto(f, ox, True)


two("pn-clase-libre-conflicto", lc_si, lc_no)


# --- libre elección
def le_si(f, ox):
    panel(f, ox, True, "Libre elección", "T1 y T2 están en conflicto por P1 y no tienen otra entrada: si una está habilitada, la otra también. T3 sincroniza P3 con P4 sin conflicto.")
    P(f, ox, "P1", 115, 85, 1)
    T(f, ox, "T1", 55, 155, "w"); T(f, ox, "T2", 175, 155, "e")
    P(f, ox, "P2", 55, 220, 0, "s"); P(f, ox, "P3", 175, 220, 0, "w"); P(f, ox, "P4", 285, 220, 1, "e")
    T(f, ox, "T3", 230, 280, "e")
    for a, b in [("P1", "T1"), ("P1", "T2"), ("T1", "P2"), ("T2", "P3"), ("P3", "T3"), ("P4", "T3")]:
        A(f, ox, a, b)


def le_no(f, ox):
    panel(f, ox, False, "No es libre elección", "T1 y T2 están en conflicto por P1, pero las dos dependen además de P2. Como P1• = P2• = {T1, T2}, sí es libre elección extendida.")
    mismas_entradas(f, ox, True)


two("pn-clase-libre-eleccion", le_si, le_no)


# --- libre elección extendida
def lee_si(f, ox):
    panel(f, ox, True, "Libre elección extendida", "P1• = P2• = {T1, T2}. Las dos transiciones del conflicto tienen el mismo conjunto de entradas, así que se habilitan siempre juntas.")
    mismas_entradas(f, ox, False)


def lee_no(f, ox):
    panel(f, ox, False, "No es libre elección extendida", "P1• = {T1, T2} y P2• = {T2} comparten T2 sin ser iguales: P2 puede habilitar a T2 y no a T1. Sí es simple.")
    asimetrica(f, ox, True)


two("pn-clase-libre-eleccion-extendida", lee_si, lee_no)


# --- simple
def s_si(f, ox):
    panel(f, ox, True, "Simple", "El único conflicto es ⟨P1, {T1, T2}⟩. P2 tiene una sola salida, así que T2 está en un solo conflicto. No es de libre elección extendida.")
    asimetrica(f, ox, False)


def s_no(f, ox):
    panel(f, ox, False, "No es simple", "T2 está en dos conflictos: ⟨P1, {T1, T2}⟩ y ⟨P2, {T2, T3}⟩. Tampoco es simple extendida, porque los conjuntos de salida se cruzan.")
    cruzada(f, ox, True)


two("pn-clase-simple", s_si, s_no)


# --- simple extendida
def se_si(f, ox):
    panel(f, ox, True, "Simple extendida", "P1• = {T1, T2} ⊆ P2• = {T1, T2, T3}: los conjuntos de salida están anidados. No es simple, porque T1 y T2 están en dos conflictos.")
    anidada(f, ox)


def se_no(f, ox):
    panel(f, ox, False, "No es simple extendida", "P1• = {T1, T2} y P2• = {T2, T3} comparten T2, pero ninguno está incluido en el otro.")
    cruzada(f, ox, True)


two("pn-clase-simple-extendida", se_si, se_no)


# ---------------------------------------------------------------- filósofos
N = 5
CX = CY = 360


def pol(th, u, v):
    return CX + u * math.cos(th) - v * math.sin(th), CY + u * math.sin(th) + v * math.cos(th)


def lab(f, th, u, v, s, color=GREY, radial=False):
    """Etiqueta en coordenadas polares; con radial=True se ancla hacia afuera del círculo."""
    x, y = pol(th, u, v)
    anchor = "middle"
    if radial:
        c, sn = math.cos(th), math.sin(th)
        if c > 0.35:
            anchor = "start"
        elif c < -0.35:
            anchor = "end"
        else:
            y += 6 * (1 if sn > 0 else -1)
    f.text(x, y + 4, s, 11.5, anchor, color)


def ring_forks(f, tokens, color=GREY):
    for i in range(N):
        th = math.radians(-90 + 72 * i - 36)
        x, y = pol(th, 215, 0)
        f.place(f"F{i}", x, y, tokens, "", r=18, color=color)
        lab(f, th, 245, 0, f"tenedor {i}", color, radial=True)


if want("pn-filosofos"):
    f = Fig(720, 720)
    ring_forks(f, 1)
    for i in range(N):
        th = math.radians(-90 + 72 * i)
        rot = math.degrees(th) + 90
        f.place(f"P{i}", *pol(th, 225, -32), 1, "", r=17, color=GREEN)
        f.place(f"C{i}", *pol(th, 225, 32), 0, "", r=17, color=BLUE)
        x, y = pol(th, 150, 0); f.trans(f"T{i}", x, y, "h", "", rot=rot)
        x, y = pol(th, 300, 0); f.trans(f"S{i}", x, y, "h", "", rot=rot)
        lab(f, th, 122, 0, f"toma {i}", ORANGE)
        lab(f, th, 322 if abs(math.cos(th)) < 0.35 else 326, 0, f"suelta {i}", ORANGE, radial=True)
        f.arc(f"P{i}", f"T{i}"); f.arc(f"T{i}", f"C{i}"); f.arc(f"C{i}", f"S{i}"); f.arc(f"S{i}", f"P{i}")
        j = (i + 1) % N
        f.arc(f"F{i}", f"T{i}"); f.arc(f"F{j}", f"T{i}")
        f.arc(f"S{i}", f"F{i}"); f.arc(f"S{i}", f"F{j}")
    for i in range(N):
        th = math.radians(-90 + 72 * i)
        lab(f, th, 225, 0, f"{i}", GREY)
    f._parts(20, 690, [("Plazas de cada filósofo: ", GREY), ("pensando", GREEN), (" y ", GREY), ("comiendo", BLUE), (". El número del medio identifica al filósofo.", GREY)], "start", None, 11.5)
    f.text(20, 708, "Cada toma necesita los dos tenedores vecinos y los consume en un solo disparo.", 11.5, "start")
    f.save(out("pn-filosofos"), "pn-filosofos")

if want("pn-filosofos-ingenua"):
    f = Fig(720, 720)
    ring_forks(f, 0, RED)
    for i in range(N):
        th = math.radians(-90 + 72 * i)
        rot = math.degrees(th) + 90
        f.place(f"P{i}", *pol(th, 240, -34), 0, "", r=16, color=GREEN)
        x, y = pol(th, 175, -34); f.trans(f"TI{i}", x, y, "h", "", rot=rot)
        f.place(f"U{i}", *pol(th, 125, 0), 1, "", r=16, color=RED)
        x, y = pol(th, 175, 34); f.trans(f"TD{i}", x, y, "h", "", rot=rot)
        f.place(f"C{i}", *pol(th, 240, 34), 0, "", r=16, color=BLUE)
        x, y = pol(th, 300, 0); f.trans(f"S{i}", x, y, "h", "", rot=rot)
        lab(f, th, 322 if abs(math.cos(th)) < 0.35 else 326, 0, f"suelta {i}", ORANGE, radial=True)
        for a, b in [(f"P{i}", f"TI{i}"), (f"TI{i}", f"U{i}"), (f"U{i}", f"TD{i}"), (f"TD{i}", f"C{i}"),
                     (f"C{i}", f"S{i}"), (f"S{i}", f"P{i}")]:
            f.arc(a, b)
        j = (i + 1) % N
        f.arc(f"F{i}", f"TI{i}"); f.arc(f"F{j}", f"TD{i}")
        f.arc(f"S{i}", f"F{i}"); f.arc(f"S{i}", f"F{j}")
        lab(f, th, 240, 0, f"{i}", GREY)
    f.text(20, 672, "Marcado de deadlock: los cinco filósofos tomaron el tenedor de su lado izquierdo.", 11.5, "start", RED)
    f.text(20, 690, "No quedan tenedores libres, así que ninguna toma derecha puede disparar.", 11.5, "start")
    f._parts(20, 708, [("Plazas de cada filósofo: ", GREY), ("pensando", GREEN), (", ", GREY), ("con uno", RED), (" (al centro) y ", GREY), ("comiendo", BLUE), (".", GREY)], "start", None, 11.5)
    f.save(out("pn-filosofos-ingenua"), "pn-filosofos-ingenua")
