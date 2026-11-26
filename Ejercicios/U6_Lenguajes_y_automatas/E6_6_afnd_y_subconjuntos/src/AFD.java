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
 * <b>Ya está hecho: es el del ejercicio 6.3.</b>
 *
 * <p>Un autómata finito determinista {@code AF = (E, Q, f, q1, F)}: reconoce un lenguaje regular.
 *
 * <pre>
 *   E    el vocabulario de entrada: los símbolos que aparecen en las transiciones
 *   Q    los estados, con nombre ("e0", "par", "{q0,q1}")
 *   f    la función de transición: f(estado, símbolo) = estado
 *   q1   el estado inicial (uno solo)
 *   F    los estados finales
 * </pre>
 *
 * <p>La cinta se lee de izquierda a derecha, un símbolo por vez. La cadena se acepta si, al terminar de
 * leerla, el control quedó en un estado final. No hay salida: la única respuesta es sí o no.
 *
 * <p>{@code f} puede ser <b>parcial</b>: si para un par (estado, símbolo) no hay transición, el autómata
 * se traba y la cadena se rechaza. Es como si hubiera un estado trampa, no final, del que no se sale, que
 * no hace falta dibujar.
 */
public class AFD {

    private final String inicial;
    private final Set<String> finales;
    private final Set<String> estados = new TreeSet<>();
    private final Map<String, Map<Character, String>> transiciones = new HashMap<>();

    /**
     * Ya está hecho: crea el autómata sin transiciones.
     *
     * @param inicial el estado inicial
     * @param finales los estados finales (puede incluir al inicial)
     * @throws IllegalArgumentException si alguno es {@code null}
     */
    public AFD(String inicial, Set<String> finales) {
        if (inicial == null || finales == null || finales.stream().anyMatch(f -> f == null)) {
            throw new IllegalArgumentException("el estado inicial y los finales no pueden ser null");
        }
        this.inicial = inicial;
        this.finales = new HashSet<>(finales);
        estados.add(inicial);
        estados.addAll(finales);
    }

    /**
     * Agrega {@code f(desde, simbolo) = hacia}, y agrega {@code desde} y {@code hacia} a los estados.
     *
     * <p>Es determinista: si ya hay una transición desde {@code desde} con {@code simbolo} que va a
     * <b>otro</b> estado, lanzá {@code IllegalArgumentException} y no cambies nada. Agregar dos veces la
     * misma transición no es un error.
     *
     * @param desde   el estado de partida
     * @param simbolo el símbolo que se lee
     * @param hacia   el estado al que se pasa
     * @throws IllegalArgumentException si algún estado es {@code null} o si la transición haría que el
     *                                  autómata no sea determinista
     */
    public void agregarTransicion(String desde, char simbolo, String hacia) {
        if (desde == null || hacia == null) {
            throw new IllegalArgumentException("los estados no pueden ser null");
        }
        Map<Character, String> salidas = transiciones.computeIfAbsent(desde, e -> new HashMap<>());
        String anterior = salidas.get(simbolo);
        if (anterior != null && !anterior.equals(hacia)) {
            throw new IllegalArgumentException("no es determinista: f(" + desde + ", " + simbolo + ") ya es "
                    + anterior + " y ahora sería " + hacia);
        }
        salidas.put(simbolo, hacia);
        estados.add(desde);
        estados.add(hacia);
    }

    /**
     * La función de transición {@code f(estado, simbolo)}.
     *
     * @param estado  un estado
     * @param simbolo un símbolo
     * @return el estado siguiente, o {@code null} si no hay transición (o si el estado no existe)
     */
    public String siguiente(String estado, char simbolo) {
        Map<Character, String> salidas = transiciones.get(estado);
        return salidas == null ? null : salidas.get(simbolo);
    }

