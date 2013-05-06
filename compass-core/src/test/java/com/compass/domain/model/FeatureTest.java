package com.compass.domain.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class FeatureTest {

    @Test
    public void keeps_its_name_and_value() {
        Feature feature = Feature.of("search_depth", 8.0);

        assertThat(feature.name(), is("search_depth"));
        assertThat(feature.value(), is(8.0));
    }

    @Test
    public void same_name_and_value_are_equal() {
        assertThat(Feature.of("search_depth", 8.0), is(Feature.of("search_depth", 8.0)));
    }

    @Test
    public void different_value_is_not_equal() {
        assertThat(Feature.of("search_depth", 8.0), is(not(Feature.of("search_depth", 9.0))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_blank_name() {
        Feature.of(" ", 1.0);
    }
}
