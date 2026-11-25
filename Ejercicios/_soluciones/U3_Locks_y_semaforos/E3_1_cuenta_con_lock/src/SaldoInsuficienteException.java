/**
 * Se lanza cuando se quiere extraer más dinero del que hay en la cuenta. Ya está hecha.
 */
public class SaldoInsuficienteException extends Exception {

    /** Saldo que había cuando se rechazó la extracción. */
    private final long saldo;

    /** Monto que se quiso extraer. */
    private final long pedido;

    /**
     * Crea la excepción.
     *
     * @param saldo  saldo que había
     * @param pedido monto que se quiso extraer
     */
    public SaldoInsuficienteException(long saldo, long pedido) {
        super("saldo insuficiente: hay " + saldo + " y se pidió " + pedido);
        this.saldo = saldo;
        this.pedido = pedido;
    }

    /** @return el saldo que había cuando se rechazó la extracción */
    public long getSaldo() {
        return saldo;
    }

    /** @return el monto que se quiso extraer */
    public long getPedido() {
        return pedido;
    }
}
