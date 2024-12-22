package com.fmoraes.ssedemo;

public record Progress(double value) {

    public Progress {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException("Value must be between 0.0 and 1.0");
        }
    }
}
