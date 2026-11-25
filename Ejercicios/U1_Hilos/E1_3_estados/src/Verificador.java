import java.util.ArrayList;
import java.util.List;

/** Verificador automático del ejercicio 1.3. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 1.3 - Estados y atributos de un hilo");

        caso("describir: formato exacto con un hilo sin arrancar", 2000, () -> {
            Thread t = new Thread(() -> {}, "ficha-1");
            t.setPriority(7);
            iguales("nombre=ficha-1 id=" + t.threadId() + " prioridad=7 daemon=false estado=NEW",
                    FichaDeHilo.describir(t), "describir()");
        });

        caso("describir: un hilo daemon ya terminado", 2000, () -> {
            Thread t = new Thread(() -> {}, "ficha-2");
            t.setDaemon(true);
            t.start();
            t.join();
            iguales("nombre=ficha-2 id=" + t.threadId() + " prioridad=" + t.getPriority()
                    + " daemon=true estado=TERMINATED", FichaDeHilo.describir(t), "describir()");
        });

        caso("crear: nombre, prioridad y estado NEW", 2000, () -> {
            Thread t = FichaDeHilo.crear(() -> {}, "prioritario", 9);
            iguales("prioritario", t.getName(), "nombre");
            iguales(9, t.getPriority(), "prioridad");
            iguales(Thread.State.NEW, t.getState(), "estado");
        });

        caso("crear: rechaza prioridades 0 y 11", 2000, () -> {
            esperaExcepcion(IllegalArgumentException.class, () -> FichaDeHilo.crear(() -> {}, "x", 0), "prioridad 0");
            esperaExcepcion(IllegalArgumentException.class, () -> FichaDeHilo.crear(() -> {}, "x", 11), "prioridad 11");
        });

        caso("calcularDurante(150) tarda unos 150 ms", 2000, () -> {
            long inicio = System.nanoTime();
            Recorrido.calcularDurante(150);
            long ms = (System.nanoTime() - inicio) / 1_000_000;
            verificar(ms >= 145 && ms < 400, "tardó " + ms + " ms");
        });

        caso("arrancarBloqueador: devuelve un hilo 'bloqueador' arrancado que retiene el candado", 3000, () -> {
            Thread b = new Recorrido().arrancarBloqueador();
            iguales("bloqueador", b.getName(), "nombre");
            verificar(b.getState() != Thread.State.NEW, "el bloqueador no fue arrancado");
            verificar(esperarEstado(b, Thread.State.TIMED_WAITING, 500),
                    "el bloqueador tendría que estar durmiendo (TIMED_WAITING) con el candado tomado, y está "
                            + b.getState());
            long inicio = System.nanoTime();
            b.join();
            long ms = (System.nanoTime() - inicio) / 1_000_000;
            verificar(ms >= 6 * Recorrido.FASE_MS - 150, "retuvo el candado solo ~" + ms + " ms (tendría que"
                    + " retenerlo " + 6 * Recorrido.FASE_MS + " ms)");
        });

        caso("crearHiloObservado: devuelve un hilo 'observado' sin arrancar", 2000, () -> {
            Thread t = new Recorrido().crearHiloObservado();
            iguales("observado", t.getName(), "nombre");
            iguales(Thread.State.NEW, t.getState(), "estado");
        });

        caso("El hilo observado pasa por los 6 estados en orden", 5000, () -> {
            Thread t = new Recorrido().crearHiloObservado();
            List<Thread.State> vistos = new ArrayList<>();
            vistos.add(t.getState());
            t.start();
            long limite = System.currentTimeMillis() + 4000;
            while (System.currentTimeMillis() < limite) {
                Thread.State s = t.getState();
                if (s != vistos.get(vistos.size() - 1)) {
                    vistos.add(s);
                }
                if (s == Thread.State.TERMINATED) {
                    break;
                }
                Thread.sleep(3);
            }
            info("secuencia observada: " + vistos);
            List<Thread.State> orden = List.of(Thread.State.NEW, Thread.State.RUNNABLE, Thread.State.TIMED_WAITING,
                    Thread.State.WAITING, Thread.State.BLOCKED, Thread.State.TERMINATED);
            int j = 0;
            for (Thread.State s : vistos) {
                if (j < orden.size() && s == orden.get(j)) {
                    j++;
                }
            }
            verificar(j == orden.size(), "no se vio la secuencia " + orden + " (faltó a partir de "
                    + (j < orden.size() ? orden.get(j) : "-") + ")");
        });

        fin();
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
