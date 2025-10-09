# Copilot Instructions for quarkus-logbook

## Project Overview
- This is a Quarkus extension integrating [Zalando Logbook](https://github.com/zalando/logbook) for HTTP request/response logging.
- The codebase is organized as a multi-module Maven project:
  - `runtime/`: Main extension logic, runtime configuration, Logbook provider
  - `deployment/`: Quarkus deployment-time integration
  - `integration-tests/`: Integration tests using Quarkus and RestAssured
  - `docs/`: Antora-based documentation

## Architecture & Patterns
- Extension entrypoint: `LogbookProvider` in `runtime/` provides a CDI bean for Logbook, using config from `LogbookConfiguration`.
- Configuration is mapped via `@ConfigMapping` and `@ConfigRoot` (see `runtime/configuration/LogbookConfiguration.java`).
- Quarkus extension conventions: deployment module wires up build-time logic, runtime module provides beans and config.
- Tests use `QuarkusUnitTest` and `@RegisterExtension` for isolated extension testing (see `deployment/src/test/java/io/quarkiverse/logbook/test/LogbookTest.java`).

## Build & Test Workflows
- Build all modules: `./mvnw clean install`
- Run integration tests: `./mvnw verify -pl integration-tests`
- To debug extension logic, use Quarkus dev mode in a sample app or run unit tests in `deployment/`.

## Key Conventions
- All configuration properties for users are prefixed with `quarkus.logbook`.
- Runtime config is defined in `LogbookConfiguration.java` and exposed via CDI beans.
- External dependencies: Zalando Logbook, Quarkus core, Quarkus REST, Quarkus REST Jackson, Quarkus ARC.
- Documentation follows Antora structure in `docs/`.

## Examples
- To add a new config property, update `LogbookConfiguration.java` and document in `docs/modules/ROOT/pages/index.adoc`.
- To extend Logbook behavior, modify `LogbookProvider.java` and ensure new beans are annotated for CDI.
- For integration tests, use RestAssured and Quarkus test extensions as shown in `integration-tests/`.

## References
- [Quarkus Extension Guide](https://quarkus.io/guides/building-my-first-extension)
- [Quarkiverse Wiki](https://github.com/quarkiverse/quarkiverse/wiki)
- [Zalando Logbook](https://github.com/zalando/logbook)

---
If any section is unclear or missing, please provide feedback to improve these instructions.