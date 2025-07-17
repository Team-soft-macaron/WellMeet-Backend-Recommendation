package com.wellmeet.WellMeet_Recommendation.restaurant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
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
import com.wellmeet.WellMeet_Recommendation.crawlingreview.service.ReviewVectorGenerator;
import com.wellmeet.WellMeet_Recommendation.restaurant.client.RestaurantClient;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RestaurantDetailResponse;
import com.wellmeet.WellMeet_Recommendation.restaurant.repository.RestaurantVectorRepository;
import com.wellmeet.WellMeet_Recommendation.restaurant.service.RestaurantVectorService;

@ExtendWith(MockitoExtension.class)
public class RestaurantVectorServiceTest {

        @Mock
        private RestaurantVectorRepository restaurantVectorRepository;

        @Mock
        private ReviewVectorGenerator reviewVectorGenerator;

        @Mock
        private RestaurantClient restaurantClient;

        @InjectMocks
        private RestaurantVectorService restaurantVectorService;

        @Test
        @DisplayName("쿼리를 입력받아 추천 레스토랑 목록을 반환한다")
        void recommendRestaurantSuccess() {
                // given
                String testQuery = "여자친구와 데이트하기 좋은 분위기 있는 파스타 맛집 추천해줘";
                ReviewVector mockReviewVector = new ReviewVector(
                                new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                                new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                                new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                                new float[Constant.OPENAI_EMBEDDING_DIMENSION]);

                List<RestaurantDetailResponse> mockRestaurants = Arrays.asList(
                                new RestaurantDetailResponse(1L, "1", "서울시 강남구", 3.7, 1, 35.9780, 126.9780, ""),
                                new RestaurantDetailResponse(2L, "2", "서울시 서초구", 4.1, 1, 34.9780, 126.9780, ""),
                                new RestaurantDetailResponse(3L, "3", "서울시 송파구", 5.0, 1, 37.9780, 126.9780, ""),
                                new RestaurantDetailResponse(4L, "4", "서울시 마포구", 1.1, 1, 36.9780, 126.9780, ""),
                                new RestaurantDetailResponse(5L, "5", "서울시 용산구", 2.1, 1, 38.9780, 126.9780, ""));
                // when
                for (int i = 0; i < mockRestaurants.size(); i++) {
                        when(restaurantClient.getRestaurantById(mockRestaurants.get(i).getId()))
                                        .thenReturn(mockRestaurants.get(i));
                }
                when(reviewVectorGenerator.generateFromContent(testQuery)).thenReturn(mockReviewVector);
                when(restaurantVectorRepository.findTopRestaurantIdsByCombinedSimilarity(
                                mockReviewVector.getVibeVector(),
                                mockReviewVector.getFoodVector(),
                                mockReviewVector.getCompanionVector(),
                                mockReviewVector.getPurposeVector(),
                                5))
                                .thenReturn(mockRestaurants.stream().map(RestaurantDetailResponse::getId)
                                                .collect(Collectors.toList()));
                List<RestaurantDetailResponse> result = restaurantVectorService.recommendRestaurant(testQuery);

                // then
                assertNotNull(result);
                assertEquals(5, result.size());
                for (RestaurantDetailResponse response : result) {
                        RestaurantDetailResponse expectedRestaurant = mockRestaurants.stream()
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
                verify(reviewVectorGenerator, times(1)).generateFromContent(testQuery);
                verify(restaurantVectorRepository, times(1)).findTopRestaurantIdsByCombinedSimilarity(
                                mockReviewVector.getVibeVector(),
                                mockReviewVector.getFoodVector(),
                                mockReviewVector.getCompanionVector(),
                                mockReviewVector.getPurposeVector(),
                                5);
        }
}
