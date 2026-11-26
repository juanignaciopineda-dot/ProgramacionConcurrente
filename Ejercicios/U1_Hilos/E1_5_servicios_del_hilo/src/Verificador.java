import java.util.ArrayList;
import java.util.List;

/** Verificador automático del ejercicio 1.5. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 1.5 - Daemon, excepciones no controladas y ThreadLocal");

        caso("crearVigilante: hilo 'vigilante', daemon y sin arrancar", 2000, () -> {
            Thread v = Servicios.crearVigilante(() -> {});
            iguales("vigilante", v.getName(), "nombre");
            verificar(v.isDaemon(), "el hilo no es daemon");
            iguales(Thread.State.NEW, v.getState(), "estado");
        });

        caso("crearVigilante: el vigilante ejecuta la tarea que recibe", 2000, () -> {
            boolean[] corrio = new boolean[1];
            Thread v = Servicios.crearVigilante(() -> corrio[0] = true);
            v.start();
            v.join();
            verificar(corrio[0], "la tarea no se ejecutó");
        });

        caso("crearSupervisado: nombre, manejador propio y sin arrancar", 2000, () -> {
            RegistroDeFallas registro = new RegistroDeFallas();
            Thread t = Servicios.crearSupervisado(() -> {}, "supervisado", registro);
            iguales("supervisado", t.getName(), "nombre");
            verificar(t.getUncaughtExceptionHandler() == registro, "el hilo no tiene asignado el manejador recibido");
            iguales(Thread.State.NEW, t.getState(), "estado");
        });

        caso("RegistroDeFallas.uncaughtException: formato de la línea (llamándolo directo)", 2000, () -> {
            RegistroDeFallas registro = new RegistroDeFallas();
            registro.uncaughtException(new Thread(() -> {}, "importador-2"), new IllegalStateException("archivo vacío"));
            iguales(List.of("importador-2: IllegalStateException: archivo vacío"), registro.getFallas(), "fallas registradas");
        });

        caso("RegistroDeFallas: anota 'hilo: Excepcion: mensaje' cuando un hilo muere", 2000, () -> {
            RegistroDeFallas registro = new RegistroDeFallas();
            Thread t = Servicios.crearSupervisado(() -> {
                throw new IllegalStateException("sin stock");
            }, "reponedor", registro);
            t.start();
            t.join();
            iguales(List.of("reponedor: IllegalStateException: sin stock"), registro.getFallas(), "fallas registradas");
        });

        caso("RegistroDeFallas: un hilo que termina bien no deja registro", 2000, () -> {
            RegistroDeFallas registro = new RegistroDeFallas();
            Thread t = Servicios.crearSupervisado(() -> {}, "prolijo", registro);
            t.start();
            t.join();
            iguales(List.of(), registro.getFallas(), "fallas registradas");
        });

        caso("RegistroDeFallas: 5 hilos que mueren a la vez quedan todos registrados", 3000, () -> {
            RegistroDeFallas registro = new RegistroDeFallas();
            List<Thread> hilos = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                int n = i;
                hilos.add(Servicios.crearSupervisado(() -> {
                    throw new ArithmeticException("división por cero en " + n);
                }, "calculador-" + i, registro));
            }
            for (Thread h : hilos) {
                h.start();
            }
            for (Thread h : hilos) {
                h.join();
            }
            List<String> fallas = new ArrayList<>(registro.getFallas());
            iguales(5, fallas.size(), "cantidad de fallas");
            for (int i = 1; i <= 5; i++) {
                verificar(fallas.contains("calculador-" + i + ": ArithmeticException: división por cero en " + i),
                        "falta la falla de calculador-" + i + " en " + fallas);
            }
        });

        caso("NumeradorPorHilo: 1, 2, 3 y después de reiniciar() vuelve a 1", 2000, () -> {
            NumeradorPorHilo num = new NumeradorPorHilo();
            iguales(1, num.siguiente(), "primera llamada");
            iguales(2, num.siguiente(), "segunda llamada");
            iguales(3, num.siguiente(), "tercera llamada");
            num.reiniciar();
            iguales(1, num.siguiente(), "después de reiniciar()");
        });

        caso("NumeradorPorHilo: 4 hilos x 10000 llamadas, cada uno ve su secuencia 1..10000", 5000, () -> {
            NumeradorPorHilo num = new NumeradorPorHilo();
            int llamadas = 10_000;
            boolean[] bien = new boolean[4];
            List<Thread> hilos = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                int id = i;
                hilos.add(new Thread(() -> {
                    boolean ok = true;
                    for (int esperado = 1; esperado <= llamadas; esperado++) {
                        ok &= num.siguiente() == esperado;
                    }
                    bien[id] = ok;
                }, "numerado-" + i));
            }
            for (Thread h : hilos) {
                h.start();
            }
            for (Thread h : hilos) {
                h.join();
            }
            for (int i = 0; i < 4; i++) {
                verificar(bien[i], "el hilo numerado-" + i + " no recibió la secuencia 1.." + llamadas
                        + ": las secuencias de los hilos se están mezclando");
            }
        });

        caso("NumeradorPorHilo: reiniciar() en un hilo no afecta a los otros", 2000, () -> {
            NumeradorPorHilo num = new NumeradorPorHilo();
            num.siguiente();
            num.siguiente();
            Thread otro = new Thread(() -> {
                num.siguiente();
                num.reiniciar();
            }, "otro");
            otro.start();
            otro.join();
            iguales(3, num.siguiente(), "secuencia de este hilo");
        });

        prohibir(new String[] {"NumeradorPorHilo.java"}, "synchronized", "java.util.concurrent");

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
