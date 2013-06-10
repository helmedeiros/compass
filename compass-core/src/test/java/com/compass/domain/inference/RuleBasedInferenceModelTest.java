package com.compass.domain.inference;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.compass.domain.model.Evidence;
import com.compass.domain.model.Profile;
import com.compass.domain.model.Signal;

public class RuleBasedInferenceModelTest {

    private static final double TOLERANCE = 1e-9;

    private final Profile explorer = Profile.of("Explorer");
    private final Profile bargainHunter = Profile.of("BargainHunter");

    private final InferenceModel model = new RuleBasedInferenceModel(Arrays.asList(
            ProfileRule.of("high_search_depth", explorer, 1.0),
            ProfileRule.of("frequent_buyer", bargainHunter, 1.0)));

    @Test
    public void builds_a_distribution_and_records_evidence_from_the_rules_that_fire() {
        Inference inference = model.infer(Arrays.asList(
                Signal.of("high_search_depth", 0.8),
                Signal.of("frequent_buyer", 0.2)));

        assertThat(inference.distribution().primaryProfile(), is(explorer));
        assertThat(inference.distribution().confidence(), is(closeTo(0.8, TOLERANCE)));
        assertThat(inference.distribution().probabilityOf(bargainHunter), is(closeTo(0.2, TOLERANCE)));
        assertThat(inference.evidence(), contains(
                Evidence.of("high_search_depth", 0.8),
                Evidence.of("frequent_buyer", 0.2)));
    }

    @Test
    public void lets_several_signals_push_the_same_profile() {
        InferenceModel model = new RuleBasedInferenceModel(Arrays.asList(
                ProfileRule.of("deep_search", explorer, 1.0),
                ProfileRule.of("long_session", explorer, 1.0)));

        Inference inference = model.infer(Arrays.asList(
                Signal.of("deep_search", 0.5),
                Signal.of("long_session", 0.5)));

        assertThat(inference.distribution().primaryProfile(), is(explorer));
        assertThat(inference.distribution().confidence(), is(closeTo(1.0, TOLERANCE)));
        assertThat(inference.evidence().size(), is(2));
    }

    @Test
    public void has_no_opinion_when_no_signal_matches_a_rule() {
        Inference inference = model.infer(Collections.singletonList(Signal.of("unknown_signal", 0.9)));

        assertThat(inference.distribution().isEmpty(), is(true));
        assertThat(inference.evidence().isEmpty(), is(true));
    }

    @Test
    public void ignores_a_zero_strength_signal() {
        Inference inference = model.infer(Collections.singletonList(Signal.of("high_search_depth", 0.0)));

        assertThat(inference.distribution().isEmpty(), is(true));
        assertThat(inference.evidence().isEmpty(), is(true));
    }

    @Test
    public void has_no_opinion_without_any_signals() {
        Inference inference = model.infer(Collections.<Signal>emptyList());

        assertThat(inference.distribution().isEmpty(), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_null_rules() {
        new RuleBasedInferenceModel(null);
    }
}
