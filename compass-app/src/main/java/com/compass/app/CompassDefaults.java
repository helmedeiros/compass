package com.compass.app;

import java.util.Arrays;

import com.compass.domain.inference.InferenceModel;
import com.compass.domain.inference.ProfileRule;
import com.compass.domain.inference.RuleBasedInferenceModel;
import com.compass.domain.model.Profile;
import com.compass.domain.pipeline.AttributeValueCountFeatureExtractor;
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
                new AttributeValueCountFeatureExtractor("topic", "sports", "sports_engagement"),
                new AttributeValueCountFeatureExtractor("topic", "politics", "politics_engagement"),
                new AttributeValueCountFeatureExtractor("topic", "markets", "markets_engagement"),
                new EventCountFeatureExtractor("subscribe", "subscriptions")));
    }

    public static SignalPipeline signalPipeline() {
        return new SignalPipeline(Arrays.<SignalDetector>asList(
                new SaturatingSignalDetector("sports_engagement", "follows_sports", 10.0),
                new SaturatingSignalDetector("politics_engagement", "follows_politics", 10.0),
                new SaturatingSignalDetector("markets_engagement", "follows_markets", 10.0),
                new SaturatingSignalDetector("subscriptions", "is_subscriber", 1.0)));
    }

    public static InferenceModel inferenceModel() {
        return new RuleBasedInferenceModel(Arrays.asList(
                ProfileRule.of("follows_sports", Profile.of("Sports Follower"), 1.0),
                ProfileRule.of("follows_politics", Profile.of("Politics Reader"), 1.0),
                ProfileRule.of("follows_markets", Profile.of("Markets Watcher"), 1.0),
                ProfileRule.of("is_subscriber", Profile.of("Subscriber"), 0.3)));
    }
}