    /**
     * Los estados por los que pasa el control al leer la cadena, empezando por el inicial. Si lee todo, la
     * lista tiene {@code cadena.length() + 1} estados.
     *
     * <p>Si en algún momento no hay transición, el autómata se traba: el recorrido termina en el último
     * estado al que llegó, sin agregar nada más. Con el autómata de L3 de los apuntes, {@code "ca"} da
     * {@code [e0, e2]}: de {@code e2} no se sale con {@code a}.
     *
     * @param cadena la entrada
     * @return los estados recorridos
     */
    public List<String> recorrido(String cadena) {
        List<String> resultado = new ArrayList<>();
        String actual = inicial;
        resultado.add(actual);
        for (char simbolo : cadena.toCharArray()) {
            actual = siguiente(actual, simbolo);
            if (actual == null) {
                break;
            }
            resultado.add(actual);
        }
        return resultado;
    }

    /**
     * ¿Reconoce la cadena? Sí cuando leyó <b>toda</b> la cadena (no se trabó) y terminó en un estado final.
     * Usá {@link #recorrido(String)}.
     *
     * @param cadena la entrada (λ es {@code ""}: se acepta si el inicial es final)
     * @return {@code true} si la cadena pertenece al lenguaje del autómata
     */
    public boolean acepta(String cadena) {
        List<String> estadosRecorridos = recorrido(cadena);
        return estadosRecorridos.size() == cadena.length() + 1
                && finales.contains(estadosRecorridos.get(estadosRecorridos.size() - 1));
    }

    /**
     * Los estados accesibles desde el inicial: aquellos a los que se llega con alguna cadena. El inicial es
     * accesible desde sí mismo, con λ.
     *
     * <p>Es un recorrido del grafo de estados: arrancá con el inicial en una cola y, para cada estado que
     * sacás, agregá los destinos de todas sus transiciones que todavía no viste. Ojo: no alcanza con los
     * vecinos del inicial; un estado puede estar a varias transiciones de distancia.
     *
     * @return los estados accesibles
     */
    public Set<String> accesibles() {
        Set<String> vistos = new HashSet<>(Set.of(inicial));
        Deque<String> cola = new ArrayDeque<>(List.of(inicial));
        while (!cola.isEmpty()) {
            String estado = cola.poll();
            for (String destino : transiciones.getOrDefault(estado, Map.of()).values()) {
                if (vistos.add(destino)) {
                    cola.add(destino);
                }
            }
        }
        return vistos;
    }

    /**
     * Un autómata es conexo si todos sus estados son accesibles desde el inicial. Si no lo es, borrando los
     * inaccesibles se obtiene uno equivalente: a esos estados no se llega nunca.
     *
     * @return {@code true} si todos los estados son accesibles
     */
    public boolean esConexo() {
        return accesibles().containsAll(estados);
    }

    /** @return el estado inicial. Ya está hecho. */
    public String inicial() {
        return inicial;
    }

    /** @return los estados finales. Ya está hecho. */
    public Set<String> finales() {
        return Collections.unmodifiableSet(finales);
    }

    /** @return todos los estados, ordenados. Ya está hecho. */
    public Set<String> estados() {
        return Collections.unmodifiableSet(estados);
    }

    /** @return los símbolos que aparecen en alguna transición, ordenados. Ya está hecho. */
    public Set<Character> alfabeto() {
        Set<Character> simbolos = new TreeSet<>();
        for (Map<Character, String> salidas : transiciones.values()) {
            simbolos.addAll(salidas.keySet());
        }
        return simbolos;
    }

    /** La tabla de transiciones: una fila por estado, una columna por símbolo. Ya está hecho. */
    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder("f");
        Set<Character> simbolos = alfabeto();
        for (char s : simbolos) {
            texto.append('\t').append(s);
        }
        for (String estado : estados) {
            texto.append('\n').append(estado.equals(inicial) ? "→" : "").append(estado)
                    .append(finales.contains(estado) ? "*" : "");
            for (char s : simbolos) {
                String destino = transiciones.getOrDefault(estado, Map.of()).get(s);
                texto.append('\t').append(destino == null ? "-" : destino);
            }
        }
        return texto.toString();
    }
}
