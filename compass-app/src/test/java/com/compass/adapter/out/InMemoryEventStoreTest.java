package com.compass.adapter.out;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;

import com.compass.domain.model.EntityId;
import com.compass.domain.model.Event;

public class InMemoryEventStoreTest {

    private final EntityId alice = EntityId.of("alice");
    private final EntityId bob = EntityId.of("bob");
    private final DateTime when = new DateTime(2013, 6, 24, 20, 0);

    private final InMemoryEventStore store = new InMemoryEventStore();

    @Test
    public void keeps_an_entity_events_in_the_order_they_were_appended() {
        Event search = Event.of(alice, "search", when);
        Event purchase = Event.of(alice, "purchase", when);
        store.append(search);
        store.append(purchase);

        assertThat(store.eventsOf(alice), contains(search, purchase));
    }

    @Test
    public void keeps_each_entity_events_apart() {
        Event aliceSearch = Event.of(alice, "search", when);
        Event bobSearch = Event.of(bob, "search", when);
        store.append(aliceSearch);
        store.append(bobSearch);

        assertThat(store.eventsOf(alice), contains(aliceSearch));
        assertThat(store.eventsOf(bob), contains(bobSearch));
    }

    @Test
    public void has_no_events_for_an_unknown_entity() {
        assertThat(store.eventsOf(alice).isEmpty(), is(true));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void exposes_events_as_a_read_only_list() {
        store.append(Event.of(alice, "search", when));

        store.eventsOf(alice).add(Event.of(alice, "purchase", when));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_null_event() {
        store.append(null);
    }
}
