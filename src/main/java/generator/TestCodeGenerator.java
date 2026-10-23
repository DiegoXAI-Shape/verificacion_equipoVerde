package generator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Genera el codigo inicial de las pruebas (seccion III-B, Fig. 4 y Fig. 5 del paper):
 * un Scenario de Gherkin por cada camino y una clase de steps con metodos vacios
 * que despues se completan con el codigo capturado con Selenium.
 */
public class TestCodeGenerator {

    /** Tag que lleva el feature generado; el runner lo excluye por defecto porque sus steps estan vacios. */
    public static final String TAG = "@generated";

    private static String givenText(BpmnProcess.Node task) {
        return "I am on the task " + task.label();
    }

    private static String thenText(BpmnProcess.Node task) {
        return "the task " + task.label() + " is completed";
    }

    public String feature(BpmnProcess process, List<PathFinder.Path> paths) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Archivo generado por generator.Main a partir del modelo BPMN. No editar a mano.\n");
        sb.append(TAG).append("\n");
        sb.append("Feature: Testing BPM Processes - ").append(process.label()).append("\n");
        for (int i = 0; i < paths.size(); i++) {
            PathFinder.Path path = paths.get(i);
            sb.append("\n  # F").append(i + 1).append(": ")
                    .append(String.join(" -> ", path.nodes().stream().map(BpmnProcess.Node::id).toList()))
                    .append("\n");
            sb.append("  Scenario: ").append(process.label()).append(" ").append(i)
                    .append(" (").append(path.endEvent().label()).append(")\n");
            boolean first = true;
            for (BpmnProcess.Node task : path.userTasks()) {
                sb.append("    ").append(first ? "Given " : "When ").append(givenText(task)).append("\n");
                sb.append("    Then ").append(thenText(task)).append("\n");
                first = false;
            }
        }
        return sb.toString();
    }

    public String steps(BpmnProcess process, List<PathFinder.Path> paths) {
        // Un metodo por texto de step distinto (Cucumber no permite dos definiciones con el mismo texto).
        Map<String, String> stepToKeyword = new LinkedHashMap<>();
        for (PathFinder.Path path : paths) {
            boolean first = true;
            for (BpmnProcess.Node task : path.userTasks()) {
                stepToKeyword.putIfAbsent(givenText(task), first ? "Given" : "When");
                stepToKeyword.putIfAbsent(thenText(task), "Then");
                first = false;
            }
        }

        List<String> methods = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, String> step : stepToKeyword.entrySet()) {
            methods.add("""
                        @%s("%s")
                        public void method%d() throws Exception {
                            // TODO: completar con las acciones capturadas con Selenium
                        }
                    """.formatted(step.getValue(), javaString("^" + regexQuote(step.getKey()) + "$"), i++));
        }

        return """
                // Archivo generado por generator.Main a partir del modelo BPMN. No editar a mano.
                package steps;

                import io.cucumber.java.After;
                import io.cucumber.java.Before;
                import io.cucumber.java.en.Given;
                import io.cucumber.java.en.Then;
                import io.cucumber.java.en.When;

                public class GeneratedSteps {

                    @Before("%1$s")
                    public void beforeScenario() {
                    }

                    @After("%1$s")
                    public void afterScenario() {
                    }

                    // Methods for process %2$s
                %3$s}
                """.formatted(TAG, process.label(), String.join("\n", methods));
    }

    public void write(String content, Path file) throws IOException {
        Files.createDirectories(file.toAbsolutePath().getParent());
        Files.writeString(file, content, StandardCharsets.UTF_8);
    }

    /** Escapa los metacaracteres de expresion regular para que el texto del step se tome literal. */
    private static String regexQuote(String text) {
        return text.replaceAll("([\\\\^$.|?*+()\\[\\]{}])", "\\\\$1");
    }

    private static String javaString(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
