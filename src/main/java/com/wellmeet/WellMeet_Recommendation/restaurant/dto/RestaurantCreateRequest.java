package com.wellmeet.WellMeet_Recommendation.restaurant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestaurantCreateRequest {

    private String restaurantId;
    private String placeId;
    private Double latitude;
    private Double longitude;

    public RestaurantCreateRequest(String restaurantId, String placeId, Double latitude, Double longitude) {
        this.restaurantId = restaurantId;
        this.placeId = placeId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
