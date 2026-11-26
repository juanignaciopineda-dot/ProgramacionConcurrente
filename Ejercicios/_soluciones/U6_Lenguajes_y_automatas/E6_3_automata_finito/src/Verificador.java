import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/** Verificador automático del ejercicio 6.3. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 6.3 - Autómata finito determinista");

        caso("Transiciones: f es una función, así que no puede tener dos destinos", 2000, () -> {
            AFD m = new AFD("q0", Set.of("q1"));
            m.agregarTransicion("q0", 'a', "q1");
            m.agregarTransicion("q0", 'a', "q1");
            iguales("q1", m.siguiente("q0", 'a'), "f(q0, a)");
            esperaExcepcion(IllegalArgumentException.class, () -> m.agregarTransicion("q0", 'a', "q2"),
                    "f(q0, a) = q2 cuando ya era q1 (no sería determinista)");
            iguales("q1", m.siguiente("q0", 'a'), "f(q0, a) después del intento rechazado");
            iguales(null, m.siguiente("q0", 'b'), "f(q0, b), que no está definida");
            iguales(null, m.siguiente("q9", 'a'), "f de un estado que no existe");
            m.agregarTransicion("q1", 'b', "q2");
            iguales("[q0, q1, q2]", m.estados().toString(), "estados (desde y hacia se agregan)");
            esperaExcepcion(IllegalArgumentException.class, () -> m.agregarTransicion(null, 'a', "q1"), "estado null");
        });

        caso("Recorrido de L3: e0 → e1 → e0 → e2 → e3 → e4", 2000, () -> {
            AFD m = Automatas.l3();
            iguales("[e0, e1, e0, e2, e3, e4]", m.recorrido("bbccc").toString(), "recorrido de bbccc");
            iguales("[e0]", m.recorrido("").toString(), "recorrido de λ");
            iguales("[e0, e2]", m.recorrido("ca").toString(), "recorrido de ca (de e2 no se sale con a: se traba)");
        });

        caso("Aceptar: leer todo y terminar en un estado final", 2000, () -> {
            AFD m = Automatas.l3();
            verificar(m.acepta("bbccc"), "bbccc está en L3 (dos b y tres c)");
            verificar(m.acepta(""), "λ está en L3 (cero b, cero c): e0 es final");
            verificar(m.acepta("abab") && m.acepta("aaa"), "abab y aaa están en L3");
            verificar(!m.acepta("bccc"), "bccc no está en L3 (una sola b)");
            verificar(!m.acepta("cc"), "cc no está en L3 (dos c no es múltiplo de 3)");
            verificar(!m.acepta("ccca"), "ccca no está en L3: después de las c no puede venir una a (se traba)");
            verificar(!m.acepta("ca"), "ca no está en L3: el recorrido se traba en e2");
        });

        caso("L3: el autómata de los apuntes contra la definición del lenguaje", 3000, () -> {
            compararConLaDefinicion(Automatas.l3(), "abc", 8, Verificador::enL3, "L3");
        });

        caso("Accesibles y conexo: a los estados sueltos no se llega", 2000, () -> {
            AFD m = new AFD("q0", Set.of("q2", "x"));
            m.agregarTransicion("q0", 'a', "q1");
            m.agregarTransicion("q1", 'b', "q2");
            m.agregarTransicion("q2", 'a', "q0");
            m.agregarTransicion("x", 'a', "q0");
            m.agregarTransicion("y", 'b', "x");
            iguales("[q0, q1, q2]", new java.util.TreeSet<>(m.accesibles()).toString(),
                    "accesibles (q2 está a dos transiciones del inicial; x e y no tienen arcos que lleguen desde q0)");
            verificar(!m.esConexo(), "con x e y sueltos, el autómata no es conexo");
            verificar(Automatas.l3().esConexo(), "el autómata de L3 es conexo");
            AFD solo = new AFD("q0", Set.of());
            iguales("[q0]", solo.accesibles().toString(), "un autómata sin transiciones: el inicial es accesible con λ");
        });

        caso("Diseño: cantidad par de b, con dos estados", 3000, () -> {
            AFD m = Automatas.parDeB();
            compararConLaDefinicion(m, "ab", 10, s -> cuantas(s, 'b') % 2 == 0, "par de b");
            verificar(m.estados().size() <= 2, "alcanza con dos estados y el tuyo tiene " + m.estados().size()
                    + ": " + m.estados() + ". ¿Qué hay que recordar de la historia?");
        });

        caso("Diseño: L4, las cadenas que contienen 00 ó 11", 3000, () -> {
            AFD m = Automatas.contiene00o11();
            compararConLaDefinicion(m, "01", 10, s -> s.contains("00") || s.contains("11"), "L4");
            info("tu autómata de L4 tiene " + m.estados().size() + " estados");
        });

        caso("Diseño: binarios múltiplos de 3, recordando solo el resto", 3000, () -> {
            AFD m = Automatas.multiploDe3();
            compararConLaDefinicion(m, "01", 12, s -> !s.isEmpty() && Integer.parseInt(s, 2) % 3 == 0, "múltiplos de 3");
            verificar(m.estados().size() <= 4, "alcanza con cuatro estados y el tuyo tiene " + m.estados().size()
                    + ": " + m.estados() + ". ¿Qué hay que recordar del número leído?");
        });

        fin();
    }

    /** Prueba el autómata con todas las cadenas del alfabeto hasta el largo y lo compara con la definición. */
    private static void compararConLaDefinicion(AFD m, String alfabeto, int largoMax, Predicate<String> definicion,
            String nombre) {
        int probadas = 0;
        for (String s : todas(alfabeto, largoMax)) {
            boolean esperado = definicion.test(s);
            if (m.acepta(s) != esperado) {
                throw new AssertionError(nombre + ": con " + (s.isEmpty() ? "λ" : s) + " se esperaba que "
                        + (esperado ? "acepte" : "rechace") + " y " + (esperado ? "rechazó" : "aceptó")
                        + ". Recorrido: " + m.recorrido(s) + "\nTabla del autómata:\n" + m);
            }
            probadas++;
        }
        info(nombre + ": coincide con la definición en las " + probadas + " cadenas de largo <= " + largoMax);
    }

    /** L3 según su definición: x c^(3m), con x ∈ {a, b}* con cantidad par de b. */
    private static boolean enL3(String s) {
        int i = 0;
        while (i < s.length() && s.charAt(i) != 'c') {
            i++;
        }
        String x = s.substring(0, i);
        String resto = s.substring(i);
        return cuantas(x, 'b') % 2 == 0 && resto.chars().allMatch(c -> c == 'c') && resto.length() % 3 == 0;
    }

    private static long cuantas(String s, char c) {
        return s.chars().filter(x -> x == c).count();
    }

    /** Todas las cadenas sobre el alfabeto de largo 0 a largoMax, de las más cortas a las más largas. */
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
