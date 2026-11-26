/**
 * Una red de Petri marcada {@code <P, T, Pre, Post, M0>}, con pesos enteros.
 *
 * <pre>
 *   pre[p][t]    peso del arco del lugar p a la transición t   (0 si no hay arco)
 *   post[p][t]   peso del arco de la transición t al lugar p   (0 si no hay arco)
 *   C = post - pre                                             (la matriz de incidencia)
 *   marcado[p]   fichas en el lugar p                          (el estado de la red)
 * </pre>
 *
 * <p>Las dos reglas de la teoría:
 *
 * <ul>
 *   <li><b>Sensibilizado</b>: {@code t} está sensibilizada en {@code M} si, para todo lugar {@code p},
 *       {@code M(p) >= Pre(p, t)}.
 *   <li><b>Disparo</b> (la ecuación de estado): {@code M' = M + C(·, t)}, la columna {@code t} de la
 *       matriz de incidencia sumada al marcado. Es indivisible: o no disparó o disparó completa.
 * </ul>
 *
 * <p>Ojo con la tentación de decidir el sensibilizado con la matriz de incidencia ("está sensibilizada
 * si {@code M + C(·, t)} no tiene componentes negativas"). Solo vale en redes <b>puras</b>, sin
 * auto-lazos. Si un lugar es entrada y salida de la misma transición, en {@code C} el arco de ida y
 * el de vuelta se cancelan, y la incidencia ya no dice que la transición necesita esa ficha.
 *
 * <p>Esta clase no es thread-safe: en la Unidad 7 se usa desde un solo hilo, y en el ejercicio 7.5 la
 * protege el monitor.
 */
public class RedDePetri {

    private final int[][] pre;
    private final int[][] post;
    private final int[][] incidencia;
    private final int[] marcado;

    /**
     * Crea la red.
     *
     * <ol>
     *   <li>{@link #validar(int[][], int[][], int[])}, que ya está hecho.
     *   <li>Guardá <b>copias</b> de {@code pre}, {@code post} y {@code marcadoInicial} (con
     *       {@link #copiar(int[][])} y {@code clone()}): si guardaras los arreglos que te pasan, quien
     *       los creó podría cambiarle la red desde afuera.
     *   <li>Calculá {@code incidencia[p][t] = post[p][t] - pre[p][t]}.
     * </ol>
     *
     * @param pre            pesos de los arcos de entrada, {@code lugares x transiciones}
     * @param post           pesos de los arcos de salida, {@code lugares x transiciones}
     * @param marcadoInicial fichas de cada lugar
     * @throws IllegalArgumentException si las dimensiones no coinciden o hay valores negativos
     */
    public RedDePetri(int[][] pre, int[][] post, int[] marcadoInicial) {
        validar(pre, post, marcadoInicial);
        this.pre = copiar(pre);
        this.post = copiar(post);
        this.marcado = marcadoInicial.clone();
        this.incidencia = new int[pre.length][pre[0].length];
        for (int p = 0; p < pre.length; p++) {
            for (int t = 0; t < pre[0].length; t++) {
                incidencia[p][t] = post[p][t] - pre[p][t];
            }
        }
    }

    /**
     * ¿Está sensibilizada {@code t} en el marcado actual? Con {@code pre}: cada lugar tiene que tener
     * al menos tantas fichas como el peso de su arco hacia {@code t}.
     *
     * @param t una transición
     * @return {@code true} si {@code t} se puede disparar
     * @throws IllegalArgumentException si {@code t} no existe (usá {@link #validarTransicion(int)})
     */
    public boolean estaSensibilizada(int t) {
        validarTransicion(t);
        for (int p = 0; p < marcado.length; p++) {
            if (marcado[p] < pre[p][t]) {
                return false;
            }
        }
        return true;
    }

    /**
     * El vector de sensibilizado: {@code estaSensibilizada(t)} para cada transición.
     *
     * @return un {@code boolean} por transición
     */
    public boolean[] sensibilizadas() {
        boolean[] e = new boolean[transiciones()];
        for (int t = 0; t < e.length; t++) {
            e[t] = estaSensibilizada(t);
        }
        return e;
    }

    /**
     * Dispara {@code t}: si no está sensibilizada lanza {@code IllegalStateException} sin tocar nada;
     * si lo está, aplica la ecuación de estado {@code M = M + C(·, t)}.
     *
     * @param t la transición a disparar
     * @throws IllegalStateException    si {@code t} no está sensibilizada
     * @throws IllegalArgumentException si {@code t} no existe
     */
    public void disparar(int t) {
        if (!estaSensibilizada(t)) {
            throw new IllegalStateException("T" + t + " no está sensibilizada");
        }
        for (int p = 0; p < marcado.length; p++) {
            marcado[p] += incidencia[p][t];
        }
    }

