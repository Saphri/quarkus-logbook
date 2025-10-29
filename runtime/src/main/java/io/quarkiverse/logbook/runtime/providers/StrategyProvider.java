package io.quarkiverse.logbook.runtime.providers;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.Strategy;
import org.zalando.logbook.core.BodyOnlyIfStatusAtLeastStrategy;
import org.zalando.logbook.core.DefaultStrategy;
import org.zalando.logbook.core.StatusAtLeastStrategy;
import org.zalando.logbook.core.WithoutBodyStrategy;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration;
import io.quarkus.arc.DefaultBean;

/**
 * StrategyProvider is a CDI producer that provides a {@link Strategy} bean.
 * This class is responsible for creating a logging strategy based on the configured strategy
 * name in the Logbook configuration. It supports "default", "without-body", "status-at-least",
 * and "body-only-if-status-at-least" strategies.
 */
public class StrategyProvider {

    /**
     * Creates and configures a {@link Strategy} bean based on the application's configuration.
     *
     * @param logbookConfiguration the Logbook configuration.
     * @return a configured {@link Strategy} instance.
     * @throws IllegalArgumentException if the configured strategy is unknown.
     */
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
