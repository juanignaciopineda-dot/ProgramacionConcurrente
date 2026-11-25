import java.util.ArrayList;
import java.util.List;

/**
 * Manejador de excepciones no controladas: anota qué hilo se murió y por qué.
 *
 * <p>Si el {@code run()} de un hilo termina con una excepción que nadie capturó, el hilo muere y,
 * por defecto, la JVM solo imprime el stack trace en la consola. Con un
 * {@link Thread.UncaughtExceptionHandler} podemos decidir qué hacer: registrarlo, avisar, relanzar la
 * tarea, etc.
 */
public class RegistroDeFallas implements Thread.UncaughtExceptionHandler {

    /** Fallas registradas, en el orden en que llegaron. */
    private final List<String> fallas = new ArrayList<>();

    /**
     * Lo llama la JVM, <b>desde el hilo que se está muriendo</b>, cuando su {@code run()} termina con
     * una excepción no capturada.
     *
     * <p>Registrá una línea con el formato exacto:
     *
     * <pre>&lt;nombre del hilo&gt;: &lt;nombre simple de la clase de la excepción&gt;: &lt;mensaje&gt;</pre>
     *
     * <p>por ejemplo {@code "importador-2: IllegalStateException: archivo vacío"}. El nombre simple de
     * la clase se obtiene con {@code e.getClass().getSimpleName()}.
     *
     * <p>Para agregar la línea usá {@link #registrar(String)}, que ya está hecho: como varios hilos
     * pueden morirse a la vez, la lista está protegida con {@code synchronized} (Unidad 2).
     *
     * @param t el hilo que terminó por la excepción
     * @param e la excepción que nadie capturó
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        registrar(t.getName() + ": " + e.getClass().getSimpleName() + ": " + e.getMessage());
    }

    /**
     * Agrega una línea al registro. Ya está hecho.
     *
     * @param linea la línea a agregar
     */
    private synchronized void registrar(String linea) {
        fallas.add(linea);
    }

    /**
     * Devuelve una copia de las fallas registradas. Ya está hecho.
     *
     * @return las líneas registradas, en orden de llegada
     */
    public synchronized List<String> getFallas() {
        return List.copyOf(fallas);
    }
}
