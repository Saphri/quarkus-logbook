package io.quarkiverse.logbook.runtime.providers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.core.CurlHttpLogFormatter;
import org.zalando.logbook.core.DefaultHttpLogFormatter;
import org.zalando.logbook.core.SplunkHttpLogFormatter;
import org.zalando.logbook.json.JsonHttpLogFormatter;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration.FormatConfiguration;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class HttpLogFormatterProviderTest {

    @Mock
    private FormatConfiguration formatConfig;

    private JsonMapper jsonMapper;

    private HttpLogFormatterProvider provider;

    @BeforeEach
    void setUp() {
        provider = new HttpLogFormatterProvider();
        jsonMapper = JsonMapper.builder().build();
    }

    @Test
    void shouldProvideDefaultFormatter() {
        when(formatConfig.style()).thenReturn("http");
        HttpLogFormatter formatter = provider.format(formatConfig, jsonMapper);
        assertThat(formatter).isNotNull().isInstanceOf(DefaultHttpLogFormatter.class);
    }

    @Test
    void shouldProvideCurlFormatter() {
        when(formatConfig.style()).thenReturn("curl");
        HttpLogFormatter formatter = provider.format(formatConfig, jsonMapper);
        assertThat(formatter).isNotNull().isInstanceOf(CurlHttpLogFormatter.class);
    }

    @Test
    void shouldProvideSplunkFormatter() {
        when(formatConfig.style()).thenReturn("splunk");
        HttpLogFormatter formatter = provider.format(formatConfig, jsonMapper);
        assertThat(formatter).isNotNull().isInstanceOf(SplunkHttpLogFormatter.class);
    }

    @Test
    void shouldProvideJsonFormatter() {
        when(formatConfig.style()).thenReturn("json");
        HttpLogFormatter formatter = provider.format(formatConfig, jsonMapper);
        assertThat(formatter).isNotNull().isInstanceOf(JsonHttpLogFormatter.class);
    }

    @Test
    void shouldThrowExceptionForUnknownStyle() {
        when(formatConfig.style()).thenReturn("unknown-style");
        assertThatThrownBy(() -> provider.format(formatConfig, jsonMapper))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
