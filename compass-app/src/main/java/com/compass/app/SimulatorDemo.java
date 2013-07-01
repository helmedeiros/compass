package com.compass.app;

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

        EntityId alice = EntityId.of("alice");
        simulator.simulate(
                SyntheticBehavior.forEntity(alice).does("search", 8).does("purchase", 2),
                new DateTime(2013, 7, 1, 9, 0));

        Classification classification = compass.classifier().classify(alice);

        System.out.println("entity:           " + classification.entityId());
        System.out.println("primary profile:  " + classification.primaryProfile());
        System.out.println("confidence:       " + classification.confidence());
        System.out.println("distribution:     " + classification.distribution());
        System.out.println("evidence:         " + classification.evidence());
    }
}
