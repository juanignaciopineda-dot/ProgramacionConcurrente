/**
 * Molinete <b>sin sincronizar</b>. Ya está hecho y está mal a propósito: el verificador lo usa para
 * mostrarte cuántas pasadas se pierden por la condición de carrera.
 *
 * <p>{@code pasadas++} parece una sola operación, pero son tres: leer el valor, sumarle uno y
 * escribirlo. Si dos hilos leen el mismo valor antes de que alguno escriba, los dos escriben el mismo
 * resultado y una de las pasadas se pierde:
 *
 * <pre>
 *   hilo A: lee 41 ──────────── suma ── escribe 42
 *   hilo B: ─────── lee 41 ── suma ──────────────── escribe 42     ← se perdió una pasada
 * </pre>
 */
public class MolineteInseguro {

    private int pasadas = 0;

    /** Registra una pasada, sin ninguna protección. */
    public void registrarPasada() {
        pasadas++;
    }

    /**
     * Devuelve cuántas pasadas registró.
     *
     * @return las pasadas registradas
     */
    public int getPasadas() {
        return pasadas;
    }
}
