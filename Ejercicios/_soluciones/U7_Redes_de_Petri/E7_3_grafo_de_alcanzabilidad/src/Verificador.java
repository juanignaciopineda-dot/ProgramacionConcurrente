import java.util.Arrays;
import java.util.List;

/** Verificador automático del ejercicio 7.3. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 7.3 - Grafo de alcanzabilidad");

        caso("El ejemplo de los apuntes: m0 = (2, 0), 4 marcados, 3 arcos y 2 puntos muertos", 3000, () -> {
            RedDePetri red = new RedDePetri(matriz(2, 2, new int[] {0, 0, 1}, new int[] {0, 1, 1}, new int[] {1, 1, 1}),
                    matriz(2, 2, new int[] {1, 0, 1}, new int[] {1, 1, 1}), new int[] {2, 0});
            Grafo g = Explorador.explorar(red, 1000);
            iguales(4, g.marcados().size(), "cantidad de marcados");
            iguales(3, g.arcos(), "cantidad de arcos (disparos)");
            iguales(2, g.puntosMuertos().size(), "cantidad de puntos muertos");
            verificar(g.puntosMuertos().contains(List.of(0, 2)) && g.puntosMuertos().contains(List.of(0, 1)),
                    "los puntos muertos tendrían que ser (0, 2) y (0, 1), y son " + g.puntosMuertos());
            iguales(List.of(0, 0), g.caminoAlPrimero(), "camino más corto al primer punto muerto (T0 T0 lleva a (0, 2))");
            iguales(2, g.cota(), "cota (P1 llega a tener 2 fichas)");
            verificar(!g.esSegura(), "la red no es segura");
            verificar(g.completo(), "el recorrido tendría que estar completo");
            iguales("[2, 0]", Arrays.toString(red.getMarcado()), "marcado de la red después de explorar (no hay que"
                    + " dispararla)");
        });

        caso("Exclusión mutua: 3 marcados, 4 arcos, sin deadlock y segura", 3000, () -> {
            Grafo g = Explorador.explorar(redExclusionMutua(), 1000);
            iguales(3, g.marcados().size(), "cantidad de marcados");
            iguales(4, g.arcos(), "cantidad de arcos");
            verificar(!g.tieneDeadlock(), "la exclusión mutua no tiene puntos muertos, y se encontraron " + g.puntosMuertos());
            iguales(List.of(), g.caminoAlPrimero(), "camino al primer punto muerto (no hay)");
            verificar(g.esSegura(), "la exclusión mutua es segura");
            for (List<Integer> m : g.marcados()) {
                verificar(m.get(1) + m.get(3) <= 1, "hay un marcado con A y B adentro a la vez: " + m);
            }
        });

        caso("Buffer de capacidad 3: el P-invariante libres + ocupados = 3 vale en todos los marcados", 3000, () -> {
            RedDePetri red = new RedDePetri(matriz(2, 2, new int[] {0, 0, 1}, new int[] {1, 1, 1}),
                    matriz(2, 2, new int[] {1, 0, 1}, new int[] {0, 1, 1}), new int[] {3, 0});
            Grafo g = Explorador.explorar(red, 1000);
            iguales(4, g.marcados().size(), "cantidad de marcados (0, 1, 2 o 3 elementos)");
            iguales(3, g.cota(), "cota");
            for (List<Integer> m : g.marcados()) {
                iguales(3, m.get(0) + m.get(1), "libres + ocupados en " + m);
            }
        });

        caso("Filósofos ingenuos: hay deadlock, y el contraejemplo son los 5 tomando el izquierdo", 5000, () -> {
            Grafo g = Explorador.explorar(filosofosIngenuos(5), 100_000);
            info(g.marcados().size() + " marcados, " + g.arcos() + " arcos, " + g.puntosMuertos().size()
                    + " punto(s) muerto(s); contraejemplo: " + nombres(g.caminoAlPrimero()));
            verificar(g.completo(), "el recorrido no terminó");
            verificar(g.tieneDeadlock(), "la versión ingenua tiene un deadlock alcanzable y no se encontró");
            List<Integer> camino = g.caminoAlPrimero();
            iguales(5, camino.size(), "largo del camino más corto al deadlock (¿recorriste en anchura?)");
            for (int t : camino) {
                verificar(t % 3 == 0, "el camino al deadlock tendría que tener solo TomaIzq, y tiene " + nombres(camino));
            }
        });

        caso("Filósofos con disparo atómico: sin deadlock, segura, y dos vecinos nunca comen a la vez", 5000, () -> {
            Grafo g = Explorador.explorar(filosofosAtomicos(5), 100_000);
            info(g.marcados().size() + " marcados, " + g.arcos() + " arcos");
            verificar(g.completo(), "el recorrido no terminó");
            verificar(!g.tieneDeadlock(), "la versión con disparo atómico no tiene deadlock, y se encontró " + g.puntosMuertos());
            verificar(g.esSegura(), "la red es segura");
            for (List<Integer> m : g.marcados()) {
                for (int i = 0; i < 5; i++) {
                    int anterior = (i + 4) % 5;
                    iguales(1, m.get(3 * i + 2) + m.get(3 * i + 1) + m.get(3 * anterior + 1),
                            "M(Tenedor" + i + ") + M(Comiendo" + i + ") + M(Comiendo" + anterior + ") en " + m);
                }
            }
        });

        caso("Una red no acotada: el recorrido se corta en el máximo y lo avisa", 5000, () -> {
            RedDePetri fuente = new RedDePetri(new int[][] {{0}}, new int[][] {{1}}, new int[] {0});
            Grafo g = Explorador.explorar(fuente, 50);
            verificar(!g.completo(), "con una transición fuente el grafo es infinito: completo() tendría que ser false");
            iguales(50, g.marcados().size(), "marcados guardados (el máximo)");
        });

        fin();
    }

    private static int[][] matriz(int filas, int columnas, int[]... arcos) {
        int[][] m = new int[filas][columnas];
        for (int[] a : arcos) {
            m[a[0]][a[1]] = a[2];
        }
        return m;
    }

    /** Exclusión mutua: P0 A afuera, P1 A adentro, P2 B afuera, P3 B adentro, P4 mutex. */
    private static RedDePetri redExclusionMutua() {
        return new RedDePetri(
                matriz(5, 4, new int[] {0, 0, 1}, new int[] {4, 0, 1}, new int[] {1, 1, 1},
                        new int[] {2, 2, 1}, new int[] {4, 2, 1}, new int[] {3, 3, 1}),
                matriz(5, 4, new int[] {1, 0, 1}, new int[] {0, 1, 1}, new int[] {4, 1, 1},
                        new int[] {3, 2, 1}, new int[] {2, 3, 1}, new int[] {4, 3, 1}),
                new int[] {1, 0, 1, 0, 1});
    }

    /**
     * Filósofos de a un tenedor. Lugares del filósofo i: Pensando 4i, ConUno 4i+1, Comiendo 4i+2,
     * Tenedor 4i+3. Transiciones: TomaIzq 3i, TomaDer 3i+1, Suelta 3i+2.
     */
    private static RedDePetri filosofosIngenuos(int n) {
        int[][] pre = new int[4 * n][3 * n];
        int[][] post = new int[4 * n][3 * n];
        int[] m0 = new int[4 * n];
        for (int i = 0; i < n; i++) {
            int der = (i + 1) % n;
            pre[4 * i][3 * i] = 1;
            pre[4 * i + 3][3 * i] = 1;
            post[4 * i + 1][3 * i] = 1;
            pre[4 * i + 1][3 * i + 1] = 1;
            pre[4 * der + 3][3 * i + 1] = 1;
            post[4 * i + 2][3 * i + 1] = 1;
            pre[4 * i + 2][3 * i + 2] = 1;
            post[4 * i][3 * i + 2] = 1;
            post[4 * i + 3][3 * i + 2] = 1;
            post[4 * der + 3][3 * i + 2] = 1;
            m0[4 * i] = 1;
            m0[4 * i + 3] = 1;
        }
        return new RedDePetri(pre, post, m0);
    }

    /**
     * Filósofos que toman los dos tenedores a la vez. Lugares del filósofo i: Pensando 3i, Comiendo
     * 3i+1, Tenedor 3i+2. Transiciones: Toma 2i, Suelta 2i+1.
     */
    private static RedDePetri filosofosAtomicos(int n) {
        int[][] pre = new int[3 * n][2 * n];
        int[][] post = new int[3 * n][2 * n];
        int[] m0 = new int[3 * n];
        for (int i = 0; i < n; i++) {
            int der = (i + 1) % n;
            pre[3 * i][2 * i] = 1;
            pre[3 * i + 2][2 * i] = 1;
            pre[3 * der + 2][2 * i] = 1;
            post[3 * i + 1][2 * i] = 1;
            pre[3 * i + 1][2 * i + 1] = 1;
            post[3 * i][2 * i + 1] = 1;
            post[3 * i + 2][2 * i + 1] = 1;
            post[3 * der + 2][2 * i + 1] = 1;
            m0[3 * i] = 1;
            m0[3 * i + 2] = 1;
        }
        return new RedDePetri(pre, post, m0);
    }

    private static String nombres(List<Integer> camino) {
        StringBuilder sb = new StringBuilder();
        String[] tipos = {"TomaIzq", "TomaDer", "Suelta"};
        for (int t : camino) {
            sb.append(tipos[t % 3]).append(t / 3).append(' ');
        }
        return sb.toString().trim();
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
