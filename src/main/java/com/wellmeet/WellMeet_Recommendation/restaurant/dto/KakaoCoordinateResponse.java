package com.wellmeet.WellMeet_Recommendation.restaurant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoCoordinateResponse {
    private String x;
    private String y;
    private String placeName;

    public KakaoCoordinateResponse(String x, String y, String placeName) {
        this.x = x;
        this.y = y;
        this.placeName = placeName;
    }
}
