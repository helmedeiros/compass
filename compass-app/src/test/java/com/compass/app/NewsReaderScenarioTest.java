package com.compass.app;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Test;

import com.compass.domain.model.Classification;
import com.compass.domain.model.EntityId;
import com.compass.domain.model.Profile;
import com.compass.simulator.EventSimulator;
import com.compass.simulator.SyntheticBehavior;

public class NewsReaderScenarioTest {

    private static final double TOLERANCE = 1e-9;

    private final InMemoryCompass compass = new InMemoryCompass();
    private final EventSimulator simulator = new EventSimulator(compass.ingest());

    @Test
    public void a_reader_who_mostly_follows_sports_is_segmented_as_a_sports_follower() {
        EntityId ana = EntityId.of("ana");

        simulator.simulate(
                SyntheticBehavior.forEntity(ana)
                        .does("article_view", topic("sports"), 5)
                        .does("video_watch", topic("sports"), 3)
                        .does("article_view", topic("markets"), 2)
                        .does("subscribe", 1),
                DateTime.now());

        Classification classification = compass.classifier().classify(ana);

        assertThat(classification.entityId(), is(ana));
        assertThat(classification.primaryProfile(), is(Profile.of("Sports Follower")));
        assertThat(classification.confidence(), is(closeTo(0.8 / 1.3, TOLERANCE)));
        assertThat(classification.distribution().probabilityOf(Profile.of("Subscriber")),
                is(closeTo(0.3 / 1.3, TOLERANCE)));
        assertThat(classification.distribution().probabilityOf(Profile.of("Markets Watcher")),
                is(closeTo(0.2 / 1.3, TOLERANCE)));
    }

    @Test
    public void an_unseen_reader_has_no_opinion() {
        Classification classification = compass.classifier().classify(EntityId.of("stranger"));

        assertThat(classification.distribution().isEmpty(), is(true));
        assertThat(classification.confidence(), is(0.0));
    }

    private Map<String, Object> topic(String value) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("topic", value);
        return attributes;
    }
}
