import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Un autómata finito no determinista {@code AFND = (E, Q, f, Q0, F)}, con transiciones sobre λ.
 *
 * <pre>
 *   f: E × Q → P(Q)      para un par (estado, símbolo), un CONJUNTO de estados (puede ser vacío)
 *   Q0                   los estados iniciales: puede haber más de uno
 *   λ                    transiciones que se toman sin leer nada
 * </pre>
 *
 * <p>El autómata está en <b>varios estados a la vez</b>: mientras no termine de leer, no se sabe en cuál
 * de ellos está. Acepta si, al terminar la lectura, <b>alguno</b> de los estados en los que puede estar es
 * final. Es el mismo no determinismo del planificador de hilos: no se sabe qué camino se toma, así que se
 * siguen todos.
 *
 * <p>Siempre se puede convertir en un AFD equivalente con la construcción de subconjuntos: cada estado del
 * AFD es un conjunto de estados del AFND.
 */
public class AFND {

    /** El símbolo de las transiciones que no leen nada. */
    public static final char LAMBDA = 'λ';

    private final Set<String> iniciales;
    private final Set<String> finales;
    private final Set<String> estados = new TreeSet<>();
    private final Map<String, Map<Character, Set<String>>> f = new HashMap<>();

    /**
     * Ya está hecho: crea el autómata sin transiciones.
     *
     * @param iniciales los estados iniciales
     * @param finales   los estados finales
     */
    public AFND(Set<String> iniciales, Set<String> finales) {
        if (iniciales == null || finales == null) {
            throw new IllegalArgumentException("los iniciales y los finales no pueden ser null");
        }
        this.iniciales = new TreeSet<>(iniciales);
        this.finales = new TreeSet<>(finales);
        estados.addAll(iniciales);
        estados.addAll(finales);
    }

    /**
     * Ya está hecho: agrega {@code hacia} a {@code f(desde, simbolo)}. Puede haber varios destinos para el
     * mismo par: eso es lo no determinista.
     *
     * @param desde   el estado de partida
     * @param simbolo el símbolo, o {@link #LAMBDA}
     * @param hacia   un estado destino
     */
    public void agregarTransicion(String desde, char simbolo, String hacia) {
        if (desde == null || hacia == null) {
            throw new IllegalArgumentException("los estados no pueden ser null");
        }
        f.computeIfAbsent(desde, e -> new HashMap<>()).computeIfAbsent(simbolo, s -> new TreeSet<>()).add(hacia);
        estados.add(desde);
        estados.add(hacia);
    }

    /**
     * Ya está hecho: {@code f(estado, simbolo)}.
     *
     * @return los destinos (vacío si no hay ninguno); no se puede modificar
     */
    public Set<String> destinos(String estado, char simbolo) {
        return Collections.unmodifiableSet(f.getOrDefault(estado, Map.of()).getOrDefault(simbolo, Set.of()));
    }

    /**
     * La clausura-λ de un conjunto de estados: los estados del conjunto más todos aquellos a los que se
     * llega desde ellos siguiendo <b>solo</b> transiciones λ, en cualquier cantidad (cero incluida).
     *
     * <p>Es un recorrido del grafo por los arcos λ. Ojo con los ciclos de λ ({@code p → q → p}): solo
     * agregá a la cola los estados que no viste.
     *
     * @param conjunto los estados de partida (no lo modifiques)
     * @return la clausura, un conjunto nuevo
     */
    public Set<String> clausuraLambda(Set<String> conjunto) {
        Set<String> resultado = new TreeSet<>(conjunto);
        Deque<String> cola = new ArrayDeque<>(conjunto);
        while (!cola.isEmpty()) {
            for (String destino : destinos(cola.poll(), LAMBDA)) {
                if (resultado.add(destino)) {
                    cola.add(destino);
                }
            }
        }
        return resultado;
    }

    /**
     * Adónde se puede ir leyendo {@code simbolo} desde alguno de los estados del conjunto: la unión de
     * {@code f(q, simbolo)} para cada {@code q}. No aplica la clausura-λ: eso se hace aparte.
     *
     * @param conjunto los estados de partida
     * @param simbolo  el símbolo leído
     * @return los destinos, un conjunto nuevo (vacío si no hay ninguno)
     */
    public Set<String> mover(Set<String> conjunto, char simbolo) {
        Set<String> resultado = new TreeSet<>();
        for (String q : conjunto) {
            resultado.addAll(destinos(q, simbolo));
        }
        return resultado;
    }

    /**
     * ¿Reconoce la cadena? Se simula el autómata en varios estados a la vez:
     *
     * <pre>
     *   actuales ← clausuraLambda(iniciales)
     *   para cada símbolo c: actuales ← clausuraLambda(mover(actuales, c))
     *   acepta si algún estado de actuales es final
     * </pre>
     *
     * @param cadena la entrada (λ es {@code ""})
     * @return {@code true} si pertenece al lenguaje
     */
    public boolean acepta(String cadena) {
        Set<String> actuales = clausuraLambda(iniciales);
        for (char c : cadena.toCharArray()) {
            actuales = clausuraLambda(mover(actuales, c));
        }
        for (String q : actuales) {
            if (finales.contains(q)) {
                return true;
            }
        }
        return false;
    }

