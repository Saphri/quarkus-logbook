package io.quarkiverse.logbook.runtime.providers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.logbook.HttpLogFormatter;
import org.zalando.logbook.core.CurlHttpLogFormatter;
import org.zalando.logbook.core.DefaultHttpLogFormatter;
import org.zalando.logbook.core.SplunkHttpLogFormatter;
import org.zalando.logbook.json.JsonHttpLogFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({ "rawtypes", "unchecked" })
class HttpLogFormatterProviderTest {

    @Mock
    private LogbookConfiguration logbookConfiguration;

    @Mock
    private LogbookConfiguration.FormatConfiguration formatConfig;

    private HttpLogFormatterProvider provider;

    @BeforeEach
    void setUp() {
        provider = new HttpLogFormatterProvider();
        when(logbookConfiguration.format()).thenReturn(formatConfig);
    }

    @Test
    void shouldProvideDefaultFormatter() {
        when(formatConfig.style()).thenReturn("http");
        HttpLogFormatter formatter = provider.format(logbookConfiguration);
        assertThat(formatter).isNotNull().isInstanceOf(DefaultHttpLogFormatter.class);
    }

    @Test
    void shouldProvideCurlFormatter() {
        when(formatConfig.style()).thenReturn("curl");
        HttpLogFormatter formatter = provider.format(logbookConfiguration);
        assertThat(formatter).isNotNull().isInstanceOf(CurlHttpLogFormatter.class);
    }

    @Test
    void shouldProvideSplunkFormatter() {
        when(formatConfig.style()).thenReturn("splunk");
        HttpLogFormatter formatter = provider.format(logbookConfiguration);
        assertThat(formatter).isNotNull().isInstanceOf(SplunkHttpLogFormatter.class);
    }

    @Test
    void shouldProvideJsonFormatter() {
        try (MockedStatic<CDI> cdi = Mockito.mockStatic(CDI.class)) {
            CDI cdiInstance = Mockito.mock(CDI.class);
            Instance objectMapperInstance = Mockito.mock(Instance.class);
            when(objectMapperInstance.get()).thenReturn(new ObjectMapper());
            when(cdiInstance.select(ObjectMapper.class)).thenReturn(objectMapperInstance);
            cdi.when(CDI::current).thenReturn(cdiInstance);

            when(formatConfig.style()).thenReturn("json");
            HttpLogFormatter formatter = provider.format(logbookConfiguration);
            assertThat(formatter).isNotNull().isInstanceOf(JsonHttpLogFormatter.class);
        }
    }

    @Test
    void shouldThrowExceptionForUnknownStyle() {
        when(formatConfig.style()).thenReturn("unknown-style");
        assertThatThrownBy(() -> provider.format(logbookConfiguration))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
