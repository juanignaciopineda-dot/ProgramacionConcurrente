"""Reemplazos que solo afectan al audio (no a los subtítulos ni al guion).

Solo va aquí lo que el usuario eligió de oído (ver voces_prueba/). No
inventar grafías "fonéticas" sin probarlas: a veces suenan peor que la
palabra original (pasó con "softgüer").

Aprobado tal cual, sin reemplazo: Dijkstra, Therac, Ariane, Petri, deadlock,
livelock, starvation, rendezvous, PCB, software, hardware, CPU, bug.

Cambiar un reemplazo cambia el texto que se sintetiza: solo esas frases se
vuelven a generar (el caché de audio se indexa por el texto).
"""
import re

# (patrón, reemplazo). Se aplican en orden, sin distinguir mayúsculas.
REEMPLAZOS: list[tuple[str, str]] = [
    # elegidos de oído con voces_prueba/probar_anglicismos.py
    (r"\btesting\b", "téstin"),
    (r"\binterleaving\b", "interlíving"),
    (r"\bmutex\b", "mútex"),
    (r"\bspoiler\b", "espóiler"),
]


def aplicar(texto: str) -> str:
    """Aplica los reemplazos de pronunciación a un texto de narración."""
    for patron, reemplazo in REEMPLAZOS:
        texto = re.sub(patron, reemplazo, texto, flags=re.IGNORECASE)
    return texto
