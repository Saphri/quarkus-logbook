package io.quarkiverse.logbook.runtime.providers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.logbook.Strategy;
import org.zalando.logbook.core.BodyOnlyIfStatusAtLeastStrategy;
import org.zalando.logbook.core.DefaultStrategy;
import org.zalando.logbook.core.StatusAtLeastStrategy;
import org.zalando.logbook.core.WithoutBodyStrategy;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration;

@ExtendWith(MockitoExtension.class)
class StrategyProviderTest {

    @Mock
    private LogbookConfiguration logbookConfiguration;

    @Mock
    private LogbookConfiguration.StrategyConfiguration strategyConfig;

    private StrategyProvider provider;

    @BeforeEach
    void setUp() {
        provider = new StrategyProvider();
        when(logbookConfiguration.strategy()).thenReturn(strategyConfig);
    }

    @Test
    void shouldProvideDefaultStrategy() {
        when(strategyConfig.strategy()).thenReturn("default");
        Strategy strategy = provider.strategy(logbookConfiguration);
        assertThat(strategy).isNotNull().isInstanceOf(DefaultStrategy.class);
    }

    @Test
    void shouldProvideWithoutBodyStrategy() {
        when(strategyConfig.strategy()).thenReturn("without-body");
        Strategy strategy = provider.strategy(logbookConfiguration);
        assertThat(strategy).isNotNull().isInstanceOf(WithoutBodyStrategy.class);
    }

    @Test
    void shouldProvideStatusAtLeastStrategy() {
        when(strategyConfig.strategy()).thenReturn("status-at-least");
        when(logbookConfiguration.minimumStatus()).thenReturn(400);
        Strategy strategy = provider.strategy(logbookConfiguration);
        assertThat(strategy).isNotNull().isInstanceOf(StatusAtLeastStrategy.class);
    }

    @Test
    void shouldProvideBodyOnlyIfStatusAtLeastStrategy() {
        when(strategyConfig.strategy()).thenReturn("body-only-if-status-at-least");
        when(logbookConfiguration.minimumStatus()).thenReturn(500);
        Strategy strategy = provider.strategy(logbookConfiguration);
        assertThat(strategy).isNotNull().isInstanceOf(BodyOnlyIfStatusAtLeastStrategy.class);
    }

    @Test
    void shouldThrowExceptionForUnknownStrategy() {
        when(strategyConfig.strategy()).thenReturn("unknown-strategy");
        assertThatThrownBy(() -> provider.strategy(logbookConfiguration))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
