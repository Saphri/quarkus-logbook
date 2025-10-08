package io.quarkiverse.logbook.runtime.providers;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.attributes.AttributeExtractor;
import org.zalando.logbook.attributes.NoOpAttributeExtractor;

import io.quarkus.arc.DefaultBean;

public class AttributeExtractorProvider {

    @ApplicationScoped
    @DefaultBean
    public AttributeExtractor attributeExtractor() {
        return new NoOpAttributeExtractor();
    }
}
