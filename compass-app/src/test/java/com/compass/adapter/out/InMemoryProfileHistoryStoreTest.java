package com.compass.adapter.out;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.junit.Test;

import com.compass.domain.model.Classification;
import com.compass.domain.model.EntityId;
import com.compass.domain.model.Evidence;
import com.compass.domain.model.Profile;
import com.compass.domain.model.ProfileDistribution;

public class InMemoryProfileHistoryStoreTest {

    private final EntityId alice = EntityId.of("alice");
    private final EntityId bob = EntityId.of("bob");

    private final InMemoryProfileHistoryStore store = new InMemoryProfileHistoryStore();

    @Test
    public void keeps_an_entity_history_in_the_order_it_was_recorded() {
        Classification first = classificationFor(alice, "Explorer");
        Classification second = classificationFor(alice, "Expert");
        store.record(first);
        store.record(second);

        assertThat(store.historyOf(alice), contains(first, second));
    }

    @Test
    public void keeps_each_entity_history_apart() {
        Classification aliceProfile = classificationFor(alice, "Explorer");
        Classification bobProfile = classificationFor(bob, "Expert");
        store.record(aliceProfile);
        store.record(bobProfile);

        assertThat(store.historyOf(alice), contains(aliceProfile));
        assertThat(store.historyOf(bob), contains(bobProfile));
    }

    @Test
    public void has_no_history_for_an_unknown_entity() {
        assertThat(store.historyOf(alice).isEmpty(), is(true));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void exposes_history_as_a_read_only_list() {
        store.record(classificationFor(alice, "Explorer"));

        store.historyOf(alice).add(classificationFor(alice, "Expert"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_null_classification() {
        store.record(null);
    }

    private Classification classificationFor(EntityId entityId, String profileName) {
        return Classification.of(entityId,
                ProfileDistribution.of(Collections.singletonMap(Profile.of(profileName), 1.0)),
                Collections.<Evidence>emptyList());
    }
}
