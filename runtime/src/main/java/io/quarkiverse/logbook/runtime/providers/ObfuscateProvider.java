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

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration.ObfuscateConfiguration;
import io.quarkus.arc.DefaultBean;

/**
 * ObfuscateProvider is a CDI provider that provides beans for obfuscating sensitive data.
 * This class is responsible for creating various Logbook filters, such as {@link HeaderFilter},
 * {@link QueryFilter}, {@link PathFilter}, and {@link BodyFilter}, based on the obfuscate
 * section of the application's configuration. These filters help prevent sensitive information
 * from being written to the logs.
 */
public class ObfuscateProvider {

    /**
     * Creates and configures a {@link HeaderFilter} bean.
     * This filter obfuscates the values of configured headers.
     *
     * @param obfuscateConfiguration the obfuscate configuration section.
     * @return a configured {@link HeaderFilter} instance.
     */
    @ApplicationScoped
    @DefaultBean
    public HeaderFilter headerFilter(final ObfuscateConfiguration obfuscateConfiguration) {
        final var headers = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        headers.addAll(obfuscateConfiguration.headers().orElseGet(List::of));

        return headers.isEmpty() ? HeaderFilters.defaultValue()
                : replaceHeaders(headers, obfuscateConfiguration.replacement());
    }

    /**
     * Creates and configures a {@link QueryFilter} bean.
     * This filter obfuscates the values of configured query parameters.
     *
     * @param obfuscateConfiguration the obfuscate configuration section.
     * @return a configured {@link QueryFilter} instance.
     */
    @ApplicationScoped
    @DefaultBean
    public QueryFilter queryFilter(final ObfuscateConfiguration obfuscateConfiguration) {
        final var parameters = obfuscateConfiguration.parameters().orElseGet(List::of);

        return parameters.isEmpty() ? QueryFilters.defaultValue()
                : replaceQuery(new HashSet<>(parameters)::contains, obfuscateConfiguration.replacement());
    }

    /**
     * Creates and configures a {@link PathFilter} bean.
     * This filter obfuscates parts of the URL path based on configured patterns.
     *
     * @param obfuscateConfiguration the obfuscate configuration section.
     * @return a configured {@link PathFilter} instance.
     */
    @ApplicationScoped
    @DefaultBean
    public PathFilter pathFilter(final ObfuscateConfiguration obfuscateConfiguration) {
        final var paths = obfuscateConfiguration.paths().orElseGet(List::of);
        return paths.isEmpty() ? PathFilter.none()
                : paths.stream()
                        .map(path -> PathFilters.replace(path, obfuscateConfiguration.replacement()))
                        .reduce(PathFilter::merge)
                        .orElseGet(PathFilter::none);
    }

    /**
     * Creates and configures a {@link BodyFilter} bean.
     * This filter obfuscates sensitive fields in JSON bodies and common OAuth2 form-encoded properties.
     *
     * @param obfuscateConfiguration the obfuscate configuration section.
     * @return a configured {@link BodyFilter} instance.
     */
    @ApplicationScoped
    @DefaultBean
    public BodyFilter bodyFilter(final ObfuscateConfiguration obfuscateConfiguration) {
        final var fields = obfuscateConfiguration.jsonBodyFields().orElseGet(Set::of);
        final var jsonBodyFilter = fields.isEmpty() ? BodyFilter.none()
                : new JacksonJsonFieldBodyFilter(fields, obfuscateConfiguration.replacement());

        return BodyFilter.merge(oauthRequest(obfuscateConfiguration), jsonBodyFilter);
    }

    /**
     * Creates a {@link BodyFilter} specifically for obfuscating common OAuth2 form-encoded properties.
     *
     * @param obfuscateConfiguration the obfuscate configuration section.
     * @return a {@link BodyFilter} for OAuth2 properties.
     */
    private BodyFilter oauthRequest(final ObfuscateConfiguration obfuscateConfiguration) {
        final var properties = new HashSet<String>();
        properties.add("client_secret");
        properties.add("password");
        properties.add("refresh_token");
        return BodyFilters.replaceFormUrlEncodedProperty(properties, obfuscateConfiguration.replacement());
    }
}
