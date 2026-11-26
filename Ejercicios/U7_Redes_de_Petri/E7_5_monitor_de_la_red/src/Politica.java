/**
 * La política del monitor: entre las transiciones que se pueden despertar (sensibilizadas y con hilos
 * esperando), elige una. Es la misma interfaz del ejercicio 5.5, con transiciones en vez de tipos de
 * cliente. Ya está hecha.
 *
 * <p>El monitor la llama con el mutex tomado, así que puede tener estado sin sincronizar nada.
 */
public interface Politica {

    /**
     * Elige a qué transición despertar.
     *
     * @param despertables {@code despertables[t]} es {@code true} si {@code t} está sensibilizada y hay
     *                     hilos esperando en su cola; hay al menos una en {@code true}
     * @return el índice de una transición despertable
     */
    int elegir(boolean[] despertables);
}
