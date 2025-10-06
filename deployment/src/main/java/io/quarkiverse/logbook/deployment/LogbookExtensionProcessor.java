package io.quarkiverse.logbook.deployment;

import io.quarkiverse.logbook.runtime.LogbookServerProvider;
import io.quarkiverse.logbook.runtime.providers.HttpLogFormatterProvider;
import io.quarkiverse.logbook.runtime.providers.StrategyProvider;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.RunTimeConfigurationDefaultBuildItem;

class LogbookExtensionProcessor {

    private static final String FEATURE = "logbook";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    RunTimeConfigurationDefaultBuildItem logbookLevelMinConfig() {
        return new RunTimeConfigurationDefaultBuildItem("quarkus.log.category.\"org.zalando.logbook\".min-level", "TRACE");
    }

    @BuildStep
    RunTimeConfigurationDefaultBuildItem logbookLevelConfig() {
        return new RunTimeConfigurationDefaultBuildItem("quarkus.log.category.\"org.zalando.logbook\".level", "TRACE");
    }

    @BuildStep
    AdditionalIndexedClassesBuildItem logbookIndexedClasses() {
        return new AdditionalIndexedClassesBuildItem(
                "org.zalando.logbook.Logbook",
                "org.zalando.logbook.jaxrs.LogbookServerFilter");
    }

    @BuildStep
    public AdditionalBeanBuildItem producer() {
        return new AdditionalBeanBuildItem(HttpLogFormatterProvider.class, StrategyProvider.class, LogbookServerProvider.class);
    }
}
