package io.quarkiverse.logbook.runtime.providers;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.logbook.attributes.AttributeExtractor;
import org.zalando.logbook.attributes.NoOpAttributeExtractor;
import org.zalando.logbook.core.attributes.CompositeAttributeExtractor;
import org.zalando.logbook.core.attributes.JwtAllMatchingClaimsExtractor;
import org.zalando.logbook.core.attributes.JwtFirstMatchingClaimExtractor;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration;

@ExtendWith(MockitoExtension.class)
class AttributeExtractorProviderTest {

    @Mock
    private LogbookConfiguration logbookConfiguration;

    @Mock
    private LogbookConfiguration.AttributeExtractorConfiguration firstExtractorConfig;

    @Mock
    private LogbookConfiguration.AttributeExtractorConfiguration secondExtractorConfig;

    private ObjectMapper objectMapper = new ObjectMapper();

    private AttributeExtractorProvider provider;

    @BeforeEach
    void setUp() {
        provider = new AttributeExtractorProvider(logbookConfiguration, objectMapper);
    }

    @Test
    void shouldProvideNoOpExtractorWhenNoExtractorsConfigured() {
        when(logbookConfiguration.attributeExtractors()).thenReturn(Optional.empty());

        AttributeExtractor extractor = provider.attributeExtractor();

        assertNotNull(extractor, "AttributeExtractor should not be null");
        assertInstanceOf(NoOpAttributeExtractor.class, extractor, "Should be an instance of NoOpAttributeExtractor");
    }

    @Test
    void shouldProvideJwtFirstMatchingClaimExtractor() {
        when(firstExtractorConfig.type()).thenReturn("JwtFirstMatchingClaimExtractor");
        when(firstExtractorConfig.claimNames()).thenReturn(Collections.singletonList("sub"));
        when(firstExtractorConfig.claimKey()).thenReturn(Optional.of("subject"));
        when(logbookConfiguration.attributeExtractors()).thenReturn(Optional.of(List.of(firstExtractorConfig)));

        AttributeExtractor extractor = provider.attributeExtractor();

        assertNotNull(extractor);
        assertInstanceOf(JwtFirstMatchingClaimExtractor.class, extractor);
    }

    @Test
    void shouldProvideJwtAllMatchingClaimsExtractor() {
        when(firstExtractorConfig.type()).thenReturn("JwtAllMatchingClaimsExtractor");
        when(firstExtractorConfig.claimNames()).thenReturn(Collections.singletonList("aud"));
        when(logbookConfiguration.attributeExtractors()).thenReturn(Optional.of(List.of(firstExtractorConfig)));

        AttributeExtractor extractor = provider.attributeExtractor();

        assertNotNull(extractor);
        assertInstanceOf(JwtAllMatchingClaimsExtractor.class, extractor);
    }

    @Test
    void shouldProvideCompositeExtractorForMultipleConfigurations() {
        when(firstExtractorConfig.type()).thenReturn("JwtFirstMatchingClaimExtractor");
        when(firstExtractorConfig.claimNames()).thenReturn(Collections.singletonList("sub"));
        when(firstExtractorConfig.claimKey()).thenReturn(Optional.of("subject"));
        when(secondExtractorConfig.type()).thenReturn("JwtAllMatchingClaimsExtractor");
        when(secondExtractorConfig.claimNames()).thenReturn(Collections.singletonList("aud"));
        when(logbookConfiguration.attributeExtractors())
                .thenReturn(Optional.of(List.of(firstExtractorConfig, secondExtractorConfig)));

        AttributeExtractor extractor = provider.attributeExtractor();

        assertNotNull(extractor);
        assertInstanceOf(CompositeAttributeExtractor.class, extractor);
    }
}
