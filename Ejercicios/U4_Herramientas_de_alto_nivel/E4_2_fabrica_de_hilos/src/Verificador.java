import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/** Verificador automático del ejercicio 4.2. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 4.2 - Fábrica de hilos");

        caso("Constructor: valida el prefijo y el máximo", 2000, () -> {
            esperaExcepcion(IllegalArgumentException.class, () -> new FabricaDeHilos(null, false, 5), "prefijo null");
            esperaExcepcion(IllegalArgumentException.class, () -> new FabricaDeHilos("  ", false, 5), "prefijo en blanco");
            esperaExcepcion(IllegalArgumentException.class, () -> new FabricaDeHilos("x", false, 0), "máximo 0");
            iguales(0, new FabricaDeHilos("x").getCreados(), "getCreados() de una fábrica nueva");
        });

        caso("newThread: nombre numerado desde 1, daemon según la fábrica, sin arrancar", 3000, () -> {
            FabricaDeHilos normal = new FabricaDeHilos("trabajador");
            boolean[] corrio = {false};
            Thread h1 = normal.newThread(() -> corrio[0] = true);
            Thread h2 = normal.newThread(() -> { });
            verificar(h1 != null && h2 != null, "newThread devolvió null en una fábrica sin límite");
            iguales("trabajador-1", h1.getName(), "nombre del primer hilo");
            iguales("trabajador-2", h2.getName(), "nombre del segundo hilo");
            iguales(Thread.State.NEW, h1.getState(), "estado del hilo creado (la fábrica no lo arranca)");
            verificar(!h1.isDaemon(), "la fábrica no daemon creó un hilo daemon");
            h1.start();
            h1.join(1000);
            verificar(corrio[0], "el hilo creado no ejecutó la tarea que recibió newThread");
            FabricaDeHilos demonios = new FabricaDeHilos("vigilante", true, 10);
            verificar(demonios.newThread(() -> { }).isDaemon(), "la fábrica daemon creó un hilo no daemon");
            iguales(2, normal.getCreados(), "getCreados()");
            iguales(List.of("creado trabajador-1", "creado trabajador-2"), normal.getEstadisticas(), "estadísticas");
        });

        caso("Límite: al llegar al máximo, newThread devuelve null y lo anota", 2000, () -> {
            FabricaDeHilos f = new FabricaDeHilos("limitado", false, 2);
            verificar(f.newThread(() -> { }) != null, "el primer hilo no se creó");
            verificar(f.newThread(() -> { }) != null, "el segundo hilo no se creó");
            verificar(f.newThread(() -> { }) == null, "con el límite en 2, el tercer pedido tendría que devolver null");
            iguales(2, f.getCreados(), "getCreados() después del rechazo");
            iguales(List.of("creado limitado-1", "creado limitado-2", "rechazado: límite de 2 hilos"),
                    f.getEstadisticas(), "estadísticas");
        });

        caso("getEstadisticas devuelve una copia", 2000, () -> {
            FabricaDeHilos f = new FabricaDeHilos("x");
            f.newThread(() -> { });
            List<String> foto = f.getEstadisticas();
            f.newThread(() -> { });
            iguales(1, foto.size(), "líneas de una foto sacada antes del segundo hilo (¿devolviste la lista interna?)");
        });

        caso("Estrés: 8 hilos le piden 500 hilos cada uno a la misma fábrica", 10000, () -> {
            FabricaDeHilos f = new FabricaDeHilos("h");
            List<List<String>> nombres = new ArrayList<>();
            List<Thread> pedidores = new ArrayList<>();
            for (int p = 0; p < 8; p++) {
                List<String> propios = new ArrayList<>();
                nombres.add(propios);
                pedidores.add(new Thread(() -> {
                    for (int k = 0; k < 500; k++) {
                        propios.add(f.newThread(() -> { }).getName());
                    }
                }, "pedidor-" + p));
            }
            for (Thread t : pedidores) {
                t.start();
            }
            for (Thread t : pedidores) {
                t.join(5000);
            }
            Set<String> distintos = new HashSet<>();
            for (List<String> l : nombres) {
                distintos.addAll(l);
            }
            iguales(4000, distintos.size(), "nombres distintos (si hay repetidos, dos pedidos leyeron el mismo"
                    + " contador: ¿newThread es synchronized?)");
            iguales(4000, f.getCreados(), "getCreados()");
            iguales(4000, f.getEstadisticas().size(), "líneas de estadísticas");
        });

        caso("crearPool: los hilos del pool salen de la fábrica (3 hilos para 12 tareas)", 5000, () -> {
            FabricaDeHilos f = new FabricaDeHilos("trabajador");
            ExecutorService pool = f.crearPool(3);
            Set<String> usados = new HashSet<>();
            for (int i = 0; i < 12; i++) {
                pool.execute(() -> {
                    synchronized (usados) {
                        usados.add(Thread.currentThread().getName());
                    }
                    dormir(20);
                });
            }
            pool.shutdown();
            verificar(pool.awaitTermination(3, TimeUnit.SECONDS), "el pool no terminó las tareas");
            iguales(Set.of("trabajador-1", "trabajador-2", "trabajador-3"), usados, "hilos que ejecutaron tareas");
            iguales(3, f.getCreados(), "hilos que creó la fábrica (el pool los reutiliza)");
        });

        caso("Un pool de 4 con una fábrica limitada a 2 trabaja con 2 hilos", 5000, () -> {
            FabricaDeHilos f = new FabricaDeHilos("limitado", false, 2);
            ExecutorService pool = f.crearPool(4);
            Set<String> usados = new HashSet<>();
            int[] hechas = {0};
            for (int i = 0; i < 8; i++) {
                pool.execute(() -> {
                    synchronized (usados) {
                        usados.add(Thread.currentThread().getName());
                        hechas[0]++;
                    }
                    dormir(20);
                });
            }
            pool.shutdown();
            verificar(pool.awaitTermination(3, TimeUnit.SECONDS), "el pool no terminó las tareas");
            synchronized (usados) {
                iguales(8, hechas[0], "tareas ejecutadas");
                iguales(Set.of("limitado-1", "limitado-2"), usados, "hilos que ejecutaron tareas");
            }
            verificar(f.getEstadisticas().contains("rechazado: límite de 2 hilos"),
                    "las estadísticas no muestran los pedidos rechazados del pool: " + f.getEstadisticas());
            long rechazos = f.getEstadisticas().stream().filter(l -> l.startsWith("rechazado")).count();
            info("la fábrica creó " + f.getCreados() + " hilos y rechazó " + rechazos + " pedidos del pool");
        });

        prohibir(new String[] {"FabricaDeHilos.java"}, "Atomic", "Collections.synchronized", "Concurrent");

        fin();
    }

    private static void dormir(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
