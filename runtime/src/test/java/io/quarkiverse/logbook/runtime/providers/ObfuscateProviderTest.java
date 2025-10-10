package io.quarkiverse.logbook.runtime.providers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.HeaderFilter;
import org.zalando.logbook.PathFilter;
import org.zalando.logbook.QueryFilter;
import org.zalando.logbook.json.JacksonJsonFieldBodyFilter;

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
        assertThat(filter).isNotNull();
    }

    @Test
    void shouldProvideCustomHeaderFilter() {
        when(obfuscateConfig.headers()).thenReturn(Optional.of(List.of("X-Secret")));
        when(obfuscateConfig.replacement()).thenReturn("filtered");
        HeaderFilter filter = provider.headerFilter();
        assertThat(filter).isNotNull();
    }

    @Test
    void shouldProvideDefaultQueryFilter() {
        when(obfuscateConfig.parameters()).thenReturn(Optional.empty());
        QueryFilter filter = provider.queryFilter();
        assertThat(filter).isNotNull();
    }

    @Test
    void shouldProvideCustomQueryFilter() {
        when(obfuscateConfig.parameters()).thenReturn(Optional.of(List.of("token")));
        when(obfuscateConfig.replacement()).thenReturn("filtered");
        QueryFilter filter = provider.queryFilter();
        assertThat(filter).isNotNull();
    }

    @Test
    void shouldProvideDefaultPathFilter() {
        when(obfuscateConfig.paths()).thenReturn(Optional.empty());
        PathFilter filter = provider.pathFilter();
        assertThat(filter).isNotNull().isEqualTo(PathFilter.none());
    }

    @Test
    void shouldProvideCustomPathFilter() {
        when(obfuscateConfig.paths()).thenReturn(Optional.of(List.of("/api/secret")));
        when(obfuscateConfig.replacement()).thenReturn("filtered");
        PathFilter filter = provider.pathFilter();
        assertThat(filter).isNotNull();
    }

    @Test
    void shouldProvideDefaultBodyFilter() {
        when(obfuscateConfig.jsonBodyFields()).thenReturn(Optional.empty());
        BodyFilter filter = provider.bodyFilter();
        assertThat(filter).isNotNull().isEqualTo(BodyFilter.none());
    }

    @Test
    void shouldProvideCustomBodyFilter() {
        when(obfuscateConfig.jsonBodyFields()).thenReturn(Optional.of(Set.of("password")));
        when(obfuscateConfig.replacement()).thenReturn("filtered");
        BodyFilter filter = provider.bodyFilter();
        assertThat(filter).isNotNull().isInstanceOf(JacksonJsonFieldBodyFilter.class);
    }
}
