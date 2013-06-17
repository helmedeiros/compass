package com.compass.application;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.joda.time.DateTime;
import org.junit.Test;

import com.compass.domain.model.Classification;
import com.compass.domain.model.EntityId;
import com.compass.domain.model.Event;
import com.compass.domain.model.Evidence;
import com.compass.domain.model.Profile;
import com.compass.domain.model.ProfileDistribution;
import com.compass.domain.port.in.ClassifyEntity;
import com.compass.domain.port.out.EventStore;
import com.compass.domain.port.out.ProfileHistoryStore;

public class IngestionServiceTest {

    private final EntityId entityId = EntityId.of("c-1");
    private final Event event = Event.of(entityId, "search", new DateTime(2013, 6, 17, 20, 0));

    private final EventStore eventStore = mock(EventStore.class);
    private final ClassifyEntity classifier = mock(ClassifyEntity.class);
    private final ProfileHistoryStore profileHistoryStore = mock(ProfileHistoryStore.class);

    private final IngestionService service =
            new IngestionService(eventStore, classifier, profileHistoryStore);

    @Test
    public void stores_the_event() {
        service.ingest(event);

        verify(eventStore).append(event);
    }

    @Test
    public void records_the_fresh_profile_after_storing_the_event() {
        Classification classification = Classification.of(entityId,
                ProfileDistribution.of(Collections.singletonMap(Profile.of("Explorer"), 1.0)),
                Collections.<Evidence>emptyList());
        when(classifier.classify(entityId)).thenReturn(classification);

        service.ingest(event);

        verify(profileHistoryStore).record(classification);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_null_event() {
        service.ingest(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_null_classifier() {
        new IngestionService(eventStore, null, profileHistoryStore);
    }
}
