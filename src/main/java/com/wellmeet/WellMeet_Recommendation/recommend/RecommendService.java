package com.wellmeet.WellMeet_Recommendation.recommend;


import com.wellmeet.WellMeet_Recommendation.restaurant.domain.BoundingBox;
import com.wellmeet.WellMeet_Recommendation.restaurant.domain.Restaurant;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RecommendRestaurantResponse;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RestaurantResponse;
import com.wellmeet.WellMeet_Recommendation.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final RestaurantService restaurantService;

    public List<RecommendRestaurantResponse> getNearbyRestaurants(double latitude, double longitude) {
        BoundingBox boundingBox = new BoundingBox(latitude, longitude);
        return restaurantService.findWithBoundBox(boundingBox)
                .stream()
                .map(RecommendRestaurantResponse::new)
                .toList();
    }

    public RestaurantResponse getRestaurant(Long id) {
        Restaurant restaurant = restaurantService.getById(id);
        return new RestaurantResponse(restaurant);
    }
}
