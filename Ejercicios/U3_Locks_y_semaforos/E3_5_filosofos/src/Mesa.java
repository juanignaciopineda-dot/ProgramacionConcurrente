import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * La mesa de la cena de los filósofos: {@code n} filósofos en ronda y un tenedor entre cada par.
 * Para comer, el filósofo {@code i} necesita sus dos tenedores: el izquierdo ({@code i}) y el derecho
 * ({@code (i + 1) % n}).
 *
 * <pre>
 *            F0
 *        T0      T1          F = filósofo, T = tenedor
 *     F4            F1       F0 usa T0 y T1, F1 usa T1 y T2, ..., F4 usa T4 y T0
 *        T4      T2
 *         F3  T3  F2
 * </pre>
 *
 * <p>Cada tenedor es un {@link ReentrantLock}. {@link #comerIngenuo(int)} ya está hecho, y es la
 * versión que se traba: cada uno toma su tenedor izquierdo y después el derecho. Si los {@code n}
 * toman el izquierdo a la vez, cada uno espera el tenedor que tiene el de al lado, en ronda: una
 * <b>espera circular</b>, y eso es un deadlock. El verificador te lo muestra en el primer caso.
 *
 * <p>Vas a escribir tres maneras de evitarlo. Cada una rompe una de las condiciones que hacen falta
 * para que haya deadlock:
 *
 * <ul>
 *   <li>{@link #comerConOrdenGlobal(int)}: todos toman los tenedores en el mismo orden global (el de
 *       número más chico primero). Rompe la <b>espera circular</b>.
 *   <li>{@link #comerConMayordomo(int)}: un semáforo deja sentarse como máximo a {@code n - 1}. Con
 *       un lugar vacío, siempre hay alguno que consigue los dos tenedores. También rompe la espera
 *       circular, pero limitando cuántos compiten.
 *   <li>{@link #intentarComer(int, long)}: si no consigue el segundo tenedor a tiempo, suelta el
 *       primero. Rompe la <b>retención y espera</b>: nadie se queda esperando con un tenedor en la
 *       mano.
 * </ul>
 *
 * <p>En las tres, entre el primer tenedor y el segundo llamá a {@link #pausa()}: es lo que hace que,
 * si la estrategia se puede trabar, se trabe. Y para comer llamá a {@link #comer(int)}, que
 * controla que tengas los dos tenedores.
 *
 * <p>Herramientas permitidas: {@code ReentrantLock} (los tenedores) y {@code Semaphore} (el
 * mayordomo). Nada de {@code synchronized} ni {@code wait}/{@code notify}.
 */
public class Mesa {

    /** Cantidad de filósofos (y de tenedores). */
    private final int n;

    /** {@code tenedores[k]} es el tenedor {@code k}. */
    private final ReentrantLock[] tenedores;

    /** Cuánto tarda {@link #pausa()}. */
    private final long pausaMs;

    /** {@code comidas[i]} es cuántas veces comió el filósofo {@code i}. */
    private final int[] comidas;

    // TODO: declará acá el semáforo del mayordomo (mirá comerConMayordomo).

    /**
     * Arma la mesa con todos los tenedores libres. Ya está hecho, salvo el mayordomo.
     *
     * @param filosofos cantidad de filósofos
     * @param pausaMs   cuánto tarda {@link #pausa()}
     * @throws IllegalArgumentException si hay menos de 2 filósofos o la pausa es negativa
     */
    public Mesa(int filosofos, long pausaMs) {
        if (filosofos < 2) {
            throw new IllegalArgumentException("hacen falta al menos 2 filósofos: " + filosofos);
        }
        if (pausaMs < 0) {
            throw new IllegalArgumentException("pausa negativa: " + pausaMs);
        }
        this.n = filosofos;
        this.pausaMs = pausaMs;
        this.tenedores = new ReentrantLock[n];
        for (int k = 0; k < n; k++) {
            tenedores[k] = new ReentrantLock();
        }
        this.comidas = new int[n];
        // TODO: creá acá el semáforo del mayordomo. ¿Cuántos permisos tiene?
    }

    /**
     * La versión que se traba. Ya está hecha: es el modelo de cómo tomar y soltar dos locks
     * anidados, con un {@code try}/{@code finally} por cada uno.
     *
     * <p>Usa {@code lockInterruptibly()} en vez de {@code lock()} para que el verificador pueda
     * destrabar a los filósofos interrumpiéndolos después de mostrarte el deadlock.
     *
     * @param i el filósofo que quiere comer
     * @throws InterruptedException si lo interrumpen mientras espera un tenedor
     */
    public void comerIngenuo(int i) throws InterruptedException {
        ReentrantLock izquierdo = tenedores[izquierdo(i)];
        ReentrantLock derecho = tenedores[derecho(i)];
        izquierdo.lockInterruptibly();
        try {
            pausa();
            derecho.lockInterruptibly();
            try {
                comer(i);
            } finally {
                derecho.unlock();
            }
        } finally {
            izquierdo.unlock();
        }
    }

    /**
     * Come tomando los tenedores en un orden global: primero el de <b>número más chico</b> de los
     * dos, después el otro.
     *
     * <p>Es la misma estructura que {@link #comerIngenuo(int)}; lo único que cambia es cuál se toma
     * primero. Para los filósofos 0 a {@code n - 2} da lo mismo que el ingenuo (su izquierdo es el
     * más chico). El que cambia es el último: su derecho es el tenedor 0, así que lo pide primero, y
     * compite por él con el filósofo 0 antes de tener nada en la mano. Así la ronda de esperas no se
     * puede cerrar.
     *
     * @param i el filósofo que quiere comer
     * @throws InterruptedException si lo interrumpen mientras espera un tenedor
     */
    public void comerConOrdenGlobal(int i) throws InterruptedException {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Mesa.comerConOrdenGlobal()");
    }

    /**
     * Come pidiéndole permiso al mayordomo, que deja sentarse como máximo a {@code n - 1} filósofos a
     * la vez.
     *
     * <pre>
     *   mayordomo.acquire()
     *   try {
     *       comerIngenuo(i)      ← sí, el ingenuo: con un lugar vacío ya no se puede trabar
     *   } finally {
     *       mayordomo.release()
     *   }
     * </pre>
     *
     * <p>Primero declará el semáforo como campo y crealo en el constructor. ¿Por qué {@code n - 1}
     * alcanza? Con {@code n - 1} sentados y {@code n} tenedores, aunque cada sentado tenga ya un
     * tenedor en la mano, sobra uno, y es el segundo tenedor de alguno de ellos: ese come, suelta los
     * dos y los demás siguen.
     *
     * @param i el filósofo que quiere comer
     * @throws InterruptedException si lo interrumpen mientras espera
     */
    public void comerConMayordomo(int i) throws InterruptedException {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Mesa.comerConMayordomo()");
    }

    /**
     * Intenta comer: toma el tenedor izquierdo y después el derecho, esperando cada uno <b>como
     * máximo</b> {@code esperaMaxMs}. Si alguno no llega a tiempo, suelta lo que tenía y devuelve
     * {@code false} (el filósofo vuelve a pensar y lo intentará más tarde).
     *
     * <pre>
     *   if (!izquierdo.tryLock(esperaMaxMs, TimeUnit.MILLISECONDS)) return false;
     *   try {
     *       pausa()
     *       if (!derecho.tryLock(esperaMaxMs, TimeUnit.MILLISECONDS)) return false;
     *       try {
     *           comer(i); return true;
     *       } finally { derecho.unlock(); }
     *   } finally { izquierdo.unlock(); }      ← suelta el izquierdo en los dos casos
     * </pre>
     *
     * <p>Mirá qué pasa con el {@code return false} del medio: sale del {@code try} de afuera, así que
     * el {@code finally} suelta el izquierdo. Nadie se queda con un tenedor en la mano esperando.
     *
     * @param i           el filósofo que quiere comer
     * @param esperaMaxMs cuánto esperar cada tenedor, como máximo
     * @return {@code true} si comió, {@code false} si se rindió
     * @throws InterruptedException si lo interrumpen mientras espera un tenedor
     */
    public boolean intentarComer(int i, long esperaMaxMs) throws InterruptedException {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Mesa.intentarComer()");
    }

    /**
     * El filósofo {@code i} come. Ya está hecho. Controla que el hilo que llama tenga los dos
     * tenedores de {@code i}; si no, lanza {@code IllegalStateException}.
     *
     * @param i el filósofo que come
     */
    public void comer(int i) {
        if (!tenedores[izquierdo(i)].isHeldByCurrentThread() || !tenedores[derecho(i)].isHeldByCurrentThread()) {
            throw new IllegalStateException("el filósofo " + i + " quiso comer sin tener sus dos tenedores");
        }
        comidas[i]++;
    }

    /**
     * Espera {@code pausaMs}. Ya está hecho. Va entre el primer tenedor y el segundo.
     *
     * @throws InterruptedException si interrumpen al hilo mientras espera
     */
    public void pausa() throws InterruptedException {
        if (pausaMs > 0) {
            Thread.sleep(pausaMs);
        }
    }

    /**
     * Ya está hecho.
     *
     * @param i un filósofo
     * @return el número de su tenedor izquierdo
     */
    public int izquierdo(int i) {
        validar(i);
        return i;
    }

    /**
     * Ya está hecho.
     *
     * @param i un filósofo
     * @return el número de su tenedor derecho
     */
    public int derecho(int i) {
        validar(i);
        return (i + 1) % n;
    }

    /**
     * Ya está hecho: deja que el verificador tome un tenedor a mano.
     *
     * @param k el número del tenedor
     * @return el tenedor
     */
    public ReentrantLock tenedor(int k) {
        validar(k);
        return tenedores[k];
    }

    /**
     * Ya está hecho.
     *
     * @return la cantidad de filósofos
     */
    public int cantidad() {
        return n;
    }

    /**
     * Ya está hecho. Llamalo después de hacer {@code join()} a los filósofos: es lo que garantiza que
     * se vean todas las comidas.
     *
     * @return una copia de cuántas veces comió cada filósofo
     */
    public int[] getComidas() {
        return comidas.clone();
    }

    private void validar(int i) {
        if (i < 0 || i >= n) {
            throw new IllegalArgumentException("no existe el número " + i + " en una mesa de " + n);
        }
    }
}
