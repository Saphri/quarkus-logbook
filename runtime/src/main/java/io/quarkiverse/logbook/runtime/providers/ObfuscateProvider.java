package io.quarkiverse.logbook.runtime.providers;

import static org.zalando.logbook.core.HeaderFilters.replaceHeaders;
import static org.zalando.logbook.core.QueryFilters.replaceQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.HeaderFilter;
import org.zalando.logbook.PathFilter;
import org.zalando.logbook.QueryFilter;
import org.zalando.logbook.core.HeaderFilters;
import org.zalando.logbook.core.PathFilters;
import org.zalando.logbook.core.QueryFilters;
import org.zalando.logbook.json.JacksonJsonFieldBodyFilter;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration;
import io.quarkus.arc.DefaultBean;

public class ObfuscateProvider {

    private final LogbookConfiguration logbookConfiguration;

    ObfuscateProvider(final LogbookConfiguration logbookConfiguration) {
        this.logbookConfiguration = logbookConfiguration;
    }

    @ApplicationScoped
    @DefaultBean
    public HeaderFilter headerFilter() {
        final var headers = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        headers.addAll(logbookConfiguration.obfuscate().headers().orElseGet(List::of));

        return headers.isEmpty() ? HeaderFilters.defaultValue()
                : replaceHeaders(headers, logbookConfiguration.obfuscate().replacement());
    }

    @ApplicationScoped
    @DefaultBean
    public QueryFilter queryFilter() {
        final var parameters = logbookConfiguration.obfuscate().parameters().orElseGet(List::of);

        return parameters.isEmpty() ? QueryFilters.defaultValue()
                : replaceQuery(new HashSet<>(parameters)::contains, logbookConfiguration.obfuscate().replacement());
    }

    @ApplicationScoped
    @DefaultBean
    public PathFilter pathFilter() {
        final var paths = logbookConfiguration.obfuscate().paths().orElseGet(List::of);
        return paths.isEmpty() ? PathFilter.none()
                : paths.stream()
                        .map(path -> PathFilters.replace(path, logbookConfiguration.obfuscate().replacement()))
                        .reduce(PathFilter::merge)
                        .orElseGet(PathFilter::none);
    }

    @ApplicationScoped
    @DefaultBean
    public BodyFilter bodyFilter() {
        final var fields = logbookConfiguration.obfuscate().jsonBodyFields().orElseGet(Set::of);
        return fields.isEmpty() ? BodyFilter.none()
                : new JacksonJsonFieldBodyFilter(fields, logbookConfiguration.obfuscate().replacement());
    }
}
