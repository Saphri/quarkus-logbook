package io.quarkiverse.logbook.runtime;

import static org.zalando.logbook.core.BodyFilters.truncate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import jakarta.enterprise.context.ApplicationScoped;

import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.CorrelationId;
import org.zalando.logbook.HeaderFilter;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.PathFilter;
import org.zalando.logbook.QueryFilter;
import org.zalando.logbook.RequestFilter;
import org.zalando.logbook.ResponseFilter;
import org.zalando.logbook.Sink;
import org.zalando.logbook.Strategy;
import org.zalando.logbook.attributes.AttributeExtractor;
import org.zalando.logbook.core.ChunkingSink;
import org.zalando.logbook.core.DefaultCorrelationId;
import org.zalando.logbook.core.DefaultSink;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration;
import io.quarkiverse.logbook.runtime.spi.QuarkusHttpLogWriter;
import io.quarkus.arc.All;
import io.quarkus.arc.DefaultBean;

public class LogbookProvider {

    private final LogbookConfiguration logbookConfiguration;

    public LogbookProvider(LogbookConfiguration logbookConfiguration) {
        this.logbookConfiguration = logbookConfiguration;
    }

    @ApplicationScoped
    @DefaultBean
    public Logbook logbook(
            final Predicate<HttpRequest> condition,
            final CorrelationId correlationId,
            final @All List<HeaderFilter> headerFilters,
            final @All List<PathFilter> pathFilters,
            final @All List<QueryFilter> queryFilters,
            final @All List<BodyFilter> bodyFilters,
            final @All List<RequestFilter> requestFilters,
            final @All List<ResponseFilter> responseFilters,
            final Strategy strategy,
            final AttributeExtractor attributeExtractor,
            final Sink sink) {
        return Logbook.builder()
                .condition(condition)
                .correlationId(correlationId)
                .headerFilters(headerFilters)
                .queryFilters(queryFilters)
                .pathFilters(pathFilters)
                .bodyFilters(mergeWithTruncation(bodyFilters))
                .requestFilters(requestFilters)
                .responseFilters(responseFilters)
                .strategy(strategy)
                .attributeExtractor(attributeExtractor)
                .sink(sink)
                .build();
    }

    private Collection<BodyFilter> mergeWithTruncation(final List<BodyFilter> bodyFilters) {
        final var maxBodySize = logbookConfiguration.write().maxBodySize();
        if (maxBodySize < 0) {
            return bodyFilters;
        }

        // To ensure that truncation will happen after all other body filters
        final var filters = new ArrayList<BodyFilter>(bodyFilters);
        final var filter = truncate(maxBodySize);
        filters.add(filter);
        return filters;
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
