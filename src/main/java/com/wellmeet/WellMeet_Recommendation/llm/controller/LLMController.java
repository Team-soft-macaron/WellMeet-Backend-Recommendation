package com.wellmeet.WellMeet_Recommendation.llm.controller;

import com.wellmeet.WellMeet_Recommendation.llm.dto.ExtractedInfoResponse;
import com.wellmeet.WellMeet_Recommendation.llm.service.LLMService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LLMController {
    private final LLMService LLMService;

    @PostMapping("/api/llm/generate")
    public ExtractedInfoResponse getExtractedInfo(@RequestBody String query) {
        return LLMService.extractUserRequest(query);
    }

}
