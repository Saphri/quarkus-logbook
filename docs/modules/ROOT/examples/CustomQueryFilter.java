package io.quarkiverse.logbook.examples;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.QueryFilter;

@ApplicationScoped
public class CustomQueryFilter implements QueryFilter {
    @Override
    public String filter(String query) {
        return query;
    }
}
