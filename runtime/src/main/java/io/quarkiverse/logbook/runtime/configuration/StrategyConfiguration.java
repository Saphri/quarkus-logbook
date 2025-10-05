package io.quarkiverse.logbook.runtime.configuration;

import java.util.Optional;
import java.util.OptionalInt;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "quarkus.logbook.strategy")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface StrategyConfiguration {
    /**
     * The strategy to use for logging requests and responses.
     * Valid values are:
     * - default: Log everything (default)
     * - without-body: Log everything except bodies
     * - status-at-least: Log only if status >= minimum-status (default 400)
     * - body-only-if-status-at-least: Log request without body, then log both request and response with bodies only if status
     * >= minimum-status (default 400)
     */
    Optional<String> name();

    /**
     * The minimum status code to use with the "status-at-least" and "body-only-if-status-at-least" strategies. Default is 400.
     */
    OptionalInt minimumStatus();
}
