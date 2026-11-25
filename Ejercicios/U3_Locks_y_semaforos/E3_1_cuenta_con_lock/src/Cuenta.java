import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Cuenta bancaria protegida con un {@link ReentrantLock} en lugar de {@code synchronized}.
 *
 * <p>Todas las operaciones siguen el mismo patrón, que conviene saber de memoria:
 *
 * <pre>
 *   candado.lock();
 *   try {
 *       ... sección crítica ...
 *   } finally {
 *       candado.unlock();        ← SIEMPRE en el finally
 *   }
 * </pre>
 *
 * <p>Con {@code synchronized}, si sale una excepción de la sección crítica, el monitor se libera
 * solo. Con un {@code Lock} no: si el {@code unlock()} no está en un {@code finally}, una excepción
 * deja el candado tomado para siempre y el próximo hilo que lo pida se cuelga. {@link #getRegistro()}
 * ya está escrito así: usalo de modelo.
 *
 * <p>Lo que da un {@code ReentrantLock} y {@code synchronized} no:
 *
 * <ul>
 *   <li>{@code tryLock(tiempo, unidad)}: esperar el candado un tiempo máximo y, si no llega,
 *       rendirse.
 *   <li><i>Fairness</i>: con {@code new ReentrantLock(true)} el candado se lo lleva el hilo que hace
 *       más tiempo que espera.
 * </ul>
 *
 * <p>Lo que tienen en común: los dos son <b>reentrantes</b>. El hilo que ya tiene el candado lo puede
 * volver a pedir sin trabarse; queda tomado "dos veces" y se libera recién con el segundo
 * {@code unlock()}.
 *
 * <p>Herramientas permitidas: {@code ReentrantLock}. Nada de {@code synchronized}, semáforos ni clases
 * {@code Atomic...}.
 */
public class Cuenta {

    /** El candado que protege {@code saldo} y {@code registro}. */
    private final ReentrantLock candado;

    /** Dinero disponible. Solo se lee o se modifica con el candado tomado. */
    private long saldo;

    /** Cuánto tarda cada operación (simula el acceso a una base de datos). */
    private final long demoraMs;

    /** Nombres de los hilos que pasaron por {@link #procesar()}, en orden. Lo protege el candado. */
    private final List<String> registro = new ArrayList<>();

    /**
     * Crea una cuenta sin demora y con un candado no justo. Ya está hecho.
     *
     * @param saldoInicial dinero con el que arranca la cuenta
     */
    public Cuenta(long saldoInicial) {
        this(saldoInicial, 0, false);
    }

    /**
     * Crea una cuenta.
     *
     * <p>Validá los dos números primero. Después guardalos y creá el candado con
     * {@code new ReentrantLock(justa)}: ese {@code boolean} es el modo <i>fair</i> del candado.
     *
     * @param saldoInicial dinero con el que arranca la cuenta
     * @param demoraMs     cuánto tarda cada operación (0 para que no tarde nada)
     * @param justa        {@code true} para un candado justo (fair), {@code false} para uno no justo
     * @throws IllegalArgumentException si {@code saldoInicial} o {@code demoraMs} son negativos
     */
    public Cuenta(long saldoInicial, long demoraMs, boolean justa) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("constructor de Cuenta");
    }

    /**
     * Suma {@code monto} al saldo.
     *
     * <ol>
     *   <li>Validá {@code monto} <b>antes</b> de tomar el candado: no toca nada compartido.
     *   <li>{@code candado.lock()}, y enseguida el {@code try}.
     *   <li>Adentro del {@code try}: llamá a {@link #procesar()} y después sumá.
     *   <li>En el {@code finally}: {@code candado.unlock()}.
     * </ol>
     *
     * @param monto cuánto depositar
     * @throws IllegalArgumentException si {@code monto <= 0}
     */
    public void depositar(long monto) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Cuenta.depositar()");
    }

    /**
     * Resta {@code monto} del saldo, si alcanza.
     *
     * <p>Es como {@link #depositar(long)}, pero adentro de la sección crítica, después de
     * {@code procesar()}, mirá el saldo: si no alcanza, lanzá
     * {@code new SaldoInsuficienteException(saldo, monto)} <b>desde adentro del {@code try}</b>. Es el
     * caso para el que existe el {@code finally}: la excepción sale del método y el candado se libera
     * igual.
     *
     * <p>Mirar el saldo y restar van en la misma sección crítica (<i>check-then-act</i>): si soltaras
     * el candado entre las dos cosas, otro hilo podría extraer en el medio.
     *
     * @param monto cuánto extraer
     * @throws IllegalArgumentException   si {@code monto <= 0}
     * @throws SaldoInsuficienteException si el saldo es menor que {@code monto} (el saldo no cambia)
     */
    public void extraer(long monto) throws SaldoInsuficienteException {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Cuenta.extraer()");
    }

    /**
     * Como {@link #extraer(long)}, pero espera el candado <b>como máximo</b> {@code esperaMaxMs}. Si
     * en ese tiempo no lo consigue, se rinde y devuelve {@code false} sin tocar nada.
     *
     * <pre>
     *   validar monto
     *   if (!candado.tryLock(esperaMaxMs, TimeUnit.MILLISECONDS)) {
     *       return false;            ← no lo conseguiste: no hay nada que liberar
     *   }
     *   try {
     *       ...                      ← lo tenés: ahora sí, try/finally
     *   } finally {
     *       candado.unlock();
     *   }
     * </pre>
     *
     * <p>Fijate dónde empieza el {@code try}: <b>después</b> de saber que conseguiste el candado. Si
     * el {@code unlock()} corriera también cuando {@code tryLock} devolvió {@code false}, lanzaría
     * {@code IllegalMonitorStateException}: no se puede soltar un candado que no tenés.
     *
     * <p>Adentro podés llamar directamente a {@link #extraer(long)}. Va a volver a pedir el candado y,
     * como es reentrante, se lo dan enseguida.
     *
     * @param monto       cuánto extraer
     * @param esperaMaxMs cuánto esperar el candado, como máximo
     * @return {@code true} si extrajo, {@code false} si no consiguió el candado a tiempo
     * @throws InterruptedException       si interrumpen al hilo mientras espera el candado
     * @throws SaldoInsuficienteException si consiguió el candado pero el saldo no alcanza
     * @throws IllegalArgumentException   si {@code monto <= 0}
     */
    public boolean intentarExtraer(long monto, long esperaMaxMs)
            throws InterruptedException, SaldoInsuficienteException {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Cuenta.intentarExtraer()");
    }

    /**
     * Saca todo el saldo y devuelve cuánto era, en una sola operación atómica.
     *
     * <p>Se arma con lo que ya tenés: {@link #getSaldo()} y {@link #extraer(long)}. El problema es
     * que, si llamás a una y después a la otra sin más, entre las dos se puede meter otro hilo y sacar
     * plata: {@code extraer} fallaría con un saldo que un instante antes alcanzaba. La solución es
     * tomar el candado <b>alrededor</b> de las dos llamadas. Adentro, cada una lo vuelve a pedir, y la
     * reentrancia hace que eso no se trabe.
     *
     * <p>Si el saldo es 0, devolvé 0 sin llamar a {@code extraer}, que rechaza montos {@code <= 0}.
     *
     * <p>{@code extraer} declara {@link SaldoInsuficienteException} y este método no, así que vas a
     * tener que capturarla. Con el candado tomado no puede pasar; si pasa, es un error de programación:
     * convertila en {@code new IllegalStateException("...", e)}.
     *
     * @return el saldo que había (ahora es 0)
     */
    public long extraerTodo() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Cuenta.extraerTodo()");
    }

    /**
     * Devuelve el saldo. Aunque sea solo una lectura, va con el candado: sin él, nada garantiza que
     * este hilo vea el último valor que escribió otro (visibilidad). No hace falta llamar a
     * {@code procesar()}: consultar no demora.
     *
     * @return el dinero disponible
     */
    public long getSaldo() {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Cuenta.getSaldo()");
    }

    /**
     * Ya está hecho.
     *
     * @return {@code true} si el candado es justo (fair)
     */
    public boolean esJusta() {
        return candado.isFair();
    }

    /**
     * Ya está hecho: lo usa el verificador para ver si quedó el candado tomado.
     *
     * @return {@code true} si algún hilo tiene el candado tomado en este momento
     */
    public boolean estaTomada() {
        return candado.isLocked();
    }

    /**
     * Devuelve una copia del registro de operaciones. Ya está hecho, y es el modelo del patrón
     * lock/try/finally.
     *
     * @return los nombres de los hilos que pasaron por {@link #procesar()}, en orden
     */
    public List<String> getRegistro() {
        candado.lock();
        try {
            return new ArrayList<>(registro);
        } finally {
            candado.unlock();
        }
    }

    /**
     * Simula el trabajo de una operación: anota qué hilo la hizo y tarda {@code demoraMs}. Ya está
     * hecho. Llamalo desde adentro de la sección crítica de {@code depositar} y {@code extraer}: si
     * lo llamás sin tener el candado, lanza {@code IllegalStateException}.
     */
    private void procesar() {
        if (!candado.isHeldByCurrentThread()) {
            throw new IllegalStateException("procesar() se llamó sin tener el candado tomado");
        }
        registro.add(Thread.currentThread().getName());
        if (demoraMs > 0) {
            try {
                Thread.sleep(demoraMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
