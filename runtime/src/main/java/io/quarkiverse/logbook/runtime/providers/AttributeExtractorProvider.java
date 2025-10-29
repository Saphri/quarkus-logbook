package io.quarkiverse.logbook.runtime.providers;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.attributes.AttributeExtractor;
import org.zalando.logbook.attributes.NoOpAttributeExtractor;
import org.zalando.logbook.core.attributes.CompositeAttributeExtractor;
import org.zalando.logbook.core.attributes.JwtAllMatchingClaimsExtractor;
import org.zalando.logbook.core.attributes.JwtFirstMatchingClaimExtractor;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration;
import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration.AttributeExtractorConfiguration;
import io.quarkus.arc.DefaultBean;

/**
 * AttributeExtractorProvider is a CDI producer that provides an {@link AttributeExtractor} bean.
 * This class is responsible for creating an attribute extractor based on the Logbook configuration.
 * It supports creating a {@link NoOpAttributeExtractor}, a single JWT-based extractor, or a
 * composite extractor if multiple configurations are provided.
 */
public class AttributeExtractorProvider {

    private final LogbookConfiguration logbookConfiguration;
    private final ObjectMapper objectMapper;

    /**
     * Constructs a new AttributeExtractorProvider with the given configuration and object mapper.
     *
     * @param logbookConfiguration the Logbook configuration.
     * @param objectMapper the Jackson object mapper for JSON processing.
     */
    public AttributeExtractorProvider(final LogbookConfiguration logbookConfiguration, final ObjectMapper objectMapper) {
        this.logbookConfiguration = logbookConfiguration;
        this.objectMapper = objectMapper;
    }

    /**
     * Creates and configures an {@link AttributeExtractor} bean based on the application's configuration.
     * If no attribute extractors are configured, a {@link NoOpAttributeExtractor} is returned.
     * If one is configured, a corresponding extractor is created.
     * If multiple are configured, a {@link CompositeAttributeExtractor} is created.
     *
     * @return a configured {@link AttributeExtractor} instance.
     */
    @ApplicationScoped
    @DefaultBean
    public AttributeExtractor attributeExtractor() {
        final var attributeExtractors = logbookConfiguration.attributeExtractors().orElseGet(List::of);
        switch (attributeExtractors.size()) {
            case 0:
                return new NoOpAttributeExtractor();
            case 1:
                return toExtractor(attributeExtractors.get(0), objectMapper);
            default:
                return new CompositeAttributeExtractor(
                        attributeExtractors.stream()
                                .map(property -> toExtractor(property, objectMapper))
                                .toList());
        }
    }

    /**
     * Converts an {@link AttributeExtractorConfiguration} into an {@link AttributeExtractor} instance.
     *
     * @param config the attribute extractor configuration.
     * @param objectMapper the Jackson object mapper.
     * @return a new {@link AttributeExtractor} instance.
     * @throws IllegalArgumentException if the extractor type is unknown.
     */
    private AttributeExtractor toExtractor(final AttributeExtractorConfiguration config, final ObjectMapper objectMapper) {
        switch (config.type()) {
            case "JwtFirstMatchingClaimExtractor":
                return JwtFirstMatchingClaimExtractor.builder()
                        .objectMapper(objectMapper)
                        .claimNames(config.claimNames())
                        .claimKey(config.claimKey().orElse(null))
                        .build();
            case "JwtAllMatchingClaimsExtractor":
                return JwtAllMatchingClaimsExtractor.builder()
                        .objectMapper(objectMapper)
                        .claimNames(config.claimNames())
                        .build();
            default:
                throw new IllegalArgumentException("Unknown AttributeExtractor type: " + config.type());
        }
    }
}
