package generator;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * Punto de entrada del generador: BPMN -> tabla de flujos -> .feature + steps.
 *
 * Uso: mvn compile exec:java
 *      mvn compile exec:java -Dexec.args="ruta/al/modelo.bpmn"
 */
public class Main {

    static final String DEFAULT_BPMN = "src/main/resources/saucedemo.bpmn";
    static final Path FLOW_TABLE = Path.of("output", "flow_table.csv");
    static final Path FEATURE = Path.of("src", "test", "resources", "features", "generated.feature");
    static final Path STEPS = Path.of("src", "test", "java", "steps", "GeneratedSteps.java");
    static final int MAX_VISITS_PER_FLOW = 2;

    public static void main(String[] args) throws Exception {
        File bpmn = new File(args.length > 0 ? args[0] : DEFAULT_BPMN);
        System.out.println("Analizando " + bpmn.getPath());

        List<BpmnProcess> processes = new BpmnParser().parse(bpmn);
        if (processes.isEmpty()) {
            throw new IllegalStateException("El archivo no contiene ningun <process>");
        }
        // El modelo de SauceDemo tiene un solo proceso; si hubiera varios se usa el primero.
        BpmnProcess process = processes.get(0);

        List<PathFinder.Path> paths = new PathFinder(MAX_VISITS_PER_FLOW).findPaths(process);
        System.out.printf("Proceso '%s': %d tareas de usuario, %d caminos posibles%n%n",
                process.label(), process.userTasks().size(), paths.size());

        FlowTableWriter table = new FlowTableWriter();
        List<List<String>> rows = table.build(process, paths);
        System.out.println(table.toText(rows));
        table.writeCsv(rows, FLOW_TABLE);

        TestCodeGenerator generator = new TestCodeGenerator();
        generator.write(generator.feature(process, paths), FEATURE);
        generator.write(generator.steps(process, paths), STEPS);

        System.out.println("Generado: " + FLOW_TABLE);
        System.out.println("Generado: " + FEATURE);
        System.out.println("Generado: " + STEPS);
    }
}
