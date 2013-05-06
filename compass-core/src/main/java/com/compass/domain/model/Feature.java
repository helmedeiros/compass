package com.compass.domain.model;

/**
 * A named number computed from events. A feature is still a fact, not a guess.
 * For example: search_depth = 8, or session_minutes = 45.
 */
public final class Feature {

    private final String name;
    private final double value;

    private Feature(String name, double value) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("feature name must not be blank");
        }
        this.name = name;
        this.value = value;
    }

    public static Feature of(String name, double value) {
        return new Feature(name, value);
    }

    public String name() {
        return name;
    }

    public double value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Feature)) {
            return false;
        }
        Feature that = (Feature) other;
        return name.equals(that.name) && Double.compare(value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode() + Double.valueOf(value).hashCode();
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }
}
