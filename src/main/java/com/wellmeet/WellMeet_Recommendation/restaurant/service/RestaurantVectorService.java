package com.wellmeet.WellMeet_Recommendation.restaurant.service;

import com.wellmeet.WellMeet_Recommendation.common.constant.Constant;
import com.wellmeet.WellMeet_Recommendation.common.domain.ReviewVector;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.service.ReviewVectorGenerator;
import com.wellmeet.WellMeet_Recommendation.restaurant.client.RestaurantClient;
import com.wellmeet.WellMeet_Recommendation.restaurant.domain.RestaurantVector;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RestaurantCreateRequest;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RestaurantDetailResponse;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RestaurantResponse;
import com.wellmeet.WellMeet_Recommendation.restaurant.repository.RestaurantVectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantVectorService {

        private final RestaurantVectorRepository restaurantVectorRepository;
        private final ReviewVectorGenerator reviewVectorGenerator;
        private final RestaurantClient restaurantClient;

        public RestaurantResponse saveRestaurant(RestaurantCreateRequest request) {
                RestaurantVector restaurant = new RestaurantVector(
                                request.getRestaurantId(),
                                request.getPlaceId(),
                                request.getLatitude(),
                                request.getLongitude(),
                                new ReviewVector(new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                                                new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                                                new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                                                new float[Constant.OPENAI_EMBEDDING_DIMENSION]));

                RestaurantVector savedRestaurant = restaurantVectorRepository.save(restaurant);
                return new RestaurantResponse(savedRestaurant);
        }

        public List<RestaurantDetailResponse> recommendRestaurant(String query) {
                int TOP_RESTAURANT_COUNT = 5;
                ReviewVector reviewVector = reviewVectorGenerator.generateFromContent(query);

                // 데이터베이스에서 직접 합산된 유사도로 정렬
                List<Long> topRestaurants = restaurantVectorRepository.findTopRestaurantIdsByCombinedSimilarity(
                                reviewVector.getVibeVector(),
                                reviewVector.getFoodVector(),
                                reviewVector.getCompanionVector(),
                                reviewVector.getPurposeVector(),
                                TOP_RESTAURANT_COUNT);
                return topRestaurants.stream().map(restaurantId -> restaurantClient
                                .getRestaurantById(restaurantId))
                                .collect(Collectors.toList());
        }

}
