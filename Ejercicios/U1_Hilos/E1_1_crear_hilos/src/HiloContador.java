/**
 * Un hilo creado de la <b>otra</b> forma: heredando de {@link Thread} y sobrescribiendo
 * {@link #run()}. Suma los números de 1 a {@code hasta}.
 *
 * <p>Funciona, pero tiene dos desventajas frente a {@link Runnable}: gasta la única herencia que
 * tiene la clase (Java no tiene herencia múltiple) y mezcla la tarea con el hilo que la ejecuta. Por
 * eso en la materia se prefiere implementar {@link Runnable}, como en {@link Tarea}.
 */
public class HiloContador extends Thread {

    /** Hasta qué número sumar (inclusive). */
    private final int hasta;

    /** Resultado de la suma. Vale 0 hasta que el hilo termina. */
    private long resultado;

    /**
     * Crea el hilo, <b>sin arrancarlo</b>.
     *
     * <p>Llamá al constructor de {@link Thread} que recibe el nombre, con {@code super(nombre)}, para
     * que el hilo se llame así (tiene que ser la primera línea del constructor). Después guardá
     * {@code hasta}.
     *
     * @param nombre nombre que va a tener el hilo
     * @param hasta último número que se suma
     */
    public HiloContador(String nombre, int hasta) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("constructor de HiloContador");
    }

    /**
     * Suma {@code 1 + 2 + ... + hasta} con un bucle y guarda el total en {@link #resultado}.
     *
     * <p>Este método sobrescribe a {@link Thread#run()}. Igual que con {@link Runnable}, nunca lo
     * llames directamente: se ejecuta en el hilo nuevo cuando alguien llama a {@link #start()}.
     */
    @Override
    public void run() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("HiloContador.run()");
    }

    /**
     * Devuelve el resultado de la suma. Solo tiene sentido leerlo después de hacer {@code join()}
     * sobre este hilo.
     *
     * @return la suma de 1 a {@code hasta}
     */
    public long getResultado() {
        return resultado;
    }
}
