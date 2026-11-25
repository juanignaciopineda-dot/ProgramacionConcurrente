/**
 * Cuenta las funciones que dio el teatro. Lo usan varios hilos a la vez.
 *
 * <p><b>Este código lo escribió un compañero y tiene un bug.</b> Compila, parece estar sincronizado,
 * y con pocas llamadas hasta da bien. Tu tarea es encontrar el error, explicarlo (pregunta 3 del
 * README) y corregirlo sin cambiar las firmas de los métodos.
 */
public class ContadorDeFunciones {

    /** Candado: un objeto que nunca cambia de identidad. */
    private final Object llave = new Object();

    private int funciones = 0;

    /**
     * Suma una función. ¿Qué objeto es el candado en cada llamada?
     *
     * <p>Pista: {@link Integer} es <i>inmutable</i>. {@code funciones = funciones + 1} no modifica el
     * objeto: crea uno nuevo y cambia la referencia.
     */
    public void sumarFuncion() {
        synchronized (llave) {
            funciones = funciones + 1;
        }
    }

    /**
     * Devuelve cuántas funciones se contaron.
     *
     * @return la cantidad de funciones
     */
    public int getFunciones() {
        synchronized (llave) {
            return funciones;
        }
    }
}
