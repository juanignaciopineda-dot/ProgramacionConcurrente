"""Mini librería para dibujar redes de Petri y autómatas en SVG, con chequeo de superposiciones."""
import math
from xml.sax.saxutils import escape

GREY, ORANGE, BLUE, RED, GREEN, PURPLE = "#78829a", "#f0883e", "#4c8dff", "#ff6b6b", "#3fb950", "#c86dd7"

NARROW = set("ijlI.,:;'|!()[]{}tfr ·")
WIDE = set("mwMW")


def text_width(s, size, bold=False):
    w = 0.0
    for ch in s:
        if ch in NARROW:
            w += 0.30
        elif ch in WIDE:
            w += 0.86
        elif ch.isupper() or ch.isdigit():
            w += 0.63
        elif ch in "—→⊆⊂∩≥≤⇒⇏≠×":
            w += 0.9
        else:
            w += 0.53
    w *= size
    return w * (1.07 if bold else 1.0)


class Node:
    def __init__(self, kind, x, y, **kw):
        self.kind, self.x, self.y = kind, x, y
        self.__dict__.update(kw)

    def boundary(self, tx, ty):
        """Punto del borde en dirección a (tx, ty)."""
        dx, dy = tx - self.x, ty - self.y
        d = math.hypot(dx, dy) or 1.0
        if self.kind in ("place", "state"):
            r = self.r + (4 if getattr(self, "final", False) else 0)
            return self.x + dx / d * r, self.y + dy / d * r
        # rectángulo (posiblemente rotado)
        a = math.radians(-getattr(self, "rot", 0))
        lx = dx * math.cos(a) - dy * math.sin(a)
        ly = dx * math.sin(a) + dy * math.cos(a)
        hw, hh = self.w / 2, self.h / 2
        t = min(hw / abs(lx) if lx else 1e9, hh / abs(ly) if ly else 1e9)
        bx, by = lx * t, ly * t
        a = -a
        return (self.x + bx * math.cos(a) - by * math.sin(a), self.y + bx * math.sin(a) + by * math.cos(a))


