package com.compass.domain.port.out;

import java.util.List;

import com.compass.domain.model.EntityId;
import com.compass.domain.model.Event;

public interface EventStore {

    void append(Event event);

    List<Event> eventsOf(EntityId entityId);
}
