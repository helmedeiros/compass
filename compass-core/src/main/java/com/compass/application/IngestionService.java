package com.compass.application;

import com.compass.domain.model.Classification;
import com.compass.domain.model.Event;
import com.compass.domain.port.in.ClassifyEntity;
import com.compass.domain.port.in.IngestEvent;
import com.compass.domain.port.out.EventStore;
import com.compass.domain.port.out.ProfileHistoryStore;

public final class IngestionService implements IngestEvent {

    private final EventStore eventStore;
    private final ClassifyEntity classifier;
    private final ProfileHistoryStore profileHistoryStore;

    public IngestionService(EventStore eventStore, ClassifyEntity classifier,
            ProfileHistoryStore profileHistoryStore) {
        if (eventStore == null) {
            throw new IllegalArgumentException("eventStore must not be null");
        }
        if (classifier == null) {
            throw new IllegalArgumentException("classifier must not be null");
        }
        if (profileHistoryStore == null) {
            throw new IllegalArgumentException("profileHistoryStore must not be null");
        }
        this.eventStore = eventStore;
        this.classifier = classifier;
        this.profileHistoryStore = profileHistoryStore;
    }

    @Override
    public void ingest(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        eventStore.append(event);
        Classification classification = classifier.classify(event.entityId());
        profileHistoryStore.record(classification);
    }
}
