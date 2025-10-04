package io.quarkiverse.logbook.runtime.configuration;

import jakarta.annotation.Priority;

import org.eclipse.microprofile.config.spi.Converter;
import org.zalando.logbook.Strategy;
import org.zalando.logbook.core.BodyOnlyIfStatusAtLeastStrategy;
import org.zalando.logbook.core.DefaultStrategy;
import org.zalando.logbook.core.StatusAtLeastStrategy;
import org.zalando.logbook.core.WithoutBodyStrategy;

@Priority(100)
public class StrategyConverter implements Converter<Strategy> {

    @Override
    public Strategy convert(String value) {
        if (value == null || value.isEmpty()) {
            return new DefaultStrategy();
        }

        return switch (value.toLowerCase()) {
            case "default" -> new DefaultStrategy();
            case "without-body" -> new WithoutBodyStrategy();
            case "status-at-least" -> new StatusAtLeastStrategy(400);
            case "body-only-if-status-at-least" -> new BodyOnlyIfStatusAtLeastStrategy(400);
            default -> throw new IllegalArgumentException(
                    "Unknown strategy: " + value
                            + ". Valid values are: default, without-body, status-at-least, body-only-if-status-at-least");
        };
    }
}
