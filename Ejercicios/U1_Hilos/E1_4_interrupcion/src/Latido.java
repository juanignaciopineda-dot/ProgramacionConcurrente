/**
 * Un latido: cada {@code periodoMs} milisegundos suma uno a un contador, indefinidamente, hasta que
 * alguien lo interrumpe.
 *
 * <p>Es el caso típico de hilo que pasa casi todo el tiempo <b>dormido</b>: cuando lo interrumpen,
 * {@link Thread#sleep(long)} se despierta de golpe lanzando {@link InterruptedException}. Esa
 * excepción es el aviso de "pedido de terminar".
 */
public class Latido implements Runnable {

    /** Cada cuánto late, en milisegundos. */
    private final long periodoMs;

    /** Cantidad de latidos. Lo escribe solo el hilo del latido; se lee después de {@code join()}. */
    private int latidos;

    /**
     * Crea un latido. Este constructor ya está hecho.
     *
     * @param periodoMs cada cuánto late, en milisegundos
     */
    public Latido(long periodoMs) {
        this.periodoMs = periodoMs;
    }

    /**
     * Late hasta que lo interrumpan.
     *
     * <p>Un bucle "infinito" que en cada vuelta:
     *
     * <ol>
     *   <li>incrementa {@link #latidos};
     *   <li>duerme {@link #periodoMs} con {@link Thread#sleep(long)}.
     * </ol>
     *
     * <p>Cuando {@code sleep} lance {@link InterruptedException}, <b>salí del bucle</b> y terminá el
     * método. El lugar natural para el {@code try/catch} es <b>afuera</b> del bucle: así la excepción
     * corta el bucle sola.
     *
     * <p><b>La trampa:</b> si capturás la excepción adentro del bucle y solo hacés
     * {@code e.printStackTrace()}, el bucle sigue dando vueltas para siempre. Además, {@code sleep}
     * <i>limpia</i> la marca de interrupción al lanzar la excepción, así que no queda rastro del
     * pedido. Probalo después de que el verificador te dé OK (pregunta 2 del README).
     */
    @Override
    public void run() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Latido.run()");
    }

    /**
     * Devuelve cuántas veces latió. Leelo después de hacer {@code join()} sobre el hilo.
     *
     * @return la cantidad de latidos
     */
    public int getLatidos() {
        return latidos;
    }
}
