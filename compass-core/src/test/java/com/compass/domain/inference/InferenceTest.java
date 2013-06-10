package com.compass.domain.inference;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import com.compass.domain.model.Evidence;
import com.compass.domain.model.Profile;
import com.compass.domain.model.ProfileDistribution;

public class InferenceTest {

    private final ProfileDistribution distribution =
            ProfileDistribution.of(Collections.singletonMap(Profile.of("Explorer"), 1.0));

    @Test
    public void exposes_its_distribution_and_evidence() {
        Inference inference = Inference.of(distribution, Arrays.asList(Evidence.of("high_search_depth", 0.8)));

        assertThat(inference.distribution(), is(distribution));
        assertThat(inference.evidence(), contains(Evidence.of("high_search_depth", 0.8)));
    }

    @Test
    public void treats_null_evidence_as_empty() {
        Inference inference = Inference.of(distribution, null);

        assertThat(inference.evidence().isEmpty(), is(true));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void exposes_evidence_as_a_read_only_list() {
        Inference inference = Inference.of(distribution, null);

        inference.evidence().add(Evidence.of("x", 1.0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_null_distribution() {
        Inference.of(null, null);
    }
}
