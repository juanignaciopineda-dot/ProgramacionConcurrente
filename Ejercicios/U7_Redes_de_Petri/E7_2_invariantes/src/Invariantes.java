import java.util.Arrays;

/**
 * Cuentas con invariantes, sobre la matriz de incidencia {@code C} ({@code lugares x transiciones}).
 *
 * <ul>
 *   <li><b>P-invariante</b>: un vector {@code y} de pesos sobre los lugares con {@code yᵀ · C = 0}.
 *       La suma pesada de fichas {@code yᵀ · M} no cambia nunca, dispare lo que dispare. Es lo que
 *       permite probar propiedades de seguridad sin recorrer los estados (por ejemplo,
 *       {@code M(A adentro) + M(B adentro) + M(mutex) = 1}, así que nunca hay dos adentro).
 *   <li><b>T-invariante</b>: un vector {@code x} de disparos con {@code C · x = 0}. Si existe una
 *       secuencia con ese vector característico, dispararla devuelve la red al marcado del que partió.
 * </ul>
 *
 * <p>Todos los métodos son estáticos y no modifican sus argumentos, salvo
 * {@link #esRepetitiva(RedDePetri, int[])}, que dispara la red que le pasan.
 */
public final class Invariantes {

    private Invariantes() {
    }

    /**
     * ¿Es {@code y} un P-invariante? Para cada transición {@code t}, la suma
     * {@code Σ_p y[p] · C[p][t]} tiene que dar 0.
     *
     * @param incidencia la matriz {@code C}
     * @param y          un peso por lugar
     * @return {@code true} si {@code yᵀ · C = 0}
     * @throws IllegalArgumentException si {@code y} no tiene una componente por lugar
     */
    public static boolean esPInvariante(int[][] incidencia, int[] y) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Invariantes.esPInvariante()");
    }

    /**
     * ¿Es {@code x} un T-invariante? Para cada lugar {@code p}, la suma {@code Σ_t C[p][t] · x[t]}
     * tiene que dar 0.
     *
     * @param incidencia la matriz {@code C}
     * @param x          una cantidad de disparos por transición
     * @return {@code true} si {@code C · x = 0}
     * @throws IllegalArgumentException si {@code x} no tiene una componente por transición
     */
    public static boolean esTInvariante(int[][] incidencia, int[] x) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Invariantes.esTInvariante()");
    }

    /**
     * El valor de un P-invariante en un marcado: {@code yᵀ · M = Σ_p y[p] · M[p]}. Si {@code y} es un
     * P-invariante, da lo mismo en todos los marcados alcanzables: es la "constante" del invariante.
     *
     * @param y       un peso por lugar
     * @param marcado un marcado
     * @return la suma pesada de fichas
     * @throws IllegalArgumentException si los largos no coinciden
     */
    public static int valor(int[] y, int[] marcado) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Invariantes.valor()");
    }

    /**
     * El vector característico de una secuencia: cuántas veces aparece cada transición. Por ejemplo,
     * con 4 transiciones, la secuencia {@code [0, 1, 0, 2]} da {@code [2, 1, 1, 0]}.
     *
     * @param secuencia    las transiciones disparadas, en orden
     * @param transiciones cuántas transiciones tiene la red
     * @return una cuenta por transición
     * @throws IllegalArgumentException si la secuencia tiene una transición que no existe
     */
    public static int[] vectorCaracteristico(int[] secuencia, int transiciones) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Invariantes.vectorCaracteristico()");
    }

    /**
     * Dispara la secuencia en la red, en orden, y dice si la red volvió al marcado del que partió. Si
     * alguna transición no está sensibilizada cuando le toca, el {@code IllegalStateException} de
     * {@code disparar} sale tal cual: la secuencia no se puede disparar.
     *
     * <p>Una secuencia repetitiva tiene como vector característico un T-invariante. La recíproca no
     * vale: que {@code C · x = 0} no garantiza que exista una secuencia disparable con ese vector.
     *
     * @param red       la red (la modifica)
     * @param secuencia las transiciones a disparar, en orden
     * @return {@code true} si al final el marcado es igual al inicial
     */
    public static boolean esRepetitiva(RedDePetri red, int[] secuencia) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Invariantes.esRepetitiva()");
    }
}
