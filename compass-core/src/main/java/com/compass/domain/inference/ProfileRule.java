package com.compass.domain.inference;

import com.compass.domain.model.Profile;
import com.compass.domain.model.Signal;

public final class ProfileRule {

    private final String signalName;
    private final Profile profile;
    private final double weight;

    private ProfileRule(String signalName, Profile profile, double weight) {
        if (signalName == null || signalName.trim().isEmpty()) {
            throw new IllegalArgumentException("signalName must not be blank");
        }
        if (profile == null) {
            throw new IllegalArgumentException("profile must not be null");
        }
        if (weight <= 0.0) {
            throw new IllegalArgumentException("weight must be positive");
        }
        this.signalName = signalName;
        this.profile = profile;
        this.weight = weight;
    }

    public static ProfileRule of(String signalName, Profile profile, double weight) {
        return new ProfileRule(signalName, profile, weight);
    }

    public boolean appliesTo(Signal signal) {
        return signalName.equals(signal.name());
    }

    public double contributionFor(Signal signal) {
        return weight * signal.strength();
    }

    public Profile profile() {
        return profile;
    }
}
