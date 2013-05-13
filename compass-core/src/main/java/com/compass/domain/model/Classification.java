package com.compass.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The full, explainable answer for an entity: which profiles are likely, the
 * primary one, the confidence, and the evidence behind it.
 *
 * <p>The primary profile and the confidence are not stored here. They come from
 * the distribution, so there is one source of truth.
 */
public final class Classification {

    private final EntityId entityId;
    private final ProfileDistribution distribution;
    private final List<Evidence> evidence;

    private Classification(EntityId entityId, ProfileDistribution distribution, List<Evidence> evidence) {
        if (entityId == null) {
            throw new IllegalArgumentException("entityId must not be null");
        }
        if (distribution == null) {
            throw new IllegalArgumentException("distribution must not be null");
        }
        List<Evidence> copy = new ArrayList<Evidence>();
        if (evidence != null) {
            copy.addAll(evidence);
        }
        this.entityId = entityId;
        this.distribution = distribution;
        this.evidence = Collections.unmodifiableList(copy);
    }

    public static Classification of(EntityId entityId, ProfileDistribution distribution, List<Evidence> evidence) {
        return new Classification(entityId, distribution, evidence);
    }

    public EntityId entityId() {
        return entityId;
    }

    public ProfileDistribution distribution() {
        return distribution;
    }

    /** The evidence, as a read-only list. */
    public List<Evidence> evidence() {
        return evidence;
    }

    public Profile primaryProfile() {
        return distribution.primaryProfile();
    }

    public double confidence() {
        return distribution.confidence();
    }
}
