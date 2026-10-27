package io.quarkiverse.logbook.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.HttpHeaders;
import org.zalando.logbook.HttpRequest;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.attributes.NoOpAttributeExtractor;
import org.zalando.logbook.core.ChunkingSink;
import org.zalando.logbook.core.DefaultCorrelationId;
import org.zalando.logbook.core.DefaultSink;
import org.zalando.logbook.core.DefaultStrategy;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration.LogbookPredicate;
import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration.ObfuscateConfiguration;
import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration.PredicateConfiguration;
import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration.WriteConfiguration;
import io.quarkiverse.logbook.runtime.providers.ObfuscateProvider;
import io.quarkiverse.logbook.runtime.providers.PredicateProvider;
import io.quarkiverse.logbook.runtime.spi.LocalRequest;
import io.quarkiverse.logbook.runtime.spi.QuarkusHttpLogWriter;
import io.quarkiverse.logbook.runtime.spi.RemoteResponse;
import io.vertx.core.http.HttpMethod;

@ExtendWith(MockitoExtension.class)
class LogbookProviderTest {

    @Mock
    private WriteConfiguration writeConfig;

    @Mock
    private PredicateConfiguration predicateConfig;

    @Mock
    private LogbookPredicate includePredicate;

    @Mock
    private LogbookPredicate excludePredicate;

    @Mock
    private ObfuscateConfiguration obfuscateConfig;

    private LogbookProvider provider;

    private ArrayList<String> messages;

    private RecordingSinkHelper recorder;

    private LocalRequest request;

    private RemoteResponse response;

    @BeforeEach
    void setUp() {
        provider = new LogbookProvider();
        messages = new ArrayList<String>();
        recorder = new RecordingSinkHelper(messages);
        request = new LocalRequest(java.net.URI.create("http://example.com/api"), io.vertx.core.http.HttpMethod.GET,
                HttpHeaders.empty(), "hello12345678world".getBytes());
        response = new RemoteResponse(200, HttpHeaders.empty(), "hello12345678world".getBytes());
    }

    private Logbook assemble(final Predicate<HttpRequest> condition, final List<BodyFilter> bodyFilters) {
        return provider.logbook(writeConfig, condition, new DefaultCorrelationId(), List.of(), List.of(), List.of(),
                bodyFilters, List.of(), List.of(), new DefaultStrategy(), new NoOpAttributeExtractor(),
                new DefaultSink(recorder, recorder));
    }

    @Test
    void shouldTruncateAfterCustomBodyFilter() throws java.io.IOException {
        final BodyFilter shorten = (final String contentType, final String body) -> body.replaceAll("hello", "HI");
        when(writeConfig.maxBodySize()).thenReturn(10);
        final var logbook = assemble(request -> true, List.of(shorten));
        logbook.process(request).write().process(response).write();
        assertThat(messages).hasSize(2);
        // The custom filter shortens the body from 18 to 15 characters first, and only afterwards
        // the result is truncated to the configured maximum size. Truncating first would have cut
        // the unfiltered body and left "hello" intact, so this also anchors the ordering.
        assertThat(messages.get(1)).isEqualTo(java.util.Arrays.toString("HI12345678...".getBytes()));
    }

    @Test
    void shouldTruncateWithoutOtherBodyFilters() throws java.io.IOException {
        when(writeConfig.maxBodySize()).thenReturn(10);
        final var logbook = assemble(request -> true, List.of());
        logbook.process(request).write();
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo(java.util.Arrays.toString("hello12345...".getBytes()));
    }

    @Test
    void shouldNotTruncateWhenMaxBodySizeIsDisabled() throws java.io.IOException {
        when(writeConfig.maxBodySize()).thenReturn(-1);
        final var logbook = assemble(request -> true, List.of());
        logbook.process(request).write();
        assertThat(messages.get(0)).isEqualTo(java.util.Arrays.toString("hello12345678world".getBytes()));
    }

