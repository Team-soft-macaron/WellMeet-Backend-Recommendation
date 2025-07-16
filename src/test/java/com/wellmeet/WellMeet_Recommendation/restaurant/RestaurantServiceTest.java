package com.wellmeet.WellMeet_Recommendation.restaurant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.wellmeet.WellMeet_Recommendation.common.constant.Constant;
import com.wellmeet.WellMeet_Recommendation.common.domain.ReviewVector;
import com.wellmeet.WellMeet_Recommendation.common.dto.ExtractedInfoResponse;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.service.ReviewVectorGenerator;
import com.wellmeet.WellMeet_Recommendation.restaurant.domain.Restaurant;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RestaurantResponse;
import com.wellmeet.WellMeet_Recommendation.restaurant.repository.RestaurantRepository;
import com.wellmeet.WellMeet_Recommendation.restaurant.service.RestaurantService;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

        @Mock
        private RestaurantRepository restaurantRepository;

        @Mock
        private ReviewVectorGenerator reviewVectorGenerator;

        @InjectMocks
        private RestaurantService restaurantService;

        @Test
        @DisplayName("쿼리를 입력받아 추천 레스토랑 목록을 반환한다")
        void recommendRestaurant_withValidQuery_shouldReturnRecommendedRestaurants() {
                // given
                String testQuery = "여자친구와 데이트하기 좋은 분위기 있는 파스타 맛집 추천해줘";
                ExtractedInfoResponse mockExtractedInfo = new ExtractedInfoResponse(
                                "분위기 좋은",
                                "파스타",
                                "여자친구",
                                "데이트");
                ReviewVector mockReviewVector = new ReviewVector(
                                new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                                new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                                new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                                new float[Constant.OPENAI_EMBEDDING_DIMENSION]);

                List<Restaurant> mockRestaurants = Arrays.asList(
                                createMockRestaurant("1", "로맨틱 파스타", "서울시 강남구", 37.5665, 126.9780),
                                createMockRestaurant("2", "분위기 좋은 이탈리안", "서울시 서초구", 37.4837, 127.0324),
                                createMockRestaurant("3", "데이트 코스 레스토랑", "서울시 송파구", 37.5145, 127.1059),
                                createMockRestaurant("4", "프리미엄 파스타 하우스", "서울시 마포구", 37.5547, 126.9707),
                                createMockRestaurant("5", "커플 추천 맛집", "서울시 용산구", 37.5311, 126.9810));
                // when
                when(reviewVectorGenerator.generateFromContent(testQuery)).thenReturn(mockReviewVector);
                when(restaurantRepository.findTopByCombinedSimilarity(
                                mockReviewVector.getVibeVector(),
                                mockReviewVector.getFoodVector(),
                                mockReviewVector.getCompanionVector(),
                                mockReviewVector.getPurposeVector(),
                                5)).thenReturn(mockRestaurants);
                List<RestaurantResponse> result = restaurantService.recommendRestaurant(testQuery);

                // then
                assertNotNull(result);
                assertEquals(5, result.size());

                // 첫 번째 레스토랑 검증
                RestaurantResponse firstRestaurant = result.get(0);
                assertEquals("1", firstRestaurant.getPlaceId());
                assertEquals("로맨틱 파스타", firstRestaurant.getName());
                assertEquals("서울시 강남구", firstRestaurant.getAddress());

                // 메서드 호출 검증
                verify(reviewVectorGenerator, times(1)).generateFromContent(testQuery);
                verify(restaurantRepository, times(1)).findTopByCombinedSimilarity(
                                mockReviewVector.getVibeVector(),
                                mockReviewVector.getFoodVector(),
                                mockReviewVector.getCompanionVector(),
                                mockReviewVector.getPurposeVector(),
                                5);
        }

        private Restaurant createMockRestaurant(String placeId, String name, String address,
                        double latitude, double longitude) {
                return new Restaurant(
                                placeId,
                                name,
                                address,
                                latitude,
                                longitude,
                                "https://example.com/thumbnail.jpg",
                                new ReviewVector(
                                                new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                                                new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                                                new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                                                new float[Constant.OPENAI_EMBEDDING_DIMENSION]));
        }
}
