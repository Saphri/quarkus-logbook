package io.quarkiverse.logbook.runtime.configuration;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration.FormatConfiguration;
import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration.ObfuscateConfiguration;
import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration.PredicateConfiguration;
import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration.StrategyConfiguration;
import io.quarkiverse.logbook.runtime.configuration.LogbookConfiguration.WriteConfiguration;

/**
 * ConfigurationSections is a CDI provider class that exposes each nested section of the
 * Logbook configuration as its own bean. Only the {@link LogbookConfiguration} root of the
 * {@code @ConfigMapping} is a CDI bean (SmallRye/Quarkus give nested sections none), so these
 * one-line unwraps are the seam through which providers can depend on a single section
 * instead of on the whole {@code quarkus.logbook.*} interface.
 */
public class ConfigurationSections {

    /**
     * Provides the strategy section of the Logbook configuration as a bean.
     *
     * @param logbookConfiguration the Logbook configuration.
     * @return the strategy configuration section.
     */
    @ApplicationScoped
    public StrategyConfiguration strategy(final LogbookConfiguration logbookConfiguration) {
        return logbookConfiguration.strategy();
    }

    /**
     * Provides the write section of the Logbook configuration as a bean.
     *
     * @param logbookConfiguration the Logbook configuration.
     * @return the write configuration section.
     */
    @ApplicationScoped
    public WriteConfiguration write(final LogbookConfiguration logbookConfiguration) {
        return logbookConfiguration.write();
    }

    /**
     * Provides the format section of the Logbook configuration as a bean.
     *
     * @param logbookConfiguration the Logbook configuration.
     * @return the format configuration section.
     */
    @ApplicationScoped
    public FormatConfiguration format(final LogbookConfiguration logbookConfiguration) {
        return logbookConfiguration.format();
    }

    /**
     * Provides the obfuscate section of the Logbook configuration as a bean.
     *
     * @param logbookConfiguration the Logbook configuration.
     * @return the obfuscate configuration section.
     */
    @ApplicationScoped
    public ObfuscateConfiguration obfuscate(final LogbookConfiguration logbookConfiguration) {
        return logbookConfiguration.obfuscate();
    }

    /**
     * Provides the predicate section of the Logbook configuration as a bean.
     *
     * @param logbookConfiguration the Logbook configuration.
     * @return the predicate configuration section.
     */
    @ApplicationScoped
    public PredicateConfiguration predicate(final LogbookConfiguration logbookConfiguration) {
        return logbookConfiguration.predicate();
    }
}
