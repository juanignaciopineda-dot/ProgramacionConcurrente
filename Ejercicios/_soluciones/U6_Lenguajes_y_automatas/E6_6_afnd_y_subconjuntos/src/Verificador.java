import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

/** Verificador automático del ejercicio 6.6. No hace falta modificarlo. */
public class Verificador {

    private static final char L = AFND.LAMBDA;

    public static void main(String[] args) {
        titulo("Ejercicio 6.6 - AFND, subconjuntos y gramáticas regulares");

        caso("Clausura-λ: los estados a los que se llega sin leer nada", 2000, () -> {
            AFND m = conCicloDeLambda();
            iguales("[p, q, r]", m.clausuraLambda(Set.of("p")).toString(), "clausura-λ de {p} (p → q → r → p por λ)");
            iguales("[p, q, r]", m.clausuraLambda(Set.of("q")).toString(), "clausura-λ de {q}");
            iguales("[s]", m.clausuraLambda(Set.of("s")).toString(), "clausura-λ de {s}: s mismo (cero transiciones λ)");
            iguales("[]", m.clausuraLambda(Set.of()).toString(), "clausura-λ del vacío");
            Set<String> argumento = new HashSet<>(Set.of("p"));
            m.clausuraLambda(argumento);
            iguales("[p]", argumento.toString(), "el conjunto que se pasó como argumento (no hay que modificarlo)");
        });

        caso("Mover: la unión de los destinos, sin clausura", 2000, () -> {
            AFND m = terminaEnAb();
            iguales("[q0, q1]", m.mover(Set.of("q0"), 'a').toString(), "mover({q0}, a)");
            iguales("[q0, q2]", m.mover(Set.of("q0", "q1"), 'b').toString(), "mover({q0, q1}, b)");
            iguales("[]", m.mover(Set.of("q2"), 'a').toString(), "mover({q2}, a)");
            iguales("[]", conCicloDeLambda().mover(Set.of("p"), 'a').toString(),
                    "mover({p}, a): la a sale de r, pero mover no sigue los λ (eso lo hace la clausura)");
        });

        caso("Aceptar: el ejercicio 5.a de los apuntes", 2000, () -> {
            AFND m = terminaEnAb();
            for (String w : List.of("ab", "aab", "babab")) {
                verificar(m.acepta(w), w + " termina en ab y tendría que aceptarse");
            }
            for (String w : List.of("aba", "ba", "", "a")) {
                verificar(!m.acepta(w), (w.isEmpty() ? "λ" : w) + " no termina en ab y no tendría que aceptarse");
            }
            comparar(m::acepta, "ab", 8, s -> s.endsWith("ab"), "el AFND de las terminadas en ab");
        });

        caso("Aceptar con λ y con dos estados iniciales", 2000, () -> {
            AFND asBes = new AFND(Set.of("x"), Set.of("y"));
            asBes.agregarTransicion("x", 'a', "x");
            asBes.agregarTransicion("x", L, "y");
            asBes.agregarTransicion("y", 'b', "y");
            comparar(asBes::acepta, "ab", 7, s -> s.matches("a*b*"), "a*b* con una transición λ");
            AFND dosInicios = new AFND(Set.of("i1", "i2"), Set.of("f1", "f2"));
            dosInicios.agregarTransicion("i1", 'a', "f1");
            dosInicios.agregarTransicion("f1", 'a', "f1");
            dosInicios.agregarTransicion("i2", 'b', "f2");
            dosInicios.agregarTransicion("f2", 'b', "f2");
            comparar(dosInicios::acepta, "ab", 7, s -> s.matches("a+|b+"), "a⁺ + b⁺ con dos estados iniciales");
        });

        caso("Subconjuntos: el AFND de la figura da 3 de los 8 subconjuntos", 2000, () -> {
            AFD d = terminaEnAb().aAFD();
            iguales("[{q0,q1}, {q0,q2}, {q0}]", d.estados().toString(), "estados del AFD");
            iguales("{q0}", d.inicial(), "estado inicial del AFD");
            iguales("[{q0,q2}]", new TreeSet<>(d.finales()).toString(), "finales: los que tienen a q2");
            iguales("{q0,q2}", d.siguiente("{q0,q1}", 'b'), "f'({q0,q1}, b)");
            comparar(d::acepta, "ab", 8, s -> s.endsWith("ab"), "el AFD de las terminadas en ab");
        });

        caso("Subconjuntos con λ y con dos iniciales", 2000, () -> {
            AFND m = new AFND(Set.of("i", "j"), Set.of("f"));
            m.agregarTransicion("i", 'a', "i");
            m.agregarTransicion("i", L, "k");
            m.agregarTransicion("k", 'b', "f");
            m.agregarTransicion("j", 'c', "f");
            m.agregarTransicion("f", L, "j");
            AFD d = m.aAFD();
            iguales("{i,j,k}", d.inicial(), "inicial del AFD: la clausura-λ de los dos iniciales");
            comparar(d::acepta, "abc", 6, s -> s.matches("a*bc*|c+"), "el AFD de a*bc* + c⁺");
        });

        caso("Subconjuntos: la explosión, de n + 1 estados a 2ⁿ", 5000, () -> {
            for (int n = 2; n <= 5; n++) {
                AFD d = enesimaDesdeElFinal(n).aAFD();
                int largo = n;
                comparar(d::acepta, "ab", 10, s -> s.length() >= largo && s.charAt(s.length() - largo) == 'a',
                        "la " + n + "ª desde el final es a");
                iguales(1 << n, d.estados().size(), "estados del AFD con n = " + n + " (el AFND tiene " + (n + 1) + ")");
            }
        });

        caso("De la gramática al AFND: el ejemplo de los apuntes", 2000, () -> {
            AFND m = AFND.deGramatica(new Gramatica('S', "S -> aS | aA", "A -> bA | b"));
            iguales("[A, S, qf]", m.estados().toString(), "estados: uno por no terminal, más qf");
            iguales("[S]", m.iniciales().toString(), "inicial");
            iguales("[qf]", m.finales().toString(), "finales");
            iguales("[A, S]", m.mover(Set.of("S"), 'a').toString(), "f(S, a): S → aS y S → aA");
            iguales("[A, qf]", m.mover(Set.of("A"), 'b').toString(), "f(A, b): A → bA y A → b");
            comparar(m::acepta, "ab", 8, s -> s.matches("a+b+"), "el AFND de a*ab*b");
            esperaExcepcion(IllegalArgumentException.class,
                    () -> AFND.deGramatica(new Gramatica('S', "S -> aSb | ab")), "una gramática de tipo 2");
        });

        caso("De la gramática al AFND, con B → λ, y de ahí al AFD (ejercicio 3)", 2000, () -> {
            AFND m = AFND.deGramatica(new Gramatica('S', "S -> aA", "A -> aA | bB", "B -> bB | a | λ"));
            iguales("[B, qf]", m.finales().toString(), "finales (B → λ hace final a B)");
            comparar(m::acepta, "ab", 8, s -> s.matches("a+b+a?"), "el AFND del ejercicio 3");
            AFD d = m.aAFD();
            comparar(d::acepta, "ab", 8, s -> s.matches("a+b+a?"), "el AFD del ejercicio 3");
            info("ejercicio 3: AFND de " + m.estados().size() + " estados, AFD de " + d.estados().size() + " " + d.estados());
        });

        caso("A mano: la gramática de L3 (ejercicio 1.c)", 4000, () -> {
            revisarALaMano(ALaMano.L3, "ALaMano.L3", "abc", 7, Verificador::enL3);
        });

        caso("A mano: la gramática de L4 (ejercicio 2.b)", 4000, () -> {
            revisarALaMano(ALaMano.L4, "ALaMano.L4", "01", 8, s -> s.contains("00") || s.contains("11"));
        });

        fin();
    }