    /**
     * La construcción de subconjuntos: el AFD equivalente. Cada estado del AFD es un conjunto de estados
     * del AFND, con el nombre que da {@link #nombre(Set)} (por ejemplo {@code "{q0,q1}"}).
     *
     * <pre>
     *   inicio ← clausuraLambda(iniciales)
     *   recorrido en anchura desde inicio:
     *       para el conjunto X y cada símbolo c del alfabeto():
     *           Y ← clausuraLambda(mover(X, c))
     *           si Y está vacío, no hay arco (el AFD rechaza cuando no tiene transición)
     *           si no: anotar el arco X --c--&gt; Y, y si Y es nuevo, a la cola
     *   son finales los conjuntos que tienen al menos un estado final del AFND
     * </pre>
     *
     * <p>Se construyen solo los conjuntos <b>alcanzables</b>, no los {@code 2ⁿ}. Como el constructor del
     * {@link AFD} necesita los finales, conviene hacerlo en dos pasadas: primero el recorrido, anotando los
     * conjuntos y los arcos, y después armar el AFD.
     *
     * @return el AFD equivalente
     */
    public AFD aAFD() {
        Set<String> inicio = clausuraLambda(iniciales);
        List<Set<String>> vistos = new ArrayList<>(List.of(inicio));
        Deque<Set<String>> cola = new ArrayDeque<>(List.of(inicio));
        List<String[]> arcos = new ArrayList<>();
        while (!cola.isEmpty()) {
            Set<String> x = cola.poll();
            for (char c : alfabeto()) {
                Set<String> y = clausuraLambda(mover(x, c));
                if (y.isEmpty()) {
                    continue;
                }
                arcos.add(new String[] {nombre(x), String.valueOf(c), nombre(y)});
                if (!vistos.contains(y)) {
                    vistos.add(y);
                    cola.add(y);
                }
            }
        }
        Set<String> finalesDelAfd = new HashSet<>();
        for (Set<String> x : vistos) {
            if (!Collections.disjoint(x, finales)) {
                finalesDelAfd.add(nombre(x));
            }
        }
        AFD afd = new AFD(nombre(inicio), finalesDelAfd);
        for (String[] arco : arcos) {
            afd.agregarTransicion(arco[0], arco[1].charAt(0), arco[2]);
        }
        return afd;
    }

    /**
     * El algoritmo de los apuntes para pasar de una gramática de tipo 3 a un autómata finito. El autómata
     * que sale es, en general, no determinista.
     *
     * <table>
     *   <caption>De la gramática al autómata</caption>
     *   <tr><th>Del AF</th><th>Cómo se obtiene</th></tr>
     *   <tr><td>estados</td><td>uno por cada no terminal (con su nombre, {@code "S"}, {@code "A"}...), más
     *       uno nuevo, {@code "qf"}</td></tr>
     *   <tr><td>inicial</td><td>el símbolo inicial de la gramática</td></tr>
     *   <tr><td>finales</td><td>{@code qf}, y además cada {@code A} con una producción {@code A → λ}</td></tr>
     *   <tr><td>{@code A → aB}</td><td>{@code B ∈ f(A, a)}</td></tr>
     *   <tr><td>{@code A → a}</td><td>{@code qf ∈ f(A, a)}</td></tr>
     * </table>
     *
     * <p>Los apuntes dan solo {@code A → aB} y {@code A → a}. La regla de {@code A → λ} es la que hace falta
     * para la gramática del ejercicio 3: si desde {@code A} se puede terminar sin leer nada, llegar a
     * {@code A} ya alcanza para aceptar.
     *
     * @param g una gramática de tipo 3
     * @return el AFND que reconoce {@code L(g)}
     * @throws IllegalArgumentException si la gramática no es de tipo 3
     */
    public static AFND deGramatica(Gramatica g) {
        if (g.tipo() != 3) {
            throw new IllegalArgumentException("el algoritmo es para gramáticas de tipo 3, y esta es de tipo " + g.tipo());
        }
        Set<String> finales = new TreeSet<>(Set.of("qf"));
        for (Gramatica.Produccion p : g.producciones()) {
            if (p.derecha().isEmpty()) {
                finales.add(p.izquierda());
            }
        }
        AFND m = new AFND(Set.of(String.valueOf(g.inicial())), finales);
        for (Gramatica.Produccion p : g.producciones()) {
            String der = p.derecha();
            if (der.length() == 1) {
                m.agregarTransicion(p.izquierda(), der.charAt(0), "qf");
            } else if (der.length() == 2) {
                m.agregarTransicion(p.izquierda(), der.charAt(0), String.valueOf(der.charAt(1)));
            }
        }
        return m;
    }

    /**
     * Ya está hecho: el nombre de un conjunto de estados, ordenado y sin espacios: {@code "{q0,q2}"}, o
     * {@code "{}"} para el vacío.
     *
     * @param conjunto los estados
     * @return el nombre
     */
    public static String nombre(Set<String> conjunto) {
        return "{" + String.join(",", new TreeSet<>(conjunto)) + "}";
    }

    /** @return los símbolos de las transiciones, sin λ, ordenados. Ya está hecho. */
    public Set<Character> alfabeto() {
        Set<Character> simbolos = new TreeSet<>();
        for (Map<Character, Set<String>> salidas : f.values()) {
            simbolos.addAll(salidas.keySet());
        }
        simbolos.remove(LAMBDA);
        return simbolos;
    }

    /** @return los estados iniciales. Ya está hecho. */
    public Set<String> iniciales() {
        return Collections.unmodifiableSet(iniciales);
    }

    /** @return los estados finales. Ya está hecho. */
    public Set<String> finales() {
        return Collections.unmodifiableSet(finales);
    }

    /** @return todos los estados, ordenados. Ya está hecho. */
    public Set<String> estados() {
        return Collections.unmodifiableSet(estados);
    }
}
