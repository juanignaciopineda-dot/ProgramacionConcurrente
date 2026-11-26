import sys, os
sys.path.insert(0, os.path.dirname(__file__))
from pnlib import *

OUT = sys.argv[1]
ONLY = sys.argv[2:]


def out(name):
    return os.path.join(OUT, name + ".svg")


def want(name):
    return not ONLY or name in ONLY


# ---------------------------------------------------------------- exclusión mutua (Introducción)
if want("pn-mutex-intro"):
    f = Fig(640, 400)
    f.title(20, 28, "Exclusión mutua con una plaza mutex")
    X1, X2 = 160, 480
    L, R = (-58, 4, "end"), (58, 4, "start")
    f.para(20, 48, "Dos procesos que comparten una sección crítica (SC). La única ficha de mutex la tiene quien está adentro.", 600)
    f.place("p1", X1, 115, 1, "p1: P1 fuera", "n")
    f.trans("t1", X1, 180, "h", "t1: P1 entra", L)
    f.place("p2", X1, 245, 0, "p2: P1 en SC", L)
    f.trans("t2", X1, 310, "h", "t2: P1 sale", L)
    f.place("m", 320, 245, 1, "mutex", "s")
    f.place("p3", X2, 115, 1, "p3: P2 fuera", "n")
    f.trans("t3", X2, 180, "h", "t3: P2 entra", R)
    f.place("p4", X2, 245, 0, "p4: P2 en SC", R)
    f.trans("t4", X2, 310, "h", "t4: P2 sale", R)
    for a, b in [("p1", "t1"), ("t1", "p2"), ("p2", "t2"), ("p3", "t3"), ("t3", "p4"), ("p4", "t4")]:
        f.arc(a, b)
    f.arc("t2", "p1", via=[(X1 - 45, 310), (X1 - 45, 115)])
    f.arc("t4", "p3", via=[(X2 + 45, 310), (X2 + 45, 115)])
    f.arc("m", "t1"); f.arc("m", "t3"); f.arc("t2", "m"); f.arc("t4", "m")
    f.para(20, 360, "M₀ = (p1, p2, p3, p4, mutex) = (1, 0, 1, 0, 1). P-invariante: p2 + p4 + mutex = 1, así que p2 + p4 ≤ 1.", 600)
    f.save(out("pn-mutex-intro"), "pn-mutex-intro")

# ---------------------------------------------------------------- disparo
if want("pn-disparo"):
    f = Fig(640, 340)
    for k, (x0, ttl, before) in enumerate([(20, "Antes del disparo: T1 está sensibilizada", True), (340, "Después del disparo", False)]):
        f.title(x0, 28, ttl)
        f.place(f"P1{k}", x0 + 50, 95, 1 if before else 0, "P1", "n")
        f.place(f"P2{k}", x0 + 50, 215, 2 if before else 0, "P2", "s")
        f.trans(f"T1{k}", x0 + 160, 155, "v", "T1", "n", h=56)
        f.place(f"P3{k}", x0 + 250, 155, 0 if before else 1, "P3", "n")
        f.arc(f"P1{k}", f"T1{k}")
        f.arc(f"P2{k}", f"T1{k}", weight=2, wpos=0.45, woff=-10)
        f.arc(f"T1{k}", f"P3{k}")
    f.line(320, 45, 320, 255, dash="4 4")
    f.para(20, 285, "Sensibilizada: cada plaza de entrada tiene al menos tantas fichas como el peso de su arco (P1 necesita 1 y P2 necesita 2). Al disparar, T1 consume las fichas de todas sus entradas y produce en todas sus salidas. El disparo es indivisible y dura cero.", 600)
    f.save(out("pn-disparo"), "pn-disparo")


