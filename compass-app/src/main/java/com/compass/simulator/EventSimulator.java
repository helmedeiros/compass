package com.compass.simulator;

import org.joda.time.DateTime;

import com.compass.domain.model.Event;
import com.compass.domain.port.in.IngestEvent;

public final class EventSimulator {

    private final IngestEvent ingestEvent;

    public EventSimulator(IngestEvent ingestEvent) {
        if (ingestEvent == null) {
            throw new IllegalArgumentException("ingestEvent must not be null");
        }
        this.ingestEvent = ingestEvent;
    }

    public void simulate(SyntheticBehavior behavior, DateTime startingAt) {
        if (behavior == null) {
            throw new IllegalArgumentException("behavior must not be null");
        }
        if (startingAt == null) {
            throw new IllegalArgumentException("startingAt must not be null");
        }
        DateTime when = startingAt;
        for (SyntheticBehavior.Action action : behavior.actions()) {
            for (int i = 0; i < action.times(); i++) {
                ingestEvent.ingest(Event.of(behavior.entityId(), action.eventType(), when, action.attributes()));
                when = when.plusMinutes(1);
            }
        }
    }
}
