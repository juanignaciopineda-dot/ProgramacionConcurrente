import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Una ventanilla que atiende de a un cliente, con varios <b>tipos</b> de cliente (por ejemplo,
 * prioritarios y comunes). Cada tipo espera en su propia cola, y cuando la ventanilla se libera una
 * {@link Politica} decide de qué cola sale el siguiente.
 *
 * <p>Es la estructura del monitor del TP final, sin la red de Petri:
 *
 * <pre>
 *   mutex      ← exclusión mutua de entrada al monitor
 *   colas[t]   ← una variable de condición por tipo de cliente   (en el TP: una por transición)
 *   politica   ← a qué cola despertar cuando hay varias con gente  (resuelve los conflictos)
 *   ocupada    ← el estado del recurso                             (en el TP: el marcado de la red)
 * </pre>
 *
 * <p>Con <i>signal and exit</i>, la ventanilla <b>pasa de mano</b>: el que la libera y despierta a
 * alguien no la marca libre, le pasa el mutex al despertado con la ventanilla todavía ocupada, y el
 * despertado sigue como su nuevo dueño. Ningún recién llegado se la puede ganar en el medio.
 *
 * <p>Herramientas permitidas: {@code Semaphore} y la {@link ColaDeCondicion}. Nada de
 * {@code synchronized}, locks ni {@code wait}/{@code notify}.
 */
public class Ventanilla {

    /** La cola de entrada del monitor. */
    private final Semaphore mutex = new Semaphore(1, true);

    /** Una cola por tipo de cliente. */
    private final ColaDeCondicion[] colas;

    /** Quién decide a qué cola despertar. */
    private final Politica politica;

    /** Cuánto tarda la atención (fuera del monitor). */
    private final long demoraMs;

    /** {@code true} si hay un cliente en la ventanilla. Lo protege el mutex. */
    private boolean ocupada = false;

    /** Para el verificador: a quién se atendió, en orden. Lo protege el mutex. */
    private final List<String> atendidos = new ArrayList<>();

    /** Para el verificador: cuántos se atienden a la vez ahora, y el máximo que hubo. */
    private final AtomicInteger atendiendo = new AtomicInteger();
    private final AtomicInteger maxAtendiendo = new AtomicInteger();

    /**
     * Crea la ventanilla libre, con una cola vacía por tipo.
     *
     * @param tipos    cantidad de tipos de cliente
     * @param politica a qué cola despertar cuando hay varias con gente
     * @param demoraMs cuánto tarda la atención
     * @throws IllegalArgumentException si {@code tipos < 1}, {@code politica} es {@code null} o
     *                                  {@code demoraMs < 0}
     */
    public Ventanilla(int tipos, Politica politica, long demoraMs) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("constructor de Ventanilla");
    }

    /**
     * Procedimiento del monitor: el cliente de tipo {@code tipo} pide la ventanilla.
     *
     * <pre>
     *   validar tipo
     *   mutex.acquireUninterruptibly()
     *   if (ocupada)
     *       colas[tipo].esperar(mutex)     ← vuelve con el mutex y con la ventanilla ya suya
     *   else
     *       ocupada = true
     *   anotarAtencion()
     *   mutex.release()
     * </pre>
     *
     * @param tipo el tipo de cliente
     * @throws IllegalArgumentException si {@code tipo} no existe
     */
    public void solicitar(int tipo) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Ventanilla.solicitar()");
    }

    /**
     * Procedimiento del monitor: el cliente que estaba en la ventanilla la deja.
     *
     * <ol>
     *   <li>Con el mutex tomado: si no estaba ocupada, soltá el mutex y lanzá
     *       {@code IllegalStateException}.
     *   <li>Armá {@code conGente}: {@code conGente[t]} es {@code colas[t].esperando() > 0}.
     *   <li>Si ninguna cola tiene gente: {@code ocupada = false} y soltá el mutex.
     *   <li>Si alguna tiene: {@code politica.elegir(conGente)} y {@code despertarUno()} en esa cola. No
     *       toques {@code ocupada} ni sueltes el mutex: los dos pasan al despertado.
     * </ol>
     *
     * @throws IllegalStateException si la ventanilla no estaba ocupada
     */
    public void liberar() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Ventanilla.liberar()");
    }

    /**
     * Atiende a un cliente: pide la ventanilla, lo atiende <b>fuera del monitor</b> y la libera.
     *
     * <pre>
     *   solicitar(tipo);
     *   try {
     *       trabajar();
     *   } finally {
     *       liberar();
     *   }
     * </pre>
     *
     * @param tipo el tipo de cliente
     */
    public void atender(int tipo) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Ventanilla.atender()");
    }

    /** Ya está hecho: la atención en sí, fuera del monitor. */
    private void trabajar() {
        int ahora = atendiendo.incrementAndGet();
        maxAtendiendo.accumulateAndGet(ahora, Math::max);
        try {
            if (demoraMs > 0) {
                Thread.sleep(demoraMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            atendiendo.decrementAndGet();
        }
    }

    /** Ya está hecho: anota quién pasó a la ventanilla. Se llama con el mutex tomado. */
    private void anotarAtencion() {
        atendidos.add(Thread.currentThread().getName());
    }

    /**
     * Ya está hecho.
     *
     * @param tipo un tipo de cliente
     * @return cuántos clientes de ese tipo esperan
     */
    public int esperando(int tipo) {
        mutex.acquireUninterruptibly();
        try {
            return colas[tipo].esperando();
        } finally {
            mutex.release();
        }
    }

    /**
     * Ya está hecho.
     *
     * @return los nombres de los hilos que pasaron por la ventanilla, en orden
     */
    public List<String> getAtendidos() {
        mutex.acquireUninterruptibly();
        try {
            return new ArrayList<>(atendidos);
        } finally {
            mutex.release();
        }
    }

    /** @return el máximo de clientes que se atendieron a la vez. Ya está hecho. */
    public int maxAtendiendo() {
        return maxAtendiendo.get();
    }
}
