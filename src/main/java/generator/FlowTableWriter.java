package generator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Tabla de flujos (Fig. 3 / Tabla II del paper):
 * columnas = tareas de usuario, filas = caminos, "X" si la tarea esta en el camino, "-" si no.
 */
public class FlowTableWriter {

    public List<List<String>> build(BpmnProcess process, List<PathFinder.Path> paths) {
        List<BpmnProcess.Node> tasks = process.userTasks();
        List<List<String>> rows = new ArrayList<>();

        List<String> header = new ArrayList<>();
        header.add("Flujo");
        tasks.forEach(t -> header.add(t.label()));
        header.add("Evento final");
        rows.add(header);

        for (int i = 0; i < paths.size(); i++) {
            PathFinder.Path path = paths.get(i);
            List<String> row = new ArrayList<>();
            row.add("F" + (i + 1));
            tasks.forEach(t -> row.add(path.contains(t) ? "X" : "-"));
            row.add(path.endEvent().label());
            rows.add(row);
        }
        return rows;
    }

    public void writeCsv(List<List<String>> rows, Path file) throws IOException {
        Files.createDirectories(file.toAbsolutePath().getParent());
        StringBuilder csv = new StringBuilder();
        for (List<String> row : rows) {
            csv.append(String.join(",", row.stream().map(FlowTableWriter::csvCell).toList())).append("\n");
        }
        Files.writeString(file, csv, StandardCharsets.UTF_8);
    }

    /** Version legible para la consola. */
    public String toText(List<List<String>> rows) {
        int cols = rows.get(0).size();
        int[] width = new int[cols];
        for (List<String> row : rows) {
            for (int c = 0; c < cols; c++) {
                width[c] = Math.max(width[c], row.get(c).length());
            }
        }
        StringBuilder sb = new StringBuilder();
        for (List<String> row : rows) {
            for (int c = 0; c < cols - 1; c++) {
                sb.append(String.format("%-" + width[c] + "s", row.get(c))).append(" | ");
            }
            sb.append(row.get(cols - 1)).append("\n");
        }
        return sb.toString();
    }

    private static String csvCell(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
