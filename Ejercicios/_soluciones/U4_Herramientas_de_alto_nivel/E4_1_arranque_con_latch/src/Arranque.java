import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Arma el arranque del servidor: un hilo por módulo y uno daemon para el tablero, coordinados por un
 * {@link CountDownLatch} que arranca en la cantidad de módulos.
 *
 * <pre>
 *   CountDownLatch listos = new CountDownLatch(modulos.size());
 *   lanzarTablero(new Tablero(listos, 1000));     ← espera en await()
 *   lanzarModulos(modulos);                       ← cada uno, al terminar, countDown()
 * </pre>
 */
public final class Arranque {

    private Arranque() {
    }

    /**
     * Crea y arranca un hilo por módulo, con nombre {@code "modulo-" + getNombre()}.
     *
     * @param modulos los módulos a inicializar
     * @return los hilos, ya arrancados, en el mismo orden que los módulos
     */
    public static List<Thread> lanzarModulos(List<Modulo> modulos) {
        List<Thread> hilos = new ArrayList<>();
        for (Modulo m : modulos) {
            Thread h = new Thread(m, "modulo-" + m.getNombre());
            hilos.add(h);
            h.start();
        }
        return hilos;
    }

    /**
     * Crea y arranca el hilo del tablero: nombre {@code "tablero"} y <b>daemon</b>.
     *
     * <p>{@code setDaemon(true)} va <b>antes</b> de {@code start()}: con el hilo ya arrancado lanza
     * {@code IllegalThreadStateException}.
     *
     * @param tablero el tablero
     * @return el hilo del tablero, ya arrancado
     */
    public static Thread lanzarTablero(Tablero tablero) {
        Thread hilo = new Thread(tablero, "tablero");
        hilo.setDaemon(true);
        hilo.start();
        return hilo;
    }

    /**
     * Espera a que todos los módulos avisen, pero como máximo {@code maxMs}. Sirve para no quedarse
     * esperando para siempre a un módulo que se colgó.
     *
     * <p>Es una línea: {@code listos.await(maxMs, TimeUnit.MILLISECONDS)}, que devuelve {@code true}
     * si el contador llegó a 0 y {@code false} si se venció el plazo. Si el contador ya estaba en 0,
     * vuelve enseguida con {@code true}: un latch en 0 queda en 0 para siempre.
     *
     * @param listos el latch de los módulos
     * @param maxMs  cuánto esperar, como máximo
     * @return {@code true} si avisaron todos a tiempo
     * @throws InterruptedException si interrumpen al hilo mientras espera
     */
    public static boolean esperarModulos(CountDownLatch listos, long maxMs) throws InterruptedException {
        return listos.await(maxMs, TimeUnit.MILLISECONDS);
    }
}
