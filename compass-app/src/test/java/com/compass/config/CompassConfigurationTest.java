package com.compass.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.compass.domain.model.Classification;
import com.compass.domain.model.EntityId;
import com.compass.domain.model.Event;
import com.compass.domain.model.Profile;
import com.compass.domain.port.in.ClassifyEntity;
import com.compass.domain.port.in.IngestEvent;
import com.compass.simulator.EventSimulator;
import com.compass.simulator.SyntheticBehavior;

public class CompassConfigurationTest {

    private static final double TOLERANCE = 1e-9;

    private AnnotationConfigApplicationContext context;

    @Before
    public void startContext() {
        context = new AnnotationConfigApplicationContext(CompassConfiguration.class);
    }

    @After
    public void closeContext() {
        context.close();
    }

    @Test
    public void wires_a_compass_that_classifies_from_ingested_events() {
        EntityId alice = EntityId.of("alice");
        EventSimulator simulator = new EventSimulator(context.getBean(IngestEvent.class));

        simulator.simulate(
                SyntheticBehavior.forEntity(alice).does("search", 8).does("purchase", 2),
                new DateTime(2013, 7, 8, 9, 0));

        Classification classification = context.getBean(ClassifyEntity.class).classify(alice);

        assertThat(classification.primaryProfile(), is(Profile.of("Explorer")));
        assertThat(classification.confidence(), is(closeTo(0.8, TOLERANCE)));
    }

    @Test
    public void shares_one_event_store_between_ingestion_and_classification() {
        EntityId bob = EntityId.of("bob");
        context.getBean(IngestEvent.class).ingest(
                Event.of(bob, "search", new DateTime(2013, 7, 8, 9, 0)));

        Classification classification = context.getBean(ClassifyEntity.class).classify(bob);

        assertThat(classification.distribution().isEmpty(), is(false));
    }
}
