/**
 * Un corredor de la {@link Carrera}. Recorre las etapas en orden y, al final de cada una, espera a
 * los demás. Puede abandonar al llegar a una etapa determinada.
 */
public class Corredor implements Runnable {

    private final String nombre;
    private final Carrera carrera;

    /** La etapa en la que abandona, o {@code -1} si corre todas. */
    private final int abandonaEn;

    /**
     * Crea un corredor. Ya está hecho.
     *
     * @param nombre     nombre del corredor
     * @param carrera    la carrera
     * @param abandonaEn etapa en la que abandona (antes de correrla), o {@code -1} para correr todas
     */
    public Corredor(String nombre, Carrera carrera, int abandonaEn) {
        this.nombre = nombre;
        this.carrera = carrera;
        this.abandonaEn = abandonaEn;
    }

    /**
     * Para cada etapa, de 0 a {@code carrera.getEtapas() - 1}:
     *
     * <ol>
     *   <li>Si es la etapa en la que abandona: {@code carrera.abandonar(nombre, etapa)} y terminar el
     *       {@code run()}.
     *   <li>Si no: {@code carrera.correrEtapa(nombre, etapa)} y después
     *       {@code carrera.llegarAlFinDeEtapa()}.
     * </ol>
     */
    @Override
    public void run() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Corredor.run()");
    }
}
