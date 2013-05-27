package com.compass.domain.pipeline;

import java.util.Collections;
import java.util.List;

import com.compass.domain.model.Event;
import com.compass.domain.model.Feature;

public final class EventCountFeatureExtractor implements FeatureExtractor {

    private final String eventType;
    private final String featureName;

    public EventCountFeatureExtractor(String eventType, String featureName) {
        if (eventType == null || eventType.trim().isEmpty()) {
            throw new IllegalArgumentException("eventType must not be blank");
        }
        if (featureName == null || featureName.trim().isEmpty()) {
            throw new IllegalArgumentException("featureName must not be blank");
        }
        this.eventType = eventType;
        this.featureName = featureName;
    }

    @Override
    public List<Feature> extract(List<Event> events) {
        int count = 0;
        for (Event event : events) {
            if (eventType.equals(event.type())) {
                count++;
            }
        }
        return Collections.singletonList(Feature.of(featureName, count));
    }
}
