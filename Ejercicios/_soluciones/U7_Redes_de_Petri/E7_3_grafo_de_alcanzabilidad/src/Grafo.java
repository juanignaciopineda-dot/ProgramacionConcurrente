import java.util.List;
import java.util.Set;

/**
 * El resultado de recorrer el grafo de alcanzabilidad de una red. Ya está hecho.
 *
 * <p>Los marcados se guardan como {@code List<Integer>} (ver {@link Explorador#clave(int[])}) porque
 * un {@code int[]} no sirve de clave en un {@code Set}: dos arreglos con los mismos números son objetos
 * distintos y {@code equals} los compara por identidad.
 *
 * @param marcados        los marcados alcanzables que se encontraron
 * @param arcos           cuántos disparos se encontraron entre ellos (un arco por cada transición
 *                        sensibilizada en cada marcado explorado)
 * @param puntosMuertos   los marcados sin ninguna transición sensibilizada
 * @param caminoAlPrimero la secuencia de disparos más corta desde M0 hasta el primer punto muerto
 *                        encontrado, o una lista vacía si no hay
 * @param cota            la mayor cantidad de fichas que tuvo un lugar en algún marcado encontrado
 * @param completo        {@code false} si el recorrido se cortó por llegar al máximo de marcados
 */
public record Grafo(Set<List<Integer>> marcados, int arcos, List<List<Integer>> puntosMuertos,
        List<Integer> caminoAlPrimero, int cota, boolean completo) {

    /** @return {@code true} si hay algún marcado alcanzable sin transiciones sensibilizadas */
    public boolean tieneDeadlock() {
        return !puntosMuertos.isEmpty();
    }

    /** @return {@code true} si ningún lugar tuvo más de una ficha (la red es segura) */
    public boolean esSegura() {
        return cota <= 1;
    }
}
