import java.util.Arrays;

/** Verificador automático del ejercicio 7.4. No hace falta modificarlo. */
public class Verificador {

    private static final long INF = RedTemporizada.INFINITO;

    /** Un reloj que solo avanza cuando el verificador lo pide. */
    private static final class RelojFalso implements Reloj {
        private long ahora = 1000;

        @Override
        public long ahoraMs() {
            return ahora;
        }

        void avanzar(long ms) {
            ahora += ms;
        }
    }

    public static void main(String[] args) {
        titulo("Ejercicio 7.4 - Red de Petri con tiempo");

        caso("Constructor: un intervalo por transición y 0 <= α <= β", 2000, () -> {
            RelojFalso r = new RelojFalso();
            esperaExcepcion(IllegalArgumentException.class,
                    () -> new RedTemporizada(cadena(), new long[] {0}, new long[] {INF, INF}, r), "un α de menos");
            esperaExcepcion(IllegalArgumentException.class,
                    () -> new RedTemporizada(cadena(), new long[] {50, 0}, new long[] {10, INF}, r), "α > β");
            esperaExcepcion(IllegalArgumentException.class,
                    () -> new RedTemporizada(cadena(), new long[] {-1, 0}, new long[] {INF, INF}, r), "α negativo");
        });

        caso("Sin tiempo ([0, ∞)) se comporta como la red común", 2000, () -> {
            RelojFalso r = new RelojFalso();
            RedTemporizada red = new RedTemporizada(cadena(), new long[] {0, 0}, new long[] {INF, INF}, r);
            verificar(red.puedeDisparar(0), "T0 está sensibilizada y no tiene tiempo: se puede disparar");
            verificar(!red.puedeDisparar(1), "T1 no está sensibilizada");
            iguales(-1L, red.faltaParaDisparar(1), "faltaParaDisparar de una no sensibilizada");
            red.disparar(0);
            red.disparar(1);
            iguales("[0, 0, 1]", Arrays.toString(red.getMarcado()), "marcado final");
        });

        caso("α: antes de que pase, no se puede; después, sí", 2000, () -> {
            RelojFalso r = new RelojFalso();
            RedTemporizada red = new RedTemporizada(cadena(), new long[] {100, 0}, new long[] {INF, INF}, r);
            iguales(0L, red.tiempoSensibilizada(0), "tiempo sensibilizada al arrancar");
            r.avanzar(60);
            iguales(60L, red.tiempoSensibilizada(0), "tiempo sensibilizada después de 60 ms");
            verificar(!red.puedeDisparar(0), "a los 60 ms, con α = 100, T0 todavía no se puede disparar");
            iguales(40L, red.faltaParaDisparar(0), "lo que falta para α");
            esperaExcepcion(IllegalStateException.class, () -> red.disparar(0), "disparar antes de α");
            iguales("[1, 0, 0]", Arrays.toString(red.getMarcado()), "marcado después del disparo rechazado");
            r.avanzar(40);
            verificar(red.puedeDisparar(0), "a los 100 ms, con α = 100, T0 ya se puede disparar");
            iguales(0L, red.faltaParaDisparar(0), "lo que falta cuando ya se puede");
        });

        caso("β: después de que pasa, ya no se puede disparar", 2000, () -> {
            RelojFalso r = new RelojFalso();
            RedTemporizada red = new RedTemporizada(cadena(), new long[] {50, 0}, new long[] {200, INF}, r);
            r.avanzar(200);
            verificar(red.puedeDisparar(0), "a los 200 ms, con [50, 200], todavía se puede");
            r.avanzar(1);
            verificar(!red.puedeDisparar(0), "a los 201 ms, con [50, 200], ya no se puede");
            iguales(-1L, red.faltaParaDisparar(0), "faltaParaDisparar con la ventana vencida (esperar no sirve)");
        });

        caso("El cronómetro arranca cuando la transición se sensibiliza, no cuando se crea la red", 2000, () -> {
            RelojFalso r = new RelojFalso();
            RedTemporizada red = new RedTemporizada(cadena(), new long[] {0, 100}, new long[] {INF, INF}, r);
            iguales(-1L, red.tiempoSensibilizada(1), "T1 no está sensibilizada al arrancar");
            r.avanzar(30);
            red.disparar(0);
            iguales(0L, red.tiempoSensibilizada(1), "T1 se acaba de sensibilizar");
            r.avanzar(90);
            verificar(!red.puedeDisparar(1), "T1 lleva 90 ms sensibilizada (desde el disparo de T0), y α = 100");
            r.avanzar(10);
            verificar(red.puedeDisparar(1), "T1 lleva 100 ms sensibilizada: ya se puede");
            iguales(-1L, red.tiempoSensibilizada(0), "T0 ya no está sensibilizada");
        });

        caso("Una transición que sigue sensibilizada conserva su cronómetro cuando dispara otra", 2000, () -> {
            RelojFalso r = new RelojFalso();
            RedTemporizada red = new RedTemporizada(independientes(), new long[] {100, 0}, new long[] {INF, INF}, r);
            r.avanzar(50);
            red.disparar(1);
            iguales(50L, red.tiempoSensibilizada(0), "T0 sigue sensibilizada y no tiene que reiniciarse por el disparo"
                    + " de T1");
            r.avanzar(50);
            verificar(red.puedeDisparar(0), "T0 lleva 100 ms sensibilizada");
        });

        caso("La que se disparó y sigue sensibilizada arranca de nuevo (otras fichas)", 2000, () -> {
            RelojFalso r = new RelojFalso();
            RedDePetri dosFichas = new RedDePetri(new int[][] {{1}, {0}}, new int[][] {{0}, {1}}, new int[] {2, 0});
            RedTemporizada red = new RedTemporizada(dosFichas, new long[] {100}, new long[] {INF}, r);
            r.avanzar(100);
            red.disparar(0);
            iguales(0L, red.tiempoSensibilizada(0), "T0 sigue sensibilizada (queda una ficha), pero es una"
                    + " sensibilización nueva: su cronómetro arranca de cero");
            r.avanzar(50);
            verificar(!red.puedeDisparar(0), "a los 50 ms del primer disparo, T0 no se puede disparar de nuevo");
        });

        caso("Si deja de estar sensibilizada y vuelve, el cronómetro arranca de cero", 2000, () -> {
            RelojFalso r = new RelojFalso();
            RedTemporizada red = new RedTemporizada(conflicto(), new long[] {100, 0, 0}, new long[] {INF, INF, INF}, r);
            r.avanzar(50);
            red.disparar(1);
            iguales(-1L, red.tiempoSensibilizada(0), "T1 se llevó la ficha: T0 ya no está sensibilizada");
            r.avanzar(30);
            red.disparar(2);
            iguales(0L, red.tiempoSensibilizada(0), "la ficha volvió: T0 se sensibilizó de nuevo");
            r.avanzar(70);
            verificar(!red.puedeDisparar(0), "T0 lleva 70 ms (no 150) desde que se volvió a sensibilizar");
            r.avanzar(30);
            verificar(red.puedeDisparar(0), "T0 lleva 100 ms desde que se volvió a sensibilizar");
        });

        fin();
    }

    /** P0 → T0 → P1 → T1 → P2, con una ficha en P0. */
    private static RedDePetri cadena() {
        return new RedDePetri(new int[][] {{1, 0}, {0, 1}, {0, 0}}, new int[][] {{0, 0}, {1, 0}, {0, 1}},
                new int[] {1, 0, 0});
    }

    /** Dos ciclos sin relación: T0 mueve la ficha de P0 a P1, y T1 la de P2 a P3. */
    private static RedDePetri independientes() {
        return new RedDePetri(new int[][] {{1, 0}, {0, 0}, {0, 1}, {0, 0}}, new int[][] {{0, 0}, {1, 0}, {0, 0}, {0, 1}},
                new int[] {1, 0, 1, 0});
    }

    /** T0 y T1 compiten por la ficha de P0; T1 la pasa a P1 y T2 la devuelve a P0. */
    private static RedDePetri conflicto() {
        return new RedDePetri(new int[][] {{1, 1, 0}, {0, 0, 1}, {0, 0, 0}}, new int[][] {{0, 0, 1}, {0, 1, 0}, {1, 0, 0}},
                new int[] {1, 0, 0});
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
