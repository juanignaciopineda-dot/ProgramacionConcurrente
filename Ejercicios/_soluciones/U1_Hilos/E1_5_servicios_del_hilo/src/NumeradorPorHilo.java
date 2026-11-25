/**
 * Entrega números consecutivos {@code 1, 2, 3, ...}, pero con una secuencia <b>independiente para
 * cada hilo</b>: si dos hilos usan el mismo numerador, cada uno ve su propia secuencia.
 *
 * <pre>
 *   hilo A: siguiente() → 1   siguiente() → 2   siguiente() → 3
 *   hilo B: siguiente() → 1   siguiente() → 2
 * </pre>
 *
 * <p>Es un solo objeto, compartido por todos los hilos, y sin {@code synchronized}. El truco es
 * {@link ThreadLocal}: una variable que tiene una copia distinta por cada hilo que la usa. Como cada
 * hilo solo toca su copia, no hay nada compartido que proteger.
 *
 * <p>Uso típico en sistemas reales: guardar por hilo una conexión, un formateador de fechas que no
 * es thread-safe, o el identificador del pedido que se está atendiendo.
 */
public class NumeradorPorHilo {

    /** Último número entregado a cada hilo. Cada hilo arranca en 0. */
    private final ThreadLocal<Integer> ultimo = ThreadLocal.withInitial(() -> 0);

    /**
     * Devuelve el siguiente número de la secuencia del hilo que llama.
     *
     * <p>Leé el valor del hilo actual con {@link ThreadLocal#get()}, sumale uno, guardalo con
     * {@link ThreadLocal#set(Object)} y devolvelo. La primera llamada de cada hilo devuelve 1.
     *
     * @return el siguiente número para este hilo
     */
    public int siguiente() {
        int nuevo = ultimo.get() + 1;
        ultimo.set(nuevo);
        return nuevo;
    }

    /**
     * Reinicia la secuencia del hilo que llama, con {@link ThreadLocal#remove()}: la próxima llamada
     * a {@link #siguiente()} de este hilo vuelve a devolver 1. No afecta a los demás hilos.
     *
     * <p>En un pool de hilos (Unidad 4), donde un mismo hilo atiende muchas tareas, olvidarse del
     * {@code remove()} hace que una tarea vea los datos que dejó la anterior.
     */
    public void reiniciar() {
        ultimo.remove();
    }
}
