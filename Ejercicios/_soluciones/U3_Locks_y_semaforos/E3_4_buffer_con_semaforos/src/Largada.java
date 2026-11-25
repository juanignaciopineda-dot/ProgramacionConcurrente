import java.util.concurrent.Semaphore;

/**
 * La largada de una carrera: los corredores llegan de a uno y esperan, y cuando el juez da la señal
 * salen todos juntos.
 *
 * <p>Es un semáforo usado como <b>señal</b>, no como candado: arranca sin permisos, los corredores
 * hacen {@code acquire()} y se duermen, y el juez hace {@code release(corredores)} para soltar todos
 * los permisos de golpe. Nadie devuelve lo que tomó: el que libera (el juez) no es el que adquiere
 * (cada corredor).
 *
 * <p>Un detalle que lo diferencia de {@code wait}/{@code notifyAll}: el semáforo <b>recuerda</b> la
 * señal. Si un corredor llega tarde, después de la largada, encuentra su permiso esperándolo y sale
 * sin dormir. Con {@code notifyAll()}, un aviso que llega cuando nadie espera se pierde. En la red
 * de Petri es el lugar "señal", que guarda el token hasta que alguien lo consume.
 *
 * <p>Herramientas permitidas: {@code Semaphore}. Nada de {@code synchronized},
 * {@code wait}/{@code notify}, {@code CountDownLatch} ni clases {@code Atomic...}.
 */
public class Largada {

    /** La señal de largada: un permiso por corredor, que el juez suelta todos juntos. */
    private final Semaphore senal = new Semaphore(0);

    /** Cuántos corredores largan. */
    private final int corredores;

    /**
     * Crea una largada. Ya está hecho.
     *
     * @param corredores cuántos corredores van a esperar la señal
     * @throws IllegalArgumentException si {@code corredores < 1}
     */
    public Largada(int corredores) {
        if (corredores < 1) {
            throw new IllegalArgumentException("cantidad de corredores inválida: " + corredores);
        }
        this.corredores = corredores;
    }

    /**
     * La llama cada corredor: espera hasta que el juez largue. Si ya largó, vuelve enseguida.
     * Es un {@code acquire()} sobre la señal.
     *
     * @throws InterruptedException si interrumpen al corredor mientras espera
     */
    public void esperarLargada() throws InterruptedException {
        senal.acquire();
    }

    /**
     * La llama el juez, una sola vez: suelta un permiso por corredor, todos de una vez
     * ({@code release(corredores)}).
     */
    public void largar() {
        senal.release(corredores);
    }

    /**
     * Devuelve cuántos corredores están esperando la señal ahora mismo: la cantidad de hilos en la cola
     * del semáforo ({@code getQueueLength()}). Es una estimación que sirve para mostrar o para
     * verificar, no para decidir.
     *
     * @return la cantidad de corredores esperando
     */
    public int esperando() {
        return senal.getQueueLength();
    }
}
