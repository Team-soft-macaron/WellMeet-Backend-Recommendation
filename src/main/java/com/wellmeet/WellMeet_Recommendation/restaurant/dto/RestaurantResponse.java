package com.wellmeet.WellMeet_Recommendation.restaurant.dto;

import com.wellmeet.WellMeet_Recommendation.restaurant.domain.Restaurant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestaurantResponse {

    private Long id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String thumbnail;
    private String placeId;

    public RestaurantResponse(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
        this.thumbnail = restaurant.getThumbnail();
        this.placeId = restaurant.getPlaceId();
    }
}
