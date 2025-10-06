package io.quarkiverse.logbook.runtime.configuration;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithParentName;

@ConfigMapping(prefix = "quarkus.logbook")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface LogbookConfiguration {
    /**
     * Configuration properties for the strategy.
     */
    StrategyConfiguration strategy();

    /**
     * Configuration properties for the write.
     */
    WriteConfiguration write();

    /**
     * Configuration properties for the format.
     */
    FormatConfiguration format();

    /**
     * Minimum status to enable logging (status-at-least and
     * body-only-if-status-at-least). Default is 400.
     */
    @WithDefault("400")
    int minimumStatus();

    public interface StrategyConfiguration {
        /**
         * The strategy to use for logging requests and responses.
         * Valid values are:
         * - default: Log everything (default)
         * - without-body: Log everything except bodies
         * - status-at-least: Log only if status >= minimum-status (default 400)
         * - body-only-if-status-at-least: Log request without body, then log both
         * request and response with bodies only if status
         * >= minimum-status (default 400)
         */
        @WithDefault("default")
        @WithParentName
        String strategy();
    }

    public interface WriteConfiguration {

        /**
         * Splits log lines into smaller chunks of size up-to chunk-size. Default is 0,
         * which means no chunking.
         */
        @WithDefault("0")
        int chunkSize();

        /**
         * Truncates the body up to max-body-size characters and appends .... Default is
         * -1, which means no truncation.
         */
        @WithDefault("-1")
        int maxBodySize();
    }

    public interface FormatConfiguration {

        /**
         * The style to use for formatting the log messages. Valid values are:
         * - json: JSON format (default)
         * - curl: cURL format
         * - http: HTTP format
         * - splunk: Splunk format
         */
        @WithDefault("json")
        String style();
    }
}
