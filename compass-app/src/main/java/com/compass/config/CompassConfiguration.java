package com.compass.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.compass.app.CompassDefaults;
import com.compass.application.ClassificationService;
import com.compass.application.IngestionService;
import com.compass.adapter.out.InMemoryEventStore;
import com.compass.adapter.out.InMemoryProfileHistoryStore;
import com.compass.domain.inference.InferenceModel;
import com.compass.domain.pipeline.FeaturePipeline;
import com.compass.domain.pipeline.SignalPipeline;
import com.compass.domain.port.in.ClassifyEntity;
import com.compass.domain.port.in.IngestEvent;
import com.compass.domain.port.out.EventStore;
import com.compass.domain.port.out.ProfileHistoryStore;

@Configuration
public class CompassConfiguration {

    @Bean
    public EventStore eventStore() {
        return new InMemoryEventStore();
    }

    @Bean
    public ProfileHistoryStore profileHistoryStore() {
        return new InMemoryProfileHistoryStore();
    }

    @Bean
    public FeaturePipeline featurePipeline() {
        return CompassDefaults.featurePipeline();
    }

    @Bean
    public SignalPipeline signalPipeline() {
        return CompassDefaults.signalPipeline();
    }

    @Bean
    public InferenceModel inferenceModel() {
        return CompassDefaults.inferenceModel();
    }

    @Bean
    public ClassifyEntity classifyEntity(EventStore eventStore, FeaturePipeline featurePipeline,
            SignalPipeline signalPipeline, InferenceModel inferenceModel) {
        return new ClassificationService(eventStore, featurePipeline, signalPipeline, inferenceModel);
    }

    @Bean
    public IngestEvent ingestEvent(EventStore eventStore, ClassifyEntity classifyEntity,
            ProfileHistoryStore profileHistoryStore) {
        return new IngestionService(eventStore, classifyEntity, profileHistoryStore);
    }
}
