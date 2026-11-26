import java.util.concurrent.BlockingQueue;

/**
 * La máquina de Mealy como <b>sistema reactivo</b>: el entorno manda los eventos de a uno, al ritmo que
 * quiere, y el detector reacciona a cada uno apenas llega.
 *
 * <pre>
 *   entorno ──eventos──► [ DetectorReactivo: la máquina de Mealy ] ──salidas──► entorno
 *             (cola)                                                 (cola)
 * </pre>
 *
 * <p>Es la arquitectura dirigida por eventos de los apuntes: el consumidor es responsable de reaccionar
 * tan pronto como el evento está presente. No junta los eventos para procesarlos al final ni pregunta en un
 * bucle si llegó algo: se bloquea en la cola hasta que llega el próximo evento.
 *
 * <p>Herramientas permitidas: los métodos de {@code BlockingQueue}. Nada de {@code sleep} ni
 * {@code synchronized}.
 */
public class DetectorReactivo implements Runnable {

    /** El evento que indica que el entorno terminó. */
    public static final char FIN = '#';

    private final MaquinaMealy maquina;
    private final BlockingQueue<Character> eventos;
    private final BlockingQueue<Character> salidas;

    /**
     * Ya está hecho.
     *
     * @param maquina la máquina de Mealy que decide qué emitir
     * @param eventos de donde llegan los eventos
     * @param salidas adonde van las salidas, una por evento
     */
    public DetectorReactivo(MaquinaMealy maquina, BlockingQueue<Character> eventos, BlockingQueue<Character> salidas) {
        this.maquina = maquina;
        this.eventos = eventos;
        this.salidas = salidas;
    }

    /**
     * El ciclo de vida del detector:
     *
     * <pre>
     *   estado ← el inicial de la máquina
     *   repetir:
     *       e ← eventos.take()                   (se bloquea hasta que llega uno)
     *       si e es FIN: terminar
     *       t ← maquina.transicion(estado, e)
     *       si no hay arco para e: ignorarlo (ni cambia de estado ni emite)
     *       si no: salidas.put(t.salida()) y estado ← t.hacia()
     * </pre>
     *
     * <p>Si lo interrumpen mientras espera, termina, y antes vuelve a marcar la interrupción con
     * {@code Thread.currentThread().interrupt()} (como en el ejercicio 1.4).
     */
    @Override
    public void run() {
        String estado = maquina.inicial();
        try {
            while (true) {
                char e = eventos.take();
                if (e == FIN) {
                    return;
                }
                MaquinaMealy.Transicion t = maquina.transicion(estado, e);
                if (t != null) {
                    salidas.put(t.salida());
                    estado = t.hacia();
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
