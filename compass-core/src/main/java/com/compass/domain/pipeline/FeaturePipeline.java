package com.compass.domain.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.compass.domain.model.Event;
import com.compass.domain.model.Feature;

public final class FeaturePipeline {

    private final List<FeatureExtractor> extractors;

    public FeaturePipeline(List<FeatureExtractor> extractors) {
        if (extractors == null) {
            throw new IllegalArgumentException("extractors must not be null");
        }
        this.extractors = Collections.unmodifiableList(new ArrayList<FeatureExtractor>(extractors));
    }

    public List<Feature> extract(List<Event> events) {
        List<Feature> features = new ArrayList<Feature>();
        for (FeatureExtractor extractor : extractors) {
            features.addAll(extractor.extract(events));
        }
        return Collections.unmodifiableList(features);
    }
}
