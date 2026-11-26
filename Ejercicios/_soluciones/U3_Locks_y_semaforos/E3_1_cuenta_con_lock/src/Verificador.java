import java.util.ArrayList;
import java.util.List;

/** Verificador automático del ejercicio 3.1. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 3.1 - Cuenta con ReentrantLock");

        caso("Constructor: valida y crea el candado justo o no justo", 2000, () -> {
            esperaExcepcion(IllegalArgumentException.class, () -> new Cuenta(-1, 0, false), "saldo inicial negativo");
            esperaExcepcion(IllegalArgumentException.class, () -> new Cuenta(0, -1, false), "demora negativa");
            iguales(500L, new Cuenta(500).getSaldo(), "saldo inicial");
            verificar(new Cuenta(0, 0, true).esJusta(),
                    "con justa = true el candado tendría que ser justo: ¿le pasaste el boolean al ReentrantLock?");
            verificar(!new Cuenta(0, 0, false).esJusta(), "con justa = false el candado no tendría que ser justo");
        });

        caso("Un solo hilo: depositar, extraer y validaciones", 2000, () -> {
            Cuenta c = new Cuenta(100);
            c.depositar(50);
            iguales(150L, c.getSaldo(), "saldo después de depositar 50");
            c.extraer(30);
            iguales(120L, c.getSaldo(), "saldo después de extraer 30");
            esperaExcepcion(IllegalArgumentException.class, () -> c.depositar(0), "depositar(0)");
            esperaExcepcion(IllegalArgumentException.class, () -> c.extraer(-5), "extraer(-5)");
            esperaExcepcion(SaldoInsuficienteException.class, () -> c.extraer(1000), "extraer más de lo que hay");
            iguales(120L, c.getSaldo(), "saldo después de una extracción rechazada");
            verificar(c.getRegistro().size() >= 2,
                    "el registro tiene " + c.getRegistro().size() + " operaciones: depositar y extraer tienen que"
                            + " llamar a procesar() adentro de la sección crítica");
        });

        caso("Después de una SaldoInsuficienteException el candado queda libre", 3000, () -> {
            Cuenta c = new Cuenta(10);
            try {
                c.extraer(100);
            } catch (SaldoInsuficienteException esperada) {
                // es lo que tiene que pasar
            }
            verificar(!c.estaTomada(),
                    "el candado quedó tomado después de la excepción: ¿el unlock() está en un finally?");
            Thread otro = new Thread(() -> c.depositar(5), "otro");
            otro.start();
            otro.join(1000);
            verificar(!otro.isAlive(), "otro hilo no pudo depositar: el candado quedó tomado");
            iguales(15L, c.getSaldo(), "saldo final");
        });

        caso("Estrés: 8 hilos depositan y 8 extraen a la vez (3 rondas)", 30000, () -> {
            for (int ronda = 1; ronda <= 3; ronda++) {
                Cuenta c = new Cuenta(1_000_000);
                List<Thread> hilos = new ArrayList<>();
                for (int i = 0; i < 8; i++) {
                    hilos.add(new Thread(() -> {
                        for (int k = 0; k < 10_000; k++) {
                            c.depositar(3);
                        }
                    }, "depositante-" + i));
                    hilos.add(new Thread(() -> {
                        for (int k = 0; k < 10_000; k++) {
                            try {
                                c.extraer(2);
                            } catch (SaldoInsuficienteException e) {
                                throw new IllegalStateException(e);
                            }
                        }
                    }, "extractor-" + i));
                }
                for (Thread h : hilos) {
                    h.start();
                }
                for (Thread h : hilos) {
                    h.join(8000);
                    verificar(!h.isAlive(), "ronda " + ronda + ": el hilo " + h.getName() + " no terminó (estado "
                            + h.getState() + "): ¿quedó algún candado sin liberar?");
                }
                iguales(1_000_000L + 8L * 10_000 * (3 - 2), c.getSaldo(), "ronda " + ronda + ": saldo final");
                verificar(!c.estaTomada(), "ronda " + ronda + ": el candado quedó tomado al final");
            }
        });

        caso("intentarExtraer se rinde si el candado está ocupado", 5000, () -> {
            Cuenta c = new Cuenta(100, 400, false);
            Thread ocupante = new Thread(() -> c.depositar(1), "ocupante");
            ocupante.start();
            long limite = System.currentTimeMillis() + 1000;
            while (!c.estaTomada() && System.currentTimeMillis() < limite) {
                Thread.sleep(2);
            }
            verificar(c.estaTomada(), "el depósito del otro hilo no tomó el candado");
            long t0 = System.nanoTime();
            boolean pudo;
            try {
                pudo = c.intentarExtraer(10, 100);
            } catch (IllegalMonitorStateException e) {
                throw new AssertionError("intentarExtraer lanzó IllegalMonitorStateException: ¿hiciste unlock()"
                        + " aunque tryLock devolvió false? El try va después de saber que tenés el candado");
            }
            long ms = (System.nanoTime() - t0) / 1_000_000;
            verificar(!pudo, "intentarExtraer devolvió true con el candado ocupado por otro hilo"
                    + " (tardó " + ms + " ms: ¿usaste lock() en vez de tryLock(tiempo, unidad)?)");
            verificar(ms >= 90, "intentarExtraer se rindió en " + ms + " ms y tendría que esperar hasta 100:"
                    + " ¿usaste tryLock() sin tiempo?");
            verificar(ms < 350, "intentarExtraer tardó " + ms + " ms: tendría que rendirse a los 100 ms");
            ocupante.join(2000);
            iguales(101L, c.getSaldo(), "saldo (el intento fallido no tiene que tocarlo)");
            verificar(!c.estaTomada(), "el candado quedó tomado al final");
        });

        caso("intentarExtraer con el candado libre: extrae, o lanza SaldoInsuficienteException", 3000, () -> {
            Cuenta c = new Cuenta(100);
            verificar(c.intentarExtraer(30, 100), "con el candado libre intentarExtraer tendría que extraer");
            iguales(70L, c.getSaldo(), "saldo después de intentarExtraer(30)");
            verificar(!c.estaTomada(), "el candado quedó tomado: ¿hay un unlock() por cada lock() o tryLock()?");
            esperaExcepcion(SaldoInsuficienteException.class, () -> c.intentarExtraer(500, 100),
                    "intentarExtraer de más de lo que hay");
            verificar(!c.estaTomada(), "el candado quedó tomado después de la SaldoInsuficienteException");
            esperaExcepcion(IllegalArgumentException.class, () -> c.intentarExtraer(0, 100), "intentarExtraer(0)");
            iguales(70L, c.getSaldo(), "saldo final");
        });

        caso("extraerTodo es atómico: nadie se mete entre getSaldo() y extraer()", 10000, () -> {
            // Candado justo y dos vaciadores en fila detrás de un depósito lento: si extraerTodo suelta el
            // candado entre getSaldo() y extraer(), el modo justo le da el turno al otro vaciador, que lee
            // el mismo saldo y después no lo puede extraer.
            for (int intento = 1; intento <= 3; intento++) {
                Cuenta c = new Cuenta(1000, 100, true);
                Thread ocupante = new Thread(() -> c.depositar(1), "ocupante");
                ocupante.start();
                long limite = System.currentTimeMillis() + 1000;
                while (!c.estaTomada() && System.currentTimeMillis() < limite) {
                    Thread.sleep(2);
                }
                verificar(c.estaTomada(), "el depósito del otro hilo no tomó el candado");
                long[] sacado = new long[2];
                RuntimeException[] error = new RuntimeException[2];
                List<Thread> vaciadores = new ArrayList<>();
                for (int i = 0; i < 2; i++) {
                    int n = i;
                    Thread v = new Thread(() -> {
                        try {
                            sacado[n] = c.extraerTodo();
                        } catch (UnsupportedOperationException e) {
                            throw e;
                        } catch (RuntimeException e) {
                            error[n] = e;
                        }
                    }, "vaciador-" + i);
                    vaciadores.add(v);
                    v.start();
                    esperarEstado(v, Thread.State.WAITING, 1000);
                }
                ocupante.join(2000);
                for (Thread v : vaciadores) {
                    v.join(3000);
                    verificar(!v.isAlive(), "intento " + intento + ": el " + v.getName() + " no terminó (estado "
                            + v.getState() + ")");
                }
                for (RuntimeException e : error) {
                    verificar(e == null, "intento " + intento + ": extraerTodo lanzó " + e + ". Entre getSaldo() y"
                            + " extraer() se metió el otro hilo y se llevó la plata: ¿tomaste el candado alrededor"
                            + " de las dos llamadas?");
                }
                iguales(1001L, sacado[0] + sacado[1], "intento " + intento + ": lo que sacaron entre los dos");
                verificar(sacado[0] == 0 || sacado[1] == 0, "intento " + intento + ": los dos sacaron algo ("
                        + sacado[0] + " y " + sacado[1] + ")");
                iguales(0L, c.getSaldo(), "intento " + intento + ": saldo final");
            }
        });

        caso("extraerTodo bajo carga: 4 hilos depositan y 4 vacían la cuenta a la vez", 30000, () -> {
            Cuenta c = new Cuenta(0);
            long[] extraido = new long[4];
            List<Thread> hilos = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                hilos.add(new Thread(() -> {
                    for (int k = 0; k < 20_000; k++) {
                        c.depositar(1);
                    }
                }, "depositante-" + i));
                int n = i;
                hilos.add(new Thread(() -> {
                    for (int k = 0; k < 20_000; k++) {
                        extraido[n] += c.extraerTodo();
                    }
                }, "vaciador-" + i));
            }
            for (Thread h : hilos) {
                h.start();
            }
            for (Thread h : hilos) {
                h.join(10_000);
                verificar(!h.isAlive(), "el hilo " + h.getName() + " no terminó (estado " + h.getState() + ")");
            }
            long total = c.getSaldo();
            for (long e : extraido) {
                total += e;
            }
            iguales(80_000L, total, "lo que se extrajo más lo que quedó (si algún vaciador terminó con una"
                    + " excepción, entre getSaldo() y extraer() se metió otro hilo)");
            verificar(!c.estaTomada(), "el candado quedó tomado al final");
        });

        caso("Experimento: candado justo vs. no justo (informativo)", 10000, () -> {
            for (boolean justa : new boolean[] {true, false}) {
                Cuenta c = new Cuenta(0, 40, justa);
                List<Thread> hilos = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    Thread h = new Thread(() -> {
                        c.depositar(1);
                        c.depositar(1);
                    }, String.valueOf(i));
                    hilos.add(h);
                    h.start();
                    Thread.sleep(10);
                }
                for (Thread h : hilos) {
                    h.join(5000);
                }
                info((justa ? "candado justo:    " : "candado no justo: ") + String.join(" ", c.getRegistro())
                        + "   (cada número es un hilo que depositó)");
            }
        });

        prohibir(new String[] {"Cuenta.java"}, "synchronized", "Semaphore", "Atomic");

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
