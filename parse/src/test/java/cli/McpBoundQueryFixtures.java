package cli;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

final class McpBoundQueryFixtures {

    private static final String RESOURCE_PREFIX = "/mcp/bound-query/csv-row-";

    private McpBoundQueryFixtures() {
    }

    static String sqlForRow(int csvRow) throws IOException {
        String resource = RESOURCE_PREFIX + csvRow + ".sql";
        try (InputStream in = McpBoundQueryFixtures.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("Missing test resource: " + resource);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
