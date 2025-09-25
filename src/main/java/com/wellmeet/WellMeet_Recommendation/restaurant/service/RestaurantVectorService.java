package com.wellmeet.WellMeet_Recommendation.restaurant.service;

import com.wellmeet.WellMeet_Recommendation.common.constant.Constant;
import com.wellmeet.WellMeet_Recommendation.common.domain.ReviewVector;
import com.wellmeet.WellMeet_Recommendation.common.util.LLMUtil;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.service.ReviewVectorGenerator;
import com.wellmeet.WellMeet_Recommendation.restaurant.client.RestaurantClient;
import com.wellmeet.WellMeet_Recommendation.restaurant.domain.BoundingBox;
import com.wellmeet.WellMeet_Recommendation.restaurant.domain.RestaurantVector;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.KakaoCoordinateResponse;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RestaurantCreateRequest;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RestaurantDetailResponse;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RestaurantResponse;
import com.wellmeet.WellMeet_Recommendation.restaurant.repository.RestaurantVectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantVectorService {

    private final RestaurantVectorRepository restaurantVectorRepository;
    private final ReviewVectorGenerator reviewVectorGenerator;
    private final RestaurantClient restaurantClient;
    private final KakaoMapAPIService kakaoMapAPIService;
    private final LLMUtil llmUtil;

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

    public List<RestaurantDetailResponse> recommendRestaurants(String query) {

        String location = llmUtil.extractLocation(query);
        if (location.isEmpty()) {
            return recommendRestaurantWithoutBoundingBox(query);
        } else {
            return recommendRestaurantWithBoundingBox(query, location);
        }
    }

    private List<RestaurantDetailResponse> recommendRestaurantWithoutBoundingBox(String query) {
        ReviewVector reviewVector = reviewVectorGenerator.generateFromContent(query);

        List<String> topRestaurants = restaurantVectorRepository.findTopRestaurantIdsByCombinedSimilarity(
                reviewVector.getVibeVector(),
                reviewVector.getFoodVector(),
                reviewVector.getCompanionVector(),
                reviewVector.getPurposeVector(),
                5);

        return topRestaurants.stream().map(restaurantId -> restaurantClient
                .getRestaurantById(restaurantId))
                .collect(Collectors.toList());
    }

    private List<RestaurantDetailResponse> recommendRestaurantWithBoundingBox(String query, String location) {

        KakaoCoordinateResponse kakaoCoordinateResponse = kakaoMapAPIService.getFirstPlaceCoordinate(location);
        BoundingBox boundingBox = new BoundingBox(kakaoCoordinateResponse.getLatitude(),
                kakaoCoordinateResponse.getLongitude());
        ReviewVector reviewVector = reviewVectorGenerator.generateFromContent(query);

        List<String> topRestaurants = restaurantVectorRepository
                .findTopRestaurantIdsByCombinedSimilarityWithBoundingBox(
                        reviewVector.getVibeVector(),
                        reviewVector.getFoodVector(),
                        reviewVector.getCompanionVector(),
                        reviewVector.getPurposeVector(),
                        boundingBox,
                        5);
        topRestaurants = new ArrayList<>();
        topRestaurants.add("1");
        topRestaurants.add("2");
        topRestaurants.add("3");
        return topRestaurants.stream().map(restaurantId -> restaurantClient
                .getRestaurantById(restaurantId))
                .collect(Collectors.toList());
    }
}
