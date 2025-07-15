package com.wellmeet.WellMeet_Recommendation.common.domain;

import com.wellmeet.WellMeet_Recommendation.common.constant.Constant;
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

    public static ReviewVector calculateIncrementalAverage(ReviewVector oldAverage, ReviewVector newVector,
            long totalCount) {
        float[] vibe = calculateIncrementalAverageArray(oldAverage.getVibeVector(), newVector.getVibeVector(),
                totalCount);
        float[] food = calculateIncrementalAverageArray(oldAverage.getFoodVector(), newVector.getFoodVector(),
                totalCount);
        float[] companion = calculateIncrementalAverageArray(oldAverage.getCompanionVector(),
                newVector.getCompanionVector(), totalCount);
        float[] purpose = calculateIncrementalAverageArray(oldAverage.getPurposeVector(),
                newVector.getPurposeVector(), totalCount);
        return new ReviewVector(vibe, food, companion, purpose);
    }

    private static float[] calculateIncrementalAverageArray(float[] oldAvg, float[] newVec, long totalCount) {
        float[] result = new float[Constant.OPENAI_EMBEDDING_DIMENSION];
        float weight = (totalCount - 1) / (float) totalCount;
        float newWeight = 1.0f / totalCount;
        for (int i = 0; i < Constant.OPENAI_EMBEDDING_DIMENSION; i++) {
            result[i] = oldAvg[i] * weight + newVec[i] * newWeight;
        }
        return result;
    }
}
