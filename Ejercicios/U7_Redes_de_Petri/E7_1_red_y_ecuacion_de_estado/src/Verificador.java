import java.util.Arrays;

/** Verificador automático del ejercicio 7.1. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 7.1 - La red y la ecuación de estado");

        caso("Constructor: valida dimensiones y negativos, y guarda copias", 2000, () -> {
            esperaExcepcion(IllegalArgumentException.class, () -> new RedDePetri(null, new int[1][1], new int[1]), "pre null");
            esperaExcepcion(IllegalArgumentException.class,
                    () -> new RedDePetri(new int[2][1], new int[1][1], new int[2]), "post con otra cantidad de lugares");
            esperaExcepcion(IllegalArgumentException.class,
                    () -> new RedDePetri(new int[1][1], new int[1][1], new int[] {-1}), "marcado negativo");
            int[][] pre = {{1}};
            int[][] post = {{0}};
            int[] m0 = {1};
            RedDePetri red = new RedDePetri(pre, post, m0);
            pre[0][0] = 5;
            m0[0] = 7;
            iguales("[1]", Arrays.toString(red.getMarcado()), "marcado después de cambiar el arreglo original (¿guardaste copias?)");
            iguales("[[-1]]", Arrays.deepToString(red.getIncidencia()), "incidencia después de cambiar pre (¿guardaste copias?)");
        });

        caso("La red del disparo: incidencia, sensibilizado y disparo", 2000, () -> {
            RedDePetri red = redDelDisparo();
            iguales("[[-1], [-2], [1]]", Arrays.deepToString(red.getIncidencia()), "matriz de incidencia C = Post - Pre");
            verificar(red.estaSensibilizada(0), "con M = (1, 2, 0), T0 tendría que estar sensibilizada");
            red.disparar(0);
            iguales("[0, 0, 1]", Arrays.toString(red.getMarcado()), "marcado después de disparar T0 (M' = M + C(·, T0))");
            verificar(!red.estaSensibilizada(0), "con M = (0, 0, 1), T0 no puede estar sensibilizada");
            esperaExcepcion(IllegalStateException.class, () -> red.disparar(0), "disparar una transición no sensibilizada");
            iguales("[0, 0, 1]", Arrays.toString(red.getMarcado()), "marcado después del disparo rechazado");
            esperaExcepcion(IllegalArgumentException.class, () -> red.estaSensibilizada(1), "transición inexistente");
        });

        caso("Los pesos de los arcos cuentan: con una ficha, un arco de peso 2 no alcanza", 2000, () -> {
            RedDePetri red = new RedDePetri(new int[][] {{1}, {2}, {0}}, new int[][] {{0}, {0}, {1}}, new int[] {1, 1, 0});
            verificar(!red.estaSensibilizada(0), "con 1 ficha en P1 y un arco de peso 2, T0 no está sensibilizada");
        });

        caso("Exclusión mutua: con A en la sección crítica, B no puede entrar", 2000, () -> {
            RedDePetri red = redExclusionMutua();
            iguales("[true, false, true, false]", Arrays.toString(red.sensibilizadas()), "sensibilizadas en M0");
            red.disparar(0);
            iguales("[0, 1, 1, 0, 0]", Arrays.toString(red.getMarcado()), "marcado con A adentro");
            iguales("[false, true, false, false]", Arrays.toString(red.sensibilizadas()), "sensibilizadas con A adentro");
            red.disparar(1);
            red.disparar(2);
            iguales("[1, 0, 0, 1, 0]", Arrays.toString(red.getMarcado()), "marcado con B adentro");
        });

        caso("Auto-lazo: el sensibilizado se decide con Pre, no con la incidencia", 2000, () -> {
            RedDePetri vacia = redConAutoLazo(0);
            iguales("[[0], [1]]", Arrays.deepToString(vacia.getIncidencia()), "incidencia (el auto-lazo se cancela)");
            verificar(!vacia.estaSensibilizada(0), "T0 necesita una ficha en P0 (es un auto-lazo) y P0 está vacío;"
                    + " como C(P0, T0) = 0, M + C(·, T0) no es negativo, pero eso no alcanza: hay que mirar Pre");
            RedDePetri llena = redConAutoLazo(1);
            verificar(llena.estaSensibilizada(0), "con una ficha en P0, T0 tendría que estar sensibilizada");
            llena.disparar(0);
            iguales("[1, 1]", Arrays.toString(llena.getMarcado()), "marcado después de disparar (la ficha de P0 vuelve)");
        });

        caso("Ecuación de estado: M + C·s, sin tocar la red", 2000, () -> {
            RedDePetri red = redExclusionMutua();
            iguales("[1, 0, 1, 0, 1]", Arrays.toString(red.ecuacionDeEstado(new int[] {1, 1, 1, 1})),
                    "M0 + C·(1, 1, 1, 1): el ciclo completo de A y de B vuelve a M0");
            iguales("[0, 1, 1, 0, 0]", Arrays.toString(red.ecuacionDeEstado(new int[] {1, 0, 0, 0})),
                    "M0 + C·(1, 0, 0, 0)");
            iguales("[2, -1, 1, 0, 2]", Arrays.toString(red.ecuacionDeEstado(new int[] {0, 1, 0, 0})),
                    "M0 + C·(0, 1, 0, 0): la cuenta da negativa porque no existe esa secuencia, y la ecuación no lo"
                            + " controla");
            iguales("[1, 0, 1, 0, 1]", Arrays.toString(red.getMarcado()), "marcado de la red (la ecuación no dispara nada)");
            esperaExcepcion(IllegalArgumentException.class, () -> red.ecuacionDeEstado(new int[] {1, 1}), "vector corto");
        });

        caso("Dos secuencias con el mismo vector característico llegan al mismo marcado", 2000, () -> {
            RedDePetri a = redExclusionMutua();
            for (int t : new int[] {0, 1, 2, 3, 0}) {
                a.disparar(t);
            }
            RedDePetri b = redExclusionMutua();
            for (int t : new int[] {2, 3, 0, 1, 0}) {
                b.disparar(t);
            }
            iguales(Arrays.toString(a.getMarcado()), Arrays.toString(b.getMarcado()), "marcado de las dos secuencias");
            iguales(Arrays.toString(redExclusionMutua().ecuacionDeEstado(new int[] {2, 1, 1, 1})),
                    Arrays.toString(a.getMarcado()), "marcado según la ecuación de estado");
        });

        caso("Conflictos estructurales: A y B compiten por el mutex", 2000, () -> {
            boolean[][] c = redExclusionMutua().conflictosEstructurales();
            verificar(c[0][2] && c[2][0], "T0 y T2 comparten el lugar mutex: tendrían que estar en conflicto");
            verificar(!c[1][3] && !c[3][1], "T1 y T3 no comparten lugares de entrada");
            verificar(!c[0][1], "T0 y T1 no comparten lugares de entrada (T1 devuelve el mutex, no lo toma)");
            for (int t = 0; t < 4; t++) {
                verificar(!c[t][t], "la diagonal tiene que ser false (T" + t + " consigo misma)");
            }
        });

        fin();
    }

    /** Arma una matriz {@code filas x columnas} a partir de ternas {lugar, transición, peso}. */
    private static int[][] matriz(int filas, int columnas, int[]... arcos) {
        int[][] m = new int[filas][columnas];
        for (int[] a : arcos) {
            m[a[0]][a[1]] = a[2];
        }
        return m;
    }

    /** La red del disparo de los apuntes: Pre(P0, T0) = 1, Pre(P1, T0) = 2, Post(P2, T0) = 1. */
    private static RedDePetri redDelDisparo() {
        return new RedDePetri(matriz(3, 1, new int[] {0, 0, 1}, new int[] {1, 0, 2}),
                matriz(3, 1, new int[] {2, 0, 1}), new int[] {1, 2, 0});
    }

    /** Exclusión mutua: P0 A afuera, P1 A adentro, P2 B afuera, P3 B adentro, P4 mutex. */
    static RedDePetri redExclusionMutua() {
        int[][] pre = matriz(5, 4, new int[] {0, 0, 1}, new int[] {4, 0, 1}, new int[] {1, 1, 1},
                new int[] {2, 2, 1}, new int[] {4, 2, 1}, new int[] {3, 3, 1});
        int[][] post = matriz(5, 4, new int[] {1, 0, 1}, new int[] {0, 1, 1}, new int[] {4, 1, 1},
                new int[] {3, 2, 1}, new int[] {2, 3, 1}, new int[] {4, 3, 1});
        return new RedDePetri(pre, post, new int[] {1, 0, 1, 0, 1});
    }

    /** P0 es entrada y salida de T0 (auto-lazo), y T0 deposita además en P1. */
    private static RedDePetri redConAutoLazo(int fichasEnP0) {
        return new RedDePetri(matriz(2, 1, new int[] {0, 0, 1}), matriz(2, 1, new int[] {0, 0, 1}, new int[] {1, 0, 1}),
                new int[] {fichasEnP0, 0});
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
