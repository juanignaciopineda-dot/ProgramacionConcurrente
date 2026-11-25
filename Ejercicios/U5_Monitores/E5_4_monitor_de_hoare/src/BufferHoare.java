/**
 * El buffer acotado escrito sobre el {@link MonitorHoare}. Es la cuarta versión de la materia, después
 * del 2.3 ({@code synchronized} y {@code wait}/{@code notifyAll}), el 3.4 (semáforos) y el 5.1
 * ({@code Lock} y {@code Condition}).
 *
 * <p>Con la política SU, el despertado entra enseguida y la condición sigue siendo cierta. Por eso acá
 * se espera con <b>{@code if}</b>, no con {@code while}: si el monitor está bien hecho, el buffer
 * funciona. Si no lo está (por ejemplo, si el que señaliza no cede el monitor), el {@code if} deja
 * pasar a un hilo con la condición falsa y el verificador lo ve.
 *
 * <p>Herramientas permitidas: el {@link MonitorHoare} y un arreglo. Nada más: todo lo que tenga que
 * ver con hilos lo resuelve el monitor.
 *
 * @param <T> tipo de los elementos
 */
public class BufferHoare<T> {

    private final MonitorHoare monitor = new MonitorHoare();
    private final MonitorHoare.Condicion noLleno = monitor.nuevaCondicion();
    private final MonitorHoare.Condicion noVacio = monitor.nuevaCondicion();

    /** Buffer circular. Lo protege el monitor. */
    private final Object[] elementos;
    private int primero = 0;
    private int cantidad = 0;

    /**
     * Crea un buffer vacío. Ya está hecho.
     *
     * @param capacidad cantidad máxima de elementos
     * @throws IllegalArgumentException si {@code capacidad < 1}
     */
    public BufferHoare(int capacidad) {
        if (capacidad < 1) {
            throw new IllegalArgumentException("capacidad inválida: " + capacidad);
        }
        elementos = new Object[capacidad];
    }

    /**
     * Agrega un elemento al final, esperando si el buffer está lleno.
     *
     * <pre>
     *   monitor.entrar()
     *   if (cantidad == elementos.length) noLleno.esperar()
     *   elementos[(primero + cantidad) % elementos.length] = elemento
     *   cantidad++
     *   noVacio.senalar()
     *   monitor.salir()
     * </pre>
     *
     * @param elemento el elemento a agregar
     */
    public void poner(T elemento) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("BufferHoare.poner()");
    }

    /**
     * Saca el primer elemento, esperando si el buffer está vacío. Es el simétrico de
     * {@link #poner(Object)}: espera en {@code noVacio} con {@code if}, saca
     * {@code elementos[primero]}, avanza {@code primero} (circular), resta uno a {@code cantidad},
     * señaliza {@code noLleno} y sale.
     *
     * @return el elemento más antiguo
     */
    @SuppressWarnings("unchecked")
    public T sacar() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("BufferHoare.sacar()");
    }

    /**
     * Ya está hecho.
     *
     * @return la cantidad de elementos guardados ahora
     */
    public int tamanio() {
        monitor.entrar();
        int n = cantidad;
        monitor.salir();
        return n;
    }
}
