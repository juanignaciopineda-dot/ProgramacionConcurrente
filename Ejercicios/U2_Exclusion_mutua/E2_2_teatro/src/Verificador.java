import java.util.ArrayList;
import java.util.List;

/** Verificador automático del ejercicio 2.2. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 2.2 - Llaves independientes y el bug del Integer");

        caso("Ventas en un solo hilo: vende si hay lugar y rechaza si no", 2000, () -> {
            Teatro t = new Teatro(0);
            verificar(t.venderPlatea(30), "no vendió 30 de platea con el teatro vacío");
            iguales(70, t.getLibresPlatea(), "libres en platea");
            verificar(!t.venderPlatea(80), "vendió 80 de platea con solo 70 libres");
            iguales(70, t.getLibresPlatea(), "libres en platea después de una venta rechazada");
            verificar(t.venderPalco(20), "no vendió el palco completo");
            verificar(!t.venderPalco(1), "vendió en un palco lleno");
            iguales(0, t.getLibresPalco(), "libres en palco");
        });

        caso("Cantidades menores que 1 lanzan IllegalArgumentException", 2000, () -> {
            Teatro t = new Teatro(0);
            esperaExcepcion(IllegalArgumentException.class, () -> t.venderPlatea(0), "venderPlatea(0)");
            esperaExcepcion(IllegalArgumentException.class, () -> t.venderPalco(-2), "venderPalco(-2)");
            esperaExcepcion(IllegalArgumentException.class, () -> t.pasarAPalco(0), "pasarAPalco(0)");
        });

        caso("Sin sobreventa: 10 hilos piden 15 de platea a la vez, se venden exactamente 6", 5000, () -> {
            Teatro t = new Teatro(5);
            int vendidas = contarExitos(10, () -> t.venderPlatea(15));
            iguales(6, vendidas, "ventas exitosas");
            iguales(10, t.getLibresPlatea(), "libres en platea");
        });

        caso("Sin sobreventa en palco: 8 hilos piden 3 a la vez, se venden exactamente 6", 5000, () -> {
            Teatro t = new Teatro(5);
            int vendidas = contarExitos(8, () -> t.venderPalco(3));
            iguales(6, vendidas, "ventas exitosas");
            iguales(2, t.getLibresPalco(), "libres en palco");
        });

        caso("Exclusión dentro de un sector: 4 cobros de 100 ms en platea van de a uno (>= 380 ms)", 5000, () -> {
            Teatro t = new Teatro(100);
            long ms = medir(() -> contarExitos(4, () -> t.venderPlatea(1)));
            info("4 ventas de platea tardaron " + ms + " ms");
            verificar(ms >= 380, "tardó " + ms + " ms: los cuatro cobraron a la vez."
                    + " El cobro tiene que estar adentro de la sección crítica");
        });

        caso("Sectores independientes: 4 ventas de platea + 4 de palco en paralelo (< 650 ms)", 5000, () -> {
            Teatro t = new Teatro(100);
            long ms = medir(() -> {
                List<Thread> hilos = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    hilos.add(new Thread(() -> intentar(() -> t.venderPlatea(1)), "platea-" + i));
                    hilos.add(new Thread(() -> intentar(() -> t.venderPalco(1)), "palco-" + i));
                }
                for (Thread h : hilos) {
                    h.start();
                }
                for (Thread h : hilos) {
                    h.join();
                }
                return 0;
            });
            info("4 + 4 ventas tardaron " + ms + " ms (con una sola llave serían unos 800)");
            verificar(ms < 650, "tardó " + ms + " ms: platea y palco se están esperando entre sí."
                    + " ¿Usás la misma llave (o synchronized en los métodos) para los dos sectores?");
            iguales(96, t.getLibresPlatea(), "libres en platea");
            iguales(16, t.getLibresPalco(), "libres en palco");
        });

        caso("pasarAPalco: casos en un solo hilo", 2000, () -> {
            Teatro t = new Teatro(0);
            verificar(!t.pasarAPalco(5), "pasó 5 al palco sin que hubiera nadie en platea");
            t.venderPlatea(10);
            verificar(t.pasarAPalco(5), "no pasó 5 al palco habiendo 10 en platea");
            iguales(95, t.getLibresPlatea(), "libres en platea");
            iguales(15, t.getLibresPalco(), "libres en palco");
            verificar(!t.pasarAPalco(6), "pasó 6 al palco habiendo solo 5 en platea");
            t.venderPalco(15);
            verificar(!t.pasarAPalco(1), "pasó al palco con el palco lleno");
        });

        caso("pasarAPalco mezclado con ventas: sin deadlock y las cuentas cierran", 10000, () -> {
            Teatro t = new Teatro(1);
            int[] ventasPlatea = new int[1];
            int[] ventasPalco = new int[1];
            int[] pases = new int[1];
            List<Thread> hilos = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                hilos.add(new Thread(() -> repetir(20, () -> t.venderPlatea(2), ventasPlatea), "vende-platea-" + i));
            }
            for (int i = 0; i < 2; i++) {
                hilos.add(new Thread(() -> repetir(5, () -> t.venderPalco(1), ventasPalco), "vende-palco-" + i));
            }
            for (int i = 0; i < 3; i++) {
                hilos.add(new Thread(() -> repetir(50, () -> {
                    Thread.sleep(1);
                    return t.pasarAPalco(1);
                }, pases), "pasa-" + i));
            }
            for (Thread h : hilos) {
                h.start();
            }
            for (Thread h : hilos) {
                h.join();
            }
            int platea = t.getLibresPlatea();
            int palco = t.getLibresPalco();
            info("ventas de platea: " + ventasPlatea[0] + ", de palco: " + ventasPalco[0] + ", pases: " + pases[0]);
            iguales(Teatro.CAPACIDAD_PLATEA - 2 * ventasPlatea[0] + pases[0], platea, "libres en platea");
            iguales(Teatro.CAPACIDAD_PALCO - ventasPalco[0] - pases[0], palco, "libres en palco");
            verificar(platea >= 0 && platea <= Teatro.CAPACIDAD_PLATEA, "platea fuera de rango: " + platea);
            verificar(palco >= 0 && palco <= Teatro.CAPACIDAD_PALCO, "palco fuera de rango: " + palco);
        });

        caso("ContadorDeFunciones: 8 hilos x 50000 funciones dan 400000 (3 rondas)", 30000, () -> {
            for (int ronda = 1; ronda <= 3; ronda++) {
                ContadorDeFunciones c = new ContadorDeFunciones();
                List<Thread> hilos = new ArrayList<>();
                for (int i = 0; i < 8; i++) {
                    hilos.add(new Thread(() -> {
                        for (int k = 0; k < 50_000; k++) {
                            c.sumarFuncion();
                        }
                    }, "funciones-" + i));
                }
                for (Thread h : hilos) {
                    h.start();
                }
                for (Thread h : hilos) {
                    h.join();
                }
                int contadas = c.getFunciones();
                verificar(contadas == 400_000, "ronda " + ronda + ": contó " + contadas + " funciones de 400000."
                        + " El bug sigue ahí: ¿sobre qué objeto se sincroniza?");
            }
        });

        prohibir(new String[] {"Teatro.java", "ContadorDeFunciones.java"}, "java.util.concurrent", "Atomic",
                "synchronized (funciones)", "synchronized(funciones)");

        fin();
    }

    /** Acción que devuelve si tuvo éxito. */
    private interface Intento {
        boolean intentar() throws Exception;
    }

    private interface Medible {
        int correr() throws Exception;
    }

    private static boolean intentar(Intento intento) {
        try {
            return intento.intentar();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void repetir(int veces, Intento intento, int[] exitos) {
        int propios = 0;
        for (int i = 0; i < veces; i++) {
            if (intentar(intento)) {
                propios++;
            }
        }
        synchronized (exitos) {
            exitos[0] += propios;
        }
    }

    private static int contarExitos(int hilos, Intento intento) throws InterruptedException {
        int[] exitos = new int[1];
        List<Thread> lista = new ArrayList<>();
        for (int i = 0; i < hilos; i++) {
            lista.add(new Thread(() -> repetir(1, intento, exitos), "cliente-" + i));
        }
        for (Thread h : lista) {
            h.start();
        }
        for (Thread h : lista) {
            h.join();
        }
        return exitos[0];
    }

    private static long medir(Medible m) throws Exception {
        long inicio = System.nanoTime();
        m.correr();
        return (System.nanoTime() - inicio) / 1_000_000;
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
