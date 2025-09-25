package com.wellmeet.WellMeet_Recommendation.restaurant.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.KakaoCoordinateResponse;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.KakaoDocumentResponse;
import com.wellmeet.WellMeet_Recommendation.restaurant.dto.KakaoKeywordSearchResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KakaoAPIClient {
    private final RestClient kakaoRestClient;
    private final String KEYWORD_SEARCH_URL = "/v2/local/search/keyword.json";

    public KakaoAPIClient(@Qualifier("kakaoRestClient") RestClient kakaoRestClient) {
        this.kakaoRestClient = kakaoRestClient;
    }

    @Cacheable(value = "location")
    public KakaoCoordinateResponse getFirstPlaceCoordinate(String query) {
        KakaoKeywordSearchResponse response = kakaoRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(KEYWORD_SEARCH_URL)
                        .queryParam("query", query)
                        .queryParam("size", 1) // 첫 번째 결과만
                        .build())
                .retrieve()
                .body(KakaoKeywordSearchResponse.class);
        KakaoDocumentResponse document = response.getDocuments().get(0);
        log.info("place name: {}", document.getPlaceName());
        log.info("address name: {}", document.getAddressName());
        return new KakaoCoordinateResponse(Double.parseDouble(document.getLongitude()),
                Double.parseDouble(document.getLatitude()),
                document.getPlaceName());

    }
}
