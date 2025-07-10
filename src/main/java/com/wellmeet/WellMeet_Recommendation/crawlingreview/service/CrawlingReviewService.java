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

        // 증분 평균으로 식당 벡터 업데이트
        updateRestaurantVectorsIncremental(restaurant, vibeVector, foodVector, companionVector, purposeVector);

        return new CrawlingReviewResponse(savedCrawlingReview);
    }

    private void updateRestaurantVectorsIncremental(Restaurant restaurant,
                                                    float[] newVibeVector,
                                                    float[] newFoodVector,
                                                    float[] newCompanionVector,
                                                    float[] newPurposeVector) {
        // 현재 리뷰 개수 (방금 추가한 것 포함)
        long reviewCount = crawlingReviewRepository.countByRestaurantId(restaurant.getId());

        if (reviewCount == 1) {
            // 첫 번째 리뷰인 경우
            restaurant.updateVectors(newVibeVector, newFoodVector, newCompanionVector, newPurposeVector);
        } else {
            // 증분 평균 계산: newAvg = (oldAvg * (n-1) + newValue) / n
            float[] updatedVibeVector = calculateIncrementalAverage(
                    restaurant.getVibeVector(), newVibeVector, reviewCount);
            float[] updatedFoodVector = calculateIncrementalAverage(
                    restaurant.getFoodVector(), newFoodVector, reviewCount);
            float[] updatedCompanionVector = calculateIncrementalAverage(
                    restaurant.getCompanionVector(), newCompanionVector, reviewCount);
            float[] updatedPurposeVector = calculateIncrementalAverage(
                    restaurant.getPurposeVector(), newPurposeVector, reviewCount);

            restaurant.updateVectors(updatedVibeVector, updatedFoodVector,
                    updatedCompanionVector, updatedPurposeVector);
        }

        restaurantRepository.save(restaurant);
    }

    private float[] calculateIncrementalAverage(float[] oldAverage, float[] newVector, long totalCount) {
        float[] result = new float[768];
        float weight = (totalCount - 1) / (float) totalCount;
        float newWeight = 1.0f / totalCount;

        for (int i = 0; i < 768; i++) {
            result[i] = oldAverage[i] * weight + newVector[i] * newWeight;
        }

        return result;
    }
}
