import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Un autómata de pila determinista {@code AP = (Q, Σ, Γ, δ, q0, Z, F)}: un autómata finito con una pila
 * como memoria auxiliar. Reconoce lenguajes libres de contexto como {@code aⁿbⁿ}, que un autómata finito no
 * puede (no tiene dónde guardar cuántas {@code a} leyó).
 *
 * <pre>
 *   δ(q, a, Z) = (p, γ)     en el estado q, leyendo a, con Z en el tope:
 *                           pasa a p, saca Z y apila γ (el primer símbolo de γ queda en el tope)
 *   δ(q, λ, Z) = (p, γ)     lo mismo, pero sin leer: la cinta no se mueve
 * </pre>
 *
 * <p>En los arcos se escribe {@code entrada, tope ; lo que apilo}: {@code a, Z ; aZ} es "leo a, saco Z,
 * apilo aZ", o sea, agregar una {@code a} sobre la {@code Z}. Y {@code b, a ; λ} es desapilar.
 *
 * <p>La <b>configuración</b> {@code (q, W, α)} es la situación en un instante: el estado, lo que falta
 * leer y la pila, con el tope a la izquierda. Un <b>movimiento</b> {@code ⊢} pasa de una configuración a la
 * siguiente: {@code (q, aW, Zα) ⊢ (p, W, γα)}.
 */
public class AutomataDePila {

    /** La λ de las transiciones que no leen nada de la entrada. */
    public static final char LAMBDA = 'λ';

    /** Cuántos movimientos se permiten antes de suponer que el autómata entró en un bucle de λ. */
    public static final int MAX_MOVIMIENTOS = 10_000;

    /**
     * Una configuración {@code (q, W, α)}.
     *
     * @param estado   el estado actual
     * @param restante lo que falta leer ({@code ""} si ya se leyó todo)
     * @param pila     el contenido de la pila, con el tope en la posición 0 ({@code ""} si está vacía)
     */
    public record Configuracion(String estado, String restante, String pila) {
        @Override
        public String toString() {
            return "(" + estado + ", " + (restante.isEmpty() ? "λ" : restante) + ", " + (pila.isEmpty() ? "λ" : pila) + ")";
        }
    }

    /**
     * El resultado de una transición.
     *
     * @param hacia  el estado siguiente
     * @param apilar lo que reemplaza al tope ({@code ""} para desapilar)
     */
    public record Movimiento(String hacia, String apilar) {
    }

    private final String inicial;
    private final char fondo;
    private final Set<String> finales;
    /**
     * δ, agrupada por (estado, tope): para cada par, qué hace el autómata con cada entrada (λ incluida).
     * Así, las transiciones que pueden competir entre sí quedan juntas en el mismo mapa.
     */
    private final Map<String, Map<Character, Movimiento>> delta = new HashMap<>();

    /**
     * Ya está hecho: crea el autómata sin transiciones. La pila arranca con el símbolo de fondo.
     *
     * @param inicial el estado inicial
     * @param fondo   el símbolo que está en la pila al empezar ({@code Z})
     * @param finales los estados finales
     */
    public AutomataDePila(String inicial, char fondo, Set<String> finales) {
        if (inicial == null || finales == null) {
            throw new IllegalArgumentException("el estado inicial y los finales no pueden ser null");
        }
        this.inicial = inicial;
        this.fondo = fondo;
        this.finales = new HashSet<>(finales);
    }

    /**
     * Ya está hecho: las transiciones que salen de {@code estado} con {@code tope} en la pila, por entrada.
     * El mapa es el que guarda {@code delta}: si lo modificás, modificás el autómata.
     */
    private Map<Character, Movimiento> desde(String estado, char tope) {
        return delta.computeIfAbsent(estado + "|" + tope, k -> new HashMap<>());
    }

    /** Ya está hecho: {@code δ(estado, entrada, tope)}, o {@code null} si no está definida. */
    private Movimiento delta(String estado, char entrada, char tope) {
        return desde(estado, tope).get(entrada);
    }

