package com.compass.domain.port.in;

import com.compass.domain.model.Classification;
import com.compass.domain.model.EntityId;

public interface ClassifyEntity {

    Classification classify(EntityId entityId);
}
