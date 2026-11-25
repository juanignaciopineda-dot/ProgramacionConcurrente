import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * El buffer acotado como <b>monitor con dos variables de condición</b>: un {@link ReentrantLock} da
 * la exclusión mutua, y dos {@link Condition} creadas a partir de él son las dos colas donde esperan
 * los hilos.
 *
 * <pre>
 *   noLleno   ← acá esperan los productores (buffer lleno);   la avisa sacar()
 *   noVacio   ← acá esperan los consumidores (buffer vacío);  la avisa poner()
 * </pre>
 *
 * <p>Una {@code Condition} funciona como el {@code wait}/{@code notify} de un objeto, pero un lock
 * puede tener varias:
 *
 * <ul>
 *   <li>{@code condicion.await()}: suelta el lock, duerme en la cola de esa condición y, al despertar,
 *       vuelve a tomar el lock antes de seguir. Se llama con el lock tomado.
 *   <li>{@code condicion.signal()}: despierta a <b>un</b> hilo de la cola de esa condición. También con
 *       el lock tomado.
 * </ul>
 *
 * <p>En el ejercicio 2.3 hacía falta {@code notifyAll()} porque productores y consumidores esperaban
 * en el mismo <i>wait set</i>, y {@code notify()} podía despertar al tipo equivocado. Acá cada tipo
 * espera en su propia cola, así que {@code signal()} siempre despierta a uno que sirve.
 *
 * <p>Lo que <b>no</b> cambia: la política de Java es <i>signal and continue</i>. El que hace
 * {@code signal()} sigue con el lock, y el despertado tiene que volver a competir por él. En el medio
 * puede entrar otro hilo y cambiar el estado, así que la espera sigue siendo con {@code while}.
 *
 * <p>Herramientas permitidas: {@code ReentrantLock}, {@code Condition} ({@code await} y
 * {@code signal}) y colecciones comunes. Nada de {@code signalAll}, {@code synchronized},
 * {@code wait}/{@code notify} ni semáforos.
 *
 * @param <T> tipo de los elementos
 */
public class BufferConCondiciones<T> {

    /** Elementos guardados; el primero en salir está al principio. Lo protege {@code candado}. */
    private final Deque<T> elementos = new ArrayDeque<>();

    /** Cantidad máxima de elementos. */
    private final int capacidad;

    /** La exclusión mutua del monitor. */
    private final ReentrantLock candado = new ReentrantLock();

    /** Cola de los productores que esperan lugar. */
    private final Condition noLleno = candado.newCondition();

    /** Cola de los consumidores que esperan un elemento. */
    private final Condition noVacio = candado.newCondition();

    /**
     * Crea un buffer vacío.
     *
     * @param capacidad cantidad máxima de elementos
     * @throws IllegalArgumentException si {@code capacidad < 1}
     */
    public BufferConCondiciones(int capacidad) {
        if (capacidad < 1) {
            throw new IllegalArgumentException("capacidad inválida: " + capacidad);
        }
        this.capacidad = capacidad;
    }

    /**
     * Agrega un elemento al final, esperando si el buffer está lleno.
     *
     * <pre>
     *   validar que no sea null
     *   candado.lock();
     *   try {
     *       while (lleno) noLleno.await();      ← espera en la cola de los productores
     *       agregar al final
     *       noVacio.signal();                   ← avisa a UN consumidor
     *   } finally {
     *       candado.unlock();
     *   }
     * </pre>
     *
     * @param elemento el elemento a agregar (no {@code null})
     * @throws InterruptedException si interrumpen al hilo mientras espera
     * @throws NullPointerException si el elemento es {@code null}
     */
    public void poner(T elemento) throws InterruptedException {
        if (elemento == null) {
            throw new NullPointerException("elemento null");
        }
        candado.lock();
        try {
            while (elementos.size() == capacidad) {
                noLleno.await();
            }
            elementos.addLast(elemento);
            noVacio.signal();
        } finally {
            candado.unlock();
        }
    }

    /**
     * Saca el primer elemento, esperando si el buffer está vacío. Es el simétrico de
     * {@link #poner(Object)}: espera en {@code noVacio} y avisa en {@code noLleno}.
     *
     * @return el elemento más antiguo del buffer
     * @throws InterruptedException si interrumpen al hilo mientras espera
     */
    public T sacar() throws InterruptedException {
        candado.lock();
        try {
            while (elementos.isEmpty()) {
                noVacio.await();
            }
            T primero = elementos.removeFirst();
            noLleno.signal();
            return primero;
        } finally {
            candado.unlock();
        }
    }

    /**
     * @return la cantidad de elementos guardados ahora (con el candado tomado)
     */
    public int tamanio() {
        candado.lock();
        try {
            return elementos.size();
        } finally {
            candado.unlock();
        }
    }

    /** @return la cantidad máxima de elementos. Ya está hecho. */
    public int capacidad() {
        return capacidad;
    }

    /**
     * Ya está hecho: lo usa el verificador.
     *
     * @return cuántos productores esperan en {@code noLleno} ahora
     */
    public int esperandoLugar() {
        candado.lock();
        try {
            return candado.getWaitQueueLength(noLleno);
        } finally {
            candado.unlock();
        }
    }

    /**
     * Ya está hecho: lo usa el verificador.
     *
     * @return cuántos consumidores esperan en {@code noVacio} ahora
     */
    public int esperandoElemento() {
        candado.lock();
        try {
            return candado.getWaitQueueLength(noVacio);
        } finally {
            candado.unlock();
        }
    }
}