# ---------------------------------------------------------------- productor/consumidor
def prod_cons(name, prioridad=False):
    f = Fig(640, 545 if prioridad else 580)
    ttl = "Productor/consumidor con prioridad al productor" if prioridad else "Productor/consumidor con buffer acotado (N = 3)"
    f.title(20, 28, ttl)
    sub = ("El arco inhibidor desde «Con hueco» hacia T4 no deja que el consumidor tome el mutex mientras haya un productor esperándolo."
           if prioridad else
           "El productor baja por la izquierda y el consumidor sube por la derecha. Huecos, Productos y Mutex son los tres semáforos.")
    y = f.para(20, 48, sub, 600)
    dy = y - 50
    X1, X2, XM = 160, 480, 320
    L, R = (-56, 4, "end"), (56, 4, "start")
    f.place("Pp", X1, 110 + dy, 1, "Produciendo", "n")
    f.trans("T0", X1, 175 + dy, "h", "T0", L)
    f.place("Ph", X1, 240 + dy, 0, "Con hueco", L)
    f.trans("T1", X1, 305 + dy, "h", "T1", L)
    f.place("Pd", X1, 370 + dy, 0, "Depositando", L)
    f.trans("T2", X1, 435 + dy, "h", "T2", L)
    f.place("Cc", X2, 435 + dy, 1, "Consumiendo", "s")
    f.trans("T3", X2, 370 + dy, "h", "T3", R)
    f.place("Cp", X2, 305 + dy, 0, "Con producto", R)
    f.trans("T4", X2, 240 + dy, "h", "T4", R)
    f.place("Cr", X2, 175 + dy, 0, "Retirando", R)
    f.trans("T5", X2, 110 + dy, "h", "T5", R)
    f.place("H", XM, 120 + dy, 3, "Huecos", "n")
    f.place("M", XM, 272 + dy, 1, "Mutex", (0, 44))
    f.place("B", XM, 425 + dy, 0, "Productos", "s")
    for a, b in [("Pp", "T0"), ("T0", "Ph"), ("Ph", "T1"), ("T1", "Pd"), ("Pd", "T2"),
                 ("Cc", "T3"), ("T3", "Cp"), ("Cp", "T4"), ("T4", "Cr"), ("Cr", "T5")]:
        f.arc(a, b)
    f.arc("T2", "Pp", via=[(X1 - 44, 435 + dy), (X1 - 44, 110 + dy)])
    f.arc("T5", "Cc", via=[(X2 + 44, 110 + dy), (X2 + 44, 435 + dy)])
    f.arc("H", "T0"); f.arc("T5", "H")
    f.arc("B", "T3"); f.arc("T2", "B")
    f.arc("M", "T1"); f.arc("M", "T4"); f.arc("T2", "M"); f.arc("T5", "M")
    if prioridad:
        f.arc("Ph", "T4", kind="inhib", color=RED, label="prioridad al productor", lcolor=RED, wpos=0.3, loff=-12)
    yb = 490 + dy
    if prioridad:
        f.para(20, yb + 12, "Para darle prioridad al consumidor, el arco va al revés: desde «Con producto» hacia T1.", 600)
    else:
        f.para(20, yb + 12, "P-invariantes: Produciendo + Con hueco + Depositando = 1, Consumiendo + Con producto + Retirando = 1, Mutex + Depositando + Retirando = 1, Huecos + Con hueco + Depositando + Productos + Con producto + Retirando = 3.", 600)
    f.save(out(name), name)


if want("pn-productor-consumidor"):
    prod_cons("pn-productor-consumidor")
if want("pn-prod-cons-prioridad"):
    prod_cons("pn-prod-cons-prioridad", True)

# ---------------------------------------------------------------- lectores / escritores
if want("pn-lectores-escritores"):
    f = Fig(620, 420)
    f.title(20, 28, "Lectores y escritores (hasta N = 3 lectores a la vez)")
    f.para(20, 48, "Cada lector toma una ficha de Recursos y el escritor las toma todas, con un arco de peso 3.", 580)
    XL, XE, XR = 160, 460, 310
    L, R = (-56, 4, "end"), (56, 4, "start")
    f.place("L", XL, 110, 3, "Lectores ociosos", "n")
    f.trans("T0", XL, 175, "h", "T0", L)
    f.place("Lr", XL, 240, 0, "Leyendo", L)
    f.trans("T1", XL, 305, "h", "T1", L)
    f.place("E", XE, 110, 2, "Escritores ociosos", "n")
    f.trans("T2", XE, 175, "h", "T2", R)
    f.place("Ew", XE, 240, 0, "Escribiendo", R)
    f.trans("T3", XE, 305, "h", "T3", R)
    f.place("R", XR, 240, 3, "Recursos", (0, 44))
    for a, b in [("L", "T0"), ("T0", "Lr"), ("Lr", "T1"), ("E", "T2"), ("T2", "Ew"), ("Ew", "T3")]:
        f.arc(a, b)
    f.arc("T1", "L", via=[(XL - 44, 305), (XL - 44, 110)])
    f.arc("T3", "E", via=[(XE + 44, 305), (XE + 44, 110)])
    f.arc("R", "T0"); f.arc("T1", "R")
    f.arc("R", "T2", weight=3, woff=-10); f.arc("T3", "R", weight=3, woff=-10)
    f.para(20, 355, "P-invariante: Leyendo + 3 · Escribiendo + Recursos = 3. Si hay un escritor (Escribiendo = 1), no queda ninguna ficha para leer, y viceversa.", 580)
    f.save(out("pn-lectores-escritores"), "pn-lectores-escritores")

# ---------------------------------------------------------------- cuatro estaciones
if want("pn-estaciones"):
    f = Fig(420, 380)
    f.title(20, 28, "Ciclo estacional")
    f.para(20, 48, "Una plaza por estación y una ficha: la estación actual.", 380)
    cx, cy, R = 210, 200, 95
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
    f.para(20, 352, "P-invariante: M(primavera) + M(verano) + M(otoño) + M(invierno) = 1.", 380)
    f.save(out("pn-estaciones"), "pn-estaciones")
