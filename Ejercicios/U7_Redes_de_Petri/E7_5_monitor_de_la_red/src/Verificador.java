import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Verificador automático del ejercicio 7.5. No hace falta modificarlo. */
public class Verificador {

    private static final int T0 = ProductorConsumidor.TOMAR_HUECO;
    private static final int T1 = ProductorConsumidor.DEPOSITAR;
    private static final int T2 = ProductorConsumidor.TOMAR_PRODUCTO;
    private static final int T3 = ProductorConsumidor.LIBERAR_HUECO;

    /** Los P-invariantes esperados (productores, consumidores, sección crítica, buffer). */
    private static final int[][] INVARIANTES = {
        {1, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 1},
        {0, 1, 0, 0, 1, 0, 1},
        {0, 1, 1, 1, 0, 0, 1},
    };

    public static void main(String[] args) {
        titulo("Ejercicio 7.5 - El monitor de la red");

        caso("ProductorConsumidor.crear: la red de la tabla (incidencia y marcado inicial)", 2000, () -> {
            RedDePetri red = ProductorConsumidor.crear(3, 2, 4);
            iguales(Arrays.deepToString(esperada(3, 2, 4).getIncidencia()), Arrays.deepToString(red.getIncidencia()),
                    "matriz de incidencia (una fila por lugar, P0 a P6)");
            iguales("[2, 0, 3, 0, 1, 4, 0]", Arrays.toString(red.getMarcado()), "marcado inicial");
            iguales("[true, false, false, false]", Arrays.toString(red.sensibilizadas()),
                    "sensibilizadas al arrancar (solo puede empezar un productor)");
        });

        caso("ProductorConsumidor.pInvariantes: cuatro P-invariantes, con las constantes del enunciado", 2000, () -> {
            int[][] y = ProductorConsumidor.pInvariantes();
            iguales(4, y.length, "cantidad de invariantes");
            int[][] c = esperada(3, 2, 4).getIncidencia();
            int[] m0 = {2, 0, 3, 0, 1, 4, 0};
            String[] que = {"productores", "consumidores", "sección crítica", "buffer"};
            int[] constantes = {2, 4, 1, 3};
            for (int k = 0; k < 4; k++) {
                iguales(7, y[k].length, "componentes del invariante de " + que[k]);
                for (int t = 0; t < 4; t++) {
                    int suma = 0;
                    for (int p = 0; p < 7; p++) {
                        suma += y[k][p] * c[p][t];
                    }
                    iguales(0, suma, "yᵀ·C en T" + t + " para el invariante de " + que[k]);
                }
                int valor = 0;
                for (int p = 0; p < 7; p++) {
                    valor += y[k][p] * m0[p];
                }
                iguales(constantes[k], valor, "constante del invariante de " + que[k]
                        + " con 2 productores, 4 consumidores y capacidad 3");
            }
        });

        caso("MonitorDeRed: un solo hilo deposita un elemento", 2000, () -> {
            esperaExcepcion(IllegalArgumentException.class, () -> new MonitorDeRed(esperada(2, 1, 1), null), "política null");
            MonitorDeRed m = new MonitorDeRed(esperada(2, 1, 1), new PoliticaPrimera());
            m.dispararTransicion(T0);
            m.dispararTransicion(T1);
            iguales("[1, 0, 1, 1, 1, 1, 0]", Arrays.toString(m.getMarcado()), "marcado después de un depósito");
            iguales(List.of(T0, T1), m.getHistorial(), "historial");
            esperaExcepcion(IllegalArgumentException.class, () -> m.dispararTransicion(7), "transición inexistente");
        });

        caso("Con el buffer vacío, el consumidor espera (WAITING) en la cola de TOMAR_PRODUCTO", 3000, () -> {
            MonitorDeRed m = new MonitorDeRed(esperada(2, 1, 1), new PoliticaPrimera());
            Thread consumidor = new Thread(() -> m.dispararTransicion(T2), "consumidor");
            consumidor.start();
            verificar(esperarEstado(consumidor, Thread.State.WAITING, 1000), "el consumidor tendría que estar"
                    + " dormido en la cola de TOMAR_PRODUCTO y está " + consumidor.getState()
                    + (consumidor.getState() == Thread.State.RUNNABLE ? ": ¿espera activa?" : ""));
            iguales(1, m.esperando(T2), "hilos en la cola de TOMAR_PRODUCTO");
            m.dispararTransicion(T0);
            m.dispararTransicion(T1);
            consumidor.join(1000);
            verificar(!consumidor.isAlive(), "DEPOSITAR sensibilizó TOMAR_PRODUCTO y el consumidor no se despertó");
            iguales(List.of(T0, T1, T2), m.getHistorial(), "historial");
            iguales(0, m.esperando(T2), "hilos en la cola de TOMAR_PRODUCTO al final");
            iguales(1, permisosDelMutex(m), "permisos del mutex al final (con más de 1, alguien lo soltó después de"
                    + " pasárselo al despertado: con signal and exit, el que despierta no lo suelta)");
        });

        caso("La política elige entre las despertables (sensibilizadas y con gente)", 3000, () -> {
            List<String> consultas = new ArrayList<>();
            Politica laSegunda = despertables -> {
                consultas.add(Arrays.toString(despertables));
                return despertables[1] ? 1 : new PoliticaPrimera().elegir(despertables);
            };
            MonitorDeRed m = new MonitorDeRed(conflicto(), laSegunda);
            Thread a = new Thread(() -> m.dispararTransicion(0), "A");
            Thread b = new Thread(() -> m.dispararTransicion(1), "B");
            a.start();
            esperarEstado(a, Thread.State.WAITING, 1000);
            b.start();
            esperarEstado(b, Thread.State.WAITING, 1000);
            m.dispararTransicion(2);
            b.join(1000);
            verificar(!b.isAlive(), "la política eligió T1 y B no se despertó");
            verificar(a.isAlive(), "A no tendría que haber disparado: había una sola ficha, y la política eligió a B");
            m.dispararTransicion(2);
            a.join(1000);
            verificar(!a.isAlive(), "A no se despertó con la segunda ficha");
            iguales(List.of(2, 1, 2, 0), m.getHistorial(), "historial");
            iguales(List.of("[true, true, false]", "[true, false, false]"), consultas, "lo que recibió la política (T2"
                    + " siempre está sensibilizada, pero no tiene a nadie esperando; y cuando no hay nadie para"
                    + " despertar, no se la consulta)");
        });

        caso("2 productores y 2 consumidores, buffer de 3, 400 vueltas: invariantes en cada disparo", 30000, () -> {
            String[] problema = {null};
            RedDePetri base = esperada(3, 2, 2);
            int[] constantes = new int[INVARIANTES.length];
            for (int k = 0; k < constantes.length; k++) {
                constantes[k] = valor(INVARIANTES[k], base.getMarcado());
            }
            RedDePetri vigilada = new RedDePetri(PRE, POST, base.getMarcado()) {
                @Override
                public void disparar(int t) {
                    super.disparar(t);
                    int[] m = getMarcado();
                    for (int k = 0; k < INVARIANTES.length; k++) {
                        if (valor(INVARIANTES[k], m) != constantes[k] && problema[0] == null) {
                            problema[0] = "después de disparar T" + t + " se violó el P-invariante " + k + ": "
                                    + Arrays.toString(m) + (k == 2 ? " (hubo dos hilos en la sección crítica)" : "");
                        }
                    }
                }
            };
            MonitorDeRed m = new MonitorDeRed(vigilada, new PoliticaPrimera());
            List<Thread> hilos = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                hilos.add(new Thread(new Productor(m, 400), "productor-" + i));
                hilos.add(new Thread(new Consumidor(m, 400), "consumidor-" + i));
            }
            for (Thread h : hilos) {
                h.start();
            }
            for (Thread h : hilos) {
                h.join(20_000);
                verificar(!h.isAlive(), "el " + h.getName() + " quedó esperando (estado " + h.getState() + "): ¿un"
                        + " aviso perdido? ¿se despierta a alguien después de cada disparo?");
            }
            verificar(problema[0] == null, problema[0]);
            iguales("[800, 800, 800, 800]", Arrays.toString(m.getDisparos()), "disparos de cada transición");
            iguales(Arrays.toString(base.getMarcado()), Arrays.toString(m.getMarcado()), "marcado final (tiene que"
                    + " volver a M0)");
            iguales(1, permisosDelMutex(m), "permisos del mutex al final (con más de 1, alguien lo soltó después de"
                    + " pasárselo a un despertado)");
        });

