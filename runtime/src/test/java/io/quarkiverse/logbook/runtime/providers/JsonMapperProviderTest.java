package io.quarkiverse.logbook.runtime.providers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
class JsonMapperProviderTest {

    @Test
    void shouldProvideDefaultJsonMapper() {
        JsonMapper jsonMapper = new JsonMapperProvider().jsonMapper();

        assertThat(jsonMapper).isNotNull().isInstanceOf(JsonMapper.class);
    }
}