    private static void revisarALaMano(String[] producciones, String nombre, String alfabeto, int largoMax,
            Predicate<String> definicion) {
        if (producciones.length == 0) {
            throw new UnsupportedOperationException(nombre + ": escribí las producciones");
        }
        Gramatica g = new Gramatica('S', producciones);
        iguales(3, g.tipo(), nombre + ": tipo de la gramática");
        for (Gramatica.Produccion p : g.producciones()) {
            verificar(!p.derecha().isEmpty() || p.izquierda().equals("S"),
                    nombre + ": " + p + " (en la forma estricta, la única producción con λ es S → λ)");
            verificar(!p.derecha().contains("S"), nombre + ": " + p + " (S no puede aparecer a la derecha:"
                    + " si al estado inicial le llegan arcos, tiene su propio no terminal)");
        }
        Set<String> generadas = g.generar(largoMax);
        for (String s : todas(alfabeto, largoMax)) {
            boolean esperado = definicion.test(s);
            if (generadas.contains(s) != esperado) {
                throw new AssertionError(nombre + ": " + (s.isEmpty() ? "λ" : s) + (esperado
                        ? " está en el lenguaje y la gramática no la genera"
                        : " no está en el lenguaje y la gramática la genera"));
            }
        }
        info(nombre + ": genera exactamente el lenguaje en las cadenas de largo <= " + largoMax);
    }

    /** p → q → r → p por λ, y r --a--&gt; s. */
    private static AFND conCicloDeLambda() {
        AFND m = new AFND(Set.of("p"), Set.of("s"));
        m.agregarTransicion("p", L, "q");
        m.agregarTransicion("q", L, "r");
        m.agregarTransicion("r", L, "p");
        m.agregarTransicion("r", 'a', "s");
        return m;
    }

    /** El AFND de la figura de los apuntes: las cadenas de {a, b} que terminan en ab. */
    private static AFND terminaEnAb() {
        AFND m = new AFND(Set.of("q0"), Set.of("q2"));
        m.agregarTransicion("q0", 'a', "q0");
        m.agregarTransicion("q0", 'b', "q0");
        m.agregarTransicion("q0", 'a', "q1");
        m.agregarTransicion("q1", 'b', "q2");
        return m;
    }

    /** n + 1 estados: "adivina" cuál es la a que queda n lugares antes del final. */
    private static AFND enesimaDesdeElFinal(int n) {
        AFND m = new AFND(Set.of("q0"), Set.of("q" + n));
        m.agregarTransicion("q0", 'a', "q0");
        m.agregarTransicion("q0", 'b', "q0");
        m.agregarTransicion("q0", 'a', "q1");
        for (int i = 1; i < n; i++) {
            m.agregarTransicion("q" + i, 'a', "q" + (i + 1));
            m.agregarTransicion("q" + i, 'b', "q" + (i + 1));
        }
        return m;
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

    private static void comparar(Predicate<String> automata, String alfabeto, int largoMax, Predicate<String> definicion,
            String nombre) {
        for (String s : todas(alfabeto, largoMax)) {
            boolean esperado = definicion.test(s);
            if (automata.test(s) != esperado) {
                throw new AssertionError(nombre + ": con " + (s.isEmpty() ? "λ" : s) + " se esperaba que "
                        + (esperado ? "acepte" : "rechace") + " y " + (esperado ? "rechazó" : "aceptó"));
            }
        }
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
