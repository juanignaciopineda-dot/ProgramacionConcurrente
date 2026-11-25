import java.util.concurrent.CountDownLatch;

/**
 * Un módulo del servidor (base de datos, caché, red...) que se inicializa en su propio hilo y, al
 * terminar, avisa con {@code countDown()} sobre el {@link CountDownLatch} que comparte con el
 * {@link Tablero}.
 *
 * <p>La inicialización puede fallar: {@link #inicializar()} lanza una excepción unchecked. Si el aviso
 * dependiera de que todo salga bien, un módulo roto dejaría al tablero esperando para siempre. Por
 * eso el {@code countDown()} va en un {@code finally}, que se ejecuta salga bien o mal.
 */
public class Modulo implements Runnable {

    private final String nombre;
    private final long demoraMs;
    private final boolean falla;
    private final CountDownLatch listos;

    /** {@code true} cuando la inicialización terminó bien. Lo escribe el hilo del módulo. */
    private volatile boolean inicializado = false;

    /**
     * Crea un módulo. Ya está hecho.
     *
     * @param nombre   nombre del módulo
     * @param demoraMs cuánto tarda en inicializarse
     * @param falla    {@code true} para que la inicialización lance una excepción
     * @param listos   el latch sobre el que avisa cuando termina
     */
    public Modulo(String nombre, long demoraMs, boolean falla, CountDownLatch listos) {
        this.nombre = nombre;
        this.demoraMs = demoraMs;
        this.falla = falla;
        this.listos = listos;
    }

    /**
     * Inicializa el módulo y avisa que terminó.
     *
     * <pre>
     *   try {
     *       inicializar();
     *       inicializado = true;
     *   } finally {
     *       listos.countDown();      ← siempre, aunque inicializar() lance una excepción
     *   }
     * </pre>
     *
     * <p>No captures la excepción de {@code inicializar()}: dejala salir del {@code run()}. El hilo
     * termina con ella y la recibe su {@code UncaughtExceptionHandler} (Unidad 1), que es quien se
     * entera de que el módulo falló. El {@code try}/{@code finally} sin {@code catch} hace las dos
     * cosas: avisa y deja pasar el error.
     */
    @Override
    public void run() {
        try {
            inicializar();
            inicializado = true;
        } finally {
            listos.countDown();
        }
    }

    /**
     * Simula la inicialización: tarda {@code demoraMs} y, si el módulo está configurado para fallar,
     * lanza {@link IllegalStateException}. Ya está hecho.
     */
    private void inicializar() {
        try {
            Thread.sleep(demoraMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrumpieron la inicialización de " + nombre, e);
        }
        if (falla) {
            throw new IllegalStateException("falló la inicialización de " + nombre);
        }
    }

    /** @return el nombre del módulo */
    public String getNombre() {
        return nombre;
    }

    /** @return {@code true} si la inicialización terminó bien */
    public boolean isInicializado() {
        return inicializado;
    }
}
