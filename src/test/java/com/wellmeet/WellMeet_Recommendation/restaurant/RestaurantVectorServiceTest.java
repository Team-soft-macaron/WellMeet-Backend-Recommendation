package com.wellmeet.WellMeet_Recommendation.restaurant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.wellmeet.WellMeet_Recommendation.common.constant.Constant;
import com.wellmeet.WellMeet_Recommendation.common.domain.ReviewVector;
import com.wellmeet.WellMeet_Recommendation.common.util.LLMUtil;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.service.ReviewVectorGenerator;
import com.wellmeet.WellMeet_Recommendation.restaurant.client.RestaurantClient;
import com.wellmeet.WellMeet_Recommendation.restaurant.domain.BoundingBox;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.KakaoCoordinateResponse;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RestaurantDetailResponse;
import com.wellmeet.WellMeet_Recommendation.restaurant.repository.RestaurantVectorRepository;
import com.wellmeet.WellMeet_Recommendation.restaurant.service.KakaoMapAPIService;
import com.wellmeet.WellMeet_Recommendation.restaurant.service.RestaurantVectorService;

@ExtendWith(MockitoExtension.class)
public class RestaurantVectorServiceTest {

    @Mock
    private RestaurantVectorRepository restaurantVectorRepository;

    @Mock
    private ReviewVectorGenerator reviewVectorGenerator;

    @Mock
    private RestaurantClient restaurantClient;

    @Mock
    private LLMUtil llmUtil;

    @Mock
    private KakaoMapAPIService kakaoMapAPIService;

    @InjectMocks
    private RestaurantVectorService restaurantVectorService;

    @Test
    @DisplayName("위치 정보가 없을 때 findTopRestaurantIdsByCombinedSimilarity가 호출된다.")
    void recommendRestaurantsWithoutLocationSuccess() {
        String testQuery = "여자친구와 데이트하기 좋은 분위기 있는 파스타 맛집 추천해줘";
        ReviewVector mockReviewVector = createMockReviewVector();
        List<RestaurantDetailResponse> mockRestaurants = createMockRestaurants();

        setupMockLocationEmpty(testQuery, mockReviewVector, mockRestaurants);

        List<RestaurantDetailResponse> result = restaurantVectorService.recommendRestaurants(testQuery);

        assertRestaurantList(result, mockRestaurants);
        verifyLocationEmpty(testQuery, mockReviewVector);
    }

    @Test
    @DisplayName("위치 정보가 있을 때 findTopRestaurantIdsByCombinedSimilarityWithBoundingBox가 호출된다.")
    void recommendRestaurantsWithLocationSuccess() {
        String testQuery = "강남역 근처에서 여자친구와 데이트하기 좋은 분위기 있는 파스타 맛집 추천해줘";
        String location = "강남역";
        ReviewVector mockReviewVector = createMockReviewVector();
        List<RestaurantDetailResponse> mockRestaurants = createMockRestaurants();
        KakaoCoordinateResponse mockCoordinate = createMockCoordinate();

        setupMockLocationExists(testQuery, location, mockReviewVector, mockRestaurants, mockCoordinate);

        List<RestaurantDetailResponse> result = restaurantVectorService.recommendRestaurants(testQuery);

        assertRestaurantList(result, mockRestaurants);
        verifyLocationExists(testQuery, location, mockReviewVector, mockCoordinate);
    }

    private ReviewVector createMockReviewVector() {
        return new ReviewVector(
                new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                new float[Constant.OPENAI_EMBEDDING_DIMENSION]);
    }

    private List<RestaurantDetailResponse> createMockRestaurants() {
        return Arrays.asList(
                new RestaurantDetailResponse("1", "레스토랑1", "서울시 강남구", 3.7, 1, 35.9780, 126.9780, ""),
                new RestaurantDetailResponse("2", "레스토랑2", "서울시 서초구", 4.1, 1, 34.9780, 126.9780, ""),
                new RestaurantDetailResponse("3", "레스토랑3", "서울시 송파구", 5.0, 1, 37.9780, 126.9780, ""),
                new RestaurantDetailResponse("4", "레스토랑4", "서울시 마포구", 1.1, 1, 36.9780, 126.9780, ""),
                new RestaurantDetailResponse("5", "레스토랑5", "서울시 용산구", 2.1, 1, 38.9780, 126.9780, ""));
    }

    private KakaoCoordinateResponse createMockCoordinate() {
        return new KakaoCoordinateResponse(127.0276, 37.4979, "강남역");
    }

