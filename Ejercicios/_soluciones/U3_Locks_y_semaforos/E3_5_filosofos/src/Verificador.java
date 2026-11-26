import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/** Verificador automático del ejercicio 3.5. No hace falta modificarlo. */
public class Verificador {

    /** Una manera de comer: lo que hace el filósofo i en cada comida. */
    private interface Estrategia {
        void comer(Mesa mesa, int i) throws InterruptedException;
    }

    private static final Estrategia INGENUA = Mesa::comerIngenuo;
    private static final Estrategia ORDEN_GLOBAL = Mesa::comerConOrdenGlobal;
    private static final Estrategia MAYORDOMO = Mesa::comerConMayordomo;
    private static final Estrategia REINTENTANDO = (mesa, i) -> {
        while (!mesa.intentarComer(i, 5)) {
            Thread.sleep(ThreadLocalRandom.current().nextInt(3));
        }
    };

    public static void main(String[] args) {
        titulo("Ejercicio 3.5 - La cena de los filósofos");

        caso("La versión ingenua (ya está hecha) se traba si todos toman el izquierdo a la vez", 8000, () -> {
            Mesa mesa = new Mesa(5, 50);
            List<Thread> filosofos = lanzar(mesa, INGENUA, 1);
            List<ThreadInfo> trabados = esperarDeadlock(filosofos, 3000);
            if (trabados.isEmpty()) {
                info("esta vez no se trabó (puede pasar si un hilo arrancó tarde); corré de nuevo para verlo");
            } else {
                info("la JVM detectó la espera circular:");
                for (ThreadInfo t : trabados) {
                    info("  '" + t.getThreadName() + "' tiene un tenedor y espera el que tiene '"
                            + t.getLockOwnerName() + "'");
                }
                info("los interrumpo para destrabarlos (por eso comerIngenuo usa lockInterruptibly)");
            }
            for (Thread f : filosofos) {
                f.interrupt();
            }
            for (Thread f : filosofos) {
                f.join(2000);
                verificar(!f.isAlive(), "el " + f.getName() + " no se destrabó con la interrupción");
            }
            tenedoresLibres(mesa);
        });

        caso("Orden global: los 5 toman el primer tenedor a la vez y no se traba", 8000, () -> {
            Mesa mesa = new Mesa(5, 50);
            terminar(lanzar(mesa, ORDEN_GLOBAL, 1), 4000);
            comieron(mesa, 1);
            tenedoresLibres(mesa);
        });

        caso("Orden global: estrés con 5 filósofos (40 comidas cada uno) y con 2", 20000, () -> {
            Mesa cinco = new Mesa(5, 1);
            terminar(lanzar(cinco, ORDEN_GLOBAL, 40), 15000);
            comieron(cinco, 40);
            tenedoresLibres(cinco);
            Mesa dos = new Mesa(2, 1);
            terminar(lanzar(dos, ORDEN_GLOBAL, 40), 5000);
            comieron(dos, 40);
            tenedoresLibres(dos);
        });

        caso("Mayordomo: los 5 quieren sentarse a la vez y no se traba", 8000, () -> {
            Mesa mesa = new Mesa(5, 50);
            terminar(lanzar(mesa, MAYORDOMO, 1), 4000);
            comieron(mesa, 1);
            tenedoresLibres(mesa);
        });

        caso("Mayordomo: estrés con 5 filósofos (40 comidas cada uno) y con 2", 20000, () -> {
            Mesa cinco = new Mesa(5, 1);
            terminar(lanzar(cinco, MAYORDOMO, 40), 15000);
            comieron(cinco, 40);
            tenedoresLibres(cinco);
            Mesa dos = new Mesa(2, 1);
            terminar(lanzar(dos, MAYORDOMO, 40), 5000);
            comieron(dos, 40);
            tenedoresLibres(dos);
        });

        caso("intentarComer se rinde si un tenedor está ocupado, y suelta el otro", 5000, () -> {
            Mesa mesa = new Mesa(5, 0);
            intentoFallido(mesa, 1, 0, "derecho");
            intentoFallido(mesa, 0, 1, "izquierdo");
            iguales(0, mesa.getComidas()[0], "comidas del filósofo 0 después de rendirse dos veces");
            verificar(mesa.intentarComer(0, 100), "con los dos tenedores libres, intentarComer tendría que comer");
            iguales(1, mesa.getComidas()[0], "comidas del filósofo 0");
            tenedoresLibres(mesa);
        });

        caso("intentarComer: estrés, los 5 reintentan hasta comer 40 veces cada uno", 20000, () -> {
            Mesa mesa = new Mesa(5, 1);
            terminar(lanzar(mesa, REINTENTANDO, 40), 15000);
            comieron(mesa, 40);
            tenedoresLibres(mesa);
        });

        prohibir(new String[] {"Mesa.java"}, "synchronized", ".wait(", "notify");

        fin();
    }

