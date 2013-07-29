package com.compass.app;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import com.compass.domain.model.Classification;
import com.compass.domain.model.EntityId;
import com.compass.simulator.EventSimulator;
import com.compass.simulator.SyntheticBehavior;

public final class SimulatorDemo {

    private SimulatorDemo() {
    }

    public static void main(String[] args) {
        InMemoryCompass compass = new InMemoryCompass();
        EventSimulator simulator = new EventSimulator(compass.ingest());

        EntityId ana = EntityId.of("ana");
        simulator.simulate(
                SyntheticBehavior.forEntity(ana)
                        .does("article_view", topic("sports"), 5)
                        .does("video_watch", topic("sports"), 3)
                        .does("article_view", topic("markets"), 2)
                        .does("subscribe", 1),
                DateTime.now());

        Classification classification = compass.classifier().classify(ana);

        System.out.println("entity:           " + classification.entityId());
        System.out.println("primary profile:  " + classification.primaryProfile());
        System.out.println("confidence:       " + classification.confidence());
        System.out.println("distribution:     " + classification.distribution());
        System.out.println("evidence:         " + classification.evidence());
    }

    private static Map<String, Object> topic(String value) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("topic", value);
        return attributes;
    }
}
