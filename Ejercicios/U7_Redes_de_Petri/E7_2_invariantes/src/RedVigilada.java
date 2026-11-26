/**
 * Una red que controla sus P-invariantes <b>en cada disparo</b>, como la red del TP final.
 *
 * <p>Si la red está bien hecha, un P-invariante no se puede violar: lo garantiza el álgebra. Entonces,
 * ¿para qué controlarlo? Para encontrar errores de <b>implementación</b>: un disparo mal programado o
 * un marcado tocado por fuera de la red. En un programa concurrente, encontrar el error en el primer
 * disparo que lo produce vale mucho más que ver un resultado raro al final.
 */
public class RedVigilada {

    private final RedDePetri red;

    /** Un P-invariante por fila: un peso por lugar. */
    private final int[][] invariantes;

    /** El valor de cada invariante en el marcado inicial. */
    private final int[] constantes;

    /**
     * Crea la red vigilada.
     *
     * <ol>
     *   <li>Cada fila de {@code invariantes} tiene que ser un P-invariante de la red
     *       ({@link Invariantes#esPInvariante(int[][], int[])} con {@code red.getIncidencia()}). Si
     *       alguna no lo es, {@code IllegalArgumentException} con el número de fila.
     *   <li>Guardá una copia de cada fila, y su valor en el marcado actual de la red: esas son las
     *       constantes que se tienen que mantener.
     * </ol>
     *
     * @param red         la red a vigilar
     * @param invariantes un P-invariante por fila
     * @throws IllegalArgumentException si alguna fila no es un P-invariante de la red
     */
    public RedVigilada(RedDePetri red, int[][] invariantes) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("constructor de RedVigilada");
    }

    /**
     * Dispara {@code t} en la red y, después, controla cada invariante: si el valor en el marcado
     * nuevo ({@code red.getMarcado()}) no es su constante, lanza {@code IllegalStateException} diciendo
     * qué invariante se violó y qué valor dio.
     *
     * @param t la transición a disparar
     * @throws IllegalStateException si {@code t} no está sensibilizada, o si después del disparo se
     *                               violó algún invariante
     */
    public void disparar(int t) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("RedVigilada.disparar()");
    }

    /** @return una copia de las constantes de los invariantes. Ya está hecho. */
    public int[] getConstantes() {
        return constantes.clone();
    }

    /** @return la red vigilada. Ya está hecho. */
    public RedDePetri getRed() {
        return red;
    }
}
