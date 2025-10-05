package io.quarkiverse.logbook.runtime;

import static org.zalando.logbook.core.Conditions.exclude;
import static org.zalando.logbook.core.Conditions.requestTo;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.Logbook;
import org.zalando.logbook.Strategy;
import org.zalando.logbook.core.PathFilters;
import org.zalando.logbook.core.QueryFilters;

import io.quarkus.arc.DefaultBean;

public class LogbookServerProvider {

    private final Strategy strategy;

    public LogbookServerProvider(final Strategy strategy) {
        this.strategy = strategy;
    }

    @ApplicationScoped
    @DefaultBean
    public Logbook serverLogbook() {
        return Logbook.builder()
                .condition(exclude(requestTo("/q/**")))
                .pathFilter(PathFilters.defaultValue())
                .pathFilter(PathFilters.replace("/hello/json/{fff}", "XXX"))
                .queryFilter(QueryFilters.defaultValue())
                .queryFilter(QueryFilters.replaceQuery("secret", "XXX"))
                .strategy(strategy)
                .build();
    }
}
