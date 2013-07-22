---
layout: default
title: Compass
---

# Compass

Compass learns how an entity behaves. An entity can be a customer, a reader, an
account, a device — anything you send events about. With each event Compass grows
more sure about a **profile**, and it can always explain why.

A profile is a probability, not a label set in stone. It shifts as new events
arrive. Compass keeps **facts** (what happened) apart from **guesses** (what it
means), so every answer comes with a confidence score and the evidence behind it.

## How it works

Compass turns raw behavior into an explainable profile in four steps:

1. **Events** come in — one fact about an entity at a point in time ("searched",
   "read an article", "bought").
2. **Features** are counted from those events ("searches = 8").
3. **Signals** read the features and rate them from 0 to 1 ("high_search_depth = 0.8").
4. **Inference** weighs the signals into a **profile distribution** and records the
   evidence for each one.

You send events through one HTTP call and read a profile through another. Nothing
in the engine knows or cares whether an event came from your live app or from the
built-in simulator.

## The API in a few calls

### Send an event

```
curl -X POST http://localhost:8080/events \
  -H 'Content-Type: application/json' \
  -d '{"entityId":"alice","type":"search","occurredAt":"2014-03-01T09:00:00Z"}'
```

Compass replies `202 Accepted`. Send as many as you like.

### Read the profile

After alice has searched 8 times and bought twice:

```
curl http://localhost:8080/entities/alice/profile
```

```json
{
  "entityId": "alice",
  "primaryProfile": "Explorer",
  "confidence": 0.8,
  "distribution": { "Explorer": 0.8, "BargainHunter": 0.2 },
  "evidence": [
    { "signal": "high_search_depth", "contribution": 0.8 },
    { "signal": "frequent_buyer", "contribution": 0.2 }
  ]
}
```

Compass is 80% sure alice is an **Explorer**, and it shows its reasons: deep
searching pushed hardest, buying a little.

### The opposite behavior

A second entity that searched twice and bought eight times comes back the other
way — same engine, opposite input, opposite answer. That is inference, not a
lookup:

```json
{
  "entityId": "bob",
  "primaryProfile": "BargainHunter",
  "confidence": 0.8,
  "distribution": { "BargainHunter": 0.8, "Explorer": 0.2 },
  "evidence": [
    { "signal": "high_search_depth", "contribution": 0.2 },
    { "signal": "frequent_buyer", "contribution": 0.8 }
  ]
}
```

### Honest about the unknown

An entity it has never seen gets no made-up answer:

```json
{ "entityId": "nobody", "primaryProfile": null, "confidence": 0.0, "distribution": {}, "evidence": [] }
```

No data, no opinion.

### Watch a profile evolve

```
curl http://localhost:8080/entities/alice/history
```

A snapshot is recorded every time an event arrives, so you can see exactly when a
profile changed and why.

## Using Compass for experimentation

Compass ships with an **Event Simulator** that generates synthetic behavior and
submits it through the very same path a real app uses. You can develop, demo,
benchmark, and run controlled experiments with no live traffic.

```java
InMemoryCompass compass = new InMemoryCompass();
EventSimulator simulator = new EventSimulator(compass.ingest());

EntityId alice = EntityId.of("alice");
simulator.simulate(
    SyntheticBehavior.forEntity(alice).does("search", 8).does("purchase", 2),
    DateTime.now());

compass.classifier().classify(alice);
```

prints

```
primary profile:  Explorer
confidence:       0.8
distribution:     {Explorer=0.8, BargainHunter=0.2}
evidence:         [high_search_depth (+0.8), frequent_buyer (+0.2)]
```

Because simulated and real events run the exact same code, an experiment that
holds here holds in production. That makes a few things easy:

- run two rule sets against the same synthetic population and compare the profiles;
- replay a known cohort and check the engine still segments it the way you expect;
- push thousands of synthetic entities through the pipeline before going live.

## Using Compass in a news portal

A news portal wants to recommend stories. Instead of guessing, it can send Compass
what readers actually do and let it segment them — with reasons it can stand behind.

You define your own events, signals, and reader profiles. For a portal that might be:

- events: `article_view` carrying a `section` ("sports", "politics", "markets");
- features: how many views fall in each section;
- signals: `follows_sports`, `follows_politics`, `follows_markets`;
- profiles: `Sports Follower`, `Politics Reader`, `Markets Watcher`.

Send what a reader did:

```
curl -X POST http://localhost:8080/events \
  -H 'Content-Type: application/json' \
  -d '{"entityId":"reader-42","type":"article_view","occurredAt":"2014-03-01T09:00:00Z","attributes":{"section":"sports"}}'
```

Read who they are:

```json
{
  "entityId": "reader-42",
  "primaryProfile": "Sports Follower",
  "confidence": 0.74,
  "distribution": { "Sports Follower": 0.74, "Markets Watcher": 0.26 },
  "evidence": [
    { "signal": "follows_sports", "contribution": 0.74 },
    { "signal": "follows_markets", "contribution": 0.26 }
  ]
}
```

Now the recommendation is simple and defensible: lead reader-42 with sports, mix
in a little markets, and you can say *why* — the evidence is right there. As the
reader's habits shift, the profile shifts with them, and the history shows when it
turned.

(The bundled demo ships with an Explorer / Bargain-Hunter rule set; the events,
signals, and profiles above are an example of your own to define.)

## Why the evidence matters

Plenty of systems hand you a label. Compass hands you a label, a confidence, and
the reasons. So you can:

- explain a recommendation to a colleague, a user, or an auditor;
- debug a wrong profile by reading which signal pushed it there;
- trust a high-confidence call and treat a low-confidence one with care.

## Try it

The code is on GitHub: [helmedeiros/compass](https://github.com/helmedeiros/compass).
Clone it and run `mvn test` to see the whole pipeline proven end to end, or start
the API with `mvn -pl compass-app jetty:run` and send it some events.
