package com.compass.domain.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class EntityIdTest {

    @Test
    public void keeps_the_given_value() {
        assertThat(EntityId.of("123").value(), is("123"));
    }

    @Test
    public void two_ids_with_the_same_value_are_equal() {
        assertThat(EntityId.of("123"), is(EntityId.of("123")));
        assertThat(EntityId.of("123").hashCode(), is(EntityId.of("123").hashCode()));
    }

    @Test
    public void two_ids_with_different_values_are_not_equal() {
        assertThat(EntityId.of("123"), is(not(EntityId.of("456"))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_null_value() {
        EntityId.of(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_blank_value() {
        EntityId.of("  ");
    }
}
