import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <b>Ya está hecha: es la del ejercicio 6.2.</b>
 *
 * <p>Una gramática formal {@code G = (V_T, V_N, S, P)}.
 *
 * <pre>
 *   terminales      minúsculas y dígitos: a, b, c, 0, 1     (los símbolos de las cadenas del lenguaje)
 *   no terminales   mayúsculas: S, A, B                     (auxiliares: no aparecen en las cadenas)
 *   S               el símbolo inicial, un no terminal
 *   P               las producciones, escritas "aSb -&gt; aaSbb", "S -&gt; aS | aB" o "B -&gt; λ"
 * </pre>
 *
 * <p>Una <b>forma sentencial</b> es una cadena de terminales y no terminales, como {@code "aaSbb"}.
 * Aplicar una producción {@code α → β} es reemplazar <b>una</b> aparición de {@code α} por {@code β}:
 * eso es una derivación directa. El lenguaje generado son las formas de puros terminales a las que se
 * llega desde {@code S}:
 *
 * <pre>
 *   L(G) = { w ∈ V_T* | S ⇒* w }
 * </pre>
 *
 * <p>La cadena vacía λ se escribe {@code ""} en las formas sentenciales y {@code "λ"} en las producciones.
 */
public class Gramatica {

    /**
     * Una producción {@code izquierda → derecha}. La derecha es {@code ""} cuando la producción es
     * {@code A → λ}.
     *
     * @param izquierda lo que se reemplaza (tiene al menos un no terminal)
     * @param derecha   por qué se reemplaza
     */
    public record Produccion(String izquierda, String derecha) {
        @Override
        public String toString() {
            return izquierda + " → " + (derecha.isEmpty() ? "λ" : derecha);
        }
    }

    private final char inicial;
    private final List<Produccion> producciones = new ArrayList<>();

    /**
     * Ya está hecho: lee las producciones. Cada texto es {@code "izquierda -> derecha"}, con varias
     * derechas separadas por {@code |}, y {@code λ} para la derecha vacía.
     *
     * <pre>
     *   new Gramatica('S', "S -&gt; aS | aB", "B -&gt; bB | b")
     * </pre>
     *
     * @param inicial      el símbolo inicial
     * @param producciones las producciones
     * @throws IllegalArgumentException si el inicial no es un no terminal, si una producción está mal
     *                                  escrita, usa símbolos que no son terminales ni no terminales, o su
     *                                  parte izquierda no tiene ningún no terminal
     */
    public Gramatica(char inicial, String... producciones) {
        if (!esNoTerminal(inicial)) {
            throw new IllegalArgumentException("el símbolo inicial tiene que ser un no terminal (mayúscula): " + inicial);
        }
        this.inicial = inicial;
        for (String texto : producciones) {
            String[] lados = texto.split("->|→", -1);
            if (lados.length != 2) {
                throw new IllegalArgumentException("una producción se escribe 'izquierda -> derecha': " + texto);
            }
            String izquierda = lados[0].strip();
            if (izquierda.isEmpty() || !izquierda.chars().allMatch(c -> esTerminal((char) c) || esNoTerminal((char) c))) {
                throw new IllegalArgumentException("parte izquierda inválida en '" + texto + "'");
            }
            if (izquierda.chars().noneMatch(c -> esNoTerminal((char) c))) {
                throw new IllegalArgumentException("la parte izquierda tiene que tener al menos un no terminal: " + texto);
            }
            for (String alternativa : lados[1].split("\\|", -1)) {
                String derecha = alternativa.strip();
                if (derecha.equals("λ")) {
                    derecha = "";
                } else if (derecha.isEmpty()
                        || !derecha.chars().allMatch(c -> esTerminal((char) c) || esNoTerminal((char) c))) {
                    throw new IllegalArgumentException("parte derecha inválida en '" + texto + "' (λ se escribe λ)");
                }
                this.producciones.add(new Produccion(izquierda, derecha));
            }
        }
    }

    /** @return el símbolo inicial */
    public char inicial() {
        return inicial;
    }

    /** @return las producciones, en el orden en que se escribieron (no se puede modificar) */
    public List<Produccion> producciones() {
        return Collections.unmodifiableList(producciones);
    }

