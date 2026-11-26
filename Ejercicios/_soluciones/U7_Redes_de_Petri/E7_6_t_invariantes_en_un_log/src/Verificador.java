import java.util.List;
import java.util.regex.Pattern;

/** Verificador automático del ejercicio 7.6. No hace falta modificarlo. */
public class Verificador {

    /** Los T-invariantes de la exclusión mutua: A entra y sale (T0 T1), B entra y sale (T2 T3). */
    private static final List<int[]> MUTEX = List.of(new int[] {0, 1}, new int[] {2, 3});

    /** El T-invariante del productor/consumidor de los apuntes, en los cuatro órdenes en que puede aparecer. */
    private static final List<int[]> PRODUCTOR_CONSUMIDOR = List.of(new int[] {0, 1, 2, 3, 4, 5},
            new int[] {0, 1, 3, 2, 4, 5}, new int[] {0, 1, 3, 4, 2, 5}, new int[] {0, 1, 3, 4, 5, 2});

    /** Los dos invariantes de los apuntes que comparten la transición inicial. */
    private static final List<int[]> COMPARTEN_T0 = List.of(new int[] {0, 1, 2}, new int[] {0, 2, 3});

    /** La expresión del productor/consumidor tal como está en los apuntes (sin anclar los números). */
    private static final String PATRON_DE_LOS_APUNTES = "T0(.*?)T1(.*?)(?:T2(.*?)T3(.*?)T4(.*?)T5"
            + "|T3(.*?)(?:T2(.*?)T4(.*?)T5|T4(.*?)(?:T2(.*?)T5|T5(.*?)T2)))";

