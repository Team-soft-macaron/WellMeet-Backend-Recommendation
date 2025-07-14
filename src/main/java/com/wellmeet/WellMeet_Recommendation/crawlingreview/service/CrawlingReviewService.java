package com.wellmeet.WellMeet_Recommendation.crawlingreview.service;

import com.wellmeet.WellMeet_Recommendation.common.dto.ExtractedInfoResponse;
import com.wellmeet.WellMeet_Recommendation.common.dto.ReviewVector;
import com.wellmeet.WellMeet_Recommendation.common.util.EmbeddingUtil;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.domain.CrawlingReview;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.dto.CrawlingReviewSaveRequest;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.dto.CrawlingReviewResponse;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.repository.CrawlingReviewRepository;
import com.wellmeet.WellMeet_Recommendation.common.util.LLMUtil;
import com.wellmeet.WellMeet_Recommendation.exception.ErrorCode;
import com.wellmeet.WellMeet_Recommendation.exception.WellMeetException;
import com.wellmeet.WellMeet_Recommendation.restaurant.domain.Restaurant;
import com.wellmeet.WellMeet_Recommendation.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrawlingReviewService {

    private final CrawlingReviewRepository crawlingReviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final LLMUtil llmUtil;
    private final EmbeddingUtil embeddingUtil;

    public CrawlingReviewResponse saveReview(CrawlingReviewSaveRequest request) {
        Restaurant restaurant = restaurantRepository.findByPlaceId(request.getRestaurantId().toString())
                .orElseThrow(() -> new WellMeetException(ErrorCode.RESTAURANT_NOT_FOUND));

        // 리뷰 추출
        // ex) {vibe: "친구와 함께 하기 좋은", food: "치킨", companion: "친구", purpose: "데이트"}
        ExtractedInfoResponse extractedInfoResponse = llmUtil.extractUserRequest(request.getContent());

        ReviewVector reviewVector = embeddingUtil.createReviewVector(
                extractedInfoResponse.getVibe(),
                extractedInfoResponse.getFood(),
                extractedInfoResponse.getCompanion(),
                extractedInfoResponse.getPurpose());

        CrawlingReview review = new CrawlingReview(
                request.getContent(),
                restaurant,
                request.getHash(),
                reviewVector);

        CrawlingReview savedCrawlingReview = crawlingReviewRepository.save(review);

        updateRestaurantVectorsIncremental(restaurant, reviewVector);

        return new CrawlingReviewResponse(savedCrawlingReview);
    }

    private void updateRestaurantVectorsIncremental(Restaurant restaurant, ReviewVector newVector) {
        long reviewCount = crawlingReviewRepository.countByRestaurantId(restaurant.getId());

        ReviewVector oldVector = new ReviewVector(
                restaurant.getVibeVector(),
                restaurant.getFoodVector(),
                restaurant.getCompanionVector(),
                restaurant.getPurposeVector());
        ReviewVector updatedVector = ReviewVector.calculateIncrementalAverage(
                oldVector,
                newVector,
                reviewCount);
        restaurant.updateVectors(updatedVector);

        restaurantRepository.save(restaurant);
    }
}
