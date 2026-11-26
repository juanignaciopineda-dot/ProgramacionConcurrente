import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * De Mealy a Moore. Las dos máquinas son igual de expresivas, pero Moore tiende a tener más estados: como
 * la salida va en el estado, un estado de Mealy al que se llega con salidas distintas se tiene que partir
 * en uno por cada salida.
 */
public final class Conversion {

    private Conversion() {
    }

    /**
     * Construye la máquina de Moore equivalente a una de Mealy. Cada estado de Moore es un par
     * <b>(estado de Mealy, última salida)</b>, con el nombre {@code estado + "/" + salida} (por ejemplo
     * {@code "b/1"}), y emite esa salida.
     *
     * <pre>
     *   inicio ← inicial/salidaInicial, que emite salidaInicial
     *   recorrido en anchura desde inicio, solo por los pares a los que se llega:
     *       para el par q/o y cada símbolo c del alfabeto de la máquina de Mealy:
     *           si hay arco q --c / s--&gt; h:
     *               el par h/s emite s
     *               arco q/o --c--&gt; h/s
     *               si h/s es nuevo, a la cola
     * </pre>
     *
     * <p>Para cualquier entrada {@code w} tiene que valer
     * {@code moore.traducir(w).equals(salidaInicial + mealy.traducir(w))}: la misma salida, un paso después.
     *
     * @param mealy         la máquina de Mealy
     * @param salidaInicial lo que emite el estado inicial de Moore, antes de leer nada
     * @return la máquina de Moore equivalente, con solo los pares accesibles
     */
    public static MaquinaMoore mealyAMoore(MaquinaMealy mealy, char salidaInicial) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Conversion.mealyAMoore()");
    }
}
