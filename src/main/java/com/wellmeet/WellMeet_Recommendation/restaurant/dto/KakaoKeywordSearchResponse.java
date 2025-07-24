package com.wellmeet.WellMeet_Recommendation.restaurant.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoKeywordSearchResponse {
    private List<KakaoDocumentResponse> documents;
}
