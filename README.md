# Quarkus Logbook

[![Version](https://img.shields.io/maven-central/v/io.quarkiverse.logbook/quarkus-logbook?logo=apache-maven&style=flat-square)](https://central.sonatype.com/artifact/io.quarkiverse.logbook/quarkus-logbook-parent)

## Introduction

This extension integrates [Zalando Logbook](https://github.com/zalando/logbook) with [Quarkus](https://quarkus.io/). Logbook is an extensible Java library for HTTP request and response logging. It's designed to be used in microservice environments and provides a flexible and powerful way to log HTTP traffic.

This extension provides:

-   **Automatic HTTP Logging**: Automatically logs all incoming and outgoing HTTP requests and responses.
-   **Flexible Configuration**: A rich set of configuration options to customize logging behavior, including formatting, obfuscation, and filtering.
-   **CDI Integration**: Seamless integration with Quarkus's CDI container, allowing for easy customization and extension.

## Getting Started

To use this extension, add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.quarkiverse.logbook</groupId>
    <artifactId>quarkus-logbook</artifactId>
    <version>${quarkus-logbook.version}</version>
</dependency>
```

### Configuration

The extension can be configured using standard Quarkus configuration properties in your `application.properties` file. Here are some of the most common options:

-   **Formatting**: Choose from different log formats, such as JSON, cURL, or HTTP.
-   **Obfuscation**: Obfuscate sensitive data in headers, parameters, paths, and JSON bodies.
-   **Filtering**: Include or exclude requests from logging based on their path and method.
-   **Strategy**: Control how and when requests and responses are logged.

For a full list of configuration properties, please refer to the [official documentation](https://docs.quarkiverse.io/quarkus-logbook/dev/index.html).

### Example Configuration

Here's an example of how you might configure Logbook in your `application.properties`:

```properties
# Use the JSON format for logs
quarkus.logbook.format.style=json

# Obfuscate the 'Authorization' and 'X-API-Key' headers
quarkus.logbook.obfuscate.headers=Authorization,X-API-Key

# Obfuscate the 'password' and 'credit_card_number' fields in JSON bodies
quarkus.logbook.obfuscate.json-body-fields=password,credit_card_number

# Exclude health checks from logging
quarkus.logbook.predicate.exclude.path=/q/health
```

## Documentation

The full documentation for this extension can be found on the [Quarkiverse Docs website](https://docs.quarkiverse.io/quarkus-logbook/dev/index.html).

## Contributing

Contributions are welcome! If you find a bug or have a feature request, please open an issue. If you'd like to contribute code, please open a pull request.

## License

This project is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) file for details.
