package com.compass.web;

import java.util.Map;

import org.joda.time.DateTime;

import com.compass.domain.model.EntityId;
import com.compass.domain.model.Event;

public class EventRequest {

    private String entityId;
    private String type;
    private String occurredAt;
    private Map<String, Object> attributes;

    public Event toEvent() {
        DateTime when = DateTime.parse(occurredAt);
        if (attributes == null) {
            return Event.of(EntityId.of(entityId), type, when);
        }
        return Event.of(EntityId.of(entityId), type, when, attributes);
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOccurredAt(String occurredAt) {
        this.occurredAt = occurredAt;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
