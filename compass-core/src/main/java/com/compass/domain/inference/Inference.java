package com.compass.domain.inference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.compass.domain.model.Evidence;
import com.compass.domain.model.ProfileDistribution;

public final class Inference {

    private final ProfileDistribution distribution;
    private final List<Evidence> evidence;

    private Inference(ProfileDistribution distribution, List<Evidence> evidence) {
        if (distribution == null) {
            throw new IllegalArgumentException("distribution must not be null");
        }
        List<Evidence> copy = new ArrayList<Evidence>();
        if (evidence != null) {
            copy.addAll(evidence);
        }
        this.distribution = distribution;
        this.evidence = Collections.unmodifiableList(copy);
    }

    public static Inference of(ProfileDistribution distribution, List<Evidence> evidence) {
        return new Inference(distribution, evidence);
    }

    public ProfileDistribution distribution() {
        return distribution;
    }

    public List<Evidence> evidence() {
        return evidence;
    }
}
