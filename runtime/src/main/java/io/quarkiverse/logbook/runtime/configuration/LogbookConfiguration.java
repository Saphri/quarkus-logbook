package io.quarkiverse.logbook.runtime.configuration;

import org.zalando.logbook.Strategy;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "quarkus.logbook")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface LogbookConfiguration {
    /**
     * The strategy to use for logging requests and responses.
     * Valid values are:
     * - default: Log everything (default)
     * - without-body: Log everything except bodies
     * - status-at-least: Log only if status >= 400
     * - body-only-if-status-at-least: Log request without body, then log both request and response with bodies only if status
     * >= 400
     */
    @WithDefault("default")
    @WithConverter(StrategyConverter.class)
    Strategy strategy();
}
