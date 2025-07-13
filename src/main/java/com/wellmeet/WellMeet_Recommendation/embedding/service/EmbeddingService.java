package com.wellmeet.WellMeet_Recommendation.embedding.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {

    private final OpenAiEmbeddingModel embeddingModel;

    public float[] createEmbedding(String text) {
        if (text.isEmpty()) {
            return new float[768];
        }
        log.info("text: {}", text);
        EmbeddingResponse response = embeddingModel.embedForResponse(List.of(text));
        return response.getResult().getOutput();

    }
}