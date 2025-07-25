package com.wellmeet.WellMeet_Recommendation.restaurant.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class KakaoAPIConfig {

    private static final int CONNECTION_TIMEOUT_SECONDS = 5;
    private static final int READ_TIMEOUT_SECONDS = 10;
    private final String KAKAO_API_URL = "https://dapi.kakao.com";

    @Bean("kakaoRestClient")
    public RestClient restClient(RestClient.Builder restClientBuilder,
            @Value("${kakao.rest-api-key}") String kakaoRestApiKey) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(CONNECTION_TIMEOUT_SECONDS));
        requestFactory.setReadTimeout(Duration.ofSeconds(READ_TIMEOUT_SECONDS));

        return restClientBuilder
                .requestFactory(requestFactory)
                .baseUrl(KAKAO_API_URL)
                .defaultHeader("Authorization", "KakaoAK " + kakaoRestApiKey)
                .build();
    }
}
