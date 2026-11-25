import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/** Verificador automático del ejercicio 4.1. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 4.1 - Arranque de un servidor con CountDownLatch");

        caso("lanzarTablero: hilo 'tablero', daemon, que espera dormido (WAITING) en el latch", 3000, () -> {
            CountDownLatch listos = new CountDownLatch(1);
            Tablero tablero = new Tablero(listos, 20);
            Thread hilo = Arranque.lanzarTablero(tablero);
            try {
                iguales("tablero", hilo.getName(), "nombre del hilo");
                verificar(hilo.isDaemon(), "el hilo del tablero tendría que ser daemon");
                verificar(esperarEstado(hilo, Thread.State.WAITING, 1000), "el tablero tendría que estar dormido"
                        + " en await() (WAITING) y está " + hilo.getState()
                        + (hilo.getState() == Thread.State.RUNNABLE ? ": ¿espera activa preguntando getCount()?" : ""));
                verificar(!tablero.haArrancado(), "el tablero arrancó sin que ningún módulo avisara");
            } finally {
                hilo.interrupt();
            }
            hilo.join(1000);
            verificar(!hilo.isAlive(), "el tablero no terminó cuando lo interrumpieron mientras esperaba");
        });

        caso("Tablero: cuando el latch llega a 0 arranca, informa y termina si lo interrumpen", 3000, () -> {
            CountDownLatch listos = new CountDownLatch(1);
            Tablero tablero = new Tablero(listos, 20);
            Thread hilo = Arranque.lanzarTablero(tablero);
            esperarEstado(hilo, Thread.State.WAITING, 1000);
            listos.countDown();
            long limite = System.currentTimeMillis() + 1000;
            while (tablero.getReportes() < 3 && System.currentTimeMillis() < limite) {
                Thread.sleep(5);
            }
            verificar(tablero.haArrancado(), "el latch llegó a 0 y el tablero no arrancó");
            verificar(tablero.getReportes() >= 3, "el tablero informó " + tablero.getReportes() + " veces en un"
                    + " segundo con un período de 20 ms");
            hilo.interrupt();
            hilo.join(1000);
            verificar(!hilo.isAlive(), "el tablero no terminó cuando lo interrumpieron");
        });

        caso("Modulo.run: al terminar bien hace countDown()", 3000, () -> {
            CountDownLatch listos = new CountDownLatch(1);
            Modulo m = new Modulo("cache", 50, false, listos);
            Thread h = new Thread(m, "modulo-cache");
            h.start();
            h.join(2000);
            verificar(m.isInicializado(), "el módulo no quedó inicializado");
            iguales(0L, listos.getCount(), "cuenta del latch después del módulo");
        });

        caso("Modulo.run: si la inicialización falla, igual hace countDown() y deja salir la excepción", 3000, () -> {
            CountDownLatch listos = new CountDownLatch(1);
            Modulo m = new Modulo("red", 50, true, listos);
            Throwable[] salio = new Throwable[1];
            Thread h = new Thread(m, "modulo-red");
            h.setUncaughtExceptionHandler((hilo, e) -> salio[0] = e);
            h.start();
            h.join(2000);
            if (salio[0] instanceof UnsupportedOperationException u) {
                throw u;
            }
            iguales(0L, listos.getCount(), "cuenta del latch después de un módulo que falló (¿el countDown()"
                    + " está en un finally?)");
            verificar(!m.isInicializado(), "un módulo que falló quedó marcado como inicializado");
            verificar(salio[0] instanceof IllegalStateException, "la excepción de inicializar() no salió del"
                    + " run(): ¿la capturaste? Dejala salir, con try/finally y sin catch");
        });

        caso("lanzarModulos: un hilo por módulo, con nombre, todos arrancados", 3000, () -> {
            CountDownLatch listos = new CountDownLatch(3);
            List<Modulo> modulos = List.of(new Modulo("db", 50, false, listos),
                    new Modulo("cache", 50, false, listos), new Modulo("red", 50, false, listos));
            List<Thread> hilos = Arranque.lanzarModulos(modulos);
            iguales(3, hilos.size(), "cantidad de hilos");
            for (int i = 0; i < 3; i++) {
                iguales("modulo-" + modulos.get(i).getNombre(), hilos.get(i).getName(), "nombre del hilo " + i);
                verificar(hilos.get(i).getState() != Thread.State.NEW, "el hilo " + i + " no fue arrancado");
            }
            for (Thread h : hilos) {
                h.join(2000);
            }
            iguales(0L, listos.getCount(), "cuenta del latch al final");
        });

        caso("El tablero espera a los 5 módulos y arranca recién después del último", 5000, () -> {
            arranque(-1);
        });

        caso("Un módulo que falla no deja al tablero esperando para siempre", 5000, () -> {
            arranque(2);
        });

        caso("esperarModulos: devuelve false si se vence el plazo, y true enseguida si el latch ya está en 0", 3000, () -> {
            CountDownLatch listos = new CountDownLatch(2);
            listos.countDown();
            long t0 = System.nanoTime();
            boolean todos = Arranque.esperarModulos(listos, 200);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            verificar(!todos, "con un módulo sin avisar, esperarModulos devolvió true");
            verificar(ms >= 190, "esperarModulos se rindió en " + ms + " ms, y el plazo era 200");
            verificar(ms < 700, "esperarModulos tardó " + ms + " ms con un plazo de 200");
            listos.countDown();
            t0 = System.nanoTime();
            verificar(Arranque.esperarModulos(listos, 1000), "con el latch en 0, esperarModulos devolvió false");
            ms = (System.nanoTime() - t0) / 1_000_000;
            verificar(ms < 100, "con el latch en 0, esperarModulos tardó " + ms + " ms en volver");
            listos.countDown();
            iguales(0L, listos.getCount(), "cuenta después de un countDown() de más (un latch en 0 queda en 0)");
        });

        prohibir(new String[] {"Modulo.java", "Tablero.java", "Arranque.java"}, ".join(", "synchronized",
                ".wait(", "notify", "Semaphore", "Lock", "getCount()");

        fin();
    }

    /** Arranca 5 módulos de 100 a 500 ms (el de índice {@code falla} falla) y controla al tablero. */
    private static void arranque(int falla) throws InterruptedException {
        long[] demoras = {300, 100, 500, 200, 400};
        String[] nombres = {"db", "cache", "red", "colas", "metricas"};
        CountDownLatch listos = new CountDownLatch(5);
        Tablero tablero = new Tablero(listos, 20);
        Thread hiloTablero = Arranque.lanzarTablero(tablero);
        try {
            List<Modulo> modulos = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                modulos.add(new Modulo(nombres[i], demoras[i], i == falla, listos));
            }
            List<Thread> hilos = Arranque.lanzarModulos(modulos);
            boolean antes = false;
            long limite = System.currentTimeMillis() + 3000;
            while (System.currentTimeMillis() < limite) {
                // primero el tablero y después la cuenta: como la cuenta solo baja, si el tablero ya había
                // arrancado y la cuenta todavía no es 0, arrancó antes de tiempo
                boolean arranco = tablero.haArrancado();
                long cuenta = listos.getCount();
                if (arranco && cuenta > 0) {
                    antes = true;
                    break;
                }
                if (arranco) {
                    break;
                }
                Thread.sleep(5);
            }
            for (Thread h : hilos) {
                h.join(2000);
            }
            verificar(!antes, "el tablero arrancó antes de que avisaran todos los módulos");
            verificar(tablero.haArrancado(), "el tablero no arrancó"
                    + (falla >= 0 ? " porque un módulo falló: ¿el countDown() está en un finally?" : "")
                    + " (cuenta del latch: " + listos.getCount() + ")");
            for (int i = 0; i < 5; i++) {
                verificar(modulos.get(i).isInicializado() == (i != falla), "el módulo " + nombres[i]
                        + (i == falla ? " falló y quedó inicializado" : " no quedó inicializado"));
            }
        } finally {
            hiloTablero.interrupt();
            hiloTablero.join(1000);
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
