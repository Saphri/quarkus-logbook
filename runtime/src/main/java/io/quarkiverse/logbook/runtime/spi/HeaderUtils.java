package io.quarkiverse.logbook.runtime.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zalando.logbook.HttpHeaders;

import io.vertx.core.MultiMap;

public class HeaderUtils {

    private HeaderUtils() {
        // utility class
    }

    public static HttpHeaders toLogbookHeaders(final MultiMap vertxHeaders) {
        Map<String, List<String>> convertedHeaders = new HashMap<>();
        vertxHeaders.entries().forEach(header -> {
            convertedHeaders.computeIfAbsent(header.getKey(), k -> new ArrayList<>()).add(header.getValue());
        });
        return HttpHeaders.of(convertedHeaders);
    }
}
