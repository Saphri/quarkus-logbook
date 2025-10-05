package io.quarkiverse.logbook.runtime;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.Strategy;
import org.zalando.logbook.core.BodyOnlyIfStatusAtLeastStrategy;
import org.zalando.logbook.core.DefaultStrategy;
import org.zalando.logbook.core.StatusAtLeastStrategy;
import org.zalando.logbook.core.WithoutBodyStrategy;

import io.quarkiverse.logbook.runtime.configuration.StrategyConfiguration;
import io.quarkus.arc.DefaultBean;

public class StrategyProvider {

    private final StrategyConfiguration strategyConfiguration;

    public StrategyProvider(final StrategyConfiguration strategyConfiguration) {
        this.strategyConfiguration = strategyConfiguration;
    }

    @ApplicationScoped
    @DefaultBean
    public Strategy strategy() {
        if (strategyConfiguration != null) {
            final var name = strategyConfiguration.name().orElseGet(() -> "default");
            final var minStatus = strategyConfiguration.minimumStatus().orElse(400);
            switch (name) {
                case "default":
                    return new DefaultStrategy();
                case "without-body":
                    return new WithoutBodyStrategy();
                case "status-at-least":
                    return new StatusAtLeastStrategy(minStatus);
                case "body-only-if-status-at-least":
                    return new BodyOnlyIfStatusAtLeastStrategy(minStatus);
                default:
                    throw new IllegalArgumentException(
                            "Unknown strategy: " + name
                                    + ". Valid values are: default, without-body, status-at-least, body-only-if-status-at-least");
            }
        }
        return new DefaultStrategy();
    }
}
