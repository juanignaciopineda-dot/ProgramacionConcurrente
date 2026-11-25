import java.util.ArrayList;
import java.util.List;

/**
 * Pipeline de dos etapas concurrentes, con terminación limpia (es el esqueleto del TP1 en chiquito):
 *
 * <pre>
 *   [hilo que llama] ──pedido-i──► colaPedidos ──► empaquetador-1..E ──paquete-i──► colaPaquetes
 *                                                                          │
 *                                         despachante-1..D ◄───────────────┘
 * </pre>
 *
 * <p>Todas las etapas arrancan al principio y trabajan a la vez: no se permite terminar una etapa
 * entera antes de arrancar la siguiente.
 */
public final class Pipeline {

    private Pipeline() {}

    /**
     * Resultado de una corrida.
     *
     * @param despachados todos los paquetes que salieron del pipeline (en cualquier orden)
     * @param hilos todos los hilos que creó el pipeline (empaquetadores y despachantes)
     */
    public record Resultado(List<String> despachados, List<Thread> hilos) {}

    /**
     * Arma el pipeline, lo corre hasta que se procesen todos los pedidos y devuelve el resultado
     * <b>con todos los hilos ya terminados</b>.
     *
     * <ol>
     *   <li>Crear las dos colas.
     *   <li>Crear y <b>arrancar</b> todos los hilos de las dos etapas, antes de cargar ningún pedido:
     *       {@code empaquetadores} hilos {@code "empaquetador-1"}, {@code "empaquetador-2"}, ... con un
     *       {@link Empaquetador} cada uno, y {@code despachantes} hilos {@code "despachante-1"}, ...
     *       con un {@link Despachante} cada uno.
     *   <li>Poner {@code "pedido-1"} ... {@code "pedido-N"} en la cola de pedidos, y cerrarla.
     *   <li>Esperar ({@code join}) a todos los empaquetadores y <b>recién ahí</b> cerrar la cola de
     *       paquetes. (¿Qué pasaría si la cerraras apenas terminás de cargar los pedidos?)
     *   <li>Esperar a todos los despachantes y juntar sus listas.
     * </ol>
     *
     * <p>No hay {@code sleep}, ni {@code stop}, ni {@code System.exit}, ni banderas consultadas en un
     * bucle: cada hilo termina solo cuando su cola de entrada le avisa que se terminó.
     *
     * @param pedidos cantidad de pedidos (0 o más)
     * @param empaquetadores cantidad de hilos empaquetadores (1 o más)
     * @param despachantes cantidad de hilos despachantes (1 o más)
     * @return los paquetes despachados y los hilos usados
     * @throws InterruptedException si interrumpen al hilo que llama mientras espera
     * @throws IllegalArgumentException si alguna cantidad es inválida
     */
    public static Resultado ejecutar(int pedidos, int empaquetadores, int despachantes)
            throws InterruptedException {
        if (pedidos < 0 || empaquetadores < 1 || despachantes < 1) {
            throw new IllegalArgumentException("cantidades inválidas");
        }
        ColaCerrable<String> colaPedidos = new ColaCerrable<>();
        ColaCerrable<String> colaPaquetes = new ColaCerrable<>();

        List<Thread> etapa1 = new ArrayList<>();
        for (int i = 1; i <= empaquetadores; i++) {
            etapa1.add(new Thread(new Empaquetador(colaPedidos, colaPaquetes), "empaquetador-" + i));
        }
        List<Despachante> lista = new ArrayList<>();
        List<Thread> etapa2 = new ArrayList<>();
        for (int i = 1; i <= despachantes; i++) {
            Despachante d = new Despachante(colaPaquetes);
            lista.add(d);
            etapa2.add(new Thread(d, "despachante-" + i));
        }
        List<Thread> hilos = new ArrayList<>(etapa1);
        hilos.addAll(etapa2);
        for (Thread h : hilos) {
            h.start();
        }

        for (int i = 1; i <= pedidos; i++) {
            colaPedidos.poner("pedido-" + i);
        }
        colaPedidos.cerrar();

        for (Thread h : etapa1) {
            h.join();
        }
        colaPaquetes.cerrar();

        List<String> despachados = new ArrayList<>();
        for (int i = 0; i < etapa2.size(); i++) {
            etapa2.get(i).join();
            despachados.addAll(lista.get(i).getDespachados());
        }
        return new Resultado(despachados, hilos);
    }
}
