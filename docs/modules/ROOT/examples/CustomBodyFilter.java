package io.quarkiverse.logbook.examples;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.BodyFilter;

@ApplicationScoped
public class CustomBodyFilter implements BodyFilter {
    @Override
    public String filter(String contentType, String body) {
        return body;
    }
}
