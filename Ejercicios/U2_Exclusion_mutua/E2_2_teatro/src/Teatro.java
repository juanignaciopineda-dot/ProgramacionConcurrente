/**
 * Venta de entradas de un teatro con dos sectores <b>independientes</b>: platea y palco.
 *
 * <p>Vender en platea y vender en palco no comparten ningún dato, así que no tienen por qué
 * esperarse entre sí. Si todo el teatro usara un único candado (por ejemplo, poniendo
 * {@code synchronized} en todos los métodos, que toma el monitor de {@code this}), una venta de palco
 * tendría que esperar a que termine una de platea sin ninguna necesidad.
 *
 * <p>La solución es usar <b>una llave por recurso</b>: dos objetos {@code final} que sirven solo de
 * candado, y bloques {@code synchronized (llave) { ... }}.
 *
 * <pre>
 *   venderPlatea ──► synchronized (llavePlatea) ──► libresPlatea
 *   venderPalco  ──► synchronized (llavePalco)  ──► libresPalco
 *   pasarAPalco  ──► synchronized (llavePlatea) { synchronized (llavePalco) { ... } }
 * </pre>
 *
 * <p>Herramientas permitidas: solo {@code synchronized}. Nada de {@code java.util.concurrent}.
 */
public class Teatro {

    /** Lugares de la platea. */
    public static final int CAPACIDAD_PLATEA = 100;

    /** Lugares del palco. */
    public static final int CAPACIDAD_PALCO = 20;

    /** Cuánto tarda en cobrar el sistema de pagos, en milisegundos. */
    private final long demoraCobroMs;

    /** Lugares libres en platea. Lo protege la llave de la platea. */
    private int libresPlatea = CAPACIDAD_PLATEA;

    /** Lugares libres en palco. Lo protege la llave del palco. */
    private int libresPalco = CAPACIDAD_PALCO;

    // TODO: declará acá las dos llaves: dos objetos que nunca cambien (private final Object ...).

    /**
     * Crea un teatro vacío. Este constructor ya está hecho.
     *
     * @param demoraCobroMs cuánto tarda cada cobro, en milisegundos
     */
    public Teatro(long demoraCobroMs) {
        this.demoraCobroMs = demoraCobroMs;
    }

    /**
     * Vende {@code cantidad} entradas de platea, si hay lugar.
     *
     * <p>Todo esto va adentro de un {@code synchronized} con la llave de la platea:
     *
     * <ol>
     *   <li>Si quedan menos de {@code cantidad} lugares, devolvé {@code false} sin cobrar.
     *   <li>Cobrá con {@link #cobrar()}. El cobro va <b>adentro</b> de la sección crítica: los
     *       lugares quedan reservados mientras se cobra, y nadie más puede venderlos.
     *   <li>Descontá los lugares y devolvé {@code true}.
     * </ol>
     *
     * <p>¿Por qué el chequeo y el descuento tienen que estar en la <b>misma</b> sección crítica? Si
     * chequearas, soltaras la llave y después descontaras, dos hilos podrían ver "hay 10 lugares" a la
     * vez y vender 10 cada uno: sobreventa (es el patrón <i>check-then-act</i>).
     *
     * @param cantidad cuántas entradas se quieren
     * @return {@code true} si se vendieron, {@code false} si no había lugar
     * @throws IllegalArgumentException si {@code cantidad < 1}
     * @throws InterruptedException si interrumpen al hilo mientras cobra
     */
    public boolean venderPlatea(int cantidad) throws InterruptedException {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Teatro.venderPlatea()");
    }

    /**
     * Igual que {@link #venderPlatea(int)}, pero para el palco y con <b>su</b> llave.
     *
     * @param cantidad cuántas entradas se quieren
     * @return {@code true} si se vendieron, {@code false} si no había lugar
     * @throws IllegalArgumentException si {@code cantidad < 1}
     * @throws InterruptedException si interrumpen al hilo mientras cobra
     */
    public boolean venderPalco(int cantidad) throws InterruptedException {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Teatro.venderPalco()");
    }

    /**
     * Pasa a {@code cantidad} espectadores de la platea al palco: se liberan {@code cantidad}
     * lugares de platea y se ocupan {@code cantidad} de palco. No se cobra (no hay demora).
     *
     * <p>Toca los dos sectores, así que necesita las <b>dos</b> llaves a la vez: si no, otro hilo
     * podría ver el teatro a mitad de camino. Tomalas anidadas y <b>siempre en el mismo orden</b>:
     * primero la de platea y adentro la de palco. Si otro método las tomara al revés, dos hilos
     * podrían quedar esperándose mutuamente para siempre (<i>deadlock</i>, tema de la Unidad 3).
     *
     * <p>No hace nada y devuelve {@code false} si:
     *
     * <ul>
     *   <li>en el palco quedan menos de {@code cantidad} lugares, o
     *   <li>en la platea no hay {@code cantidad} espectadores para mover (es decir,
     *       {@code CAPACIDAD_PLATEA - libresPlatea < cantidad}).
     * </ul>
     *
     * @param cantidad cuántos espectadores pasar
     * @return {@code true} si se hizo el cambio
     * @throws IllegalArgumentException si {@code cantidad < 1}
     */
    public boolean pasarAPalco(int cantidad) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Teatro.pasarAPalco()");
    }

    /**
     * Devuelve los lugares libres en platea, leídos con la llave de la platea.
     *
     * @return los lugares libres en platea
     */
    public int getLibresPlatea() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Teatro.getLibresPlatea()");
    }

    /**
     * Devuelve los lugares libres en palco, leídos con la llave del palco.
     *
     * @return los lugares libres en palco
     */
    public int getLibresPalco() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Teatro.getLibresPalco()");
    }

    /** Simula el sistema de pagos. Ya está hecho. */
    private void cobrar() throws InterruptedException {
        Thread.sleep(demoraCobroMs);
    }

    /** Rechaza cantidades menores que 1. Ya está hecho. */
    private static void validar(int cantidad) {
        if (cantidad < 1) {
            throw new IllegalArgumentException("cantidad inválida: " + cantidad);
        }
    }
}
