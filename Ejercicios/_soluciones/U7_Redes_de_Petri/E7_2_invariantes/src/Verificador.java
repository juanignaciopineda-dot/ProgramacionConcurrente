import java.util.Arrays;

/** Verificador automático del ejercicio 7.2. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 7.2 - Invariantes de lugar y de transición");

        caso("esPInvariante en la red de exclusión mutua", 2000, () -> {
            int[][] c = redExclusionMutua().getIncidencia();
            verificar(Invariantes.esPInvariante(c, new int[] {0, 1, 0, 1, 1}),
                    "M(A adentro) + M(B adentro) + M(mutex) tendría que ser un P-invariante");
            verificar(Invariantes.esPInvariante(c, new int[] {1, 1, 0, 0, 0}), "M(A afuera) + M(A adentro) tendría que serlo");
            verificar(Invariantes.esPInvariante(c, new int[] {0, 0, 1, 1, 0}), "M(B afuera) + M(B adentro) tendría que serlo");
            verificar(!Invariantes.esPInvariante(c, new int[] {1, 0, 0, 0, 0}), "M(A afuera) sola no es un P-invariante");
            verificar(!Invariantes.esPInvariante(c, new int[] {1, 0, 1, 0, 1}), "(1, 0, 1, 0, 1) no es un P-invariante");
            esperaExcepcion(IllegalArgumentException.class, () -> Invariantes.esPInvariante(c, new int[] {1, 1}), "vector corto");
        });

        caso("esTInvariante, valor y vectorCaracteristico", 2000, () -> {
            int[][] c = redExclusionMutua().getIncidencia();
            verificar(Invariantes.esTInvariante(c, new int[] {1, 1, 0, 0}), "(1, 1, 0, 0): A entra y sale");
            verificar(Invariantes.esTInvariante(c, new int[] {1, 1, 1, 1}), "(1, 1, 1, 1): entran y salen los dos");
            verificar(!Invariantes.esTInvariante(c, new int[] {1, 0, 0, 0}), "(1, 0, 0, 0) no es un T-invariante");
            verificar(!Invariantes.esTInvariante(c, new int[] {2, 1, 0, 0}), "(2, 1, 0, 0) no es un T-invariante");
            verificar(!Invariantes.esTInvariante(new int[][] {{1}}, new int[] {1}), "una transición fuente sola no es"
                    + " un T-invariante: C·x = (1), que no es 0 aunque no sea negativo");
            iguales(1, Invariantes.valor(new int[] {0, 1, 0, 1, 1}, new int[] {1, 0, 1, 0, 1}), "valor del invariante del mutex en M0");
            iguales("[2, 2, 1, 0]", Arrays.toString(Invariantes.vectorCaracteristico(new int[] {0, 1, 0, 1, 2}, 4)),
                    "vector característico de T0 T1 T0 T1 T2");
            esperaExcepcion(IllegalArgumentException.class, () -> Invariantes.vectorCaracteristico(new int[] {4}, 4),
                    "secuencia con una transición que no existe");
        });

        caso("esRepetitiva: dispara la secuencia y dice si volvió al marcado inicial", 2000, () -> {
            verificar(Invariantes.esRepetitiva(redExclusionMutua(), new int[] {0, 1}), "T0 T1 vuelve a M0");
            verificar(Invariantes.esRepetitiva(redExclusionMutua(), new int[] {0, 1, 2, 3}), "T0 T1 T2 T3 vuelve a M0");
            verificar(!Invariantes.esRepetitiva(redExclusionMutua(), new int[] {0}), "T0 solo no vuelve a M0");
            esperaExcepcion(IllegalStateException.class, () -> Invariantes.esRepetitiva(redExclusionMutua(), new int[] {1}),
                    "T1 no está sensibilizada en M0: la secuencia no se puede disparar");
        });

        caso("La barbería de los apuntes: los seis P-invariantes y un T-invariante", 2000, () -> {
            RedDePetri barberia = barberia();
            int[][] c = barberia.getIncidencia();
            for (int k = 0; k < INVARIANTES_BARBERIA.length; k++) {
                verificar(Invariantes.esPInvariante(c, INVARIANTES_BARBERIA[k]), "el invariante " + NOMBRES[k]
                        + " de los apuntes tendría que ser un P-invariante");
            }
            int[] sinP13 = INVARIANTES_BARBERIA[2].clone();
            sinP13[13] = 0;
            verificar(!Invariantes.esPInvariante(c, sinP13), "el de los peluqueros sin P13 (los que vuelven de la caja)"
                    + " no es un P-invariante");
            int[] cicloDelPeluquero1 = {1, 1, 1, 0, 1, 0, 1, 1, 1, 0};
            verificar(Invariantes.esTInvariante(c, cicloDelPeluquero1), "T0 T1 T2 T4 T6 T7 T8 (un cliente atendido por el"
                    + " peluquero 1) tendría que ser un T-invariante");
            int[] secuencia = {4, 6, 7, 8, 0, 1, 2};
            iguales(Arrays.toString(cicloDelPeluquero1),
                    Arrays.toString(Invariantes.vectorCaracteristico(secuencia, 10)), "vector característico de la secuencia");
            verificar(Invariantes.esRepetitiva(barberia, secuencia), "la secuencia T4 T6 T7 T8 T0 T1 T2 tendría que volver"
                    + " al marcado inicial");
        });

        caso("RedVigilada: rechaza vectores que no son P-invariantes y calcula las constantes", 2000, () -> {
            esperaExcepcion(IllegalArgumentException.class,
                    () -> new RedVigilada(redExclusionMutua(), new int[][] {{0, 1, 0, 1, 1}, {1, 0, 0, 0, 0}}),
                    "la fila 1 no es un P-invariante");
            RedVigilada mutex = new RedVigilada(redExclusionMutua(),
                    new int[][] {{0, 1, 0, 1, 1}, {1, 1, 0, 0, 0}, {0, 0, 1, 1, 0}});
            iguales("[1, 1, 1]", Arrays.toString(mutex.getConstantes()), "constantes en la exclusión mutua");
            for (int t : new int[] {0, 1, 2, 3, 2, 3, 0}) {
                mutex.disparar(t);
            }
            RedVigilada b = new RedVigilada(barberia(), INVARIANTES_BARBERIA);
            iguales("[1, 5, 2, 1, 1, 19]", Arrays.toString(b.getConstantes()), "constantes de la barbería (los números"
                    + " de los apuntes)");
            for (int t : new int[] {4, 6, 7, 8, 0, 1, 2, 5, 6, 7, 9}) {
                b.disparar(t);
            }
        });

        caso("RedVigilada: si el marcado se corrompe, el disparo lo detecta enseguida", 2000, () -> {
            RedDePetri rota = new RedDePetri(PRE_MUTEX, POST_MUTEX, new int[] {1, 0, 1, 0, 1}) {
                private boolean disparada = false;

                @Override
                public void disparar(int t) {
                    super.disparar(t);
                    disparada = true;
                }

                @Override
                public int[] getMarcado() {
                    int[] m = super.getMarcado();
                    if (disparada) {
                        m[4]++;
                    }
                    return m;
                }
            };
            RedVigilada vigilada = new RedVigilada(rota, new int[][] {{0, 1, 0, 1, 1}});
            esperaExcepcion(IllegalStateException.class, () -> vigilada.disparar(0), "disparar T0 (que está"
                    + " sensibilizada) en una red cuyo marcado aparece con una ficha de más en el mutex");
        });

        fin();
    }

    private static final String[] NOMBRES = {"de la puerta de entrada", "de los bancos", "de los peluqueros",
        "de la caja", "de la puerta de salida", "de los clientes"};

    /** Los seis P-invariantes de la barbería, como están en los apuntes. */
    private static final int[][] INVARIANTES_BARBERIA = {
        lugares(14, 12, 1),
        lugares(14, 3, 1, 2),
        lugares(14, 4, 6, 7, 5, 8, 13),
        lugares(14, 9, 8),
        lugares(14, 11, 10),
        lugares(14, 0, 1, 2, 7, 5, 8, 10),
    };

    /** Un vector de {@code n} componentes con un 1 en cada lugar indicado. */
    private static int[] lugares(int n, int... indices) {
        int[] v = new int[n];
        for (int i : indices) {
            v[i] = 1;
        }
        return v;
    }

    private static int[][] matriz(int filas, int columnas, int[]... arcos) {
        int[][] m = new int[filas][columnas];
        for (int[] a : arcos) {
            m[a[0]][a[1]] = a[2];
        }
        return m;
    }

    private static final int[][] PRE_MUTEX = matriz(5, 4, new int[] {0, 0, 1}, new int[] {4, 0, 1},
            new int[] {1, 1, 1}, new int[] {2, 2, 1}, new int[] {4, 2, 1}, new int[] {3, 3, 1});
    private static final int[][] POST_MUTEX = matriz(5, 4, new int[] {1, 0, 1}, new int[] {0, 1, 1},
            new int[] {4, 1, 1}, new int[] {3, 2, 1}, new int[] {2, 3, 1}, new int[] {4, 3, 1});

    /** Exclusión mutua: P0 A afuera, P1 A adentro, P2 B afuera, P3 B adentro, P4 mutex. */
    private static RedDePetri redExclusionMutua() {
        return new RedDePetri(PRE_MUTEX, POST_MUTEX, new int[] {1, 0, 1, 0, 1});
    }

    /** La barbería de los apuntes (14 lugares, 10 transiciones), sin los arcos inhibidores. */
    private static RedDePetri barberia() {
        int[][][] entradasYSalidas = {
            {{0, 12, 3}, {1}},
            {{1}, {2, 12}},
            {{2, 4}, {7, 3}},
            {{2, 6}, {5, 3}},
            {{7, 9}, {8}},
            {{5, 9}, {8}},
            {{8, 11}, {10, 9, 13}},
            {{10}, {0, 11}},
            {{13}, {4}},
            {{13}, {6}},
        };
        int[][] pre = new int[14][10];
        int[][] post = new int[14][10];
        for (int t = 0; t < 10; t++) {
            for (int p : entradasYSalidas[t][0]) {
                pre[p][t] = 1;
            }
            for (int p : entradasYSalidas[t][1]) {
                post[p][t] = 1;
            }
        }
        int[] m0 = new int[14];
        m0[0] = 17;
        m0[12] = 1;
        m0[3] = 5;
        m0[7] = 1;
        m0[5] = 1;
        m0[9] = 1;
        m0[11] = 1;
        return new RedDePetri(pre, post, m0);
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
