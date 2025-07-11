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
public class OpenAITextProcessingService implements EmbeddingService {

    private final OpenAiEmbeddingModel openAiEmbeddingModel;

    @Override
    public float[] createEmbedding(String text) {
        EmbeddingResponse response = openAiEmbeddingModel.embedForResponse(List.of(text));
        return response.getResult().getOutput();

    }
}
