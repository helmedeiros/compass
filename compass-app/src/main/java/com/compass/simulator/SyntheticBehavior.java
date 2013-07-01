package com.compass.simulator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.compass.domain.model.EntityId;

public final class SyntheticBehavior {

    private final EntityId entityId;
    private final Map<String, Integer> eventCounts;

    private SyntheticBehavior(EntityId entityId, Map<String, Integer> eventCounts) {
        if (entityId == null) {
            throw new IllegalArgumentException("entityId must not be null");
        }
        this.entityId = entityId;
        this.eventCounts = Collections.unmodifiableMap(new LinkedHashMap<String, Integer>(eventCounts));
    }

    public static SyntheticBehavior forEntity(EntityId entityId) {
        return new SyntheticBehavior(entityId, Collections.<String, Integer>emptyMap());
    }

    public SyntheticBehavior does(String eventType, int times) {
        if (eventType == null || eventType.trim().isEmpty()) {
            throw new IllegalArgumentException("eventType must not be blank");
        }
        if (times < 0) {
            throw new IllegalArgumentException("times must not be negative");
        }
        Map<String, Integer> next = new LinkedHashMap<String, Integer>(eventCounts);
        next.put(eventType, times);
        return new SyntheticBehavior(entityId, next);
    }

    public EntityId entityId() {
        return entityId;
    }

    public Map<String, Integer> eventCounts() {
        return eventCounts;
    }
}
