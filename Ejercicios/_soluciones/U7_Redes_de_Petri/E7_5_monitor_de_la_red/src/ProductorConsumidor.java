/**
 * El modelo del productor/consumidor con buffer acotado, como red de Petri. El buffer tiene una
 * sección crítica (meter o sacar un elemento), protegida por un lugar <i>Mutex</i> de la red.
 *
 * <pre>
 *   Lugares:                                   Transiciones:
 *   P0 Productor listo        (productores)    T0 TOMAR_HUECO:     Productor listo + Huecos + Mutex  → Productor produciendo
 *   P1 Productor produciendo  (0)              T1 DEPOSITAR:       Productor produciendo → Productor listo + Productos + Mutex
 *   P2 Huecos                 (capacidad)      T2 TOMAR_PRODUCTO:  Consumidor listo + Productos + Mutex → Consumidor consumiendo
 *   P3 Productos              (0)              T3 LIBERAR_HUECO:   Consumidor consumiendo → Consumidor listo + Huecos + Mutex
 *   P4 Mutex                  (1)
 *   P5 Consumidor listo       (consumidores)
 *   P6 Consumidor consumiendo (0)
 * </pre>
 *
 * <p>Es el buffer de tres semáforos del ejercicio 3.4 escrito como red: {@code Huecos} es el semáforo
 * de huecos, {@code Productos} el de items y {@code Mutex} el mutex. Con la red, el orden de
 * adquisición ya no lo elige cada hilo: está en la estructura (T0 toma el hueco y el mutex en un solo
 * disparo).
 */
public final class ProductorConsumidor {

    /** Índices de los lugares. */
    public static final int PRODUCTOR_LISTO = 0;
    public static final int PRODUCTOR_PRODUCIENDO = 1;
    public static final int HUECOS = 2;
    public static final int PRODUCTOS = 3;
    public static final int MUTEX = 4;
    public static final int CONSUMIDOR_LISTO = 5;
    public static final int CONSUMIDOR_CONSUMIENDO = 6;

    /** Índices de las transiciones. */
    public static final int TOMAR_HUECO = 0;
    public static final int DEPOSITAR = 1;
    public static final int TOMAR_PRODUCTO = 2;
    public static final int LIBERAR_HUECO = 3;

    private ProductorConsumidor() {
    }

    /**
     * Arma la red de la tabla de la clase: {@code pre} y {@code post} de 7 lugares por 4 transiciones
     * (todos los arcos de peso 1) y el marcado inicial.
     *
     * <p>Usá las constantes: {@code pre[HUECOS][TOMAR_HUECO] = 1} se lee mejor que {@code pre[2][0] = 1}.
     *
     * @param capacidad    fichas iniciales de Huecos
     * @param productores  fichas iniciales de Productor listo
     * @param consumidores fichas iniciales de Consumidor listo
     * @return la red, con su marcado inicial
     */
    public static RedDePetri crear(int capacidad, int productores, int consumidores) {
        int[][] pre = new int[7][4];
        int[][] post = new int[7][4];
        pre[PRODUCTOR_LISTO][TOMAR_HUECO] = 1;
        pre[HUECOS][TOMAR_HUECO] = 1;
        pre[MUTEX][TOMAR_HUECO] = 1;
        post[PRODUCTOR_PRODUCIENDO][TOMAR_HUECO] = 1;

        pre[PRODUCTOR_PRODUCIENDO][DEPOSITAR] = 1;
        post[PRODUCTOR_LISTO][DEPOSITAR] = 1;
        post[PRODUCTOS][DEPOSITAR] = 1;
        post[MUTEX][DEPOSITAR] = 1;

        pre[CONSUMIDOR_LISTO][TOMAR_PRODUCTO] = 1;
        pre[PRODUCTOS][TOMAR_PRODUCTO] = 1;
        pre[MUTEX][TOMAR_PRODUCTO] = 1;
        post[CONSUMIDOR_CONSUMIENDO][TOMAR_PRODUCTO] = 1;

        pre[CONSUMIDOR_CONSUMIENDO][LIBERAR_HUECO] = 1;
        post[CONSUMIDOR_LISTO][LIBERAR_HUECO] = 1;
        post[HUECOS][LIBERAR_HUECO] = 1;
        post[MUTEX][LIBERAR_HUECO] = 1;

        int[] m0 = new int[7];
        m0[PRODUCTOR_LISTO] = productores;
        m0[HUECOS] = capacidad;
        m0[MUTEX] = 1;
        m0[CONSUMIDOR_LISTO] = consumidores;
        return new RedDePetri(pre, post, m0);
    }

    /**
     * Los P-invariantes de la red, uno por fila (un peso por lugar). Son cuatro:
     *
     * <ol>
     *   <li>Los productores: siempre hay la misma cantidad, listos o produciendo.
     *   <li>Los consumidores: lo mismo.
     *   <li>La sección crítica: el mutex, más los que están adentro (un productor produciendo o un
     *       consumidor consumiendo), suman 1. De ahí sale que nunca hay dos adentro.
     *   <li>El buffer: los huecos, los productos y los que están adentro moviendo un elemento suman la
     *       capacidad.
     * </ol>
     *
     * <p>Encontralos mirando la tabla de la clase: para cada transición, lo que saca de los lugares del
     * invariante tiene que ser igual a lo que pone. Fijate que no dependen de la capacidad ni de la
     * cantidad de hilos: solo de la estructura.
     *
     * @return una matriz de 4 filas y 7 columnas
     */
    public static int[][] pInvariantes() {
        int[][] y = new int[4][7];
        y[0][PRODUCTOR_LISTO] = 1;
        y[0][PRODUCTOR_PRODUCIENDO] = 1;
        y[1][CONSUMIDOR_LISTO] = 1;
        y[1][CONSUMIDOR_CONSUMIENDO] = 1;
        y[2][MUTEX] = 1;
        y[2][PRODUCTOR_PRODUCIENDO] = 1;
        y[2][CONSUMIDOR_CONSUMIENDO] = 1;
        y[3][HUECOS] = 1;
        y[3][PRODUCTOS] = 1;
        y[3][PRODUCTOR_PRODUCIENDO] = 1;
        y[3][CONSUMIDOR_CONSUMIENDO] = 1;
        return y;
    }
}
