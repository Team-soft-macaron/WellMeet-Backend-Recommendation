package com.wellmeet.WellMeet_Recommendation.common.util;

import org.springframework.stereotype.Component;

import com.wellmeet.WellMeet_Recommendation.common.dto.ReviewVector;
import com.wellmeet.WellMeet_Recommendation.embedding.service.EmbeddingService;

@Component
public class ReviewVectorUtil {
    private final EmbeddingService embeddingService;

    public ReviewVectorUtil(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    public ReviewVector createReviewVector(String vibe, String food, String companion, String purpose) {
        return new ReviewVector(
                embeddingService.createEmbedding(vibe),
                embeddingService.createEmbedding(food),
                embeddingService.createEmbedding(companion),
                embeddingService.createEmbedding(purpose));
    }
}
