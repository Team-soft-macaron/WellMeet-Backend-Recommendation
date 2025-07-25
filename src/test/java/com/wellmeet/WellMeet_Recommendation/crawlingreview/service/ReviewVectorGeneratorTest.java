package com.wellmeet.WellMeet_Recommendation.crawlingreview.service;

import com.wellmeet.WellMeet_Recommendation.common.constant.Constant;
import com.wellmeet.WellMeet_Recommendation.common.domain.ReviewVector;
import com.wellmeet.WellMeet_Recommendation.common.dto.ExtractedInfoResponse;
import com.wellmeet.WellMeet_Recommendation.common.util.EmbeddingUtil;
import com.wellmeet.WellMeet_Recommendation.common.util.LLMUtil;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReviewVectorGeneratorTest {
    private static final String TEST_CONTENT = "여자친구와 데이트 하는데 분위기 좋은 파스타 집 추천 좀";
    private static final String VIBE = "분위기 좋은";
    private static final String FOOD = "파스타";
    private static final String COMPANION = "여자친구";
    private static final String PURPOSE = "데이트";

    @Mock
    private LLMUtil llmUtil;

    @Mock
    private EmbeddingUtil embeddingUtil;

    @InjectMocks
    private ReviewVectorGenerator reviewVectorGenerator;

    @Test
    @DisplayName("정상적인 리뷰 내용이 주어졌을 때 ReviewVector를 생성한다")
    void generateFromContentSuccess() {
        // given
        final String testContent = TEST_CONTENT;
        final ExtractedInfoResponse mockExtractedInfo = new ExtractedInfoResponse(VIBE, FOOD, COMPANION,
                PURPOSE);
        final ReviewVector mockReviewVector = new ReviewVector(
                new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                new float[Constant.OPENAI_EMBEDDING_DIMENSION],
                new float[Constant.OPENAI_EMBEDDING_DIMENSION]);

        // when
        when(llmUtil.extractUserRequest(testContent)).thenReturn(mockExtractedInfo);
        when(embeddingUtil.createReviewVector(
                mockExtractedInfo.getVibe(),
                mockExtractedInfo.getFood(),
                mockExtractedInfo.getCompanion(),
                mockExtractedInfo.getPurpose())).thenReturn(mockReviewVector);

        // when
        ReviewVector result = reviewVectorGenerator.generateFromContent(testContent);

        // then
        assertEquals(mockReviewVector, result);
        verify(llmUtil, times(1)).extractUserRequest(testContent);
        verify(embeddingUtil, times(1)).createReviewVector(
                VIBE,
                FOOD,
                COMPANION,
                PURPOSE);
    }

}
