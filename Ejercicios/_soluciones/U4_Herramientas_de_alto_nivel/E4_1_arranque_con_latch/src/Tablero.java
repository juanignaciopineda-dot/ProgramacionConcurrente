import java.util.concurrent.CountDownLatch;

/**
 * El tablero que muestra el estado del servidor. No puede empezar hasta que <b>todos</b> los módulos
 * hayan terminado de inicializarse, y después informa cada {@code periodoMs}, para siempre, hasta que
 * lo interrumpan.
 *
 * <p>Esperar a N cosas es justo lo que hace un {@link CountDownLatch}: arranca en N, cada módulo
 * descuenta uno con {@code countDown()}, y {@code await()} duerme al tablero hasta que el contador
 * llega a 0. Comparado con {@code join()}, el tablero no necesita conocer los hilos de los módulos,
 * solo el latch.
 *
 * <p>Corre en un hilo <b>daemon</b> (ver {@link Arranque#lanzarTablero(Tablero)}): como su bucle no
 * termina nunca, si no fuera daemon impediría que el programa termine.
 */
public class Tablero implements Runnable {

    private final CountDownLatch listos;
    private final long periodoMs;

    /** {@code true} desde que terminó la espera y empezó a informar. */
    private volatile boolean arranco = false;

    /** Cuántas veces informó. Solo lo escribe el hilo del tablero. */
    private volatile int reportes = 0;

    /**
     * Crea el tablero. Ya está hecho.
     *
     * @param listos    el latch que descuentan los módulos
     * @param periodoMs cada cuánto informa
     */
    public Tablero(CountDownLatch listos, long periodoMs) {
        this.listos = listos;
        this.periodoMs = periodoMs;
    }

    /**
     * Espera a los módulos y después informa periódicamente hasta que lo interrumpan.
     *
     * <pre>
     *   try {
     *       listos.await();               ← duerme (WAITING) hasta que el contador llegue a 0
     *       arranco = true;
     *       while (true) {
     *           reportes++;
     *           Thread.sleep(periodoMs);
     *       }
     *   } catch (InterruptedException e) {
     *       // lo interrumpieron: termina
     *   }
     * </pre>
     *
     * <p>Nada de preguntar {@code listos.getCount() == 0} en un bucle: eso es espera activa y gasta un
     * núcleo entero sin hacer nada. {@code await()} duerme al hilo sin consumir CPU.
     */
    @Override
    public void run() {
        try {
            listos.await();
            arranco = true;
            while (true) {
                reportes++;
                Thread.sleep(periodoMs);
            }
        } catch (InterruptedException e) {
            // lo interrumpieron mientras esperaba o entre dos reportes: termina
        }
    }

    /** @return {@code true} si ya terminó de esperar a los módulos */
    public boolean haArrancado() {
        return arranco;
    }

    /** @return cuántas veces informó */
    public int getReportes() {
        return reportes;
    }
}
