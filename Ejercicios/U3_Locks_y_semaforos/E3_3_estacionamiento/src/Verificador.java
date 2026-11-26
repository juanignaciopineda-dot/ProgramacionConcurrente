import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Verificador automático del ejercicio 3.3. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 3.3 - Estacionamiento con semáforos");

        caso("Constructor: valida y arranca con todos los lugares libres", 2000, () -> {
            esperaExcepcion(IllegalArgumentException.class, () -> new Estacionamiento(0), "0 lugares");
            Estacionamiento e = new Estacionamiento(3);
            iguales(3, e.capacidad(), "capacidad()");
            iguales(3, e.libres(), "libres() al arrancar");
        });

        caso("Un auto a la vez: ocupa el lugar libre de número más chico", 2000, () -> {
            Estacionamiento e = new Estacionamiento(3);
            iguales(0, e.entrar(), "lugar del primer auto");
            iguales(1, e.entrar(), "lugar del segundo auto");
            iguales(2, e.entrar(), "lugar del tercer auto");
            iguales(0, e.libres(), "libres() con el estacionamiento lleno");
            e.salir(1);
            iguales(1, e.libres(), "libres() después de que sale el auto del lugar 1");
            iguales(1, e.entrar(), "lugar del auto que entra después (el 1 quedó libre)");
        });

        caso("Salir de un lugar libre o inexistente: excepción, y el semáforo no gana permisos", 2000, () -> {
            Estacionamiento e = new Estacionamiento(2);
            int lugar = e.entrar();
            esperaExcepcion(IllegalArgumentException.class, () -> e.salir(-1), "salir(-1)");
            esperaExcepcion(IllegalArgumentException.class, () -> e.salir(2), "salir(2) con 2 lugares");
            esperaExcepcion(IllegalStateException.class, () -> e.salir(1 - lugar), "salir de un lugar libre");
            iguales(1, e.libres(), "libres() después de los intentos inválidos (un release() de más agranda el"
                    + " estacionamiento: el semáforo no sabe quién tomó cada permiso)");
            e.salir(lugar);
            esperaExcepcion(IllegalStateException.class, () -> e.salir(lugar), "salir dos veces del mismo lugar");
            iguales(2, e.libres(), "libres() al final");
        });

        caso("Con el estacionamiento lleno, el auto espera (WAITING) y entra cuando sale otro", 3000, () -> {
            Estacionamiento e = new Estacionamiento(2);
            e.entrar();
            e.entrar();
            int[] lugar = {-1};
            Thread auto = new Thread(() -> {
                try {
                    lugar[0] = e.entrar();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }, "auto");
            auto.start();
            verificar(esperarEstado(auto, Thread.State.WAITING, 1000),
                    "el auto tendría que estar esperando en el semáforo y está " + auto.getState());
            e.salir(0);
            auto.join(1000);
            verificar(!auto.isAlive(), "el auto no entró después de que se liberó un lugar");
            iguales(0, lugar[0], "lugar que le tocó al auto que esperaba");
            iguales(0, e.libres(), "libres() al final");
        });

        caso("intentarEntrar con el estacionamiento lleno se rinde y devuelve -1", 3000, () -> {
            Estacionamiento e = new Estacionamiento(1);
            iguales(0, e.intentarEntrar(100), "intentarEntrar con lugar libre");
            long t0 = System.nanoTime();
            int lugar = e.intentarEntrar(150);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            iguales(-1, lugar, "intentarEntrar con el estacionamiento lleno");
            verificar(ms >= 140, "intentarEntrar se rindió en " + ms + " ms y tendría que esperar hasta 150");
            verificar(ms < 500, "intentarEntrar tardó " + ms + " ms en rendirse");
            iguales(0, e.libres(), "libres() después del intento fallido");
        });

        caso("Interrumpir a un auto que espera: entrar() lanza InterruptedException y no se pierden lugares", 3000, () -> {
            Estacionamiento e = new Estacionamiento(1);
            int ocupado = e.entrar();
            boolean[] lanzo = {false};
            Thread auto = new Thread(() -> {
                try {
                    e.entrar();
                } catch (InterruptedException ex) {
                    lanzo[0] = true;
                }
            }, "auto");
            auto.start();
            esperarEstado(auto, Thread.State.WAITING, 1000);
            auto.interrupt();
            auto.join(1000);
            verificar(!auto.isAlive(), "el auto siguió esperando después de la interrupción");
            verificar(lanzo[0], "entrar() no lanzó InterruptedException");
            e.salir(ocupado);
            iguales(1, e.libres(), "libres() después de que sale el único auto");
        });

        caso("Estrés: 16 autos, 3 lugares, nunca más de 3 adentro ni dos en el mismo lugar (3 rondas)", 30000, () -> {
            for (int ronda = 1; ronda <= 3; ronda++) {
                estres(ronda);
            }
        });

        prohibir(new String[] {"Estacionamiento.java"}, "synchronized", ".wait(", "notify",
                "java.util.concurrent.locks", "Atomic");

        fin();
    }

    private static void estres(int ronda) throws Exception {
        Estacionamiento e = new Estacionamiento(3);
        Set<Integer> enUso = new HashSet<>();
        String[] problema = {null};
        List<Thread> autos = new ArrayList<>();
        for (int a = 0; a < 16; a++) {
            boolean paciente = a % 2 == 0;
            autos.add(new Thread(() -> {
                try {
                    for (int vuelta = 0; vuelta < 1000; vuelta++) {
                        int lugar = paciente ? e.entrar() : e.intentarEntrar(5);
                        if (lugar < 0) {
                            continue;
                        }
                        synchronized (enUso) {
                            if (!enUso.add(lugar)) {
                                problema[0] = "dos autos en el lugar " + lugar + ": ¿protegiste ocupado con el mutex?";
                            }
                            if (enUso.size() > 3) {
                                problema[0] = "hubo " + enUso.size() + " autos adentro con 3 lugares";
                            }
                        }
                        Thread.yield();
                        synchronized (enUso) {
                            enUso.remove(lugar);
                        }
                        e.salir(lugar);
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }, "auto-" + a));
        }
        for (Thread t : autos) {
            t.start();
        }
        for (Thread t : autos) {
            t.join(10_000);
            verificar(!t.isAlive(), "ronda " + ronda + ": el " + t.getName() + " quedó esperando (estado "
                    + t.getState() + ")");
        }
        verificar(problema[0] == null, "ronda " + ronda + ": " + problema[0]);
        iguales(3, e.libres(), "ronda " + ronda + ": libres() al final");
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
