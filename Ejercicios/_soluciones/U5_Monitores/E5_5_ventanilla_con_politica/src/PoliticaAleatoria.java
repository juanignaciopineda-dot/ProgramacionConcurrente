import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Política de ejemplo, ya hecha: elige al azar entre las colas con gente. Es la política "random" del
 * TP final. Sirve de modelo para las otras dos.
 */
public class PoliticaAleatoria implements Politica {

    private final Random azar;

    /**
     * Crea la política.
     *
     * @param semilla semilla del generador (con la misma semilla, las mismas elecciones)
     */
    public PoliticaAleatoria(long semilla) {
        this.azar = new Random(semilla);
    }

    @Override
    public int elegir(boolean[] conGente) {
        List<Integer> candidatas = new ArrayList<>();
        for (int i = 0; i < conGente.length; i++) {
            if (conGente[i]) {
                candidatas.add(i);
            }
        }
        return candidatas.get(azar.nextInt(candidatas.size()));
    }
}
