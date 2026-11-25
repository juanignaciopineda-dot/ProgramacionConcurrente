import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** Verificador automático del ejercicio 1.2. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 1.2 - Suma paralela con join()");

        caso("partir(10, 3), partir(9, 3), partir(7, 1)", 2000, () -> {
            iguales("[[0, 4], [4, 7], [7, 10]]", Arrays.deepToString(SumaParalela.partir(10, 3)), "partir(10, 3)");
            iguales("[[0, 3], [3, 6], [6, 9]]", Arrays.deepToString(SumaParalela.partir(9, 3)), "partir(9, 3)");
            iguales("[[0, 7]]", Arrays.deepToString(SumaParalela.partir(7, 1)), "partir(7, 1)");
        });

        caso("partir con más tramos que posiciones: partir(2, 4) y partir(0, 2)", 2000, () -> {
            iguales("[[0, 1], [1, 2], [2, 2], [2, 2]]", Arrays.deepToString(SumaParalela.partir(2, 4)), "partir(2, 4)");
            iguales("[[0, 0], [0, 0]]", Arrays.deepToString(SumaParalela.partir(0, 2)), "partir(0, 2)");
        });

        caso("partir rechaza n < 0 y k < 1", 2000, () -> {
            esperaExcepcion(IllegalArgumentException.class, () -> SumaParalela.partir(-1, 2), "partir(-1, 2)");
            esperaExcepcion(IllegalArgumentException.class, () -> SumaParalela.partir(5, 0), "partir(5, 0)");
        });

        caso("SumadorDeTramo suma [2, 5) de {1..10} = 3 + 4 + 5, y un tramo vacío da 0", 2000, () -> {
            int[] datos = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
            SumadorDeTramo s = new SumadorDeTramo(datos, 2, 5);
            s.run();
            iguales(12L, s.getResultado(), "suma de [2, 5)");
            SumadorDeTramo vacio = new SumadorDeTramo(datos, 4, 4);
            vacio.run();
            iguales(0L, vacio.getResultado(), "suma de [4, 4)");
        });

        caso("crearSumadores: un sumador por tramo, en orden", 2000, () -> {
            List<SumadorDeTramo> sumadores = SumaParalela.crearSumadores(new int[10], 3);
            iguales(3, sumadores.size(), "cantidad de sumadores");
            int[][] esperado = {{0, 4}, {4, 7}, {7, 10}};
            for (int i = 0; i < 3; i++) {
                SumadorDeTramo s = sumadores.get(i);
                iguales(Arrays.toString(esperado[i]), Arrays.toString(new int[] {s.getDesde(), s.getHasta()}),
                        "tramo del sumador " + (i + 1));
            }
        });

        caso("lanzar: un hilo por sumador, con nombre 'sumador-i' y ya arrancado", 2000, () -> {
            List<SumadorDeTramo> sumadores = SumaParalela.crearSumadores(new int[1000], 4);
            List<Thread> hilos = SumaParalela.lanzar(sumadores);
            iguales(4, hilos.size(), "cantidad de hilos");
            for (int i = 0; i < hilos.size(); i++) {
                Thread h = hilos.get(i);
                iguales("sumador-" + (i + 1), h.getName(), "nombre del hilo " + (i + 1));
                verificar(h.getState() != Thread.State.NEW, "el hilo " + h.getName() + " no fue arrancado");
            }
            for (Thread h : hilos) {
                h.join();
            }
        });

        caso("esperarYCombinar: suma bien y deja todos los hilos terminados", 3000, () -> {
            int[] datos = aleatorio(100_000, 1);
            List<SumadorDeTramo> sumadores = SumaParalela.crearSumadores(datos, 5);
            List<Thread> hilos = SumaParalela.lanzar(sumadores);
            iguales(SumaParalela.sumarSecuencial(datos), SumaParalela.esperarYCombinar(hilos, sumadores), "total");
            for (Thread h : hilos) {
                iguales(Thread.State.TERMINATED, h.getState(), "estado de " + h.getName());
            }
        });

        caso("sumar: mismo resultado que la suma secuencial para varios tamaños y k", 10000, () -> {
            int[] tamanios = {0, 1, 7, 1000, 1_000_003};
            int[] ks = {1, 2, 3, 8, 16};
            for (int n : tamanios) {
                int[] datos = aleatorio(n, n);
                long esperado = SumaParalela.sumarSecuencial(datos);
                for (int k : ks) {
                    iguales(esperado, SumaParalela.sumar(datos, k), "sumar(n=" + n + ", k=" + k + ")");
                }
            }
        });

        caso("Experimento (informativo): tiempos con 20 millones de elementos", 30000, () -> {
            int[] datos = aleatorio(20_000_000, 42);
            SumaParalela.sumar(datos, 2); // calentamiento de la JVM
            info(String.format("%-6s %10s", "k", "ms"));
            for (int k : new int[] {1, 2, 4, 8, 64}) {
                long mejor = Long.MAX_VALUE;
                for (int rep = 0; rep < 3; rep++) {
                    long inicio = System.nanoTime();
                    SumaParalela.sumar(datos, k);
                    mejor = Math.min(mejor, System.nanoTime() - inicio);
                }
                info(String.format("%-6d %10.1f", k, mejor / 1e6));
            }
            info("núcleos disponibles: " + Runtime.getRuntime().availableProcessors());
        });

        fin();
    }

    private static int[] aleatorio(int n, long semilla) {
        Random r = new Random(semilla);
        int[] datos = new int[n];
        for (int i = 0; i < n; i++) {
            datos[i] = r.nextInt(2_000_001) - 1_000_000;
        }
        return datos;
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
        System.out.println("=== " + texto + " ===");
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
                System.out.println("           " + nota);
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
        System.out.printf("%-10s %s%n", etiqueta, nombre);
        if (detalle != null) {
            for (String linea : detalle.split("\n")) {
                System.out.println("           " + linea);
            }
        }
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
