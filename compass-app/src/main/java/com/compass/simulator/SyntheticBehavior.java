package com.compass.simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.compass.domain.model.EntityId;

public final class SyntheticBehavior {

    private final EntityId entityId;
    private final List<Action> actions;

    private SyntheticBehavior(EntityId entityId, List<Action> actions) {
        if (entityId == null) {
            throw new IllegalArgumentException("entityId must not be null");
        }
        this.entityId = entityId;
        this.actions = Collections.unmodifiableList(new ArrayList<Action>(actions));
    }

    public static SyntheticBehavior forEntity(EntityId entityId) {
        return new SyntheticBehavior(entityId, Collections.<Action>emptyList());
    }

    public SyntheticBehavior does(String eventType, int times) {
        return does(eventType, Collections.<String, Object>emptyMap(), times);
    }

    public SyntheticBehavior does(String eventType, Map<String, Object> attributes, int times) {
        if (eventType == null || eventType.trim().isEmpty()) {
            throw new IllegalArgumentException("eventType must not be blank");
        }
        if (times < 0) {
            throw new IllegalArgumentException("times must not be negative");
        }
        List<Action> next = new ArrayList<Action>(actions);
        next.add(new Action(eventType, attributes, times));
        return new SyntheticBehavior(entityId, next);
    }

    public EntityId entityId() {
        return entityId;
    }

    public List<Action> actions() {
        return actions;
    }

    public static final class Action {

        private final String eventType;
        private final Map<String, Object> attributes;
        private final int times;

        private Action(String eventType, Map<String, Object> attributes, int times) {
            this.eventType = eventType;
            Map<String, Object> copy = new HashMap<String, Object>();
            if (attributes != null) {
                copy.putAll(attributes);
            }
            this.attributes = Collections.unmodifiableMap(copy);
            this.times = times;
        }

        public String eventType() {
            return eventType;
        }

        public Map<String, Object> attributes() {
            return attributes;
        }

        public int times() {
            return times;
        }
    }
}
