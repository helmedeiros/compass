package com.compass.domain.pipeline;

import java.util.Collections;
import java.util.List;

import com.compass.domain.model.Event;
import com.compass.domain.model.Feature;

public final class AttributeValueCountFeatureExtractor implements FeatureExtractor {

    private final String attributeKey;
    private final String attributeValue;
    private final String featureName;

    public AttributeValueCountFeatureExtractor(String attributeKey, String attributeValue, String featureName) {
        if (attributeKey == null || attributeKey.trim().isEmpty()) {
            throw new IllegalArgumentException("attributeKey must not be blank");
        }
        if (attributeValue == null || attributeValue.trim().isEmpty()) {
            throw new IllegalArgumentException("attributeValue must not be blank");
        }
        if (featureName == null || featureName.trim().isEmpty()) {
            throw new IllegalArgumentException("featureName must not be blank");
        }
        this.attributeKey = attributeKey;
        this.attributeValue = attributeValue;
        this.featureName = featureName;
    }

    @Override
    public List<Feature> extract(List<Event> events) {
        int count = 0;
        for (Event event : events) {
            if (attributeValue.equals(event.attribute(attributeKey))) {
                count++;
            }
        }
        return Collections.singletonList(Feature.of(featureName, count));
    }
}
