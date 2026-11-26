import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * El monitor de la materia: un monitor guiado por una red de Petri, sin tiempo. Cada hilo hace su
 * trabajo como una secuencia de disparos, y cada disparo pasa por acá.
 *
 * <pre>
 *   mutex       ← exclusión mutua de entrada al monitor
 *   red         ← la lógica: el marcado, qué está sensibilizado, el disparo
 *   colas[t]    ← una variable de condición por transición (relación biunívoca)
 *   politica    ← entre las que se pueden despertar, cuál
 * </pre>
 *
 * <p>La sincronización está toda en la red. Los hilos no saben nada de mutex ni de colas: solo saben
 * qué transiciones disparan. En la red de {@link ProductorConsumidor}, un productor dispara
 * TOMAR_HUECO y DEPOSITAR. Si el buffer está lleno, TOMAR_HUECO no está sensibilizada y el productor
 * espera en la cola de esa transición sin escribir una línea de control.
 *
 * <p>La política de señalización es <i>signal and exit</i>, como en el ejercicio 5.3: el que dispara y
 * encuentra a alguien para despertar le pasa el mutex y sale sin soltarlo.
 *
 * <p>Herramientas permitidas: {@code Semaphore}, la {@link ColaDeCondicion}, la {@link RedDePetri} y la
 * {@link Politica}. Nada de {@code synchronized}, locks ni {@code wait}/{@code notify}.
 */
public class MonitorDeRed {

    private final Semaphore mutex = new Semaphore(1, true);
    private final RedDePetri red;
    private final ColaDeCondicion[] colas;
    private final Politica politica;

    /** Para el verificador: cuántas veces se disparó cada transición, y en qué orden. Los protege el mutex. */
    private final int[] disparos;
    private final List<Integer> historial = new ArrayList<>();

    /**
     * Crea el monitor: guardá la red y la política, y creá una cola por transición.
     *
     * @param red      la red que guía al monitor
     * @param politica a qué transición despertar cuando hay varias posibles
     * @throws IllegalArgumentException si alguno es {@code null}
     */
    public MonitorDeRed(RedDePetri red, Politica politica) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("constructor de MonitorDeRed");
    }

    /**
     * Dispara {@code t}, esperando lo que haga falta a que esté sensibilizada. Es el algoritmo de los
     * apuntes ("Un monitor guiado por una red de Petri"):
     *
     * <pre>
     *   mutex.acquireUninterruptibly()
     *   while (!red.estaSensibilizada(t))
     *       colas[t].esperar(mutex)                 ← vuelve con el mutex: se lo pasó el que lo despertó
     *   red.disparar(t)
     *   registrarDisparo(t)
     *   despertables[u] = red.estaSensibilizada(u) &amp;&amp; colas[u].esperando() &gt; 0   (para cada u)
     *   si hay alguna despertable:
     *       colas[politica.elegir(despertables)].despertarUno()   ← le paso el mutex: NO lo suelto
     *   si no:
     *       mutex.release()
     * </pre>
     *
     * <p>El despertado vuelve al {@code while}, ya con el mutex. Como solo se despierta a hilos cuya
     * transición está sensibilizada, y el mutex le llega en mano, la va a encontrar sensibilizada.
     *
     * @param t la transición a disparar
     * @throws IllegalArgumentException si {@code t} no existe
     */
    public void dispararTransicion(int t) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("MonitorDeRed.dispararTransicion()");
    }

    /** Ya está hecho: anota el disparo. Se llama con el mutex tomado, justo después de disparar. */
    private void registrarDisparo(int t) {
        disparos[t]++;
        historial.add(t);
    }

    /** @return cuántas veces se disparó cada transición. Ya está hecho. */
    public int[] getDisparos() {
        mutex.acquireUninterruptibly();
        try {
            return disparos.clone();
        } finally {
            mutex.release();
        }
    }

    /** @return las transiciones disparadas, en orden. Ya está hecho. */
    public List<Integer> getHistorial() {
        mutex.acquireUninterruptibly();
        try {
            return new ArrayList<>(historial);
        } finally {
            mutex.release();
        }
    }

    /** @return el marcado actual de la red. Ya está hecho. */
    public int[] getMarcado() {
        mutex.acquireUninterruptibly();
        try {
            return red.getMarcado();
        } finally {
            mutex.release();
        }
    }

    /**
     * Ya está hecho.
     *
     * @param t una transición
     * @return cuántos hilos esperan para dispararla
     */
    public int esperando(int t) {
        mutex.acquireUninterruptibly();
        try {
            return colas[t].esperando();
        } finally {
            mutex.release();
        }
    }
}
