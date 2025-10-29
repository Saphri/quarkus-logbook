package io.quarkiverse.logbook.deployment;

import io.quarkiverse.logbook.runtime.LogbookProvider;
import io.quarkiverse.logbook.runtime.providers.AttributeExtractorProvider;
import io.quarkiverse.logbook.runtime.providers.HttpLogFormatterProvider;
import io.quarkiverse.logbook.runtime.providers.ObfuscateProvider;
import io.quarkiverse.logbook.runtime.providers.PredicateProvider;
import io.quarkiverse.logbook.runtime.providers.StrategyProvider;
import io.quarkiverse.logbook.runtime.spi.QuarkusClientLogger;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.RunTimeConfigurationDefaultBuildItem;

/**
 * LogbookExtensionProcessor is a Quarkus build-time extension that configures and registers Logbook components.
 * This processor sets up default logging levels, registers Logbook filters, and makes Logbook providers available
 * as CDI beans for runtime use. It ensures that Logbook is properly integrated with the Quarkus lifecycle.
 */
class LogbookExtensionProcessor {

    private static final String FEATURE = "logbook";

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
     * Sets the default minimum logging level for the Logbook category to TRACE.
     * This ensures that all Logbook messages are captured by the logging framework.
     *
     * @return a {@link RunTimeConfigurationDefaultBuildItem} with the default min-level.
     */
    @BuildStep
    RunTimeConfigurationDefaultBuildItem logbookLevelMinConfig() {
        return new RunTimeConfigurationDefaultBuildItem("quarkus.log.category.\"org.zalando.logbook\".min-level", "TRACE");
    }

    /**
     * Sets the default logging level for the Logbook category to TRACE.
     * This ensures that all Logbook messages are logged at the TRACE level by default.
     *
     * @return a {@link RunTimeConfigurationDefaultBuildItem} with the default level.
     */
    @BuildStep
    RunTimeConfigurationDefaultBuildItem logbookLevelConfig() {
        return new RunTimeConfigurationDefaultBuildItem("quarkus.log.category.\"org.zalando.logbook\".level", "TRACE");
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
    public AdditionalBeanBuildItem producer() {
        return new AdditionalBeanBuildItem(ObfuscateProvider.class, HttpLogFormatterProvider.class,
                StrategyProvider.class, PredicateProvider.class, AttributeExtractorProvider.class, LogbookProvider.class,
                QuarkusClientLogger.class);
    }
}
