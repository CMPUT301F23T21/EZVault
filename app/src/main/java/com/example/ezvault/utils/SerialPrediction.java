package com.example.ezvault.utils;

import androidx.annotation.NonNull;

public class SerialPrediction {
    private final double confidence;
    @NonNull
    private final String contents;

    public SerialPrediction(@NonNull String contents, double confidence) {
        this.contents = contents;
        this.confidence = confidence;
    }

    public double getConfidence() {
        return confidence;
    }

    @NonNull
    public String getContents() {
        return contents;
    }
}
