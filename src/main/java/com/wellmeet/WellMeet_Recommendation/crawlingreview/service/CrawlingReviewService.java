package com.wellmeet.WellMeet_Recommendation.crawlingreview.service;

import com.wellmeet.WellMeet_Recommendation.crawlingreview.domain.CrawlingReview;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.dto.CrawlingReviewSaveRequest;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.dto.CrawlingReviewResponse;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.repository.CrawlingReviewRepository;
import com.wellmeet.WellMeet_Recommendation.embedding.service.EmbeddingService;
import com.wellmeet.WellMeet_Recommendation.exception.ErrorCode;
import com.wellmeet.WellMeet_Recommendation.exception.WellMeetException;
import com.wellmeet.WellMeet_Recommendation.llm.dto.ExtractedInfoResponse;
import com.wellmeet.WellMeet_Recommendation.llm.service.LLMService;
import com.wellmeet.WellMeet_Recommendation.restaurant.domain.Restaurant;
import com.wellmeet.WellMeet_Recommendation.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrawlingReviewService {

    private final CrawlingReviewRepository crawlingReviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final EmbeddingService embeddingService;
    private final LLMService llmService;

    public CrawlingReviewResponse saveReview(CrawlingReviewSaveRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new WellMeetException(ErrorCode.RESTAURANT_NOT_FOUND));

        ExtractedInfoResponse extractedInfoResponse = llmService.extractUserRequest(request.getContent());
        float[] vibeVector = extractedInfoResponse.getVibe().isEmpty() ? new float[768] : embeddingService.createEmbedding(extractedInfoResponse.getVibe());
        float[] foodVector = extractedInfoResponse.getFood().isEmpty() ? new float[768] : embeddingService.createEmbedding(extractedInfoResponse.getFood());
        float[] companionVector = extractedInfoResponse.getCompanion().isEmpty() ? new float[768] : embeddingService.createEmbedding(extractedInfoResponse.getCompanion());
        float[] purposeVector = extractedInfoResponse.getPurpose().isEmpty() ? new float[768] : embeddingService.createEmbedding(extractedInfoResponse.getPurpose());
        CrawlingReview review = new CrawlingReview(
                request.getContent(),
                restaurant,
                request.getHash(),
                vibeVector,
                foodVector,
                companionVector,
                purposeVector
        );

        CrawlingReview savedCrawlingReview = crawlingReviewRepository.save(review);
        return new CrawlingReviewResponse(savedCrawlingReview);
    }
}