    private void setupMockLocationEmpty(String query, ReviewVector reviewVector,
            List<RestaurantDetailResponse> restaurants) {
        when(llmUtil.extractLocation(query)).thenReturn("");
        when(reviewVectorGenerator.generateFromContent(query)).thenReturn(reviewVector);
        when(restaurantVectorRepository.findTopRestaurantIdsByCombinedSimilarity(
                reviewVector.getVibeVector(),
                reviewVector.getFoodVector(),
                reviewVector.getCompanionVector(),
                reviewVector.getPurposeVector(),
                5))
                .thenReturn(extractRestaurantIds(restaurants));

        setupRestaurantClientMocks(restaurants);
    }

    private void setupMockLocationExists(String query, String location, ReviewVector reviewVector,
            List<RestaurantDetailResponse> restaurants, KakaoCoordinateResponse coordinate) {
        when(llmUtil.extractLocation(query)).thenReturn(location);
        when(kakaoMapAPIService.getFirstPlaceCoordinate(location)).thenReturn(coordinate);
        when(reviewVectorGenerator.generateFromContent(query)).thenReturn(reviewVector);

        when(restaurantVectorRepository.findTopRestaurantIdsByCombinedSimilarityWithBoundingBox(
                reviewVector.getVibeVector(),
                reviewVector.getFoodVector(),
                reviewVector.getCompanionVector(),
                reviewVector.getPurposeVector(),
                new BoundingBox(coordinate.getLatitude(), coordinate.getLongitude()),
                5))
                .thenReturn(extractRestaurantIds(restaurants));

        setupRestaurantClientMocks(restaurants);
    }

    private void setupRestaurantClientMocks(List<RestaurantDetailResponse> restaurants) {
        for (RestaurantDetailResponse restaurant : restaurants) {
            when(restaurantClient.getRestaurantById(restaurant.getId()))
                    .thenReturn(restaurant);
        }
    }

    private List<String> extractRestaurantIds(List<RestaurantDetailResponse> restaurants) {
        return restaurants.stream()
                .map(RestaurantDetailResponse::getId)
                .collect(Collectors.toList());
    }

    private void verifyLocationEmpty(String query, ReviewVector reviewVector) {
        verify(kakaoMapAPIService, never()).getFirstPlaceCoordinate(any());
        verify(restaurantVectorRepository, times(1)).findTopRestaurantIdsByCombinedSimilarity(
                reviewVector.getVibeVector(),
                reviewVector.getFoodVector(),
                reviewVector.getCompanionVector(),
                reviewVector.getPurposeVector(),
                5);
    }

    private void verifyLocationExists(String query, String location, ReviewVector reviewVector,
            KakaoCoordinateResponse coordinate) {
        verify(kakaoMapAPIService, times(1)).getFirstPlaceCoordinate(location);
        verify(restaurantVectorRepository, times(1)).findTopRestaurantIdsByCombinedSimilarityWithBoundingBox(
                reviewVector.getVibeVector(),
                reviewVector.getFoodVector(),
                reviewVector.getCompanionVector(),
                reviewVector.getPurposeVector(),
                new BoundingBox(coordinate.getLatitude(), coordinate.getLongitude()),
                5);
    }

    private void assertRestaurantList(List<RestaurantDetailResponse> result,
            List<RestaurantDetailResponse> expectedRestaurants) {
        assertNotNull(result);
        assertEquals(5, result.size());

        for (RestaurantDetailResponse response : result) {
            RestaurantDetailResponse expectedRestaurant = expectedRestaurants.stream()
                    .filter(restaurant -> restaurant.getId().equals(response.getId()))
                    .findFirst()
                    .orElse(null);

            assertAll(
                    () -> assertEquals(expectedRestaurant.getId(), response.getId()),
                    () -> assertEquals(expectedRestaurant.getName(), response.getName()),
                    () -> assertEquals(expectedRestaurant.getAddress(), response.getAddress()),
                    () -> assertEquals(expectedRestaurant.getRating(), response.getRating(), 0.01),
                    () -> assertEquals(expectedRestaurant.getReviewCount(),
                            response.getReviewCount()),
                    () -> assertEquals(expectedRestaurant.getLatitude(), response.getLatitude(),
                            0.01),
                    () -> assertEquals(expectedRestaurant.getLongitude(), response.getLongitude(),
                            0.01),
                    () -> assertEquals(expectedRestaurant.getThumbnail(), response.getThumbnail()));
        }
    }
}
