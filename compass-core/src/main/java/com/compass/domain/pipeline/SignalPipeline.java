package com.compass.domain.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.compass.domain.model.Feature;
import com.compass.domain.model.Signal;

public final class SignalPipeline {

    private final List<SignalDetector> detectors;

    public SignalPipeline(List<SignalDetector> detectors) {
        if (detectors == null) {
            throw new IllegalArgumentException("detectors must not be null");
        }
        this.detectors = Collections.unmodifiableList(new ArrayList<SignalDetector>(detectors));
    }

    public List<Signal> detect(List<Feature> features) {
        List<Signal> signals = new ArrayList<Signal>();
        for (SignalDetector detector : detectors) {
            signals.addAll(detector.detect(features));
        }
        return Collections.unmodifiableList(signals);
    }
}
