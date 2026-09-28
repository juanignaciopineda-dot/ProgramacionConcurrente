"""Prueba cómo pronuncia la voz elegida los nombres propios y siglas del
guion, para detectar cuáles necesitan una entrada en comun/pronunciacion.py.

Uso:
    python probar_nombres.py
"""
import asyncio
from pathlib import Path

import edge_tts

VOZ = "es-ES-XimenaNeural"
SALIDA = Path(__file__).parent / "nombres"

CASOS = {
    "1_dijkstra_tal_cual": "Edsger Dijkstra fue una de las figuras que fundó esta disciplina.",
    "2_dijkstra_fonetico": "Edsger Dáikstra fue una de las figuras que fundó esta disciplina.",
    "3_therac": "Un equipo de radioterapia llamado Therac veinticinco.",
    "4_ariane": "El cohete Ariane cinco se autodestruyó cuarenta segundos después del despegue.",
    "5_petri_tal_cual": "Carl Adam Petri era un chico alemán que a los trece años ya andaba inventando diagramas.",
    "6_deadlock_livelock": "Esto se llama deadlock, y no hay que confundirlo con el livelock.",
    "7_rendezvous_tal_cual": "Eso tiene nombre propio, rendezvous, y es la forma más fuerte de sincronización.",
    "8_pcb_tal_cual": "El sistema le abre una ficha: el Bloque de Control de Proceso, o PCB.",
    "9_starvation": "La inanición, o starvation, es más sutil.",
}


async def generar(nombre: str, texto: str) -> None:
    destino = SALIDA / f"{nombre}.mp3"
    comunicador = edge_tts.Communicate(texto, VOZ)
    await comunicador.save(str(destino))
    print(f"  listo: {destino.name}  <-  {texto}")


async def main() -> None:
    SALIDA.mkdir(exist_ok=True)
    print(f"Generando {len(CASOS)} muestras en {SALIDA} ...")
    for nombre, texto in CASOS.items():
        await generar(nombre, texto)
    print("\nListo. Escuchá cada par (tal cual vs. fonético) y decime cuál suena mejor.")


if __name__ == "__main__":
    asyncio.run(main())
