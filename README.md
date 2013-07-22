# Compass

Compass is a tool to learn how an entity behaves. An entity can be a customer,
an account, a device, a shop, and so on. Compass reads events about the entity.
With each event it grows more sure about a **profile**.

A profile is shown as a probability. It is not fixed. It changes when new events
come.

Compass keeps **facts** apart from **guesses**. Facts come from events. Guesses
are the profiles. A profile is a hypothesis, not a truth. Every answer can be
explained: the current profiles, a confidence score, the evidence behind it, and
how it changed over time.

## Project site

A guided tour with real request and response examples, plus ideas for
experimentation and for news portals building recommendations, lives at
<https://helmedeiros.github.io/compass/>.

## Architecture

The design is hexagonal (ports and adapters). Two modules keep it clean:

- **`compass-core`** — the inside of the hexagon. Plain Java, no framework. It
  has the domain model, the ports (in and out), and the pipeline:
  `Event → FeatureExtractor* → SignalDetector* → InferenceModel →
  ProfileDistribution + Evidence`. The `InferenceModel` port keeps the API the
  same when the inference is rules, statistics, ML, or a mix.
- **`compass-app`** — the adapters. Spring setup, the REST API, and in-memory
  stores. It depends on `compass-core`. The core never depends on it.

## Tech stack

Java 7, Spring 3.2, Servlet 3.0, Jackson 2.2, Joda-Time, JUnit 4 + Hamcrest +
Mockito. Built with Maven.

## Build and test

```sh
mvn test          # unit, integration, and e2e tests
mvn -pl compass-app jetty:run   # run the REST API on http://localhost:8080
```

## Status

Work in progress. This is a small project to study. It is not deployed.
