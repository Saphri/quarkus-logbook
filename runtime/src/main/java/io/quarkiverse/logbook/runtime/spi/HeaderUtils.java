package io.quarkiverse.logbook.runtime.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zalando.logbook.HttpHeaders;

import io.vertx.core.MultiMap;

/**
 * A utility class for header conversions.
 * This class provides helper methods for converting header representations between different formats.
 */
public class HeaderUtils {

    private HeaderUtils() {
        // utility class
    }

    /**
     * Converts a Vert.x {@link MultiMap} of headers to Logbook's {@link HttpHeaders}.
     *
     * @param vertxHeaders the Vert.x headers to convert.
     * @return a new {@link HttpHeaders} instance.
     */
    public static HttpHeaders toLogbookHeaders(final MultiMap vertxHeaders) {
        Map<String, List<String>> convertedHeaders = new HashMap<>();
        vertxHeaders.entries().forEach(header -> {
            convertedHeaders.computeIfAbsent(header.getKey(), k -> new ArrayList<>()).add(header.getValue());
        });
        return HttpHeaders.of(convertedHeaders);
    }
}
