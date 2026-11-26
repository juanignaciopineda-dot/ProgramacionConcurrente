import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/** Verificador automático del ejercicio 6.4. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 6.4 - Máquinas de Moore y Mealy");

        caso("Mealy: un símbolo de salida por cada símbolo de entrada", 2000, () -> {
            MaquinaMealy complemento = new MaquinaMealy("q");
            complemento.agregarTransicion("q", '0', "q", '1');
            complemento.agregarTransicion("q", '1', "q", '0');
            iguales("1001", complemento.traducir("0110"), "complemento de 0110");
            iguales("", complemento.traducir(""), "traducir λ (no lee nada, no emite nada)");
            esperaExcepcion(IllegalArgumentException.class, () -> complemento.traducir("01x"), "entrada sin arco");
            MaquinaMealy cambio = mealyDeCambio();
            iguales("01110", cambio.traducir("01011"), "\"cambió el bit\": la salida depende de dónde se viene");
        });

        caso("Moore: a cada estado una salida y sólo una", 2000, () -> {
            MaquinaMoore m = new MaquinaMoore("par");
            m.definirSalida("par", '0');
            m.definirSalida("par", '0');
            esperaExcepcion(IllegalArgumentException.class, () -> m.definirSalida("par", '1'),
                    "darle otra salida a un estado que ya tenía una");
            m.definirSalida("impar", '1');
            iguales("[impar, par]", m.estados().toString(), "estados (definirSalida agrega el estado)");
        });

        caso("Moore: n símbolos de entrada dan n + 1 de salida", 2000, () -> {
            MaquinaMoore paridad = new MaquinaMoore("par");
            paridad.definirSalida("par", '0');
            paridad.definirSalida("impar", '1');
            paridad.agregarTransicion("par", '0', "par");
            paridad.agregarTransicion("par", '1', "impar");
            paridad.agregarTransicion("impar", '0', "impar");
            paridad.agregarTransicion("impar", '1', "par");
            iguales("01001", paridad.traducir("1101"), "paridad de unos de 1101 (la primera es la del inicial)");
            iguales("0", paridad.traducir(""), "traducir λ: la salida del estado inicial");
            esperaExcepcion(IllegalArgumentException.class, () -> paridad.traducir("12"), "entrada sin arco");
            MaquinaMoore incompleta = new MaquinaMoore("a");
            incompleta.definirSalida("a", '0');
            incompleta.agregarTransicion("a", '1', "b");
            esperaExcepcion(IllegalStateException.class, () -> incompleta.traducir("1"), "llegar a un estado sin salida");
        });

        caso("Detector de 101 con Mealy: con solapamiento y en 3 estados", 3000, () -> {
            MaquinaMealy m = Detectores.mealy101();
            iguales("00101", m.traducir("10101"), "en 10101 aparece dos veces (la segunda usa el último 1 de la primera)");
            for (String w : todas("01", 10)) {
                iguales(detectar101(w), m.traducir(w), "salida de Mealy para " + (w.isEmpty() ? "λ" : w));
            }
            verificar(m.estados().size() <= 3, "alcanza con 3 estados y el tuyo tiene " + m.estados().size() + ": " + m.estados());
            info("Mealy: " + m.estados().size() + " estados " + m.estados());
        });

        caso("Detector de 101 con Moore: la misma salida, un paso después, y en 4 estados", 3000, () -> {
            MaquinaMoore m = Detectores.moore101();
            iguales("000101", m.traducir("10101"), "salida de Moore para 10101");
            for (String w : todas("01", 10)) {
                iguales("0" + detectar101(w), m.traducir(w), "salida de Moore para " + (w.isEmpty() ? "λ" : w));
            }
            verificar(m.estados().size() <= 4, "alcanza con 4 estados y el tuyo tiene " + m.estados().size() + ": " + m.estados());
            info("Moore: " + m.estados().size() + " estados " + m.estados());
        });

        caso("De Mealy a Moore: un estado por cada par (estado, última salida)", 2000, () -> {
            MaquinaMoore m = Conversion.mealyAMoore(mealy101DeReferencia(), '0');
            iguales("[a/0, b/0, b/1, c/0]", m.estados().toString(),
                    "estados de Moore (a b se llega emitiendo 0 y emitiendo 1: se parte en dos)");
            iguales("a/0", m.inicial(), "estado inicial");
            for (String w : todas("01", 8)) {
                iguales("0" + detectar101(w), m.traducir(w), "salida de la máquina convertida para " + (w.isEmpty() ? "λ" : w));
            }
            MaquinaMoore deCambio = Conversion.mealyAMoore(mealyDeCambio(), '0');
            for (String w : todas("01", 8)) {
                iguales("0" + mealyDeCambio().traducir(w), deCambio.traducir(w), "\"cambió el bit\" convertida, con " + w);
            }
            info("\"cambió el bit\": Mealy " + mealyDeCambio().estados().size() + " estados, Moore " + deCambio.estados().size());
        });

        caso("De Mealy a Moore con tu detector", 2000, () -> {
            MaquinaMealy mealy = Detectores.mealy101();
            MaquinaMoore moore = Conversion.mealyAMoore(mealy, '0');
            for (String w : todas("01", 8)) {
                iguales("0" + mealy.traducir(w), moore.traducir(w), "salida para " + (w.isEmpty() ? "λ" : w));
            }
            info("tu Mealy tiene " + mealy.estados().size() + " estados y la Moore convertida " + moore.estados().size());
        });

        caso("Reactivo: espera bloqueado en la cola, sin preguntar en un bucle", 3000, () -> {
            BlockingQueue<Character> eventos = new LinkedBlockingQueue<>();
            BlockingQueue<Character> salidas = new LinkedBlockingQueue<>();
            Thread detector = new Thread(new DetectorReactivo(mealy101DeReferencia(), eventos, salidas), "detector");
            detector.start();
            verificar(esperarEstado(detector, Thread.State.WAITING, 1000), "sin eventos, el detector tendría que estar"
                    + " bloqueado en take() (WAITING) y está " + detector.getState());
            eventos.put(DetectorReactivo.FIN);
            detector.join(1000);
            verificar(!detector.isAlive(), "con FIN, el detector tendría que terminar");
        });

        caso("Reactivo: reacciona a cada evento apenas llega", 3000, () -> {
            BlockingQueue<Character> eventos = new LinkedBlockingQueue<>();
            BlockingQueue<Character> salidas = new LinkedBlockingQueue<>();
            Thread detector = new Thread(new DetectorReactivo(mealy101DeReferencia(), eventos, salidas), "detector");
            detector.start();
            String entrada = "10101";
            String esperado = detectar101(entrada);
            for (int i = 0; i < entrada.length(); i++) {
                eventos.put(entrada.charAt(i));
                Character salida = salidas.poll(1, TimeUnit.SECONDS);
                verificar(salida != null, "mandé el evento " + (i + 1) + " ('" + entrada.charAt(i)
                        + "') y no llegó ninguna salida en un segundo: el detector tiene que reaccionar a cada evento,"
                        + " no esperar al final");
                iguales(esperado.charAt(i), salida, "salida del evento " + (i + 1));
            }
            eventos.put('x');
            eventos.put(DetectorReactivo.FIN);
            detector.join(1000);
            verificar(!detector.isAlive(), "con FIN, el detector tendría que terminar");
            verificar(salidas.isEmpty(), "un evento sin arco ('x') no emite nada, y quedó " + salidas);
        });

        caso("Reactivo: una ráfaga de 5000 eventos da lo mismo que traducir", 5000, () -> {
            BlockingQueue<Character> eventos = new LinkedBlockingQueue<>();
            BlockingQueue<Character> salidas = new LinkedBlockingQueue<>();
            MaquinaMealy maquina = mealy101DeReferencia();
            Thread detector = new Thread(new DetectorReactivo(maquina, eventos, salidas), "detector");
            detector.start();
            StringBuilder entrada = new StringBuilder();
            Random azar = new Random(101);
            for (int i = 0; i < 5000; i++) {
                char c = azar.nextBoolean() ? '1' : '0';
                entrada.append(c);
                eventos.put(c);
            }
            eventos.put(DetectorReactivo.FIN);
            detector.join(3000);
            verificar(!detector.isAlive(), "con FIN, el detector tendría que terminar");
            List<Character> recibidas = new ArrayList<>();
            salidas.drainTo(recibidas);
            StringBuilder texto = new StringBuilder();
            recibidas.forEach(texto::append);
            iguales(maquina.traducir(entrada.toString()), texto.toString(), "salidas de la ráfaga");
        });

        caso("Reactivo: si lo interrumpen mientras espera, termina", 3000, () -> {
            BlockingQueue<Character> eventos = new LinkedBlockingQueue<>();
            Thread detector = new Thread(new DetectorReactivo(mealy101DeReferencia(), eventos, new LinkedBlockingQueue<>()),
                    "detector");
            detector.start();
            esperarEstado(detector, Thread.State.WAITING, 1000);
            detector.interrupt();
            detector.join(1000);
            verificar(!detector.isAlive(), "interrumpido mientras espera en take(), el detector tendría que terminar");
        });

        prohibir(new String[] {"DetectorReactivo.java"}, "sleep", "synchronized", ".poll(", ".peek(");

        fin();
    }

    /** Mealy que emite 1 cuando el bit leído es distinto del anterior (arranca como si el anterior fuera 0). */
    private static MaquinaMealy mealyDeCambio() {
        MaquinaMealy m = new MaquinaMealy("ultimo0");
        m.agregarTransicion("ultimo0", '0', "ultimo0", '0');
        m.agregarTransicion("ultimo0", '1', "ultimo1", '1');
        m.agregarTransicion("ultimo1", '1', "ultimo1", '0');
        m.agregarTransicion("ultimo1", '0', "ultimo0", '1');
        return m;
    }

    /** Un detector de 101 correcto, para no depender del tuyo: a = nada, b = visto 1, c = visto 10. */
    private static MaquinaMealy mealy101DeReferencia() {
        MaquinaMealy m = new MaquinaMealy("a");
        m.agregarTransicion("a", '0', "a", '0');
        m.agregarTransicion("a", '1', "b", '0');
        m.agregarTransicion("b", '0', "c", '0');
        m.agregarTransicion("b", '1', "b", '0');
        m.agregarTransicion("c", '0', "a", '0');
        m.agregarTransicion("c", '1', "b", '1');
        return m;
    }

    /** Lo que tiene que emitir el detector: 1 en cada posición donde termina un 101. */
    private static String detectar101(String w) {
        StringBuilder salida = new StringBuilder();
        for (int i = 0; i < w.length(); i++) {
            salida.append(i >= 2 && w.startsWith("101", i - 2) ? '1' : '0');
        }
        return salida.toString();
    }

    /** Todas las cadenas sobre el alfabeto de largo 0 a largoMax. */
    private static List<String> todas(String alfabeto, int largoMax) {
        List<String> resultado = new ArrayList<>();
        List<String> tanda = List.of("");
        for (int largo = 0; largo <= largoMax; largo++) {
            resultado.addAll(tanda);
            List<String> siguiente = new ArrayList<>();
            for (String s : tanda) {
                for (char c : alfabeto.toCharArray()) {
                    siguiente.add(s + c);
                }
            }
            tanda = siguiente;
        }
        return resultado;
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
