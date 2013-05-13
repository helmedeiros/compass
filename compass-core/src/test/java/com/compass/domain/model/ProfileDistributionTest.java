package com.compass.domain.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ProfileDistributionTest {

    private static final double TOLERANCE = 1e-9;

    private final Profile explorer = Profile.of("Explorer");
    private final Profile expert = Profile.of("Expert");
    private final Profile bargainHunter = Profile.of("BargainHunter");

    @Test
    public void normalizes_weights_so_they_sum_to_one() {
        Map<Profile, Double> weights = new LinkedHashMap<Profile, Double>();
        weights.put(explorer, 72.0);
        weights.put(expert, 15.0);
        weights.put(bargainHunter, 13.0);

        ProfileDistribution distribution = ProfileDistribution.of(weights);

        assertThat(distribution.probabilityOf(explorer), is(closeTo(0.72, TOLERANCE)));
        assertThat(distribution.probabilityOf(expert), is(closeTo(0.15, TOLERANCE)));
        assertThat(distribution.probabilityOf(bargainHunter), is(closeTo(0.13, TOLERANCE)));
    }

    @Test
    public void primary_profile_is_the_most_likely_one() {
        ProfileDistribution distribution = ProfileDistribution.of(weights(72.0, 15.0, 13.0));

        assertThat(distribution.primaryProfile(), is(explorer));
        assertThat(distribution.confidence(), is(closeTo(0.72, TOLERANCE)));
    }

    @Test
    public void orders_probabilities_from_most_to_least_likely() {
        ProfileDistribution distribution = ProfileDistribution.of(weights(13.0, 72.0, 15.0));

        List<Profile> ordered = new ArrayList<Profile>(distribution.probabilities().keySet());

        assertThat(ordered, contains(expert, bargainHunter, explorer));
    }

    @Test
    public void unknown_profile_has_zero_probability() {
        ProfileDistribution distribution = ProfileDistribution.of(weights(1.0, 1.0, 1.0));

        assertThat(distribution.probabilityOf(Profile.of("PowerUser")), is(0.0));
    }

    @Test
    public void breaks_ties_by_name_for_a_stable_primary() {
        Map<Profile, Double> weights = new LinkedHashMap<Profile, Double>();
        weights.put(explorer, 1.0);
        weights.put(expert, 1.0);

        ProfileDistribution distribution = ProfileDistribution.of(weights);

        // Equal weight: the name that sorts first wins, every time.
        assertThat(distribution.primaryProfile(), is(expert));
    }

    @Test
    public void empty_distribution_means_no_opinion() {
        ProfileDistribution distribution = ProfileDistribution.empty();

        assertThat(distribution.isEmpty(), is(true));
        assertThat(distribution.confidence(), is(0.0));
    }

    @Test
    public void all_zero_weights_give_an_empty_distribution() {
        assertThat(ProfileDistribution.of(weights(0.0, 0.0, 0.0)).isEmpty(), is(true));
    }

    @Test(expected = IllegalStateException.class)
    public void empty_distribution_has_no_primary_profile() {
        ProfileDistribution.empty().primaryProfile();
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_negative_weight() {
        ProfileDistribution.of(weights(1.0, -1.0, 1.0));
    }

    private Map<Profile, Double> weights(double explorerWeight, double expertWeight, double bargainWeight) {
        Map<Profile, Double> weights = new LinkedHashMap<Profile, Double>();
        weights.put(explorer, explorerWeight);
        weights.put(expert, expertWeight);
        weights.put(bargainHunter, bargainWeight);
        return weights;
    }
}
