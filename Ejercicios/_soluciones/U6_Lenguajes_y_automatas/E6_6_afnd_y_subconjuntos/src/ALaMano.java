/**
 * El camino inverso, del autómata a la gramática, hecho a mano: aplicá el algoritmo AF → gramática
 * regular de los apuntes y escribí acá las producciones que te dieron. El verificador comprueba que la
 * gramática sea de tipo 3 en la forma estricta y que genere el mismo lenguaje que el autómata.
 *
 * <p>El algoritmo, con los casos borde que más se olvidan:
 *
 * <ol>
 *   <li>Al estado inicial le corresponde el símbolo distinguido {@code S}.
 *   <li>A cada otro estado, un no terminal ({@code A}, {@code B}, {@code C}...), salvo a los finales de
 *       los que no sale ningún arco. Si al estado inicial le llega algún arco, le corresponde
 *       <b>también</b> un no terminal propio.
 *   <li>Por cada arco {@code f(ei, a) = ej}: {@code Xi → aXj}. Si {@code ej} es final, además
 *       {@code Xi → a}. Si {@code ej} es el inicial, se usa su no terminal, nunca {@code S}. Y si
 *       {@code ei} es el inicial con dos símbolos, el arco da producciones para los dos: {@code S} y su
 *       no terminal.
 *   <li>Si el estado inicial es también final, {@code S → λ}.
 * </ol>
 *
 * <p>La forma estricta: la única producción con λ puede ser {@code S → λ}, y {@code S} no aparece nunca a
 * la derecha. Las producciones se escriben como en el 6.2: {@code "S -> aA | bB | λ"}.
 */
public final class ALaMano {

    private ALaMano() {
    }

    /**
     * El ejercicio 1.c de los apuntes: la gramática del autómata {@code M3D} de L3 ({@code e0} a {@code e4},
     * con finales {@code e0} y {@code e4}; está en {@code Automatas.l3()} del 6.3). Cuidado: el inicial es
     * también final, y le entran arcos.
     */
    public static final String[] L3 = {
        "S -> aA | a | bB | cC | λ",
        "A -> aA | a | bB | cC",
        "B -> aB | bA | b",
        "C -> cD",
        "D -> cE | c",
        "E -> cC",
    };

    /**
     * El ejercicio 2.b de los apuntes: la gramática del autómata de L4 (las cadenas de {@code {0, 1}} que
     * contienen {@code 00} ó {@code 11}). Usá el autómata que diseñaste en el 6.3.
     */
    public static final String[] L4 = {
        "S -> 0A | 1B",
        "A -> 0C | 0 | 1B",
        "B -> 1C | 1 | 0A",
        "C -> 0C | 1C | 0 | 1",
    };
}
