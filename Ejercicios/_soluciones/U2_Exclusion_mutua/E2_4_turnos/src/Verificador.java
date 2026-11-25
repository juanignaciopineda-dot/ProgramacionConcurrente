import java.util.ArrayList;
import java.util.List;

/** Verificador automático del ejercicio 2.4. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 2.4 - Turnos en ronda con wait() y notifyAll()");

        caso("Constructor y getTurno: empieza en 0; rechaza menos de 1 participante", 2000, () -> {
            iguales(0, new Turnero(3).getTurno(), "turno inicial");
            esperaExcepcion(IllegalArgumentException.class, () -> new Turnero(0), "new Turnero(0)");
        });

        caso("Un solo hilo: esperarTurno vuelve enseguida si es su turno, y pasarTurno avanza en ronda", 2000, () -> {
            Turnero t = new Turnero(3);
            for (int vuelta = 0; vuelta < 2; vuelta++) {
                for (int id = 0; id < 3; id++) {
                    t.esperarTurno(id);
                    t.pasarTurno(id);
                    iguales((id + 1) % 3, t.getTurno(), "turno después de que pasa el " + id);
                }
            }
        });

        caso("pasarTurno de quien no tiene el turno lanza IllegalStateException; ids fuera de rango, IllegalArgumentException", 2000, () -> {
            Turnero t = new Turnero(3);
            esperaExcepcion(IllegalStateException.class, () -> t.pasarTurno(1), "pasarTurno(1) con turno 0");
            iguales(0, t.getTurno(), "el turno no tiene que cambiar");
            esperaExcepcion(IllegalArgumentException.class, () -> t.pasarTurno(3), "pasarTurno(3)");
            esperaExcepcion(IllegalArgumentException.class, () -> t.esperarTurno(-1), "esperarTurno(-1)");
        });

        caso("Quien no tiene el turno espera en WAITING (sin espera activa)", 3000, () -> {
            Turnero t = new Turnero(2);
            Thread uno = new Thread(() -> {
                try {
                    t.esperarTurno(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "participante-1");
            uno.start();
            verificar(esperarEstado(uno, Thread.State.WAITING, 1000),
                    "el participante 1 tendría que estar en WAITING y está " + uno.getState());
            t.pasarTurno(0);
            uno.join(1000);
            verificar(!uno.isAlive(), "el participante 1 no se despertó cuando le pasaron el turno");
        });

        caso("Ping-pong: 2 participantes x 5 rondas, arrancados al revés", 5000, () -> {
            iguales("PING PONG ".repeat(5).trim(), ronda(new String[] {"PING", "PONG"}, 5, true), "registro");
        });

        caso("3 participantes x 5 rondas: A B C A B C ...", 5000, () -> {
            iguales("A B C ".repeat(5).trim(), ronda(new String[] {"A", "B", "C"}, 5, true), "registro");
        });

        caso("5 participantes x 1000 rondas: el orden se mantiene y nadie se queda dormido", 20000, () -> {
            String[] marcas = {"a", "b", "c", "d", "e"};
            iguales("a b c d e ".repeat(1000).trim(), ronda(marcas, 1000, false), "registro");
        });

        prohibir(new String[] {"Turnero.java", "Participante.java"}, "java.util.concurrent", "Thread.sleep");

        fin();
    }

    /** Corre una ronda completa y devuelve el registro separado por espacios. */
    private static String ronda(String[] marcas, int rondas, boolean alReves) throws Exception {
        Turnero t = new Turnero(marcas.length);
        List<String> registro = new ArrayList<>();
        List<Thread> hilos = new ArrayList<>();
        for (int i = 0; i < marcas.length; i++) {
            hilos.add(new Thread(new Participante(i, marcas[i], rondas, t, registro), "participante-" + i));
        }
        if (alReves) {
            for (int i = hilos.size() - 1; i >= 0; i--) {
                hilos.get(i).start();
                Thread.sleep(20);
            }
        } else {
            for (Thread h : hilos) {
                h.start();
            }
        }
        for (Thread h : hilos) {
            h.join(15000);
            verificar(!h.isAlive(), h.getName() + " quedó esperando para siempre (estado " + h.getState()
                    + "). ¿Usaste notify() en vez de notifyAll()?");
        }
        return String.join(" ", registro);
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
