/**
 * Un pedido que atiende el servidor: tarda {@code duracionMs} y anota qué hilo del pool lo atendió.
 *
 * <p>Tiene que <b>responder a la interrupción</b>. {@code shutdownNow()} no frena a los hilos del
 * pool a la fuerza: les hace {@code interrupt()}, y una tarea que se traga la interrupción sigue
 * corriendo como si nada.
 */
public class Pedido implements Runnable {

    private final String nombre;
    private final long duracionMs;

    /** Nombre del hilo que lo atendió, o {@code null} si todavía no lo atendió nadie. */
    private volatile String atendidoPor = null;

    /** {@code true} si se atendió completo. */
    private volatile boolean terminado = false;

    /** {@code true} si lo interrumpieron mientras se atendía. */
    private volatile boolean interrumpido = false;

    /**
     * Crea un pedido. Ya está hecho.
     *
     * @param nombre     nombre del pedido
     * @param duracionMs cuánto tarda en atenderse
     */
    public Pedido(String nombre, long duracionMs) {
        this.nombre = nombre;
        this.duracionMs = duracionMs;
    }

    /**
     * Atiende el pedido.
     *
     * <ol>
     *   <li>Guardá en {@code atendidoPor} el nombre del hilo actual.
     *   <li>{@code Thread.sleep(duracionMs)} y después {@code terminado = true}.
     *   <li>Si lo interrumpen durante el {@code sleep}: {@code interrumpido = true}, restaurá la
     *       interrupción con {@code Thread.currentThread().interrupt()} y terminá. No marques
     *       {@code terminado}.
     * </ol>
     *
     * <p>¿Por qué restaurar la interrupción si el método termina igual? Porque el hilo no es tuyo: es
     * del pool, y el pool se fija en esa marca para saber que lo están cerrando.
     */
    @Override
    public void run() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Pedido.run()");
    }

    /** @return el nombre del hilo que lo atendió, o {@code null} */
    public String getAtendidoPor() {
        return atendidoPor;
    }

    /** @return {@code true} si se atendió completo */
    public boolean isTerminado() {
        return terminado;
    }

    /** @return {@code true} si lo interrumpieron mientras se atendía */
    public boolean isInterrumpido() {
        return interrumpido;
    }

    /** @return el nombre del pedido (así aparece en la lista de rechazados) */
    @Override
    public String toString() {
        return nombre;
    }
}
