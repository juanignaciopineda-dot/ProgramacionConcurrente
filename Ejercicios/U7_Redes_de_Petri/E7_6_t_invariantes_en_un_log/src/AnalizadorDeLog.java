import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Analiza el log de una ejecución: la secuencia de transiciones que disparó el monitor, como
 * {@code "T0T1T3T0T4..."}. Hay dos preguntas distintas, y cada una tiene su herramienta:
 *
 * <ol>
 *   <li><b>¿Era posible?</b> Se reproduce el log sobre la red, disparo por disparo
 *       ({@link #primerDisparoImposible}). Si alguno no estaba sensibilizado, el monitor dejó pasar algo
 *       que la red no permite.
 *   <li><b>¿Se cumplieron los T-invariantes?</b> Se borran del log los invariantes completos con una
 *       expresión regular ({@link #quitarInvariantes}), y se mira qué sobra. Por la ecuación de estado, si
 *       el log se descompone en invariantes completos, la red volvió al marcado inicial.
 * </ol>
 *
 * <p>Los invariantes vienen intercalados con disparos de otros hilos, así que la expresión los busca con
 * <b>huecos perezosos</b> entre sus transiciones, y los huecos se conservan:
 *
 * <pre>
 *   T0(.*?)T1(.*?)T2          sobre   T0 T3 T1 T2      →   queda T3
 *        └ hueco ┘                      └ hueco: se conserva
 * </pre>
 *
 * <p>Un mismo T-invariante se puede ver en el log en varios <b>órdenes</b> (el consumidor puede empezar
 * antes de que el productor termine), y la red puede tener varios invariantes. Todos los órdenes van en
 * <b>una sola</b> expresión, con el prefijo común factorizado ({@link #expresion}).
 */
public final class AnalizadorDeLog {

    /** El hueco entre dos transiciones de un invariante: perezoso, y captura lo que hay para conservarlo. */
    public static final String HUECO = "(.*?)";

    /** Un disparo del log: una T y el número de la transición, que puede tener varios dígitos. */
    private static final Pattern DISPARO = Pattern.compile("T(\\d+)");

    /**
     * Lo que queda después de borrar los invariantes.
     *
     * @param invariantes cuántos invariantes completos se borraron
     * @param resto       lo que sobró del log ({@code ""} si no sobró nada)
     */
    public record Resultado(int invariantes, String resto) {
    }

    private AnalizadorDeLog() {
    }

    /**
     * Ya está hecho: la transición {@code t} en una expresión, con {@code (?!\d)} detrás para que
     * {@code T1} no coincida con el comienzo de {@code T10}.
     *
     * @param t el número de la transición
     * @return por ejemplo {@code "T1(?!\d)"}
     */
    public static String transicion(int t) {
        return "T" + t + "(?!\\d)";
    }

    /**
     * Los disparos del log, en orden: {@code "T0T12T3"} da {@code [0, 12, 3]}.
     *
     * <p>Recorré el log con {@code DISPARO.matcher(log)} y {@code find()}, y sacá el número de cada disparo
     * con {@code group(1)}. El log tiene que ser <b>solo</b> disparos pegados: si entre uno y el siguiente
     * hay otra cosa (o la hay antes del primero o después del último), lanzá
     * {@code IllegalArgumentException}. Para darte cuenta, compará el {@code start()} de cada coincidencia
     * con el {@code end()} de la anterior.
     *
     * @param log el log
     * @return los números de las transiciones disparadas (vacía si el log es {@code ""})
     * @throws IllegalArgumentException si el log tiene algo que no es un disparo
     */
    public static List<Integer> disparos(String log) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("AnalizadorDeLog.disparos()");
    }

    /**
     * Reproduce el log sobre la red, desde su marcado actual: dispara cada transición del log, en orden.
     *
     * @param red la red, en su marcado inicial (se dispara sobre ella)
     * @param log el log
     * @return la posición (desde 0, en la lista de {@link #disparos}) del primer disparo que no estaba
     *         sensibilizado, o {@code -1} si todos lo estaban
     * @throws IllegalArgumentException si el log tiene algo que no es un disparo, o una transición que la
     *                                  red no tiene
     */
    public static int primerDisparoImposible(RedDePetri red, String log) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("AnalizadorDeLog.primerDisparoImposible()");
    }

    /**
     * Arma <b>una sola</b> expresión que reconoce cualquiera de los órdenes, con huecos perezosos entre sus
     * transiciones y el <b>prefijo común factorizado</b>. Es la expresión de los apuntes:
     *
     * <pre>
     *   órdenes:  T0 T1 T2  y  T0 T2 T3
     *   árbol:    T0 ─┬─ T1 ── T2
     *                 └─ T2 ── T3
     *   expresión: T0(.*?)(?:T1(.*?)T2|T2(.*?)T3)        (cada Tn con su (?!\d) detrás)
     * </pre>
     *
     * <p>Primero metés los órdenes en un árbol de prefijos: cada nodo tiene un hijo por cada transición que
     * puede venir después (un {@code LinkedHashMap<Integer, Nodo>}, para respetar el orden en que llegan).
     * Después lo recorrés con una función recursiva que arma las alternativas que salen de un nodo:
     *
     * <pre>
     *   alternativas(nodo):
     *       para cada hijo (t, sub):
     *           parte ← transicion(t)
     *           si sub tiene hijos: parte ← parte + HUECO + alternativas(sub)
     *       si hay una sola parte: esa
     *       si hay varias: "(?:" + las partes separadas por "|" + ")"
     * </pre>
     *
     * <p>La expresión es {@code alternativas(raíz)}. Que el prefijo quede afuera de la alternativa es lo
     * que importa: así, para cada largo del hueco perezoso, el motor prueba <b>todas</b> las alternativas
     * antes de alargarlo. Si cada orden fuera una alternativa entera,
     * {@code (?:T0(.*?)T1(.*?)T2|T0(.*?)T2(.*?)T3)}, el motor agotaría la primera, con todos los largos de
     * hueco, antes de probar la segunda (el verificador lo muestra).
     *
     * @param ordenes las secuencias de transiciones de cada invariante, en cada orden en que puede aparecer
     *                en el log; un orden repetido no es un error
     * @return la expresión regular
     * @throws IllegalArgumentException si no hay órdenes, si alguno está vacío, o si uno es el comienzo de
     *                                  otro (como {@code T0 T1} y {@code T0 T1 T2}: no se sabría dónde
     *                                  termina el invariante)
     */
    public static String expresion(List<int[]> ordenes) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("AnalizadorDeLog.expresion()");
    }


    /**
     * Borra del log todos los invariantes completos, conservando lo que estaba en sus huecos, y repite
     * hasta que no quede ninguno. Es el {@code quitar_invariantes} de los apuntes, que usa {@code re.subn}:
     *
     * <pre>
     *   total ← 0
     *   repetir:
     *       una pasada: reemplazar cada coincidencia por la concatenación de sus grupos que no son null
     *                   (los huecos; los de las alternativas que no se usaron son null), contándolas
     *       si en la pasada no hubo ninguna: devolver (total, log)
     *       total ← total + las de la pasada
     * </pre>
     *
     * <p>Para la pasada sirve un bucle con {@code find()}, {@code appendReplacement} y {@code appendTail}
     * (o {@code replaceAll} con una función). Ojo: lo que se pasa como reemplazo se interpreta, y un
     * {@code $} o una {@code \} tienen significado especial. Pasalo por {@code Matcher.quoteReplacement}.
     *
     * @param patron la expresión de los invariantes, cuyos grupos son solo los huecos
     * @param log    el log
     * @return cuántos invariantes se borraron y qué sobró
     */
    public static Resultado quitarInvariantes(Pattern patron, String log) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("AnalizadorDeLog.quitarInvariantes()");
    }

    /**
     * Ya está hecho: arma la expresión de los órdenes y borra los invariantes del log.
     *
     * @param ordenes los órdenes de los invariantes, como en {@link #expresion}
     * @param log     el log
     * @return cuántos invariantes se borraron y qué sobró
     */
    public static Resultado analizar(List<int[]> ordenes, String log) {
        return quitarInvariantes(Pattern.compile(expresion(ordenes)), log);
    }
}
