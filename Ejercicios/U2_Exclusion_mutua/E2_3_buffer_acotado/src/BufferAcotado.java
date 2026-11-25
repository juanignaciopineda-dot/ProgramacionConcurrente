import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Buffer de capacidad fija para el problema del <b>productor/consumidor</b>.
 *
 * <ul>
 *   <li>Un productor que quiere {@link #poner(Object)} con el buffer lleno se queda esperando hasta
 *       que haya lugar.
 *   <li>Un consumidor que quiere {@link #sacar()} con el buffer vacío se queda esperando hasta que
 *       haya algo.
 *   <li>Los elementos salen en el mismo orden en que entraron (FIFO).
 * </ul>
 *
 * <p>Es un <b>monitor</b> hecho con las herramientas de Java: {@code synchronized} da la exclusión
 * mutua, y {@code wait()}/{@code notifyAll()} dan la sincronización por condición. Es la misma
 * restricción que la red de Petri del buffer acotado: <i>producir</i> necesita un token en "libres"
 * y <i>consumir</i> uno en "ocupados".
 *
 * <p>Herramientas permitidas: {@code synchronized}, {@code wait}, {@code notify}/{@code notifyAll} y
 * colecciones comunes (no thread-safe). Nada de {@code java.util.concurrent}: este buffer es,
 * justamente, una {@code BlockingQueue} hecha a mano.
 *
 * @param <T> tipo de los elementos
 */
public class BufferAcotado<T> {

    /** Elementos guardados; el primero en salir está al principio. Lo protege el monitor de this. */
    private final Deque<T> elementos = new ArrayDeque<>();

    /** Cantidad máxima de elementos. */
    private final int capacidad;

    /**
     * Crea un buffer vacío.
     *
     * @param capacidad cantidad máxima de elementos
     * @throws IllegalArgumentException si {@code capacidad < 1}
     */
    public BufferAcotado(int capacidad) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("constructor de BufferAcotado");
    }

    /**
     * Agrega un elemento al final, esperando si el buffer está lleno.
     *
     * <p>Estructura (el método es {@code synchronized}):
     *
     * <pre>
     *   mientras esté lleno:
     *       wait()                 ← libera el monitor y duerme hasta que alguien avise
     *   agregar al final
     *   notifyAll()                ← avisar a los que esperan "que haya algo"
     * </pre>
     *
     * <p>Dos decisiones que no son detalles:
     *
     * <ul>
     *   <li><b>{@code while}, nunca {@code if}</b>: cuando un hilo se despierta, otro pudo haberse
     *       adelantado y volver a llenar el buffer. Hay que volver a mirar la condición. (Además Java
     *       permite <i>despertares espurios</i>: un {@code wait()} puede volver sin que nadie haya
     *       avisado.)
     *   <li><b>{@code notifyAll()}, no {@code notify()}</b>: productores y consumidores esperan en el
     *       mismo monitor. {@code notify()} despierta a uno cualquiera, que puede ser del tipo
     *       equivocado (otro productor), y el aviso se pierde.
     * </ul>
     *
     * <p>No capturés la {@link InterruptedException} de {@code wait()}: dejala salir, así quien llama
     * se entera de que lo interrumpieron.
     *
     * @param elemento el elemento a agregar (no {@code null})
     * @throws InterruptedException si interrumpen al hilo mientras espera
     * @throws NullPointerException si el elemento es {@code null}
     */
    public synchronized void poner(T elemento) throws InterruptedException {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("BufferAcotado.poner()");
    }

    /**
     * Saca el primer elemento, esperando si el buffer está vacío.
     *
     * <p>Es el simétrico de {@link #poner(Object)}: mientras esté vacío, {@code wait()}; después sacá
     * el primero, avisá con {@code notifyAll()} (ahora hay un lugar libre) y devolvelo.
     *
     * @return el elemento más antiguo del buffer
     * @throws InterruptedException si interrumpen al hilo mientras espera
     */
    public synchronized T sacar() throws InterruptedException {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("BufferAcotado.sacar()");
    }

    /**
     * Devuelve cuántos elementos hay ahora. Como otros hilos pueden cambiarlo enseguida, sirve para
     * mostrar o verificar, no para decidir (no hagas {@code if (tamanio() > 0) sacar()}).
     *
     * @return la cantidad de elementos guardados
     */
    public synchronized int tamanio() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("BufferAcotado.tamanio()");
    }

    /**
     * Devuelve la capacidad. Ya está hecho: como {@code capacidad} es {@code final} y no cambia, no
     * hace falta sincronizar.
     *
     * @return la cantidad máxima de elementos
     */
    public int capacidad() {
        return capacidad;
    }
}
