package io.quarkiverse.logbook.runtime.providers;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.Strategy;
import org.zalando.logbook.core.BodyOnlyIfStatusAtLeastStrategy;
import org.zalando.logbook.core.DefaultStrategy;
import org.zalando.logbook.core.StatusAtLeastStrategy;
import org.zalando.logbook.core.WithoutBodyStrategy;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration;
import io.quarkus.arc.DefaultBean;

public class StrategyProvider {

    @ApplicationScoped
    @DefaultBean
    public Strategy strategy(final LogbookConfiguration logbookConfiguration) {
        final var strategyConfiguration = logbookConfiguration.strategy();
        switch (strategyConfiguration.strategy()) {
            case "default":
                return new DefaultStrategy();
            case "without-body":
                return new WithoutBodyStrategy();
            case "status-at-least":
                return new StatusAtLeastStrategy(logbookConfiguration.minimumStatus());
            case "body-only-if-status-at-least":
                return new BodyOnlyIfStatusAtLeastStrategy(logbookConfiguration.minimumStatus());
            default:
                throw new IllegalArgumentException(
                        "Unknown strategy: " + strategyConfiguration.strategy()
                                + ". Valid values are: default, without-body, status-at-least, body-only-if-status-at-least");
        }
    }
}
