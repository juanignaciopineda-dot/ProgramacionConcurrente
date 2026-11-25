import java.util.Optional;

/**
 * Etapa intermedia del pipeline: saca pedidos de una cola, los empaqueta y pone los paquetes en otra
 * cola.
 *
 * <pre>  colaPedidos ──"pedido-7"──► [Empaquetador] ──"paquete-7"──► colaPaquetes</pre>
 */
public class Empaquetador implements Runnable {

    /** De dónde saca los pedidos. */
    private final ColaCerrable<String> entrada;

    /** Dónde deja los paquetes. */
    private final ColaCerrable<String> salida;

    /**
     * Crea un empaquetador. Este constructor ya está hecho.
     *
     * @param entrada cola de pedidos
     * @param salida cola de paquetes
     */
    public Empaquetador(ColaCerrable<String> entrada, ColaCerrable<String> salida) {
        this.entrada = entrada;
        this.salida = salida;
    }

    /**
     * Repite hasta que la cola de entrada avise que se terminó:
     *
     * <ol>
     *   <li>sacar un pedido de {@link #entrada};
     *   <li>si vino {@link Optional#empty()}, terminar;
     *   <li>si no, poner {@code empaquetar(pedido)} en {@link #salida}.
     * </ol>
     *
     * <p>El empaquetador <b>no</b> cierra la cola de salida: hay varios empaquetadores, y si el primero
     * que termina la cerrara, los otros no podrían poner los paquetes que les quedan. Quién la cierra,
     * y cuándo, lo resuelve {@link Pipeline#ejecutar(int, int, int)}.
     *
     * <p>Si lo interrumpen, restaurá la interrupción y terminá.
     */
    @Override
    public void run() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Empaquetador.run()");
    }

    /**
     * Convierte un pedido en su paquete: {@code "pedido-7"} → {@code "paquete-7"}. Ya está hecho.
     *
     * @param pedido el pedido, de la forma {@code "pedido-N"}
     * @return el paquete, de la forma {@code "paquete-N"}
     */
    public static String empaquetar(String pedido) {
        return pedido.replace("pedido-", "paquete-");
    }
}
