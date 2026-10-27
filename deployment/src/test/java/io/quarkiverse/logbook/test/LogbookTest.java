package io.quarkiverse.logbook.test;

import org.jboss.logmanager.Level;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.deployment.builditem.AdditionalIndexedClassesBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.GeneratedResourceBuildItem;
import io.quarkus.deployment.builditem.LogCategoryBuildItem;
import io.quarkus.deployment.builditem.RunTimeConfigurationDefaultBuildItem;
import io.quarkus.test.QuarkusExtensionTest;

/**
 * Anchors the build-time conventions of the deployment module: the Logbook feature is contributed, the
 * Logbook log category is registered at TRACE including its minimum-level default, the extension indexes its
 * Logbook classes, and the Quarkus REST client logging scope defaults to request-response. These facts are
 * asserted inside a build-chain step that consumes the inspected build items and produces a generated
 * application resource, so the step only runs after every producer of the inspected items and a violation
 * fails the build and therefore the test.
 */
class LogbookTest {

    @RegisterExtension
    static final QuarkusExtensionTest unitTest = new QuarkusExtensionTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class))
            .withEmptyApplication()
            .withConfigurationResource("application.properties")
            .addBuildChainCustomizer(builder -> builder.addBuildStep(context -> {
                Assertions.assertTrue(!context.consumeMulti(AdditionalIndexedClassesBuildItem.class).isEmpty(),
                        "The extension must index at least one Logbook class");
                final var features = context.consumeMulti(FeatureBuildItem.class);
                Assertions.assertTrue(features.stream()
                        .anyMatch(feature -> feature.getName().equals("logbook")),
                        "The logbook feature must be contributed by the extension");
                final var categories = context.consumeMulti(LogCategoryBuildItem.class);
                Assertions.assertTrue(categories.stream()
                        .anyMatch(category -> category.getCategory().equals("org.zalando.logbook")
                                && category.getLevel().equals(Level.TRACE)
                                && category.isSetMinLevelDefault()),
                        "The org.zalando.logbook category must be registered at TRACE with a minimum level default");
                Assertions.assertTrue(categories.stream()
                        .noneMatch(category -> category.getCategory().equals("org.zalando.logbook")
                                && !category.getLevel().equals(Level.TRACE)),
                        "The org.zalando.logbook category must only be registered at TRACE");
                final var defaults = context.consumeMulti(RunTimeConfigurationDefaultBuildItem.class);
                Assertions.assertTrue(defaults.stream()
                        .anyMatch(entry -> entry.getKey().equals("quarkus.rest-client.logging.scope")
                                && entry.getValue().equals("request-response")),
                        "The REST client logging scope must default to request-response");
                Assertions.assertTrue(defaults.stream()
                        .noneMatch(entry -> entry.getKey().equals("quarkus.rest-client.logging.scope")
                                && !entry.getValue().equals("request-response")),
                        "The REST client logging scope must only default to request-response");
                final var content = "logbook build item assertions";
                context.produce(new GeneratedResourceBuildItem(
                        "META-INF/io.quarkiverse.logbook.build-item-assertions.txt", content.getBytes()));
            })
                    .consumes(AdditionalIndexedClassesBuildItem.class)
                    .consumes(FeatureBuildItem.class)
                    .consumes(LogCategoryBuildItem.class)
                    .consumes(RunTimeConfigurationDefaultBuildItem.class)
                    .produces(GeneratedResourceBuildItem.class)
                    .build());

    @Test
    void shouldBuildWithAssertedExtensionItems() {
        // The build-chain customizer above inspects the build items contributed by the extension while
        // building the application; a missing or wrong item fails the build and makes this test fail.
    }
}
