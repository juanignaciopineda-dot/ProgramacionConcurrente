import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/** Verificador automático del ejercicio 5.2. No hace falta modificarlo. */
public class Verificador {

    public static void main(String[] args) {
        titulo("Ejercicio 5.2 - Puente de una mano");

        caso("Constructor: valida y arranca vacío", 2000, () -> {
            esperaExcepcion(IllegalArgumentException.class, () -> new Puente(0), "maxSeguidos 0");
            Puente p = new Puente(3);
            iguales(0, p.enElPuente(), "autos en el puente");
            iguales(null, p.sentidoActual(), "sentido de un puente vacío");
        });

        caso("Un auto entra y sale; salir sin estar cruzando es un error", 2000, () -> {
            Puente p = new Puente(3);
            esperaExcepcion(IllegalStateException.class, () -> p.salir(Sentido.NORTE), "salir de un puente vacío");
            p.entrar(Sentido.NORTE);
            iguales(1, p.enElPuente(), "autos en el puente");
            iguales(Sentido.NORTE, p.sentidoActual(), "sentido");
            esperaExcepcion(IllegalStateException.class, () -> p.salir(Sentido.SUR),
                    "salir hacia el sur con un auto cruzando hacia el norte");
            p.salir(Sentido.NORTE);
            iguales(0, p.enElPuente(), "autos en el puente después de salir");
            iguales(null, p.sentidoActual(), "sentido después de que sale el último");
        });

        caso("Varios autos del mismo sentido cruzan a la vez", 2000, () -> {
            Puente p = new Puente(10);
            for (int i = 0; i < 3; i++) {
                p.entrar(Sentido.SUR);
            }
            iguales(3, p.enElPuente(), "autos cruzando hacia el sur a la vez");
            for (int i = 0; i < 3; i++) {
                p.salir(Sentido.SUR);
            }
        });

        caso("Un auto del otro sentido espera (WAITING) y entra cuando el puente se vacía", 3000, () -> {
            Puente p = new Puente(10);
            p.entrar(Sentido.NORTE);
            p.entrar(Sentido.NORTE);
            Thread sur = lanzar(p, Sentido.SUR, "auto-sur");
            verificar(esperarEstado(sur, Thread.State.WAITING, 1000), "el auto del sur tendría que estar esperando"
                    + " en su condición y está " + sur.getState());
            iguales(1, p.esperando(Sentido.SUR), "autos esperando hacia el sur");
            p.salir(Sentido.NORTE);
            Thread.sleep(50);
            verificar(sur.isAlive(), "el auto del sur entró con un auto todavía cruzando hacia el norte");
            p.salir(Sentido.NORTE);
            sur.join(1000);
            verificar(!sur.isAlive(), "el puente se vació y el auto del sur no entró: ¿salir() avisa a los que"
                    + " esperan?");
            iguales(Sentido.SUR, p.sentidoActual(), "sentido después del cambio");
            iguales(0, p.esperando(Sentido.SUR), "autos esperando hacia el sur después de entrar");
        });

        caso("Cuando cambia el sentido, entran todos los que esperaban del otro lado", 3000, () -> {
            Puente p = new Puente(10);
            p.entrar(Sentido.NORTE);
            List<Thread> sur = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                sur.add(lanzar(p, Sentido.SUR, "auto-sur-" + i));
            }
            for (Thread t : sur) {
                esperarEstado(t, Thread.State.WAITING, 1000);
            }
            p.salir(Sentido.NORTE);
            for (Thread t : sur) {
                t.join(1000);
            }
            iguales(3, p.enElPuente(), "autos del sur que entraron cuando se vació el puente (con signal() en vez"
                    + " de signalAll() entra uno solo)");
        });

        caso("Estrés: 5 autos de cada lado cruzan 300 veces y nunca hay autos en los dos sentidos", 20000, () -> {
            Puente p = new Puente(3);
            int[] cruzando = new int[2];
            String[] problema = {null};
            List<Thread> autos = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Sentido s = i % 2 == 0 ? Sentido.NORTE : Sentido.SUR;
                autos.add(new Thread(() -> {
                    try {
                        for (int k = 0; k < 300; k++) {
                            p.entrar(s);
                            synchronized (cruzando) {
                                cruzando[s.ordinal()]++;
                                if (cruzando[s.otro().ordinal()] > 0) {
                                    problema[0] = "hubo autos cruzando hacia el norte y hacia el sur a la vez";
                                }
                            }
                            Thread.yield();
                            synchronized (cruzando) {
                                cruzando[s.ordinal()]--;
                            }
                            p.salir(s);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }, "auto-" + s + "-" + i));
            }
            for (Thread t : autos) {
                t.start();
            }
            for (Thread t : autos) {
                t.join(15_000);
                verificar(!t.isAlive(), "el " + t.getName() + " quedó esperando (estado " + t.getState() + ")");
            }
            verificar(problema[0] == null, problema[0]);
            iguales(0, p.enElPuente(), "autos en el puente al final");
        });

        caso("Sin postergación indefinida: con autos al norte sin parar, el del sur cruza igual", 6000, () -> {
            Puente p = new Puente(3);
            AtomicBoolean seguir = new AtomicBoolean(true);
            List<Thread> norte = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                Thread t = new Thread(() -> {
                    try {
                        while (seguir.get()) {
                            p.entrar(Sentido.NORTE);
                            Thread.sleep(30);
                            p.salir(Sentido.NORTE);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }, "chorro-norte-" + i);
                norte.add(t);
                t.start();
                Thread.sleep(8);
            }
            Thread.sleep(200);
            long t0 = System.nanoTime();
            Thread sur = lanzar(p, Sentido.SUR, "auto-sur");
            sur.join(2500);
            long ms = (System.nanoTime() - t0) / 1_000_000;
            seguir.set(false);
            boolean cruzo = !sur.isAlive();
            if (cruzo) {
                p.salir(Sentido.SUR);
            }
            for (Thread t : norte) {
                t.join(2000);
            }
            verificar(cruzo, "el auto del sur esperó más de 2,5 s mientras los del norte seguían entrando:"
                    + " postergación indefinida. ¿puedeEntrar tiene en cuenta maxSeguidos?");
            info("el auto del sur esperó " + ms + " ms con un chorro de autos al norte");
        });

        prohibir(new String[] {"Puente.java"}, "synchronized", "Semaphore", ".wait(", "notify");

        fin();
    }

    /** Arranca un hilo que entra al puente en el sentido dado (y no sale). */
    private static Thread lanzar(Puente p, Sentido s, String nombre) {
        Thread t = new Thread(() -> {
            try {
                p.entrar(s);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, nombre);
        t.start();
        return t;
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
