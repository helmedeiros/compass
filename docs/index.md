---
layout: default
title: Compass
---

# Compass

Compass learns how an entity behaves and turns that behavior into an explainable
profile. This site walks through it as a **news portal** would use it: readers
browse articles, watch videos on different topics, and some subscribe — and
Compass segments each reader so the portal can recommend the right content, with
reasons it can stand behind.

A profile is a probability, not a label set in stone. It shifts as new events
arrive. Compass keeps **facts** (what a reader did) apart from **guesses** (what
it means), so every answer comes with a confidence score and the evidence behind it.

## How it works

Compass turns raw behavior into an explainable profile in four steps:

1. **Events** come in — one fact about a reader at a point in time: viewed an
   article on sports, watched a video on markets, subscribed.
2. **Features** are counted from those events: sports engagement = 8.
3. **Signals** read the features and rate them from 0 to 1: follows_sports = 0.8.
4. **Inference** weighs the signals into a **profile distribution** and records the
   evidence for each one.

You send events through one HTTP call and read a profile through another. Nothing
in the engine knows or cares whether an event came from the live portal or from
the built-in simulator.

## Send what a reader does

A view, a watch, a subscribe — each is one event. Topic rides along as an attribute,
so articles and videos on the same topic count together.

```
curl -X POST http://localhost:8080/events \
  -H 'Content-Type: application/json' \
  -d '{"entityId":"ana","type":"article_view","occurredAt":"2014-03-01T09:00:00Z","attributes":{"topic":"sports"}}'

curl -X POST http://localhost:8080/events \
  -H 'Content-Type: application/json' \
  -d '{"entityId":"ana","type":"video_watch","occurredAt":"2014-03-01T09:05:00Z","attributes":{"topic":"sports"}}'

curl -X POST http://localhost:8080/events \
  -H 'Content-Type: application/json' \
  -d '{"entityId":"ana","type":"subscribe","occurredAt":"2014-03-01T09:10:00Z"}'
```

Each returns `202 Accepted`.

## Read who they are

Ana mostly follows sports (5 articles and 3 videos), reads markets now and then
(2 articles), and has subscribed. Ask Compass:

```
curl http://localhost:8080/entities/ana/profile
```

```json
{
  "entityId": "ana",
  "primaryProfile": "Sports Follower",
  "confidence": 0.6153846153846154,
  "distribution": {
    "Sports Follower": 0.6153846153846154,
    "Subscriber": 0.23076923076923075,
    "Markets Watcher": 0.15384615384615385
  },
  "evidence": [
    { "signal": "follows_sports", "contribution": 0.8 },
    { "signal": "follows_markets", "contribution": 0.2 },
    { "signal": "is_subscriber", "contribution": 0.3 }
  ]
}
```

Ana is first of all a **Sports Follower** (about 62%), she is a **Subscriber**
(23%), and she keeps an eye on **markets** (15%). The evidence shows exactly what
pushed each way.

Now the recommendation is simple and defensible: lead ana with sports, surface
subscriber-only stories, and mix in some markets — and you can say *why*.

## A different reader, a different segment

Dora reads politics and little else. Same engine, different behavior, different
answer:

```json
{
  "entityId": "dora",
  "primaryProfile": "Politics Reader",
  "confidence": 1.0,
  "distribution": { "Politics Reader": 1.0 },
  "evidence": [ { "signal": "follows_politics", "contribution": 0.6 } ]
}
```

That is inference, not a lookup. Give dora politics.

## Honest about the unknown

A reader Compass has never seen gets no made-up answer:

```json
{ "entityId": "nobody", "primaryProfile": null, "confidence": 0.0, "distribution": {}, "evidence": [] }
```

No data, no opinion — so you can fall back to popular or editor-picked content.

## Watch a profile evolve

```
curl http://localhost:8080/entities/ana/history
```

A snapshot is recorded every time an event arrives, so you can see exactly when a
reader turned from, say, a Markets Watcher into a Sports Follower — and recommend
accordingly.

## Using Compass for experimentation

Compass ships with an **Event Simulator** that generates synthetic behavior and
submits it through the very same path the live portal uses. You can develop, demo,
benchmark, and run controlled experiments with no real traffic.

```java
InMemoryCompass compass = new InMemoryCompass();
EventSimulator simulator = new EventSimulator(compass.ingest());

EntityId ana = EntityId.of("ana");
simulator.simulate(
    SyntheticBehavior.forEntity(ana)
        .does("article_view", topic("sports"), 5)
        .does("video_watch", topic("sports"), 3)
        .does("article_view", topic("markets"), 2)
        .does("subscribe", 1),
    DateTime.now());

compass.classifier().classify(ana);
```

prints

```
primary profile:  Sports Follower
confidence:       0.6153846153846154
distribution:     {Sports Follower=0.6153846153846154, Subscriber=0.23076923076923075, Markets Watcher=0.15384615384615385}
evidence:         [follows_sports (+0.8), follows_markets (+0.2), is_subscriber (+0.3)]
```

Because simulated and real events run the exact same code, an experiment that
holds here holds in production. That makes a few things easy:

- generate a synthetic audience and check the segments come out as you expect;
- try a different rule set (new topics, different weights) and compare the profiles;
- push thousands of synthetic readers through the pipeline before going live.

## Make it your own

The topics, signals, and profiles above are just one configuration. You define
your own:

- events: what readers do (`article_view`, `video_watch`, `subscribe`, …);
- features: what to count (engagement per topic, subscriptions);
- signals: how a count becomes a strength from 0 to 1;
- profiles and rules: which signals point to which reader segments.

Want `Tech Enthusiast`, `Weekend Reader`, or `Premium Subscriber`? Add the events,
signals, and rules — the engine and the API stay the same.

## Why the evidence matters

Plenty of systems hand you a label. Compass hands you a label, a confidence, and
the reasons. So you can:

- explain a recommendation to an editor, a reader, or an auditor;
- debug a wrong segment by reading which signal pushed it there;
- trust a high-confidence call and treat a low-confidence one with care.

## Try it

The code is on GitHub: [helmedeiros/compass](https://github.com/helmedeiros/compass).
Clone it and run `mvn test` to see the whole pipeline proven end to end, or start
the API with `mvn -pl compass-app jetty:run` and send it some reader events.
