package generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Lee un archivo .bpmn con la API DOM de javax.xml.
 *
 * Como en el paper, se ignoran los prefijos de namespace de las etiquetas
 * ("semantic:task", "bpmn:userTask" -> "task", "userTask"), porque cada
 * herramienta de modelado exporta con un prefijo distinto.
 */
public class BpmnParser {

    /** Etiquetas que se consideran nodos del flujo; el resto (documentation, extensionElements, DI...) se ignora. */
    private static final Set<String> FLOW_NODES = Set.of(
            "startEvent", "endEvent", "intermediateCatchEvent", "intermediateThrowEvent", "boundaryEvent",
            "task", "userTask", "manualTask", "serviceTask", "scriptTask", "sendTask", "receiveTask",
            "businessRuleTask", "callActivity", "subProcess",
            "exclusiveGateway", "inclusiveGateway", "parallelGateway", "eventBasedGateway", "complexGateway");

    public List<BpmnProcess> parse(File bpmnFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // Sin namespaces: asi getNodeName() devuelve "bpmn:task" y nosotros quitamos el prefijo.
        factory.setNamespaceAware(false);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(bpmnFile);

        List<BpmnProcess> processes = new ArrayList<>();
        for (Element processEl : childrenNamed(doc.getDocumentElement(), "process")) {
            BpmnProcess process = new BpmnProcess(processEl.getAttribute("id"), processEl.getAttribute("name"));
            for (Element el : children(processEl)) {
                String tag = localName(el);
                if (tag.equals("sequenceFlow")) {
                    process.addFlow(new BpmnProcess.SequenceFlow(el.getAttribute("id"),
                            el.getAttribute("sourceRef"), el.getAttribute("targetRef"), el.getAttribute("name")));
                } else if (FLOW_NODES.contains(tag)) {
                    // Un subProcess se trata como una sola tarea (seccion III-C del paper).
                    process.addNode(new BpmnProcess.Node(el.getAttribute("id"), tag, el.getAttribute("name")));
                }
            }
            processes.add(process);
        }
        return processes;
    }

    /** "semantic:task" -> "task" */
    static String localName(Node node) {
        String name = node.getNodeName();
        int colon = name.indexOf(':');
        return colon >= 0 ? name.substring(colon + 1) : name;
    }

    private static List<Element> children(Element parent) {
        List<Element> result = new ArrayList<>();
        NodeList list = parent.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i) instanceof Element el) {
                result.add(el);
            }
        }
        return result;
    }

    private static List<Element> childrenNamed(Element parent, String localName) {
        return children(parent).stream().filter(e -> localName(e).equals(localName)).toList();
    }
}
