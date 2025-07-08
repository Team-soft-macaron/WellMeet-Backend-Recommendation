package com.wellmeet.WellMeet_Recommendation.embedding.controller;

import com.wellmeet.WellMeet_Recommendation.embedding.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmbeddingController {
    private final EmbeddingService embeddingService;
    @PostMapping("/api/embedding/generate")
    float[] getEmbedding(@RequestBody String text){
        return embeddingService.createEmbedding(text);
    }
}
