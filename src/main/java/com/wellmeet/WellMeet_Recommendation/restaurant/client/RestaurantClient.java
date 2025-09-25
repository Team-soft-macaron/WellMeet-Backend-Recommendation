package com.wellmeet.WellMeet_Recommendation.restaurant.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.wellmeet.WellMeet_Recommendation.exception.ErrorCode;
import com.wellmeet.WellMeet_Recommendation.exception.WellMeetException;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RestaurantDetailResponse;

@Component
public class RestaurantClient {
    private final RestClient restClient;
    private final String restaurantUrl;

    public RestaurantClient(RestClient restClient, @Value("${services.restaurant.url}") String restaurantUrl) {
        this.restClient = restClient;
        this.restaurantUrl = restaurantUrl;
    }

    @Cacheable(value = "restaurant")
    public RestaurantDetailResponse getRestaurantById(String restaurantId) {
        return restClient.get()
                .uri(restaurantUrl + "/user/restaurant/{id}?memberId=1", restaurantId)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        (request, response) -> {
                            throw new WellMeetException(ErrorCode.RESTAURANT_NOT_FOUND);
                        })
                .onStatus(status -> status.is5xxServerError(),
                        (request, response) -> {
                            throw new WellMeetException(ErrorCode.INTERNAL_SERVER_ERROR);
                        })
                .body(RestaurantDetailResponse.class);
    }
}
