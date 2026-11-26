/**
 * Un productor: repite su T-invariante (TOMAR_HUECO y después DEPOSITAR) una cantidad fija de veces.
 *
 * <p>No sabe nada de semáforos ni de colas: solo qué transiciones dispara y en qué orden. Si el buffer
 * está lleno, {@code TOMAR_HUECO} no está sensibilizada y el monitor lo hace esperar.
 */
public class Productor implements Runnable {

    private final MonitorDeRed monitor;
    private final int vueltas;

    /**
     * Ya está hecho.
     *
     * @param monitor el monitor de la red
     * @param vueltas cuántas veces produce
     */
    public Productor(MonitorDeRed monitor, int vueltas) {
        this.monitor = monitor;
        this.vueltas = vueltas;
    }

    /**
     * {@code vueltas} veces: {@code dispararTransicion(TOMAR_HUECO)} y después
     * {@code dispararTransicion(DEPOSITAR)} (con las constantes de {@link ProductorConsumidor}). Entre
     * los dos disparos el productor está "adentro" del buffer, según la red, pero fuera del monitor.
     */
    @Override
    public void run() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Productor.run()");
    }
}
