package com.compass.domain.inference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.compass.domain.model.Evidence;
import com.compass.domain.model.Profile;
import com.compass.domain.model.ProfileDistribution;
import com.compass.domain.model.Signal;

public final class RuleBasedInferenceModel implements InferenceModel {

    private final List<ProfileRule> rules;

    public RuleBasedInferenceModel(List<ProfileRule> rules) {
        if (rules == null) {
            throw new IllegalArgumentException("rules must not be null");
        }
        this.rules = Collections.unmodifiableList(new ArrayList<ProfileRule>(rules));
    }

    @Override
    public Inference infer(List<Signal> signals) {
        Map<Profile, Double> weights = new LinkedHashMap<Profile, Double>();
        List<Evidence> evidence = new ArrayList<Evidence>();
        for (Signal signal : signals) {
            for (ProfileRule rule : rules) {
                if (rule.appliesTo(signal)) {
                    double contribution = rule.contributionFor(signal);
                    if (contribution > 0.0) {
                        addWeight(weights, rule.profile(), contribution);
                        evidence.add(Evidence.of(signal.name(), contribution));
                    }
                }
            }
        }
        return Inference.of(ProfileDistribution.of(weights), evidence);
    }

    private static void addWeight(Map<Profile, Double> weights, Profile profile, double contribution) {
        Double current = weights.get(profile);
        weights.put(profile, (current == null ? 0.0 : current) + contribution);
    }
}