    public static void main(String[] args) {
        titulo("Ejercicio 7.6 - T-invariantes en el log");

        caso("Disparos: leer el log con T(\\d+)", 2000, () -> {
            iguales("[0, 12, 3]", AnalizadorDeLog.disparos("T0T12T3").toString(), "disparos de T0T12T3");
            iguales("[]", AnalizadorDeLog.disparos("").toString(), "disparos de un log vacío");
            iguales("[10, 1, 0]", AnalizadorDeLog.disparos("T10T1T0").toString(), "disparos de T10T1T0");
            esperaExcepcion(IllegalArgumentException.class, () -> AnalizadorDeLog.disparos("T0xT1"), "T0xT1 (una x en el medio)");
            esperaExcepcion(IllegalArgumentException.class, () -> AnalizadorDeLog.disparos("T0T1 "), "T0T1 con un espacio al final");
            esperaExcepcion(IllegalArgumentException.class, () -> AnalizadorDeLog.disparos("0T1"), "0T1 (algo antes del primer disparo)");
        });

        caso("Reproducir el log en la red de exclusión mutua", 2000, () -> {
            iguales(-1, AnalizadorDeLog.primerDisparoImposible(redExclusionMutua(), "T0T1T2T3T2T3"), "T0T1T2T3T2T3 es posible");
            iguales(-1, AnalizadorDeLog.primerDisparoImposible(redExclusionMutua(), ""), "el log vacío es posible");
            iguales(1, AnalizadorDeLog.primerDisparoImposible(redExclusionMutua(), "T0T2T1T3"),
                    "en T0T2T1T3, B entra (T2) con A adentro: el mutex no tiene ficha");
            iguales(1, AnalizadorDeLog.primerDisparoImposible(redExclusionMutua(), "T0T0T1T1"),
                    "en T0T0T1T1, A entra dos veces: no hay una segunda ficha en \"A afuera\"");
            esperaExcepcion(IllegalArgumentException.class,
                    () -> AnalizadorDeLog.primerDisparoImposible(redExclusionMutua(), "T0T9"), "una transición que la red no tiene");
        });

        caso("Expresión: los dos invariantes de la exclusión mutua", 2000, () -> {
            iguales(anclar("(?:T0(.*?)T1|T2(.*?)T3)"), AnalizadorDeLog.expresion(MUTEX), "expresión de la exclusión mutua");
            iguales(anclar("T0(.*?)T1(.*?)T2"), AnalizadorDeLog.expresion(List.of(new int[] {0, 1, 2})),
                    "expresión de un solo orden (sin alternativas)");
        });

        caso("Expresión: el prefijo común queda afuera de la alternativa", 2000, () -> {
            iguales(anclar("T0(.*?)(?:T1(.*?)T2|T2(.*?)T3)"), AnalizadorDeLog.expresion(COMPARTEN_T0),
                    "expresión de T0 T1 T2 y T0 T2 T3");
            iguales(anclar(PATRON_DE_LOS_APUNTES), AnalizadorDeLog.expresion(PRODUCTOR_CONSUMIDOR),
                    "expresión del productor/consumidor (tendría que ser la de los apuntes, con los números anclados)");
            iguales(AnalizadorDeLog.expresion(MUTEX),
                    AnalizadorDeLog.expresion(List.of(new int[] {0, 1}, new int[] {2, 3}, new int[] {0, 1})),
                    "expresión con un orden repetido");
        });

        caso("Expresión: órdenes que no se pueden juntar", 2000, () -> {
            esperaExcepcion(IllegalArgumentException.class, () -> AnalizadorDeLog.expresion(List.of()), "sin órdenes");
            esperaExcepcion(IllegalArgumentException.class, () -> AnalizadorDeLog.expresion(List.of(new int[] {})), "un orden vacío");
            esperaExcepcion(IllegalArgumentException.class,
                    () -> AnalizadorDeLog.expresion(List.of(new int[] {0, 1}, new int[] {0, 1, 2})), "T0 T1 y después T0 T1 T2");
            esperaExcepcion(IllegalArgumentException.class,
                    () -> AnalizadorDeLog.expresion(List.of(new int[] {0, 1, 2}, new int[] {0, 1})), "T0 T1 T2 y después T0 T1");
        });

        caso("Borrar invariantes: la tabla de los apuntes", 2000, () -> {
            Pattern patron = Pattern.compile(PATRON_DE_LOS_APUNTES);
            resultado(1, "", AnalizadorDeLog.quitarInvariantes(patron, "T0T1T2T3T4T5"), "T0T1T2T3T4T5");
            resultado(1, "", AnalizadorDeLog.quitarInvariantes(patron, "T0T1T3T4T5T2"), "T0T1T3T4T5T2");
            resultado(2, "", AnalizadorDeLog.quitarInvariantes(patron, "T0T1T0T3T2T1T4T3T5T2T4T5"),
                    "T0T1T0T3T2T1T4T3T5T2T4T5 (dos intercalados)");
            resultado(1, "T0T1", AnalizadorDeLog.quitarInvariantes(patron, "T0T1T3T4T5T2T0T1"),
                    "T0T1T3T4T5T2T0T1 (un ciclo en curso al cortar)");
            resultado(2, "", AnalizadorDeLog.quitarInvariantes(patron, "T0T1T2T3T4T5T0T1T3T4T5T2"),
                    "dos ciclos seguidos (los dos se borran en la misma pasada: hay que contar coincidencias, no pasadas)");
            resultado(0, "", AnalizadorDeLog.quitarInvariantes(patron, ""), "el log vacío");
        });

        caso("Borrar invariantes: los huecos se copian tal cual, aunque tengan $ o \\", 2000, () -> {
            resultado(1, "x$y\\z", AnalizadorDeLog.quitarInvariantes(Pattern.compile("A(.*?)B"), "Ax$y\\zB"),
                    "A(.*?)B sobre Ax$y\\zB (¿pasaste el reemplazo por Matcher.quoteReplacement?)");
        });

        caso("El falso error: dos expresiones separadas, o una sin factorizar", 2000, () -> {
            String log = "T0T2T0T1T2T3";
            AnalizadorDeLog.Resultado primero = AnalizadorDeLog.analizar(List.of(new int[] {0, 1, 2}), log);
            resultado(1, "T2T0T3", primero, "primero el invariante T0 T1 T2, solo");
            resultado(0, "T2T0T3", AnalizadorDeLog.analizar(List.of(new int[] {0, 2, 3}), primero.resto()),
                    "después T0 T2 T3, solo, sobre lo que quedó (le robaron el T0)");
            resultado(1, "T2T0T3", AnalizadorDeLog.quitarInvariantes(
                    Pattern.compile("(?:T0(.*?)T1(.*?)T2|T0(.*?)T2(.*?)T3)"), log),
                    "una sola expresión, pero con cada orden entero como alternativa");
            resultado(2, "", AnalizadorDeLog.analizar(COMPARTEN_T0, log), "tu expresión, con el T0 factorizado");
            info("separadas o sin factorizar sobra T2T0T3, y el log era válido; con el prefijo afuera no sobra nada");
        });

        caso("Huecos codiciosos: se saltean invariantes enteros", 2000, () -> {
            String perezosa = AnalizadorDeLog.expresion(PRODUCTOR_CONSUMIDOR);
            Pattern codiciosa = Pattern.compile(perezosa.replace("(.*?)", "(.*)"));
            String log = "T0T1T0T3T2T1T4T3T5T2T4T5";
            resultado(2, "", AnalizadorDeLog.analizar(PRODUCTOR_CONSUMIDOR, log), "con huecos perezosos");
            resultado(1, "T1T0T3T2T4T5", AnalizadorDeLog.quitarInvariantes(codiciosa, log),
                    "la misma expresión con huecos codiciosos (.*)");
            Pattern mutexCodiciosa = Pattern.compile(AnalizadorDeLog.expresion(MUTEX).replace("(.*?)", "(.*)"));
            resultado(1, "T1T0", AnalizadorDeLog.quitarInvariantes(mutexCodiciosa, "T0T1T0T1"),
                    "T0T1T0T1 en la exclusión mutua, con huecos codiciosos: el primer T0 se fue hasta el último T1");
            info("codiciosa: 1 invariante y sobra T1T0T3T2T4T5, en un log con dos invariantes completos");
        });

        caso("T1 no es el comienzo de T10", 2000, () -> {
            resultado(2, "", AnalizadorDeLog.analizar(List.of(new int[] {1, 2}, new int[] {10, 11}), "T10T1T2T11"),
                    "invariantes T1 T2 y T10 T11 sobre T10T1T2T11");
            resultado(1, "T12", AnalizadorDeLog.analizar(List.of(new int[] {1, 2}), "T12T1T2"),
                    "invariante T1 T2 sobre T12T1T2 (T12 no es parte del invariante)");
            info("sin (?!\\d), T1(.*?)T2 sobre T12T1T2 deja \"2T1\": un pedazo de número suelto");
        });

        caso("Los T-invariantes no ven la exclusión mutua: hay que reproducir el log", 2000, () -> {
            for (String log : List.of("T0T2T1T3", "T0T0T1T1")) {
                resultado(2, "", AnalizadorDeLog.analizar(MUTEX, log), log + " según los T-invariantes");
                iguales(1, AnalizadorDeLog.primerDisparoImposible(redExclusionMutua(), log), log + " reproducido en la red");
            }
            info("T0T2T1T3 y T0T0T1T1 se descomponen en invariantes completos, y son imposibles para la red");
        });

        fin();
    }

