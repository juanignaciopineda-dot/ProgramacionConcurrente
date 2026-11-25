import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Estacionamiento con una cantidad fija de lugares numerados. Los autos que llegan con el
 * estacionamiento lleno esperan a que salga alguno.
 *
 * <p>Es el ejemplo de las tres impresoras de la teoría, con dos semáforos que hacen dos trabajos
 * distintos:
 *
 * <ul>
 *   <li>{@code lugaresLibres}, de <b>N permisos</b>: cuenta cuántos lugares quedan. Garantiza que
 *       <b>haya</b> un lugar para el auto que entra. Es el lugar "recursos" de la red de Petri, con N
 *       tokens.
 *   <li>{@code mutex}, <b>binario</b> (1 permiso): protege el arreglo {@code ocupado}. Garantiza que
 *       dos autos no se lleven <b>el mismo</b> lugar.
 * </ul>
 *
 * <p>Un semáforo <b>no tiene dueño</b>: cualquier hilo puede hacer {@code release()}, haya hecho
 * {@code acquire()} o no. Si alguien "sale" de un lugar que estaba libre y eso hace un
 * {@code release()}, el semáforo pasa a tener un permiso de más y el estacionamiento "crece". Por eso
 * {@link #salir(int)} valida antes de liberar.
 *
 * <p>Herramientas permitidas: {@code Semaphore}. Nada de {@code synchronized},
 * {@code wait}/{@code notify}, {@code Lock} ni clases {@code Atomic...}.
 */
public class Estacionamiento {

    /** Un permiso por lugar libre. */
    private final Semaphore lugaresLibres;

    /** Semáforo binario que protege {@code ocupado}. */
    private final Semaphore mutex;

    /** {@code ocupado[i]} es {@code true} si hay un auto en el lugar {@code i}. Lo protege {@code mutex}. */
    private final boolean[] ocupado;

    /**
     * Crea un estacionamiento vacío.
     *
     * <p>Validá {@code lugares}. Después creá el arreglo y los dos semáforos: ¿con cuántos permisos
     * arranca cada uno?
     *
     * @param lugares cantidad de lugares
     * @throws IllegalArgumentException si {@code lugares < 1}
     */
    public Estacionamiento(int lugares) {
        if (lugares < 1) {
            throw new IllegalArgumentException("cantidad de lugares inválida: " + lugares);
        }
        ocupado = new boolean[lugares];
        lugaresLibres = new Semaphore(lugares);
        mutex = new Semaphore(1);
    }

    /**
     * Entra un auto, esperando si está lleno, y devuelve el lugar que le tocó.
     *
     * <ol>
     *   <li>{@code lugaresLibres.acquire()}: si no hay permisos, el hilo duerme en la cola del
     *       semáforo hasta que alguien salga.
     *   <li>Ya hay un lugar seguro para este auto: buscalo con {@link #ocuparLugar()} y devolvelo.
     * </ol>
     *
     * <p>No capturés la {@link InterruptedException}: si interrumpen al auto mientras espera, no
     * consiguió el permiso y no hay nada que devolver.
     *
     * @return el número del lugar que ocupó el auto
     * @throws InterruptedException si interrumpen al hilo mientras espera lugar
     */
    public int entrar() throws InterruptedException {
        lugaresLibres.acquire();
        return ocuparLugar();
    }

    /**
     * Como {@link #entrar()}, pero espera lugar como máximo {@code esperaMaxMs}.
     *
     * <p>{@code lugaresLibres.tryAcquire(esperaMaxMs, TimeUnit.MILLISECONDS)} devuelve {@code true}
     * si consiguió el permiso a tiempo. Si no lo consiguió, devolvé {@code -1}; si lo consiguió,
     * {@link #ocuparLugar()}.
     *
     * @param esperaMaxMs cuánto esperar, como máximo
     * @return el número del lugar, o {@code -1} si no hubo lugar a tiempo
     * @throws InterruptedException si interrumpen al hilo mientras espera
     */
    public int intentarEntrar(long esperaMaxMs) throws InterruptedException {
        if (!lugaresLibres.tryAcquire(esperaMaxMs, TimeUnit.MILLISECONDS)) {
            return -1;
        }
        return ocuparLugar();
    }

    /**
     * Elige un lugar libre, lo marca como ocupado y devuelve su número. Solo se llama después de
     * conseguir un permiso de {@code lugaresLibres}, así que seguro hay alguno libre.
     *
     * <p>El recorrido de {@code ocupado} es una sección crítica: si dos autos lo recorren a la vez,
     * los dos pueden ver libre el mismo lugar. Protegelo con {@code mutex}:
     *
     * <pre>
     *   mutex.acquireUninterruptibly();
     *   try {
     *       buscar el primer lugar libre (el de número más chico), marcarlo y devolverlo
     *   } finally {
     *       mutex.release();
     *   }
     * </pre>
     *
     * <p>¿Por qué {@code acquireUninterruptibly()} y no {@code acquire()}? Porque este hilo ya tiene un
     * permiso de {@code lugaresLibres}. Si lo interrumpieran acá, saldría con la excepción sin haber
     * ocupado ningún lugar, y ese permiso se perdería para siempre. La espera por el {@code mutex} es
     * cortísima, así que no hace falta que se pueda interrumpir.
     *
     * @return el número del lugar ocupado
     */
    private int ocuparLugar() {
        mutex.acquireUninterruptibly();
        try {
            for (int i = 0; i < ocupado.length; i++) {
                if (!ocupado[i]) {
                    ocupado[i] = true;
                    return i;
                }
            }
            throw new IllegalStateException("había un permiso pero ningún lugar libre");
        } finally {
            mutex.release();
        }
    }

    /**
     * Sale el auto que estaba en {@code lugar}.
     *
     * <ol>
     *   <li>Si {@code lugar} no existe, {@code IllegalArgumentException}.
     *   <li>Con el {@code mutex} tomado: si el lugar no estaba ocupado, {@code IllegalStateException};
     *       si estaba ocupado, marcalo libre.
     *   <li>Después de soltar el {@code mutex}, y solo si el lugar estaba ocupado:
     *       {@code lugaresLibres.release()}.
     * </ol>
     *
     * <p>Si hicieras el {@code release()} sin validar, cada "salida" de un lugar vacío le regalaría un
     * permiso al semáforo, y después entrarían más autos que lugares.
     *
     * @param lugar el número del lugar que se libera
     * @throws IllegalArgumentException si {@code lugar} no es un lugar del estacionamiento
     * @throws IllegalStateException    si el lugar estaba libre
     */
    public void salir(int lugar) {
        if (lugar < 0 || lugar >= ocupado.length) {
            throw new IllegalArgumentException("no existe el lugar " + lugar);
        }
        mutex.acquireUninterruptibly();
        try {
            if (!ocupado[lugar]) {
                throw new IllegalStateException("el lugar " + lugar + " ya estaba libre");
            }
            ocupado[lugar] = false;
        } finally {
            mutex.release();
        }
        lugaresLibres.release();
    }

    /**
     * Devuelve cuántos lugares libres hay ahora: los permisos disponibles de {@code lugaresLibres}
     * ({@code availablePermits()}). Como puede cambiar enseguida, sirve para mostrar, no para decidir.
     *
     * @return la cantidad de lugares libres
     */
    public int libres() {
        return lugaresLibres.availablePermits();
    }

    /**
     * Ya está hecho.
     *
     * @return la cantidad total de lugares
     */
    public int capacidad() {
        return ocupado.length;
    }
}
