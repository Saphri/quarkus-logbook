# Quarkus Logbook

A Quarkus extension that logs HTTP exchanges (requests and responses) using the Logbook core library. Terms here describe what gets logged and the parts that shape a log entry.

## Language

**Predicate**:
A path-and-method rule that decides whether an exchange is logged at all. Configured as includes/excludes; excludes take precedence over includes.
_Avoid_: filter, condition

**Filter**:
A transform applied to a logged exchange before it is recorded — redaction, truncation, dropping parts. A Filter never decides whether the exchange appears.
_Avoid_: predicate, obfuscation rule

**Condition**:
The single app-level gate in front of the whole Logbook that enables or disables logging for a request. Replaceable by application code; rarely used.
_Avoid_: predicate

**Exchange**:
A single HTTP interaction as observed by the logging app: one request paired with one response, linked by a correlation ID. Exchanges are observed on the client side automatically (outbound REST client calls) and on the server side deliberately (the app wraps its JAX-RS handlers with the server filter).
_Avoid_: transaction, call, request-response pair

**Local**:
The half of an Exchange produced by the logging app itself: the sent request on the client side, the sent response on the server side. Tied to perspective, not to message role.
_Avoid_: outbound, ours

**Remote**:
The half of an Exchange arriving from the other side: the received response on the client side, the received request on the server side.
_Avoid_: inbound, theirs

**Strategy**:
The policy deciding when and how much of an Exchange is recorded: everything, no bodies, only past a status, or bodies only past a status. A Strategy may emit the same half of an Exchange more than once; it never splits one Exchange into two.
_Avoid_: mode, verbosity, level

**Minimum status**:
The response-status threshold at which a status-dependent Strategy switches on its status-dependent behavior. Belongs to the strategies, not to logging as a whole.
_Avoid_: status code, error threshold

**Provider**:
A class that supplies one Logbook component (a strategy, filters, a formatter, an extractor) as an injectable part. The extension's Provider for a component is replaceable by the application's own.
_Avoid_: producer, factory

**JSON mapper**:
The injectable JSON codec shared by the parts that shape JSON: the JSON Formatter renders through it, JWT extractors read claims through it. One mapper per app; replaceable by the application's own.
_Avoid_: ObjectMapper, parser, serializer

**Sink**:
Where a log entry surfaces: a Formatter and a Writer bound together, optionally wrapped in chunking for transports that cannot take long lines.
_Avoid_: destination, output

**Writer**:
The bridge from a log entry into the app's logging framework. Entries surface under the Logbook logger category at TRACE, so that category's level is what switches logging on. In a built application the framework first drops anything below the category's minimum logging level, which the application owns, so entries can be filtered before the Writer is reached.
_Avoid_: logger, output

**Formatter**:
Renders one half of an Exchange as a single log-entry text in a style: JSON, cURL, HTTP, or Splunk.
_Avoid_: style, rendering
