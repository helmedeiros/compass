package com.compass.app;

import java.util.Arrays;

import com.compass.adapter.out.InMemoryEventStore;
import com.compass.adapter.out.InMemoryProfileHistoryStore;
import com.compass.application.ClassificationService;
import com.compass.application.IngestionService;
import com.compass.domain.inference.InferenceModel;
import com.compass.domain.inference.ProfileRule;
import com.compass.domain.inference.RuleBasedInferenceModel;
import com.compass.domain.model.Profile;
import com.compass.domain.pipeline.EventCountFeatureExtractor;
import com.compass.domain.pipeline.FeatureExtractor;
import com.compass.domain.pipeline.FeaturePipeline;
import com.compass.domain.pipeline.SaturatingSignalDetector;
import com.compass.domain.pipeline.SignalDetector;
import com.compass.domain.pipeline.SignalPipeline;
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
        ClassificationService classification =
                new ClassificationService(eventStore, featurePipeline(), signalPipeline(), inferenceModel());
        this.classifier = classification;
        this.ingest = new IngestionService(eventStore, classification, profileHistoryStore);
    }

    public IngestEvent ingest() {
        return ingest;
    }

    public ClassifyEntity classifier() {
        return classifier;
    }

    private static FeaturePipeline featurePipeline() {
        return new FeaturePipeline(Arrays.<FeatureExtractor>asList(
                new EventCountFeatureExtractor("search", "searches"),
                new EventCountFeatureExtractor("purchase", "purchases")));
    }

    private static SignalPipeline signalPipeline() {
        return new SignalPipeline(Arrays.<SignalDetector>asList(
                new SaturatingSignalDetector("searches", "high_search_depth", 10.0),
                new SaturatingSignalDetector("purchases", "frequent_buyer", 10.0)));
    }

    private static InferenceModel inferenceModel() {
        return new RuleBasedInferenceModel(Arrays.asList(
                ProfileRule.of("high_search_depth", Profile.of("Explorer"), 1.0),
                ProfileRule.of("frequent_buyer", Profile.of("BargainHunter"), 1.0)));
    }
}
