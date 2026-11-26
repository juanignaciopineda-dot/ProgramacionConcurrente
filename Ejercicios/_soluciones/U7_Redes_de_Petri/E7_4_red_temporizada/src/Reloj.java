/**
 * De dónde saca la hora la {@link RedTemporizada}. Ya está hecho.
 *
 * <p>Que sea una interfaz permite probar la red sin esperar de verdad. El verificador usa un reloj
 * falso que avanza cuando él quiere, y así los casos son exactos y no dependen de lo cargada que esté
 * la máquina. En el programa real se usa {@link #DEL_SISTEMA}.
 */
public interface Reloj {

    /** El reloj real. Usa {@code System.nanoTime()}, que no salta si se cambia la hora de la máquina. */
    Reloj DEL_SISTEMA = () -> System.nanoTime() / 1_000_000;

    /** @return el instante actual, en milisegundos (solo sirven las diferencias entre dos instantes) */
    long ahoraMs();
}
