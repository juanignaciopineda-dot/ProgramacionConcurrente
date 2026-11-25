import java.util.ArrayList;
import java.util.List;

/**
 * Suma un arreglo grande repartiéndolo entre {@code k} hilos: cada hilo suma un tramo y al final el
 * hilo principal junta los resultados parciales.
 *
 * <p>Es el esquema más simple de paralelismo de datos (<i>fork/join</i> a mano):
 *
 * <pre>
 *            ┌─ sumador-1: [0, 4)  ─┐
 *   datos ───┼─ sumador-2: [4, 7)  ─┼──► join() de todos ──► total
 *            └─ sumador-3: [7, 10) ─┘
 * </pre>
 *
 * <p>Los pasos están separados en métodos para que puedas probarlos de a uno.
 */
public final class SumaParalela {

    private SumaParalela() {}

    /**
     * Parte {@code n} posiciones en {@code k} tramos contiguos, lo más parejos posible.
     *
     * <p>Cada tramo es un arreglo {@code {desde, hasta}}, con {@code hasta} exclusive. Los tramos:
     *
     * <ul>
     *   <li>van en orden y cubren {@code [0, n)} sin huecos ni solapamientos;
     *   <li>tienen tamaños que difieren a lo sumo en 1: miden {@code n / k}, y los primeros
     *       {@code n % k} tramos tienen un elemento más;
     *   <li>pueden ser vacíos si hay más tramos que posiciones.
     * </ul>
     *
     * <p>Ejemplos:
     *
     * <pre>
     *   partir(10, 3) → {{0, 4}, {4, 7}, {7, 10}}
     *   partir(9, 3)  → {{0, 3}, {3, 6}, {6, 9}}
     *   partir(2, 4)  → {{0, 1}, {1, 2}, {2, 2}, {2, 2}}
     * </pre>
     *
     * <p>Pista: llevá una variable {@code desde} que arranca en 0; para cada tramo {@code i}, calculá
     * su tamaño y avanzá.
     *
     * @param n cantidad de posiciones a repartir
     * @param k cantidad de tramos
     * @return un arreglo de {@code k} tramos {@code {desde, hasta}}
     * @throws IllegalArgumentException si {@code n < 0} o {@code k < 1}
     */
    public static int[][] partir(int n, int k) {
        if (n < 0 || k < 1) {
            throw new IllegalArgumentException("n=" + n + ", k=" + k);
        }
        int[][] tramos = new int[k][];
        int desde = 0;
        for (int i = 0; i < k; i++) {
            int tamanio = n / k + (i < n % k ? 1 : 0);
            tramos[i] = new int[] {desde, desde + tamanio};
            desde += tamanio;
        }
        return tramos;
    }

    /**
     * Crea un {@link SumadorDeTramo} por cada tramo de {@code partir(datos.length, k)}, en orden.
     *
     * @param datos arreglo a sumar
     * @param k cantidad de tramos (y de sumadores)
     * @return los {@code k} sumadores, todavía sin ejecutar
     * @throws IllegalArgumentException si {@code k < 1}
     */
    public static List<SumadorDeTramo> crearSumadores(int[] datos, int k) {
        List<SumadorDeTramo> sumadores = new ArrayList<>();
        for (int[] tramo : partir(datos.length, k)) {
            sumadores.add(new SumadorDeTramo(datos, tramo[0], tramo[1]));
        }
        return sumadores;
    }

    /**
     * Crea un hilo por sumador, con nombres {@code "sumador-1"}, {@code "sumador-2"}, ..., y los
     * arranca a todos. <b>No</b> espera a que terminen: eso lo hace
     * {@link #esperarYCombinar(List, List)}.
     *
     * @param sumadores tareas a ejecutar
     * @return los hilos, en el mismo orden que los sumadores, ya arrancados
     */
    public static List<Thread> lanzar(List<SumadorDeTramo> sumadores) {
        List<Thread> hilos = new ArrayList<>();
        for (int i = 0; i < sumadores.size(); i++) {
            Thread hilo = new Thread(sumadores.get(i), "sumador-" + (i + 1));
            hilos.add(hilo);
            hilo.start();
        }
        return hilos;
    }

    /**
     * Espera a que terminen todos los hilos y devuelve la suma de los resultados parciales.
     *
     * <p>Primero hacé {@code join()} a <b>todos</b> los hilos y recién después leé
     * {@link SumadorDeTramo#getResultado()}. Si leés antes, podés encontrar un 0 (el sumador todavía
     * no terminó) o, peor, un valor que el hilo ya escribió pero que el hilo principal todavía no ve:
     * {@code join()} es lo que garantiza que lo que hizo el otro hilo sea visible.
     *
     * @param hilos los hilos devueltos por {@link #lanzar(List)}
     * @param sumadores los sumadores que corren esos hilos
     * @return la suma de todos los resultados parciales
     * @throws InterruptedException si el hilo que llama es interrumpido mientras espera
     */
    public static long esperarYCombinar(List<Thread> hilos, List<SumadorDeTramo> sumadores)
            throws InterruptedException {
        for (Thread hilo : hilos) {
            hilo.join();
        }
        long total = 0;
        for (SumadorDeTramo sumador : sumadores) {
            total += sumador.getResultado();
        }
        return total;
    }

    /**
     * Suma el arreglo con {@code k} hilos, usando los tres métodos anteriores en orden:
     * {@link #crearSumadores(int[], int)}, {@link #lanzar(List)} y
     * {@link #esperarYCombinar(List, List)}.
     *
     * @param datos arreglo a sumar
     * @param k cantidad de hilos
     * @return la suma de todos los elementos
     * @throws InterruptedException si el hilo que llama es interrumpido mientras espera
     * @throws IllegalArgumentException si {@code k < 1}
     */
    public static long sumar(int[] datos, int k) throws InterruptedException {
        List<SumadorDeTramo> sumadores = crearSumadores(datos, k);
        return esperarYCombinar(lanzar(sumadores), sumadores);
    }

    /**
     * Suma el arreglo en el hilo actual, sin hilos extra. Ya está hecho: sirve para comparar.
     *
     * @param datos arreglo a sumar
     * @return la suma de todos los elementos
     */
    public static long sumarSecuencial(int[] datos) {
        long total = 0;
        for (int dato : datos) {
            total += dato;
        }
        return total;
    }
}
