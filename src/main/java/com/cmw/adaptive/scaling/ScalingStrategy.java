package com.cmw.adaptive.scaling;

public interface ScalingStrategy {

    int desiredWorkers(ScalingContext context);
}