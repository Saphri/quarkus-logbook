package io.quarkiverse.logbook.runtime.providers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.logbook.HeaderFilter;
import org.zalando.logbook.PathFilter;
import org.zalando.logbook.QueryFilter;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration;

@ExtendWith(MockitoExtension.class)
class ObfuscateProviderTest {

    @Mock
    private LogbookConfiguration logbookConfiguration;

    @Mock
    private LogbookConfiguration.ObfuscateConfiguration obfuscateConfig;

    private ObfuscateProvider provider;

    @BeforeEach
    void setUp() {
        provider = new ObfuscateProvider(logbookConfiguration);
        when(logbookConfiguration.obfuscate()).thenReturn(obfuscateConfig);
    }

    @Test
    void shouldProvideDefaultHeaderFilter() {
        when(obfuscateConfig.headers()).thenReturn(Optional.empty());
        HeaderFilter filter = provider.headerFilter();
        assertNotNull(filter);
    }

    @Test
    void shouldProvideCustomHeaderFilter() {
        when(obfuscateConfig.headers()).thenReturn(Optional.of(List.of("X-Secret")));
        when(obfuscateConfig.replacement()).thenReturn("filtered");
        HeaderFilter filter = provider.headerFilter();
        assertNotNull(filter);
    }

    @Test
    void shouldProvideDefaultQueryFilter() {
        when(obfuscateConfig.parameters()).thenReturn(Optional.empty());
        QueryFilter filter = provider.queryFilter();
        assertNotNull(filter);
    }

    @Test
    void shouldProvideCustomQueryFilter() {
        when(obfuscateConfig.parameters()).thenReturn(Optional.of(List.of("token")));
        when(obfuscateConfig.replacement()).thenReturn("filtered");
        QueryFilter filter = provider.queryFilter();
        assertNotNull(filter);
    }

    @Test
    void shouldProvideDefaultPathFilter() {
        when(obfuscateConfig.paths()).thenReturn(Optional.empty());
        PathFilter filter = provider.pathFilter();
        assertNotNull(filter);
        assertEquals(PathFilter.none(), filter);
    }

    @Test
    void shouldProvideCustomPathFilter() {
        when(obfuscateConfig.paths()).thenReturn(Optional.of(List.of("/api/secret")));
        when(obfuscateConfig.replacement()).thenReturn("filtered");
        PathFilter filter = provider.pathFilter();
        assertNotNull(filter);
    }
}
