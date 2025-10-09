package io.quarkiverse.logbook.examples;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.attributes.AttributeExtractor;
import org.zalando.logbook.attributes.HttpAttributes;

@ApplicationScoped
public class CustomAttributeExtractor implements AttributeExtractor {
    @Override
    public HttpAttributes extract(HttpRequest request) {
        return HttpAttributes.EMPTY;
    }
}
