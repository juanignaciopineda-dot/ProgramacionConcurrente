/**
 * La política más simple, ya hecha: la despertable de índice más chico. Es una prioridad fija por
 * número de transición.
 */
public class PoliticaPrimera implements Politica {

    @Override
    public int elegir(boolean[] despertables) {
        for (int t = 0; t < despertables.length; t++) {
            if (despertables[t]) {
                return t;
            }
        }
        throw new IllegalArgumentException("no hay ninguna transición despertable");
    }
}
