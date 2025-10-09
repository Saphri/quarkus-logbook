package io.quarkiverse.logbook.examples;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.PathFilter;

@ApplicationScoped
public class CustomPathFilter implements PathFilter {
    @Override
    public String filter(String path) {
        return path;
    }
}
