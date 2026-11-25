import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Fábrica de hilos: centraliza la creación de todos los hilos de una parte del programa, para que
 * salgan configurados igual (nombre, daemon), para llevar estadísticas y para poder limitar cuántos
 * se crean. Es el patrón Factory aplicado a los hilos.
 *
 * <p>{@link ThreadFactory} tiene un solo método, {@code Thread newThread(Runnable r)}: recibe la
 * tarea y devuelve el hilo que la va a ejecutar, <b>sin arrancarlo</b>. Según su contrato, puede
 * devolver {@code null} para rechazar el pedido, y eso es lo que hace esta fábrica cuando se llega
 * al límite.
 *
 * <p>Los {@code Executors} usan una {@code ThreadFactory} para crear los hilos del pool. Si les pasás
 * esta ({@link #crearPool(int)}), los hilos del pool salen con estos nombres y estas estadísticas.
 *
 * <p>A una fábrica le pueden pedir hilos varios hilos a la vez (por ejemplo, un pool que crece). Por
 * eso es un <b>monitor</b>: {@code newThread} y los getters son {@code synchronized}, y protegen el
 * contador y la lista.
 *
 * <p>Herramientas permitidas: {@code synchronized}, {@code ThreadFactory}, {@code Executors} y
 * colecciones comunes. Nada de clases {@code Atomic...} ni {@code Collections.synchronized...}.
 */
public class FabricaDeHilos implements ThreadFactory {

    private final String prefijo;
    private final boolean daemon;
    private final int maxHilos;

    /** Cuántos hilos se crearon. Lo protege el monitor de this. */
    private int creados = 0;

    /** Una línea por pedido: {@code "creado <nombre>"} o {@code "rechazado: ..."}. Lo protege el monitor de this. */
    private final List<String> estadisticas = new ArrayList<>();

    /**
     * Crea una fábrica de hilos no daemon y sin límite. Ya está hecho.
     *
     * @param prefijo con qué empieza el nombre de cada hilo
     */
    public FabricaDeHilos(String prefijo) {
        this(prefijo, false, Integer.MAX_VALUE);
    }

    /**
     * Crea una fábrica.
     *
     * @param prefijo  con qué empieza el nombre de cada hilo
     * @param daemon   si los hilos creados son daemon
     * @param maxHilos cuántos hilos puede crear, como máximo
     * @throws IllegalArgumentException si {@code prefijo} es {@code null} o está en blanco, o si
     *                                  {@code maxHilos < 1}
     */
    public FabricaDeHilos(String prefijo, boolean daemon, int maxHilos) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("constructor de FabricaDeHilos");
    }

    /**
     * Crea un hilo para la tarea, sin arrancarlo.
     *
     * <ol>
     *   <li>Si ya se crearon {@code maxHilos}, anotá {@code "rechazado: límite de " + maxHilos + " hilos"}
     *       en las estadísticas y devolvé {@code null}.
     *   <li>Si no, sumá uno a {@code creados} y creá el hilo con nombre {@code prefijo + "-" + creados}
     *       (el primero es {@code "prefijo-1"}).
     *   <li>{@code setDaemon(daemon)}, anotá {@code "creado " + nombre} y devolvelo.
     * </ol>
     *
     * <p>Es {@code synchronized}: sin eso, dos hilos que piden a la vez podrían leer el mismo valor de
     * {@code creados} y salir dos hilos con el mismo nombre (es el {@code contador++} de la Unidad 2).
     *
     * @param tarea lo que va a ejecutar el hilo
     * @return el hilo nuevo, en estado {@code NEW}, o {@code null} si se llegó al límite
     */
    @Override
    public synchronized Thread newThread(Runnable tarea) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("FabricaDeHilos.newThread()");
    }

    /**
     * Crea un pool fijo de {@code hilos} hilos que fabrica sus hilos con esta fábrica. Es una línea:
     * {@code Executors.newFixedThreadPool(hilos, this)}.
     *
     * <p>El pool le va a pedir hilos a la fábrica a medida que los necesite. Si la fábrica devuelve
     * {@code null} porque llegó a su límite, el pool sigue funcionando con los hilos que ya tiene: las
     * tareas que no tienen hilo esperan en su cola.
     *
     * @param hilos tamaño del pool
     * @return el pool
     */
    public ExecutorService crearPool(int hilos) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("FabricaDeHilos.crearPool()");
    }

    /**
     * @return cuántos hilos creó la fábrica hasta ahora
     */
    public synchronized int getCreados() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("FabricaDeHilos.getCreados()");
    }

    /**
     * Devuelve una <b>copia</b> de las estadísticas: si devolvieras la lista interna, quien la recibe
     * la leería sin el monitor mientras otro hilo le agrega líneas.
     *
     * @return una línea por pedido de hilo, en orden
     */
    public synchronized List<String> getEstadisticas() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("FabricaDeHilos.getEstadisticas()");
    }
}
