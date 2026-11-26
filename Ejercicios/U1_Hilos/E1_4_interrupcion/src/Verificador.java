/** Verificador automático del ejercicio 1.4. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 1.4 - Interrupción, sleep y join");

        caso("esPrimo: casos borde y algunos primos y compuestos", 2000, () -> {
            long[] primos = {2, 3, 5, 17, 97, 7919, 1_000_000_007L};
            long[] noPrimos = {-7, 0, 1, 4, 9, 25, 91, 7917, 1_000_000_008L};
            for (long p : primos) {
                verificar(BuscadorDePrimos.esPrimo(p), p + " es primo");
            }
            for (long n : noPrimos) {
                verificar(!BuscadorDePrimos.esPrimo(n), n + " no es primo");
            }
        });

        caso("dormir: sin interrupción duerme y devuelve true", 2000, () -> {
            long inicio = System.nanoTime();
            boolean r = Interrupciones.dormir(100);
            long ms = (System.nanoTime() - inicio) / 1_000_000;
            verificar(r, "devolvió false sin que nadie lo interrumpiera");
            verificar(ms >= 95, "durmió solo " + ms + " ms");
        });

        caso("dormir: si el hilo ya está interrumpido, devuelve false enseguida y conserva la marca", 2000, () -> {
            Thread.currentThread().interrupt();
            long inicio = System.nanoTime();
            boolean r = Interrupciones.dormir(1000);
            long ms = (System.nanoTime() - inicio) / 1_000_000;
            boolean marca = Thread.interrupted(); // la lee y la borra, para no afectar lo que sigue
            verificar(!r, "devolvió true aunque el hilo estaba interrumpido");
            verificar(ms < 500, "tardó " + ms + " ms: tendría que despertarse enseguida");
            verificar(marca, "la marca de interrupción se perdió: falta Thread.currentThread().interrupt() en el catch");
        });

        caso("detener: interrumpe, espera con plazo y avisa si el hilo terminó", 3000, () -> {
            Thread dormido = new Thread(() -> Interrupciones.dormir(10_000), "dormido");
            dormido.start();
            verificar(Interrupciones.detener(dormido, 1000), "devolvió false con un hilo que sí terminó");
            Thread terco = new Thread(() -> {
                long fin = System.currentTimeMillis() + 600;
                while (System.currentTimeMillis() < fin) {
                    // ignora las interrupciones a propósito
                }
            }, "terco");
            terco.start();
            long inicio = System.nanoTime();
            boolean r = Interrupciones.detener(terco, 100);
            long ms = (System.nanoTime() - inicio) / 1_000_000;
            verificar(!r, "devolvió true con un hilo que ignora la interrupción y sigue vivo");
            verificar(ms < 400, "esperó " + ms + " ms: ¿usás join() sin plazo?");
            terco.join();
        });

        caso("Latido: late cada 50 ms y termina al ser interrumpido", 3000, () -> {
            Latido latido = new Latido(50);
            Thread t = new Thread(latido, "latido");
            t.start();
            Thread.sleep(500);
            long inicio = System.nanoTime();
            boolean termino = Interrupciones.detener(t, 1000);
            long ms = (System.nanoTime() - inicio) / 1_000_000;
            verificar(termino, "el latido no terminó después de interrumpirlo: ¿capturás la excepción adentro del bucle?");
            info("latidos en 500 ms: " + latido.getLatidos() + "; tardó " + ms + " ms en terminar");
            verificar(ms < 200, "tardó " + ms + " ms en terminar: tendría que ser inmediato");
            verificar(latido.getLatidos() >= 5 && latido.getLatidos() <= 15,
                    "latió " + latido.getLatidos() + " veces en 500 ms (se esperaban unas 10)");
        });

        caso("BuscadorDePrimos: calcula sin parar y termina al ser interrumpido", 3000, () -> {
            BuscadorDePrimos b = new BuscadorDePrimos();
            Thread t = new Thread(b, "buscador");
            t.start();
            Thread.sleep(300);
            verificar(Interrupciones.detener(t, 1000),
                    "el buscador no terminó: ¿consultás isInterrupted() en cada vuelta?");
            info("revisó " + b.getRevisados() + " números; último primo: " + b.getUltimoPrimo());
            verificar(b.getRevisados() > 1000, "revisó solo " + b.getRevisados() + " números en 300 ms");
            verificar(BuscadorDePrimos.esPrimo(b.getUltimoPrimo()), b.getUltimoPrimo() + " no es primo");
        });

        caso("BuscadorDePrimos: si el hilo ya está interrumpido, no revisa nada", 2000, () -> {
            BuscadorDePrimos b = new BuscadorDePrimos();
            Thread.currentThread().interrupt();
            b.run();
            boolean marca = Thread.interrupted();
            iguales(0L, b.getRevisados(), "números revisados");
            verificar(marca, "la marca de interrupción se borró: ¿usaste Thread.interrupted() en vez de isInterrupted()?");
        });

        caso("Cadena: un bucle while (!isInterrupted()) { dormir(20); } se puede detener", 3000, () -> {
            Thread t = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    Interrupciones.dormir(20);
                }
            }, "cadena");
            t.start();
            Thread.sleep(100);
            verificar(Interrupciones.detener(t, 500),
                    "el bucle no terminó: dormir() se está tragando la interrupción");
        });

        fin();
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
