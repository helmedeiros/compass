package com.compass.domain.pipeline;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import com.compass.domain.model.EntityId;
import com.compass.domain.model.Event;
import com.compass.domain.model.Feature;

public class FeaturePipelineTest {

    private final EntityId entityId = EntityId.of("c-1");
    private final DateTime when = new DateTime(2013, 5, 21, 20, 0);

    @Test
    public void runs_every_extractor_and_collects_their_features() {
        FeaturePipeline pipeline = new FeaturePipeline(Arrays.<FeatureExtractor>asList(
                new EventCountFeatureExtractor("search", "searches"),
                new EventCountFeatureExtractor("purchase", "purchases")));
        List<Event> events = Arrays.asList(
                Event.of(entityId, "search", when),
                Event.of(entityId, "search", when),
                Event.of(entityId, "purchase", when));

        List<Feature> features = pipeline.extract(events);

        assertThat(features, contains(Feature.of("searches", 2.0), Feature.of("purchases", 1.0)));
    }

    @Test
    public void has_no_features_when_there_are_no_extractors() {
        FeaturePipeline pipeline = new FeaturePipeline(Collections.<FeatureExtractor>emptyList());

        assertThat(pipeline.extract(Collections.<Event>emptyList()).isEmpty(), is(true));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void exposes_features_as_a_read_only_list() {
        FeaturePipeline pipeline = new FeaturePipeline(Collections.<FeatureExtractor>emptyList());

        pipeline.extract(Collections.<Event>emptyList()).add(Feature.of("x", 1.0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_null_extractors() {
        new FeaturePipeline(null);
    }
}
