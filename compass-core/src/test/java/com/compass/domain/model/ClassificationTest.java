package com.compass.domain.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class ClassificationTest {

    private final EntityId entityId = EntityId.of("123");
    private final Profile explorer = Profile.of("Explorer");
    private final Profile expert = Profile.of("Expert");

    private ProfileDistribution distribution() {
        Map<Profile, Double> weights = new LinkedHashMap<Profile, Double>();
        weights.put(explorer, 0.7);
        weights.put(expert, 0.3);
        return ProfileDistribution.of(weights);
    }

    @Test
    public void exposes_its_entity_and_distribution() {
        Classification classification = Classification.of(entityId, distribution(), null);

        assertThat(classification.entityId(), is(entityId));
        assertThat(classification.distribution(), is(distribution()));
    }

    @Test
    public void reads_primary_profile_and_confidence_from_the_distribution() {
        Classification classification = Classification.of(entityId, distribution(), null);

        assertThat(classification.primaryProfile(), is(explorer));
        assertThat(classification.confidence(), is(closeTo(0.7, 1e-9)));
    }

    @Test
    public void keeps_the_evidence() {
        Evidence depth = Evidence.of("high_search_depth", 25.0);
        Evidence diversity = Evidence.of("destination_diversity", 15.0);

        Classification classification =
                Classification.of(entityId, distribution(), Arrays.asList(depth, diversity));

        assertThat(classification.evidence(), is(Arrays.asList(depth, diversity)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void exposes_evidence_as_a_read_only_list() {
        Classification classification = Classification.of(entityId, distribution(), null);

        classification.evidence().add(Evidence.of("x", 1.0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_null_distribution() {
        Classification.of(entityId, null, null);
    }
}
