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
        System.out.println("location: " + location);
        if (location.isEmpty()) {
            System.out.println("location is empty");
            return recommendRestaurantWithoutBoundingBox(query);
        } else {
            System.out.println("location is not empty");
            return recommendRestaurantWithBoundingBox(query, location);
        }
    }

    private List<RestaurantDetailResponse> recommendRestaurantWithoutBoundingBox(String query) {
        int TOP_RESTAURANT_COUNT = 5;
        ReviewVector reviewVector = reviewVectorGenerator.generateFromContent(query);

        // 데이터베이스에서 직접 합산된 유사도로 정렬
        List<String> topRestaurants = restaurantVectorRepository.findTopRestaurantIdsByCombinedSimilarity(
                reviewVector.getVibeVector(),
                reviewVector.getFoodVector(),
                reviewVector.getCompanionVector(),
                reviewVector.getPurposeVector(),
                TOP_RESTAURANT_COUNT);
        return topRestaurants.stream().map(restaurantId -> restaurantClient
                .getRestaurantById(restaurantId))
                .collect(Collectors.toList());
    }

    private List<RestaurantDetailResponse> recommendRestaurantWithBoundingBox(String query, String location) {

        KakaoCoordinateResponse kakaoCoordinateResponse = kakaoMapAPIService.getFirstPlaceCoordinate(location);
        double longitude = kakaoCoordinateResponse.getX();
        double latitude = kakaoCoordinateResponse.getY();
        System.out.println("latitude: " + latitude);
        System.out.println("longitude: " + longitude);
        System.out.println("place name: " + kakaoCoordinateResponse.getPlaceName());
        BoundingBox boundingBox = new BoundingBox(latitude, longitude);
        int TOP_RESTAURANT_COUNT = 5;
        ReviewVector reviewVector = reviewVectorGenerator.generateFromContent(query);
        // 데이터베이스에서 직접 합산된 유사도로 정렬
        List<String> topRestaurants = restaurantVectorRepository
                .findTopRestaurantIdsByCombinedSimilarityWithBoundingBox(
                        reviewVector.getVibeVector(),
                        reviewVector.getFoodVector(),
                        reviewVector.getCompanionVector(),
                        reviewVector.getPurposeVector(),
                        boundingBox,
                        TOP_RESTAURANT_COUNT);

        topRestaurants.forEach(System.out::println);
        return topRestaurants.stream().map(restaurantId -> restaurantClient
                .getRestaurantById(restaurantId))
                .collect(Collectors.toList());
    }
}
