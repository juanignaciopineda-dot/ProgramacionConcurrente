/**
 * Una tarea de trabajo simulado: cuando se ejecuta, anota qué hilo la corrió y después "trabaja"
 * durmiendo un rato.
 *
 * <p>Implementa {@link Runnable}, que es la forma recomendada de definir lo que hace un hilo: la
 * <b>tarea</b> (qué hay que hacer) queda separada del <b>mecanismo</b> que la ejecuta (el
 * {@link Thread}). La misma tarea se puede correr en un hilo nuevo, en el hilo actual o, más
 * adelante en la materia, en un pool de hilos.
 */
public class Tarea implements Runnable {

    /** Nombre descriptivo de la tarea (solo para mostrar). */
    private final String nombre;

    /** Cuánto "trabaja" la tarea, en milisegundos. */
    private final long duracionMs;

    /**
     * Nombre del hilo que ejecutó {@link #run()}, o {@code null} si todavía no se ejecutó.
     *
     * <p>Lo escribe un hilo y lo lee otro, pero quien lo lee siempre lo hace después de un
     * {@code join()} sobre el hilo que lo escribió, y eso garantiza que la escritura sea visible.
     */
    private String ejecutadaPor;

    /**
     * Crea una tarea. Este constructor ya está hecho.
     *
     * @param nombre nombre descriptivo de la tarea
     * @param duracionMs cuánto dura el trabajo simulado, en milisegundos
     */
    public Tarea(String nombre, long duracionMs) {
        this.nombre = nombre;
        this.duracionMs = duracionMs;
    }

    /**
     * Cuerpo de la tarea: es lo que corre el hilo cuando alguien llama a {@link Thread#start()}.
     *
     * <p>Pasos:
     *
     * <ol>
     *   <li>Guardá en {@link #ejecutadaPor} el nombre del hilo que está ejecutando este código. Ese
     *       hilo se obtiene con {@link Thread#currentThread()}, y su nombre con
     *       {@link Thread#getName()}.
     *   <li>Simulá el trabajo durmiendo {@link #duracionMs} milisegundos con
     *       {@link Thread#sleep(long)}.
     *   <li>{@code sleep} puede lanzar {@link InterruptedException}, pero {@code run()} no puede
     *       declararla (la firma de {@link Runnable#run()} no lo permite), así que capturala. Si te
     *       interrumpen, volvé a marcar la interrupción con
     *       {@code Thread.currentThread().interrupt()} y terminá. La interrupción se ve a fondo en el
     *       ejercicio 1.4; por ahora alcanza con esto.
     * </ol>
     */
    @Override
    public void run() {
        ejecutadaPor = Thread.currentThread().getName();
        try {
            Thread.sleep(duracionMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Devuelve el nombre del hilo que ejecutó la tarea.
     *
     * @return el nombre del hilo, o {@code null} si la tarea todavía no corrió
     */
    public String getEjecutadaPor() {
        return ejecutadaPor;
    }

    /**
     * Devuelve el nombre de la tarea.
     *
     * @return el nombre descriptivo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve la duración del trabajo simulado.
     *
     * @return la duración en milisegundos
     */
    public long getDuracionMs() {
        return duracionMs;
    }
}