    /** @return {@code true} si {@code c} es un terminal: una minúscula o un dígito */
    public static boolean esTerminal(char c) {
        return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
    }

    /** @return {@code true} si {@code c} es un no terminal: una mayúscula */
    public static boolean esNoTerminal(char c) {
        return c >= 'A' && c <= 'Z';
    }

    /** @return {@code true} si la forma sentencial es de puros terminales (λ incluida) */
    public static boolean esDeTerminales(String forma) {
        return forma.chars().allMatch(c -> esTerminal((char) c));
    }

    /**
     * El tipo de la gramática en la jerarquía de Chomsky: el <b>más restrictivo</b> cuya forma cumplen
     * <b>todas</b> las producciones. Se prueba del 3 para abajo:
     *
     * <table>
     *   <caption>Las formas de cada tipo</caption>
     *   <tr><th>Tipo</th><th>Cada producción es</th></tr>
     *   <tr><td>3, regular</td><td>{@code A → aB}, {@code A → a} o {@code A → λ}: a la izquierda un no
     *       terminal, a la derecha un terminal seguido o no de un no terminal (o λ, como en JFLAP)</td></tr>
     *   <tr><td>2, libre de contexto</td><td>{@code A → α}: a la izquierda un solo no terminal, a la
     *       derecha cualquier cosa</td></tr>
     *   <tr><td>1, sensible al contexto</td><td>la derecha no es más corta que la izquierda
     *       ({@code |izq| <= |der|}), salvo {@code S → λ} con el símbolo inicial</td></tr>
     *   <tr><td>0, sin restricciones</td><td>cualquier producción (con un no terminal a la izquierda, que
     *       ya lo controla el constructor)</td></tr>
     * </table>
     *
     * <p>Dos trampas: {@code A → Ba} (el no terminal a la izquierda de la derecha) y {@code A → ab} (dos
     * terminales) no tienen la forma del tipo 3, así que esas gramáticas son de tipo 2, aunque el lenguaje
     * que generan sea regular. Una cosa es el tipo de la gramática y otra el del lenguaje.
     *
     * @return 3, 2, 1 o 0
     */
    public int tipo() {
        if (producciones.stream().allMatch(Gramatica::esRegular)) {
            return 3;
        }
        if (producciones.stream().allMatch(p -> p.izquierda().length() == 1)) {
            return 2;
        }
        String s = String.valueOf(inicial);
        if (producciones.stream().allMatch(p -> p.izquierda().length() <= p.derecha().length()
                || (p.izquierda().equals(s) && p.derecha().isEmpty()))) {
            return 1;
        }
        return 0;
    }

    private static boolean esRegular(Produccion p) {
        String izq = p.izquierda();
        String der = p.derecha();
        if (izq.length() != 1 || !esNoTerminal(izq.charAt(0))) {
            return false;
        }
        return der.isEmpty()
                || (der.length() == 1 && esTerminal(der.charAt(0)))
                || (der.length() == 2 && esTerminal(der.charAt(0)) && esNoTerminal(der.charAt(1)));
    }

    /**
     * Las derivaciones directas de una forma sentencial: todas las formas que salen de aplicar
     * <b>una</b> producción en <b>una</b> de las posiciones donde aparece su parte izquierda.
     *
     * <p>Con {@code S → SS | a}, de {@code "SS"} salen {@code "SSS"} (por cualquiera de las dos S),
     * {@code "aS"} y {@code "Sa"}. Si la izquierda aparece varias veces, hay que probar cada aparición
     * (buscá con {@code indexOf(izquierda, desde)}, avanzando {@code desde}).
     *
     * @param forma la forma sentencial
     * @return las formas a las que se llega en un paso ({@code ⇒}); vacío si no se puede aplicar ninguna
     * @throws IllegalArgumentException si {@code forma} es {@code null}
     */
    public Set<String> derivacionesDirectas(String forma) {
        if (forma == null) {
            throw new IllegalArgumentException("la forma sentencial no puede ser null (λ es \"\")");
        }
        Set<String> resultado = new HashSet<>();
        for (Produccion p : producciones) {
            String izq = p.izquierda();
            for (int i = forma.indexOf(izq); i >= 0; i = forma.indexOf(izq, i + 1)) {
                resultado.add(forma.substring(0, i) + p.derecha() + forma.substring(i + izq.length()));
            }
        }
        return resultado;
    }

