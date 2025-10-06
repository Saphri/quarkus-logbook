package io.quarkiverse.logbook.runtime;

import static org.zalando.logbook.core.Conditions.exclude;
import static org.zalando.logbook.core.Conditions.requestTo;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.CorrelationId;
import org.zalando.logbook.HeaderFilter;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.PathFilter;
import org.zalando.logbook.QueryFilter;
import org.zalando.logbook.Sink;
import org.zalando.logbook.Strategy;
import org.zalando.logbook.core.ChunkingSink;
import org.zalando.logbook.core.DefaultCorrelationId;
import org.zalando.logbook.core.DefaultSink;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration;
import io.quarkiverse.logbook.runtime.spi.QuarkusHttpLogWriter;
import io.quarkus.arc.All;
import io.quarkus.arc.DefaultBean;

public class LogbookServerProvider {

    @ApplicationScoped
    @DefaultBean
    public Logbook serverLogbook(
            final CorrelationId correlationId,
            final @All List<HeaderFilter> headerFilters,
            final @All List<PathFilter> pathFilters,
            final @All List<QueryFilter> queryFilters,
            final Strategy strategy,
            final Sink sink) {
        return Logbook.builder()
                .condition(exclude(requestTo("/q/**")))
                .correlationId(correlationId)
                .headerFilters(headerFilters)
                .queryFilters(queryFilters)
                .pathFilters(pathFilters)
                .strategy(strategy)
                .sink(sink)
                .build();
    }

    @ApplicationScoped
    @DefaultBean
    public CorrelationId correlationId() {
        return new DefaultCorrelationId();
    }

    @ApplicationScoped
    @DefaultBean
    public HttpLogWriter writer() {
        return new QuarkusHttpLogWriter();
    }

    @ApplicationScoped
    @DefaultBean
    public Sink sink(final LogbookConfiguration configuration, final HttpLogFormatter httpLogFormatter,
            final HttpLogWriter httpLogWriter) {
        final var sink = new DefaultSink(
                httpLogFormatter,
                httpLogWriter);
        if (configuration.write().chunkSize() > 0) {
            return new ChunkingSink(sink, configuration.write().chunkSize());
        } else {
            return sink;
        }
    }
}
