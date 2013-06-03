package com.compass.domain.pipeline;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.compass.domain.model.Feature;
import com.compass.domain.model.Signal;

public class SaturatingSignalDetectorTest {

    private static final double TOLERANCE = 1e-9;

    private final SignalDetector detector =
            new SaturatingSignalDetector("search_depth", "high_search_depth", 10.0);

    @Test
    public void maps_a_feature_value_to_a_proportional_strength() {
        List<Signal> signals = detector.detect(Collections.singletonList(Feature.of("search_depth", 8.0)));

        assertThat(signals.size(), is(1));
        assertThat(signals.get(0).name(), is("high_search_depth"));
        assertThat(signals.get(0).strength(), is(closeTo(0.8, TOLERANCE)));
    }

    @Test
    public void caps_strength_at_one_when_the_value_exceeds_full_strength() {
        List<Signal> signals = detector.detect(Collections.singletonList(Feature.of("search_depth", 25.0)));

        assertThat(signals.get(0).strength(), is(closeTo(1.0, TOLERANCE)));
    }

    @Test
    public void clamps_a_negative_value_to_zero_strength() {
        List<Signal> signals = detector.detect(Collections.singletonList(Feature.of("search_depth", -4.0)));

        assertThat(signals.get(0).strength(), is(closeTo(0.0, TOLERANCE)));
    }

    @Test
    public void is_silent_when_its_feature_is_absent() {
        List<Signal> signals = detector.detect(Arrays.asList(
                Feature.of("session_minutes", 45.0),
                Feature.of("purchases", 2.0)));

        assertThat(signals.isEmpty(), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_non_positive_full_strength() {
        new SaturatingSignalDetector("search_depth", "high_search_depth", 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_blank_signal_name() {
        new SaturatingSignalDetector("search_depth", " ", 10.0);
    }
}
