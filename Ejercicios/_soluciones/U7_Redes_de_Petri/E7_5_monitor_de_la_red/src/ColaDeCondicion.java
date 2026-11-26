import java.util.concurrent.Semaphore;

/**
 * La variable de condición del ejercicio 5.3, ya resuelta: un semáforo justo que arranca en 0 y un
 * contador de cuántos duermen. Se usa con un monitor <i>signal and exit</i>: el que despierta a otro le
 * pasa el mutex en mano. Todos los métodos se llaman con el mutex del monitor tomado.
 */
public class ColaDeCondicion {

    private final Semaphore dormidos = new Semaphore(0, true);
    private int esperando = 0;

    /**
     * Se anota, suelta el mutex y duerme. Vuelve con el mutex, que le pasa quien lo despierta.
     *
     * @param mutex el mutex del monitor, tomado por el hilo que llama
     */
    public void esperar(Semaphore mutex) {
        esperando++;
        mutex.release();
        dormidos.acquireUninterruptibly();
    }

    /**
     * Si hay alguien, despierta al primero y le pasa el mutex.
     *
     * @return {@code true} si despertó a alguien (y entonces quien llama no tiene que soltar el mutex)
     */
    public boolean despertarUno() {
        if (esperando == 0) {
            return false;
        }
        esperando--;
        dormidos.release();
        return true;
    }

    /** @return cuántos hilos duermen en esta cola */
    public int esperando() {
        return esperando;
    }
}
