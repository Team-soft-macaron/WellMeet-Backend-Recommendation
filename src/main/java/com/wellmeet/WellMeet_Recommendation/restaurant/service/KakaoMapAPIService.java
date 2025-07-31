package com.wellmeet.WellMeet_Recommendation.restaurant.service;

import org.springframework.stereotype.Service;

import com.wellmeet.WellMeet_Recommendation.restaurant.client.KakaoAPIClient;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.KakaoCoordinateResponse;

@Service
public class KakaoMapAPIService {
    private final KakaoAPIClient kakaoAPIClient;

    public KakaoMapAPIService(KakaoAPIClient kakaoAPIClient) {
        this.kakaoAPIClient = kakaoAPIClient;
    }

    public KakaoCoordinateResponse getFirstPlaceCoordinate(String query) {
        return kakaoAPIClient.getFirstPlaceCoordinate(query);
    }
}
