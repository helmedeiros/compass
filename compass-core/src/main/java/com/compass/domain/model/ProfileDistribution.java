package com.compass.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * How likely each profile is for an entity. The probabilities always sum to 1.
 * This class is the single place that knows the primary profile (the most
 * likely one) and the confidence (its probability).
 *
 * <p>An empty distribution means Compass has no opinion yet.
 */
public final class ProfileDistribution {

    private static final ProfileDistribution EMPTY =
            new ProfileDistribution(Collections.<Profile, Double>emptyMap());

    private final Map<Profile, Double> probabilities;

    private ProfileDistribution(Map<Profile, Double> probabilities) {
        this.probabilities = Collections.unmodifiableMap(probabilities);
    }

    public static ProfileDistribution empty() {
        return EMPTY;
    }

    /**
     * Builds a distribution from weights. Weights may be any non-negative
     * numbers; they are normalized so the probabilities sum to 1. Empty or
     * all-zero weights give an empty distribution.
     */
    public static ProfileDistribution of(Map<Profile, Double> weights) {
        if (weights == null || weights.isEmpty()) {
            return EMPTY;
        }
        double total = totalOf(weights);
        if (total == 0.0) {
            return EMPTY;
        }
        return new ProfileDistribution(normalize(weights, total));
    }

    private static double totalOf(Map<Profile, Double> weights) {
        double total = 0.0;
        for (Map.Entry<Profile, Double> weight : weights.entrySet()) {
            Double value = weight.getValue();
            if (value == null || value < 0.0) {
                throw new IllegalArgumentException(
                        "profile weight must not be negative: " + weight.getKey());
            }
            total += value;
        }
        return total;
    }

    private static Map<Profile, Double> normalize(Map<Profile, Double> weights, double total) {
        List<Map.Entry<Profile, Double>> entries =
                new ArrayList<Map.Entry<Profile, Double>>(weights.entrySet());
        Collections.sort(entries, byProbabilityDescThenName());
        Map<Profile, Double> normalized = new LinkedHashMap<Profile, Double>();
        for (Map.Entry<Profile, Double> entry : entries) {
            normalized.put(entry.getKey(), entry.getValue() / total);
        }
        return normalized;
    }

    private static Comparator<Map.Entry<Profile, Double>> byProbabilityDescThenName() {
        return new Comparator<Map.Entry<Profile, Double>>() {
            @Override
            public int compare(Map.Entry<Profile, Double> left, Map.Entry<Profile, Double> right) {
                int byProbability = Double.compare(right.getValue(), left.getValue());
                if (byProbability != 0) {
                    return byProbability;
                }
                return left.getKey().name().compareTo(right.getKey().name());
            }
        };
    }

    public boolean isEmpty() {
        return probabilities.isEmpty();
    }

    public double probabilityOf(Profile profile) {
        Double probability = probabilities.get(profile);
        return probability == null ? 0.0 : probability;
    }

    /** The probabilities, ordered from most to least likely, as a read-only map. */
    public Map<Profile, Double> probabilities() {
        return probabilities;
    }

    public Profile primaryProfile() {
        if (probabilities.isEmpty()) {
            throw new IllegalStateException("an empty distribution has no primary profile");
        }
        return probabilities.keySet().iterator().next();
    }

    public double confidence() {
        if (probabilities.isEmpty()) {
            return 0.0;
        }
        return probabilities.values().iterator().next();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ProfileDistribution)) {
            return false;
        }
        return probabilities.equals(((ProfileDistribution) other).probabilities);
    }

    @Override
    public int hashCode() {
        return probabilities.hashCode();
    }

    @Override
    public String toString() {
        return probabilities.toString();
    }
}
