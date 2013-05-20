package com.compass.domain.port.out;

import java.util.List;

import com.compass.domain.model.Classification;
import com.compass.domain.model.EntityId;

public interface ProfileHistoryStore {

    void record(Classification classification);

    List<Classification> historyOf(EntityId entityId);
}
