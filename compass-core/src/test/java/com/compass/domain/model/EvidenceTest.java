package com.compass.domain.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class EvidenceTest {

    @Test
    public void keeps_its_signal_and_contribution() {
        Evidence evidence = Evidence.of("high_search_depth", 25.0);

        assertThat(evidence.signal(), is("high_search_depth"));
        assertThat(evidence.contribution(), is(25.0));
    }

    @Test
    public void same_signal_and_contribution_are_equal() {
        assertThat(Evidence.of("a", 25.0), is(Evidence.of("a", 25.0)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_blank_signal() {
        Evidence.of(" ", 25.0);
    }
}
