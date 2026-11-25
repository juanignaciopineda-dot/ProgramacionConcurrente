/**
 * Un molinete de entrada a un estadio. El estadio tiene varios molinetes, y por cada molinete pasan
 * personas atendidas por varios hilos a la vez.
 *
 * <p>Cada molinete lleva su propia cuenta ({@link #pasadas}) y además todos suman a un total del
 * estadio ({@link #totalEstadio}), que es {@code static}: hay <b>una sola</b> variable para todos los
 * molinetes.
 *
 * <pre>
 *   molinete 1 ── pasadas ──┐
 *   molinete 2 ── pasadas ──┼──► totalEstadio (static, compartido por todos)
 *   molinete 3 ── pasadas ──┘
 * </pre>
 *
 * <p><b>Regla de oro:</b> cada variable compartida tiene que estar protegida siempre por el
 * <b>mismo</b> monitor, tanto al escribirla como al leerla.
 *
 * <ul>
 *   <li>{@link #pasadas} es de este molinete: la protege el monitor de {@code this}.
 *   <li>{@link #totalEstadio} es de todos: hace falta un monitor que también sea único para todos,
 *       como el de la clase ({@code Molinete.class}).
 * </ul>
 *
 * <p>Herramientas permitidas: solo {@code synchronized} (en métodos o en bloques). Nada de
 * {@code java.util.concurrent} ni clases atómicas.
 */
public class Molinete {

    /** Total de pasadas del estadio, sumando todos los molinetes. */
    private static int totalEstadio = 0;

    /** Pasadas por este molinete. */
    private int pasadas = 0;

    /**
     * Registra que pasó una persona por este molinete: incrementa {@link #pasadas} <b>y</b>
     * {@link #totalEstadio}, los dos de forma segura.
     *
     * <p>Un camino posible: un bloque {@code synchronized (this)} para {@code pasadas} y otro
     * {@code synchronized (Molinete.class)} para {@code totalEstadio}.
     *
     * <p><b>La trampa:</b> marcar el método como {@code synchronized} toma el monitor de
     * {@code this}. Eso protege {@code pasadas}, pero dos molinetes distintos tienen dos
     * {@code this} distintos, así que pueden tocar {@code totalEstadio} a la vez. Probalo: poné solo
     * {@code synchronized} en el método y mirá qué caso del verificador falla.
     */
    public void registrarPasada() {
        synchronized (this) {
            pasadas++;
        }
        synchronized (Molinete.class) {
            totalEstadio++;
        }
    }

    /**
     * Devuelve las pasadas de este molinete.
     *
     * <p>Leé bajo el <b>mismo</b> monitor que usás para escribir. Sin eso, Java no garantiza que este
     * hilo vea el último valor que escribió otro (problema de <i>visibilidad</i>, no solo de
     * atomicidad).
     *
     * @return las pasadas de este molinete
     */
    public int getPasadas() {
        synchronized (this) {
            return pasadas;
        }
    }

    /**
     * Devuelve el total del estadio, leído bajo el mismo monitor con el que se escribe.
     *
     * <p>Fijate que este método es {@code static}: un {@code static synchronized} toma el monitor de
     * la clase, que es justo el que protege {@code totalEstadio}.
     *
     * @return el total de pasadas de todos los molinetes
     */
    public static int getTotalEstadio() {
        synchronized (Molinete.class) {
            return totalEstadio;
        }
    }

    /** Pone el total del estadio en cero (bajo el mismo monitor). Lo usa el verificador entre casos. */
    public static void reiniciarTotal() {
        synchronized (Molinete.class) {
            totalEstadio = 0;
        }
    }
}
