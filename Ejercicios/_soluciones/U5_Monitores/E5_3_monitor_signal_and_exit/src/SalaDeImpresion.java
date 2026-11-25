import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Una sala con {@code N} impresoras, administrada por un monitor <i>signal and exit</i> hecho a mano
 * con semáforos, con la misma estructura que el monitor del TP final:
 *
 * <pre>
 *   mutex                ← la cola de entrada del monitor (semáforo binario, justo)
 *   esperanImpresora     ← la variable de condición: hilos que encontraron todas ocupadas
 *   libres               ← el estado del recurso
 * </pre>
 *
 * <p>Dos reglas de diseño, las dos de los parciales:
 *
 * <ul>
 *   <li><b>El monitor solo administra el acceso.</b> {@link #tomar()} y {@link #devolver()} son los
 *       procedimientos del monitor, y son cortos. La acción, que es imprimir, va <b>afuera</b>: si se
 *       hiciera con el mutex tomado, se imprimiría de a un documento por vez aunque haya {@code N}
 *       impresoras.
 *   <li><b>Signal and exit.</b> {@code devolver()} le pasa el mutex al despertado y sale sin soltarlo.
 *       Como nadie se puede meter en el medio, el despertado encuentra la impresora que le dejaron, y
 *       en {@code tomar()} alcanza con un {@code if}, no hace falta un {@code while}.
 * </ul>
 *
 * <p>Herramientas permitidas: {@code Semaphore} y la {@link ColaDeCondicion}. Nada de
 * {@code synchronized}, locks, {@code Condition} ni {@code wait}/{@code notify}.
 */
public class SalaDeImpresion {

    /** La cola de entrada del monitor. */
    private final Semaphore mutex = new Semaphore(1, true);

    /** Donde esperan los que encontraron todas las impresoras ocupadas. */
    private final ColaDeCondicion esperanImpresora = new ColaDeCondicion();

    /** Cantidad total de impresoras. */
    private final int impresoras;

    /** Impresoras libres. Lo protege el mutex. */
    private int libres;

    /** Cuánto tarda en imprimirse cada documento. */
    private final long demoraMs;

    /** Para el verificador: cuántos documentos se están imprimiendo ahora, y el máximo que hubo. */
    private final AtomicInteger imprimiendo = new AtomicInteger();
    private final AtomicInteger maxImprimiendo = new AtomicInteger();

    /** Para el verificador: el orden en que se tomaron las impresoras. Lo protege el mutex. */
    private final List<String> ordenDeAtencion = new ArrayList<>();

    /**
     * Crea la sala con todas las impresoras libres.
     *
     * @param impresoras cantidad de impresoras
     * @param demoraMs   cuánto tarda en imprimirse un documento
     * @throws IllegalArgumentException si {@code impresoras < 1} o {@code demoraMs < 0}
     */
    public SalaDeImpresion(int impresoras, long demoraMs) {
        if (impresoras < 1 || demoraMs < 0) {
            throw new IllegalArgumentException("sala inválida: " + impresoras + " impresoras, " + demoraMs + " ms");
        }
        this.impresoras = impresoras;
        this.libres = impresoras;
        this.demoraMs = demoraMs;
    }

    /**
     * Procedimiento del monitor: toma una impresora, esperando si están todas ocupadas.
     *
     * <pre>
     *   mutex.acquireUninterruptibly()
     *   if (libres == 0)
     *       esperanImpresora.esperar(mutex)    ← vuelve con el mutex, y con una impresora reservada
     *   libres--
     *   anotarAtencion()
     *   mutex.release()
     * </pre>
     *
     * <p>¿Por qué alcanza con {@code if}? Porque quien despierta a este hilo hace {@code libres++} y le
     * pasa el mutex sin soltarlo. Entre el aviso y este {@code libres--} no corrió nadie más.
     */
    public void tomar() {
        mutex.acquireUninterruptibly();
        if (libres == 0) {
            esperanImpresora.esperar(mutex);
        }
        libres--;
        anotarAtencion();
        mutex.release();
    }

    /**
     * Procedimiento del monitor: devuelve una impresora.
     *
     * <pre>
     *   mutex.acquireUninterruptibly()
     *   si libres == impresoras: soltar el mutex y lanzar IllegalStateException (nadie tenía una)
     *   libres++
     *   if (!esperanImpresora.despertarUno())
     *       mutex.release()                    ← nadie la esperaba: suelto el monitor
     *   // si desperté a alguien, el mutex ya es suyo: no lo suelto (signal and exit)
     * </pre>
     *
     * @throws IllegalStateException si estaban todas libres
     */
    public void devolver() {
        mutex.acquireUninterruptibly();
        if (libres == impresoras) {
            mutex.release();
            throw new IllegalStateException("estaban todas las impresoras libres");
        }
        libres++;
        if (!esperanImpresora.despertarUno()) {
            mutex.release();
        }
    }

    /**
     * Imprime un documento: toma una impresora, imprime <b>fuera del monitor</b> y la devuelve.
     *
     * <pre>
     *   tomar();
     *   try {
     *       trabajar(documento);    ← sin el mutex: otros pueden tomar y devolver mientras tanto
     *   } finally {
     *       devolver();
     *   }
     * </pre>
     *
     * @param documento el documento
     */
    public void imprimir(String documento) {
        tomar();
        try {
            trabajar(documento);
        } finally {
            devolver();
        }
    }

    /**
     * Ya está hecho. Es la acción: imprime el documento (tarda {@code demoraMs}) y lleva la cuenta de
     * cuántos se imprimen a la vez. Va fuera del monitor.
     */
    private void trabajar(String documento) {
        int ahora = imprimiendo.incrementAndGet();
        maxImprimiendo.accumulateAndGet(ahora, Math::max);
        try {
            if (demoraMs > 0) {
                Thread.sleep(demoraMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            imprimiendo.decrementAndGet();
        }
    }

    /** Ya está hecho: anota quién tomó una impresora. Se llama con el mutex tomado. */
    private void anotarAtencion() {
        ordenDeAtencion.add(Thread.currentThread().getName());
    }

    /**
     * Ya está hecho.
     *
     * @return cuántas impresoras están libres ahora
     */
    public int libres() {
        mutex.acquireUninterruptibly();
        try {
            return libres;
        } finally {
            mutex.release();
        }
    }

    /**
     * Ya está hecho.
     *
     * @return cuántos hilos esperan una impresora ahora
     */
    public int esperando() {
        mutex.acquireUninterruptibly();
        try {
            return esperanImpresora.esperando();
        } finally {
            mutex.release();
        }
    }

    /**
     * Ya está hecho.
     *
     * @return los nombres de los hilos que tomaron una impresora, en orden
     */
    public List<String> getOrdenDeAtencion() {
        mutex.acquireUninterruptibly();
        try {
            return new ArrayList<>(ordenDeAtencion);
        } finally {
            mutex.release();
        }
    }

    /** @return el máximo de documentos que se imprimieron a la vez. Ya está hecho. */
    public int maxImprimiendo() {
        return maxImprimiendo.get();
    }
}
