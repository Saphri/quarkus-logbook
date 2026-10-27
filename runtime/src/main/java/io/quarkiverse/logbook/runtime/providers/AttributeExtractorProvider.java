package io.quarkiverse.logbook.runtime.providers;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.attributes.AttributeExtractor;
import org.zalando.logbook.attributes.NoOpAttributeExtractor;
import org.zalando.logbook.core.attributes.CompositeAttributeExtractor;
import org.zalando.logbook.core.attributes.JwtAllMatchingClaimsExtractor;
import org.zalando.logbook.core.attributes.JwtFirstMatchingClaimExtractor;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration;
import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration.AttributeExtractorConfiguration;
import io.quarkus.arc.DefaultBean;
import tools.jackson.databind.json.JsonMapper;

/**
 * AttributeExtractorProvider is a CDI provider that provides an {@link AttributeExtractor} bean.
 * This class is responsible for creating an attribute extractor based on the Logbook configuration.
 * It supports creating a {@link NoOpAttributeExtractor}, a single JWT-based extractor, or a
 * composite extractor if multiple configurations are provided.
 */
public class AttributeExtractorProvider {

    /**
     * Creates and configures an {@link AttributeExtractor} bean based on the application's configuration.
     * If no attribute extractors are configured, a {@link NoOpAttributeExtractor} is returned.
     * If one is configured, a corresponding extractor is created.
     * If multiple are configured, a {@link CompositeAttributeExtractor} is created.
     *
     * @param logbookConfiguration the Logbook configuration.
     * @param jsonMapper the Jackson 3 JSON mapper for JSON processing.
     * @return a configured {@link AttributeExtractor} instance.
     */
    @ApplicationScoped
    @DefaultBean
    public AttributeExtractor attributeExtractor(final LogbookConfiguration logbookConfiguration,
            final JsonMapper jsonMapper) {
        final var attributeExtractors = logbookConfiguration.attributeExtractors().orElseGet(List::of);
        switch (attributeExtractors.size()) {
            case 0:
                return new NoOpAttributeExtractor();
            case 1:
                return toExtractor(attributeExtractors.get(0), jsonMapper);
            default:
                return new CompositeAttributeExtractor(
                        attributeExtractors.stream()
                                .map(property -> toExtractor(property, jsonMapper))
                                .toList());
        }
    }

    /**
     * Converts an {@link AttributeExtractorConfiguration} into an {@link AttributeExtractor} instance.
     *
     * @param config the attribute extractor configuration.
     * @param jsonMapper the Jackson 3 JSON mapper.
     * @return a new {@link AttributeExtractor} instance.
     * @throws IllegalArgumentException if the extractor type is unknown.
     */
    private AttributeExtractor toExtractor(final AttributeExtractorConfiguration config, final JsonMapper jsonMapper) {
        switch (config.type()) {
            case "JwtFirstMatchingClaimExtractor":
                return JwtFirstMatchingClaimExtractor.builder()
                        .jsonMapper(jsonMapper)
                        .claimNames(config.claimNames())
                        .claimKey(config.claimKey().orElse(null))
                        .build();
            case "JwtAllMatchingClaimsExtractor":
                return JwtAllMatchingClaimsExtractor.builder()
                        .jsonMapper(jsonMapper)
                        .claimNames(config.claimNames())
                        .build();
            default:
                throw new IllegalArgumentException("Unknown AttributeExtractor type: " + config.type());
        }
    }
}
