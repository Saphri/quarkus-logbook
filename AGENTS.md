# Copilot instructions

## Build and test

This is a Maven project targeting Java 17. Use the Maven wrapper:

- Full build and test suite, including the docs and integration-test modules: `./mvnw clean install`
- The CI build uses `./mvnw -B clean install -Dno-format`.
- Run runtime unit tests: `./mvnw -pl runtime test`
- Run one runtime test class: `./mvnw -pl runtime -Dtest=ObfuscateProviderTest test`
- Run one deployment test class: `./mvnw -pl deployment -am -Dtest=LogbookTest -Dsurefire.failIfNoSpecifiedTests=false test`
- Run the integration-test module's Surefire test class: `./mvnw -pl integration-tests -am -Dtest=LogbookResourceTest -Dsurefire.failIfNoSpecifiedTests=false test`
- Run the Failsafe integration test: `./mvnw -pl integration-tests -am -DskipITs=false -Dit.test=LogbookResourceIT verify`

The `integration-tests` module skips Failsafe tests by default. `LogbookResourceTest` runs with `@QuarkusTest`; `LogbookResourceIT` runs the same tests against the built application with `@QuarkusIntegrationTest`. There is no separate repository lint command; the CI workflow is `.github/workflows/build.yml`.

## Architecture

The root POM builds the Quarkus extension from two core modules: `runtime` holds the runtime configuration and CDI producers, and `deployment` provides Quarkus build steps. The `integration-tests` modules are added by default Maven profiles.

At build time, `LogbookExtensionProcessor` registers the runtime producers and supporting classes as CDI beans, indexes Logbook types, and sets Quarkus logging and REST-client defaults. At runtime, `LogbookConfiguration` maps `quarkus.logbook.*` properties; provider classes create the strategy, filters, formatters, predicates, and attribute extractors. `LogbookProvider` assembles those components into the `Logbook` instance and default sink/writer. Most provider beans use `@DefaultBean`, so applications can replace them with custom CDI beans.

`deployment` contains extension-level tests using `QuarkusUnitTest`; `runtime` contains focused unit tests; `integration-tests` exercises HTTP server and client behavior in a Quarkus application.

## Repository-specific conventions

- Configuration is runtime-phase SmallRye Config mapping in `runtime/.../configuration/LogbookConfiguration.java`. Add user-facing settings beneath the `quarkus.logbook` prefix and document their meaning in mapping Javadocs; the docs build generates the configuration reference included by `docs/modules/ROOT/pages/index.adoc`.
- Keep configurable behavior in the provider classes and wire new extension-owned CDI beans through `LogbookExtensionProcessor`. Use `@DefaultBean` for defaults intended to be replaceable by application beans.
- Preserve filter composition and precedence semantics: custom filters participate in the assembled Logbook, configured exclusion predicates take precedence over inclusion predicates, and body truncation runs after other body filters.
- Runtime unit tests use JUnit 5, Mockito, and AssertJ. Extension boot tests use `QuarkusUnitTest`; end-to-end HTTP tests use RestAssured with `@QuarkusTest` or `@QuarkusIntegrationTest`.

## Agent skills

### Issue tracker

Issues live in GitHub Issues (`Saphri/quarkus-logbook`), managed with the `gh` CLI. See `docs/agents/issue-tracker.md`.

### Triage labels

Default vocabulary: `needs-triage`, `needs-info`, `ready-for-agent`, `ready-for-human`, `wontfix`. See `docs/agents/triage-labels.md`.

### Domain docs

Single-context layout: `GLOSSARY.md` at the repo root plus `docs/adr/`. See `docs/agents/domain.md`.
