import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Verificador automático del ejercicio 2.5. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 2.5 - Cola cerrable y pipeline con terminación limpia");

        caso("ColaCerrable en un solo hilo: FIFO", 2000, () -> {
            ColaCerrable<String> c = new ColaCerrable<>();
            c.poner("a");
            c.poner("b");
            iguales(Optional.of("a"), c.sacar(), "primer sacar()");
            iguales(Optional.of("b"), c.sacar(), "segundo sacar()");
            verificar(!c.estaCerrada(), "la cola dice estar cerrada sin que nadie la cerrara");
        });

        caso("Cerrar con elementos: se entregan los que quedan y después Optional.empty()", 2000, () -> {
            ColaCerrable<String> c = new ColaCerrable<>();
            c.poner("x");
            c.poner("y");
            c.cerrar();
            verificar(c.estaCerrada(), "estaCerrada() da false después de cerrar()");
            iguales(Optional.of("x"), c.sacar(), "primer sacar() después de cerrar");
            iguales(Optional.of("y"), c.sacar(), "segundo sacar() después de cerrar");
            iguales(Optional.empty(), c.sacar(), "sacar() con la cola cerrada y vacía");
            iguales(Optional.empty(), c.sacar(), "sacar() otra vez");
            c.cerrar();
        });

        caso("poner() en una cola cerrada lanza IllegalStateException; poner(null), NullPointerException", 2000, () -> {
            ColaCerrable<String> c = new ColaCerrable<>();
            esperaExcepcion(NullPointerException.class, () -> c.poner(null), "poner(null)");
            c.cerrar();
            esperaExcepcion(IllegalStateException.class, () -> c.poner("tarde"), "poner() después de cerrar()");
        });

        caso("sacar() con la cola vacía espera en WAITING, y poner() lo despierta", 3000, () -> {
            ColaCerrable<String> c = new ColaCerrable<>();
            List<Optional<String>> recibido = new ArrayList<>();
            Thread consumidor = consumidor(c, recibido, 1);
            verificar(esperarEstado(consumidor, Thread.State.WAITING, 1000),
                    "el consumidor tendría que estar en WAITING y está " + consumidor.getState());
            c.poner("hola");
            consumidor.join(1000);
            verificar(!consumidor.isAlive(), "el consumidor no se despertó con poner()");
            iguales(List.of(Optional.of("hola")), recibido, "lo que recibió");
        });

        caso("cerrar() despierta a TODOS los consumidores que esperan (5 hilos)", 3000, () -> {
            ColaCerrable<String> c = new ColaCerrable<>();
            List<Optional<String>> recibido = new ArrayList<>();
            List<Thread> consumidores = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                consumidores.add(consumidor(c, recibido, 1));
            }
            for (Thread h : consumidores) {
                verificar(esperarEstado(h, Thread.State.WAITING, 1000), h.getName() + " no quedó esperando");
            }
            c.cerrar();
            for (Thread h : consumidores) {
                h.join(1000);
                verificar(!h.isAlive(), h.getName() + " sigue esperando después de cerrar(): ¿notify() en vez de notifyAll()?");
            }
            iguales(5, recibido.size(), "cantidad de respuestas");
            for (Optional<String> r : recibido) {
                iguales(Optional.empty(), r, "respuesta de un consumidor");
            }
        });

        caso("Empaquetador: procesa todo lo que hay en la entrada y termina cuando se cierra", 3000, () -> {
            ColaCerrable<String> entrada = new ColaCerrable<>();
            ColaCerrable<String> salida = new ColaCerrable<>();
            for (int i = 1; i <= 3; i++) {
                entrada.poner("pedido-" + i);
            }
            entrada.cerrar();
            Thread h = new Thread(new Empaquetador(entrada, salida), "empaquetador");
            h.start();
            h.join(1000);
            verificar(!h.isAlive(), "el empaquetador no terminó con la entrada cerrada y vacía");
            verificar(!salida.estaCerrada(), "el empaquetador cerró la cola de salida, y eso no le corresponde");
            salida.cerrar();
            for (int i = 1; i <= 3; i++) {
                iguales(Optional.of("paquete-" + i), salida.sacar(), "paquete " + i);
            }
        });

        caso("Despachante: anota todo lo que saca y termina cuando se cierra", 3000, () -> {
            ColaCerrable<String> entrada = new ColaCerrable<>();
            Despachante d = new Despachante(entrada);
            Thread h = new Thread(d, "despachante");
            h.start();
            entrada.poner("paquete-1");
            entrada.poner("paquete-2");
            entrada.cerrar();
            h.join(1000);
            verificar(!h.isAlive(), "el despachante no terminó con la entrada cerrada");
            iguales(List.of("paquete-1", "paquete-2"), d.getDespachados(), "despachados");
        });

        caso("Pipeline 1000 pedidos, 3 empaquetadores, 2 despachantes", 10000, () -> pipeline(1000, 3, 2));
        caso("Pipeline sin pedidos: termina igual", 5000, () -> pipeline(0, 2, 2));
        caso("Pipeline 50 pedidos, 1 empaquetador, 8 despachantes", 5000, () -> pipeline(50, 1, 8));
        caso("Pipeline 20000 pedidos, 4 y 4 (3 rondas)", 30000, () -> {
            for (int i = 0; i < 3; i++) {
                pipeline(20_000, 4, 4);
            }
        });

        prohibir(new String[] {"ColaCerrable.java", "Empaquetador.java", "Despachante.java", "Pipeline.java"},
                "java.util.concurrent", "Thread.sleep", "System.exit", ".stop(", "Collections.synchronized");

        fin();
    }

    private static Thread consumidor(ColaCerrable<String> c, List<Optional<String>> recibido, int veces) {
        Thread h = new Thread(() -> {
            try {
                for (int i = 0; i < veces; i++) {
                    Optional<String> r = c.sacar();
                    synchronized (recibido) {
                        recibido.add(r);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "consumidor-" + System.nanoTime() % 1000);
        h.start();
        return h;
    }

    private static void pipeline(int pedidos, int empaquetadores, int despachantes) throws Exception {
        Pipeline.Resultado r = Pipeline.ejecutar(pedidos, empaquetadores, despachantes);
        verificar(r != null, "ejecutar() devolvió null");
        iguales(empaquetadores + despachantes, r.hilos().size(), "cantidad de hilos creados");
        Set<String> nombres = new HashSet<>();
        for (Thread h : r.hilos()) {
            nombres.add(h.getName());
            iguales(Thread.State.TERMINATED, h.getState(), "estado de " + h.getName() + " al volver de ejecutar()");
        }
        for (int i = 1; i <= empaquetadores; i++) {
            verificar(nombres.contains("empaquetador-" + i), "no hay un hilo llamado empaquetador-" + i);
        }
        for (int i = 1; i <= despachantes; i++) {
            verificar(nombres.contains("despachante-" + i), "no hay un hilo llamado despachante-" + i);
        }
        iguales(pedidos, r.despachados().size(), "cantidad de paquetes despachados");
        Set<String> paquetes = new HashSet<>(r.despachados());
        for (int i = 1; i <= pedidos; i++) {
            verificar(paquetes.contains("paquete-" + i), "falta el paquete-" + i);
        }
        verificar(paquetes.size() == pedidos, "hay paquetes repetidos");
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
