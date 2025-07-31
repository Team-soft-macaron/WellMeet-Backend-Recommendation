package com.wellmeet.WellMeet_Recommendation.restaurant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestaurantDetailResponse {
    private String id;
    private String name;
    private String address;
    private double rating;
    private int reviewCount;
    private double latitude;
    private double longitude;
    private String thumbnail;

    public RestaurantDetailResponse(String id, String name, String address, double rating, int reviewCount,
            double latitude, double longitude, String thumbnail) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.latitude = latitude;
        this.longitude = longitude;
        this.thumbnail = thumbnail;
    }
}
