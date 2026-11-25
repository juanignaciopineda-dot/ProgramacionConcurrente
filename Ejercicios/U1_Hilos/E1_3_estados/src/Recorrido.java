/**
 * Arma un hilo que pasa, en orden, por los seis estados de {@link Thread.State}, y se queda unos
 * {@value #FASE_MS} ms en cada uno para que un observador externo (el verificador) lo pueda ver:
 *
 * <pre>
 *   NEW ──start()──► RUNNABLE ──sleep()──► TIMED_WAITING ──join()──► WAITING
 *       ──synchronized ocupado──► BLOCKED ──fin de run()──► TERMINATED
 * </pre>
 *
 * <p>Para la fase {@code BLOCKED} hace falta que otro hilo tenga tomado un candado
 * ({@code synchronized}) justo cuando el hilo observado quiere entrar. {@code synchronized} se ve a
 * fondo en la Unidad 2: acá solo se usa como obstáculo.
 *
 * <p>Línea de tiempo esperada (F = {@value #FASE_MS} ms), con el bloqueador arrancado en t = 0:
 *
 * <pre>
 *   t:          0        F        2F        3F                          6F
 *   observado:  |RUNNABLE|TIMED_W.|WAITING  |BLOCKED                    |TERMINATED
 *   bloqueador: |=================== tiene el candado ===================|
 * </pre>
 *
 * <p>El bloqueador retiene el candado tres fases más de lo que tardan las anteriores: así, aunque
 * alguna fase se demore un poco, el candado sigue tomado cuando el observado llega.
 */
public class Recorrido {

    /** Duración de cada fase, en milisegundos. */
    public static final long FASE_MS = 200;

    /** Candado que el hilo observado va a encontrar ocupado en la fase {@code BLOCKED}. */
    private final Object candado = new Object();

    /**
     * Crea el hilo observado, con nombre {@code "observado"}, <b>sin arrancarlo</b> (así el
     * observador lo ve en {@code NEW}).
     *
     * <p>Su tarea (escribila como lambda) hace, en orden:
     *
     * <ol>
     *   <li>{@link #arrancarBloqueador()}: así, cuando llegue a la fase 5, el candado ya va a estar
     *       ocupado.
     *   <li><b>RUNNABLE</b>: {@link #calcularDurante(long)} durante {@link #FASE_MS}.
     *   <li><b>TIMED_WAITING</b>: {@code Thread.sleep(FASE_MS)}.
     *   <li><b>WAITING</b>: creá y arrancá un hilo {@code "dormilon"} que solo duerma
     *       {@link #FASE_MS}, y esperalo con {@code join()} <b>sin</b> tiempo máximo (con
     *       {@code join(ms)} el estado sería {@code TIMED_WAITING}).
     *   <li><b>BLOCKED</b>: {@code synchronized (candado) { }}. El bloqueador lo tiene tomado hasta
     *       {@code t = 6F}, así que el hilo se queda bloqueado en la entrada.
     *   <li>Terminar: al salir de la lambda el hilo pasa a <b>TERMINATED</b>.
     * </ol>
     *
     * <p>{@code sleep} y {@code join} lanzan {@link InterruptedException}: capturala, restaurá la
     * interrupción con {@code Thread.currentThread().interrupt()} y terminá.
     *
     * @return el hilo observado, en estado {@code NEW}
     */
    public Thread crearHiloObservado() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Recorrido.crearHiloObservado()");
    }

    /**
     * Arranca un hilo {@code "bloqueador"} que toma {@link #candado} y lo retiene durante
     * {@code 6 * FASE_MS}, es decir:
     *
     * <pre>synchronized (candado) { Thread.sleep(6 * FASE_MS); }</pre>
     *
     * <p>Mientras duerme <b>adentro</b> del {@code synchronized}, el candado sigue tomado: dormir no
     * libera los candados (a diferencia de {@code wait()}, que se ve en la Unidad 2).
     *
     * @return el hilo bloqueador, ya arrancado
     */
    public Thread arrancarBloqueador() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Recorrido.arrancarBloqueador()");
    }

    /**
     * Mantiene la CPU ocupada durante {@code ms} milisegundos, para que el hilo se vea
     * {@code RUNNABLE}. Por ejemplo, un bucle que haga alguna cuenta hasta que
     * {@link System#nanoTime()} pase de un límite calculado al principio.
     *
     * <p><b>Ojo:</b> esto es <i>espera activa</i> (gastar CPU esperando) y en un programa real, y en
     * los TPs, está prohibida. Acá la usamos a propósito, solo para poder ver el estado.
     *
     * @param ms cuánto tiempo calcular
     * @return el resultado de la cuenta (cualquier cosa; sirve para que el compilador no la elimine)
     */
    public static long calcularDurante(long ms) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Recorrido.calcularDurante()");
    }
}
