import java.util.ArrayList;
import java.util.List;

/**
 * Formas de crear y lanzar hilos, y la trampa clásica de llamar a {@code run()} en lugar de
 * {@code start()}.
 *
 * <p>Es una clase utilitaria: solo tiene métodos {@code static} y no se instancia.
 */
public final class Lanzador {

    private Lanzador() {}

    /**
     * Crea un hilo cuya tarea está escrita como una <b>lambda</b>: la tercera forma de crear un hilo.
     * {@link Runnable} tiene un único método abstracto, así que es una interfaz funcional y se puede
     * escribir en línea: {@code new Thread(() -> { ... }, nombre)}.
     *
     * <p>Cuando el hilo corra, tiene que escribir <b>su propio nombre</b> en {@code buzon[0]}
     * (obtenelo con {@code Thread.currentThread().getName()}, no uses el parámetro {@code nombre}).
     *
     * <p>Devolvelo <b>sin arrancar</b>: quien lo recibe decide cuándo llamar a {@code start()}.
     *
     * @param nombre nombre que tiene que tener el hilo
     * @param buzon arreglo de una posición donde el hilo deja su nombre al correr
     * @return el hilo nuevo, en estado {@link Thread.State#NEW}
     */
    public static Thread crearConLambda(String nombre, String[] buzon) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Lanzador.crearConLambda()");
    }

    /**
     * Ejecuta todas las tareas <b>a la vez</b>, cada una en un hilo nuevo, y espera a que terminen.
     *
     * <p>Pasos:
     *
     * <ol>
     *   <li>Anotá el instante de inicio con {@link System#nanoTime()}.
     *   <li>Por cada tarea, creá un {@link Thread} que la ejecute, con nombre {@code "hilo-1"},
     *       {@code "hilo-2"}, ... (en el orden de la lista). El constructor
     *       {@link Thread#Thread(Runnable, String)} recibe la tarea y el nombre.
     *   <li>Arrancalos <b>a todos</b> con {@link Thread#start()}.
     *   <li>Recién después, en otro bucle, esperá a cada uno con {@link Thread#join()}.
     *   <li>Devolvé cuántos milisegundos pasaron desde el inicio.
     * </ol>
     *
     * <p><b>Ojo:</b> si hacés {@code start()} y {@code join()} dentro del mismo bucle, cada hilo
     * arranca recién cuando terminó el anterior: el programa queda secuencial aunque use hilos.
     *
     * @param tareas tareas a ejecutar (no se modifica la lista)
     * @return milisegundos que tardó en total
     * @throws InterruptedException si el hilo que llama es interrumpido mientras espera en un
     *     {@code join()}
     */
    public static long ejecutarConStart(List<Tarea> tareas) throws InterruptedException {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Lanzador.ejecutarConStart()");
    }

    /**
     * Hace lo mismo que {@link #ejecutarConStart(List)}, pero <b>a propósito mal</b>: en lugar de
     * {@code start()}, llama a {@link Thread#run()} sobre cada hilo.
     *
     * <p>Copiá {@link #ejecutarConStart(List)} y cambiá solo esa llamada. Después corré el verificador
     * y fijate dos cosas: qué hilo ejecutó cada tarea y cuánto tardó todo. La explicación está en el
     * README.
     *
     * @param tareas tareas a ejecutar (no se modifica la lista)
     * @return milisegundos que tardó en total
     * @throws InterruptedException si el hilo que llama es interrumpido mientras espera
     */
    public static long ejecutarConRun(List<Tarea> tareas) throws InterruptedException {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("Lanzador.ejecutarConRun()");
    }
}
