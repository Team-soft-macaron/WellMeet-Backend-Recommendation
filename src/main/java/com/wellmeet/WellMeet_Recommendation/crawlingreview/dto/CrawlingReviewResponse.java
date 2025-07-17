package com.wellmeet.WellMeet_Recommendation.crawlingreview.dto;

import com.wellmeet.WellMeet_Recommendation.crawlingreview.domain.CrawlingReview;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CrawlingReviewResponse {
    private Long id;
    private String content;
    private String hash;
    private Long restaurantId;

    public CrawlingReviewResponse(CrawlingReview review) {
        this.id = review.getId();
        this.content = review.getContent();
        this.hash = review.getHash();
        this.restaurantId = review.getRestaurant().getId();
    }
}
