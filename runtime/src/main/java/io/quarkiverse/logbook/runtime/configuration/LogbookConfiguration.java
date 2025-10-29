package io.quarkiverse.logbook.runtime.configuration;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;
import io.smallrye.config.WithParentName;

/**
 * Configuration for the Logbook extension.
 * This interface defines the various configuration properties that can be used to customize
 * the behavior of Logbook in a Quarkus application.
 */
@ConfigMapping(prefix = "quarkus.logbook")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface LogbookConfiguration {
    /**
     * Configuration properties for the logging strategy.
     * The strategy determines how requests and responses are logged.
     */
    StrategyConfiguration strategy();

    /**
     * Configuration properties for writing log messages.
     * This includes settings for chunking and body truncation.
     */
    WriteConfiguration write();

    /**
     * Configuration properties for formatting log messages.
     * This allows you to choose between different log formats like JSON, cURL, or HTTP.
     */
    FormatConfiguration format();

    /**
     * Configuration properties for obfuscating sensitive data.
     * This includes settings for obfuscating headers, parameters, paths, and JSON body fields.
     */
    ObfuscateConfiguration obfuscate();

    /**
     * Configuration properties for filtering requests.
     * This allows you to include or exclude requests from being logged based on their path and method.
     */
    PredicateConfiguration predicate();

    /**
     * Minimum status code to enable logging for strategies that depend on it.
     * This is used by the "status-at-least" and "body-only-if-status-at-least" strategies.
     * The default is 400.
     */
    @WithDefault("400")
    int minimumStatus();

    /**
     * Configuration properties for attribute extractors.
     * This allows for extracting attributes from JWTs and adding them to the log.
     */
    Optional<List<AttributeExtractorConfiguration>> attributeExtractors();

    /**
     * Configuration for the logging strategy.
     */
    public interface StrategyConfiguration {
        /**
         * The strategy to use for logging requests and responses.
         * Valid values are:
         * <ul>
         * <li>{@code default}: Log everything.</li>
         * <li>{@code without-body}: Log everything except bodies.</li>
         * <li>{@code status-at-least}: Log only if the response status is >= minimumStatus.</li>
         * <li>{@code body-only-if-status-at-least}: Log the request without a body,
         * and then log both the request and response with bodies only if the response status is >= minimumStatus.</li>
         * </ul>
         */
        @WithDefault("default")
        @WithParentName
        String strategy();
    }

    /**
     * Configuration for writing log messages.
     */
    public interface WriteConfiguration {
        /**
         * Splits log lines into smaller chunks of a given size.
         * A value of 0 (the default) disables chunking.
         */
        @WithDefault("0")
        int chunkSize();

        /**
         * Truncates the body to a given number of characters and appends "...".
         * A value of -1 (the default) disables truncation.
         */
        @WithDefault("-1")
        int maxBodySize();
    }

    /**
     * Configuration for formatting log messages.
     */
    public interface FormatConfiguration {
        /**
         * The style to use for formatting log messages. Valid values are:
         * <ul>
         * <li>{@code json}: JSON format (default).</li>
         * <li>{@code curl}: cURL command format.</li>
         * <li>{@code http}: HTTP message format.</li>
         * <li>{@code splunk}: Splunk-friendly format.</li>
         * </ul>
         */
        @WithDefault("json")
        String style();
    }

    /**
     * Configuration for obfuscating sensitive data.
     */
    public interface ObfuscateConfiguration {
        /**
         * A list of header names to be obfuscated.
         * By default, "Authorization" is obfuscated.
         */
        @WithDefault("Authorization")
        Optional<List<String>> headers();

        /**
         * A set of JSON body fields to be obfuscated.
         */
        Optional<Set<String>> jsonBodyFields();

        /**
         * A list of parameter names to be obfuscated.
         * By default, "access_token" is obfuscated.
         */
        @WithDefault("access_token")
        Optional<List<String>> parameters();

        /**
         * A list of URL paths to be obfuscated.
         */
        Optional<List<String>> paths();

        /**
         * The value to be used as a replacement for obfuscated data.
         * The default is "XXX".
         */
        @WithDefault("XXX")
        String replacement();
    }

    /**
     * Configuration for filtering requests.
     */
    public interface PredicateConfiguration {
        /**
         * A list of predicates to include requests for logging.
         * Requests that match any of these predicates will be logged.
         */
        @WithName("include")
        Optional<List<LogbookPredicate>> includes();

        /**
         * A list of predicates to exclude requests from logging.
         * Requests that match any of these predicates will not be logged.
         * Excludes take precedence over includes.
         */
        @WithName("exclude")
        Optional<List<LogbookPredicate>> excludes();
    }

    /**
     * A predicate definition for filtering requests.
     */
    public interface LogbookPredicate {
        /**
         * The URL path to filter.
         */
        String path();

        /**
         * A list of HTTP methods to filter for the given path.
         */
        Optional<List<String>> methods();
    }

    /**
     * Configuration for an attribute extractor.
     */
    public interface AttributeExtractorConfiguration {
        /**
         * The type of attribute extractor to use.
         * Currently supports "JwtFirstMatchingClaimExtractor" and "JwtAllMatchingClaimsExtractor".
         */
        String type();

        /**
         * A list of claim names to be extracted from the JWT.
         */
        List<String> claimNames();

        /**
         * The key to be used for the extracted claim in the log.
         */
        Optional<String> claimKey();
    }
}
