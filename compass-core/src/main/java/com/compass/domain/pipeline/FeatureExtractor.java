package com.compass.domain.pipeline;

import java.util.List;

import com.compass.domain.model.Event;
import com.compass.domain.model.Feature;

public interface FeatureExtractor {

    List<Feature> extract(List<Event> events);
}
