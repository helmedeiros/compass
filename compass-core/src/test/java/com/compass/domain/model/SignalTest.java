package com.compass.domain.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SignalTest {

    @Test
    public void keeps_its_name_and_strength() {
        Signal signal = Signal.of("high_search_depth", 0.8);

        assertThat(signal.name(), is("high_search_depth"));
        assertThat(signal.strength(), is(0.8));
    }

    @Test
    public void accepts_the_bounds_zero_and_one() {
        assertThat(Signal.of("a", 0.0).strength(), is(0.0));
        assertThat(Signal.of("b", 1.0).strength(), is(1.0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_strength_below_zero() {
        Signal.of("a", -0.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_strength_above_one() {
        Signal.of("a", 1.1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_blank_name() {
        Signal.of(" ", 0.5);
    }
}
