package io.quarkiverse.logbook.runtime.providers;

import java.util.List;
import java.util.function.Predicate;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.core.Conditions;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration;
import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration.LogbookPredicate;
import io.quarkus.arc.DefaultBean;

/**
 * PredicateProvider is a CDI producer that provides a {@link Predicate<HttpRequest>} bean.
 * This predicate is used as a condition to determine whether a given HTTP request should be logged.
 * It is built based on the include and exclude rules defined in the Logbook configuration.
 */
public class PredicateProvider {

    private final LogbookConfiguration logbookConfiguration;

    /**
     * Constructs a new PredicateProvider with the given Logbook configuration.
     *
     * @param logbookConfiguration the Logbook configuration.
     */
    public PredicateProvider(LogbookConfiguration logbookConfiguration) {
        this.logbookConfiguration = logbookConfiguration;
    }

    /**
     * Creates and configures a {@link Predicate<HttpRequest>} bean.
     * The predicate is built by combining include and exclude rules from the configuration.
     * Excludes take precedence over includes.
     *
     * @return a configured {@link Predicate<HttpRequest>} instance.
     */
    @ApplicationScoped
    @DefaultBean
    public Predicate<HttpRequest> condition() {
        return mergeWithExcludes(mergeWithIncludes($ -> true));
    }

    /**
     * Merges the given predicate with the exclude rules from the configuration.
     *
     * @param predicate the base predicate.
     * @return a new predicate that incorporates the exclude rules.
     */
    private Predicate<HttpRequest> mergeWithExcludes(final Predicate<HttpRequest> predicate) {
        final var excludes = logbookConfiguration.predicate().excludes().orElseGet(List::of);

        return excludes.stream()
                .map(this::convertToPredicate)
                .map(Predicate::negate)
                .reduce(predicate, Predicate::and);
    }

    /**
     * Merges the given predicate with the include rules from the configuration.
     *
     * @param predicate the base predicate.
     * @return a new predicate that incorporates the include rules.
     */
    private Predicate<HttpRequest> mergeWithIncludes(final Predicate<HttpRequest> predicate) {
        final var includes = logbookConfiguration.predicate().includes().orElseGet(List::of);
        return includes.stream()
                .map(this::convertToPredicate)
                .reduce(Predicate::or)
                .map(predicate::and)
                .orElse(predicate);
    }

    /**
     * Converts a {@link LogbookPredicate} from the configuration into a {@link Predicate<HttpRequest>}.
     *
     * @param logbookPredicate the predicate configuration.
     * @return a new {@link Predicate<HttpRequest>} instance.
     */
    private Predicate<HttpRequest> convertToPredicate(final LogbookPredicate logbookPredicate) {
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
