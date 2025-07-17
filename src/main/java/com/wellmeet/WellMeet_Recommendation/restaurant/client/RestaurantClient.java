package com.wellmeet.WellMeet_Recommendation.restaurant.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.wellmeet.WellMeet_Recommendation.restaurant.dto.RestaurantDetailResponse;

@Component
public class RestaurantClient {
    private final RestClient restClient;

    public RestaurantClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public RestaurantDetailResponse getRestaurantById(Long id) {
        return restClient.get()
                .uri("http://host.docker.internal:8080/api/restaurant/{id}?memberId=1", id)
                .retrieve()
                .body(RestaurantDetailResponse.class);
    }
}
