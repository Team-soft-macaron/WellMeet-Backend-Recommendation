package com.wellmeet.WellMeet_Recommendation.restaurant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoCoordinateResponse {
    private double longitude;
    private double latitude;
    private String placeName;

    public KakaoCoordinateResponse(double longitude, double latitude, String placeName) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.placeName = placeName;
    }
}
