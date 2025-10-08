package io.quarkiverse.logbook.runtime.configuration;

import java.util.List;
import java.util.Optional;

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
     * Configuration properties for the obfuscate.
     */
    ObfuscateConfiguration obfuscate();

    /**
     * Configuration properties for the predicate.
     */
    PredicateConfiguration predicate();

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

    public interface ObfuscateConfiguration {
        /**
         * List of header names that need obfuscation.
         */
        @WithDefault("Authorization")
        Optional<List<String>> headers();

        /**
         * List of JSON body fields to be obfuscated.
         */
        Optional<List<String>> jsonBodyFields();

        /**
         * List of parameter names that need obfuscation.
         */
        @WithDefault("access_token")
        Optional<List<String>> parameters();

        /**
         * List of paths that need obfuscation. Check Filtering for syntax.
         */
        Optional<List<String>> paths();

        /**
         * A value to be used instead of an obfuscated one.
         */
        @WithDefault("XXX")
        String replacement();
    }

    public interface PredicateConfiguration {
        /**
         * List of paths to include from logging. Check Filtering for syntax.
         */
        Optional<List<LogbookPredicate>> include();

        /**
         * List of paths to exclude from logging. Check Filtering for syntax.
         */
        Optional<List<LogbookPredicate>> exclude();
    }

    /**
     * A predicate definition.
     */
    public interface LogbookPredicate {
        /**
         * The predicate type. Valid values are:
         * - request-to: matches the request path
         * - remote-host: matches the remote host
         * - method: matches the HTTP method
         */
        String path();

        /**
         * The predicate pattern. Check Filtering for syntax.
         */
        List<String> methods();
    }
}
