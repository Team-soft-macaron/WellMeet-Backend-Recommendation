package com.wellmeet.WellMeet_Recommendation.restaurant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecommendRestaurantRequest {
    private String query;

    public RecommendRestaurantRequest(String query) {
        this.query = query;
    }
}
