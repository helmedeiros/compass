package com.compass.simulator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Test;

import com.compass.domain.model.EntityId;
import com.compass.domain.model.Event;
import com.compass.domain.port.in.IngestEvent;

public class EventSimulatorTest {

    private final EntityId alice = EntityId.of("alice");
    private final DateTime startingAt = new DateTime(2013, 7, 1, 20, 0);

    private final List<Event> ingested = new ArrayList<Event>();
    private final IngestEvent target = new IngestEvent() {
        @Override
        public void ingest(Event event) {
            ingested.add(event);
        }
    };
    private final EventSimulator simulator = new EventSimulator(target);

    @Test
    public void submits_one_event_for_each_synthetic_action() {
        simulator.simulate(SyntheticBehavior.forEntity(alice).does("search", 2).does("purchase", 1), startingAt);

        assertThat(ingested.size(), is(3));
        assertThat(ingested.get(0).type(), is("search"));
        assertThat(ingested.get(1).type(), is("search"));
        assertThat(ingested.get(2).type(), is("purchase"));
    }

    @Test
    public void submits_every_event_for_the_behavior_entity() {
        simulator.simulate(SyntheticBehavior.forEntity(alice).does("search", 2), startingAt);

        for (Event event : ingested) {
            assertThat(event.entityId(), is(alice));
        }
    }

    @Test
    public void attaches_attributes_to_the_events_it_creates() {
        Map<String, Object> sports = new HashMap<String, Object>();
        sports.put("topic", "sports");

        simulator.simulate(SyntheticBehavior.forEntity(alice).does("article_view", sports, 1), startingAt);

        assertThat(ingested.get(0).attribute("topic"), is("sports"));
    }

    @Test
    public void moves_time_forward_between_events() {
        simulator.simulate(SyntheticBehavior.forEntity(alice).does("search", 2), startingAt);

        assertThat(ingested.get(1).occurredAt().isAfter(ingested.get(0).occurredAt()), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_null_ingest_target() {
        new EventSimulator(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_null_behavior() {
        simulator.simulate(null, startingAt);
    }
}
