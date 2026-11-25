import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/**
 * Cola FIFO sin límite que además se puede <b>cerrar</b>, para que los consumidores sepan cuándo
 * terminar.
 *
 * <p>El problema que resuelve: un consumidor que hace {@code while (true) { x = cola.sacar(); ... }}
 * nunca termina, porque cuando no hay más trabajo se queda en {@code wait()} para siempre. Y el
 * enunciado de los TPs prohíbe {@code Thread.stop}, {@code System.exit} y la espera activa. La
 * solución: el productor, cuando termina, <b>cierra</b> la cola, y {@link #sacar()} le avisa al
 * consumidor que no va a venir nada más.
 *
 * <pre>
 *   estado de la cola          sacar() hace...
 *   ─────────────────────────  ──────────────────────────────────────────
 *   con elementos              devuelve el primero (abierta o cerrada)
 *   vacía y abierta            espera (puede llegar algo)
 *   vacía y cerrada            devuelve Optional.empty(): fin del trabajo
 * </pre>
 *
 * <p>Herramientas permitidas: {@code synchronized}, {@code wait}, {@code notifyAll} y colecciones
 * comunes. Nada de {@code java.util.concurrent}.
 *
 * @param <T> tipo de los elementos
 */
public class ColaCerrable<T> {

    /** Elementos pendientes. Lo protege el monitor de this. */
    private final Deque<T> elementos = new ArrayDeque<>();

    /** Si ya se cerró. Lo protege el monitor de this. */
    private boolean cerrada = false;

    /**
     * Agrega un elemento al final. Nunca espera (la cola no tiene límite).
     *
     * <p>Después de agregar, avisá con {@code notifyAll()} a los que esperan en {@link #sacar()}.
     *
     * @param elemento el elemento a agregar (no {@code null})
     * @throws IllegalStateException si la cola ya está cerrada
     * @throws NullPointerException si el elemento es {@code null}
     */
    public synchronized void poner(T elemento) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("ColaCerrable.poner()");
    }

    /**
     * Saca el primer elemento, esperando mientras la cola esté vacía <b>y</b> abierta.
     *
     * <pre>
     *   mientras esté vacía y no esté cerrada:
     *       wait()
     *   si está vacía (entonces está cerrada): devolver Optional.empty()
     *   si no: sacar el primero y devolverlo envuelto en Optional.of(...)
     * </pre>
     *
     * <p>Fijate que si la cola se cierra con elementos adentro, esos elementos igual se entregan: el
     * cierre significa "no va a entrar nada más", no "tirá lo que queda".
     *
     * @return el primer elemento, o {@link Optional#empty()} si la cola está cerrada y vacía
     * @throws InterruptedException si interrumpen al hilo mientras espera
     */
    public synchronized Optional<T> sacar() throws InterruptedException {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("ColaCerrable.sacar()");
    }

    /**
     * Cierra la cola: a partir de ahora no se puede {@link #poner(Object)} y, cuando se vacíe,
     * {@link #sacar()} devuelve {@link Optional#empty()}. Cerrar dos veces no hace nada.
     *
     * <p>Al cerrar hay que despertar a <b>todos</b> los consumidores que estén esperando: cada uno
     * tiene que volver a mirar la condición y enterarse de que se terminó. Con {@code notify()} se
     * despertaría uno solo y los demás quedarían colgados.
     */
    public synchronized void cerrar() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("ColaCerrable.cerrar()");
    }

    /**
     * Dice si la cola ya fue cerrada.
     *
     * @return {@code true} si se llamó a {@link #cerrar()}
     */
    public synchronized boolean estaCerrada() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("ColaCerrable.estaCerrada()");
    }
}
