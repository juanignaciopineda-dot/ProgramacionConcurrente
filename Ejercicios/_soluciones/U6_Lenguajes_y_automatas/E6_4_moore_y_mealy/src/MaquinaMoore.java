import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Una máquina de Moore {@code (E, S, Q, q0, f, g)}: un autómata con salida, donde la salida depende
 * <b>solo</b> del estado.
 *
 * <pre>
 *   f: E × Q → Q     el estado siguiente
 *   g: Q → S         la salida: va en el ESTADO
 *
 *             1
 *   (a / 0) ────► (b / 1)      se lee "el estado b emite 1"
 * </pre>
 *
 * <p>La salida se ve desde que la máquina arranca (la del estado inicial) y cambia cuando cambia el
 * estado, un paso después de leer el símbolo. Por eso, para una entrada de {@code n} símbolos, la salida
 * tiene {@code n + 1}: la del inicial y la de cada estado al que se llega.
 */
public class MaquinaMoore {

    private final String inicial;
    private final Set<String> estados = new TreeSet<>();
    private final Map<String, Character> salidas = new HashMap<>();
    private final Map<String, Map<Character, String>> tabla = new HashMap<>();

    /**
     * Ya está hecho: crea la máquina sin transiciones ni salidas.
     *
     * @param inicial el estado inicial
     */
    public MaquinaMoore(String inicial) {
        if (inicial == null) {
            throw new IllegalArgumentException("el estado inicial no puede ser null");
        }
        this.inicial = inicial;
        estados.add(inicial);
    }

    /**
     * Ya está hecho: agrega el arco {@code desde --entrada--> hacia}.
     *
     * @throws IllegalArgumentException si ya había un arco con esa entrada desde ese estado que va a otro
     *                                  lado (no sería determinista)
     */
    public void agregarTransicion(String desde, char entrada, String hacia) {
        if (desde == null || hacia == null) {
            throw new IllegalArgumentException("los estados no pueden ser null");
        }
        Map<Character, String> arcos = tabla.computeIfAbsent(desde, e -> new HashMap<>());
        String anterior = arcos.putIfAbsent(entrada, hacia);
        if (anterior != null && !anterior.equals(hacia)) {
            throw new IllegalArgumentException("desde " + desde + " con " + entrada + " ya se iba a " + anterior);
        }
        estados.add(desde);
        estados.add(hacia);
    }

    /**
     * Define {@code g(estado) = salida}, y agrega el estado si no estaba.
     *
     * <p>Es la condición que define a Moore en los apuntes: a cada estado se le asocia <b>una salida y
     * sólo una</b>. Definir la misma salida dos veces no es un error; definir otra distinta sí.
     *
     * @param estado el estado
     * @param salida lo que emite mientras la máquina está en ese estado
     * @throws IllegalArgumentException si el estado es {@code null} o ya tenía otra salida
     */
    public void definirSalida(String estado, char salida) {
        if (estado == null) {
            throw new IllegalArgumentException("el estado no puede ser null");
        }
        Character anterior = salidas.putIfAbsent(estado, salida);
        if (anterior != null && anterior != salida) {
            throw new IllegalArgumentException("a cada estado una salida y sólo una: " + estado + " ya emite " + anterior);
        }
        estados.add(estado);
    }

    /**
     * Traduce una entrada: emite la salida del inicial y, por cada símbolo, pasa al estado siguiente y
     * emite la salida de ese estado.
     *
     * <pre>
     *   estado ← inicial
     *   respuesta ← g(inicial)
     *   para cada símbolo c de la entrada:
     *       estado ← f(c, estado)               (si no hay arco: IllegalArgumentException)
     *       agregar g(estado) a la respuesta    (si no tiene salida: IllegalStateException)
     * </pre>
     *
     * @param entrada los símbolos de entrada
     * @return la salida, con {@code entrada.length() + 1} símbolos
     * @throws IllegalArgumentException si en algún momento no hay arco para el símbolo leído
     * @throws IllegalStateException    si se pasa por un estado sin salida definida
     */
    public String traducir(String entrada) {
        String estado = inicial;
        StringBuilder respuesta = new StringBuilder().append(salidaDe(estado));
        for (char c : entrada.toCharArray()) {
            String siguiente = tabla.getOrDefault(estado, Map.of()).get(c);
            if (siguiente == null) {
                throw new IllegalArgumentException("no hay arco desde " + estado + " con " + c);
            }
            estado = siguiente;
            respuesta.append(salidaDe(estado));
        }
        return respuesta.toString();
    }

    private char salidaDe(String estado) {
        Character salida = salidas.get(estado);
        if (salida == null) {
            throw new IllegalStateException("el estado " + estado + " no tiene salida definida");
        }
        return salida;
    }

    /** @return el estado inicial. Ya está hecho. */
    public String inicial() {
        return inicial;
    }

    /** @return los estados, ordenados. Ya está hecho. */
    public Set<String> estados() {
        return Collections.unmodifiableSet(estados);
    }
}
