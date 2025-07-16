package com.wellmeet.WellMeet_Recommendation.restaurant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestaurantCreateRequest {

    private String placeId;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String thumbnail;

    public RestaurantCreateRequest(String placeId, String name, String address,
            Double latitude, Double longitude, String thumbnail) {
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.thumbnail = thumbnail;
    }
}
