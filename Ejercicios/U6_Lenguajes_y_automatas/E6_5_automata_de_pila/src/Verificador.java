import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/** Verificador automático del ejercicio 6.5. No hace falta modificarlo. */
public class Verificador {

    private static final char L = AutomataDePila.LAMBDA;

    public static void main(String[] args) {
        titulo("Ejercicio 6.5 - Autómata de pila");

        caso("Determinismo: mismo estado, entrada y tope con dos resultados", 2000, () -> {
            AutomataDePila m = new AutomataDePila("q", 'Z', Set.of());
            m.agregarTransicion("q", 'a', 'Z', "q", "aZ");
            m.agregarTransicion("q", 'a', 'Z', "q", "aZ");
            esperaExcepcion(IllegalArgumentException.class, () -> m.agregarTransicion("q", 'a', 'Z', "p", "aZ"),
                    "δ(q, a, Z) a otro estado");
            esperaExcepcion(IllegalArgumentException.class, () -> m.agregarTransicion("q", 'a', 'Z', "q", "Z"),
                    "δ(q, a, Z) apilando otra cosa");
            m.agregarTransicion("q", 'a', 'a', "q", "");
            m.agregarTransicion("q", 'a', 'b', "q", "");
            m.agregarTransicion("p", 'a', 'Z', "p", "Z");
        });

        caso("Determinismo: una transición sobre λ no puede competir con una sobre un símbolo", 2000, () -> {
            AutomataDePila m = new AutomataDePila("q", 'Z', Set.of());
            m.agregarTransicion("q", 'a', 'Z', "q", "aZ");
            esperaExcepcion(IllegalArgumentException.class, () -> m.agregarTransicion("q", L, 'Z', "p", "Z"),
                    "δ(q, λ, Z) cuando ya está δ(q, a, Z)");
            m.agregarTransicion("q", L, 'a', "p", "a");
            esperaExcepcion(IllegalArgumentException.class, () -> m.agregarTransicion("q", 'b', 'a', "p", ""),
                    "δ(q, b, a) cuando ya está δ(q, λ, a)");
            m.agregarTransicion("p", L, 'Z', "q", "Z");
            m.agregarTransicion("q", 'b', 'b', "p", "");
        });

        caso("Un movimiento: (q0, aabb, Z) ⊢ (q1, abb, aZ)", 2000, () -> {
            AutomataDePila m = AutomatasDePila.anbn();
            iguales("(q1, abb, aZ)", String.valueOf(m.paso(m.inicio("aabb"))), "el primer movimiento con aabb");
            iguales("(q2, b, aZ)", String.valueOf(m.paso(new AutomataDePila.Configuracion("q1", "bb", "aaZ"))),
                    "de (q1, bb, aaZ): b, a ; λ desapila");
            iguales("(q3, λ, Z)", String.valueOf(m.paso(new AutomataDePila.Configuracion("q2", "", "Z"))),
                    "de (q2, λ, Z): la transición sobre λ no lee nada");
            iguales("(q3, b, Z)", String.valueOf(m.paso(new AutomataDePila.Configuracion("q2", "b", "Z"))),
                    "de (q2, b, Z): no hay δ(q2, b, Z), pero sí δ(q2, λ, Z), que no mueve la cinta");
            iguales("null", String.valueOf(m.paso(new AutomataDePila.Configuracion("q3", "", "Z"))),
                    "de (q3, λ, Z) no hay movimiento: se para");
            iguales("null", String.valueOf(m.paso(new AutomataDePila.Configuracion("q1", "a", ""))),
                    "con la pila vacía, el autómata se para");
        });

        caso("Traza de configuraciones de aabb (la actividad de los apuntes)", 2000, () -> {
            iguales("[(q0, aabb, Z), (q1, abb, aZ), (q1, bb, aaZ), (q2, b, aZ), (q2, λ, Z), (q3, λ, Z)]",
                    AutomatasDePila.anbn().traza("aabb").toString(), "traza de aabb");
            verificar(AutomatasDePila.anbn().acepta("aabb"), "aabb tendría que aceptarse");
        });

        caso("Los dos rechazos: aab y abb", 2000, () -> {
            AutomataDePila m = AutomatasDePila.anbn();
            List<AutomataDePila.Configuracion> aab = m.traza("aab");
            iguales("(q2, λ, aZ)", String.valueOf(aab.get(aab.size() - 1)),
                    "última configuración de aab: se leyó todo, pero la pila no volvió a Z");
            verificar(!m.acepta("aab"), "aab no tendría que aceptarse");
            List<AutomataDePila.Configuracion> abb = m.traza("abb");
            iguales("(q3, b, Z)", String.valueOf(abb.get(abb.size() - 1)),
                    "última configuración de abb: llegó a q3, pero le queda una b sin leer");
            verificar(!m.acepta("abb"), "abb no tendría que aceptarse: llegar a un final con entrada sin leer no alcanza");
            verificar(!m.acepta(""), "λ no está en aⁿbⁿ con n > 0");
        });

        caso("Un bucle de λ no cuelga: IllegalStateException", 3000, () -> {
            AutomataDePila m = new AutomataDePila("q", 'Z', Set.of("f"));
            m.agregarTransicion("q", L, 'Z', "q", "Z");
            esperaExcepcion(IllegalStateException.class, () -> m.traza("a"), "traza de un autómata que no se para");
        });

        caso("Con la pila vacía, se para", 2000, () -> {
            AutomataDePila m = new AutomataDePila("q", 'Z', Set.of("q"));
            m.agregarTransicion("q", 'a', 'Z', "q", "");
            iguales("[(q, aa, Z), (q, a, λ)]", m.traza("aa").toString(), "traza de aa (la primera a vacía la pila)");
            verificar(!m.acepta("aa"), "aa no se acepta: el autómata se paró con una a sin leer");
            verificar(m.acepta("a"), "a se acepta: leyó todo y está en un estado final");
        });

        caso("aⁿbⁿ: el autómata de los apuntes contra la definición", 3000, () -> {
            comparar(AutomatasDePila.anbn(), "ab", 10, s -> s.matches("a+b+") && cuantas(s, 'a') == cuantas(s, 'b'), "aⁿbⁿ");
        });

        caso("Diseño: aⁿb²ⁿ", 3000, () -> {
            comparar(AutomatasDePila.anb2n(), "ab", 12,
                    s -> s.matches("a+b+") && 2 * cuantas(s, 'a') == cuantas(s, 'b'), "aⁿb²ⁿ");
        });

        caso("Diseño: los lock() y unlock() de un hilo", 3000, () -> {
            comparar(AutomatasDePila.lockBienUsado(), "lu", 12, Verificador::lockBienUsado, "lock bien usado");
        });

        fin();
    }

    /** La definición: el contador de lock() nunca baja de cero y termina en cero. */
    private static boolean lockBienUsado(String traza) {
        int abiertos = 0;
        for (char c : traza.toCharArray()) {
            abiertos += c == 'l' ? 1 : -1;
            if (abiertos < 0) {
                return false;
            }
        }
        return abiertos == 0;
    }

    private static void comparar(AutomataDePila m, String alfabeto, int largoMax, Predicate<String> definicion, String nombre) {
        int probadas = 0;
        for (String s : todas(alfabeto, largoMax)) {
            boolean esperado = definicion.test(s);
            if (m.acepta(s) != esperado) {
                throw new AssertionError(nombre + ": con " + (s.isEmpty() ? "λ" : s) + " se esperaba que "
                        + (esperado ? "acepte" : "rechace") + " y " + (esperado ? "rechazó" : "aceptó")
                        + ".\nTraza: " + m.traza(s));
            }
            probadas++;
        }
        info(nombre + ": coincide con la definición en las " + probadas + " cadenas de largo <= " + largoMax);
    }

    private static long cuantas(String s, char c) {
        return s.chars().filter(x -> x == c).count();
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
