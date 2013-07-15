package com.compass.web;

import com.compass.domain.model.Evidence;

public class EvidenceResponse {

    private final String signal;
    private final double contribution;

    private EvidenceResponse(String signal, double contribution) {
        this.signal = signal;
        this.contribution = contribution;
    }

    public static EvidenceResponse from(Evidence evidence) {
        return new EvidenceResponse(evidence.signal(), evidence.contribution());
    }

    public String getSignal() {
        return signal;
    }

    public double getContribution() {
        return contribution;
    }
}
