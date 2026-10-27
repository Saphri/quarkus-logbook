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
import org.zalando.logbook.HttpHeaders;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration.ObfuscateConfiguration;

@ExtendWith(MockitoExtension.class)
class ObfuscateProviderTest {

    @Mock
    private ObfuscateConfiguration obfuscateConfig;

    private ObfuscateProvider provider;

    @BeforeEach
    void setUp() {
        provider = new ObfuscateProvider();
    }

    @Test
    void shouldObfuscateConfiguredHeaders() {
        when(obfuscateConfig.headers()).thenReturn(Optional.of(List.of("X-Secret")));
        when(obfuscateConfig.replacement()).thenReturn("filtered");
        final var filter = provider.headerFilter(obfuscateConfig);
        final var headers = filter.filter(HttpHeaders.of("X-Secret", "topsecret").update("Keep", "me"));
        assertThat(headers.getFirst("X-Secret")).isEqualTo("filtered");
        assertThat(headers.getFirst("Keep")).isEqualTo("me");
    }

    @Test
    void shouldFallBackToDefaultHeaderFilterWhenNoHeadersConfigured() {
        when(obfuscateConfig.headers()).thenReturn(Optional.empty());
        final var filter = provider.headerFilter(obfuscateConfig);
        final var headers = filter.filter(HttpHeaders.of("Authorization", "Bearer s3cret").update("Keep", "me"));
        // The default obfuscates Authorization with the fixed Logbook replacement, not the configured one.
        assertThat(headers.getFirst("Authorization")).isEqualTo("XXX");
        assertThat(headers.getFirst("Keep")).isEqualTo("me");
    }

    @Test
    void shouldObfuscateConfiguredQueryParameters() {
        when(obfuscateConfig.parameters()).thenReturn(Optional.of(List.of("token")));
        when(obfuscateConfig.replacement()).thenReturn("filtered");
        final var filter = provider.queryFilter(obfuscateConfig);
        assertThat(filter.filter("token=abc&keep=me")).isEqualTo("token=filtered&keep=me");
    }

    @Test
    void shouldObfuscateAccessTokenByDefaultQueryFilter() {
        when(obfuscateConfig.parameters()).thenReturn(Optional.empty());
        final var filter = provider.queryFilter(obfuscateConfig);
        assertThat(filter.filter("access_token=abc&keep=me")).isEqualTo("access_token=XXX&keep=me");
        assertThat(filter.filter("keep=me")).isEqualTo("keep=me");
    }

    @Test
    void shouldObfuscateConfiguredPathSegment() {
        when(obfuscateConfig.paths()).thenReturn(Optional.of(List.of("/api/{masked}")));
        when(obfuscateConfig.replacement()).thenReturn("filtered");
        final var filter = provider.pathFilter(obfuscateConfig);
        assertThat(filter.filter("/api/12345")).isEqualTo("/api/filtered");
        // Paths that do not match the configured expression stay untouched.
        assertThat(filter.filter("/other/12345")).isEqualTo("/other/12345");
    }

    @Test
    void shouldLeavePathUnfilteredWhenNoneConfigured() {
        when(obfuscateConfig.paths()).thenReturn(Optional.empty());
        final var filter = provider.pathFilter(obfuscateConfig);
        assertThat(filter.filter("/api/open")).isEqualTo("/api/open");
    }

    @Test
    void shouldObfuscateOAuthPropertiesInFormEncodedBody() {
        when(obfuscateConfig.jsonBodyFields()).thenReturn(Optional.empty());
        when(obfuscateConfig.replacement()).thenReturn("XXX");
        final var filter = provider.bodyFilter(obfuscateConfig);
        // The OAuth filter only acts on form-url-encoded bodies; the configured JSON fields are none here,
        // so a JSON body must pass through unchanged.
        assertThat(filter.filter("application/x-www-form-urlencoded",
                "client_secret=shh&password=hunter2&refresh_token=r1&keep=me"))
                .isEqualTo("client_secret=XXX&password=XXX&refresh_token=XXX&keep=me");
        assertThat(filter.filter("application/json", "{\"secret\":\"s3cret\"}")).isEqualTo("{\"secret\":\"s3cret\"}");
    }

    @Test
    void shouldObfuscateConfiguredJsonFieldsAndOAuthPropertiesTogether() {
        when(obfuscateConfig.jsonBodyFields()).thenReturn(Optional.of(Set.of("secret")));
        when(obfuscateConfig.replacement()).thenReturn("filtered");
        final var filter = provider.bodyFilter(obfuscateConfig);
        // The merged filter must redact the configured field in JSON bodies, which also anchors the Jackson 2
        // annotations pin of ADR 0001: the JSON mapper behind JacksonJsonFieldBodyFilter introspects them at
        // runtime, and a broken pin fails here instead of in the field.
        assertThat(filter.filter("application/json",
                "{\"secret\":\"s3cret\",\"password\":\"hunter2\",\"keep\":\"me\"}"))
                .isEqualTo("{\"secret\":\"filtered\",\"password\":\"hunter2\",\"keep\":\"me\"}");
        // The OAuth half acts on form-url-encoded bodies; the JSON half redacts configured fields only,
        // so "password" stays intact in the JSON body above but not in the form body below.
        assertThat(filter.filter("application/x-www-form-urlencoded",
                "client_secret=shh&password=hunter2&keep=me"))
                .isEqualTo("client_secret=filtered&password=filtered&keep=me");
    }
}
