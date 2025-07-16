package com.wellmeet.WellMeet_Recommendation.restaurant.service;

import com.wellmeet.WellMeet_Recommendation.common.constant.Constant;
import com.wellmeet.WellMeet_Recommendation.common.domain.ReviewVector;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.service.ReviewVectorGenerator;
import com.wellmeet.WellMeet_Recommendation.restaurant.domain.Restaurant;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RestaurantCreateRequest;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RestaurantResponse;
import com.wellmeet.WellMeet_Recommendation.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

        private final RestaurantRepository restaurantRepository;
        private final ReviewVectorGenerator reviewVectorGenerator;

        public RestaurantResponse saveRestaurant(RestaurantCreateRequest request) {
                Restaurant restaurant = new Restaurant(
                                request.getPlaceId(),
                                request.getName(),
                                request.getAddress(),
                                request.getLatitude(),
                                request.getLongitude(),
                                request.getThumbnail(),
                                new ReviewVector(new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                                                new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                                                new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                                                new float[Constant.OPENAI_EMBEDDING_DIMENSION]));

                Restaurant savedRestaurant = restaurantRepository.save(restaurant);
                return new RestaurantResponse(savedRestaurant);
        }

        public List<RestaurantResponse> recommendRestaurant(String query) {
                ReviewVector reviewVector = reviewVectorGenerator.generateFromContent(query);

                // 데이터베이스에서 직접 합산된 유사도로 정렬
                List<Restaurant> topRestaurants = restaurantRepository.findTopByCombinedSimilarity(
                                reviewVector.getVibeVector(),
                                reviewVector.getFoodVector(),
                                reviewVector.getCompanionVector(),
                                reviewVector.getPurposeVector(),
                                5);

                return topRestaurants.stream()
                                .map(RestaurantResponse::new)
                                .collect(Collectors.toList());
        }

}
