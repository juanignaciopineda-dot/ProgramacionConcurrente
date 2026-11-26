import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Verificador automático del ejercicio 5.4. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 5.4 - Monitor de Hoare (signal and urgent wait)");

        caso("entrar/salir: exclusión mutua con 8 hilos que entran 2000 veces cada uno", 10000, () -> {
            MonitorHoare m = new MonitorHoare();
            int[] adentro = {0};
            int[] cuenta = {0};
            boolean[] dos = {false};
            List<Thread> hilos = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                hilos.add(new Thread(() -> {
                    for (int k = 0; k < 2000; k++) {
                        m.entrar();
                        if (++adentro[0] > 1) {
                            dos[0] = true;
                        }
                        cuenta[0]++;
                        adentro[0]--;
                        m.salir();
                    }
                }, "hilo-" + i));
            }
            for (Thread h : hilos) {
                h.start();
            }
            for (Thread h : hilos) {
                h.join(5000);
                verificar(!h.isAlive(), "el " + h.getName() + " quedó esperando para entrar (estado " + h.getState() + ")");
            }
            verificar(!dos[0], "hubo dos hilos adentro del monitor a la vez");
            m.entrar();
            iguales(16_000, cuenta[0], "entradas contadas");
            m.salir();
        });

        caso("esperar() deja el monitor: otro hilo puede entrar mientras uno espera", 3000, () -> {
            MonitorHoare m = new MonitorHoare();
            MonitorHoare.Condicion c = m.nuevaCondicion();
            Thread w = new Thread(() -> {
                m.entrar();
                c.esperar();
                m.salir();
            }, "W");
            w.start();
            verificar(esperarEstado(w, Thread.State.WAITING, 1000), "W tendría que estar esperando y está " + w.getState());
            Thread x = new Thread(() -> {
                m.entrar();
                m.salir();
            }, "X");
            x.start();
            x.join(1000);
            verificar(!x.isAlive(), "con W esperando en una condición, X no pudo entrar al monitor: esperar() tiene"
                    + " que dejarlo");
            m.entrar();
            iguales(1, c.esperando(), "hilos esperando la condición");
            c.senalar();
            m.salir();
            w.join(1000);
            verificar(!w.isAlive(), "W no se despertó con senalar()");
        });

        caso("senalar() sin nadie esperando no hace nada ni guarda el aviso", 3000, () -> {
            MonitorHoare m = new MonitorHoare();
            MonitorHoare.Condicion c = m.nuevaCondicion();
            m.entrar();
            c.senalar();
            iguales(0, m.enCortesia(), "hilos en cortesía después de una señal sin nadie esperando");
            m.salir();
            Thread w = new Thread(() -> {
                m.entrar();
                c.esperar();
                m.salir();
            }, "W");
            w.start();
            verificar(esperarEstado(w, Thread.State.WAITING, 1000), "después de una señal sin nadie, el próximo"
                    + " esperar() tendría que dormir, y W está " + w.getState());
            m.entrar();
            c.senalar();
            m.salir();
            w.join(1000);
        });

        caso("Signal and urgent wait: primero el despertado, después el que señalizó y recién ahí la entrada", 5000, () -> {
            MonitorHoare m = new MonitorHoare();
            MonitorHoare.Condicion c = m.nuevaCondicion();
            List<String> registro = new ArrayList<>();
            Thread w = new Thread(() -> {
                m.entrar();
                c.esperar();
                registro.add("despertado");
                m.salir();
            }, "W");
            w.start();
            esperarEstado(w, Thread.State.WAITING, 1000);
            m.entrar();
            Thread e = new Thread(() -> {
                m.entrar();
                registro.add("recién llegado");
                m.salir();
            }, "E");
            e.start();
            verificar(esperarEstado(e, Thread.State.WAITING, 1000), "E tendría que estar en la cola de entrada");
            c.senalar();
            registro.add("el que señalizó");
            m.salir();
            w.join(1000);
            e.join(1000);
            m.entrar();
            iguales(List.of("despertado", "el que señalizó", "recién llegado"), registro, "orden en que usaron el"
                    + " monitor");
            m.salir();
        });

        caso("Si el despertado se vuelve a dormir, el monitor pasa a la cola de cortesía y no a la entrada", 5000, () -> {
            MonitorHoare m = new MonitorHoare();
            MonitorHoare.Condicion c = m.nuevaCondicion();
            MonitorHoare.Condicion d = m.nuevaCondicion();
            List<String> registro = new ArrayList<>();
            Thread w = new Thread(() -> {
                m.entrar();
                c.esperar();
                registro.add("W despertó");
                d.esperar();
                registro.add("W otra vez");
                m.salir();
            }, "W");
            w.start();
            esperarEstado(w, Thread.State.WAITING, 1000);
            m.entrar();
            Thread e = new Thread(() -> {
                m.entrar();
                registro.add("recién llegado");
                m.salir();
            }, "E");
            e.start();
            esperarEstado(e, Thread.State.WAITING, 1000);
            c.senalar();
            registro.add("S siguió");
            d.senalar();
            registro.add("S terminó");
            m.salir();
            w.join(1000);
            e.join(1000);
            m.entrar();
            iguales(List.of("W despertó", "S siguió", "W otra vez", "S terminó", "recién llegado"), registro,
                    "orden en que usaron el monitor (cuando W se vuelve a dormir, ¿esperar() le pasa el monitor"
                            + " al que está en cortesía?)");
            m.salir();
        });

        caso("BufferHoare con if: un solo hilo", 2000, () -> {
            BufferHoare<String> b = new BufferHoare<>(2);
            b.poner("a");
            b.poner("b");
            iguales(2, b.tamanio(), "tamaño");
            iguales("a", b.sacar(), "primero");
            b.poner("c");
            iguales("b", b.sacar(), "segundo");
            iguales("c", b.sacar(), "tercero");
            iguales(0, b.tamanio(), "tamaño al final");
        });

        caso("BufferHoare con if: estrés con 4 productores y 4 consumidores, capacidad 1 y 3", 30000, () -> {
            estres(1, 1);
            estres(2, 3);
            estres(3, 1);
        });

        prohibir(new String[] {"MonitorHoare.java", "BufferHoare.java"}, "synchronized", "java.util.concurrent.locks",
                ".wait(", "notify", "while (cantidad");

        fin();
    }

    private static void estres(int ronda, int capacidad) throws Exception {
        BufferHoare<Integer> b = new BufferHoare<>(capacidad);
        int porProductor = 5_000;
        List<Integer> consumidos = new ArrayList<>();
        List<Thread> hilos = new ArrayList<>();
        for (int p = 0; p < 4; p++) {
            int base = p * porProductor;
            hilos.add(new Thread(() -> {
                for (int i = 0; i < porProductor; i++) {
                    b.poner(base + i);
                }
            }, "productor-" + p));
        }
        for (int c = 0; c < 4; c++) {
            hilos.add(new Thread(() -> {
                List<Integer> propios = new ArrayList<>();
                for (int i = 0; i < porProductor; i++) {
                    propios.add(b.sacar());
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
                    + " quedó esperando para siempre (estado " + h.getState() + ")");
        }
        int total = 4 * porProductor;
        iguales(total, consumidos.size(), "ronda " + ronda + ": elementos consumidos");
        Set<Integer> distintos = new HashSet<>(consumidos);
        verificar(distintos.size() == total && !distintos.contains(null), "ronda " + ronda + ": hay elementos"
                + " repetidos, perdidos o null: con if, el despertado encontró la condición falsa. ¿senalar() cede"
                + " el monitor y espera en cortesía?");
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
