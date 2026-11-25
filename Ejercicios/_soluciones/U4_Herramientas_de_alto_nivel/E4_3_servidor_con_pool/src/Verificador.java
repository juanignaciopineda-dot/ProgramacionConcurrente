import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;

/** Verificador automático del ejercicio 4.3. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 4.3 - Servidor con un pool fijo de hilos");

        caso("Constructor: valida, y el pool arranca sin hilos", 2000, () -> {
            esperaExcepcion(IllegalArgumentException.class, () -> new Servidor(0), "0 hilos");
            Servidor s = new Servidor(3);
            iguales(0, s.hilosEnElPool(), "hilos en el pool antes del primer pedido (los crea a medida que llegan)");
            s.cerrar();
        });

        caso("Pedido.run: anota quién lo atendió y termina", 2000, () -> {
            Pedido p = new Pedido("p", 50);
            Thread h = new Thread(p, "a-mano");
            h.start();
            h.join(1000);
            iguales("a-mano", p.getAtendidoPor(), "atendidoPor");
            verificar(p.isTerminado(), "el pedido no quedó terminado");
            verificar(!p.isInterrumpido(), "el pedido quedó marcado como interrumpido sin que nadie lo interrumpiera");
        });

        caso("Pedido.run: si lo interrumpen, termina enseguida y restaura la interrupción", 3000, () -> {
            Pedido p = new Pedido("lento", 5000);
            boolean[] marca = {false};
            Thread h = new Thread(() -> {
                p.run();
                marca[0] = Thread.currentThread().isInterrupted();
            }, "a-mano");
            h.start();
            esperarEstado(h, Thread.State.TIMED_WAITING, 1000);
            h.interrupt();
            h.join(1000);
            verificar(!h.isAlive(), "el pedido siguió corriendo después de la interrupción");
            verificar(p.isInterrumpido(), "el pedido no quedó marcado como interrumpido");
            verificar(!p.isTerminado(), "un pedido interrumpido quedó marcado como terminado");
            verificar(marca[0], "el hilo salió de run() sin la marca de interrupción: restaurala con"
                    + " Thread.currentThread().interrupt()");
        });

        caso("Pool fijo: 12 pedidos de 100 ms con 3 hilos tardan unos 400 ms y usan siempre los mismos 3", 5000, () -> {
            Servidor s = new Servidor(3);
            List<Pedido> pedidos = new ArrayList<>();
            long t0 = System.nanoTime();
            for (int i = 0; i < 12; i++) {
                Pedido p = new Pedido("pedido-" + i, 100);
                pedidos.add(p);
                s.atender(p);
            }
            int maxHilos = 0;
            while (s.completados() < 12 && System.nanoTime() - t0 < 3_000_000_000L) {
                maxHilos = Math.max(maxHilos, s.hilosEnElPool());
                Thread.sleep(5);
            }
            s.cerrar();
            verificar(s.esperarCierre(3000), "el pool no terminó después de cerrar()");
            long ms = (System.nanoTime() - t0) / 1_000_000;
            info("12 pedidos con 3 hilos: " + ms + " ms, " + maxHilos + " hilos en el pool como máximo");
            Set<String> hilos = new HashSet<>();
            for (Pedido p : pedidos) {
                verificar(p.isTerminado(), p + " no se terminó");
                hilos.add(p.getAtendidoPor());
            }
            iguales(3, hilos.size(), "hilos distintos que atendieron pedidos (¿usaste newFixedThreadPool?)");
            verificar(maxHilos <= 3, "el pool llegó a tener " + maxHilos + " hilos");
            verificar(ms >= 380, "12 pedidos de 100 ms con 3 hilos tardaron " + ms + " ms: con 3 hilos son 4 tandas");
            verificar(ms < 1500, "12 pedidos de 100 ms con 3 hilos tardaron " + ms + " ms");
            iguales(12L, s.completados(), "completados()");
        });

        caso("Después de cerrar(), un pedido nuevo se rechaza, queda anotado y no se atiende", 3000, () -> {
            Servidor s = new Servidor(1);
            s.atender(new Pedido("primero", 100));
            s.cerrar();
            Pedido tarde = new Pedido("tarde", 10);
            try {
                s.atender(tarde);
            } catch (RejectedExecutionException e) {
                throw new AssertionError("atender() lanzó RejectedExecutionException con el pool cerrado: ¿le"
                        + " pusiste el RegistroDeRechazos al pool con setRejectedExecutionHandler?");
            }
            iguales(List.of("tarde"), s.getRechazados(), "pedidos rechazados");
            verificar(s.esperarCierre(2000), "el pool no terminó después de cerrar()");
            iguales(null, tarde.getAtendidoPor(), "quién atendió el pedido rechazado");
        });

        caso("cerrar() deja terminar los pedidos que estaban en la cola", 3000, () -> {
            Servidor s = new Servidor(1);
            List<Pedido> pedidos = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                Pedido p = new Pedido("pedido-" + i, 80);
                pedidos.add(p);
                s.atender(p);
            }
            s.cerrar();
            verificar(s.esperarCierre(2000), "el pool no terminó después de cerrar()");
            for (Pedido p : pedidos) {
                verificar(p.isTerminado(), p + " no se terminó: shutdown() atiende también los que esperaban en"
                        + " la cola (¿usaste shutdownNow()?)");
            }
            iguales(3L, s.completados(), "completados()");
        });

        caso("cerrarYa() devuelve los pedidos de la cola e interrumpe a los que se están atendiendo", 5000, () -> {
            Servidor s = new Servidor(2);
            List<Pedido> pedidos = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                Pedido p = new Pedido("pedido-" + i, 3000);
                pedidos.add(p);
                s.atender(p);
            }
            long limite = System.currentTimeMillis() + 1000;
            while (s.atendiendo() < 2 && System.currentTimeMillis() < limite) {
                Thread.sleep(5);
            }
            iguales(2, s.atendiendo(), "pedidos atendiéndose antes de cerrar");
            List<Runnable> pendientes = s.cerrarYa();
            iguales(4, pendientes.size(), "pedidos que devolvió cerrarYa()");
            long t0 = System.nanoTime();
            verificar(s.esperarCierre(1000), "el pool no terminó después de cerrarYa(): ¿Pedido.run() responde a"
                    + " la interrupción?");
            long ms = (System.nanoTime() - t0) / 1_000_000;
            verificar(ms < 500, "después de cerrarYa() el pool tardó " + ms + " ms en terminar");
            int interrumpidos = 0;
            for (Pedido p : pedidos) {
                if (p.getAtendidoPor() != null) {
                    verificar(p.isInterrumpido(), p + " se estaba atendiendo y no lo interrumpieron");
                    interrumpidos++;
                } else {
                    verificar(pendientes.contains(p), p + " no se atendió y no está en la lista de pendientes");
                }
            }
            iguales(2, interrumpidos, "pedidos interrumpidos");
        });

        caso("esperarCierre devuelve false si se vence el plazo", 3000, () -> {
            Servidor s = new Servidor(1);
            s.atender(new Pedido("largo", 800));
            s.cerrar();
            long t0 = System.nanoTime();
            boolean termino = s.esperarCierre(100);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            verificar(!termino, "esperarCierre(100) devolvió true con un pedido de 800 ms en curso");
            verificar(ms >= 90, "esperarCierre(100) volvió en " + ms + " ms");
            verificar(s.esperarCierre(2000), "esperarCierre(2000) tendría que ver terminar al pool");
        });

        prohibir(new String[] {"Servidor.java", "Pedido.java", "RegistroDeRechazos.java"}, "new Thread",
                ".start()", "newCachedThreadPool");

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
