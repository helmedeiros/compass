package com.compass.domain.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

/**
 * A raw fact about an entity at a point in time. An event is an observation,
 * not a conclusion. Attributes carry extra data, like an amount or a depth.
 */
public final class Event {

    private final EntityId entityId;
    private final String type;
    private final DateTime occurredAt;
    private final Map<String, Object> attributes;

    private Event(EntityId entityId, String type, DateTime occurredAt, Map<String, Object> attributes) {
        if (entityId == null) {
            throw new IllegalArgumentException("entityId must not be null");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("type must not be blank");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt must not be null");
        }
        Map<String, Object> copy = new HashMap<String, Object>();
        if (attributes != null) {
            copy.putAll(attributes);
        }
        this.entityId = entityId;
        this.type = type;
        this.occurredAt = occurredAt;
        this.attributes = Collections.unmodifiableMap(copy);
    }

    public static Event of(EntityId entityId, String type, DateTime occurredAt, Map<String, Object> attributes) {
        return new Event(entityId, type, occurredAt, attributes);
    }

    public static Event of(EntityId entityId, String type, DateTime occurredAt) {
        return new Event(entityId, type, occurredAt, null);
    }

    public EntityId entityId() {
        return entityId;
    }

    public String type() {
        return type;
    }

    public DateTime occurredAt() {
        return occurredAt;
    }

    /** The attributes, as a read-only map. */
    public Map<String, Object> attributes() {
        return attributes;
    }

    /** Reads an attribute as a number, or null when it is missing or not a number. */
    public Double numericAttribute(String key) {
        Object raw = attributes.get(key);
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number) {
            return ((Number) raw).doubleValue();
        }
        try {
            return Double.valueOf(raw.toString());
        } catch (NumberFormatException notANumber) {
            return null;
        }
    }

    /** Reads an attribute as text, or null when it is missing. */
    public String attribute(String key) {
        Object raw = attributes.get(key);
        return raw == null ? null : raw.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Event)) {
            return false;
        }
        Event that = (Event) other;
        return entityId.equals(that.entityId)
                && type.equals(that.type)
                && occurredAt.equals(that.occurredAt)
                && attributes.equals(that.attributes);
    }

    @Override
    public int hashCode() {
        int result = entityId.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + occurredAt.hashCode();
        result = 31 * result + attributes.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Event{" + type + " on " + entityId + " at " + occurredAt + "}";
    }
}
