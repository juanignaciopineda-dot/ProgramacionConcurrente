import java.util.List;

/**
 * Un participante de la ronda: en cada vuelta espera su turno, anota su marca en un registro
 * compartido y pasa el turno.
 *
 * <p>El registro es una {@link List} común, <b>sin sincronizar</b>, y la escriben todos los
 * participantes. ¿Por qué no hay condición de carrera? Respondelo en la pregunta 3 del README.
 */
public class Participante implements Runnable {

    /** Id del participante en el turnero. */
    private final int id;

    /** Marca que deja en el registro en cada turno (por ejemplo, "A"). */
    private final String marca;

    /** Cuántas veces actúa. */
    private final int rondas;

    /** Turnero compartido por todos los participantes. */
    private final Turnero turnero;

    /** Registro compartido donde cada participante deja su marca. */
    private final List<String> registro;

    /**
     * Crea un participante. Este constructor ya está hecho.
     *
     * @param id id en el turnero
     * @param marca lo que anota en cada turno
     * @param rondas cuántas veces actúa
     * @param turnero turnero compartido
     * @param registro lista compartida donde se anotan las marcas
     */
    public Participante(int id, String marca, int rondas, Turnero turnero, List<String> registro) {
        this.id = id;
        this.marca = marca;
        this.rondas = rondas;
        this.turnero = turnero;
        this.registro = registro;
    }

    /**
     * Repite {@link #rondas} veces: esperar el turno, agregar {@link #marca} al registro y pasar el
     * turno.
     *
     * <p>Si lo interrumpen mientras espera, restaurá la interrupción y terminá. Ojo: si se va sin
     * pasar el turno, los demás se quedan esperando para siempre. En este ejercicio no pasa porque
     * nadie lo interrumpe, pero es el tipo de problema que aparece en los TPs al terminar.
     */
    @Override
    public void run() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Participante.run()");
    }
}
