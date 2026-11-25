/**
 * Suma un tramo {@code [desde, hasta)} de un arreglo: incluye la posición {@code desde} y excluye la
 * posición {@code hasta}.
 *
 * <p>Muchos sumadores leen el <b>mismo</b> arreglo a la vez, pero cada uno escribe solamente en su
 * propio campo {@link #resultado}. Como ninguna variable es escrita por dos hilos, este ejercicio no
 * necesita sincronización: alcanza con que quien lee el resultado haga antes un {@code join()} sobre
 * el hilo que lo calculó.
 */
public class SumadorDeTramo implements Runnable {

    /** Arreglo compartido. Ningún hilo lo modifica. */
    private final int[] datos;

    /** Primera posición del tramo (inclusive). */
    private final int desde;

    /** Posición donde termina el tramo (exclusive). */
    private final int hasta;

    /** Suma del tramo. Vale 0 hasta que {@link #run()} termina. */
    private long resultado;

    /**
     * Crea un sumador para el tramo {@code [desde, hasta)}. Este constructor ya está hecho.
     *
     * @param datos arreglo a sumar
     * @param desde primera posición, inclusive
     * @param hasta última posición, exclusive
     * @throws IllegalArgumentException si no se cumple {@code 0 <= desde <= hasta <= datos.length}
     */
    public SumadorDeTramo(int[] datos, int desde, int hasta) {
        if (desde < 0 || desde > hasta || hasta > datos.length) {
            throw new IllegalArgumentException("tramo inválido [" + desde + ", " + hasta + ")");
        }
        this.datos = datos;
        this.desde = desde;
        this.hasta = hasta;
    }

    /**
     * Suma {@code datos[desde] + datos[desde + 1] + ... + datos[hasta - 1]} y deja el total en
     * {@link #resultado}.
     *
     * <p>Acumulá en una variable <b>local</b> de tipo {@code long} y asignala al campo una sola vez,
     * al final. Escribir el campo en cada vuelta también funcionaría, pero la variable local es más
     * rápida y deja claro que nadie más la toca: las variables locales viven en la pila de cada hilo y
     * nunca se comparten.
     *
     * <p>Un tramo vacío ({@code desde == hasta}) suma 0.
     */
    @Override
    public void run() {
        long suma = 0;
        for (int i = desde; i < hasta; i++) {
            suma += datos[i];
        }
        resultado = suma;
    }

    /**
     * Devuelve la suma del tramo. Leelo solo después de hacer {@code join()} sobre el hilo que corrió
     * este sumador.
     *
     * @return la suma del tramo
     */
    public long getResultado() {
        return resultado;
    }

    /**
     * Devuelve dónde empieza el tramo.
     *
     * @return la primera posición, inclusive
     */
    public int getDesde() {
        return desde;
    }

    /**
     * Devuelve dónde termina el tramo.
     *
     * @return la última posición, exclusive
     */
    public int getHasta() {
        return hasta;
    }
}
