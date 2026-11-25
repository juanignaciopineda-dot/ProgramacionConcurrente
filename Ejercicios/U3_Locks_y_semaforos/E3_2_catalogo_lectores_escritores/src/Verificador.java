import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/** Verificador automático del ejercicio 3.2. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 3.2 - Catálogo con ReadWriteLock");

        caso("Un solo hilo: precio, actualizar, aplicarAumento y copia", 2000, () -> {
            Catalogo c = new Catalogo();
            iguales(Optional.empty(), c.precio("pan"), "precio de un producto que no está");
            c.actualizar("pan", 100);
            c.actualizar("leche", 200);
            iguales(Optional.of(100.0), c.precio("pan"), "precio del pan");
            esperaExcepcion(IllegalArgumentException.class, () -> c.actualizar("pan", -1), "precio negativo");
            c.aplicarAumento(10);
            cerca(110.0, c.precio("pan").orElse(-1.0), "pan después de un aumento del 10 %");
            cerca(220.0, c.precio("leche").orElse(-1.0), "leche después de un aumento del 10 %");
            Map<String, Double> foto = c.copia();
            iguales(2, foto.size(), "productos en la copia");
            c.actualizar("yerba", 300);
            iguales(2, foto.size(), "productos en una copia sacada antes de agregar la yerba (¿devolviste el"
                    + " mapa interno en vez de una copia?)");
        });

        caso("Los lectores leen a la vez: 4 lecturas de 150 ms tardan unos 150 ms", 5000, () -> {
            Catalogo c = new Catalogo(150);
            c.actualizar("pan", 100);
            long ms = enParalelo(4, i -> c.precio("pan"));
            info("4 lecturas a la vez: " + ms + " ms");
            verificar(ms >= 140, "4 lecturas tardaron " + ms + " ms: ¿llamaste a simularLectura()?");
            verificar(ms < 290, "4 lecturas tardaron " + ms + " ms: si van de a una tardan 600. ¿Leíste con el"
                    + " candado de escritura?");
        });

        caso("El escritor entra solo: 2 escrituras de 150 ms tardan unos 300 ms", 5000, () -> {
            Catalogo c = new Catalogo(150);
            long ms = enParalelo(2, i -> c.actualizar("producto-" + i, 1));
            info("2 escrituras a la vez: " + ms + " ms");
            verificar(ms >= 290, "2 escrituras tardaron " + ms + " ms: tendrían que ir de a una");
        });

        caso("Mientras hay un escritor, los lectores esperan", 5000, () -> {
            Catalogo c = new Catalogo(150);
            long t0 = System.nanoTime();
            Thread escritor = new Thread(() -> c.actualizar("pan", 100), "escritor");
            escritor.start();
            Thread.sleep(30);
            long ms = enParalelo(3, i -> c.precio("pan"));
            escritor.join(2000);
            long total = (System.nanoTime() - t0) / 1_000_000;
            info("1 escritura y 3 lecturas: " + total + " ms");
            verificar(total >= 290, "1 escritura y 3 lecturas tardaron " + total + " ms: las lecturas tendrían"
                    + " que esperar a que termine la escritura (150 + 150 ms)");
            verificar(ms < 400, "las 3 lecturas tardaron " + ms + " ms: después de la escritura tendrían que"
                    + " ir juntas");
        });

        caso("aplicarAumento es atómico: copia() nunca ve el catálogo a medio aumentar", 20000, () -> {
            Catalogo c = new Catalogo();
            for (int i = 0; i < 20; i++) {
                c.actualizar("producto-" + i, 100);
            }
            Thread escritor = new Thread(() -> {
                for (int k = 0; k < 300; k++) {
                    c.aplicarAumento(1);
                }
            }, "escritor");
            int[] mezclas = new int[1];
            List<Thread> lectores = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                lectores.add(new Thread(() -> {
                    while (escritor.isAlive()) {
                        Map<String, Double> foto = c.copia();
                        if (foto.values().stream().distinct().count() > 1) {
                            synchronized (mezclas) {
                                mezclas[0]++;
                            }
                        }
                    }
                }, "lector-" + i));
            }
            escritor.start();
            for (Thread l : lectores) {
                l.start();
            }
            escritor.join(10_000);
            for (Thread l : lectores) {
                l.join(5000);
            }
            iguales(0, mezclas[0], "copias con algunos precios aumentados y otros no (¿tomaste el candado de"
                    + " escritura una sola vez para todo el aumento?)");
            cerca(100 * Math.pow(1.01, 300), c.precio("producto-7").orElse(-1.0), "precio final");
        });

        caso("obtenerOCalcular: 8 hilos piden el mismo producto que falta y se calcula una sola vez", 5000, () -> {
            Catalogo c = new Catalogo();
            AtomicInteger calculos = new AtomicInteger();
            double[] resultados = new double[8];
            enParalelo(8, i -> resultados[i] = c.obtenerOCalcular("mate", p -> {
                calculos.incrementAndGet();
                dormir(50);
                return 42.0;
            }));
            iguales(1, calculos.get(), "veces que se llamó a calcular (después de tomar el candado de escritura,"
                    + " ¿volviste a mirar si otro hilo ya lo había calculado?)");
            for (double r : resultados) {
                cerca(42.0, r, "precio que recibió cada hilo");
            }
            iguales(Optional.of(42.0), c.precio("mate"), "precio guardado en el catálogo");
        });

        caso("obtenerOCalcular con el producto ya cargado no calcula nada", 3000, () -> {
            Catalogo c = new Catalogo();
            c.actualizar("pan", 100);
            AtomicInteger calculos = new AtomicInteger();
            double precio = c.obtenerOCalcular("pan", p -> {
                calculos.incrementAndGet();
                return -1.0;
            });
            cerca(100.0, precio, "precio devuelto");
            iguales(0, calculos.get(), "veces que se llamó a calcular");
        });

        prohibir(new String[] {"Catalogo.java"}, "synchronized", "Semaphore", "ConcurrentHashMap",
                "Collections.synchronized", "StampedLock");

        fin();
    }

    /** Tarea de un hilo, que recibe su número. */
    private interface Tarea {
        void correr(int i) throws Exception;
    }

    /** Lanza n hilos que arrancan a la vez, espera que terminen y devuelve cuántos ms tardaron. */
    private static long enParalelo(int n, Tarea tarea) throws InterruptedException {
        CountDownLatch largada = new CountDownLatch(1);
        List<Thread> hilos = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int numero = i;
            hilos.add(new Thread(() -> {
                try {
                    largada.await();
                    tarea.correr(numero);
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, "hilo-" + i));
        }
        for (Thread h : hilos) {
            h.start();
        }
        long t0 = System.nanoTime();
        largada.countDown();
        for (Thread h : hilos) {
            h.join(5000);
            verificar(!h.isAlive(), "el hilo " + h.getName() + " no terminó (estado " + h.getState() + ")");
        }
        return (System.nanoTime() - t0) / 1_000_000;
    }

    private static void dormir(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void cerca(double esperado, double obtenido, String que) {
        if (Math.abs(esperado - obtenido) > 1e-6 * Math.max(1, Math.abs(esperado))) {
            throw new AssertionError(que + ": se esperaba <" + esperado + "> pero dio <" + obtenido + ">");
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
