import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Verificador automático del ejercicio 3.4. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 3.4 - Buffer con semáforos y largada");

        caso("Buffer: el constructor rechaza capacidad < 1", 2000, () -> {
            esperaExcepcion(IllegalArgumentException.class, () -> new BufferConSemaforos<String>(0), "capacidad 0");
            iguales(3, new BufferConSemaforos<String>(3).capacidad(), "capacidad()");
        });

        caso("Buffer: un solo hilo, FIFO y tamaño", 2000, () -> {
            BufferConSemaforos<String> b = new BufferConSemaforos<>(3);
            b.poner("a");
            b.poner("b");
            b.poner("c");
            iguales(3, b.tamanio(), "tamaño con 3 elementos");
            iguales("a", b.sacar(), "primer elemento sacado");
            iguales("b", b.sacar(), "segundo elemento sacado");
            b.poner("d");
            iguales("c", b.sacar(), "tercer elemento sacado");
            iguales("d", b.sacar(), "cuarto elemento sacado");
            iguales(0, b.tamanio(), "tamaño al final");
        });

        caso("Buffer: poner(null) lanza NullPointerException sin gastar un hueco", 3000, () -> {
            BufferConSemaforos<String> b = new BufferConSemaforos<>(1);
            esperaExcepcion(NullPointerException.class, () -> b.poner(null), "poner(null)");
            Thread productor = new Thread(() -> {
                try {
                    b.poner("a");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "productor");
            productor.start();
            productor.join(1000);
            verificar(!productor.isAlive(), "después de poner(null), un poner() en un buffer vacío de capacidad 1"
                    + " se quedó esperando: ¿tomaste el hueco antes de validar?");
            iguales(1, b.tamanio(), "tamaño");
        });

        caso("Buffer: un consumidor con el buffer vacío espera (WAITING) y se despierta al poner", 3000, () -> {
            BufferConSemaforos<String> b = new BufferConSemaforos<>(2);
            String[] recibido = new String[1];
            Thread consumidor = new Thread(() -> {
                try {
                    recibido[0] = b.sacar();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "consumidor");
            consumidor.start();
            verificar(esperarEstado(consumidor, Thread.State.WAITING, 1000),
                    "el consumidor tendría que estar esperando en un semáforo y está " + consumidor.getState());
            b.poner("hola");
            consumidor.join(1000);
            verificar(!consumidor.isAlive(), "el consumidor no se despertó después de poner(): ¿falta items.release()?");
            iguales("hola", recibido[0], "lo que recibió el consumidor");
        });

        caso("Buffer: un productor con el buffer lleno espera (WAITING) y sigue al sacar", 3000, () -> {
            BufferConSemaforos<Integer> b = new BufferConSemaforos<>(2);
            b.poner(1);
            b.poner(2);
            Thread productor = new Thread(() -> {
                try {
                    b.poner(3);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "productor");
            productor.start();
            verificar(esperarEstado(productor, Thread.State.WAITING, 1000),
                    "el productor tendría que estar esperando con el buffer lleno y está " + productor.getState());
            iguales(2, b.tamanio(), "tamaño (no puede pasarse de la capacidad)");
            iguales(1, b.sacar(), "elemento sacado");
            productor.join(1000);
            verificar(!productor.isAlive(), "el productor no siguió después de sacar(): ¿falta huecos.release()?");
            iguales(2, b.tamanio(), "tamaño final");
        });

        caso("Buffer: interrumpir a un consumidor que espera no rompe el buffer", 3000, () -> {
            BufferConSemaforos<String> b = new BufferConSemaforos<>(1);
            boolean[] lanzo = {false};
            Thread consumidor = new Thread(() -> {
                try {
                    b.sacar();
                } catch (InterruptedException e) {
                    lanzo[0] = true;
                }
            }, "consumidor");
            consumidor.start();
            esperarEstado(consumidor, Thread.State.WAITING, 1000);
            consumidor.interrupt();
            consumidor.join(1000);
            verificar(!consumidor.isAlive(), "el consumidor siguió esperando después de la interrupción");
            verificar(lanzo[0], "sacar() no lanzó InterruptedException: ¿la estás capturando adentro?");
            b.poner("x");
            iguales("x", b.sacar(), "elemento después de la interrupción");
            iguales(0, b.tamanio(), "tamaño final");
        });

        caso("Buffer: estrés con 4 productores y 4 consumidores, capacidad 1 y 3", 30000, () -> {
            estres(1, 1);
            estres(2, 3);
            estres(3, 1);
        });

        caso("Largada: los corredores esperan (WAITING) hasta que el juez larga", 3000, () -> {
            Largada largada = new Largada(5);
            int[] salieron = {0};
            List<Thread> corredores = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                corredores.add(new Thread(() -> {
                    try {
                        largada.esperarLargada();
                        synchronized (salieron) {
                            salieron[0]++;
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }, "corredor-" + i));
            }
            for (Thread c : corredores) {
                c.start();
            }
            for (Thread c : corredores) {
                verificar(esperarEstado(c, Thread.State.WAITING, 1000),
                        "el " + c.getName() + " tendría que estar esperando la largada y está " + c.getState());
            }
            iguales(5, largada.esperando(), "esperando() antes de largar");
            synchronized (salieron) {
                iguales(0, salieron[0], "corredores que salieron antes de la largada");
            }
            largada.largar();
            for (Thread c : corredores) {
                c.join(1000);
                verificar(!c.isAlive(), "el " + c.getName() + " no salió después de largar(): ¿soltaste un"
                        + " permiso por corredor?");
            }
            iguales(0, largada.esperando(), "esperando() después de largar");
        });

        caso("Largada: un corredor que llega tarde sale sin esperar (el semáforo recuerda la señal)", 3000, () -> {
            Largada largada = new Largada(2);
            Thread puntual = new Thread(() -> {
                try {
                    largada.esperarLargada();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "puntual");
            puntual.start();
            esperarEstado(puntual, Thread.State.WAITING, 1000);
            largada.largar();
            puntual.join(1000);
            verificar(!puntual.isAlive(), "el corredor puntual no salió después de largar()");
            Thread tarde = new Thread(() -> {
                try {
                    largada.esperarLargada();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "tardío");
            tarde.start();
            tarde.join(1000);
            verificar(!tarde.isAlive(), "el corredor que llegó después de la largada se quedó esperando");
        });

        prohibir(new String[] {"BufferConSemaforos.java", "Largada.java"}, "synchronized", ".wait(", "notify",
                "java.util.concurrent.locks", "BlockingQueue", "CountDownLatch", "Atomic", "Collections.synchronized");

        fin();
    }

    private static void estres(int ronda, int capacidad) throws Exception {
        BufferConSemaforos<Integer> b = new BufferConSemaforos<>(capacidad);
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
                    + " quedó esperando para siempre (estado " + h.getState() + "). ¿Tomaste el mutex antes que"
                    + " huecos o items?");
        }
        int total = 4 * porProductor;
        iguales(total, consumidos.size(), "ronda " + ronda + ": cantidad de elementos consumidos");
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
