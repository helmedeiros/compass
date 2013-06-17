package com.compass.application;

import java.util.List;

import com.compass.domain.inference.Inference;
import com.compass.domain.inference.InferenceModel;
import com.compass.domain.model.Classification;
import com.compass.domain.model.EntityId;
import com.compass.domain.model.Event;
import com.compass.domain.model.Feature;
import com.compass.domain.model.Signal;
import com.compass.domain.pipeline.FeaturePipeline;
import com.compass.domain.pipeline.SignalPipeline;
import com.compass.domain.port.in.ClassifyEntity;
import com.compass.domain.port.out.EventStore;

public final class ClassificationService implements ClassifyEntity {

    private final EventStore eventStore;
    private final FeaturePipeline featurePipeline;
    private final SignalPipeline signalPipeline;
    private final InferenceModel inferenceModel;

    public ClassificationService(EventStore eventStore, FeaturePipeline featurePipeline,
            SignalPipeline signalPipeline, InferenceModel inferenceModel) {
        if (eventStore == null) {
            throw new IllegalArgumentException("eventStore must not be null");
        }
        if (featurePipeline == null) {
            throw new IllegalArgumentException("featurePipeline must not be null");
        }
        if (signalPipeline == null) {
            throw new IllegalArgumentException("signalPipeline must not be null");
        }
        if (inferenceModel == null) {
            throw new IllegalArgumentException("inferenceModel must not be null");
        }
        this.eventStore = eventStore;
        this.featurePipeline = featurePipeline;
        this.signalPipeline = signalPipeline;
        this.inferenceModel = inferenceModel;
    }

    @Override
    public Classification classify(EntityId entityId) {
        List<Event> events = eventStore.eventsOf(entityId);
        List<Feature> features = featurePipeline.extract(events);
        List<Signal> signals = signalPipeline.detect(features);
        Inference inference = inferenceModel.infer(signals);
        return Classification.of(entityId, inference.distribution(), inference.evidence());
    }
}
