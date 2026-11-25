/**
 * Dos utilidades para trabajar bien con la interrupción.
 *
 * <p>En Java no se puede "matar" a un hilo de forma segura ({@code Thread.stop()} está prohibido en
 * los TPs y ya ni siquiera funciona en las versiones nuevas). Lo que se hace es <b>pedirle</b> que
 * termine con {@link Thread#interrupt()}, y el hilo, cooperando, termina por su cuenta.
 */
public final class Interrupciones {

    private Interrupciones() {}

    /**
     * Le pide a un hilo que termine y espera, como máximo {@code timeoutMs}, a que lo haga.
     *
     * <ol>
     *   <li>Interrumpilo con {@link Thread#interrupt()}.
     *   <li>Esperalo con {@link Thread#join(long)}: si el hilo no coopera, {@code join} sin tiempo
     *       máximo nos dejaría colgados para siempre.
     *   <li>Devolvé si terminó ({@link Thread#isAlive()} da {@code false}).
     * </ol>
     *
     * @param t el hilo a detener
     * @param timeoutMs cuánto esperar como máximo, en milisegundos
     * @return {@code true} si el hilo terminó dentro del plazo
     * @throws InterruptedException si interrumpen al hilo que llama mientras espera
     */
    public static boolean detener(Thread t, long timeoutMs) throws InterruptedException {
        t.interrupt();
        t.join(timeoutMs);
        return !t.isAlive();
    }

    /**
     * Duerme {@code ms} milisegundos <b>sin perder</b> una interrupción.
     *
     * <p>Sirve para métodos que no pueden (o no quieren) declarar {@code throws
     * InterruptedException}:
     *
     * <ul>
     *   <li>Si el sueño se completa, devolvé {@code true}.
     *   <li>Si lo interrumpen, <b>restaurá la marca</b> con {@code Thread.currentThread().interrupt()}
     *       y devolvé {@code false}.
     * </ul>
     *
     * <p>¿Por qué restaurarla? Porque {@code sleep} la borró al lanzar la excepción. Quien llamó a
     * este método puede estar en un bucle {@code while (!isInterrupted())}: si te tragás la
     * excepción, ese bucle nunca se entera del pedido de terminar.
     *
     * @param ms cuánto dormir, en milisegundos
     * @return {@code true} si durmió todo el tiempo; {@code false} si lo interrumpieron
     */
    public static boolean dormir(long ms) {
        try {
            Thread.sleep(ms);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
