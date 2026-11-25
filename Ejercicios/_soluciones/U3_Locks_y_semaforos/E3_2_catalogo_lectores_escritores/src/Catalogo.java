import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Catálogo de precios que muchos hilos consultan todo el tiempo y pocos modifican: el problema de
 * <b>lectores y escritores</b>.
 *
 * <p>Un {@code ReentrantReadWriteLock} tiene dos candados que trabajan juntos:
 *
 * <ul>
 *   <li>{@code candado.readLock()}: lo pueden tener <b>varios lectores a la vez</b>.
 *   <li>{@code candado.writeLock()}: lo tiene <b>un solo escritor</b>, y mientras lo tiene no hay
 *       ningún lector adentro.
 * </ul>
 *
 * <p>Los dos se usan con el mismo patrón que un {@code Lock} común:
 *
 * <pre>
 *   candado.readLock().lock();        (o writeLock())
 *   try {
 *       ...
 *   } finally {
 *       candado.readLock().unlock();
 *   }
 * </pre>
 *
 * <p>Elegir cuál usar es responsabilidad tuya: si modificás algo con el candado de <b>lectura</b>, el
 * {@code ReadWriteLock} no te avisa, y como varios lectores entran a la vez, rompiste la exclusión
 * mutua. Para que no pase desapercibido, {@link #simularEscritura()} lo controla.
 *
 * <p>Herramientas permitidas: {@code ReentrantReadWriteLock} y colecciones comunes. Nada de
 * {@code synchronized}, semáforos, {@code ConcurrentHashMap} ni {@code Collections.synchronized...}.
 */
public class Catalogo {

    /** Precio de cada producto. Lo protege {@code candado}. */
    private final Map<String, Double> precios = new HashMap<>();

    /** Candado de lectura y escritura que protege {@code precios}. */
    private final ReentrantReadWriteLock candado = new ReentrantReadWriteLock();

    /** Cuánto tarda cada acceso (simula una base de datos lenta). */
    private final long demoraMs;

    /** Crea un catálogo vacío sin demora. Ya está hecho. */
    public Catalogo() {
        this(0);
    }

    /**
     * Crea un catálogo vacío. Ya está hecho.
     *
     * @param demoraMs cuánto tarda cada lectura y cada escritura
     * @throws IllegalArgumentException si {@code demoraMs < 0}
     */
    public Catalogo(long demoraMs) {
        if (demoraMs < 0) {
            throw new IllegalArgumentException("demora negativa: " + demoraMs);
        }
        this.demoraMs = demoraMs;
    }

    /**
     * Devuelve el precio de un producto.
     *
     * <p>Candado de <b>lectura</b>. Adentro: {@link #simularLectura()} y después buscá el precio. Usá
     * {@code Optional.ofNullable(...)}: si el producto no está, {@code get} devuelve {@code null}.
     *
     * @param producto el nombre del producto
     * @return el precio, o {@code Optional.empty()} si el producto no está en el catálogo
     */
    public Optional<Double> precio(String producto) {
        candado.readLock().lock();
        try {
            simularLectura();
            return Optional.ofNullable(precios.get(producto));
        } finally {
            candado.readLock().unlock();
        }
    }

    /**
     * Agrega un producto o le cambia el precio.
     *
     * <p>Validá el precio antes de tomar nada. Después, candado de <b>escritura</b>; adentro,
     * {@link #simularEscritura()} y el {@code put}.
     *
     * @param producto el nombre del producto
     * @param precio   el precio nuevo
     * @throws IllegalArgumentException si {@code precio < 0}
     */
    public void actualizar(String producto, double precio) {
        if (precio < 0) {
            throw new IllegalArgumentException("precio negativo: " + precio);
        }
        candado.writeLock().lock();
        try {
            simularEscritura();
            precios.put(producto, precio);
        } finally {
            candado.writeLock().unlock();
        }
    }

    /**
     * Aumenta <b>todos</b> los precios un porcentaje, en una sola operación.
     *
     * <p>Candado de escritura <b>una sola vez</b>, alrededor de todo el recorrido: así ningún lector
     * puede ver el catálogo a medio actualizar (algunos precios con aumento y otros sin). Si en cambio
     * llamaras a {@link #actualizar(String, double)} por cada producto, cada uno tomaría y soltaría el
     * candado, y entre producto y producto podría entrar un lector.
     *
     * <p>Adentro: {@link #simularEscritura()} una vez, y después cada precio pasa a valer
     * {@code precio * (1 + porcentaje / 100)}. {@code precios.replaceAll((producto, precio) -> ...)}
     * lo hace en una línea.
     *
     * @param porcentaje el aumento, por ejemplo 10 para un 10 %
     */
    public void aplicarAumento(double porcentaje) {
        candado.writeLock().lock();
        try {
            simularEscritura();
            precios.replaceAll((producto, precio) -> precio * (1 + porcentaje / 100));
        } finally {
            candado.writeLock().unlock();
        }
    }

    /**
     * Devuelve una foto del catálogo completo.
     *
     * <p>Candado de lectura, {@link #simularLectura()}, y devolvé una <b>copia</b>
     * ({@code new HashMap<>(precios)}): si devolvieras el mapa interno, quien lo recibe lo leería
     * después, ya sin candado, mientras un escritor lo modifica.
     *
     * @return una copia de los precios
     */
    public Map<String, Double> copia() {
        candado.readLock().lock();
        try {
            simularLectura();
            return new HashMap<>(precios);
        } finally {
            candado.readLock().unlock();
        }
    }

    /**
     * Devuelve el precio de un producto, y si no está, lo calcula, lo guarda y lo devuelve. Es lo que
     * hace una caché.
     *
     * <p>Lo normal es que el producto ya esté, así que primero se intenta solo leyendo. Hay dos fases:
     *
     * <ol>
     *   <li><b>Lectura</b>: candado de lectura, {@link #simularLectura()} y buscá el precio. Si está,
     *       devolvelo (el {@code finally} suelta el candado). Si no está, soltá el candado de lectura.
     *   <li><b>Escritura</b>: candado de escritura y {@link #simularEscritura()}. Volvé a buscar el
     *       precio: mientras esperabas el candado, otro hilo pudo haberlo calculado. Solo si sigue sin
     *       estar, llamá a {@code calcular.apply(producto)} y guardá el resultado. Devolvé el precio.
     * </ol>
     *
     * <p>Dos trampas que esta estructura evita:
     *
     * <ul>
     *   <li><b>No se puede "ascender" del candado de lectura al de escritura.</b> Si pedís
     *       {@code writeLock().lock()} mientras todavía tenés el {@code readLock()}, el escritor espera
     *       a que no quede ningún lector, y vos sos uno: el hilo se traba esperándose a sí mismo. Por
     *       eso la fase 1 suelta el de lectura antes de que empiece la fase 2.
     *   <li><b>Volver a mirar en la fase 2.</b> Entre soltar el de lectura y conseguir el de
     *       escritura hay un hueco. Si diez hilos piden el mismo producto que falta, los diez pasan la
     *       fase 1; sin el segundo chequeo, los diez lo calcularían.
     * </ul>
     *
     * @param producto el nombre del producto
     * @param calcular cómo calcular el precio si no está (puede ser lento)
     * @return el precio del producto
     */
    public double obtenerOCalcular(String producto, Function<String, Double> calcular) {
        candado.readLock().lock();
        try {
            simularLectura();
            Double precio = precios.get(producto);
            if (precio != null) {
                return precio;
            }
        } finally {
            candado.readLock().unlock();
        }
        candado.writeLock().lock();
        try {
            simularEscritura();
            Double precio = precios.get(producto);
            if (precio == null) {
                precio = calcular.apply(producto);
                precios.put(producto, precio);
            }
            return precio;
        } finally {
            candado.writeLock().unlock();
        }
    }

    /**
     * Simula una lectura lenta. Ya está hecho. Llamalo con el candado de lectura tomado (o el de
     * escritura, que también deja leer); si no, lanza {@code IllegalStateException}.
     */
    private void simularLectura() {
        if (candado.getReadHoldCount() == 0 && !candado.isWriteLockedByCurrentThread()) {
            throw new IllegalStateException("se leyó el catálogo sin tener el candado de lectura");
        }
        dormir();
    }

    /**
     * Simula una escritura lenta. Ya está hecho. Llamalo con el candado de escritura tomado; si no,
     * lanza {@code IllegalStateException}.
     */
    private void simularEscritura() {
        if (!candado.isWriteLockedByCurrentThread()) {
            throw new IllegalStateException(candado.getReadHoldCount() > 0
                    ? "se escribió con el candado de LECTURA, que puede estar en manos de varios hilos a la vez"
                    : "se escribió el catálogo sin tener el candado de escritura");
        }
        dormir();
    }

    private void dormir() {
        if (demoraMs > 0) {
            try {
                Thread.sleep(demoraMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
