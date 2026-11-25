import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Una carrera por etapas: nadie empieza la etapa {@code k + 1} hasta que todos los que siguen en
 * carrera terminaron la etapa {@code k}. Un corredor puede abandonar, y entonces los demás ya no lo
 * esperan.
 *
 * <pre>
 *   etapa 0:  c0 ████   c1 ██████   c2 ██        ─┐
 *                                                  ├─ barrera: esperan al último
 *   etapa 1:  c0 ███    c1 █        c2 █████     ─┘ ...
 * </pre>
 *
 * <p>Es una <b>barrera que se repite</b>, una vez por etapa, y eso hace un {@link Phaser}. Cada
 * "fase" del phaser es una etapa:
 *
 * <ul>
 *   <li>{@code new Phaser(n)}: arranca con {@code n} participantes registrados.
 *   <li>{@code arriveAndAwaitAdvance()}: "llegué al final de la fase, espero a los demás". Cuando llega
 *       el último, la fase avanza y se despiertan todos.
 *   <li>{@code arriveAndDeregister()}: "llegué y me voy". Cuenta como llegada para esta fase y, desde
 *       la siguiente, el phaser espera a uno menos.
 *   <li>{@code onAdvance(fase, registrados)}: lo ejecuta el último en llegar, justo antes de que avance
 *       la fase. Si devuelve {@code true}, el phaser termina.
 * </ul>
 *
 * <p>Un {@code CountDownLatch} no sirve acá porque es de un solo uso. Un {@code CyclicBarrier} se
 * puede repetir, pero no deja que un participante se vaya a mitad de camino.
 */
public class Carrera {

    /** Cantidad de etapas. */
    private final int etapas;

    /** El phaser: una fase por etapa. */
    private final Phaser fases;

    /** Lo que pasó, en orden. La protege su propio monitor (ver {@link #anotar(String)}). */
    private final List<String> bitacora = new ArrayList<>();

    /**
     * Crea la carrera.
     *
     * <p>Validá los parámetros, guardá {@code etapas} y creá el phaser con un participante por
     * corredor. Hace falta redefinir {@code onAdvance}, así que es una clase anónima:
     *
     * <pre>
     *   fases = new Phaser(corredores) {
     *       &#64;Override
     *       protected boolean onAdvance(int fase, int registrados) {
     *           anotar("fin de la etapa " + fase + ": siguen " + registrados);
     *           return fase == etapas - 1 || registrados == 0;     ← true termina el phaser
     *       }
     *   };
     * </pre>
     *
     * <p>El phaser termina después de la última etapa, o cuando abandonaron todos. Por defecto,
     * {@code onAdvance} solo devuelve {@code true} cuando no queda nadie registrado.
     *
     * @param corredores cuántos corredores largan
     * @param etapas     cuántas etapas tiene la carrera
     * @throws IllegalArgumentException si alguno es menor que 1
     */
    public Carrera(int corredores, int etapas) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("constructor de Carrera");
    }

    /**
     * El corredor recorre una etapa: anota que empieza, tarda un rato al azar y anota que terminó. Ya
     * está hecho.
     *
     * @param nombre nombre del corredor
     * @param etapa  número de etapa (desde 0)
     */
    public void correrEtapa(String nombre, int etapa) {
        anotar("empieza " + etapa + " " + nombre);
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(5, 26));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        anotar("termina " + etapa + " " + nombre);
    }

    /**
     * El corredor terminó su etapa y espera a los demás: {@code arriveAndAwaitAdvance()}.
     */
    public void llegarAlFinDeEtapa() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Carrera.llegarAlFinDeEtapa()");
    }

    /**
     * El corredor abandona antes de empezar {@code etapa}: anotá {@code "abandona " + etapa + " " + nombre}
     * y después {@code arriveAndDeregister()}.
     *
     * <p>Si solo anotara y se fuera, el phaser lo seguiría contando como participante, y los demás lo
     * esperarían para siempre al final de la etapa.
     *
     * @param nombre nombre del corredor
     * @param etapa  la etapa que ya no va a correr
     */
    public void abandonar(String nombre, int etapa) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Carrera.abandonar()");
    }

    /**
     * @return cuántos corredores siguen registrados en el phaser ({@code getRegisteredParties()})
     */
    public int corredoresEnCarrera() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Carrera.corredoresEnCarrera()");
    }

    /**
     * @return la etapa en curso ({@code getPhase()}); es negativa cuando el phaser ya terminó
     */
    public int etapaActual() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Carrera.etapaActual()");
    }

    /**
     * @return {@code true} si el phaser terminó ({@code isTerminated()})
     */
    public boolean terminada() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Carrera.terminada()");
    }

    /** @return la cantidad de etapas */
    public int getEtapas() {
        return etapas;
    }

    /** @return una copia de la bitácora */
    public List<String> getBitacora() {
        synchronized (bitacora) {
            return new ArrayList<>(bitacora);
        }
    }

    /**
     * Agrega una línea a la bitácora. Ya está hecho.
     *
     * @param linea lo que pasó
     */
    private void anotar(String linea) {
        synchronized (bitacora) {
            bitacora.add(linea);
        }
    }
}
