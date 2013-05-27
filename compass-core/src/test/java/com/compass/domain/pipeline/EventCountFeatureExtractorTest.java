package com.compass.domain.pipeline;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import com.compass.domain.model.EntityId;
import com.compass.domain.model.Event;
import com.compass.domain.model.Feature;

public class EventCountFeatureExtractorTest {

    private final EntityId entityId = EntityId.of("c-1");
    private final DateTime when = new DateTime(2013, 5, 21, 20, 0);
    private final FeatureExtractor searches = new EventCountFeatureExtractor("search", "searches");

    @Test
    public void counts_events_of_the_given_type() {
        List<Event> events = Arrays.asList(
                Event.of(entityId, "search", when),
                Event.of(entityId, "search", when),
                Event.of(entityId, "purchase", when));

        assertThat(searches.extract(events), is(Collections.singletonList(Feature.of("searches", 2.0))));
    }

    @Test
    public void emits_zero_when_no_event_matches() {
        List<Event> events = Collections.singletonList(Event.of(entityId, "purchase", when));

        assertThat(searches.extract(events), is(Collections.singletonList(Feature.of("searches", 0.0))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_blank_event_type() {
        new EventCountFeatureExtractor(" ", "searches");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_blank_feature_name() {
        new EventCountFeatureExtractor("search", " ");
    }
}
