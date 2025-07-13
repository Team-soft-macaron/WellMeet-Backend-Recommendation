package com.wellmeet.WellMeet_Recommendation.common.dto;

import lombok.Getter;

@Getter
public class ReviewVector {
    private final float[] vibeVector;
    private final float[] foodVector;
    private final float[] companionVector;
    private final float[] purposeVector;

    public ReviewVector(float[] vibeVector, float[] foodVector, float[] companionVector, float[] purposeVector) {
        this.vibeVector = vibeVector;
        this.foodVector = foodVector;
        this.companionVector = companionVector;
        this.purposeVector = purposeVector;
    }
}
