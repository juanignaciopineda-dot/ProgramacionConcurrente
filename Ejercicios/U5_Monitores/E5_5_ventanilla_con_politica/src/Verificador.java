import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/** Verificador automático del ejercicio 5.5. No hace falta modificarlo. */
public class Verificador {

    private static final int PRIORITARIO = 0;
    private static final int COMUN = 1;

    public static void main(String[] args) {
        titulo("Ejercicio 5.5 - Ventanilla con política");

        caso("PoliticaPrioridad: elige la preferida si tiene gente, si no la primera con gente", 2000, () -> {
            Politica p0 = new PoliticaPrioridad(0);
            iguales(0, p0.elegir(new boolean[] {true, true}), "preferida 0, las dos con gente");
            iguales(1, p0.elegir(new boolean[] {false, true}), "preferida 0 vacía");
            Politica p2 = new PoliticaPrioridad(2);
            iguales(2, p2.elegir(new boolean[] {true, true, true}), "preferida 2, las tres con gente");
            iguales(0, p2.elegir(new boolean[] {true, true, false}), "preferida 2 vacía");
            iguales(1, p2.elegir(new boolean[] {false, true, false}), "solo la 1 con gente");
        });

        caso("PoliticaAlternada: elige en ronda, salteando las colas vacías", 2000, () -> {
            Politica a = new PoliticaAlternada();
            boolean[] todas = {true, true, true};
            iguales(0, a.elegir(todas), "primera elección");
            iguales(1, a.elegir(todas), "segunda elección");
            iguales(2, a.elegir(todas), "tercera elección");
            iguales(0, a.elegir(todas), "cuarta elección (da la vuelta)");
            iguales(2, a.elegir(new boolean[] {true, false, true}), "después del 0, con la 1 vacía");
            iguales(0, a.elegir(new boolean[] {true, false, true}), "después del 2, con la 1 vacía");
            iguales(0, a.elegir(new boolean[] {true, false, false}), "solo la 0 con gente");
        });

        caso("Ventanilla: constructor, y liberar sin estar ocupada es un error", 2000, () -> {
            esperaExcepcion(IllegalArgumentException.class, () -> new Ventanilla(0, new PoliticaAlternada(), 0), "0 tipos");
            esperaExcepcion(IllegalArgumentException.class, () -> new Ventanilla(2, null, 0), "política null");
            Ventanilla v = new Ventanilla(2, new PoliticaAlternada(), 0);
            esperaExcepcion(IllegalStateException.class, v::liberar, "liberar una ventanilla libre");
            esperaExcepcion(IllegalArgumentException.class, () -> v.solicitar(2), "tipo inexistente");
            v.solicitar(COMUN);
            v.liberar();
        });

        caso("Con la ventanilla ocupada, el cliente espera (WAITING) en la cola de su tipo", 3000, () -> {
            Ventanilla v = new Ventanilla(2, new PoliticaAlternada(), 0);
            v.solicitar(COMUN);
            Thread p = new Thread(() -> v.solicitar(PRIORITARIO), "prioritario");
            p.start();
            verificar(esperarEstado(p, Thread.State.WAITING, 1000), "el cliente tendría que estar esperando y está "
                    + p.getState());
            iguales(1, v.esperando(PRIORITARIO), "esperando en la cola de prioritarios");
            iguales(0, v.esperando(COMUN), "esperando en la cola de comunes");
            v.liberar();
            p.join(1000);
            verificar(!p.isAlive(), "el cliente no pasó cuando se liberó la ventanilla");
            iguales(List.of("verificador", "prioritario"), v.getAtendidos(), "atendidos");
            v.liberar();
        });

        caso("Con PoliticaPrioridad(0), pasan todos los prioritarios antes que los comunes", 5000, () -> {
            Ventanilla v = new Ventanilla(2, new PoliticaPrioridad(PRIORITARIO), 0);
            List<String> atendidos = cola(v);
            iguales(List.of("verificador", "prioritario-0", "prioritario-1", "prioritario-2", "comun-0", "comun-1",
                    "comun-2"), atendidos, "orden de atención (los comunes llegaron antes)");
        });

        caso("Con PoliticaAlternada, prioritarios y comunes se turnan", 5000, () -> {
            Ventanilla v = new Ventanilla(2, new PoliticaAlternada(), 0);
            List<String> atendidos = cola(v);
            iguales(List.of("verificador", "prioritario-0", "comun-0", "prioritario-1", "comun-1", "prioritario-2",
                    "comun-2"), atendidos, "orden de atención");
        });

        caso("Estrés: 10 clientes de los dos tipos, 200 atenciones cada uno, nunca dos a la vez", 30000, () -> {
            Ventanilla v = new Ventanilla(2, new PoliticaAleatoria(42), 0);
            List<Thread> hilos = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                int tipo = i % 2;
                hilos.add(new Thread(() -> {
                    for (int k = 0; k < 200; k++) {
                        v.atender(tipo);
                    }
                }, "cliente-" + i));
            }
            for (Thread h : hilos) {
                h.start();
            }
            for (Thread h : hilos) {
                h.join(15_000);
                verificar(!h.isAlive(), "el " + h.getName() + " quedó esperando (estado " + h.getState() + ")");
            }
            iguales(1, v.maxAtendiendo(), "clientes atendidos a la vez como máximo (¿liberar() suelta el mutex o"
                    + " marca la ventanilla libre después de despertar a alguien?)");
            iguales(2000, v.getAtendidos().size(), "atenciones");
            v.solicitar(COMUN);
            v.liberar();
        });

