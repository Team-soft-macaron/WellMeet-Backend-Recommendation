package com.wellmeet.WellMeet_Recommendation.crawlingreview.service;

import com.wellmeet.WellMeet_Recommendation.common.constant.Constant;
import com.wellmeet.WellMeet_Recommendation.common.domain.ReviewVector;
import com.wellmeet.WellMeet_Recommendation.common.util.EmbeddingUtil;
import com.wellmeet.WellMeet_Recommendation.common.util.LLMUtil;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.dto.CrawlingReviewSaveRequest;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.dto.CrawlingReviewResponse;
import com.wellmeet.WellMeet_Recommendation.crawlingreview.repository.CrawlingReviewRepository;
import com.wellmeet.WellMeet_Recommendation.exception.ErrorCode;
import com.wellmeet.WellMeet_Recommendation.exception.WellMeetException;
import com.wellmeet.WellMeet_Recommendation.restaurant.domain.RestaurantVector;
import com.wellmeet.WellMeet_Recommendation.restaurant.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CrawlingReviewServiceTest {
    @Mock
    private CrawlingReviewRepository crawlingReviewRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private LLMUtil llmUtil;
    @Mock
    private EmbeddingUtil embeddingUtil;
    @Mock
    private ReviewVectorGenerator reviewVectorGenerator;
    @InjectMocks
    private CrawlingReviewService crawlingReviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("리뷰 저장 성공")
    void saveReviewSuccess() {
        // given
        String restaurantName = "맛있고 조용한 곳";
        String hash = "hash123";

        CrawlingReviewSaveRequest request = mock(CrawlingReviewSaveRequest.class);
        when(request.getRestaurantId()).thenReturn(1L);
        when(request.getContent()).thenReturn(restaurantName);
        when(request.getHash()).thenReturn(hash);

        RestaurantVector restaurant = mock(RestaurantVector.class);
        when(restaurant.createReviewVector())
                .thenReturn(new ReviewVector(new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                        new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                        new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                        new float[Constant.OPENAI_EMBEDDING_DIMENSION]));
        when(restaurant.getId()).thenReturn(1L);
        when(restaurant.getRestaurantId()).thenReturn(restaurantName);
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        ReviewVector reviewVector = mock(ReviewVector.class);
        when(reviewVector.getVibeVector()).thenReturn(new float[Constant.OPENAI_EMBEDDING_DIMENSION]);
        when(reviewVector.getFoodVector()).thenReturn(new float[Constant.OPENAI_EMBEDDING_DIMENSION]);
        when(reviewVector.getCompanionVector()).thenReturn(new float[Constant.OPENAI_EMBEDDING_DIMENSION]);
        when(reviewVector.getPurposeVector()).thenReturn(new float[Constant.OPENAI_EMBEDDING_DIMENSION]);
        when(reviewVectorGenerator.generateFromContent(any())).thenReturn(reviewVector);

        when(crawlingReviewRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        CrawlingReviewResponse response = crawlingReviewService.saveReview(request);

        // then
        verify(restaurantRepository, times(1)).findById(1L);
        assertEquals(1L, response.getRestaurantId());
        assertEquals(hash, response.getHash());
    }

    @Test
    @DisplayName("리뷰 저장 실패 - 식당 없음")
    void saveReviewRestaurantNotFound() {
        // given
        CrawlingReviewSaveRequest request = mock(CrawlingReviewSaveRequest.class);
        when(request.getRestaurantId()).thenReturn(1L);
        when(restaurantRepository.findById(1L)).thenReturn(Optional.empty());

        // when, then
        WellMeetException ex = assertThrows(WellMeetException.class, () -> crawlingReviewService.saveReview(request));
        assertEquals(ErrorCode.RESTAURANT_NOT_FOUND.getStatus(), ex.getStatus());
    }
}
