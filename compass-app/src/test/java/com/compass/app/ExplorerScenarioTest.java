package com.compass.app;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;

import com.compass.domain.model.Classification;
import com.compass.domain.model.EntityId;
import com.compass.domain.model.Profile;
import com.compass.simulator.EventSimulator;
import com.compass.simulator.SyntheticBehavior;

public class ExplorerScenarioTest {

    private static final double TOLERANCE = 1e-9;

    private final InMemoryCompass compass = new InMemoryCompass();
    private final EventSimulator simulator = new EventSimulator(compass.ingest());

    @Test
    public void an_entity_that_mostly_searches_is_classified_as_an_explorer() {
        EntityId alice = EntityId.of("alice");

        simulator.simulate(
                SyntheticBehavior.forEntity(alice).does("search", 8).does("purchase", 2),
                new DateTime(2013, 7, 1, 9, 0));

        Classification classification = compass.classifier().classify(alice);

        assertThat(classification.entityId(), is(alice));
        assertThat(classification.primaryProfile(), is(Profile.of("Explorer")));
        assertThat(classification.confidence(), is(closeTo(0.8, TOLERANCE)));
        assertThat(classification.distribution().probabilityOf(Profile.of("BargainHunter")),
                is(closeTo(0.2, TOLERANCE)));
        assertThat(classification.evidence().isEmpty(), is(false));
    }

    @Test
    public void an_unseen_entity_has_no_opinion() {
        Classification classification = compass.classifier().classify(EntityId.of("stranger"));

        assertThat(classification.distribution().isEmpty(), is(true));
        assertThat(classification.confidence(), is(0.0));
    }
}