    /**
     * Agrega {@code δ(desde, entrada, tope) = (hacia, apilar)}. Las transiciones que salen del mismo estado
     * con el mismo tope están juntas en {@link #desde(String, char)}: agregala ahí.
     *
     * <p>El autómata tiene que seguir siendo <b>determinista</b>. Los apuntes dan las dos situaciones que lo
     * vuelven no determinista, y en las dos tenés que lanzar {@code IllegalArgumentException} sin agregar
     * nada:
     *
     * <ol>
     *   <li>Dos transiciones con el mismo estado, la misma entrada y el mismo tope, pero distinto resultado.
     *       (Agregar dos veces exactamente la misma no es un error.)
     *   <li>Una transición sobre λ que compite con otra sobre un símbolo, desde el mismo estado y con el
     *       mismo tope. Hay que revisarlo en los dos sentidos: si llega la de λ y ya había una con símbolo, y
     *       si llega una con símbolo y ya había la de λ.
     * </ol>
     *
     * <p>Mismo estado y misma entrada con <b>distinto tope</b> no compiten: {@code b, a ; λ} y
     * {@code b, Z ; Z} desde el mismo estado es determinista.
     *
     * @param desde   el estado de partida
     * @param entrada el símbolo que se lee, o {@link #LAMBDA} si no se lee nada
     * @param tope    el símbolo que tiene que estar en el tope (y que se saca)
     * @param hacia   el estado siguiente
     * @param apilar  lo que se apila en lugar del tope, con su primer símbolo arriba ({@code ""} es λ)
     * @throws IllegalArgumentException si algún estado o {@code apilar} es {@code null}, o si el autómata
     *                                  dejaría de ser determinista
     */
    public void agregarTransicion(String desde, char entrada, char tope, String hacia, String apilar) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("AutomataDePila.agregarTransicion()");
    }

    /**
     * Ya está hecho: la configuración inicial {@code (q0, entrada, Z)}.
     *
     * @param entrada la cadena a reconocer
     * @return la configuración inicial
     */
    public Configuracion inicio(String entrada) {
        return new Configuracion(inicial, entrada, String.valueOf(fondo));
    }

    /**
     * Un movimiento {@code ⊢}: la configuración siguiente, o {@code null} si el autómata se para.
     *
     * <pre>
     *   si la pila está vacía: se para (null)
     *   Z ← el tope (pila.charAt(0))
     *   si queda entrada y hay δ(q, primer símbolo, Z) = (p, γ):
     *       (p, entrada sin su primer símbolo, γ + pila sin el tope)
     *   si no, si hay δ(q, λ, Z) = (p, γ):
     *       (p, la misma entrada, γ + pila sin el tope)
     *   si no: se para (null)
     * </pre>
     *
     * <p>Como el autómata es determinista, nunca hay que elegir entre las dos.
     *
     * @param c la configuración actual
     * @return la siguiente, o {@code null} si no hay movimiento posible
     */
    public Configuracion paso(Configuracion c) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("AutomataDePila.paso()");
    }

    /**
     * La traza completa: la configuración inicial y cada una de las que siguen, hasta que el autómata se
     * para. Es la actividad "Traza de configuraciones" de los apuntes.
     *
     * <p>Una transición sobre λ que no cambia nada ({@code δ(q, λ, Z) = (q, Z)}) hace que el autómata no se
     * pare nunca. Si la traza pasa de {@link #MAX_MOVIMIENTOS} movimientos, lanzá
     * {@code IllegalStateException}: es mejor un error que un programa colgado.
     *
     * @param entrada la cadena
     * @return las configuraciones, en orden, empezando por {@link #inicio(String)}
     * @throws IllegalStateException si el autómata no se para en {@link #MAX_MOVIMIENTOS} movimientos
     */
    public List<Configuracion> traza(String entrada) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("AutomataDePila.traza()");
    }

    /**
     * ¿Reconoce la cadena? Por estado final: se acepta si alguna configuración de la traza tiene la entrada
     * <b>toda leída</b> y un estado <b>final</b>. Las dos cosas a la vez: llegar a un final con entrada sin
     * leer no alcanza.
     *
     * @param entrada la cadena
     * @return {@code true} si la cadena pertenece al lenguaje
     */
    public boolean acepta(String entrada) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("AutomataDePila.acepta()");
    }
}
