import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/** Verificador automático del ejercicio 6.7. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 6.7 - Expresiones regulares en Java");

        caso("Actividad a): empiezan y terminan con la misma letra", 2000, () -> {
            revisar(ExpresionesRegulares.EMPIEZA_Y_TERMINA_IGUAL, "EMPIEZA_Y_TERMINA_IGUAL", "ab", 10,
                    s -> !s.isEmpty() && s.charAt(0) == s.charAt(s.length() - 1));
        });

        caso("Actividad b): al menos un 1", 2000, () -> {
            revisar(ExpresionesRegulares.AL_MENOS_UN_1, "AL_MENOS_UN_1", "01", 10, s -> s.contains("1"));
        });

        caso("Actividad c): cantidad par de b", 2000, () -> {
            revisar(ExpresionesRegulares.PAR_DE_B, "PAR_DE_B", "ab", 10, s -> s.chars().filter(c -> c == 'b').count() % 2 == 0);
        });

        caso("Ejercicio 1.b: la expresión regular de L3", 3000, () -> {
            revisar(ExpresionesRegulares.L3, "L3", "abc", 8, Verificador::enL3);
        });

        caso("Primera diferencia: (a+b)* no es a*b* (actividad d)", 2000, () -> {
            iguales("ba", ExpresionesRegulares.primeraDiferencia("(a|b)*", "a*b*", "ab", 6),
                    "primera cadena que separa (a|b)* de a*b*");
            iguales("", ExpresionesRegulares.primeraDiferencia("a*", "a+", "ab", 4),
                    "primera cadena que separa a* de a+ (es λ, que se prueba primero)");
            iguales("aa", ExpresionesRegulares.primeraDiferencia("a?b?", "a*b*", "ab", 6),
                    "primera cadena que separa a?b? de a*b* (las del mismo largo van en el orden del alfabeto)");
            iguales(null, ExpresionesRegulares.primeraDiferencia("(a|b)*", "(a*b*)*", "ab", 6),
                    "(a+b)* = (a*b*)*, una de las propiedades del cierre: no hay diferencia");
            iguales(null, ExpresionesRegulares.primeraDiferencia("ab", "ab", "ab", 0), "con largoMax = 0 solo se prueba λ");
        });

        caso("Primera diferencia: la pertenencia es con matches(), no con find()", 2000, () -> {
            iguales("a", ExpresionesRegulares.primeraDiferencia("a*", "b*", "ab", 3),
                    "a* contra b*: con find() las dos \"encuentran\" λ en cualquier cadena y parecen iguales");
        });

        caso("De JFLAP a Java: + es la unión y ! es λ", 2000, () -> {
            iguales("a|b", ExpresionesRegulares.deJflap("a+b"), "a+b");
            mismoLenguaje(ExpresionesRegulares.deJflap("a+b*"), "a|b*", "ab", 6, "a+b* de JFLAP: a o cualquier cantidad de b");
            mismoLenguaje(ExpresionesRegulares.deJflap("(!+a)b"), "a?b", "ab", 6, "(!+a)b de JFLAP");
            mismoLenguaje(ExpresionesRegulares.deJflap("!"), "", "ab", 4, "! de JFLAP: solo λ");
            mismoLenguaje(ExpresionesRegulares.deJflap("(a!)*"), "a*", "ab", 6, "(a!)* de JFLAP");
            mismoLenguaje(ExpresionesRegulares.deJflap("a!*"), "a", "ab", 6, "a!* de JFLAP (λ* = λ)");
        });

        caso("De JFLAP a Java: los ejercicios 4 de los apuntes", 2000, () -> {
            mismoLenguaje(ExpresionesRegulares.deJflap("q+a+b*+cd"), "q|a|b*|cd", "abcdq", 4, "4.a) q + a + b* + cd");
            mismoLenguaje(ExpresionesRegulares.deJflap("(a+b)*abb"), "[ab]*abb", "ab", 8, "4.b) (a + b)*abb");
            mismoLenguaje(ExpresionesRegulares.deJflap("a(b*b+a*b)a*"), "a(b*b|a*b)a*", "ab", 7, "4.c) a(b*b + a*b)a*");
            mismoLenguaje(ExpresionesRegulares.deJflap("ab+c"), "(ab)|c", "abc", 5, "ab+c (la concatenación antes que la unión)");
        });

        caso("De JFLAP a Java: los demás caracteres son literales", 2000, () -> {
            String punto = ExpresionesRegulares.deJflap("a.b");
            verificar(Pattern.matches(punto, "a.b"), "a.b de JFLAP tendría que aceptar a.b (tu traducción: " + punto + ")");
            verificar(!Pattern.matches(punto, "axb"), "a.b de JFLAP no tendría que aceptar axb: en JFLAP el punto es un"
                    + " punto, pero en Java es cualquier carácter (tu traducción: " + punto + ")");
            String otros = ExpresionesRegulares.deJflap("a?b[c]$");
            verificar(Pattern.matches(otros, "a?b[c]$"), "a?b[c]$ de JFLAP tendría que aceptarse a sí misma"
                    + " (tu traducción: " + otros + ")");
            String espacio = ExpresionesRegulares.deJflap("a b*");
            verificar(Pattern.matches(espacio, "a bb") && !Pattern.matches(espacio, "abb"),
                    "en JFLAP el espacio es un carácter más: a b* acepta \"a bb\" y no \"abb\" (tu traducción: " + espacio + ")");
        });

        caso("Buscar: codicioso contra perezoso", 2000, () -> {
            iguales("[<a>b<c>]", ExpresionesRegulares.buscarTodas("<.*>", "<a>b<c>").toString(),
                    "<.*> sobre <a>b<c> (el .* codicioso se come todo lo que puede)");
            iguales("[<a>, <c>]", ExpresionesRegulares.buscarTodas("<.*?>", "<a>b<c>").toString(), "<.*?> sobre <a>b<c>");
            iguales("[]", ExpresionesRegulares.buscarTodas("x", "<a>b<c>").toString(), "un patrón que no aparece");
            iguales("[ab, ab]", ExpresionesRegulares.buscarTodas("ab", "abab").toString(), "ab sobre abab");
        });

        caso("Buscar etiquetas con tu expresión", 2000, () -> {
            String er = obligatoria(ExpresionesRegulares.ETIQUETA, "ETIQUETA");
            iguales("[<a>, <c>]", ExpresionesRegulares.buscarTodas(er, "<a>b<c>").toString(), "etiquetas de <a>b<c>");
            iguales("[<p>, </p>, <br>]", ExpresionesRegulares.buscarTodas(er, "<p>hola</p><br>").toString(),
                    "etiquetas de <p>hola</p><br>");
        });

        caso("Buscar los disparos de un log con tu expresión", 2000, () -> {
            String er = obligatoria(ExpresionesRegulares.TRANSICION, "TRANSICION");
            iguales("[T0, T12, T3]", ExpresionesRegulares.buscarTodas(er, "T0T12T3").toString(), "disparos de T0T12T3");
            iguales("[T1, T10, T1]", ExpresionesRegulares.buscarTodas(er, "T1T10T1").toString(),
                    "disparos de T1T10T1 (T10 no es T1 seguido de un 0)");
        });

        fin();
    }

    /** Falla con [TODO] si la constante sigue en null. */
    private static String obligatoria(String er, String nombre) {
        if (er == null) {
            throw new UnsupportedOperationException("ExpresionesRegulares." + nombre + ": escribí la expresión regular");
        }
        try {
            Pattern.compile(er);
        } catch (PatternSyntaxException e) {
            throw new AssertionError(nombre + " no es una expresión válida en Java: " + e.getDescription()
                    + " (en " + er + ")");
        }
        return er;
    }

    /** Compara la expresión con la definición del lenguaje, con matches(), en todas las cadenas hasta el largo. */
    private static void revisar(String er, String nombre, String alfabeto, int largoMax, Predicate<String> definicion) {
        Pattern patron = Pattern.compile(obligatoria(er, nombre));
        for (String s : todas(alfabeto, largoMax)) {
            boolean esperado = definicion.test(s);
            if (patron.matcher(s).matches() != esperado) {
                throw new AssertionError(nombre + " = " + er + ": " + (s.isEmpty() ? "λ" : s)
                        + (esperado ? " está en el lenguaje y la expresión no la acepta" : " no está en el lenguaje y la expresión la acepta"));
            }
        }
        info(nombre + " = " + er + " coincide con el lenguaje en las cadenas de largo <= " + largoMax);
    }

    /** Las dos expresiones aceptan las mismas cadenas hasta el largo. */
    private static void mismoLenguaje(String traducida, String esperada, String alfabeto, int largoMax, String que) {
        Pattern tuya;
        try {
            tuya = Pattern.compile(traducida);
        } catch (PatternSyntaxException e) {
            throw new AssertionError(que + ": tu traducción, " + traducida + ", no es una expresión válida en Java ("
                    + e.getDescription() + ")");
        }
        Pattern referencia = Pattern.compile(esperada);
        for (String s : todas(alfabeto, largoMax)) {
            if (tuya.matcher(s).matches() != referencia.matcher(s).matches()) {
                throw new AssertionError(que + ": tu traducción es " + traducida + ", y con " + (s.isEmpty() ? "λ" : s)
                        + (referencia.matcher(s).matches() ? " no acepta, pero tendría que aceptar" : " acepta, pero no tendría que aceptar"));
            }
        }
    }

    /** L3 según su definición: x c^(3m), con x ∈ {a, b}* con cantidad par de b. */
    private static boolean enL3(String s) {
        int i = 0;
        while (i < s.length() && s.charAt(i) != 'c') {
            i++;
        }
        String x = s.substring(0, i);
        String resto = s.substring(i);
        return x.chars().filter(c -> c == 'b').count() % 2 == 0 && resto.chars().allMatch(c -> c == 'c')
                && resto.length() % 3 == 0;
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
