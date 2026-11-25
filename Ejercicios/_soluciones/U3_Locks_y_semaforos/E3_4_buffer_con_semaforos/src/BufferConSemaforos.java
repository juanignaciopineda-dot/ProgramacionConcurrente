import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Semaphore;

/**
 * El mismo buffer acotado del ejercicio 2.3 (productor/consumidor), hecho con tres semáforos en vez
 * de {@code synchronized} y {@code wait}/{@code notifyAll}.
 *
 * <pre>
 *   huecos  (arranca en capacidad)  ← cuántos lugares libres quedan:  poner()  lo toma, sacar() lo devuelve
 *   items   (arranca en 0)          ← cuántos elementos hay:          sacar()  lo toma, poner() lo devuelve
 *   mutex   (arranca en 1)          ← exclusión mutua sobre la cola de elementos
 * </pre>
 *
 * <p>Es la red de Petri del buffer acotado escrita en Java: {@code huecos} e {@code items} son los
 * lugares "libres" y "ocupados", y su suma es siempre la capacidad (salvo en el instante en que un
 * hilo pasó por uno y todavía no llegó al otro).
 *
 * <p>Comparalo con el 2.3: acá no hay {@code while} ni {@code notifyAll()}. El que espera duerme en
 * la cola del semáforo que le corresponde, y cada {@code release()} despierta a uno de los que
 * esperan <b>ese</b> semáforo, nunca a uno del tipo equivocado. Fijate también que quien toma un
 * permiso de {@code items} no es quien lo devuelve: los semáforos no tienen dueño, y eso es justo lo
 * que hace falta acá.
 *
 * <p>Herramientas permitidas: {@code Semaphore} y colecciones comunes. Nada de {@code synchronized},
 * {@code wait}/{@code notify}, {@code Lock}, {@code BlockingQueue} ni clases {@code Atomic...}.
 *
 * @param <T> tipo de los elementos
 */
public class BufferConSemaforos<T> {

    /** Elementos guardados; el primero en salir está al principio. Lo protege {@code mutex}. */
    private final Deque<T> elementos = new ArrayDeque<>();

    /** Un permiso por lugar libre. */
    private final Semaphore huecos;

    /** Un permiso por elemento guardado. */
    private final Semaphore items;

    /** Semáforo binario que protege {@code elementos}. */
    private final Semaphore mutex;

    /** Cantidad máxima de elementos. */
    private final int capacidad;

    /**
     * Crea un buffer vacío: validá la capacidad y creá los tres semáforos con los permisos del
     * diagrama de la clase.
     *
     * @param capacidad cantidad máxima de elementos
     * @throws IllegalArgumentException si {@code capacidad < 1}
     */
    public BufferConSemaforos(int capacidad) {
        if (capacidad < 1) {
            throw new IllegalArgumentException("capacidad inválida: " + capacidad);
        }
        this.capacidad = capacidad;
        huecos = new Semaphore(capacidad);
        items = new Semaphore(0);
        mutex = new Semaphore(1);
    }

    /**
     * Agrega un elemento al final, esperando si el buffer está lleno.
     *
     * <pre>
     *   validar que no sea null
     *   huecos.acquire()                 ← espera lugar (acá sí puede dormir un rato largo)
     *   mutex.acquireUninterruptibly()
     *   try {
     *       agregar al final
     *   } finally {
     *       mutex.release()
     *   }
     *   items.release()                  ← avisa que hay un elemento más
     * </pre>
     *
     * <p>El orden importa:
     *
     * <ul>
     *   <li><b>Primero {@code huecos}, después {@code mutex}.</b> Al revés, un productor podría quedarse
     *       dormido esperando un hueco <b>con el mutex tomado</b>, y ningún consumidor podría entrar a
     *       sacar para liberarlo: deadlock.
     *   <li><b>Validar antes de {@code huecos.acquire()}.</b> Si tomás el hueco y después lanzás la
     *       excepción, ese hueco no lo devuelve nadie y el buffer queda más chico para siempre.
     *   <li><b>{@code acquireUninterruptibly()} para el mutex</b>, por lo mismo: si te interrumpieran
     *       esperándolo, saldrías con el hueco tomado y sin haber agregado nada.
     * </ul>
     *
     * @param elemento el elemento a agregar (no {@code null})
     * @throws InterruptedException si interrumpen al hilo mientras espera lugar
     * @throws NullPointerException si el elemento es {@code null}
     */
    public void poner(T elemento) throws InterruptedException {
        if (elemento == null) {
            throw new NullPointerException("elemento null");
        }
        huecos.acquire();
        mutex.acquireUninterruptibly();
        try {
            elementos.addLast(elemento);
        } finally {
            mutex.release();
        }
        items.release();
    }

    /**
     * Saca el primer elemento, esperando si el buffer está vacío. Es el simétrico de
     * {@link #poner(Object)}: {@code items.acquire()}, sacar con el mutex tomado y, al final,
     * {@code huecos.release()} (quedó un lugar libre).
     *
     * @return el elemento más antiguo del buffer
     * @throws InterruptedException si interrumpen al hilo mientras espera un elemento
     */
    public T sacar() throws InterruptedException {
        items.acquire();
        T primero;
        mutex.acquireUninterruptibly();
        try {
            primero = elementos.removeFirst();
        } finally {
            mutex.release();
        }
        huecos.release();
        return primero;
    }

    /**
     * Devuelve cuántos elementos hay ahora: el tamaño de {@code elementos}, leído con el mutex tomado
     * ({@code acquireUninterruptibly()}, {@code try}, {@code finally release()}).
     *
     * @return la cantidad de elementos guardados
     */
    public int tamanio() {
        mutex.acquireUninterruptibly();
        try {
            return elementos.size();
        } finally {
            mutex.release();
        }
    }

    /**
     * Ya está hecho.
     *
     * @return la cantidad máxima de elementos
     */
    public int capacidad() {
        return capacidad;
    }
}
