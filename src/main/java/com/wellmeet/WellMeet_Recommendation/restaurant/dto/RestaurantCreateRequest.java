package com.wellmeet.WellMeet_Recommendation.restaurant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestaurantCreateRequest {

    private String restaurantId;
    private Double latitude;
    private Double longitude;

    public RestaurantCreateRequest(String restaurantId, Double latitude, Double longitude) {
        this.restaurantId = restaurantId;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
