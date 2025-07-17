package com.wellmeet.WellMeet_Recommendation.crawlingreview.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CrawlingReviewSaveRequest {
    private Long restaurantId;
    private String content;
    private String hash;
}
