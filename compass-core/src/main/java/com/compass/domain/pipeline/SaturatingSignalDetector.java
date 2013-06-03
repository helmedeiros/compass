package com.compass.domain.pipeline;

import java.util.Collections;
import java.util.List;

import com.compass.domain.model.Feature;
import com.compass.domain.model.Signal;

public final class SaturatingSignalDetector implements SignalDetector {

    private final String featureName;
    private final String signalName;
    private final double fullStrengthAt;

    public SaturatingSignalDetector(String featureName, String signalName, double fullStrengthAt) {
        if (featureName == null || featureName.trim().isEmpty()) {
            throw new IllegalArgumentException("featureName must not be blank");
        }
        if (signalName == null || signalName.trim().isEmpty()) {
            throw new IllegalArgumentException("signalName must not be blank");
        }
        if (fullStrengthAt <= 0.0) {
            throw new IllegalArgumentException("fullStrengthAt must be positive");
        }
        this.featureName = featureName;
        this.signalName = signalName;
        this.fullStrengthAt = fullStrengthAt;
    }

    @Override
    public List<Signal> detect(List<Feature> features) {
        for (Feature feature : features) {
            if (featureName.equals(feature.name())) {
                return Collections.singletonList(Signal.of(signalName, strengthOf(feature.value())));
            }
        }
        return Collections.emptyList();
    }

    private double strengthOf(double value) {
        return Math.max(0.0, Math.min(value / fullStrengthAt, 1.0));
    }
}
