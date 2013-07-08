package com.compass.app;

import java.util.Arrays;

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

public final class CompassDefaults {

    private CompassDefaults() {
    }

    public static FeaturePipeline featurePipeline() {
        return new FeaturePipeline(Arrays.<FeatureExtractor>asList(
                new EventCountFeatureExtractor("search", "searches"),
                new EventCountFeatureExtractor("purchase", "purchases")));
    }

    public static SignalPipeline signalPipeline() {
        return new SignalPipeline(Arrays.<SignalDetector>asList(
                new SaturatingSignalDetector("searches", "high_search_depth", 10.0),
                new SaturatingSignalDetector("purchases", "frequent_buyer", 10.0)));
    }

    public static InferenceModel inferenceModel() {
        return new RuleBasedInferenceModel(Arrays.asList(
                ProfileRule.of("high_search_depth", Profile.of("Explorer"), 1.0),
                ProfileRule.of("frequent_buyer", Profile.of("BargainHunter"), 1.0)));
    }
}
