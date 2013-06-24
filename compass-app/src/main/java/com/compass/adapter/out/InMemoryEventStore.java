package com.compass.adapter.out;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.compass.domain.model.EntityId;
import com.compass.domain.model.Event;
import com.compass.domain.port.out.EventStore;

public final class InMemoryEventStore implements EventStore {

    private final Map<EntityId, List<Event>> eventsByEntity = new LinkedHashMap<EntityId, List<Event>>();

    @Override
    public void append(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        List<Event> events = eventsByEntity.get(event.entityId());
        if (events == null) {
            events = new ArrayList<Event>();
            eventsByEntity.put(event.entityId(), events);
        }
        events.add(event);
    }

    @Override
    public List<Event> eventsOf(EntityId entityId) {
        List<Event> events = eventsByEntity.get(entityId);
        if (events == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<Event>(events));
    }
}
