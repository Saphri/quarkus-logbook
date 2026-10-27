package io.quarkiverse.logbook.runtime.providers;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.core.CurlHttpLogFormatter;
import org.zalando.logbook.core.DefaultHttpLogFormatter;
import org.zalando.logbook.core.SplunkHttpLogFormatter;
import org.zalando.logbook.json.JsonHttpLogFormatter;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration.FormatConfiguration;
import io.quarkus.arc.DefaultBean;
import tools.jackson.databind.json.JsonMapper;

/**
 * HttpLogFormatterProvider is a CDI provider that provides an {@link HttpLogFormatter} bean.
 * This class is responsible for creating a log formatter based on the configured style in the
 * format section of the Logbook configuration. It supports "json", "http", "curl", and "splunk"
 * styles.
 */
public class HttpLogFormatterProvider {

    /**
     * Creates and configures an {@link HttpLogFormatter} bean based on the application's configuration.
     *
     * @param formatConfiguration the format configuration section.
     * @param jsonMapper the Jackson 3 JSON mapper, used for the JSON formatter.
     * @return a configured {@link HttpLogFormatter} instance.
     * @throws IllegalArgumentException if the configured style is unknown.
     */
    @ApplicationScoped
    @DefaultBean
    public HttpLogFormatter format(final FormatConfiguration formatConfiguration, final JsonMapper jsonMapper) {
        switch (formatConfiguration.style()) {
            case "json":
                return new JsonHttpLogFormatter(jsonMapper);
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
