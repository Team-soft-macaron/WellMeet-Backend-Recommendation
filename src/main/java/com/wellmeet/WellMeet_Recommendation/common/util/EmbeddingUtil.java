package com.wellmeet.WellMeet_Recommendation.common.util;

import java.util.List;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.stereotype.Component;
import com.wellmeet.WellMeet_Recommendation.common.dto.ReviewVector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmbeddingUtil {
    private final OpenAiEmbeddingModel embeddingModel;

    private float[] createEmbedding(String text) {
        if (text.isEmpty()) {
            return new float[768];
        }
        log.info("text: {}", text);
        EmbeddingResponse response = embeddingModel.embedForResponse(List.of(text));
        return response.getResult().getOutput();

    }

    public ReviewVector createReviewVector(String vibe, String food, String companion, String purpose) {
        return new ReviewVector(
                createEmbedding(vibe),
                createEmbedding(food),
                createEmbedding(companion),
                createEmbedding(purpose));
    }
}
