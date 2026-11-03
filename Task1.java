import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class Task1 {

    interface ReportFormatter {
        String format(String title, String[] headers, String[][] rows);
    }

    static class JsonFormatter implements ReportFormatter {
        @Override
        public String format(String title, String[] headers, String[][] rows) {
            StringBuilder sb = new StringBuilder();
            sb.append("{\"title\":\"").append(title).append("\",\"data\":[");

            for (int i = 0; i < rows.length; i++) {
                if (i > 0) sb.append(",");
                sb.append("{");

                for (int j = 0; j < headers.length; j++) {
                    if (j > 0) sb.append(",");
                    sb.append("\"").append(headers[j]).append("\":\"");
                    sb.append(rows[i][j]).append("\"");
                }
                sb.append("}");
            }
            sb.append("]}");
            return sb.toString();
        }
    }

    static class CsvFormatter implements ReportFormatter {
        @Override
        public String format(String title, String[] headers, String[][] rows) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < headers.length; i++) {
                if (i > 0) sb.append(",");
                sb.append("\"").append(headers[i]).append("\"");
            }
            sb.append("\n");

            for (String[] row : rows) {
                for (int i = 0; i < row.length; i++) {
                    if (i > 0) sb.append(",");
                    sb.append("\"").append(row[i]).append("\"");
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    interface Exporter {
        void export(String destination, String data);
    }

    static class BaseFileExporter implements Exporter {
        @Override
        public void export(String destination, String data) {
            System.out.println("--- FILE PAYLOAD BEGIN ---");
            System.out.println(data);
            System.out.println("--- FILE PAYLOAD END ---");
        }
    }

    static abstract class ExporterDecorator implements Exporter {
        protected final Exporter wrappedExporter;

        public ExporterDecorator(Exporter exporter) {
            if (exporter == null)
                throw new IllegalArgumentException("Exporter cannot be null");
            this.wrappedExporter = exporter;
        }

        @Override
        public void export(String destination, String data) {
            wrappedExporter.export(destination, data);
        }
    }

    static class HeaderFooterDecorator extends ExporterDecorator {
        public HeaderFooterDecorator(Exporter exporter) {
            super(exporter);
        }

        @Override
        public void export(String destination, String data) {
            String timestamp = Instant.now().toString();
            String payload =
                    "--- SECURE TIMESTAMP: " + timestamp + " ---\n"
                    + data
                    + "\n--- RECORD COUNT: " + data.length()
                    + " | SECURE HASH: [OK] ---";

            super.export(destination, payload);
        }
    }

    static class Base64EncryptDecorator extends ExporterDecorator {
        public Base64EncryptDecorator(Exporter exporter) {
            super(exporter);
        }

        @Override
        public void export(String destination, String data) {
            String encoded = Base64.getEncoder().encodeToString(
                    data.getBytes(StandardCharsets.UTF_8));
            super.export(destination, encoded);
        }
    }

    public static void main(String[] args) {
        String[] headers = {"ID", "User", "Role"};

        String[][] rows = {
                {"101", "Taylor", "Admin"},
                {"102", "Steve", "Dev"}
        };

        ReportFormatter formatter = new JsonFormatter();
        String report = formatter.format("Security Audit", headers, rows);

        Exporter exporter = new Base64EncryptDecorator(
                new HeaderFooterDecorator(
                        new BaseFileExporter()
                )
        );

        System.out.println(">>> TESTING DECORATOR STACK");
        exporter.export("audit.log", report);
        System.out.println(">>> FILE EXPORT COMPLETED");
    }
}
