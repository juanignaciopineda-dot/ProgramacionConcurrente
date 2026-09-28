"""Variantes de pronunciación de anglicismos del guion, para elegir de oído.

Genera un mp3 por palabra en voces_prueba/anglicismos/, con todas sus
variantes numeradas dentro de la misma frase. La elegida va a
comun/pronunciacion.py.
"""
import asyncio
import sys
from pathlib import Path

import edge_tts

VOZ = "es-ES-XimenaNeural"
SALIDA = Path(__file__).parent / "anglicismos"

# palabra: (frase con {} donde va la variante, [variantes])
CASOS = {
    "software": ("el {} de la máquina falló", ["software", "sóftuer", "sóftwer", "sófwer"]),
    "hardware": ("el error estaba en el {}", ["hardware", "járduer", "járdwer", "járwer"]),
    "testing": ("el {} no alcanza", ["testing", "téstin", "téstin g"]),
    "interleaving": ("esto se llama {}", ["interleaving", "interlíving", "ínterliving"]),
    "mutex": ("el lugar {} tiene una ficha", ["mutex", "miútex", "mútex"]),
    "cpu": ("la {} ejecuta instrucciones", ["CPU", "C P U", "ce pe u"]),
    "spoiler": ("la respuesta, {}, es que no", ["spoiler", "espóiler"]),
    "bug": ("no fue un {}", ["bug", "bag", "bog"]),
    "quantum": ("ese tiempo asignado se llama {}", ["quantum", "cuántum", "cuántun", "quántum"]),
}

NUMEROS = ["uno", "dos", "tres", "cuatro", "cinco"]


async def main() -> None:
    SALIDA.mkdir(exist_ok=True)
    elegidas = sys.argv[1:] or list(CASOS)  # uso: probar_anglicismos.py [palabra ...]
    for palabra, (frase, variantes) in CASOS.items():
        if palabra not in elegidas:
            continue
        texto = " ".join(f"Opción {NUMEROS[i]}: {frase.format(v)}." for i, v in enumerate(variantes))
        await edge_tts.Communicate(texto, VOZ).save(str(SALIDA / f"{palabra}.mp3"))
        print(f"{palabra}.mp3  <-  " + " | ".join(f"{i + 1}) {v}" for i, v in enumerate(variantes)))


if __name__ == "__main__":
    asyncio.run(main())
