package com.compass.domain.inference;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.compass.domain.model.Profile;
import com.compass.domain.model.Signal;

public class ProfileRuleTest {

    private static final double TOLERANCE = 1e-9;

    private final Profile explorer = Profile.of("Explorer");
    private final ProfileRule rule = ProfileRule.of("high_search_depth", explorer, 2.0);

    @Test
    public void applies_to_a_signal_with_the_same_name() {
        assertThat(rule.appliesTo(Signal.of("high_search_depth", 0.5)), is(true));
        assertThat(rule.appliesTo(Signal.of("frequent_buyer", 0.5)), is(false));
    }

    @Test
    public void weighs_a_signal_strength_by_its_weight() {
        assertThat(rule.contributionFor(Signal.of("high_search_depth", 0.5)), is(closeTo(1.0, TOLERANCE)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_blank_signal_name() {
        ProfileRule.of(" ", explorer, 1.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_null_profile() {
        ProfileRule.of("high_search_depth", null, 1.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_non_positive_weight() {
        ProfileRule.of("high_search_depth", explorer, 0.0);
    }
}