        prohibir(new String[] {"MonitorDeRed.java"}, "synchronized", "java.util.concurrent.locks", ".wait(", "notify");
        prohibir(new String[] {"Productor.java", "Consumidor.java"}, "synchronized", "Semaphore", "Lock", ".wait(",
                "notify");

        fin();
    }

    private static final int[][] PRE = new int[7][4];
    private static final int[][] POST = new int[7][4];

    static {
        int[][][] entradasYSalidas = {
            {{0, 2, 4}, {1}},
            {{1}, {0, 3, 4}},
            {{5, 3, 4}, {6}},
            {{6}, {5, 2, 4}},
        };
        for (int t = 0; t < 4; t++) {
            for (int p : entradasYSalidas[t][0]) {
                PRE[p][t] = 1;
            }
            for (int p : entradasYSalidas[t][1]) {
                POST[p][t] = 1;
            }
        }
    }

    /** La red de la tabla de {@link ProductorConsumidor}, armada por el verificador. */
    private static RedDePetri esperada(int capacidad, int productores, int consumidores) {
        return new RedDePetri(PRE, POST, new int[] {productores, 0, capacidad, 0, 1, consumidores, 0});
    }

    /** T0 y T1 compiten por la ficha de P0; T2 (fuente) pone una ficha en P0 cada vez que dispara. */
    private static RedDePetri conflicto() {
        int[][] pre = {{1, 1, 0}, {0, 0, 0}, {0, 0, 0}};
        int[][] post = {{0, 0, 1}, {1, 0, 0}, {0, 1, 0}};
        return new RedDePetri(pre, post, new int[] {0, 0, 0});
    }

    private static int valor(int[] y, int[] m) {
        int suma = 0;
        for (int p = 0; p < y.length; p++) {
            suma += y[p] * m[p];
        }
        return suma;
    }

    /**
     * Cuántos permisos tiene el mutex del monitor. Lo lee por reflexión, porque el campo es privado: es
     * la única forma de ver desde afuera si alguien lo soltó de más.
     */
    private static int permisosDelMutex(MonitorDeRed m) throws Exception {
        java.lang.reflect.Field campo = MonitorDeRed.class.getDeclaredField("mutex");
        campo.setAccessible(true);
        return ((java.util.concurrent.Semaphore) campo.get(m)).availablePermits();
    }

    // =====================================================================
    // Infraestructura del verificador. No hace falta leerla ni modificarla.
    // =====================================================================

    private static int casosOk = 0;
    private static int casosTotales = 0;

    /** Excepciones no capturadas en hilos creados durante el caso actual, y sus mensajes. */
    private static final java.util.List<Throwable> MUERTES = new java.util.ArrayList<>();

    private static final java.util.List<String> NOTAS = new java.util.ArrayList<>();

    /** Cuerpo de un caso de prueba. */
    private interface Cuerpo {
        void correr() throws Exception;
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("=== " + legible(texto) + " ===");
        System.out.println();
        Thread.setDefaultUncaughtExceptionHandler((hilo, e) -> {
            synchronized (MUERTES) {
                MUERTES.add(e);
                if (!(e instanceof UnsupportedOperationException)) {
                    NOTAS.add("(el hilo '" + hilo.getName() + "' terminó con " + e + ")");
                }
            }
        });
    }

    /**
     * Corre un caso en un hilo aparte, con un tiempo máximo, para poder detectar programas
     * que se cuelgan (deadlock, un wait() que nadie despierta, un join() a un hilo eterno).
     * Si alguno se cuelga, el System.exit() del final termina el programa igual.
     */
    private static void caso(String nombre, long timeoutMs, Cuerpo cuerpo) {
        casosTotales++;
        Throwable[] error = new Throwable[1];
        Thread hilo = new Thread(() -> {
            try {
                cuerpo.correr();
            } catch (Throwable e) {
                error[0] = e;
            }
        }, "verificador");
        hilo.start();
        try {
            hilo.join(timeoutMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (hilo.isAlive()) {
            String detalle = "no terminó en " + timeoutMs + " ms: ¿un hilo que nadie despierta,"
                    + " un deadlock o un join() a un hilo que nunca termina?";
            String ciclo = describirDeadlock();
            if (ciclo != null) {
                detalle += "\n" + ciclo;
            }
            informar("[COLGADO]", nombre, detalle);
            hilo.interrupt();
            return;
        }
        Throwable e = error[0];
        UnsupportedOperationException todoEnOtroHilo = null;
        synchronized (MUERTES) {
            for (Throwable muerte : MUERTES) {
                if (muerte instanceof UnsupportedOperationException u) {
                    todoEnOtroHilo = u;
                }
            }
            MUERTES.clear();
        }
        if ((e == null || e instanceof AssertionError) && todoEnOtroHilo != null) {
            e = todoEnOtroHilo;
        }
        if (e == null) {
            casosOk++;
            informar("[OK]", nombre, null);
        } else if (e instanceof UnsupportedOperationException) {
            informar("[TODO]", nombre, "falta implementar: " + e.getMessage());
        } else if (e instanceof AssertionError) {
            informar("[FALLA]", nombre, e.getMessage());
        } else {
            informar("[ERROR]", nombre, "excepción inesperada: " + e);
        }
        synchronized (MUERTES) {
            for (String nota : NOTAS) {
                System.out.println("           " + legible(nota));
            }
            NOTAS.clear();
        }
    }

    /**
     * Si hay hilos trabados en una espera circular de monitores o locks (no semáforos, que no tienen
     * dueño), devuelve quién espera a quién. Si no hay, devuelve null.
     */
    private static String describirDeadlock() {
        try {
            var mx = java.lang.management.ManagementFactory.getThreadMXBean();
            long[] ids = mx.findDeadlockedThreads();
            if (ids == null) {
                return null;
            }
            StringBuilder texto = new StringBuilder("La JVM detectó un deadlock (espera circular):");
            for (var hilo : mx.getThreadInfo(ids)) {
                if (hilo != null) {
                    String llave = hilo.getLockName() == null ? "un lock"
                            : hilo.getLockName().replaceAll("^.*\\.", "").replaceAll("\\$(Nonfair|Fair)Sync", "");
                    texto.append("\n  '").append(hilo.getThreadName()).append("' espera ").append(llave)
                            .append(", que tiene '").append(hilo.getLockOwnerName()).append("'");
                }
            }
            return texto.toString();
        } catch (Throwable e) {
            return null;
        }
    }

    private static void informar(String etiqueta, String nombre, String detalle) {
        System.out.printf("%-10s %s%n", etiqueta, legible(nombre));
        if (detalle != null) {
            for (String linea : detalle.split("\n")) {
                System.out.println("           " + legible(linea));
            }
        }
    }

    /**
     * Si la consola no puede mostrar algún símbolo (λ, ∅, →, α...), lo cambia por una versión en ASCII,
     * para que no salga como '?'. Con una consola en UTF-8 el texto queda igual.
     */
    private static String legible(String texto) {
        java.nio.charset.CharsetEncoder codificador = System.out.charset().newEncoder();
        if (texto == null || codificador.canEncode(texto)) {
            return texto;
        }
        StringBuilder resultado = new StringBuilder();
        for (char c : texto.toCharArray()) {
            if (codificador.canEncode(c)) {
                resultado.append(c);
                continue;
            }
            resultado.append(switch (c) {
                case 'λ' -> "lambda";
                case '∅' -> "{}";
                case '→' -> "->";
                case '⇒' -> "=>";
                case '⊢' -> "|-";
                case '∈' -> "en";
                case '∉' -> "no en";
                case '∪' -> "U";
                case '∧' -> "y";
                case '−' -> "-";
                case '≤' -> "<=";
                case '≥' -> ">=";
                case '≠' -> "!=";
                case '∞' -> "infinito";
                case 'α' -> "alfa";
                case 'β' -> "beta";
                case '⁰' -> "^0";
                case '¹' -> "^1";
                case '²' -> "^2";
                case '³' -> "^3";
                case '⁴' -> "^4";
                case '⁺' -> "^+";
                case 'ⁱ' -> "^i";
                case 'ⁿ' -> "^n";
                case 'ᵐ' -> "^m";
                case 'ᵀ' -> "^T";
                default -> "?";
            });
        }
        return resultado.toString();
    }

    /** Muestra un dato informativo debajo del resultado del caso (no cuenta como caso). */
    private static void info(String texto) {
        synchronized (MUERTES) {
            NOTAS.add("> " + texto);
        }
    }

    private static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    private static void iguales(Object esperado, Object obtenido, String que) {
        if (!java.util.Objects.equals(esperado, obtenido)) {
            throw new AssertionError(que + ": se esperaba <" + esperado + "> pero dio <" + obtenido + ">");
        }
    }

    private static void esperaExcepcion(Class<? extends Throwable> tipo, Cuerpo cuerpo, String que)
            throws Exception {
        try {
            cuerpo.correr();
        } catch (UnsupportedOperationException e) {
            throw e;
        } catch (Throwable e) {
            if (tipo.isInstance(e)) {
                return;
            }
            throw new AssertionError(que + ": se esperaba " + tipo.getSimpleName() + " pero se lanzó " + e);
        }
        throw new AssertionError(que + ": se esperaba " + tipo.getSimpleName() + " y no se lanzó nada");
    }

    /** Espera (como máximo maxMs) a que el hilo llegue al estado pedido. */
    private static boolean esperarEstado(Thread t, Thread.State estado, long maxMs) throws InterruptedException {
        long limite = System.currentTimeMillis() + maxMs;
        while (System.currentTimeMillis() < limite) {
            if (t.getState() == estado) {
                return true;
            }
            Thread.sleep(5);
        }
        return t.getState() == estado;
    }

    /**
     * Lee un archivo fuente del ejercicio sin comentarios ni literales de texto, para buscar usos
     * de clases prohibidas. Devuelve null si no lo encuentra.
     */
    private static String fuenteSinComentarios(String archivo) {
        java.util.List<java.nio.file.Path> candidatos = new java.util.ArrayList<>();
        candidatos.add(java.nio.file.Path.of("src", archivo));
        candidatos.add(java.nio.file.Path.of(archivo));
        try {
            var origen = Verificador.class.getProtectionDomain().getCodeSource();
            if (origen != null && origen.getLocation() != null) {
                java.nio.file.Path base = java.nio.file.Path.of(origen.getLocation().toURI());
                candidatos.add(base.resolve(archivo));
                candidatos.add(base.resolveSibling("src").resolve(archivo));
            }
        } catch (Exception ignorada) {
            // si no se puede averiguar de dónde se cargó la clase, quedan los otros candidatos
        }
        for (java.nio.file.Path p : candidatos) {
            if (java.nio.file.Files.isRegularFile(p)) {
                try {
                    String texto = java.nio.file.Files.readString(p);
                    return texto.replaceAll("(?s)/\\*.*?\\*/", " ")
                            .replaceAll("//[^\\n]*", " ")
                            .replaceAll("\"(?:\\\\.|[^\"\\\\])*\"", "\"\"");
                } catch (java.io.IOException e) {
                    return null;
                }
            }
        }
        return null;
    }

    /** Caso que falla si alguno de los archivos usa alguna de las palabras prohibidas. */
    private static void prohibir(String[] archivos, String... prohibidas) {
        caso("Solo usa las herramientas permitidas en " + String.join(", ", archivos), 2000, () -> {
            for (String archivo : archivos) {
                String fuente = fuenteSinComentarios(archivo);
                if (fuente == null) {
                    info("no encontré " + archivo + " (corré el verificador desde la carpeta del ejercicio);"
                            + " salteo este control");
                    continue;
                }
                for (String palabra : prohibidas) {
                    verificar(!fuente.contains(palabra), archivo + " usa '" + palabra
                            + "', que no está permitido en este ejercicio (mirá el README)");
                }
            }
        });
    }

    private static void fin() {
        System.out.println();
        System.out.println("Resultado: " + casosOk + " de " + casosTotales + " casos OK.");
        if (casosOk == casosTotales) {
            System.out.println("¡Todo verde! Ahora respondé las preguntas de \"Para pensar\" del README:"
                    + " pasar el verificador no demuestra que el código sea correcto.");
        }
        System.exit(casosOk == casosTotales ? 0 : 1);
    }
}
