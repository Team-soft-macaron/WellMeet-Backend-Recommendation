package com.wellmeet.WellMeet_Recommendation.crawlingreview.service;

import com.wellmeet.WellMeet_Recommendation.common.domain.ReviewVector;
import com.wellmeet.WellMeet_Recommendation.common.dto.ExtractedInfoResponse;
import com.wellmeet.WellMeet_Recommendation.common.util.EmbeddingUtil;
import com.wellmeet.WellMeet_Recommendation.common.util.LLMUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewVectorGenerator {
    private final LLMUtil llmUtil;
    private final EmbeddingUtil embeddingUtil;

    public ReviewVector generateFromContent(String content) {
        // 리뷰 추출
        // "여자친구와 데이트 하는데 분위기 좋은 파스타 집 추천 좀"
        // -> {vibe: "분위기 좋은", food: "파스타", companion: "여자친구", purpose: "데이트"}
        ExtractedInfoResponse extractedInfo = llmUtil.extractUserRequest(content);

        // 벡터 생성
        return embeddingUtil.createReviewVector(
                extractedInfo.getVibe(),
                extractedInfo.getFood(),
                extractedInfo.getCompanion(),
                extractedInfo.getPurpose());
    }
}
