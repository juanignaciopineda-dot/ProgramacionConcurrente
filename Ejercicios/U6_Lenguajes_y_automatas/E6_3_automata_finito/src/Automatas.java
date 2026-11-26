import java.util.Set;

/**
 * Autómatas para diseñar. El primero es el ejemplo resuelto de los apuntes y ya está hecho: mirá cómo se
 * arma y hacé los otros tres. El verificador los prueba con todas las cadenas hasta cierto largo.
 *
 * <p>La idea que hace falta para diseñarlos: el estado es un <b>resumen</b> de lo que se leyó hasta ahora,
 * con la información justa para decidir lo que viene. Antes de dibujar, preguntate qué tenés que recordar
 * de la historia (¿la paridad de algo?, ¿el último símbolo?, ¿un resto?).
 */
public final class Automatas {

    private Automatas() {
    }

    /**
     * Ya está hecho: el autómata {@code M3D} de los apuntes, para
     * {@code L3 = { x c^(3m) | x ∈ {a, b}*, la cantidad de b en x es par, m ≥ 0 }}.
     *
     * <p>{@code e0} y {@code e1} cuentan la paridad de las {@code b}; {@code e2 → e3 → e4} cuentan las
     * {@code c} módulo 3. Los finales son {@code e0} y {@code e4}.
     *
     * @return el autómata de L3
     */
    public static AFD l3() {
        AFD m = new AFD("e0", Set.of("e0", "e4"));
        m.agregarTransicion("e0", 'a', "e0");
        m.agregarTransicion("e0", 'b', "e1");
        m.agregarTransicion("e1", 'a', "e1");
        m.agregarTransicion("e1", 'b', "e0");
        m.agregarTransicion("e0", 'c', "e2");
        m.agregarTransicion("e2", 'c', "e3");
        m.agregarTransicion("e3", 'c', "e4");
        m.agregarTransicion("e4", 'c', "e2");
        return m;
    }

    /**
     * Las cadenas sobre {@code {a, b}} con una cantidad <b>par</b> de {@code b} (cero incluido, así que λ
     * está). Es el ejercicio 1.a de los apuntes: alcanza con <b>dos</b> estados, y el verificador no acepta
     * más.
     *
     * @return el autómata
     */
    public static AFD parDeB() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Automatas.parDeB()");
    }

    /**
     * {@code L4 = { w ∈ {0, 1}* | w contiene 00 ó 11 }}, el ejercicio 2.a de los apuntes. Pensá qué hay
     * que recordar mientras todavía no apareció ninguno de los dos pares.
     *
     * @return el autómata
     */
    public static AFD contiene00o11() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Automatas.contiene00o11()");
    }

    /**
     * Los números binarios <b>no vacíos</b> que son múltiplo de 3, leídos de izquierda a derecha (el
     * dígito más significativo primero). Se permiten ceros a la izquierda: {@code "0"}, {@code "11"} y
     * {@code "0110"} están; λ, {@code "1"} y {@code "100"} no.
     *
     * <p>No se puede recordar el número entero, porque puede ser tan largo como se quiera. Lo que sí se
     * puede recordar es su <b>resto</b> al dividirlo por 3. Si hasta ahora el número vale {@code n} y se
     * lee el dígito {@code d}, pasa a valer {@code 2n + d}, así que el resto nuevo es
     * {@code (2·resto + d) mod 3}. El verificador acepta hasta cuatro estados (¿por qué no alcanza con
     * tres, si los restos son tres?).
     *
     * @return el autómata
     */
    public static AFD multiploDe3() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Automatas.multiploDe3()");
    }
}
