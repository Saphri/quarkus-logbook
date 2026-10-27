package io.quarkiverse.logbook.deployment;

import org.jboss.logmanager.Level;

import io.quarkiverse.logbook.runtime.LogbookProvider;
import io.quarkiverse.logbook.runtime.configuration.ConfigurationSections;
import io.quarkiverse.logbook.runtime.providers.AttributeExtractorProvider;
import io.quarkiverse.logbook.runtime.providers.HttpLogFormatterProvider;
import io.quarkiverse.logbook.runtime.providers.JsonMapperProvider;
import io.quarkiverse.logbook.runtime.providers.ObfuscateProvider;
import io.quarkiverse.logbook.runtime.providers.PredicateProvider;
import io.quarkiverse.logbook.runtime.providers.StrategyProvider;
import io.quarkiverse.logbook.runtime.spi.QuarkusClientLogger;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.LogCategoryBuildItem;
import io.quarkus.deployment.builditem.RunTimeConfigurationDefaultBuildItem;

/**
 * LogbookExtensionProcessor is a Quarkus build-time extension that configures and registers Logbook components.
 * This processor sets up default logging levels, registers Logbook filters, and makes Logbook providers available
 * as CDI beans for runtime use. It ensures that Logbook is properly integrated with the Quarkus lifecycle.
 */
class LogbookExtensionProcessor {

    private static final String FEATURE = "logbook";

    private static final String LOGBOOK_CATEGORY = "org.zalando.logbook";

    /**
     * Creates a {@link FeatureBuildItem} to identify the Logbook extension.
     * This is a standard part of Quarkus extension development.
     *
     * @return a new {@link FeatureBuildItem} for Logbook.
     */
    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    /**
     * Establishes the default level of the Logbook category as TRACE, and the same default for the category
     * minimum level. The {@link LogCategoryBuildItem} is the only place that owns both: Quarkus turns it into
     * the runtime {@code quarkus.log.category."org.zalando.logbook".level} default and into a minimum level
     * default, which keeps the level from being promoted to DEBUG and swallowing every Logbook entry.
     *
     * A built application filters further down than that. Its {@code LoggerNode} minimum level is fixed at
     * startup from the {@code logging.initial-configurator.min-level} property, which Quarkus computes at
     * build time out of the build time logging configuration; an extension cannot contribute to that
     * configuration, so the floor it leaves for the Logbook category is the root minimum level. An
     * application that wants Logbook entries in a built application must therefore set the build time
     * {@code quarkus.log.category."org.zalando.logbook".min-level} to TRACE as well, and let its log
     * handlers accept TRACE; see ADR 0002.
     *
     * @return a {@link LogCategoryBuildItem} for the Logbook category at TRACE, including its minimum level.
     */
    @BuildStep
    LogCategoryBuildItem logbookLogLevel() {
        return new LogCategoryBuildItem(LOGBOOK_CATEGORY, Level.TRACE, true);
    }

    /**
     * Configures the Quarkus REST client to log both requests and responses by default.
     * This is necessary for Logbook to intercept and log REST client traffic.
     *
     * @return a {@link RunTimeConfigurationDefaultBuildItem} to enable REST client logging.
     */
    @BuildStep
    RunTimeConfigurationDefaultBuildItem logbookRestClient() {
        return new RunTimeConfigurationDefaultBuildItem("quarkus.rest-client.logging.scope", "request-response");
    }

    /**
     * Registers Logbook classes for indexing by Quarkus. This is required for classes
     * that are used reflectively or need to be discovered at build time.
     *
     * @return an {@link AdditionalIndexedClassesBuildItem} with the Logbook classes.
     */
    @BuildStep
    AdditionalIndexedClassesBuildItem logbookIndexedClasses() {
        return new AdditionalIndexedClassesBuildItem(
                "org.zalando.logbook.Logbook",
                "org.zalando.logbook.jaxrs.LogbookServerFilter");
    }

    /**
     * Registers Logbook providers as additional CDI beans.
     * This makes the providers available for injection at runtime, allowing for customization
     * of Logbook's behavior.
     *
     * @return an {@link AdditionalBeanBuildItem} with the Logbook provider classes.
     */
    @BuildStep
    public AdditionalBeanBuildItem providers() {
        return new AdditionalBeanBuildItem(ObfuscateProvider.class, HttpLogFormatterProvider.class,
                StrategyProvider.class, PredicateProvider.class, AttributeExtractorProvider.class, JsonMapperProvider.class,
                LogbookProvider.class, QuarkusClientLogger.class, ConfigurationSections.class);
    }
}
