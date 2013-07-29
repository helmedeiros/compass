package com.compass.domain.pipeline;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Test;

import com.compass.domain.model.EntityId;
import com.compass.domain.model.Event;
import com.compass.domain.model.Feature;

public class AttributeValueCountFeatureExtractorTest {

    private final EntityId reader = EntityId.of("reader-1");
    private final DateTime when = new DateTime(2014, 3, 1, 9, 0);
    private final FeatureExtractor sportsEngagement =
            new AttributeValueCountFeatureExtractor("topic", "sports", "sports_engagement");

    @Test
    public void counts_events_whose_attribute_matches_the_value() {
        List<Event> events = Arrays.asList(
                event("article_view", "sports"),
                event("video_watch", "sports"),
                event("article_view", "politics"));

        assertThat(sportsEngagement.extract(events), is(Collections.singletonList(Feature.of("sports_engagement", 2.0))));
    }

    @Test
    public void ignores_events_without_the_attribute() {
        List<Event> events = Arrays.asList(
                Event.of(reader, "subscribe", when),
                event("article_view", "sports"));

        assertThat(sportsEngagement.extract(events), is(Collections.singletonList(Feature.of("sports_engagement", 1.0))));
    }

    @Test
    public void emits_zero_when_nothing_matches() {
        List<Event> events = Collections.singletonList(event("article_view", "markets"));

        assertThat(sportsEngagement.extract(events), is(Collections.singletonList(Feature.of("sports_engagement", 0.0))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_blank_attribute_value() {
        new AttributeValueCountFeatureExtractor("topic", " ", "sports_engagement");
    }

    private Event event(String type, String topic) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("topic", topic);
        return Event.of(reader, type, when, attributes);
    }
}
