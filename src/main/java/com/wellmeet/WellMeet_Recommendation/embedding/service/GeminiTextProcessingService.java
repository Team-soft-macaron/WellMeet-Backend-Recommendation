package com.wellmeet.WellMeet_Recommendation.embedding.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiTextProcessingService implements EmbeddingService {

    private final EmbeddingModel geminiEmbeddingModel;

    @Override
    public float[] createEmbedding(String text) {
        EmbeddingResponse response = geminiEmbeddingModel.embedForResponse(List.of(text));
        return response.getResult().getOutput();

    }
}
