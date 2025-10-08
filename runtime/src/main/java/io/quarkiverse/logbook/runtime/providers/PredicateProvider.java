package io.quarkiverse.logbook.runtime.providers;

import java.util.function.Predicate;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.HttpRequest;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration;
import io.quarkus.arc.DefaultBean;

public class PredicateProvider {

    private final LogbookConfiguration logbookConfiguration;

    public PredicateProvider(LogbookConfiguration logbookConfiguration) {
        this.logbookConfiguration = logbookConfiguration;
    }

    @ApplicationScoped
    @DefaultBean
    public Predicate<HttpRequest> condition() {
        return $ -> true;
    }
}
