import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Última etapa del pipeline: saca paquetes de una cola y los "despacha", anotándolos en su
 * <b>propia</b> lista.
 *
 * <p>Cada despachante tiene su lista y nadie más la toca mientras corre. El hilo principal las lee
 * todas recién después de hacer {@code join()}: así no hace falta ninguna lista compartida ni
 * sincronizada. Mientras menos estado compartido, menos que proteger.
 */
public class Despachante implements Runnable {

    /** De dónde saca los paquetes. */
    private final ColaCerrable<String> entrada;

    /** Paquetes que despachó este despachante, en orden. */
    private final List<String> despachados = new ArrayList<>();

    /**
     * Crea un despachante. Este constructor ya está hecho.
     *
     * @param entrada cola de paquetes
     */
    public Despachante(ColaCerrable<String> entrada) {
        this.entrada = entrada;
    }

    /**
     * Repite: sacar un paquete de {@link #entrada}; si vino {@link Optional#empty()}, terminar; si
     * no, agregarlo a {@link #despachados}.
     *
     * <p>Si lo interrumpen, restaurá la interrupción y terminá.
     */
    @Override
    public void run() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Despachante.run()");
    }

    /**
     * Devuelve los paquetes despachados. Llamalo solo después de hacer {@code join()} sobre el hilo
     * de este despachante.
     *
     * @return la lista de paquetes despachados
     */
    public List<String> getDespachados() {
        return despachados;
    }
}
