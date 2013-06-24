package com.compass.adapter.out;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.compass.domain.model.Classification;
import com.compass.domain.model.EntityId;
import com.compass.domain.port.out.ProfileHistoryStore;

public final class InMemoryProfileHistoryStore implements ProfileHistoryStore {

    private final Map<EntityId, List<Classification>> historyByEntity =
            new LinkedHashMap<EntityId, List<Classification>>();

    @Override
    public void record(Classification classification) {
        if (classification == null) {
            throw new IllegalArgumentException("classification must not be null");
        }
        List<Classification> history = historyByEntity.get(classification.entityId());
        if (history == null) {
            history = new ArrayList<Classification>();
            historyByEntity.put(classification.entityId(), history);
        }
        history.add(classification);
    }

    @Override
    public List<Classification> historyOf(EntityId entityId) {
        List<Classification> history = historyByEntity.get(entityId);
        if (history == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<Classification>(history));
    }
}
