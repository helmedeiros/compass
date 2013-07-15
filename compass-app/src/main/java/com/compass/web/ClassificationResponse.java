package com.compass.web;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.compass.domain.model.Classification;
import com.compass.domain.model.Evidence;
import com.compass.domain.model.Profile;
import com.compass.domain.model.ProfileDistribution;

public class ClassificationResponse {

    private final String entityId;
    private final String primaryProfile;
    private final double confidence;
    private final Map<String, Double> distribution;
    private final List<EvidenceResponse> evidence;

    private ClassificationResponse(String entityId, String primaryProfile, double confidence,
            Map<String, Double> distribution, List<EvidenceResponse> evidence) {
        this.entityId = entityId;
        this.primaryProfile = primaryProfile;
        this.confidence = confidence;
        this.distribution = distribution;
        this.evidence = evidence;
    }

    public static ClassificationResponse from(Classification classification) {
        ProfileDistribution distribution = classification.distribution();
        return new ClassificationResponse(
                classification.entityId().value(),
                distribution.isEmpty() ? null : distribution.primaryProfile().name(),
                classification.confidence(),
                distributionOf(distribution),
                evidenceOf(classification));
    }

    private static Map<String, Double> distributionOf(ProfileDistribution distribution) {
        Map<String, Double> probabilities = new LinkedHashMap<String, Double>();
        for (Map.Entry<Profile, Double> entry : distribution.probabilities().entrySet()) {
            probabilities.put(entry.getKey().name(), entry.getValue());
        }
        return probabilities;
    }

    private static List<EvidenceResponse> evidenceOf(Classification classification) {
        List<EvidenceResponse> evidence = new ArrayList<EvidenceResponse>();
        for (Evidence reason : classification.evidence()) {
            evidence.add(EvidenceResponse.from(reason));
        }
        return evidence;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getPrimaryProfile() {
        return primaryProfile;
    }

    public double getConfidence() {
        return confidence;
    }

    public Map<String, Double> getDistribution() {
        return distribution;
    }

    public List<EvidenceResponse> getEvidence() {
        return evidence;
    }
}
