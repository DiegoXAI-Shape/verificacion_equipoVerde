package generator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Representacion en memoria de un proceso BPMN: nodos y sequenceFlows. */
public class BpmnProcess {

    /** Un elemento del diagrama (evento, tarea, gateway...). */
    public record Node(String id, String type, String name) {
        /** Solo las tareas que realiza una persona se vuelven columnas (seccion III-B del paper). */
        public boolean isUserTask() {
            return type.equals("userTask") || type.equals("manualTask");
        }

        public String label() {
            return name == null || name.isBlank() ? id : name.trim();
        }
    }

    /** Arista dirigida sourceRef -> targetRef. */
    public record SequenceFlow(String id, String sourceRef, String targetRef, String name) {
    }

    private final String id;
    private final String name;
    private final Map<String, Node> nodes = new LinkedHashMap<>();
    private final List<SequenceFlow> flows = new ArrayList<>();

    public BpmnProcess(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String id() {
        return id;
    }

    public String label() {
        return name == null || name.isBlank() ? id : name.trim();
    }

    public void addNode(Node node) {
        nodes.put(node.id(), node);
    }

    public void addFlow(SequenceFlow flow) {
        flows.add(flow);
    }

    public Node node(String nodeId) {
        return nodes.get(nodeId);
    }

    public List<Node> nodesOfType(String type) {
        return nodes.values().stream().filter(n -> n.type().equals(type)).toList();
    }

    /** Tareas de usuario en el orden en que aparecen en el XML (orden de las columnas). */
    public List<Node> userTasks() {
        return nodes.values().stream().filter(Node::isUserTask).toList();
    }

    public List<SequenceFlow> outgoing(String nodeId) {
        return flows.stream().filter(f -> f.sourceRef().equals(nodeId)).toList();
    }
}