    /**
     * La ecuación fundamental: el marcado al que se llegaría desde el actual disparando cada
     * transición {@code t} la cantidad de veces {@code s[t]}, sin importar el orden:
     * {@code M + C · s}. No modifica la red.
     *
     * <p>Solo hace la cuenta: no verifica que exista una secuencia de disparos válida con ese vector
     * característico. Que la aritmética cierre no quiere decir que la secuencia se pueda disparar.
     *
     * @param s el vector característico: cuántas veces se dispara cada transición
     * @return el marcado resultante (puede tener componentes negativas si {@code s} es imposible)
     * @throws IllegalArgumentException si {@code s} no tiene una componente por transición
     */
    public int[] ecuacionDeEstado(int[] s) {
        if (s == null || s.length != transiciones()) {
            throw new IllegalArgumentException("el vector tiene que tener " + transiciones() + " componentes");
        }
        int[] resultado = marcado.clone();
        for (int p = 0; p < marcado.length; p++) {
            for (int t = 0; t < s.length; t++) {
                resultado[p] += incidencia[p][t] * s[t];
            }
        }
        return resultado;
    }

    /**
     * Los conflictos estructurales: {@code c[t1][t2]} es {@code true} si {@code t1 != t2} y las dos
     * comparten al menos un lugar de entrada ({@code pre[p][t1] > 0 && pre[p][t2] > 0}). La matriz es
     * simétrica y tiene la diagonal en {@code false}.
     *
     * <p>Es estructural: no mira el marcado. Que dos transiciones compitan "en el papel" no quiere decir
     * que compitan en un marcado dado (eso es el conflicto <i>efectivo</i>).
     *
     * @return la matriz de conflictos, {@code transiciones x transiciones}
     */
    public boolean[][] conflictosEstructurales() {
        int n = transiciones();
        boolean[][] c = new boolean[n][n];
        for (int t1 = 0; t1 < n; t1++) {
            for (int t2 = 0; t2 < n; t2++) {
                if (t1 == t2) {
                    continue;
                }
                for (int p = 0; p < pre.length; p++) {
                    if (pre[p][t1] > 0 && pre[p][t2] > 0) {
                        c[t1][t2] = true;
                        break;
                    }
                }
            }
        }
        return c;
    }

    /** @return la cantidad de lugares. Ya está hecho. */
    public int lugares() {
        return marcado.length;
    }

    /** @return la cantidad de transiciones. Ya está hecho. */
    public int transiciones() {
        return incidencia[0].length;
    }

    /** @return una copia del marcado actual. Ya está hecho. */
    public int[] getMarcado() {
        return marcado.clone();
    }

    /** @return una copia de la matriz de incidencia. Ya está hecho. */
    public int[][] getIncidencia() {
        return copiar(incidencia);
    }

    /**
     * Ya está hecho.
     *
     * @param t una transición
     * @throws IllegalArgumentException si {@code t} no existe
     */
    protected void validarTransicion(int t) {
        if (t < 0 || t >= transiciones()) {
            throw new IllegalArgumentException("no existe la transición T" + t);
        }
    }

    /** Ya está hecho: controla que las dimensiones coincidan y que no haya negativos. */
    private static void validar(int[][] pre, int[][] post, int[] marcado) {
        if (pre == null || post == null || marcado == null) {
            throw new IllegalArgumentException("pre, post y el marcado no pueden ser null");
        }
        if (pre.length == 0 || pre.length != post.length || pre.length != marcado.length) {
            throw new IllegalArgumentException("pre, post y el marcado tienen que tener una fila por lugar");
        }
        int transiciones = pre[0].length;
        if (transiciones == 0) {
            throw new IllegalArgumentException("la red tiene que tener al menos una transición");
        }
        for (int p = 0; p < pre.length; p++) {
            if (pre[p].length != transiciones || post[p].length != transiciones) {
                throw new IllegalArgumentException("la fila " + p + " no tiene " + transiciones + " columnas");
            }
            if (marcado[p] < 0) {
                throw new IllegalArgumentException("marcado negativo en P" + p);
            }
            for (int t = 0; t < transiciones; t++) {
                if (pre[p][t] < 0 || post[p][t] < 0) {
                    throw new IllegalArgumentException("peso negativo en P" + p + ", T" + t);
                }
            }
        }
    }

    /** Ya está hecho: copia una matriz fila por fila. */
    private static int[][] copiar(int[][] m) {
        int[][] copia = new int[m.length][];
        for (int i = 0; i < m.length; i++) {
            copia[i] = m[i].clone();
        }
        return copia;
    }
}
