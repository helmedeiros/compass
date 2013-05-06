package com.compass.domain.model;

/**
 * The id of an entity that Compass watches. An entity can be a customer, an
 * account, a device, a shop, and so on.
 */
public final class EntityId {

    private final String value;

    private EntityId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("entity id must not be blank");
        }
        this.value = value;
    }

    public static EntityId of(String value) {
        return new EntityId(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EntityId)) {
            return false;
        }
        return value.equals(((EntityId) other).value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
