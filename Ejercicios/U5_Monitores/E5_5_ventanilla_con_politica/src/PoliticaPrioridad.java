/**
 * Política de prioridad fija: si en la cola preferida hay gente, siempre elige esa; si no, la primera
 * cola con gente (la de índice más chico).
 *
 * <p>Es simple y predecible, pero tiene un costo: si a la cola preferida no dejan de llegar hilos, las
 * otras no se atienden nunca (postergación indefinida).
 */
public class PoliticaPrioridad implements Politica {

    private final int preferida;

    /**
     * Ya está hecho.
     *
     * @param preferida el índice de la cola que tiene prioridad
     */
    public PoliticaPrioridad(int preferida) {
        this.preferida = preferida;
    }

    /**
     * Elige la preferida si tiene gente; si no, la primera con gente.
     *
     * @param conGente qué colas tienen hilos esperando
     * @return el índice elegido
     */
    @Override
    public int elegir(boolean[] conGente) {
        // TODO: implementalo siguiendo el Javadoc de arriba.
        throw new UnsupportedOperationException("PoliticaPrioridad.elegir()");
    }
}
