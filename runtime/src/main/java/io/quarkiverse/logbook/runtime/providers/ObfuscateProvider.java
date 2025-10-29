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
import org.zalando.logbook.core.BodyFilters;
import org.zalando.logbook.core.HeaderFilters;
import org.zalando.logbook.core.PathFilters;
import org.zalando.logbook.core.QueryFilters;
import org.zalando.logbook.json.JacksonJsonFieldBodyFilter;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration;
import io.quarkus.arc.DefaultBean;

/**
 * ObfuscateProvider is a CDI producer that provides beans for obfuscating sensitive data.
 * This class is responsible for creating various Logbook filters, such as {@link HeaderFilter},
 * {@link QueryFilter}, {@link PathFilter}, and {@link BodyFilter}, based on the application's
 * configuration. These filters help prevent sensitive information from being written to the logs.
 */
public class ObfuscateProvider {

    private final LogbookConfiguration logbookConfiguration;

    /**
     * Constructs a new ObfuscateProvider with the given Logbook configuration.
     *
     * @param logbookConfiguration the Logbook configuration.
     */
    ObfuscateProvider(final LogbookConfiguration logbookConfiguration) {
        this.logbookConfiguration = logbookConfiguration;
    }

    /**
     * Creates and configures a {@link HeaderFilter} bean.
     * This filter obfuscates the values of configured headers.
     *
     * @return a configured {@link HeaderFilter} instance.
     */
    @ApplicationScoped
    @DefaultBean
    public HeaderFilter headerFilter() {
        final var headers = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        headers.addAll(logbookConfiguration.obfuscate().headers().orElseGet(List::of));

        return headers.isEmpty() ? HeaderFilters.defaultValue()
                : replaceHeaders(headers, logbookConfiguration.obfuscate().replacement());
    }

    /**
     * Creates and configures a {@link QueryFilter} bean.
     * This filter obfuscates the values of configured query parameters.
     *
     * @return a configured {@link QueryFilter} instance.
     */
    @ApplicationScoped
    @DefaultBean
    public QueryFilter queryFilter() {
        final var parameters = logbookConfiguration.obfuscate().parameters().orElseGet(List::of);

        return parameters.isEmpty() ? QueryFilters.defaultValue()
                : replaceQuery(new HashSet<>(parameters)::contains, logbookConfiguration.obfuscate().replacement());
    }

    /**
     * Creates and configures a {@link PathFilter} bean.
     * This filter obfuscates parts of the URL path based on configured patterns.
     *
     * @return a configured {@link PathFilter} instance.
     */
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

    /**
     * Creates and configures a {@link BodyFilter} bean.
     * This filter obfuscates sensitive fields in JSON bodies and common OAuth2 form-encoded properties.
     *
     * @return a configured {@link BodyFilter} instance.
     */
    @ApplicationScoped
    @DefaultBean
    public BodyFilter bodyFilter() {
        final var fields = logbookConfiguration.obfuscate().jsonBodyFields().orElseGet(Set::of);
        final var jsonBodyFilter = fields.isEmpty() ? BodyFilter.none()
                : new JacksonJsonFieldBodyFilter(fields, logbookConfiguration.obfuscate().replacement());

        return BodyFilter.merge(oauthRequest(), jsonBodyFilter);
    }

    /**
     * Creates a {@link BodyFilter} specifically for obfuscating common OAuth2 form-encoded properties.
     *
     * @return a {@link BodyFilter} for OAuth2 properties.
     */
    private BodyFilter oauthRequest() {
        final var properties = new HashSet<String>();
        properties.add("client_secret");
        properties.add("password");
        properties.add("refresh_token");
        return BodyFilters.replaceFormUrlEncodedProperty(properties, logbookConfiguration.obfuscate().replacement());
    }
}
