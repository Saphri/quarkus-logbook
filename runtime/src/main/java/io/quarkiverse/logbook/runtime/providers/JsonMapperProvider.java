package io.quarkiverse.logbook.runtime.providers;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkus.arc.DefaultBean;
import tools.jackson.databind.json.JsonMapper;

/**
 * JsonMapperProvider is a CDI provider that provides a Jackson 3 {@link JsonMapper} bean.
 * Logbook 4.x uses Jackson 3 for its JSON formatter and JWT-based attribute extractors, so
 * Logbook components are wired to this mapper. Applications can replace the default bean with
 * a custom {@link JsonMapper} bean to control JSON serialization behavior.
 */
public class JsonMapperProvider {

    /**
     * Provides the default {@link JsonMapper} used by the Logbook components.
     *
     * @return a {@link JsonMapper} created with Jackson 3 defaults.
     */
    @ApplicationScoped
    @DefaultBean
    public JsonMapper jsonMapper() {
        return JsonMapper.builder().build();
    }
}
