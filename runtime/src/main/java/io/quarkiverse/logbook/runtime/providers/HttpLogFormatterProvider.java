package io.quarkiverse.logbook.runtime.providers;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.core.CurlHttpLogFormatter;
import org.zalando.logbook.core.DefaultHttpLogFormatter;
import org.zalando.logbook.core.SplunkHttpLogFormatter;
import org.zalando.logbook.json.JsonHttpLogFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration;
import io.quarkus.arc.DefaultBean;

public class HttpLogFormatterProvider {

    @ApplicationScoped
    @DefaultBean
    public HttpLogFormatter format(final LogbookConfiguration logbookConfiguration, final ObjectMapper objectMapper) {
        final var formatConfiguration = logbookConfiguration.format();
        switch (formatConfiguration.style()) {
            case "json":
                return new JsonHttpLogFormatter(objectMapper);
            case "http":
                return new DefaultHttpLogFormatter();
            case "curl":
                return new CurlHttpLogFormatter();
            case "splunk":
                return new SplunkHttpLogFormatter();
            default:
                throw new IllegalArgumentException(
                        "Unknown style: " + formatConfiguration.style()
                                + ". Valid values are: json, http, curl, splunk");
        }
    }
}
