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

/**
 * LogbookProvider is a CDI producer that provides Logbook components as beans.
 * This class is responsible for creating and configuring the main {@link Logbook} instance,
 * as well as other components like {@link CorrelationId}, {@link HttpLogWriter}, and {@link Sink}.
 * These beans can be customized or replaced by user-provided beans.
 */
public class LogbookProvider {

    /**
     * Creates and configures the main {@link Logbook} bean.
     * This method injects all available Logbook filters and other components to build a fully
     * configured Logbook instance.
     *
     * @param logbookConfiguration the Logbook configuration.
     * @param condition a predicate to conditionally enable or disable logging for a given request.
     * @param correlationId a generator for correlation IDs.
     * @param headerFilters a list of filters for HTTP headers.
     * @param pathFilters a list of filters for URL paths.
     * @param queryFilters a list of filters for URL query parameters.
     * @param bodyFilters a list of filters for HTTP bodies.
     * @param requestFilters a list of filters for HTTP requests.
     * @param responseFilters a list of filters for HTTP responses.
     * @param strategy a strategy for handling request/response logging.
     * @param attributeExtractor an extractor for additional attributes.
     * @param sink the destination for log messages.
     * @return a configured {@link Logbook} instance.
     */
    @ApplicationScoped
    @DefaultBean
    public Logbook logbook(
            final LogbookConfiguration logbookConfiguration,
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
                .bodyFilters(mergeWithTruncation(bodyFilters, logbookConfiguration))
                .requestFilters(requestFilters)
                .responseFilters(responseFilters)
                .strategy(strategy)
                .attributeExtractor(attributeExtractor)
                .sink(sink)
                .build();
    }

    /**
     * Merges the provided body filters with a truncation filter if the max body size is configured.
     * The truncation filter is added to the end of the list to ensure it runs after all other filters.
     *
     * @param bodyFilters the list of body filters to merge.
     * @param logbookConfiguration the Logbook configuration.
     * @return a new list of body filters with the truncation filter added, if applicable.
     */
    private Collection<BodyFilter> mergeWithTruncation(final List<BodyFilter> bodyFilters,
            final LogbookConfiguration logbookConfiguration) {
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

    /**
     * Provides a default {@link CorrelationId} bean.
     * This can be replaced by a custom implementation.
     *
     * @return a new {@link DefaultCorrelationId} instance.
     */
    @ApplicationScoped
    @DefaultBean
    public CorrelationId correlationId() {
        return new DefaultCorrelationId();
    }

    /**
     * Provides a default {@link HttpLogWriter} bean.
     * This can be replaced by a custom implementation.
     *
     * @return a new {@link QuarkusHttpLogWriter} instance.
     */
    @ApplicationScoped
    @DefaultBean
    public HttpLogWriter writer() {
        return new QuarkusHttpLogWriter();
    }

    /**
     * Provides a default {@link Sink} bean.
     * This can be replaced by a custom implementation.
     * The sink can be configured to chunk large log messages.
     *
     * @param configuration the Logbook configuration.
     * @param httpLogFormatter the formatter for HTTP log messages.
     * @param httpLogWriter the writer for HTTP log messages.
     * @return a configured {@link Sink} instance.
     */
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