    @Test
    void shouldTruncateAfterObfuscatingJsonBodyFields() throws java.io.IOException {
        when(obfuscateConfig.jsonBodyFields()).thenReturn(Optional.of(Set.of("password")));
        when(obfuscateConfig.replacement()).thenReturn("filtered");
        final var obfuscate = new ObfuscateProvider().bodyFilter(obfuscateConfig);
        final var body = "{\"password\":\"supersecretvalue1234567890\"}";
        final var jsonRequest = new LocalRequest(java.net.URI.create("http://example.com/api"),
                io.vertx.core.http.HttpMethod.GET,
                HttpHeaders.of("Content-Type", "application/json"), body.getBytes());
        when(writeConfig.maxBodySize()).thenReturn(20);
        final var logbook = assemble(request -> true, List.of(obfuscate));
        logbook.process(jsonRequest).write();
        // The password value is obfuscated first (41 characters become 23), and only the obfuscated
        // body is truncated: the cut lands inside the replacement, which truncating first could not do.
        assertThat(messages.get(0)).isEqualTo(java.util.Arrays.toString("{\"password\":\"filtere...".getBytes()));
    }

    @Test
    void shouldNotLogWhenExclusionOverridesInclusionInAssembledLogbook() throws java.io.IOException {
        when(includePredicate.path()).thenReturn("/api");
        when(includePredicate.methods()).thenReturn(Optional.empty());
        when(excludePredicate.path()).thenReturn("/api");
        when(excludePredicate.methods()).thenReturn(Optional.empty());
        when(predicateConfig.includes()).thenReturn(Optional.of(List.of(includePredicate)));
        when(predicateConfig.excludes()).thenReturn(Optional.of(List.of(excludePredicate)));
        final var condition = new PredicateProvider().condition(predicateConfig);
        when(writeConfig.maxBodySize()).thenReturn(-1);
        final var logbook = assemble(condition, List.of());
        logbook.process(request).write().process(response).write();
        // Both rules match the request; the exclusion must win, so nothing is logged. If includes
        // took precedence this exchange would write two messages instead of none.
        assertThat(messages).isEmpty();
    }

    @Test
    void shouldNotLogWhenConditionDenies() throws java.io.IOException {
        final Predicate<HttpRequest> deny = request -> false;
        when(writeConfig.maxBodySize()).thenReturn(-1);
        final var logbook = assemble(deny, List.of());
        logbook.process(request).write().process(response).write();
        assertThat(messages).isEmpty();
    }

    @Test
    void shouldLogWhenConditionAllows() throws java.io.IOException {
        final Predicate<HttpRequest> allow = request -> true;
        when(writeConfig.maxBodySize()).thenReturn(-1);
        final var logbook = assemble(allow, List.of());
        logbook.process(request).write().process(response).write();
        assertThat(messages).hasSize(2);
    }

    @Test
    void shouldNotWrapSinkWhenChunkSizeIsZero() throws java.io.IOException {
        when(writeConfig.chunkSize()).thenReturn(0);
        final var sink = provider.sink(writeConfig, recorder, recorder);
        assertThat(sink).isInstanceOf(DefaultSink.class);
    }

    @Test
    void shouldWrapSinkWithChunkingWhenChunkSizeIsPositive() throws java.io.IOException {
        when(writeConfig.chunkSize()).thenReturn(4);
        final var sink = provider.sink(writeConfig, recorder, recorder);
        assertThat(sink).isInstanceOf(ChunkingSink.class);
    }

    @Test
    void shouldProvideDefaultCorrelationIdBean() throws java.io.IOException {
        assertThat(provider.correlationId()).isInstanceOf(DefaultCorrelationId.class);
    }

    @Test
    void shouldProvideQuarkusWriterBean() throws java.io.IOException {
        assertThat(provider.writer()).isInstanceOf(QuarkusHttpLogWriter.class);
    }
}
