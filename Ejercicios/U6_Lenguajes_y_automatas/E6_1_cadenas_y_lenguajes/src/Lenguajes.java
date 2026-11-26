import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Operaciones sobre lenguajes. Un lenguaje es un conjunto de cadenas ({@code Set<String>}), y la cadena
 * vacía λ es {@code ""} (la constante {@link #LAMBDA}).
 *
 * <pre>
 *   V* = { λ, a, b, aa, ab, ... }        el universo del discurso: es infinito
 *   L1 L2 = { xy | x ∈ L1, y ∈ L2 }      concatenación
 *   L⁰ = { λ }    Lⁱ = L Lⁱ⁻¹            potencia
 *   L* = L⁰ ∪ L¹ ∪ L² ∪ ...              cierre (estrella de Kleene)
 *   L⁺ = L¹ ∪ L² ∪ ... = L L*            cierre positivo
 * </pre>
 *
 * <p>{@code V*} y {@code L*} son infinitos (salvo casos muy particulares), así que no entran en un
 * {@code Set}. Por eso las operaciones que los calculan reciben un {@code largoMax} y devuelven solo las
 * cadenas de largo menor o igual: es la parte finita que se puede mirar.
 *
 * <p>Las dos trampas de siempre, que el verificador prueba:
 *
 * <ul>
 *   <li>El lenguaje vacío {@code ∅} ({@code Set.of()}, cero cadenas) no es lo mismo que {@code {λ}}
 *       ({@code Set.of("")}, una cadena, que resulta ser la vacía).
 *   <li>{@code {λ}} es el neutro de la concatenación y {@code ∅} es el absorbente.
 * </ul>
 *
 * <p>Ninguna operación modifica los conjuntos que recibe: todas devuelven uno nuevo.
 */
public final class Lenguajes {

    /** La cadena vacía λ. */
    public static final String LAMBDA = "";

    private Lenguajes() {
    }

    /**
     * El universo del discurso {@code V*}, hasta {@code largoMax}: todas las cadenas que se pueden armar
     * con los símbolos del alfabeto, de largo {@code 0} a {@code largoMax}. Incluye a λ.
     *
     * <p>Con {@code V = {a, b}} y {@code largoMax = 2}: {@code {λ, a, b, aa, ab, ba, bb}}.
     *
     * <p>Una forma: arrancá con {@code {λ}} y, {@code largoMax} veces, agregá cada cadena de la última
     * tanda seguida de cada símbolo del alfabeto.
     *
     * @param alfabeto los símbolos
     * @param largoMax el largo máximo de las cadenas
     * @return las cadenas de {@code V*} de largo {@code <= largoMax}
     * @throws IllegalArgumentException si el alfabeto es {@code null} o está vacío (un alfabeto es un
     *                                  conjunto finito y <b>no vacío</b>), o si {@code largoMax < 0}
     */
    public static Set<String> universo(Set<Character> alfabeto, int largoMax) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Lenguajes.universo()");
    }

    /**
     * La concatenación {@code L1 L2 = { xy | x ∈ L1, y ∈ L2 }}: cada cadena de {@code l1} seguida de
     * cada cadena de {@code l2}.
     *
     * <p>Ojo: es un conjunto, así que dos pares distintos que dan la misma cadena cuentan una sola vez.
     * Con {@code {a, ab}} y {@code {b, λ}} queda {@code {ab, a, abb}}: tres cadenas, no cuatro.
     *
     * @param l1 el primer lenguaje
     * @param l2 el segundo lenguaje
     * @return {@code L1 L2}
     * @throws IllegalArgumentException si alguno es {@code null}
     */
    public static Set<String> concatenacion(Set<String> l1, Set<String> l2) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Lenguajes.concatenacion()");
    }

    /**
     * La potencia {@code Lⁱ}: el lenguaje concatenado consigo mismo {@code i} veces. Por definición
     * {@code L⁰ = {λ}}, para <b>cualquier</b> {@code L}, incluso el vacío.
     *
     * @param l el lenguaje
     * @param i el exponente
     * @return {@code Lⁱ}
     * @throws IllegalArgumentException si {@code l} es {@code null} o {@code i < 0}
     */
    public static Set<String> potencia(Set<String> l, int i) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Lenguajes.potencia()");
    }

    /**
     * El cierre {@code L* = L⁰ ∪ L¹ ∪ L² ∪ ...}, hasta {@code largoMax}: las cadenas que se arman
     * concatenando cero o más cadenas de {@code L}, de largo {@code <= largoMax}.
     *
     * <p>Cuidado con dos casos que cuelgan o dan mal a la primera:
     *
     * <ul>
     *   <li>{@code ∅* = {λ}}: aunque {@code L} no tenga cadenas, {@code L⁰} tiene una.
     *   <li>Si {@code λ ∈ L}, las potencias no se alargan ({@code λλλ = λ}). Si cortás "cuando la
     *       potencia supera el largo", no cortás nunca.
     * </ul>
     *
     * <p>Una forma que no tiene esos problemas es pensarlo como un punto fijo: arrancá con
     * {@code {λ}} y agregá {@code x + y} (con {@code x ∈ L}, {@code y} ya en el resultado y
     * {@code |xy| <= largoMax}) hasta que una vuelta entera no agregue nada nuevo.
     *
     * @param l        el lenguaje
     * @param largoMax el largo máximo
     * @return las cadenas de {@code L*} de largo {@code <= largoMax}
     * @throws IllegalArgumentException si {@code l} es {@code null} o {@code largoMax < 0}
     */
    public static Set<String> cierre(Set<String> l, int largoMax) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Lenguajes.cierre()");
    }

    /**
     * El cierre positivo {@code L⁺ = L¹ ∪ L² ∪ ...}, hasta {@code largoMax}: como el cierre, pero sin
     * {@code L⁰}. Se calcula como {@code L L*}: una cadena de {@code L} seguida de cualquier cosa de
     * {@code L*}.
     *
     * <p>Los apuntes lo escriben también como {@code L* − {λ}}, y es lo mismo cuando λ no está en
     * {@code L}. Si está, λ ya es una cadena de {@code L¹}, así que {@code L⁺} la tiene (es una de las
     * preguntas del README).
     *
     * @param l        el lenguaje
     * @param largoMax el largo máximo
     * @return las cadenas de {@code L⁺} de largo {@code <= largoMax}
     * @throws IllegalArgumentException si {@code l} es {@code null} o {@code largoMax < 0}
     */
    public static Set<String> cierrePositivo(Set<String> l, int largoMax) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Lenguajes.cierrePositivo()");
    }

    /**
     * "Los lenguajes se definen por las propiedades que cumplen sus cadenas": las cadenas de
     * {@code V*} (hasta {@code largoMax}) que cumplen la propiedad.
     *
     * @param alfabeto  los símbolos
     * @param largoMax  el largo máximo
     * @param propiedad lo que tiene que cumplir cada cadena para estar en el lenguaje
     * @return las cadenas de {@code V*} de largo {@code <= largoMax} que cumplen la propiedad
     * @throws IllegalArgumentException si algún argumento es inválido (como en {@link #universo})
     */
    public static Set<String> porPropiedad(Set<Character> alfabeto, int largoMax, Predicate<String> propiedad) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Lenguajes.porPropiedad()");
    }

    /**
     * ¿Se lee igual en los dos sentidos? λ y las cadenas de un símbolo son palíndromos.
     *
     * @param cadena la cadena
     * @return {@code true} si es un palíndromo
     */
    public static boolean esPalindromo(String cadena) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Lenguajes.esPalindromo()");
    }

    /**
     * Ya está hecho: muestra un lenguaje ordenado por largo y después alfabéticamente, con λ para la
     * cadena vacía y ∅ para el lenguaje vacío. Por ejemplo {@code {λ, a, b, ab}}.
     *
     * @param l el lenguaje
     * @return el texto
     */
    public static String mostrar(Set<String> l) {
        if (l.isEmpty()) {
            return "∅";
        }
        List<String> orden = new ArrayList<>(l);
        orden.sort(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()));
        List<String> textos = new ArrayList<>();
        for (String cadena : orden) {
            textos.add(cadena.isEmpty() ? "λ" : cadena);
        }
        return "{" + String.join(", ", textos) + "}";
    }

    private static void validarLenguaje(Set<String> l) {
        if (l == null) {
            throw new IllegalArgumentException("el lenguaje no puede ser null (el vacío es Set.of())");
        }
    }

    private static void validarLargo(int largoMax) {
        if (largoMax < 0) {
            throw new IllegalArgumentException("el largo máximo no puede ser negativo: " + largoMax);
        }
    }
}
