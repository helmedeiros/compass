package com.compass.app;

import com.compass.adapter.out.InMemoryEventStore;
import com.compass.adapter.out.InMemoryProfileHistoryStore;
import com.compass.application.ClassificationService;
import com.compass.application.IngestionService;
import com.compass.domain.port.in.ClassifyEntity;
import com.compass.domain.port.in.IngestEvent;
import com.compass.domain.port.out.EventStore;
import com.compass.domain.port.out.ProfileHistoryStore;

public final class InMemoryCompass {

    private final IngestEvent ingest;
    private final ClassifyEntity classifier;

    public InMemoryCompass() {
        EventStore eventStore = new InMemoryEventStore();
        ProfileHistoryStore profileHistoryStore = new InMemoryProfileHistoryStore();
        ClassificationService classification = new ClassificationService(
                eventStore,
                CompassDefaults.featurePipeline(),
                CompassDefaults.signalPipeline(),
                CompassDefaults.inferenceModel());
        this.classifier = classification;
        this.ingest = new IngestionService(eventStore, classification, profileHistoryStore);
    }

    public IngestEvent ingest() {
        return ingest;
    }

    public ClassifyEntity classifier() {
        return classifier;
    }
}
