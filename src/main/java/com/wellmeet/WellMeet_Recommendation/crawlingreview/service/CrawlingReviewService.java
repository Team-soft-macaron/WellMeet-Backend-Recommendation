package com.wellmeet.WellMeet_Recommendation.crawlingreview.service;

import com.wellmeet.WellMeet_Recommendation.common.domain.ReviewVector;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.domain.CrawlingReview;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.dto.CrawlingReviewSaveRequest;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.dto.CrawlingReviewResponse;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.repository.CrawlingReviewRepository;
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
        private final ReviewVectorGenerator reviewVectorGenerator;

        public CrawlingReviewResponse saveReview(CrawlingReviewSaveRequest request) {
                Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                                .orElseThrow(() -> new WellMeetException(ErrorCode.RESTAURANT_NOT_FOUND));

                ReviewVector reviewVector = reviewVectorGenerator.generateFromContent(request.getContent());

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

                ReviewVector oldVector = restaurant.createReviewVector();
                ReviewVector updatedVector = oldVector.calculateIncrementalAverage(newVector, reviewCount);
                restaurant.updateVectors(updatedVector);

                restaurantRepository.save(restaurant);
        }
}
