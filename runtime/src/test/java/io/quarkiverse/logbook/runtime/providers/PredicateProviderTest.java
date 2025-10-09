package io.quarkiverse.logbook.runtime.providers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.logbook.HttpRequest;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration;

@ExtendWith(MockitoExtension.class)
class PredicateProviderTest {

    @Mock
    private LogbookConfiguration logbookConfiguration;

    @Mock
    private LogbookConfiguration.PredicateConfiguration predicateConfig;

    @Mock
    private LogbookConfiguration.LogbookPredicate includePredicate;

    @Mock
    private LogbookConfiguration.LogbookPredicate excludePredicate;

    @Mock
    private HttpRequest request;

    private PredicateProvider provider;

    @BeforeEach
    void setUp() {
        provider = new PredicateProvider(logbookConfiguration);
        when(logbookConfiguration.predicate()).thenReturn(predicateConfig);
    }

    @Test
    void shouldPermitAllWhenNoPredicates() {
        when(predicateConfig.includes()).thenReturn(Optional.empty());
        when(predicateConfig.excludes()).thenReturn(Optional.empty());

        Predicate<HttpRequest> predicate = provider.condition();
        assertTrue(predicate.test(request));
    }

    @Test
    void shouldIncludePath() {
        when(includePredicate.path()).thenReturn("/api/include");
        when(includePredicate.methods()).thenReturn(Optional.empty());
        when(predicateConfig.includes()).thenReturn(Optional.of(List.of(includePredicate)));
        when(predicateConfig.excludes()).thenReturn(Optional.empty());
        when(request.getPath()).thenReturn("/api/include");

        Predicate<HttpRequest> predicate = provider.condition();
        assertTrue(predicate.test(request));
    }

    @Test
    void shouldExcludePath() {
        when(excludePredicate.path()).thenReturn("/api/exclude");
        when(excludePredicate.methods()).thenReturn(Optional.empty());
        when(predicateConfig.includes()).thenReturn(Optional.empty());
        when(predicateConfig.excludes()).thenReturn(Optional.of(List.of(excludePredicate)));
        when(request.getPath()).thenReturn("/api/exclude");

        Predicate<HttpRequest> predicate = provider.condition();
        assertFalse(predicate.test(request));
    }

    @Test
    void shouldHandleIncludeAndExclude() {
        when(includePredicate.path()).thenReturn("/api/include");
        when(includePredicate.methods()).thenReturn(Optional.empty());
        when(excludePredicate.path()).thenReturn("/api/exclude");
        when(excludePredicate.methods()).thenReturn(Optional.empty());
        when(predicateConfig.includes()).thenReturn(Optional.of(List.of(includePredicate)));
        when(predicateConfig.excludes()).thenReturn(Optional.of(List.of(excludePredicate)));

        Predicate<HttpRequest> predicate = provider.condition();

        when(request.getPath()).thenReturn("/api/include");
        assertTrue(predicate.test(request));

        when(request.getPath()).thenReturn("/api/exclude");
        assertFalse(predicate.test(request));
    }

    @Test
    void shouldFilterByMethod() {
        when(includePredicate.path()).thenReturn("/api/test");
        when(includePredicate.methods()).thenReturn(Optional.of(List.of("GET")));
        when(predicateConfig.includes()).thenReturn(Optional.of(List.of(includePredicate)));
        when(predicateConfig.excludes()).thenReturn(Optional.empty());

        Predicate<HttpRequest> predicate = provider.condition();

        when(request.getPath()).thenReturn("/api/test");
        when(request.getMethod()).thenReturn("GET");
        assertTrue(predicate.test(request));

        when(request.getMethod()).thenReturn("POST");
        assertFalse(predicate.test(request));
    }
}
