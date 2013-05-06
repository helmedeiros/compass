package com.compass.domain.model;

/**
 * A behavioral signal read from features. A signal has a name and a strength
 * between 0 and 1. For example: high_search_depth with strength 0.8.
 */
public final class Signal {

    private final String name;
    private final double strength;

    private Signal(String name, double strength) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("signal name must not be blank");
        }
        if (strength < 0.0 || strength > 1.0) {
            throw new IllegalArgumentException("signal strength must be between 0 and 1");
        }
        this.name = name;
        this.strength = strength;
    }

    public static Signal of(String name, double strength) {
        return new Signal(name, strength);
    }

    public String name() {
        return name;
    }

    public double strength() {
        return strength;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Signal)) {
            return false;
        }
        Signal that = (Signal) other;
        return name.equals(that.name) && Double.compare(strength, that.strength) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode() + Double.valueOf(strength).hashCode();
    }

    @Override
    public String toString() {
        return name + "@" + strength;
    }
}
