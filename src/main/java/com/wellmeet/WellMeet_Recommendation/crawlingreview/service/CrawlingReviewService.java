package com.wellmeet.WellMeet_Recommendation.crawlingreview.service;

import com.wellmeet.WellMeet_Recommendation.common.dto.ReviewVector;
import com.wellmeet.WellMeet_Recommendation.embedding.service.EmbeddingService;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.domain.CrawlingReview;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.dto.CrawlingReviewSaveRequest;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.dto.CrawlingReviewResponse;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.repository.CrawlingReviewRepository;
import com.wellmeet.WellMeet_Recommendation.exception.ErrorCode;
import com.wellmeet.WellMeet_Recommendation.exception.WellMeetException;
import com.wellmeet.WellMeet_Recommendation.llm.dto.ExtractedInfoResponse;
import com.wellmeet.WellMeet_Recommendation.llm.service.LLMService;
import com.wellmeet.WellMeet_Recommendation.restaurant.domain.Restaurant;
import com.wellmeet.WellMeet_Recommendation.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrawlingReviewService {

        private final CrawlingReviewRepository crawlingReviewRepository;
        private final RestaurantRepository restaurantRepository;
        private final LLMService llmService;
        private final EmbeddingService embeddingService;

        public CrawlingReviewResponse saveReview(CrawlingReviewSaveRequest request) {
                // 식당 조회
                Restaurant restaurant = restaurantRepository.findByPlaceId(request.getRestaurantId().toString())
                                .orElseThrow(() -> new WellMeetException(ErrorCode.RESTAURANT_NOT_FOUND));

                // 리뷰 추출
                // ex) {vibe: "친구와 함께 하기 좋은", food: "치킨", companion: "친구", purpose: "데이트"}
                ExtractedInfoResponse extractedInfoResponse = llmService.extractUserRequest(request.getContent());

                // 리뷰 벡터 생성
                ReviewVector reviewVector = embeddingService.createReviewVector(
                                extractedInfoResponse.getVibe(),
                                extractedInfoResponse.getFood(),
                                extractedInfoResponse.getCompanion(),
                                extractedInfoResponse.getPurpose());

                // 리뷰 저장
                CrawlingReview review = new CrawlingReview(
                                request.getContent(),
                                restaurant,
                                request.getHash(),
                                reviewVector);

                CrawlingReview savedCrawlingReview = crawlingReviewRepository.save(review);

                // 증분 평균으로 식당 벡터 업데이트
                updateRestaurantVectorsIncremental(restaurant, reviewVector);

                return new CrawlingReviewResponse(savedCrawlingReview);
        }

        private void updateRestaurantVectorsIncremental(Restaurant restaurant, ReviewVector newVector) {
                // 현재 리뷰 개수 (방금 추가한 것 포함)
                long reviewCount = crawlingReviewRepository.countByRestaurantId(restaurant.getId());

                // 증분 평균 계산: newAvg = (oldAvg * (n-1) + newValue) / n
                ReviewVector oldVector = new ReviewVector(
                                restaurant.getVibeVector(),
                                restaurant.getFoodVector(),
                                restaurant.getCompanionVector(),
                                restaurant.getPurposeVector());
                ReviewVector updatedVector = calculateIncrementalAverage(
                                oldVector,
                                newVector,
                                reviewCount);
                restaurant.updateVectors(updatedVector);

                restaurantRepository.save(restaurant);
        }

        private ReviewVector calculateIncrementalAverage(ReviewVector oldAverage, ReviewVector newVector,
                        long totalCount) {
                float[] vibe = calculateIncrementalAverageArray(oldAverage.getVibeVector(), newVector.getVibeVector(),
                                totalCount);
                float[] food = calculateIncrementalAverageArray(oldAverage.getFoodVector(), newVector.getFoodVector(),
                                totalCount);
                float[] companion = calculateIncrementalAverageArray(oldAverage.getCompanionVector(),
                                newVector.getCompanionVector(), totalCount);
                float[] purpose = calculateIncrementalAverageArray(oldAverage.getPurposeVector(),
                                newVector.getPurposeVector(), totalCount);
                return new ReviewVector(vibe, food, companion, purpose);
        }

        private float[] calculateIncrementalAverageArray(float[] oldAvg, float[] newVec, long totalCount) {
                float[] result = new float[768];
                float weight = (totalCount - 1) / (float) totalCount;
                float newWeight = 1.0f / totalCount;
                for (int i = 0; i < 768; i++) {
                        result[i] = oldAvg[i] * weight + newVec[i] * newWeight;
                }
                return result;
        }
}
