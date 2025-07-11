package com.wellmeet.WellMeet_Recommendation.llm.service;

import com.wellmeet.WellMeet_Recommendation.llm.dto.ExtractedInfoResponse;

public interface LLMService {
    ExtractedInfoResponse extractUserRequest(String userQuery);
}
