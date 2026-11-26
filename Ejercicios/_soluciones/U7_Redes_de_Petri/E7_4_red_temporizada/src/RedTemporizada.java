/**
 * Una red de Petri con tiempo (RdPT): cada transición tiene un intervalo estático {@code [α, β]}, y
 * se usa la semántica de <b>tiempo de sensibilización</b>:
 *
 * <pre>
 *   si t empieza a estar sensibilizada en el instante τ y sigue sensibilizada,
 *   se puede disparar en cualquier instante de [τ + α, τ + β]
 * </pre>
 *
 * <p>Con {@code [0, ∞)} la transición se comporta como en una red sin tiempo. Para β = ∞ se usa
 * {@link #INFINITO}.
 *
 * <p>El estado ya no es solo el marcado: hace falta saber desde cuándo está sensibilizada cada
 * transición. Eso se guarda en {@code desde[t]} ({@link #NO_SENSIBILIZADA} si no lo está), y lo
 * importante es <b>cuándo arranca de nuevo</b> cada cronómetro. Es la regla del TP final:
 *
 * <ul>
 *   <li>Al crear la red, arrancan las que ya están sensibilizadas en M0.
 *   <li>Después de cada disparo, arrancan de cero las que <b>quedaron sensibilizadas y antes no lo
 *       estaban</b>, y también la que se disparó, si sigue sensibilizada (es una sensibilización
 *       nueva, con otras fichas).
 *   <li>Las que siguen sensibilizadas sin haberse disparado <b>conservan</b> su instante.
 *   <li>Las que dejaron de estar sensibilizadas pasan a {@link #NO_SENSIBILIZADA}.
 * </ul>
 */
public class RedTemporizada {

    /** Valor de β para "sin límite superior". */
    public static final long INFINITO = Long.MAX_VALUE;

    /** Valor de {@code desde[t]} para una transición que no está sensibilizada. */
    public static final long NO_SENSIBILIZADA = -1;

    private final RedDePetri red;
    private final long[] alfa;
    private final long[] beta;
    private final Reloj reloj;

    /** Desde qué instante está sensibilizada cada transición, o {@link #NO_SENSIBILIZADA}. */
    private final long[] desde;

    /**
     * Crea la red con tiempo.
     *
     * <ol>
     *   <li>Validá: {@code alfa} y {@code beta} con una componente por transición, y
     *       {@code 0 <= alfa[t] <= beta[t]} para todas. Si no, {@code IllegalArgumentException}.
     *   <li>Guardá copias de los arreglos.
     *   <li>Arrancá los cronómetros: {@code desde[t] = reloj.ahoraMs()} para las sensibilizadas en el
     *       marcado inicial, y {@link #NO_SENSIBILIZADA} para las demás.
     * </ol>
     *
     * @param red   la red (sin tiempo) que lleva el marcado
     * @param alfa  el α de cada transición, en ms
     * @param beta  el β de cada transición, en ms ({@link #INFINITO} si no tiene límite)
     * @param reloj de dónde sacar la hora
     * @throws IllegalArgumentException si los intervalos no son válidos
     */
    public RedTemporizada(RedDePetri red, long[] alfa, long[] beta, Reloj reloj) {
        int n = red.transiciones();
        if (alfa == null || beta == null || alfa.length != n || beta.length != n) {
            throw new IllegalArgumentException("hace falta un α y un β por transición");
        }
        for (int t = 0; t < n; t++) {
            if (alfa[t] < 0 || alfa[t] > beta[t]) {
                throw new IllegalArgumentException("intervalo inválido para T" + t + ": [" + alfa[t] + ", " + beta[t] + "]");
            }
        }
        this.red = red;
        this.alfa = alfa.clone();
        this.beta = beta.clone();
        this.reloj = reloj;
        this.desde = new long[n];
        long ahora = reloj.ahoraMs();
        boolean[] sensibilizadas = red.sensibilizadas();
        for (int t = 0; t < n; t++) {
            desde[t] = sensibilizadas[t] ? ahora : NO_SENSIBILIZADA;
        }
    }

    /**
     * Cuánto hace que {@code t} está sensibilizada: {@code ahora - desde[t]}, o {@code -1} si no lo
     * está.
     *
     * @param t una transición
     * @return los ms desde que se sensibilizó, o {@code -1}
     */
    public long tiempoSensibilizada(int t) {
        if (desde[t] == NO_SENSIBILIZADA) {
            return -1;
        }
        return reloj.ahoraMs() - desde[t];
    }

    /**
     * ¿Se puede disparar {@code t} ahora? Tiene que estar sensibilizada por el marcado y estar dentro
     * de su ventana: {@code alfa[t] <= tiempoSensibilizada(t) <= beta[t]}.
     *
     * @param t una transición
     * @return {@code true} si se puede disparar en este instante
     */
    public boolean puedeDisparar(int t) {
        long tiempo = tiempoSensibilizada(t);
        return tiempo >= 0 && tiempo >= alfa[t] && tiempo <= beta[t];
    }

    /**
     * Cuánto falta para que {@code t} se pueda disparar. Es lo que en el monitor con tiempo del TP
     * final se duerme el hilo que llegó antes de α.
     *
     * <ul>
     *   <li>Si no está sensibilizada, o ya se pasó de β: {@code -1} (esperar no sirve).
     *   <li>Si no: {@code max(0, alfa[t] - tiempoSensibilizada(t))}.
     * </ul>
     *
     * @param t una transición
     * @return los ms que faltan, 0 si ya se puede, o {@code -1}
     */
    public long faltaParaDisparar(int t) {
        long tiempo = tiempoSensibilizada(t);
        if (tiempo < 0 || tiempo > beta[t]) {
            return -1;
        }
        return Math.max(0, alfa[t] - tiempo);
    }

    /**
     * Dispara {@code t} si está en su ventana, y actualiza los cronómetros con las reglas de la clase.
     *
     * <pre>
     *   si !puedeDisparar(t): IllegalStateException (sin tocar nada)
     *   antes = red.sensibilizadas()
     *   red.disparar(t)
     *   despues = red.sensibilizadas()
     *   para cada u:
     *       si !despues[u]:                      desde[u] = NO_SENSIBILIZADA
     *       si despues[u] y (!antes[u] o u == t): desde[u] = ahora
     *       (si no, queda como estaba)
     * </pre>
     *
     * @param t la transición a disparar
     * @throws IllegalStateException si {@code t} no está sensibilizada o está fuera de su ventana
     */
    public void disparar(int t) {
        if (!puedeDisparar(t)) {
            throw new IllegalStateException("T" + t + " no se puede disparar ahora (lleva " + tiempoSensibilizada(t)
                    + " ms sensibilizada, ventana [" + alfa[t] + ", " + beta[t] + "])");
        }
        boolean[] antes = red.sensibilizadas();
        red.disparar(t);
        boolean[] despues = red.sensibilizadas();
        long ahora = reloj.ahoraMs();
        for (int u = 0; u < despues.length; u++) {
            if (!despues[u]) {
                desde[u] = NO_SENSIBILIZADA;
            } else if (!antes[u] || u == t) {
                desde[u] = ahora;
            }
        }
    }

    /** @return una copia del marcado. Ya está hecho. */
    public int[] getMarcado() {
        return red.getMarcado();
    }
}
