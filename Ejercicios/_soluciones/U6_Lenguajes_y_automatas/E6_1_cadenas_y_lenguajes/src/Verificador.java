import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Verificador automático del ejercicio 6.1. No hace falta modificarlo. */
public class Verificador {

    private static final Set<Character> AB = Set.of('a', 'b');

    public static void main(String[] args) {
        titulo("Ejercicio 6.1 - Cadenas y lenguajes");

        caso("Universo del discurso: V* hasta un largo, con λ incluida", 2000, () -> {
            iguales("{λ, a, aa, aaa}", texto(Lenguajes.universo(Set.of('a'), 3)), "universo({a}, 3)");
            iguales("{λ, a, b, aa, ab, ba, bb}", texto(Lenguajes.universo(AB, 2)), "universo({a, b}, 2)");
            iguales(1 + 2 + 4 + 8, Lenguajes.universo(AB, 3).size(), "cantidad de cadenas de {a, b}* con largo <= 3");
            iguales("{λ}", texto(Lenguajes.universo(AB, 0)), "universo({a, b}, 0): solo la cadena vacía");
            esperaExcepcion(IllegalArgumentException.class, () -> Lenguajes.universo(Set.of(), 2),
                    "alfabeto vacío (un alfabeto es finito y no vacío)");
            esperaExcepcion(IllegalArgumentException.class, () -> Lenguajes.universo(AB, -1), "largo negativo");
        });

        caso("Concatenación: los pares que dan la misma cadena cuentan una vez", 2000, () -> {
            iguales("{a, b, ac, bc}", texto(Lenguajes.concatenacion(conj("a", "b"), conj("c", ""))), "{a, b}{c, λ}");
            iguales("{a, ab, abb}", texto(Lenguajes.concatenacion(conj("a", "ab"), conj("b", ""))),
                    "{a, ab}{b, λ}: a·b y ab·λ dan las dos ab");
            iguales("{ba}", texto(Lenguajes.concatenacion(conj("b"), conj("a"))), "{b}{a} (el orden importa)");
        });

        caso("{λ} es el neutro de la concatenación y ∅ el absorbente", 2000, () -> {
            Set<String> l = conj("a", "ab", "");
            iguales(texto(l), texto(Lenguajes.concatenacion(l, conj(""))), "L{λ} tendría que ser L");
            iguales(texto(l), texto(Lenguajes.concatenacion(conj(""), l)), "{λ}L tendría que ser L");
            iguales("∅", texto(Lenguajes.concatenacion(l, conj())), "L∅ tendría que ser ∅ (no hay y para armar xy)");
            iguales("∅", texto(Lenguajes.concatenacion(conj(), l)), "∅L tendría que ser ∅");
        });

        caso("Potencia: L⁰ = {λ} para cualquier L, incluso ∅", 2000, () -> {
            iguales("{λ}", texto(Lenguajes.potencia(conj("ab"), 0)), "{ab}⁰");
            iguales("{λ}", texto(Lenguajes.potencia(conj(), 0)), "∅⁰ (L⁰ = {λ} por definición, aunque L esté vacío)");
            iguales("{ab}", texto(Lenguajes.potencia(conj("ab"), 1)), "{ab}¹");
            iguales("{ababab}", texto(Lenguajes.potencia(conj("ab"), 3)), "{ab}³");
            iguales("{aa, ab, ba, bb}", texto(Lenguajes.potencia(conj("a", "b"), 2)), "{a, b}²");
            iguales("∅", texto(Lenguajes.potencia(conj(), 2)), "∅²");
            esperaExcepcion(IllegalArgumentException.class, () -> Lenguajes.potencia(conj("a"), -1), "exponente negativo");
        });

        caso("Cierre: L* hasta un largo, con ∅* = {λ}", 2000, () -> {
            iguales("{λ, ab, abab, ababab}", texto(Lenguajes.cierre(conj("ab"), 6)), "{ab}* hasta largo 6");
            iguales("{λ, ab, abab}", texto(Lenguajes.cierre(conj("ab"), 5)), "{ab}* hasta largo 5");
            iguales("{λ}", texto(Lenguajes.cierre(conj(), 4)), "∅* (L⁰ = {λ} sigue estando)");
            iguales(texto(Lenguajes.universo(AB, 3)), texto(Lenguajes.cierre(conj("a", "b"), 3)),
                    "{a, b}* hasta largo 3 tendría que ser el universo de {a, b}");
            iguales("{λ, a, aa, aaa, aaaa}", texto(Lenguajes.cierre(conj("aa", "a"), 4)), "{a, aa}* hasta largo 4");
        });

        caso("Cierre con λ en el lenguaje: tiene que terminar", 2000, () -> {
            iguales("{λ}", texto(Lenguajes.cierre(conj(""), 5)), "{λ}* = {λ}");
            iguales("{λ, a, aa, aaa}", texto(Lenguajes.cierre(conj("", "a"), 3)), "{λ, a}* hasta largo 3");
        });

        caso("Cierre positivo: L⁺ = L L*", 2000, () -> {
            iguales("{ab, abab}", texto(Lenguajes.cierrePositivo(conj("ab"), 4)), "{ab}⁺ hasta largo 4 (sin λ)");
            iguales("∅", texto(Lenguajes.cierrePositivo(conj(), 4)), "∅⁺ (sin L⁰ no queda nada)");
            iguales("{λ, a, aa}", texto(Lenguajes.cierrePositivo(conj("", "a"), 2)),
                    "{λ, a}⁺ hasta largo 2: λ ∈ L¹, así que λ ∈ L⁺ (L⁺ = L* − {λ} solo vale si λ ∉ L)");
        });

        caso("Por propiedad: los palíndromos de {0, 1} hasta largo 4", 2000, () -> {
            verificar(Lenguajes.esPalindromo(""), "λ es un palíndromo");
            verificar(Lenguajes.esPalindromo("0110") && Lenguajes.esPalindromo("101"), "0110 y 101 son palíndromos");
            verificar(!Lenguajes.esPalindromo("01") && !Lenguajes.esPalindromo("0100"), "01 y 0100 no son palíndromos");
            Set<String> palindromos = Lenguajes.porPropiedad(Set.of('0', '1'), 4, Lenguajes::esPalindromo);
            iguales("{λ, 0, 1, 00, 11, 000, 010, 101, 111, 0000, 0110, 1001, 1111}", texto(palindromos),
                    "palíndromos sobre {0, 1} hasta largo 4");
            Set<String> parDeB = Lenguajes.porPropiedad(AB, 3, s -> s.chars().filter(c -> c == 'b').count() % 2 == 0);
            iguales("{λ, a, aa, bb, aaa, abb, bab, bba}", texto(parDeB), "cadenas de {a, b} con cantidad par de b, hasta largo 3");
        });

        caso("No modifica los lenguajes que recibe", 2000, () -> {
            Set<String> l1 = conj("a", "");
            Set<String> l2 = conj("b");
            Lenguajes.concatenacion(l1, l2);
            Lenguajes.potencia(l1, 3);
            Lenguajes.cierre(l1, 3);
            Lenguajes.cierrePositivo(l2, 3);
            iguales("{λ, a}", texto(l1), "el primer lenguaje después de operar");
            iguales("{b}", texto(l2), "el segundo lenguaje después de operar");
        });

        fin();
    }

    /** Un lenguaje modificable con esas cadenas ("" es λ). */
    private static Set<String> conj(String... cadenas) {
        return new HashSet<>(List.of(cadenas));
    }

    /** El lenguaje ordenado por largo y alfabéticamente, con λ y ∅. */
    private static String texto(Set<String> l) {
        if (l == null) {
            return "null";
        }
        if (l.isEmpty()) {
            return "∅";
        }
        List<String> orden = new ArrayList<>(l);
        orden.sort(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()));
        List<String> textos = new ArrayList<>();
        for (String cadena : orden) {
            textos.add(cadena.isEmpty() ? "λ" : cadena);
        }
        return "{" + String.join(", ", textos) + "}";
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
