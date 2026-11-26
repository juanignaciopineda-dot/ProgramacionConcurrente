/**
 * El ejercicio 9 de los apuntes: emitir {@code 1} cada vez que en la entrada aparece la secuencia
 * {@code 101}, con solapamiento, y {@code 0} en cualquier otro momento. En {@code 10101} la secuencia
 * aparece dos veces: la segunda usa el último {@code 1} de la primera.
 *
 * <pre>
 *   entrada          1 0 1 0 1
 *   Mealy            0 0 1 0 1          (un símbolo por cada uno que lee)
 *   Moore          0 0 0 1 0 1          (el primero es la salida del estado inicial)
 * </pre>
 *
 * <p>Antes de escribir código, dibujá las dos máquinas. Los estados resumen qué parte de {@code 101} ya
 * se vio. El verificador acepta hasta 3 estados para Mealy y hasta 4 para Moore.
 */
public final class Detectores {

    private Detectores() {
    }

    /**
     * El detector de {@code 101} como máquina de Mealy, sobre {@code {0, 1}}. La salida va en los arcos:
     * el {@code 1} se emite en el mismo arco que lee el último {@code 1} de la secuencia.
     *
     * @return la máquina, con un arco para cada estado y cada símbolo
     */
    public static MaquinaMealy mealy101() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Detectores.mealy101()");
    }

    /**
     * El detector de {@code 101} como máquina de Moore, sobre {@code {0, 1}}. La salida va en los
     * estados, así que hace falta un estado que signifique "acabo de ver 101" y emita {@code 1}. Ojo con el
     * solapamiento: desde ese estado, ¿qué parte de la secuencia ya está vista?
     *
     * @return la máquina, con un arco para cada estado y cada símbolo, y una salida para cada estado
     */
    public static MaquinaMoore moore101() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Detectores.moore101()");
    }
}
