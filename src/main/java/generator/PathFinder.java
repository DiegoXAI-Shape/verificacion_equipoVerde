package generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encuentra todos los caminos desde cada startEvent hasta cualquier endEvent
 * recorriendo los sequenceFlow (sourceRef -> targetRef) con una busqueda en profundidad.
 *
 * Delimitador de recursion (seccion III-C del paper): un mismo sequenceFlow solo puede
 * recorrerse {@code maxVisitsPerFlow} veces dentro de un camino; asi los ciclos terminan.
 * Los caminos que se quedan sin salida antes de llegar a un endEvent se descartan.
 */
public class PathFinder {

    /** Un camino completo: la secuencia de nodos visitados y el evento final al que llega. */
    public record Path(List<BpmnProcess.Node> nodes, BpmnProcess.Node endEvent) {
        /** Solo userTask/manualTask, en orden y con repeticiones (si el camino pasa dos veces). */
        public List<BpmnProcess.Node> userTasks() {
            return nodes.stream().filter(BpmnProcess.Node::isUserTask).toList();
        }

        public boolean contains(BpmnProcess.Node task) {
            return nodes.contains(task);
        }
    }

    private final int maxVisitsPerFlow;

    public PathFinder(int maxVisitsPerFlow) {
        this.maxVisitsPerFlow = maxVisitsPerFlow;
    }

    public List<Path> findPaths(BpmnProcess process) {
        List<Path> paths = new ArrayList<>();
        for (BpmnProcess.Node start : process.nodesOfType("startEvent")) {
            List<BpmnProcess.Node> current = new ArrayList<>();
            current.add(start);
            dfs(process, start, current, new HashMap<>(), paths);
        }
        return paths;
    }

    private void dfs(BpmnProcess process, BpmnProcess.Node node, List<BpmnProcess.Node> current,
                     Map<String, Integer> visits, List<Path> paths) {
        if (node.type().equals("endEvent")) {
            paths.add(new Path(List.copyOf(current), node));
            return;
        }
        for (BpmnProcess.SequenceFlow flow : process.outgoing(node.id())) {
            int count = visits.getOrDefault(flow.id(), 0);
            if (count >= maxVisitsPerFlow) {
                continue;
            }
            BpmnProcess.Node target = process.node(flow.targetRef());
            if (target == null) {
                System.err.println("Aviso: " + flow.id() + " apunta a un elemento desconocido: " + flow.targetRef());
                continue;
            }
            visits.put(flow.id(), count + 1);
            current.add(target);
            dfs(process, target, current, visits, paths);
            current.remove(current.size() - 1);
            visits.put(flow.id(), count);
        }
    }
}