    /** Arranca un hilo por filósofo; cada uno come {@code veces} veces con la estrategia dada. */
    private static List<Thread> lanzar(Mesa mesa, Estrategia estrategia, int veces) {
        List<Thread> filosofos = new ArrayList<>();
        for (int i = 0; i < mesa.cantidad(); i++) {
            int yo = i;
            filosofos.add(new Thread(() -> {
                try {
                    for (int k = 0; k < veces; k++) {
                        estrategia.comer(mesa, yo);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "filosofo-" + i));
        }
        for (Thread f : filosofos) {
            f.start();
        }
        return filosofos;
    }

    /** Espera a que terminen; si alguno no termina, falla diciendo si hay deadlock y los destraba. */
    private static void terminar(List<Thread> filosofos, long maxMs) throws InterruptedException {
        long limite = System.currentTimeMillis() + maxMs;
        for (Thread f : filosofos) {
            f.join(Math.max(1, limite - System.currentTimeMillis()));
        }
        List<String> vivos = new ArrayList<>();
        for (Thread f : filosofos) {
            if (f.isAlive()) {
                vivos.add(f.getName() + " (" + f.getState() + ")");
            }
        }
        if (vivos.isEmpty()) {
            return;
        }
        String ciclo = describirDeadlock();
        for (Thread f : filosofos) {
            f.interrupt();
        }
        throw new AssertionError("no terminaron en " + maxMs + " ms: " + String.join(", ", vivos)
                + (ciclo != null ? "\n" + ciclo : ""));
    }

    /** Espera hasta maxMs a que la JVM vea un deadlock entre estos hilos, y devuelve los trabados. */
    private static List<ThreadInfo> esperarDeadlock(List<Thread> hilos, long maxMs) throws InterruptedException {
        Set<Long> nuestros = new HashSet<>();
        for (Thread h : hilos) {
            nuestros.add(h.threadId());
        }
        var mx = ManagementFactory.getThreadMXBean();
        long limite = System.currentTimeMillis() + maxMs;
        while (System.currentTimeMillis() < limite) {
            long[] ids = mx.findDeadlockedThreads();
            if (ids != null) {
                List<ThreadInfo> trabados = new ArrayList<>();
                for (ThreadInfo t : mx.getThreadInfo(ids)) {
                    if (t != null && nuestros.contains(t.getThreadId())) {
                        trabados.add(t);
                    }
                }
                if (!trabados.isEmpty()) {
                    trabados.sort((a, b) -> a.getThreadName().compareTo(b.getThreadName()));
                    return trabados;
                }
            }
            Thread.sleep(20);
        }
        return List.of();
    }

    /** Un vecino toma el tenedor {@code ocupado}; el filósofo 0 intenta comer y tiene que rendirse. */
    private static void intentoFallido(Mesa mesa, int ocupado, int otro, String cual) throws Exception {
        CountDownLatch tomado = new CountDownLatch(1);
        CountDownLatch soltar = new CountDownLatch(1);
        Thread vecino = new Thread(() -> {
            mesa.tenedor(ocupado).lock();
            try {
                tomado.countDown();
                soltar.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                mesa.tenedor(ocupado).unlock();
            }
        }, "vecino");
        vecino.start();
        tomado.await();
        try {
            long t0 = System.nanoTime();
            boolean comio;
            try {
                comio = mesa.intentarComer(0, 100);
            } catch (IllegalMonitorStateException e) {
                throw new AssertionError("intentarComer lanzó IllegalMonitorStateException con el tenedor " + cual
                        + " ocupado: ¿soltaste un tenedor que no habías conseguido?");
            }
            long ms = (System.nanoTime() - t0) / 1_000_000;
            verificar(!comio, "con el tenedor " + cual + " ocupado, intentarComer devolvió true");
            verificar(ms >= 90, "con el tenedor " + cual + " ocupado se rindió en " + ms + " ms, y tendría que"
                    + " esperarlo hasta 100 ms: ¿usaste tryLock() sin tiempo?");
            verificar(ms < 500, "con el tenedor " + cual + " ocupado tardó " + ms + " ms en rendirse");
            verificar(!mesa.tenedor(otro).isLocked(), "después de rendirse quedó tomado el otro tenedor (el "
                    + otro + "): el filósofo se quedó con un tenedor en la mano");
        } finally {
            soltar.countDown();
            vecino.join(1000);
        }
    }

    private static void comieron(Mesa mesa, int veces) {
        int[] comidas = mesa.getComidas();
        for (int i = 0; i < comidas.length; i++) {
            iguales(veces, comidas[i], "comidas del filósofo " + i);
        }
    }

    private static void tenedoresLibres(Mesa mesa) {
        for (int k = 0; k < mesa.cantidad(); k++) {
            verificar(!mesa.tenedor(k).isLocked(), "el tenedor " + k + " quedó tomado al final: ¿falta un unlock()"
                    + " en un finally?");
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
