import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Las expresiones regulares de la teoría, escritas en Java con {@code java.util.regex}. Describen los mismos
 * lenguajes que en JFLAP, pero con otra notación:
 *
 * <pre>
 *   JFLAP / teoría     Java
 *   a + b              a|b          la unión          (en Java, + es "una o más veces")
 *   !                  (?:)         la cadena vacía λ (un grupo que no captura y no lee nada)
 *   a*                 a*           el cierre
 *   aa*                a+           el cierre positivo
 *                      [ab]         uno de esos caracteres, en una posición
 *                      .            cualquier carácter
 * </pre>
 *
 * <p>Dos formas de usar una expresión, que no hay que confundir:
 *
 * <ul>
 *   <li>{@code matcher(s).matches()}: ¿la cadena <b>entera</b> pertenece al lenguaje? Es la pregunta de la
 *       teoría.
 *   <li>{@code matcher(s).find()}: ¿hay un <b>pedazo</b> de la cadena que pertenezca? Es buscar un patrón
 *       en un texto, como en el log de una ejecución.
 * </ul>
 *
 * <p>Las expresiones de las constantes se escriben en notación de Java y se prueban con {@code matches()}.
 */
public final class ExpresionesRegulares {

    private ExpresionesRegulares() {
    }

    /**
     * Actividad a) de los apuntes: las cadenas sobre {@code {a, b}} que empiezan y terminan con la misma
     * letra. Una cadena de una sola letra cuenta (empieza y termina con ella); λ no (no empieza con nada).
     */
    public static final String EMPIEZA_Y_TERMINA_IGUAL =
            "a|b|a[ab]*a|b[ab]*b";

    /** Actividad b): las cadenas sobre {@code {0, 1}} que contienen al menos un {@code 1}. */
    public static final String AL_MENOS_UN_1 =
            "[01]*1[01]*";

    /**
     * Actividad c): las cadenas sobre {@code {a, b}} con una cantidad par de {@code b} (cero incluido). Es el
     * lenguaje del autómata {@code parDeB} del 6.3.
     */
    public static final String PAR_DE_B =
            "a*(ba*ba*)*";

    /**
     * Ejercicio 1.b de los apuntes: {@code L3 = { x c^(3m) | x ∈ {a, b}*, la cantidad de b en x es par,
     * m ≥ 0 }}, el lenguaje del autómata {@code M3D}.
     */
    public static final String L3 =
            "(a|ba*b)*(ccc)*";

    /**
     * Para buscar con {@link #buscarTodas}: cada etiqueta de un texto, como {@code <a>}, {@code <p>} o
     * {@code </p>}, por separado. En {@code "<a>b<c>"} tiene que encontrar {@code <a>} y {@code <c>}, no
     * {@code <a>b<c>} entero. (Pista: los apuntes explican la diferencia entre codicioso y perezoso con este
     * mismo ejemplo.)
     */
    public static final String ETIQUETA =
            "<.*?>";

    /**
     * Para buscar con {@link #buscarTodas}: cada disparo de un log como {@code "T0T12T3"}, que es una
     * {@code T} seguida del número de la transición. En ese log tiene que encontrar {@code T0}, {@code T12} y
     * {@code T3}: el número puede tener más de un dígito.
     */
    public static final String TRANSICION =
            "T\\d+";

    /**
     * Traduce una expresión regular de JFLAP a Java. Recorré la expresión carácter por carácter:
     *
     * <table>
     *   <caption>De JFLAP a Java</caption>
     *   <tr><th>JFLAP</th><th>Java</th></tr>
     *   <tr><td>{@code +}</td><td>{@code |}</td></tr>
     *   <tr><td>{@code !}</td><td>{@code (?:)}: no alcanza con borrarlo, porque {@code !*} quedaría
     *       {@code *}, que no es una expresión válida</td></tr>
     *   <tr><td>{@code *}, {@code (} y {@code )}</td><td>igual</td></tr>
     *   <tr><td>una letra o un dígito</td><td>igual</td></tr>
     *   <tr><td>cualquier otro carácter</td><td>con una barra invertida adelante, para que sea literal:
     *       en JFLAP {@code .} es un punto, pero en Java es "cualquier carácter"</td></tr>
     * </table>
     *
     * <p>La precedencia es la misma en las dos notaciones (cierre, después concatenación, después unión),
     * así que no hace falta agregar paréntesis: {@code ab+c} queda {@code ab|c}, que en las dos es
     * {@code (ab)+c}.
     *
     * @param erJflap la expresión en notación de JFLAP
     * @return la misma expresión en notación de Java
     */
    public static String deJflap(String erJflap) {
        StringBuilder java = new StringBuilder();
        for (char c : erJflap.toCharArray()) {
            switch (c) {
                case '+' -> java.append('|');
                case '!' -> java.append("(?:)");
                case '*', '(', ')' -> java.append(c);
                default -> {
                    if (!Character.isLetterOrDigit(c)) {
                        java.append('\\');
                    }
                    java.append(c);
                }
            }
        }
        return java.toString();
    }

    /**
     * La primera cadena que está en el lenguaje de una expresión y no en el de la otra: sirve para mostrar
     * que dos expresiones <b>no</b> son iguales, como en la actividad d) de los apuntes
     * ({@code (a+b)* = a*b*}?).
     *
     * <p>Probá las cadenas de las más cortas a las más largas (λ primero) y, entre las del mismo largo, en el
     * orden del alfabeto: con {@code alfabeto = "ab"}, el orden es λ, a, b, aa, ab, ba, bb, aaa... La
     * pertenencia es con {@code matches()}, no con {@code find()}. Compilá cada expresión una sola vez, con
     * {@code Pattern.compile}, antes de empezar a probar.
     *
     * @param er1      una expresión en notación de Java
     * @param er2      otra
     * @param alfabeto los símbolos con los que se arman las cadenas, en orden
     * @param largoMax hasta qué largo probar
     * @return la primera cadena que una acepta y la otra no, o {@code null} si coinciden en todas las
     *         cadenas hasta {@code largoMax} (lo que no prueba que sean iguales)
     */
    public static String primeraDiferencia(String er1, String er2, String alfabeto, int largoMax) {
        Pattern p1 = Pattern.compile(er1);
        Pattern p2 = Pattern.compile(er2);
        List<String> tanda = List.of("");
        for (int largo = 0; largo <= largoMax; largo++) {
            List<String> siguiente = new ArrayList<>();
            for (String s : tanda) {
                if (p1.matcher(s).matches() != p2.matcher(s).matches()) {
                    return s;
                }
                for (char c : alfabeto.toCharArray()) {
                    siguiente.add(s + c);
                }
            }
            tanda = siguiente;
        }
        return null;
    }

    /**
     * Todos los pedazos del texto que coinciden con la expresión, de izquierda a derecha y sin
     * superponerse: es buscar un patrón en un texto.
     *
     * <pre>
     *   Matcher m = Pattern.compile(er).matcher(texto);
     *   mientras m.find(): agregar m.group()
     * </pre>
     *
     * @param er    la expresión, en notación de Java
     * @param texto donde buscar
     * @return lo que encontró, en orden (vacío si no encontró nada)
     */
    public static List<String> buscarTodas(String er, String texto) {
        List<String> resultado = new ArrayList<>();
        Matcher m = Pattern.compile(er).matcher(texto);
        while (m.find()) {
            resultado.add(m.group());
        }
        return resultado;
    }
}
