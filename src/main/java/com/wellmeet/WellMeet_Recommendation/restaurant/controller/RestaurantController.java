package com.wellmeet.WellMeet_Recommendation.restaurant.controller;

import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RecommendRestaurantRequest;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RestaurantCreateRequest;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RestaurantResponse;
import com.wellmeet.WellMeet_Recommendation.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping("/api/restaurants")
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse createRestaurant(@RequestBody RestaurantCreateRequest request) {
        return restaurantService.saveRestaurant(request);
    }

    @PostMapping("/api/restaurants/recommend")
    @ResponseStatus(HttpStatus.OK)
    public List<RestaurantResponse> recommendRestaurants(@RequestBody RecommendRestaurantRequest request) {
        return restaurantService.recommendRestaurant(request.getQuery());
    }
}
