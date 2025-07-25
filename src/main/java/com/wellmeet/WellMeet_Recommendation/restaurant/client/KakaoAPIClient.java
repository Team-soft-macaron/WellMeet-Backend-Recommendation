package com.wellmeet.WellMeet_Recommendation.restaurant.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.wellmeet.WellMeet_Recommendation.restaurant.dto.KakaoCoordinateResponse;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.KakaoDocumentResponse;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.KakaoKeywordSearchResponse;
import org.springframework.beans.factory.annotation.Qualifier;

@Component
public class KakaoAPIClient {
    private final RestClient kakaoRestClient;
    private final String KEYWORD_SEARCH_URL = "/v2/local/search/keyword.json";

    public KakaoAPIClient(@Qualifier("kakaoRestClient") RestClient kakaoRestClient) {
        this.kakaoRestClient = kakaoRestClient;
    }

    public KakaoCoordinateResponse getFirstPlaceCoordinate(String query) {
        System.out.println("query: " + query);
        System.out.println("KEYWORD_SEARCH_URL: " + KEYWORD_SEARCH_URL);
        KakaoKeywordSearchResponse response = kakaoRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(KEYWORD_SEARCH_URL)
                        .queryParam("query", query)
                        .queryParam("size", 1) // 첫 번째 결과만
                        .build())
                .retrieve()
                .body(KakaoKeywordSearchResponse.class);
        KakaoDocumentResponse document = response.getDocuments().get(0);
        return new KakaoCoordinateResponse(document.getX(), document.getY(), document.getPlaceName());

    }
}
