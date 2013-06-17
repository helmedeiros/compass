package com.compass.application;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import com.compass.domain.inference.InferenceModel;
import com.compass.domain.inference.ProfileRule;
import com.compass.domain.inference.RuleBasedInferenceModel;
import com.compass.domain.model.Classification;
import com.compass.domain.model.EntityId;
import com.compass.domain.model.Event;
import com.compass.domain.model.Evidence;
import com.compass.domain.model.Profile;
import com.compass.domain.pipeline.EventCountFeatureExtractor;
import com.compass.domain.pipeline.FeatureExtractor;
import com.compass.domain.pipeline.FeaturePipeline;
import com.compass.domain.pipeline.SaturatingSignalDetector;
import com.compass.domain.pipeline.SignalDetector;
import com.compass.domain.pipeline.SignalPipeline;
import com.compass.domain.port.out.EventStore;

public class ClassificationServiceTest {

    private static final double TOLERANCE = 1e-9;

    private final EntityId entityId = EntityId.of("c-1");
    private final DateTime when = new DateTime(2013, 6, 17, 20, 0);
    private final Profile explorer = Profile.of("Explorer");
    private final Profile bargainHunter = Profile.of("BargainHunter");

    private final EventStore eventStore = mock(EventStore.class);
    private final FeaturePipeline featurePipeline = new FeaturePipeline(Arrays.<FeatureExtractor>asList(
            new EventCountFeatureExtractor("search", "searches"),
            new EventCountFeatureExtractor("purchase", "purchases")));
    private final SignalPipeline signalPipeline = new SignalPipeline(Arrays.<SignalDetector>asList(
            new SaturatingSignalDetector("searches", "high_search_depth", 10.0),
            new SaturatingSignalDetector("purchases", "frequent_buyer", 10.0)));
    private final InferenceModel inferenceModel = new RuleBasedInferenceModel(Arrays.asList(
            ProfileRule.of("high_search_depth", explorer, 1.0),
            ProfileRule.of("frequent_buyer", bargainHunter, 1.0)));

    private final ClassificationService service =
            new ClassificationService(eventStore, featurePipeline, signalPipeline, inferenceModel);

    @Test
    public void classifies_an_entity_from_its_events() {
        when(eventStore.eventsOf(entityId)).thenReturn(events(8, "search", 2, "purchase"));

        Classification classification = service.classify(entityId);

        assertThat(classification.entityId(), is(entityId));
        assertThat(classification.primaryProfile(), is(explorer));
        assertThat(classification.confidence(), is(closeTo(0.8, TOLERANCE)));
        assertThat(classification.distribution().probabilityOf(bargainHunter), is(closeTo(0.2, TOLERANCE)));
        assertThat(classification.evidence(), contains(
                Evidence.of("high_search_depth", 0.8),
                Evidence.of("frequent_buyer", 0.2)));
    }

    @Test
    public void has_no_opinion_when_the_entity_has_no_events() {
        when(eventStore.eventsOf(entityId)).thenReturn(Collections.<Event>emptyList());

        Classification classification = service.classify(entityId);

        assertThat(classification.distribution().isEmpty(), is(true));
        assertThat(classification.confidence(), is(0.0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_null_event_store() {
        new ClassificationService(null, featurePipeline, signalPipeline, inferenceModel);
    }

    private List<Event> events(int searches, String searchType, int purchases, String purchaseType) {
        List<Event> events = new ArrayList<Event>();
        for (int i = 0; i < searches; i++) {
            events.add(Event.of(entityId, searchType, when));
        }
        for (int i = 0; i < purchases; i++) {
            events.add(Event.of(entityId, purchaseType, when));
        }
        return events;
    }
}
