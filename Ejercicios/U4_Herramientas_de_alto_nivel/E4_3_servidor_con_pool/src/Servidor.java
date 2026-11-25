import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Un servidor que atiende pedidos con un pool fijo de hilos, como el ejemplo de los apuntes.
 *
 * <p>El servidor no crea hilos: le pasa cada pedido al pool con {@code execute()}, y el pool decide
 * qué hilo lo atiende. Si todos están ocupados, el pedido espera en la cola del pool. El ciclo de vida
 * tiene tres salidas:
 *
 * <pre>
 *   shutdown()              → no acepta más pedidos, pero atiende los que ya tiene (corriendo y en cola)
 *   shutdownNow()           → no acepta más, descarta los de la cola (y los devuelve) e interrumpe a los que corren
 *   awaitTermination(t, u)  → espera, como máximo t, a que el pool termine del todo
 * </pre>
 *
 * <p>{@code shutdown()} y {@code shutdownNow()} <b>no bloquean</b>: avisan y vuelven enseguida. Para
 * esperar, {@code awaitTermination}.
 *
 * <p>Herramientas permitidas: {@code Executors} y {@code ThreadPoolExecutor}. Nada de crear hilos a
 * mano ({@code new Thread}).
 */
public class Servidor {

    /** El pool que atiende los pedidos. */
    private final ThreadPoolExecutor pool;

    /** Dónde quedan anotados los pedidos que el pool rechazó. */
    private final RegistroDeRechazos rechazos = new RegistroDeRechazos();

    /**
     * Crea el servidor con un pool fijo.
     *
     * <ol>
     *   <li>Validá {@code hilos}.
     *   <li>{@code pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(hilos)}. El cast hace falta
     *       porque {@code newFixedThreadPool} declara que devuelve un {@code ExecutorService}, y los
     *       métodos de estadística ({@code getPoolSize()}, etc.) son de {@code ThreadPoolExecutor}.
     *   <li>{@code pool.setRejectedExecutionHandler(rechazos)}.
     * </ol>
     *
     * @param hilos tamaño del pool
     * @throws IllegalArgumentException si {@code hilos < 1}
     */
    public Servidor(int hilos) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("constructor de Servidor");
    }

    /**
     * Le pasa el pedido al pool con {@code execute()}. Si el pool ya está cerrado, el handler lo
     * anota y este método vuelve sin lanzar nada.
     *
     * @param pedido el pedido a atender
     */
    public void atender(Pedido pedido) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Servidor.atender()");
    }

    /**
     * Cierre ordenado: {@code shutdown()}. No espera.
     */
    public void cerrar() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Servidor.cerrar()");
    }

    /**
     * Cierre inmediato: {@code shutdownNow()}. Devuelve los pedidos que estaban en la cola y no
     * llegaron a atenderse, e interrumpe a los que se están atendiendo. No espera.
     *
     * @return las tareas que quedaron sin ejecutar
     */
    public List<Runnable> cerrarYa() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Servidor.cerrarYa()");
    }

    /**
     * Espera, como máximo {@code maxMs}, a que el pool termine del todo: {@code awaitTermination}.
     * Solo tiene sentido después de {@link #cerrar()} o {@link #cerrarYa()}: un pool abierto no
     * termina nunca, porque sus hilos esperan pedidos nuevos.
     *
     * @param maxMs cuánto esperar, como máximo
     * @return {@code true} si terminó, {@code false} si se venció el plazo
     * @throws InterruptedException si interrumpen al hilo mientras espera
     */
    public boolean esperarCierre(long maxMs) throws InterruptedException {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Servidor.esperarCierre()");
    }

    /**
     * @return cuántos hilos tiene el pool ahora ({@code getPoolSize()})
     */
    public int hilosEnElPool() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Servidor.hilosEnElPool()");
    }

    /**
     * @return cuántos hilos están atendiendo un pedido ahora ({@code getActiveCount()})
     */
    public int atendiendo() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Servidor.atendiendo()");
    }

    /**
     * @return cuántos pedidos terminó el pool ({@code getCompletedTaskCount()})
     */
    public long completados() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Servidor.completados()");
    }

    /**
     * Ya está hecho.
     *
     * @return los nombres de los pedidos rechazados
     */
    public List<String> getRechazados() {
        return rechazos.getRechazados();
    }
}