    /** Agrega (?!\d) después de cada número de transición. */
    private static String anclar(String expresion) {
        return expresion.replaceAll("(T\\d+)", "$1(?!\\\\d)");
    }

    private static void resultado(int invariantes, String resto, AnalizadorDeLog.Resultado obtenido, String que) {
        verificar(obtenido != null, que + ": el resultado es null");
        iguales("(" + invariantes + ", \"" + resto + "\")",
                "(" + obtenido.invariantes() + ", \"" + obtenido.resto() + "\")", que + " (invariantes, resto)");
    }

    /** Exclusión mutua: P0 A afuera, P1 A adentro, P2 B afuera, P3 B adentro, P4 mutex (la del 7.1). */
    private static RedDePetri redExclusionMutua() {
        int[][] pre = new int[5][4];
        int[][] post = new int[5][4];
        pre[0][0] = 1;
        pre[4][0] = 1;
        post[1][0] = 1;
        pre[1][1] = 1;
        post[0][1] = 1;
        post[4][1] = 1;
        pre[2][2] = 1;
        pre[4][2] = 1;
        post[3][2] = 1;
        pre[3][3] = 1;
        post[2][3] = 1;
        post[4][3] = 1;
        return new RedDePetri(pre, post, new int[] {1, 0, 1, 0, 1});
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
