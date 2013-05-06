package com.compass.domain.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Test;

public class EventTest {

    private final EntityId entityId = EntityId.of("123");
    private final DateTime when = new DateTime(2013, 5, 7, 20, 13);

    @Test
    public void exposes_its_fields() {
        Event event = Event.of(entityId, "purchase_completed", when);

        assertThat(event.entityId(), is(entityId));
        assertThat(event.type(), is("purchase_completed"));
        assertThat(event.occurredAt(), is(when));
    }

    @Test
    public void reads_a_numeric_attribute_from_a_number() {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("amount", 42);

        Event event = Event.of(entityId, "purchase_completed", when, attributes);

        assertThat(event.numericAttribute("amount"), is(42.0));
    }

    @Test
    public void reads_a_numeric_attribute_from_text() {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("depth", "8");

        Event event = Event.of(entityId, "search", when, attributes);

        assertThat(event.numericAttribute("depth"), is(8.0));
    }

    @Test
    public void returns_null_for_a_missing_numeric_attribute() {
        Event event = Event.of(entityId, "search", when);

        assertThat(event.numericAttribute("depth"), is(nullValue()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void exposes_attributes_as_a_read_only_map() {
        Event event = Event.of(entityId, "search", when);

        event.attributes().put("x", 1);
    }

    @Test
    public void does_not_see_later_changes_to_the_given_map() {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("depth", 8);
        Event event = Event.of(entityId, "search", when, attributes);

        attributes.put("depth", 99);

        assertThat(event.numericAttribute("depth"), is(8.0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_blank_type() {
        Event.of(entityId, " ", when);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_null_time() {
        Event.of(entityId, "search", null);
    }
}
