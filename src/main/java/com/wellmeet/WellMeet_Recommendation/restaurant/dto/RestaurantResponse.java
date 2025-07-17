package com.wellmeet.WellMeet_Recommendation.restaurant.dto;

import com.wellmeet.WellMeet_Recommendation.restaurant.domain.Restaurant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestaurantResponse {

    private Long id;
    private double latitude;
    private double longitude;
    private String restaurantId;

    public RestaurantResponse(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
        this.restaurantId = restaurant.getRestaurantId();
    }
}
