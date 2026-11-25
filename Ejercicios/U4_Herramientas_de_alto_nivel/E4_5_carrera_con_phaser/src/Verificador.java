import java.util.ArrayList;
import java.util.List;

/** Verificador automático del ejercicio 4.5. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 4.5 - Carrera por etapas con Phaser");

        caso("Constructor: valida y registra un participante por corredor", 2000, () -> {
            esperaExcepcion(IllegalArgumentException.class, () -> new Carrera(0, 3), "0 corredores");
            esperaExcepcion(IllegalArgumentException.class, () -> new Carrera(3, 0), "0 etapas");
            Carrera c = new Carrera(4, 3);
            iguales(4, c.corredoresEnCarrera(), "corredores registrados en el phaser (¿con qué valor lo creaste?)");
            iguales(0, c.etapaActual(), "etapa al largar");
            verificar(!c.terminada(), "la carrera arrancó terminada");
        });

        caso("abandonar: el phaser deja de esperar al que se fue", 2000, () -> {
            Carrera c = new Carrera(3, 2);
            c.abandonar("x", 0);
            iguales(2, c.corredoresEnCarrera(), "corredores registrados después de un abandono (¿arriveAndDeregister?)");
            iguales(List.of("abandona 0 x"), c.getBitacora(), "bitácora");
        });

        caso("Nadie empieza una etapa hasta que todos terminaron la anterior (4 corredores, 5 etapas)", 8000, () -> {
            for (int ronda = 1; ronda <= 3; ronda++) {
                Carrera c = new Carrera(4, 5);
                correr(c, new int[] {-1, -1, -1, -1});
                List<String> b = c.getBitacora();
                controlarOrden(b, 5, "ronda " + ronda);
                for (int i = 0; i < 4; i++) {
                    for (int e = 0; e < 5; e++) {
                        verificar(b.contains("termina " + e + " corredor-" + i), "ronda " + ronda + ": el corredor-"
                                + i + " no terminó la etapa " + e);
                    }
                }
                for (int e = 0; e < 5; e++) {
                    verificar(b.contains("fin de la etapa " + e + ": siguen 4"), "ronda " + ronda
                            + ": falta \"fin de la etapa " + e + ": siguen 4\" (¿redefiniste onAdvance?)");
                }
            }
        });

        caso("Después de la última etapa, la carrera queda terminada", 5000, () -> {
            Carrera c = new Carrera(3, 2);
            correr(c, new int[] {-1, -1, -1});
            verificar(c.terminada(), "terminaron todas las etapas y el phaser no terminó: onAdvance tiene que"
                    + " devolver true en la última etapa");
            verificar(c.etapaActual() < 0, "un phaser terminado devuelve una fase negativa, y dio " + c.etapaActual());
        });

        caso("Un corredor que abandona en la etapa 2 no deja colgados a los demás", 5000, () -> {
            Carrera c = new Carrera(4, 5);
            correr(c, new int[] {-1, -1, 2, -1});
            List<String> b = c.getBitacora();
            controlarOrden(b, 5, "con abandono");
            verificar(b.contains("abandona 2 corredor-2"), "falta \"abandona 2 corredor-2\" en la bitácora");
            verificar(!b.contains("empieza 2 corredor-2"), "el corredor-2 corrió la etapa en la que abandonaba");
            verificar(b.contains("fin de la etapa 1: siguen 4"), "falta \"fin de la etapa 1: siguen 4\"");
            verificar(b.contains("fin de la etapa 2: siguen 3"), "falta \"fin de la etapa 2: siguen 3\"");
            verificar(b.contains("termina 4 corredor-3"), "el corredor-3 no llegó al final");
            verificar(c.terminada(), "la carrera no quedó terminada");
        });

        caso("Si abandonan todos, la carrera termina", 5000, () -> {
            Carrera c = new Carrera(2, 4);
            correr(c, new int[] {1, 1});
            verificar(c.terminada(), "abandonaron todos y el phaser no terminó");
            verificar(c.getBitacora().contains("fin de la etapa 1: siguen 0"), "falta \"fin de la etapa 1: siguen 0\""
                    + " en la bitácora: " + c.getBitacora());
        });

        prohibir(new String[] {"Carrera.java", "Corredor.java"}, "CyclicBarrier", "CountDownLatch", "Semaphore",
                ".join(", ".wait(", "notify");

        fin();
    }

    /** Larga un hilo por corredor ({@code abandonos[i]} es la etapa en la que abandona el i) y los espera. */
    private static void correr(Carrera c, int[] abandonos) throws InterruptedException {
        List<Thread> hilos = new ArrayList<>();
        for (int i = 0; i < abandonos.length; i++) {
            hilos.add(new Thread(new Corredor("corredor-" + i, c, abandonos[i]), "corredor-" + i));
        }
        for (Thread h : hilos) {
            h.start();
        }
        long limite = System.currentTimeMillis() + 3000;
        for (Thread h : hilos) {
            h.join(Math.max(1, limite - System.currentTimeMillis()));
        }
        List<String> colgados = new ArrayList<>();
        for (Thread h : hilos) {
            if (h.isAlive()) {
                colgados.add(h.getName());
            }
        }
        verificar(colgados.isEmpty(), "se quedaron esperando en la etapa " + c.etapaActual() + ": " + colgados
                + ". ¿El que abandona hace arriveAndDeregister()? ¿Todos hacen arriveAndAwaitAdvance() al final"
                + " de cada etapa?");
    }

    /** Controla que cada "empieza k+1" esté después de todos los "termina k" y del "fin de la etapa k". */
    private static void controlarOrden(List<String> b, int etapas, String que) {
        for (int e = 0; e + 1 < etapas; e++) {
            int ultimoTermina = -1;
            int fin = -1;
            int primeroSiguiente = Integer.MAX_VALUE;
            for (int i = 0; i < b.size(); i++) {
                String linea = b.get(i);
                if (linea.startsWith("termina " + e + " ")) {
                    ultimoTermina = i;
                }
                if (linea.startsWith("fin de la etapa " + e + ":")) {
                    fin = i;
                }
                if (linea.startsWith("empieza " + (e + 1) + " ") && primeroSiguiente == Integer.MAX_VALUE) {
                    primeroSiguiente = i;
                }
            }
            verificar(ultimoTermina < primeroSiguiente, que + ": alguien empezó la etapa " + (e + 1)
                    + " antes de que todos terminaran la " + e + ":\n" + String.join("\n", b));
            verificar(fin >= 0 && fin < primeroSiguiente, que + ": el \"fin de la etapa " + e + "\" tendría que"
                    + " aparecer antes de que alguien empiece la " + (e + 1));
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
