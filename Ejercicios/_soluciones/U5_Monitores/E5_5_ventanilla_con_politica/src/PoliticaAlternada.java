/**
 * Política por turnos (<i>round robin</i>): recuerda a qué cola le tocó la última vez, y elige la
 * siguiente que tenga gente, dando la vuelta si hace falta.
 *
 * <pre>
 *   colas con gente: [sí, sí, sí]   →   0, 1, 2, 0, 1, ...
 *   colas con gente: [sí, no, sí]   →   (después del 0) 2, después 0, ...
 * </pre>
 *
 * <p>Ninguna cola con gente espera más de una vuelta, así que no hay postergación indefinida.
 */
public class PoliticaAlternada implements Politica {

    /** La cola elegida la última vez; -1 si todavía no se eligió ninguna. */
    private int ultima = -1;

    /**
     * Elige la primera cola con gente a partir de la siguiente a la última elegida, en ronda: probá
     * {@code (ultima + 1) % n}, {@code (ultima + 2) % n}, y así hasta dar la vuelta. Guardá la elegida
     * en {@code ultima} y devolvela.
     *
     * <p>La primera vez ({@code ultima == -1}) arranca por la cola 0.
     *
     * @param conGente qué colas tienen hilos esperando
     * @return el índice elegido
     */
    @Override
    public int elegir(boolean[] conGente) {
        int n = conGente.length;
        for (int paso = 1; paso <= n; paso++) {
            int i = Math.floorMod(ultima + paso, n);
            if (conGente[i]) {
                ultima = i;
                return i;
            }
        }
        throw new IllegalArgumentException("ninguna cola tiene gente");
    }
}
