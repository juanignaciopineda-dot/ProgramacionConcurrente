import java.util.Set;

/**
 * Autómatas de pila para diseñar. El primero es el de los apuntes y ya está hecho; los otros dos los
 * diseñás vos, deterministas (si no, {@code agregarTransicion} te lo rechaza).
 */
public final class AutomatasDePila {

    private static final char L = AutomataDePila.LAMBDA;

    private AutomatasDePila() {
    }

    /**
     * Ya está hecho: el PDA determinista de los apuntes para {@code { aⁿbⁿ | n > 0 }}. Por cada {@code a}
     * apila una {@code a}, por cada {@code b} desapila una, y si al final queda solo la {@code Z}, acepta.
     *
     * <pre>
     *   δ = { (q0, a, Z, q1, aZ), (q1, a, a, q1, aa), (q1, b, a, q2, λ), (q2, b, a, q2, λ), (q2, λ, Z, q3, Z) }
     * </pre>
     *
     * @return el autómata
     */
    public static AutomataDePila anbn() {
        AutomataDePila m = new AutomataDePila("q0", 'Z', Set.of("q3"));
        m.agregarTransicion("q0", 'a', 'Z', "q1", "aZ");
        m.agregarTransicion("q1", 'a', 'a', "q1", "aa");
        m.agregarTransicion("q1", 'b', 'a', "q2", "");
        m.agregarTransicion("q2", 'b', 'a', "q2", "");
        m.agregarTransicion("q2", L, 'Z', "q3", "Z");
        return m;
    }

    /**
     * {@code { aⁿb²ⁿ | n > 0 }}: por cada {@code a}, dos {@code b}. Es el ejercicio 6.b de los apuntes:
     * modificá el de {@link #anbn()}. Se puede cambiar lo que se apila por cada {@code a}, o lo que se
     * desapila por cada {@code b}; elegí una.
     *
     * @return el autómata
     */
    public static AutomataDePila anb2n() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("AutomatasDePila.anb2n()");
    }

    /**
     * Las trazas de un hilo que usa bien un {@code ReentrantLock}, sobre {@code {l, u}}: {@code l} es un
     * {@code lock()} y {@code u} un {@code unlock()}. Una traza está bien si:
     *
     * <ul>
     *   <li>nunca hay un {@code unlock()} sin un {@code lock()} abierto antes (sería un
     *       {@code IllegalMonitorStateException}), y
     *   <li>al terminar no queda ningún {@code lock()} sin su {@code unlock()} (el hilo se quedaría con el
     *       lock para siempre).
     * </ul>
     *
     * <p>Como el lock es reentrante, se puede tomar varias veces seguidas: {@code llulu} está mal (sobra un
     * {@code l}), {@code lluu} y {@code lulu} están bien, y λ también (no usó el lock). La pila cuenta lo
     * mismo que el {@code getHoldCount()} del lock.
     *
     * @return el autómata
     */
    public static AutomataDePila lockBienUsado() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("AutomatasDePila.lockBienUsado()");
    }
}
