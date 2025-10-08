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

public class AttributeExtractorProvider {

    private final LogbookConfiguration logbookConfiguration;
    private final ObjectMapper objectMapper;

    public AttributeExtractorProvider(final LogbookConfiguration logbookConfiguration, final ObjectMapper objectMapper) {
        this.logbookConfiguration = logbookConfiguration;
        this.objectMapper = objectMapper;
    }

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
