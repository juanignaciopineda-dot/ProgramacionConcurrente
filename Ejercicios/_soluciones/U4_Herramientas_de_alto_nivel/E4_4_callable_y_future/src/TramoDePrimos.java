import java.util.concurrent.Callable;

/**
 * Tarea que cuenta los números primos de un tramo {@code [desde, hasta)} y <b>devuelve</b> el
 * resultado.
 *
 * <p>Es un {@link Callable}, no un {@code Runnable}. Las dos diferencias que importan:
 *
 * <ul>
 *   <li>{@code call()} devuelve un valor. Con un {@code Runnable} había que guardar el resultado en un
 *       campo y leerlo después del {@code join()} (ejercicio 1.2). Acá lo devuelve, y quien la envió lo
 *       recibe con {@code Future.get()}.
 *   <li>{@code call()} puede lanzar excepciones <b>checked</b>. Si lanza algo, {@code get()} lanza
 *       {@code ExecutionException} con esa excepción adentro ({@code getCause()}).
 * </ul>
 */
public class TramoDePrimos implements Callable<Long> {

    private final long desde;
    private final long hasta;

    /**
     * Crea la tarea. Ya está hecho.
     *
     * @param desde primer número del tramo (incluido)
     * @param hasta último número del tramo (excluido)
     * @throws IllegalArgumentException si {@code desde > hasta}
     */
    public TramoDePrimos(long desde, long hasta) {
        if (desde > hasta) {
            throw new IllegalArgumentException("tramo inválido: [" + desde + ", " + hasta + ")");
        }
        this.desde = desde;
        this.hasta = hasta;
    }

    /**
     * Cuenta los primos del tramo con {@link #esPrimo(long)}.
     *
     * <p>Antes de revisar cada número, fijate si al hilo lo interrumpieron
     * ({@code Thread.currentThread().isInterrupted()}). Si es así, lanzá
     * {@code new InterruptedException(...)}. Es lo que hace que {@code Future.cancel(true)} pueda
     * frenar esta tarea: {@code cancel(true)} interrumpe al hilo que la está ejecutando, y una tarea
     * que nunca mira la marca sigue calculando hasta el final.
     *
     * @return cuántos primos hay en {@code [desde, hasta)}
     * @throws InterruptedException si interrumpen al hilo mientras cuenta
     */
    @Override
    public Long call() throws InterruptedException {
        long cuenta = 0;
        for (long n = desde; n < hasta; n++) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("tramo interrumpido en " + n);
            }
            if (esPrimo(n)) {
                cuenta++;
            }
        }
        return cuenta;
    }

    /**
     * Ya está hecho.
     *
     * @param n un número
     * @return {@code true} si {@code n} es primo
     */
    public static boolean esPrimo(long n) {
        if (n < 2) {
            return false;
        }
        for (long d = 2; d * d <= n; d++) {
            if (n % d == 0) {
                return false;
            }
        }
        return true;
    }
}
