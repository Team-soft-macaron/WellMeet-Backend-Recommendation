package com.wellmeet.WellMeet_Recommendation.restaurant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoCoordinateResponse {
    private double x; // 경도 (longitude)
    private double y; // 위도 (latitude)
    private String placeName;

    public KakaoCoordinateResponse(double x, double y, String placeName) {
        this.x = x;
        this.y = y;
        this.placeName = placeName;
    }
}
