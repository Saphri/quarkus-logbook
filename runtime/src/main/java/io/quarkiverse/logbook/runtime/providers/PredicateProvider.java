package io.quarkiverse.logbook.runtime.providers;

import java.util.List;
import java.util.function.Predicate;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.core.Conditions;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration;
import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration.LogbookPredicate;
import io.quarkus.arc.DefaultBean;

public class PredicateProvider {

    private final LogbookConfiguration logbookConfiguration;

    public PredicateProvider(LogbookConfiguration logbookConfiguration) {
        this.logbookConfiguration = logbookConfiguration;
    }

    @ApplicationScoped
    @DefaultBean
    public Predicate<HttpRequest> condition() {
        return mergeWithExcludes(mergeWithIncludes($ -> true));
    }

    private Predicate<HttpRequest> mergeWithExcludes(final Predicate<HttpRequest> predicate) {
        final var excludes = logbookConfiguration.predicate().excludes().orElseGet(List::of);

        return excludes.stream()
                .map(this::convertToPredicate)
                .map(Predicate::negate)
                .reduce(predicate, Predicate::and);
    }

    private Predicate<HttpRequest> mergeWithIncludes(final Predicate<HttpRequest> predicate) {
        final var includes = logbookConfiguration.predicate().includes().orElseGet(List::of);
        return includes.stream()
                .map(this::convertToPredicate)
                .reduce(Predicate::or)
                .map(predicate::and)
                .orElse(predicate);
    }

    private Predicate<HttpRequest> convertToPredicate(LogbookPredicate logbookPredicate) {
        final var predicatePath = logbookPredicate.path();
        final var predicateMethods = logbookPredicate.methods().orElseGet(List::of);

        if (predicateMethods.isEmpty()) {
            return Conditions.requestTo(predicatePath);
        }

        return predicateMethods.stream()
                .map(Conditions::requestWithMethod)
                .map(methodPredicate -> {
                    if (predicatePath != null) {
                        return Conditions.requestTo(predicatePath).and(methodPredicate);
                    } else {
                        return methodPredicate;
                    }
                })
                .reduce(Predicate::or)
                .orElse(x -> false);
    }
}
