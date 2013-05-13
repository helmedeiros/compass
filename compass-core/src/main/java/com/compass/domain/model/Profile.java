package com.compass.domain.model;

/**
 * A behavioral profile that Compass may assign to an entity. A profile is a
 * hypothesis, not a truth. For example: Explorer, Expert, New Customer.
 */
public final class Profile {

    private final String name;

    private Profile(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("profile name must not be blank");
        }
        this.name = name;
    }

    public static Profile of(String name) {
        return new Profile(name);
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Profile)) {
            return false;
        }
        return name.equals(((Profile) other).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
