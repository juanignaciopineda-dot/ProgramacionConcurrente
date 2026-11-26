import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Una máquina de Mealy {@code (E, S, Q, q0, f, g)}: un autómata con salida, donde la salida depende del
 * estado <b>y</b> de la entrada.
 *
 * <pre>
 *   f: E × Q → Q     el estado siguiente
 *   g: E × Q → S     la salida: va en el ARCO
 *
 *         1 / 0                 se lee "con entrada 1, emite 0"
 *   (a) ────────► (b)
 * </pre>
 *
 * <p>Por cada símbolo que lee emite uno, en el mismo paso: para una entrada de {@code n} símbolos, la
 * salida tiene {@code n}.
 */
public class MaquinaMealy {

    /**
     * Lo que pasa con una entrada desde un estado.
     *
     * @param hacia  el estado siguiente, {@code f(entrada, estado)}
     * @param salida lo que emite, {@code g(entrada, estado)}
     */
    public record Transicion(String hacia, char salida) {
    }

    private final String inicial;
    private final Set<String> estados = new TreeSet<>();
    private final Map<String, Map<Character, Transicion>> tabla = new HashMap<>();

    /**
     * Ya está hecho: crea la máquina sin transiciones.
     *
     * @param inicial el estado inicial
     */
    public MaquinaMealy(String inicial) {
        if (inicial == null) {
            throw new IllegalArgumentException("el estado inicial no puede ser null");
        }
        this.inicial = inicial;
        estados.add(inicial);
    }

    /**
     * Ya está hecho: agrega el arco {@code desde --entrada / salida--> hacia}.
     *
     * @throws IllegalArgumentException si ya había un arco con esa entrada desde ese estado que va a otro
     *                                  lado o emite otra cosa (no sería determinista)
     */
    public void agregarTransicion(String desde, char entrada, String hacia, char salida) {
        if (desde == null || hacia == null) {
            throw new IllegalArgumentException("los estados no pueden ser null");
        }
        Transicion nueva = new Transicion(hacia, salida);
        Map<Character, Transicion> arcos = tabla.computeIfAbsent(desde, e -> new HashMap<>());
        Transicion anterior = arcos.putIfAbsent(entrada, nueva);
        if (anterior != null && !anterior.equals(nueva)) {
            throw new IllegalArgumentException("desde " + desde + " con " + entrada + " ya había " + anterior);
        }
        estados.add(desde);
        estados.add(hacia);
    }

    /**
     * Ya está hecho.
     *
     * @return el arco que sale de {@code estado} con {@code entrada}, o {@code null} si no hay
     */
    public Transicion transicion(String estado, char entrada) {
        return tabla.getOrDefault(estado, Map.of()).get(entrada);
    }

    /**
     * Traduce una entrada: arranca en el inicial y, por cada símbolo, sigue su arco y emite la salida del
     * arco.
     *
     * <pre>
     *   estado ← inicial
     *   para cada símbolo c de la entrada:
     *       t ← transicion(estado, c)          (si no hay: IllegalArgumentException)
     *       agregar t.salida() a la respuesta
     *       estado ← t.hacia()
     * </pre>
     *
     * @param entrada los símbolos de entrada
     * @return la salida, un símbolo por cada símbolo de entrada
     * @throws IllegalArgumentException si en algún momento no hay arco para el símbolo leído
     */
    public String traducir(String entrada) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("MaquinaMealy.traducir()");
    }

    /** @return el estado inicial. Ya está hecho. */
    public String inicial() {
        return inicial;
    }

    /** @return los estados, ordenados. Ya está hecho. */
    public Set<String> estados() {
        return Collections.unmodifiableSet(estados);
    }

    /** @return los símbolos de entrada que aparecen en algún arco, ordenados. Ya está hecho. */
    public Set<Character> alfabeto() {
        Set<Character> simbolos = new TreeSet<>();
        for (Map<Character, Transicion> arcos : tabla.values()) {
            simbolos.addAll(arcos.keySet());
        }
        return simbolos;
    }
}
