package io.quarkiverse.logbook.runtime.configuration;

import org.zalando.logbook.Strategy;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "quarkus.logbook")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface LogbookConfiguration {
    /**
     * The strategy to use for logging requests and responses.
     */
    @WithDefault("default")
    Strategy strategy();
}
