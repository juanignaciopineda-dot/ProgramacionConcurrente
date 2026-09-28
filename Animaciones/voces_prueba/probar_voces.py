"""Genera el mismo párrafo con distintas voces de edge-tts para elegir de oído.

Uso:
    python probar_voces.py

Escuchá los .mp3 generados en esta misma carpeta y avisale a Claude cuál te
gustó más (por nombre de archivo, ej. "es-AR-TomasNeural"). Esa voz queda fija
en comun/voz.py para todas las escenas del curso.
"""
import asyncio
from pathlib import Path

import edge_tts

# Párrafo de prueba: un fragmento real del guion (condición de carrera),
# con términos técnicos y una pausa, para juzgar la voz en condiciones reales.
TEXTO = (
    "Pensemos en dos hilos que ejecutan, cada uno, la instrucción "
    "contador más más. Parece una sola operación, pero el procesador la "
    "hace en tres pasos separados: primero lee el valor de la memoria, "
    "después lo incrementa en un registro, y recién al final escribe el "
    "resultado de vuelta en la memoria. Si los dos hilos leen el mismo "
    "valor antes de que ninguno haya escrito, el incremento de uno se "
    "pierde. Y esto no es un caso raro ni un error del programador: es "
    "una intercalación perfectamente válida de las acciones atómicas."
)

VOCES = [
    "es-AR-ElenaNeural",
    "es-AR-TomasNeural",
    "es-MX-DaliaNeural",
    "es-MX-JorgeNeural",
    "es-ES-AlvaroNeural",
    "es-ES-XimenaNeural",
    "es-ES-ElviraNeural",
]

SALIDA = Path(__file__).parent


async def generar(voz: str) -> None:
    destino = SALIDA / f"{voz}.mp3"
    comunicador = edge_tts.Communicate(TEXTO, voz)
    await comunicador.save(str(destino))
    print(f"  listo: {destino.name}")


async def main() -> None:
    print(f"Generando {len(VOCES)} muestras en {SALIDA} ...")
    for voz in VOCES:
        print(f"- {voz}")
        await generar(voz)
    print("\nListo. Escuchá los .mp3 y elegí la que suene más humana.")


if __name__ == "__main__":
    asyncio.run(main())
