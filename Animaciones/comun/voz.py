"""Servicio de voz para manim-voiceover, y la voz fija de todo el curso.

manim-voiceover no trae un servicio para edge-tts "de fábrica" en la versión
instalada (0.4.0) — sólo Azure, gTTS, ElevenLabs, OpenAI, Gemini y pyttsx3.
Edge-tts es gratis, no pide clave y da mejor calidad que gTTS, así que se
implementa acá un `EdgeTTSService` mínimo siguiendo el mismo patrón que
`manim_voiceover.services.gtts.GTTSService`.

Para cambiar de voz o de motor en TODO el curso, se toca sólo este archivo.
"""
from __future__ import annotations

import asyncio
from pathlib import Path

import edge_tts
from manim_voiceover._typing import VoiceoverData, WordBoundary
from manim_voiceover.helper import remove_bookmarks
from manim_voiceover.services.base import PathLike, SpeechService, initialize_speech_service, path_to_string

# ------------------------------------------------------------------------
# La voz elegida a oído entre 7 candidatas gratuitas de edge-tts
# (ver voces_prueba/probar_voces.py). Cambiar acá alcanza para todo el curso.
VOZ_POR_DEFECTO = "es-ES-XimenaNeural"
# Un poco más lenta que la natural: es una clase, y ayuda a llegar a ~10 min
# por episodio sin agregar texto. Cambiarla vuelve a sintetizar todo el audio.
VELOCIDAD = "-5%"


class EdgeTTSService(SpeechService):
    """SpeechService para Microsoft Edge TTS (voces neuronales gratuitas),
    usando la librería `edge-tts`. No requiere API key.
    """

    def __init__(self, voice: str = VOZ_POR_DEFECTO, rate: str = VELOCIDAD,
                 pitch: str = "+0Hz", **kwargs: object) -> None:
        """
        Args:
            voice: nombre de la voz de edge-tts (ej. "es-ES-XimenaNeural").
                Listar todas con `edge-tts --list-voices`.
            rate: ajuste de velocidad relativo, ej. "-10%" para más lento.
            pitch: ajuste de tono relativo, ej. "+2Hz".
        """
        self.voice = voice
        self.rate = rate
        self.pitch = pitch
        initialize_speech_service(self, kwargs)

    def generate_from_text(
        self,
        text: str,
        cache_dir: PathLike | None = None,
        path: PathLike | None = None,
        **kwargs: object,
    ) -> VoiceoverData:
        """"""
        if cache_dir is None:
            cache_dir = self.cache_dir

        input_text = remove_bookmarks(text)
        input_data = {
            "input_text": input_text,
            "service": "edge-tts",
            "config": {"voice": self.voice, "rate": self.rate, "pitch": self.pitch,
                       "boundary": "word"},
        }

        cached_result = self.get_cached_result(input_data, cache_dir)
        if cached_result is not None:
            # El caché se indexa por el texto SIN marcas (el audio no depende
            # de ellas), pero las marcas hay que tomarlas del texto actual: si
            # no, un momento agregado al guion no existiría para el tracker.
            cached_result["input_text"] = text
            _desde_el_principio(cached_result.get("word_boundaries"))
            return cached_result

        if path is None:
            audio_path = self.get_audio_basename(input_data) + ".mp3"
        else:
            audio_path = path_to_string(path)

        destino = Path(cache_dir) / audio_path
        word_boundaries = asyncio.run(self._sintetizar(input_text, destino))

        json_dict: VoiceoverData = {
            "input_text": text,
            "input_data": input_data,
            "original_audio": audio_path,
        }
        if word_boundaries:
            json_dict["word_boundaries"] = word_boundaries

        return json_dict

    async def _sintetizar(self, texto: str, destino: Path) -> list[WordBoundary]:
        # edge-tts 7 manda por defecto límites de ORACIÓN; los de palabra hay
        # que pedirlos. Sin ellos las marcas del guion no se pueden ubicar.
        comunicador = edge_tts.Communicate(texto, self.voice, rate=self.rate,
                                           pitch=self.pitch, boundary="WordBoundary")
        word_boundaries: list[WordBoundary] = []
        cursor = 0
        with open(destino, "wb") as archivo:
            async for chunk in comunicador.stream():
                if chunk["type"] == "audio":
                    archivo.write(chunk["data"])
                elif chunk["type"] == "WordBoundary":
                    # edge-tts no dice en qué posición del texto está cada
                    # palabra: se la busca en orden a partir de la anterior.
                    # Si el TTS la normalizó distinto y no aparece, se omite
                    # (el interpolador de manim-voiceover cubre el hueco).
                    palabra = chunk["text"]
                    pos = texto.find(palabra, cursor)
                    if pos == -1:
                        continue
                    cursor = pos + len(palabra)
                    # offset en unidades de 100 ns: la misma resolución que
                    # usa manim-voiceover.
                    word_boundaries.append({
                        "audio_offset": chunk["offset"],
                        "duration_milliseconds": chunk["duration"] // 10_000,
                        "text_offset": pos,
                        "word_length": len(palabra),
                        "text": palabra,
                        "boundary_type": "Word",
                    })
        _desde_el_principio(word_boundaries)
        return word_boundaries


def _desde_el_principio(word_boundaries: list[WordBoundary] | None) -> None:
    """Si el texto empieza con un signo («¿Qué…», «"Hola…»), la primera palabra
    queda en el offset 1 y una marca al principio del bloque (offset 0) cae
    fuera del rango del interpolador de manim-voiceover, que entonces la manda
    al FINAL del audio. Se ancla la primera palabra al offset 0."""
    if word_boundaries and word_boundaries[0]["text_offset"] > 0:
        word_boundaries[0]["text_offset"] = 0


def servicio_voz(**kwargs) -> EdgeTTSService:
    """Fábrica que usan todas las escenas: `self.set_speech_service(servicio_voz())`.

    Cambiar de motor de voz para TODO el curso es reemplazar acá qué se
    devuelve (por ejemplo, por un `ChatterboxService` local del día que haya
    GPU), sin tocar ninguna escena.
    """
    return EdgeTTSService(**kwargs)
