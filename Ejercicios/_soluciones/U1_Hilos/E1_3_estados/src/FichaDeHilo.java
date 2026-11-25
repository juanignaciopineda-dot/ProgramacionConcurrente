/**
 * Atributos de un hilo: nombre, identificador, prioridad, si es daemon y estado.
 *
 * <p>Clase utilitaria: solo métodos {@code static}.
 */
public final class FichaDeHilo {

    private FichaDeHilo() {}

    /**
     * Describe los atributos principales de un hilo en una línea, con este formato <b>exacto</b>:
     *
     * <pre>nombre=worker-1 id=23 prioridad=5 daemon=false estado=RUNNABLE</pre>
     *
     * <p>Los métodos que necesitás son {@link Thread#getName()}, {@link Thread#threadId()} (en Java
     * anteriores al 19 se llamaba {@code getId()}), {@link Thread#getPriority()},
     * {@link Thread#isDaemon()} y {@link Thread#getState()}.
     *
     * @param t el hilo a describir (puede estar en cualquier estado)
     * @return la descripción en una línea
     */
    public static String describir(Thread t) {
        return "nombre=" + t.getName()
                + " id=" + t.threadId()
                + " prioridad=" + t.getPriority()
                + " daemon=" + t.isDaemon()
                + " estado=" + t.getState();
    }

    /**
     * Crea un hilo, <b>sin arrancarlo</b>, con la tarea, el nombre y la prioridad indicados.
     *
     * <p>Antes de crear nada, validá la prioridad: tiene que estar entre {@link Thread#MIN_PRIORITY}
     * (1) y {@link Thread#MAX_PRIORITY} (10). {@link Thread#setPriority(int)} ya rechaza valores fuera
     * de rango, pero con un mensaje poco claro; acá lanzá vos la excepción, con un mensaje que diga
     * cuál es el rango válido.
     *
     * <p>La prioridad es solo una <i>sugerencia</i> para el planificador del sistema operativo: no
     * garantiza ningún orden de ejecución. Nunca uses prioridades para sincronizar.
     *
     * @param tarea lo que va a ejecutar el hilo
     * @param nombre nombre del hilo
     * @param prioridad prioridad entre 1 y 10
     * @return el hilo nuevo, en estado {@link Thread.State#NEW}
     * @throws IllegalArgumentException si la prioridad está fuera de rango
     */
    public static Thread crear(Runnable tarea, String nombre, int prioridad) {
        if (prioridad < Thread.MIN_PRIORITY || prioridad > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException("la prioridad tiene que estar entre " + Thread.MIN_PRIORITY
                    + " y " + Thread.MAX_PRIORITY + ", y vino " + prioridad);
        }
        Thread hilo = new Thread(tarea, nombre);
        hilo.setPriority(prioridad);
        return hilo;
    }
}
