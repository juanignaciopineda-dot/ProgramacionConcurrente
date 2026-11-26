import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** Verificador automático del ejercicio 4.4. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 4.4 - Callable y Future");

        caso("TramoDePrimos.call: cuenta los primos del tramo", 3000, () -> {
            iguales(25L, new TramoDePrimos(2, 100).call(), "primos en [2, 100)");
            iguales(0L, new TramoDePrimos(0, 2).call(), "primos en [0, 2)");
            iguales(1L, new TramoDePrimos(97, 98).call(), "primos en [97, 98)");
            iguales(0L, new TramoDePrimos(50, 50).call(), "primos en un tramo vacío");
        });

        caso("TramoDePrimos.call: si el hilo está interrumpido, lanza InterruptedException", 5000, () -> {
            Thread.currentThread().interrupt();
            try {
                new TramoDePrimos(2, 3_000_000).call();
                throw new AssertionError("con el hilo interrumpido, call() contó todo el tramo en vez de lanzar"
                        + " InterruptedException");
            } catch (InterruptedException esperada) {
                // es lo que tiene que pasar
            } finally {
                Thread.interrupted();
            }
        });

        ExecutorService pool = Executors.newFixedThreadPool(4);

        caso("ejecutarTodas: devuelve los resultados en el orden de las tareas", 3000, () -> {
            List<Callable<String>> tareas = List.of(demora("a", 150), demora("b", 50), demora("c", 100));
            iguales(List.of("a", "b", "c"), Paralelo.ejecutarTodas(tareas, pool), "resultados");
        });

        caso("ejecutarTodas: 4 tareas de 200 ms en un pool de 4 tardan unos 200 ms", 3000, () -> {
            List<Callable<String>> tareas = List.of(demora("1", 200), demora("2", 200), demora("3", 200), demora("4", 200));
            long t0 = System.nanoTime();
            Paralelo.ejecutarTodas(tareas, pool);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            info("4 tareas de 200 ms: " + ms + " ms");
            verificar(ms >= 190, "4 tareas de 200 ms tardaron " + ms + " ms: ¿esperaste los resultados?");
            verificar(ms < 380, "4 tareas de 200 ms en un pool de 4 tardaron " + ms + " ms: ¿hiciste get() en el"
                    + " mismo bucle que submit()?");
        });

        caso("ejecutarTodas: si una tarea lanza una excepción, sale ExecutionException con la causa", 3000, () -> {
            Callable<String> rota = () -> {
                throw new IllegalStateException("se rompió");
            };
            List<Callable<String>> tareas = List.of(demora("a", 20), rota);
            try {
                Paralelo.ejecutarTodas(tareas, pool);
                throw new AssertionError("ejecutarTodas no lanzó nada con una tarea rota");
            } catch (ExecutionException e) {
                verificar(e.getCause() instanceof IllegalStateException, "la causa de la ExecutionException"
                        + " tendría que ser la IllegalStateException de la tarea, y es " + e.getCause());
            }
        });

        caso("contarPrimos: reparte en tramos con invokeAll y suma bien", 10000, () -> {
            ThreadPoolExecutor medido = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
            try {
                iguales(9592L, Paralelo.contarPrimos(100_000, 8, medido), "primos menores que 100000 con 8 tramos");
                iguales(8L, medido.getCompletedTaskCount(), "tareas que ejecutó el pool (una por tramo)");
                iguales(9592L, Paralelo.contarPrimos(100_000, 1, medido), "primos menores que 100000 con 1 tramo");
                iguales(4L, Paralelo.contarPrimos(10, 3, medido), "primos menores que 10 con 3 tramos");
                iguales(0L, Paralelo.contarPrimos(2, 4, medido), "primos menores que 2");
                esperaExcepcion(IllegalArgumentException.class, () -> Paralelo.contarPrimos(100, 0, medido), "0 tramos");
            } finally {
                medido.shutdownNow();
            }
        });

        caso("conTiempoMaximo: si la tarea termina a tiempo, devuelve su resultado", 3000, () -> {
            Optional<String> r = Paralelo.conTiempoMaximo(demora("listo", 50), pool, 1000);
            iguales(Optional.of("listo"), r, "resultado");
        });

        caso("conTiempoMaximo: si no termina a tiempo, devuelve vacío y la cancela (la interrumpe)", 3000, () -> {
            boolean[] interrumpida = {false};
            Callable<String> lenta = () -> {
                try {
                    Thread.sleep(3000);
                    return "tarde";
                } catch (InterruptedException e) {
                    interrumpida[0] = true;
                    throw e;
                }
            };
            long t0 = System.nanoTime();
            Optional<String> r = Paralelo.conTiempoMaximo(lenta, pool, 100);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            iguales(Optional.empty(), r, "resultado de una tarea que no terminó a tiempo");
            verificar(ms >= 90, "conTiempoMaximo se rindió en " + ms + " ms con un plazo de 100");
            verificar(ms < 500, "conTiempoMaximo tardó " + ms + " ms con un plazo de 100");
            long limite = System.currentTimeMillis() + 500;
            while (!interrumpida[0] && System.currentTimeMillis() < limite) {
                Thread.sleep(5);
            }
            verificar(interrumpida[0], "la tarea siguió corriendo después del plazo: ¿llamaste a cancel(true)?");
        });

        pool.shutdownNow();

        prohibir(new String[] {"TramoDePrimos.java", "Paralelo.java"}, "CompletableFuture", "parallelStream",
                ".parallel()", "ForkJoin", "new Thread", "Executors.");

        fin();
    }

    /** Una tarea que tarda {@code ms} y devuelve {@code valor}. */
    private static Callable<String> demora(String valor, long ms) {
        return () -> {
            Thread.sleep(ms);
            return valor;
        };
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
