package com.compass.domain.model;

/**
 * One reason behind a classification: a signal and how much it pushed toward
 * the result. A positive contribution supports the profile.
 */
public final class Evidence {

    private final String signal;
    private final double contribution;

    private Evidence(String signal, double contribution) {
        if (signal == null || signal.trim().isEmpty()) {
            throw new IllegalArgumentException("evidence signal must not be blank");
        }
        this.signal = signal;
        this.contribution = contribution;
    }

    public static Evidence of(String signal, double contribution) {
        return new Evidence(signal, contribution);
    }

    public String signal() {
        return signal;
    }

    public double contribution() {
        return contribution;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Evidence)) {
            return false;
        }
        Evidence that = (Evidence) other;
        return signal.equals(that.signal) && Double.compare(contribution, that.contribution) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * signal.hashCode() + Double.valueOf(contribution).hashCode();
    }

    @Override
    public String toString() {
        return signal + " (+" + contribution + ")";
    }
}
