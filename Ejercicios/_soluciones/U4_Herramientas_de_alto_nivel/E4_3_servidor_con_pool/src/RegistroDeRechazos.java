import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Lo que hace el servidor con un pedido que el pool no puede aceptar (por ejemplo, porque ya lo
 * cerraron): lo anota en vez de tirar una excepción.
 *
 * <p>Sin un handler, {@code execute()} sobre un pool cerrado lanza
 * {@code RejectedExecutionException} en la cara del que envió la tarea. Con uno, el pool llama a
 * {@link #rejectedExecution(Runnable, ThreadPoolExecutor)} y quien envió sigue de largo.
 *
 * <p>{@code rejectedExecution} se ejecuta en el hilo que llamó a {@code execute()}, y pueden ser
 * varios a la vez. Por eso la lista está protegida por el monitor de this.
 */
public class RegistroDeRechazos implements RejectedExecutionHandler {

    /** Nombres de las tareas rechazadas, en orden. La protege el monitor de this. */
    private final List<String> rechazados = new ArrayList<>();

    /**
     * Anota la tarea rechazada: agregá {@code tarea.toString()} a la lista. Es {@code synchronized}.
     *
     * @param tarea    la tarea que no se pudo ejecutar
     * @param ejecutor el pool que la rechazó (acá no se usa, pero sirve para preguntarle su estado,
     *                 como {@code isShutdown()})
     */
    @Override
    public synchronized void rejectedExecution(Runnable tarea, ThreadPoolExecutor ejecutor) {
        rechazados.add(tarea.toString());
    }

    /**
     * Ya está hecho.
     *
     * @return una copia de los nombres de las tareas rechazadas
     */
    public synchronized List<String> getRechazados() {
        return new ArrayList<>(rechazados);
    }
}
