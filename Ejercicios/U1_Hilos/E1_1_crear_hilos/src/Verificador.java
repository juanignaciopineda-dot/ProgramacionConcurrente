import java.util.ArrayList;
import java.util.List;

/** Verificador automático del ejercicio 1.1. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 1.1 - Dos formas de crear un hilo (y una trampa)");

        caso("HiloContador: el constructor le pone nombre al hilo y no lo arranca", 2000, () -> {
            HiloContador h = new HiloContador("contador-1", 10);
            iguales("contador-1", h.getName(), "nombre del hilo");
            iguales(Thread.State.NEW, h.getState(), "estado antes de start()");
        });

        caso("HiloContador: suma de 1 a 100 en su propio hilo", 2000, () -> {
            HiloContador h = new HiloContador("contador-2", 100);
            h.start();
            h.join();
            iguales(5050L, h.getResultado(), "resultado de la suma");
            iguales(Thread.State.TERMINATED, h.getState(), "estado después de join()");
        });

        caso("crearConLambda: devuelve un hilo sin arrancar, con el nombre pedido", 2000, () -> {
            Thread h = Lanzador.crearConLambda("lambda-1", new String[1]);
            verificar(h != null, "devolvió null");
            iguales("lambda-1", h.getName(), "nombre del hilo");
            iguales(Thread.State.NEW, h.getState(), "estado (no tiene que estar arrancado)");
        });

        caso("crearConLambda: al correr, el hilo deja su nombre en el buzón", 2000, () -> {
            String[] buzon = new String[1];
            Thread h = Lanzador.crearConLambda("lambda-2", buzon);
            verificar(buzon[0] == null, "el buzón se llenó antes de arrancar el hilo");
            h.start();
            h.join();
            iguales("lambda-2", buzon[0], "contenido del buzón");
        });

        caso("Tarea.run(): anota el hilo que la ejecuta", 2000, () -> {
            Tarea t = new Tarea("suelta", 10);
            Thread h = new Thread(t, "hilo-de-prueba");
            h.start();
            h.join();
            iguales("hilo-de-prueba", t.getEjecutadaPor(), "getEjecutadaPor()");
        });

        caso("Tarea.run(): trabaja (duerme) la duración pedida", 2000, () -> {
            Tarea t = new Tarea("lenta", 200);
            long inicio = System.nanoTime();
            t.run();
            long ms = (System.nanoTime() - inicio) / 1_000_000;
            verificar(ms >= 190, "run() tardó " + ms + " ms y tenía que dormir 200 ms");
        });

        caso("ejecutarConStart: cada tarea corre en su propio hilo 'hilo-i'", 5000, () -> {
            List<Tarea> tareas = crearTareas(5, 50);
            Lanzador.ejecutarConStart(tareas);
            for (int i = 0; i < tareas.size(); i++) {
                iguales("hilo-" + (i + 1), tareas.get(i).getEjecutadaPor(), "hilo que ejecutó la tarea " + (i + 1));
            }
        });

        caso("ejecutarConStart: 5 tareas de 300 ms corren a la vez (tarda menos de 900 ms)", 5000, () -> {
            List<Tarea> tareas = crearTareas(5, 300);
            long ms = Lanzador.ejecutarConStart(tareas);
            info("ejecutarConStart tardó " + ms + " ms");
            verificar(ms >= 280, "devolvió a los " + ms + " ms, antes de que terminaran las tareas: ¿faltan los join()?");
            verificar(ms < 900, "tardó " + ms + " ms: las tareas corrieron de a una."
                    + " ¿Estás haciendo join() en el mismo bucle que start()?");
        });

        caso("ejecutarConRun: todas las tareas las ejecuta el hilo que llama", 5000, () -> {
            String yo = Thread.currentThread().getName();
            List<Tarea> tareas = crearTareas(3, 10);
            Lanzador.ejecutarConRun(tareas);
            for (int i = 0; i < tareas.size(); i++) {
                iguales(yo, tareas.get(i).getEjecutadaPor(), "hilo que ejecutó la tarea " + (i + 1)
                        + " (con run() no se crea ningún hilo nuevo)");
            }
        });

        caso("ejecutarConRun: corre todo en secuencia (5 x 100 ms tarda al menos 500 ms)", 5000, () -> {
            List<Tarea> tareas = crearTareas(5, 100);
            long ms = Lanzador.ejecutarConRun(tareas);
            info("ejecutarConRun tardó " + ms + " ms");
            verificar(ms >= 480, "tardó " + ms + " ms: ¿seguro que llamás a run() y no a start()?");
        });

        fin();
    }

    private static List<Tarea> crearTareas(int cantidad, long duracionMs) {
        List<Tarea> tareas = new ArrayList<>();
        for (int i = 1; i <= cantidad; i++) {
            tareas.add(new Tarea("tarea-" + i, duracionMs));
        }
        return tareas;
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
