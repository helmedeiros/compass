package com.compass.domain.inference;

import java.util.List;

import com.compass.domain.model.Signal;

public interface InferenceModel {

    Inference infer(List<Signal> signals);
}
