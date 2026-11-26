import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Recorre el grafo de alcanzabilidad de una red: todos los marcados a los que se puede llegar desde
 * el marcado actual, y los disparos entre ellos.
 *
 * <p>Es el método <b>enumerativo</b> de análisis, y de él se leen las propiedades de comportamiento:
 *
 * <ul>
 *   <li><b>Puntos muertos</b>: marcados sin ninguna transición sensibilizada (deadlock).
 *   <li><b>Acotación</b>: la mayor cantidad de fichas que tiene un lugar. Si es 1, la red es segura.
 *   <li>Un <b>contraejemplo</b>: la secuencia de disparos que lleva a un punto muerto, que es lo que
 *       hace falta para entender por qué el modelo se traba.
 * </ul>
 *
 * <p>Con una red no acotada el grafo es infinito: por eso el recorrido tiene un máximo de marcados y,
 * si llega a él, se corta y lo informa.
 */
public final class Explorador {

    private Explorador() {
    }

    /**
     * Recorre el grafo <b>en anchura</b> desde el marcado actual de la red, sin modificarla.
     *
     * <pre>
     *   camino = mapa marcado → secuencia de disparos desde M0   (sirve además de "ya visto")
     *   camino[M0] = []
     *   pendientes = cola con M0
     *   mientras haya pendientes:
     *       m = sacar el primero
     *       actualizar la cota con el máximo de m
     *       para cada transición t sensibilizada en m:           ← red.sensibilizadaEn(m, t)
     *           arcos++
     *           siguiente = red.disparoDesde(m, t)
     *           si siguiente no está en camino:
     *               si ya hay maxMarcados: completo = false (no lo agregues)
     *               si no: camino[siguiente] = camino[m] + [t], y a la cola
     *       si m no tenía ninguna sensibilizada: es un punto muerto
     *           (si es el primero, su camino es el contraejemplo)
     *   devolver new Grafo(...)
     * </pre>
     *
     * <p>¿Por qué en anchura? Porque así cada marcado se descubre por el camino más corto, y el
     * contraejemplo es la secuencia mínima que lleva al deadlock.
     *
     * <p>Usá {@link #clave(int[])} para guardar los marcados en el mapa y en la lista de puntos
     * muertos. Para {@code marcados} del {@code Grafo}, pasá {@code new LinkedHashSet<>(camino.keySet())}.
     *
     * @param red         la red
     * @param maxMarcados cuántos marcados distintos guardar, como máximo
     * @return el grafo encontrado
     */
    public static Grafo explorar(RedDePetri red, int maxMarcados) {
        Map<List<Integer>, List<Integer>> camino = new HashMap<>();
        Deque<int[]> pendientes = new ArrayDeque<>();
        List<List<Integer>> puntosMuertos = new ArrayList<>();
        List<Integer> contraejemplo = List.of();
        int arcos = 0;
        int cota = 0;
        boolean completo = true;

        int[] inicial = red.getMarcado();
        camino.put(clave(inicial), List.of());
        pendientes.add(inicial);
        while (!pendientes.isEmpty()) {
            int[] m = pendientes.poll();
            for (int fichas : m) {
                cota = Math.max(cota, fichas);
            }
            List<Integer> hastaM = camino.get(clave(m));
            boolean alguna = false;
            for (int t = 0; t < red.transiciones(); t++) {
                if (!red.sensibilizadaEn(m, t)) {
                    continue;
                }
                alguna = true;
                arcos++;
                int[] siguiente = red.disparoDesde(m, t);
                List<Integer> clave = clave(siguiente);
                if (camino.containsKey(clave)) {
                    continue;
                }
                if (camino.size() >= maxMarcados) {
                    completo = false;
                    continue;
                }
                List<Integer> hastaSiguiente = new ArrayList<>(hastaM);
                hastaSiguiente.add(t);
                camino.put(clave, hastaSiguiente);
                pendientes.add(siguiente);
            }
            if (!alguna) {
                if (puntosMuertos.isEmpty()) {
                    contraejemplo = hastaM;
                }
                puntosMuertos.add(clave(m));
            }
        }
        return new Grafo(new LinkedHashSet<>(camino.keySet()), arcos, puntosMuertos, contraejemplo, cota, completo);
    }

    /**
     * Ya está hecho: un marcado como lista, para usarlo de clave en mapas y conjuntos.
     *
     * @param marcado un marcado
     * @return una lista con los mismos números
     */
    public static List<Integer> clave(int[] marcado) {
        List<Integer> lista = new ArrayList<>(marcado.length);
        for (int fichas : marcado) {
            lista.add(fichas);
        }
        return List.copyOf(lista);
    }
}
