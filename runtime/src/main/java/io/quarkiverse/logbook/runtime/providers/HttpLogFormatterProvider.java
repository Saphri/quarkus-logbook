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

/**
 * HttpLogFormatterProvider is a CDI producer that provides an {@link HttpLogFormatter} bean.
 * This class is responsible for creating a log formatter based on the configured style in the
 * Logbook configuration. It supports "json", "http", "curl", and "splunk" styles.
 */
public class HttpLogFormatterProvider {

    /**
     * Creates and configures an {@link HttpLogFormatter} bean based on the application's configuration.
     *
     * @param logbookConfiguration the Logbook configuration.
     * @param objectMapper the Jackson object mapper, used for the JSON formatter.
     * @return a configured {@link HttpLogFormatter} instance.
     * @throws IllegalArgumentException if the configured style is unknown.
     */
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
