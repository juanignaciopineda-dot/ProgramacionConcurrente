import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/** Verificador automático del ejercicio 5.3. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 5.3 - Monitor con semáforos y signal and exit");

        caso("ColaDeCondicion: despertarUno() sin nadie esperando no guarda el aviso", 3000, () -> {
            Semaphore mutex = new Semaphore(1);
            ColaDeCondicion cola = new ColaDeCondicion();
            mutex.acquire();
            verificar(!cola.despertarUno(), "despertarUno() devolvió true sin nadie esperando");
            mutex.release();
            Thread a = new Thread(() -> {
                mutex.acquireUninterruptibly();
                cola.esperar(mutex);
                mutex.release();
            }, "A");
            a.start();
            verificar(esperarEstado(a, Thread.State.WAITING, 1000), "después de un despertarUno() sin nadie, el"
                    + " próximo esperar() tendría que dormir, y el hilo está " + a.getState()
                    + ": ¿hiciste release() aunque no había nadie?");
            mutex.acquire();
            cola.despertarUno();
            a.join(1000);
        });

        caso("ColaDeCondicion: esperar() suelta el mutex, y despertarUno() se lo pasa al despertado", 3000, () -> {
            Semaphore mutex = new Semaphore(1);
            ColaDeCondicion cola = new ColaDeCondicion();
            int[] permisosAlVolver = {-1};
            Thread a = new Thread(() -> {
                mutex.acquireUninterruptibly();
                cola.esperar(mutex);
                permisosAlVolver[0] = mutex.availablePermits();
                mutex.release();
            }, "A");
            a.start();
            verificar(esperarEstado(a, Thread.State.WAITING, 1000), "A tendría que estar durmiendo en la cola y está "
                    + a.getState());
            iguales(1, mutex.availablePermits(), "permisos del mutex con A dormido (esperar() tiene que soltarlo)");
            mutex.acquire();
            iguales(1, cola.esperando(), "hilos esperando en la cola");
            verificar(cola.despertarUno(), "despertarUno() devolvió false con A esperando");
            iguales(0, cola.esperando(), "hilos esperando después de despertar a A");
            a.join(1000);
            verificar(!a.isAlive(), "A no se despertó");
            iguales(0, permisosAlVolver[0], "permisos del mutex cuando A volvió de esperar() (tiene que volver con el"
                    + " mutex que le pasaron, sin que nadie más lo pueda tomar)");
            iguales(1, mutex.availablePermits(), "permisos del mutex al final");
        });

        caso("ColaDeCondicion: esperar() se anota en la cola antes de soltar el mutex", 3000, () -> {
            ColaDeCondicion cola = new ColaDeCondicion();
            MutexQueMira mutex = new MutexQueMira(cola);
            Thread a = new Thread(() -> {
                mutex.acquireUninterruptibly();
                cola.esperar(mutex);
                mutex.release();
            }, "A");
            a.start();
            verificar(esperarEstado(a, Thread.State.WAITING, 1000), "A tendría que estar durmiendo en la cola y está "
                    + a.getState());
            iguales(1, mutex.esperandoAlSoltar, "hilos anotados en la cola en el instante en que esperar() soltó el"
                    + " mutex (con 0, otro hilo podría entrar justo ahí, no ver a nadie esperando y no despertarlo:"
                    + " un aviso perdido)");
            mutex.acquire();
            cola.despertarUno();
            a.join(1000);
        });

        caso("ColaDeCondicion: despierta en orden de llegada", 3000, () -> {
            Semaphore mutex = new Semaphore(1);
            ColaDeCondicion cola = new ColaDeCondicion();
            List<String> orden = new ArrayList<>();
            List<Thread> hilos = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                Thread t = new Thread(() -> {
                    mutex.acquireUninterruptibly();
                    cola.esperar(mutex);
                    orden.add(Thread.currentThread().getName());
                    mutex.release();
                }, "W" + i);
                hilos.add(t);
                t.start();
                verificar(esperarEstado(t, Thread.State.WAITING, 1000), t.getName() + " tendría que estar durmiendo"
                        + " en la cola y está " + t.getState());
            }
            for (int i = 0; i < 3; i++) {
                mutex.acquire();
                cola.despertarUno();
            }
            for (Thread t : hilos) {
                t.join(1000);
            }
            mutex.acquire();
            iguales(List.of("W0", "W1", "W2"), orden, "orden en que se despertaron");
        });

        caso("SalaDeImpresion: constructor, tomar y devolver de a uno", 2000, () -> {
            esperaExcepcion(IllegalArgumentException.class, () -> new SalaDeImpresion(0, 0), "0 impresoras");
            SalaDeImpresion sala = new SalaDeImpresion(2, 0);
            iguales(2, sala.libres(), "libres al arrancar");
            sala.tomar();
            iguales(1, sala.libres(), "libres después de tomar una");
            sala.devolver();
            iguales(2, sala.libres(), "libres después de devolverla");
            esperaExcepcion(IllegalStateException.class, sala::devolver, "devolver con todas libres");
            iguales(2, sala.libres(), "libres después del devolver de más (si se cuelga, el mutex quedó tomado)");
        });

        caso("Con todas ocupadas, tomar() espera (WAITING) y devolver() le pasa la impresora", 3000, () -> {
            SalaDeImpresion sala = new SalaDeImpresion(1, 0);
            sala.tomar();
            Thread w = new Thread(sala::tomar, "W");
            w.start();
            verificar(esperarEstado(w, Thread.State.WAITING, 1000), "W tendría que estar esperando una impresora y está "
                    + w.getState());
            iguales(1, sala.esperando(), "hilos esperando una impresora");
            sala.devolver();
            w.join(1000);
            verificar(!w.isAlive(), "W no se despertó después de devolver()");
            iguales(0, sala.libres(), "libres (la impresora es de W ahora)");
            iguales(List.of("verificador", "W"), sala.getOrdenDeAtencion(), "orden de atención");
            sala.devolver();
        });

        caso("Los que esperan se atienden en orden de llegada", 3000, () -> {
            SalaDeImpresion sala = new SalaDeImpresion(1, 0);
            sala.tomar();
            List<Thread> hilos = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                Thread t = new Thread(() -> {
                    sala.tomar();
                    sala.devolver();
                }, "W" + i);
                hilos.add(t);
                t.start();
                int esperados = i;
                long limite = System.currentTimeMillis() + 1000;
                while (sala.esperando() < esperados && System.currentTimeMillis() < limite) {
                    Thread.sleep(2);
                }
            }
            sala.devolver();
            for (Thread t : hilos) {
                t.join(1000);
            }
            iguales(List.of("verificador", "W1", "W2", "W3"), sala.getOrdenDeAtencion(), "orden de atención");
            iguales(1, sala.libres(), "libres al final");
        });

        caso("La impresión va fuera del monitor: 3 documentos de 200 ms en 3 impresoras tardan unos 200 ms", 3000, () -> {
            SalaDeImpresion sala = new SalaDeImpresion(3, 200);
            long ms = enParalelo(3, 1, sala);
            info("3 documentos en 3 impresoras: " + ms + " ms");
            verificar(ms < 350, "3 documentos de 200 ms en 3 impresoras tardaron " + ms + " ms: ¿imprimís con el"
                    + " mutex tomado? La acción va fuera del monitor");
            iguales(3, sala.maxImprimiendo(), "documentos imprimiéndose a la vez");
        });

        caso("Estrés: 10 hilos imprimen 300 documentos cada uno en 2 impresoras (3 rondas)", 30000, () -> {
            for (int ronda = 1; ronda <= 3; ronda++) {
                SalaDeImpresion sala = new SalaDeImpresion(2, 0);
                enParalelo(10, 300, sala);
                verificar(sala.maxImprimiendo() <= 2, "ronda " + ronda + ": hubo " + sala.maxImprimiendo()
                        + " documentos imprimiéndose a la vez con 2 impresoras. ¿devolver() suelta el mutex después"
                        + " de despertar a alguien? Con signal and exit se lo pasa");
                iguales(2, sala.libres(), "ronda " + ronda + ": libres al final");
                iguales(3000, sala.getOrdenDeAtencion().size(), "ronda " + ronda + ": impresoras tomadas");
            }
        });

        prohibir(new String[] {"SalaDeImpresion.java", "ColaDeCondicion.java"}, "synchronized",
                "java.util.concurrent.locks", ".wait(", "notify");

        fin();
    }

    /** Un mutex que, cada vez que lo sueltan, anota cuántos hilos había anotados en la cola. */
    @SuppressWarnings("serial")
    private static final class MutexQueMira extends Semaphore {
        private final ColaDeCondicion cola;
        private volatile int esperandoAlSoltar = -1;

        MutexQueMira(ColaDeCondicion cola) {
            super(1);
            this.cola = cola;
        }

        @Override
        public void release() {
            esperandoAlSoltar = cola.esperando();
            super.release();
        }
    }

    /** {@code hilos} hilos imprimen {@code documentos} documentos cada uno; devuelve cuántos ms tardaron. */
    private static long enParalelo(int hilos, int documentos, SalaDeImpresion sala) throws InterruptedException {
        List<Thread> lista = new ArrayList<>();
        for (int i = 0; i < hilos; i++) {
            lista.add(new Thread(() -> {
                for (int d = 0; d < documentos; d++) {
                    sala.imprimir("doc-" + d);
                }
            }, "hilo-" + i));
        }
        long t0 = System.nanoTime();
        for (Thread t : lista) {
            t.start();
        }
        for (Thread t : lista) {
            t.join(10_000);
            verificar(!t.isAlive(), "el " + t.getName() + " quedó esperando (estado " + t.getState() + ")");
        }
        return (System.nanoTime() - t0) / 1_000_000;
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
