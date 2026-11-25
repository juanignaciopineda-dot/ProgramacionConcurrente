import java.util.concurrent.Semaphore;

/**
 * Una variable de condición hecha con un semáforo, como las colas del monitor del TP final: un
 * semáforo que arranca en 0 (los hilos se duermen haciendo {@code acquire()}) y un contador de cuántos
 * duermen ahí.
 *
 * <p>Pensada para un monitor <i>signal and exit</i>: el que despierta a otro le <b>pasa el mutex en
 * mano</b>. No lo suelta, y el despertado sigue como si lo hubiera tomado él. Así nadie de la cola de
 * entrada se puede meter en el medio, y el despertado encuentra el estado tal como se lo dejaron.
 *
 * <p>Todos los métodos se llaman <b>con el mutex del monitor tomado</b>: ese mutex es el que protege
 * el contador, así que esta clase no necesita sincronizar nada por su cuenta.
 */
public class ColaDeCondicion {

    /** Donde duermen los hilos. Arranca sin permisos, y es justo: despierta en orden de llegada. */
    private final Semaphore dormidos = new Semaphore(0, true);

    /** Cuántos hilos duermen en esta cola. Lo protege el mutex del monitor. */
    private int esperando = 0;

    /**
     * El hilo que llama (que tiene el mutex) se duerme en esta cola hasta que otro lo despierte.
     * Vuelve <b>con el mutex</b>, porque se lo pasa el que lo despertó.
     *
     * <pre>
     *   esperando++                       ← primero se anota, todavía con el mutex
     *   mutex.release()                   ← suelta el monitor
     *   dormidos.acquireUninterruptibly() ← duerme hasta que alguien haga despertarUno()
     * </pre>
     *
     * <p>El orden importa. Si soltara el mutex antes de anotarse, otro hilo podría entrar, ver
     * {@code esperando == 0} y no despertar a nadie: un aviso perdido.
     *
     * @param mutex el mutex del monitor, tomado por el hilo que llama
     */
    public void esperar(Semaphore mutex) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("ColaDeCondicion.esperar()");
    }

    /**
     * Si hay alguien esperando, despierta al primero y le pasa el mutex.
     *
     * <ul>
     *   <li>Si {@code esperando == 0}: devolvé {@code false} sin tocar el semáforo. Si hicieras
     *       {@code release()} igual, el permiso quedaría guardado y el próximo que llame a
     *       {@link #esperar(Semaphore)} pasaría de largo.
     *   <li>Si no: {@code esperando--}, {@code dormidos.release()} y devolvé {@code true}.
     * </ul>
     *
     * <p>Quien llama mira el resultado: si es {@code true}, le pasó el mutex al despertado y <b>no lo
     * tiene que soltar</b>. Si es {@code false}, nadie lo recibió, y lo tiene que soltar él.
     *
     * @return {@code true} si despertó a alguien (y le pasó el mutex)
     */
    public boolean despertarUno() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("ColaDeCondicion.despertarUno()");
    }

    /**
     * Ya está hecho. Se llama con el mutex del monitor tomado.
     *
     * @return cuántos hilos duermen en esta cola
     */
    public int esperando() {
        return esperando;
    }
}
