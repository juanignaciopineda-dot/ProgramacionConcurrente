/**
 * Fábrica de hilos "especiales": un hilo daemon y un hilo con manejador de excepciones propio.
 *
 * <p>Clase utilitaria: solo métodos {@code static}.
 */
public final class Servicios {

    private Servicios() {}

    /**
     * Crea un hilo <b>daemon</b> llamado {@code "vigilante"} que ejecuta la tarea, sin arrancarlo.
     *
     * <p>Un programa Java termina cuando terminan todos sus hilos <b>no</b> daemon: los daemon no lo
     * retienen, y la JVM los corta donde estén. Por eso sirven para servicios de fondo (limpiar,
     * monitorear) y nunca para trabajo que tiene que completarse.
     *
     * <p>{@link Thread#setDaemon(boolean)} hay que llamarlo <b>antes</b> de {@code start()}; después
     * lanza {@link IllegalThreadStateException}.
     *
     * @param tarea lo que va a hacer el vigilante
     * @return el hilo daemon, en estado {@code NEW}
     */
    public static Thread crearVigilante(Runnable tarea) {
        Thread vigilante = new Thread(tarea, "vigilante");
        vigilante.setDaemon(true);
        return vigilante;
    }

    /**
     * Crea un hilo con el nombre pedido y con su <b>propio</b> manejador de excepciones no
     * controladas, sin arrancarlo.
     *
     * <p>Usá {@link Thread#setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler)}, que lo
     * asigna solo a este hilo. (También existe
     * {@link Thread#setDefaultUncaughtExceptionHandler(Thread.UncaughtExceptionHandler)}, que es
     * {@code static} y vale para todos los hilos que no tengan uno propio; acá no lo uses.)
     *
     * @param tarea lo que va a hacer el hilo
     * @param nombre nombre del hilo
     * @param manejador a quién avisar si el hilo muere por una excepción
     * @return el hilo nuevo, en estado {@code NEW}
     */
    public static Thread crearSupervisado(Runnable tarea, String nombre, Thread.UncaughtExceptionHandler manejador) {
        Thread hilo = new Thread(tarea, nombre);
        hilo.setUncaughtExceptionHandler(manejador);
        return hilo;
    }
}