    /**
     * ¿Es {@code pasos} una derivación completa de una cadena del lenguaje? Tiene que:
     *
     * <ol>
     *   <li>Empezar en el símbolo inicial.
     *   <li>Avanzar de a una derivación directa: cada forma sale de la anterior en un paso.
     *   <li>Terminar en una forma de puros terminales (λ, {@code ""}, también vale).
     * </ol>
     *
     * <p>Es lo que se pide cuando dicen "derivá la cadena desde el símbolo distinguido, mostrando cada
     * paso". La de los apuntes: {@code S ⇒ aS ⇒ aaB ⇒ aabB ⇒ aabb}.
     *
     * @param pasos las formas sentenciales, en orden
     * @return {@code true} si es una derivación de una cadena de {@code L(G)}; {@code false} si es
     *         {@code null}, está vacía o falla alguna de las tres condiciones
     */
    public boolean esDerivacion(List<String> pasos) {
        if (pasos == null || pasos.isEmpty() || !String.valueOf(inicial).equals(pasos.get(0))) {
            return false;
        }
        for (int i = 1; i < pasos.size(); i++) {
            if (!derivacionesDirectas(pasos.get(i - 1)).contains(pasos.get(i))) {
                return false;
            }
        }
        return esDeTerminales(pasos.get(pasos.size() - 1));
    }

    /**
     * Las cadenas de {@code L(G)} de largo {@code <= largoMax}: un recorrido en anchura de las formas
     * sentenciales, desde {@code S}.
     *
     * <pre>
     *   cola ← [S]   vistas ← {S}
     *   mientras haya formas en la cola:
     *       f ← sacar de la cola
     *       si f es de puros terminales: es una cadena del lenguaje (y no se deriva más)
     *       si no: para cada g en derivacionesDirectas(f)
     *           descartar g si tiene más de largoMax terminales, o si su largo pasa 2·largoMax + 2
     *           si no la vimos: agregarla a vistas y a la cola
     * </pre>
     *
     * <p>Las podas son las que hacen que termine:
     *
     * <ul>
     *   <li><b>Terminales:</b> una producción nunca borra terminales en las gramáticas de tipo 1, 2 y 3,
     *       así que una forma con más de {@code largoMax} terminales no va a dar una cadena corta.
     *   <li><b>Largo:</b> sin esta cota, con {@code S → SS} la cola crece para siempre
     *       ({@code SS}, {@code SSS}, ...) aunque no aparezca ningún terminal.
     * </ul>
     *
     * <p>Para las gramáticas de tipo 1, que nunca acortan, la poda es exacta. Para las que tienen
     * {@code A → λ} alcanza en las gramáticas de este ejercicio, pero no es una garantía general: es una
     * de las preguntas del README.
     *
     * @param largoMax el largo máximo de las cadenas
     * @return las cadenas generadas de largo {@code <= largoMax}
     * @throws IllegalArgumentException si {@code largoMax < 0}
     */
    public Set<String> generar(int largoMax) {
        if (largoMax < 0) {
            throw new IllegalArgumentException("el largo máximo no puede ser negativo: " + largoMax);
        }
        String s = String.valueOf(inicial);
        Set<String> vistas = new HashSet<>(Set.of(s));
        Deque<String> cola = new ArrayDeque<>(List.of(s));
        Set<String> resultado = new HashSet<>();
        while (!cola.isEmpty()) {
            String f = cola.poll();
            if (esDeTerminales(f)) {
                resultado.add(f);
                continue;
            }
            for (String g : derivacionesDirectas(f)) {
                long terminales = g.chars().filter(c -> esTerminal((char) c)).count();
                if (terminales > largoMax || g.length() > 2 * largoMax + 2) {
                    continue;
                }
                if (vistas.add(g)) {
                    cola.add(g);
                }
            }
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "G(" + inicial + ", " + producciones + ")";
    }
}
