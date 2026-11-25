import java.util.concurrent.Semaphore;

/**
 * Un monitor genérico con la política <b>signal and urgent wait</b> (SU), la de Hoare (1974), hecho
 * con semáforos. Tiene tres tipos de cola:
 *
 * <pre>
 *   mutex      ← cola de ENTRADA: los que llaman a entrar() mientras hay alguien adentro
 *   cortesia   ← cola de CORTESÍA (urgente): los que hicieron señalar() y le cedieron el monitor
 *                al despertado. Tiene prioridad sobre la de entrada
 *   cada Condicion tiene su cola: los que hicieron esperar()
 * </pre>
 *
 * <p>Cuando un hilo señaliza y hay alguien esperando, el despertado entra <b>enseguida</b> y el que
 * señalizó espera en la cola de cortesía. El objetivo, con las palabras de la cátedra: "ceder el
 * tiempo de procesamiento restante del hilo activo al hilo despertado, por encontrarse urgido".
 * Cuando el despertado sale (o se vuelve a dormir), el monitor pasa primero al que está en cortesía y
 * después a los de la entrada.
 *
 * <p>Una regla resume el monitor: <b>quien deja el monitor se lo pasa a la cola de cortesía si hay
 * alguien ahí; si no, lo libera para la cola de entrada</b>. "Dejar el monitor" es salir o dormirse en
 * una condición.
 *
 * <p>Como el despertado entra sin que nadie se le cuele, la condición por la que esperaba sigue
 * siendo cierta, y en el código que usa el monitor alcanza con {@code if}.
 *
 * <p>Todos los semáforos son justos (FIFO). Los contadores ({@code enCortesia} y el {@code esperando}
 * de cada condición) los toca solo quien tiene el monitor, así que no hace falta otra protección.
 *
 * <p>Herramientas permitidas: {@code Semaphore}. Nada de {@code synchronized}, locks,
 * {@code Condition} de Java ni {@code wait}/{@code notify}.
 */
public class MonitorHoare {

    /** Cola de entrada del monitor. */
    private final Semaphore mutex = new Semaphore(1, true);

    /** Cola de cortesía: los que señalizaron y esperan para seguir. */
    private final Semaphore cortesia = new Semaphore(0, true);

    /** Cuántos hilos hay en la cola de cortesía. Lo toca solo quien tiene el monitor. */
    private int enCortesia = 0;

    /**
     * Entra al monitor: espera en la cola de entrada hasta que esté libre
     * ({@code mutex.acquireUninterruptibly()}).
     */
    public void entrar() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("MonitorHoare.entrar()");
    }

    /**
     * Sale del monitor y se lo pasa al que corresponde: {@link #cederMonitor()}.
     */
    public void salir() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("MonitorHoare.salir()");
    }

    /**
     * Deja el monitor: si hay alguien en la cola de cortesía, se lo pasa ({@code cortesia.release()});
     * si no, lo libera para la cola de entrada ({@code mutex.release()}). Lo usan {@link #salir()} y
     * {@link Condicion#esperar()}.
     */
    private void cederMonitor() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("MonitorHoare.cederMonitor()");
    }

    /**
     * Crea una variable de condición de este monitor. Ya está hecho.
     *
     * @return la condición nueva
     */
    public Condicion nuevaCondicion() {
        return new Condicion();
    }

    /**
     * Ya está hecho. Se llama con el monitor tomado.
     *
     * @return cuántos hilos esperan en la cola de cortesía
     */
    public int enCortesia() {
        return enCortesia;
    }

    /**
     * Una variable de condición del monitor: una cola (un semáforo en 0) y cuántos esperan en ella.
     * Es una clase interna porque necesita tocar el estado del monitor que la creó
     * ({@code enCortesia}, {@code cortesia}, {@code cederMonitor()}).
     */
    public class Condicion {

        /** Donde duermen los que esperan esta condición. */
        private final Semaphore cola = new Semaphore(0, true);

        /** Cuántos esperan esta condición. Lo toca solo quien tiene el monitor. */
        private int esperando = 0;

        /**
         * El hilo (que está adentro del monitor) se duerme hasta que alguien señalice esta condición.
         *
         * <pre>
         *   esperando++
         *   cederMonitor()                 ← deja el monitor: a cortesía si hay alguien, si no a la entrada
         *   cola.acquireUninterruptibly()  ← duerme
         *   // vuelve adentro del monitor: se lo pasó el que señalizó
         * </pre>
         */
        public void esperar() {
            // TODO: implementalo siguiendo el Javadoc de arriba.
            throw new UnsupportedOperationException("Condicion.esperar()");
        }

        /**
         * Si hay alguien esperando esta condición, le cede el monitor y espera en la cola de cortesía
         * hasta que se lo devuelvan. Si no hay nadie, no hace nada.
         *
         * <pre>
         *   if (esperando &gt; 0) {
         *       esperando--
         *       enCortesia++
         *       cola.release()                    ← despierta al primero: ahora el monitor es suyo
         *       cortesia.acquireUninterruptibly() ← espera, con prioridad sobre la entrada
         *       enCortesia--                      ← volvió: el monitor es suyo otra vez
         *   }
         * </pre>
         */
        public void senalar() {
            // TODO: implementalo siguiendo el Javadoc de arriba.
            throw new UnsupportedOperationException("Condicion.senalar()");
        }

        /**
         * Ya está hecho. Se llama con el monitor tomado.
         *
         * @return cuántos hilos esperan esta condición
         */
        public int esperando() {
            return esperando;
        }
    }
}
