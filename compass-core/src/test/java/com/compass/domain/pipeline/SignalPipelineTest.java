package com.compass.domain.pipeline;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.compass.domain.model.Feature;
import com.compass.domain.model.Signal;

public class SignalPipelineTest {

    @Test
    public void runs_every_detector_and_collects_their_signals() {
        SignalPipeline pipeline = new SignalPipeline(Arrays.<SignalDetector>asList(
                new SaturatingSignalDetector("search_depth", "high_search_depth", 10.0),
                new SaturatingSignalDetector("purchases", "frequent_buyer", 4.0)));
        List<Feature> features = Arrays.asList(
                Feature.of("search_depth", 5.0),
                Feature.of("purchases", 4.0));

        List<Signal> signals = pipeline.detect(features);

        assertThat(signals, contains(
                Signal.of("high_search_depth", 0.5),
                Signal.of("frequent_buyer", 1.0)));
    }

    @Test
    public void has_no_signals_when_there_are_no_detectors() {
        SignalPipeline pipeline = new SignalPipeline(Collections.<SignalDetector>emptyList());

        assertThat(pipeline.detect(Collections.<Feature>emptyList()).isEmpty(), is(true));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void exposes_signals_as_a_read_only_list() {
        SignalPipeline pipeline = new SignalPipeline(Collections.<SignalDetector>emptyList());

        pipeline.detect(Collections.<Feature>emptyList()).add(Signal.of("x", 0.5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_null_detectors() {
        new SignalPipeline(null);
    }
}
