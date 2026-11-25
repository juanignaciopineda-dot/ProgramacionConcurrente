import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Verificador automático del ejercicio 5.1. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 5.1 - Buffer con un lock y dos variables de condición");

        caso("Constructor: rechaza capacidad < 1", 2000, () -> {
            esperaExcepcion(IllegalArgumentException.class, () -> new BufferConCondiciones<String>(0), "capacidad 0");
            iguales(3, new BufferConCondiciones<String>(3).capacidad(), "capacidad()");
        });

        caso("Un solo hilo: FIFO, tamaño y poner(null)", 2000, () -> {
            BufferConCondiciones<String> b = new BufferConCondiciones<>(3);
            b.poner("a");
            b.poner("b");
            iguales(2, b.tamanio(), "tamaño con 2 elementos");
            iguales("a", b.sacar(), "primer elemento sacado");
            b.poner("c");
            iguales("b", b.sacar(), "segundo elemento sacado");
            iguales("c", b.sacar(), "tercer elemento sacado");
            iguales(0, b.tamanio(), "tamaño al final");
            esperaExcepcion(NullPointerException.class, () -> b.poner(null), "poner(null)");
        });

        caso("Con el buffer vacío, los consumidores esperan en noVacio (y no en noLleno)", 3000, () -> {
            BufferConCondiciones<String> b = new BufferConCondiciones<>(2);
            List<Thread> consumidores = lanzar(2, i -> b.sacar(), "consumidor");
            for (Thread c : consumidores) {
                verificar(esperarEstado(c, Thread.State.WAITING, 1000), "el " + c.getName() + " tendría que estar"
                        + " esperando en una condición y está " + c.getState());
            }
            iguales(2, b.esperandoElemento(), "consumidores esperando en noVacio");
            iguales(0, b.esperandoLugar(), "hilos esperando en noLleno (ahí esperan solo los productores)");
            b.poner("x");
            b.poner("y");
            for (Thread c : consumidores) {
                c.join(1000);
                verificar(!c.isAlive(), "el " + c.getName() + " no se despertó: ¿poner() hace noVacio.signal()?");
            }
        });

        caso("Con el buffer lleno, los productores esperan en noLleno (y no en noVacio)", 3000, () -> {
            BufferConCondiciones<Integer> b = new BufferConCondiciones<>(1);
            b.poner(0);
            List<Thread> productores = lanzar(2, i -> b.poner(i + 1), "productor");
            for (Thread p : productores) {
                verificar(esperarEstado(p, Thread.State.WAITING, 1000), "el " + p.getName() + " tendría que estar"
                        + " esperando en una condición y está " + p.getState());
            }
            iguales(2, b.esperandoLugar(), "productores esperando en noLleno");
            iguales(0, b.esperandoElemento(), "hilos esperando en noVacio (ahí esperan solo los consumidores)");
            iguales(1, b.tamanio(), "tamaño (no puede pasarse de la capacidad)");
            iguales(0, b.sacar(), "primer elemento sacado");
            iguales(true, b.sacar() != null, "segundo elemento sacado");
            iguales(true, b.sacar() != null, "tercer elemento sacado");
            for (Thread p : productores) {
                p.join(1000);
                verificar(!p.isAlive(), "el " + p.getName() + " no siguió: ¿sacar() hace noLleno.signal()?");
            }
        });

        caso("Interrumpir a un consumidor que espera: sacar() lanza InterruptedException y suelta el candado", 3000, () -> {
            BufferConCondiciones<String> b = new BufferConCondiciones<>(1);
            boolean[] lanzo = {false};
            Thread c = new Thread(() -> {
                try {
                    b.sacar();
                } catch (InterruptedException e) {
                    lanzo[0] = true;
                }
            }, "consumidor");
            c.start();
            esperarEstado(c, Thread.State.WAITING, 1000);
            c.interrupt();
            c.join(1000);
            verificar(!c.isAlive(), "el consumidor siguió esperando después de la interrupción");
            verificar(lanzo[0], "sacar() no lanzó InterruptedException: ¿la estás capturando adentro?");
            b.poner("x");
            iguales("x", b.sacar(), "elemento después de la interrupción (si se cuelga, quedó el candado tomado)");
        });

        caso("Estrés: 4 productores y 4 consumidores, capacidad 1 y 3 (3 rondas)", 30000, () -> {
            estres(1, 1);
            estres(2, 3);
            estres(3, 1);
        });

        prohibir(new String[] {"BufferConCondiciones.java"}, "signalAll", "synchronized", ".wait(", "notify",
                "Semaphore", "java.util.concurrent.Blocking");

        fin();
    }

    /** Tarea de un hilo, que recibe su número. */
    private interface Tarea {
        void correr(int i) throws Exception;
    }

    private static List<Thread> lanzar(int n, Tarea tarea, String nombre) {
        List<Thread> hilos = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int numero = i;
            hilos.add(new Thread(() -> {
                try {
                    tarea.correr(numero);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, nombre + "-" + i));
        }
        for (Thread h : hilos) {
            h.start();
        }
        return hilos;
    }

    private static void estres(int ronda, int capacidad) throws Exception {
        BufferConCondiciones<Integer> b = new BufferConCondiciones<>(capacidad);
        int porProductor = 5_000;
        List<Integer> consumidos = new ArrayList<>();
        boolean[] excedido = new boolean[1];
        List<Thread> hilos = new ArrayList<>();
        for (int p = 0; p < 4; p++) {
            int base = p * porProductor;
            hilos.add(new Thread(() -> {
                try {
                    for (int i = 0; i < porProductor; i++) {
                        b.poner(base + i);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "productor-" + p));
        }
        for (int c = 0; c < 4; c++) {
            hilos.add(new Thread(() -> {
                List<Integer> propios = new ArrayList<>();
                try {
                    for (int i = 0; i < porProductor; i++) {
                        propios.add(b.sacar());
                        if (b.tamanio() > b.capacidad()) {
                            excedido[0] = true;
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                synchronized (consumidos) {
                    consumidos.addAll(propios);
                }
            }, "consumidor-" + c));
        }
        for (Thread h : hilos) {
            h.start();
        }
        for (Thread h : hilos) {
            h.join(8000);
            verificar(!h.isAlive(), "ronda " + ronda + " (capacidad " + capacidad + "): el hilo " + h.getName()
                    + " quedó esperando para siempre (estado " + h.getState() + "). ¿Cada uno espera en su"
                    + " condición y avisa en la del otro?");
        }
        int total = 4 * porProductor;
        iguales(total, consumidos.size(), "ronda " + ronda + ": cantidad de elementos consumidos (si un consumidor"
                + " terminó con una excepción, ¿esperaste con if en vez de while?)");
        Set<Integer> distintos = new HashSet<>(consumidos);
        verificar(distintos.size() == total, "ronda " + ronda + ": hay elementos repetidos o perdidos");
        verificar(!excedido[0], "ronda " + ronda + ": el buffer tuvo más elementos que su capacidad");
        iguales(0, b.tamanio(), "ronda " + ronda + ": tamaño final");
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
