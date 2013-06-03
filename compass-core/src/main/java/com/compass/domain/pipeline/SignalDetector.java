package com.compass.domain.pipeline;

import java.util.List;

import com.compass.domain.model.Feature;
import com.compass.domain.model.Signal;

public interface SignalDetector {

    List<Signal> detect(List<Feature> features);
}