class Fig:
    def __init__(self, w, h):
        self.w, self.h = w, h
        self.body = []
        self.texts = []    # (bbox, owner, label)
        self.circles = []  # (cx, cy, r, node)
        self.rects = []    # polygon points
        self.paths = []    # (points, owner)
        self.nodes = {}

    # ---------- texto ----------
    def text(self, x, y, s, size=11.5, anchor="middle", color=GREY, bold=False, italic=False,
             owner=None, check=True):
        w = text_width(s, size, bold)
        x0 = x - w / 2 if anchor == "middle" else (x - w if anchor == "end" else x)
        bbox = (x0, y - 0.78 * size, x0 + w, y + 0.24 * size)
        if check:
            self.texts.append((bbox, owner, s))
        st = f'font-size:{size}px;fill:{color}'
        if bold:
            st += ";font-weight:600"
        if italic:
            st += ";font-style:italic"
        self.body.append(f'<text x="{x:.1f}" y="{y:.1f}" text-anchor="{anchor}" style="{st}">{escape(s)}</text>')
        return bbox

    def para(self, x, y, s, maxw, size=11.5, lh=None, color=GREY, anchor="start"):
        """Texto con cortes de línea automáticos; devuelve la y de la línea siguiente."""
        lh = lh or size * 1.5
        for raw in s.split("\n"):
            line = ""
            for word in raw.split(" "):
                cand = (line + " " + word).strip()
                if text_width(cand, size) > maxw and line:
                    self.text(x, y, line, size, anchor, color)
                    y += lh
                    line = word
                else:
                    line = cand
            if line:
                self.text(x, y, line, size, anchor, color)
                y += lh
        return y

    def title(self, x, y, s, color=GREY, size=13.5):
        return self.text(x, y, s, size, "start", color, bold=True)

    # ---------- formas ----------
    def line(self, x1, y1, x2, y2, color=GREY, dash=None, width=1.2, check=True):
        d = f' stroke-dasharray="{dash}"' if dash else ""
        self.body.append(f'<line x1="{x1:.1f}" y1="{y1:.1f}" x2="{x2:.1f}" y2="{y2:.1f}" stroke="{color}" stroke-width="{width}"{d}/>')
        if check:
            self.paths.append(([(x1 + (x2 - x1) * i / 40, y1 + (y2 - y1) * i / 40) for i in range(41)], "deco"))

    def rect(self, x, y, w, h, color=GREY, rx=0, dash=None, width=1.6, fill="none"):
        d = f' stroke-dasharray="{dash}"' if dash else ""
        self.body.append(f'<rect x="{x:.1f}" y="{y:.1f}" width="{w:.1f}" height="{h:.1f}" rx="{rx}" fill="{fill}" stroke="{color}" stroke-width="{width}"{d}/>')
        pts = []
        for (ax, ay, bx, by) in [(x, y, x + w, y), (x + w, y, x + w, y + h), (x + w, y + h, x, y + h), (x, y + h, x, y)]:
            pts += [(ax + (bx - ax) * i / 30, ay + (by - ay) * i / 30) for i in range(31)]
        self.paths.append((pts, "deco"))

    def ellipse(self, cx, cy, rx, ry, color=GREY, width=2, dash=None):
        d = f' stroke-dasharray="{dash}"' if dash else ""
        self.body.append(f'<ellipse cx="{cx}" cy="{cy}" rx="{rx}" ry="{ry}" fill="none" stroke="{color}" stroke-width="{width}"{d}/>')
        self.paths.append(([(cx + rx * math.cos(t / 60 * 2 * math.pi), cy + ry * math.sin(t / 60 * 2 * math.pi)) for t in range(61)], "deco"))

    # ---------- red de Petri ----------
    def place(self, name, x, y, tokens=0, label=None, lpos="n", r=20, color=GREY, tcolors=None):
        n = Node("place", x, y, r=r)
        self.nodes[name] = n
        self.circles.append((x, y, r, name))
        self.body.append(f'<circle cx="{x}" cy="{y}" r="{r}" fill="none" stroke="{color}" stroke-width="1.8"/>')
        self._tokens(x, y, tokens, r, tcolors)
        if label is None:
            label = name
        if label:
            self._label(n, label, lpos, r + 6, r + 6)
        return n

    def _tokens(self, x, y, k, r, tcolors=None):
        pos = {1: [(0, 0)], 2: [(-7, 0), (7, 0)], 3: [(-7, -5), (7, -5), (0, 7)],
               4: [(-6, -6), (6, -6), (-6, 6), (6, 6)], 5: [(-7, -7), (7, -7), (0, 0), (-7, 7), (7, 7)]}
        if isinstance(k, str) or k > 5:
            self.body.append(f'<text x="{x}" y="{y + 5}" text-anchor="middle" style="font-size:14px;font-weight:600;fill:{BLUE}">{escape(str(k))}</text>')
            return
        for i, (dx, dy) in enumerate(pos.get(k, [])):
            c = tcolors[i] if tcolors else BLUE
            rr = 5 if k == 1 else 4.3
            self.body.append(f'<circle cx="{x + dx}" cy="{y + dy}" r="{rr}" fill="{c}"/>')

    def trans(self, name, x, y, orient="h", label=None, lpos="e", rot=0, color=ORANGE, w=None, h=None):
        if orient == "h":
            w, h = w or 30, h or 10
        else:
            w, h = w or 10, h or 30
        n = Node("trans", x, y, w=w, h=h, rot=rot)
        self.nodes[name] = n
        tf = f' transform="rotate({rot} {x} {y})"' if rot else ""
        self.body.append(f'<rect x="{x - w / 2:.1f}" y="{y - h / 2:.1f}" width="{w}" height="{h}" fill="none" stroke="{color}" stroke-width="2.2"{tf}/>')
        a = math.radians(rot)
        corners = []
        for (cx, cy) in [(-w / 2, -h / 2), (w / 2, -h / 2), (w / 2, h / 2), (-w / 2, h / 2)]:
            corners.append((x + cx * math.cos(a) - cy * math.sin(a), y + cx * math.sin(a) + cy * math.cos(a)))
        self.rects.append((corners, name))
        if label is None:
            label = name
        if label:
            ex = max(abs(c[0] - x) for c in corners) + 6
            ey = max(abs(c[1] - y) for c in corners) + 6
            self._label(n, label, lpos, ex, ey)
        return n

    def _label(self, n, label, lpos, ex, ey, size=11.5, color=GREY):
        if isinstance(lpos, tuple):
            dx, dy = lpos[0], lpos[1]
            anchor = lpos[2] if len(lpos) == 3 else "middle"
            self.text(n.x + dx, n.y + dy, label, size, anchor, color, owner=None)
            return
        if lpos == "n":
            self.text(n.x, n.y - ey - 2, label, size, "middle", color)
        elif lpos == "s":
            self.text(n.x, n.y + ey + 10, label, size, "middle", color)
        elif lpos == "e":
            self.text(n.x + ex, n.y + 4, label, size, "start", color)
        elif lpos == "w":
            self.text(n.x - ex, n.y + 4, label, size, "end", color)

    def arc(self, a, b, weight=None, bend=0.0, kind="normal", color=GREY, wpos=0.5, woff=9, via=None,
            label=None, lcolor=None, loff=10, lsize=11.5):
        A, B = self.nodes[a], self.nodes[b]
        if via:  # polilínea con un punto intermedio
            p0 = A.boundary(*via[0])
            p1 = B.boundary(*via[-1])
            pts = [p0] + list(via) + [p1]
            d = "M " + " L ".join(f"{px:.1f} {py:.1f}" for px, py in pts)
            samples = []
            for (ax, ay), (bx, by) in zip(pts, pts[1:]):
                samples += [(ax + (bx - ax) * i / 20, ay + (by - ay) * i / 20) for i in range(21)]
        else:
            mx, my = (A.x + B.x) / 2, (A.y + B.y) / 2
            dx, dy = B.x - A.x, B.y - A.y
            dist = math.hypot(dx, dy) or 1
            cx, cy = mx - dy / dist * bend * dist, my + dx / dist * bend * dist
            p0 = A.boundary(cx, cy) if bend else A.boundary(B.x, B.y)
            p1 = B.boundary(cx, cy) if bend else B.boundary(A.x, A.y)
            if bend:
                d = f"M {p0[0]:.1f} {p0[1]:.1f} Q {cx:.1f} {cy:.1f} {p1[0]:.1f} {p1[1]:.1f}"
                samples = [((1 - t) ** 2 * p0[0] + 2 * (1 - t) * t * cx + t * t * p1[0],
                            (1 - t) ** 2 * p0[1] + 2 * (1 - t) * t * cy + t * t * p1[1]) for t in [i / 40 for i in range(41)]]
            else:
                d = f"M {p0[0]:.1f} {p0[1]:.1f} L {p1[0]:.1f} {p1[1]:.1f}"
                samples = [(p0[0] + (p1[0] - p0[0]) * i / 40, p0[1] + (p1[1] - p0[1]) * i / 40) for i in range(41)]
        # acortar el final para que la punta toque el borde
        marker = {"normal": "ah", "inhib": "inh", "read": "rd", "reset": "rs"}[kind]
        mcolor = color
        dash = ' stroke-dasharray="5 3"' if kind == "read" else ""
        mid = f"{marker}-{mcolor[1:]}"
        self._marker(marker, mcolor)
        self.body.append(f'<path d="{d}" fill="none" stroke="{color}" stroke-width="1.5"{dash} marker-end="url(#{mid})"/>')
        owner = f"{a}->{b}"
        self.paths.append((samples[2:-2], owner))
        if weight is not None or label is not None:
            i = int(wpos * (len(samples) - 1))
            px, py = samples[i]
            qx, qy = samples[min(i + 1, len(samples) - 1)]
            tx, ty = qx - px, qy - py
            tl = math.hypot(tx, ty) or 1
            nx, ny = -ty / tl, tx / tl
            s = str(weight) if weight is not None else label
            off = woff if weight is not None else loff
            self.text(px + nx * off, py + ny * off + 4, s, lsize, "middle", lcolor or color, owner=owner)
        return samples

    def _marker(self, kind, color):
        mid = f"{kind}-{color[1:]}"
        if getattr(self, "_markers", None) is None:
            self._markers = {}
        if mid in self._markers:
            return
        if kind == "ah":
            m = f'<marker id="{mid}" markerWidth="9" markerHeight="7" refX="8.5" refY="3.5" orient="auto" markerUnits="userSpaceOnUse"><polygon points="0 0, 9 3.5, 0 7" fill="{color}"/></marker>'
        elif kind == "rd":
            m = f'<marker id="{mid}" markerWidth="8" markerHeight="8" refX="4" refY="4" orient="auto" markerUnits="userSpaceOnUse"><circle cx="4" cy="4" r="3.2" fill="{color}"/></marker>'
        elif kind == "inh":
            m = f'<marker id="{mid}" markerWidth="11" markerHeight="11" refX="9.5" refY="5.5" orient="auto" markerUnits="userSpaceOnUse"><circle cx="5.5" cy="5.5" r="4.2" fill="none" stroke="{color}" stroke-width="1.6"/></marker>'
        else:  # reset: doble punta
            m = f'<marker id="{mid}" markerWidth="16" markerHeight="7" refX="15.5" refY="3.5" orient="auto" markerUnits="userSpaceOnUse"><polygon points="0 0, 8 3.5, 0 7" fill="{color}"/><polygon points="7 0, 15 3.5, 7 7" fill="{color}"/></marker>'
        self._markers[mid] = m

    # ---------- autómatas ----------
    def state(self, name, x, y, label=None, final=False, r=22, sub=None, subcolor=ORANGE):
        n = Node("state", x, y, r=r, final=final)
        self.nodes[name] = n
        self.circles.append((x, y, r + (4 if final else 0), name))
        self.body.append(f'<circle cx="{x}" cy="{y}" r="{r}" fill="none" stroke="{GREY}" stroke-width="1.8"/>')
        if final:
            self.body.append(f'<circle cx="{x}" cy="{y}" r="{r + 4}" fill="none" stroke="{GREY}" stroke-width="1.8"/>')
        lab = name if label is None else label
        if sub is None:
            self.body.append(f'<text x="{x}" y="{y + 5}" text-anchor="middle" style="font-size:13px;fill:{GREY}">{escape(lab)}</text>')
        else:
            self.body.append(f'<text x="{x}" y="{y - 2}" text-anchor="middle" style="font-size:12.5px;fill:{GREY}">{escape(lab)}</text>')
            self.body.append(f'<text x="{x}" y="{y + 14}" text-anchor="middle" style="font-size:12.5px;fill:{subcolor}">{escape(sub)}</text>')
        return n

    def start(self, name, length=40, side="w"):
        n = self.nodes[name]
        r = n.r + (4 if n.final else 0)
        if side == "w":
            x1, y1, x2, y2 = n.x - r - length, n.y, n.x - r, n.y
        else:
            x1, y1, x2, y2 = n.x, n.y - r - length, n.x, n.y - r
        self._marker("ah", GREY)
        self.body.append(f'<line x1="{x1}" y1="{y1}" x2="{x2}" y2="{y2}" stroke="{GREY}" stroke-width="1.5" marker-end="url(#ah-{GREY[1:]})"/>')
        self.paths.append(([(x1 + (x2 - x1) * i / 10, y1 + (y2 - y1) * i / 10) for i in range(11)], "start"))

    def loop(self, name, side="n", label=None, lcolor=ORANGE, parts=None):
        """Bucle propio sobre un estado o plaza; parts = [(texto, color), ...] para etiquetas de dos colores."""
        n = self.nodes[name]
        r = n.r + (4 if getattr(n, "final", False) else 0)
        ang = {"n": -90, "s": 90, "e": 0, "w": 180}[side]
        a1, a2 = math.radians(ang - 28), math.radians(ang + 28)
        p0 = (n.x + r * math.cos(a1), n.y + r * math.sin(a1))
        p1 = (n.x + r * math.cos(a2), n.y + r * math.sin(a2))
        far = r + 46
        c0 = (n.x + far * math.cos(a1 - 0.12), n.y + far * math.sin(a1 - 0.12))
        c1 = (n.x + far * math.cos(a2 + 0.12), n.y + far * math.sin(a2 + 0.12))
        self._marker("ah", GREY)
        self.body.append(f'<path d="M {p0[0]:.1f} {p0[1]:.1f} C {c0[0]:.1f} {c0[1]:.1f} {c1[0]:.1f} {c1[1]:.1f} {p1[0]:.1f} {p1[1]:.1f}" fill="none" stroke="{GREY}" stroke-width="1.5" marker-end="url(#ah-{GREY[1:]})"/>')
        samples = []
        for i in range(41):
            t = i / 40
            x = (1 - t) ** 3 * p0[0] + 3 * (1 - t) ** 2 * t * c0[0] + 3 * (1 - t) * t * t * c1[0] + t ** 3 * p1[0]
            y = (1 - t) ** 3 * p0[1] + 3 * (1 - t) ** 2 * t * c0[1] + 3 * (1 - t) * t * t * c1[1] + t ** 3 * p1[1]
            samples.append((x, y))
        owner = f"loop-{name}"
        self.paths.append((samples[3:-3], owner))
        xs = [p[0] for p in samples]; ys = [p[1] for p in samples]
        top = {"n": (n.x, min(ys)), "s": (n.x, max(ys)), "e": (max(xs), n.y), "w": (min(xs), n.y)}[side]
        if label or parts:
            ox = {"n": 0, "s": 0, "e": 6, "w": -6}[side]
            oy = {"n": -7, "s": 16, "e": 4, "w": 4}[side]
            anchor = {"n": "middle", "s": "middle", "e": "start", "w": "end"}[side]
            if parts:
                self._parts(top[0] + ox, top[1] + oy, parts, anchor, owner)
            else:
                self.text(top[0] + ox, top[1] + oy, label, 12.5, anchor, lcolor, owner=owner)

    def _parts(self, x, y, parts, anchor, owner, size=12.5):
        total = sum(text_width(s, size) for s, _ in parts)
        x0 = x - total / 2 if anchor == "middle" else (x - total if anchor == "end" else x)
        self.texts.append(((x0, y - 0.78 * size, x0 + total, y + 0.24 * size), owner, "".join(s for s, _ in parts)))
        spans = "".join(f'<tspan style="fill:{c}">{escape(s)}</tspan>' for s, c in parts)
        self.body.append(f'<text x="{x0:.1f}" y="{y:.1f}" style="font-size:{size}px">{spans}</text>')

    def edge_label_parts(self, samples, parts, t=0.5, off=12, owner=None):
        i = int(t * (len(samples) - 1))
        px, py = samples[i]
        qx, qy = samples[min(i + 1, len(samples) - 1)]
        tx, ty = qx - px, qy - py
        tl = math.hypot(tx, ty) or 1
        nx, ny = -ty / tl, tx / tl
        self._parts(px + nx * off, py + ny * off + 4, parts, "middle", owner)

    # ---------- salida y chequeo ----------
    def check(self, name):
        probs = []

        def inter(a, b, m=0.5):
            return a[0] < b[2] - m and b[0] < a[2] - m and a[1] < b[3] - m and b[1] < a[3] - m

        for i, (bb, own, s) in enumerate(self.texts):
            if bb[0] < 1 or bb[1] < 1 or bb[2] > self.w - 1 or bb[3] > self.h - 1:
                probs.append(f"fuera del lienzo: '{s}' {tuple(round(v) for v in bb)}")
            for j in range(i + 1, len(self.texts)):
                bb2, _, s2 = self.texts[j]
                if inter(bb, bb2):
                    probs.append(f"texto/texto: '{s}' <> '{s2}'")
            for (cx, cy, r, nm) in self.circles:
                nx = min(max(cx, bb[0]), bb[2]); ny = min(max(cy, bb[1]), bb[3])
                if math.hypot(nx - cx, ny - cy) < r - 0.5 and not (bb[0] > cx - r and bb[2] < cx + r and bb[1] > cy - r and bb[3] < cy + r):
                    probs.append(f"texto/plaza: '{s}' <> {nm}")
            for (corners, nm) in self.rects:
                xs = [c[0] for c in corners]; ys = [c[1] for c in corners]
                if inter(bb, (min(xs), min(ys), max(xs), max(ys)), 1):
                    probs.append(f"texto/transición: '{s}' <> {nm}")
            for (pts, pown) in self.paths:
                if pown == own and own is not None:
                    continue
                for (px, py) in pts:
                    if bb[0] + 1 < px < bb[2] - 1 and bb[1] + 1 < py < bb[3] - 1:
                        probs.append(f"texto/arco: '{s}' <> {pown}")
                        break
        if probs:
            print(f"[{name}] {len(probs)} problemas")
            for p in probs:
                print("   ", p)
        else:
            print(f"[{name}] ok")
        return probs

    def save(self, path, name=None):
        name = name or path
        self.check(name)
        style = ('<style>text { font-family: "Segoe UI", "Helvetica Neue", Arial, sans-serif; }</style>')
        defs = "<defs>" + "".join(getattr(self, "_markers", {}).values()) + "</defs>"
        svg = (f'<svg xmlns="http://www.w3.org/2000/svg" width="{self.w}" height="{self.h}" '
               f'viewBox="0 0 {self.w} {self.h}" version="1.1">\n{style}\n{defs}\n' + "\n".join(self.body) + "\n</svg>\n")
        open(path, "w", encoding="utf-8", newline="\n").write(svg)
