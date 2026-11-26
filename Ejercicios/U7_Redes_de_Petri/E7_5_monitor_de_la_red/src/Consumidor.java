/**
 * Un consumidor: repite su T-invariante (TOMAR_PRODUCTO y después LIBERAR_HUECO) una cantidad fija de
 * veces. Como el {@link Productor}, solo sabe qué transiciones dispara.
 */
public class Consumidor implements Runnable {

    private final MonitorDeRed monitor;
    private final int vueltas;

    /**
     * Ya está hecho.
     *
     * @param monitor el monitor de la red
     * @param vueltas cuántas veces consume
     */
    public Consumidor(MonitorDeRed monitor, int vueltas) {
        this.monitor = monitor;
        this.vueltas = vueltas;
    }

    /**
     * {@code vueltas} veces: {@code dispararTransicion(TOMAR_PRODUCTO)} y después
     * {@code dispararTransicion(LIBERAR_HUECO)}.
     */
    @Override
    public void run() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Consumidor.run()");
    }
}