        caso("Postergación: con prioritarios sin parar, un común pasa con PoliticaAlternada", 8000, () -> {
            long alternada = esperaDelComun(new PoliticaAlternada());
            verificar(alternada >= 0, "con PoliticaAlternada el cliente común esperó más de 1,5 s");
            info("con PoliticaAlternada el común esperó " + alternada + " ms");
            long prioridad = esperaDelComun(new PoliticaPrioridad(PRIORITARIO));
            info(prioridad < 0 ? "con PoliticaPrioridad(0) el común no pasó en 1,5 s: postergación indefinida"
                    : "con PoliticaPrioridad(0) el común esperó " + prioridad + " ms (esta vez hubo un hueco)");
        });

        prohibir(new String[] {"Ventanilla.java", "PoliticaPrioridad.java", "PoliticaAlternada.java"}, "synchronized",
                "java.util.concurrent.locks", ".wait(", "notify");

        fin();
    }

    /**
     * El verificador ocupa la ventanilla; esperan 3 comunes y después 3 prioritarios; la libera y
     * devuelve el orden de atención.
     */
    private static List<String> cola(Ventanilla v) throws InterruptedException {
        v.solicitar(COMUN);
        List<Thread> hilos = new ArrayList<>();
        String[] nombres = {"comun", "prioritario"};
        int[] tipos = {COMUN, PRIORITARIO};
        for (int grupo = 0; grupo < 2; grupo++) {
            for (int i = 0; i < 3; i++) {
                int tipo = tipos[grupo];
                Thread t = new Thread(() -> v.atender(tipo), nombres[grupo] + "-" + i);
                hilos.add(t);
                t.start();
                int esperados = i + 1;
                long limite = System.currentTimeMillis() + 1000;
                while (v.esperando(tipo) < esperados && System.currentTimeMillis() < limite) {
                    Thread.sleep(2);
                }
            }
        }
        v.liberar();
        for (Thread t : hilos) {
            t.join(2000);
            verificar(!t.isAlive(), "el " + t.getName() + " quedó esperando (estado " + t.getState() + ")");
        }
        return v.getAtendidos();
    }

    /**
     * Tres prioritarios usan la ventanilla sin parar; llega un común. Devuelve cuántos ms esperó, o -1
     * si no pasó en 1,5 s.
     */
    private static long esperaDelComun(Politica politica) throws InterruptedException {
        Ventanilla v = new Ventanilla(2, politica, 20);
        AtomicBoolean seguir = new AtomicBoolean(true);
        List<Thread> chorro = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Thread t = new Thread(() -> {
                while (seguir.get()) {
                    v.atender(PRIORITARIO);
                }
            }, "prioritario-" + i);
            chorro.add(t);
            t.start();
        }
        Thread.sleep(100);
        long t0 = System.nanoTime();
        Thread comun = new Thread(() -> v.atender(COMUN), "comun");
        comun.start();
        comun.join(1500);
        long ms = comun.isAlive() ? -1 : (System.nanoTime() - t0) / 1_000_000;
        seguir.set(false);
        comun.join(3000);
        for (Thread t : chorro) {
            t.join(3000);
        }
        return ms;
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
