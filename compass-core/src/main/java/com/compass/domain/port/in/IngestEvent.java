package com.compass.domain.port.in;

import com.compass.domain.model.Event;

public interface IngestEvent {

    void ingest(Event event);
}
