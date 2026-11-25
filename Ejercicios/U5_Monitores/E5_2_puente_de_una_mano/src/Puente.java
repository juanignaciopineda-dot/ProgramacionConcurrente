import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Un puente angosto de una sola mano: pueden cruzar varios autos a la vez, pero <b>todos en el mismo
 * sentido</b>. El que llega del otro lado espera a que el puente se vacíe.
 *
 * <pre>
 *   NORTE ◄── ▓▓▓ ▓▓▓ ▓▓▓ ──    (cruzan juntos)        SUR ──► espera
 * </pre>
 *
 * <p>Es un monitor: un candado y una variable de condición por sentido. El estado son tres números
 * y el sentido actual, y de ese estado salen las condiciones para entrar. Si no se cumplen, el auto
 * espera en la cola de su sentido.
 *
 * <p>La versión que deja entrar siempre a los del sentido que está cruzando tiene un problema: si
 * del norte llegan autos sin parar, el puente nunca se vacía y el que espera en el sur no cruza nunca.
 * Es <b>postergación indefinida</b> (inanición), y no es un deadlock, porque el sistema avanza. Para
 * evitarla, este puente cede el turno: después de {@code maxSeguidos} autos seguidos en un sentido,
 * si hay alguien esperando del otro lado, no entra nadie más en ese sentido hasta que crucen los del
 * otro.
 *
 * <p>Herramientas permitidas: {@code ReentrantLock} y {@code Condition}. Nada de {@code synchronized},
 * {@code wait}/{@code notify} ni semáforos.
 */
public class Puente {

    /** La exclusión mutua del monitor. */
    private final ReentrantLock candado = new ReentrantLock();

    /** Una cola por sentido: ahí esperan los autos que no pueden entrar. */
    private final Map<Sentido, Condition> colas = new EnumMap<>(Sentido.class);

    /** Cuántos autos esperan en cada sentido. */
    private final Map<Sentido, Integer> esperando = new EnumMap<>(Sentido.class);

    /** Cuántos autos puede dejar entrar seguidos un sentido si del otro lado hay gente esperando. */
    private final int maxSeguidos;

    /** El sentido de los autos que están cruzando o, si el puente está vacío, el del último que cruzó. */
    private Sentido sentido = null;

    /** Cuántos autos hay sobre el puente. */
    private int enElPuente = 0;

    /** Cuántos autos entraron seguidos en el sentido actual (desde que el puente cambió de sentido). */
    private int seguidos = 0;

    /**
     * Crea el puente vacío.
     *
     * <p>Validá {@code maxSeguidos}, y para cada sentido creá su cola ({@code candado.newCondition()})
     * y poné {@code esperando} en 0.
     *
     * @param maxSeguidos cuántos autos seguidos en un sentido, como máximo, si del otro lado esperan
     * @throws IllegalArgumentException si {@code maxSeguidos < 1}
     */
    public Puente(int maxSeguidos) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("constructor de Puente");
    }

    /**
     * La condición para que un auto en sentido {@code s} entre al puente. Se llama con el candado
     * tomado.
     *
     * <ul>
     *   <li><b>Puente vacío</b>: puede entrar si del otro lado no espera nadie, o si el último que cruzó
     *       fue del otro sentido (así, si esperan de los dos lados, se alternan).
     *   <li><b>Puente ocupado</b>: puede entrar si los que cruzan van en su mismo sentido y, además,
     *       todavía no se llegó a {@code maxSeguidos} o del otro lado no espera nadie.
     * </ul>
     *
     * @param s el sentido del auto
     * @return {@code true} si puede entrar ahora
     */
    private boolean puedeEntrar(Sentido s) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Puente.puedeEntrar()");
    }

    /**
     * Un auto quiere cruzar en sentido {@code s}: espera hasta que pueda y entra.
     *
     * <pre>
     *   candado.lock();
     *   try {
     *       esperando[s]++
     *       try {
     *           while (!puedeEntrar(s)) colas[s].await();
     *       } finally {
     *           esperando[s]--                ← también si lo interrumpen mientras espera
     *       }
     *       si el puente estaba vacío: sentido = s y seguidos = 0
     *       enElPuente++ y seguidos++
     *   } finally {
     *       candado.unlock();
     *   }
     * </pre>
     *
     * <p>Fijate que el auto se cuenta en {@code esperando} <b>antes</b> de mirar la condición. Así, si
     * no puede entrar, los de su sentido que están cruzando ya saben que alguien espera del otro lado.
     *
     * @param s el sentido del auto
     * @throws InterruptedException si interrumpen al auto mientras espera
     */
    public void entrar(Sentido s) throws InterruptedException {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Puente.entrar()");
    }

    /**
     * El auto que iba en sentido {@code s} termina de cruzar.
     *
     * <ol>
     *   <li>Si el puente está vacío o los que cruzan no van en sentido {@code s}:
     *       {@code IllegalStateException}.
     *   <li>{@code enElPuente--}.
     *   <li>Si el puente quedó vacío, despertá a <b>todos</b> los que esperan, de los dos sentidos
     *       ({@code signalAll()} en las dos colas). Cada uno vuelve a mirar {@code puedeEntrar}, y
     *       entran los que correspondan.
     * </ol>
     *
     * <p>¿Por qué {@code signalAll} y no {@code signal}? Porque cuando cambia el sentido pueden entrar
     * <b>varios</b> autos a la vez, todos los que esperaban del otro lado.
     *
     * @param s el sentido del auto que sale
     * @throws IllegalStateException si no hay autos cruzando en sentido {@code s}
     */
    public void salir(Sentido s) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Puente.salir()");
    }

    /**
     * Ya está hecho.
     *
     * @return cuántos autos hay sobre el puente ahora
     */
    public int enElPuente() {
        candado.lock();
        try {
            return enElPuente;
        } finally {
            candado.unlock();
        }
    }

    /**
     * Ya está hecho.
     *
     * @return el sentido de los que cruzan ahora, o {@code null} si el puente está vacío
     */
    public Sentido sentidoActual() {
        candado.lock();
        try {
            return enElPuente == 0 ? null : sentido;
        } finally {
            candado.unlock();
        }
    }

    /**
     * Ya está hecho.
     *
     * @param s un sentido
     * @return cuántos autos esperan para cruzar en ese sentido
     */
    public int esperando(Sentido s) {
        candado.lock();
        try {
            return esperando.get(s);
        } finally {
            candado.unlock();
        }
    }
}
