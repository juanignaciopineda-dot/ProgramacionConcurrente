import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/** Verificador automático del ejercicio 6.2. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 6.2 - Gramáticas y la jerarquía de Chomsky");

        caso("Tipo: la actividad \"Clasificar gramáticas\" de los apuntes", 2000, () -> {
            iguales(3, new Gramatica('S', "S -> aB", "B -> bB | b").tipo(), "a) S → aB, B → bB, B → b");
            iguales(2, new Gramatica('S', "S -> aSb | ab").tipo(), "b) S → aSb, S → ab");
            iguales(1, new Gramatica('S', "aSb -> aaSbb", "S -> c").tipo(), "c) aSb → aaSbb, S → c");
            iguales(2, new Gramatica('S', "S -> aS | Sb | λ").tipo(),
                    "d) S → aS, S → Sb, S → λ (S → Sb no tiene la forma A → aB, aunque el lenguaje a*b* sea regular)");
        });

        caso("Tipo 3: A → aB, A → a o A → λ, y nada más", 2000, () -> {
            iguales(3, gramaticaDeLosApuntes().tipo(), "S → aS | aB, B → bB | b");
            iguales(3, gramaticaDelEjercicio3().tipo(), "la del ejercicio 3 de los apuntes, con B → λ");
            iguales(3, new Gramatica('S', "S -> λ").tipo(), "S → λ");
            iguales(2, new Gramatica('S', "S -> Ba", "B -> b").tipo(),
                    "S → Ba: el no terminal va a la derecha del terminal en el tipo 3 (lineal a la derecha)");
            iguales(2, new Gramatica('S', "S -> ab").tipo(), "S → ab: dos terminales no es la forma del tipo 3");
            iguales(2, new Gramatica('S', "S -> AB", "A -> a", "B -> b").tipo(), "S → AB: dos no terminales");
        });

        caso("Tipo 1 y tipo 0: la parte derecha no puede acortar (salvo S → λ)", 2000, () -> {
            iguales(1, gramaticaAnBnCn().tipo(), "la gramática de aⁿbⁿcⁿ");
            iguales(1, new Gramatica('S', "aSb -> aaSbb", "S -> c | λ").tipo(),
                    "aSb → aaSbb con S → λ (la excepción del símbolo inicial)");
            iguales(0, new Gramatica('S', "aSb -> c", "S -> ab").tipo(), "aSb → c acorta: tipo 0");
            iguales(0, new Gramatica('S', "aSb -> aaSbb", "S -> c", "B -> λ").tipo(),
                    "B → λ acorta y B no es el inicial: en una gramática que no es de tipo 2, eso la deja en tipo 0");
        });

        caso("Derivaciones directas: una producción, en una de sus apariciones", 2000, () -> {
            Gramatica anbn = new Gramatica('S', "S -> aSb | ab");
            iguales("{aabb, aaSbb}", texto(anbn.derivacionesDirectas("aSb")), "de aSb con S → aSb | ab");
            iguales("∅", texto(anbn.derivacionesDirectas("ab")), "de ab (no hay no terminales)");
            Gramatica dobles = new Gramatica('S', "S -> SS | a");
            iguales("{Sa, aS, SSS}", texto(dobles.derivacionesDirectas("SS")),
                    "de SS con S → SS | a (hay que probar cada aparición de S)");
            iguales("{aBBCC, abCBC}", texto(gramaticaAnBnCn().derivacionesDirectas("aBCBC")),
                    "de aBCBC en la gramática de aⁿbⁿcⁿ (CB → BC y aB → ab)");
            iguales("{a, aaS}", texto(new Gramatica('S', "S -> aS | λ").derivacionesDirectas("aS")),
                    "de aS con S → aS | λ (λ borra la S)");
        });

        caso("Derivación completa: la de los apuntes y algunas que no lo son", 2000, () -> {
            Gramatica g = gramaticaDeLosApuntes();
            verificar(g.esDerivacion(List.of("S", "aS", "aaB", "aabB", "aabb")), "S ⇒ aS ⇒ aaB ⇒ aabB ⇒ aabb es una derivación");
            verificar(!g.esDerivacion(List.of("S", "aaB", "aabB", "aabb")), "S ⇒ aaB salta un paso: no es derivación directa");
            verificar(!g.esDerivacion(List.of("S", "aS", "aaB")), "termina en aaB, que tiene un no terminal");
            verificar(!g.esDerivacion(List.of("aS", "aaB", "aab")), "no empieza en el símbolo inicial");
            verificar(!g.esDerivacion(List.of()), "una lista vacía no es una derivación");
            verificar(new Gramatica('S', "S -> aS | Sb | λ").esDerivacion(List.of("S", "")), "S ⇒ λ con S → λ");
        });

        caso("Generar: aⁿbⁿ y el lenguaje de la gramática de los apuntes", 3000, () -> {
            iguales("{ab, aabb, aaabbb}", texto(new Gramatica('S', "S -> aSb | ab").generar(6)), "S → aSb | ab hasta largo 6");
            iguales("{ab, aab, abb, aaab, aabb, abbb}", texto(gramaticaDeLosApuntes().generar(4)),
                    "S → aS | aB, B → bB | b hasta largo 4 (a⁺b⁺)");
            iguales("∅", texto(new Gramatica('S', "S -> aSb | ab").generar(1)), "aⁿbⁿ hasta largo 1");
        });

        caso("Generar con λ: S ⇒* λ hace que λ esté en el lenguaje", 3000, () -> {
            iguales("{λ, a, b, aa, ab, bb}", texto(new Gramatica('S', "S -> aS | Sb | λ").generar(2)),
                    "S → aS | Sb | λ hasta largo 2 (a*b*)");
            iguales(texto(porRegex("a+b+a?", "ab", 5)), texto(gramaticaDelEjercicio3().generar(5)),
                    "la gramática del ejercicio 3 hasta largo 5 (a⁺b⁺ con una a opcional al final)");
        });

        caso("Generar con S → SS: tiene que terminar", 3000, () -> {
            iguales("{a, aa, aaa}", texto(new Gramatica('S', "S -> SS | a").generar(3)), "S → SS | a hasta largo 3");
        });

        caso("Generar con una gramática de tipo 1: aⁿbⁿcⁿ", 8000, () -> {
            iguales("{abc, aabbcc, aaabbbccc}", texto(gramaticaAnBnCn().generar(9)), "la gramática de aⁿbⁿcⁿ hasta largo 9");
        });

        fin();
    }

    /** La del ejemplo de los apuntes: L = { aⁿbᵐ | n, m ≥ 1 }. */
    private static Gramatica gramaticaDeLosApuntes() {
        return new Gramatica('S', "S -> aS | aB", "B -> bB | b");
    }

    /** La del ejercicio 3 de los apuntes, con B → λ. */
    private static Gramatica gramaticaDelEjercicio3() {
        return new Gramatica('S', "S -> aA", "A -> aA | bB", "B -> bB | a | λ");
    }

    /** La gramática clásica de aⁿbⁿcⁿ, sensible al contexto. */
    private static Gramatica gramaticaAnBnCn() {
        return new Gramatica('S', "S -> aSBC | aBC", "CB -> BC", "aB -> ab", "bB -> bb", "bC -> bc", "cC -> cc");
    }

    /** Las cadenas sobre el alfabeto, hasta el largo, que la expresión regular acepta entera. */
    private static Set<String> porRegex(String regex, String alfabeto, int largoMax) {
        Pattern patron = Pattern.compile(regex);
        Set<String> resultado = new HashSet<>();
        List<String> tanda = List.of("");
        for (int largo = 0; largo <= largoMax; largo++) {
            List<String> siguiente = new ArrayList<>();
            for (String s : tanda) {
                if (patron.matcher(s).matches()) {
                    resultado.add(s);
                }
                for (char c : alfabeto.toCharArray()) {
                    siguiente.add(s + c);
                }
            }
            tanda = siguiente;
        }
        return resultado;
    }

    /** El conjunto ordenado por largo y alfabéticamente, con λ y ∅. */
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
