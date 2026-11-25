/**
 * Hace que {@code n} hilos actúen en <b>ronda estricta</b>: primero el 0, después el 1, ..., después
 * el {@code n - 1}, y otra vez el 0. Con {@code n = 2} es el clásico ping-pong.
 *
 * <pre>
 *   turno:   0 → 1 → 2 → 0 → 1 → 2 → ...
 *   hilo 0:  ██ ·  ·  ██ ·  ·
 *   hilo 1:  ·  ██ ·  ·  ██ ·
 *   hilo 2:  ·  ·  ██ ·  ·  ██
 * </pre>
 *
 * <p>Cada participante, en cada ronda, hace: {@link #esperarTurno(int)} → su acción →
 * {@link #pasarTurno(int)}.
 *
 * <p>Es sincronización por condición pura: la condición de cada hilo es "{@code turno == mi id}",
 * distinta para cada uno, y todos esperan en el mismo monitor. Por eso este ejercicio deja en
 * evidencia la diferencia entre {@code notify()} y {@code notifyAll()}.
 *
 * <p>Herramientas permitidas: {@code synchronized}, {@code wait} y {@code notifyAll}. Nada de
 * {@code java.util.concurrent} ni {@code sleep}.
 */
public class Turnero {

    /** Cantidad de participantes. */
    private final int participantes;

    /** Id del participante al que le toca. Lo protege el monitor de this. */
    private int turno = 0;

    /**
     * Crea un turnero; el primer turno es del participante 0.
     *
     * @param participantes cantidad de participantes
     * @throws IllegalArgumentException si {@code participantes < 1}
     */
    public Turnero(int participantes) {
        if (participantes < 1) {
            throw new IllegalArgumentException("participantes inválidos: " + participantes);
        }
        this.participantes = participantes;
    }

    /**
     * Bloquea al hilo que llama hasta que sea el turno del participante {@code id}.
     *
     * <pre>
     *   mientras turno != id:
     *       wait()
     * </pre>
     *
     * <p>Si ya es su turno, vuelve enseguida.
     *
     * @param id el participante que quiere actuar, entre 0 y {@code participantes - 1}
     * @throws IllegalArgumentException si el id está fuera de rango
     * @throws InterruptedException si interrumpen al hilo mientras espera
     */
    public synchronized void esperarTurno(int id) throws InterruptedException {
        validar(id);
        while (turno != id) {
            wait();
        }
    }

    /**
     * Termina el turno del participante {@code id} y se lo pasa al siguiente:
     * {@code turno = (turno + 1) % participantes}, y después avisa.
     *
     * <p>¿{@code notify()} o {@code notifyAll()}? Pensalo con tres hilos: si el 0 termina y hace
     * {@code notify()}, la JVM puede despertar al 2, que ve que no es su turno y vuelve a dormir. El
     * 1, que era el que tenía que seguir, nunca se enteró: todos quedan dormidos para siempre. Con
     * {@code notifyAll()} se despiertan todos y cada uno vuelve a mirar su {@code while}.
     *
     * @param id el participante que termina su turno
     * @throws IllegalStateException si no es el turno de {@code id} (alguien quiere pasar un turno
     *     que no tiene)
     * @throws IllegalArgumentException si el id está fuera de rango
     */
    public synchronized void pasarTurno(int id) {
        validar(id);
        if (turno != id) {
            throw new IllegalStateException("el participante " + id + " quiso pasar el turno de " + turno);
        }
        turno = (turno + 1) % participantes;
        notifyAll();
    }

    /**
     * Devuelve a quién le toca ahora.
     *
     * @return el id del participante que tiene el turno
     */
    public synchronized int getTurno() {
        return turno;
    }

    /**
     * Devuelve la cantidad de participantes. Ya está hecho.
     *
     * @return la cantidad de participantes
     */
    public int getParticipantes() {
        return participantes;
    }

    private void validar(int id) {
        if (id < 0 || id >= participantes) {
            throw new IllegalArgumentException("id fuera de rango: " + id);
        }
    }
}
