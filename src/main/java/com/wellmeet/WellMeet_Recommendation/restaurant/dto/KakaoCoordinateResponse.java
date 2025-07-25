package com.wellmeet.WellMeet_Recommendation.restaurant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoCoordinateResponse {
    private double x;
    private double y;
    private String placeName;

    public KakaoCoordinateResponse(double x, double y, String placeName) {
        this.x = x;
        this.y = y;
        this.placeName = placeName;
    }
}
