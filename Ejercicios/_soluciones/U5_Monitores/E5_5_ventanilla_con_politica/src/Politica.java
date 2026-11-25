/**
 * La <b>política</b> del monitor: cuando hay hilos esperando en varias colas de condición y el recurso
 * se libera, decide a qué cola despertar. Ya está hecha (es una interfaz).
 *
 * <p>Es lo que en el monitor del TP final resuelve los <b>conflictos</b>: varias transiciones
 * sensibilizadas con hilos esperando, y una sola puede seguir. Separarla en un objeto aparte permite
 * cambiar la estrategia (prioridades, turnos, al azar) sin tocar el monitor.
 *
 * <p>El monitor la llama con su mutex tomado, así que una política puede tener estado (por ejemplo,
 * a quién eligió la última vez) sin sincronizar nada.
 */
public interface Politica {

    /**
     * Elige a qué cola despertar.
     *
     * @param conGente {@code conGente[i]} es {@code true} si en la cola {@code i} hay al menos un hilo
     *                 esperando; hay al menos uno en {@code true}
     * @return el índice de una cola con gente
     */
    int elegir(boolean[] conGente);
}
