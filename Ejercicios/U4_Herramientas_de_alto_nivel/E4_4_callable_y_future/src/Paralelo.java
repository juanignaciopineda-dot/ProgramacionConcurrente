import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Tres maneras de ejecutar {@code Callable}s en un pool y recoger sus resultados con {@link Future}.
 *
 * <p>Un {@code Future} es un "vale" por un resultado que todavía no está: {@code pool.submit(tarea)}
 * vuelve enseguida con el vale, y {@code futuro.get()} espera hasta que la tarea termine y devuelve lo
 * que devolvió {@code call()}.
 *
 * <p>Todos los métodos reciben el pool ya creado: no crean hilos ni pools propios. Quien llama es el
 * dueño del pool y quien lo cierra.
 *
 * <p>Herramientas permitidas: {@code ExecutorService}, {@code Future}, {@code Callable}. Nada de
 * {@code CompletableFuture}, {@code parallelStream}, {@code ForkJoin}, {@code new Thread} ni
 * {@code Executors} (el pool ya te lo dan).
 */
public final class Paralelo {

    private Paralelo() {
    }

    /**
     * Ejecuta todas las tareas en el pool y devuelve sus resultados, en el mismo orden que las tareas.
     *
     * <ol>
     *   <li>Un bucle que envía <b>todas</b> las tareas con {@code pool.submit(tarea)} y guarda los
     *       {@code Future} en una lista.
     *   <li><b>Después</b>, otro bucle que hace {@code get()} de cada {@code Future}, en orden.
     * </ol>
     *
     * <p>Son dos bucles por la misma razón que en el ejercicio 1.1 {@code start()} y {@code join()}
     * van en bucles separados. Si hicieras {@code submit(t).get()} en el mismo bucle, esperarías a cada
     * tarea antes de enviar la siguiente, y se ejecutarían de a una.
     *
     * <p>No captures las excepciones de {@code get()}: dejalas salir. Si una tarea lanzó algo,
     * {@code get()} lanza {@code ExecutionException} con la excepción original adentro.
     *
     * @param tareas las tareas
     * @param pool   el pool donde ejecutarlas
     * @param <T>    tipo de los resultados
     * @return los resultados, en el orden de las tareas
     * @throws InterruptedException si interrumpen al hilo mientras espera un resultado
     * @throws ExecutionException   si alguna tarea lanzó una excepción
     */
    public static <T> List<T> ejecutarTodas(List<? extends Callable<T>> tareas, ExecutorService pool)
            throws InterruptedException, ExecutionException {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Paralelo.ejecutarTodas()");
    }

    /**
     * Cuenta los primos de {@code [2, hasta)} repartiendo el trabajo en {@code tramos} tareas
     * {@link TramoDePrimos}.
     *
     * <ol>
     *   <li>Partí {@code [2, hasta)} con {@link #partir(long, long, int)} y armá una lista con un
     *       {@code TramoDePrimos} por tramo.
     *   <li>{@code pool.invokeAll(lista)}: envía todas y espera a que terminen todas. Devuelve la lista
     *       de {@code Future}, ya listos.
     *   <li>Sumá los {@code get()} y devolvé el total.
     * </ol>
     *
     * <p>{@code invokeAll} es lo mismo que los dos bucles de {@link #ejecutarTodas(List, ExecutorService)},
     * ya hecho por el pool.
     *
     * @param hasta  límite del rango (excluido)
     * @param tramos en cuántas tareas repartir el trabajo
     * @param pool   el pool donde ejecutarlas
     * @return cuántos primos hay en {@code [2, hasta)}
     * @throws InterruptedException si interrumpen al hilo mientras espera
     * @throws ExecutionException   si algún tramo lanzó una excepción
     */
    public static long contarPrimos(long hasta, int tramos, ExecutorService pool)
            throws InterruptedException, ExecutionException {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Paralelo.contarPrimos()");
    }

    /**
     * Ejecuta una tarea esperando su resultado como máximo {@code maxMs}. Si no termina a tiempo, la
     * cancela y devuelve {@code Optional.empty()}.
     *
     * <pre>
     *   Future&lt;T&gt; futuro = pool.submit(tarea);
     *   try {
     *       return Optional.ofNullable(futuro.get(maxMs, TimeUnit.MILLISECONDS));
     *   } catch (TimeoutException e) {
     *       futuro.cancel(true);       ← true: interrumpe al hilo que la está ejecutando
     *       return Optional.empty();
     *   }
     * </pre>
     *
     * <p>Sin el {@code cancel(true)}, la tarea seguiría ocupando un hilo del pool aunque a nadie le
     * importe ya su resultado.
     *
     * @param tarea la tarea
     * @param pool  el pool donde ejecutarla
     * @param maxMs cuánto esperar el resultado, como máximo
     * @param <T>   tipo del resultado
     * @return el resultado, o {@code Optional.empty()} si no terminó a tiempo
     * @throws InterruptedException si interrumpen al hilo mientras espera
     * @throws ExecutionException   si la tarea lanzó una excepción
     */
    public static <T> Optional<T> conTiempoMaximo(Callable<T> tarea, ExecutorService pool, long maxMs)
            throws InterruptedException, ExecutionException {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Paralelo.conTiempoMaximo()");
    }

    /**
     * Parte {@code [desde, hasta)} en {@code partes} tramos consecutivos de tamaños parecidos (como el
     * {@code partir} del ejercicio 1.2). Ya está hecho.
     *
     * @param desde  principio del rango (incluido)
     * @param hasta  fin del rango (excluido)
     * @param partes cantidad de tramos
     * @return {@code partes} pares {@code {desde, hasta}}; si el rango está vacío, son tramos vacíos
     * @throws IllegalArgumentException si {@code partes < 1}
     */
    public static long[][] partir(long desde, long hasta, int partes) {
        if (partes < 1) {
            throw new IllegalArgumentException("cantidad de tramos inválida: " + partes);
        }
        long total = Math.max(0, hasta - desde);
        long base = total / partes;
        long resto = total % partes;
        long[][] tramos = new long[partes][];
        long inicio = desde;
        for (int i = 0; i < partes; i++) {
            long largo = base + (i < resto ? 1 : 0);
            tramos[i] = new long[] {inicio, inicio + largo};
            inicio += largo;
        }
        return tramos;
    }
}
