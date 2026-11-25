/**
 * Busca números primos desde 2 en adelante, sin parar, hasta que lo interrumpen.
 *
 * <p>A diferencia de {@link Latido}, este hilo <b>nunca duerme</b>: está todo el tiempo calculando.
 * Por eso la interrupción no le llega como excepción (nadie la lanza si el hilo no está en
 * {@code sleep}, {@code join} o {@code wait}). Tiene que <b>preguntar</b> si lo interrumpieron.
 */
public class BuscadorDePrimos implements Runnable {

    /** Último primo encontrado, o 0 si todavía no encontró ninguno. */
    private long ultimoPrimo;

    /** Cantidad de números revisados. */
    private long revisados;

    /**
     * Dice si {@code n} es primo, probando divisores desde 2 hasta la raíz cuadrada de {@code n}
     * (la condición del bucle puede ser {@code d * d <= n}).
     *
     * <p>Los números menores que 2 no son primos.
     *
     * @param n el número a probar
     * @return {@code true} si {@code n} es primo
     */
    public static boolean esPrimo(long n) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("BuscadorDePrimos.esPrimo()");
    }

    /**
     * Recorre {@code n = 2, 3, 4, ...}. Por cada número: lo cuenta en {@link #revisados} y, si es
     * primo, lo guarda en {@link #ultimoPrimo}.
     *
     * <p><b>Antes</b> de revisar cada número, consultá {@code Thread.currentThread().isInterrupted()}.
     * Si da {@code true}, terminá el método. En particular, si el hilo ya estaba interrumpido antes
     * de empezar, no tiene que revisar ningún número.
     *
     * <p>Usá {@link Thread#isInterrupted()} y no {@link Thread#interrupted()}: el segundo
     * es {@code static}, pregunta por el hilo actual y además <i>borra</i> la marca.
     */
    @Override
    public void run() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("BuscadorDePrimos.run()");
    }

    /**
     * Devuelve el último primo encontrado. Leelo después de {@code join()}.
     *
     * @return el último primo, o 0 si no encontró ninguno
     */
    public long getUltimoPrimo() {
        return ultimoPrimo;
    }

    /**
     * Devuelve cuántos números revisó. Leelo después de {@code join()}.
     *
     * @return la cantidad de números revisados
     */
    public long getRevisados() {
        return revisados;
    }
}
